package de.nb.aventiure2.german.praedikat;


import static de.nb.aventiure2.german.base.Kasus.AKK;
import static de.nb.aventiure2.german.base.Konstituentenfolge.kf;
import static de.nb.aventiure2.german.base.Konstituentenfolge.schliesseInKommaEin;
import static de.nb.aventiure2.german.praedikat.IInfinitesPraedikat.toKonstituentenfolge;
import static de.nb.aventiure2.german.praedikat.Praedikatseinbindung.firstInterrogativwort;

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
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.Negationspartikelphrase;
import de.nb.aventiure2.german.base.PraedRegMerkmale;
import de.nb.aventiure2.german.base.Reflexivpronomen;
import de.nb.aventiure2.german.description.ITextContext;

/**
 * Ein "semantisches Prädikat" eines <i>reflexiven Verbs mit intentionaler Bedeutung</i>,
 * in dem alle Leerstellen besetzt sind. Beispiele:
 * <ul>
 *     <li>"die junge Frau bemüht sich, ihre Haare wieder hinunterzulassen"
 *     <li>"die junge Frau bemüht sich, sich zu beruhigen"
 * </ul>
 * <p>
 * Adverbiale Angaben ("aus einer Laune heraus") können immer noch eingefügt werden.
 * <p>
 * "Die vom Subjekt bezeichnete Person will oder will nicht die Handlung
 * ausführen, die im Komplement genannt ist", siehe Peter Eisenberg,
 * Der Satz, S. 356 (Kapitel 11.2)
 *
 * @see SemPraedikatIntentionalesVerbOhneLeerstellen
 */
