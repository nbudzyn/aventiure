package de.nb.aventiure2.german.praedikat;

import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import de.nb.aventiure2.german.base.KasusOderPraepositionalkasus;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

/**
 * Ein Prädikat, in dem genau nur für ein Objekt eine Leerstelle besteht.
 */
class PraedikatMitEinerObjLeerstelle implements PraedikatMitEinerObjektleerstelle {
    /**
     * Das Verb an sich, ohne Informationen zur Valenz, ohne Ergänzungen, ohne
     * Angaben
     */
    private final Verb verb;

    /**
     * Der Kasus (z.B. Akkusativ, "die Kugel nehmen") oder Präpositionalkasus
     * (z.B. "mit dem Frosch reden"), mit dem dieses Verb steht (den dieses Verb regiert).
     */
    private final KasusOderPraepositionalkasus kasusOderPraepositionalkasus;

    @Nullable
    private final AdverbialeAngabeSkopusVerbWohinWoher adverbialeAngabeSkopusVerbWohinWoher;

    PraedikatMitEinerObjLeerstelle(
            final Verb verb,
            final KasusOderPraepositionalkasus kasusOderPraepositionalkasus,
            @Nullable final AdverbialeAngabeSkopusVerbWohinWoher
                    adverbialeAngabeSkopusVerbWohinWoher) {
        this.verb = verb;
        this.kasusOderPraepositionalkasus = kasusOderPraepositionalkasus;
        this.adverbialeAngabeSkopusVerbWohinWoher = adverbialeAngabeSkopusVerbWohinWoher;
    }

    public PraedikatMitEinerObjLeerstelle mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusVerbWohinWoher adverbialeAngabe) {
        if (adverbialeAngabe == null) {
            return this;
        }

        return new PraedikatMitEinerObjLeerstelle(
                verb, kasusOderPraepositionalkasus,
                adverbialeAngabe
        );
    }

    @Override
    public PraedikatSubjObjOhneLeerstellen mit(
            final SubstantivischePhrase describable) {
        return new PraedikatSubjObjOhneLeerstellen(verb,
                kasusOderPraepositionalkasus,
                describable, ImmutableList.of(),
                null, null,
                adverbialeAngabeSkopusVerbWohinWoher);
    }
}
