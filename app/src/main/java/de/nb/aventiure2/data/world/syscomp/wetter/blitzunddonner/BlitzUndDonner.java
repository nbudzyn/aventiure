package de.nb.aventiure2.data.world.syscomp.wetter.blitzunddonner;

public enum BlitzUndDonner {
    KEIN_BLITZ_ODER_DONNER,

    // FIXME "In der Ferne hört man Donnergrollen"
    //  "Auf einmal hörst du langen Donner in der Ferne"
    // FIXME Blitz schlägt irgendwo ein?
    //  (erst, wenn der SC nicht da ist?)
    DONNERGROLLEN_IN_DER_FERNE,
    BLITZ_UND_DONNER_NICHT_DIREKT_UEBER_EINEM,
    BLITZ_UND_DONNER_DIREKT_UEBER_EINEM;

    public BlitzUndDonner getVorgaenger() {
        if (ordinal() == 0) {
            return values()[values().length - 1];
        }

        return values()[ordinal() - 1];
    }

    public BlitzUndDonner getNachfolger() {
        if (ordinal() == values().length - 1) {
            return values()[0];
        }

        return values()[ordinal() + 1];
    }
}
