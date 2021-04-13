package de.nb.aventiure2.data.world.syscomp.wetter.bewoelkung;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.data.time.Tageszeit;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbWohinWoher;

import static de.nb.aventiure2.data.time.Tageszeit.TAGSUEBER;
import static de.nb.aventiure2.data.world.syscomp.wetter.bewoelkung.Bewoelkung.BEWOELKT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.TAG;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.IN_AKK;
import static de.nb.aventiure2.util.StreamUtil.*;

/**
 * Beschreibt die {@link Bewoelkung} als
 * {@link de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbWohinWoher}.
 */
public class BewoelkungAdvAngabeSkopusVerbWohinWoherDescriber {
    private final BewoelkungPraedikativumDescriber praedikativumDescriber;

    public BewoelkungAdvAngabeSkopusVerbWohinWoherDescriber(
            final BewoelkungPraedikativumDescriber praedikativumDescriber) {
        this.praedikativumDescriber = praedikativumDescriber;
    }

    public ImmutableCollection<AdvAngabeSkopusVerbWohinWoher> altWohinHinausUnterOffenenHimmel(
            final Bewoelkung bewoelkung,
            final AvTime time) {
        return altWohinHinausUnterOffenenHimmel(bewoelkung, time.getTageszeit());
    }

    private ImmutableCollection<AdvAngabeSkopusVerbWohinWoher> altWohinHinausUnterOffenenHimmel(
            final Bewoelkung bewoelkung,
            final Tageszeit tageszeit) {
        final ImmutableList.Builder<AdvAngabeSkopusVerbWohinWoher> alt =
                ImmutableList.builder();

        alt.addAll(mapToList(praedikativumDescriber.altLichtInDemEtwasLiegt(
                bewoelkung, tageszeit, true),
                licht -> new AdvAngabeSkopusVerbWohinWoher(IN_AKK.mit(licht))));

        // "in den grauen Morgen"
        alt.addAll(mapToSet(praedikativumDescriber.altTageszeitUnterOffenenHimmelDef(
                bewoelkung, tageszeit),
                s -> new AdvAngabeSkopusVerbWohinWoher(IN_AKK.mit(s))));

        if (bewoelkung == BEWOELKT && tageszeit == TAGSUEBER) {
            alt.add(new AdvAngabeSkopusVerbWohinWoher(IN_AKK.mit(TAG)));
        }

        return alt.build();
    }

}
