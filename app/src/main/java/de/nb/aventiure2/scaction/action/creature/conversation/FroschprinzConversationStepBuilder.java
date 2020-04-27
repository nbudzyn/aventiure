package de.nb.aventiure2.scaction.action.creature.conversation;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.List;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.IHasStoringPlaceGO;
import de.nb.aventiure2.data.world.syscomp.talking.ITalkerGO;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.german.base.DeklinierbarePhrase;
import de.nb.aventiure2.german.base.Indefinitpronomen;
import de.nb.aventiure2.german.base.Nominalphrase;

import static de.nb.aventiure2.data.storystate.StoryState.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.data.storystate.StoryState.StructuralElement.SENTENCE;
import static de.nb.aventiure2.data.storystate.StoryState.StructuralElement.WORD;
import static de.nb.aventiure2.data.storystate.StoryStateBuilder.t;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.IM_WALD_BEIM_BRUNNEN;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.UNTEN_IM_BRUNNEN;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.loadDescribableNonLivingInventory;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.VOLLER_FREUDE;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.AUF_DEM_WEG_ZUM_BRUNNEN_UM_DINGE_HERAUSZUHOLEN;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.HAT_FORDERUNG_GESTELLT;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.HAT_NACH_BELOHNUNG_GEFRAGT;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.secs;
import static de.nb.aventiure2.german.base.DuDescription.du;
import static de.nb.aventiure2.german.base.GermanUtil.capitalize;
import static de.nb.aventiure2.german.base.Indefinitpronomen.ALLES;
import static de.nb.aventiure2.german.base.Nominalphrase.ANGEBOTE;
import static de.nb.aventiure2.german.praedikat.SeinUtil.istSind;
import static de.nb.aventiure2.german.praedikat.VerbSubjDatAkk.MACHEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjDatAkk.VERSPRECHEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.IGNORIEREN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.REDEN;

/**
 * Erzeugt {@link ConversationStep}s für den
 * {@link de.nb.aventiure2.data.world.gameobjects.GameObjects#FROSCHPRINZ}en.
 */
