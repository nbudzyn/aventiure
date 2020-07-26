package de.nb.aventiure2.data.world.syscomp.talking.impl;

import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import java.util.List;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzStateComp;
import de.nb.aventiure2.data.world.syscomp.talking.AbstractTalkingComp;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.german.base.Indefinitpronomen;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static de.nb.aventiure2.data.world.gameobject.World.FROSCHPRINZ;
import static de.nb.aventiure2.data.world.gameobject.World.IM_WALD_BEIM_BRUNNEN;
import static de.nb.aventiure2.data.world.gameobject.World.UNTEN_IM_BRUNNEN;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.VOLLER_FREUDE;
import static de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState.AUF_DEM_WEG_ZUM_BRUNNEN_UM_DINGE_HERAUSZUHOLEN;
import static de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState.ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS;
import static de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState.HAT_FORDERUNG_GESTELLT;
import static de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState.HAT_NACH_BELOHNUNG_GEFRAGT;
import static de.nb.aventiure2.data.world.syscomp.talking.impl.SCTalkAction.entrySt;
import static de.nb.aventiure2.data.world.syscomp.talking.impl.SCTalkAction.exitSt;
import static de.nb.aventiure2.data.world.syscomp.talking.impl.SCTalkAction.immReEntrySt;
import static de.nb.aventiure2.data.world.syscomp.talking.impl.SCTalkAction.reEntrySt;
import static de.nb.aventiure2.data.world.syscomp.talking.impl.SCTalkAction.st;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.secs;
import static de.nb.aventiure2.german.base.AllgDescription.neuerSatz;
import static de.nb.aventiure2.german.base.AllgDescription.satzanschluss;
import static de.nb.aventiure2.german.base.DuDescription.du;
import static de.nb.aventiure2.german.base.GermanUtil.capitalize;
import static de.nb.aventiure2.german.base.Indefinitpronomen.ALLES;
import static de.nb.aventiure2.german.base.Nominalphrase.ANGEBOTE;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
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
                                  final World world,
                                  final FroschprinzStateComp stateComp) {
        super(FROSCHPRINZ, db, world);
        this.stateComp = stateComp;
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

            return getDescription(objectInDenBrunnenGefallen, false);
        }

        return Nominalphrase.DINGE;
    }

    private String getAkkShort(final List<? extends IDescribableGO> objects) {
        if (objects.size() == 1) {
            return getDescription(objects.iterator().next(), true).akk();
        }

        return "sie";
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
                                this::hallo_froschReagiertNicht),
                        reEntrySt(() -> !objectsInDenBrunnenGefallen.isEmpty(),
                                MACHEN.mitAkk(ANGEBOTE),
                                this::froschHatNachBelohnungGefragt_ReEntry),
                        reEntrySt(objectsInDenBrunnenGefallen::isEmpty,
                                this::hallo_froschReagiertNicht)
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
                                this::hallo_froschReagiertNicht),
                        reEntrySt(() -> !objectsInDenBrunnenGefallen.isEmpty(),
                                VERSPRECHEN.mitAkk(ALLES),
                                this::froschHatForderungGestellt_reEntry),
                        reEntrySt(objectsInDenBrunnenGefallen::isEmpty,
                                this::hallo_froschReagiertNicht)
                );
            case AUF_DEM_WEG_ZUM_BRUNNEN_UM_DINGE_HERAUSZUHOLEN:
                return ImmutableList.of(
                        entrySt(
                                // STORY "Vertraue mir, bald hast du ...die goldene Kugel...
                                //  wieder. Aber vergiss dein Versprechen nicht!"
                                //  Bis dahin:
                                this::hallo_froschErinnertAnVersprechen)
                );
            case ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS:
                return ImmutableList.of(
                        entrySt(this::hallo_froschErinnertAnVersprechen)
                );
            case WARTET_AUF_SC_BEIM_SCHLOSSFEST:
            case AUF_DEM_WEG_ZUM_SCHLOSSFEST:
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

    // -------------------------------------------------------------------------------
    // .. HAT_SC_HILFSBEREIT_ANGESPROCHEN
    // -------------------------------------------------------------------------------
    private AvTimeSpan froschHatAngesprochen_antworten() {
        final ImmutableList<? extends IDescribableGO> objectsInDenBrunnenGefallen =
                getObjectsInDenBrunnenGefallen();

        if (objectsInDenBrunnenGefallen.isEmpty()) {
            unsetTalkingTo();

            return n.add(neuerSatz("„Ach, du bist's, alter Wasserpatscher“, sagst du",
                    secs(5))
                    .undWartest()
                    .dann());
        }

        AvTimeSpan timeElapsed = noTime();
        timeElapsed = timeElapsed.plus(inDenBrunnenGefallenErklaerung());
        return timeElapsed.plus(herausholenAngebot());
    }

    private AvTimeSpan froschHatAngesprochen_ImmReEntry() {
        final ImmutableList<? extends IDescribableGO> objectsInDenBrunnenGefallen =
                getObjectsInDenBrunnenGefallen();

        if (objectsInDenBrunnenGefallen.isEmpty()) {
            return hallo_froschReagiertNicht();
        }

        final StoryState initialStoryState = db.storyStateDao().requireStoryState();

        if (initialStoryState.dann()) {
            if (initialStoryState.allowsAdditionalDuSatzreihengliedOhneSubjekt()) {
                n.add(satzanschluss("– aber dann gibst du dir einen Ruck:",
                        noTime()));
            } else {
                n.add(neuerSatz("Aber dann gibst du dir einen Ruck:", noTime()));
            }
        } else {
            n.add(du("gibst", "dir einen Ruck:", noTime()));
        }

        return froschHatAngesprochen_ReEntry();
    }

    private AvTimeSpan froschHatAngesprochen_ReEntry() {
        final ImmutableList<? extends IDescribableGO> objectsInDenBrunnenGefallen =
                getObjectsInDenBrunnenGefallen();

        if (objectsInDenBrunnenGefallen.isEmpty()) {
            return hallo_froschReagiertNicht();
        }

        AvTimeSpan timeElapsed = n.add(
                neuerSatz("„Hallo, du hässlicher Frosch!“, redest du ihn an", noTime())
                        .undWartest()
                        .dann());

        world.loadSC().talkingComp().setTalkingTo(FROSCHPRINZ);

        timeElapsed = timeElapsed.plus(inDenBrunnenGefallenErklaerung());
        return timeElapsed.plus(herausholenAngebot());
    }

    private AvTimeSpan inDenBrunnenGefallenErklaerung() {
        final ImmutableList<? extends IDescribableGO> objectsInDenBrunnenGefallen =
                getObjectsInDenBrunnenGefallen();

        if (world.loadSC().memoryComp().getLastAction().is(Action.Type.HEULEN)) {
            final SubstantivischePhrase objectsDesc =
                    getDescriptionSingleOrCollective(objectsInDenBrunnenGefallen);

            return n.add(
                    neuerSatz("„Ich weine über "
                            + objectsDesc.akk() // die goldene Kugel
                            + ", "
                            + objectsDesc.relPron().akk() // die
                            + " mir in den Brunnen hinabgefallen " +
                            istSind(objectsDesc.getNumerusGenus()) +
                            ".“", secs(10)));
        }

        if (objectsInDenBrunnenGefallen.size() == 1) {
            final IDescribableGO objectInDenBrunnenGefallen =
                    objectsInDenBrunnenGefallen.iterator().next();

            return n.add(neuerSatz("„"
                    + capitalize(getDescription(objectInDenBrunnenGefallen).nom())
                    + " ist mir in den Brunnen hinabgefallen.“", secs(10)));
        }

        return n.add(neuerSatz("„Mir sind Dinge in den Brunnen hinabgefallen.“", secs(5)));
    }

    private AvTimeSpan herausholenAngebot() {
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

        stateComp.setState(HAT_NACH_BELOHNUNG_GEFRAGT);

        return n.add(neuerSatz(PARAGRAPH, "„Sei still und "
                        + ratschlag
                        + "“, antwortet "
                        + getFroschprinzDescription(true).nom()
                        + ", „ich kann wohl Rat schaffen, aber was gibst du mir, wenn ich "
                        + objectsInDenBrunnenGefallenShortAkk
                        + " wieder heraufhole?“",
                secs(15))
                .beendet(PARAGRAPH));
    }

    private AvTimeSpan froschHatAngesprochen_Exit() {
        unsetTalkingTo();

        return n.add(du(SENTENCE, "tust", ", als hättest du nichts gehört",
                secs(3))
                .komma()
                .undWartest()
                .dann());
    }

    // -------------------------------------------------------------------------------
    // .. HAT_NACH_BELOHNUNG_GEFRAGT
    // -------------------------------------------------------------------------------

    private AvTimeSpan froschHatNachBelohnungGefragt_AngeboteMachen() {
        world.loadSC().talkingComp().setTalkingTo(FROSCHPRINZ);

        final AvTimeSpan timeSpan = n.add(neuerSatz(
                "„Was du haben willst, lieber Frosch“, sagst du, „meine Kleider, "
                        + "Perlen oder Edelsteine?“", secs(5)));

        stateComp.setState(HAT_FORDERUNG_GESTELLT);

        final ImmutableList<? extends IDescribableGO> objectsInDenBrunnenGefallen =
                getObjectsInDenBrunnenGefallen();

        return timeSpan.plus(n.add(neuerSatz(PARAGRAPH,
                "Der Frosch antwortet: „Deine Kleider, Perlen oder Edelsteine, die mag "
                        + "ich nicht. "
                        + "Aber wenn ich am Tischlein bei dir sitzen soll und von deinem Tellerlein "
                        + "essen: Wenn du mir das versprichst, so will ich "
                        + "hinuntersteigen und dir "
                        // die goldene Kugel / die Dinge
                        + getDescriptionSingleOrCollective(objectsInDenBrunnenGefallen).akk()
                        + " wieder heraufholen.“", secs(15))
                .beendet(PARAGRAPH)));
    }

    private AvTimeSpan froschHatNachBelohnungGefragt_ImmReEntry() {
        return
                n.add(du(SENTENCE, "gehst", "kurz in dich…", secs(5)))
                        .plus(
                                froschHatNachBelohnungGefragt_ReEntry());
    }

    private AvTimeSpan froschHatNachBelohnungGefragt_ReEntry() {
        world.loadSC().talkingComp().setTalkingTo(getGameObjectId());

        AvTimeSpan timeSpan = n.add(
                neuerSatz(PARAGRAPH, "„Frosch“, sprichst du ihn an, „steht dein Angebot noch?“",
                        secs(5)));

        timeSpan = timeSpan.plus(n.add(
                neuerSatz(PARAGRAPH,
                        "„Sicher“, antwortet der Frosch, „ich kann dir alles aus dem Brunnen "
                                + "holen, was hineingefallen ist. Was gibst du mir dafür?“ "
                                + "„Was du haben willst, lieber Frosch“, sagst du, „meine Kleider, "
                                + "Perlen oder Edelsteine?“",
                        secs(10))));


        stateComp.setState(HAT_FORDERUNG_GESTELLT);

        final ImmutableList<? extends IDescribableGO> objectsInDenBrunnenGefallen =
                getObjectsInDenBrunnenGefallen();

        return timeSpan.plus(n.add(neuerSatz(PARAGRAPH,
                "Der Frosch antwortet: „Deine Kleider, Perlen oder Edelsteine, die mag "
                        + "ich nicht. "
                        + "Aber wenn ich am Tischlein neben dir sitzen soll, von deinem Tellerlein "
                        + "essen und "
                        + "aus deinem Becherlein trinken: Wenn du mir das versprichst, so will ich "
                        + "hinuntersteigen und dir "
                        // die goldene Kugel / die Dinge
                        + getDescriptionSingleOrCollective(objectsInDenBrunnenGefallen).akk()
                        + " wieder herauf holen.“",
                secs(15))
                .beendet(PARAGRAPH)));
    }

    private AvTimeSpan froschHatNachBelohnungGefragt_Exit() {
        unsetTalkingTo();

        return n.add(
                neuerSatz(
                        "„Denkst du etwa, ich überschütte dich mit Gold und Juwelen? – Vergiss es!“",
                        secs(5)));
    }

    // -------------------------------------------------------------------------------
    // .. HAT_NACH_BELOHNUNG_GEFRAGT
    // -------------------------------------------------------------------------------
    private AvTimeSpan froschHatForderungGestellt_AllesVersprechen() {
        final ImmutableList<? extends IDescribableGO> objectsInDenBrunnenGefallen =
                getObjectsInDenBrunnenGefallen();

        // die goldene Kugel / die Dinge
        final AvTimeSpan timeSpan = n.add(
                neuerSatz(PARAGRAPH, "„Ach ja“, sagst du, „ich verspreche dir alles, was du "
                                + "willst, wenn du mir nur "
                                + getDescriptionSingleOrCollective(objectsInDenBrunnenGefallen).akk()
                                + " wiederbringst.“ Du denkst "
                                + "aber: „Was der einfältige Frosch schwätzt, der sitzt im Wasser bei "
                                + "seinesgleichen und quakt und kann keines Menschen Geselle sein.“",
                        secs(20)));

        return timeSpan.plus(froschReagiertAufVersprechen());
    }

    private AvTimeSpan froschHatForderungGestellt_ImmReEntry() {
        final ImmutableList<? extends IDescribableGO> objectsInDenBrunnenGefallen =
                getObjectsInDenBrunnenGefallen();

        final AvTimeSpan timeSpan = n.add(neuerSatz(
                "Aber im nächsten Moment entschuldigst du dich schon: "
                        + "„Nichts für ungut! Wenn du mir wirklich "
                        + getDescriptionSingleOrCollective(objectsInDenBrunnenGefallen).akk()
                        + " wieder besorgen kannst – ich verspreche dir alles, was du willst!“"
                        + " Bei dir selbst denkst du: "
                        + "„Was der einfältige Frosch schwätzt, der sitzt im Wasser bei "
                        + "seinesgleichen und quakt und kann keines Menschen Geselle sein.“",
                secs(20)));

        return timeSpan.plus(froschReagiertAufVersprechen());
    }

    private AvTimeSpan froschHatForderungGestellt_reEntry() {
        final ImmutableList<? extends IDescribableGO> objectsInDenBrunnenGefallen =
                getObjectsInDenBrunnenGefallen();

        final AvTimeSpan timeSpan = n.add(neuerSatz(
                "„Lieber Frosch“, sagst du, „ich habe es mir überlegt. Ich verspreche dir alles, "
                        + "was du "
                        + "willst, wenn du mir nur "
                        + getDescriptionSingleOrCollective(objectsInDenBrunnenGefallen).akk()
                        + " wiederbringst.“ – Was du dir eigentlich überlegt hast, ist:"
                        + " „Was der einfältige Frosch schwätzt, der sitzt im"
                        + " Wasser bei seinesgleichen und quakt und kann keines Menschen "
                        + "Geselle sein.“", secs(20)));

        return timeSpan.plus(froschReagiertAufVersprechen());
    }

    private <LOC_DESC extends ILocatableGO & IDescribableGO> AvTimeSpan froschReagiertAufVersprechen() {
        n.add(neuerSatz(PARAGRAPH,
                "Der Frosch, als er die Zusage erhalten hat,",
                noTime()));

        @Nullable final GameObjectId scLocationId = world.loadSC().locationComp().getLocationId();
        if (!IM_WALD_BEIM_BRUNNEN.equals(scLocationId)) {
            unsetTalkingTo();

            stateComp.setState(AUF_DEM_WEG_ZUM_BRUNNEN_UM_DINGE_HERAUSZUHOLEN);
            // STORY Der Froschprinz muss eine KI erhalten, dass er
            //  nach einer Weile automatisch beim Brunnen
            //  auftaucht und sich, die Dinge herausholt
            //  und sie dem SC übergibt oder beim Brunnen bereitlegt
            //  (dann sollte er das ankündigen).
            //  Oder wir überspringen diesen Schritt, und der Frosch bewegt
            //  sich nie vom Brunnen fort, solange er nichts herausgeholt hat
            //  (IllegalState).

            return n.add(satzanschluss("hüpft er sogleich davon", secs(5))
                    .beendet(PARAGRAPH));
        }

        final ImmutableList<LOC_DESC> objectsInDenBrunnenGefallen =
                getObjectsInDenBrunnenGefallen();

        final SubstantivischePhrase descObjectsInDenBrunnenGefallen =
                getDescriptionSingleOrCollective(objectsInDenBrunnenGefallen);

        unsetTalkingTo();

        stateComp.setState(ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS);

        world.loadSC().feelingsComp().setMoodMin(VOLLER_FREUDE);

        AvTimeSpan timeElapsed = n.add(satzanschluss("taucht seinen Kopf "
                        + "unter, sinkt hinab und über ein Weilchen kommt er wieder herauf gerudert, "
                        + "hat "
                        // die goldene Kugel / die Dinge
                        + descObjectsInDenBrunnenGefallen.akk()
                        + " im Maul und wirft "
                        + descObjectsInDenBrunnenGefallen.persPron().akk()
                        + " ins Gras. Du "
                        + "bist voll Freude, als du "
                        // die goldene Kugel / die Dinge / TODO Synonym: "die schönes Spielzeug"
                        // Idee zu Synonymen: Synonyme sollte erst NACH dem Originalbegriff auftauchen
                        // und automatisch gewählt werden, wenn syn() oder Ähnliches
                        // programmiert wird.
                        + descObjectsInDenBrunnenGefallen.akk()
                        + " wieder erblickst",
                secs(30)));

        for (final LOC_DESC object : objectsInDenBrunnenGefallen) {
            timeElapsed = timeElapsed.plus(object.locationComp()
                    .narrateAndSetLocation(scLocationId));
        }

        return timeElapsed;
    }

    private AvTimeSpan froschHatForderungGestellt_Exit() {
        unsetTalkingTo();

        return n.addAlt(
                neuerSatz("„Na, bei dir piept's wohl!“ – Entrüstet wendest du dich ab",
                        secs(10))
                        .undWartest(),
                neuerSatz("„Wenn ich darüber nachdenke… – nein!“",
                        secs(10)),
                neuerSatz("„Am Ende willst du noch in meinem Bettchen schlafen! Schäm dich, "
                                + "Frosch!“",
                        secs(10))
                        .beendet(PARAGRAPH));
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

    private AvTimeSpan hallo_froschReagiertNicht() {
        final AvTimeSpan timeSpan = n.addAlt(
                neuerSatz("„Hallo, Kollege Frosch!“", secs(3)),
                neuerSatz("„Hallo, du hässlicher Frosch!“, redest du ihn an", secs(3))
                        .undWartest()
                        .dann(),
                neuerSatz("„Hallo nochmal, Meister Frosch!“", secs(3)));

        return timeSpan.plus(froschReagiertNicht());
    }

    private AvTimeSpan froschReagiertNicht() {
        unsetTalkingTo();

        return n.add(neuerSatz("Der Frosch reagiert nicht",
                secs(3))
                .beendet(PARAGRAPH));
    }

    private AvTimeSpan hallo_froschErinnertAnVersprechen() {
        unsetTalkingTo();

        return n.addAlt(
                du(
                        "sprichst",
                        getFroschprinzDescription(true).akk()
                                + " an: „Wie läuft's, Frosch? Schönes Wetter heut.“ "
                                + "„Vergiss dein Versprechen nicht“, sagt er nur",
                        secs(15)).beendet(PARAGRAPH),
                du("holst", "Luft, aber da kommt dir "
                                + getFroschprinzDescription().nom()
                                + " schon zuvor: „Wir sehen uns noch!“",
                        secs(15)).beendet(PARAGRAPH),
                neuerSatz("„Und jetzt, Frosch?“ "
                                + " „Du weißt, was du versprochen hast“, gibt er zurück",
                        secs(15)));
    }

    private AvTimeSpan froschAufTischDraengelt() {
        unsetTalkingTo();

        final Nominalphrase froschprinzDesc = getFroschprinzDescription(true);
        return n.addAlt(
                du(
                        "hast", "gerade Luft geholt, da schneidet dir "
                                + froschprinzDesc.nom()
                                + " schon das Wort ab. „Was gibt es da noch zu diskutieren?“, quakt "
                                + froschprinzDesc.persPron().nom()
                                + " dich laut an",
                        "gerade",
                        secs(10))
                        .phorikKandidat(froschprinzDesc, FROSCHPRINZ),
                du("druckst", "ein bisschen herum und faselst etwas von "
                                + "hygienischen Gründen. "
                                + capitalize(froschprinzDesc.nom())
                                + " schaut dich nur… traurig? verächtlich?… an",
                        secs(15)).beendet(PARAGRAPH));
    }


    private <LOC_DESC extends ILocatableGO & IDescribableGO>
    ImmutableList<LOC_DESC> getObjectsInDenBrunnenGefallen() {
        // STORY Es könnten auch Gegenstände unten im Brunnen
        //  sein, von denen der Spieler gar nichts weiß - hier filtern nach Known durch den SC.
        return world.loadDescribableNonLivingMovableRecursiveInventory(UNTEN_IM_BRUNNEN);
    }

    /**
     * Gibt eine Nominalphrase zurück, die den Froschprinzen beschreibt.
     * Die Phrase kann unterschiedlich sein, je nachdem, ob
     * ob der Spieler den Froschprinzen schon kennt oder nicht.
     */
    private Nominalphrase getFroschprinzDescription() {
        return getFroschprinzDescription(false);
    }

    /**
     * Gibt eine Nominalphrase zurück, die den Froschprinzen beschreibt.
     * Die Phrase kann unterschiedlich sein, je nachdem, ob
     * ob der Spieler den Froschprinzen schon kennt oder nicht.
     *
     * @param shortIfKnown <i>Falls der Spieler(-charakter)</i> den
     *                     Froschprinzen schon kennt, wird eher eine
     *                     kürzere Beschreibung gewählt
     */
    private Nominalphrase getFroschprinzDescription(final boolean shortIfKnown) {
        return world.getDescription(FROSCHPRINZ, shortIfKnown);
    }
}
