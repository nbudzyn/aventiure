package de.nb.aventiure2.data.world.syscomp.description;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.Objects;

import de.nb.aventiure2.data.world.counter.CounterDao;
import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;

public class DescriptionTriple {
    private final CounterDao counterDao;

    @NonNull
    private final ImmutableList<EinzelneSubstantivischePhrase> altAtFirstSight;
    @NonNull
    private final ImmutableList<EinzelneSubstantivischePhrase> altNormalWhenKnown;
    @NonNull
    private final ImmutableList<EinzelneSubstantivischePhrase> altShortWhenKnown;

    public DescriptionTriple(final CounterDao counterDao,
                             @NonNull final EinzelneSubstantivischePhrase descriptionAtFirstSight,
                             @NonNull final EinzelneSubstantivischePhrase descriptionWhenKnown) {
        this(counterDao, descriptionAtFirstSight, descriptionWhenKnown, descriptionWhenKnown);
    }

    public DescriptionTriple(final CounterDao counterDao,
                             @NonNull final EinzelneSubstantivischePhrase descriptionAtFirstSight,
                             @NonNull
                             final EinzelneSubstantivischePhrase normalDescriptionWhenKnown,
                             @NonNull
                             final EinzelneSubstantivischePhrase shortDescriptionWhenKnown) {
        this(counterDao,
                ImmutableList.of(descriptionAtFirstSight),
                ImmutableList.of(normalDescriptionWhenKnown),
                ImmutableList.of(shortDescriptionWhenKnown));
    }

    public DescriptionTriple(final CounterDao counterDao,
                             @NonNull final Collection<? extends EinzelneSubstantivischePhrase>
                                     altAtFirstSight,
                             @NonNull
                             final EinzelneSubstantivischePhrase normalDescriptionWhenKnown,
                             @NonNull
                             final EinzelneSubstantivischePhrase shortDescriptionWhenKnown) {
        this(counterDao,
                altAtFirstSight,
                ImmutableList.of(normalDescriptionWhenKnown),
                shortDescriptionWhenKnown);
    }

    public DescriptionTriple(final CounterDao counterDao,
                             @NonNull final EinzelneSubstantivischePhrase descriptionAtFirstSight,
                             @NonNull
                             final Collection<? extends EinzelneSubstantivischePhrase> altNormalWhenKnown,
                             @NonNull
                             final EinzelneSubstantivischePhrase shortDescriptionWhenKnown) {
        this(counterDao,
                ImmutableList.of(descriptionAtFirstSight),
                altNormalWhenKnown,
                shortDescriptionWhenKnown);
    }

    private DescriptionTriple(final CounterDao counterDao,
                              @NonNull final Collection<? extends EinzelneSubstantivischePhrase>
                                      altAtFirstSight,
                              @NonNull final Collection<? extends EinzelneSubstantivischePhrase>
                                      altNormalWhenKnown,
                              @NonNull
                              final EinzelneSubstantivischePhrase shortDescriptionWhenKnown) {
        this(counterDao,
                altAtFirstSight,
                altNormalWhenKnown,
                ImmutableList.of(shortDescriptionWhenKnown));
    }

    DescriptionTriple(final CounterDao counterDao,
                      @NonNull
                      final Collection<? extends EinzelneSubstantivischePhrase> altAtFirstSight,
                      @NonNull
                      final Collection<? extends EinzelneSubstantivischePhrase> altNormalWhenKnown,
                      @NonNull
                      final Collection<? extends EinzelneSubstantivischePhrase> altShortWhenKnown) {
        this.counterDao = counterDao;
        this.altAtFirstSight = ImmutableList.copyOf(
                checkNotEmpty(altAtFirstSight));
        this.altNormalWhenKnown = ImmutableList.copyOf(
                checkNotEmpty(altNormalWhenKnown));
        this.altShortWhenKnown = ImmutableList.copyOf(
                checkNotEmpty(altShortWhenKnown));
    }

    private static <T> Collection<T> checkNotEmpty(final Collection<T> collection) {
        checkNotNull(collection, "Collection is null!");
        checkArgument(!collection.isEmpty(), "Collection is empty!");
        return collection;
    }

