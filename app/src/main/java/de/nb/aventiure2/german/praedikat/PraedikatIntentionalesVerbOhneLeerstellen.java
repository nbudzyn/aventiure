package de.nb.aventiure2.german.praedikat;


import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.nb.aventiure2.annotations.Argument;
import de.nb.aventiure2.annotations.Valenz;
import de.nb.aventiure2.german.base.GermanUtil;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;

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
    @Argument
    @Nonnull
    private final PraedikatOhneLeerstellen lexikalischerKern;

    @Valenz
    PraedikatIntentionalesVerbOhneLeerstellen(
            final Verb verb,
            final PraedikatOhneLeerstellen lexikalischerKern) {
        this(verb,
                null, null,
                null, lexikalischerKern);
    }

    private PraedikatIntentionalesVerbOhneLeerstellen(
            final Verb verb,
            @Nullable final AdverbialeAngabeSkopusSatz adverbialeAngabeSkopusSatz,
            @Nullable final AdverbialeAngabeSkopusVerbAllg adverbialeAngabeSkopusVerbAllg,
            @Nullable
            final AdverbialeAngabeSkopusVerbWohinWoher adverbialeAngabeSkopusVerbWohinWoher,
            final PraedikatOhneLeerstellen lexikalischerKern) {
        super(verb, adverbialeAngabeSkopusSatz,
                adverbialeAngabeSkopusVerbAllg, adverbialeAngabeSkopusVerbWohinWoher);
        this.lexikalischerKern = lexikalischerKern;
    }

    @Override
    public PraedikatIntentionalesVerbOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusSatz adverbialeAngabe) {
        if (adverbialeAngabe == null) {
            return this;
        }

        return new PraedikatIntentionalesVerbOhneLeerstellen(
                getVerb(),
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
                getAdverbialeAngabeSkopusSatz(),
                getAdverbialeAngabeSkopusVerbAllg(),
                adverbialeAngabe,
                lexikalischerKern
        );
    }

    @Override
    public boolean duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen() {
        return true;
    }

    @Override
    public boolean kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden() {
        // "Gebeten dich zu waschen [gehst du ins Bad]"
        return true;
    }
    
    @Nullable
    @Override
    public Konstituente getSpeziellesVorfeld(final Person person,
                                             final Numerus numerus) {
        @Nullable final Konstituente speziellesVorfeldFromSuper = super.getSpeziellesVorfeld(person,
                numerus);
        if (speziellesVorfeldFromSuper != null) {
            return speziellesVorfeldFromSuper;
        }

        @Nullable final AdverbialeAngabeSkopusVerbAllg adverbialeAngabeSkopusVerbAllg =
                getAdverbialeAngabeSkopusVerbAllg();
        if (adverbialeAngabeSkopusVerbAllg != null
                && !adverbialeAngabeSkopusVerbAllg.imMittelfeldErlaubt()) {
            @Nullable final Konstituente
                    adverbialeAngabeSkopusVerbAllgDescriptionFuerZwangsausklammerung =
                    getAdverbialeAngabeSkopusVerbAllgDescriptionFuerZwangsausklammerung();
            if (adverbialeAngabeSkopusVerbAllgDescriptionFuerZwangsausklammerung != null) {
                return adverbialeAngabeSkopusVerbAllgDescriptionFuerZwangsausklammerung
                        .withVorkommaNoetig(false);
                // "Und glücklich, dich zu sehen, ..."
            }
        }

        // "Ihre Haare (versucht sie wieder hinunterzulassen)"
        return lexikalischerKern.getSpeziellesVorfeld(person, numerus);
    }

    @Override
    public Iterable<Konstituente> getMittelfeld(final Collection<Modalpartikel> modalpartikeln,
                                                final Person personSubjekt,
                                                final Numerus numerusSubjekt) {
        @Nullable final AdverbialeAngabeSkopusVerbAllg adverbialeAngabeSkopusVerbAllg =
                getAdverbialeAngabeSkopusVerbAllg();

        if (adverbialeAngabeSkopusVerbAllg == null ||
                adverbialeAngabeSkopusVerbAllg.imMittelfeldErlaubt()) {
            return Konstituente.joinToKonstituenten(
                    getAdverbialeAngabeSkopusSatzDescription(), // "aus einer Laune heraus"
                    GermanUtil.joinToNullString(modalpartikeln), // "mal eben"
                    adverbialeAngabeSkopusVerbAllg.getDescription(), // "erneut"
                    getAdverbialeAngabeSkopusVerbWohinWoherDescription()
                    // (kann es wohl gar nicht geben)
            );

            // STORY Der lexikalische Kern könnte als Alternative zusätztlich ins Mittelfeld gestellt
            //  werden: "ihre Haare wieder hinunterzulassen versuchen"
        }

        return Konstituente.joinToKonstituenten(
                getAdverbialeAngabeSkopusSatzDescription(), // "aus einer Laune heraus"
                GermanUtil.joinToNullString(modalpartikeln), // "mal eben"
                adverbialeAngabeSkopusVerbAllg.getDescription(), // "erneut"
                lexikalischerKern.getZuInfinitiv(
                        // Es liegt "Subjektkontrolle" vor.
                        personSubjekt, numerusSubjekt
                ) // "ihre Haare wieder hinunterzulassen"
                // (kann es wohl gar nicht geben)
        );
    }

    @Override
    public Iterable<Konstituente> getNachfeld(final Person personSubjekt,
                                              final Numerus numerusSubjekt) {
        @Nullable final AdverbialeAngabeSkopusVerbAllg adverbialeAngabeSkopusVerbAllg =
                getAdverbialeAngabeSkopusVerbAllg();
        if (adverbialeAngabeSkopusVerbAllg != null
                && !adverbialeAngabeSkopusVerbAllg.imMittelfeldErlaubt()) {
            return Konstituente.joinToKonstituenten(
                    adverbialeAngabeSkopusVerbAllg.getDescription() // "glücklich, dich zu sehen"
            );
        }

        return lexikalischerKern.getZuInfinitiv(
                // Es liegt "Subjektkontrolle" vor.
                personSubjekt, numerusSubjekt
        ); // "(Du versuchst) dich zu waschen"
        // Wir lassen die Kommata rund um den Infinitiv weg - das ist erlaubt.
    }

    @Override
    public boolean umfasstSatzglieder() {
        return super.umfasstSatzglieder() || lexikalischerKern.umfasstSatzglieder();
    }

    @Override
    public boolean hatAkkusativobjekt() {
        return false;
    }

    @Nullable
    @Override
    public Konstituente getErstesInterrogativpronomen() {
        return lexikalischerKern.getErstesInterrogativpronomen();
    }
}
