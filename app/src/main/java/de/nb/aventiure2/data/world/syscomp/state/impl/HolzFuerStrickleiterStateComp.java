package de.nb.aventiure2.data.world.syscomp.state.impl;

import static de.nb.aventiure2.data.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.state.impl.HolzFuerStrickleiterState.AM_BAUM;
import static de.nb.aventiure2.data.world.syscomp.state.impl.HolzFuerStrickleiterState.GESAMMELT;
import static de.nb.aventiure2.data.world.syscomp.state.impl.HolzFuerStrickleiterState.IN_STUECKE_GEBROCHEN;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.HANDLICH;
import static de.nb.aventiure2.german.base.ArtikelwortFlexionsspalte.Typ.ETWAS;
import static de.nb.aventiure2.german.base.ArtikelwortFlexionsspalte.Typ.INDEF;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.STUECKE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.ZEIT;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.Numerus.SG;
import static de.nb.aventiure2.german.base.Person.P2;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.IN_AKK;
import static de.nb.aventiure2.german.praedikat.ReflVerbSubjObj.SICH_NEHMEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.BRECHEN;
import static de.nb.aventiure2.german.string.GermanStringUtil.capitalize;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.state.AbstractStateComp;
import de.nb.aventiure2.data.world.syscomp.state.StateModification;
import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.DescriptionBuilder;
import de.nb.aventiure2.german.description.TimedDescription;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.praedikat.ZweiPraedikateOhneLeerstellen;

public class HolzFuerStrickleiterStateComp extends AbstractStateComp<HolzFuerStrickleiterState> {
    public HolzFuerStrickleiterStateComp(final AvDatabase db,
                                         final TimeTaker timeTaker,
                                         final World world) {
        super(HOLZ_FUER_STRICKLEITER, db, timeTaker, world,
                AM_BAUM);
    }

    @Override
    public ImmutableList<StateModification<HolzFuerStrickleiterState>> getScStateModificationData() {
        if (hasState(GESAMMELT)) {
            return ImmutableList.of(
                    new StateModification<>(capitalize(
                            BRECHEN.mit(getDescription(true))
                                    .mitAdvAngabe(new AdvAngabeSkopusVerbAllg(
                                            IN_AKK.mit(np(INDEF, STUECKE))))
                                    .getInfinitiv(P2, SG).joinToString()),
                            this::altTimedDescriptionsAndDo_holzZerbrechen,
                            IN_STUECKE_GEBROCHEN)
            );
        }

        return ImmutableList.of();
    }

    private ImmutableCollection<TimedDescription<?>>
    altTimedDescriptionsAndDo_holzZerbrechen() {
        return ImmutableList.of(
                DescriptionBuilder.du(
                        new ZweiPraedikateOhneLeerstellen(
                                SICH_NEHMEN.mit(np(ETWAS, ZEIT)),
                                BRECHEN.mit(getDescription())
                                        .mitAdvAngabe(new AdvAngabeSkopusVerbAllg(
                                                IN_AKK.mit(np(INDEF, HANDLICH, STUECKE))))))
                        .timed(mins(10))
                        .dann());
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
    protected SubstantivischePhrase anaph() {
        return anaph(true);
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
    protected SubstantivischePhrase anaph(final boolean descShortIfKnown) {
        return anaph(getGameObjectId(), descShortIfKnown);
    }

    /**
     * Gibt eine Nominalphrase zurück, die das Game Object beschreibt.
     * Die Phrase kann unterschiedlich sein, je nachdem,
     * ob der Spieler das Game Object schon kennt oder nicht.
     */
    protected EinzelneSubstantivischePhrase getDescription() {
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
    protected EinzelneSubstantivischePhrase getDescription(final boolean shortIfKnown) {
        return getDescription(getGameObjectId(), shortIfKnown);
    }

}
