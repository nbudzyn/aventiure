package de.nb.aventiure2.german.praedikat;

import static de.nb.aventiure2.german.base.Negationspartikelphrase.NICHT;

import com.google.common.collect.ImmutableList;

import java.util.Arrays;
import java.util.Collection;

import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.Belebtheit;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativSkopusSatz;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativVerbAllg;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativWohinWoher;
import de.nb.aventiure2.german.base.NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld;
import de.nb.aventiure2.german.base.Negationspartikelphrase;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.PraedRegMerkmale;
import de.nb.aventiure2.german.base.SubstantivischPhrasierbar;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.ITextContext;
import de.nb.aventiure2.german.satz.EinzelnerSemSatz;

/**
 * Ein "semantisches Prädikat" im Sinne eines Verbs mit allen Ergänzungen und Angaben, jedoch
 * ohne Subjekt, bei dem alle semantischen Leerstellen (mit Diskursreferenten) besetzt sind ("mit
 * dem Frosch reden").
 *
 * @see EinzelnerSemSatz
 */
public interface SemPraedikatOhneLeerstellen extends SemPraedikat {
    default SemPraedikatOhneLeerstellen mitModalpartikeln(
            final Modalpartikel... modalpartikeln) {
        return mitModalpartikeln(Arrays.asList(modalpartikeln));
    }

    SemPraedikatOhneLeerstellen mitModalpartikeln(Collection<Modalpartikel> modalpartikeln);

    /**
     * Erzeugt aus diesem Prädikat ein Prädikat im Perfekt
     * (z.B. <i>Spannendes berichtet haben</i>,  <i>mit Paul diskutiert haben/i>,
     * <i>geschlafen haben</i>, <i>sich gewaschen haben</i>).
     */
    default PerfektSemPraedikatOhneLeerstellen perfekt() {
        return new PerfektSemPraedikatOhneLeerstellen(this);
    }

    /**
     * Gibt zurück, ob dieses Prädikat in der Regel ohne Subjekt steht
     * ("Mich friert"), aber optional ein expletives "es" möglich ist
     * ("Es friert mich").
     */
    boolean inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich();

    boolean hauptsatzLaesstSichBeiGleichemSubjektMitNachfolgendemVerbzweitsatzZusammenziehen();

    /**
     * Gibt zurück, ob die Partizip-II-Phrase
     * am Anfang oder mitten im SemSatz möglich ist (<code>true</code>) oder nur am Ende
     * (<code>false</code>).
     * <ul>
     * <li>Diese Partizip-II-Phrasen sind am Anfang oder mitten im SemSatz  möglich: "unten
     * angekommen
     * [bist du erschöpft]". "gut gefüttert [ist der Fisch zufrieden]"
     * <li>Diese Partizip-II-Phrase kann <i>nicht</i>satzwertig verwendet werden: gerufen:
     * "Kommt alle her."
     * </ul>
     */
    boolean kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden();

    /**
     * Gibt zurück, ob dieses Prädikat Satzglieder enthält (nicht nur Verbbestandteile).
     * "läuft los" enthält keine Satzgliederk, "läuft schnell los" oder
     * "hebt die Kugel hoch" hingegen schon.
     */
    boolean umfasstSatzglieder();

    boolean hatAkkusativobjekt();

    /**
     * Gibt zurück, ob durch das Prädikat ein Bezug auf den Nachzustand gegeben ist.
     * Z.B. ist bei "nach Berlin gehen" ein Bezug auf den Nachzustand gegeben (Aktant ist
     * in Berlin) - bei "gehen" jedoch nicht. Auch bei "hinausgehen" oder "weggehen" ist ein
     * Bezug auf den Nachzustand gegeben.
     */
    boolean isBezugAufNachzustandDesAktantenGegeben();

    SemPraedikatOhneLeerstellen mitAdvAngabe(
            @Nullable IAdvAngabeOderInterrogativSkopusSatz advAngabe);

    SemPraedikatOhneLeerstellen mitAdvAngabe(
            @Nullable IAdvAngabeOderInterrogativVerbAllg advAngabe);

    SemPraedikatOhneLeerstellen mitAdvAngabe(
            @Nullable IAdvAngabeOderInterrogativWohinWoher advAngabe);

