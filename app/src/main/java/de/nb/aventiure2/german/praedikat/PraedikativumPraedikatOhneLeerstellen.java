package de.nb.aventiure2.german.praedikat;

import java.util.Collection;

import javax.annotation.Nullable;

import de.nb.aventiure2.annotations.Argument;
import de.nb.aventiure2.annotations.VerbValenz;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.description.AllgDescription;

import static de.nb.aventiure2.german.base.GermanUtil.joinToNull;

/**
 * Ein Prädikat, bestehend aus <i>sein</i> und einem Prädikativum - alle Leerstellen sind besetzt.
 */
public class PraedikativumPraedikatOhneLeerstellen extends AbstractPraedikatOhneLeerstellen {
    /**
     * Das Prädikativum
     */
    @Argument
    private final AllgDescription praedikativum;

    public static PraedikativumPraedikatOhneLeerstellen praedikativumPraedikatMit(
            final AllgDescription praedikativum) {
        return new PraedikativumPraedikatOhneLeerstellen(SeinUtil.VERB, praedikativum);
    }

    @VerbValenz
    private PraedikativumPraedikatOhneLeerstellen(final Verb verb,
                                                  final AllgDescription praedikativum) {
        this(verb, praedikativum,
                null, null,
                null);
    }

    private PraedikativumPraedikatOhneLeerstellen(
            final Verb verb,
            final AllgDescription praedikativum,
            @Nullable final AdverbialeAngabeSkopusSatz adverbialeAngabeSkopusSatz,
            @Nullable final AdverbialeAngabeSkopusVerbAllg adverbialeAngabeSkopusVerbAllg,
            @Nullable
            final AdverbialeAngabeSkopusVerbWohinWoher adverbialeAngabeSkopusVerbWohinWoher) {
        super(verb, adverbialeAngabeSkopusSatz,
                adverbialeAngabeSkopusVerbAllg, adverbialeAngabeSkopusVerbWohinWoher);
        this.praedikativum = praedikativum;
    }

    @Override
    public PraedikativumPraedikatOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusSatz adverbialeAngabe) {
        if (adverbialeAngabe == null) {
            return this;
        }

        return new PraedikativumPraedikatOhneLeerstellen(
                getVerb(),
                praedikativum,
                adverbialeAngabe, getAdverbialeAngabeSkopusVerbAllg(),
                getAdverbialeAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public PraedikativumPraedikatOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusVerbAllg adverbialeAngabe) {
        if (adverbialeAngabe == null) {
            return this;
        }

        return new PraedikativumPraedikatOhneLeerstellen(
                getVerb(),
                praedikativum,
                getAdverbialeAngabeSkopusSatz(), adverbialeAngabe,
                getAdverbialeAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public PraedikativumPraedikatOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusVerbWohinWoher adverbialeAngabe) {
        if (adverbialeAngabe == null) {
            return this;
        }

        // Ich frage mich, ob es so etwas überhauppt geben kann.
        // *"Peter ist nach Norden glücklich" - wohl nicht.
        return new PraedikativumPraedikatOhneLeerstellen(
                getVerb(),
                praedikativum,
                getAdverbialeAngabeSkopusSatz(),
                getAdverbialeAngabeSkopusVerbAllg(),
                adverbialeAngabe
        );
    }

    @Override
    public boolean duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen() {
        return !praedikativum.isKommaStehtAus();
    }

    @Override
    public boolean kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden() {
        // ?"Endlich wieder einmal glücklich gewesen verlässt du das Theater."
        return false;

        // STORY Möglich wäre auf jeden Fall die Negation:
        //  "Lange nicht mehr so glücklich gewesen verlässt du das Theater."
    }

    @Override
    public @Nullable
    String getSpeziellesVorfeld() {
        @Nullable final String speziellesVorfeldFromSuper = super.getSpeziellesVorfeld();
        if (speziellesVorfeldFromSuper != null) {
            return speziellesVorfeldFromSuper;
        }

        // "Ein Esel ist Peter", "Glücklich ist Peter".
        // Stark markiert - aber möglich.
        return praedikativum.getDescriptionHauptsatz();
    }

    @Override
    public String getMittelfeld(final Collection<Modalpartikel> modalpartikeln,
                                final Person personSubjekt,
                                final Numerus numerusSubjekt) {
        return joinToNull(
                getAdverbialeAngabeSkopusSatz(), // "plötzlich"
                joinToNull(modalpartikeln), // "halt"
                getAdverbialeAngabeSkopusVerbAllg(), // "erneut"
                praedikativum, // "glücklich"
                getAdverbialeAngabeSkopusVerbWohinWoher() // (diese Position kann wohl
                // nicht besetzt sein)
        );
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
        return false;
    }
}
