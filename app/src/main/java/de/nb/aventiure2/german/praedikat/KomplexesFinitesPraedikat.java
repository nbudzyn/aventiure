package de.nb.aventiure2.german.praedikat;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToKonstituentenfolge;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

/**
 * Ein Prädikat, dessen Verbalkomplex nicht nur aus einem finiten Verb besteht
 * (sondern z.B. aus weiteren Modal- oder Hilfsverben), und aus dem man
 * z.B. Verbzweitsätze oder Verbletztsätze erzeugen kann.
 * <p/>
 * Aus diesem Prädikat kann man Verzweitsätze erzeugen wie
 * <ul>
 * <li>"[du ]möchtest Spannendes berichten" (Modalverb)
 * <li>"[du ]wirst den Weg gelaufen sein"
 * <li>"[du ]wirst den Weg laufen wollen" (mehrfach geschachtelt)
 * <li>"[du ]bist den Weg gelaufen" (Partizip II)
 * <li>"[du ]hast Spannendes berichtet"
 * <li>"[du ]hast den Schaden gewollt" (Partizip II, kein Ersatzinfinitiv)
 * <li>"[du ]hast Spannendes berichten wollen" ("wollen" als Ersatzinfinitiv)
 * <li>"[er ]möchte seine Geliebte in die Arme schließen" (Modalverb und Präfix)
 * <li>"[er ]hat eine Schlange gesehen und sich sehr erschreckt" (Reihung von Partizipien)
 * <li>"[es ]scheint zu schlafen und keine Gefahr darzustellen" (Reihung von Inifitiven mit zu)
 * <li>"[er ]hat laufen und sich abreagieren wollen" (Reihung von reinen Infinitiven)
 * </ul>
 * Aus diesem Prädikat kann man Verbletztsätze erzeugen wie
 * <ul>
 * <li>"[du ]Spannendes berichten möchtest" (Modalverb)
 * <li>"[du ]den Weg gelaufen sein wirst"
 * <li>"[du ]den Weg laufen wollen wirst" (mehrfach geschachtelt ohne Oberfeld)
 * <li>"[du ]den Weg gelaufen bist" (Partizip II)
 * <li>"[du ]Spannendes berichtet hast" ("hast" nicht im Oberfeld)
 * <li>"[du ]den Schaden gewollt hast" (Partizip II, kein Ersatzinfinitiv)
 * <li>"[du ]Spannendes hast berichten wollen" ("wollen" als Ersatzinfinitiv, "hast" im Oberfeld)
 * <li>"[er ]seine Geliebte in die Arme schließen möchte" (Modalverb und Präfix)
 * <li>"[er ]eine Schlange gesehen und sich sehr erschreckt hat" (Reihung von Partizipien)
 * <li>"[es ]zu schlafen und keine Gefahr darzustellen scheint" (Reihung von Inifitiven mit zu)
 * <li>"[er ]hat laufen und sich abreagieren wollen" (Reihung von reinen Infinitiven, "hat" im
 * Oberfeld)
 * </ul>
 */
@Immutable
public class KomplexesFinitesPraedikat extends AbstractFinitesPraedikat {
    /**
     * Die Infinitive, Partizipien etc., die als nächste Ebene eingeschachtelt sind.
     */
    private final ImmutableList<IInfinitesPraedikat> children;

    KomplexesFinitesPraedikat(final String finiteVerbformOhnePartikel,
                              @Nullable final String partikel,
                              final IInfinitesPraedikat children) {
        this(finiteVerbformOhnePartikel, partikel, ImmutableList.of(children));
    }

    KomplexesFinitesPraedikat(
            final String finiteVerbformOhnePartikel,
            @Nullable final String partikel,
            final Collection<? extends IInfinitesPraedikat> children) {
        super(finiteVerbformOhnePartikel, partikel);

        checkArgument(!children.isEmpty(), "Keine children");

        this.children = ImmutableList.copyOf(children);
    }

    @Override
    public KomplexesFinitesPraedikat mitKonnektor(
            @Nullable final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld konnektor) {
        return mapFirst(child -> child.mitKonnektor(konnektor));
    }

    @Override
    public KomplexesFinitesPraedikat ohneKonnektor() {
        return mapFirst(IInfinitesPraedikat::ohneKonnektor);
    }

    private KomplexesFinitesPraedikat mapFirst(
            final UnaryOperator<IInfinitesPraedikat> operator) {
        return new KomplexesFinitesPraedikat(
                getFiniteVerbformOhnePartikel(),
                getPartikel(),
                ImmutableList.<IInfinitesPraedikat>builder()
                        .add(operator.apply(children.get(0)))
                        .addAll(children
                                .subList(1, children.size()))
                        .build());
    }

    @Nullable
    @Override
    public NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld getKonnektor() {
        return getMapFirst(IInfinitesPraedikat::getKonnektor);
    }