    /**
     * Fügt dem Prädikat die {@link Negationspartikelphrase} "nicht" hinzu:
     * <ul>
     * <li>Der <i>Geltungsbereich der Negation</i> (was wird logisch negiert?) ist das
     * gesamte Prädikat oder der gesamte Einzel-SemSatz, der mit dem Prädikat gebildet wird -
     * eventuell mit Ausnahmen gewisser Satzadverbien wie "leider" etc.
     * <li>Der <i>Fokus der Negation</i> (der Aspekt der Aussage, in dem eine
     * Erwartungshaltungshaltung
     * korrigiert werden soll) ist das gesamte Prädikat (ausgenommen die Satzadverbien, die noch
     * nicht einmal zum Geltungsbereich gehören).
     */
    default SemPraedikatOhneLeerstellen neg() {
        return neg(NICHT);
    }

    /**
     * Fügt dem Prädikat diese {@link Negationspartikelphrase} hinzu.
     * <ul>
     * <li>Der <i>Geltungsbereich der Negation</i> (was wird logisch negiert?) ist das
     * gesamte Prädikat oder der gesamte Einzel-SemSatz, der mit dem Prädikat gebildet wird -
     * eventuell mit Ausnahmen gewisser Satzadverbien wie "leider" etc.
     * <li>Der <i>Fokus der Negation</i> (der Aspekt der Aussage, in dem eine
     * Erwartungshaltungshaltung
     * korrigiert werden soll) ist das gesamte Prädikat (ausgenommen die Satzadverbien, die noch
     * nicht einmal zum Geltungsbereich gehören).
     * <p>
     * Gibt es bereits eine Negationspartikelphrase, wird dies überschrieben, - das Argument
     * {@code null} überschreibt nicht.
     */
    SemPraedikatOhneLeerstellen neg(@Nullable
                                            Negationspartikelphrase negationspartikelphrase);

    // FIXME Verneinung: Der Geltungsbereich ist das gesamte Prädikat oder der gesamte 
    //  Einzel-SemSatz, der
    //  mit dem Prädikat gebildet wird. Der Fokus ist das gesamte Prädikat - vielleicht
    //  ausgenommen Satzadverbien wie "leider" etc.
    //  Dann wird die Negationspartikelphrase an entsprechender Stelle positioniert, ggf. auch
    //  der SemSatz umgestellt (neues Vorfeld o.Ä., Inhalte in Nebensatz auslagern
    //  o.Ä.).
    //  - Andere Fälle bedenken:
    //  -- "Peter ist (ein) Schuster" - "Ein Schuster ist Peter nicht" / "Peter ist kein Schuster"
    //  -- "Der Mörder war der Gärtner" - "Der Gärtner war der Mörder nicht" / "Der Mörder war
    //  nicht der Gärtner" nicht
    //  -- Achtung bei Satzadverbien:
    //  "Helga liest den Spiegel wahrscheinlich nicht",
    //  "Helga liest *wahrscheinlich* nicht den Spiegel"

    default EinzelnerSemSatz alsSatzMitSubjekt(@Nullable final SubstantivischPhrasierbar subjekt) {
        return alsSatzMitSubjekt(null, subjekt);
    }

    default EinzelnerSemSatz alsSatzMitSubjekt(
            final @Nullable
                    NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld anschlusswort,
            @Nullable final SubstantivischPhrasierbar subjekt) {
        return new EinzelnerSemSatz(anschlusswort, subjekt, this);
    }

    /**
     * Gibt eine Liste finiter Prädikat zurück ("nimmt das Schwert", "geht noch Norden") - für ein
     * Subjekt wie dieses (was z.B. Person und Numerus angeht).
     * <p>
     * Ein finites Prädikat hat eine Person, Numerus etc. - Beispiel:
     * "[Ich] nehme das Schwert an mich" (nicht *"[Ich] nimmt das Schwert an sich")
     * <p>
     * Wenn ein syntaktisches Element erzeugt wird, darf diese Methode nicht mehrfach
     * aufgerufen werden - sofern wichtig ist, dass sich immer dasselbe Ergebnis ergibt.
     */
    default ImmutableList<AbstractFinitesPraedikat> getFinitePraedikate(
            final ITextContext textContext,
            final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld anschlusswort,
            final SubstantivischePhrase subjekt) {
        return getFinitePraedikate(textContext, anschlusswort, subjekt.getPraedRegMerkmale());
    }

