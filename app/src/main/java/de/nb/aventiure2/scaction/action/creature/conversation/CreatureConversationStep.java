package de.nb.aventiure2.scaction.action.creature.conversation;

import de.nb.aventiure2.data.world.entity.creature.Creature;
import de.nb.aventiure2.data.world.entity.creature.CreatureState;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.praedikat.Praedikat;
import de.nb.aventiure2.german.praedikat.PraedikatMitEinerObjektleerstelle;
import de.nb.aventiure2.german.praedikat.PraedikatOhneLeerstellen;
import de.nb.aventiure2.german.praedikat.VerbSubjObj;

/**
 * Ein Schritt in einem Gespräch mit einer {@link Creature}.
 * Führt in der Regel von einem {@link CreatureState} zu einem
 * anderen, oft im Rahmen einer {@link de.nb.aventiure2.scaction.action.RedenAction}.
 * <p>
 * Das Konzept ist ähnliche wie die
 * {@link de.nb.aventiure2.scaction.action.room.connection.RoomConnection}, die
 * von einem {@link de.nb.aventiure2.data.world.room.AvRoom} zum anderen führt,
 * oft im Rahmen einer {@link de.nb.aventiure2.scaction.action.BewegenAction}.
 */
public class CreatureConversationStep {
    public enum Type {
        /**
         * Ein Entry-Schritt ist nur möglich, wenn der SC unmittelbar zuvor NICHT
         * im Gespräch mit der {@link Creature} war UND DIESES GESPRÄCH AUCH NICHT GERADE
         * EBEN BEENDET HAT. Der Benutzer möchte ein Gespräch beginnen.
         */
        ENTRY_RE_ENTRY,
        /**
         * Ein Entry-Schritt ist nur möglich, wenn der SC unmittelbar zuvor noch im Gespräch
         * mit der {@link Creature} war. Der Benutzer möchte das Gespräch fortführen.
         */
        NORMAL,
        /**
         * Ein Entry-Schritt ist nur möglich, wenn der SC unmittelbar zuvor noch im Gespräch
         * mit der {@link Creature} war. Der Benutzer möchte das Gespräch beenden.
         */
        EXIT,
        /**
         * Ein Entry-Schritt ist nur möglich, wenn der SC GERADE EBEN das
         * im Gespräch mit der {@link Creature} beendet hat (oder die Creature hat das Gespräch
         * beendet). Der Benutzer hat es sich offenbar
         * anders überlegt und möchte sofort wieder ein Gespräch beginnen.
         */
        IMMEDIATE_RE_ENTRY
    }

    final static PraedikatMitEinerObjektleerstelle DEFAULT_ENTRY_RE_ENTRY_NAME =
            VerbSubjObj.REDEN;

    final static PraedikatOhneLeerstellen DEFAULT_EXIT_NAME =
            VerbSubjObj.BEENDEN.mitObj(Nominalphrase.GESPRAECH);

    /**
     * Condition to check whether a <code>CreatureConversationStep</code> is possible / allowed.
     */
    interface Condition {
        /**
         * Return true iff the step is possible / allowed.
         */
        boolean isStepPossible();
    }

    /**
     * This ist what the conversation step actually does.
     */
    interface NarrationAndAction {
        /**
         * Führt den {@link CreatureConversationStep} aus - erzählt die Geschichte weiter
         * und verändert ggf. die Welt.
         *
         * @return Zeitspanne die das alles dauert
         */
        AvTimeSpan narrateAndDo();
    }

    final static Condition ALWAYS_POSSIBLE = () -> true;

    /**
     * Typ des Schritts
     */
    private final Type stepType;

    /**
     * Prädikat für den Namen des Schritts, z.B. ("Mit dem Frosch reden")
     */
    private final Praedikat name;

    /**
     * This ist what the conversation step actually does.
     */
    private final NarrationAndAction narrationAndAction;

    /**
     * If this condition does not hold, the step is impossible
     * (there won't be an action for the step)
     */
    private final Condition condition;

    /**
     * Constructor.
     *
     * @param condition If this condition does not hold, this step is impossible
     *                  (there won't be an action for this step).
     */
    CreatureConversationStep(
            final Type stepType,
            final Condition condition,
            final Praedikat name,
            final NarrationAndAction narrationAndAction) {
        this.stepType = stepType;
        this.condition = condition;
        this.name = name;
        this.narrationAndAction = narrationAndAction;
    }

    /**
     * Whether the step is possible. (If a step is impossible, there won't be an action for the step.)
     */
    boolean isStepPossible() {
        return condition.isStepPossible();
    }

    public Type getStepType() {
        return stepType;
    }

    /**
     * Gibt den Namen des Schritts zurück (Basis für den Namen der Aktion, den der Benutzer sieht)
     */
    public Praedikat getName() {
        return name;
    }

    /**
     * Den Schritt ausführen.
     */
    public AvTimeSpan narrateAndDo() {
        return narrationAndAction.narrateAndDo();
    }
}
