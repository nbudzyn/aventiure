package de.nb.aventiure2.german.praedikat;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.nb.aventiure2.annotations.Komplement;
import de.nb.aventiure2.annotations.Valenz;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.SubstantivischePhraseOderReflexivpronomen;

import static de.nb.aventiure2.german.base.Konstituentenfolge.kf;

/**
 * Ein Prädikat eines <i>Verbs mit intentionaler Bedeutung</i>,
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
 */
public class PraedikatIntentionalesVerbOhneLeerstellen
        extends AbstractAngabenfaehigesPraedikatOhneLeerstellen {
    /**
     * "(...versucht) ihre Haare wieder hinunterzulassen"
     */
    @Komplement
    @Nonnull
    private final PraedikatOhneLeerstellen lexikalischerKern;

    @Valenz
    PraedikatIntentionalesVerbOhneLeerstellen(
            final Verb verb,
            final PraedikatOhneLeerstellen lexikalischerKern) {
        this(verb, ImmutableList.of(),
                null, null,
                null, lexikalischerKern);
    }

    private PraedikatIntentionalesVerbOhneLeerstellen(
            final Verb verb,
            final Iterable<Modalpartikel> modalpartikeln,
            @Nullable final AdverbialeAngabeSkopusSatz adverbialeAngabeSkopusSatz,
            @Nullable final AdverbialeAngabeSkopusVerbAllg adverbialeAngabeSkopusVerbAllg,
            @Nullable
            final AdverbialeAngabeSkopusVerbWohinWoher adverbialeAngabeSkopusVerbWohinWoher,
            final PraedikatOhneLeerstellen lexikalischerKern) {
        super(verb, modalpartikeln, adverbialeAngabeSkopusSatz,
                adverbialeAngabeSkopusVerbAllg, adverbialeAngabeSkopusVerbWohinWoher);
        this.lexikalischerKern = lexikalischerKern;
    }

    @Override
    public PraedikatIntentionalesVerbOhneLeerstellen mitModalpartikeln(
            final Collection<Modalpartikel> modalpartikeln) {
        return new PraedikatIntentionalesVerbOhneLeerstellen(
                getVerb(),
                Iterables.concat(getModalpartikeln(), modalpartikeln),
                getAdverbialeAngabeSkopusSatz(),
                getAdverbialeAngabeSkopusVerbAllg(),
                getAdverbialeAngabeSkopusVerbWohinWoher(),
                lexikalischerKern
        );
    }


    @Override
    public PraedikatIntentionalesVerbOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusSatz adverbialeAngabe) {
        if (adverbialeAngabe == null) {
            return this;
        }

        return new PraedikatIntentionalesVerbOhneLeerstellen(
                getVerb(),
                getModalpartikeln(),
                adverbialeAngabe, getAdverbialeAngabeSkopusVerbAllg(),
                getAdverbialeAngabeSkopusVerbWohinWoher(),
                lexikalischerKern);
    }

    @Override
    public PraedikatIntentionalesVerbOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusVerbAllg adverbialeAngabe) {
        if (adverbialeAngabe == null) {
            return this;
        }

        return new PraedikatIntentionalesVerbOhneLeerstellen(
                getVerb(),
                getModalpartikeln(),
                getAdverbialeAngabeSkopusSatz(), adverbialeAngabe,
                getAdverbialeAngabeSkopusVerbWohinWoher(),
                lexikalischerKern
        );
    }

    @Override
    public PraedikatIntentionalesVerbOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusVerbWohinWoher adverbialeAngabe) {
        if (adverbialeAngabe == null) {
            return this;
        }

        return new PraedikatIntentionalesVerbOhneLeerstellen(
                getVerb(),
                getModalpartikeln(),
                getAdverbialeAngabeSkopusSatz(),
                getAdverbialeAngabeSkopusVerbAllg(),
                adverbialeAngabe,
                lexikalischerKern
        );
    }


    @Override
    public boolean kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden() {
        // "Gebeten dich zu waschen [gehst du ins Bad]"
        return true;
    }

    @Nullable
    @Override
    public Konstituente getSpeziellesVorfeldAlsWeitereOption(
            final Person person, final Numerus numerus) {
        @Nullable final Konstituente speziellesVorfeldFromSuper =
                super.getSpeziellesVorfeldAlsWeitereOption(person,
                        numerus);
        if (speziellesVorfeldFromSuper != null) {
            return speziellesVorfeldFromSuper;
        }

        // "Ihre Haare (versucht sie wieder hinunterzulassen)"
        return lexikalischerKern.getSpeziellesVorfeldAlsWeitereOption(person, numerus);
    }

    @Override
    Konstituentenfolge getMittelfeldOhneLinksversetzungUnbetonterPronomen(
            final Person personSubjekt, final Numerus numerusSubjekt) {
        @Nullable final AdverbialeAngabeSkopusSatz adverbialeAngabeSkopusSatz =
                getAdverbialeAngabeSkopusSatz();
        @Nullable final AdverbialeAngabeSkopusVerbAllg adverbialeAngabeSkopusVerbAllg =
                getAdverbialeAngabeSkopusVerbAllg();

        return Konstituentenfolge.joinToNullKonstituentenfolge(
                adverbialeAngabeSkopusSatz != null &&
                        adverbialeAngabeSkopusSatz.imMittelfeldErlaubt() ?
                        adverbialeAngabeSkopusSatz.getDescription(personSubjekt, numerusSubjekt) :
                        // "aus einer Laune heraus"
                        null, // (ins Nachfeld verschieben)
                kf(getModalpartikeln()), // "mal eben"
                adverbialeAngabeSkopusVerbAllg != null &&
                        adverbialeAngabeSkopusVerbAllg.imMittelfeldErlaubt() ?
                        adverbialeAngabeSkopusVerbAllg.getDescription(personSubjekt,
                                numerusSubjekt) :  // "erneut"
                        null, // (ins Nachfeld verschieben)
                adverbialeAngabeSkopusVerbAllg != null &&
                        !adverbialeAngabeSkopusVerbAllg.imMittelfeldErlaubt()
                        // Die adverbialeAngabeSkopusSatz schieben wir immer ins Nachfeld,
                        // daraus wird sie nach Möglichkeit ins Vorfeld gezogen werden.
                        ?
                        // -> Lex. Kern sollten wir aus dem Nachfeld vorziehen
                        lexikalischerKern.getZuInfinitiv(
                                // Es liegt Subjektkontrolle vor.
                                personSubjekt, numerusSubjekt
                        ) // "ihre Haare wieder hinunterzulassen"
                        : null, // (Normalfall: lexikalischer Kern im Nachfeld)
                getAdverbialeAngabeSkopusVerbWohinWoherDescription(personSubjekt,
                        numerusSubjekt)
                // (kann es wohl gar nicht geben)
        );
    }

    @Nullable
    @Override
    SubstantivischePhraseOderReflexivpronomen getDat(
            final Person personSubjekt, final Numerus numerusSubjekt) {
        return null;
    }

    @Nullable
    @Override
    SubstantivischePhraseOderReflexivpronomen getAkk(
            final Person personSubjekt, final Numerus numerusSubjekt) {
        return null;
    }

    @Nullable
    @Override
    SubstantivischePhraseOderReflexivpronomen getZweitesAkk() {
        return null;
    }

    @Override
    public Konstituentenfolge getNachfeld(final Person personSubjekt,
                                          final Numerus numerusSubjekt) {
        @Nullable final AdverbialeAngabeSkopusSatz adverbialeAngabeSkopusSatz =
                getAdverbialeAngabeSkopusSatz();
        @Nullable final AdverbialeAngabeSkopusVerbAllg adverbialeAngabeSkopusVerbAllg =
                getAdverbialeAngabeSkopusVerbAllg();
        return Konstituentenfolge.joinToNullKonstituentenfolge(
                adverbialeAngabeSkopusVerbAllg == null
                        || adverbialeAngabeSkopusVerbAllg.imMittelfeldErlaubt() ?
                        lexikalischerKern.getZuInfinitiv(
                                // Es liegt Subjektkontrolle vor.
                                personSubjekt, numerusSubjekt
                        ) // "(Du versuchst) dich zu waschen"
                        // Wir lassen die Kommata rund um den Infinitiv weg - das ist erlaubt.
                        : null,
                adverbialeAngabeSkopusVerbAllg != null
                        && !adverbialeAngabeSkopusVerbAllg.imMittelfeldErlaubt() ?
                        adverbialeAngabeSkopusVerbAllg
                                .getDescription(personSubjekt, numerusSubjekt)
                        // "glücklich, dich zu sehen"
                        : null,
                adverbialeAngabeSkopusSatz != null
                        && !adverbialeAngabeSkopusSatz.imMittelfeldErlaubt() ?
                        adverbialeAngabeSkopusSatz
                                .getDescription(personSubjekt, numerusSubjekt)
                        : null);
    }

    @Override
    public boolean umfasstSatzglieder() {
        return super.umfasstSatzglieder() || lexikalischerKern.umfasstSatzglieder();
    }

    @Nullable
    @Override
    public Konstituente getErstesInterrogativpronomen() {
        return lexikalischerKern.getErstesInterrogativpronomen();
    }
}
