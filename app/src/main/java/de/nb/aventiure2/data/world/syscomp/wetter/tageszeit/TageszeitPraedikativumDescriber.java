package de.nb.aventiure2.data.world.syscomp.wetter.tageszeit;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.data.time.Tageszeit;
import de.nb.aventiure2.german.adjektiv.AdjPhrOhneLeerstellen;
import de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen;
import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;
import de.nb.aventiure2.german.base.NomenFlexionsspalte;
import de.nb.aventiure2.german.base.Praedikativum;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusSatz;

import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.EINBRECHEND;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.NACHT;

/**
 * Beschreibt die {@link Tageszeit} als {@link Praedikativum}.
 * <p>
 * Diese Phrasen sind für jede Temperatur und Bewölkung sinnvoll (wobei manchmal die Bewölkung
 * oder andere Wetteraspekte wichtiger sind und man dann diese Sätze
 * vielleicht gar nicht erzeugen wird).
 */
@SuppressWarnings({"DuplicateBranchesInSwitch", "MethodMayBeStatic"})
public class TageszeitPraedikativumDescriber {
    /**
     * Gibt Alternativen zurück wie "der Tag", "die einbrechende Nacht"
     */
    ImmutableCollection<EinzelneSubstantivischePhrase> alt(
            final AvTime time) {
        final ImmutableList.Builder<EinzelneSubstantivischePhrase> alt =
                ImmutableList.builder();

        // "der Tag"
        alt.addAll(alt(time.getTageszeit()));

        if (time.kurzNachSonnenuntergang()) {
            alt.add(NACHT.mit(EINBRECHEND));
        }

        return alt.build();
    }

    /**
     * Gibt Alternativen zurück wie "der Tag"
     */
    private ImmutableCollection<NomenFlexionsspalte> alt(final Tageszeit tageszeit) {
        final ImmutableList.Builder<NomenFlexionsspalte> alt =
                ImmutableList.builder();

        // "der Tag"
        alt.add(tageszeit.getNomenFlexionsspalte());

        return alt.build();
    }

    public ImmutableCollection<AdjPhrOhneLeerstellen> altSchonBereitsNochDunkelHellAdjPhr(
            final AvTime time) {
        final ImmutableList.Builder<AdjPhrOhneLeerstellen> alt =
                ImmutableList.builder();

        if (time.kurzVorSonnenaufgang()) {
            alt.add(AdjektivOhneErgaenzungen.DUNKEL
                    .mitAdvAngabe(new AdvAngabeSkopusSatz("noch")));
        } else if (time.kurzNachSonnenuntergang()) {
            alt.add(AdjektivOhneErgaenzungen.HELL
                    .mitAdvAngabe(new AdvAngabeSkopusSatz("bereits")));
            alt.add(AdjektivOhneErgaenzungen.HELL
                    .mitAdvAngabe(new AdvAngabeSkopusSatz("schon")));
        } else if (time.kurzVorSonnenuntergang()) {
            alt.add(AdjektivOhneErgaenzungen.HELL
                    .mitAdvAngabe(new AdvAngabeSkopusSatz("gerade noch")));
            alt.add(AdjektivOhneErgaenzungen.HELL
                    .mitAdvAngabe(new AdvAngabeSkopusSatz("immer noch")));
        } else if (time.kurzNachSonnenuntergang()) {
            alt.add(AdjektivOhneErgaenzungen.DUNKEL
                    .mitAdvAngabe(new AdvAngabeSkopusSatz("bereits")));
            alt.add(AdjektivOhneErgaenzungen.DUNKEL
                    .mitAdvAngabe(new AdvAngabeSkopusSatz("schon")));
        } else {
            // "hell" / "dunkel"
            alt.add(time.getTageszeit().getLichtverhaeltnisseDraussen().getAdjektiv());
        }

        return alt.build();
    }
}
