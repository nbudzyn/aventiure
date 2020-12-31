package de.nb.aventiure2.data.world.syscomp.talking.impl;

import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import java.util.List;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzStateComp;
import de.nb.aventiure2.data.world.syscomp.talking.AbstractTalkingComp;
import de.nb.aventiure2.german.base.Indefinitpronomen;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static de.nb.aventiure2.data.time.AvTimeSpan.noTime;
import static de.nb.aventiure2.data.time.AvTimeSpan.secs;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.VOLLER_FREUDE;
import static de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState.ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS;
import static de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState.HAT_FORDERUNG_GESTELLT;
import static de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState.HAT_NACH_BELOHNUNG_GEFRAGT;
import static de.nb.aventiure2.data.world.syscomp.talking.impl.SCTalkAction.entrySt;
import static de.nb.aventiure2.data.world.syscomp.talking.impl.SCTalkAction.exitSt;
import static de.nb.aventiure2.data.world.syscomp.talking.impl.SCTalkAction.immReEntrySt;
import static de.nb.aventiure2.data.world.syscomp.talking.impl.SCTalkAction.reEntrySt;
import static de.nb.aventiure2.data.world.syscomp.talking.impl.SCTalkAction.st;
import static de.nb.aventiure2.german.base.GermanUtil.capitalize;
import static de.nb.aventiure2.german.base.Indefinitpronomen.ALLES;
import static de.nb.aventiure2.german.base.Nominalphrase.ANGEBOTE;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static de.nb.aventiure2.german.description.DescriptionBuilder.satzanschluss;
import static de.nb.aventiure2.german.praedikat.SeinUtil.istSind;
import static de.nb.aventiure2.german.praedikat.VerbSubjDatAkk.MACHEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjDatAkk.VERSPRECHEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.DISKUTIEREN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.IGNORIEREN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.REDEN;

/**
 * Component for den {@link World#FROSCHPRINZ}en: Der Spieler
 * kann mit dem Froschprinzen im Gespräch sein (dann auch umgekehrt).
 * <p>
 * Es gibt {@link SCTalkAction}s, also mögliche Redebeiträge, die der
 * Spieler(-Charakter) an den Froschprinzen richten kann (und auf die der
 * Froschprinz dann jeweils reagiert).
 */
public class FroschprinzTalkingComp extends AbstractTalkingComp {
    private final FroschprinzStateComp stateComp;

    public FroschprinzTalkingComp(final AvDatabase db,
                                  final Narrator n, final World world,
                                  final FroschprinzStateComp stateComp,
                                  final boolean initialSchonBegruesstMitSC) {
        super(FROSCHPRINZ, db, n, world, initialSchonBegruesstMitSC);
        this.stateComp = stateComp;
    }

