package de.nb.aventiure2.german.praedikat;

import java.util.Arrays;
import java.util.Collection;

import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.satz.Satz;

/**
 * Ein Prädikat im Sinne eines Verbs mit allen Ergänzungen und Angaben, jedoch ohne Subjekt, bei
 * dem alle Leerstellen besetzt sind ("mit dem Frosch reden").
 *
 * @see de.nb.aventiure2.german.satz.Satz
 */
public interface PraedikatOhneLeerstellen extends Praedikat {
    default PraedikatOhneLeerstellen mitModalpartikeln(final Modalpartikel... modalpartikeln) {
        return mitModalpartikeln(Arrays.asList(modalpartikeln));
    }

    PraedikatOhneLeerstellen mitModalpartikeln(final Collection<Modalpartikel> modalpartikeln);

    default Satz alsSatzMitSubjekt(final SubstantivischePhrase subjekt) {
        return alsSatzMitSubjekt(null, subjekt);
    }

    default Satz alsSatzMitSubjekt(
            final @Nullable String anschlusswort, final SubstantivischePhrase subjekt) {
        return new Satz(anschlusswort, subjekt, this);
    }

    boolean hauptsatzLaesstSichBeiGleichemSubjektMitNachfolgendemVerbzweitsatzZusammenziehen();

    /**
     * Gibt das Prädikat "in Verbzweitform" zurück - das Verb steht also ganz am Anfang
     * (in einem Verbzweitsatz würde dann noch das Subjekt davor stehen) - für ein
     * Subjekt wie dieses (was Person und Numerus angeht).
     */
    default Iterable<Konstituente> getVerbzweit(final SubstantivischePhrase subjekt) {
        return getVerbzweit(subjekt.getPerson(), subjekt.getNumerus());
    }

    /**
     * Gibt das Prädikat "in Verbzweitform" zurück - das Verb steht also ganz am Anfang
     * (in einem Verbzweitsatz würde dann noch das Subjekt davor stehen).
     */
    Iterable<Konstituente> getVerbzweit(Person person, Numerus numerus);

    /**
     * Gibt das Prädikat "in Verbzweitform" zurück - das Verb steht also ganz am Anfang -,
     * wobei dieses Subjekt zusätzlich ins Mittelfeld eingebaut wird.
     * (In einem Verbzweitsatz würde dann noch ein Satzglied vor dem Ganzen stehen, z.B.
     * eine adverbiale Angabe).
     */
    Iterable<Konstituente> getVerbzweitMitSubjektImMittelfeld(SubstantivischePhrase subjekt);

    /**
     * Gibt das Prädikat "in Verbletztform" zurück - das Verb steht also am Ende,
     * nur noch gefolgt vom Nachfeld.
     */
    Iterable<Konstituente> getVerbletzt(Person person, Numerus numerus);

    /**
     * Gibt eine unflektierte Phrase mit Partizip II zurück: "unten angekommen",
     * "die Kugel genommen"
     * <p>
     * Implizit (oder bei reflexiven Verben auch explizit) hat eine Partizip-II-Phrase
     * eine Person und einen Numerus - Beispiel:
     * "[Ich habe] die Kugel an mich genommen"
     * (nicht *"[Ich habe] die Kugel an sich genommen")
     */
    Iterable<Konstituente> getPartizipIIPhrase(final Person person, final Numerus numerus);

    /**
     * Gibt zurück, ob die Partizip-II-Phrase
     * (vgl. {@link #getPartizipIIPhrase(Person, Numerus)})
     * am Anfang oder mitten im Satz möglich ist (<code>true</code>) oder nur am Ende
     * (<code>false</code>).
     * <ul>
     * <li>Diese Partizip-II-Phrasen sind am Anfang oder mitten im Satz  möglich: "unten angekommen
     * [bist du erschöpft]". "gut gefüttert [ist der Fisch zufrieden]"
     * <li>Diese Partizip-II-Phrase kann <i>nicht</i>satzwertig verwendet werden: gerufen:
     * "Kommt alle her."
     * </ul>
     */
    boolean kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden();

    /**
     * Gibt eine Infinitivkonstruktion mit diesem
     * Prädikat zurück ("das Schwert nehmen")
     * <p>
     * Implizit (oder bei reflexiven Verben auch explizit) hat der
     * Infinitiv eine Person und einen Numerus - Beispiel:
     * "[Ich möchte] die Kugel an mich nehmen"
     * (nicht *"[Ich möchte] die Kugel an sich nehmen")
     */
    Iterable<Konstituente> getInfinitiv(final Person person, final Numerus numerus);

    /**
     * Gibt eine Infinitivkonstruktion mit dem zu-Infinitiv mit diesem
     * Prädikat zurück ("das Schwert zu nehmen")
     * <p>
     * Implizit (oder bei reflexiven Verben auch explizit) hat der
     * zu-Infinitiv eine Person und einen Numerus - Beispiel:
     * "[Ich gedenke,] die Kugel an mich zu nehmen"
     * (nicht *"[Ich gedenke,] die Kugel an sich zu nehmen")
     */
    Iterable<Konstituente> getZuInfinitiv(final Person person, final Numerus numerus);

    /**
     * Gibt ein "spezielles" Vorfeld zurück, das (bei Sätzen, die ein Vorfeld haben)
     * der Stellung, wo das Subjekt im Vorfeld ist, vorgezogen werden sollte.
     */
    @Nullable
    Konstituente getSpeziellesVorfeldSehrErwuenscht(Person personSubjekt, Numerus numerusSubjekt);

    /**
     * Gibt ein "spezielles" Vorfeld zurück, das (bei Sätzen, die ein Vorfeld haben)
     * optional (um weitere Alternativen zu haben) verwendet werden kann.
     * <p>
     * Wenn {@link #getSpeziellesVorfeldSehrErwuenscht(Person, Numerus)} einen Wert zurückgibt,
     * so sollte diese Methode etweder einen anderen oder keinen Wert zurückgeben.
     */
    @Nullable
    Konstituente getSpeziellesVorfeldAlsWeitereOption(Person personSubjekt,
                                                      Numerus numerusSubjekt);

    Iterable<Konstituente> getNachfeld(Person person, Numerus numerus);

    /**
     * Gibt zurück, ob dieses Prädikat Satzglieder enthält (nicht nur Verbbestandteile).
     * "läuft los" enthält keine Satzgliederk, "läuft schnell los" oder
     * "hebt die Kugel hoch" hingegen schon.
     */
    boolean umfasstSatzglieder();

    boolean bildetPerfektMitSein();

    boolean hatAkkusativobjekt();

    /**
     * Gibt zurück, ob durch das Prädikat ein Bezug auf den Nachzustand gegeben ist.
     * Z.B. ist bei "nach Berlin gehen" ein Bezug auf den Nachzustand gegeben (Aktant ist
     * in Berlin) - bei "gehen" jedoch nicht. Auch bei "hinausgehen" oder "weggehen" ist ein
     * Bezug auf den Nachzustand gegeben.
     */
    boolean isBezugAufNachzustandDesAktantenGegeben();

    PraedikatOhneLeerstellen mitAdverbialerAngabe(
            @Nullable AdverbialeAngabeSkopusSatz adverbialeAngabe);

    PraedikatOhneLeerstellen mitAdverbialerAngabe(
            @Nullable AdverbialeAngabeSkopusVerbAllg adverbialeAngabe);

    PraedikatOhneLeerstellen mitAdverbialerAngabe(
            @Nullable AdverbialeAngabeSkopusVerbWohinWoher adverbialeAngabe);

    @Nullable
    Konstituente getErstesInterrogativpronomen();
}
