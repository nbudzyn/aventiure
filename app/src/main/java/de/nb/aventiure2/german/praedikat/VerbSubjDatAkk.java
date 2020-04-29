package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import de.nb.aventiure2.german.base.DeklinierbarePhrase;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;

/**
 * Ein Verb (ggf. mit Präfix), das genau mit einem Subjekt, einem Dativobjekt und
 * einem Akkusativ-Objekt steht.
 */
public enum VerbSubjDatAkk implements Praedikat {
    // "dem Frosch Angebote machen"
    MACHEN("machen", "machst"),
    VERSPRECHEN("versprechen", "versprichst"),
    ;

    /**
     * Das Verb an sich, ohne Informationen zur Valenz, ohne Ergänzungen, ohne
     * Angaben
     */
    @NonNull
    private final Verb verb;

    VerbSubjDatAkk(@NonNull final String infinitiv,
                   @NonNull final String duForm) {
        this(new Verb(infinitiv, duForm));
    }

    VerbSubjDatAkk(@NonNull final String infinitiv,
                   @NonNull final String duForm,
                   @Nullable final String partikel) {
        this(new Verb(infinitiv, duForm, partikel));
    }

    VerbSubjDatAkk(final Verb verb) {
        this.verb = verb;
    }

    /**
     * Gibt einen Satz zurück mit diesem Verb und diesen Objekten zurück.
     * ("Du machst dem Frosch Angebote")
     */
    public String getDescriptionHauptsatz(
            final DeklinierbarePhrase describableDat,
            final DeklinierbarePhrase describableAkk) {
        return mitDat(describableDat).getDescriptionDuHauptsatz(describableAkk);
    }

    /**
     * Gibt eine Infinitivkonstruktion zurück mit diesem Verb und diesen Objekten.
     * ("dem Frosch Angebote machen")
     */
    public String getDescriptionInfinitiv(final Person person, final Numerus numerus,
                                          final DeklinierbarePhrase describableDat,
                                          final DeklinierbarePhrase describableAkk) {
        return mitDat(describableDat).getDescriptionInfinitiv(
                person, numerus, describableAkk);
    }

    /**
     * Gibt eine Infinitivkonstruktion zurück mit diesem Verb und diesen Objekten.
     * ("dem Frosch Angebote zu machen")
     */
    public String getDescriptionZuInfinitiv(final Person person, final Numerus numerus,
                                            final DeklinierbarePhrase describableDat,
                                            final DeklinierbarePhrase describableAkk) {
        return mitDat(describableDat).getDescriptionInfinitiv(
                person, numerus, describableAkk);
    }

    public PraedikatMitEinerObjektleerstelle mitDat(
            final DeklinierbarePhrase describableDat) {
        return new PraedikatDatMitEinerAkkLeerstelle(verb,
                describableDat);
    }

    public PraedikatMitEinerObjektleerstelle mitAkk(
            final DeklinierbarePhrase describableAkk) {
        return new PraedikatAkkMitEinerDatLeerstelle(verb,
                describableAkk);
    }
}
