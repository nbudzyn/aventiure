package de.nb.aventiure2.german.praedikat;

import static java.util.Objects.requireNonNull;
import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToKonstituentenfolge;

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

/**
 * Ein Prädikat, dessen Verbalkomplex ausschließlich aus einem einzigen finiten Verb
 * (ggf. einschließlich Präfix) besteht, und aus dem man - in der Regeln in Kombination
 * mit einem Subjekt - z.B. Verbzweitsätze oder Verbletztsätze erzeugen kann.
 * <p/>
 * Aus diesem Prädikat kann man Verzweitsätze erzeugen wie
 * <ul>
 * <li>"[er ]läuft" (ohne Präfix)
 * <li>"[du ]gehst über die Straße"
 * <li>"[du ]berichtest Spannendes"
 * <li>"[du ]willst den Schaden"
 * <li>"[er ]schließt seine Geliebte in die Arme"
 * <li>"[er ]sieht seine Geliebte wieder" (Präfix in rechter Satzklammer)
 * <li>"[es ]stellt keine Gefahr dar"
 * <li>"[er ]reagiert sich ab"
 * </ul>
 * <p/>
 * Aus diesem Prädikat kann man Verbletztsätze erzeugen wie
 * <ul>
 * <li>"[er ]läuft" (ohne Präfix)
 * <li>"[du ]über die Straße geht"
 * <li>"[du ]Spannendes berichtest"
 * <li>"[du ]den Schaden willst"
 * <li>"[er ]seine Geliebte in die Arme schließt"
 * <li>"[er ]seine Geliebte wiedersieht" (mit Präfix)
 * <li>"[es ]keine Gefahr darstellt"
 * <li>"[er ]sich abreagiert"
 * </ul>
 */
@Immutable
public class EinfachesFinitesPraedikat extends AbstractFinitesPraedikat {
    /**
     * Der Konnektor vor diesem finiten Prädikat:
     * "und", "aber", "oder" oder "sondern"
     */
    @Nullable
    private final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld konnektor;

    private final Mittelfeld mittelfeld;

    @Nullable
    private final Konstituentenfolge relativpronomen;

    @Nullable
    private final Konstituentenfolge erstesInterrogativwort;

    private final Nachfeld nachfeld;

    /**
     * Ggf. ein "spezielles" Vorfeld, das (bei Sätzen, die ein Vorfeld haben)
     * der Stellung, wo das Subjekt im Vorfeld ist, vorgezogen werden sollte.
     */
    @Nullable
    private final Vorfeld speziellesVorfeldSehrErwuenscht;

