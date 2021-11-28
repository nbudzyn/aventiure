package de.nb.aventiure2.german.praedikat;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Objects.requireNonNull;
import static de.nb.aventiure2.german.base.Belebtheit.UNBELEBT;
import static de.nb.aventiure2.german.base.Konstituentenfolge.cutFirstOneByOne;
import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToKonstituentenfolge;
import static de.nb.aventiure2.german.base.Konstituentenfolge.kf;
import static de.nb.aventiure2.german.base.Negationspartikelphrase.impliziertZustandsaenderung;
import static de.nb.aventiure2.german.base.Negationspartikelphrase.isMehrteilig;
import static de.nb.aventiure2.german.base.Numerus.SG;
import static de.nb.aventiure2.german.base.Person.P2;

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
import de.nb.aventiure2.german.base.Negationspartikelphrase;
import de.nb.aventiure2.german.base.PraedRegMerkmale;
import de.nb.aventiure2.german.base.SubstPhrOderReflexivpronomen;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

/**
 * Ein "semantisches Prädikat", in dem alle Leerstellen besetzt sind und dem grundsätzlich
 * "eigene" adverbiale Angaben ("aus Langeweile") immer noch hinzugefügt werden können. Beispiele:
 * <ul>
 *     <li>"das Buch nehmen"
 *     <li>"dem Frosch Angebote machen"
 *     <li>"dem Frosch endlich Angebote machen"
 * </ul>
 * <p>
 */
