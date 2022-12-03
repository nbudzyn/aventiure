package de.nb.aventiure2.german.praedikat;


import static java.util.Objects.requireNonNull;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.nb.aventiure2.annotations.Komplement;
import de.nb.aventiure2.annotations.Valenz;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativSkopusSatz;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativVerbAllg;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativWohinWoher;
import de.nb.aventiure2.german.base.NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld;
import de.nb.aventiure2.german.base.Negationspartikelphrase;
import de.nb.aventiure2.german.base.PraedRegMerkmale;
import de.nb.aventiure2.german.description.ITextContext;

/**
 * Ein zu-haben-Prädikat, bei dem alle Leerstellen gefüllt sind
 * (z.B. <i>Spannendes zu berichten haben</i>,  <i>mit Paul zu diskutieren haben</i>,
 * <i>zu schlafen haben</i>, <i>sich zu waschen haben</i>).
 */
public class ZuHabenSemPraedikatOhneLeerstellen implements EinzelnesSemPraedikatOhneLeerstellen {
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

    @Override
    public boolean hauptsatzLaesstSichBeiGleichemSubjektMitNachfolgendemVerbzweitsatzZusammenziehen() {
        // Diese Einschränkung hängt wohl hauptsächlich vom Nachfeld ab, dass
        // auch in der zu-haben-Konstruktion das Nachfeld wird:
        // Du sagst: "Hallo!" -> Du hast zu sagen: "Hallo!"

        return lexikalischerKern
                .hauptsatzLaesstSichBeiGleichemSubjektMitNachfolgendemVerbzweitsatzZusammenziehen();
    }

    @Override
    public AbstractFinitesPraedikat getFinit(
            final ITextContext textContext,
            @Nullable final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld konnektor,
            final PraedRegMerkmale praedRegMerkmale) {
        final ImmutableList<ZuInfinitiv> lexKernZuInfinitiv =
                lexikalischerKern.getZuInfinitiv(textContext,
                        // Infinitiv steht nach dem Verb, nicht nach dem konnektor!
                        false, praedRegMerkmale);

        return new KomplexesFinitesPraedikat(
                requireNonNull(HabenUtil.VERB.getPraesensOhnePartikel(
                        praedRegMerkmale.getPerson(), praedRegMerkmale.getNumerus())), // "hast"
                null,
                lexKernZuInfinitiv
        ).mitKonnektor(konnektor);
    }

    @Override
    public ImmutableList<PartizipIIOderErsatzInfinitivPhrase> getPartizipIIOderErsatzInfinitivPhrasen(
            final ITextContext textContext, final boolean nachAnschlusswort,
            final PraedRegMerkmale praedRegMerkmale) {
        return ImmutableList.copyOf(
                getPartizipIIPhrasen(textContext, nachAnschlusswort, praedRegMerkmale));
    }

    @Override
    public ImmutableList<PartizipIIPhrase> getPartizipIIPhrasen(
            final ITextContext textContext,
            final boolean nachAnschlusswort, final PraedRegMerkmale praedRegMerkmale) {
        // Spannendes zu berichten gehabt
        // dich zu waschen gehabt
        // zu sagen gehabt: "Hallo!"
        // Spannendes zu berichten und viel zu erzählen gehabt

        final ImmutableList<ZuInfinitiv> zuInfinitivLexKern =
                lexikalischerKern.getZuInfinitiv(textContext, nachAnschlusswort, praedRegMerkmale);

        return ImmutableList.of(
                new KomplexePartizipIIPhrase(
                        HabenUtil.VERB.getPartizipII(), // "gehabt"
                        HabenUtil.VERB.getHilfsverbFuerPerfekt().getPerfektbildung(), // (haben)
                        zuInfinitivLexKern)); // "ein guter Mensch geworden"
    }

    @Override
    public boolean kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden() {
        // Auch diese Einschränkung hängt wohl hauptsächlich vom Nachfeld ab, dass
        // auch in der zu-haben-Konstruktion das Nachfeld wird:
        // gesagt: "Hallo!" -> zu sagen gehabt: "Hallo!"

        return lexikalischerKern.kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden();
    }

    @Override
    public ImmutableList<Infinitiv> getInfinitiv(
            final ITextContext textContext,
            final boolean nachAnschlusswort, final PraedRegMerkmale praedRegMerkmale) {
        return ImmutableList.of(
                new KomplexerInfinitiv(
                        HabenUtil.VERB.getInfinitiv(),
                        HabenUtil.VERB.getHilfsverbFuerPerfekt().getPerfektbildung(), // (haben)
                        lexikalischerKern
                                .getZuInfinitiv(textContext, nachAnschlusswort, praedRegMerkmale)
                        // "Spannendes zu berichten", ": Odysseus ist zurück."
                ));  // "zu haben"
    }

    @Override
    public ImmutableList<ZuInfinitiv> getZuInfinitiv(
            final ITextContext textContext,
            final boolean nachAnschlusswort, final PraedRegMerkmale praedRegMerkmale) {
        return ImmutableList.of(
                new KomplexerZuInfinitiv(
                        HabenUtil.VERB.getInfinitiv(),
                        lexikalischerKern
                                .getZuInfinitiv(textContext, nachAnschlusswort, praedRegMerkmale)
                        // "Spannendes zu berichten", ": Odysseus ist zurück."
                ));  // "zu haben"
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
