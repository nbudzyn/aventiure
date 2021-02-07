package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import javax.annotation.Nonnull;

import de.nb.aventiure2.german.base.KasusOderPraepositionalkasus;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.satz.Satz;

import static de.nb.aventiure2.german.base.Kasus.AKK;

/**
 * Ein Verb wie <i>jn. fragen, ob / wer / wie /... </i> oder <i>mit jm. diskutieren, ob / wer /
 * wie / ... </i>
 */
public enum VerbSubjObjIndirekterFragesatz implements VerbMitValenz {
    // "Rapunzel fragt die Zauberin, wie sie sich fühlt"
    FRAGEN_OB_W("fragen", AKK,
            "frage", "fragst", "fragt", "fragt",
            Perfektbildung.HABEN, "gefragt");
    // Weitere solche Verben sind mitteilen, berichten, prüfen

    @Nonnull
    private final KasusOderPraepositionalkasus kasusOderPraepositionalkasus;

    /**
     * Das Verb an sich, ohne Informationen zur Valenz, ohne Ergänzungen, ohne
     * Angaben
     */
    @NonNull
    private final Verb verb;

    VerbSubjObjIndirekterFragesatz(@NonNull final String infinitiv,
                                   final KasusOderPraepositionalkasus kasusOderPraepositionalkasus,
                                   @NonNull final String ichForm,
                                   @NonNull final String duForm,
                                   @NonNull final String erSieEsForm,
                                   @NonNull final String ihrForm,
                                   final Perfektbildung perfektbildung, final String partizipII) {
        this(new Verb(infinitiv, ichForm, duForm, erSieEsForm, ihrForm, perfektbildung,
                partizipII), kasusOderPraepositionalkasus);
    }

    VerbSubjObjIndirekterFragesatz(@NonNull final Verb verb,
                                   final KasusOderPraepositionalkasus kasusOderPraepositionalkasus) {
        this.verb = verb;
        this.kasusOderPraepositionalkasus = kasusOderPraepositionalkasus;
    }

    public PraedikatSubjObjIndirekterFragesatzMitEinerIndirekterFragesatzLeerstelle mitObjekt(
            final SubstantivischePhrase objekt) {
        return new PraedikatSubjObjIndirekterFragesatzMitEinerIndirekterFragesatzLeerstelle(
                verb, kasusOderPraepositionalkasus, objekt);
    }

    public PraedikatSubjObjIndirekterFragesatzMitEinerObjektLeerstelle mitIndirekterFragesatz(
            final Satz indirekterFragesatz) {
        return new PraedikatSubjObjIndirekterFragesatzMitEinerObjektLeerstelle(
                verb, kasusOderPraepositionalkasus, indirekterFragesatz);
    }

    @Override
    @NonNull
    public Verb getVerb() {
        return verb;
    }
}
