package de.nb.aventiure2.data.world.syscomp.talking.impl;

import de.nb.aventiure2.data.world.base.SpatialConnection;
import de.nb.aventiure2.data.world.syscomp.talking.ITalkerGO;
import de.nb.aventiure2.german.praedikat.AbstractAngabenfaehigesPraedikatOhneLeerstellen;
import de.nb.aventiure2.german.praedikat.Praedikat;
import de.nb.aventiure2.german.praedikat.PraedikatMitEinerObjektleerstelle;
import de.nb.aventiure2.german.praedikat.VerbSubjObj;

import static de.nb.aventiure2.data.world.syscomp.talking.impl.SCTalkAction.Type.IMMEDIATE_RE_ENTRY_NSC_HATTE_GESPRAECH_BEENDET;
import static de.nb.aventiure2.data.world.syscomp.talking.impl.SCTalkAction.Type.IMMEDIATE_RE_ENTRY_SC_HATTE_GESPRAECH_BEENDET;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.GESPRAECH;

/**
 * Ein Redebeitrag, den der Spieler(-Charakter) an das {@link ITalkerGO} richten kann
 * (und auf den das {@link ITalkerGO} dann irgendwie reagiert).
 * <p>
 * Die Reaktion des {@link ITalkerGO}s auf den Redebeitrag führt oft von
 * einem State zu einem anderen.
 * <p>
 * Das Konzept ist ähnliche wie die
 * {@link SpatialConnection},
 * die von einem
 * {@link de.nb.aventiure2.data.world.syscomp.spatialconnection.ISpatiallyConnectedGO} zum
 * anderen führt, oft im Rahmen einer {@link de.nb.aventiure2.scaction.impl.BewegenAction}.
 */
public class SCTalkAction {
    public enum Type {
        /**
         * Ein ENTRY_RE_ENTRY-Schritt ist nur möglich, wenn der SC unmittelbar zuvor NICHT
         * im Gespräch mit der Kreatur war UND DIESES GESPRÄCH AUCH NICHT GERADE
         * EBEN BEENDET HAT. Der Benutzer möchte ein Gespräch beginnen.
         */
        ENTRY_RE_ENTRY,
        /**
         * Ein NORMAL-Schritt ist nur möglich, wenn der SC unmittelbar zuvor noch im Gespräch
         * mit der Kreatur war. Der Benutzer möchte das Gespräch fortführen.
         */
        NORMAL,
        /**
         * Ein EXIT-Schritt ist nur möglich, wenn der SC unmittelbar zuvor noch im Gespräch
         * mit der Kreatur war. Der Benutzer möchte das Gespräch beenden.
         * <p>
         * Diese Talk-Action unterscheidet sich formal nicht von {@link #NORMAL} - allerdings
         * formalisiert sie die Spielererwartung, jederzeit das Gespräch aktiv beenden zu können.
         * Man wird also in aller Regel in jedem Zustand (wenn das Gespräch erst einmal im Gang
         * ist) eine EXIT-Talk-Action haben - und vielleicht einige {@link #NORMAL}-Talk-Actions.
         */
        EXIT,
        /**
         * Ein IMMEDIATE_RE_ENTRY_SC_HATTE_GESPRAECH_BEENDET-Schritt ist nur möglich, wenn der
         * SC GERADE EBEN das Gespräch beendet hat. Der Benutzer hat es sich
         * offenbar anders überlegt und möchte sofort wieder ein Gespräch beginnen.
         */
        IMMEDIATE_RE_ENTRY_SC_HATTE_GESPRAECH_BEENDET,
        /**
         * Ein IMMEDIATE_RE_ENTRY_TALKER_HATTE_GESPRAECH_BEENDET-Schritt ist nur möglich, wenn
         * <i>nicht des SC</i>, sondern sein Gesprächspartner GERADE EBEN das
         * im Gespräch beendet hat. Der Benutzer möchte sofort wieder ein Gespräch beginnen.
         */
        IMMEDIATE_RE_ENTRY_NSC_HATTE_GESPRAECH_BEENDET
    }

    private final static PraedikatMitEinerObjektleerstelle DEFAULT_ENTRY_RE_ENTRY_NAME =
            VerbSubjObj.REDEN;

