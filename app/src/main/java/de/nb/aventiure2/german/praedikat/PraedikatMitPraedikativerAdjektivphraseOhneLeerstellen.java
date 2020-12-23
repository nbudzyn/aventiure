package de.nb.aventiure2.german.praedikat;

import java.util.Collection;

import javax.annotation.Nullable;

import de.nb.aventiure2.annotations.Argument;
import de.nb.aventiure2.annotations.Valenz;
import de.nb.aventiure2.german.base.GermanUtil;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.description.AllgDescription;

import static de.nb.aventiure2.german.base.GermanUtil.joinToNullString;

/**
 * Ein Prädikat, bestehend aus einem Verb und einer prädikativen Adjektivphrase, in dem
 * alle Leerstellen besetzt sind.
 * <p>
 * Beispiele:
 * <ul>
 *     <li>glücklich wirken
 *     <li>glücklich wirken, Peter zu sehen
 * </ul>
 * <p>
 * Hier geht es nicht um Prädikative, vgl. {@link PraedikativumPraedikatOhneLeerstellen}.
 */
public class PraedikatMitPraedikativerAdjektivphraseOhneLeerstellen
        extends AbstractAngabenfaehigesPraedikatOhneLeerstellen {
    /**
     * Die prädikative Adjektivphrase
     */
    @Argument
    private final AllgDescription praedikativeAdjektivphrase;

    @Valenz
    PraedikatMitPraedikativerAdjektivphraseOhneLeerstellen(
            final Verb verb,
            final AllgDescription praedikativeAdjektivphrase) {
        this(verb, praedikativeAdjektivphrase,
                null, null,
                null);
    }

    private PraedikatMitPraedikativerAdjektivphraseOhneLeerstellen(
            final Verb verb,
            final AllgDescription praedikativeAdjektivphrase,
            @Nullable final AdverbialeAngabeSkopusSatz adverbialeAngabeSkopusSatz,
            @Nullable final AdverbialeAngabeSkopusVerbAllg adverbialeAngabeSkopusVerbAllg,
            @Nullable
            final AdverbialeAngabeSkopusVerbWohinWoher adverbialeAngabeSkopusVerbWohinWoher) {
        super(verb, adverbialeAngabeSkopusSatz,
                adverbialeAngabeSkopusVerbAllg, adverbialeAngabeSkopusVerbWohinWoher);
        this.praedikativeAdjektivphrase = praedikativeAdjektivphrase;
    }

    @Override
    public PraedikatMitPraedikativerAdjektivphraseOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusSatz adverbialeAngabe) {
        if (adverbialeAngabe == null) {
            return this;
        }

        return new PraedikatMitPraedikativerAdjektivphraseOhneLeerstellen(
                getVerb(),
                praedikativeAdjektivphrase,
                adverbialeAngabe, getAdverbialeAngabeSkopusVerbAllg(),
                getAdverbialeAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public PraedikatMitPraedikativerAdjektivphraseOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusVerbAllg adverbialeAngabe) {
        if (adverbialeAngabe == null) {
            return this;
        }

        return new PraedikatMitPraedikativerAdjektivphraseOhneLeerstellen(
                getVerb(),
                praedikativeAdjektivphrase,
                getAdverbialeAngabeSkopusSatz(), adverbialeAngabe,
                getAdverbialeAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public PraedikatMitPraedikativerAdjektivphraseOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusVerbWohinWoher adverbialeAngabe) {
        if (adverbialeAngabe == null) {
            return this;
        }

        // Ich frage mich, ob es so etwas überhauppt geben kann.
        // *"Peter ist nach Norden glücklich" - wohl nicht.
        return new PraedikatMitPraedikativerAdjektivphraseOhneLeerstellen(
                getVerb(),
                praedikativeAdjektivphrase,
                getAdverbialeAngabeSkopusSatz(),
                getAdverbialeAngabeSkopusVerbAllg(),
                adverbialeAngabe
        );
    }

    @Override
    public boolean duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen() {
        return !praedikativeAdjektivphrase.isKommaStehtAus();
    }

    @Override
    public boolean kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden() {
        return !praedikativeAdjektivphrase.isKommaStehtAus();
    }

    @Override
    public @Nullable
    String getSpeziellesVorfeld() {
        @Nullable final String speziellesVorfeldFromSuper = super.getSpeziellesVorfeld();
        if (speziellesVorfeldFromSuper != null) {
            return speziellesVorfeldFromSuper;
        }

        if (!praedikativeAdjektivphrase.isKommaStehtAus()) {
            // "Glücklich wirkt sie"
            // Stark markiert - aber möglich.
            return praedikativeAdjektivphrase.getDescriptionHauptsatz();
        }

        return null;
    }

    @Override
    public String getMittelfeld(final Collection<Modalpartikel> modalpartikeln,
                                final Person personSubjekt,
                                final Numerus numerusSubjekt) {
        return joinToNullString(
                getAdverbialeAngabeSkopusSatz(), // "leider"
                GermanUtil.joinToNullString(modalpartikeln), // "halt"
                getAdverbialeAngabeSkopusVerbAllg(), // "erneut"
                getAdverbialeAngabeSkopusVerbWohinWoher(), // "nach außen" (?)
                praedikativeAdjektivphrase // "glücklich"
        );
    }

    @Override
    public String getNachfeld(final Person personSubjekt,
                              final Numerus numerusSubjekt) {
        // STORY Die Adjektivphrase könnte diskontinuierlich aufgeteilt werden, dann könnte
        //  ein Teil ins Nachfeld kommen:
        //  Sie hat GLÜCKLICH gewirkt, DICH ZU SEHEN.
        //  (alternativ zu "Sie hat GLÜCKLICH, DICH ZU SEHEN, gewirkt").

        return null;
    }

    @Override
    public boolean umfasstSatzglieder() {
        return true;
    }

    @Override
    public boolean hatAkkusativobjekt() {
        return false;
    }

    @Nullable
    @Override
    public String getErstesInterrogativpronomenAlsString() {
        return null;
    }
}
