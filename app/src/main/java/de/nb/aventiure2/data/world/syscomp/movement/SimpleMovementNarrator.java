package de.nb.aventiure2.data.world.syscomp.movement;

import androidx.annotation.NonNull;

import javax.annotation.Nullable;

import de.nb.aventiure2.data.storystate.StoryStateDao;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.gameobject.player.SpielerCharakter;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.ISpatiallyConnectedGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static de.nb.aventiure2.data.world.syscomp.memory.Action.Type.BEWEGEN;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;
import static de.nb.aventiure2.german.base.AllgDescription.neuerSatz;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;

/**
 * Grundlegende Implementierung, um dem Spieler die Bewegung
 * eines {@link de.nb.aventiure2.data.world.syscomp.movement.IMovingGO} zu beschreiben
 */
public class SimpleMovementNarrator implements IMovementNarrator {
    protected final GameObjectId gameObjectId;
    protected final StoryStateDao n;
    protected final World world;

    public SimpleMovementNarrator(
            final GameObjectId gameObjectId,
            final StoryStateDao storyStateDao,
            final World world) {
        this.gameObjectId = gameObjectId;
        n = storyStateDao;
        this.world = world;
    }

    @Override
    public <FROM extends ILocationGO & ISpatiallyConnectedGO>
    AvTimeSpan narrateAndDoStartsLeaving(
            final FROM from, final ILocationGO to) {
        final AvTimeSpan extraTime = narrateStartsLeaving(from, to);

        world.upgradeKnownToSC(gameObjectId);

        return extraTime;
    }

    private <FROM extends ILocationGO & ISpatiallyConnectedGO>
    AvTimeSpan narrateStartsLeaving(
            final FROM from, final ILocationGO to) {
        @Nullable final ILocationGO scLastLocation = loadSC().locationComp().getLastLocation();

        if (world.isOrHasRecursiveLocation(scLastLocation, to)) {
            return narrateMovingGOKommtSCEntgegenUndGehtAnSCVorbei(from, to);
        }

        return narrateGehtWeg(from, to);
    }

