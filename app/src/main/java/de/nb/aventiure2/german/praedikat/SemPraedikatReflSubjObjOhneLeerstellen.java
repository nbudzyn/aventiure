package de.nb.aventiure2.german.praedikat;

import static de.nb.aventiure2.german.base.Kasus.AKK;
import static de.nb.aventiure2.german.base.Konstituentenfolge.kf;
import static de.nb.aventiure2.german.praedikat.Praedikatseinbindung.firstInterrogativwort;
import static de.nb.aventiure2.german.praedikat.Praedikatseinbindung.firstRelativpronomen;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.Collection;
import java.util.Objects;

import javax.annotation.Nullable;

import de.nb.aventiure2.annotations.Komplement;
import de.nb.aventiure2.annotations.Valenz;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativSkopusSatz;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativVerbAllg;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativWohinWoher;
import de.nb.aventiure2.german.base.KasusOderPraepositionalkasus;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.Negationspartikelphrase;
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.base.PraedRegMerkmale;
import de.nb.aventiure2.german.base.Reflexivpronomen;
import de.nb.aventiure2.german.base.SubstantivischPhrasierbar;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.ITextContext;

/**
 * Ein "semantisches Prädikat" wie "die Kugel an sich nehmen", das mit einer
 * "reflexiven Präpositionalkonstruktion" und einem Akkusativobjet steht -
 * alle Leerstellen besetzt
 */