    @NonNull
    public EinzelneSubstantivischePhrase get(
            final boolean known,
            final boolean shortIfKnown) {
        if (known) {
            return getWhenKnown(shortIfKnown);
        }

        return getAtFirstSight();
    }

    @NonNull
    public ImmutableList<EinzelneSubstantivischePhrase> alt(
            final boolean known,
            final boolean shortIfKnown) {
        if (known) {
            return altWhenKnown(shortIfKnown);
        }

        return altAtFirstSight();
    }

    /**
     * Gibt eine Beschreibung zurück für den Fall, dass der SC sich nicht an das Objekt erinnert.
     * Stehen mehrere Beschreibungen zur Auswahl, wird reihum eine gewählt - hier führt oft
     * {@link #altAtFirstSight()} zu besseren Ergebnissen, weil dort der Aufrufer
     * entscheiden und dabei den sprachlichen Kontext berücksichtigen kann.
     */
    @NonNull
    public EinzelneSubstantivischePhrase getAtFirstSight() {
        return chooseOne(altAtFirstSight);
    }

    @NonNull
    public ImmutableList<EinzelneSubstantivischePhrase> altAtFirstSight() {
        return altAtFirstSight;
    }

    private EinzelneSubstantivischePhrase getWhenKnown(
            final boolean shortIfKnown) {
        return shortIfKnown ? getShortWhenKnown() : getNormalWhenKnown();
    }

    private ImmutableList<EinzelneSubstantivischePhrase> altWhenKnown(
            final boolean shortIfKnown) {
        return shortIfKnown ? altShortWhenKnown() : altNormalWhenKnown();
    }

    /**
     * Gibt eine normale (nicht besonders kurze) Beschreibung zurück für den Fall, dass der SC
     * das Objekt kennt.
     * Stehen mehrere Beschreibungen zur Auswahl, wird reihum eine gewählt - hier führt oft
     * {@link #altNormalWhenKnown()} zu besseren Ergebnissen, weil dort der Aufrufer
     * entscheiden und dabei den sprachlichen Kontext berücksichtigen kann.
     */
    @NonNull
    public EinzelneSubstantivischePhrase getNormalWhenKnown() {
        return chooseOne(altNormalWhenKnown);
    }

    @NonNull
    public ImmutableList<EinzelneSubstantivischePhrase> altNormalWhenKnown() {
        return altNormalWhenKnown;
    }

    /**
     * Gibt eine kurze Beschreibung zurück für den Fall, dass der SC das Objekt bereits kennt.
     * Stehen mehrere Beschreibungen zur Auswahl, wird reihum eine gewählt - hier führt oft
     * {@link #altShortWhenKnown()} zu besseren Ergebnissen, weil dort der Aufrufer
     * entscheiden und dabei den sprachlichen Kontext berücksichtigen kann.
     */
    @NonNull
    public EinzelneSubstantivischePhrase getShortWhenKnown() {
        return chooseOne(altShortWhenKnown);
    }

    @NonNull
    public ImmutableList<EinzelneSubstantivischePhrase> altShortWhenKnown() {
        return altShortWhenKnown;
    }

    private EinzelneSubstantivischePhrase chooseOne(
            final ImmutableList<? extends EinzelneSubstantivischePhrase> alternatives) {
        if (alternatives.size() == 1) {
            // shortcut!
            return alternatives.get(0);
        }

        final int count = counterDao.incAndGet(getClass().getCanonicalName() + "_"
                + alternatives.hashCode()) - 1;

        return alternatives.get(count % alternatives.size());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final DescriptionTriple that = (DescriptionTriple) o;
        return altAtFirstSight.equals(that.altAtFirstSight) &&
                altNormalWhenKnown.equals(that.altNormalWhenKnown) &&
                altShortWhenKnown.equals(that.altShortWhenKnown);
    }

    @Override
    public int hashCode() {
        return Objects
                .hash(altAtFirstSight, altNormalWhenKnown,
                        altShortWhenKnown);
    }
}