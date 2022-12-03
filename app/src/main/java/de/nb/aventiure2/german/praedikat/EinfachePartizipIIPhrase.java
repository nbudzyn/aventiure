package de.nb.aventiure2.german.praedikat;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Objects.requireNonNull;
import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToKonstituentenfolge;

import androidx.annotation.NonNull;

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld;

/**
 * Eine einfache unflektierte Phrase mit Partizip II, einschließlich der Information,
 * welche Hilfsverb verlangt ist: "unten angekommen (sein)",
 * "die Kugel genommen (haben)".
 * <p>
 * Implizit (oder bei reflexiven Verben auch explizit) hat eine Partizip-II-Phrase
 * eine Person und einen Numerus - Beispiel:
 * "[Ich habe] die Kugel an mich genommen"
 * (nicht *"[Ich habe] die Kugel an sich genommen")
 */
@Immutable
public class EinfachePartizipIIPhrase implements PartizipIIPhrase {

    /**
     * Der Konnektor vor dieser Patizip-II-Phase:
     * "und", "aber", "oder" oder "sondern"
     */
    @Nullable
    private final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld konnektor;

    /**
     * Mittelfeld der Phrase, z.B.:
     * <ul>
     * <li>leer (z.B. bei "gelaufen")
     * <li>"unten[ angekommen]"
     * <li>"die Kugel[ genommen]"
     * <li>"[Er hat ]ein guter Mensch[ haben werden wollen]"
     * <li>"[du wirst ]den Weg[ gelaufen sein]"
     * <li>"[du bist ]den Weg[ gelaufen]"
     * <li>"[du hast ]Spannendes[ berichtet]"
     * </ul>
     * <p>
     * Implizit (oder bei reflexiven Verben auch explizit) hat das Mittelfeld
     * eine Person und einen Numerus - Beispiel:
     * "[Ich habe] die Kugel an mich[ genommen]"
     * (nicht *"[Ich habe] die Kugel an sich[ genommen]")
     */
    private final Mittelfeld mittelfeld;

    /**
     * Der Verbalkomplex der Phrase, z.B:
     * <ul>
     * <li>"gelaufen"
     * <li>"[unten ]angekommen"
     * <li>"[die Kugel [genommen]"
     * <li>"[du wirst den Weg ]gelaufen[ sein]"
     * <li>"[du bist den Weg ]gelaufen"
     * <li>"[du hast Spannendes ]berichtet"
     * </ul>
     */
    private final String partizipII;
    // FIXME Alle Verwender dieses Verbalkomplexes prüfen! Möglicherweise darf
    //  nicht eine Partizip-II-Phrase verwendet werden, sondern es muss ein
    //  Ersatzinfinitiv verwendet werden mit dem Verb haben im Oberfeld.

    private final Nachfeld nachfeld;

    /**
     * Ggf. ein "spezielles" Vorfeld, das (bei Sätzen, die ein Vorfeld haben)
     * der Stellung, wo das Subjekt im Vorfeld ist, vorgezogen werden sollte.
     * Muss bereits in {@link #mittelfeld} oder {@link #nachfeld} enthalten sein.
     */
    @Nullable
    private final Vorfeld speziellesVorfeldSehrErwuenscht;

    /**
     * Ggf. ein "spezielles" Vorfeld, das (bei Sätzen, die ein Vorfeld haben)
     * optional (um weitere Alternativen zu haben) verwendet werden kann.
     * Muss bereits in {@link #mittelfeld} oder {@link #nachfeld}
     * enthalten sein.
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
     * Es gibt mehrere Vordergrund-Informationen, von denen
     * eine besonders akzentuiert werden soll. Diese Information
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

    /**
     * Welche Hilfsverb ist verlangt - "(unten angekommen) sein" oder
     * "(die Kugel genommen) haben"?
     */
    private final Perfektbildung perfektbildung;

