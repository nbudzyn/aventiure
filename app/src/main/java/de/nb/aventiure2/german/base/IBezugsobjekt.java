package de.nb.aventiure2.german.base;

import javax.annotation.concurrent.Immutable;

/**
 * Eine Person, ein Gegenstand, ein Konzept o.Ä., auf das sich ein sprachliches Elemente
 * (z.B. ein Substantiv oder ein Personalpronomen) beziehen könnte.
 * <p>
 * Beispiel: "Der Mann steht mitten auf der Straße. Jeder sieht ihn." In diesem Beispiel
 * ist das Bezugsobjekt von "der Mann" und "ihn" ein bestimmter Mann, z.B. Peter.
 * <p>
 * Alle implementierungen müssen Immutable sein.
 */
@Immutable
public interface IBezugsobjekt {
}
