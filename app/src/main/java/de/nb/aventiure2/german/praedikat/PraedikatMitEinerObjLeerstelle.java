package de.nb.aventiure2.german.praedikat;

import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativSkopusSatz;
import de.nb.aventiure2.german.base.KasusOderPraepositionalkasus;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

/**
 * Ein Prädikat, in dem genau nur für ein Objekt eine Leerstelle besteht.
 */
public class PraedikatMitEinerObjLeerstelle implements PraedikatMitEinerObjektleerstelle {
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

    /**
     * Gibt an, ob dieses Prädikat in der Regel ohne Subjekt steht
     * ("Mich friert"), aber optional ein expletives "es" möglich ist
     * ("Es friert mich").
     */
    private final boolean inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich;

    @Nullable
    private final IAdvAngabeOderInterrogativSkopusSatz advAngabeSkopusSatz;

    @Nullable
    private final AdvAngabeSkopusVerbWohinWoher advAngabeSkopusVerbWohinWoher;

    PraedikatMitEinerObjLeerstelle(
            final Verb verb,
            final KasusOderPraepositionalkasus kasusOderPraepositionalkasus,
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabeSkopusSatz,
            @Nullable final AdvAngabeSkopusVerbWohinWoher advAngabeSkopusVerbWohinWoher) {
        this(verb, kasusOderPraepositionalkasus,
                false,
                advAngabeSkopusSatz,
                advAngabeSkopusVerbWohinWoher);
    }

    PraedikatMitEinerObjLeerstelle(
            final Verb verb,
            final KasusOderPraepositionalkasus kasusOderPraepositionalkasus,
            final boolean inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich,
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabeSkopusSatz,
            @Nullable final AdvAngabeSkopusVerbWohinWoher advAngabeSkopusVerbWohinWoher) {
        this.verb = verb;
        this.kasusOderPraepositionalkasus = kasusOderPraepositionalkasus;
        this.inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich =
                inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich;
        this.advAngabeSkopusSatz = advAngabeSkopusSatz;
        this.advAngabeSkopusVerbWohinWoher = advAngabeSkopusVerbWohinWoher;
    }

    public PraedikatMitEinerObjLeerstelle mitAdvAngabe(
            @Nullable final AdvAngabeSkopusVerbWohinWoher advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        return new PraedikatMitEinerObjLeerstelle(
                verb, kasusOderPraepositionalkasus,
                inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich,
                advAngabeSkopusSatz,
                advAngabe
        );
    }

    public PraedikatMitEinerObjLeerstelle mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        return new PraedikatMitEinerObjLeerstelle(
                verb, kasusOderPraepositionalkasus,
                inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich,
                advAngabeSkopusSatz,
                advAngabeSkopusVerbWohinWoher
        );
    }

    @Override
    public PraedikatSubjObjOhneLeerstellen mit(
            final SubstantivischePhrase substPhr) {
        return new PraedikatSubjObjOhneLeerstellen(verb,
                kasusOderPraepositionalkasus,
                inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich,
                substPhr, ImmutableList.of(),
                advAngabeSkopusSatz, null, null,
                advAngabeSkopusVerbWohinWoher);
    }
}