public class SemPraedikatReflIntentionalesVerbOhneLeerstellen
        extends AbstractAngabenfaehigesSemPraedikatOhneLeerstellen {
    /**
     * Der Kasus mit dem das Verb reflexiv steht - z.B. Akkusativ ("sich bemühen").
     */
    @NonNull
    private final Kasus kasus;

    /**
     * "(...versucht) ihre Haare wieder hinunterzulassen"
     */
    @Komplement
    @Nonnull
    private final SemPraedikatOhneLeerstellen lexikalischerKern;

    @Valenz
    SemPraedikatReflIntentionalesVerbOhneLeerstellen(
            final Verb verb,
            final Kasus kasus,
            final SemPraedikatOhneLeerstellen lexikalischerKern) {
        this(verb, kasus, ImmutableList.of(),
                null, null, null,
                null, lexikalischerKern);
    }

    private SemPraedikatReflIntentionalesVerbOhneLeerstellen(
            final Verb verb,
            final Kasus kasus,
            final Iterable<Modalpartikel> modalpartikeln,
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabeSkopusSatz,
            @Nullable final Negationspartikelphrase negationspartikelphrase,
            @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabeSkopusVerbAllg,
            @Nullable final IAdvAngabeOderInterrogativWohinWoher advAngabeSkopusVerbWohinWoher,
            final SemPraedikatOhneLeerstellen lexikalischerKern) {
        super(verb, modalpartikeln,
                advAngabeSkopusSatz,
                negationspartikelphrase, advAngabeSkopusVerbAllg, advAngabeSkopusVerbWohinWoher);
        this.kasus = kasus;
        this.lexikalischerKern = lexikalischerKern;
    }

    @Override
    public SemPraedikatReflIntentionalesVerbOhneLeerstellen mitModalpartikeln(
            final Collection<Modalpartikel> modalpartikeln) {
        return new SemPraedikatReflIntentionalesVerbOhneLeerstellen(
                getVerb(),
                kasus,
                Iterables.concat(getModalpartikeln(), modalpartikeln),
                getAdvAngabeSkopusSatz(),
                getNegationspartikel(), getAdvAngabeSkopusVerbAllg(),
                getAdvAngabeSkopusVerbWohinWoher(),
                lexikalischerKern
        );
    }

    @Override
    public SemPraedikatReflIntentionalesVerbOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        return new SemPraedikatReflIntentionalesVerbOhneLeerstellen(
                getVerb(),
                kasus,
                getModalpartikeln(),
                advAngabe, getNegationspartikel(), getAdvAngabeSkopusVerbAllg(),
                getAdvAngabeSkopusVerbWohinWoher(),
                lexikalischerKern);
    }

    @Override
    public SemPraedikatReflIntentionalesVerbOhneLeerstellen neg() {
        return (SemPraedikatReflIntentionalesVerbOhneLeerstellen) super.neg();
    }

    @Override
    public SemPraedikatReflIntentionalesVerbOhneLeerstellen neg(
            @Nullable final Negationspartikelphrase negationspartikelphrase) {
        if (negationspartikelphrase == null) {
            return this;
        }

        return new SemPraedikatReflIntentionalesVerbOhneLeerstellen(
                getVerb(), kasus, getModalpartikeln(),
                getAdvAngabeSkopusSatz(), negationspartikelphrase, getAdvAngabeSkopusVerbAllg(),
                getAdvAngabeSkopusVerbWohinWoher(),
                lexikalischerKern);
    }

    @Override
    public SemPraedikatReflIntentionalesVerbOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        return new SemPraedikatReflIntentionalesVerbOhneLeerstellen(
                getVerb(),
                kasus,
                getModalpartikeln(),
                getAdvAngabeSkopusSatz(), getNegationspartikel(), advAngabe,
                getAdvAngabeSkopusVerbWohinWoher(),
                lexikalischerKern
        );
    }

    @Override
    public SemPraedikatReflIntentionalesVerbOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativWohinWoher advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        return new SemPraedikatReflIntentionalesVerbOhneLeerstellen(
                getVerb(),
                kasus,
                getModalpartikeln(),
                getAdvAngabeSkopusSatz(),
                getNegationspartikel(), getAdvAngabeSkopusVerbAllg(),
                advAngabe,
                lexikalischerKern
        );
    }

    @Override
    public boolean kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden() {
        // *"Dich bemüht dich zu waschen [gehst du ins Bad]"
        return false;
    }

    @Nullable
    public Vorfeld getSpeziellesVorfeldAlsWeitereOption(
            final PraedRegMerkmale praedRegMerkmale,
            final ZuInfinitiv lexKernZuInfinitiv) {
        @Nullable final Vorfeld speziellesVorfeldFromSuper =
                super.getGgfVorfeldAdvAngabeSkopusVerb(praedRegMerkmale);
        if (speziellesVorfeldFromSuper != null) {
            return speziellesVorfeldFromSuper;
        }

        // "Ihre Haare (versucht sie wieder hinunterzulassen)"
        return lexKernZuInfinitiv.getSpeziellesVorfeldAlsWeitereOption();
    }

    @Override
    public boolean hatAkkusativobjekt() {
        return kasus == AKK;
    }

    @Override
    TopolFelder getTopolFelder(final ITextContext textContext,
                               final PraedRegMerkmale praedRegMerkmale,
                               final boolean nachAnschlusswort) {
        @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabeSkopusSatz =
                getAdvAngabeSkopusSatz();
        @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabeSkopusVerbAllg =
                getAdvAngabeSkopusVerbAllg();

        final Konstituente advAngabeSkopusSatzSyntFuerMittelfeld =
                advAngabeSkopusSatz != null &&
                        advAngabeSkopusSatz.imMittelfeldErlaubt() ?
                        advAngabeSkopusSatz.getDescription(praedRegMerkmale) :
                        // "aus einer Laune heraus"
                        null; // (ins Nachfeld verschieben);

        final Reflexivpronomen reflexivpronomen = Reflexivpronomen.get(praedRegMerkmale);

        final Konstituente advAngabeSkopusVerbSyntFuerMittelfeld =
                advAngabeSkopusVerbAllg != null &&
                        advAngabeSkopusVerbAllg.imMittelfeldErlaubt() ?
                        advAngabeSkopusVerbAllg.getDescription(praedRegMerkmale) :
                        // "erneut"
                        null; // (ins Nachfeld verschieben)

        final Konstituente advAngabeSkopusVerbWohinWoherSynt =
                getAdvAngabeSkopusVerbWohinWoherDescription(praedRegMerkmale);

        final ImmutableList<ZuInfinitiv> lexKernZuInfinitive = lexikalischerKern.getZuInfinitiv(
                // Es liegt Subjektkontrolle vor.
                textContext,
                // Infinitiv steht nach dem "dich", nicht nach dem Anschlusswort!
                false, praedRegMerkmale
        );

        return new TopolFelder(
                new Mittelfeld(
                        Konstituentenfolge.joinToNullKonstituentenfolge(
                                reflexivpronomen.imK(kasus), // "dich"
                                advAngabeSkopusSatzSyntFuerMittelfeld,
                                // "aus einer Laune heraus"
                                kf(getModalpartikeln()), // "mal eben"
                                advAngabeSkopusVerbSyntFuerMittelfeld, // "erneut"
                                getNegationspartikel(), // "nicht"
                                advAngabeSkopusVerbAllg != null &&
                                        !advAngabeSkopusVerbAllg.imMittelfeldErlaubt()
                                        // Die advAngabeSkopusSatz schieben wir immer ins
                                        // Nachfeld,
                                        // daraus wird sie nach Möglichkeit ins Vorfeld
                                        // gezogen
                                        // werden.
                                        ?
                                        // -> Lex. Kern sollten wir aus dem Nachfeld
                                        // vorziehen
                                        schliesseInKommaEin(
                                                toKonstituentenfolge(lexKernZuInfinitive))
                                        // ", ihre Haare wieder hinunterzulassen, "
                                        : null,
                                // (Normalfall: lexikalischer Kern im Nachfeld)
                                advAngabeSkopusVerbWohinWoherSynt
                                // (kann es wohl gar nicht geben)
                        ), reflexivpronomen, kasus),
                new Nachfeld(
                        Konstituentenfolge.joinToNullKonstituentenfolge(
                                advAngabeSkopusVerbAllg == null
                                        || advAngabeSkopusVerbAllg.imMittelfeldErlaubt() ?
                                        schliesseInKommaEin(
                                                toKonstituentenfolge(lexKernZuInfinitive))
                                        // "(Du bemühst dich), dich zu waschen[,]"
                                        // (Die Kommata rund um den Infinitiv sind offenbar
                                        // fakultativ.)
                                        : null,
                                advAngabeSkopusVerbAllg != null
                                        && !advAngabeSkopusVerbAllg.imMittelfeldErlaubt() ?
                                        advAngabeSkopusVerbAllg
                                                .getDescription(praedRegMerkmale)
                                        // "glücklich, dich zu sehen"
                                        : null,
                                advAngabeSkopusSatz != null
                                        && !advAngabeSkopusSatz.imMittelfeldErlaubt() ?
                                        advAngabeSkopusSatz
                                                .getDescription(praedRegMerkmale)
                                        : null)),
                getVorfeldAdvAngabeSkopusSatz(praedRegMerkmale),
                getSpeziellesVorfeldAlsWeitereOption(praedRegMerkmale, lexKernZuInfinitive.get(0)),
                lexKernZuInfinitive.get(0).getRelativpronomen(),
                firstInterrogativwort(advAngabeSkopusSatzSyntFuerMittelfeld,
                        advAngabeSkopusVerbSyntFuerMittelfeld,
                        advAngabeSkopusVerbWohinWoherSynt,
                        lexKernZuInfinitive.get(0).getErstesInterrogativwort()));
    }

    @Override
    public boolean umfasstSatzglieder() {
        return super.umfasstSatzglieder()
                // Ich bin nicht sicher, ob das reflexivische "sich" als Satzglied gilt.
                // Sagen wir mal sicherheitshalber: Nein.
                || lexikalischerKern.umfasstSatzglieder();
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
        final SemPraedikatReflIntentionalesVerbOhneLeerstellen that =
                (SemPraedikatReflIntentionalesVerbOhneLeerstellen) o;
        return kasus == that.kasus &&
                lexikalischerKern.equals(that.lexikalischerKern);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), kasus, lexikalischerKern);
    }
}
