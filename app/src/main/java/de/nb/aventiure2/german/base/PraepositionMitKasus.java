package de.nb.aventiure2.german.base;

import static de.nb.aventiure2.german.base.Kasus.AKK;
import static de.nb.aventiure2.german.base.Kasus.DAT;
import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToKonstituentenfolge;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import javax.annotation.CheckReturnValue;

/**
 * Eine Präposition, die einen bestimmten Kasus fordert.
 */
public enum PraepositionMitKasus implements KasusOderPraepositionalkasus {
    AN_DAT("an", DAT, "am"),
    AN_AKK("an", AKK),
    AUS("aus", DAT),
    AUF_DAT("auf", DAT),
    AUF_AKK("auf", AKK),
    // IDEA: "ans", "aufs" (Akk) - die sind allerdings seltener obligatorisch,
    //  eher nur bei Infinitiven ("ans Kochen") und in festen Wendungen ("bis aufs Messer").
    //  Eher muss man sie vielleicht manchmal vermeiden.
    //  Vgl. Duden 924ff

    AUSSER_DAT("außer", DAT), // "außerm" generieren wir nicht, kein
    // rechtes Schriftdeutsch

    BEI_DAT("bei", DAT, "beim"),

    DURCH("durch", AKK,
            null, null, "durchs"),

    IN_DAT("in", DAT, "im"),
    IN_AKK("in", AKK,
            null, null, "ins"),

    FUER("für", AKK), // "fürn" generieren wir nicht, auch "fürs" scheint
    // selten obligatorisch zu sein

    HINTER_DAT("hinter", DAT, "hinterm"),

    /**
     * "mit dem Frosch"
     */
    MIT_DAT("mit", DAT),

    NACH("nach", DAT),
    UEBER_DAT("über", DAT),
    UEBER_AKK("über", AKK), // "übern" scheint selten obligatorisch zu sein
    UNTER_DAT("unter", DAT),
    UNTER_AKK("unter", AKK), // "untern" scheint selten obligatorisch zu sein

    /**
     * "vom Tisch"
     */
    VON("von", DAT, "vom"),

    /**
     * "vor Wut"
     */
    VOR("vor", DAT), // "vorm Haus" generieren wir nicht - ist nicht so schön

    ZU("zu", DAT,
            "zum", "zur",
            "zum");

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
     * ("zum")
     */
    @Nullable
    private final String praepositionVerschmolzenM;

    /**
     * Präposition, verschmolzen mit dem femininen definiten Artikel
     * ("zur")
     */
    @Nullable
    private final String praepositionVerschmolzenF;

    /**
     * Präposition, verschmolzen mit dem definiten Artikel im Neutrum
     * ("zum", "ins")
     */
    @Nullable
    private final String praepositionVerschmolzenN;

    PraepositionMitKasus(final String praeposition, final Kasus kasus) {
        this(praeposition, kasus, null);
    }

    PraepositionMitKasus(final String praeposition, final Kasus kasus,
                         @Nullable final String praepositionVerschmolzenMN) {
        this(praeposition, kasus, praepositionVerschmolzenMN,
                null, praepositionVerschmolzenMN);
    }

    PraepositionMitKasus(final String praeposition, final Kasus kasus,
                         @Nullable final String praepositionVerschmolzenM,
                         @Nullable final String praepositionVerschmolzenF,
                         @Nullable final String praepositionVerschmolzenN) {
        this.praeposition = praeposition;
        this.kasus = kasus;
        this.praepositionVerschmolzenM = praepositionVerschmolzenM;
        this.praepositionVerschmolzenF = praepositionVerschmolzenF;
        this.praepositionVerschmolzenN = praepositionVerschmolzenN;
    }

    public Praepositionalphrase mit(
            final SubstPhrOderReflexivpronomen
                    substPhrOderReflexivpronomen) {
        return new Praepositionalphrase(this,
                substPhrOderReflexivpronomen);
    }

    public Konstituente getDescription(final SubstPhrOderReflexivpronomen substPhrOderReflPron) {
        if (substPhrOderReflPron instanceof SubstantivischePhrase) {
            return getDescription((SubstantivischePhrase) substPhrOderReflPron);
        }

        // Ansonsten kann es keine Verschmelzungen geben, weil die anderen Phrasen
        // (Reflexivpronomen) keinen Artikel haben, mit dem die Präposition verschmelzen könnte.
        return getDescriptionUnverschmolzen(substPhrOderReflPron);
    }

    @CheckReturnValue
    public Konstituente getDescription(final SubstantivischePhrase substantivischePhrase) {
        if (kasus == DAT &&
                substantivischePhrase.erlaubtVerschmelzungVonPraepositionMitArtikel()) {
            @Nullable final String praepositionVerschmolzen =
                    getPraepositionVerschmolzen(substantivischePhrase.getNumerusGenus());

            if (praepositionVerschmolzen != null) {
                return joinToKonstituentenfolge(
                        substantivischePhrase.getFokuspartikel(),
                        praepositionVerschmolzen,
                        substantivischePhrase.ohneFokuspartikel().artikellosDatK())
                        .joinToSingleKonstituente();
            }
        }

        if (kasus == AKK &&
                substantivischePhrase.erlaubtVerschmelzungVonPraepositionMitArtikel()) {
            @Nullable final String praepositionVerschmolzen =
                    getPraepositionVerschmolzen(substantivischePhrase.getNumerusGenus());

            if (praepositionVerschmolzen != null) {
                return joinToKonstituentenfolge(
                        substantivischePhrase.getFokuspartikel(),
                        praepositionVerschmolzen,
                        substantivischePhrase.ohneFokuspartikel().artikellosAkkK())
                        .joinToSingleKonstituente();
            }
        }

        return getDescriptionUnverschmolzen(substantivischePhrase);
    }

    @Nullable
    private String getPraepositionVerschmolzen(final NumerusGenus numerusGenus) {
        switch (numerusGenus) {
            case M:
                return praepositionVerschmolzenM;
            case F:
                return praepositionVerschmolzenF;
            case N:
                return praepositionVerschmolzenN;
            default:
                // PL
                return null;
        }
    }

    @NonNull
    @CheckReturnValue
    private Konstituente getDescriptionUnverschmolzen(
            final SubstPhrOderReflexivpronomen substPhrOderReflPron) {
        return joinToKonstituentenfolge(
                substPhrOderReflPron.getFokuspartikel(),
                substPhrOderReflPron.getNegationspartikelphrase(),
                praeposition,
                substPhrOderReflPron
                        .ohneFokuspartikel().ohneNegationspartikelphrase()
                        .imK(kasus))
                .joinToSingleKonstituente();
    }

    public String getPraeposition() {
        return praeposition;
    }

    public Kasus getKasus() {
        return kasus;
    }
}
