package de.nb.aventiure2.german.praedikat;


import static java.util.Objects.requireNonNull;

import androidx.annotation.NonNull;

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
 * Ein "semantisches Prädikat" mit einem Modalverb, bei dem alle Leerstellen gefüllt sind
 * (z.B. <i>schlafen müssen</i>,  <i>einen Apfel essen wollen</i>).
 */
public class SemPraedikatModalverbOhneLeerstellen implements EinzelnesSemPraedikatOhneLeerstellen {
    /**
     * Das Modalverb an sich, ohne Informationen zur Valenz, ohne Ergänzungen, ohne
     * Angaben
     */
    @NonNull
    private final Verb verb;

    /**
     * Das Prädikat in seiner "ursprünglichen" Form (ohne "können", "möchten", ...). Die
     * "ursprüngliche Form" von <i>laufen können</i> ist z.B.
     * <i>laufen</i>.
     */
    @Nonnull
    @Komplement
    private final SemPraedikatOhneLeerstellen lexikalischerKern;

    @Valenz
    SemPraedikatModalverbOhneLeerstellen(
            final Verb verb,
            final SemPraedikatOhneLeerstellen lexikalischerKern) {
        this.verb = verb;
        this.lexikalischerKern = lexikalischerKern;
    }

    @Override
    public SemPraedikatModalverbOhneLeerstellen mitModalpartikeln(
            final Collection<Modalpartikel> modalpartikeln) {
        // Der Einfachheit halber tun wir so, als würde sich das "halt"
        // bei "halt singen können" auf das Singen beziehen.
        // Das ist eine Vereinfachung. Jedenfalls ist ein doppeltes "halt" unmöglich.
        return new SemPraedikatModalverbOhneLeerstellen(
                verb, lexikalischerKern.mitModalpartikeln(modalpartikeln));
    }

