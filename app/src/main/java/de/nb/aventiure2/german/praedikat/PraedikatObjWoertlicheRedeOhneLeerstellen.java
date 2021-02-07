package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.Collection;

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
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.SubstPhrOderReflexivpronomen;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.base.WoertlicheRede;

import static de.nb.aventiure2.german.base.Kasus.AKK;
import static de.nb.aventiure2.german.base.Kasus.DAT;
import static de.nb.aventiure2.german.base.Konstituente.k;
import static de.nb.aventiure2.german.base.Konstituentenfolge.kf;

/**
 * Ein Prädikat mit einem Objekt und wörtlicher Rede, in dem alle Leerstellen besetzt sind.
 * Beispiele:
 * <ul>
 *     <li>"ihr entgegenblaffen: „Geh!“"
 * </ul>
 * <p>
 * Adverbiale Angaben ("laut") können immer noch eingefügt werden.
 */
public class PraedikatObjWoertlicheRedeOhneLeerstellen
        extends AbstractAngabenfaehigesPraedikatOhneLeerstellen {
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
    PraedikatObjWoertlicheRedeOhneLeerstellen(
            final Verb verb,
            final KasusOderPraepositionalkasus kasusOderPraepositionalkasus,
            final SubstantivischePhrase objekt,
            final WoertlicheRede woertlicheRede) {
        this(verb, kasusOderPraepositionalkasus, objekt, woertlicheRede,
                ImmutableList.of(), null, null, null);
    }

    private PraedikatObjWoertlicheRedeOhneLeerstellen(
            final Verb verb,
            final KasusOderPraepositionalkasus kasusOderPraepositionalkasus,
            final SubstantivischePhrase objekt,
            final WoertlicheRede woertlicheRede,
            final Iterable<Modalpartikel> modalpartikeln,
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz adverbialeAngabeSkopusSatz,
            @Nullable final IAdvAngabeOderInterrogativVerbAllg adverbialeAngabeSkopusVerbAllg,
            @Nullable
            final IAdvAngabeOderInterrogativWohinWoher adverbialeAngabeSkopusVerbWohinWoher) {
        super(verb, modalpartikeln, adverbialeAngabeSkopusSatz,
                adverbialeAngabeSkopusVerbAllg, adverbialeAngabeSkopusVerbWohinWoher);
        this.kasusOderPraepositionalkasus = kasusOderPraepositionalkasus;
        this.objekt = objekt;
        this.woertlicheRede = woertlicheRede;
    }

    @Override
    public PraedikatObjWoertlicheRedeOhneLeerstellen mitModalpartikeln(
            final Collection<Modalpartikel> modalpartikeln) {
        return new PraedikatObjWoertlicheRedeOhneLeerstellen(
                getVerb(), kasusOderPraepositionalkasus, objekt, woertlicheRede,
                Iterables.concat(getModalpartikeln(), modalpartikeln),
                getAdverbialeAngabeSkopusSatz(),
                getAdverbialeAngabeSkopusVerbAllg(),
                getAdverbialeAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public PraedikatObjWoertlicheRedeOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz adverbialeAngabe) {
        if (adverbialeAngabe == null) {
            return this;
        }

        return new PraedikatObjWoertlicheRedeOhneLeerstellen(
                getVerb(),
                kasusOderPraepositionalkasus, objekt, woertlicheRede,
                getModalpartikeln(),
                adverbialeAngabe, getAdverbialeAngabeSkopusVerbAllg(),
                getAdverbialeAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public PraedikatObjWoertlicheRedeOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final IAdvAngabeOderInterrogativVerbAllg adverbialeAngabe) {
        if (adverbialeAngabe == null) {
            return this;
        }

        return new PraedikatObjWoertlicheRedeOhneLeerstellen(
                getVerb(),
                kasusOderPraepositionalkasus, objekt, woertlicheRede,
                getModalpartikeln(),
                getAdverbialeAngabeSkopusSatz(), adverbialeAngabe,
                getAdverbialeAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public PraedikatObjWoertlicheRedeOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final IAdvAngabeOderInterrogativWohinWoher adverbialeAngabe) {
        if (adverbialeAngabe == null) {
            return this;
        }

        return new PraedikatObjWoertlicheRedeOhneLeerstellen(
                getVerb(),
                kasusOderPraepositionalkasus, objekt, woertlicheRede,
                getModalpartikeln(),
                getAdverbialeAngabeSkopusSatz(),
                getAdverbialeAngabeSkopusVerbAllg(),
                adverbialeAngabe
        );
    }

    @Override
    public boolean kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden() {
        return false;
    }

    @Nullable
    @Override
    public Konstituente getSpeziellesVorfeldSehrErwuenscht(final Person personSubjekt,
                                                           final Numerus numerusSubjekt) {
        final Konstituente speziellesVorfeldSehrErwuenschtFromSuper =
                super.getSpeziellesVorfeldSehrErwuenscht(personSubjekt, numerusSubjekt);
        if (speziellesVorfeldSehrErwuenschtFromSuper != null) {
            return speziellesVorfeldSehrErwuenschtFromSuper;
        }

        if (!woertlicheRede.isLangOderMehrteilig()) {
            return k(woertlicheRede.getDescription(),
                    true,
                    true); // "„Kommt alle her[“, ]"
        }

        return null;
    }

    @Override
    @Nullable
    Konstituentenfolge getMittelfeldOhneLinksversetzungUnbetonterPronomen(
            final Person personSubjekt, final Numerus numerusSubjekt) {
        return Konstituentenfolge.joinToNullKonstituentenfolge(
                objekt.imK(kasusOderPraepositionalkasus), // "der Hexe"
                getAdverbialeAngabeSkopusSatzDescriptionFuerMittelfeld(personSubjekt,
                        numerusSubjekt),
                // "aus einer Laune heraus"
                kf(getModalpartikeln()),  // "mal eben"
                getAdverbialeAngabeSkopusVerbTextDescriptionFuerMittelfeld(personSubjekt,
                        numerusSubjekt), // "erneut"
                getAdverbialeAngabeSkopusVerbWohinWoherDescription(personSubjekt, numerusSubjekt)
                // ("mitten ins Gesicht" - sofern überhaupt möglich)
        );
    }

    @Nullable
    @Override
    SubstPhrOderReflexivpronomen getDat(
            final Person personSubjekt, final Numerus numerusSubjekt) {
        if (kasusOderPraepositionalkasus == DAT) {
            return objekt;
        }

        return null;
    }

    @Nullable
    @Override
    SubstPhrOderReflexivpronomen getAkk(
            final Person personSubjekt, final Numerus numerusSubjekt) {
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
    public Konstituentenfolge getNachfeld(final Person personSubjekt,
                                          final Numerus numerusSubjekt) {
        return Konstituentenfolge.joinToKonstituentenfolge(
                getAdverbialeAngabeSkopusVerbTextDescriptionFuerZwangsausklammerung(personSubjekt,
                        numerusSubjekt),
                getAdverbialeAngabeSkopusSatzDescriptionFuerZwangsausklammerung(personSubjekt,
                        numerusSubjekt),
                k(woertlicheRede.getDescription(),
                        true,
                        true)
                        .withVordoppelpunktNoetig()); // "[: ]„Kommt alle her[.“]"

        //  Laut http://www.pro-publish.com/korrektor/zeichensetzung-interpunktion/den
        //  -doppelpunkt-richtig-setzen/
        //  ist es möglich, nach dem Doppelpunkt den Satz fortzuführen - dabei darf die
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
    public Konstituentenfolge getErstesInterrogativwort() {
        if (objekt instanceof Interrogativpronomen) {
            return objekt.imK(kasusOderPraepositionalkasus);
        }

        @Nullable
        Konstituentenfolge res = interroAdverbToKF(getAdverbialeAngabeSkopusSatz());
        if (res != null) {
            return res;
        }

        res = interroAdverbToKF(getAdverbialeAngabeSkopusVerbAllg());
        if (res != null) {
            return res;
        }

        return interroAdverbToKF(getAdverbialeAngabeSkopusVerbWohinWoher());
    }
}