    protected <FROM extends ILocationGO & ISpatiallyConnectedGO>
    AvTimeSpan narrateMovingGOKommtSCEntgegenUndGehtAnSCVorbei(
            final FROM from, final ILocationGO to
    ) {
        final Nominalphrase desc = getDescription();
        final SubstantivischePhrase anaphOderDesc =
                getAnaphPersPronWennMglSonstDescription(false);

        return n.addAlt(
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nom() +
                                " kommt dir entgegen und geht an dir vorbei", noTime())
                        .phorikKandidat(desc, gameObjectId),
                neuerSatz(PARAGRAPH,
                        "Dir kommt " +
                                desc.nom() +
                                " entgegen und geht an dir vorbei", noTime())
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        "Dir kommt " +
                                desc.nom() +
                                " entgegen und geht hinter dir von dannen", noTime())
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        "Dir kommt " +
                                desc.nom() +
                                " entgegen und geht hinter dir davon", noTime())
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        desc.nom() +
                                " kommt auf dich zu und geht an dir vorbei", noTime())
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        desc.nom() +
                                " kommt auf dich zu und läuft vorbei", noTime())
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(PARAGRAPH)
        );
    }

    protected <FROM extends ILocationGO & ISpatiallyConnectedGO>
    AvTimeSpan narrateGehtWeg(
            final FROM from, final ILocationGO to
    ) {
        final SubstantivischePhrase anaphOderDesc =
                getAnaphPersPronWennMglSonstDescription(false);

        return n.addAlt(
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nom() +
                                " geht von dannen", noTime())
                        .phorikKandidat(anaphOderDesc, gameObjectId)
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nom() +
                                " geht davon", noTime())
                        .phorikKandidat(anaphOderDesc, gameObjectId)
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nom() +
                                " geht weiter", noTime())
                        .phorikKandidat(anaphOderDesc, gameObjectId)
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nom() +
                                " geht weg", noTime())
                        .phorikKandidat(anaphOderDesc, gameObjectId)
                        .beendet(PARAGRAPH),
                // STORY: "X geht seines / ihres Wegs" - Possessivartikel vor Genitiv!
                // STORY: "X geht seiner / ihrer Wege" - Possessivartikel vor Genitiv!
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nom() +
                                " geht fort", noTime())
                        .phorikKandidat(anaphOderDesc, gameObjectId)
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nom() +
                                " läuft vorbei", noTime())
                        .phorikKandidat(anaphOderDesc, gameObjectId)
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nom() +
                                " läuft weiter", noTime())
                        .phorikKandidat(anaphOderDesc, gameObjectId)
                        .beendet(PARAGRAPH)
        );
    }

    @Override
    public <FROM extends ILocationGO & ISpatiallyConnectedGO>
    AvTimeSpan narrateAndDoStartsEntering(
            final FROM from, final ILocationGO to) {
        final AvTimeSpan extraTime = narrateStartsEntering(from, to);

        world.upgradeKnownToSC(gameObjectId);

        return extraTime;
    }

    final protected <FROM extends ILocationGO & ISpatiallyConnectedGO>
    AvTimeSpan narrateStartsEntering(
            final FROM from, final ILocationGO to) {
        @Nullable final ILocationGO scLastLocation =
                loadSC().locationComp().getLastLocation();
        if (loadSC().memoryComp().getLastAction().is(BEWEGEN)) {
            if (world.isOrHasRecursiveLocation(scLastLocation, from)) {
                return narrateMovingGOKommtSCNach(from, to);
            }

            if (!world.isOrHasRecursiveLocation(scLastLocation, from)) {
                return narrateMovingGOKommtScEntgegen(from, to);
            }
        }

        // Default
        return narrateKommtGegangen(from, to);
    }

    protected <FROM extends ILocationGO & ISpatiallyConnectedGO>
    AvTimeSpan narrateMovingGOKommtSCNach(
            final FROM from, final ILocationGO to
    ) {
        final Nominalphrase desc = getDescription();
        final SubstantivischePhrase anaphOderDesc =
                getAnaphPersPronWennMglSonstDescription(false);


        return n.addAlt(
                neuerSatz(PARAGRAPH,
                        "Dir kommt " +
                                desc.nom() +
                                " nach", noTime())
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        "Hinter dir geht " +
                                desc.nom(), noTime())
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nom() +
                                " geht hinter dir her", noTime())
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        "Hinter dir kommt " +
                                desc.nom() +
                                " gegangen", noTime())
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        "Dir kommt " +
                                desc.nom() +
                                " heran", noTime())
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(PARAGRAPH)
        );
    }

    protected <FROM extends ILocationGO & ISpatiallyConnectedGO>
    AvTimeSpan narrateMovingGOKommtScEntgegen(
            final FROM from, final ILocationGO to
    ) {
        final Nominalphrase desc = getDescription();

        // STORY Wenn to mehr als zwei Zugänge hat ist auch bei "entgegen" nicht klar,
        //  woher das IMovingGO kommt. Aus der SpatialConnection Details erfragen, z.B.
        //  "den kleinen Pfad herab kommt", "auf dem Weg geht X",
        //  "Von dem Pfad her kommt X gegangen" o.Ä.

        return n.addAlt(
                neuerSatz(PARAGRAPH,
                        "Dir kommt " +
                                desc.nom() +
                                " entgegen", noTime())
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        "Dir kommt " +
                                desc.nom() +
                                " entgegengegangen", noTime())
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(PARAGRAPH)
        );
    }

    @NonNull
    protected <FROM extends ILocationGO & ISpatiallyConnectedGO>
    AvTimeSpan narrateKommtGegangen(
            final FROM from, final ILocationGO to
    ) {
        final Nominalphrase desc = getDescription();
        final SubstantivischePhrase anaphOderDesc =
                getAnaphPersPronWennMglSonstDescription(false);

        return n.addAlt(neuerSatz(PARAGRAPH,
                anaphOderDesc.nom() + " kommt herzu", noTime())
                        .phorikKandidat(anaphOderDesc, gameObjectId),
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nom() + " kommt heran", noTime())
                        .phorikKandidat(anaphOderDesc, gameObjectId),
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nom() + " kommt gegangen", noTime())
                        .phorikKandidat(anaphOderDesc, gameObjectId),
                neuerSatz(PARAGRAPH,
                        "Es kommt " +
                                desc.nom(), noTime())
                        .phorikKandidat(desc, gameObjectId),
                neuerSatz(PARAGRAPH,
                        desc.nom()
                                + " kommt daher", noTime())
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        desc.nom()
                                + " kommt gegangen", noTime())
                        .phorikKandidat(desc, gameObjectId),
                neuerSatz(PARAGRAPH,
                        desc.nom()
                                + " kommt", noTime())
                        .phorikKandidat(desc, gameObjectId)
        );
    }

    /**
     * Gibt das Personalpronomen zurück, mit dem ein
     * anaphorischer Bezug auf dieses
     * Game Object möglich ist - wenn das nicht möglich ist, dann eine kurze
     * Beschreibung des Game Objects.
     * <br/>
     * Es muss sich um eine {@link IDescribableGO} handeln!
     * <br/>
     * Beispiel 1: "Du hebst die Lampe auf..." - jetzt ist ein anaphorischer Bezug
     * auf die Lampe möglich und diese Methode gibt "sie" zurück.
     * <br/>
     * Beispiel 2: "Du zündest das Feuer an..." - jetzt ist <i>kein</i> anaphorischer Bezug
     * auf die Lampe möglich und diese Methode gibt "die Lampe" zurück.
     */
    protected final SubstantivischePhrase getAnaphPersPronWennMglSonstShortDescription() {
        return getAnaphPersPronWennMglSonstDescription(true);
    }

    /**
     * Gibt das Personalpronomen zurück, mit dem ein
     * anaphorischer Bezug auf dieses
     * Game Object möglich ist - wenn das nicht möglich ist, dann eine
     * Beschreibung des Game Objects.
     * <br/>
     * Beispiel 1: "Du hebst die Lampe auf..." - jetzt ist ein anaphorischer Bezug
     * auf die Lampe möglich und diese Methode gibt "sie" zurück.
     * <br/>
     * Beispiel 2: "Du zündest das Feuer an..." - jetzt ist <i>kein</i> anaphorischer Bezug
     * auf die Lampe möglich und diese Methode gibt "die mysteriöse Lampe" zurück.
     */
    protected final SubstantivischePhrase getAnaphPersPronWennMglSonstDescription(
            final boolean descShortIfKnown) {

        final IDescribableGO describableGO = (IDescribableGO) world.load(getGameObjectId());

        @Nullable final Personalpronomen anaphPersPron =
                n.getStoryState().getAnaphPersPronWennMgl(describableGO);
        if (anaphPersPron != null) {
            return anaphPersPron;
        }

        return world.getDescription(describableGO, descShortIfKnown);
    }

    /**
     * Gibt eine Nominalphrase zurück, die das Game Object beschreibt.
     * Die Phrase kann unterschiedlich sein, je nachdem,
     * ob der Spieler das Game Object schon kennt oder nicht.
     */
    protected final Nominalphrase getDescription() {
        return getDescription(false);
    }

    /**
     * Gibt eine Nominalphrase zurück, die das Game Object beschreibt.
     * Die Phrase kann unterschiedlich sein, je nachdem,
     * ob der Spieler das Game Object schon kennt oder nicht.
     *
     * @param shortIfKnown <i>Falls der Spieler(-charakter)</i> das
     *                     Game Object schon kennt, wird eher eine
     *                     kürzere Beschreibung gewählt
     */
    protected final Nominalphrase getDescription(final boolean shortIfKnown) {
        return world.getDescription(gameObjectId, shortIfKnown);
    }

    @NonNull
    protected final SpielerCharakter loadSC() {
        return world.loadSC();
    }

    protected final GameObjectId getGameObjectId() {
        return gameObjectId;
    }
}
