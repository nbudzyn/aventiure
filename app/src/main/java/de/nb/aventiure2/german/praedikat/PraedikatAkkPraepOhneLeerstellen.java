package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import java.util.Collection;

import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.PraepositionMitKasus;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static de.nb.aventiure2.german.base.GermanUtil.joinToNull;

/**
 * Ein Prädikat, in dem ein Akkusativobjekt und ein Präpositionalobjekt gesetzt sind
 * und es auch sonst keine Leerstellen gibt. Beispiel: "den Frosch in die Hände nehmen"
 */
public class PraedikatAkkPraepOhneLeerstellen extends AbstractPraedikatOhneLeerstellen {
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
        super(verb);
        this.praepositionMitKasus = praepositionMitKasus;
        this.describablePraep = describablePraep;
        this.describableAkk = describableAkk;
    }

    @Override
    public boolean duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen() {
        return true;
    }

    @Override
    public @Nullable
    String getSpeziellesVorfeld() {
        return describableAkk.akk(); // "das Teil"
    }

    @Override
    public String getMittelfeld(final Collection<Modalpartikel> modalpartikeln) {
        return joinToNull(
                describableAkk.akk(), // "das Teil"
                joinToNull(modalpartikeln), // "besser doch"
                describablePraep.im(praepositionMitKasus)); // "auf dem Boden"
    }

    @Override
    public String getInfinitiv(final Person person, final Numerus numerus,
                               @Nullable final AdverbialeAngabe adverbialeAngabe) {
        return joinToNull(
                describableAkk.akk(), // "das Teil"
                adverbialeAngabe, // "erneut"
                describablePraep.im(praepositionMitKasus), // "auf dem Boden"
                getVerb().getInfinitiv()); // "abstellen"
    }

    @Override
    public String getZuInfinitiv(final Person person, final Numerus numerus,
                                 @Nullable final AdverbialeAngabe adverbialeAngabe) {
        return joinToNull(
                describableAkk.akk(), // "das Teil"
                adverbialeAngabe, // "erneut"
                describablePraep.im(praepositionMitKasus), // "auf dem Boden"
                getVerb().getZuInfinitiv()); // "abzustellen"
    }

    @Override
    public String getNachfeld() {
        return null;
    }
}
