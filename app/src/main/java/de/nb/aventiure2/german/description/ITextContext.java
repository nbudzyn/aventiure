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
 * Implementierungen dürfen durchaus einen Status besitzen und ihr auch als
 * Seiteneffekt ändern, damit ein Aufrufer auch bei verschiedenen Aufrufen
 * in Folge konsistente Daten erhält.
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
}
