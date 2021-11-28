package de.nb.aventiure2.data.world.syscomp.wetter.tageszeit;

import static de.nb.aventiure2.german.base.Nominalphrase.npArtikellos;
import static de.nb.aventiure2.util.StreamUtil.*;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.data.time.Tageszeit;
import de.nb.aventiure2.german.base.Praedikativum;
import de.nb.aventiure2.german.satz.EinzelnerSemSatz;
import de.nb.aventiure2.german.satz.SemSatz;

/**
 * Beschreibt die {@link Tageszeit} als {@link SemSatz}.
 * <p>
 * Diese Phrasen sind für jede Temperatur und Bewölkung sinnvoll (wobei manchmal die Bewölkung
 * oder andere Wetteraspekte wichtiger sind und man dann diese Sätze
 * vielleicht gar nicht erzeugen werden).
 */
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
    static ImmutableSet<SemSatz>
    altWechselDraussen(final Tageszeit newTageszeit) {
        final ImmutableSet.Builder<SemSatz> alt = ImmutableSet.builder();

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

    /**
     * Gibt Alternativen zurück wie "draußen ist es schon dunkel" - oder eine leere
     * {@link java.util.Collection}.
     */
    public ImmutableCollection<EinzelnerSemSatz> altSpDraussen(
            final AvTime time,
            final boolean auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben) {
        final ImmutableSet.Builder<EinzelnerSemSatz> alt = ImmutableSet.builder();

        if (time.getTageszeit() != Tageszeit.TAGSUEBER) {
            // "es ist Morgen"
            alt.add(npArtikellos(
                    time.getTageszeit().getNomenFlexionsspalte()).alsEsIstSatz());
        }

        // "es ist schon hell"
        alt.addAll(altSpSchonBereitsNochDunkelHellDraussen(time,
                auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben));

        return alt.build();
    }

    /**
     * Gibt Alternativen zurück wie "es ist schon dunkel" - oder eine leere
     * {@link java.util.Collection}.
     */
    ImmutableCollection<EinzelnerSemSatz> altSpSchonBereitsNochDunkelHellDraussen(
            final AvTime time,
            final boolean auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben) {
        return mapToSet(praedikativumDescriber.altSpSchonBereitsNochDunkelHellAdjPhr(time,
                auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben),
                Praedikativum::alsEsIstSatz);
    }
}
