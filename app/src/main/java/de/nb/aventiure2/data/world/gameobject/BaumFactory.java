package de.nb.aventiure2.data.world.gameobject;

import static de.nb.aventiure2.data.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.world.base.Lichtverhaeltnisse.DUNKEL;
import static de.nb.aventiure2.data.world.base.SpatialConnectionData.conDataAltDescTimed;
import static de.nb.aventiure2.data.world.gameobject.BaumFactory.Counter.*;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.storingplace.Geschlossenheit.MAN_KANN_HINEINSEHEN_UND_LICHT_SCHEINT_HINEIN_UND_HINAUS;
import static de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceComp.LEUCHTET_NIE;
import static de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceType.ASTGABEL;
import static de.nb.aventiure2.german.base.ArtikelwortFlexionsspalte.Typ.DEF;
import static de.nb.aventiure2.german.base.ArtikelwortFlexionsspalte.Typ.INDEF;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.BAUM;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.AltTimedDescriptionsBuilder.altTimed;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static de.nb.aventiure2.german.praedikat.VerbSubj.ANKOMMEN;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.Known;
import de.nb.aventiure2.data.world.base.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.syscomp.description.impl.SimpleDescriptionComp;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceComp;
import de.nb.aventiure2.german.description.TimedDescription;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;

public class BaumFactory extends AbstractGameObjectFactory {
    public enum Counter {
        HOCHKLETTERN, HINABKLETTERN
    }

    BaumFactory(final AvDatabase db,
                final TimeTaker timeTaker,
                final World world) {
        super(db, timeTaker, world);
    }

    GameObject createImGartenHinterDerHuetteImWald() {
        return create(HINTER_DER_HUETTE);
    }

    @NonNull
    private GameObject create(
            @SuppressWarnings("SameParameterValue") final GameObjectId locationId) {
        final SimpleDescriptionComp descriptionComp =
                new SimpleDescriptionComp(World.BAUM_IM_GARTEN_HINTER_DER_HUETTE_IM_WALD,
                        np(M, INDEF, "einzelner Baum in der Mitte des Gartens",
                                "einzelnen Baum in der Mitte des Gartens",
                                "einzelnen Baum in der Mitte des Gartens",
                                World.BAUM_IM_GARTEN_HINTER_DER_HUETTE_IM_WALD),
                        np(M, DEF, "einzelne Baum in der Mitte des Gartens",
                                "einzelnen Baum in der Mitte des Gartens",
                                "einzelnen Baum in der Mitte des Gartens",
                                World.BAUM_IM_GARTEN_HINTER_DER_HUETTE_IM_WALD),
                        np(BAUM, World.BAUM_IM_GARTEN_HINTER_DER_HUETTE_IM_WALD));

        final LocationComp locationComp = new LocationComp(
                World.BAUM_IM_GARTEN_HINTER_DER_HUETTE_IM_WALD, db, world, locationId,
                null, false);

        final StoringPlaceComp storingPlaceComp = new StoringPlaceComp(
                World.BAUM_IM_GARTEN_HINTER_DER_HUETTE_IM_WALD, timeTaker, world,
                locationComp,
                ASTGABEL,
                false,
                MAN_KANN_HINEINSEHEN_UND_LICHT_SCHEINT_HINEIN_UND_HINAUS, LEUCHTET_NIE,
                conDataAltDescTimed("im Geäst",
                        "Auf den Baum klettern",
                        mins(6),
                        (newLocationKnown, lichtverhaeltnisseInNewLocation) -> altDescIn()),
                conDataAltDescTimed("im Geäst",
                        "Zum Boden hinabklettern",
                        mins(4),
                        this::altDescOut));

        return new StoringPlaceObject(World.BAUM_IM_GARTEN_HINTER_DER_HUETTE_IM_WALD,
                descriptionComp,
                locationComp,
                storingPlaceComp);
    }

    @CheckReturnValue
    private ImmutableCollection<TimedDescription<?>> altDescIn() {
        final int count = db.counterDao().get(HOCHKLETTERN);
        switch (count) {
            case 0:
                return ImmutableList.of(getDescInErstesMal());
            case 1:
                return ImmutableList.of(getDescInZweitesMal());
            default:
                return altDescInNtesMal();
        }
    }

