package de.nb.aventiure2.german.praedikat;

import static de.nb.aventiure2.german.base.Kasus.AKK;
import static de.nb.aventiure2.german.base.Kasus.DAT;
import static de.nb.aventiure2.german.base.Personalpronomen.EXPLETIVES_ES;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativSkopusSatz;
import de.nb.aventiure2.german.base.Kasus;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

/**
 * Ein Verb (ggf. mit Präfix), bei dem das Subjekt fehlen kann oder nur
 * durch ein expletives "es" realisiert wird - und das mit einem Objekt steht:
 * "Mich friert" / "Es friert mich", "Dir graut vor ihm" etc.
 */
public enum VerbOhneSubjAusserOptionalemExpletivemEs
        implements VerbMitValenz, PraedikatMitEinerObjektleerstelle {
    // Verben ohne Partikel
    DUERSTEN("dürsten", AKK, "dürstet", "gedürstet"),
    EKELN("ekeln", AKK, "ekelt", "geekelt"),
    FRIEREN(Witterungsverb.FRIEREN, AKK),
    FROESTELN(VerbSubj.FROESTELN, AKK),
    GRAUEN("grauen", DAT, "graut", "gegraut"),
    GRAUSEN("grausen", DAT, "graust", "gegraust"),
    HUNGERN("hungern", AKK, "hungert", "gehungert"),
    SCHAUDERN("schaudern", DAT, "schaudert", "geschaudert"),
    SCHWINDELN("schwindeln", DAT, "schwindelt", "geschwindelt");

    /**
     * Das Verb an sich, ohne Informationen zur Valenz, ohne Ergänzungen, ohne
     * Angaben
     */
    @NonNull
    private final Verb verb;

    /**
     * Der Kasus (z.B. Akkusativ, "die Kugel nehmen") oder Präpositionalkasus
     * (z.B. "mit dem Frosch reden"), mit dem dieses Verb steht (den dieses Verb regiert).
     */
    @NonNull
    private final Kasus kasus;


    VerbOhneSubjAusserOptionalemExpletivemEs(final String infinitiv,
                                             final Kasus kasus,
                                             final String esForm,
                                             final String partizipII) {
        this(new Verb(infinitiv, null, null,
                esForm, null, Perfektbildung.HABEN,
                partizipII), kasus);
    }

    VerbOhneSubjAusserOptionalemExpletivemEs(final VerbMitValenz verbMitValenz,
                                             final Kasus kasus) {
        this(verbMitValenz.getVerb().mitPerfektbildung(Perfektbildung.HABEN), kasus);
    }

    VerbOhneSubjAusserOptionalemExpletivemEs(final Verb verb,
                                             final Kasus kasus) {
        this.verb = verb;
        this.kasus = kasus;
    }

    public PraedikatMitEinerObjLeerstelle mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabe) {
        return new PraedikatMitEinerObjLeerstelle(verb, kasus,
                true, advAngabe, null);
    }

    public PraedikatMitEinerObjLeerstelle mitAdvAngabe(
            @Nullable final AdvAngabeSkopusVerbWohinWoher advAngabe) {
        return new PraedikatMitEinerObjLeerstelle(verb, kasus,
                true, null, advAngabe);
    }

    @Override
    public PraedikatSubjObjOhneLeerstellen mit(final SubstantivischePhrase substPhr) {
        return new PraedikatSubjObjOhneLeerstellen(
                verb, kasus, true,
                substPhr);
    }

    @Nullable
    public String getPraesensOhnePartikel() {
        return verb.getPraesensOhnePartikel(EXPLETIVES_ES);
    }

    @Override
    @NonNull
    public Verb getVerb() {
        return verb;
    }
}
