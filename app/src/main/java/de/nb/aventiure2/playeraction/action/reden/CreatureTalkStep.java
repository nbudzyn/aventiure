package de.nb.aventiure2.playeraction.action.reden;

import de.nb.aventiure2.data.world.creature.Creature;
import de.nb.aventiure2.data.world.creature.CreatureState;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.praedikat.Praedikat;
import de.nb.aventiure2.german.praedikat.PraedikatOhneLeerstellen;
import de.nb.aventiure2.german.praedikat.VerbSubjObj;

/**
 * Ein Schritt in einem Gespräch mit einer {@link Creature}.
 * Führt in der Regel von einem {@link CreatureState} zu einem
 * anderen, oft im Rahmen einer {@link de.nb.aventiure2.playeraction.action.RedenAction}.
 * <p>
 * Das Konzept ist ähnliche wie die
 * {@link de.nb.aventiure2.data.world.room.connection.RoomConnection}, die
 * von einem {@link de.nb.aventiure2.data.world.room.AvRoom} zum anderen führt,
 * oft im Rahmen einer {@link de.nb.aventiure2.playeraction.action.BewegenAction}.
 */
public class CreatureTalkStep {
    /**
     * Condition to check whether a <code>CreatureTalkStep</code> is possible / allowed.
     */
    interface TalkStepCondition {
        /**
         * Return true iff the step is possible / allowed.
         */
        boolean isStepPossible();
    }

    /**
     * This ist what the talk step actually does.
     */
    interface TalkStepNarrationAndAction {
        /**
         * Führt den {@link CreatureTalkStep} aus - erzählt die Geschichte weiter
         * und verändert ggf. die Welt.
         */
        void narrateAndDo();
    }

    final static PraedikatOhneLeerstellen DEFAULT_EXIT_NAME =
            VerbSubjObj.BEENDEN.mitObj(Nominalphrase.GESPRAECH);

    final static TalkStepCondition ALWAYS_POSSIBLE = () -> true;

    /**
     * Ob der Spieler mit diesem Schritt das Gespräch beenden möchte.
     */
    private final boolean exitStep;

    /**
     * Prädikat für den Namen des Schritts, z.B. ("Mit dem Frosch reden")
     */
    private final Praedikat name;

    /**
     * This ist what the talk step actually does.
     */
    private final TalkStepNarrationAndAction narrationAndAction;

    private final TalkStepCondition condition;

    CreatureTalkStep(
            final boolean exitStep,
            final TalkStepCondition condition,
            final Praedikat name,
            final TalkStepNarrationAndAction narrationAndAction) {
        this.exitStep = exitStep;
        this.condition = condition;
        this.name = name;
        this.narrationAndAction = narrationAndAction;
    }

    public boolean isStepPossible() {
        return condition.isStepPossible();
    }

    public boolean isExitStep() {
        return exitStep;
    }

    /**
     * Gibt den Namen des Schritts zurück (Basis für den Namen der Aktion, den der Benutzer sieht)
     */
    public Praedikat getName() {
        return name;
    }

    /**
     * Die Talk-Step-Aktion ausführen.
     */
    public void narrateAndDo() {
        narrationAndAction.narrateAndDo();
    }
}
