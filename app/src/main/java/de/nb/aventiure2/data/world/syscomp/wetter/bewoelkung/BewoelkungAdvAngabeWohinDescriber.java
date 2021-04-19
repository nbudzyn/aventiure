package de.nb.aventiure2.data.world.syscomp.wetter.bewoelkung;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.data.time.Tageszeit;
import de.nb.aventiure2.german.base.GermanUtil;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbWohinWoher;

import static de.nb.aventiure2.data.time.Tageszeit.NACHTS;
import static de.nb.aventiure2.data.world.syscomp.wetter.bewoelkung.Bewoelkung.LEICHT_BEWOELKT;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.IN_AKK;
import static de.nb.aventiure2.util.StreamUtil.*;
import static java.util.stream.Collectors.toSet;

/**
 * Beschreibt die {@link Bewoelkung} als
 * {@link de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbWohinWoher}.
 * <p>
 * Diese Phrasen sind für jede Temperatur sinnvoll (wobei manchmal die Temperatur
 * oder andere Wetteraspekte wichtiger sind und man dann diese Sätze
 * vielleicht gar nicht erzeugen wird).
 */
@SuppressWarnings({"DuplicateBranchesInSwitch", "MethodMayBeStatic"})
public class BewoelkungAdvAngabeWohinDescriber {
    private final BewoelkungPraedikativumDescriber praedikativumDescriber;

    private final BewoelkungPraepPhrDescriber praepPhrDescriber;

    public BewoelkungAdvAngabeWohinDescriber(
            final BewoelkungPraepPhrDescriber praepPhrDescriber,
            final BewoelkungPraedikativumDescriber praedikativumDescriber) {
        this.praepPhrDescriber = praepPhrDescriber;
        this.praedikativumDescriber = praedikativumDescriber;
    }

    public ImmutableCollection<AdvAngabeSkopusVerbWohinWoher> altHinausUnterOffenenHimmel(
            final Bewoelkung bewoelkung,
            final AvTime time,
            final boolean auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben) {
        final ImmutableSet.Builder<AdvAngabeSkopusVerbWohinWoher> alt = ImmutableSet.builder();

        alt.addAll(altHinausUnterOffenenHimmel(bewoelkung, time.getTageszeit()));

        if (time.kurzNachSonnenaufgang()
                && bewoelkung.compareTo(LEICHT_BEWOELKT) <= 0
                && auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben) {
            alt.add(new AdvAngabeSkopusVerbWohinWoher("in die ersten Sonnenstrahlen"),
                    new AdvAngabeSkopusVerbWohinWoher(
                            "in die ersten Strahlen der aufgehenden Sonne"));
        }

        return alt.build();
    }

    private ImmutableCollection<AdvAngabeSkopusVerbWohinWoher> altHinausUnterOffenenHimmel(
            final Bewoelkung bewoelkung,
            final Tageszeit tageszeit) {
        final ImmutableList.Builder<AdvAngabeSkopusVerbWohinWoher> alt =
                ImmutableList.builder();

        // "unter den nachtschwarzen Himmel"
        alt.addAll(mapToSet(praepPhrDescriber.altUnterOffenenHimmelAkk(bewoelkung, tageszeit),
                AdvAngabeSkopusVerbWohinWoher::new));

        // "in den grauen Morgen", "in den hellen Tag", "in die Morgensonne",
        // "in den Sonnenschein"
        alt.addAll(mapToSet(praepPhrDescriber.altInLichtTageszeit(bewoelkung, tageszeit),
                AdvAngabeSkopusVerbWohinWoher::new));

        if (bewoelkung.compareTo(LEICHT_BEWOELKT) <= 0) {
            if (tageszeit != NACHTS) {
                alt.addAll(tageszeit.altGestirn().stream()
                        .flatMap(gestirn ->
                                praepPhrDescriber.altUnterOffenenHimmelAkk(bewoelkung, tageszeit)
                                        .stream()
                                        .filter(unterHimmel -> !unterHimmel.getDescription()
                                                .kommaStehtAus())
                                        .map(unterHimmel ->
                                                // "in die Morgensonne unter den blauen Himmel"
                                                new AdvAngabeSkopusVerbWohinWoher(
                                                        GermanUtil.joinToString(
                                                                IN_AKK.mit(gestirn)
                                                                        .getDescription(),
                                                                unterHimmel.getDescription()))))
                        .collect(toSet()));
            }
        }

        switch (bewoelkung) {
            case WOLKENLOS:
                break;
            case LEICHT_BEWOELKT:
                break;
            case BEWOELKT:
                break;
            case BEDECKT:
                break;
            default:
                throw new IllegalStateException("Unexpected Bewoelkung: " + bewoelkung);
        }

        return alt.build();
    }

    /**
     * Gibt etwas wie "in den schönen Morgen" oder Ähnliches zurück.
     */
    @NonNull
    public AdvAngabeSkopusVerbWohinWoher schoeneTageszeit(final Tageszeit tageszeit) {
        // "in den schönen Abend"
        return new AdvAngabeSkopusVerbWohinWoher(praepPhrDescriber.inSchoeneTageszeit(tageszeit));
    }
}
