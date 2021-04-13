package de.nb.aventiure2.data.world.syscomp.wetter.bewoelkung;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import de.nb.aventiure2.data.time.Tageszeit;
import de.nb.aventiure2.german.adjektiv.AdjPhrOhneLeerstellen;
import de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen;
import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;
import de.nb.aventiure2.german.base.Praedikativum;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static de.nb.aventiure2.data.time.Tageszeit.NACHTS;
import static de.nb.aventiure2.data.time.Tageszeit.TAGSUEBER;
import static de.nb.aventiure2.data.world.syscomp.wetter.bewoelkung.Bewoelkung.WOLKENLOS;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.BEZOGEN;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.DUESTER;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.GETRUEBT;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.GRAU;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.HELL;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.KLAR;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.LICHTLOS;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.NACHTSCHWARZ;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.SANFT;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.SCHUMMRIG;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.STOCKDUNKEL;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.TRUEB;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.VERHANGEN;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.WOLKENVERHANGEN;
import static de.nb.aventiure2.german.base.Artikel.Typ.INDEF;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.ABENDLICHT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.DAEMMERLICHT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.DUESTERNIS;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.DUNKEL;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.DUNKELHEIT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.HALBDUNKEL;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.HIMMEL;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.LICHT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.MONDLICHT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.MORGENLICHT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.SCHUMMERLICHT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.STERNENLICHT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.TAGESLICHT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.ZWIELICHT;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.util.StreamUtil.*;

/**
 * Beschreibt die {@link Bewoelkung} als {@link Praedikativum}.
 */
public class BewoelkungPraedikativumDescriber {

    /**
     * Gibt Alternativen wie "der schummrige Morgen" oder "der Tag" zurück.
     */
    ImmutableCollection<SubstantivischePhrase> altTageszeitUnterOffenenHimmelDef(
            final Bewoelkung bewoelkung,
            final Tageszeit tageszeit) {
        final ImmutableCollection<AdjektivOhneErgaenzungen> altAdj =
                altTageszeitUnterOffenemHimmelAdj(bewoelkung, tageszeit);

        if (!altAdj.isEmpty()) {
            // "der schummrige Morgen"
            return mapToSet(altAdj, a -> tageszeit.getNomenFlexionsspalte().mit(a));
        }

        // "der Morgen"
        return ImmutableSet.of(tageszeit.getNomenFlexionsspalte());
    }

    /**
     * Gibt Alternativen wie "ein schummriger Morgen" zurück, immer mit Adjektiv, ggf. leer.
     */
    ImmutableCollection<SubstantivischePhrase> altTageszeitUnterOffenenHimmelIndefMitAdj(
            final Bewoelkung bewoelkung,
            final Tageszeit tageszeit) {
        // "ein schummriger Morgen"
        return mapToSet(altTageszeitUnterOffenemHimmelAdj(bewoelkung, tageszeit), a ->
                np(INDEF, a, tageszeit.getNomenFlexionsspalte()));
    }

    private ImmutableCollection<AdjektivOhneErgaenzungen> altTageszeitUnterOffenemHimmelAdj(
            final Bewoelkung bewoelkung,
            final Tageszeit tageszeit) {
        switch (tageszeit) {
            case MORGENS:
                return altMorgenUnterOffenenHimmelAdj(bewoelkung);
            case TAGSUEBER:
                return altTagUnterOffenenHimmelAdj(bewoelkung);
            case ABENDS:
                return altAbendUnterOffenenHimmelAdj(bewoelkung);
            case NACHTS:
                return altNachtUnterOffenenHimmelAdj(bewoelkung);
            default:
                throw new IllegalStateException("Unexpected Tageszeit: " + bewoelkung);
        }
    }

