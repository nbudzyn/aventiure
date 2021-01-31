package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.Collection;

import javax.annotation.Nullable;

import de.nb.aventiure2.annotations.Komplement;
import de.nb.aventiure2.annotations.Valenz;
import de.nb.aventiure2.german.base.Interrogativpronomen;
import de.nb.aventiure2.german.base.KasusOderPraepositionalkasus;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.base.Reflexivpronomen;
import de.nb.aventiure2.german.base.SubstPhrOderReflexivpronomen;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static de.nb.aventiure2.german.base.Kasus.AKK;
import static de.nb.aventiure2.german.base.Kasus.DAT;
import static de.nb.aventiure2.german.base.Konstituentenfolge.kf;

/**
 * Ein Prädikat wie "die Kugel an sich nehmen", das mit einer
 * "reflexiven Präpositionalkonstruktion" und einem Akkusativobjet steht -
 * alle Leerstellen besetzt
 */
class PraedikatReflSubjObjOhneLeerstellen
        extends AbstractAngabenfaehigesPraedikatOhneLeerstellen {
    /**
     * Der Kasus mit dem das Verb reflexiv steht - z.B. Akkusativ ("sich von ... verabschieden")
     * oder ein Präpositionalkasus ("... an sich nehmen")
     */
    @NonNull
    private final KasusOderPraepositionalkasus reflKasusOderPraepositionalKasus;

    /**
     * Der Kasus ("die Kugel [an sich nehmen]") oder Präpositionalkasus
     * (z.B. "[sich] von der Zauberin [verabschieden]"), mit dem dieses Verb inhaltlich
     * steht (zusätzlich zum reflixiven Kasus)
     */
    @NonNull
    private final KasusOderPraepositionalkasus objektKasusOderPraepositionalkasus;

    @Komplement
    private final SubstantivischePhrase objekt;

    @Valenz
    PraedikatReflSubjObjOhneLeerstellen(
            final Verb verb,
            final KasusOderPraepositionalkasus reflKasusOderPraepositionalKasus,
            final KasusOderPraepositionalkasus objektKasusOderPraepositionalkasus,
            final SubstantivischePhrase objekt) {
        this(verb, reflKasusOderPraepositionalKasus, objektKasusOderPraepositionalkasus,
                objekt,
                ImmutableList.of(),
                null, null,
                null);
    }

    private PraedikatReflSubjObjOhneLeerstellen(
            final Verb verb,
            final KasusOderPraepositionalkasus reflKasusOderPraepositionalKasus,
            final KasusOderPraepositionalkasus objektKasusOderPraepositionalkasus,
            final SubstantivischePhrase objekt,
            final Iterable<Modalpartikel> modalpartikeln,
            @Nullable final AdverbialeAngabeSkopusSatz adverbialeAngabeSkopusSatz,
            @Nullable final AdverbialeAngabeSkopusVerbAllg adverbialeAngabeSkopusVerbAllg,
            @Nullable
            final AdverbialeAngabeSkopusVerbWohinWoher adverbialeAngabeSkopusVerbWohinWoher) {
        super(verb,
                modalpartikeln, adverbialeAngabeSkopusSatz, adverbialeAngabeSkopusVerbAllg,
                adverbialeAngabeSkopusVerbWohinWoher);
        this.reflKasusOderPraepositionalKasus = reflKasusOderPraepositionalKasus;
        this.objektKasusOderPraepositionalkasus = objektKasusOderPraepositionalkasus;
        this.objekt = objekt;
    }

    @Override
    public PraedikatReflSubjObjOhneLeerstellen mitModalpartikeln(
            final Collection<Modalpartikel> modalpartikeln) {
        return new PraedikatReflSubjObjOhneLeerstellen(
                getVerb(), reflKasusOderPraepositionalKasus, objektKasusOderPraepositionalkasus,
                objekt,
                Iterables.concat(getModalpartikeln(), modalpartikeln),
                getAdverbialeAngabeSkopusSatz(),
                getAdverbialeAngabeSkopusVerbAllg(),
                getAdverbialeAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public PraedikatReflSubjObjOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusSatz adverbialeAngabe) {
        if (adverbialeAngabe == null) {
            return this;
        }

        return new PraedikatReflSubjObjOhneLeerstellen(
                getVerb(), reflKasusOderPraepositionalKasus, objektKasusOderPraepositionalkasus,
                objekt,
                getModalpartikeln(),
                adverbialeAngabe, getAdverbialeAngabeSkopusVerbAllg(),
                getAdverbialeAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public PraedikatReflSubjObjOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusVerbAllg adverbialeAngabe) {
        if (adverbialeAngabe == null) {
            return this;
        }

        return new PraedikatReflSubjObjOhneLeerstellen(
                getVerb(), reflKasusOderPraepositionalKasus, objektKasusOderPraepositionalkasus,
                objekt,
                getModalpartikeln(),
                getAdverbialeAngabeSkopusSatz(), adverbialeAngabe,
                getAdverbialeAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public PraedikatReflSubjObjOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusVerbWohinWoher adverbialeAngabe) {
        if (adverbialeAngabe == null) {
            return this;
        }

        return new PraedikatReflSubjObjOhneLeerstellen(
                getVerb(), reflKasusOderPraepositionalKasus, objektKasusOderPraepositionalkasus,
                objekt,
                getModalpartikeln(),
                getAdverbialeAngabeSkopusSatz(),
                getAdverbialeAngabeSkopusVerbAllg(),
                adverbialeAngabe
        );
    }

    @Override
    public boolean kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden() {
        return true;
    }

    @Nullable
    @Override
    public Konstituentenfolge getSpeziellesVorfeldAlsWeitereOption(final Person person,
                                                                   final Numerus numerus) {
        @Nullable final Konstituentenfolge speziellesVorfeldFromSuper =
                super.getSpeziellesVorfeldAlsWeitereOption(person, numerus);
        if (speziellesVorfeldFromSuper != null) {
            return speziellesVorfeldFromSuper;
        }

        // Wenn "es" ein Objekt ist, darf es nicht im Vorfeld stehen.
        // (Eisenberg Der Satz 5.4.2)
        // Aber auch andere Personalpronomen wirken im Vorfeld oft eher unangebracht,
        // wenn es sich um ein Objekt handelt.
        // "Ihn nimmst du an dich."
        if (!(objekt instanceof Personalpronomen)) {
            return objekt.imK(objektKasusOderPraepositionalkasus);  // "den Frosch"
        }

        return null;
    }

    @Override
    Konstituentenfolge getMittelfeldOhneLinksversetzungUnbetonterPronomen(
            final Person personSubjekt, final Numerus numerusSubjekt) {
        return Konstituentenfolge.joinToKonstituentenfolge(
                getAdverbialeAngabeSkopusSatzDescriptionFuerMittelfeld(personSubjekt,
                        numerusSubjekt),
                // "aus einer Laune heraus"
                objekt.imK(objektKasusOderPraepositionalkasus), // "die goldene Kugel"
                kf(getModalpartikeln()), // "besser doch"
                getAdverbialeAngabeSkopusVerbTextDescriptionFuerMittelfeld(personSubjekt,
                        numerusSubjekt), // "erneut"
                Reflexivpronomen.get(personSubjekt, numerusSubjekt)
                        .imStr(reflKasusOderPraepositionalKasus), // "an dich",
                getAdverbialeAngabeSkopusVerbWohinWoherDescription(personSubjekt, numerusSubjekt)
                // "in deine Jackentasche"
        );
    }

    @Nullable
    @Override
    SubstPhrOderReflexivpronomen getAkk(
            final Person personSubjekt, final Numerus numerusSubjekt) {
        if (reflKasusOderPraepositionalKasus == AKK) {
            return Reflexivpronomen.get(personSubjekt, numerusSubjekt);
        }

        if (objektKasusOderPraepositionalkasus == AKK) {
            return objekt;
        }

        return null;
    }

    @Nullable
    @Override
    SubstPhrOderReflexivpronomen getZweitesAkk() {
        if (reflKasusOderPraepositionalKasus == AKK && objektKasusOderPraepositionalkasus == AKK) {
            // Dann ist das Objekt das zweite. (Wenn es sowas überhaupt gibt.)
            return objekt;
        }

        return null;
    }

    @Nullable
    @Override
    SubstPhrOderReflexivpronomen getDat(final Person personSubjekt, final Numerus numerusSubjekt) {
        if (reflKasusOderPraepositionalKasus == DAT) {
            return Reflexivpronomen.get(personSubjekt, numerusSubjekt);
        }

        if (objektKasusOderPraepositionalkasus == DAT) {
            return objekt;
        }

        return null;
    }

    @Override
    @Nullable
    public Konstituentenfolge getNachfeld(final Person personSubjekt,
                                          final Numerus numerusSubjekt) {
        return Konstituentenfolge.joinToNullKonstituentenfolge(
                getAdverbialeAngabeSkopusVerbTextDescriptionFuerZwangsausklammerung(personSubjekt,
                        numerusSubjekt),
                getAdverbialeAngabeSkopusSatzDescriptionFuerZwangsausklammerung(personSubjekt,
                        numerusSubjekt)
        );
    }

    @Override
    public boolean umfasstSatzglieder() {
        return true;
    }

    @Override
    public boolean isBezugAufNachzustandDesAktantenGegeben() {
        return true;
    }

    @Nullable
    @Override
    public Konstituentenfolge getErstesInterrogativpronomen() {
        if (objekt instanceof Interrogativpronomen) {
            return objekt.imK(objektKasusOderPraepositionalkasus);
        }

        return null;
    }
}
