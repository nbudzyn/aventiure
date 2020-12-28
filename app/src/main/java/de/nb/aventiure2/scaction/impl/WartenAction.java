package de.nb.aventiure2.scaction.impl;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import com.google.common.collect.ImmutableList;

import java.util.List;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.alive.ILivingBeingGO;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.german.base.GermanUtil;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.DescriptionUmformulierer;
import de.nb.aventiure2.german.description.Kohaerenzrelation;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusSatz;
import de.nb.aventiure2.scaction.AbstractScAction;

import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.*;
import static de.nb.aventiure2.german.base.Numerus.SG;
import static de.nb.aventiure2.german.base.Person.P2;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.Kohaerenzrelation.VERSTEHT_SICH_VON_SELBST;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.WARTEN;

/**
 * Der Spielercharakter legt (wach!) eine Rast ein.
 */
public class WartenAction<LIVGO extends IDescribableGO & ILocatableGO & ILivingBeingGO>
        extends AbstractScAction {
    private final LIVGO erwartet;
    private final ILocationGO location;

    public static <LIVGO extends IDescribableGO & ILocatableGO & ILivingBeingGO>
    List<WartenAction<LIVGO>> buildActions(
            final AvDatabase db,
            final Narrator n, final World world,
            final LIVGO erwartet,
            final ILocationGO location) {
        final ImmutableList.Builder<WartenAction<LIVGO>> res = ImmutableList.builder();
        if (location.is(VOR_DEM_ALTEN_TURM_SCHATTEN_DER_BAEUME) &&
                erwartet.is(RAPUNZELS_ZAUBERIN) &&
                world.loadSC().memoryComp().isKnown(RAPUNZELS_ZAUBERIN) &&
                !erwartet.locationComp().hasSameUpperMostLocationAs(location)) {
            res.add(new WartenAction<>(db, n, world, erwartet, location));
        }

        return res.build();
    }

    @VisibleForTesting
    WartenAction(final AvDatabase db,
                 final Narrator n,
                 final World world,
                 final LIVGO erwartet,
                 final ILocationGO location) {
        super(db, n, world);
        this.erwartet = erwartet;
        this.location = location;
    }

    @Override
    public String getType() {
        return "actionRastenWarten";
    }

    @Override
    @NonNull
    public String getName() {
        // "Auf die magere Frau warten"
        return GermanUtil.capitalize(
                GermanUtil.joinToNullString(
                        WARTEN
                                .mit(world.getDescription(erwartet))
                                .getInfinitiv(P2, SG)
                )
        );
    }

    @Override
    public void narrateAndDo() {
        // FIXME Aktion
        //  - Vielleicht wollen wir vermeiden, dass sehr viele kurze Texte gedruckt werden?
        //    (Ist das überhaupt ein Problem?)
        //  - Der Text ("Du wartest sehr lange. Die Vägel singen über dir, und allmählich wirst du
        //    hungrig. Endlich kommt...") sollte vielleicht erst am Ende erzeugt werden. Er müsste
        //    dann aber aLles berücksichtigen, was zwischenzeitlich passiert ist.
        //    Vermutlich braucht man weitere Möglichkeiten, bei denen das Warten abgebrochen wird,
        //    z.B. wenn der Spieler müder oder hungriger wird?
        //  - Man könnte das Warten beim Narrator registrieren. Dann werden in der Wartezeit
        //    keine Texte geschrieben.
        //  - Rapunzels Gesang sollte das Warten abbrechen - wenn man ihn noch nicht kennt.
        //  Konzept könnte sein
        //  1. Dem Narrator vorschreiben: Nichts schreiben! ("Wartemodus")
        //  2. Reactions-Componente anweisen: Unterbrich den Wartemodus, auch wenn
        //    der Spieler hungriger wird oder müder oder ein Tageszeitenwechsel geschieht o.Ä.).
        //    In diesem Fällen muss der Spieler einen Hinweistext bekommen in der Art "Du wartest
        //    lange. Allmählich wirst du hungrig."
        //  3. Wenn der Wartemodus ausgeschaltet wurde, die letzten Texte schreiben (hoffentlich
        //    etwas wie "Endlich kommt die alte Frau" oder so).

        // Der SC wartet
        narrate();

        // Erst einmal vergeht fast keine Zeit. Die ScAutomaticReactionsComp sorgt
        // im onTimePassed() im Zusammenspiel mit der WaitingComp dafür, dass die
        // Zeit vergeht (maximal 3 Stunden).
        world.loadSC().waitingComp().startWaiting(db.nowDao().now().plus(hours(3)));

        sc.memoryComp().setLastAction(buildMemorizedAction());
    }

    private void narrate() {
        final Kohaerenzrelation kohaerenzrelation = getKohaerenzrelationFuerUmformulierung();

        if (kohaerenzrelation == VERSTEHT_SICH_VON_SELBST) {
            final SubstantivischePhrase anaph =
                    getAnaphPersPronWennMglSonstDescription(erwartet, false);
            n.narrateAlt(secs(5),
                    du(WARTEN.mit(anaph))
                            .dann()
                            .phorikKandidat(anaph, erwartet.getId()),
                    du("beginnst",
                            "auf "
                                    + anaph.akk()
                                    + " zu warten")
                            .dann()
                            .phorikKandidat(anaph, erwartet.getId()));
        } else {
            final SubstantivischePhrase anaph =
                    getAnaphPersPronWennMglSonstDescription(erwartet, true);
            n.narrateAlt(
                    DescriptionUmformulierer.drueckeAus(
                            kohaerenzrelation,
                            du(WARTEN
                                    .mit(anaph)
                                    .mitAdverbialerAngabe(
                                            new AdverbialeAngabeSkopusSatz("weiter")
                                    )
                            )
                                    .dann()
                                    .phorikKandidat(anaph, erwartet.getId())
                    ), secs(5));
        }
    }

    @Override
    protected boolean isDefinitivWiederholung() {
        return false;
    }

    @Override
    protected boolean isDefinitivFortsetzung() {
        return buildMemorizedAction().equals(sc.memoryComp().getLastAction());
    }

    @Override
    protected boolean isDefinitivDiskontinuitaet() {
        return false;
    }

    @NonNull
    private Action buildMemorizedAction() {
        return new Action(Action.Type.WARTEN, erwartet);
    }
}
