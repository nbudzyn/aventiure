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
    ANBIETEN("anbieten", "bietest", "an",
            Perfektbildung.HABEN, "angeboten"),
    AUSSCHUETTEN("ausschütten", "schüttest", "aus",
            Perfektbildung.HABEN, "ausgeschüttet"),
    GEBEN("geben", "gibst", Perfektbildung.HABEN, "gegeben"),
    HINHALTEN("hinhalten", "hältst", "in",
            Perfektbildung.HABEN, "hingehalten"),
    // "dem Frosch Angebote machen"
    MACHEN("machen", "machst", Perfektbildung.HABEN, "gemacht"),
    REICHEN("reichen", "reichst", Perfektbildung.HABEN, "gereicht"),
    VERSPRECHEN("versprechen", "versprichst",
            Perfektbildung.HABEN, "versprochen"),
    ZEIGEN("zeigen", "zeigst", Perfektbildung.HABEN, "gezeigt"),
    ;

    /**
     * Das Verb an sich, ohne Informationen zur Valenz, ohne Ergänzungen, ohne
     * Angaben
     */
    @NonNull
    private final Verb verb;

    VerbSubjDatAkk(@NonNull final String infinitiv,
                   @NonNull final String duForm,
                   final Perfektbildung perfektbildung, final String partizipII) {
        this(new Verb(infinitiv, duForm, perfektbildung, partizipII));
    }

    VerbSubjDatAkk(@NonNull final String infinitiv,
                   @NonNull final String duForm,
                   @Nullable final String partikel,
                   final Perfektbildung perfektbildung, final String partizipII) {
        this(new Verb(infinitiv, duForm, partikel, perfektbildung, partizipII));
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
        return mitDat(describableDat).mit(describableAkk).getDuHauptsatz();
    }

    /**
     * Gibt eine Infinitivkonstruktion zurück mit diesem Verb und diesen Objekten.
     * ("dem Frosch Angebote machen")
     */
    public String getDescriptionInfinitiv(final Person person, final Numerus numerus,
                                          final SubstantivischePhrase describableDat,
                                          final SubstantivischePhrase describableAkk) {
        return mitDat(describableDat).mit(describableAkk).getInfinitiv(person, numerus);
    }

    /**
     * Gibt eine Infinitivkonstruktion zurück mit diesem Verb und diesen Objekten.
     * ("dem Frosch Angebote zu machen")
     */
    public String getDescriptionZuInfinitiv(final Person person, final Numerus numerus,
                                            final SubstantivischePhrase describableDat,
                                            final SubstantivischePhrase describableAkk) {
        return mitDat(describableDat).mit(describableAkk).getInfinitiv(person, numerus);
    }

    public PraedikatMitEinerObjektleerstelle mitDat(
            final SubstantivischePhrase describableDat) {
        return new PraedikatDatAkkMitEinerAkkLeerstelle(verb,
                describableDat);
    }

    public PraedikatMitEinerObjektleerstelle mitAkk(
            final SubstantivischePhrase describableAkk) {
        return new PraedikatDatAkkMitEinerDatLeerstelle(verb,
                describableAkk);
    }
}
