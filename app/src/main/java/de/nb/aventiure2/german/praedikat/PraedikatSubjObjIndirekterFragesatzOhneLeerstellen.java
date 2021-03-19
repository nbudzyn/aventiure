package de.nb.aventiure2.german.praedikat;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.Collection;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.nb.aventiure2.annotations.Komplement;
import de.nb.aventiure2.annotations.Valenz;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativSkopusSatz;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativVerbAllg;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativWohinWoher;
import de.nb.aventiure2.german.base.Interrogativpronomen;
import de.nb.aventiure2.german.base.KasusOderPraepositionalkasus;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.SubstPhrOderReflexivpronomen;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.satz.Satz;

import static de.nb.aventiure2.german.base.Kasus.AKK;
import static de.nb.aventiure2.german.base.Kasus.DAT;
import static de.nb.aventiure2.german.base.Konstituentenfolge.kf;

/**
 * Ein Prädikat eines Verbs wie
 * <i>jn. fragen, ob / wer / wie /... </i> oder <i>mit jm. diskutieren, ob
 * / wer / wie / ... </i>,
 * in dem alle Leerstellen besetzt sind. Beispiele:
 * <ul>
 *     <li>"fragt die Zauberin, wie sie sich fühlt"
 * </ul>
 * <p>
 * Adverbiale Angaben ("aus einer Laune heraus") können immer noch eingefügt werden.
 */
public class PraedikatSubjObjIndirekterFragesatzOhneLeerstellen
        extends AbstractAngabenfaehigesPraedikatOhneLeerstellen {
    @Nonnull
    private final KasusOderPraepositionalkasus kasusOderPraepositionalkasus;

    /**
     * Das Objekt (z.B. die Zauberin, die gefragt wird)
     */
    @Komplement
    @Nonnull
    private final SubstantivischePhrase objekt;

    /**
     * "(...versucht) wie sie sich fühlt"
     */
    @Komplement
    @Nonnull
    private final Satz indirekterFragesatz;

    @Valenz
    PraedikatSubjObjIndirekterFragesatzOhneLeerstellen(
            final Verb verb,
            final KasusOderPraepositionalkasus kasusOderPraepositionalkasus,
            final SubstantivischePhrase objekt,
            final Satz indirekterFragesatz) {
        this(verb, kasusOderPraepositionalkasus, objekt,
                ImmutableList.of(),
                null, null,
                null, indirekterFragesatz);
    }

    private PraedikatSubjObjIndirekterFragesatzOhneLeerstellen(
            final Verb verb,
            final KasusOderPraepositionalkasus kasusOderPraepositionalkasus,
            final SubstantivischePhrase objekt,
            final Iterable<Modalpartikel> modalpartikeln,
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabeSkopusSatz,
            @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabeSkopusVerbAllg,
            @Nullable final IAdvAngabeOderInterrogativWohinWoher advAngabeSkopusVerbWohinWoher,
            final Satz indirekterFragesatz) {
        super(verb, modalpartikeln,
                advAngabeSkopusSatz,
                advAngabeSkopusVerbAllg, advAngabeSkopusVerbWohinWoher);
        this.kasusOderPraepositionalkasus = kasusOderPraepositionalkasus;
        this.objekt = objekt;
        this.indirekterFragesatz = indirekterFragesatz;
    }

    @Override
    public PraedikatSubjObjIndirekterFragesatzOhneLeerstellen mitModalpartikeln(
            final Collection<Modalpartikel> modalpartikeln) {
        return new PraedikatSubjObjIndirekterFragesatzOhneLeerstellen(
                getVerb(),
                kasusOderPraepositionalkasus, objekt,
                Iterables.concat(getModalpartikeln(), modalpartikeln),
                getAdvAngabeSkopusSatz(),
                getAdvAngabeSkopusVerbAllg(),
                getAdvAngabeSkopusVerbWohinWoher(),
                indirekterFragesatz
        );
    }

    @Override
    public PraedikatSubjObjIndirekterFragesatzOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        return new PraedikatSubjObjIndirekterFragesatzOhneLeerstellen(
                getVerb(),
                kasusOderPraepositionalkasus, objekt,
                getModalpartikeln(),
                advAngabe, getAdvAngabeSkopusVerbAllg(),
                getAdvAngabeSkopusVerbWohinWoher(),
                indirekterFragesatz);
    }

    @Override
    public PraedikatSubjObjIndirekterFragesatzOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        return new PraedikatSubjObjIndirekterFragesatzOhneLeerstellen(
                getVerb(),
                kasusOderPraepositionalkasus, objekt,
                getModalpartikeln(),
                getAdvAngabeSkopusSatz(), advAngabe,
                getAdvAngabeSkopusVerbWohinWoher(),
                indirekterFragesatz
        );
    }

    @Override
    public PraedikatSubjObjIndirekterFragesatzOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativWohinWoher advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        return new PraedikatSubjObjIndirekterFragesatzOhneLeerstellen(
                getVerb(),
                kasusOderPraepositionalkasus, objekt,
                getModalpartikeln(),
                getAdvAngabeSkopusSatz(),
                getAdvAngabeSkopusVerbAllg(),
                advAngabe,
                indirekterFragesatz
        );
    }

    @Override
    public boolean kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden() {
        // "Gefragt, wer ..., [gehst du ins Bad]"
        return true;
    }

    @Nullable
    @Override
    public Konstituentenfolge getSpeziellesVorfeldAlsWeitereOption(
            final Person person, final Numerus numerus) {
        @Nullable final Konstituentenfolge speziellesVorfeldFromSuper =
                super.getSpeziellesVorfeldAlsWeitereOption(person,
                        numerus);
        if (speziellesVorfeldFromSuper != null) {
            return speziellesVorfeldFromSuper;
        }

        // "[,] ob du etwas zu berichten hast[, fragt er dich] "
        // "[,] was du zu berichten hast[, fragt er dich] "
        return Konstituentenfolge.schliesseInKommaEin(
                indirekterFragesatz.getIndirekteFrage());
    }

    @Override
    @CheckReturnValue
    Konstituentenfolge getMittelfeldOhneLinksversetzungUnbetonterPronomen(
            final Person personSubjekt, final Numerus numerusSubjekt) {
        return Konstituentenfolge.joinToNullKonstituentenfolge(
                objekt.imK(kasusOderPraepositionalkasus), // "die Hexe"
                getAdvAngabeSkopusSatzDescriptionFuerMittelfeld(personSubjekt,
                        numerusSubjekt),
                // "aus einer Laune heraus"
                kf(getModalpartikeln()),  // "mal eben"
                getAdvAngabeSkopusVerbTextDescriptionFuerMittelfeld(personSubjekt,
                        numerusSubjekt), // "erneut"
                getAdvAngabeSkopusVerbWohinWoherDescription(personSubjekt, numerusSubjekt)
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
                getAdvAngabeSkopusVerbTextDescriptionFuerZwangsausklammerung(personSubjekt,
                        numerusSubjekt),
                getAdvAngabeSkopusSatzDescriptionFuerZwangsausklammerung(personSubjekt,
                        numerusSubjekt),
                // "[,] ob du etwas zu berichten hast[, ]"
                // "[,] was du zu berichten hast[, ]"
                Konstituentenfolge.schliesseInKommaEin(
                        indirekterFragesatz.getIndirekteFrage()));
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
}