    @Override
    protected Iterable<SCTalkAction> getSCTalkActionsWithoutCheckingConditions() {
        final ImmutableList<? extends IDescribableGO> objectsInDenBrunnenGefallen =
                getObjectsInDenBrunnenGefallen();

        switch (stateComp.getState()) {
            case UNAUFFAELLIG:
                return ImmutableList.of();
            case HAT_SC_HILFSBEREIT_ANGESPROCHEN:
                return ImmutableList.of(
                        st(REDEN,
                                this::froschHatAngesprochen_antworten),
                        exitSt(IGNORIEREN,
                                this::froschHatAngesprochen_Exit),
                        immReEntrySt(this::froschHatAngesprochen_ImmReEntry),
                        reEntrySt(this::froschHatAngesprochen_ReEntry)
                );
            case HAT_NACH_BELOHNUNG_GEFRAGT:
                return ImmutableList.of(
                        st(() -> !objectsInDenBrunnenGefallen.isEmpty(),
                                MACHEN.mitAkk(ANGEBOTE),
                                this::froschHatNachBelohnungGefragt_AngeboteMachen),
                        exitSt(this::froschHatNachBelohnungGefragt_Exit),
                        immReEntrySt(() -> !objectsInDenBrunnenGefallen.isEmpty(),
                                MACHEN.mitAkk(ANGEBOTE),
                                this::froschHatNachBelohnungGefragt_ImmReEntry),
                        immReEntrySt(objectsInDenBrunnenGefallen::isEmpty,
                                this::ansprechen_froschReagiertNicht),
                        reEntrySt(() -> !objectsInDenBrunnenGefallen.isEmpty(),
                                MACHEN.mitAkk(ANGEBOTE),
                                this::froschHatNachBelohnungGefragt_ReEntry),
                        reEntrySt(objectsInDenBrunnenGefallen::isEmpty,
                                this::ansprechen_froschReagiertNicht)
                );
            case HAT_FORDERUNG_GESTELLT:
                return ImmutableList.of(
                        st(() -> !objectsInDenBrunnenGefallen.isEmpty(),
                                VERSPRECHEN.mitAkk(ALLES),
                                this::froschHatForderungGestellt_AllesVersprechen),
                        exitSt(this::froschHatForderungGestellt_Exit),
                        immReEntrySt(() -> !objectsInDenBrunnenGefallen.isEmpty(),
                                VERSPRECHEN.mitAkk(ALLES),
                                this::froschHatForderungGestellt_ImmReEntry),
                        immReEntrySt(objectsInDenBrunnenGefallen::isEmpty,
                                this::ansprechen_froschReagiertNicht),
                        reEntrySt(() -> !objectsInDenBrunnenGefallen.isEmpty(),
                                VERSPRECHEN.mitAkk(ALLES),
                                this::froschHatForderungGestellt_reEntry),
                        reEntrySt(objectsInDenBrunnenGefallen::isEmpty,
                                this::ansprechen_froschReagiertNicht)
                );
            case ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS:
                return ImmutableList.of(
                        entrySt(this::ansprechen_froschErinnertAnVersprechen)
                );
            case WARTET_AUF_SC_BEIM_SCHLOSSFEST:
            case HAT_HOCHHEBEN_GEFORDERT:
                return ImmutableList.of();
            case BEIM_SCHLOSSFEST_AUF_TISCH_WILL_ZUSAMMEN_ESSEN:
                return ImmutableList.of(
                        entrySt(DISKUTIEREN, this::froschAufTischDraengelt)
                );
            case ZURUECKVERWANDELT_IN_VORHALLE:
            case ZURUECKVERWANDELT_SCHLOSS_VORHALLE_VERLASSEN:
                return ImmutableList.of();
            default:
                throw new IllegalStateException("Unexpected Froschprinz state: "
                        + stateComp.getState());
        }
    }

    /**
     * Gibt eine Beschreibung dieses Objekts zurück - wenn es nur eines ist - sonst
     * etwas wie "die Dinge".
     */
    private SubstantivischePhrase getDescriptionSingleOrCollective(
            final List<? extends IDescribableGO> objects) {
        if (objects.isEmpty()) {
            return Indefinitpronomen.NICHTS;
        }

        if (objects.size() == 1) {
            final IDescribableGO objectInDenBrunnenGefallen =
                    objects.iterator().next();

            return world.getDescription(objectInDenBrunnenGefallen, false);
        }

        return Nominalphrase.DINGE;
    }

    private String getAkkShort(final List<? extends IDescribableGO> objects) {
        if (objects.size() == 1) {
            return world.getDescription(objects.iterator().next(), true).akk();
        }

        return "sie";
    }

    // -------------------------------------------------------------------------------
    // .. HAT_SC_HILFSBEREIT_ANGESPROCHEN
    // -------------------------------------------------------------------------------
    private void froschHatAngesprochen_antworten() {
        final ImmutableList<? extends IDescribableGO> objectsInDenBrunnenGefallen =
                getObjectsInDenBrunnenGefallen();

        if (objectsInDenBrunnenGefallen.isEmpty()) {
            n.narrate(neuerSatz("„Ach, du bist's, alter Wasserpatscher“, sagst du",
                    secs(5))
                    .undWartest()
                    .dann());

            setSchonBegruesstMitSC(true);
            unsetTalkingTo();
            return;
        }

        inDenBrunnenGefallenErklaerung();
        herausholenAngebot();
    }

