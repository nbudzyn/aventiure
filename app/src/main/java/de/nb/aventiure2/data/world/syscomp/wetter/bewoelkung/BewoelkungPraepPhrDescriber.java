package de.nb.aventiure2.data.world.syscomp.wetter.bewoelkung;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableSet;

import de.nb.aventiure2.data.time.Tageszeit;
import de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen;
import de.nb.aventiure2.german.base.Praepositionalphrase;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusSatz;

import static de.nb.aventiure2.data.time.Tageszeit.ABENDS;
import static de.nb.aventiure2.data.time.Tageszeit.NACHTS;
import static de.nb.aventiure2.data.time.Tageszeit.TAGSUEBER;
import static de.nb.aventiure2.data.world.syscomp.wetter.bewoelkung.Bewoelkung.LEICHT_BEWOELKT;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.DUESTER;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.HELL;
import static de.nb.aventiure2.german.base.Artikel.Typ.DEF;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.HIMMEL;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.LICHT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.MONDSCHEIN;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.MONDSCHIMMER;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.NACHT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.SONNENSCHEIN;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.SONNENUNTERGANG;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.TAG;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.TAGESLICHT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.WOLKEN;
import static de.nb.aventiure2.german.base.Nominalphrase.npArtikellos;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.BEI_DAT;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.IN_AKK;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.IN_DAT;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.UNTER_AKK;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.UNTER_DAT;
import static de.nb.aventiure2.util.StreamUtil.*;

/**
 * Beschreibt die {@link Bewoelkung} als {@link Praepositionalphrase}.
 * <p>
 * Diese Phrasen sind für jede Bewölkung sinnvoll (wobei manchmal die Temperatur
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

    ImmutableSet<Praepositionalphrase> altInLichtTageszeit(
            final Bewoelkung bewoelkung,
            final Tageszeit tageszeit) {
        final ImmutableSet.Builder<Praepositionalphrase> alt = ImmutableSet.builder();

        alt.addAll(mapToList(praedikativumDescriber.altLichtInDemEtwasLiegt(
                bewoelkung, tageszeit, true),
                IN_AKK::mit));

        // "in den grauen Morgen"
        alt.addAll(mapToSet(praedikativumDescriber
                        .altStatischTageszeitUnterOffenenHimmelMitAdj(bewoelkung, tageszeit, DEF),
                IN_AKK::mit));

        if (bewoelkung.compareTo(LEICHT_BEWOELKT) <= 0) {
            // "in den Sonnenschein", "in den Mondschein"
            alt.addAll(mapToList(tageszeit.altGestirnschein(), IN_AKK::mit));
            // "in den hellen Tag"
            alt.add(IN_AKK.mit(TAG.mit(HELL)));

            if (tageszeit != NACHTS) {
                // "in die Morgensonne"
                alt.addAll(mapToSet(tageszeit.altGestirn(), IN_AKK::mit));
            }
        }

        switch (bewoelkung) {
            case WOLKENLOS:
                if (tageszeit == TAGSUEBER) {
                    alt.add(IN_AKK.mit(SONNENSCHEIN.mit(HELL)));
                } else if (tageszeit == ABENDS) {
                    alt.add(IN_AKK.mit(SONNENUNTERGANG));
                } else if (tageszeit == Tageszeit.NACHTS) {
                    alt.add(IN_AKK.mit(MONDSCHEIN.mit(HELL)));
                    alt.add(IN_AKK.mit(
                            NACHT.mit(AdjektivOhneErgaenzungen.ERHELLT
                                    .mitAdvAngabe(new AdvAngabeSkopusSatz("vom Mond")))));
                }
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

    /**
     * Gibt etwas zurück wie "unter den nachtschwarzen Himmel".
     */
    public ImmutableSet<Praepositionalphrase> altUnterOffenenHimmelAkk(
            final Bewoelkung bewoelkung,
            final Tageszeit tageszeit) {
        final ImmutableSet.Builder<Praepositionalphrase> alt = ImmutableSet.builder();

        // "unter den nachtschwarzen Himmel"
        alt.addAll(mapToSet(praedikativumDescriber.altHimmelAdjPhr(bewoelkung, tageszeit),
                a -> UNTER_AKK.mit(HIMMEL.mit(a))));

        switch (bewoelkung) {
            case WOLKENLOS:
                break;
            case LEICHT_BEWOELKT:
                break;
            case BEWOELKT:
                break;
            case BEDECKT:
                alt.add(UNTER_AKK.mit(WOLKEN.mit(DUESTER)));
                break;
            default:
                throw new IllegalStateException("Unexpected Bewoelkung: " + bewoelkung);
        }

        return alt.build();
    }


    public ImmutableSet<Praepositionalphrase> altUnterOffenemHimmelDat(
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


    @NonNull
    Praepositionalphrase inSchoeneTageszeit(final Tageszeit tageszeit) {
        return IN_AKK.mit(praedikativumDescriber.schoeneTageszeit(tageszeit));
    }
}
