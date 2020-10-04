package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

/**
 * Ein Verb (ggf. mit Präfix), das genau mit einem Subjekt, einem Dativobjekt und
 * einem Akkusativ-Objekt steht.
 */
public enum VerbSubjDatAkk implements Praedikat {
    ANBIETEN("anbieten", "bietest", "an"),
    GEBEN("geben", "gibst"),
    HINHALTEN("hinhalten", "hältst", "in"),
    // "dem Frosch Angebote machen"
    MACHEN("machen", "machst"),
    REICHEN("reichen", "reichst"),
    VERSPRECHEN("versprechen", "versprichst"),
    ZEIGEN("zeigen", "zeigst"),
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
            final SubstantivischePhrase describableDat,
            final SubstantivischePhrase describableAkk) {
        return mitDat(describableDat).mitObj(describableAkk).getDuHauptsatz();
    }

    /**
     * Gibt eine Infinitivkonstruktion zurück mit diesem Verb und diesen Objekten.
     * ("dem Frosch Angebote machen")
     */
    public String getDescriptionInfinitiv(final Person person, final Numerus numerus,
                                          final SubstantivischePhrase describableDat,
                                          final SubstantivischePhrase describableAkk) {
        return mitDat(describableDat).mitObj(describableAkk).getInfinitiv(person, numerus);
    }

    /**
     * Gibt eine Infinitivkonstruktion zurück mit diesem Verb und diesen Objekten.
     * ("dem Frosch Angebote zu machen")
     */
    public String getDescriptionZuInfinitiv(final Person person, final Numerus numerus,
                                            final SubstantivischePhrase describableDat,
                                            final SubstantivischePhrase describableAkk) {
        return mitDat(describableDat).mitObj(describableAkk).getInfinitiv(person, numerus);
    }

    public PraedikatMitEinerObjektleerstelle mitDat(
            final SubstantivischePhrase describableDat) {
        return new PraedikatDatMitEinerAkkLeerstelle(verb,
                describableDat);
    }

    public PraedikatMitEinerObjektleerstelle mitAkk(
            final SubstantivischePhrase describableAkk) {
        return new PraedikatAkkMitEinerDatLeerstelle(verb,
                describableAkk);
    }
}
