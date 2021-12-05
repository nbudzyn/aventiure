package de.nb.aventiure2.german.praedikat;


import static de.nb.aventiure2.german.base.Kasus.AKK;
import static de.nb.aventiure2.german.base.Konstituentenfolge.kf;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.Collection;
import java.util.Objects;

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
import de.nb.aventiure2.german.base.Negationspartikelphrase;
import de.nb.aventiure2.german.base.PraedRegMerkmale;
import de.nb.aventiure2.german.base.Relativpronomen;
import de.nb.aventiure2.german.base.SubstantivischPhrasierbar;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.ITextContext;
import de.nb.aventiure2.german.satz.SemSatz;

/**
 * Ein "semantisches Prädikat" eines Verbs wie
 * <i>jn. fragen, ob / wer / wie /... </i> oder <i>mit jm. diskutieren, ob
 * / wer / wie / ... </i>,
 * in dem alle Leerstellen besetzt sind. Beispiele:
 * <ul>
 *     <li>"fragt die Zauberin, wie sie sich fühlt"
 * </ul>
 * <p>
 * Adverbiale Angaben ("aus einer Laune heraus") können immer noch eingefügt werden.
 */
public class SemPraedikatSubjObjIndirekterFragesatzOhneLeerstellen
        extends AbstractAngabenfaehigesSemPraedikatOhneLeerstellen {
    @Nonnull
    private final KasusOderPraepositionalkasus kasusOderPraepositionalkasus;

    /**
     * Das Objekt (z.B. die Zauberin, die gefragt wird)
     */
    @Komplement
    @Nonnull
    private final SubstantivischPhrasierbar objekt;

    /**
     * "(...versucht) wie sie sich fühlt"
     */
    @Komplement
    @Nonnull
    private final SemSatz indirekterFragesatz;

    @Valenz
    SemPraedikatSubjObjIndirekterFragesatzOhneLeerstellen(
            final Verb verb,
            final KasusOderPraepositionalkasus kasusOderPraepositionalkasus,
            final SubstantivischPhrasierbar objekt,
            final SemSatz indirekterFragesatz) {
        this(verb, kasusOderPraepositionalkasus, objekt,
                ImmutableList.of(),
                null, null, null,
                null, indirekterFragesatz);
    }

    private SemPraedikatSubjObjIndirekterFragesatzOhneLeerstellen(
            final Verb verb,
            final KasusOderPraepositionalkasus kasusOderPraepositionalkasus,
            final SubstantivischPhrasierbar objekt,
            final Iterable<Modalpartikel> modalpartikeln,
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabeSkopusSatz,
            @Nullable final Negationspartikelphrase negationspartikelphrase,
            @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabeSkopusVerbAllg,
            @Nullable final IAdvAngabeOderInterrogativWohinWoher advAngabeSkopusVerbWohinWoher,
            final SemSatz indirekterFragesatz) {
        super(verb, modalpartikeln,
                advAngabeSkopusSatz,
                negationspartikelphrase, advAngabeSkopusVerbAllg, advAngabeSkopusVerbWohinWoher);
        this.kasusOderPraepositionalkasus = kasusOderPraepositionalkasus;
        this.objekt = objekt;
        this.indirekterFragesatz = indirekterFragesatz;
    }

    @Override
    public SemPraedikatSubjObjIndirekterFragesatzOhneLeerstellen mitModalpartikeln(
            final Collection<Modalpartikel> modalpartikeln) {
        return new SemPraedikatSubjObjIndirekterFragesatzOhneLeerstellen(
                getVerb(),
                kasusOderPraepositionalkasus, objekt,
                Iterables.concat(getModalpartikeln(), modalpartikeln),
                getAdvAngabeSkopusSatz(),
                getNegationspartikel(), getAdvAngabeSkopusVerbAllg(),
                getAdvAngabeSkopusVerbWohinWoher(),
                indirekterFragesatz
        );
    }

    @Override
    public SemPraedikatSubjObjIndirekterFragesatzOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        return new SemPraedikatSubjObjIndirekterFragesatzOhneLeerstellen(
                getVerb(),
                kasusOderPraepositionalkasus, objekt,
                getModalpartikeln(),
                advAngabe, getNegationspartikel(), getAdvAngabeSkopusVerbAllg(),
                getAdvAngabeSkopusVerbWohinWoher(),
                indirekterFragesatz);
    }

    @Override
    public SemPraedikatSubjObjIndirekterFragesatzOhneLeerstellen neg() {
        return (SemPraedikatSubjObjIndirekterFragesatzOhneLeerstellen) super.neg();
    }

    @Override
    public SemPraedikatSubjObjIndirekterFragesatzOhneLeerstellen neg(
            @Nullable final Negationspartikelphrase negationspartikelphrase) {
        if (negationspartikelphrase == null) {
            return this;
        }

        return new SemPraedikatSubjObjIndirekterFragesatzOhneLeerstellen(
                getVerb(),
                kasusOderPraepositionalkasus, objekt,
                getModalpartikeln(),
                getAdvAngabeSkopusSatz(), negationspartikelphrase, getAdvAngabeSkopusVerbAllg(),
                getAdvAngabeSkopusVerbWohinWoher(),
                indirekterFragesatz);
    }

    @Override
    public SemPraedikatSubjObjIndirekterFragesatzOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        return new SemPraedikatSubjObjIndirekterFragesatzOhneLeerstellen(
                getVerb(),
                kasusOderPraepositionalkasus, objekt,
                getModalpartikeln(),
                getAdvAngabeSkopusSatz(), getNegationspartikel(), advAngabe,
                getAdvAngabeSkopusVerbWohinWoher(),
                indirekterFragesatz
        );
    }

    @Override
    public SemPraedikatSubjObjIndirekterFragesatzOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativWohinWoher advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        return new SemPraedikatSubjObjIndirekterFragesatzOhneLeerstellen(
                getVerb(),
                kasusOderPraepositionalkasus, objekt,
                getModalpartikeln(),
                getAdvAngabeSkopusSatz(),
                getNegationspartikel(), getAdvAngabeSkopusVerbAllg(),
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
            final PraedRegMerkmale praedRegMerkmale) {
        @Nullable final Konstituentenfolge speziellesVorfeldFromSuper =
                super.getSpeziellesVorfeldAlsWeitereOption(praedRegMerkmale);
        if (speziellesVorfeldFromSuper != null) {
            return speziellesVorfeldFromSuper;
        }

        // "[,] ob du etwas zu berichten hast[, fragt er dich] "
        // "[,] was du zu berichten hast[, fragt er dich] "
        return Konstituentenfolge.schliesseInKommaEin(
                indirekterFragesatz.getIndirekteFrage());
    }

    @Override
    public boolean hatAkkusativobjekt() {
        return kasusOderPraepositionalkasus == AKK;
    }

    @Override
    TopolFelder getTopolFelder(final ITextContext textContext,
                               final PraedRegMerkmale praedRegMerkmale) {
        final SubstantivischePhrase objektPhrase = objekt.alsSubstPhrase(textContext);

        return new TopolFelder(
                new Mittelfeld(
                        Konstituentenfolge.joinToNullKonstituentenfolge(
                                objektPhrase.imK(kasusOderPraepositionalkasus), // "die Hexe"
                                getAdvAngabeSkopusSatzDescriptionFuerMittelfeld(praedRegMerkmale),
                                // "aus einer Laune heraus"
                                kf(getModalpartikeln()),  // "mal eben"
                                getAdvAngabeSkopusVerbTextDescriptionFuerMittelfeld(
                                        praedRegMerkmale),
                                // "erneut"
                                getNegationspartikel(), // "nicht"
                                getAdvAngabeSkopusVerbWohinWoherDescription(praedRegMerkmale)
                                // ("mitten ins Gesicht" - sofern überhaupt möglich)
                        ),
                        objektPhrase, kasusOderPraepositionalkasus),
                new Nachfeld(
                        Konstituentenfolge.joinToKonstituentenfolge(
                                getAdvAngabeSkopusVerbTextDescriptionFuerZwangsausklammerung(
                                        praedRegMerkmale),
                                getAdvAngabeSkopusSatzDescriptionFuerZwangsausklammerung(
                                        praedRegMerkmale),
                                // "[,] ob du etwas zu berichten hast[, ]"
                                // "[,] was du zu berichten hast[, ]"
                                Konstituentenfolge.schliesseInKommaEin(
                                        indirekterFragesatz.getIndirekteFrage()))));
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
        final SemPraedikatSubjObjIndirekterFragesatzOhneLeerstellen that =
                (SemPraedikatSubjObjIndirekterFragesatzOhneLeerstellen) o;
        return kasusOderPraepositionalkasus.equals(that.kasusOderPraepositionalkasus) &&
                objekt.equals(that.objekt) &&
                indirekterFragesatz.equals(that.indirekterFragesatz);
    }

    @Override
    public int hashCode() {
        return Objects
                .hash(super.hashCode(), kasusOderPraepositionalkasus, objekt, indirekterFragesatz);
    }
}
