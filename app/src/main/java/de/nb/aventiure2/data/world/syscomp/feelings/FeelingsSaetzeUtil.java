package de.nb.aventiure2.data.world.syscomp.feelings;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.stream.Stream;

import de.nb.aventiure2.german.adjektiv.AdjPhrOhneLeerstellen;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.praedikat.VerbSubjObj;
import de.nb.aventiure2.german.satz.Satz;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static de.nb.aventiure2.german.praedikat.PraedikativumPraedikatOhneLeerstellen.praedikativumPraedikatMit;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.ANBLICKEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.ANGUCKEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.ANSCHAUEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.ANSEHEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.HINTERHERSCHAUEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.HINTERHERSEHEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.NACHBLICKEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.NACHSCHAUEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.NACHSEHEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.ZUGUCKEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.ZUSCHAUEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.ZUSEHEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjPraedikativeAdjektivphrase.AUSSEHEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjPraedikativeAdjektivphrase.DREINSCHAUEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjPraedikativeAdjektivphrase.GUCKEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjPraedikativeAdjektivphrase.SCHAUEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjPraedikativeAdjektivphrase.SCHEINEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjPraedikativeAdjektivphrase.WIRKEN;

/**
 * Statische Methode, die {@link de.nb.aventiure2.german.satz.Satz}-Objekte zu
 * Gefühlen erzeugen.
 */
public class FeelingsSaetzeUtil {
    private FeelingsSaetzeUtil() {
    }

    /**
     * Wandelt diese Adjektivphrasen - die Eindrücke beschreiben - in Sätze um.
     */
    static ImmutableList<Satz> toReaktionSaetze(
            final SubstantivischePhrase subjekt,
            final SubstantivischePhrase feelingTargetDesc,
            final ImmutableList<AdjPhrOhneLeerstellen> eindruckAdjPhr) {
        final ImmutableList<AdverbialeAngabeSkopusVerbAllg> adverbialeAngaben =
                AdjPhrOhneLeerstellen
                        .toAdvAngabenSkopusVerbAllg(subjekt, eindruckAdjPhr);

        return toReaktionSaetze(subjekt, feelingTargetDesc, eindruckAdjPhr,
                adverbialeAngaben);
    }

    /**
     * Wandelt diese Adjektivphrasen - die Eindrücke beschreiben - und diese adverbialen
     * Angaben in Sätze um.
     */
    static ImmutableList<Satz> toReaktionSaetze(
            final SubstantivischePhrase subjekt,
            final SubstantivischePhrase feelingTargetDesc,
            final ImmutableList<AdjPhrOhneLeerstellen> eindruckAdjPhr,
            final ImmutableList<AdverbialeAngabeSkopusVerbAllg> eindruckAdverbialeAngaben) {
        final ImmutableList.Builder<Satz> res = ImmutableList.builder();

        res.addAll(altAnsehenSaetze(
                subjekt, feelingTargetDesc, eindruckAdverbialeAngaben));

        res.addAll(altEindrueckSaetze(subjekt, eindruckAdjPhr));

        res.addAll(eindruckAdjPhr.stream()
                .flatMap(adjPhr ->
                        Stream.of("offenkundig", "sichtlich", "offenbar", "ganz offenbar")
                                .map(
                                        advAng ->
                                                praedikativumPraedikatMit(adjPhr)
                                                        .mitAdverbialerAngabe(
                                                                new AdverbialeAngabeSkopusSatz(
                                                                        advAng))
                                                        .alsSatzMitSubjekt(subjekt)
                                )
                ).collect(toImmutableList()));

        return res.build();
    }

    public static ImmutableList<Satz> altAnsehenSaetze(
            final SubstantivischePhrase subjekt,
            final SubstantivischePhrase angesehenDesc,
            final AdjPhrOhneLeerstellen adjektivPhrase) {
        return altAnsehenSaetze(subjekt, angesehenDesc,
                adjektivPhrase.alsAdverbialeAngabeSkopusVerbAllg());
    }

    public static ImmutableList<Satz> altAnsehenSaetze(
            final SubstantivischePhrase subjekt,
            final SubstantivischePhrase angesehenDesc,
            final AdverbialeAngabeSkopusVerbAllg adverbialeAngabe) {
        return altAnsehenSaetze(subjekt, angesehenDesc,
                ImmutableList.of(adverbialeAngabe));
    }

    public static ImmutableList<Satz> altAnsehenSaetze(
            final SubstantivischePhrase subjekt,
            final SubstantivischePhrase angesehenDesc,
            final ImmutableList<AdverbialeAngabeSkopusVerbAllg> adverbialeAngaben) {
        return Satz.altSubjObjSaetze(subjekt, ansehenVerben(), angesehenDesc,
                adverbialeAngaben);
    }

    public static ImmutableList<Satz> altNachsehenHinterhersehenSaetze(
            final SubstantivischePhrase subjekt,
            final SubstantivischePhrase objekt) {
        return Satz
                .altSubjObjSaetze(subjekt, nachsehenHinterhersehenVerben(), objekt);
    }

    public static ImmutableList<Satz> altZusehenSaetze(
            final SubstantivischePhrase subjekt,
            final SubstantivischePhrase objekt,
            final Collection<AdverbialeAngabeSkopusVerbAllg> adverbialeAngaben) {
        return Satz.altSubjObjSaetze(subjekt, zusehenVerben(), objekt,
                adverbialeAngaben);
    }

    @NonNull
    private static ImmutableList<VerbSubjObj> ansehenVerben() {
        return ImmutableList.of(ANBLICKEN, ANGUCKEN, ANSEHEN, ANSCHAUEN);
    }

    @NonNull
    private static ImmutableList<VerbSubjObj> nachsehenHinterhersehenVerben() {
        return ImmutableList
                .of(HINTERHERSEHEN, HINTERHERSCHAUEN, NACHBLICKEN, NACHSEHEN, NACHSCHAUEN);
    }

    @NonNull
    private static ImmutableList<VerbSubjObj> zusehenVerben() {
        return ImmutableList.of(ZUGUCKEN, ZUSEHEN, ZUSCHAUEN);
    }

    public static ImmutableList<Satz> altEindrueckSaetze(
            final SubstantivischePhrase subjekt,
            final AdjPhrOhneLeerstellen adjektivPhrase) {
        return altEindrueckSaetze(subjekt, ImmutableList.of(adjektivPhrase));
    }

    public static ImmutableList<Satz> altEindrueckSaetze(
            final SubstantivischePhrase subjekt,
            final ImmutableList<AdjPhrOhneLeerstellen> adjektivPhrasen) {
        return adjektivPhrasen.stream()
                .flatMap(ap -> Stream.of(AUSSEHEN, DREINSCHAUEN, GUCKEN, SCHAUEN, SCHEINEN, WIRKEN)
                        .map(v -> v.mit(ap).alsSatzMitSubjekt(subjekt)))
                .collect(toImmutableList());
    }
}
