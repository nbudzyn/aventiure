package de.nb.aventiure2.data.world.syscomp.talking.impl;

import de.nb.aventiure2.data.world.syscomp.talking.ITalkerGO;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.praedikat.Praedikat;
import de.nb.aventiure2.german.praedikat.PraedikatMitEinerObjektleerstelle;
import de.nb.aventiure2.german.praedikat.PraedikatOhneLeerstellen;
import de.nb.aventiure2.german.praedikat.VerbSubjObj;

/**
 * Ein Redebeitrag, den der Spieler(-Charakter) an das {@link ITalkerGO} richten kann
 * (und auf den das {@link ITalkerGO} dann irgendwie reagiert).
 * <p>
 * Die Reaktion des {@link ITalkerGO}s auf den Redebeitrag führt oft von
 * einem {@link de.nb.aventiure2.data.world.syscomp.state.GameObjectState}
 * zu einem anderen.
 * <p>
 * Das Konzept ist ähnliche wie die
 * {@link de.nb.aventiure2.data.world.syscomp.spatialconnection.impl.SpatialConnection},
 * die von einem
 * {@link de.nb.aventiure2.data.world.syscomp.spatialconnection.ISpatiallyConnectedGO} zum
 * anderen führt, oft im Rahmen einer {@link de.nb.aventiure2.scaction.impl.BewegenAction}.
 */
public class SCTalkAction {
    public enum Type {
        /**
         * Ein Entry-Schritt ist nur möglich, wenn der SC unmittelbar zuvor NICHT
         * im Gespräch mit der Kreatur war UND DIESES GESPRÄCH AUCH NICHT GERADE
         * EBEN BEENDET HAT. Der Benutzer möchte ein Gespräch beginnen.
         */
        ENTRY_RE_ENTRY,
        /**
         * Ein Entry-Schritt ist nur möglich, wenn der SC unmittelbar zuvor noch im Gespräch
         * mit der Kreatur war. Der Benutzer möchte das Gespräch fortführen.
         */
        NORMAL,
        /**
         * Ein Entry-Schritt ist nur möglich, wenn der SC unmittelbar zuvor noch im Gespräch
         * mit der Kreatur war. Der Benutzer möchte das Gespräch beenden.
         */
        EXIT,
        /**
         * Ein Entry-Schritt ist nur möglich, wenn der SC GERADE EBEN das
         * im Gespräch mit der Kreatur beendet hat (oder die Creature hat das Gespräch
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
         * Führt den {@link SCTalkAction} aus - erzählt die Geschichte weiter
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

    static SCTalkAction entrySt(
            final SCTalkAction.NarrationAndAction narrationAndAction) {
        return entrySt(DEFAULT_ENTRY_RE_ENTRY_NAME, narrationAndAction);
    }

    static SCTalkAction entrySt(final Praedikat entryName,
                                final SCTalkAction.NarrationAndAction narrationAndAction) {
        return entrySt(ALWAYS_POSSIBLE, entryName, narrationAndAction);
    }

    /**
     * Creates a {@link SCTalkAction} to enter a conversation.
     *
     * @param condition If this condition does not hold, this step is impossible
     *                  (there won't be an action for this step).
     */
    static SCTalkAction entrySt(
            final SCTalkAction.Condition condition,
            final SCTalkAction.NarrationAndAction narrationAndAction) {
        return entrySt(condition, DEFAULT_ENTRY_RE_ENTRY_NAME, narrationAndAction);
    }

    /**
     * Creates a {@link SCTalkAction} to enter a conversation.
     *
     * @param condition If this condition does not hold, this step is impossible
     *                  (there won't be an action for this step).
     */
    static SCTalkAction entrySt(
            final SCTalkAction.Condition condition,
            final Praedikat entryName,
            final SCTalkAction.NarrationAndAction narrationAndAction) {
        return new SCTalkAction(SCTalkAction.Type.ENTRY_RE_ENTRY, condition,
                entryName,
                narrationAndAction);
    }

    static SCTalkAction immReEntrySt(
            final SCTalkAction.NarrationAndAction narrationAndAction) {
        return immReEntrySt(DEFAULT_ENTRY_RE_ENTRY_NAME, narrationAndAction);
    }

