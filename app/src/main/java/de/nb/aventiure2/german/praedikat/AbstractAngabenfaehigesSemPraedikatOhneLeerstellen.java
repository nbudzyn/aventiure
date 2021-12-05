package de.nb.aventiure2.german.praedikat;

import static java.util.Objects.requireNonNull;
import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToKonstituentenfolge;
import static de.nb.aventiure2.german.base.Negationspartikelphrase.impliziertZustandsaenderung;
import static de.nb.aventiure2.german.base.Negationspartikelphrase.isMehrteilig;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Objects;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativ;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativSkopusSatz;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativVerbAllg;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativWohinWoher;
import de.nb.aventiure2.german.base.IInterrogativadverb;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.Negationspartikelphrase;
import de.nb.aventiure2.german.base.PraedRegMerkmale;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.ITextContext;

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
    public final Konstituentenfolge getVerbzweit(final ITextContext textContext,
                                                 final PraedRegMerkmale praedRegMerkmale) {
        final TopolFelder topolFelder = getTopolFelder(textContext, praedRegMerkmale);

        return Konstituentenfolge.joinToKonstituentenfolge(
                requireNonNull(verb.getPraesensOhnePartikel(
                        praedRegMerkmale.getPerson(), praedRegMerkmale.getNumerus())),
                topolFelder.getMittelfeld(),
                verb.getPartikel(),
                topolFelder.getNachfeld());
    }

    @Override
    public Konstituentenfolge getVerbzweitMitSubjektImMittelfeld(
            final ITextContext textContext,
            final SubstantivischePhrase subjekt) {
        final TopolFelder topolFelder = getTopolFelder(textContext, subjekt.getPraedRegMerkmale());

        return Konstituentenfolge.joinToKonstituentenfolge(
                requireNonNull(verb.getPraesensOhnePartikel(subjekt)),
                // Damit steht das Subjekt entweder als nicht-pronominales Subjekt vor der
                // Wackernagelposition oder als unbetontes Pronomen zu Anfang der
                // Wackernagelposition:
                subjekt.nomK(),
                topolFelder.getMittelfeld(),
                verb.getPartikel(),
                topolFelder.getNachfeld());
    }

    @Override
    public final Konstituentenfolge getVerbletzt(
            final ITextContext textContext, final PraedRegMerkmale praedRegMerkmale) {
        final TopolFelder topolFelder = getTopolFelder(textContext, praedRegMerkmale);

        return Konstituentenfolge.joinToKonstituentenfolge(
                topolFelder.getMittelfeld(),
                requireNonNull(verb.getPraesensMitPartikel(
                        praedRegMerkmale.getPerson(), praedRegMerkmale.getNumerus())),
                topolFelder.getNachfeld());
    }

    @Override
    public final ImmutableList<PartizipIIPhrase> getPartizipIIPhrasen(
            final ITextContext textContext,
            final PraedRegMerkmale praedRegMerkmale) {
        final TopolFelder topolFelder = getTopolFelder(textContext, praedRegMerkmale);

        return ImmutableList.of(new PartizipIIPhrase(
                Konstituentenfolge.joinToKonstituentenfolge(
                        topolFelder.getMittelfeld(),
                        verb.getPartizipII()),
                topolFelder.getNachfeld(),
                verb.getPerfektbildung()));
    }

    /**
     * Gibt eine Infinitivkonstruktion zurück mit Prädikat
     * ("den Frosch ignorieren", "das Leben genießen")
     */
    @Override
    public final Infinitiv getInfinitiv(
            final ITextContext textContext, final PraedRegMerkmale praedRegMerkmale) {
        return new Infinitiv(verb, getTopolFelder(textContext, praedRegMerkmale));
    }

    /**
     * Gibt eine Infinitivkonstruktion mit zu-Infinitiv zurück mit Prädikat
     * ("den Frosch zu ignorieren", "das Leben zu genießen")
     */
    @Override
    public final ZuInfinitiv getZuInfinitiv(
            final ITextContext textContext, final PraedRegMerkmale praedRegMerkmale) {
        return new ZuInfinitiv(verb, getTopolFelder(textContext, praedRegMerkmale));
    }

    /**
     * Gibt die konkreten topologischen Felder zurück, aus denen ein (syntaktischer)
     * Satz zusammengesetzt werden kann.
     */
    @CheckReturnValue
    abstract TopolFelder getTopolFelder(ITextContext textContext,
                                        PraedRegMerkmale praedRegMerkmale);

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

