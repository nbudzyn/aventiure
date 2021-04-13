package de.nb.aventiure2.data.world.syscomp.wetter.bewoelkung;

import com.google.common.collect.ImmutableSet;

import de.nb.aventiure2.data.time.Tageszeit;
import de.nb.aventiure2.german.base.Praepositionalphrase;

import static de.nb.aventiure2.data.world.syscomp.wetter.bewoelkung.Bewoelkung.LEICHT_BEWOELKT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.LICHT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.TAGESLICHT;
import static de.nb.aventiure2.german.base.Nominalphrase.npArtikellos;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.BEI_DAT;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.IN_DAT;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.UNTER_DAT;
import static de.nb.aventiure2.util.StreamUtil.*;

/**
 * Beschreibt die {@link Bewoelkung} als {@link Praepositionalphrase}.
 */
public class BewoelkungPraepPhrDescriber {
    private final BewoelkungPraedikativumDescriber praedikativumDescriber;

    public BewoelkungPraepPhrDescriber(
            final BewoelkungPraedikativumDescriber praedikativumDescriber) {
        this.praedikativumDescriber = praedikativumDescriber;
    }

    public ImmutableSet<Praepositionalphrase> altBeiLichtImLicht(final Bewoelkung bewoelkung,
                                                                 final Tageszeit tageszeit,
                                                                 final boolean unterOffenemHimmel) {
        return altImLicht(bewoelkung, tageszeit, unterOffenemHimmel,
                BEI_DAT.mit(npArtikellos(LICHT)));
    }

    public ImmutableSet<Praepositionalphrase> altBeiTageslichtImLicht(final Bewoelkung bewoelkung,
                                                                      final Tageszeit tageszeit,
                                                                      final boolean unterOffenemHimmel) {
        return altImLicht(bewoelkung, tageszeit, unterOffenemHimmel,
                BEI_DAT.mit(npArtikellos(TAGESLICHT)));
    }

    private ImmutableSet<Praepositionalphrase> altImLicht(
            final Bewoelkung bewoelkung,
            final Tageszeit tageszeit,
            final boolean unterOffenemHimmel,
            final Praepositionalphrase alternative) {
        final ImmutableSet.Builder<Praepositionalphrase> alt = ImmutableSet.builder();
        alt.add(alternative);
        alt.addAll(
                mapToSet(praedikativumDescriber
                                .altLichtInDemEtwasLiegt(bewoelkung, tageszeit, unterOffenemHimmel),
                        IN_DAT::mit));
        return alt.build();
    }

    // FIXME "beim Mondschimmer"
    // FIXME "der Hügel liegt in einsamem Mondschein."

    public ImmutableSet<Praepositionalphrase> altUnterOffenemHimmel(
            final Bewoelkung bewoelkung,
            final Tageszeit tageszeit) {
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