    private void froschHatAngesprochen_ImmReEntry() {
        final ImmutableList<? extends IDescribableGO> objectsInDenBrunnenGefallen =
                getObjectsInDenBrunnenGefallen();

        if (objectsInDenBrunnenGefallen.isEmpty()) {
            ansprechen_froschReagiertNicht();
            return;
        }

        if (n.dann()) {
            if (n.allowsAdditionalDuSatzreihengliedOhneSubjekt()) {
                n.narrate(satzanschluss("– aber dann gibst du dir einen Ruck:",
                        noTime()));
            } else {
                n.narrate(neuerSatz("Aber dann gibst du dir einen Ruck:", noTime()));
            }
        } else {
            n.narrate(du("gibst", "dir einen Ruck:", noTime()));
        }

        froschHatAngesprochen_ReEntry();
    }

    private void froschHatAngesprochen_ReEntry() {
        final ImmutableList<? extends IDescribableGO> objectsInDenBrunnenGefallen =
                getObjectsInDenBrunnenGefallen();

        if (objectsInDenBrunnenGefallen.isEmpty()) {
            ansprechen_froschReagiertNicht();
            return;
        }

        n.narrate(neuerSatz("„Hallo, du hässlicher Frosch!“, redest du ihn an", noTime())
                .undWartest()
                .dann());

        world.loadSC().talkingComp().setTalkingTo(FROSCHPRINZ);

        inDenBrunnenGefallenErklaerung();
        herausholenAngebot();
    }

    private void inDenBrunnenGefallenErklaerung() {
        final ImmutableList<? extends IDescribableGO> objectsInDenBrunnenGefallen =
                getObjectsInDenBrunnenGefallen();

        if (world.loadSC().memoryComp().getLastAction().is(Action.Type.HEULEN)) {
            final SubstantivischePhrase objectsDesc =
                    getDescriptionSingleOrCollective(objectsInDenBrunnenGefallen);
            // die goldene Kugel
            // die
            n.narrate(neuerSatz("„Ich weine über "
                    + objectsDesc.akk() // die goldene Kugel
                    + ", "
                    + objectsDesc.relPron().akk() // die
                    + " mir in den Brunnen hinabgefallen " +
                    istSind(objectsDesc.getNumerusGenus()) +
                    ".“", secs(10)));
            setSchonBegruesstMitSC(true);
            return;
        }

        if (objectsInDenBrunnenGefallen.size() == 1) {
            final IDescribableGO objectInDenBrunnenGefallen =
                    objectsInDenBrunnenGefallen.iterator().next();

            n.narrate(neuerSatz("„"
                    + capitalize(world.getDescription(objectInDenBrunnenGefallen).nom())
                    + " ist mir in den Brunnen hinabgefallen.“", secs(10)));
            setSchonBegruesstMitSC(true);
            return;
        }

        setSchonBegruesstMitSC(true);
        n.narrate(neuerSatz("„Mir sind Dinge in den Brunnen hinabgefallen.“", secs(5)));
    }

    private void herausholenAngebot() {
        final ImmutableList<? extends IDescribableGO> objectsInDenBrunnenGefallen =
                getObjectsInDenBrunnenGefallen();

        final String objectsInDenBrunnenGefallenShortAkk =
                getAkkShort(objectsInDenBrunnenGefallen);

        final String ratschlag;
        if (world.loadSC().memoryComp().getLastAction().is(Action.Type.HEULEN)) {
            ratschlag = "weine nicht";
        } else {
            ratschlag = "sorge dich nicht";
        }

        n.narrate(neuerSatz(PARAGRAPH, "„Sei still und "
                        + ratschlag
                        + "“, antwortet "
                        + getDescription(true).nom()
                        + ", „ich kann wohl Rat schaffen, aber was gibst du mir, wenn ich "
                        + objectsInDenBrunnenGefallenShortAkk
                        + " wieder heraufhole?“",
                secs(15))
                .beendet(PARAGRAPH));

        setSchonBegruesstMitSC(true);
        stateComp.narrateAndSetState(HAT_NACH_BELOHNUNG_GEFRAGT);
    }

    private void froschHatAngesprochen_Exit() {
        n.narrate(du(SENTENCE, "tust", ", als hättest du nichts gehört",
                secs(3))
                .komma()
                .undWartest()
                .dann());

        unsetTalkingTo();
    }

