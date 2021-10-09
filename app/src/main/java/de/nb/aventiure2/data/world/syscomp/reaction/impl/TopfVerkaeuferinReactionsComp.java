package de.nb.aventiure2.data.world.syscomp.reaction.impl;

import static de.nb.aventiure2.data.time.AvTimeSpan.NO_TIME;
import static de.nb.aventiure2.data.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.time.AvTimeSpan.secs;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState.MARKTZEIT;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.IRDEN;
import static de.nb.aventiure2.german.base.ArtikelFlexionsspalte.Typ.INDEF;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.GESCHIRR;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.MARKT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.TOEPFE;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.Nominalphrase.npArtikellos;
import static de.nb.aventiure2.german.base.NumerusGenus.F;
import static de.nb.aventiure2.german.base.NumerusGenus.PL_MFN;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.NEBEN;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.VOR;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.alt;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.altNeueSaetze;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.AUFBAUEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.KLAPPERN_MIT;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.VERLASSEN;

import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.AvDateTime;
import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.data.time.AvTimeInterval;
import de.nb.aventiure2.data.world.base.Change;
import de.nb.aventiure2.data.world.counter.CounterDao;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.location.LocationSystem;
import de.nb.aventiure2.data.world.syscomp.reaction.AbstractDescribableReactionsComp;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IMovementReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.ITimePassedReactions;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.AltDescriptionsBuilder;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;

/**
 * Reaktionen der Topf-Verkäuferin - z.B. darauf, dass der Markt öffnet oder schließt.
 */
