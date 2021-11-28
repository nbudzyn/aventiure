package de.nb.aventiure2.german.praedikat;

import static de.nb.aventiure2.german.base.Kasus.AKK;
import static de.nb.aventiure2.german.base.Kasus.DAT;
import static de.nb.aventiure2.german.base.Konstituente.k;
import static de.nb.aventiure2.german.base.Konstituentenfolge.kf;

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
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.Negationspartikelphrase;
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.base.PraedRegMerkmale;
import de.nb.aventiure2.german.base.Relativpronomen;
import de.nb.aventiure2.german.base.SubstPhrOderReflexivpronomen;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.base.WoertlicheRede;

/**
 * Ein "semantisches Prädikat" mit einem Objekt und wörtlicher Rede, in dem alle Leerstellen
 * besetzt sind.
 * Beispiele:
 * <ul>
 *     <li>"ihr entgegenblaffen: „Geh!“"
 * </ul>
 * <p>
 * Adverbiale Angaben ("laut") können immer noch eingefügt werden.
 */
public class SemPraedikatObjWoertlicheRedeOhneLeerstellen
        extends AbstractAngabenfaehigesSemPraedikatOhneLeerstellen {
    /**
     * Der Kasus (z.B. Dativ, "ihr ... entgegenblaffen") oder Präpositionalkasus, mit dem
     * dieses Verb steht (den dieses Verb regiert).
     */
    @NonNull
    private final KasusOderPraepositionalkasus kasusOderPraepositionalkasus;

    /**
     * Das Objekt
     */
    @Komplement
    private final SubstantivischePhrase objekt;

    /**
     * Die wörtliche Rede
     */
    @Komplement
    @NonNull
    private final WoertlicheRede woertlicheRede;

    @Valenz
    SemPraedikatObjWoertlicheRedeOhneLeerstellen(
            final Verb verb,
            final KasusOderPraepositionalkasus kasusOderPraepositionalkasus,
            final SubstantivischePhrase objekt,
            final WoertlicheRede woertlicheRede) {
        this(verb, kasusOderPraepositionalkasus, objekt, woertlicheRede,
                ImmutableList.of(), null, null, null, null);
    }

    private SemPraedikatObjWoertlicheRedeOhneLeerstellen(
            final Verb verb,
            final KasusOderPraepositionalkasus kasusOderPraepositionalkasus,
            final SubstantivischePhrase objekt,
            final WoertlicheRede woertlicheRede,
            final Iterable<Modalpartikel> modalpartikeln,
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabeSkopusSatz,
            @Nullable final Negationspartikelphrase negationspartikelphrase,
            @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabeSkopusVerbAllg,
            @Nullable final IAdvAngabeOderInterrogativWohinWoher advAngabeSkopusVerbWohinWoher) {
        super(verb, modalpartikeln,
                advAngabeSkopusSatz,
                negationspartikelphrase, advAngabeSkopusVerbAllg, advAngabeSkopusVerbWohinWoher);
        this.kasusOderPraepositionalkasus = kasusOderPraepositionalkasus;
        this.objekt = objekt;
        this.woertlicheRede = woertlicheRede;
    }

    @Override
    public SemPraedikatObjWoertlicheRedeOhneLeerstellen mitModalpartikeln(
            final Collection<Modalpartikel> modalpartikeln) {
        return new SemPraedikatObjWoertlicheRedeOhneLeerstellen(
                getVerb(), kasusOderPraepositionalkasus, objekt, woertlicheRede,
                Iterables.concat(getModalpartikeln(), modalpartikeln),
                getAdvAngabeSkopusSatz(),
                getNegationspartikel(), getAdvAngabeSkopusVerbAllg(),
                getAdvAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public SemPraedikatObjWoertlicheRedeOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        return new SemPraedikatObjWoertlicheRedeOhneLeerstellen(
                getVerb(),
                kasusOderPraepositionalkasus, objekt, woertlicheRede,
                getModalpartikeln(),
                advAngabe, getNegationspartikel(), getAdvAngabeSkopusVerbAllg(),
                getAdvAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public SemPraedikatObjWoertlicheRedeOhneLeerstellen neg() {
        return (SemPraedikatObjWoertlicheRedeOhneLeerstellen) super.neg();
    }

    @Override
    public SemPraedikatObjWoertlicheRedeOhneLeerstellen neg(
            @Nullable final Negationspartikelphrase negationspartikelphrase) {
        if (negationspartikelphrase == null) {
            return this;
        }

        return new SemPraedikatObjWoertlicheRedeOhneLeerstellen(
                getVerb(),
                kasusOderPraepositionalkasus, objekt, woertlicheRede,
                getModalpartikeln(),
                getAdvAngabeSkopusSatz(), negationspartikelphrase, getAdvAngabeSkopusVerbAllg(),
                getAdvAngabeSkopusVerbWohinWoher());
    }

    @Override
    public SemPraedikatObjWoertlicheRedeOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        return new SemPraedikatObjWoertlicheRedeOhneLeerstellen(
                getVerb(),
                kasusOderPraepositionalkasus, objekt, woertlicheRede,
                getModalpartikeln(),
                getAdvAngabeSkopusSatz(), getNegationspartikel(), advAngabe,
                getAdvAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public SemPraedikatObjWoertlicheRedeOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativWohinWoher advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        return new SemPraedikatObjWoertlicheRedeOhneLeerstellen(
                getVerb(),
                kasusOderPraepositionalkasus, objekt, woertlicheRede,
                getModalpartikeln(),
                getAdvAngabeSkopusSatz(),
                getNegationspartikel(), getAdvAngabeSkopusVerbAllg(),
                advAngabe
        );
    }

    @Override
    public boolean kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden() {
        return false;
    }

    @Nullable
    @Override
    public Konstituente getSpeziellesVorfeldSehrErwuenscht(final PraedRegMerkmale praedRegMerkmale,
                                                           final boolean nachAnschlusswort) {
        final Konstituente speziellesVorfeldSehrErwuenschtFromSuper =
                super.getSpeziellesVorfeldSehrErwuenscht(praedRegMerkmale, nachAnschlusswort);
        if (speziellesVorfeldSehrErwuenschtFromSuper != null) {
            return speziellesVorfeldSehrErwuenschtFromSuper;
        }

        if (!nachAnschlusswort
                && !woertlicheRede.isLangOderMehrteilig()) {
            return k(woertlicheRede.getDescription(),
                    true,
                    true); // "„Kommt alle her[“, ]"
        }

        return null;
    }

    @Override
    public @Nullable
    Konstituentenfolge getSpeziellesVorfeldAlsWeitereOption(
            final PraedRegMerkmale praedRegMerkmale) {
        @Nullable final Konstituentenfolge speziellesVorfeldFromSuper =
                super.getSpeziellesVorfeldAlsWeitereOption(praedRegMerkmale);
        if (speziellesVorfeldFromSuper != null) {
            return speziellesVorfeldFromSuper;
        }

        /*
         * "es" allein darf nicht im Vorfeld stehen, wenn es ein Objekt ist
         * (Eisenberg Der SemSatz 5.4.2).
         */
        if (Personalpronomen.isPersonalpronomenEs(objekt, kasusOderPraepositionalkasus)) {
            return null;
        }

        /*
         * Phrasen (auch Personalpronomen) mit Fokuspartikel
         * sind häufig kontrastiv und daher oft für das Vorfeld geeignet.
         */
        if (objekt.getFokuspartikel() != null) {
            // "Nur Rapunzel sagst du:..."
            return objekt.imK(kasusOderPraepositionalkasus);
        }

        return null;
    }

    @Override
    @Nullable
    @CheckReturnValue
    Konstituentenfolge getMittelfeldOhneLinksversetzungUnbetonterPronomen(
            final PraedRegMerkmale praedRegMerkmale) {
        return Konstituentenfolge.joinToNullKonstituentenfolge(
                objekt.imK(kasusOderPraepositionalkasus), // "der Hexe"
                getAdvAngabeSkopusSatzDescriptionFuerMittelfeld(praedRegMerkmale),
                // "aus einer Laune heraus"
                kf(getModalpartikeln()),  // "mal eben"
                getAdvAngabeSkopusVerbTextDescriptionFuerMittelfeld(praedRegMerkmale), // "erneut"
                getNegationspartikel(), // "nicht"
                getAdvAngabeSkopusVerbWohinWoherDescription(praedRegMerkmale)
                // ("mitten ins Gesicht" - sofern überhaupt möglich)
        );
    }

    @Nullable
    @Override
    SubstPhrOderReflexivpronomen getDat(final PraedRegMerkmale praedRegMerkmale) {
        if (kasusOderPraepositionalkasus == DAT) {
            return objekt;
        }

        return null;
    }

    @Nullable
    @Override
    SubstPhrOderReflexivpronomen getAkk(final PraedRegMerkmale praedRegMerkmale) {
        if (kasusOderPraepositionalkasus == AKK) {
            return objekt;
        }

        return null;
    }

    @Nullable
    @Override
    SubstPhrOderReflexivpronomen getZweitesAkk() {
        return null;
    }

    @Override
    public Konstituentenfolge getNachfeld(final PraedRegMerkmale praedRegMerkmale) {
        return Konstituentenfolge.joinToKonstituentenfolge(
                getAdvAngabeSkopusVerbTextDescriptionFuerZwangsausklammerung(praedRegMerkmale),
                getAdvAngabeSkopusSatzDescriptionFuerZwangsausklammerung(praedRegMerkmale),
                k(woertlicheRede.getDescription(),
                        true,
                        true)
                        .withVordoppelpunktNoetig()); // "[: ]„Kommt alle her[.“]"

        //  Laut http://www.pro-publish.com/korrektor/zeichensetzung-interpunktion/den
        //  -doppelpunkt-richtig-setzen/
        //  ist es möglich, nach dem Doppelpunkt den SemSatz fortzuführen - dabei darf die
        //  wörtliche Rede allerdings nicht mit Punkt abgeschlossen worden sein:
        //  "Peter sagte: „Ich fahre jetzt nach Hause“, doch es wurde noch viel später an diesem
        //  Abend."
    }

    @Override
    public boolean umfasstSatzglieder() {
        return true;
    }

    @Nullable
    @Override
    @CheckReturnValue
    public Konstituentenfolge getErstesInterrogativwort() {
        if (objekt instanceof Interrogativpronomen) {
            return objekt.imK(kasusOderPraepositionalkasus);
        }

        @Nullable
        Konstituentenfolge res = interroAdverbToKF(getAdvAngabeSkopusSatz());
        if (res != null) {
            return res;
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
            return objekt.imK(kasusOderPraepositionalkasus);
        }

        return null;
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
        final SemPraedikatObjWoertlicheRedeOhneLeerstellen that =
                (SemPraedikatObjWoertlicheRedeOhneLeerstellen) o;
        return kasusOderPraepositionalkasus.equals(that.kasusOderPraepositionalkasus) &&
                Objects.equals(objekt, that.objekt) &&
                woertlicheRede.equals(that.woertlicheRede);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), kasusOderPraepositionalkasus, objekt, woertlicheRede);
    }
}