    /**
     * Gibt Alternativen wie "schummeriger Morgen" zurück (immer mit Adjektivphrase), evtl. leer.
     */
    private static ImmutableCollection<AdjektivOhneErgaenzungen> altMorgenUnterOffenenHimmelAdj(
            final Bewoelkung bewoelkung) {
        final ImmutableList.Builder<AdjektivOhneErgaenzungen> alt =
                ImmutableList.builder();
        switch (bewoelkung) {
            case WOLKENLOS:
                alt.add(KLAR);
                // fall-through
            case LEICHT_BEWOELKT:
                alt.add(HELL);
                break;
            case BEWOELKT:
                alt.add(SCHUMMRIG);
                break;
            case BEDECKT:
                alt.add(TRUEB, DUESTER, GRAU, LICHTLOS, VERHANGEN);
                break;
            default:
                throw new IllegalStateException("Unexpected Bewoelkung: " + bewoelkung);
        }

        return alt.build();
    }

    /**
     * Gibt Alternativen wie "trüb" zurück - ggf. eine leere Collection.
     */
    private static ImmutableCollection<AdjektivOhneErgaenzungen> altTagUnterOffenenHimmelAdj(
            final Bewoelkung bewoelkung) {
        final ImmutableList.Builder<AdjektivOhneErgaenzungen> alt =
                ImmutableList.builder();
        switch (bewoelkung) {
            case WOLKENLOS:
                // fall-through
            case LEICHT_BEWOELKT:
                alt.add(HELL);
                break;
            case BEWOELKT:
                break;
            case BEDECKT:
                alt.add(TRUEB, DUESTER, GRAU);
                break;
            default:
                throw new IllegalStateException("Unexpected Bewoelkung: " + bewoelkung);
        }

        return alt.build();
    }

    /**
     * Gibt Alternativen wie "schummerige" zurück, ggf. leer.
     */
    private static ImmutableCollection<AdjektivOhneErgaenzungen> altAbendUnterOffenenHimmelAdj(
            final Bewoelkung bewoelkung
    ) {
        final ImmutableList.Builder<AdjektivOhneErgaenzungen> alt =
                ImmutableList.builder();
        switch (bewoelkung) {
            case WOLKENLOS:
                break;
            case LEICHT_BEWOELKT:
                break;
            case BEWOELKT:
                alt.add(AdjektivOhneErgaenzungen.DUNKEL);
                break;
            case BEDECKT:
                alt.add(DUESTER, AdjektivOhneErgaenzungen.DUNKEL, SCHUMMRIG);
            default:
                throw new IllegalStateException("Unexpected Bewoelkung: " + bewoelkung);
        }

        return alt.build();
    }

    /**
     * Gibt Alternativen wie "stockdunkel" zurück, evtl. leer.
     */
    private ImmutableCollection<AdjektivOhneErgaenzungen> altNachtUnterOffenenHimmelAdj(
            final Bewoelkung bewoelkung) {
        final ImmutableList.Builder<AdjektivOhneErgaenzungen> alt =
                ImmutableList.builder();
        switch (bewoelkung) {
            case WOLKENLOS:
                break;
            case LEICHT_BEWOELKT:
                break;
            case BEWOELKT:
                alt.add(AdjektivOhneErgaenzungen.DUNKEL);
                break;
            case BEDECKT:
                alt.add(AdjektivOhneErgaenzungen.DUNKEL, STOCKDUNKEL);
                break;
            default:
                throw new IllegalStateException("Unexpected Bewoelkung: " + bewoelkung);
        }

        return alt.build();
    }

    /**
     * Gibt alternative substantivische Phrasen zurück, die den (offenen) Himmel
     * beschreiben - <i>Ergebnis kann leer sein</i>.
     */
    ImmutableSet<EinzelneSubstantivischePhrase> altOffenerHimmel(
            final Bewoelkung bewoelkung,
            final Tageszeit tageszeit) {
        final ImmutableSet.Builder<EinzelneSubstantivischePhrase> alt = ImmutableSet.builder();

        alt.addAll(mapToSet(altHimmelAdjPhr(bewoelkung, tageszeit), HIMMEL::mit));

        if (bewoelkung == WOLKENLOS) {
            alt.addAll(tageszeit.altWolkenloserHimmel());
        }

        return alt.build();
    }


