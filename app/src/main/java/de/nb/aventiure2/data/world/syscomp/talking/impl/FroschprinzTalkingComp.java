package de.nb.aventiure2.data.world.syscomp.talking.impl;

import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import java.util.List;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobjects.GameObjects;
import de.nb.aventiure2.data.world.syscomp.description.DescriptionComp;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.data.world.syscomp.state.StateComp;
import de.nb.aventiure2.data.world.syscomp.talking.AbstractTalkingComp;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.german.base.Indefinitpronomen;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static de.nb.aventiure2.data.world.gameobjects.GameObjects.FROSCHPRINZ;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.IM_WALD_BEIM_BRUNNEN;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.UNTEN_IM_BRUNNEN;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.loadSC;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.VOLLER_FREUDE;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.AUF_DEM_WEG_ZUM_BRUNNEN_UM_DINGE_HERAUSZUHOLEN;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.HAT_FORDERUNG_GESTELLT;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.HAT_NACH_BELOHNUNG_GEFRAGT;
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
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.IGNORIEREN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.REDEN;

/**
 * Component for den {@link GameObjects#FROSCHPRINZ}en: Der Spieler
 * kann mit dem Froschprinzen im Gespräch sein (dann auch umgekehrt).
 * <p>
 * Es gibt {@link SCTalkAction}s, also mögliche Redebeiträge, die der
 * Spieler(-Charakter) an den Froschprinzen richten kann (und auf die der
 * Froschprinz dann jeweils reagiert).
 */
public class FroschprinzTalkingComp extends AbstractTalkingComp {
    private final DescriptionComp descriptionComp;
    private final StateComp stateComp;

    public FroschprinzTalkingComp(final AvDatabase db,
                                  final DescriptionComp descriptionComp,
                                  final StateComp stateComp) {
        super(FROSCHPRINZ, db);
        this.descriptionComp = descriptionComp;
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
                return ImmutableList.of();
            case HAT_HOCHHEBEN_GEFORDERT:
                return ImmutableList.of(
                        // STORY Frosch auf den Tisch hochheben - das ist vermutlich eine AKTION
                        //  die WÄHREND des Gesprächs geht!
                        //  Hochheben:  "dein Herz klopft gewaltig"
                        exitSt(this::froschHatHochhebenGefordert_Exit)
                        // STORY:
                        //  Tisch beim Schlossfest als eigenen Raum modellieren,
                        //  An einen Tisch setzen = Bewegung, location = "auf dem Tisch"
                        //  STORY Frosch nehmen erhält am Tisch eine separate
                        //   Beschreibung (ohne "in die Tasche")
                        //   Ggf. Extra Satz beim verlassen des Tisches MIT FROSCH:
                        //   "du steckst den Frosch in eine Tasche"
                        //  STORY  Frosch absetzen -> Frosch landet auf dem Tisch
                );
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

        final StoryState initialStoryState = db.storyStateDao().getStoryState();

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

        loadSC(db).talkingComp().setTalkingTo(FROSCHPRINZ);

        timeElapsed = timeElapsed.plus(inDenBrunnenGefallenErklaerung());
        return timeElapsed.plus(herausholenAngebot());
    }

    private AvTimeSpan inDenBrunnenGefallenErklaerung() {
        final ImmutableList<? extends IDescribableGO> objectsInDenBrunnenGefallen =
                getObjectsInDenBrunnenGefallen();

        if (loadSC(db).memoryComp().getLastAction().is(Action.Type.HEULEN)) {
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
        if (loadSC(db).memoryComp().getLastAction().is(Action.Type.HEULEN)) {
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
        loadSC(db).talkingComp().setTalkingTo(FROSCHPRINZ);

        final AvTimeSpan timeSpan = n.add(neuerSatz(SENTENCE,
                "„Was du haben willst, lieber Frosch“, sagst du, „meine Kleider, "
                        + "Perlen oder Edelsteine?“", secs(5)));

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
        loadSC(db).talkingComp().setTalkingTo(getGameObjectId());

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

        @Nullable final GameObjectId scLocationId = loadSC(db).locationComp().getLocationId();
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

        loadSC(db).feelingsComp().setMood(VOLLER_FREUDE);

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
    // .. HAT_HOCHHEBEN_GEFORDERT
    // -------------------------------------------------------------------------------

    private AvTimeSpan froschHatHochhebenGefordert_Exit() {
        unsetTalkingTo();

        return n.addAlt(
                neuerSatz(PARAGRAPH,
                        "„Du tickst ja wohl nicht richtig! So ein Ekeltier wie du hat auf "
                                + "meiner Tafel nichts "
                                + "verloren!“ Du wendest du dich empört ab",
                        secs(15))
                        .undWartest(),
                neuerSatz(PARAGRAPH,
                        "„Ich soll deine schleimigen Patscher auf den Tisch stellen? "
                                + "Dafür musst du dir wen anderes suchen!“",
                        secs(15))
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        "Dir wird ganz angst, aber du sagst: „Du denkst wohl, was man "
                                + "versprochen hat, das "
                                + "muss man auch halten? Da bist du bei mir an den Falschen "
                                + "geraten!“ Demonstrativ wendest du dich ab",
                        secs(15))
                        .undWartest());
    }

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

    private <LOC_DESC extends ILocatableGO & IDescribableGO>
    ImmutableList<LOC_DESC> getObjectsInDenBrunnenGefallen() {
        // STORY Es könnten auch andere Gegenstände unten im Brunnen
        //  sein - hier filtern known durch den SC.
        return GameObjects.loadDescribableNonLivingInventory(db, UNTEN_IM_BRUNNEN);
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
        return descriptionComp.getDescription(
                loadSC(db).memoryComp().isKnown(FROSCHPRINZ), shortIfKnown);
    }
}
