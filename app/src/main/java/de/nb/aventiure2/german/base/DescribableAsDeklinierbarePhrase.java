package de.nb.aventiure2.german.base;

/**
 * Etwas das sich mit einer deklinierbaren Phrase (z.B. "die goldene Kugel" oder
 * "der Sinn des Lebens") beschreiben lässt.
 */
public interface DescribableAsDeklinierbarePhrase {
    /**
     * Gibt eine Beschreibung in diesem
     * Kasus ("die Tür") oder Präspositionalkasus ("mit der Tür") zurück.
     *
     * @param shortIfKnown wenn das Wesen / Objekt / Konzept dem Spieler bereits bekannt ist, dann
     *                     wird eine kurze Beschreibung gewählt ("die Kugel" statt
     *                     "die goldene Kugel")
     */
    String im(final KasusOderPraepositionalkasus kasusOderPraepositionalkasus,
              final boolean shortIfKnown);

    /**
     * Returns the normal description as Nominativ.
     */
    String nom();

    /**
     * Returns the description as Nominativ - short, if the entity or concept
     * is already knwon to the player
     */
    String nom(final boolean shortIfKnown);

    /**
     * Returns the normal description as Dativ
     */
    String dat();

    /**
     * Returns the description as Dativ - short, if the entity or concept is already knwon to the player
     */
    String dat(final boolean shortIfKnown);

    /**
     * Returns the normal description as Akkusativ.
     */
    String akk();

    /**
     * Returns the description as Akkusativ - short, if the entity or concept is already knwon to the player
     */
    String akk(final boolean shortIfKnown);

    /**
     * Gibt die Beschreibung als deklinierbare Phrase zurück
     *
     * @param shortIfKnown ob - wenn das Wesen / Objekt / Konzept dem SC bekannt ist,
     *                     eine <i>kurze</i> Phrase verwendet werden soll
     */
    DeklinierbarePhrase getDescription(final boolean shortIfKnown);
}