    private final static AbstractAngabenfaehigesPraedikatOhneLeerstellen DEFAULT_EXIT_NAME =
            VerbSubjObj.BEENDEN.mit(GESPRAECH);

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
         */
        void narrateAndDo();
    }

    private final static Condition ALWAYS_POSSIBLE = () -> true;

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

    static SCTalkAction entryReEntrySt(
            final SCTalkAction.NarrationAndAction narrationAndAction) {
        return entryReEntrySt(DEFAULT_ENTRY_RE_ENTRY_NAME, narrationAndAction);
    }

    static SCTalkAction entryReEntrySt(final Praedikat entryName,
                                       final SCTalkAction.NarrationAndAction narrationAndAction) {
        return entryReEntrySt(ALWAYS_POSSIBLE, entryName, narrationAndAction);
    }

    /**
     * Creates a {@link SCTalkAction} to enter a conversation.
     *
     * @param condition If this condition does not hold, this step is impossible
     *                  (there won't be an action for this step).
     */
    static SCTalkAction entryReEntrySt(
            final SCTalkAction.Condition condition,
            final SCTalkAction.NarrationAndAction narrationAndAction) {
        return entryReEntrySt(condition, DEFAULT_ENTRY_RE_ENTRY_NAME, narrationAndAction);
    }

    /**
     * Creates a {@link SCTalkAction} to enter a conversation - or to re-enter, though not
     * immediately.
     *
     * @param condition If this condition does not hold, this step is impossible
     *                  (there won't be an action for this step).
     */
    static SCTalkAction entryReEntrySt(
            final SCTalkAction.Condition condition,
            final Praedikat entryName,
            final SCTalkAction.NarrationAndAction narrationAndAction) {
        return new SCTalkAction(SCTalkAction.Type.ENTRY_RE_ENTRY, condition,
                entryName,
                narrationAndAction);
    }

    static SCTalkAction immReEntryStSCHatteGespraechBeendet(
            final SCTalkAction.NarrationAndAction narrationAndAction) {
        return immReEntryStSCHatteGespraechBeendet(DEFAULT_ENTRY_RE_ENTRY_NAME, narrationAndAction);
    }

    static SCTalkAction immReEntryStSCHatteGespraechBeendet(final Praedikat entryName,
                                                            final SCTalkAction.NarrationAndAction narrationAndAction) {
        return immReEntryStSCHatteGespraechBeendet(ALWAYS_POSSIBLE, entryName, narrationAndAction);
    }

    /**
     * Creates a {@link SCTalkAction} to immediately re-enter a conversation after the SC
     * had finished the previous conversation.
     *
     * @param condition If this condition does not hold, this step is impossible
     *                  (there won't be an action for this step).
     */
    static SCTalkAction immReEntryStSCHatteGespraechBeendet(
            final SCTalkAction.Condition condition,
            final SCTalkAction.NarrationAndAction narrationAndAction) {
        return immReEntryStSCHatteGespraechBeendet(
                condition, DEFAULT_ENTRY_RE_ENTRY_NAME, narrationAndAction);
    }

    /**
     * Creates a {@link SCTalkAction} to immediately re-enter a conversation after the SC
     * had finished the previous conversation.
     *
     * @param condition If this condition does not hold, this step is impossible
     *                  (there won't be an action for this step).
     */
    static SCTalkAction immReEntryStSCHatteGespraechBeendet(
            final SCTalkAction.Condition condition,
            final Praedikat entryName,
            final SCTalkAction.NarrationAndAction narrationAndAction) {
        return new SCTalkAction(IMMEDIATE_RE_ENTRY_SC_HATTE_GESPRAECH_BEENDET,
                condition,
                entryName, narrationAndAction);
    }

    static SCTalkAction immReEntryStNSCHatteGespraechBeendet(
            final SCTalkAction.NarrationAndAction narrationAndAction) {
        return immReEntryStNSCHatteGespraechBeendet(
                DEFAULT_ENTRY_RE_ENTRY_NAME, narrationAndAction);
    }

    static SCTalkAction immReEntryStNSCHatteGespraechBeendet(
            final Praedikat entryName,
            final SCTalkAction.NarrationAndAction narrationAndAction) {
        return immReEntryStNSCHatteGespraechBeendet(ALWAYS_POSSIBLE, entryName, narrationAndAction);
    }

    /**
     * Creates a {@link SCTalkAction} to immediately re-enter a conversation after the NSC
     * had finished the previous conversation.
     *
     * @param condition If this condition does not hold, this step is impossible
     *                  (there won't be an action for this step).
     */
    static SCTalkAction immReEntryStNSCHatteGespraechBeendet(
            final SCTalkAction.Condition condition,
            final SCTalkAction.NarrationAndAction narrationAndAction) {
        return immReEntryStNSCHatteGespraechBeendet(
                condition, DEFAULT_ENTRY_RE_ENTRY_NAME, narrationAndAction);
    }

    /**
     * Creates a {@link SCTalkAction} to immediately re-enter a conversation after the NSC
     * had finished the previous conversation.
     *
     * @param condition If this condition does not hold, this step is impossible
     *                  (there won't be an action for this step).
     */
    static SCTalkAction immReEntryStNSCHatteGespraechBeendet(
            final SCTalkAction.Condition condition,
            final Praedikat entryName,
            final SCTalkAction.NarrationAndAction narrationAndAction) {
        return new SCTalkAction(IMMEDIATE_RE_ENTRY_NSC_HATTE_GESPRAECH_BEENDET,
                condition,
                entryName, narrationAndAction);
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

    static SCTalkAction exitSt(final SCTalkAction.Condition condition,
                               final SCTalkAction.NarrationAndAction narrationAndAction) {
        return exitSt(condition, DEFAULT_EXIT_NAME, narrationAndAction);
    }

    static SCTalkAction exitSt(final Praedikat exitName,
                               final SCTalkAction.NarrationAndAction narrationAndAction) {
        return exitSt(ALWAYS_POSSIBLE,
                exitName,
                narrationAndAction);
    }

    static SCTalkAction exitSt(final SCTalkAction.Condition condition,
                               final Praedikat exitName,
                               final SCTalkAction.NarrationAndAction narrationAndAction) {
        return new SCTalkAction(SCTalkAction.Type.EXIT, condition,
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
     * Whether the step is possible. (If a step is impossible, there won't be an action for the
     * step.)
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
    public void narrateAndDo() {
        narrationAndAction.narrateAndDo();
    }
}
