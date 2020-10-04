package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import java.util.Collection;

import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.GermanUtil;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;

import static de.nb.aventiure2.german.base.GermanUtil.capitalize;
import static de.nb.aventiure2.german.base.GermanUtil.joinToNull;
import static de.nb.aventiure2.german.base.Numerus.SG;
import static de.nb.aventiure2.german.base.Person.P2;
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
    public String getDuHauptsatzMitVorfeld(final String vorfeld) {
        return joinToNull(
                capitalize(vorfeld), // "dann"
                verb.getDuForm(), // "nimmst"
                "du",
                getMittelfeld(P2, SG), // "den Frosch" / "dich"
                verb.getPartikel(), // "mit"
                getNachfeld(P2, SG)); // "deswegen"
    }

    @Override
    public String getDuHauptsatzMitSpeziellemVorfeld() {
        @Nullable final String speziellesVorfeld = getSpeziellesVorfeld();
        if (speziellesVorfeld == null) {
            return getDuHauptsatz();
        }

        return capitalize(
                joinToNull(
                        speziellesVorfeld, // "Den Frosch"
                        verb.getDuForm(), // "nimmst"
                        "du",
                        getMittelfeldOhneSpeziellesVorfeld(P2, SG),
                        verb.getPartikel(), // "mit"
                        getNachfeld(P2, SG))); // "deswegen"
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
                    GermanUtil.cutSatzglied(
                            getMittelfeld(P2, SG), adverbialeAngabeSkopusSatz.getText()),
                    verb.getPartikel(),
                    getNachfeld(P2, SG));
        }

        return "Du " + getDuSatzanschlussOhneSubjekt(modalpartikeln);
    }

    @Override
    public String getDuSatzanschlussOhneSubjekt(final Collection<Modalpartikel> modalpartikeln) {
        return joinToNull(
                verb.getDuForm(),
                getMittelfeld(modalpartikeln, P2, SG),
                verb.getPartikel(),
                getNachfeld(P2, SG));
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
        return joinToNull(getMittelfeld(person, numerus),
                verb.getInfinitiv(),
                getNachfeld(person, numerus));
    }

    /**
     * Gibt eine Infinitivkonstruktion zurück mit Prädikat.
     * ("den Frosch erneut zu ignorieren", "das Leben zu genießen")
     */
    @Override
    public String getZuInfinitiv(final Person person, final Numerus numerus) {
        return joinToNull(getMittelfeld(person, numerus),
                verb.getZuInfinitiv(),
                getNachfeld(person, numerus));
    }

    @Override
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
    String getMittelfeld(final Person personSubjekt,
                         final Numerus numerusSubjekt) {
        return getMittelfeld(personSubjekt, numerusSubjekt, new Modalpartikel[0]);
        // "den Frosch" oder "sich" / "mich"
    }

    public String getMittelfeld(final Person personSubjekt,
                                final Numerus numerusSubjekt,
                                final Modalpartikel... modalpartikeln) {
        return getMittelfeld(asList(modalpartikeln), personSubjekt, numerusSubjekt);
    }

    public abstract String getMittelfeld(final Collection<Modalpartikel> modalpartikeln,
                                         Person personSubjekt,
                                         Numerus numerusSubjekt);

    @Nullable
    public String getMittelfeldOhneSpeziellesVorfeld(
            final Person personSubjekt, final Numerus numerusSubjekt) {
        return GermanUtil.cutSatzglied(
                getMittelfeld(personSubjekt, numerusSubjekt),
                getSpeziellesVorfeld());
    }

    public abstract String getNachfeld(Person personSubjekt,
                                       Numerus numerusSubjekt);

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
