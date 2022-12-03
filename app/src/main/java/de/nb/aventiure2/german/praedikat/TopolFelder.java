package de.nb.aventiure2.german.praedikat;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.Konstituentenfolge;

/**
 * Einzelne konkrete topologischen Felder (Mittelfeld, Nachfeld, ...), aus denen ein Satz
 * zusammengesetzt werden kann.
 */
public class TopolFelder {
    static final TopolFelder EMPTY = new TopolFelder(
            Mittelfeld.EMPTY, Nachfeld.EMPTY,
            null, null,
            null, null);

    private final Mittelfeld mittelfeld;

    private final Nachfeld nachfeld;

    /**
     * Ggf. ein "spezielles" Vorfeld, das (bei Sätzen, die ein Vorfeld haben)
     * der Stellung, wo das Subjekt im Vorfeld ist, vorgezogen werden sollte.
     * Muss bereits im Mittelfeld oder Nachfeld enthalten sein.
     */
    @Nullable
    private final Vorfeld speziellesVorfeldSehrErwuenscht;

    /**
     * Ggf. ein "spezielles" Vorfeld, das (bei Sätzen, die ein Vorfeld haben)
     * optional (um weitere Alternativen zu haben) verwendet werden kann.
     * Muss bereits im Mittelfeld oder Nachfeld enthalten sein.
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

    @Nullable
    private final Konstituentenfolge relativpronomen;

    @Nullable
    private final Konstituentenfolge erstesInterrogativwort;

    public TopolFelder(final Mittelfeld mittelfeld, final Nachfeld nachfeld,
                       @Nullable final Vorfeld speziellesVorfeldSehrErwuenscht,
                       @Nullable final Vorfeld speziellesVorfeldAlsWeitereOption,
                       @Nullable final Konstituentenfolge relativpronomen,
                       @Nullable final Konstituentenfolge erstesInterrogativwort) {
        this.mittelfeld = mittelfeld;
        this.nachfeld = nachfeld;

        checkArgument(mittelfeld.contains(speziellesVorfeldSehrErwuenscht)
                        || nachfeld.contains(speziellesVorfeldSehrErwuenscht),
                this + " enthält nicht speziellesVorfeldSehrErwuenscht: "
                        + speziellesVorfeldSehrErwuenscht);
        this.speziellesVorfeldSehrErwuenscht = speziellesVorfeldSehrErwuenscht;

        checkArgument(mittelfeld.contains(speziellesVorfeldAlsWeitereOption)
                        || nachfeld.contains(speziellesVorfeldAlsWeitereOption),
                this + " enthält nicht speziellesVorfeldAlsWeitereOption: "
                        + speziellesVorfeldAlsWeitereOption);
        this.speziellesVorfeldAlsWeitereOption = speziellesVorfeldAlsWeitereOption;

        this.relativpronomen = relativpronomen;
        this.erstesInterrogativwort = erstesInterrogativwort;
    }

    @Nonnull
    public Mittelfeld getMittelfeld() {
        return mittelfeld;
    }

    public Nachfeld getNachfeld() {
        return nachfeld;
    }

    // FIXME Genügt nicht eines davon - mit einer Angabe, wie stark es erwünscht ist?!

    @Nullable
    public Vorfeld getSpeziellesVorfeldSehrErwuenscht() {
        return speziellesVorfeldSehrErwuenscht;
    }

    @Nullable
    public Vorfeld getSpeziellesVorfeldAlsWeitereOption() {
        return speziellesVorfeldAlsWeitereOption;
    }

    @Nullable
    public Konstituentenfolge getRelativpronomen() {
        return relativpronomen;
    }

    @Nullable
    public Konstituentenfolge getErstesInterrogativwort() {
        return erstesInterrogativwort;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TopolFelder that = (TopolFelder) o;
        return mittelfeld.equals(that.mittelfeld) && nachfeld.equals(that.nachfeld) && Objects
                .equals(speziellesVorfeldSehrErwuenscht, that.speziellesVorfeldSehrErwuenscht)
                && Objects
                .equals(speziellesVorfeldAlsWeitereOption, that.speziellesVorfeldAlsWeitereOption)
                && Objects.equals(relativpronomen, that.relativpronomen)
                && Objects.equals(erstesInterrogativwort, that.erstesInterrogativwort);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mittelfeld, nachfeld, speziellesVorfeldSehrErwuenscht,
                speziellesVorfeldAlsWeitereOption);
    }

    @Override
    public String toString() {
        return "TopolFelder{" +
                "mittelfeld=" + mittelfeld +
                ", nachfeld=" + nachfeld +
                ", speziellesVorfeldSehrErwuenscht=" + speziellesVorfeldSehrErwuenscht +
                ", speziellesVorfeldAlsWeitereOption=" + speziellesVorfeldAlsWeitereOption +
                ", relativpronomen=" + relativpronomen +
                ", erstesInterrogativwort=" + erstesInterrogativwort +
                '}';
    }
}
