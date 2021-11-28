package de.nb.aventiure2.german.adjektiv;


import static de.nb.aventiure2.german.base.Konstituente.k;

import java.util.Objects;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.nb.aventiure2.annotations.Komplement;
import de.nb.aventiure2.annotations.Valenz;
import de.nb.aventiure2.german.base.Belebtheit;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativSkopusSatz;
import de.nb.aventiure2.german.base.Kasus;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.NumerusGenus;
import de.nb.aventiure2.german.base.PraedRegMerkmale;
import de.nb.aventiure2.german.base.Praedikativum;
import de.nb.aventiure2.german.satz.SemSatz;

/**
 * Eine Adjektivphrase mit ob- oder w-Fragesatz, in der alle Leerstellen besetzt sind. Beispiel:
 * <ul>
 * <li>gespannt, ob du etwas zu berichten hast
 * <li>gespannt, was du zu berichten hast
 * <li>gespannt, was wer zu berichten hat
 * <li>gespannt, mit wem sie sich treffen wird
 * <li>gespannt, wann du etwas zu berichten hast
 * <li>gespannt, wessen Heldentaten wer zu berichten hat
 * <li>gespannt, was zu erzählen du beginnen wirst
 * <li>gespannt, was du zu erzählen beginnen wirst
 * </ul>
 */
public class AdjPhrMitIndirektemFragesatzOhneLeerstellen extends AbstractAdjPhrOhneLeerstellen {
    /**
     * Der ob- oder w-Fragesatz (die indirekte Frage), z.B.
     * <ul>
     * <li>(gespannt, ob) du etwas zu berichten hast
     * <li>(gespannt, ) was du zu berichten hast
     * <li>(gespannt, ) mit wem sie sich treffen wird
     * <li>(gespannt, ) wann du etwas zu berichten hast
     * <li>(gespannt, ) wessen Heldentaten wer zu berichten hat
     * <li>(gespannt,) was zu erzählen du beginnen wirst
     * <li>(gespannt,) was du zu erzählen beginnen wirst
     * </ul>
     * <p>
     * Der indirekte Fragesatz braucht keine Fragewörter / Fragephrasen zu enthalten
     * ("du etwas zu berichten hast") - das entspricht einer ob-Frage.
     * Er kann aber auch ein oder mehrere Fragewörter enthalten
     * ("<i>was</i> du zu berichten hast", "<i>was</i> <i>wer</i> zu berichten hat").
     * Die Fragewörter können je nach erfragtem Kasus auch eine Präposition umfassen
     * ("<i>mit wem</i>sie sich treffen wird"). Außerdem kann nach Attributen
     * gefragt werden ("<i>wessen Heldentaten</i> du zu berichten hast"). Oder es
     * kann eine ganze Infinitivphrase als Fragewort verwendet werden, die auch
     * diskontinuierlich aufgeteilt werden kann ("<i>was zu erzählen</i> du beginnen wirst",
     * "<i>was</i> du <i>zu erzählen</i> beginnen wirst").
     */
    @Nonnull
    @Komplement
    private final SemSatz indirekterFragesatz;

    @Valenz
    AdjPhrMitIndirektemFragesatzOhneLeerstellen(
            final Adjektiv adjektiv,
            final SemSatz indirekterFragesatz) {
        this(null, null, adjektiv, indirekterFragesatz);
    }

    /**
     * Z.B. "sehr gespannt, ob du etwas zu berichten hast"
     */
    private AdjPhrMitIndirektemFragesatzOhneLeerstellen(
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabeSkopusSatz,
            @Nullable final GraduativeAngabe graduativeAngabe,
            final Adjektiv adjektiv,
            final SemSatz indirekterFragesatz) {
        super(advAngabeSkopusSatz, graduativeAngabe, adjektiv);
        this.indirekterFragesatz = indirekterFragesatz;
    }

    @Override
    public AdjPhrMitIndirektemFragesatzOhneLeerstellen mitGraduativerAngabe(
            @Nullable final GraduativeAngabe graduativeAngabe) {
        if (graduativeAngabe == null) {
            return this;
        }

        return new AdjPhrMitIndirektemFragesatzOhneLeerstellen(
                getAdvAngabeSkopusSatz(), graduativeAngabe,
                getAdjektiv(),
                indirekterFragesatz
        );
    }

    @Override
    public AdjPhrMitIndirektemFragesatzOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        return new AdjPhrMitIndirektemFragesatzOhneLeerstellen(
                advAngabe, getGraduativeAngabe(),
                getAdjektiv(),
                indirekterFragesatz
        );
    }

    @Nullable
    @Override
    public String getAttributivAnteilAdjektivattribut(final NumerusGenus numerusGenus,
                                                      final Belebtheit belebtheit,
                                                      final Kasus kasus,
                                                      final boolean artikelwortTraegtKasusendung) {
        return null;
    }

    @Nullable
    @Override
    public Praedikativum getAttributivAnteilRelativsatz(
            final Kasus kasusBezugselement) {
        if (kasusBezugselement == Kasus.NOM) {
            // besser kein Relativsatz, sondern lockerer Nachtrag: "Rapunzel, gespannt, was du zu
            // berichten hast, ..."
            return null;
        }

        // "(die )gespannt( ist), was wer zu berichten hat"
        return this;
    }

    @Nullable
    @Override
    public AdjPhrOhneLeerstellen getAttributivAnteilLockererNachtrag(
            final Kasus kasusBezugselement) {
        if (kasusBezugselement == Kasus.NOM) {
            return this;
        }

        // Nebensatz - ansonsten kann es zu Missverständnissen oder falscher
        // Bedeutung kommen: "Du hilfst Rapunzel, gespannt, was du zu berichten hast"
        // hat nicht die intendierte Bedeutung!

        return null;
    }


    @Override
    @CheckReturnValue
    public Konstituentenfolge getPraedikativOderAdverbial(final PraedRegMerkmale praedRegMerkmale) {
        return Konstituentenfolge.joinToKonstituentenfolge(
                getAdvAngabeSkopusSatzDescription(praedRegMerkmale),
                // "immer noch"
                getGraduativeAngabe(), // "sehr"
                k(getAdjektiv().getPraedikativ()), // "gespannt"
                getPraedikativAnteilKandidatFuerNachfeld(praedRegMerkmale));
        // ", ob du etwas zu berichten hast[, ]"
    }

    @Override
    @Nullable
    public Konstituentenfolge getPraedikativAnteilKandidatFuerNachfeld(
            final PraedRegMerkmale praedRegMerkmale) {
        return Konstituentenfolge.schliesseInKommaEin(
                indirekterFragesatz.getIndirekteFrage()
                // "[,] ob du etwas zu berichten hast[,] ", "[,] was du zu berichten hast[,] " etc.
        );
    }

    @Override
    public boolean enthaeltZuInfinitivOderAngabensatzOderErgaenzungssatz() {
        return true;
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final AdjPhrMitIndirektemFragesatzOhneLeerstellen that =
                (AdjPhrMitIndirektemFragesatzOhneLeerstellen) o;
        return indirekterFragesatz.equals(that.indirekterFragesatz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), indirekterFragesatz);
    }
}
