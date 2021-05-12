package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.base.Joiner;

import java.util.Objects;

import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static de.nb.aventiure2.german.base.Konstituente.k;
import static de.nb.aventiure2.german.base.Numerus.SG;
import static de.nb.federkiel.string.StringUtil.stripPrefixIfAny;

/**
 * Repräsentiert ein Verb als Lexem, von dem Wortformen gebildet werden können - jedoch <i>ohne
 * Informationen zur Valenz</i>.
 * <p>
 * Die Grundidee der Architektur ist:
 * <ul>
 * <li>Es gibt ein paar Kernklassen oder Kerninterfaces für Lexeme (z.B. <code>Verb</code>
 * für Verben sowie die Implementierungen von {@link VerbMitValenz} z.B. {@link VerbSubjDatAkk}
 * etc.)
 * <li>Aus den Lexem-Klassen werden Klassen für die einzelnen Phrasen erzeugt, z.B.
 * für die {@link Praedikat}e sowie für ganze Sätze. Diese Klassen halten die syntaktischen
 * Relationen fest, in denen die Lexeme und Teil-Phrasen stehen.
 * <li>Wortformen werden bei Bedarf als String erzeugt
 * <li>Sie werden im Rahmen des {@link Praedikat}s zu
 * {@link de.nb.aventiure2.german.base.Konstituente}n zusammengefasst, über die die Verteilung
 * in die topologischen Felder sowie die Komma-Setzung gelöst werden.
 *  </ul>
 */
public class Verb {

    /**
     * /**
     * Infinitiv des Verbs ("aufheben")
     */
    @NonNull
    private final String infinitiv;

    /**
     * 1. Person Singular Präsens Indikativ des Verbs, ggf. ist das Präfix abgetrennt
     * ("hebe")
     */
    @Nullable
    private final String ichFormOhnePartikel;

    /**
     * 2. Person Singular Präsens Indikativ des Verbs, ggf. ist das Präfix abgetrennt
     * ("hebst")
     */
    @Nullable
    private final String duFormOhnePartikel;

    /**
     * 3. Person Singular Präsens Indikativ des Verbs, ggf. ist das Präfix abgetrennt
     * ("(er / sie / es) hebt")
     */
    @NonNull
    private final String erSieEsFormOhnePartikel;

    /**
     * 1. und 3. Person Plural Präsens Indikativ des Verbs, ggf. ist das Präfix abgetrennt
     * ("(wir / sie) sind")
     */
    @NonNull
    private final String wirSieFormOhnePartikel;

    /**
     * 2. Person Plural Präsens Indikativ des Verbs, ggf. ist das Präfix abgetrennt
     * ("(ihr) hebt")
     */
    @Nullable
    private final String ihrFormOhnePartikel;

    /**
     * Wenn es sich um ein <i>Partikelverb</i> ("aufheben")
     * handelt, dann die <i>Partikel</i> ("auf"). Das Besondere an Partikeln ist, dass sie
     * <i>abgetrennt</i> werden - anderes als die <i>Präfixe</i>
     * ("ver", "ge") von <i>Präfixverben</i>.
     */
    @Nullable
    private final String partikel;

    @NonNull
    private final Perfektbildung perfektbildung;

    /**
     * Der unflektierte Partizip II des Verbs ("gehoben", "angenommen", "zerbrochen")
     */
    @NonNull
    private final String partizipII;

    /**
     * Erzeugt ein Verb, das <i>kein</i> Partikelverb ist, also ein Verb
     * <i>ohne Partikel</i>. Von dem Verb wird also (anders als z.B.
     * bei "er steht auf") bei der Formenbildung nichts abgetrennt.
     */
    public Verb(final String infinitivUndWirSieForm,
                @Nullable final String ichFormOhnePartikel,
                @Nullable final String duFormOhnePartikel,
                final String erSieEsFormOhnePartikel,
                @Nullable final String ihrFormOhnePartikel,
                final Perfektbildung perfektbildung,
                final String partizipII) {
        this(infinitivUndWirSieForm,
                ichFormOhnePartikel, duFormOhnePartikel,
                erSieEsFormOhnePartikel, ihrFormOhnePartikel,
                null, perfektbildung, partizipII);
    }

    public Verb(final String infinitivUndWirSieForm,
                @Nullable final String ichFormOhnePartikel,
                @Nullable final String duFormOhnePartikel,
                final String erSieEsFormOhnePartikel,
                @Nullable final String ihrFormOhnePartikel,
                @Nullable final String partikel,
                final Perfektbildung perfektbildung,
                final String partizipII) {
        this(infinitivUndWirSieForm,
                ichFormOhnePartikel, duFormOhnePartikel, erSieEsFormOhnePartikel,
                infinitivUndWirSieForm, ihrFormOhnePartikel,
                partikel, perfektbildung, partizipII);
    }

