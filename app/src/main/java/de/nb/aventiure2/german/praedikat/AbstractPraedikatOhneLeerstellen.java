package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import java.util.Collection;

import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.GermanUtil;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;

import static de.nb.aventiure2.german.base.GermanUtil.capitalize;
import static de.nb.aventiure2.german.base.GermanUtil.joinToNull;
import static java.util.Arrays.asList;

/**
 * Ein Prädikat, in dem alle Leerstellen besetzt sind. Beispiele:
 * <ul>
 *     <li>"das Buch nehmen"
 *     <li>"dem Frosch Angebote machen!"
 * </ul>
 * <p>
 * Adverbiale Angaben ("aus Langeweile") können immer noch eingefügt werden.
 */
public abstract class AbstractPraedikatOhneLeerstellen
        implements PraedikatOhneLeerstellen {
    /**
     * Das Verb an sich, ohne Informationen zur Valenz, ohne Ergänzungen, ohne
     * Angaben
     */
    @NonNull
    private final Verb verb;

    @Nullable
    private final AdverbialeAngabeSkopusSatz adverbialeAngabeSkopusSatz;

    @Nullable
    private final AdverbialeAngabeSkopusVerbAllg adverbialeAngabeSkopusVerbAllg;

    @Nullable
    private final AdverbialeAngabeSkopusVerbWohinWoher adverbialeAngabeSkopusVerbWohinWoher;

    public AbstractPraedikatOhneLeerstellen(final Verb verb) {
        this(verb, null, null, null);
    }

    AbstractPraedikatOhneLeerstellen(
            final Verb verb,
            @Nullable final AdverbialeAngabeSkopusSatz adverbialeAngabeSkopusSatz,
            @Nullable final AdverbialeAngabeSkopusVerbAllg adverbialeAngabeSkopusVerbAllg,
            @Nullable
            final AdverbialeAngabeSkopusVerbWohinWoher adverbialeAngabeSkopusVerbWohinWoher) {
        this.verb = verb;
        this.adverbialeAngabeSkopusSatz = adverbialeAngabeSkopusSatz;
        this.adverbialeAngabeSkopusVerbAllg = adverbialeAngabeSkopusVerbAllg;
        this.adverbialeAngabeSkopusVerbWohinWoher = adverbialeAngabeSkopusVerbWohinWoher;
    }

    @Override
    public String getDuHauptsatzMitKonjunktionaladverb(final String konjunktionaladverb) {
        return joinToNull(
                capitalize(konjunktionaladverb), // "dann"
                verb.getDuForm(), // "nimmst"
                "du",
                getMittelfeld(), // "den Frosch"
                verb.getPartikel(), // "mit"
                getNachfeld()); // "deswegen"
    }

    @Override
    public String getDuHauptsatzMitSpeziellemVorfeld() {
        return capitalize(
                joinToNull(
                        getSpeziellesVorfeld(), // "Den Frosch"
                        verb.getDuForm(), // "nimmst"
                        "du",
                        getMittelfeldOhneSpeziellesVorfeld(),
                        verb.getPartikel(), // "mit"
                        getNachfeld())); // "deswegen"
    }

    @Override
    public String getDuHauptsatz() {
        return getDuHauptsatz(new Modalpartikel[0]);
    }

    @Override
    public String getDuHauptsatz(final Collection<Modalpartikel> modalpartikeln) {
        if (adverbialeAngabeSkopusSatz != null) {
            return joinToNull(
                    capitalize(adverbialeAngabeSkopusSatz.getText()),
                    verb.getDuForm(),
                    "du",
                    GermanUtil.cutSatzglied(getMittelfeld(), adverbialeAngabeSkopusSatz.getText()),
                    verb.getPartikel(),
                    getNachfeld());
        }

        return "Du " + getDuSatzanschlussOhneSubjekt(modalpartikeln);
    }

    @Override
    public String getDuSatzanschlussOhneSubjekt(final Collection<Modalpartikel> modalpartikeln) {
        return joinToNull(
                verb.getDuForm(),
                getMittelfeld(modalpartikeln),
                verb.getPartikel(),
                getNachfeld());
    }

    @Override
    public String getDuSatzanschlussOhneSubjekt() {
        return PraedikatOhneLeerstellen.super.getDuSatzanschlussOhneSubjekt();
    }

    /**
     * Gibt eine Infinitivkonstruktion zurück mit Prädikat.
     * ("den Frosch ignorieren", "das Leben genießen")
     */
    @Override
    public String getInfinitiv(final Person person, final Numerus numerus) {
        return joinToNull(getMittelfeld(),
                verb.getInfinitiv(),
                getNachfeld());
    }

    /**
     * Gibt eine Infinitivkonstruktion zurück mit Prädikat.
     * ("den Frosch erneut zu ignorieren", "das Leben zu genießen")
     */
    @Override
    public String getZuInfinitiv(final Person person, final Numerus numerus) {
        return joinToNull(getMittelfeld(),
                verb.getZuInfinitiv(),
                getNachfeld());
    }

    @Nullable
    public String getSpeziellesVorfeld() {
        if (adverbialeAngabeSkopusSatz != null) {
            return adverbialeAngabeSkopusSatz.getText();
        }

        if (adverbialeAngabeSkopusVerbAllg != null) {
            return adverbialeAngabeSkopusVerbAllg.getText();
        }

        return null;
    }

    public @Nullable
    String getMittelfeld() {
        return getMittelfeld(new Modalpartikel[0]); // "den Frosch"
    }

    public String getMittelfeld(final Modalpartikel... modalpartikeln) {
        return getMittelfeld(asList(modalpartikeln));
    }

    public abstract String getMittelfeld(final Collection<Modalpartikel> modalpartikeln);

    @Nullable
    public String getMittelfeldOhneSpeziellesVorfeld() {
        return GermanUtil.cutSatzglied(getMittelfeld(), getSpeziellesVorfeld());
    }

    public abstract String getNachfeld();

    @NonNull
    protected Verb getVerb() {
        return verb;
    }

    @Nullable
    public AdverbialeAngabeSkopusSatz getAdverbialeAngabeSkopusSatz() {
        return adverbialeAngabeSkopusSatz;
    }

    @Nullable
    public AdverbialeAngabeSkopusVerbAllg getAdverbialeAngabeSkopusVerbAllg() {
        return adverbialeAngabeSkopusVerbAllg;
    }

    @Nullable
    public AdverbialeAngabeSkopusVerbWohinWoher getAdverbialeAngabeSkopusVerbWohinWoher() {
        return adverbialeAngabeSkopusVerbWohinWoher;
    }
}
