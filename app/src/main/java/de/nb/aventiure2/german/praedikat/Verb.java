package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

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
    //  Klassen / Interfaces für Konstituenten gewisser Kategorien
    //    Kategorie(instanze)n oder Subkategorien
    //  Methoden für sytaktische Relationen (mitAdvAngabe(Präpostionalphrase)), die
    //  eine neue KatXKonsituente erzeugen, in der die zrsprügliche
    //  Konstituenten (oder Wörter? Wortformen?) In dieser Relation
    //  stehen.

    /**
     * Infinitiv des Verbs ("aufheben")
     */
    @NonNull
    private final String infinitiv;

    /**
     * 2. Person Singular Präsens Indikativ des Verbs, ggf. ohne abgetrenntes Präfix
     * ("hebst")
     */
    @NonNull
    private final String duForm;

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
    public Verb(final String infinitiv, final String duForm,
                final Perfektbildung perfektbildung, final String partizipII) {
        this(infinitiv, duForm, null, perfektbildung, partizipII);
    }

    public Verb(final String infinitiv, final String duForm,
                @Nullable final String partikel, final Perfektbildung perfektbildung,
                final String partizipII) {
        this.infinitiv = infinitiv;
        this.duForm = duForm;
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
    public String getDuForm() {
        return duForm;
    }

    @Nullable
    public String getPartikel() {
        return partikel;
    }

    public Perfektbildung getPerfektbildung() {
        return perfektbildung;
    }

    public boolean isPartikelverb() {
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
                duForm.equals(verb.duForm) &&
                Objects.equals(partikel, verb.partikel) &&
                perfektbildung == verb.perfektbildung &&
                partizipII.equals(verb.partizipII);
    }

    @Override
    public int hashCode() {
        return Objects.hash(infinitiv, duForm, partikel, perfektbildung, partizipII);
    }

    @NonNull
    @Override
    public String toString() {
        return "Verb{" +
                "infinitiv='" + infinitiv + '\'' +
                '}';
    }

}