    @Nullable
    @Override
    public Vorfeld getSpeziellesVorfeldSehrErwuenscht() {
        return getMapFirst(IInfinitesPraedikat::getSpeziellesVorfeldSehrErwuenscht);
    }

    @Nullable
    @Override
    public Vorfeld getSpeziellesVorfeldAlsWeitereOption() {
        return getMapFirst(IInfinitesPraedikat::getSpeziellesVorfeldAlsWeitereOption);
    }

    /**
     * Gibt das Prädikat "in Verbzweitform" zurück - das Verb steht also ganz am Anfang -,
     * wobei dieses Subjekt zusätzlich ins Mittelfeld eingebaut wird.
     * (In einem Verbzweitsatz würde dann noch ein Satzglied vor dem Ganzen stehen, z.B.
     * eine adverbiale Angabe).
     * <p/>
     * Liefert Ergebnisse wie
     * <ul>
     * <li>"möchtest du Spannendes berichten" (Modalverb)
     * <li>"wirst du den Weg gelaufen sein"
     * <li>"wirst du den Weg laufen wollen" (mehrfach geschachtelt)
     * <li>"bist du den Weg gelaufen" (Partizip II)
     * <li>"hast du Spannendes berichtet"
     * <li>"hast du den Schaden gewollt" (Partizip II, kein Ersatzinfinitiv)
     * <li>"hast du Spannendes berichten wollen" ("wollen" als Ersatzinfinitiv)
     * <li>"möchte er seine Geliebte in die Arme schließen" (Modalverb und Präfix)
     * <li>"hat er eine Schlange gesehen und sich sehr erschreckt" (Reihung von Partizipien)
     * <li>"scheint es zu schlafen und keine Gefahr darzustellen" (Reihung von Inifitiven mit zu)
     * <li>"hat er laufen und sich abreagieren wollen" (Reihung von reinen Infinitiven)
     * </ul>
     *
     * @param subjekt das Subjekt - muss zur finiten Verbform passen
     */
    @Override
    public Konstituentenfolge getVerbzweitMitSubjektImMittelfeld(
            final SubstantivischePhrase subjekt) {
        requireNonNull(subjekt, "subjekt");

        return getVerbzweit(subjekt);
    }

    /**
     * Gibt das Prädikat "in Verbzweitform" zurück - das Verb steht also ganz am Anfang.
     * (In einem Verbzweitsatz würde dann noch ein Subjekt vor dem Ganzen stehen).
     * <p/>
     * Liefert Ergebnisse wie
     * <ul>
     * <li>"möchtest Spannendes berichten" (Modalverb)
     * <li>"wirst den Weg gelaufen sein"
     * <li>"wirst den Weg laufen wollen" (mehrfach geschachtelt)
     * <li>"bist den Weg gelaufen" (Partizip II)
     * <li>"hast Spannendes berichtet"
     * <li>"hast den Schaden gewollt" (Partizip II, kein Ersatzinfinitiv)
     * <li>"hast Spannendes berichten wollen" ("wollen" als Ersatzinfinitiv)
     * <li>"möchte seine Geliebte in die Arme schließen" (Modalverb und Präfix)
     * <li>"hat eine Schlange gesehen und sich sehr erschreckt" (Reihung von Partizipien)
     * <li>"scheint zu schlafen und keine Gefahr darzustellen" (Reihung von Inifitiven mit zu)
     * <li>"hat laufen und sich abreagieren wollen" (Reihung von reinen Infinitiven)
     * </ul>
     */
    @Override
    public Konstituentenfolge getVerbzweit() {
        return getVerbzweit(null);
    }

    /**
     * Gibt das Prädikat "in Verbzweitform" zurück - das Verb steht also ganz am Anfang -,
     * wobei dieses Subjekt - wenn angegeben - zusätzlich ins Mittelfeld eingebaut wird.
     * (In einem Verbzweitsatz würde dann noch ein Satzglied vor dem Ganzen stehen.)
     * <p/>
     * Liefert Ergebnisse wie
     * <ul>
     * <li>"möchtest du Spannendes berichten" (Modalverb)
     * <li>"wirst den Weg gelaufen sein"
     * <li>"wirst du den Weg laufen wollen" (mehrfach geschachtelt)
     * <li>"bist den Weg gelaufen" (Partizip II)
     * <li>"hast du Spannendes berichtet"
     * <li>"hast den Schaden gewollt" (Partizip II, kein Ersatzinfinitiv)
     * <li>"hast du Spannendes berichten wollen" ("wollen" als Ersatzinfinitiv)
     * <li>"möchte seine Geliebte in die Arme schließen" (Modalverb und Präfix)
     * <li>"hat er eine Schlange gesehen und sich sehr erschreckt" (Reihung von Partizipien)
     * <li>"scheint es zu schlafen und keine Gefahr darzustellen" (Reihung von Inifitiven mit zu)
     * <li>"hat laufen und sich abreagieren wollen" (Reihung von reinen Infinitiven)
     * </ul>
     *
     * @param subjekt das Subjekt - muss zur finiten Verbform passen - sofern es verwendet
     *                werden soll
     */
    private Konstituentenfolge getVerbzweit(@Nullable final SubstantivischePhrase subjekt) {
        Konstituentenfolge res = null;

        for (int i = 0; i < children.size(); i++) {
            final IInfinitesPraedikat child = children.get(i);

            final Konstituentenfolge subjektPosition =
                    (i == 0 && subjekt != null) ? subjekt.nomK() : null;

            res = joinToKonstituentenfolge(
                    res, //
                    child.getKonnektor(), // "und"
                    i == 0 ?
                            getFiniteVerbformOhnePartikel()
                            // "möchtest" / "bist" / "wirst" / "hast"
                            : null,
                    // Damit steht das Subjekt entweder als nicht-pronominales Subjekt vor der
                    // Wackernagelposition oder als unbetontes Pronomen zu Anfang der
                    // Wackernagelposition:
                    subjektPosition, // "du"
                    child.ohneKonnektor().toKonstituentenfolgeOhneNachfeld(null,
                            i < children.size() - 1
                    ), // "laufen wollen"
                    child.getNachfeld()); // ": Herakles hat obsiegt!"
        }

        res = joinToKonstituentenfolge(
                res,
                getPartikel(),
                children.get(children.size() - 1).getNachfeld());

        return res;
    }