    /**
     * Ggf. ein "spezielles" Vorfeld, das (bei Sätzen, die ein Vorfeld haben)
     * optional (um weitere Alternativen zu haben) verwendet werden kann.
     * <p>
     * Sollte von {@link #speziellesVorfeldSehrErwuenscht} abweichen.
     * <p>
     * Generell gibt es gewisse Regeln für das Vorfeld, die berücksichtigt werden müssen:
     * <ul>
     * <li> "es" allein darf nicht im Vorfeld stehen, wenn es ein Objekt ist
     * (Eisenberg Der Satz 5.4.2)
     * <li>Auch obligatorisch Reflexsivpronomen sind im Vorfeld unmöglich:
     * *Sich steigern die Verluste <-> Die Verluste steigern sich.
     * <li>Der ethische Dativ ist im Vorfeld verboten:
     * *Mir komm nur nicht zu spät. <-> Komm mir nur nicht zu spät.
     * <li>"Nicht" ("Negationssupplement") ist im Vorfeld sehr, sehr selten.
     * <li>Tendenziell enthält das Vorfeld (unmarkiert)
     * Hintergrund-Informationen, also die Informationen,
     * die bereits bekannt sind und auf die
     * nicht die Aufmerksamkeit gelenkt werden soll.
     * Daher stehen im Vorfeld zumeist das Subjekt oder
     * adverbiale Angaben.
     * Da Prädikatsteile (Objekte, Prädikative) in der Regel rhematisch sind
     * (nicht Thema), sollen sie in der Regel nicht ins Vorfeld.
     * Es gibt zwei Ausnahmen:
     * <ol>
     * <li>Die "Hintergrundsetzung":
     * Der Prädikatsteil soll als neuer Hintergrund für
     * ein (neues), im SemSatz folgendes Rhema gesetzt werden.
     * Daher sind diese Vorfeldbesetzung mit Nicht-Subjekt
     * mit Negationen im Mittelfeld natürlich:
     * "Den Karl [neues Thema] liebt die Maria aber nicht [Rhema]"
     * "Groß [neues Thema] ist Karl nicht [Rhema]."
     * <li>Die "Hervorhebung":
     * Es gibt mehrere Vorderund-Informationen, von denen
     * eine besonders Akzeptuiert werden soll. Diese Information
     * kann in das Vorfeld gestellt werden ("Hervorhebung").
     * "Hervorhebungen" sind also vor allem möglich, wenn
     * das Prädikats-Mittelfeld
     * - mehrere weitere
     * - oder weitere längere
     * Elemente enthält.
     * <li>Daher sind generell Personalpronomen ohne Fokuspartikel im Vorfeld oft
     * eher unangebracht, wenn es sich um ein Objekt handelt. Selbst wenn
     * das Personalpronomen in einem Präpositionalkasus steht.
     * ?"Dich sieht die Frau überrascht an.", ?"Auf sie wartest du immer noch."
     * </ol>
     * <li> <i>Rhematische</i> prädikative Elemente sind im Vorfeld nur möglich,
     * wenn sie
     * <ol>
     * <li>Antwort auf eine Frage sind ("Dich habe ich gesucht!")
     * <li>oder kontrastiv ("Wir haben eine Katze. Ein Hund
     * kommt uns nicht ins Haus.") - Phrasen (auch Personalpronomen) mit Fokuspartikel
     * sind häufig kontrastiv und daher oft für das Vorfeld geeignet.
     * </ol>
     * </ul>
     */
    @Nullable
    private final Vorfeld speziellesVorfeldAlsWeitereOption;

    EinfachesFinitesPraedikat(
            @Nullable final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld konnektor,
            final String finiteVerbformOhnePartikel,
            final Mittelfeld mittelfeld,
            @Nullable final String partikel,
            final Nachfeld nachfeld,
            @Nullable final Vorfeld speziellesVorfeldSehrErwuenscht,
            @Nullable final Vorfeld speziellesVorfeldAlsWeitereOption,
            @Nullable final Konstituentenfolge relativpronomen,
            @Nullable final Konstituentenfolge erstesInterrogativwort) {
        super(finiteVerbformOhnePartikel, partikel);
        this.konnektor = konnektor;
        this.mittelfeld = mittelfeld;
        this.nachfeld = nachfeld;
        this.speziellesVorfeldSehrErwuenscht = speziellesVorfeldSehrErwuenscht;
        this.speziellesVorfeldAlsWeitereOption = speziellesVorfeldAlsWeitereOption;
        this.erstesInterrogativwort = erstesInterrogativwort;
        this.relativpronomen = relativpronomen;
    }

    public EinfachesFinitesPraedikat mitKonnektor(
            @Nullable final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld konnektor) {
        if (konnektor == null) {
            return this;
        }

        return new EinfachesFinitesPraedikat(
                konnektor,
                getFiniteVerbformOhnePartikel(),
                mittelfeld,
                getPartikel(),
                nachfeld, speziellesVorfeldSehrErwuenscht, speziellesVorfeldAlsWeitereOption,
                relativpronomen, erstesInterrogativwort
        );
    }

    @Override
    public EinfachesFinitesPraedikat ohneKonnektor() {
        if (konnektor == null) {
            return this;
        }

        return new EinfachesFinitesPraedikat(
                null,
                getFiniteVerbformOhnePartikel(),
                mittelfeld,
                getPartikel(),
                nachfeld, speziellesVorfeldSehrErwuenscht, speziellesVorfeldAlsWeitereOption,
                relativpronomen, erstesInterrogativwort
        );
    }

