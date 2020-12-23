package de.nb.aventiure2.german.praedikat;


import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.nb.aventiure2.annotations.Argument;
import de.nb.aventiure2.annotations.Valenz;
import de.nb.aventiure2.german.base.GermanUtil;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.Wortfolge;

import static de.nb.aventiure2.german.base.GermanUtil.capitalize;
import static de.nb.aventiure2.german.base.GermanUtil.joinToNull;
import static de.nb.aventiure2.german.base.GermanUtil.joinToNullString;
import static de.nb.aventiure2.german.base.Numerus.SG;
import static de.nb.aventiure2.german.base.Person.P2;

/**
 * Ein zu-haben-Prädikat, bei dem alle Leerstellen gefüllt sind
 * (z.B. <i>Spannendes zu berichten haben</i>,  <i>mit Paul zu diskutieren haben/i>,
 * <i>zu schlafen haben</i>, <i>sich zu waschen haben</i>).
 */
public class ZuHabenPraedikatOhneLeerstellen implements PraedikatOhneLeerstellen {
    /**
     * Das Prädikat in seiner "ursprünglichen" Form (ohne "zu haben"). Die
     * "ursprüngliche Form" von <i>Spannendes zu berichten haben</i> ist z.B.
     * <i>Spannendes berichten</i>.
     */
    @Nonnull
    @Argument
    private final PraedikatOhneLeerstellen lexikalischerKern;

    @Valenz
    ZuHabenPraedikatOhneLeerstellen(
            final PraedikatOhneLeerstellen lexikalischerKern) {
        this.lexikalischerKern = lexikalischerKern;
    }

