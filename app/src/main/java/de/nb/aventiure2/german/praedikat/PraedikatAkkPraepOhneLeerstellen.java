package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import java.util.Collection;

import javax.annotation.Nullable;

import de.nb.aventiure2.annotations.Argument;
import de.nb.aventiure2.annotations.VerbValenz;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.PraepositionMitKasus;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static de.nb.aventiure2.german.base.GermanUtil.joinToNull;

/**
 * Ein Prädikat, in dem ein Akkusativobjekt und ein Präpositionalobjekt gesetzt sind
 * und es auch sonst keine Leerstellen gibt. Beispiel: "den Frosch in die Hände nehmen"
 */
public class PraedikatAkkPraepOhneLeerstellen extends AbstractPraedikatOhneLeerstellen {
    @NonNull
    private final PraepositionMitKasus praepositionMitKasus;

    @Argument
    @NonNull
    private final SubstantivischePhrase akk;

    @Argument
    @NonNull
    private final SubstantivischePhrase praep;

    @VerbValenz
    PraedikatAkkPraepOhneLeerstellen(
            final Verb verb,
            final PraepositionMitKasus praepositionMitKasus,
            final SubstantivischePhrase akk,
            final SubstantivischePhrase praep) {
        this(verb, praepositionMitKasus, akk, praep,
                null, null,
                null);
    }

    private PraedikatAkkPraepOhneLeerstellen(
            final Verb verb,
            final PraepositionMitKasus praepositionMitKasus,
            final SubstantivischePhrase akk,
            final SubstantivischePhrase praep,
            @Nullable final AdverbialeAngabeSkopusSatz adverbialeAngabeSkopusSatz,
            @Nullable final AdverbialeAngabeSkopusVerbAllg adverbialeAngabeSkopusVerbAllg,
            @Nullable
            final AdverbialeAngabeSkopusVerbWohinWoher adverbialeAngabeSkopusVerbWohinWoher) {
        super(verb, adverbialeAngabeSkopusSatz,
                adverbialeAngabeSkopusVerbAllg, adverbialeAngabeSkopusVerbWohinWoher);
        this.praepositionMitKasus = praepositionMitKasus;
        this.praep = praep;
        this.akk = akk;
    }

    @Override
    public PraedikatAkkPraepOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusSatz adverbialeAngabe) {
        if (adverbialeAngabe == null) {
            return this;
        }

        // TODO Mehrere adverbiale Angaben zulassen, damit die bestehende nicht
        //  einfach überschrieben wird!
        return new PraedikatAkkPraepOhneLeerstellen(
                getVerb(),
                praepositionMitKasus, akk, praep,
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

        // TODO Mehrere adverbiale Angaben zulassen, damit die bestehende nicht
        //  einfach überschrieben wird!
        return new PraedikatAkkPraepOhneLeerstellen(
                getVerb(),
                praepositionMitKasus, akk, praep,
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

        // TODO Mehrere adverbiale Angaben zulassen, damit die bestehende nicht
        //  einfach überschrieben wird!
        return new PraedikatAkkPraepOhneLeerstellen(
                getVerb(),
                praepositionMitKasus, akk, praep,
                getAdverbialeAngabeSkopusSatz(),
                getAdverbialeAngabeSkopusVerbAllg(),
                adverbialeAngabe
        );
    }


    @Override
    public boolean duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen() {
        return true;
    }

    @Override
    public boolean kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden() {
        return true;
    }

    @Override
    public @Nullable
    String getSpeziellesVorfeld() {
        @Nullable final String speziellesVorfeldFromSuper = super.getSpeziellesVorfeld();
        if (speziellesVorfeldFromSuper != null) {
            return speziellesVorfeldFromSuper;
        }

        final String akk = this.akk.akk();
        if (!"es" .equals(akk)) {
            // Wenn "es" ein Objekt ist, darf es nicht im Vorfeld stehen.
            // (Eisenberg Der Satz 5.4.2)
            return akk; // "das Teil"
        }

        return null;
    }

    @Override
    public String getMittelfeld(final Collection<Modalpartikel> modalpartikeln,
                                final Person personSubjekt,
                                final Numerus numerusSubjekt) {
        return joinToNull(
                getAdverbialeAngabeSkopusSatz(), // "aus einer Laune heraus"
                akk.akk(), // "das Teil"
                joinToNull(modalpartikeln), // "besser doch"
                getAdverbialeAngabeSkopusVerbAllg(), // "erneut"
                getAdverbialeAngabeSkopusVerbWohinWoher(), // "anch dem Weg"
                praep.im(praepositionMitKasus)); // "aus der La main"
    }

    @Override
    public String getNachfeld(final Person personSubjekt,
                              final Numerus numerusSubjekt) {
        return null;
    }

    @Override
    public boolean umfasstSatzglieder() {
        return true;
    }

    @Override
    public boolean hatAkkusativobjekt() {
        return true;
    }
}