@SuppressWarnings("UnnecessaryReturnStatement")
public class TopfVerkaeuferinReactionsComp extends AbstractDescribableReactionsComp
        implements IMovementReactions, ITimePassedReactions {
    private final LocationComp locationComp;

    private static final AvTimeInterval ZEIT_AUF_MARKT =
            AvTimeInterval.fromExclusiveToInclusive(
                    MARKTZEIT.getStartExclusive().rotate(mins(20)),
                    MARKTZEIT.getEndInclusive().rotateMinus(mins(15)));

    public TopfVerkaeuferinReactionsComp(final CounterDao counterDao,
                                         final Narrator n,
                                         final World world,
                                         final LocationComp locationComp) {
        super(TOPF_VERKAEUFERIN, counterDao, n, world);
        this.locationComp = locationComp;
    }


    @Override
    public void onLeave(final ILocatableGO locatable, final ILocationGO from,
                        @Nullable final ILocationGO to) {
    }

    @Override
    public boolean isVorScVerborgen() {
        return false;
    }

    @Override
    public void onEnter(final ILocatableGO locatable, @Nullable final ILocationGO from,
                        final ILocationGO to) {
        if (locatable.is(SPIELER_CHARAKTER)) {
            onSCEnter(from, to);
            return;
        }
    }

    private void onSCEnter(@Nullable final ILocationGO from, final ILocationGO to) {
        if (world.isOrHasRecursiveLocation(to, BAUERNMARKT)) {
            onSCEnter_Bauernmarkt(from, to);
            return;
        }
    }

    private void onSCEnter_Bauernmarkt(@Nullable final ILocationGO scFrom, final ILocationGO scTo) {
        if (!world.hasSameVisibleOuterMostLocationAsSC(getGameObjectId())
                // SC und Topf-Verkäuferin sind nicht am gleichen Ort
                || isVorScVerborgen()
                || LocationSystem.isOrHasRecursiveLocation(scTo, scFrom)
                || LocationSystem.isOrHasRecursiveLocation(scFrom, scTo)
            // Der Spieler ist nur im selben Raum auf einen Tisch gestiegen,
            // wieder vom Tisch herabgestiegen o.Ä.
        ) {
            // Die Topf-Verkäuferin nicht (erneut) beschreiben.
            return;
        }

        final AltDescriptionsBuilder alt = alt();

        alt.add(neuerSatz(anaph(false).nomK()
                , "hat Töpfe und irdenes Geschirr vor sich stehen").schonLaenger());

        if (loadSC().memoryComp().isKnown(TOPF_VERKAEUFERIN)) {
            alt.add(
                    neuerSatz(anaph(false).nomK(), "klappert mit ihren Töpfen"),
                    neuerSatz(anaph(false).nomK(), "hat wohl gerade ein Schüsselchen",
                            "verkauft"),
                    neuerSatz(anaph().nomK(), "sortiert ihre irdenen Näpfe und Töpfe"));
        }

        n.narrateAlt(alt, secs(30));

        world.narrateAndUpgradeScKnownAndAssumedState(getGameObjectId());
    }

    @Override
    public void onTimePassed(final Change<AvDateTime> change) {
        final AvTime time = change.getNachher().getTime();

        if (time.isWithin(ZEIT_AUF_MARKT) && locationComp.hasNoLocation()) {
            betrittDenMarkt();
        } else if (!time.isWithin(ZEIT_AUF_MARKT)
                && locationComp.hasRecursiveLocation(BAUERNMARKT)) {
            verlaesstDenMarkt();
        }
    }

    private void betrittDenMarkt() {
        if (world.hasSameVisibleOuterMostLocationAsSC(getGameObjectId())) {
            final AltDescriptionsBuilder alt = alt();

            @Nullable final Personalpronomen anaphPersPronMusverkaeuferinWennMgl =
                    n.getAnaphPersPronWennMgl(MUS_VERKAEUFERIN);

            final SubstantivischePhrase anaph = anaph(false);

            if (anaphPersPronMusverkaeuferinWennMgl != null) {
                final EinzelneSubstantivischePhrase desc = getDescription();

                // "Neben ihr baut eine ... Töpfe und irdenes Geschirr vor sich auf"
                alt.add(AUFBAUEN
                        .mit(np(INDEF, TOEPFE).und(npArtikellos(IRDEN, GESCHIRR)))
                        .mitAdvAngabe(new AdvAngabeSkopusVerbAllg(
                                VOR.mit(desc.reflPron())))
                        .mitAdvAngabe(new AdvAngabeSkopusSatz(
                                NEBEN.mit(anaphPersPronMusverkaeuferinWennMgl)
                        ))
                        .alsSatzMitSubjekt(desc));
            }

            // "Eine ... baut Töpfe und irdenes Geschirr vor sich auf"
            alt.add(AUFBAUEN
                    .mit(np(INDEF, TOEPFE).und(npArtikellos(IRDEN, GESCHIRR)))
                    .mitAdvAngabe(new AdvAngabeSkopusVerbAllg(
                            VOR.mit(anaph.reflPron())))
                    .alsSatzMitSubjekt(anaph));

            // "Die...  klappert mit ihren Töpfen"
            alt.add(KLAPPERN_MIT
                    .mit(np(anaph.possArt(), TOEPFE))
                    .alsSatzMitSubjekt(anaph));

            if (loadSC().memoryComp().isKnown(getGameObjectId())) {
                alt.addAll(altNeueSaetze(
                        anaph.nomK(),
                        "hat wohl gerade",
                        ImmutableList.of(
                                "ein Schüsselchen", "ein Tellerchen", "ein irdenes Schälchen"
                        ),
                        "verkauft"
                ));
            }

            n.narrateAlt(alt, NO_TIME);
        }

        locationComp.narrateAndSetLocation(BAUERNMARKT);
    }

    private void verlaesstDenMarkt() {
        if (world.hasSameVisibleOuterMostLocationAsSC(getGameObjectId())) {
            final SubstantivischePhrase anaph = anaph();

            n.narrateAlt(NO_TIME,
                    neuerSatz(VERLASSEN.mit(MARKT).alsSatzMitSubjekt(anaph())),
                    du(PARAGRAPH, "siehst",
                            "wie", anaph.nomK(),
                            "alle",
                            anaph.possArt().vor(PL_MFN).akkStr(), // "ihre"
                            "Töpfe und Schälchen in ein Tuch stapelt",
                            SENTENCE,
                            anaph.persPron().nomK(),
                            "bindet es zusammen und geht",
                            anaph.getNumerusGenus() == F ? "ihrer Wege" : "davon")
            );
        }

        locationComp.narrateAndUnsetLocation();
    }
}
