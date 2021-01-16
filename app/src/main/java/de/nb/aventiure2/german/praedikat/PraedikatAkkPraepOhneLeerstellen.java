package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.Collection;

import javax.annotation.Nullable;

import de.nb.aventiure2.annotations.Komplement;
import de.nb.aventiure2.annotations.Valenz;
import de.nb.aventiure2.german.base.Interrogativpronomen;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.PraepositionMitKasus;
import de.nb.aventiure2.german.base.SubstPhrOderReflexivpronomen;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static de.nb.aventiure2.german.base.Konstituentenfolge.kf;
import static de.nb.aventiure2.german.base.Personalpronomen.isPersonalpronomen;

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
            @Nullable final AdverbialeAngabeSkopusSatz adverbialeAngabeSkopusSatz,
            @Nullable final AdverbialeAngabeSkopusVerbAllg adverbialeAngabeSkopusVerbAllg,
            @Nullable
            final AdverbialeAngabeSkopusVerbWohinWoher adverbialeAngabeSkopusVerbWohinWoher) {
        super(verb, modalpartikeln, adverbialeAngabeSkopusSatz,
                adverbialeAngabeSkopusVerbAllg, adverbialeAngabeSkopusVerbWohinWoher);
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
                getAdverbialeAngabeSkopusSatz(),
                getAdverbialeAngabeSkopusVerbAllg(),
                getAdverbialeAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public PraedikatAkkPraepOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusSatz adverbialeAngabe) {
        if (adverbialeAngabe == null) {
            return this;
        }

        // Man könnte mehrere adverbiale Angaben derselben Art zulassen, damit die bestehende nicht
        //  einfach überschrieben wird!
        return new PraedikatAkkPraepOhneLeerstellen(
                getVerb(),
                praepositionMitKasus, akk, praep,
                getModalpartikeln(),
                adverbialeAngabe, getAdverbialeAngabeSkopusVerbAllg(),
                getAdverbialeAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public PraedikatAkkPraepOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusVerbAllg adverbialeAngabe) {
        if (adverbialeAngabe == null) {
            return this;
        }

        // Man könnte mehrere adverbiale Angaben derselben Art zulassen, damit die bestehende nicht
        //  einfach überschrieben wird!
        return new PraedikatAkkPraepOhneLeerstellen(
                getVerb(),
                praepositionMitKasus, akk, praep,
                getModalpartikeln(),
                getAdverbialeAngabeSkopusSatz(), adverbialeAngabe,
                getAdverbialeAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public PraedikatAkkPraepOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusVerbWohinWoher adverbialeAngabe) {
        if (adverbialeAngabe == null) {
            return this;
        }

        // Man könnte mehrere adverbiale Angaben derselben Art zulassen, damit die bestehende nicht
        //  einfach überschrieben wird!
        return new PraedikatAkkPraepOhneLeerstellen(
                getVerb(),
                praepositionMitKasus, akk, praep,
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

    @Override
    public @Nullable
    Konstituente getSpeziellesVorfeldAlsWeitereOption(final Person person, final Numerus numerus) {
        @Nullable final Konstituente speziellesVorfeldFromSuper =
                super.getSpeziellesVorfeldAlsWeitereOption(person, numerus);
        if (speziellesVorfeldFromSuper != null) {
            return speziellesVorfeldFromSuper;
        }

        final Konstituente akk = this.akk.akkK();
        // Wenn "es" ein Objekt ist, darf es nicht im Vorfeld stehen.
        // (Eisenberg Der Satz 5.4.2)
        // Aber auch andere Personalpronomen wirken im Vorfeld oft eher unangebracht,
        // wenn es sich um ein Objekt handelt.
        // "Ihn nimmst du an dich."
        if (!isPersonalpronomen(akk.getString())) {
            return akk; // "das Teil"
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
                akk.akkK(), // "das Teil"
                kf(getModalpartikeln()), // "besser doch"
                getAdverbialeAngabeSkopusVerbAllgDescriptionFuerMittelfeld(personSubjekt,
                        numerusSubjekt), // "erneut"
                getAdverbialeAngabeSkopusVerbWohinWoherDescription(personSubjekt, numerusSubjekt),
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
                getAdverbialeAngabeSkopusVerbAllgDescriptionFuerZwangsausklammerung(personSubjekt,
                        numerusSubjekt),
                getAdverbialeAngabeSkopusSatzDescriptionFuerZwangsausklammerung(personSubjekt,
                        numerusSubjekt)
        );
    }

    @Override
    public boolean umfasstSatzglieder() {
        return true;
    }

    @Nullable
    @Override
    public Konstituente getErstesInterrogativpronomen() {
        if (akk instanceof Interrogativpronomen) {
            return akk.akkK();
        }

        if (praep instanceof Interrogativpronomen) {
            return praep.imK(praepositionMitKasus);
        }

        return null;
    }
}
