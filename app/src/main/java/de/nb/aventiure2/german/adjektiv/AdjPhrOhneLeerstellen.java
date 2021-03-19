package de.nb.aventiure2.german.adjektiv;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.Praedikativum;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;

import static com.google.common.collect.ImmutableList.toImmutableList;

/**
 * Eine Adjektivphrase, bei der alle geforderten Ergänzungen gesetzt sind:
 * <ul>
 * <li>glücklich
 * <li>glücklich, dich zu sehen
 * </ul>
 */
public interface AdjPhrOhneLeerstellen extends Adjektivphrase, Praedikativum {
    static ImmutableList<AdvAngabeSkopusVerbAllg> toAdvAngabenSkopusVerbAllg(
            final SubstantivischePhrase subjekt,
            final Collection<AdjPhrOhneLeerstellen> adjektivPhrasen) {
        return adjektivPhrasen.stream()
                .filter(ap -> ap.isGeeignetAlsAdvAngabe(subjekt))
                .map(AdjPhrOhneLeerstellen::alsAdvAngabeSkopusVerbAllg)
                .collect(toImmutableList());
    }

    default boolean isGeeignetAlsAdvAngabe(final SubstantivischePhrase subjekt) {
        // "Sie schaut dich überrascht an.", aber nicht
        // *"Sie schaut dich überrascht an, dich zu sehen".
        return getPraedikativAnteilKandidatFuerNachfeld(
                subjekt.getPerson(),
                subjekt.getNumerus()) == null;
    }

    default AdjPhrOhneLeerstellen mitGraduativerAngabe(@Nullable final String graduativeAngabe) {
        return mitGraduativerAngabe(
                graduativeAngabe != null ? new GraduativeAngabe(graduativeAngabe) : null);
    }

    AdjPhrOhneLeerstellen mitGraduativerAngabe(@Nullable GraduativeAngabe graduativeAngabe);

    /**
     * Gibt die prädikative Form zurück: "hoch", "glücklich, dich zu sehen",
     * "glücklich, sich erheben zu dürfen"
     */
    @Override
    default Konstituentenfolge getPraedikativ(final Person person, final Numerus numerus) {
        return getPraedikativOderAdverbial(person, numerus);
    }

    /**
     * Gibt die prädikative oder adverbiale Form zurück: "hoch", "glücklich, dich zu sehen",
     * "glücklich, sich erheben zu dürfen"
     */
    Konstituentenfolge getPraedikativOderAdverbial(final Person person, final Numerus numerus);

    default AdvAngabeSkopusSatz alsAdvAngabeSkopusSatz() {
        return new AdvAngabeSkopusSatz(this);
    }

    default AdvAngabeSkopusVerbAllg alsAdvAngabeSkopusVerbAllg() {
        return new AdvAngabeSkopusVerbAllg(this);
    }

    /**
     * Gibt zurück, ob die Adjektivphrase eine zu-Infinitivphrase,
     * einen Angabensatz oder einen Ergänzungssatz (z.B. eine indirekte Frage) enthält.
     * <p>
     * Solche Adjektivphrasen können / sollen nicht im Mittelfeld auftreten.
     */
    boolean enthaeltZuInfinitivOderAngabensatzOderErgaenzungssatz();
}