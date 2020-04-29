package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

/**
 * Repräsentiert ein Verb als Lexem, von dem Wortformen gebildet werden können - jedoch <i>ohne
 * Informationen zur Valenz</i>.
 */
public class Verb {
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

    /**
     * Erzeugt ein Verb, das <i>kein</i> Partikelverb ist, also ein Verb
     * <i>ohne Partikel</i>. Von dem Verb wird also (anders als z.B.
     * bei "er steht auf") bei der Formenbildung nichts abgetrennt.
     */
    public Verb(@NonNull final String infinitiv, @NonNull final String duForm) {
        this(infinitiv, duForm, null);
    }

    public Verb(@NonNull final String infinitiv, @NonNull final String duForm,
                @Nullable final String partikel) {
        this.infinitiv = infinitiv;
        this.duForm = duForm;
        this.partikel = partikel;
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
                Objects.equals(partikel, verb.partikel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(infinitiv, duForm, partikel);
    }
}
