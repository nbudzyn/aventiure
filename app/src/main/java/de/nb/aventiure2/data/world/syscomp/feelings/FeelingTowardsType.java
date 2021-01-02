package de.nb.aventiure2.data.world.syscomp.feelings;

import com.google.common.collect.ImmutableList;

import java.util.stream.Stream;

import de.nb.aventiure2.german.adjektiv.AdjPhrOhneLeerstellen;
import de.nb.aventiure2.german.base.NumerusGenus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.satz.Satz;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static de.nb.aventiure2.german.praedikat.PraedikativumPraedikatOhneLeerstellen.praedikativumPraedikatMit;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.ANBLICKEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.ANSCHAUEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.ANSEHEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjPraedikativeAdjektivphrase.AUSSEHEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjPraedikativeAdjektivphrase.DREINSCHAUEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjPraedikativeAdjektivphrase.SCHAUEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjPraedikativeAdjektivphrase.SCHEINEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjPraedikativeAdjektivphrase.WIRKEN;

/**
 * Ein Spektrum von Gefühlen, dass ein {@link IFeelingBeingGO} gegenüber jemandem oder
 * einer Sache haben kann. Z.B. könnte das Gefühlsspektrum ZUNEIGUNG_ABNEIGUNG zwischen den
 * Extremen abgrundtiefen Hasses und brennender Liebe ausgeprägt sein.
 */
public enum FeelingTowardsType {
    ZUNEIGUNG_ABNEIGUNG(new ZuneigungAbneigungBeiBegegnungDescriber());
    // Weitere Gefühle könnten zb sein
    //  - VERTRAUEN_MISSTRAUEN
    //  - Dankbarkeit / Rachedurst

    private final FeelingBeiBegegnungDescriber feelingBeiBegegnungDescriber;

    FeelingTowardsType(final FeelingBeiBegegnungDescriber feelingBeiBegegnungDescriber) {
        this.feelingBeiBegegnungDescriber = feelingBeiBegegnungDescriber;
    }

    /**
     * Gibt alternative Sätze zurück, die die durch dieses Gefühl hervorgerufene
     * Reaktion dieses Feeling Beings auf das Target beschreiben, wenn die beiden sich begegnen.
     * Hier werden <i>keine Begrüßungen</i> beschrieben!
     * <p>
     * Die Methode garantiert, dass niemals etwas wie "du, der du..." oder
     * "du, die du..." oder "du, das du..." generiert wird.
     *
     * @return (gibt niemals eine leere Liste zurück !)
     */
    public ImmutableList<Satz> altReaktionBeiBegegnungSaetze(
            final SubstantivischePhrase gameObjectSubjekt,
            final SubstantivischePhrase targetDesc, final int feelingIntensity,
            final boolean targetKnown) {
        final ImmutableList.Builder<Satz> res = ImmutableList.builder();

        final ImmutableList<AdjPhrOhneLeerstellen> altEindruckAdjPhr =
                altEindruckBeiBegegnungAdjPhr(
                        gameObjectSubjekt.getPerson(), gameObjectSubjekt.getNumerusGenus(),
                        targetDesc, feelingIntensity, targetKnown
                );

        res.addAll(altEindruckBeiBegegnungSaetze(gameObjectSubjekt, targetDesc,
                feelingIntensity, targetKnown));

        res.addAll(altBeiBegegnungAnsehenSaetze(gameObjectSubjekt,
                targetDesc, feelingIntensity, targetKnown));

        res.addAll(altEindruckAdjPhr.stream()
                .flatMap(adjPhr ->
                        Stream.of("offenkundig", "sichtlich", "offenbar", "ganz offenbar")
                                .map(
                                        advAng ->
                                                praedikativumPraedikatMit(adjPhr)
                                                        .mitAdverbialerAngabe(
                                                                new AdverbialeAngabeSkopusSatz(
                                                                        advAng))
                                                        .alsSatzMitSubjekt(gameObjectSubjekt)
                                )
                ).collect(toImmutableList()));

        res.addAll(feelingBeiBegegnungDescriber.altReaktionBeiBegegnungSaetze(
                gameObjectSubjekt, targetDesc, feelingIntensity, targetKnown
        ));

        return res.build();
    }

    public ImmutableList<Satz> altEindruckBeiBegegnungSaetze(
            final SubstantivischePhrase gameObjectSubjekt,
            final SubstantivischePhrase targetDesc,
            final int feelingIntensity, final boolean targetKnown) {
        return altEindruckBeiBegegnungAdjPhr(
                gameObjectSubjekt.getPerson(), gameObjectSubjekt.getNumerusGenus(),
                targetDesc, feelingIntensity, targetKnown)
                .stream()
                .flatMap(ap -> Stream.of(AUSSEHEN, DREINSCHAUEN, SCHAUEN, SCHEINEN, WIRKEN)
                        .map(v -> v.mit(ap).alsSatzMitSubjekt(gameObjectSubjekt)))
                .collect(toImmutableList());
    }

