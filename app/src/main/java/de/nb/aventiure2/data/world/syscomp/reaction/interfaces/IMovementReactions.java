package de.nb.aventiure2.data.world.syscomp.reaction.interfaces;

import androidx.annotation.Nullable;

import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.reaction.IReactions;
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
     * Gibt zurück, ob sich dieser
     * {@link de.nb.aventiure2.data.world.syscomp.reaction.IResponder} vor einem
     * SC, der an seiner Location eintrifft, verbirgt ({@code true})
     * oder nicht ({@code false}).
     * <p>
     * Das Ergebnis muss konsistent sein mit
     * {@link #onEnter(ILocatableGO, ILocationGO, ILocationGO)}:
     * <ul>
     * <li>Wenn {@code onEnter()} keine Beschreibung liefert (also den
     * {@code IResponder} nicht erwähnt, muss diese Methode ({@code true})
     * liefern.
     * <li>Wenn {@code onEnter()} allerdings eine Beschreibung liefert (also den
     * {@code IResponder} erwähnt, muss diese Methode ({@code false}) liefern.
     * </ul>
     * <p>
     * (Diese Methode ist eine Basis für Ausgaben wir "Der Frosch ist hier
     * nirgendwo mehr zu sehen")
     */
    boolean verbirgtSichVorEintreffendemSC();

    /**
     * The <code>locatable</code> enters a place (after having left some other place).
     * (This is called at the end of the movement.)
     * <p>
     * Das Verhalten muss konsistent sein mit
     * {@link #verbirgtSichVorEintreffendemSC()}:
     * <ul>
     * <li>Wenn {@code onEnter()} keine Beschreibung liefert (also den
     * {@code IResponder} nicht erwähnt, muss
     * {@code verbirgtSichVorEintreffendemSC()} ({@code true}) liefern.
     * <li>Wenn {@code onEnter()} allerdings eine Beschreibung liefert (also den
     * {@code IResponder} erwähnt, muss
     * {@code verbirgtSichVorEintreffendemSC()} ({@code false}) liefern.
     * </ul>
     */
    void onEnter(ILocatableGO locatable,
                 @Nullable ILocationGO from, ILocationGO to);
}
