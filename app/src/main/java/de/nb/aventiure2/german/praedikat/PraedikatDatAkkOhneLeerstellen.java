package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import java.util.Collection;

import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static de.nb.aventiure2.german.base.GermanUtil.joinToNull;

/**
 * Ein Prädikat, in dem ein Dativobjekt und Akkusativobjekt gesetzt sind
 * und es auch sonst keine Leerstellen gibt. Beispiel: "dem Frosch Angebote machen"
 */
public class PraedikatDatAkkOhneLeerstellen extends AbstractPraedikatOhneLeerstellen {
    /**
     * Das (Objekt / Wesen / Konzept für das) Dativobjekt (z.B. "Angebote")
     */
    @NonNull
    private final SubstantivischePhrase describableDat;

    /**
     * Das (Objekt / Wesen / Konzept für das) Akkusativobjekte (z.B. der Frosch)
     */
    @NonNull
    private final SubstantivischePhrase describableAkk;

    public PraedikatDatAkkOhneLeerstellen(final Verb verb,
                                          final SubstantivischePhrase describableDat,
                                          final SubstantivischePhrase describableAkk) {
        super(verb);
        this.describableDat = describableDat;
        this.describableAkk = describableAkk;
    }

    @Override
    public boolean duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen() {
        return true;
    }

    @Override
    public @Nullable
    String getSpeziellesVorfeld() {
        return null; // "Die Kugel gibst du dem Frosch" - nicht schön
    }

    @Override
    public String getMittelfeld(final Collection<Modalpartikel> modalpartikeln) {
        return joinToNull(
                describableDat.dat(),
                joinToNull(modalpartikeln),
                describableAkk.akk());
    }

    @Override
    public String getInfinitiv(final Person person, final Numerus numerus,
                               @Nullable final AdverbialeAngabe adverbialeAngabe) {
        return joinToNull(
                describableDat.dat(), // "dem Frosch"
                adverbialeAngabe, // "erneut"
                describableAkk.akk(), // "das Gold"
                getVerb().getInfinitiv()); // "anbieten"
    }

    @Override
    public String getZuInfinitiv(final Person person, final Numerus numerus,
                                 @Nullable final AdverbialeAngabe adverbialeAngabe) {
        return joinToNull(
                describableDat.dat(), // "dem Frosch"
                adverbialeAngabe, // "erneut"
                describableAkk.akk(), // "das Gold"
                getVerb().getZuInfinitiv()); // "anzubieten"
    }

    @Override
    public String getNachfeld() {
        return null;
    }
}
