package de.nb.aventiure2.german.base;

import androidx.annotation.Nullable;

import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.german.base.Kasus.AKK;
import static de.nb.aventiure2.german.base.Kasus.DAT;
import static de.nb.aventiure2.german.base.NumerusGenus.F;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.NumerusGenus.N;

/**
 * Eine Präposition, die einen bestimmten Kasus fordert.
 */
public enum PraepositionMitKasus implements KasusOderPraepositionalkasus {
    AN_DAT("an", DAT, "am"),
    AN_AKK("an", AKK),
    AUS("aus", DAT),
    IN_DAT("in", DAT, "im"),
    IN_AKK("in", AKK),

    /**
     * "mit dem Frosch"
     */
    MIT_DAT("mit", DAT),

    NACH("nach", DAT),
    ZU("zu", DAT, "zum", "zur");

    /**
     * Die Präposition (z.B. "mit")
     */
    private final String praeposition;

    /**
     * Der Kasus, den diese Präposition fordert.
     */
    private final Kasus kasus;

    /**
     * Präposition, verschmolzen mit dem definiten Artikel im Maskulinum
     * oder Neutrum
     * ("zum")
     */
    @Nullable
    private final String praepositionVerschmolzenMN;

    /**
     * Präposition, verschmolzen mit dem dem femininem definiten Artikel
     * ("zur")
     */
    @Nullable
    private final String praepositionVerschmolzenF;

    private PraepositionMitKasus(final String praeposition, final Kasus kasus) {
        this(praeposition, kasus, null);
    }

    private PraepositionMitKasus(final String praeposition, final Kasus kasus,
                                 @Nullable final String praepositionVerschmolzenMN) {
        this(praeposition, kasus, praepositionVerschmolzenMN, null);
    }

    private PraepositionMitKasus(final String praeposition, final Kasus kasus,
                                 @Nullable final String praepositionVerschmolzenMN,
                                 @Nullable final String praepositionVerschmolzenF) {
        checkArgument(
                kasus == DAT ||
                        (praepositionVerschmolzenMN == null &&
                                praepositionVerschmolzenF == null),
                "Es werden derzeit nur verschmolzene Präpositionen mit "
                        + "Dativ-Artikeln unterstützt");
        // "ins", "ans" und "aufs" sind seltener obligatorisch,
        // eher nur bei Infinitiven ("ans Kochen") und in festen Wendungen ("bis aufs Messer").
        // Vgl. Duden 924ff

        this.praeposition = praeposition;
        this.kasus = kasus;
        this.praepositionVerschmolzenMN = praepositionVerschmolzenMN;
        this.praepositionVerschmolzenF = praepositionVerschmolzenF;
    }

    public String getDescription(final SubstantivischePhrase substantivischePhrase) {
        if (kasus == DAT &&
                // AKK unterstützen wir derzeit nicht
                substantivischePhrase.erlaubtVerschmelzungVonPraepositionMitArtikel()) {
            if (praepositionVerschmolzenMN != null &&
                    (substantivischePhrase.getNumerusGenus() == M ||
                            (substantivischePhrase.getNumerusGenus() == N))) {
                return praepositionVerschmolzenMN + " " +
                        substantivischePhrase.artikellosDat();
            }

            if (praepositionVerschmolzenF != null &&
                    (substantivischePhrase.getNumerusGenus() == F)) {
                return praepositionVerschmolzenF + " " +
                        substantivischePhrase.artikellosDat();
            }
        }

        return praeposition + " " + substantivischePhrase.im(kasus);
    }

    public String getPraeposition() {
        return praeposition;
    }

    public Kasus getKasus() {
        return kasus;
    }
}
