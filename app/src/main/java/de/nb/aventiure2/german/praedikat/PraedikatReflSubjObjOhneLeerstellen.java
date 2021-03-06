package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.Collection;
import java.util.Objects;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;

import de.nb.aventiure2.annotations.Komplement;
import de.nb.aventiure2.annotations.Valenz;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativSkopusSatz;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativVerbAllg;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativWohinWoher;
import de.nb.aventiure2.german.base.Interrogativpronomen;
import de.nb.aventiure2.german.base.KasusOderPraepositionalkasus;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.Negationspartikelphrase;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.base.Reflexivpronomen;
import de.nb.aventiure2.german.base.Relativpronomen;
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
                null, null, null,
                null);
    }

    private PraedikatReflSubjObjOhneLeerstellen(
            final Verb verb,
            final KasusOderPraepositionalkasus reflKasusOderPraepositionalKasus,
            final KasusOderPraepositionalkasus objektKasusOderPraepositionalkasus,
            final SubstantivischePhrase objekt,
            final Iterable<Modalpartikel> modalpartikeln,
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabeSkopusSatz,
            @Nullable final Negationspartikelphrase negationspartikelphrase,
            @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabeSkopusVerbAllg,
            @Nullable final IAdvAngabeOderInterrogativWohinWoher advAngabeSkopusVerbWohinWoher) {
        super(verb,
                modalpartikeln,
                advAngabeSkopusSatz, negationspartikelphrase, advAngabeSkopusVerbAllg,
                advAngabeSkopusVerbWohinWoher);
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
                getAdvAngabeSkopusSatz(),
                getNegationspartikel(), getAdvAngabeSkopusVerbAllg(),
                getAdvAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public PraedikatReflSubjObjOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        return new PraedikatReflSubjObjOhneLeerstellen(
                getVerb(), reflKasusOderPraepositionalKasus, objektKasusOderPraepositionalkasus,
                objekt,
                getModalpartikeln(),
                advAngabe, getNegationspartikel(), getAdvAngabeSkopusVerbAllg(),
                getAdvAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public PraedikatReflSubjObjOhneLeerstellen neg() {
        return (PraedikatReflSubjObjOhneLeerstellen) super.neg();
    }

    @Override
    public PraedikatReflSubjObjOhneLeerstellen neg(
            @Nullable final Negationspartikelphrase negationspartikelphrase) {
        if (negationspartikelphrase == null) {
            return this;
        }

        return new PraedikatReflSubjObjOhneLeerstellen(
                getVerb(), reflKasusOderPraepositionalKasus, objektKasusOderPraepositionalkasus,
                objekt,
                getModalpartikeln(),
                getAdvAngabeSkopusSatz(), negationspartikelphrase, getAdvAngabeSkopusVerbAllg(),
                getAdvAngabeSkopusVerbWohinWoher());
    }

    @Override
    public PraedikatReflSubjObjOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        return new PraedikatReflSubjObjOhneLeerstellen(
                getVerb(), reflKasusOderPraepositionalKasus, objektKasusOderPraepositionalkasus,
                objekt,
                getModalpartikeln(),
                getAdvAngabeSkopusSatz(), getNegationspartikel(), advAngabe,
                getAdvAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public PraedikatReflSubjObjOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativWohinWoher advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        return new PraedikatReflSubjObjOhneLeerstellen(
                getVerb(), reflKasusOderPraepositionalKasus, objektKasusOderPraepositionalkasus,
                objekt,
                getModalpartikeln(),
                getAdvAngabeSkopusSatz(),
                getNegationspartikel(), getAdvAngabeSkopusVerbAllg(),
                advAngabe
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

        /*
         * Wenn "es" ein Objekt ist, darf es nicht im Vorfeld stehen.
         * (Eisenberg Der Satz 5.4.2)
         * ("es" ist nicht phrasenbildend, kann also keine Fokuspartikel haben)
         */
        if (Personalpronomen.isPersonalpronomenEs(objekt, objektKasusOderPraepositionalkasus)) {
            return null;
        }

        /*
         * Auch obligatorisch Reflexsivpronomen sind im Vorfeld unmöglich:
         * *Sich steigern die Verluste <-> Die Verluste steigern sich.
         */

        /*
         * Phrasen (auch Personalpronomen) mit Fokuspartikel
         * sind häufig kontrastiv und daher oft für das Vorfeld geeignet.
         */
        if (objekt.getFokuspartikel() != null) {
            // "Nur die Kugel nimmst du"
            return objekt.imK(objektKasusOderPraepositionalkasus);
        }

        return null;
    }

    @Override
    @CheckReturnValue
    Konstituentenfolge getMittelfeldOhneLinksversetzungUnbetonterPronomen(
            final Person personSubjekt, final Numerus numerusSubjekt) {
        return Konstituentenfolge.joinToKonstituentenfolge(
                getAdvAngabeSkopusSatzDescriptionFuerMittelfeld(personSubjekt,
                        numerusSubjekt),
                // "aus einer Laune heraus"
                getNegationspartikel() != null ? kf(getModalpartikeln()) : null,
                // "besser doch (nicht...)"
                getNegationspartikel(), // "nicht"
                objekt.imK(objektKasusOderPraepositionalkasus), // "die goldene Kugel"
                getNegationspartikel() == null ? kf(getModalpartikeln()) : null, // "besser doch"
                getAdvAngabeSkopusVerbTextDescriptionFuerMittelfeld(personSubjekt,
                        numerusSubjekt), // "erneut"
                Reflexivpronomen.get(personSubjekt, numerusSubjekt)
                        .imStr(reflKasusOderPraepositionalKasus), // "an dich",
                getAdvAngabeSkopusVerbWohinWoherDescription(personSubjekt, numerusSubjekt)
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
                getAdvAngabeSkopusVerbTextDescriptionFuerZwangsausklammerung(personSubjekt,
                        numerusSubjekt),
                getAdvAngabeSkopusSatzDescriptionFuerZwangsausklammerung(personSubjekt,
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
    @CheckReturnValue
    public Konstituentenfolge getErstesInterrogativwort() {
        @Nullable
        Konstituentenfolge res = interroAdverbToKF(getAdvAngabeSkopusSatz());
        if (res != null) {
            return res;
        }

        if (objekt instanceof Interrogativpronomen) {
            return objekt.imK(objektKasusOderPraepositionalkasus);
        }

        res = interroAdverbToKF(getAdvAngabeSkopusVerbAllg());
        if (res != null) {
            return res;
        }

        return interroAdverbToKF(getAdvAngabeSkopusVerbWohinWoher());
    }

    @Nullable
    @Override
    @CheckReturnValue
    public Konstituentenfolge getRelativpronomen() {
        if (objekt instanceof Relativpronomen) {
            return objekt.imK(objektKasusOderPraepositionalkasus);
        }

        return null;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final PraedikatReflSubjObjOhneLeerstellen that = (PraedikatReflSubjObjOhneLeerstellen) o;
        return reflKasusOderPraepositionalKasus.equals(that.reflKasusOderPraepositionalKasus) &&
                objektKasusOderPraepositionalkasus.equals(that.objektKasusOderPraepositionalkasus)
                &&
                Objects.equals(objekt, that.objekt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), reflKasusOderPraepositionalKasus,
                objektKasusOderPraepositionalkasus, objekt);
    }
}
