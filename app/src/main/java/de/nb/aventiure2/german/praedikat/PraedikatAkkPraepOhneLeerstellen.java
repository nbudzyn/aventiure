package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.PraepositionMitKasus;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static de.nb.aventiure2.german.base.GermanUtil.capitalize;
import static de.nb.aventiure2.german.base.GermanUtil.joinToNull;

/**
 * Ein Prädikat, in dem ein Akkusativobjekt und ein Präpositionalobjekt gesetzt sind
 * und es auch sonst keine Leerstellen gibt. Beispiel: "den Frosch in die Hände nehmen"
 */
public class PraedikatAkkPraepOhneLeerstellen implements PraedikatOhneLeerstellen {
    /**
     * Das Verb an sich, ohne Informationen zur Valenz, ohne Ergänzungen, ohne
     * Angaben
     */
    @NonNull
    private final Verb verb;

    @NonNull
    private final PraepositionMitKasus praepositionMitKasus;

    @NonNull
    private final SubstantivischePhrase describablePraep;

    @NonNull
    private final SubstantivischePhrase describableAkk;

    public PraedikatAkkPraepOhneLeerstellen(final Verb verb,
                                            final PraepositionMitKasus praepositionMitKasus,
                                            final SubstantivischePhrase describableAkk,
                                            final SubstantivischePhrase describablePraep) {
        this.verb = verb;
        this.praepositionMitKasus = praepositionMitKasus;
        this.describablePraep = describablePraep;
        this.describableAkk = describableAkk;
    }

    /**
     * Gibt einen Satz mit diesem Prädikat zurück.
     * ("Du nimmst den Frosch in die Hände")
     */
    @Override
    public String getDescriptionDuHauptsatz(final Collection<Modalpartikel> modalpartikeln) {
        return joinToNull(
                "Du",
                verb.getDuForm(), // "stellst"
                describableAkk.akk(), // "das Teil"
                joinToNull(modalpartikeln), // "besser doch"
                describablePraep.im(praepositionMitKasus), // "auf dem Boden"
                verb.getPartikel()); // "ab"
    }

    @Override
    public boolean duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen() {
        return true;
    }

    @Override
    public String getDescriptionDuHauptsatz(@Nonnull final AdverbialeAngabe adverbialeAngabe) {
        return joinToNull(capitalize(adverbialeAngabe.getText()), // Aus Langeweile
                verb.getDuForm(),
                "du",
                describableAkk.akk(), // "das Teil"
                describablePraep.im(praepositionMitKasus), // "auf dem Boden"
                verb.getPartikel()); // "ab"
    }

    @Override
    public String getDescriptionInfinitiv(final Person person, final Numerus numerus,
                                          @Nullable final AdverbialeAngabe adverbialeAngabe) {
        return joinToNull(
                describableAkk.akk(), // "das Teil"
                adverbialeAngabe, // "erneut"
                describablePraep.im(praepositionMitKasus), // "auf dem Boden"
                verb.getInfinitiv()); // "abstellen"
    }

    @Override
    public String getDescriptionZuInfinitiv(final Person person, final Numerus numerus,
                                            @Nullable final AdverbialeAngabe adverbialeAngabe) {
        return joinToNull(
                describableAkk.akk(), // "das Teil"
                adverbialeAngabe, // "erneut"
                describablePraep.im(praepositionMitKasus), // "auf dem Boden"
                verb.getZuInfinitiv()); // "abzustellen"
    }
}