    @Override
    public SemPraedikatModalverbOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabe) {
        // Der Einfachheit halber tun wir so, als würde sich das "leider"
        // bei "leider singen können" auf das Singen beziehen.
        // Das ist eine Vereinfachung. Jedenfalls ist ein doppeltes "leider" unmöglich.
        return new SemPraedikatModalverbOhneLeerstellen(
                verb, lexikalischerKern.mitAdvAngabe(advAngabe));
    }

    @Override
    public SemPraedikatModalverbOhneLeerstellen neg(
            @Nullable final Negationspartikelphrase negationspartikelphrase) {
        // Der Einfachheit halber tun wir so, als würde sich die Negationspartikel(phrase)
        // bei "nicht singen können" auf das Singen beziehen.
        // Das ist eine Vereinfachung. Jedenfalls wäre ein doppeltes "nicht" sehr ungebräuchlich.
        return new SemPraedikatModalverbOhneLeerstellen(
                verb, lexikalischerKern.neg(negationspartikelphrase));
    }

    @Override
    public SemPraedikatModalverbOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabe) {
        return new SemPraedikatModalverbOhneLeerstellen(
                verb, lexikalischerKern.mitAdvAngabe(advAngabe));
    }

    @Override
    public SemPraedikatModalverbOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativWohinWoher advAngabe) {
        return new SemPraedikatModalverbOhneLeerstellen(
                verb, lexikalischerKern.mitAdvAngabe(advAngabe));
    }

    @Override
    public boolean hauptsatzLaesstSichBeiGleichemSubjektMitNachfolgendemVerbzweitsatzZusammenziehen() {
        // Diese Einschränkung hängt wohl hauptsächlich vom Nachfeld ab, dass
        // auch mit Modalverb das Nachfeld wird:
        // Du sagst: "Hallo!" -> Du möchest sagen: "Hallo!"

        return lexikalischerKern
                .hauptsatzLaesstSichBeiGleichemSubjektMitNachfolgendemVerbzweitsatzZusammenziehen();
    }

    @Override
    public AbstractFinitesPraedikat getFinit(final ITextContext textContext,
                                             @Nullable
                                             final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld konnektor,
                                             final PraedRegMerkmale praedRegMerkmale) {
        // möchtest Spannendes berichten
        // kannst dich waschen
        // möchtest sagen: "Hallo!"

        return new KomplexesFinitesPraedikat(
                requireNonNull(verb.getPraesensOhnePartikel(
                        praedRegMerkmale.getPerson(), praedRegMerkmale.getNumerus())), // "möchtest"
                null,
                lexikalischerKern.getInfinitiv(textContext,
                        // Der Infinitiv steht nach dem Verb.
                        false, praedRegMerkmale).stream()
                        .map(i -> i.mitKonnektor(konnektor))
                        .collect(ImmutableList.toImmutableList()));
    }

    @Override
    public ImmutableList<PartizipIIOderErsatzInfinitivPhrase> getPartizipIIOderErsatzInfinitivPhrasen(
            final ITextContext textContext, final boolean nachAnschlusswort,
            final PraedRegMerkmale praedRegMerkmale) {
        // Spannendes berichten wollen (Ersatzinfinitiv! nicht *"Spannendes berichten gewollt"!)
        // dich waschen wollen
        // sagen wollen: "Hallo!"
        return ImmutableList.<PartizipIIOderErsatzInfinitivPhrase>builder()
                .addAll(getInfinitiv(textContext, nachAnschlusswort, praedRegMerkmale))
                .build();
    }

    @Override
    public ImmutableList<PartizipIIPhrase> getPartizipIIPhrasen(
            final ITextContext textContext,
            final boolean nachAnschlusswort, final PraedRegMerkmale praedRegMerkmale) {
        return ImmutableList.of(
                new KomplexePartizipIIPhrase(
                        verb.getPartizipII(), verb.getPerfektbildung(),
                        lexikalischerKern
                                .getInfinitiv(textContext, nachAnschlusswort, praedRegMerkmale)
                )
        );
    }

    @Override
    public boolean kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden() {
        // Auch diese Einschränkung hängt wohl hauptsächlich vom Nachfeld ab, dass
        // auch mit einem Modalverb das Nachfeld wird:
        // gesagt: "Hallo!" -> sagen gewollt: "Hallo!"

        return lexikalischerKern.kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden();
    }

    @Override
    public ImmutableList<Infinitiv> getInfinitiv(
            final ITextContext textContext,
            final boolean nachAnschlusswort, final PraedRegMerkmale praedRegMerkmale) {
        return ImmutableList.of(
                new KomplexerInfinitiv(
                        verb.getInfinitiv(), // "wollen"
                        verb.getPerfektbildung(),
                        lexikalischerKern
                                .getInfinitiv(textContext, nachAnschlusswort, praedRegMerkmale)
                        // "Spannendes berichten" / ": Odysseus ist zurück."
                ));
    }

    @Override
    public ImmutableList<ZuInfinitiv> getZuInfinitiv(
            final ITextContext textContext,
            final boolean nachAnschlusswort, final PraedRegMerkmale praedRegMerkmale) {
        return ImmutableList.of(
                new KomplexerZuInfinitiv(
                        verb.getInfinitiv(), // (zu) "wollen"
                        lexikalischerKern
                                .getInfinitiv(textContext, nachAnschlusswort, praedRegMerkmale)
                        // "Spannendes berichten" / ": Odysseus ist zurück."
                ));
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
        // Bei "Du musst für immer bei mir bleiben" oder "Du möchtest das Gefängnis verlassen"
        // ist nicht unbedingt ein Bezug auf den Nachzustand gegeben, weil es sich tendenziell nur
        // um ein "Handlungsziel" handelt, nicht unbedingt um eine Aktion.
        return false;
    }

    @Override
    public boolean inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich() {
        // "Mich muss frieren""
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
        final SemPraedikatModalverbOhneLeerstellen that = (SemPraedikatModalverbOhneLeerstellen) o;
        return verb.equals(that.verb) &&
                lexikalischerKern.equals(that.lexikalischerKern);
    }

    @Override
    public int hashCode() {
        return Objects.hash(verb, lexikalischerKern);
    }
}
