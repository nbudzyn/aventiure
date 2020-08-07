package de.nb.aventiure2.data.world.syscomp.mentalmodel;

import javax.annotation.Nonnull;

import de.nb.aventiure2.data.world.base.IGameObject;

/**
 * Game Object (z.B. ein NSC), das ein mentales Modell
 * der Welt hat. Der NSC geht also von gewissen Dingen aus, z.B.
 * dass sich jemand oder etwas irgendwo befindet.
 */
public interface IHasMentalModelGO extends IGameObject {
    @Nonnull
    public MentalModelComp mentalModelComp();
}
