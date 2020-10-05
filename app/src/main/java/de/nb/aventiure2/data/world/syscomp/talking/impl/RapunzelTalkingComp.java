package de.nb.aventiure2.data.world.syscomp.talking.impl;

import com.google.common.collect.ImmutableList;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelStateComp;
import de.nb.aventiure2.data.world.syscomp.talking.AbstractTalkingComp;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusVerbWohinWoher;
import de.nb.aventiure2.german.praedikat.PraedikatOhneLeerstellen;

import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.AUFGEDREHT;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.*;
import static de.nb.aventiure2.german.base.Nominalphrase.DEIN_HERZ;
import static de.nb.aventiure2.german.base.Nominalphrase.IHRE_HAARE;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.NumerusGenus.N;
import static de.nb.aventiure2.german.base.NumerusGenus.PL_MFN;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.ZU;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.AllgDescription.neuerSatz;
import static de.nb.aventiure2.german.description.DuDescriptionBuilder.du;
import static de.nb.aventiure2.german.praedikat.DirektivesVerb.BITTEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjDatAkk.AUSSCHUETTEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.HINUNTERLASSEN;

/**
 * Component for {@link World#RAPUNZEL}: Der Spieler
 * kann mit Rapunzel im Gespräch sein (dann auch umgekehrt).
 */
public class RapunzelTalkingComp extends AbstractTalkingComp {
    private final RapunzelStateComp stateComp;

    public RapunzelTalkingComp(final AvDatabase db,
                               final World world,
                               final RapunzelStateComp stateComp) {
        super(RAPUNZEL, db, world);
        this.stateComp = stateComp;
    }

    @Override
    protected Iterable<SCTalkAction> getSCTalkActionsWithoutCheckingConditions() {
        // "Die junge Frau bitten ihre Haare wieder hinunterzulassen"
        final PraedikatOhneLeerstellen bittenHaareHerunterzulassen = BITTEN
                .mitObj(getDescription(true))
                .mitLexikalischemKern(HINUNTERLASSEN
                        .mitObj(IHRE_HAARE)
                        .mitAdverbialerAngabe(
                                // "wieder hinunterlassen": Das "wieder" gehört
                                // quasi zu "hinunter".
                                new AdverbialeAngabeSkopusVerbWohinWoher(
                                        "wieder" )));
        return ImmutableList.of(
                SCTalkAction.entrySt(
                        () -> !haareSindHinuntergelassen(),
                        bittenHaareHerunterzulassen,
                        this::haareHerunterlassenBitte_EntryReEntry),
                SCTalkAction.st(
                        // "Der jungen Frau dein Herz ausschütten"
                        AUSSCHUETTEN
                                .mitDat(getDescription(true))
                                .mitObj(DEIN_HERZ),
                        this::herzAusschuetten),
                SCTalkAction.exitSt(
                        () -> !haareSindHinuntergelassen(),
                        bittenHaareHerunterzulassen,
                        this::haareHerunterlassenBitte_ExitImmReEntry)
        );
    }

    private boolean haareSindHinuntergelassen() {
        return stateComp.hasState(RapunzelState.HAARE_VOM_TURM_HERUNTERGELASSEN);
    }

    private void herzAusschuetten() {
        final SubstantivischePhrase anaph = getAnaphPersPronWennMglSonstShortDescription();

        final SubstantivischePhrase desc = getDescription();

        final String wovonHerzBewegtDat;
        if (loadSC().memoryComp().isKnown(RAPUNZELS_GESANG)) {
            wovonHerzBewegtDat = anaph.possArt().vor(M).dat() // "ihrem"
                    + " Gesang";
        } else {
            wovonHerzBewegtDat = anaph.possArt().vor(PL_MFN).dat() // "ihrem"
                    // STORY UND WENN GESANG UNBEKANNT?
                    + " glänzenden Locken";
        }

        // STORY Wenn man sich schon kennt und der SC gerade erst in den Raum gekommen ist und
        //  Rapunzel aufgeschreckt hat? - "Doch du....?"
        // STORY Das hier sollte nur gehen, wenn der Spieler ausreichend Vertrauen zu
        //  Rapunzel gefasst hat.
        // STORY Das hier sollte nur einmal gehen.
        n.add(du("fängst", "an ganz freundlich mit "
                        + anaph.dat()
                        + " zu reden. Du erzählst, dass von "
                        + wovonHerzBewegtDat
                        + " dein Herz so sehr sei bewegt worden, dass es dir "
                        + "keine Ruhe gelassen und du "
                        + anaph.persPron().akk()
                        + " selbst habest sehen müssen."
                        + " Da verliert "
                        + desc.nom()
                        + " ihre Angst und es bricht aus "
                        + desc.persPron().dat()
                        + " heraus."
                        + " Eine alte Zauberin hätte "
                        + desc.persPron().akk()
                        + " "
                        + desc.possArt().vor(PL_MFN).dat()  // "ihren"
                        + " Eltern fortgenommen, seit "
                        + desc.possArt().vor(N).dat()  // "ihrem"
                        + " zwölften Jahre sei "
                        + desc.persPron().nom() // "sie"
                        + " in diesen Turm geschlossen",
                "ganz freundlich",
                mins(1)));

        loadSC().feelingsComp().setMoodMin(AUFGEDREHT);
    }

    private void haareHerunterlassenBitte_EntryReEntry() {
        n.addAlt(
                // STORY Nur, wenn SC und Rapunzel sich noch nicht gut kennen
                neuerSatz(PARAGRAPH,
                        "„Ich wollte euch nicht belästigen“, sprichst du "
                                + getAnaphPersPronWennMglSonstShortDescription().akk()
                                + " an, "
                                + "„lasst mich wieder hinunter und ich lasse euch euren Frieden.“",
                        secs(10))
                        .beendet(PARAGRAPH)
                //  STORY "Oh, ich wünschte, ihr könntet / du könntest noch einen Moment bleiben!"
                //   antwortet RAPUNZEL.
        );

        haareHerunterlassen();
    }

    private void haareHerunterlassenBitte_ExitImmReEntry() {
        final SubstantivischePhrase rapunzelAnaph =
                getAnaphPersPronWennMglSonstDescription(true);
        n.addAlt(
                neuerSatz(PARAGRAPH,
                        "„Jetzt muss ich aber gehen“, sagst du unvermittelt und "
                                + "blickst "
                                + "zum Fenster hin",
                        secs(15))
                        .beendet(PARAGRAPH),
                neuerSatz(SENTENCE, "„Ich muss wieder hinaus in die Welt!“, "
                                + "sagst du",
                        secs(10)),
                // STORY Nur, wenn man sich schon duzt.
                //  - "Lässt du mich wieder hinunter, fragst du in die Stille hinein"
                //  - "Plötzlich spürst du neuen Tatendrang in dir. Lass mich gehen, sagst du, "
                //        + "bald bin ich wieder zurück"
                neuerSatz(PARAGRAPH, "„Dann will ich wieder ins "
                                + "Abenteuer hinaus“, sagst du "
                                + ZU.getDescription(rapunzelAnaph),
                        secs(15))
                        .phorikKandidat(rapunzelAnaph, RAPUNZEL)
        );

        loadSC().feelingsComp().setMoodMin(AUFGEDREHT);

        haareHerunterlassen();
    }

    private void haareHerunterlassen() {
        stateComp.rapunzelLaesstHaareZumAbstiegHerunter();

        unsetTalkingTo();
    }
}