    static SCTalkAction immReEntrySt(final Praedikat entryName,
                                     final SCTalkAction.NarrationAndAction narrationAndAction) {
        return immReEntrySt(ALWAYS_POSSIBLE, entryName, narrationAndAction);
    }

    /**
     * Creates a {@link SCTalkAction} to immediately re-enter a conversation.
     *
     * @param condition If this condition does not hold, this step is impossible
     *                  (there won't be an action for this step).
     */
    static SCTalkAction immReEntrySt(final SCTalkAction.Condition condition,
                                     final SCTalkAction.NarrationAndAction narrationAndAction) {
        return immReEntrySt(condition, DEFAULT_ENTRY_RE_ENTRY_NAME, narrationAndAction);
    }

    /**
     * Creates a {@link SCTalkAction} to immediately re-enter a conversation.
     *
     * @param condition If this condition does not hold, this step is impossible
     *                  (there won't be an action for this step).
     */
    static SCTalkAction immReEntrySt(
            final SCTalkAction.Condition condition,
            final Praedikat entryName,
            final SCTalkAction.NarrationAndAction narrationAndAction) {
        return new SCTalkAction(SCTalkAction.Type.IMMEDIATE_RE_ENTRY,
                condition,
                entryName, narrationAndAction);
    }

    static SCTalkAction reEntrySt(
            final SCTalkAction.NarrationAndAction narrationAndAction) {
        return reEntrySt(DEFAULT_ENTRY_RE_ENTRY_NAME, narrationAndAction);
    }

    static SCTalkAction reEntrySt(final Praedikat entryName,
                                  final SCTalkAction.NarrationAndAction narrationAndAction) {
        return reEntrySt(ALWAYS_POSSIBLE, entryName, narrationAndAction);
    }

    /**
     * Creates a {@link SCTalkAction} to re-enter a conversation (but not immediately)
     *
     * @param condition If this condition does not hold, this step is impossible
     *                  (there won't be an action for this step).
     */
    static SCTalkAction reEntrySt(
            final SCTalkAction.Condition condition,
            final SCTalkAction.NarrationAndAction narrationAndAction) {
        return reEntrySt(condition, DEFAULT_ENTRY_RE_ENTRY_NAME, narrationAndAction);
    }

    /**
     * Creates a {@link SCTalkAction} to re-enter a conversation (but not immediately)
     *
     * @param condition If this condition does not hold, this step is impossible
     *                  (there won't be an action for this step).
     */
    static SCTalkAction reEntrySt(
            final SCTalkAction.Condition condition,
            final Praedikat entryName,
            final SCTalkAction.NarrationAndAction narrationAndAction) {
        return new SCTalkAction(SCTalkAction.Type.ENTRY_RE_ENTRY, condition,
                entryName,
                narrationAndAction);
    }

    static SCTalkAction st(final Praedikat name,
                           final SCTalkAction.NarrationAndAction narrationAndAction) {
        return st(ALWAYS_POSSIBLE, name, narrationAndAction);
    }

    /**
     * Creates a normal {@link SCTalkAction}
     *
     * @param condition If this condition does not hold, this step is impossible
     *                  (there won't be an action for this step).
     */
    static SCTalkAction st(
            final SCTalkAction.Condition condition,
            final Praedikat name,
            final SCTalkAction.NarrationAndAction narrationAndAction) {
        return new SCTalkAction(
                SCTalkAction.Type.NORMAL, condition, name, narrationAndAction);
    }

    static SCTalkAction exitSt(
            final SCTalkAction.NarrationAndAction narrationAndAction) {
        return exitSt(DEFAULT_EXIT_NAME, narrationAndAction);
    }

    static SCTalkAction exitSt(final Praedikat exitName,
                               final SCTalkAction.NarrationAndAction narrationAndAction) {
        return new SCTalkAction(SCTalkAction.Type.EXIT, ALWAYS_POSSIBLE,
                exitName,
                narrationAndAction);
    }

    /**
     * Constructor.
     *
     * @param condition If this condition does not hold, this step is impossible
     *                  (there won't be an action for this step).
     */
    SCTalkAction(
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
    public boolean isPossible() {
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