    /**
     * Gibt alternative Adjektivphrasen zurück, die den (offenen) Himmel
     * beschreiben - <i>Ergebnis kann leer sein</i>.
     */
    ImmutableSet<AdjPhrOhneLeerstellen> altHimmelAdjPhr(
            final Bewoelkung bewoelkung,
            final Tageszeit tageszeit) {
        final ImmutableSet.Builder<AdjPhrOhneLeerstellen> alt = ImmutableSet.builder();

        switch (bewoelkung) {
            case WOLKENLOS:
                // kann leer sein
                alt.addAll(tageszeit.altAltAdjPhrWolkenloserHimmel());
                break;
            case LEICHT_BEWOELKT:
                // bleibt leer
                break;
            case BEWOELKT:
                alt.add(AdjektivOhneErgaenzungen.BEWOELKT);
                break;
            case BEDECKT:
                if (tageszeit == NACHTS) {
                    alt.add(NACHTSCHWARZ);
                } else {
                    alt.add(AdjektivOhneErgaenzungen.BEDECKT,
                            BEZOGEN, WOLKENVERHANGEN);
                }
                break;
            default:
                throw new IllegalStateException("Unexpected Bewoelkung: " + bewoelkung);
        }

        return alt.build();
    }

    public static ImmutableCollection<EinzelneSubstantivischePhrase> altLichtInDemEtwasLiegt(
            final Bewoelkung bewoelkung,
            final Tageszeit tageszeit, final boolean unterOffenemHimmel) {
        final ImmutableList.Builder<EinzelneSubstantivischePhrase> alt =
                ImmutableList.builder();

        switch (bewoelkung) {
            case WOLKENLOS:
                if (unterOffenemHimmel) {
                    if (tageszeit == TAGSUEBER) {
                        alt.add(np(HELL, TAGESLICHT));
                    } else if (tageszeit == NACHTS) {
                        alt.add(STERNENLICHT);
                    }
                }
                // fall-through
            case LEICHT_BEWOELKT:
                switch (tageszeit) {
                    case MORGENS:
                        alt.add(MORGENLICHT);
                        break;
                    case TAGSUEBER:
                        alt.add(TAGESLICHT);
                        break;
                    case ABENDS:
                        alt.add(ABENDLICHT);
                        break;
                    case NACHTS:
                        alt.add(MONDLICHT);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected tageszeit: " + tageszeit);
                }
                break;
            case BEWOELKT:
                alt.addAll(altLichtInDemEtwasLiegtBewoelkt(tageszeit));
                break;
            case BEDECKT:
                alt.addAll(altLichtInDemEtwasLiegtBedeckt(tageszeit));
                break;
            default:
                throw new IllegalStateException("Unexpected Bewoelkung: " + bewoelkung);
        }

        return alt.build();
    }

    private static ImmutableCollection<EinzelneSubstantivischePhrase> altLichtInDemEtwasLiegtBewoelkt(
            final Tageszeit tageszeit) {
        switch (tageszeit) {
            case MORGENS:
                return ImmutableSet.of(np(SANFT, MORGENLICHT));
            case TAGSUEBER:
                return ImmutableSet.of(np(SANFT, TAGESLICHT), np(GETRUEBT, TAGESLICHT));
            case ABENDS:
                return ImmutableSet.of(SCHUMMERLICHT, DAEMMERLICHT);
            case NACHTS:
                return ImmutableSet.of(np(SANFT, MONDLICHT));
            default:
                throw new IllegalStateException("Unexpected Tageszeit: " + tageszeit);
        }
    }

    private static ImmutableCollection<EinzelneSubstantivischePhrase> altLichtInDemEtwasLiegtBedeckt(
            final Tageszeit tageszeit) {
        switch (tageszeit) {
            case MORGENS:
                return ImmutableSet.of(np(TRUEB, DAEMMERLICHT));
            case TAGSUEBER:
                return ImmutableSet.of(np(TRUEB, LICHT));
            case ABENDS:
                return ImmutableSet.of(ZWIELICHT, HALBDUNKEL);
            case NACHTS:
                return ImmutableSet.of(DUNKEL, DUNKELHEIT, DUESTERNIS);
            default:
                throw new IllegalStateException("Unexpected Tageszeit: " + tageszeit);
        }
    }

}
