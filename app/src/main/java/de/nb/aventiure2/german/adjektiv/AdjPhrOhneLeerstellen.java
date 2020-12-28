package de.nb.aventiure2.german.adjektiv;

import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.Praedikativum;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusVerbAllg;

/**
 * Eine Adjektivphrase, bei der alle geforderten Ergänzungen gesetzt sind:
 * <ul>
 * <li>glücklich
 * <li>glücklich, dich zu sehen
 * </ul>
 */
public interface AdjPhrOhneLeerstellen extends Adjektivphrase, Praedikativum {
    default AdjPhrOhneLeerstellen mitGraduativerAngabe(@Nullable final String graduativeAngabe) {
        return mitGraduativerAngabe(new GraduativeAngabe(graduativeAngabe));
    }

    AdjPhrOhneLeerstellen mitGraduativerAngabe(@Nullable GraduativeAngabe graduativeAngabe);

    /**
     * Gibt die prädikative Form zurück: "hoch", "glücklich, dich zu sehen",
     * "glücklich, sich erheben zu dürfen"
     */
    @Override
    default Iterable<Konstituente> getPraedikativ(final Person person, final Numerus numerus) {
        return getPraedikativOderAdverbial(person, numerus);
    }

    /**
     * Gibt die prädikative oder adverbiale Form zurück: "hoch", "glücklich, dich zu sehen",
     * "glücklich, sich erheben zu dürfen"
     */
    Iterable<Konstituente> getPraedikativOderAdverbial(final Person person, final Numerus numerus);

    default AdverbialeAngabeSkopusVerbAllg alsAdverbialeAngabe(final Person personSubjekt,
                                                               final Numerus numerusSubjekt) {
        return new AdverbialeAngabeSkopusVerbAllg(
                this, personSubjekt, numerusSubjekt);
    }

    /**
     * Gibt zurück, ob die Adjektivphrase eine zu-Infinitivphrase,
     * einen Angabensatz oder einen Ergänzungssatz (z.B. eine indirekte Frage) enthält.
     * <p>
     * Solche Adjektivphrasen können / sollen nicht im Mittelfeld auftreten.
     */
    boolean enthaeltZuInfinitivOderAngabensatzOderErgaenzungssatz();
}