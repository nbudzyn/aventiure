package de.nb.aventiure2.data.world.syscomp.description;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.german.base.Belebtheit;
import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;
import de.nb.aventiure2.german.base.IArtikelworttypOderVorangestelltesGenitivattribut;
import de.nb.aventiure2.german.base.IBezugsobjekt;
import de.nb.aventiure2.german.base.Negationspartikelphrase;
import de.nb.aventiure2.german.base.NumerusGenus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.base.Reflexivpronomen;
import de.nb.aventiure2.german.base.Relativpronomen;
import de.nb.aventiure2.german.description.EmptyTextContext;
import de.nb.aventiure2.german.description.ITextContext;

/**
 * Eine Game Object (Person, Gegenstand, eKonzept o.Ä.), das sich als
 * in einem {@link ITextContext} als eine {@link EinzelneSubstantivischePhrase} beziehen
 * beschreiben lässt (also ein <i>Diskursreferent</i>).
 * <p>
 * Im Beispiel: "Der Mann steht mitten auf der Straße. Jeder sieht ihn."
 * könnte der Mann eine {@code DescribableGameObject} sein, das sich je nach
 * Text-Kontext als "der Mann / dem Mann / den Mann" und als
 * "er, ihm, ihn" beschrieben wurde.
 */
public class DescribableGameObject implements EinzelneSubstantivischePhrase,
        // Mixins
        IWorldDescriptionMixin {
    /**
     * Das eigentliche Bezugsobjekt (Diskursreferent).
     */
    private final GameObjectId describableId;
    private final boolean moeglichstAnapher;
    private final PossessivDescriptionVorgabe descPossessivDescriptionVorgabe;
    private final boolean descShortIfKnown;
    @Nullable
    private final String fokuspartikelOverride;
    @Nullable
    private final Negationspartikelphrase negationspartikelphraseOverride;
    private final boolean moeglichstNegativIndefiniteWoerterVerwendenOverride;

    // FIXME: Text-Kontext muss jeder Methode einzeln übergeben werden!!
    private final ITextContext textContext = EmptyTextContext.INSTANCE;

    private final World world;

    public DescribableGameObject(
            final World world,
            final GameObjectId describableId,
            final boolean moeglichstAnapher,
            final PossessivDescriptionVorgabe descPossessivDescriptionVorgabe,
            final boolean descShortIfKnown) {
        this(world, describableId, moeglichstAnapher,
                descPossessivDescriptionVorgabe, descShortIfKnown,
                null, null, true);
    }

    private DescribableGameObject(
            final World world,
            final GameObjectId describableId,
            final boolean moeglichstAnapher,
            final PossessivDescriptionVorgabe descPossessivDescriptionVorgabe,
            final boolean descShortIfKnown,
            final @Nullable String fokuspartikelOverride,
            final @Nullable Negationspartikelphrase negationspartikelphraseOverride,
            final boolean moeglichstNegativIndefiniteWoerterVerwendenOverride) {
        this.world = world;
        this.describableId = describableId;
        this.moeglichstAnapher = moeglichstAnapher;
        this.descPossessivDescriptionVorgabe = descPossessivDescriptionVorgabe;
        this.descShortIfKnown = descShortIfKnown;
        this.fokuspartikelOverride = fokuspartikelOverride;
        this.negationspartikelphraseOverride = negationspartikelphraseOverride;
        this.moeglichstNegativIndefiniteWoerterVerwendenOverride =
                moeglichstNegativIndefiniteWoerterVerwendenOverride;
    }


    @Override
    public String nomStr() {
        return getDescription().nomStr();
    }

    @Override
    public String datStr() {
        return getDescription().datStr();
    }

    @Override
    public String akkStr() {
        return getDescription().akkStr();
    }

    @Nullable
    @Override
    public String getFokuspartikel() {
        return getDescription().getFokuspartikel();
    }

    @Nullable
    @Override
    public Negationspartikelphrase getNegationspartikelphrase() {
        // Vielleicht etwas wie "die gar nicht unauffällige Frau"??
        return getDescription().getNegationspartikelphrase();
    }

    @Override
    public Belebtheit getBelebtheit() {
        return getDescription().getBelebtheit();
    }

    @Nullable
    @Override
    public IBezugsobjekt getBezugsobjekt() {
        return describableId;
    }

    @Override
    public boolean isUnbetontesPronomen() {
        return getDescription().isUnbetontesPronomen();
    }

    @Override
    public DescribableGameObject ohneNegationspartikelphrase() {
        if (negationspartikelphraseOverride == null) {
            // Theoretisch könnte der Describer hier immer noch eine Negation
            // vorsehen - aber was will man machen!
            return this;
        }

        return new DescribableGameObject(world, describableId,
                moeglichstAnapher,
                descPossessivDescriptionVorgabe,
                descShortIfKnown,
                fokuspartikelOverride, null, true);
    }

    @Override
    public DescribableGameObject neg(final Negationspartikelphrase negationspartikelphrase,
                                     final boolean moeglichstNegativIndefiniteWoerterVerwenden) {
        return new DescribableGameObject(world, describableId,
                moeglichstAnapher,
                descPossessivDescriptionVorgabe,
                descShortIfKnown,
                fokuspartikelOverride,
                negationspartikelphrase, moeglichstNegativIndefiniteWoerterVerwenden);
    }

    @Override
    public DescribableGameObject ohneFokuspartikel() {
        if (fokuspartikelOverride == null) {
            // Theoretisch könnte der Describer hier immer noch eine Fokuspartikel
            // vorsehen - aber was will man machen!
            return this;
        }

        return new DescribableGameObject(world, describableId,
                moeglichstAnapher,
                descPossessivDescriptionVorgabe,
                descShortIfKnown,
                null,
                negationspartikelphraseOverride,
                moeglichstNegativIndefiniteWoerterVerwendenOverride);
    }

    @Override
    public DescribableGameObject mitFokuspartikel(@Nullable final String fokuspartikel) {
        return new DescribableGameObject(world, describableId,
                moeglichstAnapher,
                descPossessivDescriptionVorgabe,
                descShortIfKnown,
                fokuspartikel,
                negationspartikelphraseOverride,
                moeglichstNegativIndefiniteWoerterVerwendenOverride);
    }

    @Override
    public boolean erlaubtVerschmelzungVonPraepositionMitArtikel() {
        return getDescription().erlaubtVerschmelzungVonPraepositionMitArtikel();
    }

    @Override
    public String artikellosDatStr() {
        return getDescription().artikellosDatStr();
    }

    @Override
    public String artikellosAkkStr() {
        return getDescription().artikellosAkkStr();
    }

    @Override
    public Personalpronomen persPron() {
        return getDescription().persPron();
    }

    @Override
    public Reflexivpronomen reflPron() {
        return getDescription().reflPron();
    }

    @Override
    public IArtikelworttypOderVorangestelltesGenitivattribut possArt() {
        return getDescription().possArt();
    }

    @Override
    public Relativpronomen relPron() {
        return getDescription().relPron();
    }

    @Override
    public NumerusGenus getNumerusGenus() {
        return getDescription().getNumerusGenus();
    }

    @Override
    public Person getPerson() {
        return getDescription().getPerson();
    }

    private EinzelneSubstantivischePhrase getDescription() {
        EinzelneSubstantivischePhrase res =
                getDescriptionWithoutOverriding();

        if (fokuspartikelOverride != null) {
            res = res.mitFokuspartikel(fokuspartikelOverride);
        }

        if (negationspartikelphraseOverride != null) {
            res = res.neg(negationspartikelphraseOverride,
                    moeglichstNegativIndefiniteWoerterVerwendenOverride);
        }

        return res;
    }

    private EinzelneSubstantivischePhrase getDescriptionWithoutOverriding() {
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
        //   Diese Methode erhält als Parameter eine ITextContext. Ihr Ergebnis
        //   ist ein Immutable. Erst dieses Immutable stellt Nominativ, Relativpronomen
        //   etc. bereit. (Verschiedene Aufrufe auf der Objekt-Klasse selbst, auch für
        //   gleiche oder ähnliche ITextContexts, können ganz unterschiedliche
        //   Immutables ergeben).

        if (moeglichstAnapher) {
            return anaph(textContext, describableId, descPossessivDescriptionVorgabe,
                    descShortIfKnown);
        }

        return getDescription(textContext, describableId, descPossessivDescriptionVorgabe,
                descShortIfKnown);
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
        return moeglichstAnapher == that.moeglichstAnapher
                && descShortIfKnown == that.descShortIfKnown
                && moeglichstNegativIndefiniteWoerterVerwendenOverride
                == that.moeglichstNegativIndefiniteWoerterVerwendenOverride && describableId
                .equals(that.describableId)
                && descPossessivDescriptionVorgabe == that.descPossessivDescriptionVorgabe
                && Objects.equals(fokuspartikelOverride, that.fokuspartikelOverride)
                && Objects
                .equals(negationspartikelphraseOverride, that.negationspartikelphraseOverride)
                && textContext.equals(that.textContext) && world.equals(that.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(describableId, moeglichstAnapher, descPossessivDescriptionVorgabe,
                descShortIfKnown, fokuspartikelOverride, negationspartikelphraseOverride,
                moeglichstNegativIndefiniteWoerterVerwendenOverride, world);
    }

    @NonNull
    @Override
    public String toString() {
        return nomStr() + " (" + describableId + ")";
    }
}
