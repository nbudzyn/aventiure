package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.Kasus;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.Negationspartikelphrase;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static de.nb.aventiure2.german.base.Kasus.AKK;
import static de.nb.aventiure2.german.base.Konstituente.k;
import static de.nb.aventiure2.german.praedikat.VerbSubj.ZIEHEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.BAUEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.KUEHLEN;

/**
 * Ein Verb wie "sich beziehen", das nur ein reflexives Objekt oder Präpositionalobjekt hat
 * ("an sich" / "sich")
 * </ul>
 */
public enum ReflVerbSubj implements VerbOhneLeerstellen, PraedikatOhneLeerstellen {
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
    public Konstituentenfolge getVerbzweit(final Person person, final Numerus numerus) {
        return toPraedikat().getVerbzweit(person, numerus);
    }

    @Override
    public Konstituentenfolge getVerbzweitMitSubjektImMittelfeld(
            final SubstantivischePhrase subjekt) {
        return toPraedikat().getVerbzweitMitSubjektImMittelfeld(subjekt);
    }

    @Override
    public Konstituentenfolge getVerbletzt(final Person person, final Numerus numerus) {
        return toPraedikat().getVerbletzt(person, numerus);
    }

    @Override
    @CheckReturnValue
    public ImmutableList<PartizipIIPhrase> getPartizipIIPhrasen(final Person person,
                                                                final Numerus numerus) {
        return ImmutableList.of(verb.getPartizipIIPhrase());
    }

    @Override
    public Konstituentenfolge getInfinitiv(final Person person, final Numerus numerus) {
        return Konstituentenfolge.joinToKonstituentenfolge(verb.getInfinitiv());
    }

    @Override
    @CheckReturnValue
    public Konstituentenfolge getZuInfinitiv(final Person person, final Numerus numerus) {
        return new Konstituentenfolge(k(verb.getZuInfinitiv()));
    }

    @Nullable
    @Override
    public Konstituente getSpeziellesVorfeldSehrErwuenscht(final Person person,
                                                           final Numerus numerus,
                                                           final boolean nachAnschlusswort) {
        return toPraedikat().getSpeziellesVorfeldSehrErwuenscht(person, numerus,
                nachAnschlusswort);
    }

    @Nullable
    @Override
    public Konstituentenfolge getSpeziellesVorfeldAlsWeitereOption(final Person person,
                                                                   final Numerus numerus) {
        return toPraedikat().getSpeziellesVorfeldAlsWeitereOption(person, numerus
        );
    }

    @Override
    public ReflPraedikatSubOhneLeerstellen neg(
            @Nullable final Negationspartikelphrase negationspartikelphrase) {
        return toPraedikat().neg(negationspartikelphrase);
    }

    @Override
    public boolean isBezugAufNachzustandDesAktantenGegeben() {
        return true;
    }

    @Override
    public ReflPraedikatSubOhneLeerstellen toPraedikat() {
        return new ReflPraedikatSubOhneLeerstellen(verb, reflKasus);
    }

    @Nullable
    @Override
    public Konstituentenfolge getNachfeld(final Person person, final Numerus numerus) {
        return null;
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