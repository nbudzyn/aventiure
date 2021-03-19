package de.nb.aventiure2.data.world.syscomp.feelings;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import de.nb.aventiure2.german.adjektiv.AdjPhrOhneLeerstellen;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.praedikat.VerbSubjObj;
import de.nb.aventiure2.german.praedikat.VerbSubjPraedikativeAdjektivphrase;
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
            final boolean subjektUndFeelingTargetKoennenEinanderSehen,
            final ImmutableList<AdjPhrOhneLeerstellen> eindruckAdjPhr) {
        final ImmutableList<AdvAngabeSkopusVerbAllg> advAngaben =
                AdjPhrOhneLeerstellen
                        .toAdvAngabenSkopusVerbAllg(subjekt, eindruckAdjPhr);

        return toReaktionSaetze(subjekt, feelingTargetDesc,
                subjektUndFeelingTargetKoennenEinanderSehen, eindruckAdjPhr,
                advAngaben);
    }

    /**
     * Wandelt diese Adjektivphrasen - die Eindrücke beschreiben - und diese adverbialen
     * Angaben in Sätze um.
     */
    static ImmutableList<Satz> toReaktionSaetze(
            final SubstantivischePhrase subjekt,
            final SubstantivischePhrase feelingTargetDesc,
            final boolean subjektUndFeelingTargetKoennenEinanderSehen,
            final ImmutableList<AdjPhrOhneLeerstellen> eindruckAdjPhr,
            final ImmutableList<AdvAngabeSkopusVerbAllg> eindruckAdvAngaben) {
        final ImmutableList.Builder<Satz> res = ImmutableList.builder();

        if (subjektUndFeelingTargetKoennenEinanderSehen) {
            res.addAll(altAnsehenSaetze(
                    subjekt, feelingTargetDesc, eindruckAdvAngaben));
        }

        res.addAll(altEindrueckSaetze(subjekt, subjektUndFeelingTargetKoennenEinanderSehen,
                eindruckAdjPhr));

        res.addAll(eindruckAdjPhr.stream()
                .flatMap(adjPhr ->
                        altEindruckAdverbien(subjektUndFeelingTargetKoennenEinanderSehen).stream()
                                .map(
                                        advAng ->
                                                praedikativumPraedikatMit(adjPhr)
                                                        .mitAdvAngabe(
                                                                new AdvAngabeSkopusSatz(
                                                                        advAng))
                                                        .alsSatzMitSubjekt(subjekt)
                                )
                ).collect(toImmutableList()));

        return res.build();
    }

    private static ImmutableList<String> altEindruckAdverbien(final boolean istZuSehen) {
        final ImmutableList.Builder<String> res = ImmutableList.builder();

        if (istZuSehen) {
            res.add("sichtlich");
        }

        res.add("offenkundig", "offenbar", "ganz offenbar");

        return res.build();
    }

    public static ImmutableList<Satz> altAnsehenSaetze(
            final SubstantivischePhrase subjekt,
            final SubstantivischePhrase angesehenDesc,
            final AdjPhrOhneLeerstellen adjektivPhrase) {
        return altAnsehenSaetze(subjekt, angesehenDesc,
                adjektivPhrase.alsAdvAngabeSkopusVerbAllg());
    }

    public static ImmutableList<Satz> altAnsehenSaetze(
            final SubstantivischePhrase subjekt,
            final SubstantivischePhrase angesehenDesc,
            final AdvAngabeSkopusVerbAllg advAngabe) {

        return altAnsehenSaetze(subjekt, angesehenDesc,
                ImmutableList.of(advAngabe));
    }

    public static ImmutableList<Satz> altAnsehenSaetze(
            final SubstantivischePhrase subjekt,
            final SubstantivischePhrase angesehenDesc,
            final ImmutableList<AdvAngabeSkopusVerbAllg> advAngaben) {
        return Satz.altSubjObjSaetze(subjekt, ansehenVerben(), angesehenDesc,
                advAngaben);
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
            final Collection<AdvAngabeSkopusVerbAllg> advAngaben) {
        return Satz.altSubjObjSaetze(subjekt, zusehenVerben(), objekt,
                advAngaben);
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
            final boolean subjektIstZuSehen,
            final AdjPhrOhneLeerstellen adjektivPhrase) {
        return altEindrueckSaetze(subjekt, subjektIstZuSehen, ImmutableList.of(adjektivPhrase));
    }

    static ImmutableList<Satz> altEindrueckSaetze(
            final SubstantivischePhrase subjekt,
            final boolean subjektIstZuSehen,
            final ImmutableList<AdjPhrOhneLeerstellen> adjektivPhrasen) {
        return adjektivPhrasen.stream()
                .flatMap(ap -> altEindrueckPraedikate(subjektIstZuSehen).stream()
                        .map(v -> v.mit(ap).alsSatzMitSubjekt(subjekt)))
                .collect(toImmutableList());
    }

    private static ImmutableList<VerbSubjPraedikativeAdjektivphrase> altEindrueckPraedikate(
            final boolean subjektIstZuSehen) {
        final ImmutableList.Builder<VerbSubjPraedikativeAdjektivphrase> res =
                ImmutableList.builder();

        if (subjektIstZuSehen) {
            res.add(AUSSEHEN, DREINSCHAUEN, GUCKEN, SCHAUEN);
        }

        res.add(SCHEINEN, WIRKEN);

        return res.build();
    }
}
