package de.nb.aventiure2.german.base;

/**
 * Eine Phrase, die dekliniert werden kann.
 */
public interface DeklinierbarePhrase {
    default String im(final Kasus kasus) {
        switch (kasus) {
            case NOM:
                return nom();
            case DAT:
                return dat();
            case AKK:
                return akk();
            default:
                throw new IllegalArgumentException("Unexpected kasus: " + kasus);
        }
    }

    public abstract String nom();

    public abstract String dat();

    public abstract String akk();
}
