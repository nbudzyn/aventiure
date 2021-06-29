package de.nb.aventiure2.data.world.syscomp.storingplace;

import javax.annotation.Nullable;

import de.nb.aventiure2.data.world.base.IGameObject;

/**
 * Game Object, das eine äußerste Location haben kann:
 * <ul>
 *     <li>Entweder ist das Objekt selbst eine Location (damit hat es auf jeden Fall auch eine
 *     äußerste Location).
 *     <li>Oder da Objekt kann sich an einer Location befinden (auch in dem Fall hat es dann eine
 *     äußerste Location).
 * </ul>
 */
public interface ICanHaveOuterMostLocation extends IGameObject {
    @Nullable
    ILocationGO getOuterMostLocation();

    @Nullable
    ILocationGO getVisibleOuterMostLocation();

    /**
     * Gibt <code>true</code> zurück falls
     * <ul>
     * <li><code>this</code> und <code>location</code> gleich sind
     * <li>oder sich <code>this</code> an der <code>location</code>befindet, ggf. rekusiv.
     * </ul>
     */
    boolean isOrHasRecursiveLocation(@Nullable final IGameObject location);

    boolean isOrHasVisiblyRecursiveLocation(@Nullable IGameObject other);
}
