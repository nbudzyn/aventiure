package de.nb.aventiure2.playeraction.action.creature.reaction;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.IPlayerAction;
import de.nb.aventiure2.data.world.creature.CreatureData;
import de.nb.aventiure2.data.world.room.AvRoom;

import static de.nb.aventiure2.data.storystate.StoryState.StartsNew.SENTENCE;
import static de.nb.aventiure2.data.world.creature.CreatureState.ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS;

class FroschprinzCreatureReactions extends AbstractCreatureReactions {
    public FroschprinzCreatureReactions(final AvDatabase db,
                                        final Class<? extends IPlayerAction> playerActionClass) {
        super(db, playerActionClass);
    }

    @Override
    public void onLeaveRoom(final AvRoom oldRoom, final CreatureData creature) {
        if (creature.hasState(ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS)
                && oldRoom != AvRoom.SCHLOSS) {
            n.add(t(SENTENCE,
                    " „Warte, warte“, ruft der Frosch, „nimm mich mit, ich kann nicht so "
                            + "laufen wie du.“ Aber was hilft ihm, dass er dir "
                            + "sein „Quak, quak!“ so laut nachschreit "
                            + "als er kann, du hörst nicht darauf")
                    .undWartest()
                    .letzterRaum(oldRoom));
        }
    }
}
