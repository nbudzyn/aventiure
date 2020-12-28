package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import java.util.Collection;

import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.GermanUtil;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.KonstituentenNotFoundException;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;

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
    public Iterable<Konstituente> getDuHauptsatzMitVorfeld(final String vorfeld) {
        return Konstituente.joinToKonstituenten(
                GermanUtil.capitalize(vorfeld), // "Dann"
                verb.getDuFormOhnePartikel(), // "nimmst"
                "du",
                getMittelfeld(P2, SG), // "den Frosch" / "dich"
                verb.getPartikel(), // "mit"
                getNachfeld(P2, SG)); // "deswegen"
    }

    @Override
    public Iterable<Konstituente> getDuHauptsatzMitSpeziellemVorfeld() {
        @Nullable final Konstituente speziellesVorfeld = getSpeziellesVorfeld(P2, SG);
        if (speziellesVorfeld == null) {
            return getDuHauptsatz();
        }

        Iterable<Konstituente> neuesMittelfeld;
        Iterable<Konstituente> neuesNachfeld;
        try {
            neuesMittelfeld = Konstituente.cutFirst(getMittelfeld(P2, SG), speziellesVorfeld);
            neuesNachfeld = getNachfeld(P2, SG);
        } catch (final KonstituentenNotFoundException e) {
            neuesMittelfeld = getMittelfeld(P2, SG);
            neuesNachfeld = Konstituente.cutFirst(getNachfeld(P2, SG), speziellesVorfeld);
        }

        return Konstituente.capitalize(
                Konstituente.joinToKonstituenten(
                        speziellesVorfeld, // "Den Frosch"
                        verb.getDuFormOhnePartikel(), // "nimmst"
                        "du",
                        neuesMittelfeld,
                        verb.getPartikel(), // "mit"
                        neuesNachfeld)); // "deswegen"
    }

    @Override
    public Iterable<Konstituente> getDuHauptsatz() {
        return getDuHauptsatz(new Modalpartikel[0]);
    }

    @Override
    public Iterable<Konstituente> getDuHauptsatz(final Collection<Modalpartikel> modalpartikeln) {
        if (adverbialeAngabeSkopusSatz != null) {
            Iterable<Konstituente> neuesMittelfeld;
            Iterable<Konstituente> neuesNachfeld;
            try {
                neuesMittelfeld = Konstituente.cutFirst(
                        getMittelfeld(P2, SG),
                        adverbialeAngabeSkopusSatz.getDescription());
                neuesNachfeld = getNachfeld(P2, SG);
            } catch (final KonstituentenNotFoundException e) {
                neuesMittelfeld = getMittelfeld(P2, SG);
                neuesNachfeld = Konstituente
                        .cutFirst(getNachfeld(P2, SG), adverbialeAngabeSkopusSatz.getDescription());
            }

            return Konstituente.capitalize(
                    Konstituente.joinToKonstituenten(
                            adverbialeAngabeSkopusSatz.getDescription(),
                            verb.getDuFormOhnePartikel(),
                            "du",
                            neuesMittelfeld,
                            verb.getPartikel(),
                            neuesNachfeld));
        }

        return Konstituente.joinToKonstituenten(
                "Du",
                getDuSatzanschlussOhneSubjekt(modalpartikeln));
    }

    @Override
    public Iterable<Konstituente> getDuSatzanschlussOhneSubjekt(
            final Collection<Modalpartikel> modalpartikeln) {
        return Konstituente.joinToKonstituenten(
                verb.getDuFormOhnePartikel(),
                getMittelfeld(modalpartikeln, P2, SG),
                verb.getPartikel(),
                getNachfeld(P2, SG));
    }

    @Override
    public Iterable<Konstituente> getDuSatzanschlussOhneSubjekt() {
        return PraedikatOhneLeerstellen.super.getDuSatzanschlussOhneSubjekt();
    }

    @Override
    public Iterable<Konstituente> getVerbzweit(final Person person, final Numerus numerus) {
        return Konstituente.joinToKonstituenten(
                verb.getPraesensOhnePartikel(person, numerus),
                getMittelfeld(person, numerus),
                verb.getPartikel(),
                getNachfeld(person, numerus));
    }

    @Override
    public Iterable<Konstituente> getVerbletzt(final Person person, final Numerus numerus) {
        return Konstituente.joinToKonstituenten(
                getMittelfeld(person, numerus),
                verb.getPraesensMitPartikel(person, numerus),
                getNachfeld(person, numerus));
    }

    @Override
    public Iterable<Konstituente> getPartizipIIPhrase(final Person person, final Numerus numerus) {
        return Konstituente.joinToKonstituenten(
                getMittelfeld(person, numerus),
                verb.getPartizipII(),
                getNachfeld(person, numerus));
    }

    /**
     * Gibt eine Infinitivkonstruktion zurück mit Prädikat
     * ("den Frosch ignorieren", "das Leben genießen")
     */
    @Override
    public Iterable<Konstituente> getInfinitiv(final Person person, final Numerus numerus) {
        return Konstituente.joinToKonstituenten(
                getMittelfeld(person, numerus),
                verb.getInfinitiv(),
                getNachfeld(person, numerus));
    }

    /**
     * Gibt eine Infinitivkonstruktion zurück mit Prädikat
     * ("den Frosch erneut zu ignorieren", "das Leben zu genießen")
     */
    @Override
    public Iterable<Konstituente> getZuInfinitiv(final Person person, final Numerus numerus) {
        return Konstituente.joinToKonstituenten(
                getMittelfeld(person, numerus),
                verb.getZuInfinitiv(),
                getNachfeld(person, numerus));
    }

    @Override
    @Nullable
    public Konstituente getSpeziellesVorfeld(final Person person,
                                             final Numerus numerus) {
        if (adverbialeAngabeSkopusSatz != null) {
            return adverbialeAngabeSkopusSatz.getDescription();
        }

        // FIXME getAdverbialeAngabeSkopusVerbAllg() - aber nur, wenn
        //  sie im Mittelfeld nicht erlaubt ist. Dann müssen die Aufrufer das spezielle
        //  Vorfeld nicht nur aus dem Mittelfeld, sondern auch aus dem Nachfeld
        //  herausschneiden! Außerdem darf das Vorkomme nur gesetzt werden, wenn
        //  kein neuer Satz anfängt und auch vorher kein Komma o.Ä. stand.

        return null;
    }

    @Nullable
    private Iterable<Konstituente> getMittelfeld(final Person personSubjekt,
                                                 final Numerus numerusSubjekt) {
        return getMittelfeld(personSubjekt, numerusSubjekt, new Modalpartikel[0]);
        // "den Frosch" oder "sich" / "mich"
    }

    private Iterable<Konstituente> getMittelfeld(final Person personSubjekt,
                                                 final Numerus numerusSubjekt,
                                                 final Modalpartikel... modalpartikeln) {
        return getMittelfeld(asList(modalpartikeln), personSubjekt, numerusSubjekt);
    }

    public abstract Iterable<Konstituente> getMittelfeld(
            final Collection<Modalpartikel> modalpartikeln,
            Person personSubjekt,
            Numerus numerusSubjekt);

    @Override
    public boolean bildetPerfektMitSein() {
        return verb.getPerfektbildung() == Perfektbildung.SEIN;
    }

    @NonNull
    protected Verb getVerb() {
        return verb;
    }

    @Nullable
    Konstituente getAdverbialeAngabeSkopusSatzDescription() {
        return adverbialeAngabeSkopusSatz != null ? adverbialeAngabeSkopusSatz.getDescription() :
                null;
    }

    @Nullable
    Konstituente getAdverbialeAngabeSkopusVerbAllgDescriptionFuerMittelfeld() {
        if (adverbialeAngabeSkopusVerbAllg == null) {
            return null;
        }

        if (!adverbialeAngabeSkopusVerbAllg.imMittelfeldErlaubt()) {
            return null;
        }

        return getAdverbialeAngabeSkopusVerbAllgDescription();
    }

    @Nullable
    Konstituente getAdverbialeAngabeSkopusVerbAllgDescriptionFuerZwangsausklammerung() {
        if (adverbialeAngabeSkopusVerbAllg == null) {
            return null;
        }

        if (adverbialeAngabeSkopusVerbAllg.imMittelfeldErlaubt()) {
            return null;
        }

        return getAdverbialeAngabeSkopusVerbAllgDescription();
    }


    @Nullable
    private Konstituente getAdverbialeAngabeSkopusVerbAllgDescription() {
        return adverbialeAngabeSkopusVerbAllg != null ?
                adverbialeAngabeSkopusVerbAllg.getDescription() : null;
    }

    @Nullable
    Konstituente getAdverbialeAngabeSkopusVerbWohinWoherDescription() {
        return adverbialeAngabeSkopusVerbWohinWoher != null ?
                adverbialeAngabeSkopusVerbWohinWoher.getDescription() : null;
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
