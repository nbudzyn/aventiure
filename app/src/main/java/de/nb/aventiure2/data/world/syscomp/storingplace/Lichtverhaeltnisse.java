package de.nb.aventiure2.data.world.syscomp.storingplace;

public enum Lichtverhaeltnisse {
    HELL("ins Helle"), DUNKEL("in die Dunkelheit");

    /**
     * Beschreibt, wohin der SC sich bewegt, wenn er sich in diese Lichtverhältnisse
     * bewegt, also etwas wie "in die Dunkelheit".
     */
    private final String wohin;

    Lichtverhaeltnisse(final String wohin) {
        this.wohin = wohin;
    }

    /**
     * Gibt eine Beschreibung zurück, wohin der SC sich bewegt, wenn er sich in diese
     * Lichtverhältnisse bewegt, also etwas wie "in die Dunkelheit".
     */
    public String getWohin() {
        return wohin;
    }
}
