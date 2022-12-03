package de.nb.aventiure2.data.world.syscomp.description;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;
import de.nb.aventiure2.german.base.SubstantivischPhrasierbar;
import de.nb.aventiure2.german.description.ITextContext;
import de.nb.aventiure2.german.description.ImmutableTextContext;

/**
 * Ein Game Object (Person, Gegenstand, eKonzept o.Ä.), das sich als
 * in einem {@link ITextContext} als eine {@link EinzelneSubstantivischePhrase} beschreiben lässt
 * (also ein <i>Diskursreferent</i>) - mit ein paar weiteren Vorgaben für die Umsetzung in die
 * substantivische Phrase.
 * <p>
 * Im Beispiel: "Der Mann steht mitten auf der Straße. Jeder sieht ihn."
 * könnte der Mann ein {@code DescribableGameObject} sein, das sich je nach
 * Text-Kontext als "der Mann / dem Mann / den Mann" und als
 * "er, ihm, ihn" beschrieben wurde. Oft gibt es auch mehrere Möglichkeiten, etwas auszudrücken,
 * und das System trifft eine Auswahl - und erzeugt so Abwechslung.
 */
public class DescribableGameObject implements SubstantivischPhrasierbar,
        // Mixins
        IWorldDescriptionMixin {
    /**
     * Das eigentliche Bezugsobjekt (Diskursreferent).
     */
    private final GameObjectId describableId;

    /**
     * Vorgabe für die Umsetzung in die substantivische Phrase, in wieweit eine
     * <i>Possessiv-Beschreibung</i> gewünscht ist.
     */
    private final PossessivDescriptionVorgabe possessivDescriptionVorgabe;

    /**
     * Vorgabe für die Umsetzung in die substantivische Phrase, ob eine
     * Kurzbeschreibung gewünscht ist, sofern das Game Object bekannt ist.
     */
    private final boolean shortIfKnown;

    /**
     * Fokuspartikel, die für die substantivische Phrase (nach Möglichkeit) verwendet werden soll.
     */
    @Nullable
    private final String fokuspartikel;

    private final World world;

    public DescribableGameObject(
            final World world,
            final GameObjectId describableId,
            final PossessivDescriptionVorgabe possessivDescriptionVorgabe,
            final boolean shortIfKnown) {
        this(world, describableId, possessivDescriptionVorgabe, shortIfKnown, null);
    }

    private DescribableGameObject(
            final World world,
            final GameObjectId describableId,
            final PossessivDescriptionVorgabe possessivDescriptionVorgabe,
            final boolean shortIfKnown,
            @Nullable final String fokuspartikel) {
        this.world = world;
        this.describableId = describableId;
        this.possessivDescriptionVorgabe = possessivDescriptionVorgabe;
        this.shortIfKnown = shortIfKnown;
        this.fokuspartikel = fokuspartikel;
    }

    @Override
    public DescribableGameObject mitFokuspartikel(@Nullable final String fokuspartikel) {
        if (Objects.equals(this.fokuspartikel, fokuspartikel)) {
            return this;
        }

        return new DescribableGameObject(getWorld(), describableId,
                possessivDescriptionVorgabe, shortIfKnown,
                fokuspartikel);
    }

    @Override
    public EinzelneSubstantivischePhrase alsSubstPhrase(final ITextContext textContext) {
        EinzelneSubstantivischePhrase res = getDescriptionWithoutOverriding(textContext);

        if (fokuspartikel != null) {
            res = res.mitFokuspartikel(fokuspartikel);
        }

        return res;
    }

    private EinzelneSubstantivischePhrase getDescriptionWithoutOverriding(
            final ITextContext textContext) {
        // FIXME Hier gibt es ein ernstes Problem: Für aufeinanderfolgede
        //  Aufrufe muss oft immer dieselbe Beschreibung verwendet werden,
        //  damit Substantiv, Personalpronomen etc. zusammenpassen.
        //  getDescription() kann jedoch jedoch über die Zeit wechselnde Beschreibungen
        //  liefern! - Das ist teilweise sogar gewünscht, wenn dasselbe Objekt in Folge mehrfach
        //  beschrieben werden soll.
        //  An dieser Stelle kann allerdings nicht erkannt werden:
        //  Ist Konsistenz nötig (z.B. für ein Relativ- oder Personalpronomen oder
        //  wenn eine bestimmter Ausschnitt genauso zweimal generiert werden muss,
        //  damit er z.B. aus dem Mittelfeld ausgeschnitten werden kann) oder
        //  Abwechslung gewünscht.
        //  - Das hier könnte grob eine Lösung sein: Ein Cache
        //  Textkontext -> Beschreibung. getDescription muss dann (bei gleichem
        //  textContext) deterministisch immer dieselbe Beschreibung liefern.
        //  Das würde allerdings heißen, dass der Aufrufer, wenn er dieselbe
        //  Beschreibung haben möchte, sicherstellen muss, dass er immer denselben
        //  Textkontext übergibt! (Das könnte ein Problem beim Textkontexten sein,
        //  die sich selbst als Seiteneffekt des Auslesens modifizieren...
        //  Andererseits schränkt ja der Narrator sich nur immer weiter selbst ein,
        //  was an sich unproblematisch ist...) Die world könnte auch aus dem
        //  TextContext einen Key ermitteln, der nur die relevanten Informationen
        //  (eigentlich nur den Phorikkandidaten?!) umfasst...
        //  - Oder andere Idee: An sich darf das DescribableGameObject sich
        //  stets neu anders beschreiben. Wenn es allerdings Einschränkungen
        //  gibt (z.B. ein Relativpronomen, dass zu einer bestimmten vorherigen
        //  Beschreibung passen soll), muss der Benutzer eine "ImmutableCopy"
        //  machen (das ist dann letztlich nicht anderes als
        //  eine EinzelneSubstantivePhrase).

        // FIXME Grundidee 1:
        //  - Die Prädikate behalten weiterhin EinzelneSubstantivePhrasen als
        //   Instanzvariablen für Objekte etc.
        //  - Die Prädikate rufen weiterhin direkt auf diesen Instanzvariablen
        //   einzelne Methoden wie nomStr() oder relPron() auf.
        //  - Für jeden Aufruf, der zu unterschiedliche Ergebnissen führen kann
        //   ("variable Methode"), muss der Aufrufer einen ITextContext mit übergeben.
        //  - Jeder ITextContext muss eine spezielle Hash-Methode implementieren.
        //   Diese Methode muss immer dann denselben Hash-Wert zurückgeben, wenn
        //   alle "variablen Methoden", bei jedem Aufruf mit ITextContexten mit diesem
        //   Hash-Wert jeweils dieselben Ergebnisse liefern sollen.
        //  - Jede "variablen Methode" muss gewährleisten, dass
        //   Aufrufe mit ITextContexten mit demselben Hash-Wert (egal ob in Folge
        //   oder nicht) dasselbe Ergebnis liefern. (Gilt das auch bei
        //   ITextContexten verschiedener Typen?) Außerdem müssen alle Rückgabewerte
        //   einer "variablen Methode" für denselben Hashwert miteinander konsistent sein
        //   (z.B. Nominativ und Relativpronomen).
        //  - Manche "variablen Methoden" liefern
        //   vermutlich ohnehin immer dasselbe Ergebnis, unabhängig vom Hash-Wert.
        //  - Andere variable Methoden könnten beim ersten Aufruf mit einem Hash-Wert
        //   ein Objekt erzeugen und für diesen Hash-Wert dauerhaft in einer Map ablegen.
        //   Aus diesem Objekt könnte das Objekt dann alle Methoden-Werte für diesen Hash-Wert
        //   ermitteln (z.B. Nominativ und Relativpronomen).
        //  - SCHWÄCHE: Vermutlich wird auch der Aufrufer diesen ITextContext meist selbst nur
        //  von außen
        //   erhalten. Der äußere Aufrufer wird also sehr sorgfältig darauf achten müssen,
        //   z.B. für ein Objekt und das zugehörige Relativpronomen einen ITextContext mit
        //   gleichem Hash vorzugeben. (Ist das überhaupt sinnvoll? Eigentlich hat sich ja
        //   der Kontext zwischen Nominalphrasenkern und Relativsatzbeginn im Allgemeinen
        //   verändert?!)

        // FIXME Grundidee 2:
        //  - Die Klassen für Objekte etc. werden angepasst: Sie liefern Nominativ,
        //   Relativpronomen etc. nicht mehr direkt. Stattdessen muss ein Aufrufer, der
        //   Nominativ, Relativpronomen etc. haben möchte erst eine Methode auf der
        //   Objektklasse aufrufen, um eine sprachliche Repräsentation festzulegen.
        //   Diese Methode erhält als Parameter einen ITextContext. Ihr Ergebnis
        //   ist ein Immutable. Erst dieses Immutable stellt Nominativ, Relativpronomen
        //   etc. bereit. (Verschiedene Aufrufe auf der Objekt-Klasse selbst, auch für
        //   gleiche oder ähnliche ITextContexts, können ganz unterschiedliche
        //   Immutables ergeben).

        return anaph(textContext, describableId, possessivDescriptionVorgabe, shortIfKnown);
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final DescribableGameObject that = (DescribableGameObject) o;
        return shortIfKnown == that.shortIfKnown && describableId.equals(that.describableId)
                && possessivDescriptionVorgabe == that.possessivDescriptionVorgabe && Objects
                .equals(fokuspartikel, that.fokuspartikel) && world.equals(that.world);
    }

    @Override
    public int hashCode() {
        return Objects
                .hash(describableId, possessivDescriptionVorgabe, shortIfKnown, fokuspartikel,
                        world);
    }

    @NonNull
    @Override
    public String toString() {
        return alsSubstPhrase(ImmutableTextContext.EMPTY).nomStr() + " (" + describableId + ")";
    }
}
