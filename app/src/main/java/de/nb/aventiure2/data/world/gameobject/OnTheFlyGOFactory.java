package de.nb.aventiure2.data.world.gameobject;

import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.AUSGERUPFT;
import static de.nb.aventiure2.german.base.Artikel.Typ.INDEF;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.BINSEN;
import static de.nb.aventiure2.german.base.Nominalphrase.np;

import androidx.annotation.NonNull;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.counter.CounterDao;
import de.nb.aventiure2.data.world.syscomp.amount.AmountComp;
import de.nb.aventiure2.data.world.syscomp.amount.IAmountableGO;
import de.nb.aventiure2.data.world.syscomp.description.AbstractDescriptionComp;
import de.nb.aventiure2.data.world.syscomp.description.impl.SimpleDescriptionComp;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.typed.GameObjectType;
import de.nb.aventiure2.data.world.syscomp.typed.ITypedGO;
import de.nb.aventiure2.data.world.syscomp.typed.TypeComp;

/**
 * Eine Factory für GameObjects, die on-the-fly erzeugt werden.
 */
public class OnTheFlyGOFactory extends AbstractGameObjectFactory {
    private enum Counter {
        NUM_ON_THE_FLY_GAME_OBJECTS
    }

    private final CounterDao counterDao;

    public OnTheFlyGOFactory(final AvDatabase db,
                             final TimeTaker timeTaker,
                             final World world) {
        super(db, timeTaker, world);

        counterDao = db.counterDao();
    }

    public GameObject createEinigeAusgerupfteBinsen() {
        final GameObjectId newId = generateNewGameObjectId();

        return new AmountObject(newId,
                db,
                new TypeComp(newId, db, GameObjectType.AUSGERUPFTE_BINSEN),
                1,
                new SimpleDescriptionComp(newId,
                        np(INDEF, AUSGERUPFT, BINSEN),
                        BINSEN.mit(AUSGERUPFT),
                        BINSEN),
                new LocationComp(newId, db, world, BINSENSUMPF, null, true,
                        true));
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