    /**
     * Gibt das Prädikat "in Verbletztform" zurück - das Verb steht also ganz am Ende, nur noch
     * gefolgt vom Nachfeld. Beispiele:
     * <ul>
     * <li>"Spannendes berichten möchtest" (Modalverb)
     * <li>"den Weg gelaufen sein wirst"
     * <li>"den Weg laufen wollen wirst" (mehrfach geschachtelt ohne Oberfeld)
     * <li>"den Weg gelaufen bist" (Partizip II)
     * <li>"Spannendes berichtet hast" ("hast" nicht im Oberfeld)
     * <li>"den Schaden gewollt hast" (Partizip II, kein Ersatzinfinitiv)
     * <li>"Spannendes hast berichten wollen" ("wollen" als Ersatzinfinitiv, "hast" im Oberfeld)
     * <li>"seine Geliebte in die Arme schließen möchte" (Modalverb und Präfix)
     * <li>"eine Schlange gesehen und sich sehr erschreckt hat" (Reihung von Partizipien)
     * <li>"zu schlafen und keine Gefahr darzustellen scheint" (Reihung von Inifitiven mit zu)
     * <li>"hat laufen und sich abreagieren wollen" (Reihung von reinen Infinitiven, "hat" im
     * Oberfeld)
     * </ul>
     */
    @Override
    public Konstituentenfolge getVerbletzt(final boolean nachfeldNachstellen) {
        Konstituentenfolge res = null;

        boolean finiteVerbformSchonEnthalten = false;
        for (int i = 0; i < children.size(); i++) {
            final IInfinitesPraedikat child = children.get(i);

            // Die finite Verbform wird normalerweise an den Schlus
            // gestellt - außer, eines der Kinder verlangt sie im Oberfeld,
            // dann wird sie bei diesem Kind ins Oberfeld gestellt und
            // danach nicht mehr wiederholt.
            final boolean finiteVerbformImOberfeld =
                    !finiteVerbformSchonEnthalten &&
                            child.finiteVerbformBeiVerbletztstellungImOberfeld();
            finiteVerbformSchonEnthalten = finiteVerbformSchonEnthalten || finiteVerbformImOberfeld;

            res = joinToKonstituentenfolge(
                    res,
                    child.toKonstituentenfolgeOhneNachfeld(
                            finiteVerbformImOberfeld ?
                                    getFiniteVerbformMitPartikel()
                                    : null,
                            i == children.size() - 1 && !nachfeldNachstellen
                    ), // "hat laufen wollen"
                    i == children.size() - 1 && !finiteVerbformSchonEnthalten ?
                            getFiniteVerbformMitPartikel()
                            : null,
                    i < children.size() - 1 || nachfeldNachstellen ?
                            child.getNachfeld() :
                            null
            );
        }

        return res;
    }

    @Nullable
    @Override
    public Konstituentenfolge getRelativpronomen() {
        return getMapFirst(IInfinitesPraedikat::getRelativpronomen);
    }

    @Nullable
    @Override
    public Konstituentenfolge getErstesInterrogativwort() {
        return getMapFirst(IInfinitesPraedikat::getErstesInterrogativwort);
    }

    @Nullable
    private <R> R getMapFirst(final Function<IInfinitesPraedikat, R> function) {
        return function.apply(children.get(0));
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final KomplexesFinitesPraedikat that = (KomplexesFinitesPraedikat) o;
        return children.equals(that.children);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), children);
    }

    @NonNull
    @Override
    public String toString() {
        return getFiniteVerbformMitPartikel() + " (" + children + ")";
    }
}
