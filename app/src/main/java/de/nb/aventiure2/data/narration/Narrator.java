package de.nb.aventiure2.data.narration;

import androidx.annotation.VisibleForTesting;
import androidx.annotation.WorkerThread;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

import com.google.common.collect.ImmutableCollection;

import java.util.Collection;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.german.description.AbstractDescription;

import static java.util.Arrays.asList;

public class Narrator {
    private static volatile Narrator INSTANCE;

    private Narration.NarrationSource narrationSourceJustInCase =
            Narration.NarrationSource.INITIALIZATION;

    private final NarrationDao dao;

    public static Narrator getInstance(final AvDatabase db) {
        if (INSTANCE == null) {
            synchronized (Narrator.class) {
                if (INSTANCE == null) {
                    INSTANCE = new Narrator(db);
                }
            }
        }
        return INSTANCE;
    }

    @VisibleForTesting
    @WorkerThread
    public static void reset() {
        INSTANCE = null;
    }


    private Narrator(final AvDatabase db) {
        dao = db.narrationDao();
    }

    public void setNarrationSourceJustInCase(
            final Narration.NarrationSource narrationSourceJustInCase) {
        this.narrationSourceJustInCase = narrationSourceJustInCase;
    }

    public Narration.NarrationSource getNarrationSourceJustInCase() {
        return narrationSourceJustInCase;
    }

    public void narrateAlt(final AbstractDescription<?>... alternatives) {
        final Narration initialNarration = dao.requireNarration();
        dao.narrateAlt(narrationSourceJustInCase, asList(alternatives), initialNarration);

        // FIXME Damit Dinge wie "Unten angekommen..." möglich werden: Der Narrator merkt sich
        //  immer einen Satz, bevor er ihn
        //  ausgibt ("rendert"). Erst mit dem nächsten Satz wird der letzte Satz "gerendert".
        //  Bevor der Benutzer handeln kann, wird aber dieser "temporäre Satz" ausgegeben.

        // STORY "Als du unten angekommen bist..."
    }

    public void narrateAlt(final ImmutableCollection.Builder<AbstractDescription<?>> alternatives) {
        narrateAlt(alternatives.build());
    }

    public void narrateAlt(final Collection<AbstractDescription<?>> alternatives) {
        final Narration initialNarration = dao.requireNarration();
        dao.narrateAlt(narrationSourceJustInCase, alternatives, initialNarration);
    }

    public void narrate(final AbstractDescription<?> desc) {
        final Narration initialNarration = dao.requireNarration();
        dao.narrate(narrationSourceJustInCase, desc, initialNarration);
    }


    /**
     * Saves all All temporary data.
     */
    public void saveAll() {
        // FIXME Save all temporary data
    }

    public boolean lastNarrationWasFromReaction() {
        return dao.requireNarration().lastNarrationWasFomReaction();
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void saveInitialNarration(final Narration narration) {
        dao.insert(narration);
    }

    public Narration getNarration() {
        return dao.getNarration();
    }

    public Narration requireNarration() {
        return dao.requireNarration();
    }
}
