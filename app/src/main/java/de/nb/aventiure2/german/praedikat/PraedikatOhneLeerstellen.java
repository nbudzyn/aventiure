package de.nb.aventiure2.german.praedikat;

import com.google.common.collect.ImmutableList;

import java.util.Arrays;
import java.util.Collection;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativSkopusSatz;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativVerbAllg;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativWohinWoher;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld;
import de.nb.aventiure2.german.base.Negationspartikelphrase;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.satz.EinzelnerSatz;

import static de.nb.aventiure2.german.base.Negationspartikelphrase.NICHT;

/**
 * Ein Prädikat im Sinne eines Verbs mit allen Ergänzungen und Angaben, jedoch ohne Subjekt, bei
 * dem alle Leerstellen besetzt sind ("mit dem Frosch reden").
 *
 * @see EinzelnerSatz
 */
public interface PraedikatOhneLeerstellen extends Praedikat {
    default PraedikatOhneLeerstellen mitModalpartikeln(
            final Modalpartikel... modalpartikeln) {
        return mitModalpartikeln(Arrays.asList(modalpartikeln));
    }

    PraedikatOhneLeerstellen mitModalpartikeln(Collection<Modalpartikel> modalpartikeln);


    /**
     * Erzeugt aus diesem Prädikat ein Prädikat im Perfekt
     * (z.B. <i>Spannendes berichtet haben</i>,  <i>mit Paul diskutiert haben/i>,
     * <i>geschlafen haben</i>, <i>sich gewaschen haben</i>).
     */
    default PerfektPraedikatOhneLeerstellen perfekt() {
        return new PerfektPraedikatOhneLeerstellen(this);
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

    PraedikatOhneLeerstellen mitAdvAngabe(
            @Nullable IAdvAngabeOderInterrogativSkopusSatz advAngabe);

    PraedikatOhneLeerstellen mitAdvAngabe(
            @Nullable IAdvAngabeOderInterrogativVerbAllg advAngabe);

    PraedikatOhneLeerstellen mitAdvAngabe(
            @Nullable IAdvAngabeOderInterrogativWohinWoher advAngabe);

    /**
     * Fügt dem Prädikat die {@link Negationspartikelphrase} "nicht" hinzu:
     * <ul>
     * <li>Der <i>Geltungsbereich der Negation</i> (was wird logisch negiert?) ist das
     * gesamte Prädikat oder der gesamte Einzel-Satz, der mit dem Prädikat gebildet wird -
     * eventuell mit Ausnahmen gewisser Satzadverbien wie "leider" etc.
     * <li>Der <i>Fokus der Negation</i> (der Aspekt der Aussage, in dem eine
     * Erwartungshaltungshaltung
     * korrigiert werden soll) ist das gesamte Prädikat (ausgenommen die Satzadverbien, die noch
     * nicht einmal zum Geltungsbereich gehören).
     */
    default PraedikatOhneLeerstellen neg() {
        return neg(NICHT);
    }

    /**
     * Fügt dem Prädikat diese {@link Negationspartikelphrase} hinzu.
     * <ul>
     * <li>Der <i>Geltungsbereich der Negation</i> (was wird logisch negiert?) ist das
     * gesamte Prädikat oder der gesamte Einzel-Satz, der mit dem Prädikat gebildet wird -
     * eventuell mit Ausnahmen gewisser Satzadverbien wie "leider" etc.
     * <li>Der <i>Fokus der Negation</i> (der Aspekt der Aussage, in dem eine
     * Erwartungshaltungshaltung
     * korrigiert werden soll) ist das gesamte Prädikat (ausgenommen die Satzadverbien, die noch
     * nicht einmal zum Geltungsbereich gehören).
     * <p>
     * Gibt es bereits eine Negationspartikelphrase, wird dies überschrieben, - das Argument
     * {@code null} überschreibt nicht.
     */
    PraedikatOhneLeerstellen neg(@Nullable
                                         Negationspartikelphrase negationspartikelphrase);

    // FIXME Verneinung: Der Geltungsbereich ist das gesamte Prädikat oder der gesamte 
    //  Einzel-Satz, der
    //  mit dem Prädikat gebildet wird. Der Fokus ist das gesamte Prädikat - vielleicht
    //  ausgenommen Satzadverbien wie "leider" etc.
    //  Dann wird die Negationspartikelphrase an entsprechender Stelle positioniert, ggf. auch
    //  der Satz umgestellt (neues Vorfeld o.Ä., Inhalte in Nebensatz auslagern
    //  o.Ä.).
    //  - Andere Fälle bedenken:
    //  -- "Peter ist (ein) Schuster" - "Ein Schuster ist Peter nicht" / "Peter ist kein Schuster"
    //  -- "Der Mörder war der Gärtner" - "Der Gärtner war der Mörder nicht" / "Der Mörder war
    //  nicht der Gärtner" nicht
    //  -- Achtung bei Satzadverbien:
    //  "Helga liest den Spiegel wahrscheinlich nicht",
    //  "Helga liest *wahrscheinlich* nicht den Spiegel"

    @Nullable
    @CheckReturnValue
    Konstituentenfolge getErstesInterrogativwort();

    @Nullable
    @CheckReturnValue
    Konstituentenfolge getRelativpronomen();

    default EinzelnerSatz alsSatzMitSubjekt(@Nullable final SubstantivischePhrase subjekt) {
        return alsSatzMitSubjekt(null, subjekt);
    }

    default EinzelnerSatz alsSatzMitSubjekt(
            final @Nullable
                    NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld anschlusswort,
            @Nullable final SubstantivischePhrase subjekt) {
        return new EinzelnerSatz(anschlusswort, subjekt, this);
    }

    /**
     * Gibt das Prädikat "in Verbzweitform" zurück - das Verb steht also ganz am Anfang
     * (in einem Verbzweitsatz würde dann noch das Subjekt davor stehen) - für ein
     * Subjekt wie dieses (was Person und Numerus angeht).
     */
    default Konstituentenfolge getVerbzweit(final SubstantivischePhrase subjekt) {
        return getVerbzweit(subjekt.getPerson(), subjekt.getNumerus());
    }

    /**
     * Gibt das Prädikat "in Verbzweitform" zurück - das Verb steht also ganz am Anfang
     * (in einem Verbzweitsatz würde dann noch das Subjekt davor stehen).
     */
    Konstituentenfolge getVerbzweit(Person person, Numerus numerus);

    /**
     * Gibt das Prädikat "in Verbzweitform" zurück - das Verb steht also ganz am Anfang -,
     * wobei dieses Subjekt zusätzlich ins Mittelfeld eingebaut wird.
     * (In einem Verbzweitsatz würde dann noch ein Satzglied vor dem Ganzen stehen, z.B.
     * eine adverbiale Angabe).
     */
    Konstituentenfolge getVerbzweitMitSubjektImMittelfeld(SubstantivischePhrase subjekt);

    /**
     * Gibt das Prädikat "in Verbletztform" zurück - das Verb steht also am Ende,
     * nur noch gefolgt vom Nachfeld.
     */
    Konstituentenfolge getVerbletzt(Person person, Numerus numerus);

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
     * {@link PartizipIIPhrase} zurückgeben, z.B. "unten angekommen und müde geworden".
     * Wenn allerdings die Teile unterschiedliche Hilfsverben verlangen
     * (<i>unten angekommen (sein)</i> und <i>die Kugel genommen (haben)</i>), gibt diese
     * Methode <i>mehrere</i> Partizip-II-Phasen zurück. Der Aufrufer wird diese Phrasen
     * in der Regel separat mit ihrem jeweiligen Hilfsverb verknüpfen müssen
     * (<i>Du bist unten angekommen und hast die Kugel genommen</i>). Es sollte allerdings
     * so sein: Folgen im Ergebnis dieser Methode zwei Partizip-II-Phrasen aufeinander,
     * so verlangen sie unterschiedliche Hilfsverben.
     */
    default ImmutableList<PartizipIIPhrase> getPartizipIIPhrasen(
            final SubstantivischePhrase bezugswort) {
        return getPartizipIIPhrasen(bezugswort.getPerson(), bezugswort.getNumerus());
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
     * Auch bei mehrteiligen Prädikaten soll diese Methode nach Möglichkeit nur eine einzige
     * {@link PartizipIIPhrase} zurückgeben, z.B. "unten angekommen und müde geworden".
     * Wenn allerdings die Teile unterschiedliche Hilfsverben verlangen
     * (<i>unten angekommen (sein)</i> und <i>die Kugel genommen (haben)</i>), gibt diese
     * Methode <i>mehrere</i> Partizip-II-Phasen zurück. Der Aufrufer wird diese Phrasen
     * in der Regel separat mit ihrem jeweiligen Hilfsverb verknüpfen müssen
     * (<i>Du bist unten angekommen und hast die Kugel genommen</i>). Es sollte allerdings
     * so sein: Folgen im Ergebnis dieser Methode zwei Partizip-II-Phrasen aufeinander,
     * so verlangen sie unterschiedliche Hilfsverben.
     */
    ImmutableList<PartizipIIPhrase> getPartizipIIPhrasen(final Person person,
                                                         final Numerus numerus);

    /**
     * Gibt eine Infinitivkonstruktion mit diesem
     * Prädikat zurück ("das Schwert nehmen")
     * <p>
     * Implizit (oder bei reflexiven Verben auch explizit) hat der
     * Infinitiv eine Person und einen Numerus - Beispiel:
     * "[Ich möchte] die Kugel an mich nehmen"
     * (nicht *"[Ich möchte] die Kugel an sich nehmen")
     */
    Konstituentenfolge getInfinitiv(final Person person, final Numerus numerus);

    /**
     * Gibt eine Infinitivkonstruktion mit dem zu-Infinitiv mit diesem
     * Prädikat zurück ("das Schwert zu nehmen")
     * <p>
     * Implizit (oder bei reflexiven Verben auch explizit) hat der
     * zu-Infinitiv eine Person und einen Numerus - Beispiel:
     * "[Ich gedenke,] die Kugel an mich zu nehmen"
     * (nicht *"[Ich gedenke,] die Kugel an sich zu nehmen")
     */
    Konstituentenfolge getZuInfinitiv(final Person person, final Numerus numerus);

    /**
     * Gibt ein "spezielles" Vorfeld zurück, das (bei Sätzen, die ein Vorfeld haben)
     * der Stellung, wo das Subjekt im Vorfeld ist, vorgezogen werden sollte.
     */
    @Nullable
    Konstituente getSpeziellesVorfeldSehrErwuenscht(Person personSubjekt, Numerus numerusSubjekt,
                                                    boolean nachAnschlusswort);

    /**
     * Gibt ein "spezielles" Vorfeld zurück, das (bei Sätzen, die ein Vorfeld haben)
     * optional (um weitere Alternativen zu haben) verwendet werden kann.
     * <p>
     * Wenn {@link #getSpeziellesVorfeldSehrErwuenscht(Person, Numerus, boolean)} einen Wert
     * zurückgibt,
     * so sollte diese Methode etweder einen anderen oder keinen Wert zurückgeben.
     * <p>
     * Generell gibt es gewisse Regeln für das Vorfeld, die bei der Implementierung dieser
     * Methode berücksichtigt werden müssen:
     * <ul>
     * <li> Wenn "es" ein Objekt ist, darf es nicht im Vorfeld stehen.
     * (Eisenberg Der Satz 5.4.2)
     * ("es" ist nicht phrasenbildend, kann also keine Fokuspartikel haben)
     * <li>Auch obligatorisch Reflexsivpronomen sind im Vorfeld unmöglich:
     * *Sich steigern die Verluste <-> Die Verluste steigern sich.
     * <li>Der ethische Dativ ist im Vorfeld verboten:
     * *Mir komm nur nicht zu spät. <-> Komm mir nur nicht zu spät.
     * <li>"Nicht" ("Negationssupplement") ist im Vorfeld sehr, sehr selten.
     * <li>Tendenziell enthält das Vorfeld (unmarkiert)
     * Hintergrund-Informationen, also die Informationen,
     * die bereits bekannt sind und auf die
     * nicht die Aufmerksamkeit gelenkt werden soll.
     * Daher stehen im Vorfeld zumeist das Subjekt oder
     * adverbiale Angaben.
     * Da Prädikatsteile (Objekte, Prädikative) in der Regel rhematisch sind
     * (nicht Thema), sollen sie in der Regel nicht ins Vorfeld.
     * Es gibt zwei Ausnahmen:
     * <ol>
     * <li>Die "Hintergrundsetzung":
     * Der Prädikatsteil soll als neuer Hintergrund für
     * ein (neues), im Satz folgendes Rhema gesetzt werden.
     * Daher sind diese Vorfeldbesetzung mit Nicht-Subjekt
     * mit Negationen im Mittelfeld natürlich:
     * "Den Karl [neues Thema] liebt die Maria aber nicht [Rhema]"
     * "Groß [neues Thema] ist Karl nicht [Rhema]."
     * <li>Die "Hervorhebung":
     * Es gibt mehrere Vorderund-Informationen, von denen
     * eine besonders Akzeptuiert werden soll. Diese Information
     * kann in das Vorfeld gestellt werden ("Hervorhebung").
     * "Hervorhebungen" sind also vor allem möglich, wenn
     * das Prädikats-Mittelfeld
     * - mehrere weitere
     * - oder weitere längere
     * Elemente enthält.
     * <li>Daher sind generell Personalpronomen ohne Fokuspartikel im Vorfeld oft
     * eher unangebracht, wenn es sich um ein Objekt handelt. Selbst wenn
     * das Personalpronomen in einem Präpositionalkasus steht.
     * ?"Dich sieht die Frau überrascht an.", ?"Auf sie wartest du immer noch."
     * </ol>
     * <li> <i>Rhematische</i> prädikative Elemente sind im Vorfeld nur möglich,
     * wenn sie
     * <ol>
     * <li>Antwort auf eine Frage sind ("Dich habe ich gesucht!")
     * <li>oder kontrastiv ("Wir haben eine Katze. Ein Hund
     * kommt uns nicht ins Haus.") - Phrasen (auch Personalpronomen) mit Fokuspartikel
     * sind häufig kontrastiv und daher oft für das Vorfeld geeignet.
     * </ol>
     * </ul>
     */
    @Nullable
    Konstituentenfolge getSpeziellesVorfeldAlsWeitereOption(Person personSubjekt,
                                                            Numerus numerusSubjekt);

    @Nullable
    Konstituentenfolge getNachfeld(Person person, Numerus numerus);
}
