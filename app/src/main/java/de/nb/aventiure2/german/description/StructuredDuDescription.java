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
import de.nb.aventiure2.german.satz.Satz;

import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.Person.P2;

/**
 * A description based on a structured data structure: A {@link de.nb.aventiure2.german.satz.Satz}.
 * We are assuming the player character is the subject. Somehting like "Du gehst in den Wald."
 */
public class StructuredDuDescription
        extends AbstractDuDescription<StructuredDuTextPart, StructuredDuDescription> {
    StructuredDuDescription(final StructuralElement startsNew,
                            final PraedikatOhneLeerstellen praedikat) {
        this(startsNew, praedikat.alsSatzMitSubjekt(Personalpronomen.get(P2,
                // Wir behaupten hier, der Adressat wäre männlich.
                // Es ist die Verantwortung des Aufrufers, keine
                // Sätze mit Konstruktionen wie "Du, der du" zu erzeugen, die
                // weibliche Adressaten ("du, die du") ausschließen.
                M)));
    }

    private StructuredDuDescription(final StructuralElement startsNew,
                                    final Satz satz) {
        super(startsNew, new StructuredDuTextPart(satz),
                guessWoertlicheRedeNochOffen(satz),
                guessKommaStehtAus(satz));
    }

    private static boolean guessWoertlicheRedeNochOffen(final Satz satz) {
        // FIXME Hier gibt es ein Problem: Ob die wörtliche Rede noch offen ist, hängt von der
        //  konkreten Realisierung des Prädikats ab (was steht im Vorfeld / Nachfeld etc.).
        //  Dies hier ist nur eine grobe Richtschnur.
        return Konstituente.woertlicheRedeNochOffen(satz.getVerbzweitsatzStandard());
    }

    private static boolean guessKommaStehtAus(final Satz satz) {
        // FIXME Hier gibt es ein Problem: Ob ein Komma aussteht, hängt von der
        //  konkreten Realisierung des Prädikats ab (was steht im Vorfeld / Nachfeld etc.).
        //  Dies hier ist nur eine grobe Richtschnur.
        return Konstituente.kommaStehtAus(satz.getVerbzweitsatzStandard());
    }

    @CheckReturnValue
    public StructuredDuDescription mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusSatz adverbialeAngabe) {
        return copy(getPraedikat().mitAdverbialerAngabe(adverbialeAngabe));
    }

    @CheckReturnValue
    public StructuredDuDescription mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusVerbAllg adverbialeAngabe) {
        return copy(getPraedikat().mitAdverbialerAngabe(adverbialeAngabe));
    }

    @CheckReturnValue
    public StructuredDuDescription mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusVerbWohinWoher adverbialeAngabe) {
        return copy(getPraedikat().mitAdverbialerAngabe(adverbialeAngabe));
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
    public String getDescriptionPartizipIIPhrase(final Person person,
                                                 final Numerus numerus) {
        return GermanUtil.joinToString(
                getPraedikat().getPartizipIIPhrase(person, numerus));
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
        return getPraedikat()
                .kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden();
    }

    @CheckReturnValue
    private StructuredDuDescription copy(final PraedikatOhneLeerstellen praedikat) {
        return new StructuredDuDescription(getStartsNew(), praedikat);
    }

    public PraedikatOhneLeerstellen getPraedikat() {
        return duTextPart.getPraedikat();
    }
}
