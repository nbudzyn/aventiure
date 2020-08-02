package de.nb.aventiure2.data.world.gameobject;

import androidx.annotation.NonNull;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.Known;
import de.nb.aventiure2.data.world.base.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.syscomp.description.impl.SimpleDescriptionComp;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceComp;
import de.nb.aventiure2.german.base.AbstractDescription;

import static de.nb.aventiure2.data.world.base.Lichtverhaeltnisse.DUNKEL;
import static de.nb.aventiure2.data.world.base.SpatialConnectionData.conData;
import static de.nb.aventiure2.data.world.gameobject.World.BAUM_IM_GARTEN_HINTER_DER_HUETTE_IM_WALD;
import static de.nb.aventiure2.data.world.gameobject.World.HINTER_DER_HUETTE;
import static de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceType.ASTGABEL;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.mins;
import static de.nb.aventiure2.german.base.AllgDescription.neuerSatz;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;

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
                        np(M, "ein einzelner Baum in der Mitte des Gartens",
                                "einem einzelnen Baum in der Mitte des Gartens",
                                "einen einzelnen Baum in der Mitte des Gartens"),
                        np(M, "der einzelne Baum in der Mitte des Gartens",
                                "dem einzelnen Baum in der Mitte des Gartens",
                                "den einzelnen Baum in der Mitte des Gartens"),
                        np(M, "der Baum",
                                "dem Baum",
                                "den Baum"));

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

    private AbstractDescription<?> getDescIn(
            final Known newLocationKnown, final Lichtverhaeltnisse lichtverhaeltnisse) {
        final int count = db.counterDao().incAndGet(HOCHKLETTERN);
        switch (count) {
            case 1:
                return getDescInErstesMal();
            case 2:
                return getDescInZweitesMal();
            default:
                return getDescInNtesMal(lichtverhaeltnisse);
        }
    }

    private static AbstractDescription<?> getDescInErstesMal() {
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
                        + "Apfelbaum", mins(6));
    }

    private static AbstractDescription<?> getDescInZweitesMal() {
        return neuerSatz(PARAGRAPH,
                "Noch einmal kletterst du eine, zwei Etagen den Baum hinauf. "
                        + "Du schaust ins Blattwerk und bist stolz auf dich", mins(6))
                .dann();
    }

    private static AbstractDescription<?> getDescInNtesMal(
            final Lichtverhaeltnisse lichtverhaeltnisse) {
        return
                // STORY Alternative:
                //                du(PARAGRAPH, "kletterst",
                //                        "ein weiteres Mal auf den Baum")
                neuerSatz(PARAGRAPH,
                        "Es ist anstrengend, aber du kletterst noch einmal "
                                + "auf den Baum. Neues gibt es hier oben nicht zu erleben",
                        mins(7));
    }

    private AbstractDescription<?> getDescOut(
            final Known newLocationKnown, final Lichtverhaeltnisse lichtverhaeltnisse) {
        final int count = db.counterDao().incAndGet(HINABKLETTERN);
        switch (count) {
            case 1:
                return getDescOutErstesMal(lichtverhaeltnisse);
            case 2:
                return getDescOutZweitesMal();
            default:
                return getDescOutNtesMal(lichtverhaeltnisse);
        }
    }

    private static AbstractDescription<?> getDescOutErstesMal(
            final Lichtverhaeltnisse lichtverhaeltnisse) {
        final String dunkelNachsatz =
                lichtverhaeltnisse == DUNKEL ?
                        " Und das alles im Dunkeln!" :
                        "";
        return neuerSatz("Mit einiger Mühe drehst du auf dem Ast um und hangelst dich "
                + "vorsichtig "
                + "wieder herab auf den Boden. Das war anstrengend!"
                + dunkelNachsatz, mins(4))
                .beendet(PARAGRAPH);
    }

    private static AbstractDescription<?> getDescOutZweitesMal() {
        return neuerSatz(SENTENCE,
                "Dann geht es vorsichtig "
                        + "wieder hinunter", mins(4))
                .beendet(PARAGRAPH);
    }

    private static AbstractDescription<?> getDescOutNtesMal(
            final Lichtverhaeltnisse lichtverhaeltnisse) {
        final String erschoepftMuedeNachsatz =
                lichtverhaeltnisse == DUNKEL ?
                        "Ein Nickerchen täte dir gut" :
                        "Und müde";

        return
                // STORY Alternative:
                //                "Ein zurückschwingender "
                //                                + "Ast verpasst dir beim Abstieg ein Schramme",
                //                        .dann(),
                neuerSatz("Unten angekommen bist du ziemlich erschöpft. " +
                        erschoepftMuedeNachsatz, mins(8)
                );
    }
}