public abstract class AbstractAngabenfaehigesSemPraedikatOhneLeerstellen
        implements SemPraedikatOhneLeerstellen {
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
    private final Negationspartikelphrase negationspartikelphrase;

    @Nullable
    private final IAdvAngabeOderInterrogativVerbAllg advAngabeSkopusVerbAllg;

    @Nullable
    private final IAdvAngabeOderInterrogativWohinWoher advAngabeSkopusVerbWohinWoher;

    public AbstractAngabenfaehigesSemPraedikatOhneLeerstellen(
            final Verb verb,
            final boolean inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich) {
        this(verb, inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich, ImmutableList.of(),
                null, null,
                null, null);
    }

    AbstractAngabenfaehigesSemPraedikatOhneLeerstellen(
            final Verb verb,
            final Iterable<Modalpartikel> modalpartikeln,
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabeSkopusSatz,
            @Nullable final Negationspartikelphrase negationspartikelphrase,
            @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabeSkopusVerbAllg,
            @Nullable final IAdvAngabeOderInterrogativWohinWoher advAngabeSkopusVerbWohinWoher) {
        this(verb, false,
                modalpartikeln, advAngabeSkopusSatz,
                negationspartikelphrase, advAngabeSkopusVerbAllg,
                advAngabeSkopusVerbWohinWoher);
    }

    AbstractAngabenfaehigesSemPraedikatOhneLeerstellen(
            final Verb verb,
            final boolean inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich,
            final Iterable<Modalpartikel> modalpartikeln,
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabeSkopusSatz,
            @Nullable final Negationspartikelphrase negationspartikelphrase,
            @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabeSkopusVerbAllg,
            @Nullable final IAdvAngabeOderInterrogativWohinWoher advAngabeSkopusVerbWohinWoher) {
        this.verb = verb;
        this.inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich =
                inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich;
        this.modalpartikeln = ImmutableList.copyOf(modalpartikeln);
        this.advAngabeSkopusSatz = advAngabeSkopusSatz;
        this.negationspartikelphrase = negationspartikelphrase;
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
    public ZuHabenSemPraedikatOhneLeerstellen zuHabenPraedikat() {
        return new ZuHabenSemPraedikatOhneLeerstellen(this);
    }

    @Override
    public final Konstituentenfolge getVerbzweit(final PraedRegMerkmale praedRegMerkmale) {
        return Konstituentenfolge.joinToKonstituentenfolge(
                requireNonNull(verb.getPraesensOhnePartikel(
                        praedRegMerkmale.getPerson(), praedRegMerkmale.getNumerus())),
                getMittelfeld(praedRegMerkmale),
                verb.getPartikel(),
                getNachfeld(praedRegMerkmale));
    }

    @Override
    public Konstituentenfolge getVerbzweitMitSubjektImMittelfeld(
            final SubstantivischePhrase subjekt) {
        return Konstituentenfolge.joinToKonstituentenfolge(
                requireNonNull(verb.getPraesensOhnePartikel(subjekt)),
                // Damit steht das Subjekt entweder als nicht-pronominales Subjekt vor der
                // Wackernagelposition oder als unbetontes Pronomen zu Anfang der
                // Wackernagelposition:
                subjekt.nomK(),
                getMittelfeld(subjekt.getPraedRegMerkmale()),
                verb.getPartikel(),
                getNachfeld(subjekt.getPraedRegMerkmale()));
    }

    @Override
    public final Konstituentenfolge getVerbletzt(final PraedRegMerkmale praedRegMerkmale) {
        return Konstituentenfolge.joinToKonstituentenfolge(
                getMittelfeld(praedRegMerkmale),
                requireNonNull(verb.getPraesensMitPartikel(
                        praedRegMerkmale.getPerson(), praedRegMerkmale.getNumerus())),
                getNachfeld(praedRegMerkmale));
    }

    @Override
    public final ImmutableList<PartizipIIPhrase> getPartizipIIPhrasen(
            final PraedRegMerkmale praedRegMerkmale) {
        return ImmutableList.of(new PartizipIIPhrase(
                Konstituentenfolge.joinToKonstituentenfolge(
                        getMittelfeld(praedRegMerkmale),
                        verb.getPartizipII(),
                        getNachfeld(praedRegMerkmale)),
                verb.getPerfektbildung()));
    }

    /**
     * Gibt eine Infinitivkonstruktion zurück mit Prädikat
     * ("den Frosch ignorieren", "das Leben genießen")
     */
    @Override
    public final Konstituentenfolge getInfinitiv(final PraedRegMerkmale praedRegMerkmale) {
        return Konstituentenfolge.joinToKonstituentenfolge(
                getMittelfeld(praedRegMerkmale),
                verb.getInfinitiv(),
                getNachfeld(praedRegMerkmale));
    }

    /**
     * Gibt eine Infinitivkonstruktion zurück mit Prädikat
     * ("den Frosch erneut zu ignorieren", "das Leben zu genießen")
     */
    @Override
    public final Konstituentenfolge getZuInfinitiv(final PraedRegMerkmale praedRegMerkmale) {
        return Konstituentenfolge.joinToKonstituentenfolge(
                getMittelfeld(praedRegMerkmale),
                verb.getZuInfinitiv(),
                getNachfeld(praedRegMerkmale));
    }

    @Nullable
    @Override
    public Konstituente getSpeziellesVorfeldSehrErwuenscht(final PraedRegMerkmale praedRegMerkmale,
                                                           final boolean nachAnschlusswort) {
        if (advAngabeSkopusSatz != null) {
            return advAngabeSkopusSatz
                    .getDescription(praedRegMerkmale)
                    .withVorkommaNoetig(false);
        }

        return null;
    }

    @Nullable
    @Override
    public Konstituentenfolge getSpeziellesVorfeldAlsWeitereOption(
            final PraedRegMerkmale praedRegMerkmale) {
        if (getNegationspartikel() != null) {
            return null;
        }

        @Nullable final Konstituente
                advAngabeSkopusVerbTextDescriptionFuerZwangsausklammerung =
                getAdvAngabeSkopusVerbTextDescriptionFuerZwangsausklammerung(praedRegMerkmale);
        if (advAngabeSkopusVerbTextDescriptionFuerZwangsausklammerung != null) {
            // "Und glücklich, sie endlich gefunden zu haben, nimmst du die Kugel."
            return joinToKonstituentenfolge(
                    advAngabeSkopusVerbTextDescriptionFuerZwangsausklammerung
                            .withVorkommaNoetig(false));
        }

        return null;
    }

    @CheckReturnValue
    private Konstituentenfolge getMittelfeld(final PraedRegMerkmale praedRegMerkmale) {
        @Nullable final SubstPhrOderReflexivpronomen datObjekt = getDat(praedRegMerkmale);
        final SubstPhrOderReflexivpronomen akkObjekt = getAkk(praedRegMerkmale);

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
                        getMittelfeldOhneLinksversetzungUnbetonterPronomen(praedRegMerkmale),
                        unbetontePronomen
                ));
    }

    @Override
    public final boolean hatAkkusativobjekt() {
        return getAkk(
                new PraedRegMerkmale(
                        // Ob es ein Akkusativobjekt gibt, sollte von Person, Numuerus und
                        // Belebtheit
                        // unabhängig sein.
                        P2, SG, UNBELEBT)) != null
                || getZweitesAkk() != null;
    }

    /**
     * Gibt das Akkusativ-Objekt zurück - sofern es eines gibt.
     */
    @Nullable
    abstract SubstPhrOderReflexivpronomen getAkk(PraedRegMerkmale praedRegMerkmale);

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
    abstract SubstPhrOderReflexivpronomen getDat(PraedRegMerkmale praedRegMerkmale);


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
     * die Methode {@link #getMittelfeld(PraedRegMerkmale)} auf Basis der Methoden
     * {@link #getAkk(PraedRegMerkmale)}, {@link #getZweitesAkk()} und
     * {@link #getDat(PraedRegMerkmale)}.
     */
    @Nullable
    abstract Konstituentenfolge
    getMittelfeldOhneLinksversetzungUnbetonterPronomen(PraedRegMerkmale praedRegMerkmale);

    @NonNull
    Verb getVerb() {
        return verb;
    }

    ImmutableList<Modalpartikel> getModalpartikeln() {
        return modalpartikeln;
    }

    @Nullable
    private Konstituente getAdvAngabeSkopusSatzDescription(
            final PraedRegMerkmale praedRegMerkmale) {
        return advAngabeSkopusSatz != null ?
                advAngabeSkopusSatz.getDescription(praedRegMerkmale) :
                null;
    }

    @Nullable
    Konstituente getAdvAngabeSkopusSatzDescriptionFuerMittelfeld(
            final PraedRegMerkmale praedRegMerkmale) {
        if (advAngabeSkopusSatz == null) {
            return null;
        }

        if (!advAngabeSkopusSatz.imMittelfeldErlaubt()) {
            return null;
        }

        return getAdvAngabeSkopusSatzDescription(praedRegMerkmale);
    }

    @Nullable
    Konstituente getAdvAngabeSkopusSatzDescriptionFuerZwangsausklammerung(
            final PraedRegMerkmale praedRegMerkmale) {
        if (advAngabeSkopusSatz == null) {
            return null;
        }

        if (advAngabeSkopusSatz.imMittelfeldErlaubt()) {
            return null;
        }

        return getAdvAngabeSkopusSatzDescription(praedRegMerkmale);
    }

    @Nullable
    Konstituente getAdvAngabeSkopusVerbTextDescriptionFuerMittelfeld(
            final PraedRegMerkmale praedRegMerkmale) {
        if (advAngabeSkopusVerbAllg == null) {
            return null;
        }

        if (!advAngabeSkopusVerbAllg.imMittelfeldErlaubt()) {
            return null;
        }

        return getAdvAngabeSkopusVerbTextDescription(praedRegMerkmale);
    }

    @Nullable
    Konstituente getAdvAngabeSkopusVerbTextDescriptionFuerZwangsausklammerung(
            final PraedRegMerkmale praedRegMerkmale) {
        if (advAngabeSkopusVerbAllg == null) {
            return null;
        }

        if (advAngabeSkopusVerbAllg.imMittelfeldErlaubt()) {
            return null;
        }

        return getAdvAngabeSkopusVerbTextDescription(praedRegMerkmale);
    }


    @Nullable
    private Konstituente getAdvAngabeSkopusVerbTextDescription(
            final PraedRegMerkmale praedRegMerkmale) {
        return advAngabeSkopusVerbAllg != null ?
                advAngabeSkopusVerbAllg.getDescription(praedRegMerkmale) : null;
    }

    @Nullable
    Konstituente getAdvAngabeSkopusVerbWohinWoherDescription(
            final PraedRegMerkmale praedRegMerkmale) {
        return advAngabeSkopusVerbWohinWoher != null ?
                advAngabeSkopusVerbWohinWoher.getDescription(praedRegMerkmale) :
                null;
    }

    @Nullable
    IAdvAngabeOderInterrogativSkopusSatz getAdvAngabeSkopusSatz() {
        return advAngabeSkopusSatz;
    }

    @Nullable
    Negationspartikelphrase getNegationspartikel() {
        return negationspartikelphrase;
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
                isMehrteilig(negationspartikelphrase) ||
                advAngabeSkopusVerbAllg != null ||
                advAngabeSkopusVerbWohinWoher != null;
    }

    @Override
    public boolean isBezugAufNachzustandDesAktantenGegeben() {
        // Bei "weggehen" ist ein Bezug auf den Nachzustand des Aktanten gegeben.
        return verb.isPartikelverb()
                // Auch bei "nicht mehr gehen" ist eine Bezug auf den Nachzustand des
                // Aktanten gegeben.
                || impliziertZustandsaenderung(negationspartikelphrase)
                // Auch bei "nach Berlin gehen" ist ein Bezug auf den Nachzustand des
                // Aktanten gegeben.
                || advAngabeSkopusVerbWohinWoher != null;

        // Sonst ("gehen", "endlich gehen") eher nicht.
    }

    @Nullable
    static Konstituentenfolge interroAdverbToKF(
            @Nullable final IAdvAngabeOderInterrogativ advAngabeOderInterrogativ) {
        if (!(advAngabeOderInterrogativ instanceof IInterrogativadverb)) {
            return null;
        }

        return joinToKonstituentenfolge(
                ((IInterrogativadverb) advAngabeOderInterrogativ).getDescription());
    }

    @Override
    public boolean inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich() {
        return inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AbstractAngabenfaehigesSemPraedikatOhneLeerstellen that =
                (AbstractAngabenfaehigesSemPraedikatOhneLeerstellen) o;
        return inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich
                == that.inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich &&
                verb.equals(that.verb) &&
                Objects.equals(modalpartikeln, that.modalpartikeln) &&
                Objects.equals(advAngabeSkopusSatz, that.advAngabeSkopusSatz) &&
                Objects.equals(negationspartikelphrase, that.negationspartikelphrase) &&
                Objects.equals(advAngabeSkopusVerbAllg, that.advAngabeSkopusVerbAllg) &&
                Objects
                        .equals(advAngabeSkopusVerbWohinWoher, that.advAngabeSkopusVerbWohinWoher);
    }

    @Override
    public int hashCode() {
        return Objects
                .hash(verb, inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich, modalpartikeln,
                        advAngabeSkopusSatz, negationspartikelphrase, advAngabeSkopusVerbAllg,
                        advAngabeSkopusVerbWohinWoher);
    }
}

