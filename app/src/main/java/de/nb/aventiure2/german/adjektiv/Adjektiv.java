package de.nb.aventiure2.german.adjektiv;

import androidx.annotation.NonNull;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.Objects;

import de.nb.aventiure2.german.base.Endungen;
import de.nb.aventiure2.german.base.Kasus;
import de.nb.aventiure2.german.base.NumerusGenus;

import static de.nb.aventiure2.german.base.NumerusGenus.F;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.NumerusGenus.N;
import static de.nb.aventiure2.german.base.NumerusGenus.PL_MFN;
import static java.util.Objects.requireNonNull;

/**
 * Repräsentiert ein Adjektiv als Lexem, von dem Wortformen gebildet werden können - jedoch <i>ohne
 * Informationen zur Valenz</i>.
 */
@SuppressWarnings("DuplicateBranchesInSwitch")
public class Adjektiv {
    private static final Map<NumerusGenus, Endungen> ENDUNGEN_STARK = ImmutableMap.of(
            M, new Endungen("er", "em", "en"),
            F, new Endungen("e", "er"),
            N, new Endungen("es", "em"),
            PL_MFN, new Endungen("e", "en"));

    private static final Map<NumerusGenus, Endungen> ENDUNGEN_SCHWACH = ImmutableMap.of(
            M, new Endungen("e", "en", "en"),
            F, new Endungen("e", "en"),
            N, new Endungen("e", "en"),
            PL_MFN, new Endungen("en"));

    /**
     * Ob das Adjektiv flektiert werden kann ("groß", "hell", "schön" ,
     * "hoch", "anders", "besonders") oder nicht ("lila", "rosa", "oliv", "prima",
     * "Kieler", "Hamburger", "neunziger" etc.)
     */
    // Der Unterschied zwischen nicht flektierbar im engeren Sinne und Ableitung auf "-er" liegt
    // vielleicht nur in der Genitvregel: Ableitungen wie "Kieler" werden
    // als "er"-Genitiv-Endung verstanden ("die Autos Kieler Professoren").
    // Derzeit unterstützen wir keinen Genitiv.
    private final boolean flektierbar;

    /**
     * Stamm des Adjektivs, endet nie auf "e": "groß", "glücklich", "hoh", "dunkl", "finstr",
     * "ander", "besonder"
     */
    @NonNull
    private final String stamm;

    /**
     * Prädikative Form des Adjektivs ("glücklich", "hoch", "anders", "besonders", "Kieler", "lila")
     */
    @NonNull
    private final String praedikativ;

    public static Adjektiv nichtFlektierbar(final String wortform) {
        return new Adjektiv(wortform, wortform, false);
    }

    /**
     * Erzeugt ein flektierbares Adjektiv
     */
    public Adjektiv(final String praedikativ) {
        this(praedikativ, entferneEVomEnde(praedikativ));
    }

    /**
     * Erzeugt ein flektierbares Adjektiv
     */
    Adjektiv(final String praedikativ, final String stamm) {
        this(praedikativ, stamm, true);
    }

    private Adjektiv(final String praedikativ, final String stamm, final boolean flektierbar) {
        Preconditions.checkArgument(
                !flektierbar
                        || stamm.endsWith("ee") || stamm.endsWith("ie")
                        || stamm.length() <= 2
                        || !stamm.endsWith("e"),
                "Adjektivstamm eines flektierbaren Adjektivs endet auf e");

        this.praedikativ = praedikativ;
        this.stamm = stamm;
        this.flektierbar = flektierbar;
    }

    private static String entferneEVomEnde(final String stammEvtlMitE) {
        if (stammEvtlMitE.endsWith("ee") || stammEvtlMitE.endsWith("ie")
                || stammEvtlMitE.length() <= 2 || !stammEvtlMitE.endsWith("e")) {
            return stammEvtlMitE;
        }

        return stammEvtlMitE.substring(0, stammEvtlMitE.length() - 1);
    }

    String getAttributiv(final NumerusGenus numerusGenus, final Kasus kasus,
                         final boolean artikelwortTraegtKasusEndung) {
        if (!flektierbar) {
            return praedikativ; // die einzige Form
        }

        if (artikelwortTraegtKasusEndung) {
            return getAttributivSchwach(numerusGenus, kasus);
        }

        return getAttributivStark(numerusGenus, kasus);
    }

    private String getAttributivSchwach(final NumerusGenus numerusGenus, final Kasus kasus) {
        return get(ENDUNGEN_SCHWACH, numerusGenus, kasus);
    }

    private String getAttributivStark(final NumerusGenus numerusGenus, final Kasus kasus) {
        return get(ENDUNGEN_STARK, numerusGenus, kasus);
    }

    private String get(final Map<NumerusGenus, Endungen> endungen, final NumerusGenus numerusGenus,
                       final Kasus kasus) {
        return requireNonNull(endungen.get(numerusGenus)).buildFlexionsreihe(stamm).im(kasus);
    }

    /**
     * Gibt die prädikative Form des Verbs zurück ("glücklich", "hoch").
     */
    @NonNull
    public String getPraedikativ() {
        return praedikativ;
    }

    public AdjPhrOhneErgaenzungenOhneLeerstellen toAdjPhr() {
        return new AdjPhrOhneErgaenzungenOhneLeerstellen(this);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Adjektiv verb = (Adjektiv) o;
        return praedikativ.equals(verb.praedikativ);
    }

    @Override
    public int hashCode() {
        return Objects.hash(praedikativ);
    }

    @NonNull
    @Override
    public String toString() {
        return "Adjektiv{" +
                "prädikative Form='" + praedikativ + '\'' +
                '}';
    }
}