    EinfachePartizipIIPhrase(
            @Nullable final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld konnektor,
            final Mittelfeld mittelfeld,
            final String partizipII,
            final Nachfeld nachfeld,
            final Perfektbildung perfektbildung,
            @Nullable final Vorfeld speziellesVorfeldSehrErwuenscht,
            @Nullable final Vorfeld speziellesVorfeldAlsWeitereOption,
            @Nullable final Konstituentenfolge relativpronomen,
            @Nullable final Konstituentenfolge erstesInterrogativwort) {
        this.konnektor = konnektor;
        checkNotNull(mittelfeld, "mittelfeld ist null");
        checkNotNull(partizipII, "verbalkomplex ist null");
        checkNotNull(nachfeld, "nachfeld ist null");
        checkNotNull(perfektbildung, "perfektbildung ist null");

        this.mittelfeld = mittelfeld;
        this.partizipII = partizipII;
        this.nachfeld = nachfeld;
        this.perfektbildung = perfektbildung;
        checkArgument(speziellesVorfeldSehrErwuenscht == null
                        || mittelfeld.contains(
                speziellesVorfeldSehrErwuenscht.toKonstituentenfolge())
                        || nachfeld.contains(speziellesVorfeldSehrErwuenscht),
                this + " enthält nicht speziellesVorfeldSehrErwuenscht: "
                        + speziellesVorfeldSehrErwuenscht);
        this.speziellesVorfeldSehrErwuenscht = speziellesVorfeldSehrErwuenscht;

        checkArgument(speziellesVorfeldAlsWeitereOption == null
                        || mittelfeld.contains(
                speziellesVorfeldAlsWeitereOption.toKonstituentenfolge())
                        || nachfeld.contains(speziellesVorfeldAlsWeitereOption),
                this + " enthält nicht speziellesVorfeldAlsWeitereOption: "
                        + speziellesVorfeldAlsWeitereOption);
        this.speziellesVorfeldAlsWeitereOption = speziellesVorfeldAlsWeitereOption;
        this.relativpronomen = relativpronomen;
        this.erstesInterrogativwort = erstesInterrogativwort;
    }

    @Override
    public EinfachePartizipIIPhrase mitKonnektorUndFallsKeinKonnektor() {
        if (getKonnektor() != null) {
            return this;
        }

        return mitKonnektor(NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld.UND);
    }

    @Override
    public EinfachePartizipIIPhrase mitKonnektor(
            @Nullable final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld konnektor) {
        if (konnektor == null) {
            return this;
        }

        return new EinfachePartizipIIPhrase(
                konnektor,
                mittelfeld,
                partizipII,
                nachfeld,
                perfektbildung,
                speziellesVorfeldSehrErwuenscht,
                speziellesVorfeldAlsWeitereOption,
                relativpronomen,
                erstesInterrogativwort);
    }

    @Override
    public EinfachePartizipIIPhrase ohneKonnektor() {
        if (konnektor == null) {
            return this;
        }

        return new EinfachePartizipIIPhrase(
                null,
                mittelfeld,
                partizipII,
                nachfeld,
                perfektbildung,
                speziellesVorfeldSehrErwuenscht,
                speziellesVorfeldAlsWeitereOption,
                relativpronomen,
                erstesInterrogativwort);
    }

    @Override
    public Konstituentenfolge toKonstituentenfolgeOhneNachfeld(
            @Nullable final String finiteVerbformFuerOberfeld,
            final boolean nachfeldEingereiht) {
        return joinToKonstituentenfolge(
                konnektor, // "und"
                mittelfeld, // "über die Straße"
                nachfeldEingereiht ? nachfeld : null, // "bei schlechtem Wetter"
                finiteVerbformFuerOberfeld, // "ist"
                partizipII); // "gelaufen"
    }

    @Override
    @Nullable
    public NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld getKonnektor() {
        return konnektor;
    }

    @NonNull
    public Mittelfeld getMittelfeld() {
        return mittelfeld;
    }

    @NonNull
    public String getPartizipII() {
        return partizipII;
    }

    @Nonnull
    @Override
    @NonNull
    public Nachfeld getNachfeld() {
        return nachfeld;
    }

    @Override
    @NonNull
    public Perfektbildung getPerfektbildung() {
        return perfektbildung;
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
    @Nullable
    public Konstituentenfolge getRelativpronomen() {
        return relativpronomen;
    }

    @Override
    @Nullable
    public Konstituentenfolge getErstesInterrogativwort() {
        return erstesInterrogativwort;
    }

    @NonNull
    @Override
    public String toString() {
        return requireNonNull(toKonstituentenfolge()).joinToSingleKonstituente()
                .toTextOhneKontext();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final EinfachePartizipIIPhrase that = (EinfachePartizipIIPhrase) o;
        return Objects.equals(konnektor, that.konnektor)
                && mittelfeld.equals(that.mittelfeld)
                && partizipII.equals(that.partizipII)
                && Objects.equals(nachfeld, that.nachfeld) && Objects
                .equals(speziellesVorfeldSehrErwuenscht, that.speziellesVorfeldSehrErwuenscht)
                && Objects
                .equals(speziellesVorfeldAlsWeitereOption, that.speziellesVorfeldAlsWeitereOption)
                && perfektbildung == that.perfektbildung
                && Objects.equals(relativpronomen, that.relativpronomen)
                && Objects.equals(erstesInterrogativwort, that.erstesInterrogativwort);
    }

    @Override
    public int hashCode() {
        return Objects.hash(konnektor, mittelfeld, partizipII, nachfeld);
    }
}
