package de.nb.aventiure2.german.praedikat;

import java.util.Collection;

import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.satz.Satz;

import static java.util.Arrays.asList;

/**
 * Ein Prädikat im Sinne eines Verbs mit allen Ergänzungen und Angaben, jedoch ohne Subjekt, bei
 * dem alle Leerstellen besetzt sind ("mit dem Frosch reden").
 *
 * @see de.nb.aventiure2.german.satz.Satz
 */
public interface PraedikatOhneLeerstellen extends Praedikat, AbstractDuTextPart {
    default Satz alsSatzMitSubjekt(final SubstantivischePhrase subjekt) {
        return new Satz(subjekt, this);
    }

    // TODO Modalpartikeln sollten zu einem
    //  neuen AbstractPraedikat führen, dass man dann auch speichern
    //  und weiterreichen kann!
    @Override
    default String getDuHauptsatz() {
        return getDuHauptsatz(new Modalpartikel[0]);
    }

    /**
     * Gibt einen Satz zurück mit diesem Prädikat
     * ("Du nimmst den Ast")
     * und ggf. diesen Modalpartikeln, die sich <i>auf das gesamte
     * Prädikat beziehen</i>
     * ("Du nimmst den Ast besser doch")
     */
    default String getDuHauptsatz(final Modalpartikel... modalpartikeln) {
        return getDuHauptsatz(asList(modalpartikeln));
    }

    default String getDuHauptsatz(final Collection<Modalpartikel> modalpartikeln) {
        return "Du " + getDuSatzanschlussOhneSubjekt(modalpartikeln);
    }

    /**
     * Gibt einen Satz zurück mit diesem Prädikat, bei dem das Subjekt, das im Vorfeld
     * stünde, eingespart ist ("nimmst den Ast")
     */
    @Override
    default String getDuSatzanschlussOhneSubjekt() {
        return getDuSatzanschlussOhneSubjekt(new Modalpartikel[0]);
    }

    /**
     * Gibt einen Satz zurück mit diesem Prädikat, bei dem das Subjekt, das im Vorfeld
     * stünde, eingespart ist ("nimmst den Ast")
     */
    default String getDuSatzanschlussOhneSubjekt(final Modalpartikel... modalpartikeln) {
        return getDuSatzanschlussOhneSubjekt(asList(modalpartikeln)
        );
    }

    /**
     * Gibt einen Satz zurück mit diesem Prädikat, bei dem das Subjekt, das im Vorfeld
     * stünde, eingespart ist ("nimmst den Ast"), sowie ggf. diesen
     * Modalpartikeln ("nimmst den Ast eben doch").
     */
    String getDuSatzanschlussOhneSubjekt(final Collection<Modalpartikel> modalpartikeln);

    boolean duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen();

    /**
     * Gibt das Prädikat "in Verbletztform" zurück - das Verb steht also am Ende,
     * nur noch gefolgt vom Nachfeld.
     */
    String getVerbletzt(Person person, Numerus numerus);

    /**
     * Gibt eine unflektierte Phrase mit Partizip II zurück: "unten angekommen",
     * "die Kugel genommen"
     * <p>
     * Implizit (oder bei reflexiven Verben auch explizit) hat eine Partizip-II-Phrase
     * eine Person und einen Numerus - Beispiel:
     * "[Ich habe] die Kugel an mich genommen"
     * (nicht *"[Ich habe] die Kugel an sich genommen")
     */
    String getPartizipIIPhrase(final Person person, final Numerus numerus);

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
    String getInfinitiv(final Person person, final Numerus numerus);

    /**
     * Gibt eine Infinitivkonstruktion mit dem zu-Infinitiv mit diesem
     * Prädikat zurück ("das Schwert zu nehmen")
     * <p>
     * Implizit (oder bei reflexiven Verben auch explizit) hat der
     * zu-Infinitiv eine Person und einen Numerus - Beispiel:
     * "[Ich gedenke,] die Kugel an mich zu nehmen"
     * (nicht *"[Ich gedenke,] die Kugel an sich zu nehmen")
     */
    String getZuInfinitiv(final Person person, final Numerus numerus);

    @Nullable
    String getSpeziellesVorfeld();

    @Nullable
    String getNachfeld(Person person, Numerus numerus);

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
    String getErstesInterrogativpronomenAlsString();
}
