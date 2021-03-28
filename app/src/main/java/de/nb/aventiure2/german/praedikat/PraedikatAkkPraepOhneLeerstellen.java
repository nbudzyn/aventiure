package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.Collection;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;

import de.nb.aventiure2.annotations.Komplement;
import de.nb.aventiure2.annotations.Valenz;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativSkopusSatz;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativVerbAllg;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativWohinWoher;
import de.nb.aventiure2.german.base.Interrogativpronomen;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.base.PraepositionMitKasus;
import de.nb.aventiure2.german.base.Relativpronomen;
import de.nb.aventiure2.german.base.SubstPhrOderReflexivpronomen;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static de.nb.aventiure2.german.base.Kasus.AKK;
import static de.nb.aventiure2.german.base.Konstituentenfolge.kf;

/**
 * Ein Prädikat, in dem ein Akkusativobjekt und ein Präpositionalobjekt gesetzt sind
 * und es auch sonst keine Leerstellen gibt. Beispiel: "den Frosch in die Hände nehmen"
 */
public class PraedikatAkkPraepOhneLeerstellen
        extends AbstractAngabenfaehigesPraedikatOhneLeerstellen {
    @Komplement
    @NonNull
    private final SubstantivischePhrase akk;

    @Komplement
    @NonNull
    private final SubstantivischePhrase praep;

    @NonNull
    private final PraepositionMitKasus praepositionMitKasus;

    @Valenz
    PraedikatAkkPraepOhneLeerstellen(
            final Verb verb,
            final PraepositionMitKasus praepositionMitKasus,
            final SubstantivischePhrase akk,
            final SubstantivischePhrase praep) {
        this(verb, praepositionMitKasus, akk, praep,
                ImmutableList.of(), null, null,
                null);
    }

    private PraedikatAkkPraepOhneLeerstellen(
            final Verb verb,
            final PraepositionMitKasus praepositionMitKasus,
            final SubstantivischePhrase akk,
            final SubstantivischePhrase praep,
            final Iterable<Modalpartikel> modalpartikeln,
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabeSkopusSatz,
            @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabeSkopusVerbAllg,
            @Nullable final IAdvAngabeOderInterrogativWohinWoher advAngabeSkopusVerbWohinWoher) {
        super(verb, modalpartikeln,
                advAngabeSkopusSatz,
                advAngabeSkopusVerbAllg, advAngabeSkopusVerbWohinWoher);
        this.praepositionMitKasus = praepositionMitKasus;
        this.praep = praep;
        this.akk = akk;
    }

    @Override
    public PraedikatAkkPraepOhneLeerstellen mitModalpartikeln(
            final Collection<Modalpartikel> modalpartikeln) {
        return new PraedikatAkkPraepOhneLeerstellen(
                getVerb(), praepositionMitKasus, akk, praep,
                Iterables.concat(getModalpartikeln(), modalpartikeln),
                getAdvAngabeSkopusSatz(),
                getAdvAngabeSkopusVerbAllg(),
                getAdvAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public PraedikatAkkPraepOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        // Man könnte mehrere adverbiale Angaben derselben Art zulassen, damit die bestehende nicht
        //  einfach überschrieben wird!
        return new PraedikatAkkPraepOhneLeerstellen(
                getVerb(),
                praepositionMitKasus, akk, praep,
                getModalpartikeln(),
                advAngabe, getAdvAngabeSkopusVerbAllg(),
                getAdvAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public PraedikatAkkPraepOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        // Man könnte mehrere adverbiale Angaben derselben Art zulassen, damit die bestehende nicht
        //  einfach überschrieben wird!
        return new PraedikatAkkPraepOhneLeerstellen(
                getVerb(),
                praepositionMitKasus, akk, praep,
                getModalpartikeln(),
                getAdvAngabeSkopusSatz(), advAngabe,
                getAdvAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public PraedikatAkkPraepOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativWohinWoher advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        // Man könnte mehrere adverbiale Angaben derselben Art zulassen, damit die bestehende nicht
        //  einfach überschrieben wird!
        return new PraedikatAkkPraepOhneLeerstellen(
                getVerb(),
                praepositionMitKasus, akk, praep,
                getModalpartikeln(),
                getAdvAngabeSkopusSatz(),
                getAdvAngabeSkopusVerbAllg(),
                advAngabe
        );
    }

    @Override
    public boolean kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden() {
        return true;
    }

    @Override
    public @Nullable
    Konstituentenfolge getSpeziellesVorfeldAlsWeitereOption(final Person person,
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
            final Person personSubjekt, final Numerus numerusSubjekt) {
        return Konstituentenfolge.joinToKonstituentenfolge(
                getAdvAngabeSkopusSatzDescriptionFuerMittelfeld(personSubjekt,
                        numerusSubjekt),
                // "aus einer Laune heraus"
                akk.akkK(), // "das Teil"
                kf(getModalpartikeln()), // "besser doch"
                getAdvAngabeSkopusVerbTextDescriptionFuerMittelfeld(personSubjekt,
                        numerusSubjekt), // "erneut"
                getAdvAngabeSkopusVerbWohinWoherDescription(personSubjekt, numerusSubjekt),
                // "anch dem Weg"
                praep.imK(praepositionMitKasus)); // "aus der La main"
    }

    @Nullable
    @Override
    SubstPhrOderReflexivpronomen getDat(
            final Person personSubjekt, final Numerus numerusSubjekt) {
        return null;
    }

    @Nullable
    @Override
    SubstPhrOderReflexivpronomen getAkk(
            final Person personSubjekt, final Numerus numerusSubjekt) {
        return akk;
    }

    @Nullable
    @Override
    SubstPhrOderReflexivpronomen getZweitesAkk() {
        return null;
    }

    @Override
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

        if (praep instanceof Interrogativpronomen) {
            // "mit dem"
            return praep.imK(praepositionMitKasus);
        }

        return null;
    }
}