    @Override
    public ZuHabenPraedikatOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusSatz adverbialeAngabe) {
        // Bei einem Prädikat wie "Leider noch etwas zu berichten haben"
        // kann man nicht differenzieren zwischen *"leider berichten" und *"leider zu haben".
        // Deshalb speichern wir die Angaben im lexikalischen Kern und erlauben keine
        // zusätzlichen Angaben für "zu haben".
        return new ZuHabenPraedikatOhneLeerstellen(
                lexikalischerKern.mitAdverbialerAngabe(adverbialeAngabe));
    }

    @Override
    public ZuHabenPraedikatOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusVerbAllg adverbialeAngabe) {
        return new ZuHabenPraedikatOhneLeerstellen(
                lexikalischerKern.mitAdverbialerAngabe(adverbialeAngabe));
    }

    @Override
    public ZuHabenPraedikatOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusVerbWohinWoher adverbialeAngabe) {
        return new ZuHabenPraedikatOhneLeerstellen(
                lexikalischerKern.mitAdverbialerAngabe(adverbialeAngabe));
    }

    @Nullable
    @Override
    public String getErstesInterrogativpronomenAlsString() {
        return lexikalischerKern.getErstesInterrogativpronomenAlsString();
    }

    @Override
    public Wortfolge getDuSatzanschlussOhneSubjekt(final Collection<Modalpartikel> modalpartikeln) {
        // hast Spannendes zu berichten
        // hast dich zu waschen
        // hast zu sagen: "Hallo!"
        return joinToNull(
                HabenUtil.VERB.getDuFormOhnePartikel(),
                // FIXME eigentlich sollte es lexikalischerKern.mitModalpartikeln()
                //  oder so ähnlich heißen (oder als adverbiale Angaben?).
                //  Derzeit gehen hier die modalpartikeln einfach verloren.
                lexikalischerKern.getZuInfinitiv(P2, SG));
    }

    @Override
    public boolean duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen() {
        // Diese Einschränkung hängt wohl hauptsächlich vom Nachfeld ab, dass
        // auch in der zu-haben-Konstruktion das Nachfeld wird:
        // Du sagst: "Hallo!" -> Du hast zu sagen: "Hallo!"

        return lexikalischerKern.duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen();
    }

    @Override
    public Wortfolge getVerbzweit(final Person person, final Numerus numerus) {
        // hast Spannendes zu berichten
        // hast dich zu waschen
        // hast zu sagen: "Hallo!"

        return joinToNull(
                HabenUtil.VERB.getPraesensOhnePartikel(person, numerus), // "hast"
                lexikalischerKern.getZuInfinitiv(person, numerus)); // "dich zu waschen"
    }

    @Override
    public Wortfolge getVerbletzt(final Person person, final Numerus numerus) {
        // Spannendes zu berichten hast
        // dich zu waschen hast
        // zu sagen hast: "Hallo!"

        @Nullable final String nachfeld = getNachfeld(person, numerus);

        return joinToNull(
                GermanUtil.cutSatzgliedVonHinten(
                        lexikalischerKern.getZuInfinitiv(person, numerus),
                        // "Spannendes zu berichten"
                        nachfeld),
                HabenUtil.VERB.getPraesensMitPartikel(person, numerus), // "hast"
                nachfeld); // : Odysseus ist zurück.
    }

    @Override
    public String getPartizipIIPhrase(final Person person, final Numerus numerus) {
        // Spannendes zu berichten gehabt
        // dich zu waschen gehabt
        // zu sagen gehabt: "Hallo!"

        @Nullable final String nachfeld = getNachfeld(person, numerus);

        return joinToNullString(
                GermanUtil.cutSatzgliedVonHinten(
                        lexikalischerKern.getZuInfinitiv(person, numerus),
                        // "Spannendes zu berichten"
                        nachfeld),
                HabenUtil.VERB.getPartizipII(), // "gehabt"
                nachfeld); // : Odysseus ist zurück.
    }

    @Override
    public boolean kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden() {
        // Auch diese Einschränkung hängt wohl hauptsächlich vom Nachfeld ab, dass
        // auch in der zu-haben-Konstruktion das Nachfeld wird:
        // gesagt: "Hallo!" -> zu sagen gehabt: "Hallo!"

        return lexikalischerKern.kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden();
    }

    @Override
    public String getInfinitiv(final Person person, final Numerus numerus) {
        // Spannendes zu berichten haben
        // dich zu waschen haben
        // zu sagen haben: "Hallo!"

        @Nullable final String nachfeld = getNachfeld(person, numerus);

        return joinToNullString(
                GermanUtil.cutSatzgliedVonHinten(
                        lexikalischerKern.getZuInfinitiv(person, numerus),
                        // "Spannendes zu berichten"
                        nachfeld),
                HabenUtil.VERB.getInfinitiv(), // haben
                nachfeld); // : Odysseus ist zurück.
    }

    @Override
    public String getZuInfinitiv(final Person person, final Numerus numerus) {
        // Spannendes zu berichten zu haben
        // dich zu waschen zu haben
        // zu sagen zu haben: "Hallo!"
        @Nullable final String nachfeld = getNachfeld(person, numerus);

        return joinToNullString(
                GermanUtil.cutSatzgliedVonHinten(
                        lexikalischerKern.getZuInfinitiv(person, numerus),
                        // "Spannendes zu berichten"
                        nachfeld),
                HabenUtil.VERB.getZuInfinitiv(), // zu haben
                nachfeld); // : Odysseus ist zurück.
    }

    @Override
    public boolean umfasstSatzglieder() {
        return lexikalischerKern.umfasstSatzglieder();
    }

    @Override
    public boolean bildetPerfektMitSein() {
        return HabenUtil.VERB.getPerfektbildung() == Perfektbildung.SEIN;
    }

    @Override
    public boolean hatAkkusativobjekt() {
        return lexikalischerKern.hatAkkusativobjekt();
    }

    @Override
    public boolean isBezugAufNachzustandDesAktantenGegeben() {
        // Selbst bei "Du hast wegzugehen" oder "Du hast den Wald zu verlassen"
        // ist kein Bezug auf den Nachzustand gegeben, weil es sich tendenziell nur um
        // eine normative Forderung handelt, nicht um eine Aktion.
        return false;
    }

    @Override
    public Wortfolge getDuHauptsatzMitVorfeld(final String vorfeld) {
        // Dann hast du Spannendes zu berichten
        // Dann hast du dich zu waschen
        // Dann hast du zu sagen: "Hallo!"
        return joinToNull(
                capitalize(vorfeld), // "dann"
                HabenUtil.VERB.getDuFormOhnePartikel(), // "hast"
                "du",
                lexikalischerKern.getZuInfinitiv(P2, SG)); // "dich zu waschen"
    }

    @Override
    public Wortfolge getDuHauptsatzMitSpeziellemVorfeld() {
        @Nullable final String speziellesVorfeld = getSpeziellesVorfeld();
        if (speziellesVorfeld == null) {
            return getDuHauptsatz();
        }

        return capitalize(
                joinToNull(
                        speziellesVorfeld, // "Spannendes"
                        HabenUtil.VERB.getDuFormOhnePartikel(), // "hast"
                        "du",
                        GermanUtil.cutSatzglied(
                                lexikalischerKern.getZuInfinitiv(P2, SG),
                                speziellesVorfeld))); // "dem König zu berichten deswegen"
    }

    @Nullable
    @Override
    public String getSpeziellesVorfeld() {
        // "Spannendes hat er zu berichten."
        return lexikalischerKern.getSpeziellesVorfeld();
    }

    @Nullable
    @Override
    public String getNachfeld(final Person person, final Numerus numerus) {
        return lexikalischerKern.getNachfeld(person, numerus);
    }
}
