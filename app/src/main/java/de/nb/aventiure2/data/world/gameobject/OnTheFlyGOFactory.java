package de.nb.aventiure2.data.world.gameobject;

import static java.util.Objects.requireNonNull;
import static de.nb.aventiure2.data.time.AvTimeSpan.hours;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.AUSGERUPFT;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.LANG;
import static de.nb.aventiure2.german.base.ArtikelwortFlexionsspalte.Typ.EINIGE;
import static de.nb.aventiure2.german.base.ArtikelwortFlexionsspalte.Typ.INDEF;
import static de.nb.aventiure2.german.base.ArtikelwortFlexionsspalte.Typ.NEG_INDEF;
import static de.nb.aventiure2.german.base.ArtikelwortFlexionsspalte.Typ.VIEL_INDEF;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.BINSEN;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.BINSENSEIL;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.BINSENSEILE;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.NumerusGenus.PL_MFN;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.AUS;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.alt;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.counter.CounterDao;
import de.nb.aventiure2.data.world.syscomp.amount.AmountComp;
import de.nb.aventiure2.data.world.syscomp.amount.IAmountableGO;
import de.nb.aventiure2.data.world.syscomp.description.AbstractDescriptionComp;
import de.nb.aventiure2.data.world.syscomp.description.AmountDescriptionComp;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.transform.GOTransformation;
import de.nb.aventiure2.data.world.syscomp.transform.GOTransformationResult;
import de.nb.aventiure2.data.world.syscomp.typed.GameObjectType;
import de.nb.aventiure2.data.world.syscomp.typed.ITypedGO;
import de.nb.aventiure2.data.world.syscomp.typed.TypeComp;
import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;
import de.nb.aventiure2.german.description.AltDescriptionsBuilder;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.VerbSubjObj;

/**
 * Eine Factory für GameObjects, die on-the-fly erzeugt werden.
 */
public class OnTheFlyGOFactory extends AbstractGameObjectFactory {
    private enum Counter {
        NUM_ON_THE_FLY_GAME_OBJECTS
    }

    private final CounterDao counterDao;

    OnTheFlyGOFactory(final AvDatabase db,
                      final TimeTaker timeTaker,
                      final World world) {
        super(db, timeTaker, world);

        counterDao = db.counterDao();
    }

    @SuppressWarnings("unchecked")
    public <AUSGERUFPFTE_BINSEN extends GameObject & IDescribableGO & ILocatableGO & IAmountableGO>
    AUSGERUFPFTE_BINSEN createEinigeAusgerupfteBinsen() {
        final GameObjectId newId = generateNewGameObjectId();

        final ImmutableMap<Integer, EinzelneSubstantivischePhrase>
                soundsovieleBinsen =
                ImmutableMap.<Integer, EinzelneSubstantivischePhrase>builder()
                        .put(0, np(NEG_INDEF, BINSEN))
                        .put(1, np(EINIGE, AUSGERUPFT, BINSEN, newId))
                        .put(3, np(VIEL_INDEF, BINSEN))
                        .build();

        return (AUSGERUFPFTE_BINSEN) new AmountObject(newId,
                db,
                new TypeComp(newId, db, GameObjectType.AUSGERUPFTE_BINSEN),
                1,
                new AmountDescriptionComp(
                        db.counterDao(),
                        newId,
                        soundsovieleBinsen,
                        np(AUSGERUPFT, BINSEN, newId),
                        np(BINSEN, newId)),
                new LocationComp(newId, db, world, BINSENSUMPF, null, true,
                        true));
    }

    GOTransformation binsenseilFlechtenTransformation() {
        return new GOTransformation(
                "Die Binsen zu einem langen Seil flechten",
                ImmutableMap.of(GameObjectType.AUSGERUPFTE_BINSEN, 3),
                this::altAndOutputBinsenseilFlechten);
    }

