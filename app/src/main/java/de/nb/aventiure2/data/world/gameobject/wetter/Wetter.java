package de.nb.aventiure2.data.world.gameobject.wetter;

import javax.annotation.Nonnull;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.reaction.IResponder;
import de.nb.aventiure2.data.world.syscomp.reaction.impl.WetterReactionsComp;
import de.nb.aventiure2.data.world.syscomp.wetter.IWetterGO;
import de.nb.aventiure2.data.world.syscomp.wetter.WetterComp;

import static de.nb.aventiure2.data.world.gameobject.World.*;

public class Wetter extends GameObject implements IWetterGO, IResponder {
    private final WetterComp wetterComp;
    private final WetterReactionsComp reactionsComp;

    Wetter(final AvDatabase db, final Narrator n, final TimeTaker timeTaker,
           final World world) {
        super(WETTER);
        // Jede Komponente muss registiert werden!
        wetterComp = addComponent(new WetterComp(db, timeTaker, n));
        reactionsComp = addComponent(new WetterReactionsComp(n, world, wetterComp));
    }

    @Nonnull
    @Override
    public WetterComp wetterComp() {
        return wetterComp;
    }

    @Nonnull
    @Override
    public WetterReactionsComp reactionsComp() {
        return reactionsComp;
    }
}