    // -------------------------------------------------------------------------------
    // .. HAT_NACH_BELOHNUNG_GEFRAGT
    // -------------------------------------------------------------------------------

    private void froschHatNachBelohnungGefragt_AngeboteMachen() {
        world.loadSC().talkingComp().setTalkingTo(FROSCHPRINZ);

        n.narrate(neuerSatz(
                "„Was du haben willst, lieber Frosch“, sagst du, „meine Kleider, "
                        + "Reichtümer oder Edelsteine?“", secs(5)));

        final ImmutableList<? extends IDescribableGO> objectsInDenBrunnenGefallen =
                getObjectsInDenBrunnenGefallen();

        n.narrate(neuerSatz(PARAGRAPH,
                "Der Frosch antwortet: „Deine Kleider, Reichtümer oder Edelsteine, die mag "
                        + "ich nicht. "
                        + "Aber wenn ich am Tischlein bei dir sitzen soll und von deinem Tellerlein "
                        + "essen: Wenn du mir das versprichst, so will ich "
                        + "hinuntersteigen und dir "
                        // die goldene Kugel / die Dinge
                        + getDescriptionSingleOrCollective(objectsInDenBrunnenGefallen).akk()
                        + " wieder heraufholen.“", secs(15))
                .beendet(PARAGRAPH));

        setSchonBegruesstMitSC(true);
        stateComp.narrateAndSetState(HAT_FORDERUNG_GESTELLT);
    }

    private void froschHatNachBelohnungGefragt_ImmReEntry() {
        n.narrate(du(SENTENCE, "gehst", "kurz in dich…", secs(5)));

        froschHatNachBelohnungGefragt_ReEntry();
    }

    private void froschHatNachBelohnungGefragt_ReEntry() {
        world.loadSC().talkingComp().setTalkingTo(getGameObjectId());

        n.narrate(
                neuerSatz(PARAGRAPH, "„Frosch“, sprichst du ihn an, „steht dein Angebot noch?“",
                        secs(5)));

        setSchonBegruesstMitSC(true);

        n.narrate(
                neuerSatz(PARAGRAPH,
                        "„Sicher“, antwortet der Frosch, „ich kann dir alles aus dem Brunnen "
                                + "holen, was hineingefallen ist. Was gibst du mir dafür?“ "
                                + "„Was du haben willst, lieber Frosch“, sagst du, „meine Kleider, "
                                + "Reichtümer oder Edelsteine?“",
                        secs(10)));


        final ImmutableList<? extends IDescribableGO> objectsInDenBrunnenGefallen =
                getObjectsInDenBrunnenGefallen();

        n.narrate(neuerSatz(PARAGRAPH,
                "Der Frosch antwortet: „Deine Kleider, Reichtümer oder Edelsteine, die mag "
                        + "ich nicht. "
                        + "Aber wenn ich am Tischlein neben dir sitzen soll, von deinem Tellerlein "
                        + "essen und "
                        + "aus deinem Becherlein trinken: Wenn du mir das versprichst, so will ich "
                        + "hinuntersteigen und dir "
                        // die goldene Kugel / die Dinge
                        + getDescriptionSingleOrCollective(objectsInDenBrunnenGefallen).akk()
                        + " wieder herauf holen.“",
                secs(15))
                .beendet(PARAGRAPH));

        stateComp.narrateAndSetState(HAT_FORDERUNG_GESTELLT);
    }

    private void froschHatNachBelohnungGefragt_Exit() {
        n.narrate(
                neuerSatz(
                        "„Denkst du etwa, ich überschütte dich mit Gold "
                                + "und Juwelen? – Vergiss es!“",
                        secs(5)));

        setSchonBegruesstMitSC(true);
        unsetTalkingTo();
    }

