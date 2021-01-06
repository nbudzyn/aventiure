package de.nb.aventiure2.german.base;

/**
 * Eine Phrase, die dekliniert werden kann.
 */
public interface DeklinierbarePhrase {
    default String imStr(final Kasus kasus) {
        switch (kasus) {
            case NOM:
                return nomStr();
            case DAT:
                return datStr();
            case AKK:
                return akkStr();
            default:
                throw new IllegalArgumentException("Unexpected kasus: " + kasus);
        }
    }

    String nomStr();

    String datStr();

    String akkStr();
}
