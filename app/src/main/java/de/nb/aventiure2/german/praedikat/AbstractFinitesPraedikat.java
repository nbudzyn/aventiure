package de.nb.aventiure2.german.praedikat;

import static com.google.common.base.Strings.nullToEmpty;

import androidx.annotation.NonNull;

import java.util.Objects;

import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

/**
 * Ein Prädikat mit einem finiten Verb, aus dem man z.B. Verbzweitsätze oder
 * Verbletztsätze erzeugen kann.
 */
public abstract class AbstractFinitesPraedikat {
    /**
     * Die finite Verbform ("kommst") ohne Partikel ("an") etc.
     */
    private final String finiteVerbformOhnePartikel;

    @Nullable
    private final String partikel;

    protected AbstractFinitesPraedikat(final String finiteVerbformOhnePartikel,
                                       @Nullable final String partikel) {
        this.finiteVerbformOhnePartikel = finiteVerbformOhnePartikel;
        this.partikel = partikel;
    }

    public abstract AbstractFinitesPraedikat ohneKonnektor();

    public abstract AbstractFinitesPraedikat mitKonnektor(
            @Nullable final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld konnektor);

    public boolean hasKonnektor() {
        return getKonnektor() != null;
    }

    @Nullable
    public abstract NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld getKonnektor();

    @Nullable
    public abstract Vorfeld getSpeziellesVorfeldSehrErwuenscht();

    @Nullable
    public abstract Vorfeld getSpeziellesVorfeldAlsWeitereOption();

    /**
     * Gibt das Prädikat "in Verbzweitform" zurück - das Verb steht also ganz am Anfang -,
     * wobei dieses Subjekt zusätzlich ins Mittelfeld eingebaut wird.
     * (In einem Verbzweitsatz würde dann noch ein Satzglied vor dem Ganzen stehen, z.B.
     * eine adverbiale Angabe).
     *
     * @param subjekt das Subjekt - muss zur finiten Verbform passen
     */
    public abstract Konstituentenfolge getVerbzweitMitSubjektImMittelfeld(
            SubstantivischePhrase subjekt);

    /**
     * Gibt das Prädikat "in Verbzweitform" zurück - das Verb steht also ganz am Anfang
     * (in einem Verbzweitsatz würde dann noch das Subjekt davor stehen).
     */
    public abstract Konstituentenfolge getVerbzweit();

    /**
     * Gibt das Prädikat "in Verbletztform" zurück - das Verb steht also ganz am Ende, nur noch
     * gefolgt vom Nachfeld. Beispiele:
     * <ul>
     * <li>"etwas zu berichten hast"
     * <li>"durch den Wald hat laufen wollen"
     * <li>"eine Schlange gesehen und sich sehr erschreckt hat"
     * </ul>
     */
    public abstract Konstituentenfolge getVerbletzt(boolean nachfeldNachstellen);

    @Nullable
    public abstract Konstituentenfolge getRelativpronomen();

    @Nullable
    public abstract Konstituentenfolge getErstesInterrogativwort();

    @NonNull
    final String getFiniteVerbformMitPartikel() {
        return nullToEmpty(partikel) + finiteVerbformOhnePartikel;
    }

    String getFiniteVerbformOhnePartikel() {
        return finiteVerbformOhnePartikel;
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
        final AbstractFinitesPraedikat that = (AbstractFinitesPraedikat) o;
        return finiteVerbformOhnePartikel.equals(that.finiteVerbformOhnePartikel) && Objects
                .equals(partikel, that.partikel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(finiteVerbformOhnePartikel, partikel);
    }
}