    // -------------------------------------------------------------------------------
    // .. HAT_NACH_BELOHNUNG_GEFRAGT
    // -------------------------------------------------------------------------------
    private void froschHatForderungGestellt_AllesVersprechen() {
        final ImmutableList<? extends IDescribableGO> objectsInDenBrunnenGefallen =
                getObjectsInDenBrunnenGefallen();

        // die goldene Kugel / die Dinge
        n.narrate(
                neuerSatz(PARAGRAPH, "„Ach ja“, sagst du, „ich verspreche dir alles, was du "
                                + "willst, wenn du mir nur "
                                + getDescriptionSingleOrCollective(objectsInDenBrunnenGefallen).akk()
                                + " wiederbringst.“ Du denkst "
                                + "aber: „Was der einfältige Frosch schwätzt, der sitzt im Wasser bei "
                                + "seinesgleichen und quakt und kann keines Menschen Geselle sein.“",
                        secs(20)));

        froschReagiertAufVersprechen();
    }

    private void froschHatForderungGestellt_ImmReEntry() {
        final ImmutableList<? extends IDescribableGO> objectsInDenBrunnenGefallen =
                getObjectsInDenBrunnenGefallen();

        n.narrate(neuerSatz(
                "Aber im nächsten Moment entschuldigst du dich schon: "
                        + "„Nichts für ungut! Wenn du mir wirklich "
                        + getDescriptionSingleOrCollective(objectsInDenBrunnenGefallen).akk()
                        + " wieder besorgen kannst – ich verspreche dir alles, was du willst!“"
                        + " Bei dir selbst denkst du: "
                        + "„Was der einfältige Frosch schwätzt, der sitzt im Wasser bei "
                        + "seinesgleichen und quakt und kann keines Menschen Geselle sein.“",
                secs(20)));

        froschReagiertAufVersprechen();
    }

    private void froschHatForderungGestellt_reEntry() {
        final ImmutableList<? extends IDescribableGO> objectsInDenBrunnenGefallen =
                getObjectsInDenBrunnenGefallen();

        n.narrate(neuerSatz(
                "„Lieber Frosch“, sagst du, „ich habe es mir überlegt. Ich verspreche dir alles, "
                        + "was du "
                        + "willst, wenn du mir nur "
                        + getDescriptionSingleOrCollective(objectsInDenBrunnenGefallen).akk()
                        + " wiederbringst.“ – Was du dir eigentlich überlegt hast, ist:"
                        + " „Was der einfältige Frosch schwätzt, der sitzt im"
                        + " Wasser bei seinesgleichen und quakt und kann keines Menschen "
                        + "Geselle sein.“", secs(20)));

        froschReagiertAufVersprechen();
    }

    private <LOC_DESC extends ILocatableGO & IDescribableGO> void froschReagiertAufVersprechen() {
        n.narrate(neuerSatz(PARAGRAPH,
                "Der Frosch, als er die Zusage erhalten hat,",
                noTime()));

        @Nullable final GameObjectId scLocationId = world.loadSC().locationComp().getLocationId();

        final ImmutableList<LOC_DESC> objectsInDenBrunnenGefallen =
                getObjectsInDenBrunnenGefallen();

        final SubstantivischePhrase descObjectsInDenBrunnenGefallen =
                getDescriptionSingleOrCollective(objectsInDenBrunnenGefallen);

        world.loadSC().feelingsComp().requestMoodMin(VOLLER_FREUDE);

        n.narrate(satzanschluss("taucht seinen Kopf "
                        + "unter, sinkt hinab und über ein Weilchen kommt er wieder herauf gerudert, "
                        + "hat "
                        // die goldene Kugel / die Dinge
                        + descObjectsInDenBrunnenGefallen.akk()
                        + " im Maul und wirft "
                        + descObjectsInDenBrunnenGefallen.persPron().akk()
                        + " ins Gras. Du "
                        + "bist voll Freude, als du "
                        // die goldene Kugel / die Dinge / IDEA Synonym: "das schöne Spielzeug"
                        // Idee zu Synonymen: Synonyme sollte erst NACH dem Originalbegriff auftauchen
                        // und automatisch gewählt werden, wenn syn() oder Ähnliches
                        // programmiert wird.
                        + descObjectsInDenBrunnenGefallen.akk()
                        + " wieder erblickst",
                secs(30)));

        for (final LOC_DESC object : objectsInDenBrunnenGefallen) {
            object.locationComp()
                    .narrateAndSetLocation(scLocationId);
        }

        stateComp.narrateAndSetState(ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS);

        setSchonBegruesstMitSC(true);
        unsetTalkingTo();
    }

