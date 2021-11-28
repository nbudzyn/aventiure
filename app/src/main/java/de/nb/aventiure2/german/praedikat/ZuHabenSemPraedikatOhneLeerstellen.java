package de.nb.aventiure2.german.praedikat;


import static java.util.Objects.requireNonNull;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.Objects;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.nb.aventiure2.annotations.Komplement;
import de.nb.aventiure2.annotations.Valenz;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativSkopusSatz;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativVerbAllg;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativWohinWoher;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.Negationspartikelphrase;
import de.nb.aventiure2.german.base.PraedRegMerkmale;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

/**
 * Ein zu-haben-Prädikat, bei dem alle Leerstellen gefüllt sind
 * (z.B. <i>Spannendes zu berichten haben</i>,  <i>mit Paul zu diskutieren haben</i>,
 * <i>zu schlafen haben</i>, <i>sich zu waschen haben</i>).
 */
public class ZuHabenSemPraedikatOhneLeerstellen implements SemPraedikatOhneLeerstellen {
    /**
     * Das Prädikat in seiner "ursprünglichen" Form (ohne "zu haben"). Die
     * "ursprüngliche Form" von <i>Spannendes zu berichten haben</i> ist z.B.
     * <i>Spannendes berichten</i>.
     */
    @Nonnull
    @Komplement
    private final SemPraedikatOhneLeerstellen lexikalischerKern;

    @Valenz
    ZuHabenSemPraedikatOhneLeerstellen(final SemPraedikatOhneLeerstellen lexikalischerKern) {
        this.lexikalischerKern = lexikalischerKern;
    }

    @Override
    public ZuHabenSemPraedikatOhneLeerstellen mitModalpartikeln(
            final Collection<Modalpartikel> modalpartikeln) {
        // Bei einem Prädikat wie "halt noch etwas zu berichten haben"
        // kann man nicht differenzieren zwischen *"halt berichten" und *"halt zu haben".
        // Deshalb speichern wir die Modalpartikeln im lexikalischen Kern und erlauben keine
        // zusätzlichen Angaben für "zu haben".
        return new ZuHabenSemPraedikatOhneLeerstellen(
                lexikalischerKern.mitModalpartikeln(modalpartikeln));
    }

