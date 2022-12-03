package de.nb.aventiure2.german.praedikat;


import static de.nb.aventiure2.german.base.Kasus.AKK;
import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToKonstituentenfolge;
import static de.nb.aventiure2.german.base.Konstituentenfolge.kf;
import static de.nb.aventiure2.german.praedikat.IInfinitesPraedikat.toKonstituentenfolge;
import static de.nb.aventiure2.german.praedikat.Praedikatseinbindung.firstInterrogativwort;
import static de.nb.aventiure2.german.praedikat.Praedikatseinbindung.firstRelativpronomen;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.Collection;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.nb.aventiure2.annotations.Komplement;
import de.nb.aventiure2.annotations.Valenz;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativSkopusSatz;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativVerbAllg;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativWohinWoher;
import de.nb.aventiure2.german.base.Kasus;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Negationspartikelphrase;
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.base.PraedRegMerkmale;
import de.nb.aventiure2.german.base.SubstantivischPhrasierbar;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.ITextContext;

/**
 * Ein "semantisches Prädikat" eines <i>direktiven Verbs</i>, in dem alle Leerstellen besetzt
 * sind. Beispiele:
 * <ul>
 *     <li>"die junge Frau bitten ihre Haare wieder hinunterzulassen"
 *     <li>"die junge Frau bitten sich zu beruhigen"
 * </ul>
 * <p>
 * Adverbiale Angaben ("aus einer Laune heraus") können immer noch eingefügt werden.
 * <p>
 * Zu direktiven Verben siehe Peter Eisenberg, Der Satz, S. 357 (Kapitel 11.2)
 */