    @CheckReturnValue
    private static TimedDescription<?> getDescInErstesMal() {
        return neuerSatz(PARAGRAPH,
                "Vom Stamm geht in Hüfthöhe ein kräftiger Ast ab, den kannst du "
                        + "ohne Mühe "
                        + "ersteigen. Danach wird es schwieriger. Du ziehst dich eine "
                        + "Ebene höher, "
                        + "und der Stamm gabelt sich. Ja, der rechte Ast müsste halten. "
                        + "Du versuchst, zu "
                        + "balancieren, aber dann kletterst du doch auf allen Vieren den "
                        + "Ast entlang, "
                        + "der immer dünner wird und gefährlich schwankt. Irgendeine "
                        + "Aussicht hast du "
                        + "nicht, und Äpfel sind auch keine zu finden. Vielleicht doch kein "
                        + "Apfelbaum")
                .timed(mins(6))
                .withCounterIdIncrementedIfTextIsNarrated(HOCHKLETTERN);
    }

    @CheckReturnValue
    private static TimedDescription<?> getDescInZweitesMal() {
        return du(PARAGRAPH, "kletterst",
                "noch einmal eine, zwei Etagen den Baum hinauf. "
                        + "Du schaust ins Blattwerk und bist stolz auf dich")
                .mitVorfeldSatzglied("noch einmal")
                .timed(mins(6))
                .withCounterIdIncrementedIfTextIsNarrated(HOCHKLETTERN)
                .dann();
    }

    @CheckReturnValue
    private static ImmutableCollection<TimedDescription<?>> altDescInNtesMal() {
        return ImmutableList.of(
                du(PARAGRAPH, "kletterst",
                        "noch einmal auf den Baum. Neues gibt es hier oben",
                        "nicht zu erleben")
                        .mitVorfeldSatzglied("noch einmal")
                        .timed(mins(7))
                        .withCounterIdIncrementedIfTextIsNarrated(HOCHKLETTERN)
                        .dann(),
                du(PARAGRAPH, "kletterst",
                        "ein weiteres Mal auf den Baum")
                        .mitVorfeldSatzglied("ein weiteres Mal")
                        .timed(mins(7))
                        .withCounterIdIncrementedIfTextIsNarrated(HOCHKLETTERN)
                        .dann()
        );
    }

    private ImmutableCollection<TimedDescription<?>> altDescOut(
            final Known newLocationKnown, final Lichtverhaeltnisse lichtverhaeltnisse) {
        final int count = db.counterDao().get(HINABKLETTERN);
        switch (count) {
            case 0:
                return ImmutableList.of(getDescOutErstesMal(lichtverhaeltnisse));
            case 1:
                return ImmutableList.of(getDescOutZweitesMal());
            default:
                return altDescOutNtesMal();
        }
    }

    private static TimedDescription<?> getDescOutErstesMal(
            final Lichtverhaeltnisse lichtverhaeltnisse) {
        final String dunkelNachsatz =
                lichtverhaeltnisse == DUNKEL ?
                        ". Und das alles im Dunkeln!" :
                        ".";
        return neuerSatz("Mit einiger Mühe drehst du auf dem Ast um und hangelst dich "
                + "vorsichtig "
                + "wieder herab auf den Boden"
                + dunkelNachsatz)
                .timed(mins(4))
                .withCounterIdIncrementedIfTextIsNarrated(HINABKLETTERN);
    }

    private static TimedDescription<?> getDescOutZweitesMal() {
        return neuerSatz("Dann geht es vorsichtig wieder hinunter", PARAGRAPH)
                .timed(mins(4))
                .withCounterIdIncrementedIfTextIsNarrated(
                        HINABKLETTERN);
    }

    @CheckReturnValue
    private static ImmutableCollection<TimedDescription<?>> altDescOutNtesMal() {
        return altTimed()
                .add(
                        // "Dann kommst du wieder unten an"
                        // Kann mit Müdigkeit kombiniert werden zu:
                        // "Unten angekommen bist du ziemlich erschöpft..."
                        du(SENTENCE, ANKOMMEN
                                .mitAdvAngabe(
                                        new AdvAngabeSkopusVerbAllg(
                                                "wieder unten")))
                                .timed(mins(8))
                                .withCounterIdIncrementedIfTextIsNarrated(HINABKLETTERN)
                                .dann(),
                        neuerSatz("ein zurückschwingender Ast verpasst dir beim Abstieg",
                                "eine Schramme")
                                .timed(mins(8))
                                .withCounterIdIncrementedIfTextIsNarrated(HINABKLETTERN)
                                .dann()
                ).build();
    }
}
