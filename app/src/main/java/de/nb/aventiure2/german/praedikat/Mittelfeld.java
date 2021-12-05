package de.nb.aventiure2.german.praedikat;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Objects.requireNonNull;
import static de.nb.aventiure2.german.base.Konstituentenfolge.cutFirstOneByOne;
import static de.nb.aventiure2.german.base.Konstituentenfolge.kf;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Stream;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import de.nb.aventiure2.german.base.IAlternativeKonstituentenfolgable;
import de.nb.aventiure2.german.base.Kasus;
import de.nb.aventiure2.german.base.KasusOderPraepositionalkasus;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.SubstPhrOderReflexivpronomen;

/**
 * Ein (syntaktisches) Mittelfeld.
 */
@Immutable
public class Mittelfeld implements IAlternativeKonstituentenfolgable {
    static final Mittelfeld EMPTY = new Mittelfeld(null);

    @Nullable
    private final Konstituentenfolge mittelfeldOhneLinksversetzungUnbetonterPronomen;

    /**
     * Die <i>unbetonten</i> Pronomen.
     */
    private final ImmutableList<Konstituentenfolge> unbetontePronomen;

    Mittelfeld(
            @Nullable final Konstituentenfolge mittelfeldOhneLinksversetzungUnbetonterPronomen,
            final SubstPhrOderReflexivpronomen substPhrOderReflexivpronomen,
            final KasusOderPraepositionalkasus kasusOderPraepositionalkasus) {
        this(mittelfeldOhneLinksversetzungUnbetonterPronomen,
                toPair(substPhrOderReflexivpronomen, kasusOderPraepositionalkasus));
    }

    Mittelfeld(
            @Nullable final Konstituentenfolge mittelfeldOhneLinksversetzungUnbetonterPronomen,
            final SubstPhrOderReflexivpronomen substPhrOderReflexivpronomen1,
            final KasusOderPraepositionalkasus kasusOderPraepositionalkasus1,
            final SubstPhrOderReflexivpronomen substPhrOderReflexivpronomen2,
            final KasusOderPraepositionalkasus kasusOderPraepositionalkasus2) {
        this(mittelfeldOhneLinksversetzungUnbetonterPronomen,
                toPair(substPhrOderReflexivpronomen1, kasusOderPraepositionalkasus1),
                toPair(substPhrOderReflexivpronomen2, kasusOderPraepositionalkasus2));
    }

    Mittelfeld(
            @Nullable final Konstituentenfolge mittelfeldOhneLinksversetzungUnbetonterPronomen,
            final Pair<SubstPhrOderReflexivpronomen, KasusOderPraepositionalkasus>...
                    substantivischePhrasenMitKasus) {
        this(mittelfeldOhneLinksversetzungUnbetonterPronomen,
                filterUnbetontePronomen(substantivischePhrasenMitKasus));
    }

    private Mittelfeld(
            @Nullable final Konstituentenfolge mittelfeldOhneLinksversetzungUnbetonterPronomen,
            final ImmutableList<Konstituentenfolge> unbetontePronomen) {
        this.mittelfeldOhneLinksversetzungUnbetonterPronomen =
                mittelfeldOhneLinksversetzungUnbetonterPronomen;
        this.unbetontePronomen = unbetontePronomen;
    }

    @Override
    public Collection<Konstituentenfolge> toAltKonstituentenfolgen() {
        return Collections.singleton(
                // Das Mittelfeld besteht aus drei Teilen:
                Konstituentenfolge.joinToNullKonstituentenfolge(
                        // 1. Der Bereich vor der Wackernagel-Position. Dort kann höchstens ein
                        //   Subjekt stehen, das keine unbetontes Pronomen ist.
                        //   Das Subjekt ist hier im Prädikat noch nicht bekannt.
                        // 2. Die Wackernagelposition. Hier stehen alle unbetonten Pronomen in den
                        // reinen Kasus in der festen Reihenfolge Nom < Akk < Dat
                        kf(unbetontePronomen),
                        // 3. Der Bereich nach der Wackernagel-Position. Hier steht alles übrige
                        cutFirstOneByOne(
                                mittelfeldOhneLinksversetzungUnbetonterPronomen,
                                unbetontePronomen
                        )));
    }

    private static Pair<SubstPhrOderReflexivpronomen, KasusOderPraepositionalkasus> toPair(
            @Nullable final
            SubstPhrOderReflexivpronomen substPhrOderReflexivpronomen,
            final KasusOderPraepositionalkasus kasusOderPraepositionalkasus) {
        if (substPhrOderReflexivpronomen == null) {
            return null;
        }

        return Pair.create(substPhrOderReflexivpronomen, kasusOderPraepositionalkasus);
    }

    @SafeVarargs
    private static ImmutableList<Konstituentenfolge> filterUnbetontePronomen(
            final Pair<SubstPhrOderReflexivpronomen, KasusOderPraepositionalkasus>...
                    substantivischePhrasenMitKasus) {
        return Stream.of(substantivischePhrasenMitKasus)
                .filter(Objects::nonNull)
                .filter(spk -> requireNonNull(spk.first).isUnbetontesPronomen())
                .filter(spk -> requireNonNull(spk.second) instanceof Kasus)
                .map(spk -> spk.first.imK(requireNonNull((Kasus) spk.second)))
                .collect(toImmutableList());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Mittelfeld that = (Mittelfeld) o;
        return Objects.equals(mittelfeldOhneLinksversetzungUnbetonterPronomen,
                that.mittelfeldOhneLinksversetzungUnbetonterPronomen) && unbetontePronomen
                .equals(that.unbetontePronomen);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mittelfeldOhneLinksversetzungUnbetonterPronomen, unbetontePronomen);
    }

    @NonNull
    @Override
    public String toString() {
        @Nullable final Konstituentenfolge konstituentenfolge =
                toAltKonstituentenfolgen().iterator().next();
        if (konstituentenfolge == null) {
            return "";
        }

        return konstituentenfolge.joinToSingleKonstituente().toTextOhneKontext();
    }
}
