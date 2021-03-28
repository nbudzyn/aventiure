package de.nb.aventiure2.german.adjektiv;


import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.nb.aventiure2.annotations.Komplement;
import de.nb.aventiure2.annotations.Valenz;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativSkopusSatz;
import de.nb.aventiure2.german.base.Kasus;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.NumerusGenus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.satz.Satz;

import static de.nb.aventiure2.german.base.Konstituente.k;

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
    private final Satz indirekterFragesatz;

    @Valenz
    AdjPhrMitIndirektemFragesatzOhneLeerstellen(
            final Adjektiv adjektiv,
            final Satz indirekterFragesatz) {
        this(null, null, adjektiv, indirekterFragesatz);
    }

    /**
     * Z.B. "sehr gespannt, ob du etwas zu berichten hast"
     */
    private AdjPhrMitIndirektemFragesatzOhneLeerstellen(
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabeSkopusSatz,
            @Nullable final GraduativeAngabe graduativeAngabe,
            final Adjektiv adjektiv,
            final Satz indirekterFragesatz) {
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
    public String getAttributivAnteilAdjektivattribut(final NumerusGenus numerusGenus, final Kasus kasus,
                                                      final boolean artikelwortTraegtKasusendung) {
        // FIXME Test-driven implementieren
        return null;
    }

    @Nullable
    @Override
    public Satz getAttributivAnteilRelativsatz() {
        // FIXME Test-driven implementieren
        return null;
    }

    @Nullable
    @Override
    public AdjPhrOhneLeerstellen getAttributivAnteilLockererNachtrag() {
        // FIXME Test-driven implementieren
        return null;
    }

    @Override
    @CheckReturnValue
    public Konstituentenfolge getPraedikativOderAdverbial(final Person personSubjekt,
                                                          final Numerus numerusSubjekt) {
        return Konstituentenfolge.joinToKonstituentenfolge(
                getAdvAngabeSkopusSatzDescription(personSubjekt, numerusSubjekt), // "immer noch"
                getGraduativeAngabe(), // "sehr"
                k(getAdjektiv().getPraedikativ()), // "gespannt"
                getPraedikativAnteilKandidatFuerNachfeld(personSubjekt, numerusSubjekt));
        // ", ob du etwas zu berichten hast[, ]"
    }

    @Override
    @Nullable
    public Konstituentenfolge getPraedikativAnteilKandidatFuerNachfeld(final Person person,
                                                                       final Numerus numerus) {
        return Konstituentenfolge.schliesseInKommaEin(
                indirekterFragesatz.getIndirekteFrage()
                // "[,] ob du etwas zu berichten hast[,] ", "[,] was du zu berichten hast[,] " etc.
        );
    }

    @Override
    public boolean enthaeltZuInfinitivOderAngabensatzOderErgaenzungssatz() {
        return true;
    }
}