    private void froschHatForderungGestellt_Exit() {
        n.narrateAlt(secs(10),
                neuerSatz("„Na, bei dir piept's wohl!“ – Entrüstet wendest du dich ab")
                        .undWartest(),
                neuerSatz("„Wenn ich darüber nachdenke… – nein!“"),
                neuerSatz("„Am Ende willst du noch in meinem Bettchen schlafen! Schäm dich, "
                        + "Frosch!“")
                        .beendet(PARAGRAPH));

        setSchonBegruesstMitSC(true);
        unsetTalkingTo();
    }

    // -------------------------------------------------------------------------------
    // .. AUF_DEM_WEG_ZUM_BRUNNEN_UM_DINGE_HERAUSZUHOLEN
    // -------------------------------------------------------------------------------

    // -------------------------------------------------------------------------------
    // .. ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS
    // -------------------------------------------------------------------------------

    // -------------------------------------------------------------------------------
    // .. ALLGEMEINES
    // -------------------------------------------------------------------------------

    private void ansprechen_froschReagiertNicht() {
        if (!isSchonBegruesstMitSC()) {
            n.narrateAlt(secs(3),
                    neuerSatz("„Hallo, Kollege Frosch!“"),
                    neuerSatz("„Hallo, du hässlicher Frosch!“, redest du ihn an")
                            .undWartest()
                            .dann(), neuerSatz("„Hallo nochmal, Meister Frosch!“"));
            setSchonBegruesstMitSC(true);
        } else {
            n.narrateAlt(secs(3),
                    neuerSatz("„Schön grün seht Ihr heute aus!“, sagst du - etwas "
                            + "Gescheiteres will "
                            + "dir partout nicht einfallen"),
                    neuerSatz(
                            "„Und sonst so?“, fragst du "
                                    + getDescription(true)
                                    + " etwas hilflos")
            );
        }

        froschReagiertNicht();
    }

    private void froschReagiertNicht() {
        n.narrate(neuerSatz("Der Frosch reagiert nicht", secs(3))
                .beendet(PARAGRAPH));

        unsetTalkingTo();
    }

    private void ansprechen_froschErinnertAnVersprechen() {
        n.narrateAlt(secs(15),
                du("sprichst",
                        getDescription(true).akk()
                                + " an: „Wie läuft's, Frosch? Schönes Wetter heut.“ "
                                + "„Vergiss dein Versprechen nicht“, sagt er nur")
                        .beendet(PARAGRAPH),
                du("holst", "Luft, aber da kommt dir "
                        + getDescription().nom()
                        + " schon zuvor: „Wir sehen uns noch!“").beendet(PARAGRAPH),
                neuerSatz("„Und jetzt, Frosch?“ "
                        + " „Du weißt, was du versprochen hast“, gibt er zurück"
                ).beendet(PARAGRAPH));

        setSchonBegruesstMitSC(true);
        unsetTalkingTo();
    }

    private void froschAufTischDraengelt() {
        final Nominalphrase desc = getDescription(true);
        n.narrateAlt(
                du(
                        "hast", "gerade Luft geholt, da schneidet dir "
                                + desc.nom()
                                + " schon das Wort ab. „Was gibt es da noch zu diskutieren?“, quakt "
                                + desc.persPron().nom()
                                + " dich laut an",
                        "gerade",
                        secs(10))
                        .phorikKandidat(desc, FROSCHPRINZ),
                du("druckst", "ein bisschen herum und faselst etwas von "
                                + "hygienischen Gründen. "
                                + capitalize(desc.nom())
                                + " schaut dich nur… traurig? verächtlich?… an",
                        secs(15)).beendet(PARAGRAPH));

        setSchonBegruesstMitSC(true);
        unsetTalkingTo();
    }

    private <LOC_DESC extends ILocatableGO & IDescribableGO>
    ImmutableList<LOC_DESC> getObjectsInDenBrunnenGefallen() {
        // IDEA Es könnten auch Gegenstände unten im Brunnen
        //  sein, von denen der Spieler gar nichts weiß - hier filtern nach Known durch den SC.
        return world.loadDescribableNonLivingMovableRecursiveInventory(UNTEN_IM_BRUNNEN);
    }
}