    @Override
    @Nullable
    public NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld getKonnektor() {
        return konnektor;
    }

    @Override
    @Nullable
    public Vorfeld getSpeziellesVorfeldSehrErwuenscht() {
        return speziellesVorfeldSehrErwuenscht;
    }

    @Override
    @Nullable
    public Vorfeld getSpeziellesVorfeldAlsWeitereOption() {
        return speziellesVorfeldAlsWeitereOption;
    }

    @Override
    public Konstituentenfolge getVerbzweitMitSubjektImMittelfeld(
            final SubstantivischePhrase subjekt) {
        requireNonNull(subjekt, "subjekt");

        return joinToKonstituentenfolge(
                konnektor,
                getFiniteVerbformOhnePartikel(), // "erzählst"
                // Damit steht das Subjekt entweder als nicht-pronominales Subjekt vor der
                // Wackernagelposition oder als unbetontes Pronomen zu Anfang der
                // Wackernagelposition:
                subjekt.nomK(), // "du"
                mittelfeld, // "Spanndes"
                getPartikel(), // "nach"
                nachfeld); // ": Herakles hat obsiegt!"
    }

    @Override
    public final Konstituentenfolge getVerbzweit() {
        return joinToKonstituentenfolge(
                konnektor,
                getFiniteVerbformOhnePartikel(),
                mittelfeld,
                getPartikel(),
                nachfeld);
    }

    @Override
    public final Konstituentenfolge getVerbletzt(final boolean nachfeldNachstellen) {
        // "Spannendes berichtest"
        return joinToKonstituentenfolge(
                konnektor,
                mittelfeld, // "Spannendes"
                !nachfeldNachstellen ? nachfeld : null,
                getFiniteVerbformMitPartikel(), // "berichtest"
                nachfeldNachstellen ? nachfeld : null);  // ": Odysseus ist zurück"
    }

    @Override
    @Nullable
    public Konstituentenfolge getRelativpronomen() {
        return relativpronomen;
    }

    @Override
    @Nullable
    public Konstituentenfolge getErstesInterrogativwort() {
        return erstesInterrogativwort;
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final EinfachesFinitesPraedikat that = (EinfachesFinitesPraedikat) o;
        return konnektor == that.konnektor && mittelfeld.equals(that.mittelfeld) && Objects
                .equals(relativpronomen, that.relativpronomen) && Objects
                .equals(erstesInterrogativwort, that.erstesInterrogativwort) && nachfeld
                .equals(that.nachfeld) && Objects
                .equals(speziellesVorfeldSehrErwuenscht, that.speziellesVorfeldSehrErwuenscht)
                && Objects
                .equals(speziellesVorfeldAlsWeitereOption, that.speziellesVorfeldAlsWeitereOption);
    }

    @Override
    public int hashCode() {
        return Objects
                .hash(super.hashCode(), konnektor, mittelfeld, relativpronomen,
                        erstesInterrogativwort,
                        nachfeld, speziellesVorfeldSehrErwuenscht,
                        speziellesVorfeldAlsWeitereOption);
    }

    @Override
    @Nonnull
    public String toString() {
        return "FinitesPraedikat{" +
                "konnektor=" + konnektor +
                ", finiteVerbform='" + getFiniteVerbformOhnePartikel() + '\'' +
                ", mittelfeld=" + mittelfeld +
                ", verbalkomplexrest=" + getPartikel() +
                ", nachfeld=" + nachfeld +
                ", speziellesVorfeldSehrErwuenscht=" + speziellesVorfeldSehrErwuenscht +
                ", speziellesVorfeldAlsWeitereOption=" + speziellesVorfeldAlsWeitereOption +
                ", erstesInterrogativwort=" + erstesInterrogativwort +
                ", relativpronomen=" + relativpronomen +
                '}';
    }
}
