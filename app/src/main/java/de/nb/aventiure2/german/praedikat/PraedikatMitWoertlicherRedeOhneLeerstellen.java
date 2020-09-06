package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.WoertlicheRede;

import static de.nb.aventiure2.german.base.GermanUtil.capitalize;
import static de.nb.aventiure2.german.base.GermanUtil.joinToNull;

/**
 * Ein Prädikat mit wörtlicher Rede, in dem alle Leerstellen besetzt sind. Beispiele:
 * <ul>
 *     <li>"„Lass dein Haar herunter“ rufen"
 * </ul>
 * <p>
 * Adverbiale Angaben ("laut") können immer noch eingefügt werden.
 */
public class PraedikatMitWoertlicherRedeOhneLeerstellen
        implements PraedikatOhneLeerstellen {
    /**
     * Das Verb an sich, ohne Informationen zur Valenz, ohne Ergänzungen, ohne
     * Angaben
     */
    @NonNull
    private final Verb verb;

    /**
     * Die wörtliche Rede
     */
    @NonNull
    private final WoertlicheRede woertlicheRede;

    public PraedikatMitWoertlicherRedeOhneLeerstellen(
            @NonNull final Verb verb, @NonNull final String woertlicheRedeText) {
        this(verb, new WoertlicheRede(woertlicheRedeText));
    }

    public PraedikatMitWoertlicherRedeOhneLeerstellen(
            @NonNull final Verb verb, @NonNull final WoertlicheRede woertlicheRede) {
        this.verb = verb;
        this.woertlicheRede = woertlicheRede;
    }

    /**
     * Gibt einen Satz zurück mit diesem Prädikat.
     * ("Du rufst mal eben aus: ...")
     */
    @Override
    public String getDescriptionDuHauptsatz(
            final Collection<Modalpartikel> modalpartikeln) {
        final String verbPartikelUndDoppelpunkt =
                joinToNull(verb.getPartikel(), ":");

        return joinToNull(
                "Du",
                verb.getDuForm(), // rufst
                joinToNull(modalpartikeln), // mal eben
                verbPartikelUndDoppelpunkt, // aus:
                woertlicheRede.amSatzende()); // "„Kommt alle her.“"
    }

    @Override
    public boolean duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen() {
        return false;
    }

    /**
     * Gibt einen Satz zurück mit diesem Prädikat.
     * ("Laut rufst du aus: ...")
     */
    @Override
    public String getDescriptionDuHauptsatz(@Nonnull final AdverbialeAngabe adverbialeAngabe) {
        final String verbPartikelUndDoppelpunkt =
                joinToNull(verb.getPartikel(), ":");

        return joinToNull(
                capitalize(adverbialeAngabe.getText()), // Laut
                verb.getDuForm(), // rufst
                "du",
                verbPartikelUndDoppelpunkt, // aus:
                woertlicheRede.amSatzende()); // "„Kommt alle her.“"
    }

    /**
     * Gibt eine Infinitivkonstruktion zurück mit dem Prädikat.
     * ("Laut ausrufen: ...")
     */
    @Override
    public String getDescriptionInfinitiv(final Person person, final Numerus numerus,
                                          @Nullable final AdverbialeAngabe adverbialeAngabe) {
        return capitalize(
                joinToNull(
                        adverbialeAngabe, // Laut
                        verb.getInfinitiv() + ":", // ausrufen:
                        woertlicheRede.amSatzende())); // "„Kommt alle her.“"
    }

    /**
     * Gibt eine zu-Infinitivkonstruktion zurück mit dem Prädikat.
     * ("Laut auszurufen: ...")
     */
    @Override
    public String getDescriptionZuInfinitiv(final Person person, final Numerus numerus,
                                            @Nullable final AdverbialeAngabe adverbialeAngabe) {
        return capitalize(
                joinToNull(
                        adverbialeAngabe, // Laut
                        verb.getZuInfinitiv() + ":", // ausrzuufen:
                        woertlicheRede.amSatzende())); // "„Kommt alle her.“"
    }
}
