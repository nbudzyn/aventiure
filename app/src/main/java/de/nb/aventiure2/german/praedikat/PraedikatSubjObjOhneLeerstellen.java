package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import java.util.Collection;

import javax.annotation.Nullable;

import de.nb.aventiure2.annotations.Argument;
import de.nb.aventiure2.annotations.Valenz;
import de.nb.aventiure2.german.base.GermanUtil;
import de.nb.aventiure2.german.base.Interrogativpronomen;
import de.nb.aventiure2.german.base.Kasus;
import de.nb.aventiure2.german.base.KasusOderPraepositionalkasus;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static de.nb.aventiure2.german.base.GermanUtil.joinToNullString;

/**
 * Ein Prädikat (Verb ggf. mit Präfix) bei dem das Verb mit einem Subjekt und einem
 * (Präpositional-) Objekt steht - alle Leerstellen sind besetzt.
 */
public class PraedikatSubjObjOhneLeerstellen
        extends AbstractAngabenfaehigesPraedikatOhneLeerstellen {
    /**
     * Der Kasus (z.B. Akkusativ, "die Kugel nehmen") oder Präpositionalkasus
     * (z.B. "mit dem Frosch reden"), mit dem dieses Verb steht (den dieses Verb regiert).
     */
    @NonNull
    private final KasusOderPraepositionalkasus kasusOderPraepositionalkasus;

    /**
     * Das Objekt (z.B. ein Ding, Wesen, Konzept oder deklinierbare Phrase)
     */
    @Argument
    private final SubstantivischePhrase objekt;

    @Valenz
    PraedikatSubjObjOhneLeerstellen(final Verb verb,
                                    final KasusOderPraepositionalkasus kasusOderPraepositionalkasus,
                                    final SubstantivischePhrase objekt) {
        this(verb, kasusOderPraepositionalkasus, objekt,
                null, null,
                null);
    }

    PraedikatSubjObjOhneLeerstellen(
            final Verb verb,
            final KasusOderPraepositionalkasus kasusOderPraepositionalkasus,
            final SubstantivischePhrase objekt,
            @Nullable final AdverbialeAngabeSkopusSatz adverbialeAngabeSkopusSatz,
            @Nullable final AdverbialeAngabeSkopusVerbAllg adverbialeAngabeSkopusVerbAllg,
            @Nullable
            final AdverbialeAngabeSkopusVerbWohinWoher adverbialeAngabeSkopusVerbWohinWoher) {
        super(verb, adverbialeAngabeSkopusSatz,
                adverbialeAngabeSkopusVerbAllg, adverbialeAngabeSkopusVerbWohinWoher);
        this.kasusOderPraepositionalkasus = kasusOderPraepositionalkasus;
        this.objekt = objekt;
    }

    @Override
    public PraedikatSubjObjOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusSatz adverbialeAngabe) {
        if (adverbialeAngabe == null) {
            return this;
        }

        // TODO Mehrere adverbiale Angaben zulassen, damit die bestehende nicht
        //  einfach überschrieben wird!
        return new PraedikatSubjObjOhneLeerstellen(
                getVerb(),
                kasusOderPraepositionalkasus, objekt,
                adverbialeAngabe, getAdverbialeAngabeSkopusVerbAllg(),
                getAdverbialeAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public PraedikatSubjObjOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusVerbAllg adverbialeAngabe) {
        if (adverbialeAngabe == null) {
            return this;
        }

        // TODO Mehrere adverbiale Angaben zulassen, damit die bestehende nicht
        //  einfach überschrieben wird!
        return new PraedikatSubjObjOhneLeerstellen(
                getVerb(),
                kasusOderPraepositionalkasus, objekt,
                getAdverbialeAngabeSkopusSatz(), adverbialeAngabe,
                getAdverbialeAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public PraedikatSubjObjOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusVerbWohinWoher adverbialeAngabe) {
        if (adverbialeAngabe == null) {
            return this;
        }

        // TODO Mehrere adverbiale Angaben zulassen, damit die bestehende nicht
        //  einfach überschrieben wird!
        return new PraedikatSubjObjOhneLeerstellen(
                getVerb(),
                kasusOderPraepositionalkasus, objekt,
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

        final String objektImKasusOderPraepkasus = objekt.im(kasusOderPraepositionalkasus);
        if (!"es" .equals(objektImKasusOderPraepkasus)) {
            // Wenn "es" ein Objekt ist, darf es nicht im Vorfeld stehen.
            // (Eisenberg Der Satz 5.4.2)
            return objektImKasusOderPraepkasus;  // "den Frosch"
        }

        return null;
    }

    @Override
    public String getMittelfeld(final Collection<Modalpartikel> modalpartikeln,
                                final Person personSubjekt,
                                final Numerus numerusSubjekt) {
        return joinToNullString(
                getAdverbialeAngabeSkopusSatz(), // "aus einer Laune heraus"
                GermanUtil.joinToNullString(modalpartikeln), // "mal eben"
                getAdverbialeAngabeSkopusVerbAllg(), // "erneut"
                objekt.im(kasusOderPraepositionalkasus),
                getAdverbialeAngabeSkopusVerbWohinWoher() // "auf den Tisch"
        );
    }

    @Override
    public String getNachfeld(final Person personSubjekt,
                              final Numerus numerusSubjekt) {
        return null;
    }

    @Override
    public boolean
    umfasstSatzglieder() {
        return true;
    }

    @Override
    public boolean hatAkkusativobjekt() {
        return kasusOderPraepositionalkasus == Kasus.AKK;
    }

    @Nullable
    @Override
    public String getErstesInterrogativpronomenAlsString() {
        if (objekt instanceof Interrogativpronomen) {
            return objekt.im(kasusOderPraepositionalkasus);
        }

        return null;
    }
}
