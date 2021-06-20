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
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.NAECHTLICH;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.NACHT;
import static de.nb.aventiure2.util.StreamUtil.*;

/**
 * Beschreibt die {@link Tageszeit} als {@link Praedikativum}.
 * <p>
 * Diese Phrasen sind für jede Temperatur und Bewölkung sinnvoll (wobei manchmal die Bewölkung
 * oder andere Wetteraspekte wichtiger sind und man dann diese Sätze
 * vielleicht gar nicht erzeugen wird).
 */
@SuppressWarnings({"MethodMayBeStatic"})
public class TageszeitPraedikativumDescriber {
    /**
     * Gibt Alternativen zurück wie "der Tag", "die einbrechende Nacht" - eventuell leer, wenn
     * nicht auch reine Tageszeiten zurückgegeben werden sollen.
     */
    ImmutableCollection<EinzelneSubstantivischePhrase> altSpSubstPhr(
            final AvTime time,
            final boolean auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben,
            final boolean auchReineTageszeiten) {
        final ImmutableList.Builder<EinzelneSubstantivischePhrase> alt =
                ImmutableList.builder();

        // "der Tag", "das Helle"
        alt.addAll(altSpSubstPhr(time.getTageszeit(),
                auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben, auchReineTageszeiten));

        if (time.kurzNachSonnenuntergang()) {
            alt.add(NACHT.mit(EINBRECHEND));
            alt.add(NACHT.mit(BEGINNEND));
        }

        return alt.build();
    }

    /**
     * Gibt Alternativen zurück wie "der Tag", "das Helle" - eventuell leer, wenn
     * nicht auch reine Tageszeiten zurückgegeben werden sollen.
     */
    private ImmutableCollection<EinzelneSubstantivischePhrase> altSpSubstPhr(
            final Tageszeit tageszeit,
            final boolean auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben,
            final boolean auchReineTageszeiten) {
        final ImmutableList.Builder<EinzelneSubstantivischePhrase> alt =
                ImmutableList.builder();

        if (auchReineTageszeiten) {
            // "der Tag"
            alt.add(tageszeit.getNomenFlexionsspalte());
        }

        if ((tageszeit == MORGENS || tageszeit == NACHTS)
                && auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben) {
            // "das Helle", "die Dunkelheit", "das Dunkel"
            alt.addAll(altLichtverhaeltnisseNomenFlexionsspalte(tageszeit));

            if (tageszeit == NACHTS) {
                alt.addAll(mapToSet(altLichtverhaeltnisseNomenFlexionsspalte(NACHTS),
                        dunkelheit -> dunkelheit.mit(NAECHTLICH)));
            }
        }

        return alt.build();
    }

    private ImmutableSet<NomenFlexionsspalte> altLichtverhaeltnisseNomenFlexionsspalte(
            final Tageszeit tageszeit) {
        return tageszeit.getLichtverhaeltnisseDraussen().altNomenFlexionsspalten();
    }

    /**
     * Gibt Adjektivphrasen zurück in der Art "noch hell" oder "schon dunkel";
     * ggf. auch einfach "hell", falls {@code
     * auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben}
     * - oder sonst eine leere {@link java.util.Collection}.
     */
    public ImmutableCollection<AdjPhrOhneLeerstellen> altSpSchonBereitsNochDunkelHellAdjPhr(
            final AvTime time,
            final boolean auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben) {
        final ImmutableList.Builder<AdjPhrOhneLeerstellen> alt =
                ImmutableList.builder();

        if (time.kurzVorSonnenaufgang()) {
            alt.add(AdjektivOhneErgaenzungen.DUNKEL
                    .mitAdvAngabe(new AdvAngabeSkopusSatz("noch")));
        } else if (time.kurzNachSonnenaufgang()) {
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