    @Override
    public ZuHabenSemPraedikatOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabe) {
        // Bei einem Prädikat wie "Leider noch etwas zu berichten haben"
        // kann man nicht differenzieren zwischen *"leider berichten" und *"leider zu haben".
        // Deshalb speichern wir die Angaben im lexikalischen Kern und erlauben keine
        // zusätzlichen Angaben für "zu haben".
        return new ZuHabenSemPraedikatOhneLeerstellen(
                lexikalischerKern.mitAdvAngabe(advAngabe));
    }

    @Override
    public ZuHabenSemPraedikatOhneLeerstellen neg(
            @Nullable final Negationspartikelphrase negationspartikelphrase) {
        // Bei einem Prädikat wie "nicht das Auto zu waschen haben"
        // kann man nicht differenzieren zwischen *"nicht zu waschen" und *"nicht zu haben".
        // Deshalb speichern wir die Negationspartikelphrase im lexikalischen Kern und erlauben
        // keine
        // zusätzliche Negationspartikelphrase für "zu haben".
        return new ZuHabenSemPraedikatOhneLeerstellen(
                lexikalischerKern.neg(negationspartikelphrase));
    }

    @Override
    public ZuHabenSemPraedikatOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabe) {
        return new ZuHabenSemPraedikatOhneLeerstellen(
                lexikalischerKern.mitAdvAngabe(advAngabe));
    }

    @Override
    public ZuHabenSemPraedikatOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativWohinWoher advAngabe) {
        return new ZuHabenSemPraedikatOhneLeerstellen(
                lexikalischerKern.mitAdvAngabe(advAngabe));
    }

    @Nullable
    @Override
    @CheckReturnValue
    public Konstituentenfolge getErstesInterrogativwort() {
        return lexikalischerKern.getErstesInterrogativwort();
    }

    @Nullable
    @Override
    @CheckReturnValue
    public Konstituentenfolge getRelativpronomen() {
        return lexikalischerKern.getRelativpronomen();
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
    public Konstituentenfolge getVerbzweit(final PraedRegMerkmale praedRegMerkmale) {
        // hast Spannendes zu berichten
        // hast dich zu waschen
        // hast zu sagen: "Hallo!"

        return Konstituentenfolge.joinToKonstituentenfolge(
                requireNonNull(HabenUtil.VERB.getPraesensOhnePartikel(
                        praedRegMerkmale.getPerson(), praedRegMerkmale.getNumerus())), // "hast"
                lexikalischerKern.getZuInfinitiv(praedRegMerkmale)); // "dich zu waschen"
    }

    @Override
    public Konstituentenfolge getVerbzweitMitSubjektImMittelfeld(
            final SubstantivischePhrase subjekt) {
        // hast du Spannendes zu berichten
        // hast du dich zu waschen
        // hast du zu sagen: "Hallo!"

        return Konstituentenfolge.joinToKonstituentenfolge(
                requireNonNull(HabenUtil.VERB.getPraesensOhnePartikel(subjekt)), // "hast"
                subjekt.nomK(), // "du"
                lexikalischerKern.getZuInfinitiv(subjekt));
        // "dich zu waschen"
    }

    @Override
    public Konstituentenfolge getVerbletzt(final PraedRegMerkmale praedRegMerkmale) {
        // Spannendes zu berichten hast
        // dich zu waschen hast
        // zu sagen hast: "Hallo!"

        @Nullable final Konstituentenfolge nachfeld = getNachfeld(praedRegMerkmale);

        return Konstituentenfolge.joinToKonstituentenfolge(
                lexikalischerKern.getZuInfinitiv(praedRegMerkmale).cutLast(nachfeld),
                // "Spannendes zu berichten"
                requireNonNull(HabenUtil.VERB.getPraesensMitPartikel(
                        praedRegMerkmale.getPerson(), praedRegMerkmale.getNumerus())), // "hast"
                nachfeld); // : Odysseus ist zurück.
    }

    @Override
    public ImmutableList<PartizipIIPhrase> getPartizipIIPhrasen(
            final PraedRegMerkmale praedRegMerkmale) {
        // Spannendes zu berichten gehabt
        // dich zu waschen gehabt
        // zu sagen gehabt: "Hallo!"

        @Nullable final Konstituentenfolge nachfeld = getNachfeld(praedRegMerkmale);

        return ImmutableList.of(new PartizipIIPhrase(
                Konstituentenfolge.joinToKonstituentenfolge(
                        lexikalischerKern.getZuInfinitiv(praedRegMerkmale).cutLast(nachfeld),
                        // "Spannendes zu berichten"
                        HabenUtil.VERB.getPartizipII(), // "gehabt"
                        nachfeld), // : Odysseus ist zurück.
                Perfektbildung.HABEN)); // "Spannendes zu berichten gehabt haben"
    }

    @Override
    public boolean kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden() {
        // Auch diese Einschränkung hängt wohl hauptsächlich vom Nachfeld ab, dass
        // auch in der zu-haben-Konstruktion das Nachfeld wird:
        // gesagt: "Hallo!" -> zu sagen gehabt: "Hallo!"

        return lexikalischerKern.kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden();
    }

    @Override
    public Konstituentenfolge getInfinitiv(final PraedRegMerkmale praedRegMerkmale) {
        // Spannendes zu berichten haben
        // dich zu waschen haben
        // zu sagen haben: "Hallo!"

        @Nullable final Konstituentenfolge nachfeld = getNachfeld(praedRegMerkmale);

        return Konstituentenfolge.joinToKonstituentenfolge(
                lexikalischerKern.getZuInfinitiv(praedRegMerkmale).cutLast(nachfeld),
                // "Spannendes zu berichten"
                HabenUtil.VERB.getInfinitiv(), // haben
                nachfeld); // : Odysseus ist zurück.
    }

    @Override
    public Konstituentenfolge getZuInfinitiv(final PraedRegMerkmale praedRegMerkmale) {
        // Spannendes zu berichten zu haben
        // dich zu waschen zu haben
        // zu sagen zu haben: "Hallo!"
        @Nullable final Konstituentenfolge nachfeld = getNachfeld(praedRegMerkmale);

        return Konstituentenfolge.joinToKonstituentenfolge(
                lexikalischerKern.getZuInfinitiv(praedRegMerkmale).cutLast(nachfeld),
                // "Spannendes zu berichten"
                HabenUtil.VERB.getZuInfinitiv(), // zu haben
                nachfeld); // : Odysseus ist zurück.
    }

    @Override
    public boolean umfasstSatzglieder() {
        return lexikalischerKern.umfasstSatzglieder();
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
    public Konstituente getSpeziellesVorfeldSehrErwuenscht(final PraedRegMerkmale praedRegMerkmale,
                                                           final boolean nachAnschlusswort) {
        // "Danach hast du Spannendes zu berichten."
        return lexikalischerKern.getSpeziellesVorfeldSehrErwuenscht(praedRegMerkmale,
                nachAnschlusswort);
    }

    @Nullable
    @Override
    public Konstituentenfolge getSpeziellesVorfeldAlsWeitereOption(
            final PraedRegMerkmale praedRegMerkmale) {
        // "Spannendes hat er zu berichten."
        return lexikalischerKern.getSpeziellesVorfeldAlsWeitereOption(praedRegMerkmale);
    }

    @Nullable
    @Override
    public Konstituentenfolge getNachfeld(final PraedRegMerkmale praedRegMerkmale) {
        // (Es könnte verschiedene Nachfeld-Optionen geben (altNachfelder()) - oder besser
        // altAusklammerungen(), das jeweils Paare (Vorfeld, Nachfeld) liefert. Dabei
        // müsste allerdings die Natürlichkeit der erzeugten Sprache immer im Vordergrund stehen.)

        return lexikalischerKern.getNachfeld(praedRegMerkmale);
    }

    @Override
    public boolean inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich() {
        // "Mich hat zu frieren".
        return lexikalischerKern.inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ZuHabenSemPraedikatOhneLeerstellen that = (ZuHabenSemPraedikatOhneLeerstellen) o;
        return lexikalischerKern.equals(that.lexikalischerKern);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lexikalischerKern);
    }
}
