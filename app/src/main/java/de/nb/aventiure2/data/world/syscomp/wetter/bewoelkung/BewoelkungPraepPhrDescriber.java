package de.nb.aventiure2.data.world.syscomp.wetter.bewoelkung;

import com.google.common.collect.ImmutableSet;

import de.nb.aventiure2.data.time.Tageszeit;
import de.nb.aventiure2.german.base.Praepositionalphrase;

import static de.nb.aventiure2.data.time.Tageszeit.NACHTS;
import static de.nb.aventiure2.data.time.Tageszeit.TAGSUEBER;
import static de.nb.aventiure2.data.world.syscomp.wetter.bewoelkung.Bewoelkung.LEICHT_BEWOELKT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.LICHT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.MONDSCHIMMER;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.TAGESLICHT;
import static de.nb.aventiure2.german.base.Nominalphrase.npArtikellos;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.BEI_DAT;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.IN_DAT;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.UNTER_DAT;
import static de.nb.aventiure2.util.StreamUtil.*;

/**
 * Beschreibt die {@link Bewoelkung} als {@link Praepositionalphrase}.
 * <p>
 * Diese Phrasen sind für jede Temperatur sinnvoll (wobei manchmal die Temperatur
 * oder andere Wetteraspekte wichtiger sind und man dann diese Sätze
 * vielleicht gar nicht erzeugen wird).
 */
@SuppressWarnings({"DuplicateBranchesInSwitch", "MethodMayBeStatic"})
public class BewoelkungPraepPhrDescriber {
    private final BewoelkungPraedikativumDescriber praedikativumDescriber;

    public BewoelkungPraepPhrDescriber(
            final BewoelkungPraedikativumDescriber praedikativumDescriber) {
        this.praedikativumDescriber = praedikativumDescriber;
    }

    public ImmutableSet<Praepositionalphrase> altBeiLichtImLicht(final Bewoelkung bewoelkung,
                                                                 final Tageszeit tageszeit,
                                                                 final boolean unterOffenemHimmel) {
        final ImmutableSet.Builder<Praepositionalphrase> alt = ImmutableSet.builder();

        if (tageszeit != NACHTS) {
            alt.add(BEI_DAT.mit(npArtikellos(LICHT)));
        }

        if (tageszeit == TAGSUEBER) {
            alt.add(BEI_DAT.mit(npArtikellos(TAGESLICHT)));
        }

        if (bewoelkung == LEICHT_BEWOELKT && tageszeit == NACHTS) {
            alt.add(BEI_DAT.mit(MONDSCHIMMER)); // "beim Mondschimmer"
        }

        alt.addAll(altImLicht(bewoelkung, tageszeit, unterOffenemHimmel));

        return alt.build();
    }

    private ImmutableSet<Praepositionalphrase> altImLicht(
            final Bewoelkung bewoelkung,
            final Tageszeit tageszeit,
            final boolean unterOffenemHimmel) {
        final ImmutableSet.Builder<Praepositionalphrase> alt = ImmutableSet.builder();
        alt.addAll(mapToSet(
                praedikativumDescriber
                        .altLichtInDemEtwasLiegt(bewoelkung, tageszeit, unterOffenemHimmel),
                IN_DAT::mit));

        // IDEA "der Hügel liegt in einsamem Mondschein." (einsam bezieht sich auf den Hügel
        //  oder den SC, nicht auf den Mondschein)

        return alt.build();
    }

    public ImmutableSet<Praepositionalphrase> altUnterOffenemHimmel(
            final Bewoelkung bewoelkung, final Tageszeit tageszeit) {
        final ImmutableSet.Builder<Praepositionalphrase> alt = ImmutableSet.builder();

        alt.addAll(mapToSet(praedikativumDescriber.altOffenerHimmel(bewoelkung, tageszeit),
                UNTER_DAT::mit));
        alt.addAll(mapToSet(
                praedikativumDescriber.altLichtInDemEtwasLiegt(bewoelkung, tageszeit, true),
                IN_DAT::mit));

        if (bewoelkung == LEICHT_BEWOELKT) {
            alt.addAll(mapToList(tageszeit.altGestirnschein(), IN_DAT::mit));
        }

        return alt.build();
    }
}
