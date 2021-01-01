package de.nb.aventiure2.german.praedikat;

import android.util.Pair;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Objects;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.GermanUtil;
import de.nb.aventiure2.german.base.Kasus;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.KonstituentenNotFoundException;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.SubstantivischePhraseOderReflexivpronomen;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static de.nb.aventiure2.german.base.Konstituente.cutFirstOneByOne;
import static de.nb.aventiure2.german.base.Konstituente.k;
import static de.nb.aventiure2.german.base.Numerus.SG;
import static de.nb.aventiure2.german.base.Person.P2;

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

    private final ImmutableList<Modalpartikel> modalpartikeln;

    @Nullable
    private final AdverbialeAngabeSkopusSatz adverbialeAngabeSkopusSatz;

    @Nullable
    private final AdverbialeAngabeSkopusVerbAllg adverbialeAngabeSkopusVerbAllg;

    @Nullable
    private final AdverbialeAngabeSkopusVerbWohinWoher adverbialeAngabeSkopusVerbWohinWoher;

    public AbstractAngabenfaehigesPraedikatOhneLeerstellen(final Verb verb) {
        this(verb, ImmutableList.of(), null, null, null);
    }

    AbstractAngabenfaehigesPraedikatOhneLeerstellen(
            final Verb verb,
            final Iterable<Modalpartikel> modalpartikeln,
            @Nullable final AdverbialeAngabeSkopusSatz adverbialeAngabeSkopusSatz,
            @Nullable final AdverbialeAngabeSkopusVerbAllg adverbialeAngabeSkopusVerbAllg,
            @Nullable
            final AdverbialeAngabeSkopusVerbWohinWoher adverbialeAngabeSkopusVerbWohinWoher) {
        this.verb = verb;
        this.modalpartikeln = ImmutableList.copyOf(modalpartikeln);
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
    public final Iterable<Konstituente> getDuHauptsatzMitVorfeld(final String vorfeld) {
        return Konstituente.joinToKonstituenten(
                GermanUtil.capitalize(vorfeld), // "Dann"
                verb.getDuFormOhnePartikel(), // "nimmst"
                "du",
                getMittelfeld(P2, SG), // "den Frosch" / "dich"
                verb.getPartikel(), // "mit"
                getNachfeld(P2, SG)); // "deswegen"
    }

    @Override
    public final Iterable<Konstituente> getDuHauptsatzMitSpeziellemVorfeld() {
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
    public final Iterable<Konstituente> getDuHauptsatz() {
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
                getDuSatzanschlussOhneSubjekt());
    }

    @Override
    public final Iterable<Konstituente> getDuSatzanschlussOhneSubjekt() {
        return Konstituente.joinToKonstituenten(
                verb.getDuFormOhnePartikel(),
                getMittelfeld(P2, SG),
                verb.getPartikel(),
                getNachfeld(P2, SG));
    }

    @Override
    public final Iterable<Konstituente> getVerbzweit(final Person person, final Numerus numerus) {
        return Konstituente.joinToKonstituenten(
                verb.getPraesensOhnePartikel(person, numerus),
                getMittelfeld(person, numerus),
                verb.getPartikel(),
                getNachfeld(person, numerus));
    }

    @Override
    public final Iterable<Konstituente> getVerbletzt(final Person person, final Numerus numerus) {
        return Konstituente.joinToKonstituenten(
                getMittelfeld(person, numerus),
                verb.getPraesensMitPartikel(person, numerus),
                getNachfeld(person, numerus));
    }

    @Override
    public final Iterable<Konstituente> getPartizipIIPhrase(final Person person,
                                                            final Numerus numerus) {
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
    public final Iterable<Konstituente> getInfinitiv(final Person person, final Numerus numerus) {
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
    public final Iterable<Konstituente> getZuInfinitiv(final Person person, final Numerus numerus) {
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

        @Nullable final Konstituente
                adverbialeAngabeSkopusVerbAllgDescriptionFuerZwangsausklammerung =
                getAdverbialeAngabeSkopusVerbAllgDescriptionFuerZwangsausklammerung();
        if (adverbialeAngabeSkopusVerbAllgDescriptionFuerZwangsausklammerung != null) {
            // "Und glücklich, sie endlich gefunden zu haben, nimmst du die Kugel."
            return adverbialeAngabeSkopusVerbAllgDescriptionFuerZwangsausklammerung
                    .withVorkommaNoetig(false);
        }

        return null;
    }

    // FIXME Prüfen, ob das Mittelfeld VOR DER WACKERNAGELPOSITION (nicht-pronominales Subjekt)
    //  von den Aufrufern korrekt hinzugefügt wird!
    private final Iterable<Konstituente> getMittelfeld(
            final Person personSubjekt,
            final Numerus numerusSubjekt) {
        @Nullable final SubstantivischePhraseOderReflexivpronomen datObjekt = getDat();
        final SubstantivischePhraseOderReflexivpronomen akkObjekt = getAkk();

        // einige wenige Verben wie "jdn. etw. lehren" haben zwei Akkusativobjekte
        final SubstantivischePhraseOderReflexivpronomen zweitesAkkObjekt = getZweitesAkk();

        final ImmutableList<Konstituente> unbetontePronomen =
                filterUnbetontePronomen(
                        toPair(akkObjekt, Kasus.AKK),
                        toPair(zweitesAkkObjekt, Kasus.AKK),
                        toPair(datObjekt, Kasus.DAT));

        // Das Mittelfeld besteht aus drei Teilen:
        return Konstituente.joinToKonstituenten(
                // 1. Der Bereich vor der Wackernagel-Position. Dort kann höchstens ein
                //   Subjekt stehen, das keine unbetontes Pronomen ist.
                //   Das Subjekt ist hier im Prädikat noch nicht bekannt.
                // 2. Die Wackernagelposition. Hier stehen alle unbetonten Pronomen in den
                // reinen Kasus in der festen Reihenfolge Nom < Akk < Dat
                unbetontePronomen,
                // 3. Der Bereich nach der Wackernagel-Position. Hier steht alles übrige
                cutFirstOneByOne(
                        getMittelfeldOhneLinksversetzungUnbetonterPronomen(
                                personSubjekt, numerusSubjekt),
                        unbetontePronomen
                ));
    }

    /**
     * Gibt das Akkusativ-Objekt zurück - sofern es eines gibt.
     */
    @Nullable
    abstract SubstantivischePhraseOderReflexivpronomen getAkk();

    /**
     * Gibt das <i>zweite</i> Akkusativ-Objekt zurück - sofern es eines gibt.
     * <p>
     * Nur sehr wenige Verben fordern ein zweites Akkusativ-Objekt - z.B.
     * <i>jdn. etw. lehren</i>
     */
    @Nullable
    abstract SubstantivischePhraseOderReflexivpronomen getZweitesAkk();

    /**
     * Gibt das Dativ-Objekt zurück - sofern es eines gibt.
     */
    @Nullable
    abstract SubstantivischePhraseOderReflexivpronomen getDat();


    private static Pair<SubstantivischePhraseOderReflexivpronomen, Kasus> toPair(
            @Nullable final
            SubstantivischePhraseOderReflexivpronomen substantivischePhraseOderReflexivpronomen,
            final Kasus kasus) {
        if (substantivischePhraseOderReflexivpronomen == null) {
            return null;
        }

        return Pair.create(substantivischePhraseOderReflexivpronomen, kasus);
    }

    @SafeVarargs
    private static ImmutableList<Konstituente> filterUnbetontePronomen(
            final Pair<SubstantivischePhraseOderReflexivpronomen, Kasus>... substantivischePhrasenMitKasus) {
        return Stream.of(substantivischePhrasenMitKasus)
                .filter(Objects::nonNull)
                .filter(spk -> spk.first.isUnbetontesPronomen())
                .map(spk -> k(spk.first.im(spk.second)))
                .collect(toImmutableList());
    }

    /**
     * Gibt das Mittelfeld dieses Prädikats zurück. Dabei brauchen die unbetonten
     * Pronomen der Objekte noch nicht nach links versetzt zu sein - die Methode könnte also
     * etwas zurückgeben wie <i>dem Ork es geben</i>.
     * <p>
     * Die Linksversetzung der unbetonten Pronomen an die Wackernagel-Position geschieht durch
     * die Methode {@link #getMittelfeld(Person, Numerus)} auf Basis der Methoden
     * {@link #getAkk()}, {@link #getZweitesAkk()} und
     * {@link #getDat()}.
     */
    abstract Iterable<Konstituente>
    getMittelfeldOhneLinksversetzungUnbetonterPronomen(final Person personSubjekt,
                                                       final Numerus numerusSubjekt);

    @Override
    public boolean bildetPerfektMitSein() {
        return verb.getPerfektbildung() == Perfektbildung.SEIN;
    }

    @NonNull
    protected Verb getVerb() {
        return verb;
    }

    public ImmutableList<Modalpartikel> getModalpartikeln() {
        return modalpartikeln;
    }

    @Nullable
    Konstituente getAdverbialeAngabeSkopusSatzDescription() {
        return adverbialeAngabeSkopusSatz != null ?
                adverbialeAngabeSkopusSatz.getDescription() :
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