    public Verb(final String infinitiv,
                @Nullable final String ichFormOhnePartikel,
                @Nullable final String duFormOhnePartikel,
                final String erSieEsFormOhnePartikel,
                final String wirSieFormOhnePartikel,
                @Nullable final String ihrFormOhnePartikel,
                @Nullable final String partikel,
                final Perfektbildung perfektbildung,
                final String partizipII) {
        this.infinitiv = infinitiv;
        this.ichFormOhnePartikel = ichFormOhnePartikel;
        this.duFormOhnePartikel = duFormOhnePartikel;
        this.erSieEsFormOhnePartikel = erSieEsFormOhnePartikel;
        this.wirSieFormOhnePartikel = wirSieFormOhnePartikel;
        this.ihrFormOhnePartikel = ihrFormOhnePartikel;
        this.partikel = partikel;
        this.perfektbildung = checkNotNull(perfektbildung, "perfektbildung ist null");
        this.partizipII = partizipII;

        checkArgument(partikel == null || infinitiv.startsWith(partikel),
                "Inifinitiv beginnt nicht mit Partikel! Partikel: %s, "
                        + "Infinitiv: %s", partikel, infinitiv);
        checkArgument(partikel == null || partizipII.startsWith(partikel),
                "Partizip II beginnt nicht mit Partikel! Partikel: %s, "
                        + "Partizip II: %s", partikel, partizipII);
    }

    private Verb ohnePartikel() {
        if (partikel == null) {
            return this;
        }

        return new Verb(stripPrefixIfAny(partikel, infinitiv),
                ichFormOhnePartikel, duFormOhnePartikel, erSieEsFormOhnePartikel,
                wirSieFormOhnePartikel, ihrFormOhnePartikel, null, perfektbildung,
                stripPrefixIfAny(partikel, partizipII));
    }

    Verb mitPartikel(final String partikel) {
        if (this.partikel != null) {
            return ohnePartikel().mitPartikel(partikel);
        }

        return new Verb(partikel + infinitiv,
                ichFormOhnePartikel, duFormOhnePartikel, erSieEsFormOhnePartikel,
                wirSieFormOhnePartikel, ihrFormOhnePartikel, partikel, perfektbildung,
                partikel + partizipII);
    }

    Verb mitPerfektbildung(final Perfektbildung perfektbildung) {
        return new Verb(infinitiv,
                ichFormOhnePartikel, duFormOhnePartikel, erSieEsFormOhnePartikel,
                wirSieFormOhnePartikel, ihrFormOhnePartikel, partikel, perfektbildung,
                partizipII);
    }

    public String getZuInfinitiv() {
        if (getPartikel() == null) {
            return "zu " + getInfinitiv();
        }

        return getPartikel() + "zu" + getInfinitivOhnePartikel();
    }

    private String getInfinitivOhnePartikel() {
        if (getPartikel() == null) {
            return getInfinitiv();
        }

        return getInfinitiv().substring(getPartikel().length());
    }

    @NonNull
    public String getInfinitiv() {
        return infinitiv;
    }

    @Nullable
    String getPraesensMitPartikel(final Person person, final Numerus numerus) {
        @Nullable final String praesensOhnePartikel = getPraesensOhnePartikel(person, numerus);
        if (praesensOhnePartikel == null) {
            return null;
        }
        return Joiner.on("").skipNulls().join(
                partikel,
                praesensOhnePartikel);
    }

    @Nullable
    String getPraesensOhnePartikel(final SubstantivischePhrase subjekt) {
        return getPraesensOhnePartikel(subjekt.getPerson(), subjekt.getNumerus());
    }

    @Nullable
    String getPraesensOhnePartikel(final Person person, final Numerus numerus) {
        switch (person) {
            case P1:
                return numerus == SG ? ichFormOhnePartikel : wirSieFormOhnePartikel;
            case P2:
                return numerus == SG ? duFormOhnePartikel : ihrFormOhnePartikel;
            case P3:
                return numerus == SG ? erSieEsFormOhnePartikel : wirSieFormOhnePartikel;
            default:
                throw new IllegalStateException("Unexpected person: " + person);
        }
    }

    @Nullable
    String getDuFormOhnePartikel() {
        return duFormOhnePartikel;
    }

    @Nullable
    String getPartikel() {
        return partikel;
    }

    @NonNull
    PartizipIIPhrase getPartizipIIPhrase() {
        return new PartizipIIPhrase(
                new Konstituentenfolge(k(getPartizipII())),
                perfektbildung);
    }

    Verb getHilfsverbFuerPerfekt() {
        switch (perfektbildung) {
            case HABEN:
                return HabenUtil.VERB;
            case SEIN:
                return SeinUtil.VERB;
            default:
                throw new IllegalStateException("Unexpected Perfektbildung");
        }
    }

    @NonNull
    public Perfektbildung getPerfektbildung() {
        return perfektbildung;
    }

    boolean isPartikelverb() {
        return partikel != null;
    }

    @NonNull
    public String getPartizipII() {
        return partizipII;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Verb verb = (Verb) o;
        return infinitiv.equals(verb.infinitiv) &&
                Objects.equals(ichFormOhnePartikel, verb.ichFormOhnePartikel) &&
                Objects.equals(duFormOhnePartikel, verb.duFormOhnePartikel) &&
                erSieEsFormOhnePartikel.equals(verb.erSieEsFormOhnePartikel) &&
                wirSieFormOhnePartikel.equals(verb.wirSieFormOhnePartikel) &&
                Objects.equals(ihrFormOhnePartikel, verb.ihrFormOhnePartikel) &&
                Objects.equals(partikel, verb.partikel) &&
                perfektbildung == verb.perfektbildung &&
                partizipII.equals(verb.partizipII);
    }

    @Override
    public int hashCode() {
        return Objects.hash(infinitiv);
    }

    @NonNull
    @Override
    public String toString() {
        return "Verb{" +
                "infinitiv='" + infinitiv + '\'' +
                '}';
    }

}
