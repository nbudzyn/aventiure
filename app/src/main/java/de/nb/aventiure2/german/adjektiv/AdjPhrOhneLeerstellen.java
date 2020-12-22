package de.nb.aventiure2.german.adjektiv;

import javax.annotation.Nullable;

/**
 * Eine Adjektivphrase, bei der alle geforderten Ergänzungen gesetzt sind:
 * <ul>
 * <li>glücklich
 * <li>glücklich, dich zu sehen
 * </ul>
 */
public interface AdjPhrOhneLeerstellen extends Adjektivphrase {
    AdjPhrOhneLeerstellen mitGraduativerAngabe(@Nullable GraduativeAngabe graduativeAngabe);
}