    /**
     * Gibt eine Liste finiter Prädikat zurück ("nimmt das Schwert", "geht nach Norden").
     * <p>
     * Ein finites Prädikat hat eine Person, Numerus etc. - Beispiel:
     * "[Ich] nehme das Schwert an mich" (nicht *"[Ich] nimmt das Schwert an sich")
     * <p>
     * Wenn ein syntaktisches Element erzeugt wird, darf diese Methode nicht mehrfach
     * aufgerufen werden - sofern wichtig ist, dass sich immer dasselbe Ergebnis ergibt.
     */
    ImmutableList<AbstractFinitesPraedikat> getFinitePraedikate(
            final ITextContext textContext,
            @Nullable final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld anschlusswort,
            PraedRegMerkmale praedRegMerkmale);

    /**
     * Gibt eine (oder in seltenen Fällen mehrere) unflektierte Phrase(n) mit Partizip II zurück
     * (<i>unten angekommen</i>, <i>die Kugel genommen</i>) oder - wenn für die Perfektbildung
     * nötig - Infinitivphrasen als "Ersatzinfinitive" (<i>[er hat den Stift ]nehmen wollen</i>).
     * <p>
     * Implizit (oder bei reflexiven Verben auch explizit) haben diese Phrasen
     * ein Bezugswort, aus dem sich Person und Numerus ergeben - Beispiel:
     * <i>[Ich habe] die Kugel an mich genommen</i>
     * (nicht <i>[Ich habe] die Kugel an sich genommen</i>)
     */
    ImmutableList<PartizipIIOderErsatzInfinitivPhrase> getPartizipIIOderErsatzInfinitivPhrasen(
            final ITextContext textContext,
            final boolean nachAnschlusswort,
            final PraedRegMerkmale praedRegMerkmale);

    /**
     * Gibt eine (oder in seltenen Fällen mehrere) unflektierte Phrase(n) mit Partizip II zurück:
     * <i>unten angekommen</i>, <i>die Kugel genommen</i>
     * <p>
     * Implizit (oder bei reflexiven Verben auch explizit) hat eine Partizip-II-Phrase
     * ein Bezugswort, aus dem sich Person und Numerus ergeben - Beispiel:
     * <i>[Ich habe] die Kugel an mich genommen</i>
     * (nicht <i>[Ich habe] die Kugel an sich genommen</i>)
     * <p>
     * Auch bei mehrteiligen Prädikaten soll diese Methode nach Möglichkeit nur eine einzige
     * {@link EinfachePartizipIIPhrase} zurückgeben, z.B. "unten angekommen und müde geworden".
     * Wenn allerdings die Teile unterschiedliche Hilfsverben verlangen
     * (<i>unten angekommen (sein)</i> und <i>die Kugel genommen (haben)</i>), gibt diese
     * Methode <i>mehrere</i> Partizip-II-Phasen zurück. Der Aufrufer wird diese Phrasen
     * in der Regel separat mit ihrem jeweiligen Hilfsverb verknüpfen müssen
     * (<i>Du bist unten angekommen und hast die Kugel genommen</i>). Es sollte allerdings
     * so sein: Folgen im Ergebnis dieser Methode zwei Partizip-II-Phrasen aufeinander,
     * so verlangen sie unterschiedliche Hilfsverben.
     */
    // FIXME Letzter Teil des Kommentars nicht mehr aktuell?
    default ImmutableList<PartizipIIPhrase> getPartizipIIPhrasen(
            final ITextContext textContext,
            final boolean nachAnschlusswort,
            final Person person,
            final Numerus numerus,
            final Belebtheit belebtheit) {
        return getPartizipIIPhrasen(textContext, nachAnschlusswort,
                new PraedRegMerkmale(person, numerus, belebtheit));
    }

    /**
     * Gibt eine (oder in seltenen Fällen mehrere) unflektierte Phrase(n) mit Partizip II zurück:
     * <i>unten angekommen</i>, <i>die Kugel genommen</i>
     * <p>
     * Implizit (oder bei reflexiven Verben auch explizit) hat eine Partizip-II-Phrase
     * ein Bezugswort, aus dem sich Person und Numerus ergeben - Beispiel:
     * <i>[Ich habe] die Kugel an mich genommen</i>
     * (nicht <i>[Ich habe] die Kugel an sich genommen</i>)
     */
    default ImmutableList<PartizipIIPhrase> getPartizipIIPhrasen(
            final ITextContext textContext,
            final boolean nachAnschlusswort,
            final SubstantivischePhrase bezugswort) {
        return getPartizipIIPhrasen(textContext, nachAnschlusswort,
                bezugswort.getPraedRegMerkmale());
    }

