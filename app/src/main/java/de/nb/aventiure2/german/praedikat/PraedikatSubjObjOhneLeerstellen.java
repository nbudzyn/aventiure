package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import de.nb.aventiure2.german.base.DeklinierbarePhrase;
import de.nb.aventiure2.german.base.KasusOderPraepositionalkasus;

import static de.nb.aventiure2.german.base.GermanUtil.capitalize;

/**
 * Ein Prädikat (Verb ggf. mit Präfix) bei dem das Verb mit einem Subjekt und einem
 * (Präpositional-) Objekt steht - alle Leerstellen sind besetzt.
 */
class PraedikatSubjObjOhneLeerstellen implements PraedikatOhneLeerstellen {
    /**
     * Infinitiv des Verbs ("aufheben")
     */
    @NonNull
    private final String infinitiv;

    /**
     * Der Kasus (z.B. Akkusativ, "die Kugel nehmen") oder Präpositionalkasus
     * (z.B. "mit dem Frosch reden"), mit dem dieses Verb steht (den dieses Verb regiert).
     */
    @NonNull
    private final KasusOderPraepositionalkasus kasusOderPraepositionalkasus;

    /**
     * 2. Person Singular Präsens Indikativ des Verbs, ggf. ohne abgetrenntes Präfix
     * ("hebst")
     */
    @NonNull
    private final String duForm;

    /**
     * Ggf. das abgetrennte Präfix des Verbs ("auf").
     * <p>
     * Wird das Präfix <i>nicht</i> abgetrennt ("ver"), ist dieses Feld <code>null</code>.
     */
    @Nullable
    private final String abgetrenntesPraefix;

    /**
     * Das Objekt (z.B. ein Ding, Wesen, Konzept oder deklinierbare Phrase)
     */
    private final DeklinierbarePhrase objekt;


    public PraedikatSubjObjOhneLeerstellen(final String infinitiv, final String duForm,
                                           final String abgetrenntesPraefix,
                                           final KasusOderPraepositionalkasus kasusOderPraepositionalkasus,
                                           final DeklinierbarePhrase objekt) {
        this.infinitiv = infinitiv;
        this.duForm = duForm;
        this.abgetrenntesPraefix = abgetrenntesPraefix;
        this.kasusOderPraepositionalkasus = kasusOderPraepositionalkasus;
        this.objekt = objekt;
    }

    /**
     * Gibt einen Satz zurück mit diesem Prädikat.
     * ("Du nimmst den Ast")
     */
    @Override
    public String getDescriptionDuHauptsatz() {
        if (abgetrenntesPraefix == null) {
            return "Du " + getDescriptionHauptsatzMitEingespartemVorfeldSubj();
        }

        return "Du " + getDescriptionHauptsatzMitEingespartemVorfeldSubj();
    }

    @Override
    public boolean duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen() {
        return true;
    }

    /**
     * Gibt einen Satz zurück mit diesem Prädikat, bei dem das Subjekt, das im Vorfeld
     * stünde, eingespart ist ("nimmst den Ast")
     */
    public String getDescriptionHauptsatzMitEingespartemVorfeldSubj() {
        if (abgetrenntesPraefix == null) {
            return duForm +
                    " " + objekt.im(kasusOderPraepositionalkasus);
        }

        return duForm +
                " " + objekt.im(kasusOderPraepositionalkasus) +
                " " + abgetrenntesPraefix;
    }

    /**
     * Gibt einen Satz zurück mit diesem Prädikat und dieser adverbialen Angabe.
     * ("Aus Langeweile nimmst du den Ast")
     */
    @Override
    public String getDescriptionDuHauptsatz(@NonNull final AdverbialeAngabe adverbialeAngabe) {
        if (abgetrenntesPraefix == null) {
            return capitalize(adverbialeAngabe.getText()) +
                    " " + duForm +
                    " du " +
                    objekt.im(kasusOderPraepositionalkasus);
        }

        return capitalize(adverbialeAngabe.getText()) +
                " " + duForm +
                " du " +
                objekt.im(kasusOderPraepositionalkasus) +
                " " + abgetrenntesPraefix;
    }

    /**
     * Gibt eine Infinitivkonstruktion zurück mit Prädikat.
     * ("Den Frosch ignorieren", "Das Leben genießen")
     */
    @Override
    public String getDescriptionInfinitiv() {
        return capitalize(objekt.im(kasusOderPraepositionalkasus) +
                " " + infinitiv);
    }

    @NonNull
    public KasusOderPraepositionalkasus getKasusOderPraepositionalkasus() {
        return kasusOderPraepositionalkasus;
    }

    public DeklinierbarePhrase getObjekt() {
        return objekt;
    }
}
