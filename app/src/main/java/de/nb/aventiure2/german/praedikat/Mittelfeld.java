package de.nb.aventiure2.german.praedikat;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Objects.requireNonNull;
import static de.nb.aventiure2.german.base.Konstituentenfolge.cutFirstOneByOne;
import static de.nb.aventiure2.german.base.Konstituentenfolge.kf;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;

import com.google.common.collect.ImmutableList;

import java.util.Objects;
import java.util.stream.Stream;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import de.nb.aventiure2.german.base.Kasus;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.SubstPhrOderReflexivpronomen;

/**
 * Ein (syntaktisches) Mittelfeld.
 */
@Immutable
public class Mittelfeld {
    @Nullable
    private final Konstituentenfolge mittelfeldOhneLinksversetzungUnbetonterPronomen;

    /**
     * Die <i>unbetonten</i> Pronomen.
     */
    private final ImmutableList<Konstituentenfolge> unbetontePronomen;

    /**
     * Konstruktor auf Basis der Objekte.
     *
     * @param zweitesAkkObjekt Manche Verben haben ein zweites Akkusativobjekt, z.B.
     *                         "jdn. etw. lehren".
     */
    Mittelfeld(
            final Konstituentenfolge mittelfeldOhneLinksversetzungUnbetonterPronomen,
            @Nullable final SubstPhrOderReflexivpronomen akkObjekt,
            @Nullable final SubstPhrOderReflexivpronomen zweitesAkkObjekt,
            @Nullable final SubstPhrOderReflexivpronomen datObjekt) {
        this(mittelfeldOhneLinksversetzungUnbetonterPronomen,
                filterUnbetontePronomen(
                        toPair(akkObjekt, Kasus.AKK),
                        toPair(zweitesAkkObjekt, Kasus.AKK),
                        toPair(datObjekt, Kasus.DAT)));
    }

    private Mittelfeld(
            final Konstituentenfolge mittelfeldOhneLinksversetzungUnbetonterPronomen,
            final ImmutableList<Konstituentenfolge> unbetontePronomen) {
        this.mittelfeldOhneLinksversetzungUnbetonterPronomen =
                mittelfeldOhneLinksversetzungUnbetonterPronomen;
        this.unbetontePronomen = unbetontePronomen;
    }

    Konstituentenfolge toKonstituentenfolge() {
        // Das Mittelfeld besteht aus drei Teilen:
        return Konstituentenfolge.joinToNullKonstituentenfolge(
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
                ));
    }

    private static Pair<SubstPhrOderReflexivpronomen, Kasus> toPair(
            @Nullable final
            SubstPhrOderReflexivpronomen substPhrOderReflexivpronomen,
            final Kasus kasus) {
        if (substPhrOderReflexivpronomen == null) {
            return null;
        }

        return Pair.create(substPhrOderReflexivpronomen, kasus);
    }

    @SafeVarargs
    private static ImmutableList<Konstituentenfolge> filterUnbetontePronomen(
            final Pair<SubstPhrOderReflexivpronomen, Kasus>... substantivischePhrasenMitKasus) {
        return Stream.of(substantivischePhrasenMitKasus)
                .filter(Objects::nonNull)
                .filter(spk -> requireNonNull(spk.first).isUnbetontesPronomen())
                .map(spk -> spk.first.imK(requireNonNull(spk.second)))
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
        return mittelfeldOhneLinksversetzungUnbetonterPronomen
                .equals(that.mittelfeldOhneLinksversetzungUnbetonterPronomen) && unbetontePronomen
                .equals(that.unbetontePronomen);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mittelfeldOhneLinksversetzungUnbetonterPronomen, unbetontePronomen);
    }

    @NonNull
    @Override
    public String toString() {
        return toKonstituentenfolge().joinToSingleKonstituente().toTextOhneKontext();
    }
}
