package de.nb.aventiure2.german.praedikat;

import static de.nb.aventiure2.german.base.Kasus.AKK;
import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToKonstituentenfolge;
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
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.Negationspartikelphrase;
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.base.PraedRegMerkmale;
import de.nb.aventiure2.german.base.PraepositionMitKasus;
import de.nb.aventiure2.german.base.Relativpronomen;
import de.nb.aventiure2.german.base.SubstPhrOderReflexivpronomen;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

/**
 * Ein "semantisches Prädikat", in dem ein Akkusativobjekt und ein Präpositionalobjekt gesetzt sind
 * und es auch sonst keine Leerstellen gibt. Beispiel: "den Frosch in die Hände nehmen"
 */
public class SemPraedikatAkkPraepOhneLeerstellen
        extends AbstractAngabenfaehigesSemPraedikatOhneLeerstellen {
    @Komplement
    @NonNull
    private final SubstantivischePhrase akk;

    @Komplement
    @NonNull
    private final SubstantivischePhrase praep;

    @NonNull
    private final PraepositionMitKasus praepositionMitKasus;

    @Valenz
    SemPraedikatAkkPraepOhneLeerstellen(
            final Verb verb,
            final PraepositionMitKasus praepositionMitKasus,
            final SubstantivischePhrase akk,
            final SubstantivischePhrase praep) {
        this(verb, praepositionMitKasus, akk, praep,
                ImmutableList.of(), null, null, null,
                null);
    }

    private SemPraedikatAkkPraepOhneLeerstellen(
            final Verb verb,
            final PraepositionMitKasus praepositionMitKasus,
            final SubstantivischePhrase akk,
            final SubstantivischePhrase praep,
            final Iterable<Modalpartikel> modalpartikeln,
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabeSkopusSatz,
            @Nullable final Negationspartikelphrase negationspartikelphrase,
            @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabeSkopusVerbAllg,
            @Nullable final IAdvAngabeOderInterrogativWohinWoher advAngabeSkopusVerbWohinWoher) {
        super(verb, modalpartikeln,
                advAngabeSkopusSatz,
                negationspartikelphrase, advAngabeSkopusVerbAllg, advAngabeSkopusVerbWohinWoher);
        this.praepositionMitKasus = praepositionMitKasus;
        this.praep = praep;
        this.akk = akk;
    }

    @Override
    public SemPraedikatAkkPraepOhneLeerstellen mitModalpartikeln(
            final Collection<Modalpartikel> modalpartikeln) {
        return new SemPraedikatAkkPraepOhneLeerstellen(
                getVerb(), praepositionMitKasus, akk, praep,
                Iterables.concat(getModalpartikeln(), modalpartikeln),
                getAdvAngabeSkopusSatz(),
                getNegationspartikel(), getAdvAngabeSkopusVerbAllg(),
                getAdvAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public SemPraedikatAkkPraepOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        // Man könnte mehrere adverbiale Angaben derselben Art zulassen, damit die bestehende nicht
        //  einfach überschrieben wird!
        return new SemPraedikatAkkPraepOhneLeerstellen(
                getVerb(),
                praepositionMitKasus, akk, praep,
                getModalpartikeln(),
                advAngabe, getNegationspartikel(), getAdvAngabeSkopusVerbAllg(),
                getAdvAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public SemPraedikatAkkPraepOhneLeerstellen neg() {
        return (SemPraedikatAkkPraepOhneLeerstellen) super.neg();
    }

    @Override
    public SemPraedikatAkkPraepOhneLeerstellen neg(
            @Nullable final Negationspartikelphrase negationspartikelphrase) {
        if (negationspartikelphrase == null) {
            return this;
        }

        return new SemPraedikatAkkPraepOhneLeerstellen(
                getVerb(),
                praepositionMitKasus, akk, praep,
                getModalpartikeln(),
                getAdvAngabeSkopusSatz(), negationspartikelphrase, getAdvAngabeSkopusVerbAllg(),
                getAdvAngabeSkopusVerbWohinWoher());
    }

    @Override
    public SemPraedikatAkkPraepOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        // Man könnte mehrere adverbiale Angaben derselben Art zulassen, damit die bestehende nicht
        //  einfach überschrieben wird!
        return new SemPraedikatAkkPraepOhneLeerstellen(
                getVerb(),
                praepositionMitKasus, akk, praep,
                getModalpartikeln(),
                getAdvAngabeSkopusSatz(), getNegationspartikel(), advAngabe,
                getAdvAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public SemPraedikatAkkPraepOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativWohinWoher advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        // Man könnte mehrere adverbiale Angaben derselben Art zulassen, damit die bestehende nicht
        //  einfach überschrieben wird!
        return new SemPraedikatAkkPraepOhneLeerstellen(
                getVerb(),
                praepositionMitKasus, akk, praep,
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

    @Override
    public @Nullable
    Konstituentenfolge getSpeziellesVorfeldAlsWeitereOption(
            final PraedRegMerkmale praedRegMerkmale) {
        @Nullable final Konstituentenfolge speziellesVorfeldFromSuper =
                super.getSpeziellesVorfeldAlsWeitereOption(praedRegMerkmale);
        if (speziellesVorfeldFromSuper != null) {
            return speziellesVorfeldFromSuper;
        }

        if (getNegationspartikel() != null) {
            return null;
        }

        /*
         * "es" allein darf nicht im Vorfeld stehen, wenn es ein Objekt ist
         * (Eisenberg Der SemSatz 5.4.2)
         */
        if (Personalpronomen.isPersonalpronomenEs(akk, AKK)) {
            return null;
        }

        /*
         * Phrasen (auch Personalpronomen) mit Fokuspartikel
         * sind häufig kontrastiv und daher oft für das Vorfeld geeignet.
         */
        if (akk.getFokuspartikel() != null) {
            // "Nur die Kugel nimmst du an dich"
            return akk.akkK();
        }

        // "Nur in das Glas schüttest du den Wein" - wirkt seltsam

        return null;
    }

    @Override
    @CheckReturnValue
    Konstituentenfolge getMittelfeldOhneLinksversetzungUnbetonterPronomen(
            final PraedRegMerkmale praedRegMerkmale) {
        return joinToKonstituentenfolge(
                getAdvAngabeSkopusSatzDescriptionFuerMittelfeld(praedRegMerkmale),
// "aus einer Laune heraus"
                getNegationspartikel() != null ? kf(getModalpartikeln()) : null,
                // "besser doch (nicht)"
                getNegationspartikel(), // "nicht"
                akk.akkK(), // "das Teil"
                getNegationspartikel() == null ? kf(getModalpartikeln()) : null, // "besser doch"
                getAdvAngabeSkopusVerbTextDescriptionFuerMittelfeld(praedRegMerkmale), // "erneut"
                getAdvAngabeSkopusVerbWohinWoherDescription(praedRegMerkmale),
                // "aus der La main"
                praep.imK(praepositionMitKasus)); // "nach dem Weg"
    }

    @Nullable
    @Override
    SubstPhrOderReflexivpronomen getDat(final PraedRegMerkmale praedRegMerkmale) {
        return null;
    }

    @Nullable
    @Override
    SubstPhrOderReflexivpronomen getAkk(final PraedRegMerkmale praedRegMerkmale) {
        return akk;
    }

    @Nullable
    @Override
    SubstPhrOderReflexivpronomen getZweitesAkk() {
        return null;
    }

    @Override
    public Konstituentenfolge getNachfeld(final PraedRegMerkmale praedRegMerkmale) {
        return Konstituentenfolge.joinToNullKonstituentenfolge(
                getAdvAngabeSkopusVerbTextDescriptionFuerZwangsausklammerung(praedRegMerkmale),
                getAdvAngabeSkopusSatzDescriptionFuerZwangsausklammerung(praedRegMerkmale)
        );
    }

    @Override
    public boolean umfasstSatzglieder() {
        return true;
    }

    @Nullable
    @Override
    public Konstituentenfolge getErstesInterrogativwort() {
        @Nullable
        Konstituentenfolge res = interroAdverbToKF(getAdvAngabeSkopusSatz());
        if (res != null) {
            return res;
        }

        if (akk instanceof Interrogativpronomen) {
            return akk.akkK();
        }

        res = interroAdverbToKF(getAdvAngabeSkopusVerbAllg());
        if (res != null) {
            return res;
        }

        res = interroAdverbToKF(getAdvAngabeSkopusVerbWohinWoher());
        if (res != null) {
            return res;
        }

        if (praep instanceof Interrogativpronomen) {
            return praep.imK(praepositionMitKasus);
        }

        return null;
    }

    @Nullable
    @Override
    @CheckReturnValue
    public Konstituentenfolge getRelativpronomen() {
        if (akk instanceof Relativpronomen) {
            return akk.akkK();
        }

        if (praep instanceof Relativpronomen) {
            // "mit dem"
            return praep.imK(praepositionMitKasus);
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
        final SemPraedikatAkkPraepOhneLeerstellen that = (SemPraedikatAkkPraepOhneLeerstellen) o;
        return akk.equals(that.akk) &&
                praep.equals(that.praep) &&
                praepositionMitKasus == that.praepositionMitKasus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), akk, praep, praepositionMitKasus);
    }
}
