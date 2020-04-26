package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import javax.annotation.Nonnull;

import de.nb.aventiure2.german.base.DeklinierbarePhrase;

import static de.nb.aventiure2.german.base.GermanUtil.capitalize;

/**
 * Ein Prädikat, in dem ein Dativobjekt und Akkusativobjekt gesetzt sind
 * und es auch sonst keine Leerstellen gibt.  Beispiel: "dem Frosch Angebote machen"
 */
public class PraedikatDatAkkOhneLeerstellen implements PraedikatOhneLeerstellen {
    /**
     * Infinitiv des Verbs ("machen")
     */
    @NonNull
    private final String infinitiv;

    /**
     * 2. Person Singular Präsens Indikativ des Verbs, ggf. ohne abgetrenntes Präfix
     * ("machst")
     */
    @NonNull
    private final String duForm;

    /**
     * Ggf. das abgetrennte Präfix des Verbs.
     * <p>
     * Wird das Präfix <i>nicht</i> abgetrennt ("ver"), ist dieses Feld <code>null</code>.
     */
    @Nullable
    private final String abgetrenntesPraefix;

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

    public PraedikatDatAkkOhneLeerstellen(final String infinitiv, final String duForm,
                                          final String abgetrenntesPraefix,
                                          final DeklinierbarePhrase describableDat,
                                          final DeklinierbarePhrase describableAkk) {
        this.infinitiv = infinitiv;
        this.duForm = duForm;
        this.abgetrenntesPraefix = abgetrenntesPraefix;
        this.describableDat = describableDat;
        this.describableAkk = describableAkk;
    }

    /**
     * Gibt einen Satz mit diesem Prädikat zurück.
     * ("Du machst dem Frosch Angebote")
     */
    @Override
    public String getDescriptionDuHauptsatz() {
        if (abgetrenntesPraefix == null) {
            return "Du " + duForm +
                    " " + describableDat.dat() +
                    " " + describableAkk.akk();
        }

        return "Du " + duForm +
                " " + describableDat.dat() +
                " " + describableAkk.akk() +
                " " + abgetrenntesPraefix;
    }

    @Override
    public boolean duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen() {
        return true;
    }

    @Override
    public String getDescriptionDuHauptsatz(@Nonnull final AdverbialeAngabe adverbialeAngabe) {
        if (abgetrenntesPraefix == null) {
            return capitalize(adverbialeAngabe.getText()) + // Aus Langeweile
                    " " + duForm +
                    " du " +
                    describableDat.dat() +
                    " " + describableAkk.akk();
        }

        return capitalize(adverbialeAngabe.getText()) + // Aus Langeweile
                " " + duForm +
                " du " +
                describableDat.dat() +
                " " + describableAkk.akk() +
                " " + abgetrenntesPraefix;
    }

    /**
     * Gibt eine Infinitivkonstruktion mit diesem Prädikat zurück.
     * ("Dem Frosch Angebote machen")
     */
    @Override
    public String getDescriptionInfinitiv() {
        return capitalize(describableDat.dat()) +
                " " + describableAkk.akk() +
                " " + infinitiv;
    }
}