    /**
     * Gibt eventuell alternative Sätze zurück, die  beschreiben, wie dieses Feeling Being
     * das Target ansieht, wenn die beiden sich begegnen.
     * <p>
     * Die Methode garantiert, dass niemals etwas wie "du, der du..." oder
     * "du, die du..." oder "du, das du..." generiert wird.
     *
     * @return Möglicherweise eine leere Liste (insbesondere bei extremen Gefühlen)!
     */
    public ImmutableList<Satz> altBeiBegegnungAnsehenSaetze(
            final SubstantivischePhrase gameObjectSubjekt,
            final SubstantivischePhrase targetDesc, final int feelingIntensity,
            final boolean targetKnown) {
        return altEindruckBeiBegegnungAdvAngaben(
                gameObjectSubjekt, targetDesc, feelingIntensity, targetKnown).stream()
                .flatMap(aa -> Stream.of(ANBLICKEN, ANSEHEN, ANSCHAUEN)
                        .map(v -> v.mit(gameObjectSubjekt)
                                .mitAdverbialerAngabe(aa)
                                .alsSatzMitSubjekt(gameObjectSubjekt)))
                .collect(toImmutableList());
    }

    /**
     * Gibt eventuell adverbiale Angaben zurück, die beschreiben, welchen Eindruck dieses
     * Feeling Being - basiert auf diesem {@link FeelingTowardsType} - auf das  Target
     * macht, wenn die beiden sich begegnen.
     * <p>
     * Die Methode garantiert, dass niemals etwas wie "du, der du..." oder
     * "du, die du..." oder "du, das du..." generiert wird.
     *
     * @return Möglicherweise eine leere Liste!
     */
    public ImmutableList<AdverbialeAngabeSkopusVerbAllg> altEindruckBeiBegegnungAdvAngaben(
            final SubstantivischePhrase gameObjectSubjekt, final SubstantivischePhrase targetDesc,
            final int feelingIntensity, final boolean targetKnown) {
        final ImmutableList<AdjPhrOhneLeerstellen> altEindruckAdjPhr =
                altEindruckBeiBegegnungAdjPhr(
                        gameObjectSubjekt.getPerson(), gameObjectSubjekt.getNumerusGenus(),
                        targetDesc, feelingIntensity, targetKnown
                );

        return altEindruckAdjPhr.stream()
                .filter(ap ->
                        !ap.getPraedikativAnteilKandidatFuerNachfeld(
                                gameObjectSubjekt.getPerson(),
                                gameObjectSubjekt.getNumerus())
                                .iterator().hasNext())
                // "Sie schaut dich überrascht an.", aber nicht
                // *"Sie schaut dich überrascht an, dich zu sehen".
                .map(ap -> ap.alsAdverbialeAngabe(
                        gameObjectSubjekt.getPerson(),
                        gameObjectSubjekt.getNumerus()))
                .collect(toImmutableList());
    }

    /**
     * Gibt eventuell alternative Adjektivphrasen zurück, die den Eindruck
     * beschreiben, den dieses Feeling Being auf das Target macht, wenn die beiden sich
     * begegnen. Die Phrasen können mit,
     * <i>wirken</i> oder <i>scheinen</i> verbunden werden.
     * <p>
     * Diese Sätze sind in
     * {@link #altReaktionBeiBegegnungSaetze(SubstantivischePhrase, SubstantivischePhrase, int, boolean)}
     * bereits enthalten.
     * <p>
     * Die Methode garantiert, dass niemals etwas wie "du, der du..." oder
     * "du, die du..." oder "du, das du..." generiert wird.
     *
     * @return Möglicherweise eine leere Liste (insbesondere bei extremen Gefühlen)!
     */
    public ImmutableList<AdjPhrOhneLeerstellen> altEindruckBeiBegegnungAdjPhr(
            final Person gameObjectSubjektPerson,
            final NumerusGenus gameObjectSubjektNumerusGenus,
            final SubstantivischePhrase targetDesc, final int feelingIntensity,
            final boolean targetKnown) {
        return feelingBeiBegegnungDescriber.altEindruckBeiBegegnungAdjPhr(
                gameObjectSubjektPerson, gameObjectSubjektNumerusGenus, targetDesc,
                feelingIntensity, targetKnown
        );
    }
}
