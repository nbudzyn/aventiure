package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import java.util.Collection;

import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.GermanUtil;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.Wortfolge;

import static de.nb.aventiure2.german.base.GermanUtil.capitalize;
import static de.nb.aventiure2.german.base.GermanUtil.joinToNull;
import static de.nb.aventiure2.german.base.GermanUtil.joinToNullString;
import static de.nb.aventiure2.german.base.Numerus.SG;
import static de.nb.aventiure2.german.base.Person.P2;
import static java.util.Arrays.asList;

/**
 * Ein Prädikat, in dem alle Leerstellen besetzt sind und dem grundsätzlich
 * "eigene" adverbiale Angaben ("aus Langeweile") immer noch hinzugefügt werden können. Beispiele:
 * <ul>
 *     <li>"das Buch nehmen"
 *     <li>"dem Frosch Angebote machen"
 *     <li>"dem Frosch endlich Angebote machen"
 * </ul>
 * <p>
 */
public abstract class AbstractAngabenfaehigesPraedikatOhneLeerstellen
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

    public AbstractAngabenfaehigesPraedikatOhneLeerstellen(final Verb verb) {
        this(verb, null, null, null);
    }

    AbstractAngabenfaehigesPraedikatOhneLeerstellen(
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


    /**
     * Erzeugt aus diesem Prädikat ein zu-haben-Prädikat
     * (z.B. <i>Spannendes zu berichten haben</i>,  <i>mit Paul zu diskutieren haben/i>,
     * <i>zu schlafen haben</i>, <i>sich zu waschen haben</i>).
     */
    public ZuHabenPraedikatOhneLeerstellen zuHabenPraedikat() {
        return new ZuHabenPraedikatOhneLeerstellen(this);
    }

    @Override
    public Wortfolge getDuHauptsatzMitVorfeld(final String vorfeld) {
        return joinToNull(
                capitalize(vorfeld), // "dann"
                verb.getDuFormOhnePartikel(), // "nimmst"
                "du",
                getMittelfeld(P2, SG), // "den Frosch" / "dich"
                verb.getPartikel(), // "mit"
                getNachfeld(P2, SG)); // "deswegen"
    }

    @Override
    public Wortfolge getDuHauptsatzMitSpeziellemVorfeld() {
        @Nullable final String speziellesVorfeld = getSpeziellesVorfeld();
        if (speziellesVorfeld == null) {
            return getDuHauptsatz();
        }

        return capitalize(
                joinToNull(
                        speziellesVorfeld, // "Den Frosch"
                        verb.getDuFormOhnePartikel(), // "nimmst"
                        "du",
                        getMittelfeldOhneSpeziellesVorfeld(P2, SG),
                        verb.getPartikel(), // "mit"
                        getNachfeld(P2, SG))); // "deswegen"
    }

    @Override
    public Wortfolge getDuHauptsatz() {
        return getDuHauptsatz(new Modalpartikel[0]);
    }

    @Override
    public Wortfolge getDuHauptsatz(final Collection<Modalpartikel> modalpartikeln) {
        if (adverbialeAngabeSkopusSatz != null) {
            return joinToNull(
                    capitalize(adverbialeAngabeSkopusSatz.getText()),
                    verb.getDuFormOhnePartikel(),
                    "du",
                    GermanUtil.cutSatzglied(
                            getMittelfeld(P2, SG), adverbialeAngabeSkopusSatz.getText()),
                    verb.getPartikel(),
                    getNachfeld(P2, SG));
        }

        return joinToNull(
                "Du",
                getDuSatzanschlussOhneSubjekt(modalpartikeln));
    }

    @Override
    public Wortfolge getDuSatzanschlussOhneSubjekt(final Collection<Modalpartikel> modalpartikeln) {
        return joinToNull(
                verb.getDuFormOhnePartikel(),
                getMittelfeld(modalpartikeln, P2, SG),
                verb.getPartikel(),
                getNachfeld(P2, SG));
    }

    @Override
    public Wortfolge getDuSatzanschlussOhneSubjekt() {
        return PraedikatOhneLeerstellen.super.getDuSatzanschlussOhneSubjekt();
    }

    @Override
    public Wortfolge getVerbzweit(final Person person, final Numerus numerus) {
        return joinToNull(
                verb.getPraesensOhnePartikel(person, numerus),
                getMittelfeld(person, numerus),
                verb.getPartikel(),
                getNachfeld(person, numerus));
    }

    @Override
    public Wortfolge getVerbletzt(final Person person, final Numerus numerus) {
        return joinToNull(
                getMittelfeld(person, numerus),
                verb.getPraesensMitPartikel(person, numerus),
                getNachfeld(person, numerus));
    }

    @Override
    public String getPartizipIIPhrase(final Person person, final Numerus numerus) {
        return joinToNullString(getMittelfeld(person, numerus),
                verb.getPartizipII(),
                getNachfeld(person, numerus));
    }

    /**
     * Gibt eine Infinitivkonstruktion zurück mit Prädikat
     * ("den Frosch ignorieren", "das Leben genießen")
     */
    @Override
    public String getInfinitiv(final Person person, final Numerus numerus) {
        return joinToNullString(getMittelfeld(person, numerus),
                verb.getInfinitiv(),
                getNachfeld(person, numerus));
    }

    /**
     * Gibt eine Infinitivkonstruktion zurück mit Prädikat
     * ("den Frosch erneut zu ignorieren", "das Leben zu genießen")
     */
    @Override
    public String getZuInfinitiv(final Person person, final Numerus numerus) {
        return joinToNullString(getMittelfeld(person, numerus),
                verb.getZuInfinitiv(),
                getNachfeld(person, numerus));
    }

    @Override
    @Nullable
    public String getSpeziellesVorfeld() {
        if (adverbialeAngabeSkopusSatz != null) {
            return adverbialeAngabeSkopusSatz.getText();
        }

        return null;
    }

    @Nullable
    private Wortfolge getMittelfeld(final Person personSubjekt,
                                    final Numerus numerusSubjekt) {
        return getMittelfeld(personSubjekt, numerusSubjekt, new Modalpartikel[0]);
        // "den Frosch" oder "sich" / "mich"
    }

    private Wortfolge getMittelfeld(final Person personSubjekt,
                                    final Numerus numerusSubjekt,
                                    final Modalpartikel... modalpartikeln) {
        return getMittelfeld(asList(modalpartikeln), personSubjekt, numerusSubjekt);
    }

    public abstract Wortfolge getMittelfeld(final Collection<Modalpartikel> modalpartikeln,
                                            Person personSubjekt,
                                            Numerus numerusSubjekt);

    @Nullable
    private Wortfolge getMittelfeldOhneSpeziellesVorfeld(
            final Person personSubjekt, final Numerus numerusSubjekt) {
        return GermanUtil.cutSatzglied(
                getMittelfeld(personSubjekt, numerusSubjekt),
                getSpeziellesVorfeld());
    }

    @Override
    public boolean bildetPerfektMitSein() {
        return verb.getPerfektbildung() == Perfektbildung.SEIN;
    }

    @NonNull
    protected Verb getVerb() {
        return verb;
    }

    @Nullable
    AdverbialeAngabeSkopusSatz getAdverbialeAngabeSkopusSatz() {
        return adverbialeAngabeSkopusSatz;
    }

    @Nullable
    AdverbialeAngabeSkopusVerbAllg getAdverbialeAngabeSkopusVerbAllg() {
        return adverbialeAngabeSkopusVerbAllg;
    }

    @Nullable
    public AdverbialeAngabeSkopusVerbWohinWoher getAdverbialeAngabeSkopusVerbWohinWoher() {
        return adverbialeAngabeSkopusVerbWohinWoher;
    }

    @Override
    public boolean umfasstSatzglieder() {
        return adverbialeAngabeSkopusSatz != null ||
                adverbialeAngabeSkopusVerbAllg != null ||
                adverbialeAngabeSkopusVerbWohinWoher != null;
    }

    @Override
    public boolean isBezugAufNachzustandDesAktantenGegeben() {
        // Bei "weggehen" ist ein Bezug auf den Nachzustand des Aktanten gegeben.
        return verb.isPartikelverb() ||
                // Auch bei "nach Berlin gehen" ist ein Bezug auf den Nachzustand des
                // Aktanten gegeben.
                adverbialeAngabeSkopusVerbWohinWoher != null;

        // Sonst ("gehen", "endlich gehen") eher nicht.
    }

}
