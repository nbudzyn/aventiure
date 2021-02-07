package de.nb.aventiure2.german.praedikat;


import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.nb.aventiure2.annotations.Komplement;
import de.nb.aventiure2.annotations.Valenz;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativSkopusSatz;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativVerbAllg;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativWohinWoher;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

/**
 * Ein zu-haben-Prädikat, bei dem alle Leerstellen gefüllt sind
 * (z.B. <i>Spannendes zu berichten haben</i>,  <i>mit Paul zu diskutieren haben</i>,
 * <i>zu schlafen haben</i>, <i>sich zu waschen haben</i>).
 */
public class ZuHabenPraedikatOhneLeerstellen implements PraedikatOhneLeerstellen {
    /**
     * Das Prädikat in seiner "ursprünglichen" Form (ohne "zu haben"). Die
     * "ursprüngliche Form" von <i>Spannendes zu berichten haben</i> ist z.B.
     * <i>Spannendes berichten</i>.
     */
    @Nonnull
    @Komplement
    private final PraedikatOhneLeerstellen lexikalischerKern;

    @Valenz
    ZuHabenPraedikatOhneLeerstellen(
            final PraedikatOhneLeerstellen lexikalischerKern) {
        this.lexikalischerKern = lexikalischerKern;
    }

    @Override
    public PraedikatOhneLeerstellen mitModalpartikeln(
            final Collection<Modalpartikel> modalpartikeln) {
        // Bei einem Prädikat wie "Halt noch etwas zu berichten haben"
        // kann man nicht differenzieren zwischen *"halt berichten" und *"halt zu haben".
        // Deshalb speichern wir die Modalpartikeln im lexikalischen Kern und erlauben keine
        // zusätzlichen Angaben für "zu haben".
        return new ZuHabenPraedikatOhneLeerstellen(
                lexikalischerKern.mitModalpartikeln(modalpartikeln));
    }

