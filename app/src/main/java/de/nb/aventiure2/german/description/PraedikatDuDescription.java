package de.nb.aventiure2.german.description;

import androidx.annotation.Nullable;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.german.base.GermanUtil;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusVerbWohinWoher;
import de.nb.aventiure2.german.praedikat.PraedikatOhneLeerstellen;

import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.Person.P2;

/**
 * A description based on a {@link PraedikatOhneLeerstellen} - assuming the player
 * character is the (first) subject. Somehting like "Du gehst in den Wald."
 */
public class PraedikatDuDescription
        extends AbstractDuDescription<PraedikatDuTextPart, PraedikatDuDescription> {
    PraedikatDuDescription(final StructuralElement startsNew,
                           final PraedikatOhneLeerstellen praedikat) {
        super(startsNew, new PraedikatDuTextPart(praedikat),
                guessWoertlicheRedeNochOffen(praedikat),
                guessKommaStehtAus(praedikat));
    }

    private static boolean guessWoertlicheRedeNochOffen(final PraedikatOhneLeerstellen praedikat) {
        // FIXME Hier gibt es ein Problem: Ob die wörtliche Rede noch offen ist, hängt von der
        //  konkreten Realisierung des Prädikats ab (was steht im Vorfeld / Nachfeld etc.).
        //  Dies hier ist nur eine grobe Richtschnur.
        return Konstituente.woertlicheRedeNochOffen(
                praedikat
                        .alsSatzMitSubjekt(Personalpronomen.get(P2, M))
                        .getVerbzweitsatzStandard());
    }

    private static boolean guessKommaStehtAus(final PraedikatOhneLeerstellen praedikat) {
        // FIXME Hier gibt es ein Problem: Ob ein Komma aussteht, hängt von der
        //  konkreten Realisierung des Prädikats ab (was steht im Vorfeld / Nachfeld etc.).
        //  Dies hier ist nur eine grobe Richtschnur.
        return Konstituente.kommaStehtAus(
                praedikat
                        .alsSatzMitSubjekt(Personalpronomen.get(P2, M))
                        .getVerbzweitsatzStandard());
    }

    @CheckReturnValue
    public PraedikatDuDescription mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusSatz adverbialeAngabe) {
        return copy(duTextPart.getPraedikat().mitAdverbialerAngabe(adverbialeAngabe));
    }

    @CheckReturnValue
    public PraedikatDuDescription mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusVerbAllg adverbialeAngabe) {
        return copy(duTextPart.getPraedikat().mitAdverbialerAngabe(adverbialeAngabe));
    }

    @CheckReturnValue
    public PraedikatDuDescription mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusVerbWohinWoher adverbialeAngabe) {
        return copy(duTextPart.getPraedikat().mitAdverbialerAngabe(adverbialeAngabe));
    }

    /**
     * Gibt das Prädikat als eine unflektierte Phrase mit Partizip II zurück: "unten angekommen",
     * "die Kugel genommen"
     * <p>
     * Implizit (oder bei reflexiven Verben auch explizit) hat Phrase
     * eine Person und einen Numerus - Beispiel:
     * "[Ich habe] die Kugel an mich genommen"
     * (nicht *"[Ich habe] die Kugel an sich genommen")
     */
    public String getDescriptionPartizipIIPhrase(final Person person, final Numerus numerus) {
        // FIXME hier kann ein Komma verloren gehen!
        return GermanUtil.joinToString(
                duTextPart.getPraedikat().getPartizipIIPhrase(person, numerus));
    }

    /**
     * Gibt zurück, ob die Partizip-II-Phrase
     * (vgl. {@link #getDescriptionPartizipIIPhrase(Person, Numerus)})
     * am Anfang oder mitten im Satz möglich ist (<code>true</code>) oder nur am Ende
     * (<code>false</code>).
     * <ul>
     * <li>Diese Partizip-II-Phrasen sind am Anfang oder mitten im Satz  möglich: "unten angekommen
     * [bist du erschöpft]". "gut gefüttert [ist der Fisch zufrieden]"
     * <li>Diese Partizip-II-Phrase kann <i>nicht</i>satzwertig verwendet werden: gerufen:
     * "Kommt alle her."
     * </ul>
     */
    public boolean kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden() {
        return duTextPart.getPraedikat()
                .kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden();
    }

    @CheckReturnValue
    private PraedikatDuDescription copy(final PraedikatOhneLeerstellen praedikat) {
        return new PraedikatDuDescription(getStartsNew(), praedikat);
    }

    public PraedikatOhneLeerstellen getPraedikat() {
        return duTextPart.getPraedikat();
    }
}
