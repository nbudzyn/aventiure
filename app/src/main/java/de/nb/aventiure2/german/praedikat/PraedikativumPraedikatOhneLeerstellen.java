package de.nb.aventiure2.german.praedikat;

import java.util.Collection;

import javax.annotation.Nullable;

import de.nb.aventiure2.annotations.Argument;
import de.nb.aventiure2.annotations.Valenz;
import de.nb.aventiure2.german.base.GermanUtil;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.Wortfolge;
import de.nb.aventiure2.german.description.AllgDescription;

import static de.nb.aventiure2.german.base.GermanUtil.joinToNull;
import static de.nb.aventiure2.german.base.Wortfolge.w;

/**
 * Ein Prädikat, bestehend aus <i>sein</i> und einem Prädikativum - alle Leerstellen sind besetzt.
 */
public class PraedikativumPraedikatOhneLeerstellen
        extends AbstractAngabenfaehigesPraedikatOhneLeerstellen {
    /**
     * Das Prädikativum
     */
    @Argument
    private final AllgDescription praedikativum;

    static PraedikativumPraedikatOhneLeerstellen praedikativumPraedikatMit(
            final AllgDescription praedikativum) {
        return new PraedikativumPraedikatOhneLeerstellen(SeinUtil.VERB,
                praedikativum);
    }

    @Valenz
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

        // Diese Position kann wohl nicht besetzt sein.
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

        if (!praedikativum.isKommaStehtAus()) {
            // "Ein Esel ist Peter", "Glücklich ist Peter".
            // Stark markiert - aber möglich.
            return praedikativum.getDescriptionHauptsatz();
        }

        return null;
    }

    @Override
    public Wortfolge getMittelfeld(final Collection<Modalpartikel> modalpartikeln,
                                   final Person personSubjekt,
                                   final Numerus numerusSubjekt) {
        return joinToNull(
                getAdverbialeAngabeSkopusSatz(), // "plötzlich"
                GermanUtil.joinToNullString(modalpartikeln), // "halt"
                getAdverbialeAngabeSkopusVerbAllg(), // "erneut"
                getAdverbialeAngabeSkopusVerbWohinWoher(), // (kann wohl nicht besetzt sein?)
                w(praedikativum.getDescriptionHauptsatz(), praedikativum.isKommaStehtAus())
                // "glücklich"
        );
    }

    @Override
    public String getNachfeld(final Person personSubjekt,
                              final Numerus numerusSubjekt) {
        // STORY Das Prädikativum könnte diskontinuierlich aufgeteilt werden, dann könnte
        //  ein Teil ins Nachfeld kommen:
        //  Sie ist GLÜCKLICH gewesen, DICH ZU SEHEN.
        //  (alternativ zu ?"Sie ist GLÜCKLICH, DICH ZU SEHEN, gewesen").

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
