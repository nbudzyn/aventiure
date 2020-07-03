package de.nb.aventiure2.data.world.syscomp.spatialconnection.impl;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.syscomp.memory.Known;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.AbstractSpatialConnectionComp;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState;
import de.nb.aventiure2.data.world.syscomp.storingplace.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.german.base.AbstractDescription;

import static de.nb.aventiure2.data.world.gameobject.World.ABZWEIG_IM_WALD;
import static de.nb.aventiure2.data.world.gameobject.World.COUNTER_ID_VOR_DEM_SCHLOSS_SCHLOSSFEST_KNOWN;
import static de.nb.aventiure2.data.world.gameobject.World.DRAUSSEN_VOR_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.gameobject.World.IM_WALD_NAHE_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.gameobject.World.SCHLOSSFEST;
import static de.nb.aventiure2.data.world.gameobject.World.VOR_DEM_ALTEN_TURM;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.ERSCHOEPFT;
import static de.nb.aventiure2.data.world.syscomp.memory.Known.KNOWN_FROM_DARKNESS;
import static de.nb.aventiure2.data.world.syscomp.memory.Known.UNKNOWN;
import static de.nb.aventiure2.data.world.syscomp.spatialconnection.impl.SpatialConnection.con;
import static de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState.BEGONNEN;
import static de.nb.aventiure2.data.world.syscomp.storingplace.Lichtverhaeltnisse.DUNKEL;
import static de.nb.aventiure2.data.world.syscomp.storingplace.Lichtverhaeltnisse.HELL;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.mins;
import static de.nb.aventiure2.german.base.AllgDescription.neuerSatz;
import static de.nb.aventiure2.german.base.DuDescription.du;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;

/**
 * An implementation of {@link AbstractSpatialConnectionComp}
 * for the {@link World#IM_WALD_NAHE_DEM_SCHLOSS}
 * room.
 */
@ParametersAreNonnullByDefault
public class ImWaldNaheDemSchlossConnectionComp extends AbstractSpatialConnectionComp {
    public ImWaldNaheDemSchlossConnectionComp(
            final AvDatabase db,
            final World world) {
        super(IM_WALD_NAHE_DEM_SCHLOSS, db, world);
    }

    @Override
    public boolean isAlternativeMovementDescriptionAllowed(final GameObjectId to,
                                                           final Known newRoomKnown,
                                                           final Lichtverhaeltnisse lichtverhaeltnisseInNewRoom) {
        if (to.equals(DRAUSSEN_VOR_DEM_SCHLOSS) &&
                ((IHasStateGO<SchlossfestState>) world.load(SCHLOSSFEST)).stateComp()
                        .hasState(BEGONNEN) &&
                db.counterDao().get(COUNTER_ID_VOR_DEM_SCHLOSS_SCHLOSSFEST_KNOWN) == 0) {
            return false;
        }

        return true;
    }

    @NonNull
    @Override
    public List<SpatialConnection> getConnections() {
        return ImmutableList.of(
                con(DRAUSSEN_VOR_DEM_SCHLOSS,
                        "Den Wald verlassen",
                        this::getDescTo_DraussenVorDemSchloss),
                con(VOR_DEM_ALTEN_TURM,
                        "Den schmalen Pfad aufwärts gehen",
                        this::getDescTo_VorDemAltenTurm),
                con(ABZWEIG_IM_WALD,
                        "Tiefer in den Wald hineingehen",
                        du(SENTENCE, "gehst", "den Weg weiter in den Wald hinein. "
                                + "Nicht lang, und es geht zur Linken zwischen "
                                + "den Bäumen ein alter, düsterer Weg ab, über "
                                + "den Farn wuchert", mins(5))
                                .komma(),
                        du("kommst", "an den farnüberwachsenen Abzweig", mins(5))
                                .undWartest()
                ));
    }


    private AbstractDescription getDescTo_DraussenVorDemSchloss(
            final Known newRoomKnown, final Lichtverhaeltnisse lichtverhaeltnisse) {

        switch (((IHasStateGO<SchlossfestState>) world.load(SCHLOSSFEST)).stateComp().getState()) {
            case BEGONNEN:
                return getDescTo_DraussenVorDemSchloss_FestBegonnen(
                        mins(10));

            default:
                return getDescTo_DraussenVorDemSchloss_KeinFest(
                        lichtverhaeltnisse);
        }
    }

