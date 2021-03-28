package de.nb.aventiure2.german.adjektiv;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativSkopusSatz;
import de.nb.aventiure2.german.base.Kasus;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.NumerusGenus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.Praedikativum;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.satz.Satz;

import static com.google.common.collect.ImmutableList.toImmutableList;

/**
 * Eine Adjektivphrase, bei der alle geforderten Ergänzungen gesetzt sind:
 * <ul>
 * <li>glücklich
 * <li>glücklich, dich zu sehen
 * </ul>
 */
public interface AdjPhrOhneLeerstellen extends Adjektivphrase, Praedikativum {
    static ImmutableList<AdvAngabeSkopusVerbAllg> toAdvAngabenSkopusVerbAllg(
            final SubstantivischePhrase subjekt,
            final Collection<AdjPhrOhneLeerstellen> adjektivPhrasen) {
        return adjektivPhrasen.stream()
                .filter(ap -> ap.isGeeignetAlsAdvAngabe(subjekt))
                .map(AdjPhrOhneLeerstellen::alsAdvAngabeSkopusVerbAllg)
                .collect(toImmutableList());
    }

    default boolean isGeeignetAlsAdvAngabe(final SubstantivischePhrase subjekt) {
        // "Sie schaut dich überrascht an.", aber nicht
        // *"Sie schaut dich überrascht an, dich zu sehen".
        return getPraedikativAnteilKandidatFuerNachfeld(
                subjekt.getPerson(),
                subjekt.getNumerus()) == null;
    }

    default AdjPhrOhneLeerstellen mitGraduativerAngabe(@Nullable final String graduativeAngabe) {
        return mitGraduativerAngabe(
                graduativeAngabe != null ? new GraduativeAngabe(graduativeAngabe) : null);
    }

    AdjPhrOhneLeerstellen mitGraduativerAngabe(@Nullable GraduativeAngabe graduativeAngabe);

    AdjPhrOhneLeerstellen mitAdvAngabe(@Nullable IAdvAngabeOderInterrogativSkopusSatz advAngabe);

    /**
     * Gibt den Anteil dieser Adjektivphrase zurück, der bei attributiver Verwendung
     * dem Nomen <i>als Adjektivattribut vorangestellt wird</i> - oder {@code null}, wenn bei
     * attributivem Gebrauch nichts vorangestellt wird.
     * <p>
     * Beispiele:
     * <ul>
     * <li>"(der )blaue( Himmel)"
     * <li>"(dem )blauen( Himmel)"
     * <li>"(ein )dunkler( Himmel)"
     * <li>"(der )sehr dunkle( Himmel)"
     * <li>"blauer( Himmel)"
     * <li>"rosa( Elefanten)"
     * <li>"rosa und grüne( Elefanten)"
     * <li>"(die )junge (Frau des Herzogs, die dich überrascht hat, gespannt, ob du etwas zu
     * berichten hast[,])"
     * <li>{@code null} bei "die Frau, gespannt, ob du etwas zu berichten hast[,]"
     * <li>{@code null} "Du hilfst der Frau des Herzogs, die mit dem Tag zufrieden ist"
     * </ul>
     * <p>
     * Bei attributiver Verwendung wird die Adjektivphrase in drei Bestandteile zerlegt:
     * <ol>
     * <li>ein Adjektivattribut
     * <li>ein Relativsatz, siehe {@link #getAttributivAnteilRelativsatz()}
     * <li>ein lockerer Nachtrag, siehe {@link #getAttributivAnteilLockererNachtrag()}
     * </ol>
     */
    @Nullable
    String getAttributivAnteilAdjektivattribut(NumerusGenus numerusGenus, Kasus kasus,
                                               boolean artikelwortTraegtKasusendung);