class SemPraedikatReflSubjObjOhneLeerstellen
        extends AbstractAngabenfaehigesSemPraedikatOhneLeerstellen {
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
    private final SubstantivischPhrasierbar objekt;

    @Valenz
    SemPraedikatReflSubjObjOhneLeerstellen(
            final Verb verb,
            final KasusOderPraepositionalkasus reflKasusOderPraepositionalKasus,
            final KasusOderPraepositionalkasus objektKasusOderPraepositionalkasus,
            final SubstantivischPhrasierbar objekt) {
        this(verb, reflKasusOderPraepositionalKasus, objektKasusOderPraepositionalkasus,
                objekt,
                ImmutableList.of(),
                null, null, null,
                null);
    }

    private SemPraedikatReflSubjObjOhneLeerstellen(
            final Verb verb,
            final KasusOderPraepositionalkasus reflKasusOderPraepositionalKasus,
            final KasusOderPraepositionalkasus objektKasusOderPraepositionalkasus,
            final SubstantivischPhrasierbar objekt,
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
    public SemPraedikatReflSubjObjOhneLeerstellen mitModalpartikeln(
            final Collection<Modalpartikel> modalpartikeln) {
        return new SemPraedikatReflSubjObjOhneLeerstellen(
                getVerb(), reflKasusOderPraepositionalKasus, objektKasusOderPraepositionalkasus,
                objekt,
                Iterables.concat(getModalpartikeln(), modalpartikeln),
                getAdvAngabeSkopusSatz(),
                getNegationspartikel(), getAdvAngabeSkopusVerbAllg(),
                getAdvAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public SemPraedikatReflSubjObjOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        return new SemPraedikatReflSubjObjOhneLeerstellen(
                getVerb(), reflKasusOderPraepositionalKasus, objektKasusOderPraepositionalkasus,
                objekt,
                getModalpartikeln(),
                advAngabe, getNegationspartikel(), getAdvAngabeSkopusVerbAllg(),
                getAdvAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public SemPraedikatReflSubjObjOhneLeerstellen neg() {
        return (SemPraedikatReflSubjObjOhneLeerstellen) super.neg();
    }

    @Override
    public SemPraedikatReflSubjObjOhneLeerstellen neg(
            @Nullable final Negationspartikelphrase negationspartikelphrase) {
        if (negationspartikelphrase == null) {
            return this;
        }

        return new SemPraedikatReflSubjObjOhneLeerstellen(
                getVerb(), reflKasusOderPraepositionalKasus, objektKasusOderPraepositionalkasus,
                objekt,
                getModalpartikeln(),
                getAdvAngabeSkopusSatz(), negationspartikelphrase, getAdvAngabeSkopusVerbAllg(),
                getAdvAngabeSkopusVerbWohinWoher());
    }

    @Override
    public SemPraedikatReflSubjObjOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        return new SemPraedikatReflSubjObjOhneLeerstellen(
                getVerb(), reflKasusOderPraepositionalKasus, objektKasusOderPraepositionalkasus,
                objekt,
                getModalpartikeln(),
                getAdvAngabeSkopusSatz(), getNegationspartikel(), advAngabe,
                getAdvAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public SemPraedikatReflSubjObjOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativWohinWoher advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        return new SemPraedikatReflSubjObjOhneLeerstellen(
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
    public Vorfeld getSpeziellesVorfeldAlsWeitereOption(
            final PraedRegMerkmale praedRegMerkmale,
            final SubstantivischePhrase objektPhrase) {
        @Nullable final Vorfeld speziellesVorfeldFromSuper =
                super.getGgfVorfeldAdvAngabeSkopusVerb(praedRegMerkmale);
        if (speziellesVorfeldFromSuper != null) {
            return speziellesVorfeldFromSuper;
        }

        /*
         * "es" allein darf es nicht im Vorfeld stehen, wenn es ein Objekt ist.
         * (Eisenberg Der Satz 5.4.2)
         */
        if (Personalpronomen
                .isPersonalpronomenEs(objektPhrase, objektKasusOderPraepositionalkasus)) {
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
        if (objektPhrase.getFokuspartikel() != null) {
            // "Nur die Kugel nimmst du"
            return new Vorfeld(objektPhrase.imK(objektKasusOderPraepositionalkasus));
        }

        return null;
    }

    @Override
    public boolean hatAkkusativobjekt() {
        return reflKasusOderPraepositionalKasus == AKK
                || objektKasusOderPraepositionalkasus == AKK;
    }

    @Override
    TopolFelder getTopolFelder(final ITextContext textContext,
                               final PraedRegMerkmale praedRegMerkmale,
                               final boolean nachAnschlusswort) {
        final Reflexivpronomen reflexivpronomen = Reflexivpronomen.get(praedRegMerkmale);

        final Konstituente advAngabeSkopusSatzSyntFuerMittelfeld =
                getAdvAngabeSkopusSatzDescriptionFuerMittelfeld(praedRegMerkmale);

        final SubstantivischePhrase objektPhrase = objekt.alsSubstPhrase(textContext);

        final Praedikatseinbindung<SubstantivischePhrase> objektEinbindung =
                new Praedikatseinbindung<>(objektPhrase,
                        o -> objektPhrase.imK(objektKasusOderPraepositionalkasus));

        final Konstituente advAngabeSkopusVerbSyntFuerMittelfeld =
                getAdvAngabeSkopusVerbTextDescriptionFuerMittelfeld(
                        praedRegMerkmale);
        final Konstituente advAngabeSkopusVerbWohinWoherSynt =
                getAdvAngabeSkopusVerbWohinWoherDescription(praedRegMerkmale);


        return new TopolFelder(
                new Mittelfeld(
                        Konstituentenfolge.joinToKonstituentenfolge(
                                advAngabeSkopusSatzSyntFuerMittelfeld,
                                // "aus einer Laune heraus"
                                getNegationspartikel() != null ? kf(getModalpartikeln()) : null,
                                // "besser doch (nicht...)"
                                getNegationspartikel(), // "nicht"
                                objektEinbindung,
                                // "die goldene Kugel"
                                getNegationspartikel() == null ? kf(getModalpartikeln()) : null,
                                // "besser doch"
                                advAngabeSkopusVerbSyntFuerMittelfeld,
                                // "erneut"
                                reflexivpronomen.imStr(reflKasusOderPraepositionalKasus),
                                // "an dich",
                                advAngabeSkopusVerbWohinWoherSynt
                                // "in deine Jackentasche"
                        ),
                        reflexivpronomen, reflKasusOderPraepositionalKasus,
                        objektPhrase, objektKasusOderPraepositionalkasus),
                new Nachfeld(
                        Konstituentenfolge.joinToNullKonstituentenfolge(
                                getAdvAngabeSkopusVerbTextDescriptionFuerZwangsausklammerung(
                                        praedRegMerkmale),
                                getAdvAngabeSkopusSatzDescriptionFuerZwangsausklammerung(
                                        praedRegMerkmale)
                        )),
                getVorfeldAdvAngabeSkopusSatz(praedRegMerkmale),
                getSpeziellesVorfeldAlsWeitereOption(praedRegMerkmale, objektPhrase),
                firstRelativpronomen(objektEinbindung),
                firstInterrogativwort(advAngabeSkopusSatzSyntFuerMittelfeld,
                        objektEinbindung, advAngabeSkopusVerbSyntFuerMittelfeld,
                        advAngabeSkopusVerbWohinWoherSynt));

    }

    @Override
    public boolean umfasstSatzglieder() {
        return true;
    }

    @Override
    public boolean isBezugAufNachzustandDesAktantenGegeben() {
        return true;
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final SemPraedikatReflSubjObjOhneLeerstellen that =
                (SemPraedikatReflSubjObjOhneLeerstellen) o;
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