    @NonNull
    private static AbstractDescription
    getDescTo_DraussenVorDemSchloss_KeinFest(
            final Lichtverhaeltnisse lichtverhaeltnisse) {
        if (lichtverhaeltnisse == HELL) {
            return du("erreichst", "bald das helle "
                    + "Tageslicht, in dem der Schlossgarten "
                    + "liegt", "bald", mins(10))
                    .undWartest()
                    .komma();
        }

        return du(SENTENCE, "gehst", "noch eine Weile vorsichtig durch den dunklen "
                + "Wald, dann öffnet sich der Weg wieder und du stehst im Schlossgarten "
                + "unter dem Sternenhimmel", "noch eine Weile", mins(15));
        // STORY Lichtverhältnisse auch bei den anderen Aktionen berücksichtigen,
        //  insbesondere nach derselben Logik (z.B. "im Schloss ist es immer hell",
        //  "eine Fackel bringt auch nachts Licht" etc.)
        // STORY gegen Abend wird man müde und kann auf jeden Fall einschlafen
        // STORY Wenn man schläft, "verpasst" man Reactions, die man dann später
        //  (beim Aufwachen) merkt ("Der Frosch ist verschwunden".) Man könnte alle
        //  verpassten Reactions als Texte speichern (Problem, wenn der Frosch nur kurz
        //  verschwundenn ist), inhaltlich speichern
        //  (WasIstAllesPassiert.FroschIstVerschwunden = true), oder man speichert
        //  den Stand VOR dem Einschlafen und vergleicht mit dem Stand NACH dem
        //  Einschlafen.
        // STORY Nachts sieht man nicht so gut - sieht man alle Objects?
        // STORY Nachts schlafen die Creatures?
        // STORY Nachts ist man hauptsächlich MUEDE / ERSCHOEPFT
    }

    @NonNull
    private AbstractDescription
    getDescTo_DraussenVorDemSchloss_FestBegonnen(
            final AvTimeSpan timeSpan) {
        if (db.counterDao().incAndGet(COUNTER_ID_VOR_DEM_SCHLOSS_SCHLOSSFEST_KNOWN) == 1) {
            return du("bist", "von dem Lärm überrascht, der dir "
                    + "schon von weitem "
                    + "entgegenschallt. Als du aus dem Wald heraustrittst, "
                    + "ist der Anblick überwältigend: "
                    + "Überall im Schlossgarten stehen kleine Pagoden "
                    + "in lustigen Farben. Kinder werden auf Kähnen durch Kanäle "
                    + "gestakt und aus dem Schloss duftet es verführerisch nach "
                    + "Gebratenem", timeSpan);
        }

        return neuerSatz("Das Schlossfest ist immer noch in vollem Gange", timeSpan);
    }

    private AbstractDescription getDescTo_VorDemAltenTurm(
            final Known newRoomKnown, final Lichtverhaeltnisse lichtverhaeltnisse) {
        if (newRoomKnown == UNKNOWN && lichtverhaeltnisse == HELL) {
            return du("nimmst", "den schmalen Pfad, der sich lange durch "
                    + "den Wald aufwärts windet. Ein Hase kreuzt den Weg, "
                    + "aber keine Menschenseele begegnet dir. Ganz am Ende – "
                    + "auf der Hügelkuppe – kommst du an einen alten Turm", mins(25))
                    .beendet(PARAGRAPH);
        }
        if (newRoomKnown == UNKNOWN && lichtverhaeltnisse == DUNKEL) {
            world.loadSC().feelingsComp().setMood(ERSCHOEPFT);

            return neuerSatz("Trotz der Dunkelheit nimmst du den schmalen Pfad, "
                    + "der sich lange durch "
                    + "den nächtlichen Wald aufwärts windet. "
                    + "Du erschrickst, als eine Nachteule laut „uhu“ schreit, "
                    + "auch, als es laut neben dir im Unterholz raschelt. "
                    + "Endlich endet der Pfad an einen alten Turm", mins(40))
                    .beendet(PARAGRAPH);
        }
        if (newRoomKnown == KNOWN_FROM_DARKNESS
                && lichtverhaeltnisse == HELL) {
            return neuerSatz("Der schmale Pfad den Hügel hinauf ist bei Tageslicht "
                            + "auch nicht kürzer, aber endlich stehst du wieder vor dem alten Turm",
                    mins(25));
        }
        return du("gehst", "den langen, schmalen Pfad den Hügel hinauf bis zum "
                + "Turm", mins(25));
    }
}