    /**
     * Gibt den Anteil dieser Adjektivphrase zurück, der bei attributiver Verwendung
     * dem Nomen <i>als Relativsatz nachgestellt wird</i> - oder {@code null}, wenn bei
     * attributivem Gebrauch kein Relativsatz nachgestellt werden muss. (Vgl. Duden 2006 Absatz
     * 468).
     * <p>
     * Beispiele:
     * <ul>
     * <li>{@code null} bei "der blaue Himmel" - das ist der Regelfall
     * <li>"(Du hilfst der Frau des Herzogs), die mit dem Tag zufrieden ist[,]"
     * <li>{@code null} bei "die junge Frau des Herzogs, gespannt, ob du etwas zu
     * berichten hast[,]"
     * </ul>
     * <p>
     * In der Regel werden Lockere Nachträge (vgl. {@link #getAttributivAnteilLockererNachtrag()})
     * bevorzugt. Sie können aber missverständlich (oder sogar falsch) sein, wenn die
     * Nominalphrase nicht im Nominativ steht: Vgl. den Unterschied zwischen
     * "Du hilfst der Frau des Herzogs, mit dem Tag zufrieden" (du bist mit den Tag zufrieden) und
     * "Du hilfst der Frau des Herzogs, die mit dem Tag zufrieden ist" (sie sit mit dem Tag
     * zufrieden).
     *
     * @see #getAttributivAnteilAdjektivattribut(NumerusGenus, Kasus, boolean)
     * @see #getAttributivAnteilLockererNachtrag()
     */
    @Nullable
    Satz getAttributivAnteilRelativsatz();

    /**
     * Gibt den Anteil dieser Adjektivphrase zurück, der bei attibutiver Verwendung
     * dem Nomen <i>als lockerer Nachtrag nachgestellt wird</i> - oder {@code null}, wenn bei
     * attributivem Gebrauch kein lockerer Nachtrag nachgestellt werden muss.
     * (Vgl. Duden 2006 Absatz 468).
     * <p>
     * Beispiele:
     * <ul>
     * <li>{@code null} bei "der blaue Himmel" - das ist der Regelfall
     * <li>auch {@code null} bei "Du hilfst der Frau des Herzogs, die mit dem Tag zufrieden ist"
     * <li>"(die junge Frau des Herzogs, die dich überrascht hat), gespannt, ob du etwas zu
     * berichten hast[,]"
     * <li>"(die Frau), zufrieden, dich zu sehen, und gespannt, ob du etwas zu berichten hast[,]"
     * </ul>
     *
     * @see #getAttributivAnteilAdjektivattribut(NumerusGenus, Kasus, boolean)
     * @see #getAttributivAnteilRelativsatz()
     */
    @Nullable
    AdjPhrOhneLeerstellen getAttributivAnteilLockererNachtrag();


    /**
     * Gibt die prädikative Form zurück: "hoch", "glücklich, dich zu sehen",
     * "glücklich, sich erheben zu dürfen"
     */
    @Override
    default Konstituentenfolge getPraedikativ(final Person person, final Numerus numerus) {
        return getPraedikativOderAdverbial(person, numerus);
    }

    /**
     * Gibt die prädikative oder adverbiale Form zurück: "hoch", "glücklich, dich zu sehen",
     * "glücklich, sich erheben zu dürfen"
     */
    Konstituentenfolge getPraedikativOderAdverbial(
            final Person personSubjekt, final Numerus numerusSubjekt);

    default AdvAngabeSkopusSatz alsAdvAngabeSkopusSatz() {
        return new AdvAngabeSkopusSatz(this);
    }

    default AdvAngabeSkopusVerbAllg alsAdvAngabeSkopusVerbAllg() {
        return new AdvAngabeSkopusVerbAllg(this);
    }

    /**
     * Gibt zurück, ob die Adjektivphrase eine zu-Infinitivphrase,
     * einen Angabensatz oder einen Ergänzungssatz (z.B. eine indirekte Frage) enthält.
     * <p>
     * Solche Adjektivphrasen können / sollen nicht im Mittelfeld auftreten.
     */
    boolean enthaeltZuInfinitivOderAngabensatzOderErgaenzungssatz();
}