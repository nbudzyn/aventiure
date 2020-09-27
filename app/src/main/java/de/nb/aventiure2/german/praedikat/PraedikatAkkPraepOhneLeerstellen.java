package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import java.util.Collection;

import javax.annotation.Nullable;

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

    @NonNull
    private final SubstantivischePhrase describablePraep;

    @NonNull
    private final SubstantivischePhrase describableAkk;

    public PraedikatAkkPraepOhneLeerstellen(
            final Verb verb,
            final PraepositionMitKasus praepositionMitKasus,
            final SubstantivischePhrase describableAkk,
            final SubstantivischePhrase describablePraep) {
        this(verb, praepositionMitKasus, describableAkk, describablePraep,
                null, null,
                null);
    }

    private PraedikatAkkPraepOhneLeerstellen(
            final Verb verb,
            final PraepositionMitKasus praepositionMitKasus,
            final SubstantivischePhrase describableAkk,
            final SubstantivischePhrase describablePraep,
            @Nullable final AdverbialeAngabeSkopusSatz adverbialeAngabeSkopusSatz,
            @Nullable final AdverbialeAngabeSkopusVerbAllg adverbialeAngabeSkopusVerbAllg,
            @Nullable
            final AdverbialeAngabeSkopusVerbWohinWoher adverbialeAngabeSkopusVerbWohinWoher) {
        super(verb, adverbialeAngabeSkopusSatz,
                adverbialeAngabeSkopusVerbAllg, adverbialeAngabeSkopusVerbWohinWoher);
        this.praepositionMitKasus = praepositionMitKasus;
        this.describablePraep = describablePraep;
        this.describableAkk = describableAkk;
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
                praepositionMitKasus, describableAkk, describablePraep,
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
                praepositionMitKasus, describableAkk, describablePraep,
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
                praepositionMitKasus, describableAkk, describablePraep,
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
    public @Nullable
    String getSpeziellesVorfeld() {
        final String speziellesVorfeldFromSuper = super.getSpeziellesVorfeld();
        if (speziellesVorfeldFromSuper != null) {
            return speziellesVorfeldFromSuper;
        }

        final String akk = describableAkk.akk();
        if (!"es".equals(akk)) {
            // Wenn "es" ein Objekt ist, darf es nicht im Vorfeld stehen.
            // (Eisenberg Der Satz 5.4.2)
            return akk; // "das Teil"
        }

        return null;
    }

    @Override
    public String getMittelfeld(final Collection<Modalpartikel> modalpartikeln) {
        return joinToNull(
                getAdverbialeAngabeSkopusSatz(), // "aus einer Laune heraus"
                describableAkk.akk(), // "das Teil"
                joinToNull(modalpartikeln), // "besser doch"
                getAdverbialeAngabeSkopusVerbAllg(), // "erneut"
                getAdverbialeAngabeSkopusVerbWohinWoher(), // "anch dem Weg"
                describablePraep.im(praepositionMitKasus)); // "aus der La main"
    }

    @Override
    public String getNachfeld() {
        return null;
    }
}
