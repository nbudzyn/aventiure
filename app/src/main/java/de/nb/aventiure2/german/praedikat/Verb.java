package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

import de.nb.aventiure2.german.base.GermanUtil;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;

import static de.nb.aventiure2.german.base.Numerus.SG;
import static de.nb.aventiure2.german.base.Person.P2;

/**
 * Repräsentiert ein Verb als Lexem, von dem Wortformen gebildet werden können - jedoch <i>ohne
 * Informationen zur Valenz</i>.
 */
public class Verb {
    // STORY Kernklassen / Kerninterfaces für
    //  - lexikalisches Wort (Lexem)?
    //  - Wortform??
    //  - Konstituente?
    //  - Konstituente der Kategorie X
    //  Klassen / Interfaces für Konstituenten gewisser Kategorien, Kategorie(instanze)n oder
    //  Subkategorien
    //  Methoden für sytaktische Relationen (mitAdvAngabe(Präpostionalphrase)), die
    //  eine neue KatXKonsituente erzeugen, in der die ursprügliche
    //  Konstituenten (oder Wörter? Wortformen?) in dieser Relation
    //  stehen.

    /**
     * /**
     * Infinitiv des Verbs ("aufheben")
     */
    @NonNull
    private final String infinitiv;

    /**
     * 2. Person Singular Präsens Indikativ des Verbs, ggf. ohne abgetrenntes Präfix
     * ("hebst")
     */
    @NonNull
    private final String duFormOhnePartikel;

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
    public Verb(final String infinitiv, final String duFormOhnePartikel,
                final Perfektbildung perfektbildung, final String partizipII) {
        this(infinitiv, duFormOhnePartikel, null, perfektbildung, partizipII);
    }

    public Verb(final String infinitiv, final String duFormOhnePartikel,
                @Nullable final String partikel, final Perfektbildung perfektbildung,
                final String partizipII) {
        this.infinitiv = infinitiv;
        this.duFormOhnePartikel = duFormOhnePartikel;
        this.partikel = partikel;
        this.perfektbildung = perfektbildung;
        this.partizipII = partizipII;
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

    @NonNull
    String getPraesensMitPartikel(final Person person, final Numerus numerus) {
        if (person == P2 && numerus == SG) {
            return getDuFormMitPartikel();
        }

        throw new IllegalStateException("Not yet implemented");
    }

    @NonNull
    private String getDuFormMitPartikel() {
        return GermanUtil.joinToNullString(
                duFormOhnePartikel,
                partikel
        );
    }

    @NonNull
    String getDuFormOhnePartikel() {
        return duFormOhnePartikel;
    }

    @Nullable
    String getPartikel() {
        return partikel;
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
                duFormOhnePartikel.equals(verb.duFormOhnePartikel) &&
                Objects.equals(partikel, verb.partikel) &&
                perfektbildung == verb.perfektbildung &&
                partizipII.equals(verb.partizipII);
    }

    @Override
    public int hashCode() {
        return Objects.hash(infinitiv, duFormOhnePartikel, partikel, perfektbildung, partizipII);
    }

    @NonNull
    @Override
    public String toString() {
        return "Verb{" +
                "infinitiv='" + infinitiv + '\'' +
                '}';
    }
}
