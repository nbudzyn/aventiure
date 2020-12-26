package de.nb.aventiure2.german.praedikat;

import java.util.Collection;

import javax.annotation.Nullable;

import de.nb.aventiure2.annotations.Argument;
import de.nb.aventiure2.annotations.Valenz;
import de.nb.aventiure2.german.base.GermanUtil;
import de.nb.aventiure2.german.base.Interrogativpronomen;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.Praedikativum;
import de.nb.aventiure2.german.base.Wortfolge;

import static de.nb.aventiure2.german.base.GermanUtil.joinToNull;
import static de.nb.aventiure2.german.base.Numerus.SG;
import static de.nb.aventiure2.german.base.Person.P2;

/**
 * Ein Prädikat, bestehend aus <i>sein</i> und einem Prädikativum - alle Leerstellen sind besetzt.
 */
public class PraedikativumPraedikatOhneLeerstellen
        extends AbstractAngabenfaehigesPraedikatOhneLeerstellen {
    /**
     * Das Prädikativum
     */
    @Argument
    private final Praedikativum praedikativum;

    public static PraedikativumPraedikatOhneLeerstellen praedikativumPraedikatMit(
            final Praedikativum praedikativum) {
        return new PraedikativumPraedikatOhneLeerstellen(SeinUtil.VERB,
                praedikativum);
    }

    @Valenz
    private PraedikativumPraedikatOhneLeerstellen(final Verb verb,
                                                  final Praedikativum praedikativum) {
        this(verb, praedikativum,
                null, null,
                null);
    }

    private PraedikativumPraedikatOhneLeerstellen(
            final Verb verb,
            final Praedikativum praedikativum,
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
        // FIXME Das hier ist etwas unschön - eigentlich sollte die Frage, ob zusammengezogen
        //  werden kann, erst an die Wortfolge gestellt werden, nicht zuvor!

        // Ob ein Komma aussteht, wird wohl in jeder Person und jedem Numerus gleich sein...
        return !praedikativum.getPraedikativ(P2, SG).kommmaStehtAus();
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

        // "Ein Esel ist Peter", "Glücklich ist Peter" - das wäre stark markiert, wir lassen
        // es hier aus.

        // FIXME Das Prädikativum könnte diskontinuierlich aufgeteilt werden, dann könnte
        //  ein Teil ins Vorgeld kommen: Glücklich ist sie, dich zu sehen.
        //  Problem dabei: GermanUtil.cut...() muss klüger gemacht werden, damit
        //  beim Ausschneiden nicht ein oder mehrere unnötiges Kommata im Mittelfeld
        //  zurückbleiden. Das sollte man ohnehin mal tun...

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
                praedikativum.getPraedikativ(personSubjekt, numerusSubjekt)
                // "glücklich", "ein Esel", "sich ihrer selbst gewiss"
                // "sehr glücklich, dich zu sehen"
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
        if (praedikativum instanceof Interrogativpronomen) {
            return praedikativum.getPraedikativ(
                    // Person und Numersu spielen beim Interrogativpronomen keine Rolle:
                    // "Sie ist interessiert, wer Peter ist",
                    // "Sie ist interessiert, wer du bist",
                    // "Sie ist interessiert, wer wir sind"
                    P2, SG).getString();
        }

        return null;
    }
}
