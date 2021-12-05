package de.nb.aventiure2.german.praedikat;

import static de.nb.aventiure2.german.base.Kasus.AKK;
import static de.nb.aventiure2.german.praedikat.VerbSubj.ZIEHEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.BAUEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.KUEHLEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.WENDEN;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.Kasus;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.Negationspartikelphrase;
import de.nb.aventiure2.german.base.PraedRegMerkmale;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.ITextContext;

/**
 * Ein Verb wie "sich beziehen", das nur ein reflexives Objekt oder Präpositionalobjekt hat
 * ("an sich" / "sich")
 * </ul>
 */
public enum ReflVerbSubj implements VerbOhneLeerstellen, SemPraedikatOhneLeerstellen {
    // Verben ohne Partikel
    SICH_ANFASSEN("anfassen", AKK,
            "fasse", "fasst", "fasst", "fasst",
            Perfektbildung.HABEN, "angefasst"),
    SICH_BEWOELKEN("bewölken", AKK,
            "bewölke", "bewölkst", "bewölkt", "bewölkt",
            Perfektbildung.HABEN, "bewölkt"),
    SICH_BEZIEHEN("beziehen", AKK,
            "beziehe", "beziehst", "bezieht",
            "bezieht",
            Perfektbildung.HABEN, "bezogen"),
    SICH_DRAENGEN("drängen", AKK,
            "dränge", "drängst", "drängt", "drängt",
            Perfektbildung.HABEN, "gedrängt"),
    SICH_LEGEN("legen", AKK, "lege", "legst", "legt", "legt",
            Perfektbildung.HABEN, "gelegt"),
    SICH_VERDUNKELN("verdunkeln", AKK,
            "verdunkle", "verdunkelst", "verdunkelt",
            "verdunkelt",
            Perfektbildung.HABEN, "verdunkelt"),
    SICH_VERDUESTERN("verdüstern", AKK,
            "verdüstere", "verdüsterst", "verdüstert",
            "verdüstert",
            Perfektbildung.HABEN, "verdüstert"),
    SICH_WENDEN(WENDEN, AKK, Perfektbildung.HABEN),

    // Partikelverben
    SICH_ABKUEHLEN(KUEHLEN, AKK, "ab", Perfektbildung.HABEN),
    SICH_AUFBAUEN(BAUEN, AKK, "auf", Perfektbildung.HABEN),
    SICH_ZUZIEHEN(ZIEHEN, AKK, "zu", Perfektbildung.HABEN);

    /**
     * Das Verb an sich, ohne Informationen zur Valenz, ohne Ergänzungen, ohne
     * Angaben
     */
    @NonNull
    private final Verb verb;

    /**
     * Der Kasus mit dem das Verb reflexiv steht - z.B. Akkusativ ("sich beziehen")
     * oder ein Präpositionalkasus ("an sich")
     */
    @NonNull
    private final Kasus reflKasus;

    ReflVerbSubj(
            final String infinitiv,
            final Kasus reflKasus,
            final String ichForm,
            final String duForm,
            final String erSieEsForm,
            final String ihrForm,
            final Perfektbildung perfektbildung,
            final String partizipII) {
        this(new Verb(infinitiv, ichForm, duForm, erSieEsForm, ihrForm, perfektbildung, partizipII),
                reflKasus);
    }

    ReflVerbSubj(final VerbMitValenz verbMitValenz,
                 final Kasus reflKasus,
                 final Perfektbildung perfektbildung) {
        this(verbMitValenz.getVerb().mitPerfektbildung(perfektbildung), reflKasus);
    }

    ReflVerbSubj(final VerbMitValenz verbMitValenz,
                 final Kasus reflKasus,
                 final String partikel,
                 final Perfektbildung perfektbildung) {
        this(verbMitValenz.getVerb(), reflKasus, partikel, perfektbildung);
    }

    ReflVerbSubj(final Verb verbOhnePartikel,
                 final Kasus reflKasus,
                 final String partikel,
                 final Perfektbildung perfektbildung) {
        this(verbOhnePartikel.mitPartikel(partikel).mitPerfektbildung(perfektbildung),
                reflKasus);
    }

    ReflVerbSubj(final Verb verb,
                 final Kasus reflKasus) {
        this.verb = verb;
        this.reflKasus = reflKasus;
    }

    @Override
    public Konstituentenfolge getVerbzweit(
            final ITextContext textContext,
            final PraedRegMerkmale praedRegMerkmale) {
        return toPraedikat().getVerbzweit(textContext, praedRegMerkmale);
    }

    @Override
    public Konstituentenfolge getVerbzweitMitSubjektImMittelfeld(
            final ITextContext textContext,
            final SubstantivischePhrase subjekt) {
        return toPraedikat().getVerbzweitMitSubjektImMittelfeld(textContext, subjekt);
    }

    @Override
    public Konstituentenfolge getVerbletzt(
            final ITextContext textContext,
            final PraedRegMerkmale praedRegMerkmale) {
        return toPraedikat().getVerbletzt(textContext, praedRegMerkmale);
    }

    @Override
    @CheckReturnValue
    public ImmutableList<PartizipIIPhrase> getPartizipIIPhrasen(
            final ITextContext textContext,
            final PraedRegMerkmale praedRegMerkmale) {
        return ImmutableList.of(verb.getPartizipIIPhrase());
    }

    @Override
    public Infinitiv getInfinitiv(
            final ITextContext textContext,
            final PraedRegMerkmale praedRegMerkmale) {
        return new Infinitiv(verb);
    }

    @Override
    public ZuInfinitiv getZuInfinitiv(
            final ITextContext textContext,
            final PraedRegMerkmale praedRegMerkmale) {
        return new ZuInfinitiv(verb);
    }

    @Nullable
    @Override
    public Konstituente getSpeziellesVorfeldSehrErwuenscht(final PraedRegMerkmale praedRegMerkmale,
                                                           final boolean nachAnschlusswort) {
        return toPraedikat().getSpeziellesVorfeldSehrErwuenscht(praedRegMerkmale,
                nachAnschlusswort);
    }

    @Nullable
    @Override
    public Konstituentenfolge getSpeziellesVorfeldAlsWeitereOption(
            final PraedRegMerkmale praedRegMerkmale) {
        return toPraedikat().getSpeziellesVorfeldAlsWeitereOption(praedRegMerkmale);
    }

    @Override
    public ReflSemPraedikatSubOhneLeerstellen neg(
            @Nullable final Negationspartikelphrase negationspartikelphrase) {
        return toPraedikat().neg(negationspartikelphrase);
    }

    @Override
    public boolean isBezugAufNachzustandDesAktantenGegeben() {
        return true;
    }

    @Override
    public ReflSemPraedikatSubOhneLeerstellen toPraedikat() {
        return new ReflSemPraedikatSubOhneLeerstellen(verb, reflKasus);
    }

    public TopolFelder getTopolFelder(final ITextContext textContext,
                                      final PraedRegMerkmale praedRegMerkmale) {
        return toPraedikat().getTopolFelder(textContext, praedRegMerkmale);
    }

    @Override
    @NonNull
    public Verb getVerb() {
        return verb;
    }

    @Override
    public boolean inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich() {
        return false;
    }
}