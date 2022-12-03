package de.nb.aventiure2.data.world.syscomp.spatialconnection.impl;

import static de.nb.aventiure2.data.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.time.AvTimeSpan.secs;
import static de.nb.aventiure2.data.world.base.SpatialConnection.con;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.description.PossessivDescriptionVorgabe.ALLES_ERLAUBT;
import static de.nb.aventiure2.data.world.syscomp.spatialconnection.impl.ObenImTurmConnectionComp.Counter.HERABGESTIEGEN;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.AN_DAT;
import static de.nb.aventiure2.german.base.StructuralElement.WORD;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.praedikat.VerbSubj.HINABSTEIGEN;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.List;

import javax.annotation.CheckReturnValue;
import javax.annotation.ParametersAreNonnullByDefault;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.Known;
import de.nb.aventiure2.data.world.base.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.base.SpatialConnection;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.description.DescribableGameObject;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.AbstractSpatialConnectionComp;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState;
import de.nb.aventiure2.german.description.TimedDescription;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;

/**
 * An implementation of {@link AbstractSpatialConnectionComp}
 * for the {@link World#OBEN_IM_ALTEN_TURM}
 * room.
 */
@ParametersAreNonnullByDefault
public class ObenImTurmConnectionComp extends AbstractSpatialConnectionComp {
    @SuppressWarnings({"unused", "RedundantSuppression"})
    enum Counter {
        HERABGESTIEGEN
    }

    public ObenImTurmConnectionComp(
            final AvDatabase db, final TimeTaker timeTaker,
            final Narrator n,
            final World world) {
        super(OBEN_IM_ALTEN_TURM, db, timeTaker, n, world);
    }

    @Override
    public boolean isAlternativeMovementDescriptionAllowed(final GameObjectId to,
                                                           final Known newLocationKnown,
                                                           final Lichtverhaeltnisse lichtverhaeltnisseInNewLocation) {
        return true;
    }

    @Override
    @NonNull
    @CheckReturnValue
    public List<SpatialConnection> getConnections() {
        final ImmutableList.Builder<SpatialConnection> res = ImmutableList.builder();

        if (loadRapunzel().stateComp().hasState(RapunzelState.HAARE_VOM_TURM_HERUNTERGELASSEN)) {
            res.add(con(VOR_DEM_ALTEN_TURM,
                    "an den Zöpfen",
                    "An den Haaren hinabsteigen",
                    secs(90),
                    this::getDescTo_VorDemTurm));
        }

        return res.build();
    }

    @CheckReturnValue
    private TimedDescription<?> getDescTo_VorDemTurm(
            final Known newLocationKnown, final Lichtverhaeltnisse lichtverhaeltnisse) {
        if (db.counterDao().get(HERABGESTIEGEN) % 2 == 1) {
            // 2.Mal, 4. Mal, ...
            return du(WORD, "bist", "schnell wieder hinab")
                    .mitVorfeldSatzglied("schnell")
                    .schonLaenger()
                    .timed(secs(30))
                    .withCounterIdIncrementedIfTextIsNarrated(HERABGESTIEGEN)
                    .undWartest()
                    .dann();
        }

        final DescribableGameObject rapunzelsHaareDGO = new DescribableGameObject(
                world,
                RAPUNZELS_HAARE,
                ALLES_ERLAUBT,
                true);

        // "Du steigst daran hinab" / "Du steigst an ihren Haren hinab" /
        // "Du steigst an Rapunzels Haaren hinab" /
        // "Du steigst an den Haaren hinab"
        return du(WORD, HINABSTEIGEN
                .mitAdvAngabe(new AdvAngabeSkopusVerbAllg(
                        AN_DAT.mit(
                                // TODO Schöner hier rapunzelsHaareDGO direkt übergeben
                                //  und den Aufruf von und
                                //  den Aufruf von  alsSubstPhrase() in der
                                //  Sprachkomponente machen!
                                rapunzelsHaareDGO.alsSubstPhrase(
                                        n
                                        // FIXME  Kann man hier vermeiden, dass man hier den
                                        //  Text-Kontext angeben muss?
                                        //  Kann nicht der TextContext beim Zusammenbau automatisch
                                        //  ermittelt werden - vor allem, wenn die Sprachkomponente
                                        //  den Text zusammenbaut?!
                                        //  Schließlich könnte die Sprachzusammenbaukomponente
                                        //  wissen:
                                        //  "hier bitte Rapunzel einfügen" - allerdings: Wie ist
                                        //  der initiale ITextContext?
                                        //  Kann ImmutableTextContext eine Inspiration sein?
                                        //  Wichtig: Beide Fälle müssen funktionieren:
                                        //  - "Jeden Morgen wäscht Rapunzel ihre (!) Haare."
                                        //  - "Ihre (!) Haare wäscht Rapunzel jeden Morgen."
                                        //  Außerdem jeweils
                                        //  - Für Texte aus der Sprachkomponente
                                        //  - FÜr Stückchenweise zusammengesetzte Texte.
                                        //  Die Sprachkomponente darf kein *"Rapunzel wäscht
                                        //  Rapunzels Haare" erzeugen,
                                        //  auch kein *"Rapunzels Haare wäscht Rapunzel."
                                        //  Anscheinend ist das Subjekt
                                        //  relevant für zwingende possessive Bezüge.
                                        //  Außerdem muss die Sprachkomponente Doppeldeutigkeiten
                                        //  auflösen.
                                        //  - "Jeden Morgen trifft Rapunzel ihre Freundin und
                                        //  wäscht deren (!) Haare"
                                        //  - "Jeden Morgen trifft Rapunzel ihre Freundin und
                                        //  wäscht ihre eigenen (!) Haare"
                                        //  Und die Sprachkomponente muss mit externem Kontext
                                        //  umgehen können:
                                        //  - "Rapunzel hat einen Sohn. Jeden Morgen wäscht sie
                                        //  seine Haare".
                                        //  - "Rapunzel hat eine Tochter. Jeden Morgen wäscht sie
                                        //  deren Haare".
                                        //  - "Rapunzel hat eine Tochter. Jeden Morgen wäscht sie
                                        //  ihre eigenen Haare".
                                        //  Für all das ließen sich schöne Testfälle schreiben!
                                )
                                // "sie" / "ihre Haare" / "Rapunzels Haare" / "die Haare"
                        )
                        // "an den Haaren" / "an ihren Haaren" / "an Rapunzels Haaren" / "daran"
                )))
                .timed(mins(1))
                .withCounterIdIncrementedIfTextIsNarrated(HERABGESTIEGEN)
                .undWartest()
                .dann();
    }

    @NonNull
    private IHasStateGO<RapunzelState> loadRapunzel() {
        return loadRequired(RAPUNZEL);
    }
}