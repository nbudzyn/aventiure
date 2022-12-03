package de.nb.aventiure2.german.description;

import androidx.annotation.Nullable;

import de.nb.aventiure2.german.base.IBezugsobjekt;
import de.nb.aventiure2.german.base.NumerusGenus;
import de.nb.aventiure2.german.base.Personalpronomen;

/**
 * Der textuelle Kontext, in dem etwas beschrieben wird.
 * Der Kontext liefert z.B. Informationen über mögliche anaphorische
 * Bezüge, die möglich sind ("Rapunzel schaut aus dem Fenster. <i>Sie</i> ist
 * schön anzusehen.") oder vielleicht sogar zwingend sind, wenn man
 * z.B. eine possessive Angabe machen möchte ("Rapunzel kämmt <i>ihre</i> Haare.",
 * aber nicht *"Rapunzel kämmt <i>Rapunzels</i> Haare").
 * <p>
 * Implementierungen dürfen durchaus einen Status besitzen und ihn auch als
 * Seiteneffekt ändern, damit ein Aufrufer auch bei verschiedenen Aufrufen
 * in Folge konsistente Ergebnisse erhält.
 */
public interface ITextContext {
    /**
     * Gibt Numerus und Genus für ein anpahorisches Pronomen ("sie", ihre", ...) zurück, <i>wenn ein
     * anaphorischer Bezug auf dieses Bezugsobjekt möglich ist</i> - sonst {@code null} .
     * <p>
     * Vermerkt ggf. (als Seiteneffekt) im Text-Kontext, dass dieses Bezugsobjekt als
     * anaphorischer Bezug möglich sein muss (wenn mehrere Alternative Texte denkbar waren).
     */
    @Nullable
    NumerusGenus getNumerusGenusAnaphWennMgl(final IBezugsobjekt bezugsobjekt);

    /**
     * Gibt ein Personalpronomen  ("sie", ...) zurück, <i>wenn ein
     * anaphorischer Bezug auf dieses Bezugsobjekt möglich ist</i> - sonst {@code null} .
     * <p>
     * Vermerkt ggf. (als Seiteneffekt) im Text-Kontext, dass dieses Bezugsobjekt als
     * anaphorischer Bezug möglich sein muss (wenn mehrere Alternative Texte denkbar waren).
     */
    @Nullable
    Personalpronomen getAnaphPersPronWennMgl(final IBezugsobjekt bezugsobjekt);

    // FIXME Idee: im Kontext alle Bezüge wissen, für die keine Genitivattribute verwendet werden
    //  dürfen! Das setzt das Prädikat. Z.B. Peter sind seine Hände / die Hände kalt. (Nicht
    //  Peters hände). Betrifft wohl Subjekt und "Dativsubjekt".
    //  ("Anapher zwingend": Liste von Objekten, ein Merkmalsbündel (Numerus erc.)
    //  Führt zu "ihm" und "ihre Haare" (alternativ "die Haare" - nicht "Rapunzels Haare").
    //  Festlegen (und wieder löschen??) Im Prädikat oder häufiger im Sart: Meist das Subjekt (ggf.
    //  A und b)))
    //  Vielleicht hat das auch mit Peter wäscht SICH zu tun.

    // FIXME Die extremste Form der Anapher wäre die Ellipse! Auch die könnte
    //  automatisch erzeugt werden. Vergleiche:
    //  1. Ich sehe den Mann und beleidige den Mann.
    //  2. Ich sehe den Mann und beleidige ihn.
    //  3. Ich sehe und beleidige den Mann.
    //  Sowohl Anapher (2.) als auch  eine Ellipse (3.) könnten vielleicht automatisch erzeugt
    //  werden aus den einzelnen Prädikaten "den Mann sehen" und "den Mann beleidigen".
}
