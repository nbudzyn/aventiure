package de.nb.aventiure2.german.praedikat;


import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.nb.aventiure2.annotations.Komplement;
import de.nb.aventiure2.annotations.Valenz;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;

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
    @Komplement
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
    public Konstituente getErstesInterrogativpronomen() {
        return lexikalischerKern.getErstesInterrogativpronomen();
    }

    @Override
    public Iterable<Konstituente> getDuSatzanschlussOhneSubjekt(
            final Collection<Modalpartikel> modalpartikeln) {
        // hast Spannendes zu berichten
        // hast dich zu waschen
        // hast zu sagen: "Hallo!"
        return Konstituente.joinToKonstituenten(
                HabenUtil.VERB.getDuFormOhnePartikel(),
                // FIXME eigentlich sollte es lexikalischerKern.mitModalpartikeln()
                //  oder so ähnlich heißen (oder als adverbiale Angaben?).
                //  Derzeit gehen hier die modalpartikeln einfach verloren.
                //  Modalpartikeln sollten besser zu einem
                //  neuen "AbstractPraedikat" führen, dass man dann auch speichern
                //  und weiterreichen kann!
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
    public Iterable<Konstituente> getVerbzweit(final Person person, final Numerus numerus) {
        // hast Spannendes zu berichten
        // hast dich zu waschen
        // hast zu sagen: "Hallo!"

        return Konstituente.joinToKonstituenten(
                HabenUtil.VERB.getPraesensOhnePartikel(person, numerus), // "hast"
                lexikalischerKern.getZuInfinitiv(person, numerus)); // "dich zu waschen"
    }

    @Override
    public Iterable<Konstituente> getVerbletzt(final Person person, final Numerus numerus) {
        // Spannendes zu berichten hast
        // dich zu waschen hast
        // zu sagen hast: "Hallo!"

        @Nullable final Iterable<Konstituente> nachfeld = getNachfeld(person, numerus);

        return Konstituente.joinToKonstituenten(
                Konstituente.cutLast(
                        lexikalischerKern.getZuInfinitiv(person, numerus),
                        // "Spannendes zu berichten"
                        nachfeld),
                HabenUtil.VERB.getPraesensMitPartikel(person, numerus), // "hast"
                nachfeld); // : Odysseus ist zurück.
    }

    @Override
    public Iterable<Konstituente> getPartizipIIPhrase(final Person person, final Numerus numerus) {
        // Spannendes zu berichten gehabt
        // dich zu waschen gehabt
        // zu sagen gehabt: "Hallo!"

        @Nullable final Iterable<Konstituente> nachfeld = getNachfeld(person, numerus);

        return Konstituente.joinToKonstituenten(
                Konstituente.cutLast(
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
    public Iterable<Konstituente> getInfinitiv(final Person person, final Numerus numerus) {
        // Spannendes zu berichten haben
        // dich zu waschen haben
        // zu sagen haben: "Hallo!"

        @Nullable final Iterable<Konstituente> nachfeld = getNachfeld(person, numerus);

        return Konstituente.joinToKonstituenten(
                Konstituente.cutLast(
                        lexikalischerKern.getZuInfinitiv(person, numerus),
                        // "Spannendes zu berichten"
                        nachfeld),
                HabenUtil.VERB.getInfinitiv(), // haben
                nachfeld); // : Odysseus ist zurück.
    }

    @Override
    public Iterable<Konstituente> getZuInfinitiv(final Person person, final Numerus numerus) {
        // Spannendes zu berichten zu haben
        // dich zu waschen zu haben
        // zu sagen zu haben: "Hallo!"
        @Nullable final Iterable<Konstituente> nachfeld = getNachfeld(person, numerus);

        return Konstituente.joinToKonstituenten(
                Konstituente.cutLast(
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
    public Iterable<Konstituente> getDuHauptsatzMitVorfeld(final String vorfeld) {
        // Dann hast du Spannendes zu berichten
        // Dann hast du dich zu waschen
        // Dann hast du zu sagen: "Hallo!"
        return Konstituente.capitalize(
                Konstituente.joinToKonstituenten(
                        vorfeld, // "dann"
                        HabenUtil.VERB.getDuFormOhnePartikel(), // "hast"
                        "du",
                        lexikalischerKern.getZuInfinitiv(P2, SG))); // "dich zu waschen"
    }

    @Override
    public Iterable<Konstituente> getDuHauptsatzMitSpeziellemVorfeld() {
        @Nullable final Konstituente speziellesVorfeld = getSpeziellesVorfeld(P2, SG);

        if (speziellesVorfeld == null) {
            return getDuHauptsatz();
        }

        return Konstituente.capitalize(
                Konstituente.joinToKonstituenten(
                        speziellesVorfeld, // "Spannendes"
                        HabenUtil.VERB.getDuFormOhnePartikel(), // "hast"
                        "du",
                        Konstituente.cutFirst(
                                lexikalischerKern.getZuInfinitiv(P2, SG),
                                speziellesVorfeld))); // "dem König zu berichten deswegen"
    }

    @Nullable
    @Override
    public Konstituente getSpeziellesVorfeld(final Person person,
                                             final Numerus numerus) {
        // "Spannendes hat er zu berichten."
        return lexikalischerKern.getSpeziellesVorfeld(person, numerus);
    }

    @Nullable
    @Override
    public Iterable<Konstituente> getNachfeld(final Person person, final Numerus numerus) {
        // (Es könnte verschiedene Nachfeld-Optionen geben (altNachfelder()) - oder besser
        // altAusklammerungen(), das jeweils Paare (Vorfeld, Nachfeld) liefert. Dabei
        // müsste allerdings die Natürlichkeit der erzeugten Sprache immer im Vordergrund stehen.)

        return lexikalischerKern.getNachfeld(person, numerus);
    }
}