public class SemPraedikatDirektivesVerbOhneLeerstellen
        extends AbstractAngabenfaehigesSemPraedikatOhneLeerstellen {
    /**
     * Der Kasus mit dem das direktive Verb steht.
     */
    @NonNull
    private final Kasus kasus;

    /**
     * Das Objekt, an das die "Direktive" geht
     */
    @Komplement
    private final SubstantivischPhrasierbar objekt;

    /**
     * "(...bitten) ihre Haare wieder hinunterzulassen"
     */
    @Nonnull
    @Komplement
    private final SemPraedikatOhneLeerstellen lexikalischerKern;

    @Valenz
    SemPraedikatDirektivesVerbOhneLeerstellen(
            final Verb verb,
            final Kasus kasus,
            final SubstantivischPhrasierbar objekt,
            final SemPraedikatOhneLeerstellen lexikalischerKern) {
        this(verb, kasus, objekt,
                ImmutableList.of(), null, null, null,
                null, lexikalischerKern);
    }

    private SemPraedikatDirektivesVerbOhneLeerstellen(
            final Verb verb,
            final Kasus kasus,
            final SubstantivischPhrasierbar objekt,
            final Iterable<Modalpartikel> modalpartikeln,
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabeSkopusSatz,
            @Nullable final Negationspartikelphrase negationspartikelphrase,
            @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabeSkopusVerbAllg,
            @Nullable final IAdvAngabeOderInterrogativWohinWoher advAngabeSkopusVerbWohinWoher,
            final SemPraedikatOhneLeerstellen lexikalischerKern) {
        super(verb,
                modalpartikeln,
                advAngabeSkopusSatz,
                negationspartikelphrase, advAngabeSkopusVerbAllg, advAngabeSkopusVerbWohinWoher);
        this.kasus = kasus;
        this.objekt = objekt;
        this.lexikalischerKern = lexikalischerKern;
    }

    @Override
    public SemPraedikatDirektivesVerbOhneLeerstellen mitModalpartikeln(
            final Collection<Modalpartikel> modalpartikeln) {
        return new SemPraedikatDirektivesVerbOhneLeerstellen(
                getVerb(), kasus, objekt,
                Iterables.concat(getModalpartikeln(), modalpartikeln),
                getAdvAngabeSkopusSatz(),
                getNegationspartikel(), getAdvAngabeSkopusVerbAllg(),
                getAdvAngabeSkopusVerbWohinWoher(),
                lexikalischerKern
        );
    }

    @Override
    public SemPraedikatDirektivesVerbOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        return new SemPraedikatDirektivesVerbOhneLeerstellen(
                getVerb(),
                kasus,
                objekt,
                getModalpartikeln(),
                advAngabe, getNegationspartikel(), getAdvAngabeSkopusVerbAllg(),
                getAdvAngabeSkopusVerbWohinWoher(),
                lexikalischerKern);
    }

    @Override
    public SemPraedikatDirektivesVerbOhneLeerstellen neg() {
        return (SemPraedikatDirektivesVerbOhneLeerstellen) super.neg();
    }

    @Override
    public SemPraedikatDirektivesVerbOhneLeerstellen neg(
            @Nullable final Negationspartikelphrase negationspartikelphrase) {
        if (negationspartikelphrase == null) {
            return this;
        }

        return new SemPraedikatDirektivesVerbOhneLeerstellen(
                getVerb(),
                kasus,
                objekt,
                getModalpartikeln(),
                getAdvAngabeSkopusSatz(), negationspartikelphrase, getAdvAngabeSkopusVerbAllg(),
                getAdvAngabeSkopusVerbWohinWoher(),
                lexikalischerKern);
    }

    @Override
    public SemPraedikatDirektivesVerbOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        return new SemPraedikatDirektivesVerbOhneLeerstellen(
                getVerb(),
                kasus,
                objekt,
                getModalpartikeln(),
                getAdvAngabeSkopusSatz(), getNegationspartikel(), advAngabe,
                getAdvAngabeSkopusVerbWohinWoher(),
                lexikalischerKern
        );
    }

    @Override
    public SemPraedikatDirektivesVerbOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativWohinWoher advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        return new SemPraedikatDirektivesVerbOhneLeerstellen(
                getVerb(),
                kasus,
                objekt,
                getModalpartikeln(),
                getAdvAngabeSkopusSatz(),
                getNegationspartikel(), getAdvAngabeSkopusVerbAllg(),
                advAngabe,
                lexikalischerKern
        );
    }

    @Override
    public boolean kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden() {
        return true;
    }

    @Nullable
    public Vorfeld getSpeziellesVorfeldAlsWeitereOption(
            final PraedRegMerkmale praedRegMerkmale,
            final SubstantivischePhrase objektPhrase) {
        @Nullable final Vorfeld speziellesVorfeldFromSuper =
                super.getGgfVorfeldAdvAngabeSkopusVerb(praedRegMerkmale);
        if (speziellesVorfeldFromSuper != null) {
            return speziellesVorfeldFromSuper;
        }

        if (getNegationspartikel() != null) {
            return null;
        }

        /*
         * "es" allein darf nicht im Vorfeld stehen, wenn es ein Objekt ist
         * (Eisenberg Der Satz 5.4.2)
         */
        if (Personalpronomen.isPersonalpronomenEs(objektPhrase, kasus)) {
            return null;
        }

        /*
         * Phrasen (auch Personalpronomen) mit Fokuspartikel
         * sind häufig kontrastiv und daher oft für das Vorfeld geeignet.
         */
        if (objektPhrase.getFokuspartikel() != null) {
            // "Nur die junge Frau bittest du..."
            return new Vorfeld(objektPhrase.imK(kasus));
        }

        return null;
    }

    @Override
    public boolean hatAkkusativobjekt() {
        return kasus == AKK;
    }

    @Override
    TopolFelder getTopolFelder(final ITextContext textContext,
                               final PraedRegMerkmale praedRegMerkmale,
                               final boolean nachAnschlusswort) {
        final Konstituente advAngabeSkopusSatzSyntFuerMittelfeld =
                getAdvAngabeSkopusSatzDescriptionFuerMittelfeld(praedRegMerkmale);

        final SubstantivischePhrase objektPhrase = objekt.alsSubstPhrase(textContext);
        final Praedikatseinbindung<SubstantivischePhrase> objektEinbindung =
                new Praedikatseinbindung<>(objektPhrase, o -> o.imK(kasus));

        final Konstituente advAngabeSkopusVerbSyntFuerMittelfeld =
                getAdvAngabeSkopusVerbTextDescriptionFuerMittelfeld(
                        praedRegMerkmale);
        final Konstituente advAngabeSkopusVerbWohinWoherSynt =
                getAdvAngabeSkopusVerbWohinWoherDescription(praedRegMerkmale);

        final ImmutableList<ZuInfinitiv> lexKernZuInfinitive =
                lexikalischerKern.getZuInfinitiv(textContext,
                        // Infinitiv steht im Nachfeld, nicht nach dem
                        // Anschlusswort!
                        false,
                        objektPhrase
                        // Es liegt "Objektkontrolle" vor.
                );

        return new TopolFelder(
                new Mittelfeld(
                        joinToKonstituentenfolge(
                                advAngabeSkopusSatzSyntFuerMittelfeld,
                                // "aus einer Laune heraus"
                                kf(getModalpartikeln()), // "mal eben"
                                getNegationspartikel(), // "nicht"
                                advAngabeSkopusVerbSyntFuerMittelfeld,
                                // "erneut"
                                objektEinbindung, // "die junge Frau"
                                advAngabeSkopusVerbWohinWoherSynt
                                // (kann es wohl gar nicht geben)
                        ),
                        objektEinbindung),

                //  Der lexikalische Kern könnte alternativ diskontinuierlich aufgeteilt
                //  werden:
                //   - "Ihre Haare die junge Frau wieder hinunterzulassen bitten"
                //   ("Ihre Haare bittest du die junge Frau wieder hinunterzulassen")

                //  Der lexikalische Kern könnte als dritte Alternative ebenfalls ins
                //  Mittelfeld
                //   gestellt werden (statt ins Nachfeld):
                //   - "Die junge Frau ihre Haare wieder hinunterzulassen bitten"
                //   - "Du hast die junge Frau ihre Haare wieder hinunterzulassen gebeten"
                new Nachfeld(
                        joinToKonstituentenfolge(
                                toKonstituentenfolge(lexKernZuInfinitive),
                                // "sich zu waschen"; wir lassen diese Kommata weg - das
                                // ist erlaubt
                                getAdvAngabeSkopusVerbTextDescriptionFuerZwangsausklammerung(
                                        praedRegMerkmale),
                                // , glücklich, dich zu sehen
                                getAdvAngabeSkopusSatzDescriptionFuerZwangsausklammerung(
                                        praedRegMerkmale)
                        )),
                getVorfeldAdvAngabeSkopusSatz(praedRegMerkmale),
                getSpeziellesVorfeldAlsWeitereOption(praedRegMerkmale, objektPhrase),
                firstRelativpronomen(objektEinbindung,
                        lexKernZuInfinitive.get(0).getRelativpronomen()),
                firstInterrogativwort(advAngabeSkopusSatzSyntFuerMittelfeld,
                        advAngabeSkopusVerbSyntFuerMittelfeld,
                        objektEinbindung,
                        advAngabeSkopusVerbWohinWoherSynt,
                        lexKernZuInfinitive.get(0).getErstesInterrogativwort()));
    }

    @Override
    public boolean umfasstSatzglieder() {
        return true;
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final SemPraedikatDirektivesVerbOhneLeerstellen that =
                (SemPraedikatDirektivesVerbOhneLeerstellen) o;
        return kasus == that.kasus &&
                Objects.equals(objekt, that.objekt) &&
                lexikalischerKern.equals(that.lexikalischerKern);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), kasus, objekt, lexikalischerKern);
    }
}
