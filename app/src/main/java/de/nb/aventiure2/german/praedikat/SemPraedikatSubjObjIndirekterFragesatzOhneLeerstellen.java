package de.nb.aventiure2.german.praedikat;


import static de.nb.aventiure2.german.base.Kasus.AKK;
import static de.nb.aventiure2.german.base.Konstituentenfolge.kf;
import static de.nb.aventiure2.german.praedikat.Praedikatseinbindung.firstInterrogativwort;
import static de.nb.aventiure2.german.praedikat.Praedikatseinbindung.firstRelativpronomen;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.Collection;
import java.util.Objects;

import javax.annotation.Nonnull;
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
import de.nb.aventiure2.german.base.PraedRegMerkmale;
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
    public Vorfeld getSpeziellesVorfeldAlsWeitereOption(
            final PraedRegMerkmale praedRegMerkmale,
            final Konstituentenfolge syntIndirekteFrage) {
        @Nullable final Vorfeld speziellesVorfeldFromSuper =
                super.getGgfVorfeldAdvAngabeSkopusVerb(praedRegMerkmale);
        if (speziellesVorfeldFromSuper != null) {
            return speziellesVorfeldFromSuper;
        }

        // "[,] ob du etwas zu berichten hast[, fragt er dich] "
        // "[,] was du zu berichten hast[, fragt er dich] "
        return new Vorfeld(syntIndirekteFrage);
    }

    @Override
    public boolean hatAkkusativobjekt() {
        return kasusOderPraepositionalkasus == AKK;
    }

    @Override
    TopolFelder getTopolFelder(final ITextContext textContext,
                               final PraedRegMerkmale praedRegMerkmale,
                               final boolean nachAnschlusswort) {
        final SubstantivischePhrase objektPhrase = objekt.alsSubstPhrase(textContext);
        final Praedikatseinbindung<SubstantivischePhrase> objektEinbindung =
                new Praedikatseinbindung<>(objektPhrase, o -> o.imK(kasusOderPraepositionalkasus));

        final Konstituente advAngabeSkopusSatzSyntFuerMittelfeld =
                getAdvAngabeSkopusSatzDescriptionFuerMittelfeld(praedRegMerkmale);

        final Konstituente advAngabeSkopusVerbSyntFuerMittelfeld =
                getAdvAngabeSkopusVerbTextDescriptionFuerMittelfeld(
                        praedRegMerkmale);
        final Konstituente advAngabeSkopusVerbWohinWoherSynt =
                getAdvAngabeSkopusVerbWohinWoherDescription(praedRegMerkmale);

        // FIXME Der textContext könnte sich im Rahmen dieser Methode ändern?!
        final Konstituentenfolge syntIndirekteFrage =
                Konstituentenfolge
                        .schliesseInKommaEin(indirekterFragesatz.getIndirekteFrage(textContext));
        return new TopolFelder(
                new Mittelfeld(
                        Konstituentenfolge.joinToNullKonstituentenfolge(
                                objektEinbindung, // "die Hexe"
                                advAngabeSkopusSatzSyntFuerMittelfeld,
                                // "aus einer Laune heraus"
                                kf(getModalpartikeln()),  // "mal eben"
                                advAngabeSkopusVerbSyntFuerMittelfeld,
                                // "erneut"
                                getNegationspartikel(), // "nicht"
                                advAngabeSkopusVerbWohinWoherSynt
                                // ("mitten ins Gesicht" - sofern überhaupt möglich)
                        ),
                        objektEinbindung),
                new Nachfeld(
                        Konstituentenfolge.joinToKonstituentenfolge(
                                getAdvAngabeSkopusVerbTextDescriptionFuerZwangsausklammerung(
                                        praedRegMerkmale),
                                getAdvAngabeSkopusSatzDescriptionFuerZwangsausklammerung(
                                        praedRegMerkmale),
                                // "[,] ob du etwas zu berichten hast[, ]"
                                // "[,] was du zu berichten hast[, ]"
                                syntIndirekteFrage)),
                getVorfeldAdvAngabeSkopusSatz(praedRegMerkmale),
                getSpeziellesVorfeldAlsWeitereOption(praedRegMerkmale, syntIndirekteFrage),
                firstRelativpronomen(objektEinbindung),
                firstInterrogativwort(objektEinbindung,
                        advAngabeSkopusSatzSyntFuerMittelfeld,
                        advAngabeSkopusVerbSyntFuerMittelfeld,
                        advAngabeSkopusVerbWohinWoherSynt));

    }

    @Override
    public boolean umfasstSatzglieder() {
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
