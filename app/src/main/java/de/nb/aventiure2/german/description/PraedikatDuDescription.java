package de.nb.aventiure2.german.description;

import androidx.annotation.Nullable;

import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusVerbWohinWoher;
import de.nb.aventiure2.german.praedikat.PraedikatOhneLeerstellen;

/**
 * A description based on a {@link PraedikatOhneLeerstellen} - assuming the player
 * character is the (first) subject. Somehting like "Du gehst in den Wald."
 */
public class PraedikatDuDescription
        extends AbstractDuDescription<PraedikatOhneLeerstellen, PraedikatDuDescription> {
    PraedikatDuDescription(final StructuralElement startsNew,
                           final PraedikatOhneLeerstellen praedikat) {
        super(startsNew, praedikat);
    }

    public PraedikatDuDescription mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusSatz adverbialeAngabe) {
        return copy(duTextPart.mitAdverbialerAngabe(adverbialeAngabe));
    }

    public PraedikatDuDescription mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusVerbAllg adverbialeAngabe) {
        return copy(duTextPart.mitAdverbialerAngabe(adverbialeAngabe));
    }

    public PraedikatDuDescription mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusVerbWohinWoher adverbialeAngabe) {
        return copy(duTextPart.mitAdverbialerAngabe(adverbialeAngabe));
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
        return duTextPart.getPartizipIIPhrase(person, numerus);
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
        return duTextPart.kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden();
    }

    private PraedikatDuDescription copy(final PraedikatOhneLeerstellen praedikat) {
        return new PraedikatDuDescription(getStartsNew(),
                praedikat);
    }

    public PraedikatOhneLeerstellen getPraedikat() {
        return duTextPart;
    }
}
