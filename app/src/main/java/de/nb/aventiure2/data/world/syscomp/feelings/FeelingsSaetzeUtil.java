package de.nb.aventiure2.data.world.syscomp.feelings;

import com.google.common.collect.ImmutableList;

import java.util.stream.Stream;

import de.nb.aventiure2.german.adjektiv.AdjPhrOhneLeerstellen;
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
 * Statische Methode, die {@link de.nb.aventiure2.german.satz.Satz}-Objekte zu
 * Gefühlen erzeugen.
 */
class FeelingsSaetzeUtil {
    private FeelingsSaetzeUtil() {
    }

    /**
     * Wandelt diese Adjektivphrasen - die Eindrücke beschreiben - und diese adverbialen
     * Angaben in Sätze um.
     */
    static ImmutableList<Satz> toReaktionSaetze(
            final SubstantivischePhrase gameObjectSubjekt,
            final ImmutableList<AdjPhrOhneLeerstellen> eindruckAdjPhr,
            final ImmutableList<AdverbialeAngabeSkopusVerbAllg> eindruckAdverbialeAngaben) {
        final ImmutableList.Builder<Satz> res = ImmutableList.builder();

        res.addAll(toAnsehenSaetze(gameObjectSubjekt, eindruckAdverbialeAngaben));

        res.addAll(toEindrueckSaetze(gameObjectSubjekt, eindruckAdjPhr));

        res.addAll(eindruckAdjPhr.stream()
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

        return res.build();
    }

    static ImmutableList<Satz> toAnsehenSaetze(final SubstantivischePhrase gameObjectSubjekt,
                                               final ImmutableList<AdverbialeAngabeSkopusVerbAllg> adverbialeAngaben) {
        return adverbialeAngaben.stream()
                .flatMap(aa -> Stream.of(ANBLICKEN, ANSEHEN, ANSCHAUEN)
                        .map(v -> v.mit(gameObjectSubjekt)
                                .mitAdverbialerAngabe(aa)
                                .alsSatzMitSubjekt(gameObjectSubjekt)))
                .collect(toImmutableList());
    }

    static ImmutableList<Satz> toEindrueckSaetze(
            final SubstantivischePhrase gameObjectSubjekt,
            final ImmutableList<AdjPhrOhneLeerstellen> adjektivPhrasen) {
        return adjektivPhrasen.stream()
                .flatMap(ap -> Stream.of(AUSSEHEN, DREINSCHAUEN, SCHAUEN, SCHEINEN, WIRKEN)
                        .map(v -> v.mit(ap).alsSatzMitSubjekt(gameObjectSubjekt)))
                .collect(toImmutableList());
    }
}
