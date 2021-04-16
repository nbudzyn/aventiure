package de.nb.aventiure2.data.world.syscomp.wetter.tageszeit;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.data.time.Tageszeit;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.satz.EinzelnerSatz;
import de.nb.aventiure2.german.satz.Satz;

import static de.nb.aventiure2.util.StreamUtil.*;

/**
 * Beschreibt die {@link Tageszeit} als {@link Satz}.
 * <p>
 * Diese Phrasen sind für jede Temperatur und Bewölkung sinnvoll (wobei manchmal die Bewölkung
 * oder andere Wetteraspekte wichtiger sind und man dann diese Sätze
 * vielleicht gar nicht erzeugen werden).
 */
@SuppressWarnings({"DuplicateBranchesInSwitch", "MethodMayBeStatic"})
public class TageszeitSatzDescriber {
    private final TageszeitPraedikativumDescriber praedikativumDescriber;

    public TageszeitSatzDescriber(
            final TageszeitPraedikativumDescriber praedikativumDescriber) {
        this.praedikativumDescriber = praedikativumDescriber;
    }

    /**
     * Gibt Alternativen zurück, die den Tageszeitenwechsel beschreiben,
     * wie man ihn draußen erlebt.
     */
    @NonNull
    @CheckReturnValue
    ImmutableSet<Satz>
    altWechselDraussen(final Tageszeit newTageszeit) {
        final ImmutableSet.Builder<Satz> alt = ImmutableSet.builder();

        // "Langsam wird es Morgen", "Der Abend bricht an"
        alt.addAll(newTageszeit.altLangsamBeginntSaetze());

        if (newTageszeit.getVorgaenger()
                .getLichtverhaeltnisseDraussen() != newTageszeit
                .getLichtverhaeltnisseDraussen()) {
            // "langsam wird es hell"
            alt.addAll(newTageszeit.getLichtverhaeltnisseDraussen().altLangsamWirdEsSaetze());
        }

        return alt.build();
    }

    public ImmutableCollection<EinzelnerSatz> altDraussen(
            final AvTime time) {
        final ImmutableSet.Builder<EinzelnerSatz> alt = ImmutableSet.builder();

        if (time.getTageszeit() != Tageszeit.TAGSUEBER) {
            // "es ist Morgen"
            alt.addAll(mapToSet(praedikativumDescriber.alt(time.getTageszeit()),
                    derMorgen -> Nominalphrase.npArtikellos(derMorgen).alsEsIstSatz()));
        }

        // "es ist schon hell"
        alt.addAll(altSchonBereitsNochDunkelHellDraussen(time));

        return alt.build();
    }


    public ImmutableCollection<EinzelnerSatz> altSchonBereitsNochDunkelHellDraussen(
            final AvTime time) {
        return mapToSet(praedikativumDescriber.altSchonBereitsNochDunkelHellAdjPhr(time),
                a -> a.alsEsIstSatz());
    }
}
