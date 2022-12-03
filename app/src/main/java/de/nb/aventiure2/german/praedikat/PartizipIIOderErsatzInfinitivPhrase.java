package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld;

/**
 * Eine unflektierte Phrase mit Partizip II oder eine Infinitiv-Phrase,
 * einschließlich der Information, welches Hilfsverb für das
 * Perfekt verlangt ist:
 * "unten angekommen (sein)", "die Kugel genommen (haben)",
 * "(haben) nehmen wollen".
 * <p>
 * Implizit (oder bei reflexiven Verben auch explizit) hat eine solche Phrase
 * eine Person und einen Numerus - Beispiel:
 * "[Ich habe] die Kugel an mich genommen"
 * (nicht *"[Ich habe] die Kugel an sich genommen")
 * <p>
 * Partizip-II-Phrasen können nur nur zusammengefasst werden, wenn
 * alle Bestandteile dasselbe Hilfsverb verlangen: <i>um die Ecke gekommen und seinen Freund
 * gesehen</i> ist keine zulässige zusammengesetzte Partizip-II-Phrase, da weder
 * <i>*er ist um die Ecke gekommen und seinen Freund gesehen</i> noch
 * <i>*er hat um die Ecke gekommen und seinen Freund gesehen</i> zulässig ist.
 */
@Immutable
public interface PartizipIIOderErsatzInfinitivPhrase
        extends IInfinitesPraedikat {
    /**
     * Schachtelt ein bestehende Partizip II (oder Ersatzinfinitiv) in ein äußeres Partizip II
     * des entsprechenden Hilfsverbs ein - oder ggf. in einen
     * Ersatzinfinitiv.
     * Beispiele:
     * <ul>
     * <li>Schachtelt "ein guter Mensch geworden" ein in das
     *      Modalverb-Partizip "gewesen": "ein guter Mensch geworden gewesen".
     * <li>Schachtelt "[er hat ]ein guter Mensch werden wollen" (Ersatzinifinitiv)
     * ein in das Modalverb-Partizip "haben":
     * "[er hat ]ein guter Mensch haben werden wollen" ("haben" ist ebenfalls Ersatzinfinitiv,
     * wird außerdem in das Oberfeld gestellt.)
     * </ul>
     * <p>
     * Man sollte schon einen sehr guten Grund haben, so etwas zu erzeugen.
     */
    static PartizipIIOderErsatzInfinitivPhrase doppeltesPartizipIIOderErsatzinfinitiv(
            final PartizipIIOderErsatzInfinitivPhrase lexikalischerKern,
            final boolean hilfsverbAlsErsatzinfinitivVorangestellt) {
        if (hilfsverbAlsErsatzinfinitivVorangestellt
                && lexikalischerKern instanceof EinfacherInfinitiv) {
            // Sehr seltener Sonderfall:
            // "(Das hatte er vorher nicht) haben wissen wollen.":  (Ersatzinfinitiv
            // "wollen", außerdem Ersatzinfinitiv "haben" und "haben"
            // vorangestellt!)
            return new KomplexerInfinitiv(
                    lexikalischerKern.getHilfsverbFuerPerfekt().getInfinitiv(),
                    lexikalischerKern.getHilfsverbFuerPerfekt().getPerfektbildung(),
                    lexikalischerKern);
        }

        // Regelfall: Schachtelt "ein guter Mensch geworden" ein in das
        // Modalverb-Partizip "gewesen": "ein guter Mensch geworden gewesen".
        return KomplexePartizipIIPhrase.doppeltesPartizipII(lexikalischerKern);
    }

    @Override
    PartizipIIOderErsatzInfinitivPhrase mitKonnektor(
            @Nullable NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld konnektor);

    @Override
    default PartizipIIOderErsatzInfinitivPhrase mitKonnektorUndFallsKeinKonnektor() {
        return (PartizipIIOderErsatzInfinitivPhrase) IInfinitesPraedikat.super
                .mitKonnektorUndFallsKeinKonnektor();
    }

    /**
     * Gibt zurück, welches Hilfsverb für das Perfekt verlangt ist - "(unten angekommen) sein" oder
     * "(die Kugel genommen) haben"?
     */
    default Verb getHilfsverbFuerPerfekt() {
        switch (getPerfektbildung()) {
            case HABEN:
                return HabenUtil.VERB;
            case SEIN:
                return SeinUtil.VERB;
            default:
                throw new IllegalStateException("Unexpected Perfektbildung");
        }
    }

    @NonNull
    Perfektbildung getPerfektbildung();

    /**
     * Gibt ggf. ein "spezielles" Vorfeld zurück, das (bei Sätzen, die ein Vorfeld haben)
     * der Stellung, wo das Subjekt im Vorfeld ist, vorgezogen werden sollte.
     * Muss bereits in den Ergebnissen von
     * {@link #toKonstituentenfolgeOhneNachfeld(String, boolean)}} oder
     * {@link #getNachfeld()} enthalten sein.
     */
    @Override
    @Nullable
    Vorfeld getSpeziellesVorfeldSehrErwuenscht();

    /**
     * Gibt ggf. ein "spezielles" Vorfeld zurück, das (bei Sätzen, die ein Vorfeld haben)
     * optional (um weitere Alternativen zu haben) verwendet werden kann.
     * Muss bereits in den Ergebnissen von
     * {@link #toKonstituentenfolgeOhneNachfeld(String, boolean)} oder
     * {@link #getNachfeld()} enthalten sein.
     * * <p>
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
    @Override
    @Nullable
    Vorfeld getSpeziellesVorfeldAlsWeitereOption();

    @Override
    @Nullable
    Konstituentenfolge getRelativpronomen();

    @Override
    @Nullable
    Konstituentenfolge getErstesInterrogativwort();

    /**
     * Gibt die Anzahl der reinen Infinitive (also ohne "zu") in dieser
     * Phrase zurück - sofern die Phrase ausschließlich reine
     * Infinitive enthält (und keine Partizipien oder Infinitive mit zu).
     * Sind nicht nur reine Infinitive enthalten, gibt diese Methode
     * {@code null} zurück.
     */
    @Nullable
    Integer getAnzahlGeschachtelteReineInfinitiveWennPhraseNichtsAnderesEnthaelt();
}
