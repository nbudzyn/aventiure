package de.nb.aventiure2.data.world.syscomp.spatialconnection.impl;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.Known;
import de.nb.aventiure2.data.world.base.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.base.SpatialConnection;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.AbstractSpatialConnectionComp;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState;
import de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState;
import de.nb.aventiure2.data.world.time.*;
import de.nb.aventiure2.german.description.TimedDescription;

import static de.nb.aventiure2.data.world.base.Known.KNOWN_FROM_DARKNESS;
import static de.nb.aventiure2.data.world.base.Known.UNKNOWN;
import static de.nb.aventiure2.data.world.base.Lichtverhaeltnisse.DUNKEL;
import static de.nb.aventiure2.data.world.base.Lichtverhaeltnisse.HELL;
import static de.nb.aventiure2.data.world.base.SpatialConnection.con;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState.BEGONNEN;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.*;
import static de.nb.aventiure2.data.world.time.Tageszeit.*;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;

/**
 * An implementation of {@link AbstractSpatialConnectionComp}
 * for the {@link World#IM_WALD_NAHE_DEM_SCHLOSS}
 * room.
 */
@ParametersAreNonnullByDefault
public class ImWaldNaheDemSchlossConnectionComp extends AbstractSpatialConnectionComp {
    public ImWaldNaheDemSchlossConnectionComp(
            final AvDatabase db,
            final Narrator n, final World world) {
        super(IM_WALD_NAHE_DEM_SCHLOSS, db, n, world);
    }

    @Override
    public boolean isAlternativeMovementDescriptionAllowed(final GameObjectId to,
                                                           final Known newLocationKnown,
                                                           final Lichtverhaeltnisse lichtverhaeltnisseInNewLocation) {
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
                        "auf dem Weg aus dem Wald",
                        "Den Wald verlassen und in den Schloßgarten gehen",
                        mins(10),
                        this::getDescTo_DraussenVorDemSchloss),
                con(VOR_DEM_ALTEN_TURM,
                        "auf dem Pfad",
                        "Den schmalen Pfad aufwärtsgehen",
                        mins(25),
                        this::getDescTo_VorDemAltenTurm),
                con(ABZWEIG_IM_WALD,
                        "auf dem Weg in den Wald hinein",
                        "Tiefer in den Wald hineingehen",
                        mins(5),
                        du(SENTENCE, "gehst", "den Weg weiter in den Wald hinein. "
                                + "Nicht lang, und es geht zur Linken zwischen "
                                + "den Bäumen ein alter, düsterer Weg ab, über "
                                + "den Farn wuchert", mins(5))
                                .komma(),
                        du("kommst", "an den farnüberwachsenen Abzweig", mins(5))
                                .undWartest()
                ));
    }


    private TimedDescription getDescTo_DraussenVorDemSchloss(
            final Known newLocationKnown, final Lichtverhaeltnisse lichtverhaeltnisse) {

        switch (((IHasStateGO<SchlossfestState>) world.load(SCHLOSSFEST)).stateComp().getState()) {
            case BEGONNEN:
                return getDescTo_DraussenVorDemSchloss_FestBegonnen(mins(10));
            default:
                return getDescTo_DraussenVorDemSchloss_KeinFest(lichtverhaeltnisse);
        }
    }

    @NonNull
    private TimedDescription
    getDescTo_DraussenVorDemSchloss_KeinFest(
            final Lichtverhaeltnisse lichtverhaeltnisse) {
        if (db.nowDao().now().getTageszeit() == TAGSUEBER) {
            return du("erreichst", "bald das helle "
                    + "Tageslicht, in dem der Schlossgarten "
                    + "liegt", "bald", mins(10))
                    .undWartest()
                    .komma();
        }

        if (lichtverhaeltnisse == HELL) {
            return du("erreichst", "bald den Schlossgarten",
                    "bald", mins(10))
                    .undWartest()
                    .komma();
        }

        return du(SENTENCE, "gehst", "noch eine Weile vorsichtig durch den dunklen "
                + "Wald, dann öffnet sich der Weg wieder und du stehst im Schlossgarten "
                + "unter dem Sternenhimmel", "noch eine Weile", mins(15));
        // FIXME Lichtverhältnisse auch bei den anderen Aktionen berücksichtigen,
        //  insbesondere nach derselben Logik (z.B. "im Schloss ist es immer hell",
        //  "eine Fackel bringt auch nachts Licht" etc.)
        // FIXME Wenn man schläft, "verpasst" man Reactions, die man dann später
        //  (beim Aufwachen) merkt ("Der Frosch ist verschwunden".) Man speichert
        //  am besten den Stand VOR dem Einschlafen und vergleicht mit dem Stand NACH dem
        //  Einschlafen.
        // STORY Nachts sieht man nicht so gut - sieht man alle Objects?
    }

    @NonNull
    private TimedDescription
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

    private TimedDescription getDescTo_VorDemAltenTurm(
            final Known newLocationKnown, final Lichtverhaeltnisse lichtverhaeltnisse) {
        if (newLocationKnown == UNKNOWN && lichtverhaeltnisse == HELL) {
            return du("nimmst", "den schmalen Pfad, der sich lange durch "
                    + "den Wald aufwärts windet. Ein Hase kreuzt den Weg"
                    + (alleinAufDemPfadZumTurm() ? ", aber keine Menschenseele begegnet dir" : "")
                    + ". Ganz am Ende – "
                    + "auf der Hügelkuppe – kommst du an einen alten Turm", mins(25))
                    .beendet(PARAGRAPH);
        }
        if (newLocationKnown == UNKNOWN && lichtverhaeltnisse == DUNKEL) {
            return neuerSatz("Trotz der Dunkelheit nimmst du den schmalen Pfad, "
                    + "der sich lange durch "
                    + "den nächtlichen Wald aufwärts windet. "
                    + "Du erschrickst, als eine Nachteule laut „uhu“ schreit, "
                    + "auch, als es laut neben dir im Unterholz raschelt. "
                    + "Endlich endet der Pfad an einen alten Turm", mins(40))
                    .beendet(PARAGRAPH);
        }
        if (newLocationKnown == KNOWN_FROM_DARKNESS
                && lichtverhaeltnisse == HELL) {
            return neuerSatz("Der schmale Pfad den Hügel hinauf ist bei Tageslicht "
                            + "auch nicht kürzer, aber endlich stehst du wieder vor dem alten Turm",
                    mins(25));
        }
        return du("gehst", "wieder den langen, schmalen Pfad den Hügel hinauf bis zum "
                + "Turm", mins(25));
    }

    private <FROSCHPRINZ extends ILocatableGO & IHasStateGO<FroschprinzState>>
    boolean alleinAufDemPfadZumTurm() {
        if (((ILocatableGO) world.load(RAPUNZELS_ZAUBERIN)).locationComp()
                .hasLocation(IM_WALD_NAHE_DEM_SCHLOSS, VOR_DEM_ALTEN_TURM)) {
            return false;
        }

        if (((ILocatableGO) world.load(RAPUNZEL)).locationComp()
                .hasLocation(IM_WALD_NAHE_DEM_SCHLOSS, VOR_DEM_ALTEN_TURM)) {
            return false;
        }

        final FROSCHPRINZ froschprinz = (FROSCHPRINZ) world.load(FROSCHPRINZ);
        if (froschprinz.stateComp().getState().hasGestalt(FroschprinzState.Gestalt.MENSCH) &&
                froschprinz.locationComp()
                        .hasLocation(IM_WALD_NAHE_DEM_SCHLOSS, VOR_DEM_ALTEN_TURM)) {
            return false;
        }

        return true;
    }
}
