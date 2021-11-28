package de.nb.aventiure2.scaction.impl;

import static de.nb.aventiure2.data.world.syscomp.typed.GameObjectType.AUSGERUPFTE_BINSEN;
import static de.nb.aventiure2.data.world.syscomp.typed.GameObjectType.LANGES_BINSENSEIL;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.amount.AmountComp;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.typed.GameObjectType;
import de.nb.aventiure2.data.world.syscomp.typed.ITypedGO;
import de.nb.aventiure2.scaction.AbstractScAction;
import de.nb.aventiure2.scaction.stepcount.SCActionStepCountDao;

/**
 * Der SC wandelt ein Objekt (es kann auch eine Objekt mit Mengenangabe sein,
 * vgl. {@link de.nb.aventiure2.data.world.syscomp.amount.AmountComp}) in ein anderes um
 * (es geht nicht um eine reine Statusänderung).
 * Das Game Object muss sich im Inventar des SCs befinden.
 *
 * @see ZustandVeraendernAction
 */
public class TransformAction<GO extends IDescribableGO & ILocatableGO> extends AbstractScAction {
    @NonNull
    private final GO gameObject;
    private final GameObjectType type;

    /**
     * Erzeugt alle Aktionen, mit denen der Benutzer dieses <code>gameObject</code>
     * in ein anders umwandeln kann.
     */
    public static <GO extends IDescribableGO & ILocatableGO>
    Collection<TransformAction<GO>> buildActions(final SCActionStepCountDao scActionStepCountDao,
                                                 final TimeTaker timeTaker,
                                                 final Narrator n, final World world,
                                                 final GO gameObject) {
        final ImmutableList.Builder<TransformAction<GO>> res = ImmutableList.builder();

        // FIXME Nicht so gut. Besser analog zur ZustandVeraenderAction
        //  mit einer neuen Component für Transformations

        if ((gameObject instanceof ITypedGO)
                && gameObject instanceof AmountComp
                && ((ITypedGO) gameObject).typeComp().hasType(AUSGERUPFTE_BINSEN)
                && ((AmountComp) gameObject).getAmount() >= 3) {
            res.add(new TransformAction<>(scActionStepCountDao, timeTaker, n, world, gameObject,
                    LANGES_BINSENSEIL));
        }

        return res.build();
    }

    private TransformAction(final SCActionStepCountDao scActionStepCountDao,
                            final TimeTaker timeTaker,
                            final Narrator n,
                            final World world,
                            final @NonNull GO gameObject,
                            final GameObjectType targetType) {
        super(scActionStepCountDao, timeTaker, n, world);
        this.gameObject = gameObject;
        type = targetType;
    }

    // FIXME Seil flechten..
    //  - "du [...Binsen...] flichst ein weiches Seil daraus"
    //  - "Binsenseil", "Fingerspitzengefühl und Kraft"
    //  - Wenn zu wenige Binsen: Du erhältst nur ein sehr kurzes Seil. / Das Seil ist
    //  nicht besonders lang, stabil sieht es auch nicht aus. / Aus den vielen Binsen flichst du
    //  ein langes, stabiles Seil.


}
