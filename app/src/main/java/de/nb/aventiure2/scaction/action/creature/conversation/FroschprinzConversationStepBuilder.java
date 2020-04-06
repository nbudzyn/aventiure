package de.nb.aventiure2.scaction.action.creature.conversation;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Map;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.IPlayerAction;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.world.entity.creature.CreatureData;
import de.nb.aventiure2.data.world.entity.object.AvObject;
import de.nb.aventiure2.data.world.entity.object.ObjectData;
import de.nb.aventiure2.data.world.room.AvRoom;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.german.base.DeklinierbarePhrase;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.scaction.action.HeulenAction;

import static de.nb.aventiure2.data.storystate.StoryState.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.data.storystate.StoryState.StructuralElement.SENTENCE;
import static de.nb.aventiure2.data.storystate.StoryState.StructuralElement.WORD;
import static de.nb.aventiure2.data.world.entity.creature.Creature.Key.FROSCHPRINZ;
import static de.nb.aventiure2.data.world.entity.creature.CreatureState.AUF_DEM_WEG_ZUM_BRUNNEN_UM_DINGE_HERAUSZUHOLEN;
import static de.nb.aventiure2.data.world.entity.creature.CreatureState.ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS;
import static de.nb.aventiure2.data.world.entity.creature.CreatureState.HAT_FORDERUNG_GESTELLT;
import static de.nb.aventiure2.data.world.entity.creature.CreatureState.HAT_NACH_BELOHNUNG_GEFRAGT;
import static de.nb.aventiure2.data.world.entity.object.ObjectData.filterInDenBrunnenGefallen;
import static de.nb.aventiure2.data.world.entity.object.ObjectData.getDescriptionSingleOrCollective;
import static de.nb.aventiure2.data.world.player.stats.ScStateOfMind.VOLLER_FREUDE;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.secs;
import static de.nb.aventiure2.german.base.GermanUtil.capitalize;
import static de.nb.aventiure2.german.base.Indefinitpronomen.ALLES;
import static de.nb.aventiure2.german.base.Nominalphrase.ANGEBOTE;
import static de.nb.aventiure2.german.praedikat.SeinUtil.istSind;
import static de.nb.aventiure2.german.praedikat.VerbSubjDatAkk.MACHEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjDatAkk.VERSPRECHEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.IGNORIEREN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.REDEN;

/**
 * Erzeugt {@link CreatureConversationStep}s für den
 * {@link de.nb.aventiure2.data.world.entity.creature.Creature.Key#FROSCHPRINZ}en.
 */
class FroschprinzConversationStepBuilder extends AbstractCreatureConversationStepBuilder {
    private final List<ObjectData> objectsInDenBrunnenGefallen;

    FroschprinzConversationStepBuilder(final AvDatabase db, final StoryState initialStoryState,
                                       final Class<? extends IPlayerAction> currentActionClass,
                                       final AvRoom room,
                                       final Map<AvObject.Key, ObjectData> allObjectsByKey,
                                       @NonNull final CreatureData creatureData) {
        super(db, initialStoryState, currentActionClass, room, allObjectsByKey, creatureData);

        objectsInDenBrunnenGefallen = filterInDenBrunnenGefallen(allObjectsByKey);
    }

