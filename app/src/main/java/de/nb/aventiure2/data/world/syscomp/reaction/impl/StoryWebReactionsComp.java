package de.nb.aventiure2.data.world.syscomp.reaction.impl;

import androidx.annotation.Nullable;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.reaction.AbstractReactionsComp;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IMovementReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.ISCActionReactions;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.syscomp.story.StoryWebComp;
import de.nb.aventiure2.data.world.syscomp.story.impl.FroschkoenigStoryNode;
import de.nb.aventiure2.data.world.time.AvTimeSpan;

import static de.nb.aventiure2.data.world.gameobject.World.GOLDENE_KUGEL;
import static de.nb.aventiure2.data.world.gameobject.World.SPIELER_CHARAKTER;
import static de.nb.aventiure2.data.world.gameobject.World.STORY_WEB;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;

/**
 * Reagiert auf die Aktionen des SCs und managet dabei die Stories, d.h. die kleinen
 * Geschichten / Märchen, die der Spieler erlebt. Im Wesentlichen gibt es drei Reaktionen:
 * <ul>
 * <li>Der Spieler erhält einen Tipp.
 * <li>Eine neue Story (ein neues Märchen) wird begonnen.
 * <li>(Es passiert nichts.)
 * </ul>
 */
public class StoryWebReactionsComp
        extends AbstractReactionsComp
        implements IMovementReactions, ISCActionReactions {
    private final StoryWebComp storyWebComp;

    public StoryWebReactionsComp(final AvDatabase db, final World world,
                                 final StoryWebComp storyWebComp) {
        super(STORY_WEB, db, world);
        this.storyWebComp = storyWebComp;
    }

    @Override
    public AvTimeSpan onLeave(final ILocatableGO locatable, final ILocationGO from,
                              @Nullable final ILocationGO to) {
        return noTime();
    }

    @Override
    public AvTimeSpan onEnter(final ILocatableGO locatable, @Nullable final ILocationGO from,
                              final ILocationGO to) {
        if (locatable.is(SPIELER_CHARAKTER)) {
            return onSCEnter(from, to);
        }

        // Die Goldene Kugel hat einen anderen Ort erreicht -
        // oder ein Container, der die Goldene Kugel (ggf. rekursiv) enthält,
        // hat einen anderen Ort erreicht
        if (world.isOrHasRecursiveLocation(GOLDENE_KUGEL, locatable)) {
            return onGoldeneKugelRecEnter(from, to);
        }

        return noTime();
    }

    private static AvTimeSpan onSCEnter(@Nullable final ILocationGO from, final ILocationGO to) {
        return noTime();
    }

    /**
     * Die Goldene Kugel hat <code>to</code> erreicht - oder ein Container, der die
     * Goldene Kugel (ggf. rekursiv) enthält, hat <code>to</code> erreicht.
     */
    private AvTimeSpan onGoldeneKugelRecEnter(@javax.annotation.Nullable final ILocationGO from,
                                              final ILocationGO to) {
        if (world.isOrHasRecursiveLocation(to, SPIELER_CHARAKTER)) {
            storyWebComp.reachStoryNode(FroschkoenigStoryNode.KUGEL_GENOMMEN);
            return noTime();
        }

        return noTime();
    }

    @Override
    public AvTimeSpan afterScActionAndFirstWorldUpdate() {
        return storyWebComp.narrateAndDoHintActionIfAny();
    }
}