class FroschprinzConversationStepBuilder<LOC_DESC extends ILocatableGO & IDescribableGO,
        F extends IDescribableGO & IHasStateGO & ITalkerGO>
        extends AbstractConversationStepBuilder<F> {
    private final List<LOC_DESC> objectsInDenBrunnenGefallen;

    FroschprinzConversationStepBuilder(final AvDatabase db, final StoryState initialStoryState,
                                       final IHasStoringPlaceGO room,
                                       @NonNull final F froschprinz) {
        super(db, initialStoryState, room, froschprinz);

        objectsInDenBrunnenGefallen = loadDescribableNonLivingInventory(db, UNTEN_IM_BRUNNEN);
    }

    /**
     * Gibt eine Beschreibung dieses Objekts zurück - wenn es nur eines ist - sonst
     * etwas wie "die Dinge".
     */
    private DeklinierbarePhrase getDescriptionSingleOrCollective(
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
    List<ConversationStep> getAllStepsForCurrentState() {
        switch (talker.stateComp().getState()) {
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
                        st(this::etwasIstInDenBrunnenGefallen,
                                MACHEN.mitAkk(ANGEBOTE),
                                this::froschHatNachBelohnungGefragt_AngeboteMachen),
                        exitSt(this::froschHatNachBelohnungGefragt_Exit),
                        immReEntrySt(this::etwasIstInDenBrunnenGefallen,
                                MACHEN.mitAkk(ANGEBOTE),
                                this::froschHatNachBelohnungGefragt_ImmReEntry),
                        immReEntrySt(this::nichtsIstInDenBrunnenGefallen,
                                this::hallo_froschReagiertNicht),
                        reEntrySt(this::etwasIstInDenBrunnenGefallen,
                                MACHEN.mitAkk(ANGEBOTE),
                                this::froschHatNachBelohnungGefragt_ReEntry),
                        reEntrySt(this::nichtsIstInDenBrunnenGefallen,
                                this::hallo_froschReagiertNicht)
                );
            case HAT_FORDERUNG_GESTELLT:
                return ImmutableList.of(
                        st(this::etwasIstInDenBrunnenGefallen,
                                VERSPRECHEN.mitAkk(ALLES),
                                this::froschHatForderungGestellt_AllesVersprechen),
                        exitSt(this::froschHatForderungGestellt_Exit),
                        immReEntrySt(this::etwasIstInDenBrunnenGefallen,
                                VERSPRECHEN.mitAkk(ALLES),
                                this::froschHatForderungGestellt_ImmReEntry),
                        immReEntrySt(this::nichtsIstInDenBrunnenGefallen,
                                this::hallo_froschReagiertNicht),
                        reEntrySt(this::etwasIstInDenBrunnenGefallen,
                                VERSPRECHEN.mitAkk(ALLES),
                                this::froschHatForderungGestellt_reEntry),
                        reEntrySt(this::nichtsIstInDenBrunnenGefallen,
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
                        + talker.stateComp().getState());
        }
    }

    // -------------------------------------------------------------------------------
    // .. HAT_SC_HILFSBEREIT_ANGESPROCHEN
    // -------------------------------------------------------------------------------
    private AvTimeSpan froschHatAngesprochen_antworten() {
        if (nichtsIstInDenBrunnenGefallen()) {
            talker.talkingComp().unsetTalkingTo();

            n.add(t(SENTENCE, "„Ach, du bist's, alter Wasserpatscher“, sagst du")
                    .undWartest()
                    .dann());
            return secs(5);
        }

        AvTimeSpan timeElapsed = noTime();
        timeElapsed = timeElapsed.plus(inDenBrunnenGefallenErklaerung());
        return timeElapsed.plus(herausholenAngebot());
    }

    private AvTimeSpan froschHatAngesprochen_ImmReEntry() {
        if (nichtsIstInDenBrunnenGefallen()) {
            return hallo_froschReagiertNicht();
        }

        if (initialStoryState.allowsAdditionalDuSatzreihengliedOhneSubjekt()
                && initialStoryState.dann()) {
            n.add(t(WORD,
                    "– aber dann gibst du dir einen Ruck:"));
        } else if (initialStoryState.dann()) {
            n.add(t(SENTENCE,
                    "Aber dann gibst du dir einen Ruck:"));

        } else {
            n.add(t(SENTENCE,
                    "Du gibst dir einen Ruck:"));
        }

        return froschHatAngesprochen_ReEntry();
    }

    private AvTimeSpan froschHatAngesprochen_ReEntry() {
        if (nichtsIstInDenBrunnenGefallen()) {
            return hallo_froschReagiertNicht();
        }

        n.add(t(SENTENCE, "„Hallo, du hässlicher Frosch!“, redest du ihn an")
                .undWartest()
                .dann());

        sc.talkingComp().setTalkingTo(talker);

        AvTimeSpan timeElapsed = noTime();
        timeElapsed = timeElapsed.plus(inDenBrunnenGefallenErklaerung());
        return timeElapsed.plus(herausholenAngebot());
    }

    private AvTimeSpan inDenBrunnenGefallenErklaerung() {
        if (sc.memoryComp().getLastAction().is(Action.Type.HEULEN)) {
            final DeklinierbarePhrase objectsDesc =
                    getDescriptionSingleOrCollective(objectsInDenBrunnenGefallen);

            n.add(t(SENTENCE, "„Ich weine über "
                    + objectsDesc.akk() // die goldene Kugel
                    + ", "
                    + objectsDesc.relPron().akk() // die
                    + " mir in den Brunnen hinabgefallen " +
                    istSind(objectsDesc.getNumerusGenus()) +
                    ".“"));
            return secs(10);
        }

        if (objectsInDenBrunnenGefallen.size() == 1) {
            final IDescribableGO objectInDenBrunnenGefallen =
                    objectsInDenBrunnenGefallen.iterator().next();

            n.add(t(SENTENCE, "„"
                    + capitalize(getDescription(objectInDenBrunnenGefallen).nom())
                    + " ist mir in den Brunnen hinabgefallen.“"));
            return secs(10);
        }

        n.add(t(SENTENCE, "„Mir sind Dinge in den Brunnen hinabgefallen.“"));
        return secs(5);
    }

    private AvTimeSpan herausholenAngebot() {
        final String objectsInDenBrunnenGefallenShortAkk =
                getAkkShort(objectsInDenBrunnenGefallen);

        final String ratschlag;
        if (sc.memoryComp().getLastAction().is(Action.Type.HEULEN)) {
            ratschlag = "weine nicht";
        } else {
            ratschlag = "sorge dich nicht";
        }
        n.add(t(PARAGRAPH, "„Sei still und "
                + ratschlag
                + "“, antwortet "
                + getDescription(talker, true).nom()
                + ", „ich kann wohl Rat schaffen, aber was gibst du mir, wenn ich "
                + objectsInDenBrunnenGefallenShortAkk
                + " wieder heraufhole?“")
                .beendet(PARAGRAPH));

        talker.stateComp().setState(HAT_NACH_BELOHNUNG_GEFRAGT);

        return secs(15);
    }

    private AvTimeSpan froschHatAngesprochen_Exit() {
        talker.talkingComp().unsetTalkingTo();

        n.add(t(SENTENCE,
                "Du tust, als hättest du nichts gehört")
                .komma()
                .undWartest()
                .dann());
        return secs(3);
    }

    // -------------------------------------------------------------------------------
    // .. HAT_NACH_BELOHNUNG_GEFRAGT
    // -------------------------------------------------------------------------------

    private AvTimeSpan froschHatNachBelohnungGefragt_AngeboteMachen() {
        sc.talkingComp().setTalkingTo(talker);

        n.add(t(SENTENCE,
                "„Was du haben willst, lieber Frosch“, sagst du, „meine Kleider, "
                        + "Perlen oder Edelsteine?“"));

        n.add(t(PARAGRAPH,
                "Der Frosch antwortet: „Deine Kleider, Perlen oder Edelsteine, die mag "
                        + "ich nicht. "
                        + "Aber wenn ich am Tischlein neben dir sitzen soll, von deinem Tellerlein "
                        + "essen und "
                        + "aus deinem Becherlein trinken: Wenn du mir das versprichst, so will ich "
                        + "hinuntersteigen und dir "
                        // die goldene Kugel / die Dinge
                        + getDescriptionSingleOrCollective(objectsInDenBrunnenGefallen).akk()
                        + " wieder heraufholen.“")
                .beendet(PARAGRAPH));

        talker.stateComp().setState(HAT_FORDERUNG_GESTELLT);
        return secs(20);
    }

    private AvTimeSpan froschHatNachBelohnungGefragt_ImmReEntry() {
        return
                n.add(du("gehst", "kurz in dich…", secs(5)))
                        .plus(
                                froschHatNachBelohnungGefragt_ReEntry());
    }

    private AvTimeSpan froschHatNachBelohnungGefragt_ReEntry() {
        sc.talkingComp().setTalkingTo(talker);

        n.add(t(PARAGRAPH,
                "„Frosch“, sprichst du ihn an, „steht dein Angebot noch?“"));

        n.add(t(PARAGRAPH,
                "„Sicher“, antwortet der Frosch, „ich kann dir alles aus dem Brunnen "
                        + "holen, was hineingefallen ist. Was gibst du mir dafür?“ "
                        + "„Was du haben willst, lieber Frosch“, sagst du, „meine Kleider, "
                        + "Perlen oder Edelsteine?“"));

        n.add(t(PARAGRAPH,
                "Der Frosch antwortet: „Deine Kleider, Perlen oder Edelsteine, die mag "
                        + "ich nicht. "
                        + "Aber wenn ich am Tischlein neben dir sitzen soll, von deinem Tellerlein "
                        + "essen und "
                        + "aus deinem Becherlein trinken: Wenn du mir das versprichst, so will ich "
                        + "hinuntersteigen und dir "
                        // die goldene Kugel / die Dinge
                        + getDescriptionSingleOrCollective(objectsInDenBrunnenGefallen).akk()
                        + " wieder herauf holen.“")
                .beendet(PARAGRAPH));

        talker.stateComp().setState(HAT_FORDERUNG_GESTELLT);

        return secs(30);
    }

    private AvTimeSpan froschHatNachBelohnungGefragt_Exit() {
        talker.talkingComp().unsetTalkingTo();

        n.add(t(SENTENCE,
                "„Denkst du etwa, ich überschütte dich mit Gold und Juwelen? – Vergiss es!“"));
        return secs(5);
    }

    // -------------------------------------------------------------------------------
    // .. HAT_NACH_BELOHNUNG_GEFRAGT
    // -------------------------------------------------------------------------------
    private AvTimeSpan froschHatForderungGestellt_AllesVersprechen() {
        // die goldene Kugel / die Dinge
        n.add(t(PARAGRAPH,
                "„Ach ja“, sagst du, „ich verspreche dir alles, was du "
                        + "willst, wenn du mir nur "
                        + getDescriptionSingleOrCollective(objectsInDenBrunnenGefallen).akk()
                        + " wiederbringst.“ Du denkst "
                        + "aber: „Was der einfältige Frosch schwätzt, der sitzt im Wasser bei "
                        + "seinesgleichen und quakt und kann keines Menschen Geselle sein.“"));

        return secs(20).plus(froschReagiertAufVersprechen());
    }

    private AvTimeSpan froschHatForderungGestellt_ImmReEntry() {
        n.add(t(SENTENCE,
                "Aber im nächsten Moment entschuldigst du dich schon: "
                        + "„Nichts für ungut! Wenn du mir wirklich "
                        + getDescriptionSingleOrCollective(objectsInDenBrunnenGefallen).akk()
                        + " wieder besorgen kannst – ich verspreche dir alles, was du willst!“"
                        + " Bei dir selbst denkst du: "
                        + "„Was der einfältige Frosch schwätzt, der sitzt im Wasser bei "
                        + "seinesgleichen und quakt und kann keines Menschen Geselle sein.“"
        ));

        return secs(20).plus(froschReagiertAufVersprechen());
    }

    private AvTimeSpan froschHatForderungGestellt_reEntry() {
        n.add(t(SENTENCE,
                "„Lieber Frosch“, sagst du, „ich habe es mir überlegt. Ich verspreche dir alles, "
                        + "was du "
                        + "willst, wenn du mir nur "
                        + getDescriptionSingleOrCollective(objectsInDenBrunnenGefallen).akk()
                        + " wiederbringst.“ –"));
        n.add(t(SENTENCE,
                "Was du dir eigentlich überlegt hast, ist:"
                        + " „Was der einfältige Frosch schwätzt, der sitzt im"
                        + " Wasser bei seinesgleichen und quakt und kann keines Menschen "
                        + "Geselle sein.“"));

        return secs(20).plus(froschReagiertAufVersprechen());
    }

    private AvTimeSpan froschReagiertAufVersprechen() {
        n.add(t(PARAGRAPH,
                "Der Frosch, als er die Zusage erhalten hat,"));

        if (!room.is(IM_WALD_BEIM_BRUNNEN)) {
            talker.talkingComp().unsetTalkingTo();

            n.add(t(WORD, "hüpft er sogleich davon")
                    .beendet(PARAGRAPH));

            talker.stateComp().setState(AUF_DEM_WEG_ZUM_BRUNNEN_UM_DINGE_HERAUSZUHOLEN);
            // STORY Der Froschprinz muss eine KI erhalten, dass er
            //  nach einer Weile automatisch beim Brunnen
            //  auftaucht und sich, die Dinge herausholt
            //  und sie dem SC übergibt oder beim Brunnen bereitlegt
            //  (dann sollte er das ankündigen).
            //  Oder wir überspringen diesen Schritt, und der Frosch bewegt
            //  sich nie vom Brunnen fort, solange er nichts herausgeholt hat
            //  (IllegalState).
            return secs(5);
        }

        final DeklinierbarePhrase descObjectsInDenBrunnenGefallen =
                getDescriptionSingleOrCollective(objectsInDenBrunnenGefallen);

        talker.talkingComp().unsetTalkingTo();

        n.add(t(WORD, "taucht seinen Kopf "
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
                + " wieder erblickst"));

        talker.stateComp().setState(ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS);

        sc.feelingsComp().setMood(VOLLER_FREUDE);

        for (final LOC_DESC object : objectsInDenBrunnenGefallen) {
            object.locationComp().setLocation(room);
        }

        return secs(30);
    }

    private AvTimeSpan froschHatForderungGestellt_Exit() {
        talker.talkingComp().unsetTalkingTo();

        n.add(alt(
                t(SENTENCE,
                        "„Na, bei dir piept's wohl!“ – Entrüstet wendest du dich ab")
                        .undWartest(),
                t(SENTENCE,
                        "„Wenn ich darüber nachdenke… – nein!“"),
                t(SENTENCE,
                        "„Am Ende willst du noch in meinem Bettchen schlafen! Schäm dich, "
                                + "Frosch!“")
                        .beendet(PARAGRAPH)));

        return secs(10);
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
        talker.talkingComp().unsetTalkingTo();

        n.add(alt(
                t(PARAGRAPH,
                        "„Du tickst ja wohl nicht richtig! So ein Ekeltier wie du hat auf "
                                + "meiner Tafel nichts "
                                + "verloren!“ Du wendest du dich empört ab")
                        .undWartest(),
                t(PARAGRAPH,
                        "„Ich soll deine schleimigen Patscher auf den Tisch stellen? "
                                + "Dafür musst du dir wen anderes suchen!“")
                        .beendet(PARAGRAPH),
                t(PARAGRAPH,
                        "Dir wird ganz angst, aber du sagst: „Du denkst wohl, was man "
                                + "versprochen hat, das "
                                + "muss man auch halten? Da bist du bei mir an den Falschen "
                                + "geraten!“ Demonstrativ wendest du dich ab")
                        .undWartest()
        ));

        return secs(15);
    }

    // -------------------------------------------------------------------------------
    // .. ALLGEMEINES
    // -------------------------------------------------------------------------------

    private AvTimeSpan hallo_froschReagiertNicht() {
        n.add(alt(
                t(SENTENCE, "„Hallo, Kollege Frosch!“"),
                t(SENTENCE, "„Hallo, du hässlicher Frosch!“, redest du ihn an")
                        .undWartest()
                        .dann(),
                t(SENTENCE, "„Hallo nochmal, Meister Frosch!“")
        ));

        return secs(3).plus(froschReagiertNicht());
    }

    private AvTimeSpan froschReagiertNicht() {
        talker.talkingComp().unsetTalkingTo();

        n.add(t(SENTENCE, "Der Frosch reagiert nicht")
                .beendet(PARAGRAPH));
        return secs(3);
    }

    private AvTimeSpan hallo_froschErinnertAnVersprechen() {
        talker.talkingComp().unsetTalkingTo();

        n.add(alt(
                t(SENTENCE,
                        // TODO "Du hebst die Kugel auf. Du sprichst den Frosch an..."
                        //  Der Narrator sollte automatisch dann, und etc. erkennen und wählen
                        "Du sprichst "
                                + getDescription(talker, true).akk()
                                + " an: „Wie läuft's, Frosch? Schönes Wetter heut.“ "
                                + "„Vergiss dein Versprechen nicht“, sagt er nur."
                ).beendet(PARAGRAPH),
                t(SENTENCE,
                        "Du holst Luft, aber da kommt dir "
                                + getDescription(talker).nom()
                                + " schon zuvor: „Wir sehen uns noch!“"
                ).beendet(PARAGRAPH),
                t(SENTENCE,
                        "„Und jetzt, Frosch?“ "
                                + " „Du weißt, was du versprochen hast“, gibt er zurück."
                )));

        return secs(15);
    }

    private boolean nichtsIstInDenBrunnenGefallen() {
        return !etwasIstInDenBrunnenGefallen();
    }

    private boolean etwasIstInDenBrunnenGefallen() {
        return !objectsInDenBrunnenGefallen.isEmpty();
    }
}
