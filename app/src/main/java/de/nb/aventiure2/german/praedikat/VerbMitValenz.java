package de.nb.aventiure2.german.praedikat;

import androidx.annotation.Nullable;

import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

/**
 * Repräsentiert ein Verb als Lexem, von dem Wortformen gebildet werden können - einschließlich
 * der Informationen zur Valenz. Typischerweise enthält so ein Objekt ein
 * {@link Verb}-Objekt (ohne die Informationen zur Valenz.
 */
interface VerbMitValenz extends Praedikat {
    @Nullable
    default String getPraesensOhnePartikel(final SubstantivischePhrase subjekt) {
        return getVerb().getPraesensOhnePartikel(subjekt);
    }

    @Nullable
    default String getPraesensOhnePartikel(final Person person, final Numerus numerus) {
        return getVerb().getPraesensOhnePartikel(person, numerus);
    }

    Verb getVerb();
}
