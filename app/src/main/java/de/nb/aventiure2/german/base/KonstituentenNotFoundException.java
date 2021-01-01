package de.nb.aventiure2.german.base;

// FIXME Prüfen: Noch benötigt? Sonst zu RuntimeException umbauen.
public class KonstituentenNotFoundException extends RuntimeException {
    KonstituentenNotFoundException(final String message) {
        super(message);
    }
}
