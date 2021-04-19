package de.nb.aventiure2.data.world.syscomp.wetter.tageszeit;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.data.time.Tageszeit;
import de.nb.aventiure2.data.world.base.Lichtverhaeltnisse;
import de.nb.aventiure2.german.adjektiv.AdjPhrOhneLeerstellen;
import de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen;
import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;
import de.nb.aventiure2.german.base.NomenFlexionsspalte;
import de.nb.aventiure2.german.base.Praedikativum;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusSatz;

import static de.nb.aventiure2.data.time.Tageszeit.MORGENS;
import static de.nb.aventiure2.data.time.Tageszeit.NACHTS;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.BEGINNEND;
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
            final AvTime time,
            final boolean auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben) {
        final ImmutableList.Builder<EinzelneSubstantivischePhrase> alt =
                ImmutableList.builder();

        // "der Tag", "das Helle"
        alt.addAll(alt(time.getTageszeit(),
                auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben));

        if (time.kurzNachSonnenuntergang()) {
            alt.add(NACHT.mit(EINBRECHEND));
            alt.add(NACHT.mit(BEGINNEND));
        }

        return alt.build();
    }

    /**
     * Gibt Alternativen zurück wie "der Tag", "das Helle"
     */
    public ImmutableCollection<NomenFlexionsspalte> alt(
            final Tageszeit tageszeit,
            final boolean auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben) {
        final ImmutableList.Builder<NomenFlexionsspalte> alt =
                ImmutableList.builder();

        // "der Tag"
        alt.add(tageszeit.getNomenFlexionsspalte());

        if ((tageszeit == MORGENS || tageszeit == NACHTS)
                && auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben) {
            // "das Helle", "die Dunkelheit", "das Dunkel"
            alt.addAll(altLichtverhaeltnisseNomenFlexionsspalte(tageszeit));
        }

        return alt.build();
    }

    ImmutableSet<NomenFlexionsspalte> altLichtverhaeltnisseNomenFlexionsspalte(
            final Tageszeit tageszeit) {
        return tageszeit.getLichtverhaeltnisseDraussen().altNomenFlexionsspalten();
    }

    /**
     * Gibt Adjektivphrasen zurück in der Art "noch hell" oder "schon dunkel";
     * ggf. auch einfach "hell", falls {@code
     * auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben}
     * - oder sonst eine leere {@link java.util.Collection}.
     */
    public ImmutableCollection<AdjPhrOhneLeerstellen> altSchonBereitsNochDunkelHellAdjPhr(
            final AvTime time,
            final boolean auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben) {
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
            final Lichtverhaeltnisse lichtverhaeltnisseDraussen =
                    time.getTageszeit().getLichtverhaeltnisseDraussen();

            if (lichtverhaeltnisseDraussen == Lichtverhaeltnisse.DUNKEL
                    || auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben) {
                // "hell" / "dunkel"
                alt.add(lichtverhaeltnisseDraussen.getAdjektiv());
            }
        }

        return alt.build();
    }
}
