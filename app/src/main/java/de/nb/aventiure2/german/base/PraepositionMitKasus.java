package de.nb.aventiure2.german.base;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.german.base.Kasus.AKK;
import static de.nb.aventiure2.german.base.Kasus.DAT;
import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToKonstituentenfolge;
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
    AUF_DAT("auf", DAT),
    AUF_AKK("auf", AKK),

    AUSSER_DAT("außer", DAT), // "außerm" generieren wir nicht, kein rechtes Schriftdeutsch

    IN_DAT("in", DAT, "im"),
    IN_AKK("in", AKK),

    /**
     * "mit dem Frosch"
     */
    MIT_DAT("mit", DAT),

    NACH("nach", DAT),

    /**
     * "vom Tisch"
     */
    VON("von", DAT, "vom"),

    /**
     * "vor Wut"
     */
    VOR("vor", DAT), // "vorm Haus" generieren wir nicht - ist nicht so schön

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
     * Präposition, verschmolzen mit dem femininen definiten Artikel
     * ("zur")
     */
    @Nullable
    private final String praepositionVerschmolzenF;

    PraepositionMitKasus(final String praeposition, final Kasus kasus) {
        this(praeposition, kasus, null);
    }

    PraepositionMitKasus(final String praeposition, final Kasus kasus,
                         @Nullable final String praepositionVerschmolzenMN) {
        this(praeposition, kasus, praepositionVerschmolzenMN, null);
    }

    PraepositionMitKasus(final String praeposition, final Kasus kasus,
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

    public Praepositionalphrase mit(
            final SubstPhrOderReflexivpronomen
                    substPhrOderReflexivpronomen) {
        return new Praepositionalphrase(this,
                substPhrOderReflexivpronomen);
    }

    public Konstituente getDescription(final SubstPhrOderReflexivpronomen substPhrOderReflPron) {
        if (substPhrOderReflPron instanceof EinzelneSubstantivischePhrase) {
            return getDescription((SubstantivischePhrase) substPhrOderReflPron);
        }

        // Ansonsten kann es keine Verschmelzungen geben, weil die anderen Phrasen
        // keinen Artikel haben, mit dem die Präposition verschmelzen könnte.
        return getDescriptionUnverschmolzen(substPhrOderReflPron);
    }

    public Konstituente getDescription(final SubstantivischePhrase substantivischePhrase) {
        if (kasus == DAT &&
                // AKK unterstützen wir derzeit nicht
                substantivischePhrase.erlaubtVerschmelzungVonPraepositionMitArtikel()) {
            if (praepositionVerschmolzenMN != null &&
                    (substantivischePhrase.getNumerusGenus() == M ||
                            (substantivischePhrase.getNumerusGenus() == N))) {
                return joinToKonstituentenfolge(
                        substantivischePhrase.getFokuspartikel(),
                        praepositionVerschmolzenMN,
                        substantivischePhrase.ohneFokuspartikel().artikellosDatK())
                        .joinToSingleKonstituente();
            }

            if (praepositionVerschmolzenF != null &&
                    (substantivischePhrase.getNumerusGenus() == F)) {
                return joinToKonstituentenfolge(
                        substantivischePhrase.getFokuspartikel(),
                        praepositionVerschmolzenF,
                        substantivischePhrase.ohneFokuspartikel().artikellosDatK())
                        .joinToSingleKonstituente();
            }
        }

        return getDescriptionUnverschmolzen(substantivischePhrase);
    }

    @NonNull
    private Konstituente getDescriptionUnverschmolzen(
            final SubstPhrOderReflexivpronomen substPhrOderReflPron) {
        return joinToKonstituentenfolge(
                substPhrOderReflPron.getFokuspartikel(),
                praeposition,
                substPhrOderReflPron.ohneFokuspartikel().imK(kasus))
                .joinToSingleKonstituente();
    }

    public String getPraeposition() {
        return praeposition;
    }

    public Kasus getKasus() {
        return kasus;
    }
}
