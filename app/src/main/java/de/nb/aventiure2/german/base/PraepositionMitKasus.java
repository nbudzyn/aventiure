package de.nb.aventiure2.german.base;

import static de.nb.aventiure2.german.base.Kasus.AKK;
import static de.nb.aventiure2.german.base.Kasus.DAT;
import static de.nb.aventiure2.german.base.Konstituente.k;
import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToKonstituentenfolge;
import static de.nb.aventiure2.german.base.Person.P3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import javax.annotation.CheckReturnValue;

/**
 * Eine Präposition, die einen bestimmten Kasus fordert.
 */
public enum PraepositionMitKasus implements KasusOderPraepositionalkasus {
    AN_DAT("an", DAT, true, "am"),
    AN_AKK("an", AKK, true),
    AUF_DAT("auf", DAT, true),
    AUF_AKK("auf", AKK, true),
    // IDEA: "ans", "aufs" (Akk) - die sind allerdings seltener obligatorisch,
    //  eher nur bei Infinitiven ("ans Kochen") und in festen Wendungen ("bis aufs Messer").
    //  Eher muss man sie vielleicht manchmal vermeiden.
    //  Vgl. Duden 924ff

    AUS("aus", DAT, true),

    AUSSER_DAT("außer", DAT, false),
    // "außerm" generieren wir nicht, kein rechtes Schriftdeutsch

    BEI_DAT("bei", DAT, true, "beim"),

    DURCH("durch", AKK, true,
            null, null, "durchs"),

    IN_DAT("in", DAT, true, "im"),
    IN_AKK("in", AKK, true,
            null, null, "ins"),

    FUER("für", AKK, true), // "fürn" generieren wir nicht, auch "fürs" scheint
    // selten obligatorisch zu sein

    HINTER_DAT("hinter", DAT, true, "hinterm"),

    /**
     * "mit dem Frosch"
     */
    MIT_DAT("mit", DAT, true),

    NACH("nach", DAT, true),
    NEBEN("neben", DAT, true),
    UEBER_DAT("über", DAT, true),
    UEBER_AKK("über", AKK, true), // "übern" scheint selten obligatorisch zu sein
    UNTER_DAT("unter", DAT, true),
    UNTER_AKK("unter", AKK, true), // "untern" scheint selten obligatorisch zu sein

    /**
     * "vom Tisch"
     */
    VON("von", DAT, true, "vom"),

    /**
     * "vor Wut"
     */
    VOR("vor", DAT, true), // "vorm Haus" generieren wir nicht - ist nicht so schön

    ZU("zu", DAT, true,
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
     * Ob diese Präposition Präpositionaladverbien bildet wie "darauf", "wohinter" etc.
     */
    private final boolean bildetPraepositionaladverbien;

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

    PraepositionMitKasus(final String praeposition, final Kasus kasus,
                         final boolean bildetPraepositionaladverbien) {
        this(praeposition, kasus, bildetPraepositionaladverbien, null);
    }

    PraepositionMitKasus(final String praeposition, final Kasus kasus,
                         final boolean bildetPraepositionaladverbien,
                         @Nullable final String praepositionVerschmolzenMN) {
        this(praeposition, kasus, bildetPraepositionaladverbien, praepositionVerschmolzenMN,
                null, praepositionVerschmolzenMN);
    }

    PraepositionMitKasus(final String praeposition, final Kasus kasus,
                         final boolean bildetPraepositionaladverbien,
                         @Nullable final String praepositionVerschmolzenM,
                         @Nullable final String praepositionVerschmolzenF,
                         @Nullable final String praepositionVerschmolzenN) {
        this.praeposition = praeposition;
        this.kasus = kasus;
        this.bildetPraepositionaladverbien = bildetPraepositionaladverbien;
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
        if (substPhrOderReflPron instanceof Personalpronomen
                // "*Du, Staubsauger, ich rede damit!" (richtig: "mit dir")
                && ((Personalpronomen) substPhrOderReflPron).getPerson() == P3
                && ((Personalpronomen) substPhrOderReflPron).isBelebt()
            // Bei Vorausweisen mit Relativsatz ist kein
            // Präpositionaladverb möglich:
            // "Du darfst über das (*darüber), was ich dir erzählt habe, nicht reden".
            // "das" ist aber auch kein Personalpronomen. :-)
        ) {
            // "Ich warte darauf" (auf den Brief), nicht *"ich warte auf ihn"

            @Nullable final String praepositionaladverbDa = getPraepositionaladverbDa();
            if (praepositionaladverbDa != null) {
                return joinToKonstituentenfolge(
                        substPhrOderReflPron.getFokuspartikel(),
                        substPhrOderReflPron.getNegationspartikelphrase(),
                        k(praepositionaladverbDa)
                                .mitPhorikKandidat(substPhrOderReflPron.getPhorikKandidat()))
                        .joinToSingleKonstituente();
            }
        }

        return joinToKonstituentenfolge(
                substPhrOderReflPron.getFokuspartikel(),
                substPhrOderReflPron.getNegationspartikelphrase(),
                praeposition,
                substPhrOderReflPron
                        .ohneFokuspartikel().ohneNegationspartikelphrase()
                        .imK(kasus))
                .joinToSingleKonstituente();
    }

    @Nullable
    public String getPraepositionaladverbDa() {
        if (!bildetPraepositionaladverbien) {
            return null;
        }

        // Ausnahme:
        if (this == IN_AKK) {
            // "darein" wirkt meist gestelzt
            return "hinein";
            // FIXME Allerdings wäre manchmal "herein" richtig...
        }

        return "da"
                + (beginntMitVokal() ? "r" : "")
                + praeposition;
    }

    @Nullable
    public String getPraepositionaladverbHier() {
        if (!bildetPraepositionaladverbien) {
            return null;
        }

        // Ausnahme:
        if (this == IN_AKK) {
            return "hierein";
        }

        return "hier" + praeposition;
    }

    @Nullable
    public String getPraepositionaladverbWo() {
        if (!bildetPraepositionaladverbien) {
            return null;
        }

        // Ausnahme:
        if (this == IN_AKK) {
            return "worein";
        }

        return "wo"
                + (beginntMitVokal() ? "r" : "")
                + praeposition;
    }

    private boolean beginntMitVokal() {
        return praeposition.startsWith("a")
                || praeposition.startsWith("e")
                || praeposition.startsWith("i")
                || praeposition.startsWith("u")
                || praeposition.startsWith("o");
    }

    public String getPraeposition() {
        return praeposition;
    }

    public Kasus getKasus() {
        return kasus;
    }
}
