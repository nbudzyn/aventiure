package de.nb.aventiure2.german.praedikat;


import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToKonstituentenfolge;
import static de.nb.aventiure2.german.base.Konstituentenfolge.kf;
import static de.nb.aventiure2.german.praedikat.IInfinitesPraedikat.toKonstituentenfolge;
import static de.nb.aventiure2.german.praedikat.Praedikatseinbindung.firstInterrogativwort;

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
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.Negationspartikelphrase;
import de.nb.aventiure2.german.base.PraedRegMerkmale;
import de.nb.aventiure2.german.description.ITextContext;

/**
 * Ein "semantisches Prädikat" eines (nicht-reflexiven) <i>Verbs mit intentionaler Bedeutung</i>,
 * in dem alle Leerstellen besetzt sind. Beispiele:
 * <ul>
 *     <li>"die junge Frau versucht ihre Haare wieder hinunterzulassen"
 *     <li>"die junge Frau versucht sich zu beruhigen"
 * </ul>
 * <p>
 * Adverbiale Angaben ("aus einer Laune heraus") können immer noch eingefügt werden.
 * <p>
 * "Die vom Subjekt bezeichnete Person will oder will nicht die Handlung
 * ausführen, die im Komplement genannt ist", siehe Peter Eisenberg,
 * Der Satz, S. 356 (Kapitel 11.2)
 *
 * @see SemPraedikatReflIntentionalesVerbOhneLeerstellen
 */
