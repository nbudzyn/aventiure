package de.nb.aventiure2.data.world.gameobject;

import androidx.annotation.NonNull;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.Known;
import de.nb.aventiure2.data.world.base.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.syscomp.description.impl.SimpleDescriptionComp;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceComp;
import de.nb.aventiure2.german.description.TimedDescription;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusVerbAllg;

import static de.nb.aventiure2.data.world.base.Lichtverhaeltnisse.DUNKEL;
import static de.nb.aventiure2.data.world.base.SpatialConnectionData.conData;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceType.ASTGABEL;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.*;
import static de.nb.aventiure2.german.base.Artikel.Typ.DEF;
import static de.nb.aventiure2.german.base.Artikel.Typ.INDEF;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static de.nb.aventiure2.german.praedikat.VerbSubj.ANKOMMEN;

public class BaumFactory {
    public static final String HOCHKLETTERN = "BaumFactory_HOCHKLETTERN";
    public static final String HINABKLETTERN = "BaumFactory_HINABKLETTERN";
    private final AvDatabase db;
    private final World world;

    BaumFactory(final AvDatabase db,
                final World world) {
        this.db = db;
        this.world = world;
    }

    GameObject createImGartenHinterDerHuetteImWald() {
        return create(BAUM_IM_GARTEN_HINTER_DER_HUETTE_IM_WALD, HINTER_DER_HUETTE);
    }

    @NonNull
    private GameObject create(final GameObjectId id, final GameObjectId locationId) {
        final SimpleDescriptionComp descriptionComp =
                new SimpleDescriptionComp(id,
                        np(M, INDEF, "einzelner Baum in der Mitte des Gartens",
                                "einzelnen Baum in der Mitte des Gartens",
                                "einzelnen Baum in der Mitte des Gartens"),
                        np(M, DEF, "einzelne Baum in der Mitte des Gartens",
                                "einzelnen Baum in der Mitte des Gartens",
                                "einzelnen Baum in der Mitte des Gartens"),
                        np(M, DEF, "Baum"));

        final LocationComp locationComp = new LocationComp(
                id, db, world, locationId,
                null, false);

        final StoringPlaceComp storingPlaceComp = new StoringPlaceComp(
                id, db, ASTGABEL,
                false,
                conData("im Geäst",
                        "Auf den Baum klettern",
                        mins(6),
                        this::getDescIn),
                conData("im Geäst",
                        "Zum Boden hinabklettern",
                        mins(4),
                        this::getDescOut)
        );

        return new StoringPlaceObject(id,
                descriptionComp,
                locationComp,
                storingPlaceComp);
    }

    private TimedDescription<?> getDescIn(
            final Known newLocationKnown, final Lichtverhaeltnisse lichtverhaeltnisse) {
        final int count = db.counterDao().get(HOCHKLETTERN);
        switch (count) {
            case 0:
                return getDescInErstesMal();
            case 1:
                return getDescInZweitesMal();
            default:
                return getDescInNtesMal(lichtverhaeltnisse);
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
                        + "Apfelbaum", mins(6), HOCHKLETTERN);
    }

    private static TimedDescription<?> getDescInZweitesMal() {
        return du(PARAGRAPH,
                "kletterst",
                "noch einmal eine, zwei Etagen den Baum hinauf. "
                        + "Du schaust ins Blattwerk und bist stolz auf dich",
                "noch einmal",
                mins(6), HOCHKLETTERN)
                .dann();
    }

    private static TimedDescription<?> getDescInNtesMal(
            final Lichtverhaeltnisse lichtverhaeltnisse) {
        return
                // STORY Alternative:
                //                du(PARAGRAPH, "kletterst",
                //                        "ein weiteres Mal auf den Baum")
                du(PARAGRAPH,
                        "kletterst", "noch einmal "
                                + "auf den Baum. Neues gibt es hier oben nicht zu erleben",
                        "noch einmal",
                        mins(7), HOCHKLETTERN)
                        .dann();
    }

    private TimedDescription<?> getDescOut(
            final Known newLocationKnown, final Lichtverhaeltnisse lichtverhaeltnisse) {
        final int count = db.counterDao().get(HINABKLETTERN);
        switch (count) {
            case 0:
                return getDescOutErstesMal(lichtverhaeltnisse);
            case 1:
                return getDescOutZweitesMal();
            default:
                return getDescOutNtesMal(lichtverhaeltnisse);
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
                + dunkelNachsatz, mins(4), HINABKLETTERN);
    }

    private static TimedDescription<?> getDescOutZweitesMal() {
        return neuerSatz(
                "Dann geht es vorsichtig wieder hinunter", mins(4), HINABKLETTERN)
                .beendet(PARAGRAPH);
    }

    private static TimedDescription<?> getDescOutNtesMal(
            final Lichtverhaeltnisse lichtverhaeltnisse) {
        // FIXME Die folgenden Konstruktionen möchte ich ebenfalls automatisch erzeugen (Nach
        //  Möglichkeit Beispiele einbauen):
        //  - Endlich angekommen bist du sehr erschöpft.
        //  - Gut ausgeschlafen bist du voller Tatendrang.
        //  - Endlich eingetreten schaust du dich genau um.
        //  - Endlich eingeschlafen hast du einen Traum.

        // STORY Auch diese Konstruktionen möchte ich nach Möglichkeit automatisch (zentral)
        //  erzeugen (Nach Möglichkeit Beispiele einbauen):
        //  - Als du unten angekommen bist, bist du sehr erschöpft.
        //  - Als du endlich angekommen bist, bist du sehr erschöpft.
        //  - Als du endlich ausgeschlafen hast, bist du voller Tatendrang.
        //  - Als du endlich eingetreten bist, schaust du dich genau um.

        // STORY Auch dies automatisch erzeugen:
        //  - "Du bist ganz zerknirscht. Du gehst ...." ->  "Ganz zerknirscht gehst du..."??
        //  - Oder so? "Du wirst ganz zerknirscht. Du gehst ...." ->  "Ganz zerknirscht gehst
        //  du..."??
        return
                // "Dann kommst du wieder unten an"
                // Kann mit Müdigkeit kombiniert werden zu:
                // "Unten angekommen bist du ziemlich erschöpft..."
                du(SENTENCE,
                        ANKOMMEN
                                .mitAdverbialerAngabe(
                                        new AdverbialeAngabeSkopusVerbAllg("wieder unten")),
                        mins(8), HINABKLETTERN)
                        .dann();

        // STORY Alternative:
        //                "Ein zurückschwingender "
        //                                + "Ast verpasst dir beim Abstieg ein Schramme",
        //                        .dann(),
    }
}
