package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;

import com.google.common.collect.ImmutableList;

import java.util.Objects;
import java.util.stream.Stream;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativ;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativSkopusSatz;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativVerbAllg;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativWohinWoher;
import de.nb.aventiure2.german.base.IInterrogativadverb;
import de.nb.aventiure2.german.base.Kasus;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.SubstPhrOderReflexivpronomen;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static de.nb.aventiure2.german.base.Konstituentenfolge.cutFirstOneByOne;
import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToKonstituentenfolge;
import static de.nb.aventiure2.german.base.Konstituentenfolge.kf;
import static de.nb.aventiure2.german.base.Numerus.SG;
import static de.nb.aventiure2.german.base.Person.P2;
import static de.nb.aventiure2.german.base.Person.P3;
import static java.util.Objects.requireNonNull;

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

    /**
     * Gibt an, ob dieses Prädikat in der Regel ohne Subjekt steht
     * ("Mich friert"), aber optional ein expletives "es" möglich ist
     * ("Es friert mich").
     */
    private final boolean inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich;

    private final ImmutableList<Modalpartikel> modalpartikeln;

    @Nullable
    private final IAdvAngabeOderInterrogativSkopusSatz advAngabeSkopusSatz;

    @Nullable
    private final IAdvAngabeOderInterrogativVerbAllg advAngabeSkopusVerbAllg;

    @Nullable
    private final IAdvAngabeOderInterrogativWohinWoher advAngabeSkopusVerbWohinWoher;

    public AbstractAngabenfaehigesPraedikatOhneLeerstellen(
            final Verb verb,
            final boolean inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich) {
        this(verb, inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich, ImmutableList.of(),
                null, null, null);
    }

    AbstractAngabenfaehigesPraedikatOhneLeerstellen(
            final Verb verb,
            final Iterable<Modalpartikel> modalpartikeln,
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabeSkopusSatz,
            @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabeSkopusVerbAllg,
            @Nullable final IAdvAngabeOderInterrogativWohinWoher advAngabeSkopusVerbWohinWoher) {
        this(verb, false,
                modalpartikeln, advAngabeSkopusSatz,
                advAngabeSkopusVerbAllg,
                advAngabeSkopusVerbWohinWoher);
    }

    AbstractAngabenfaehigesPraedikatOhneLeerstellen(
            final Verb verb,
            final boolean inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich,
            final Iterable<Modalpartikel> modalpartikeln,
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabeSkopusSatz,
            @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabeSkopusVerbAllg,
            @Nullable final IAdvAngabeOderInterrogativWohinWoher advAngabeSkopusVerbWohinWoher) {
        this.verb = verb;
        this.inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich =
                inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich;
        this.modalpartikeln = ImmutableList.copyOf(modalpartikeln);
        this.advAngabeSkopusSatz = advAngabeSkopusSatz;
        this.advAngabeSkopusVerbAllg = advAngabeSkopusVerbAllg;
        this.advAngabeSkopusVerbWohinWoher = advAngabeSkopusVerbWohinWoher;
    }

    @Override
    public final
        // Es sollte eigentlich keinen Grund gehen, warum das bei einer Unterklasse
        // nicht der Fall sein sollte
    boolean hauptsatzLaesstSichBeiGleichemSubjektMitNachfolgendemVerbzweitsatzZusammenziehen() {
        return true;
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
    public final Konstituentenfolge getVerbzweit(final Person person, final Numerus numerus) {
        return Konstituentenfolge.joinToKonstituentenfolge(
                requireNonNull(verb.getPraesensOhnePartikel(person, numerus)),
                getMittelfeld(person, numerus),
                verb.getPartikel(),
                getNachfeld(person, numerus));
    }

    @Override
    public Konstituentenfolge getVerbzweitMitSubjektImMittelfeld(
            final SubstantivischePhrase subjekt) {
        return Konstituentenfolge.joinToKonstituentenfolge(
                requireNonNull(
                        verb.getPraesensOhnePartikel(subjekt.getPerson(), subjekt.getNumerus())),
                // Damit steht das Subjekt entweder als nicht-pronominales Subjekt vor der
                // Wackernagelposition oder als unbetontes Pronomen zu Anfang der
                // Wackernagelposition:
                subjekt.nomK(),
                getMittelfeld(subjekt.getPerson(), subjekt.getNumerus()),
                verb.getPartikel(),
                getNachfeld(subjekt.getPerson(), subjekt.getNumerus()));
    }

    @Override
    public final Konstituentenfolge getVerbletzt(final Person person, final Numerus numerus) {
        return Konstituentenfolge.joinToKonstituentenfolge(
                getMittelfeld(person, numerus),
                requireNonNull(verb.getPraesensMitPartikel(person, numerus)),
                getNachfeld(person, numerus));
    }

    @Override
    public final Konstituentenfolge getPartizipIIPhrase(final Person person,
                                                        final Numerus numerus) {
        return Konstituentenfolge.joinToKonstituentenfolge(
                getMittelfeld(person, numerus),
                verb.getPartizipII(),
                getNachfeld(person, numerus));
    }

    /**
     * Gibt eine Infinitivkonstruktion zurück mit Prädikat
     * ("den Frosch ignorieren", "das Leben genießen")
     */
    @Override
    public final Konstituentenfolge getInfinitiv(final Person person, final Numerus numerus) {
        return Konstituentenfolge.joinToKonstituentenfolge(
                getMittelfeld(person, numerus),
                verb.getInfinitiv(),
                getNachfeld(person, numerus));
    }

    /**
     * Gibt eine Infinitivkonstruktion zurück mit Prädikat
     * ("den Frosch erneut zu ignorieren", "das Leben zu genießen")
     */
    @Override
    public final Konstituentenfolge getZuInfinitiv(final Person person, final Numerus numerus) {
        return Konstituentenfolge.joinToKonstituentenfolge(
                getMittelfeld(person, numerus),
                verb.getZuInfinitiv(),
                getNachfeld(person, numerus));
    }

    @Nullable
    @Override
    public Konstituente getSpeziellesVorfeldSehrErwuenscht(final Person personSubjekt,
                                                           final Numerus numerusSubjekt,
                                                           final boolean nachAnschlusswort) {
        if (advAngabeSkopusSatz != null) {
            return advAngabeSkopusSatz
                    .getDescription(personSubjekt, numerusSubjekt)
                    .withVorkommaNoetig(false);
        }

        return null;
    }

    @Nullable
    @Override
    public Konstituentenfolge getSpeziellesVorfeldAlsWeitereOption(final Person personSubjekt,
                                                                   final Numerus numerusSubjekt) {
        @Nullable final Konstituente
                advAngabeSkopusVerbTextDescriptionFuerZwangsausklammerung =
                getAdvAngabeSkopusVerbTextDescriptionFuerZwangsausklammerung(personSubjekt,
                        numerusSubjekt);
        if (advAngabeSkopusVerbTextDescriptionFuerZwangsausklammerung != null) {
            // "Und glücklich, sie endlich gefunden zu haben, nimmst du die Kugel."
            return joinToKonstituentenfolge(
                    advAngabeSkopusVerbTextDescriptionFuerZwangsausklammerung
                            .withVorkommaNoetig(false));
        }

        return null;
    }

    @CheckReturnValue
    private Konstituentenfolge getMittelfeld(
            final Person personSubjekt,
            final Numerus numerusSubjekt) {
        @Nullable final SubstPhrOderReflexivpronomen datObjekt = getDat(personSubjekt,
                numerusSubjekt);
        final SubstPhrOderReflexivpronomen akkObjekt = getAkk(personSubjekt,
                numerusSubjekt);

        // einige wenige Verben wie "jdn. etw. lehren" haben zwei Akkusativobjekte
        final SubstPhrOderReflexivpronomen zweitesAkkObjekt = getZweitesAkk();

        final ImmutableList<Konstituentenfolge> unbetontePronomen =
                filterUnbetontePronomen(
                        toPair(akkObjekt, Kasus.AKK),
                        toPair(zweitesAkkObjekt, Kasus.AKK),
                        toPair(datObjekt, Kasus.DAT));

        // Das Mittelfeld besteht aus drei Teilen:
        return Konstituentenfolge.joinToNullKonstituentenfolge(
                // 1. Der Bereich vor der Wackernagel-Position. Dort kann höchstens ein
                //   Subjekt stehen, das keine unbetontes Pronomen ist.
                //   Das Subjekt ist hier im Prädikat noch nicht bekannt.
                // 2. Die Wackernagelposition. Hier stehen alle unbetonten Pronomen in den
                // reinen Kasus in der festen Reihenfolge Nom < Akk < Dat
                kf(unbetontePronomen),
                // 3. Der Bereich nach der Wackernagel-Position. Hier steht alles übrige
                cutFirstOneByOne(
                        getMittelfeldOhneLinksversetzungUnbetonterPronomen(
                                personSubjekt, numerusSubjekt),
                        unbetontePronomen
                ));
    }

    @Override
    public final boolean hatAkkusativobjekt() {
        return getAkk(
                // Ob es ein Akkusativobjekt gibt, sollte von Person und Numuerus
                // unabhängig sein.
                P2, SG) != null
                || getZweitesAkk() != null;
    }

    /**
     * Gibt das Akkusativ-Objekt zurück - sofern es eines gibt.
     */
    @Nullable
    abstract SubstPhrOderReflexivpronomen getAkk(
            Person personSubjekt, Numerus numerusSubjekt);

    /**
     * Gibt das <i>zweite</i> Akkusativ-Objekt zurück - sofern es eines gibt.
     * <p>
     * Nur sehr wenige Verben fordern ein zweites Akkusativ-Objekt - z.B.
     * <i>jdn. etw. lehren</i>
     */
    @Nullable
    abstract SubstPhrOderReflexivpronomen getZweitesAkk();

    /**
     * Gibt das Dativ-Objekt zurück - sofern es eines gibt.
     */
    @Nullable
    abstract SubstPhrOderReflexivpronomen getDat(Person personSubjekt, Numerus numerusSubjekt);


    private static Pair<SubstPhrOderReflexivpronomen, Kasus> toPair(
            @Nullable final
            SubstPhrOderReflexivpronomen substPhrOderReflexivpronomen,
            final Kasus kasus) {
        if (substPhrOderReflexivpronomen == null) {
            return null;
        }

        return Pair.create(substPhrOderReflexivpronomen, kasus);
    }

    @SafeVarargs
    private static ImmutableList<Konstituentenfolge> filterUnbetontePronomen(
            final Pair<SubstPhrOderReflexivpronomen, Kasus>... substantivischePhrasenMitKasus) {
        return Stream.of(substantivischePhrasenMitKasus)
                .filter(Objects::nonNull)
                .filter(spk -> requireNonNull(spk.first).isUnbetontesPronomen())
                .map(spk -> spk.first.imK(requireNonNull(spk.second)))
                .collect(toImmutableList());
    }

    /**
     * Gibt das Mittelfeld dieses Prädikats zurück. Dabei brauchen die unbetonten
     * Pronomen der Objekte noch nicht nach links versetzt zu sein - die Methode könnte also
     * etwas zurückgeben wie <i>dem Ork es geben</i>.
     * <p>
     * Die Linksversetzung der unbetonten Pronomen an die Wackernagel-Position geschieht durch
     * die Methode {@link #getMittelfeld(Person, Numerus)} auf Basis der Methoden
     * {@link #getAkk(Person, Numerus)}, {@link #getZweitesAkk()} und
     * {@link #getDat(Person, Numerus)}.
     */
    @Nullable
    abstract Konstituentenfolge
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
    private Konstituente getAdvAngabeSkopusSatzDescription(
            final Person personSubjekt, final Numerus numerusSubjekt) {
        return advAngabeSkopusSatz != null ?
                advAngabeSkopusSatz.getDescription(personSubjekt, numerusSubjekt) :
                null;
    }

    @Nullable
    Konstituente getAdvAngabeSkopusSatzDescriptionFuerMittelfeld(
            final Person personSubjekt, final Numerus numerusSubjekt) {
        if (advAngabeSkopusSatz == null) {
            return null;
        }

        if (!advAngabeSkopusSatz.imMittelfeldErlaubt()) {
            return null;
        }

        return getAdvAngabeSkopusSatzDescription(personSubjekt, numerusSubjekt);
    }

    @Nullable
    Konstituente getAdvAngabeSkopusSatzDescriptionFuerZwangsausklammerung(
            final Person personSubjekt, final Numerus numerusSubjekt) {
        if (advAngabeSkopusSatz == null) {
            return null;
        }

        if (advAngabeSkopusSatz.imMittelfeldErlaubt()) {
            return null;
        }

        return getAdvAngabeSkopusSatzDescription(personSubjekt, numerusSubjekt);
    }

    @Nullable
    Konstituente getAdvAngabeSkopusVerbTextDescriptionFuerMittelfeld(
            final Person personSubjekt, final Numerus numerusSubjekt) {
        if (advAngabeSkopusVerbAllg == null) {
            return null;
        }

        if (!advAngabeSkopusVerbAllg.imMittelfeldErlaubt()) {
            return null;
        }

        return getAdvAngabeSkopusVerbTextDescription(personSubjekt, numerusSubjekt);
    }

    @Nullable
    Konstituente getAdvAngabeSkopusVerbTextDescriptionFuerZwangsausklammerung(
            final Person personSubjekt, final Numerus numerusSubjekt) {
        if (advAngabeSkopusVerbAllg == null) {
            return null;
        }

        if (advAngabeSkopusVerbAllg.imMittelfeldErlaubt()) {
            return null;
        }

        return getAdvAngabeSkopusVerbTextDescription(personSubjekt, numerusSubjekt);
    }


    @Nullable
    private Konstituente getAdvAngabeSkopusVerbTextDescription(
            final Person personSubjekt, final Numerus numerusSubjekt) {
        return advAngabeSkopusVerbAllg != null ?
                advAngabeSkopusVerbAllg.getDescription(personSubjekt, numerusSubjekt) : null;
    }

    @Nullable
    Konstituente getAdvAngabeSkopusVerbWohinWoherDescription(
            final Person personSubjekt, final Numerus numerusSubjekt) {
        return advAngabeSkopusVerbWohinWoher != null ?
                advAngabeSkopusVerbWohinWoher.getDescription(personSubjekt, numerusSubjekt) :
                null;
    }

    @Nullable
    IAdvAngabeOderInterrogativSkopusSatz getAdvAngabeSkopusSatz() {
        return advAngabeSkopusSatz;
    }

    @Nullable
    IAdvAngabeOderInterrogativVerbAllg getAdvAngabeSkopusVerbAllg() {
        return advAngabeSkopusVerbAllg;
    }

    @Nullable
    IAdvAngabeOderInterrogativWohinWoher getAdvAngabeSkopusVerbWohinWoher() {
        return advAngabeSkopusVerbWohinWoher;
    }

    @Override
    public boolean umfasstSatzglieder() {
        return advAngabeSkopusSatz != null ||
                advAngabeSkopusVerbAllg != null ||
                advAngabeSkopusVerbWohinWoher != null;
    }

    @Override
    public boolean isBezugAufNachzustandDesAktantenGegeben() {
        // Bei "weggehen" ist ein Bezug auf den Nachzustand des Aktanten gegeben.
        return verb.isPartikelverb() ||
                // Auch bei "nach Berlin gehen" ist ein Bezug auf den Nachzustand des
                // Aktanten gegeben.
                advAngabeSkopusVerbWohinWoher != null;

        // Sonst ("gehen", "endlich gehen") eher nicht.
    }

    @Nullable
    static Konstituentenfolge interroAdverbToKF(
            @Nullable final IAdvAngabeOderInterrogativ advAngabeOderInterrogativ) {
        if (!(advAngabeOderInterrogativ instanceof IInterrogativadverb)) {
            return null;
        }

        return joinToKonstituentenfolge(advAngabeOderInterrogativ.getDescription(
                // Machen bei Interrogativadverbien keinen Unterschied
                P3, SG));
    }
    
    @Override
    public boolean inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich() {
        return inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich;
    }
}

