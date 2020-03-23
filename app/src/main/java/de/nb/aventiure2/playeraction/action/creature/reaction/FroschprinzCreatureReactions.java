package de.nb.aventiure2.playeraction.action.creature.reaction;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.IPlayerAction;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.world.creature.CreatureData;
import de.nb.aventiure2.data.world.entity.AbstractEntityData;
import de.nb.aventiure2.data.world.object.ObjectData;
import de.nb.aventiure2.data.world.room.AvRoom;

import static de.nb.aventiure2.data.storystate.StoryState.StartsNew.SENTENCE;
import static de.nb.aventiure2.data.world.creature.CreatureState.ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS;
import static de.nb.aventiure2.data.world.creature.CreatureState.HAT_FORDERUNG_GESTELLT;
import static de.nb.aventiure2.data.world.creature.CreatureState.HAT_NACH_BELOHNUNG_GEFRAGT;
import static de.nb.aventiure2.data.world.creature.CreatureState.HAT_SC_HILFSBEREIT_ANGESPROCHEN;
import static de.nb.aventiure2.data.world.creature.CreatureState.UNAUFFAELLIG;
import static de.nb.aventiure2.data.world.object.AvObject.Key.GOLDENE_KUGEL;
import static de.nb.aventiure2.data.world.room.AvRoom.IM_WALD_BEIM_BRUNNEN;
import static de.nb.aventiure2.german.base.GermanUtil.capitalize;

class FroschprinzCreatureReactions extends AbstractCreatureReactions {
    public FroschprinzCreatureReactions(final AvDatabase db,
                                        final Class<? extends IPlayerAction> playerActionClass) {
        super(db, playerActionClass);
    }

    @Override
    public void onLeaveRoom(final AvRoom oldRoom, final CreatureData froschprinz,
                            final StoryState currentStoryState) {
        if (froschprinz.hasState(ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS)
                && oldRoom != AvRoom.SCHLOSS_VORHALLE) {
            n.add(t(SENTENCE,
                    " „Warte, warte“, ruft der Frosch, „nimm mich mit, ich kann nicht so "
                            + "laufen wie du.“ Aber was hilft ihm, dass er dir "
                            + "sein „Quak, quak!“ so laut nachschreit "
                            + "als er kann, du hörst nicht darauf")
                    .undWartest()
                    .letzterRaum(oldRoom));
        }
    }

    @Override
    public void onEnterRoom(final AvRoom oldRoom, final AvRoom newRoom,
                            final CreatureData froschprinz,
                            final StoryState currentStoryState) {
        if (froschprinz.hasState(UNAUFFAELLIG)) {
            return;
        }

        n.add(t(StoryState.StartsNew.SENTENCE,
                "Hier sitzt "
                        + froschprinz.getDescription(false).nom()));

        // TODO Bei späterem Status sollte der Froschprinz den SC ansprechen und auf
        // sein Versprechen hinweisen!
    }

    @Override
    public void onNehmen(final AvRoom room, final CreatureData froschprinzInRoom,
                         final AbstractEntityData genommenData,
                         final StoryState currentStoryState) {
    }


    @Override
    public void onAblegen(final AvRoom room, final CreatureData froschprinzInRoom,
                          final AbstractEntityData abgelegtData,
                          final StoryState currentStoryState) {
    }

    @Override
    public void onHochwerfen(final AvRoom room, final CreatureData froschprinzCreatureData,
                             final ObjectData objectData,
                             final StoryState currentStoryState) {
        if (room != IM_WALD_BEIM_BRUNNEN || froschprinzCreatureData.hasState(UNAUFFAELLIG)) {
            return;
        }

        final boolean scHatObjektAufgefangen =
                db.playerInventoryDao().getInventory().stream().map(o -> o.getKey())
                        .anyMatch(k -> k == objectData.getObject().getKey());

        if (froschprinzCreatureData.hasState(HAT_SC_HILFSBEREIT_ANGESPROCHEN,
                HAT_NACH_BELOHNUNG_GEFRAGT,
                HAT_FORDERUNG_GESTELLT)) {
            if (!scHatObjektAufgefangen) {
                // Der Spieler hat ein weiteres Objekt in den Brunnen fallen
                // lassen, obwohl er noch mit dem Frosch verhandelt.
                n.add(t(StoryState.StartsNew.PARAGRAPH,
                        "Ob der Frosch gerade seine glitschige Nase gerümpft hat?")
                        .letztesObject(objectData.getObject()));
            }
            return;
        }

        if (objectData.getObject().getKey() != GOLDENE_KUGEL) {
            return;
        }

        if (scHatObjektAufgefangen) {
            return;
        }

        // Der Spieler hat die goldene Kugel letztlich in den Brunnen
        // fallen lassen, NACHDEM der Frosch schon Dinge hochgeholt hat.
        // Dann ist die Kugel jetzt WEG - PECH.
        n.add(t(StoryState.StartsNew.SENTENCE,
                capitalize(froschprinzCreatureData.nom(true)) +
                        " schaut dich vorwurfsvoll und etwas hochnäsig an")
                .letztesObject(objectData.getObject()));
    }
}
