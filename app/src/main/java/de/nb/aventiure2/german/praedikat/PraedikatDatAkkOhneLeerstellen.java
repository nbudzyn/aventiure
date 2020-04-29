package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.DeklinierbarePhrase;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;

import static de.nb.aventiure2.german.base.GermanUtil.capitalize;
import static de.nb.aventiure2.german.base.GermanUtil.joinToNull;

/**
 * Ein Prädikat, in dem ein Dativobjekt und Akkusativobjekt gesetzt sind
 * und es auch sonst keine Leerstellen gibt.  Beispiel: "dem Frosch Angebote machen"
 */
public class PraedikatDatAkkOhneLeerstellen implements PraedikatOhneLeerstellen {
    /**
     * Das Verb an sich, ohne Informationen zur Valenz, ohne Ergänzungen, ohne
     * Angaben
     */
    @NonNull
    private final Verb verb;

    /**
     * Das (Objekt / Wesen / Konzept für das) Dativobjekt (z.B. "Angebote")
     */
    @NonNull
    private final DeklinierbarePhrase describableDat;

    /**
     * Das (Objekt / Wesen / Konzept für das) Akkusativobjekte (z.B. der Frosch)
     */
    @NonNull
    private final DeklinierbarePhrase describableAkk;

    public PraedikatDatAkkOhneLeerstellen(final Verb verb,
                                          final DeklinierbarePhrase describableDat,
                                          final DeklinierbarePhrase describableAkk) {
        this.verb = verb;
        this.describableDat = describableDat;
        this.describableAkk = describableAkk;
    }

    /**
     * Gibt einen Satz mit diesem Prädikat zurück.
     * ("Du machst dem Frosch Angebote")
     */
    @Override
    public String getDescriptionDuHauptsatz(
            final Collection<Modalpartikel> modalpartikeln) {
        return joinToNull(
                "Du",
                verb.getDuForm(),
                describableDat.dat(),
                joinToNull(modalpartikeln),
                describableAkk.akk(),
                verb.getPartikel());
    }

    @Override
    public boolean duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen() {
        return true;
    }

    @Override
    public String getDescriptionDuHauptsatz(@Nonnull final AdverbialeAngabe adverbialeAngabe) {
        if (verb.getPartikel() == null) {
            return capitalize(adverbialeAngabe.getText()) + // Aus Langeweile
                    " " + verb.getDuForm() +
                    " du " +
                    describableDat.dat() +
                    " " + describableAkk.akk();
        }

        return capitalize(adverbialeAngabe.getText()) + // Aus Langeweile
                " " + verb.getDuForm() +
                " du " +
                describableDat.dat() +
                " " + describableAkk.akk() +
                " " + verb.getPartikel();
    }

    @Override
    public String getDescriptionInfinitiv(final Person person, final Numerus numerus) {
        return describableDat.dat() +
                " " + describableAkk.akk() +
                " " + verb.getInfinitiv();
    }

    @Override
    public String getDescriptionZuInfinitiv(final Person person, final Numerus numerus) {
        return getDescriptionZuInfinitiv(person, numerus, null);
    }

    @Override
    public String getDescriptionZuInfinitiv(final Person person, final Numerus numerus,
                                            @Nullable final AdverbialeAngabe adverbialeAngabe) {
        return joinToNull(
                describableDat.dat(), // "dem Frosch"
                adverbialeAngabe, // "erneut"
                describableAkk.akk(), // "das Gold"
                verb.getZuInfinitiv()); // "anzubieten"
    }
}