public class SemPraedikatIntentionalesVerbOhneLeerstellen
        extends AbstractAngabenfaehigesSemPraedikatOhneLeerstellen {
    /**
     * "(...versucht) ihre Haare wieder hinunterzulassen"
     */
    @Komplement
    @Nonnull
    private final SemPraedikatOhneLeerstellen lexikalischerKern;

    @Valenz
    SemPraedikatIntentionalesVerbOhneLeerstellen(
            final Verb verb,
            final SemPraedikatOhneLeerstellen lexikalischerKern) {
        this(verb, ImmutableList.of(),
                null, null, null,
                null, lexikalischerKern);
    }

    private SemPraedikatIntentionalesVerbOhneLeerstellen(
            final Verb verb,
            final Iterable<Modalpartikel> modalpartikeln,
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabeSkopusSatz,
            @Nullable final Negationspartikelphrase negationspartikelphrase,
            @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabeSkopusVerbAllg,
            @Nullable final IAdvAngabeOderInterrogativWohinWoher advAngabeSkopusVerbWohinWoher,
            final SemPraedikatOhneLeerstellen lexikalischerKern) {
        super(verb, modalpartikeln,
                advAngabeSkopusSatz,
                negationspartikelphrase, advAngabeSkopusVerbAllg, advAngabeSkopusVerbWohinWoher);
        this.lexikalischerKern = lexikalischerKern;
    }

    @Override
    public SemPraedikatIntentionalesVerbOhneLeerstellen mitModalpartikeln(
            final Collection<Modalpartikel> modalpartikeln) {
        return new SemPraedikatIntentionalesVerbOhneLeerstellen(
                getVerb(),
                Iterables.concat(getModalpartikeln(), modalpartikeln),
                getAdvAngabeSkopusSatz(),
                getNegationspartikel(), getAdvAngabeSkopusVerbAllg(),
                getAdvAngabeSkopusVerbWohinWoher(),
                lexikalischerKern
        );
    }

    @Override
    public SemPraedikatIntentionalesVerbOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        return new SemPraedikatIntentionalesVerbOhneLeerstellen(
                getVerb(),
                getModalpartikeln(),
                advAngabe, getNegationspartikel(), getAdvAngabeSkopusVerbAllg(),
                getAdvAngabeSkopusVerbWohinWoher(),
                lexikalischerKern);
    }

    @Override
    public SemPraedikatIntentionalesVerbOhneLeerstellen neg() {
        return (SemPraedikatIntentionalesVerbOhneLeerstellen) super.neg();
    }

    @Override
    public SemPraedikatIntentionalesVerbOhneLeerstellen neg(
            @Nullable final Negationspartikelphrase negationspartikelphrase) {
        if (negationspartikelphrase == null) {
            return this;
        }

        return new SemPraedikatIntentionalesVerbOhneLeerstellen(
                getVerb(), getModalpartikeln(),
                getAdvAngabeSkopusSatz(), negationspartikelphrase, getAdvAngabeSkopusVerbAllg(),
                getAdvAngabeSkopusVerbWohinWoher(),
                lexikalischerKern);
    }

    @Override
    public SemPraedikatIntentionalesVerbOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        return new SemPraedikatIntentionalesVerbOhneLeerstellen(
                getVerb(),
                getModalpartikeln(),
                getAdvAngabeSkopusSatz(), getNegationspartikel(), advAngabe,
                getAdvAngabeSkopusVerbWohinWoher(),
                lexikalischerKern
        );
    }

    @Override
    public SemPraedikatIntentionalesVerbOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativWohinWoher advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        return new SemPraedikatIntentionalesVerbOhneLeerstellen(
                getVerb(),
                getModalpartikeln(),
                getAdvAngabeSkopusSatz(),
                getNegationspartikel(), getAdvAngabeSkopusVerbAllg(),
                advAngabe,
                lexikalischerKern
        );
    }

    @Override
    public boolean kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden() {
        // "Gebeten dich zu waschen [gehst du ins Bad]"
        return true;
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
        return false;
    }

    @Override
    TopolFelder getTopolFelder(final ITextContext textContext,
                               final PraedRegMerkmale praedRegMerkmale,
                               final boolean nachAnschlusswort) {
        @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabeSkopusSatz =
                getAdvAngabeSkopusSatz();
        @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabeSkopusVerbAllg =
                getAdvAngabeSkopusVerbAllg();

        final Konstituentenfolge advAngabeSkopusSatzSyntFuerMittelfeld =
                advAngabeSkopusSatz != null &&
                        advAngabeSkopusSatz.imMittelfeldErlaubt() ?
                        advAngabeSkopusSatz.getDescription(praedRegMerkmale)
                                .toKonstituentenfolge() :
                        // "aus einer Laune heraus"
                        null;  // (ins Nachfeld verschieben)
        final Konstituentenfolge advAngabeSkopusVerbAllgSyntFuerMittelfeld =
                advAngabeSkopusVerbAllg != null &&
                        advAngabeSkopusVerbAllg.imMittelfeldErlaubt() ?
                        advAngabeSkopusVerbAllg.getDescription(praedRegMerkmale)
                                .toKonstituentenfolge() :
                        // "erneut"
                        null;  // (ins Nachfeld verschieben)
        final Konstituentenfolge advAngabeSkopusVerbWohinWoherSynt =
                joinToKonstituentenfolge(
                        getAdvAngabeSkopusVerbWohinWoherDescription(praedRegMerkmale));
        final ImmutableList<ZuInfinitiv> lexKernZuInfinitive = lexikalischerKern.getZuInfinitiv(
                // Es liegt Subjektkontrolle vor.
                textContext,
                nachAnschlusswort
                        && (advAngabeSkopusSatz == null || !advAngabeSkopusSatz
                        .imMittelfeldErlaubt())
                        && getModalpartikeln().isEmpty()
                        && (advAngabeSkopusVerbAllg == null ||
                        // FIXME Bedingung richtig??? Unten genauso...
                        advAngabeSkopusVerbAllg.imMittelfeldErlaubt())
                        && getNegationspartikel() == null
                        && advAngabeSkopusVerbAllg != null
                        && !advAngabeSkopusVerbAllg.imMittelfeldErlaubt(),
                praedRegMerkmale);

        return new TopolFelder(
                new Mittelfeld(
                        Konstituentenfolge.joinToNullKonstituentenfolge(
                                advAngabeSkopusSatzSyntFuerMittelfeld,
                                // "aus einer Laune heraus"
                                kf(getModalpartikeln()), // "mal eben"
                                advAngabeSkopusVerbAllgSyntFuerMittelfeld, // "erneut"
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
                                        toKonstituentenfolge(lexKernZuInfinitive)
                                        // "ihre Haare wieder hinunterzulassen"
                                        : null,
                                // (Normalfall: lexikalischer Kern im Nachfeld)
                                advAngabeSkopusVerbWohinWoherSynt
                                // (kann es wohl gar nicht geben)
                        )
                ),
                new Nachfeld(
                        Konstituentenfolge.joinToNullKonstituentenfolge(
                                advAngabeSkopusVerbAllg == null
                                        || advAngabeSkopusVerbAllg.imMittelfeldErlaubt() ?
                                        toKonstituentenfolge(lexKernZuInfinitive)
                                        // "(Du versuchst) dich zu waschen"
                                        // Wir lassen die Kommata rund um den Infinitiv
                                        // weg - das
                                        // ist
                                        // erlaubt.
                                        : null,
                                advAngabeSkopusVerbAllg != null
                                        && !advAngabeSkopusVerbAllg.imMittelfeldErlaubt() ?
                                        advAngabeSkopusVerbAllg
                                                .getDescription(praedRegMerkmale)
                                        // "glücklich, dich zu sehen"
                                        : null,
                                advAngabeSkopusSatz != null
                                        && !advAngabeSkopusSatz.imMittelfeldErlaubt() ?
                                        advAngabeSkopusSatz.getDescription(praedRegMerkmale)
                                        : null)),
                getVorfeldAdvAngabeSkopusSatz(praedRegMerkmale),
                getSpeziellesVorfeldAlsWeitereOption(praedRegMerkmale,
                        lexKernZuInfinitive.get(0)),
                lexKernZuInfinitive.get(0).getRelativpronomen(),
                firstInterrogativwort(advAngabeSkopusSatzSyntFuerMittelfeld,
                        advAngabeSkopusVerbAllgSyntFuerMittelfeld,
                        advAngabeSkopusVerbWohinWoherSynt,
                        lexKernZuInfinitive.get(0).getErstesInterrogativwort()));
    }

    @Override
    public boolean umfasstSatzglieder() {
        return super.umfasstSatzglieder() || lexikalischerKern.umfasstSatzglieder();
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
        final SemPraedikatIntentionalesVerbOhneLeerstellen that =
                (SemPraedikatIntentionalesVerbOhneLeerstellen) o;
        return lexikalischerKern.equals(that.lexikalischerKern);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), lexikalischerKern);
    }
}