    @Override
    List<CreatureConversationStep> getAllStepsForCurrentState() {
        switch (creatureData.getState()) {
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
            case ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS_VON_SC_GETRAGEN:
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
                        + creatureData.getState());
        }
    }

    // -------------------------------------------------------------------------------
    // .. HAT_SC_HILFSBEREIT_ANGESPROCHEN
    // -------------------------------------------------------------------------------
    private AvTimeSpan froschHatAngesprochen_antworten() {
        if (nichtsIstInDenBrunnenGefallen()) {
            n.add(t(SENTENCE, "„Ach, du bist's, alter Wasserpatscher“, sagst du")
                    .undWartest()
                    .dann()
                    .imGespraechMit(null));
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


        AvTimeSpan timeElapsed = noTime();
        timeElapsed = timeElapsed.plus(inDenBrunnenGefallenErklaerung());
        return timeElapsed.plus(herausholenAngebot());
    }

    private AvTimeSpan inDenBrunnenGefallenErklaerung() {
        if (initialStoryState.lastActionWas(HeulenAction.class)) {
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
            final ObjectData objectInDenBrunnenGefallen =
                    objectsInDenBrunnenGefallen.iterator().next();

            n.add(t(SENTENCE, "„"
                    + capitalize(objectInDenBrunnenGefallen.nom())
                    + " ist mir in den Brunnen hinabgefallen.“"));
            return secs(10);
        }

        n.add(t(SENTENCE, "„Mir sind Dinge in den Brunnen hinabgefallen.“"));
        return secs(5);
    }

    private AvTimeSpan herausholenAngebot() {
        final String objectsInDenBrunnenGefallenShortAkk =
                ObjectData.getAkkShort(objectsInDenBrunnenGefallen);

        final String ratschlag;
        if (initialStoryState.lastActionWas(HeulenAction.class)) {
            ratschlag = "weine nicht";
        } else {
            ratschlag = "sorge dich nicht";
        }
        n.add(t(PARAGRAPH, "„Sei still und "
                + ratschlag
                + "“, antwortet "
                + creatureData.nom(true)
                + ", „ich kann wohl Rat schaffen, aber was gibst du mir, wenn ich "
                + objectsInDenBrunnenGefallenShortAkk
                + " wieder heraufhole?“")
                .beendet(PARAGRAPH));

        db.creatureDataDao().setState(FROSCHPRINZ, HAT_NACH_BELOHNUNG_GEFRAGT);

        return secs(15);
    }

    private AvTimeSpan froschHatAngesprochen_Exit() {
        n.add(t(SENTENCE,
                "Du tust, als hättest du nichts gehört")
                .komma()
                .undWartest()
                .dann()
                .imGespraechMit(null));
        return secs(3);
    }

    // -------------------------------------------------------------------------------
    // .. HAT_NACH_BELOHNUNG_GEFRAGT
    // -------------------------------------------------------------------------------

    private AvTimeSpan froschHatNachBelohnungGefragt_AngeboteMachen() {
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

        db.creatureDataDao().setState(FROSCHPRINZ, HAT_FORDERUNG_GESTELLT);
        return secs(20);
    }

    private AvTimeSpan froschHatNachBelohnungGefragt_ImmReEntry() {
        n.add(t(PARAGRAPH, "Dann gehst du kurz in dich…"));

        return froschHatNachBelohnungGefragt_ReEntry();
    }

    private AvTimeSpan froschHatNachBelohnungGefragt_ReEntry() {
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

        db.creatureDataDao().setState(FROSCHPRINZ, HAT_FORDERUNG_GESTELLT);

        return secs(30);
    }

    private AvTimeSpan froschHatNachBelohnungGefragt_Exit() {
        n.add(t(SENTENCE,
                "„Denkst du etwa, ich überschütte dich mit Gold und Juwelen? – Vergiss es!“")
                .imGespraechMit(null));
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
                "Aber im nächsten Moment entschuldigst du schon: "
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

        if (room != AvRoom.IM_WALD_BEIM_BRUNNEN) {
            n.add(t(WORD, "hüpft er sogleich davon")
                    .imGespraechMit(null)
                    .beendet(PARAGRAPH));

            db.creatureDataDao().setState(FROSCHPRINZ,
                    AUF_DEM_WEG_ZUM_BRUNNEN_UM_DINGE_HERAUSZUHOLEN);
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
                + " wieder erblickst")
                .imGespraechMit(null));

        db.creatureDataDao().setState(FROSCHPRINZ, ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS);

        db.playerStatsDao().setStateOfMind(VOLLER_FREUDE);

        for (final ObjectData objectData : objectsInDenBrunnenGefallen) {
            db.objectDataDao().setDemSCInDenBrunnenGefallen(
                    objectData.getObject(), false);
            db.objectDataDao().setRoom(objectData.getObject(), room);
        }

        return secs(30);
    }

    private AvTimeSpan froschHatForderungGestellt_Exit() {
        n.add(alt(
                t(SENTENCE,
                        "„Na, bei dir piept's wohl!“ – Entrüstet wendest du dich ab")
                        .undWartest()
                        .imGespraechMit(null),
                t(SENTENCE,
                        "„Wenn ich darüber nachdenke… – nein!“")
                        .imGespraechMit(null),
                t(SENTENCE,
                        "„Am Ende willst du noch in meinem Bettchen schlafen! Schäm dich, "
                                + "Frosch!“")
                        .imGespraechMit(null)
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
        n.add(alt(
                t(PARAGRAPH,
                        "„Du tickst ja wohl nicht richtig! So ein Ekeltier wie du hat auf "
                                + "meiner Tafel nichts "
                                + "verloren!“ Du wendest du dich empört ab")
                        .undWartest()
                        .imGespraechMit(null),
                t(PARAGRAPH,
                        "„Ich soll deine schleimigen Patscher auf den Tisch stellen? "
                                + "Dafür musst du dir wen anderes suchen!“")
                        .imGespraechMit(null),
                t(PARAGRAPH,
                        "Dir wird ganz angst, aber du sagst: „Du denkst wohl, was man "
                                + "versprochen hat, das "
                                + "muss man auch halten? Da bist du bei mir an den Falschen "
                                + "geraten!“ Demonstrativ wendest du dich ab")
                        .imGespraechMit(null)
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
        n.add(t(SENTENCE, "Der Frosch reagiert nicht")
                .imGespraechMit(null)
                .beendet(PARAGRAPH));
        return secs(3);
    }

    private AvTimeSpan hallo_froschErinnertAnVersprechen() {
        final Nominalphrase froschDesc = creatureData.getDescription(true);
        n.add(alt(
                t(SENTENCE,
                        "Du sprichst "
                                + creatureData.akk(true)
                                + " an: „Wie läuft's, Frosch? Schönes Wetter heut.“ "
                                + "„Vergiss dein Versprechen nicht“, sagt er nur."
                ).imGespraechMit(null)
                        .beendet(PARAGRAPH),
                t(SENTENCE,
                        "Du holst Luft, aber da kommt dir "
                                + creatureData.nom()
                                + " schon zuvor: „Wir sehen uns noch!“"
                ).imGespraechMit(null)
                        .beendet(PARAGRAPH),
                t(SENTENCE,
                        "„Und jetzt, Frosch?“ "
                                + " „Du weißt, was du versprochen hast“, gibt "
                                + froschDesc.persPron().nom(false)
                                + " zurück."
                ).imGespraechMit(null)
        ));

        return secs(15);
    }

    private boolean nichtsIstInDenBrunnenGefallen() {
        return !etwasIstInDenBrunnenGefallen();
    }

    private boolean etwasIstInDenBrunnenGefallen() {
        return !objectsInDenBrunnenGefallen.isEmpty();
    }
}
