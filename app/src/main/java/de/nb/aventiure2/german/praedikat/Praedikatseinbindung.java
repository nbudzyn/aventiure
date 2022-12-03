package de.nb.aventiure2.german.praedikat;

import static com.google.common.collect.ImmutableList.toImmutableList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import de.nb.aventiure2.german.base.IInterrogativwort;
import de.nb.aventiure2.german.base.IKonstituentenfolgable;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.Relativpronomen;
import de.nb.aventiure2.german.base.SubstPhrOderReflexivpronomen;
import de.nb.aventiure2.german.base.UmstellbarePhrase;

/**
 * Beschreibt, wie eine {@link de.nb.aventiure2.german.base.UmstellbarePhrase}
 * (z.B. ein Objekt, ein Prädikativum oder eine adverbiale Angabe) in ein
 * Prädikat eingebunden wird. Objekte werden beispielsweise dekliniert.
 */
public class Praedikatseinbindung<P extends UmstellbarePhrase> implements
        IKonstituentenfolgable {
    /**
     * Die Phrase (ein Objekt, ein Prädikativum, eine adverbiale Angabe o.Ä.), die ins Prädikat
     * eingebunden werden soll.
     */
    private final P phrase;

    /**
     * Die Deklination (von Objekten) oder anderweitige Anpassung der Phrase,
     * die für die Einbindung notwendig ist.
     */
    private final Function<? super P, Konstituentenfolge> flexion;

    Praedikatseinbindung(final P phrase,
                         final Function<? super P, Konstituentenfolge> flexion) {
        this.phrase = phrase;
        this.flexion = flexion;
    }

    /**
     * Extrahiert aus diesen Phrasen das erste Relativpronomen.
     */
    @Nullable
    static Konstituentenfolge firstRelativpronomen(
            final IKonstituentenfolgable... konstituentenfolgables) {
        for (final IKonstituentenfolgable konstituentenfolgable : konstituentenfolgables) {
            if (konstituentenfolgable instanceof Relativpronomen
                    || (konstituentenfolgable instanceof Praedikatseinbindung
                    && ((Praedikatseinbindung<?>) konstituentenfolgable)
                    .getPhrase() instanceof Relativpronomen)) {
                return konstituentenfolgable.toKonstituentenfolge();
            }
        }

        return null;
    }

    /**
     * Extrahiert aus diesen Phrasen das erste Interrogativwort.
     */
    @Nullable
    static Konstituentenfolge firstInterrogativwort(
            final IKonstituentenfolgable... konstituentenfolgables) {
        for (final IKonstituentenfolgable konstituentenfolgable : konstituentenfolgables) {
            if (konstituentenfolgable instanceof IInterrogativwort
                    || (konstituentenfolgable instanceof Praedikatseinbindung
                    && ((Praedikatseinbindung<?>) konstituentenfolgable)
                    .getPhrase() instanceof IInterrogativwort)) {
                return konstituentenfolgable.toKonstituentenfolge();
            }
        }

        return null;
    }

    /**
     * Extrahiert aus diesen Phrasen die unbetonten Pronomen. Die Reihenfolge bleibt erhalten.
     */
    @SafeVarargs
    static ImmutableList<Konstituentenfolge> filterUnbetontePronomen(
            final Praedikatseinbindung<?>... praedikatseinbindungen) {
        return Stream.of(praedikatseinbindungen)
                .filter(Objects::nonNull)
                .filter(p -> p.getPhrase() instanceof SubstPhrOderReflexivpronomen)
                .filter(p -> ((SubstPhrOderReflexivpronomen) p.getPhrase()).isUnbetontesPronomen())
                .map(Praedikatseinbindung::toKonstituentenfolge)
                .collect(toImmutableList());
    }

    public P getPhrase() {
        return phrase;
    }

    @Override
    public Konstituentenfolge toKonstituentenfolge() {
        return flexion.apply(phrase);
    }

    @NonNull
    @Override
    public String toString() {
        return "Praedikatseinbindung{" +
                "phrase=" + phrase +
                ", flexion=" + flexion +
                '}';
    }
}