    @Override
    public ZuHabenPraedikatOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz adverbialeAngabe) {
        // Bei einem Prädikat wie "Leider noch etwas zu berichten haben"
        // kann man nicht differenzieren zwischen *"leider berichten" und *"leider zu haben".
        // Deshalb speichern wir die Angaben im lexikalischen Kern und erlauben keine
        // zusätzlichen Angaben für "zu haben".
        return new ZuHabenPraedikatOhneLeerstellen(
                lexikalischerKern.mitAdverbialerAngabe(adverbialeAngabe));
    }

    @Override
    public ZuHabenPraedikatOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final IAdvAngabeOderInterrogativVerbAllg adverbialeAngabe) {
        return new ZuHabenPraedikatOhneLeerstellen(
                lexikalischerKern.mitAdverbialerAngabe(adverbialeAngabe));
    }

    @Override
    public ZuHabenPraedikatOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final IAdvAngabeOderInterrogativWohinWoher adverbialeAngabe) {
        return new ZuHabenPraedikatOhneLeerstellen(
                lexikalischerKern.mitAdverbialerAngabe(adverbialeAngabe));
    }

    @Nullable
    @Override
    public Konstituentenfolge getErstesInterrogativwort() {
        return lexikalischerKern.getErstesInterrogativwort();
    }

    @Override
    public boolean hauptsatzLaesstSichBeiGleichemSubjektMitNachfolgendemVerbzweitsatzZusammenziehen() {
        // Diese Einschränkung hängt wohl hauptsächlich vom Nachfeld ab, dass
        // auch in der zu-haben-Konstruktion das Nachfeld wird:
        // Du sagst: "Hallo!" -> Du hast zu sagen: "Hallo!"

        return lexikalischerKern
                .hauptsatzLaesstSichBeiGleichemSubjektMitNachfolgendemVerbzweitsatzZusammenziehen();
    }

    @Override
    public Konstituentenfolge getVerbzweit(final Person person, final Numerus numerus) {
        // hast Spannendes zu berichten
        // hast dich zu waschen
        // hast zu sagen: "Hallo!"

        return Konstituentenfolge.joinToKonstituentenfolge(
                HabenUtil.VERB.getPraesensOhnePartikel(person, numerus), // "hast"
                lexikalischerKern.getZuInfinitiv(person, numerus)); // "dich zu waschen"
    }

    @Override
    public Konstituentenfolge getVerbzweitMitSubjektImMittelfeld(
            final SubstantivischePhrase subjekt) {
        // hast du Spannendes zu berichten
        // hast du dich zu waschen
        // hast du zu sagen: "Hallo!"

        return Konstituentenfolge.joinToKonstituentenfolge(
                HabenUtil.VERB.getPraesensOhnePartikel(
                        subjekt.getPerson(), subjekt.getNumerus()), // "hast"
                subjekt.nomK(), // "du"
                lexikalischerKern.getZuInfinitiv(
                        subjekt.getPerson(), subjekt.getNumerus())); // "dich zu waschen"
    }

    @Override
    public Konstituentenfolge getVerbletzt(final Person person, final Numerus numerus) {
        // Spannendes zu berichten hast
        // dich zu waschen hast
        // zu sagen hast: "Hallo!"

        @Nullable final Konstituentenfolge nachfeld = getNachfeld(person, numerus);

        return Konstituentenfolge.joinToKonstituentenfolge(
                lexikalischerKern.getZuInfinitiv(person, numerus).cutLast(
                        // "Spannendes zu berichten"
                        nachfeld),
                HabenUtil.VERB.getPraesensMitPartikel(person, numerus), // "hast"
                nachfeld); // : Odysseus ist zurück.
    }

    @Override
    public Konstituentenfolge getPartizipIIPhrase(final Person person, final Numerus numerus) {
        // Spannendes zu berichten gehabt
        // dich zu waschen gehabt
        // zu sagen gehabt: "Hallo!"

        @Nullable final Konstituentenfolge nachfeld = getNachfeld(person, numerus);

        return Konstituentenfolge.joinToKonstituentenfolge(
                lexikalischerKern.getZuInfinitiv(person, numerus).cutLast(
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
    public Konstituentenfolge getInfinitiv(final Person person, final Numerus numerus) {
        // Spannendes zu berichten haben
        // dich zu waschen haben
        // zu sagen haben: "Hallo!"

        @Nullable final Konstituentenfolge nachfeld = getNachfeld(person, numerus);

        return Konstituentenfolge.joinToKonstituentenfolge(
                lexikalischerKern.getZuInfinitiv(person, numerus).cutLast(
                        // "Spannendes zu berichten"
                        nachfeld),
                HabenUtil.VERB.getInfinitiv(), // haben
                nachfeld); // : Odysseus ist zurück.
    }

    @Override
    public Konstituentenfolge getZuInfinitiv(final Person person, final Numerus numerus) {
        // Spannendes zu berichten zu haben
        // dich zu waschen zu haben
        // zu sagen zu haben: "Hallo!"
        @Nullable final Konstituentenfolge nachfeld = getNachfeld(person, numerus);

        return Konstituentenfolge.joinToKonstituentenfolge(
                lexikalischerKern.getZuInfinitiv(person, numerus).cutLast(
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

    @Nullable
    @Override
    public Konstituente getSpeziellesVorfeldSehrErwuenscht(final Person person,
                                                           final Numerus numerus) {
        // "Danach hast du Spannendes zu berichten."
        return lexikalischerKern.getSpeziellesVorfeldSehrErwuenscht(person, numerus);
    }

    @Nullable
    @Override
    public Konstituentenfolge getSpeziellesVorfeldAlsWeitereOption(final Person person,
                                                                   final Numerus numerus) {
        // "Spannendes hat er zu berichten."
        return lexikalischerKern.getSpeziellesVorfeldAlsWeitereOption(person, numerus);
    }

    @Nullable
    @Override
    public Konstituentenfolge getNachfeld(final Person person, final Numerus numerus) {
        // (Es könnte verschiedene Nachfeld-Optionen geben (altNachfelder()) - oder besser
        // altAusklammerungen(), das jeweils Paare (Vorfeld, Nachfeld) liefert. Dabei
        // müsste allerdings die Natürlichkeit der erzeugten Sprache immer im Vordergrund stehen.)

        return lexikalischerKern.getNachfeld(person, numerus);
    }
}
