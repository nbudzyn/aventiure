package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import java.util.Collection;

import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static de.nb.aventiure2.german.base.GermanUtil.joinToNull;

/**
 * Ein Prädikat, in dem ein Dativobjekt und Akkusativobjekt gesetzt sind
 * und es auch sonst keine Leerstellen gibt. Beispiel: "dem Frosch Angebote machen"
 */
public class PraedikatDatAkkOhneLeerstellen extends AbstractPraedikatOhneLeerstellen {
    /**
     * Das (Objekt / Wesen / Konzept für das) Dativobjekt (z.B. "Angebote")
     */
    @NonNull
    private final SubstantivischePhrase describableDat;

    /**
     * Das (Objekt / Wesen / Konzept für das) Akkusativobjekte (z.B. der Frosch)
     */
    @NonNull
    private final SubstantivischePhrase describableAkk;

    public PraedikatDatAkkOhneLeerstellen(
            final Verb verb,
            final SubstantivischePhrase describableDat,
            final SubstantivischePhrase describableAkk) {
        this(verb, describableDat, describableAkk,
                null, null,
                null);
    }

    private PraedikatDatAkkOhneLeerstellen(
            final Verb verb,
            final SubstantivischePhrase describableDat,
            final SubstantivischePhrase describableAkk,
            @Nullable final AdverbialeAngabeSkopusSatz adverbialeAngabeSkopusSatz,
            @Nullable final AdverbialeAngabeSkopusVerbAllg adverbialeAngabeSkopusVerbAllg,
            @Nullable
            final AdverbialeAngabeSkopusVerbWohinWoher adverbialeAngabeSkopusVerbWohinWoher) {
        super(verb, adverbialeAngabeSkopusSatz,
                adverbialeAngabeSkopusVerbAllg, adverbialeAngabeSkopusVerbWohinWoher);
        this.describableDat = describableDat;
        this.describableAkk = describableAkk;
    }

    @Override
    public PraedikatDatAkkOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusSatz adverbialeAngabe) {
        if (adverbialeAngabe == null) {
            return this;
        }

        // TODO Mehrere adverbiale Angaben zulassen, damit die bestehende nicht
        //  einfach überschrieben wird!
        return new PraedikatDatAkkOhneLeerstellen(
                getVerb(),
                describableDat, describableAkk,
                adverbialeAngabe, getAdverbialeAngabeSkopusVerbAllg(),
                getAdverbialeAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public PraedikatDatAkkOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusVerbAllg adverbialeAngabe) {
        if (adverbialeAngabe == null) {
            return this;
        }

        // TODO Mehrere adverbiale Angaben zulassen, damit die bestehende nicht
        //  einfach überschrieben wird!
        return new PraedikatDatAkkOhneLeerstellen(
                getVerb(),
                describableDat, describableAkk,
                getAdverbialeAngabeSkopusSatz(), adverbialeAngabe,
                getAdverbialeAngabeSkopusVerbWohinWoher()
        );
    }


    @Override
    public PraedikatDatAkkOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusVerbWohinWoher adverbialeAngabe) {
        if (adverbialeAngabe == null) {
            return this;
        }

        // TODO Mehrere adverbiale Angaben zulassen, damit die bestehende nicht
        //  einfach überschrieben wird!
        return new PraedikatDatAkkOhneLeerstellen(
                getVerb(),
                describableDat, describableAkk,
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

        return null; // "Die Kugel gibst du dem Frosch" - nicht schön
    }

    @Override
    public String getMittelfeld(final Collection<Modalpartikel> modalpartikeln) {
        return joinToNull(
                getAdverbialeAngabeSkopusSatz(), // "aus einer Laune heraus"
                describableDat.dat(), // "dem Frosch"
                joinToNull(modalpartikeln), // "halt"
                getAdverbialeAngabeSkopusVerbAllg(), // "auf deiner Flöte"
                describableAkk.akk()); // "ein Lied"
    }

    @Override
    public String getNachfeld() {
        return null;
    }
}
