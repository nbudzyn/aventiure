package de.nb.aventiure2.data.world.syscomp.taking;

import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;

public class SCTakeAction<GIVEN extends IDescribableGO & ILocatableGO> {
    private final GIVEN given;
    private final boolean wirdZunaechstAngenommen;
    private final TakerNarrationAndAction<GIVEN> takerNarrationAndAction;

    public interface TakerNarrationAndAction<GIVEN extends IDescribableGO & ILocatableGO> {
        void narrateTakerAndDo(GIVEN offered);
    }

    public static <GIVEN extends IDescribableGO & ILocatableGO>
    SCTakeAction<GIVEN> zunaechstAngenommen(
            final GIVEN given,
            final TakerNarrationAndAction<GIVEN> takerNarrationAndAction) {
        return new SCTakeAction<>(
                given, true, takerNarrationAndAction);
    }

    public static <GIVEN extends IDescribableGO & ILocatableGO>
    SCTakeAction<GIVEN> sofortAbgelehnt(
            final GIVEN given,
            final TakerNarrationAndAction<GIVEN> takerNarrationAndAction) {
        return new SCTakeAction<>(
                given, false, takerNarrationAndAction);
    }

    private SCTakeAction(final GIVEN given,
                         final boolean wirdZunaechstAngenommen,
                         final TakerNarrationAndAction<GIVEN> takerNarrationAndAction) {
        this.given = given;
        this.wirdZunaechstAngenommen = wirdZunaechstAngenommen;
        this.takerNarrationAndAction = takerNarrationAndAction;
    }

    public boolean wirdZunaechstAngenommen() {
        return wirdZunaechstAngenommen;
    }

    public void narrateTakerAndDo() {
        takerNarrationAndAction.narrateTakerAndDo(given);
    }
}