    /**
     * Gibt eine (oder in seltenen Fällen mehrere) unflektierte Phrase(n) mit Partizip II zurück:
     * <i>unten angekommen</i>, <i>die Kugel genommen</i>
     * <p>
     * Implizit (oder bei reflexiven Verben auch explizit) hat eine Partizip-II-Phrase
     * eine Person und einen Numerus - Beispiel:
     * <i>[Ich habe] die Kugel an mich genommen</i>
     * (nicht <i>[Ich habe] die Kugel an sich genommen</i>)
     * <p>
     * Wenn ein syntaktisches Element erzeugt wird, darf diese Methode nicht mehrfach
     * aufgerufen werden - sofern wichtig ist, dass sich immer dasselbe Ergebnis ergibt.
     */
    ImmutableList<PartizipIIPhrase> getPartizipIIPhrasen(
            final ITextContext textContext,
            final boolean nachAnschlusswort,
            PraedRegMerkmale praedRegMerkmale);


    /**
     * Gibt eine (in seltenen Fällen mehrere) Infinitivkonstruktion zurück ("das Schwert nehmen")
     * <p>
     * Implizit (oder bei reflexiven Verben auch explizit) hat ein
     * Infinitiv eine Person, Numerus etc. passend zu diesem Subjekt - Beispiel:
     * "[Ich möchte] die Kugel an mich nehmen"
     * (nicht *"[Ich möchte] die Kugel an sich nehmen")
     */
    default ImmutableList<Infinitiv> getInfinitiv(
            final ITextContext textContext,
            final boolean nachAnschlusswort,
            final SubstantivischePhrase substantivischePhrase) {
        return getInfinitiv(textContext, nachAnschlusswort,
                substantivischePhrase.getPraedRegMerkmale());
    }

    /**
     * Gibt eine (in seltenen Fällen mehrere) Infinitivkonstruktion zurück ("das Schwert nehmen")
     * <p>
     * Implizit (oder bei reflexiven Verben auch explizit) hat ein
     * Infinitiv eine Person und einen Numerus - Beispiel:
     * "[Ich möchte] die Kugel an mich nehmen"
     * (nicht *"[Ich möchte] die Kugel an sich nehmen")
     */
    ImmutableList<Infinitiv> getInfinitiv(final ITextContext textContext,
                                          boolean nachAnschlusswort,
                                          PraedRegMerkmale praedRegMerkmale);

    /**
     * Gibt eine (in seltenen Fällen mehrere) Infinitivkonstruktion mit "zu" zurück ("das Schwert
     * zu nehmen")
     * <p>
     * Implizit (oder bei reflexiven Verben auch explizit) hat ein
     * zu-Infinitiv Person, Numerus etc., so dass er sich auf diese substantivische
     * Phrase beziehen könnte - Beispiel:
     * "[Ich gedenke,] die Kugel an mich zu nehmen"
     * (nicht *"[Ich gedenke,] die Kugel an sich zu nehmen")
     */
    default ImmutableList<ZuInfinitiv> getZuInfinitiv(
            final ITextContext textContext,
            final boolean nachAnschlusswort,
            final SubstantivischePhrase substantivischePhrase) {
        return getZuInfinitiv(textContext, nachAnschlusswort,
                substantivischePhrase.getPraedRegMerkmale());
    }

    /**
     * Gibt eine (in seltenen Fällen mehrere) Infinitivkonstruktion mit "zu" zurück ("das Schwert
     * zu nehmen")
     * <p>
     * Implizit (oder bei reflexiven Verben auch explizit) hat ein
     * zu-Infinitiv eine Person und einen Numerus - Beispiel:
     * "[Ich gedenke,] die Kugel an mich zu nehmen"
     * (nicht *"[Ich gedenke,] die Kugel an sich zu nehmen")
     * <p>
     * Wenn ein syntaktisches Element erzeugt wird, darf diese Methode nicht mehrfach
     * aufgerufen werden - sofern wichtig ist, dass sich immer dasselbe Ergebnis ergibt.
     */
    ImmutableList<ZuInfinitiv> getZuInfinitiv(
            final ITextContext textContext,
            boolean nachAnschlusswort,
            PraedRegMerkmale praedRegMerkmale);
}
