package de.nb.aventiure2.german.praedikat;

import java.util.Collection;

import javax.annotation.Nullable;

import de.nb.aventiure2.annotations.Argument;
import de.nb.aventiure2.annotations.Valenz;
import de.nb.aventiure2.german.adjektiv.AdjPhrOhneLeerstellen;
import de.nb.aventiure2.german.base.GermanUtil;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.Wortfolge;

import static de.nb.aventiure2.german.base.GermanUtil.joinToNull;

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
    private final AdjPhrOhneLeerstellen adjektivphrase;

    @Valenz
    PraedikatMitPraedikativerAdjektivphraseOhneLeerstellen(
            final Verb verb,
            final AdjPhrOhneLeerstellen adjektivphrase) {
        this(verb, adjektivphrase,
                null, null,
                null);
    }

    private PraedikatMitPraedikativerAdjektivphraseOhneLeerstellen(
            final Verb verb,
            final AdjPhrOhneLeerstellen adjektivphrase,
            @Nullable final AdverbialeAngabeSkopusSatz adverbialeAngabeSkopusSatz,
            @Nullable final AdverbialeAngabeSkopusVerbAllg adverbialeAngabeSkopusVerbAllg,
            @Nullable
            final AdverbialeAngabeSkopusVerbWohinWoher adverbialeAngabeSkopusVerbWohinWoher) {
        super(verb, adverbialeAngabeSkopusSatz,
                adverbialeAngabeSkopusVerbAllg, adverbialeAngabeSkopusVerbWohinWoher);
        this.adjektivphrase = adjektivphrase;
    }

    @Override
    public PraedikatMitPraedikativerAdjektivphraseOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusSatz adverbialeAngabe) {
        if (adverbialeAngabe == null) {
            return this;
        }

        return new PraedikatMitPraedikativerAdjektivphraseOhneLeerstellen(
                getVerb(),
                adjektivphrase,
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
                adjektivphrase,
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
                adjektivphrase,
                getAdverbialeAngabeSkopusSatz(),
                getAdverbialeAngabeSkopusVerbAllg(),
                adverbialeAngabe
        );
    }

    @Override
    public boolean duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen() {
        return !adjektivphrase.getPraedikativ(Person.P2, Numerus.SG).kommmaStehtAus();
    }

    @Override
    public boolean kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden() {
        return !adjektivphrase.getPraedikativ(Person.P2, Numerus.SG).kommmaStehtAus();
    }

    @Override
    public @Nullable
    String getSpeziellesVorfeld() {
        @Nullable final String speziellesVorfeldFromSuper = super.getSpeziellesVorfeld();
        if (speziellesVorfeldFromSuper != null) {
            return speziellesVorfeldFromSuper;
        }

        // Ich gehe mal davon aus, dass die Komma-Problematik in jeder Person und jedem
        // Numerus gleich ist.
        if (!adjektivphrase.getPraedikativ(Person.P2, Numerus.SG).kommmaStehtAus()) {
            // "Glücklich wirkt sie"
            // Stark markiert - aber möglich.
            return adjektivphrase.getPraedikativ(Person.P2, Numerus.SG).getString();
        }

        return null;
    }

    @Override
    public Wortfolge getMittelfeld(final Collection<Modalpartikel> modalpartikeln,
                                   final Person personSubjekt,
                                   final Numerus numerusSubjekt) {
        return joinToNull(
                getAdverbialeAngabeSkopusSatz(), // "leider"
                GermanUtil.joinToNullString(modalpartikeln), // "halt"
                getAdverbialeAngabeSkopusVerbAllg(), // "erneut"
                getAdverbialeAngabeSkopusVerbWohinWoher(), // "nach außen" (?)
                adjektivphrase.getPraedikativ(personSubjekt, numerusSubjekt) // "glücklich"
        );
    }

    @Override
    public String getNachfeld(final Person personSubjekt,
                              final Numerus numerusSubjekt) {

        // FIXME Die Adjektivphrase könnte diskontinuierlich aufgeteilt werden, dann könnte
        //  ein Teil ins Nachfeld kommen:
        //  Sie hat GLÜCKLICH gewirkt, DICH ZU SEHEN.
        //  (alternativ zu "Sie hat GLÜCKLICH, DICH ZU SEHEN, gewirkt").
        //  Z.B.:         return adjektivphrase.getNachfeldKandidat(personSubjekt, numerusSubjekt)
        //  Problem dabei: GermanUtil.cut...() muss klüger gemacht werden, damit
        //  beim Ausschneiden nicht ein oder mehrere unnötiges Kommata im Mittelfeld
        //  zurückbleiden. Das sollte man ohnehin mal tun...

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