    private <BINSENSEIL extends GameObject & IDescribableGO & ILocatableGO & IAmountableGO>
    GOTransformationResult altAndOutputBinsenseilFlechten(
            final Map<GameObjectType, ITypedGO> input) {
        final IDescribableGO ausgerupfteBinsen =
                (IDescribableGO)
                        requireNonNull(input.get(GameObjectType.AUSGERUPFTE_BINSEN));

        final BINSENSEIL binsenseil = createEinLangesBinsenseil();
        final ImmutableCollection<IAmountableGO> output = ImmutableList.of(binsenseil);

        final AltDescriptionsBuilder alt = alt();

        alt.add(
                du(VerbSubjObj.FLECHTEN
                        .mitAdvAngabe(new AdvAngabeSkopusSatz(
                                // FIXME "daraus" / "aus dem Seil" / "aus der Frau"
                                //  (jedenfalls nicht *"aus ihm" (dem Seil))
                                AUS.mit(anaph(ausgerupfteBinsen))))
                        .mit(anaph(binsenseil)))
        );

        // FIXME Seil flechten..
        //  - "du [...Binsen...] flichst ein weiches Seil daraus"
        //  - "Binsenseil", "Fingerspitzengefühl und Kraft"

        return new GOTransformationResult(alt.timed(hours(2)), output);
    }

    @SuppressWarnings("unchecked")
    private <BINSENSEIL extends GameObject & IDescribableGO & ILocatableGO & IAmountableGO>
    BINSENSEIL createEinLangesBinsenseil() {
        final GameObjectId newId = generateNewGameObjectId();

        final ImmutableMap<Integer, EinzelneSubstantivischePhrase>
                soundsovieleLangeBinsenseile =
                ImmutableMap.<Integer, EinzelneSubstantivischePhrase>builder()
                        .put(0, np(NEG_INDEF, LANG, BINSENSEIL))
                        .put(1, np(INDEF, LANG, BINSENSEIL,
                                newId))
                        .put(2, np(PL_MFN, INDEF,
                                "zwei lange Binsenseile",
                                "zwei langen Binsenseilen"))
                        .put(3, np(PL_MFN, INDEF,
                                "drei lange Binsenseile",
                                "drei langen Binsenseilen"))
                        .put(4, np(PL_MFN, INDEF,
                                "vier lange Binsenseile",
                                "vier langen Binsenseilen"))
                        .put(5, np(EINIGE, LANG, BINSENSEILE))
                        .build();

        return (BINSENSEIL) new AmountObject(newId,
                db,
                new TypeComp(newId, db, GameObjectType.LANGES_BINSENSEIL),
                1,
                new AmountDescriptionComp(
                        db.counterDao(),
                        newId,
                        soundsovieleLangeBinsenseile,
                        np(LANG, BINSENSEIL, newId),
                        np(BINSENSEIL, newId)),
                new LocationComp(newId, db, world, null, null,
                        true, false));
    }

    /**
     * Erzeugt eine neue {@link GameObjectId}, die bisher noch nicht vorkam.
     */
    private GameObjectId generateNewGameObjectId() {
        return new GameObjectId(counterDao.incAndGet(Counter.NUM_ON_THE_FLY_GAME_OBJECTS)
                + MAX_STATIC_GAME_OBJECT_ID.toLong());
    }

    private static class AmountObject extends SimpleTypedObject
            implements IAmountableGO {
        private final AmountComp amountComp;

        AmountObject(final GameObjectId id,
                     final AvDatabase db,
                     final TypeComp typeComp,
                     final int initialAmount,
                     final AbstractDescriptionComp descriptionComp,
                     final LocationComp locationComp) {
            this(id, typeComp,
                    new AmountComp(id, db, initialAmount),
                    descriptionComp, locationComp);
        }

        AmountObject(final GameObjectId id,
                     final TypeComp typeComp,
                     final AmountComp amountComp,
                     final AbstractDescriptionComp descriptionComp,
                     final LocationComp locationComp) {
            super(id, typeComp, descriptionComp, locationComp);
            // Jede Komponente muss registiert werden!
            this.amountComp = addComponent(amountComp);
        }

        @NonNull
        @Override
        public AmountComp amountComp() {
            return amountComp;
        }
    }

    private static class SimpleTypedObject extends SimpleObject
            implements ITypedGO {
        private final TypeComp typeComp;

        SimpleTypedObject(final GameObjectId id,
                          final TypeComp typeComp,
                          final AbstractDescriptionComp descriptionComp,
                          final LocationComp locationComp) {
            super(id, descriptionComp, locationComp);
            // Jede Komponente muss registiert werden!
            this.typeComp = addComponent(typeComp);
        }

        @NonNull
        @Override
        public TypeComp typeComp() {
            return typeComp;
        }
    }
}
