package de.nb.aventiure2.playeraction.action.creature.reaction;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.IPlayerAction;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.world.creature.CreatureData;
import de.nb.aventiure2.data.world.entity.AbstractEntityData;
import de.nb.aventiure2.data.world.object.AvObject;
import de.nb.aventiure2.data.world.object.ObjectData;
import de.nb.aventiure2.data.world.room.AvRoom;
import de.nb.aventiure2.data.world.time.AvTimeSpan;

import static de.nb.aventiure2.data.storystate.StoryState.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.data.storystate.StoryState.StructuralElement.SENTENCE;
import static de.nb.aventiure2.data.world.creature.CreatureState.ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS;
import static de.nb.aventiure2.data.world.creature.CreatureState.HAT_FORDERUNG_GESTELLT;
import static de.nb.aventiure2.data.world.creature.CreatureState.HAT_NACH_BELOHNUNG_GEFRAGT;
import static de.nb.aventiure2.data.world.creature.CreatureState.HAT_SC_HILFSBEREIT_ANGESPROCHEN;
import static de.nb.aventiure2.data.world.creature.CreatureState.UNAUFFAELLIG;
import static de.nb.aventiure2.data.world.object.AvObject.Key.GOLDENE_KUGEL;
import static de.nb.aventiure2.data.world.room.AvRoom.IM_WALD_BEIM_BRUNNEN;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.secs;
import static de.nb.aventiure2.german.base.GermanUtil.capitalize;

class FroschprinzCreatureReactions extends AbstractCreatureReactions {
    FroschprinzCreatureReactions(final AvDatabase db,
                                 final Class<? extends IPlayerAction> playerActionClass) {
        super(db, playerActionClass);
    }

    @Override
    public AvTimeSpan onLeaveRoom(final AvRoom oldRoom, final CreatureData froschprinz,
                                  final StoryState currentStoryState) {
        if (froschprinz.hasState(ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS)
                && oldRoom != AvRoom.SCHLOSS_VORHALLE) {
            n.add(t(SENTENCE,
                    " „Warte, warte“, ruft der Frosch, „nimm mich mit, ich kann nicht so "
                            + "laufen wie du.“ Aber was hilft ihm, dass er dir "
                            + "sein „Quak, quak!“ so laut nachschreit, "
                            + "als er kann, du hörst nicht darauf")
                    .undWartest()
                    .letzterRaum(oldRoom));

            return noTime();
        }

        return noTime();
    }

    @Override
    public AvTimeSpan onEnterRoom(final AvRoom oldRoom, final AvRoom newRoom,
                                  final CreatureData froschprinz,
                                  final StoryState currentStoryState) {
        if (froschprinz.hasState(UNAUFFAELLIG)) {
            return noTime();
        }

        n.add(t(StoryState.StructuralElement.SENTENCE,
                "Hier sitzt "
                        + froschprinz.getDescription(false).nom()));

        return noTime();

        // STORY Bei späterem Status sollte der Froschprinz den SC ansprechen und auf
        //  sein Versprechen hinweisen!
    }

    @Override
    public AvTimeSpan onNehmen(final AvRoom room, final CreatureData froschprinzInRoom,
                               final AbstractEntityData genommenData,
                               final StoryState currentStoryState) {
        return noTime();
    }

    @Override
    public AvTimeSpan onAblegen(final AvRoom room, final CreatureData froschprinzInRoom,
                                final AbstractEntityData abgelegtData,
                                final StoryState currentStoryState) {
        return noTime();
    }

    @Override
    public AvTimeSpan onHochwerfen(final AvRoom room, final CreatureData froschprinzCreatureData,
                                   final ObjectData objectData,
                                   final StoryState currentStoryState) {
        if (room != IM_WALD_BEIM_BRUNNEN || froschprinzCreatureData.hasState(UNAUFFAELLIG)) {
            return noTime();
        }

        final boolean scHatObjektAufgefangen =
                db.playerInventoryDao().getInventory().stream().map(AvObject::getKey)
                        .anyMatch(k -> k == objectData.getObject().getKey());

        if (froschprinzCreatureData.hasState(HAT_SC_HILFSBEREIT_ANGESPROCHEN,
                HAT_NACH_BELOHNUNG_GEFRAGT,
                HAT_FORDERUNG_GESTELLT)) {
            if (!scHatObjektAufgefangen) {
                // Der Spieler hat ein weiteres Objekt in den Brunnen fallen
                // lassen, obwohl er noch mit dem Frosch verhandelt.
                n.add(t(StoryState.StructuralElement.PARAGRAPH,
                        "Ob der Frosch gerade seine glitschige Nase gerümpft hat?")
                        .letztesObject(objectData.getObject())
                        .beendet(PARAGRAPH)
                );
            }
            return secs(3);
        }

        if (objectData.getObject().getKey() != GOLDENE_KUGEL) {
            return noTime();
        }

        if (scHatObjektAufgefangen) {
            return noTime();
        }

        // Der Spieler hat die goldene Kugel letztlich in den Brunnen
        // fallen lassen, NACHDEM der Frosch schon Dinge hochgeholt hat.
        // Dann ist die Kugel jetzt WEG - PECH.
        n.add(t(StoryState.StructuralElement.SENTENCE,
                capitalize(froschprinzCreatureData.nom(true)) +
                        " schaut dich vorwurfsvoll und etwas hochnäsig an")
                .letztesObject(objectData.getObject()));

        return secs(5);
    }
}
