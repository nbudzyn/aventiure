package de.nb.aventiure2.data.world.syscomp.reaction.interfaces;

import androidx.annotation.Nullable;

import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.reaction.IReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.IResponder;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;

/**
 * Reactions to a game object moving from one place to another (normally).
 * <p>
 * Covers actions like:
 * <ul>
 *     <li>Someone walks from one place to an adjacent one
 *     <li>An Object is taken by someone
 *     <li>An Object laid down by someone
 *     <li>An Object is thrown by someone and has fallen down
 *     <li>An Object is thrown by someone and caught again (<code>from</code> equals
 *     <code>to</code>}.
 *     <li>An Object is created (<code>from</code> is null)
 *     <li>An Object is destroyed (<code>to</code> is null)
 * </ul>
 */
public interface IMovementReactions extends IReactions {
    /**
     * The <code>locatable</code> leaves a place for some other place.
     * (This is called at the beginning of the movement.)
     */
    void onLeave(ILocatableGO locatable,
                 ILocationGO from, @Nullable ILocationGO to);

    /**
     * Gibt zurück, ob der SC dieses GameObject bemerkt.
     */
    static boolean scBemerkt(final IGameObject gameObject) {
        if (!(gameObject instanceof IResponder)) {
            return true;
        }

        final IResponder responder = (IResponder) gameObject;
        if (!(responder.reactionsComp() instanceof IMovementReactions)) {
            return true;
        }

        return !((IMovementReactions) responder.reactionsComp()).isVorScVerborgen();
    }

    /**
     * Gibt zurück, ob dieser {@link IResponder} vor dem
     * SC vorborgen ist ({@code true}) oder nicht ({@code false}).
     * <p>
     * Zum Zeitpunkt, wenn der SC an der Location dieses {@link IResponder}s
     * eintrifft, muss der Rückgabewert dieser Methode konsistent sein mit
     * {@link #onEnter(ILocatableGO, ILocationGO, ILocationGO)}:
     * <ul>
     * <li>Wenn {@code onEnter()} keine Beschreibung liefert (also den
     * {@code IResponder} nicht erwähnt), muss diese Methode ({@code true})
     * liefern.
     * <li>Wenn {@code onEnter()} allerdings eine Beschreibung liefert (also den
     * {@code IResponder} erwähnt, muss diese Methode ({@code false}) liefern.
     * </ul>
     * <p>
     * (Diese Methode ist eine Basis für Ausgaben wir "Der Frosch ist hier
     * nirgendwo mehr zu sehen". Sie beeinflusst außerdem, ob der Sc
     * den Statuswechsel dieses {@link IResponder}s mitbekommt.)
     */
    boolean isVorScVerborgen();

    /**
     * The <code>locatable</code> enters a place (after having left some other place).
     * (This is called at the end of the movement.)
     * <p>
     * Das Verhalten muss konsistent sein mit
     * {@link #isVorScVerborgen()}:
     * <ul>
     * <li>Wenn {@code onEnter()} keine Beschreibung liefert (also den
     * {@code IResponder} nicht erwähnt, muss
     * {@code isVorScVerborgen()} ({@code true}) liefern.
     * <li>Wenn {@code onEnter()} allerdings eine Beschreibung liefert (also den
     * {@code IResponder} erwähnt, muss
     * {@code isVorScVerborgen()} ({@code false}) liefern.
     * </ul>
     */
    void onEnter(ILocatableGO locatable,
                 @Nullable ILocationGO from, ILocationGO to);
}
