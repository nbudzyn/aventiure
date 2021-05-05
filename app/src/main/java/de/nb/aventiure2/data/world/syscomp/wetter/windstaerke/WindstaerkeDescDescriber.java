package de.nb.aventiure2.data.world.syscomp.wetter.windstaerke;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.data.world.syscomp.wetter.tageszeit.TageszeitDescDescriber;
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.description.AltDescriptionsBuilder;
import de.nb.aventiure2.german.satz.EinzelnerSatz;

import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.altNeueSaetze;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;

@SuppressWarnings({"DuplicateBranchesInSwitch", "MethodMayBeStatic"})
public class WindstaerkeDescDescriber {
    private final TageszeitDescDescriber tageszeitDescDescriber;
    private final WindstaerkePraedikativumDescriber praedikativumDescriber;
    private final WindstaerkeSatzDescriber satzDescriber;

    public WindstaerkeDescDescriber(final TageszeitDescDescriber tageszeitDescDescriber,
                                    final WindstaerkePraedikativumDescriber praedikativumDescriber,
                                    final WindstaerkeSatzDescriber satzDescriber) {
        this.tageszeitDescDescriber = tageszeitDescDescriber;
        this.praedikativumDescriber = praedikativumDescriber;
        this.satzDescriber = satzDescriber;
    }


    /**
     * Gibt alternative {@link AbstractDescription}s zurück, die die Windstärke
     * beschreiben.
     */
    @NonNull
    public ImmutableCollection<AbstractDescription<?>> alt(final AvTime time,
                                                           final Windstaerke windstaerke) {
        final AltDescriptionsBuilder alt = AltDescriptionsBuilder.alt();

        final ImmutableCollection<EinzelnerSatz> altSaetze = satzDescriber.alt(time, windstaerke);
        alt.addAll(altSaetze);

        switch (windstaerke) {
            case WINDSTILL:
                alt.add(neuerSatz("kein Lufthauch ist zu spüren"),
                        neuerSatz("es geht kein Wind"));
                break;
            case LUEFTCHEN:
                alt.add(neuerSatz("es ist kaum ein Lufthauch zu spüren"),
                        neuerSatz("kaum ein Lufthauch ist zu spüren"),
                        neuerSatz("kaum bewegt sich die Luft"));
                break;
            case WINDIG:
                alt.add(neuerSatz("es geht ein Wind"),
                        neuerSatz("es weht ein Wind"));
                //  FIXME "es weht beständig ein Wind"?
                break;
            case KRAEFTIGER_WIND:
                alt.add(neuerSatz("es geht ein kräftiger Wind"),
                        // FIXME "es weht beständig ein harter Wind"?
                        neuerSatz("es weht ein kräftiger Wind"));
                break;
            case STURM:
                alt.addAll(altNeueSaetze(
                        altSaetze, SENTENCE,
                        ImmutableList.of(
                                "Hoffentlich bleibt es wenigstens trocken!",
                                "Hoffentlich regnet es nicht auch noch!",
                                "Hoffenlicht fängt nicht auch noch ein Platzregen an!"
                        )));
                break;
            case SCHWERER_STURM:
                alt.addAll(altNeueSaetze(
                        ImmutableList.of(
                                "Es tobt ein heftiges Unwetter",
                                "Es braust ein heftiger Sturm"
                        ),
                        SENTENCE,
                        ImmutableList.of(
                                "Nur mit Mühe kannst du dich auf den Beinen halten",
                                "Ein jeder Schritt kostet viel Kraft"
                        )));
                break;
            default:
                throw new IllegalArgumentException("Unexpected Windstaerke");
        }

        // FIXME Wind / Sturm - statisch, unter Bezug auf Features des Umwelt
        //  (Laub, Blätter, Bäume, Äste, Wald; etwas, das Schutz bietet)
        //  WINDSTILL:
        //   "...und kein Lüftchen streicht durch das Laub"
        //  "Es geht kein Wind, und bewegt sich kein Blättchen"
        //  LUEFTCHEN:
        //   "...und ein Lüftchen streicht durch das Laub"
        //  WINDIG:
        //  "der Wind raschelt in den Bäumen"
        //  KRAEFTIGER_WIND:
        //   "das Gezweig"
        //  "der Wind rauscht draußen in den Bäumen"
        //  STURM:
        //   "Die Äste biegen sich"
        //  SCHWERER STURM:
        //  Der Sturm biegt die Bäume.
        //   "...und gehst nach dem Wald zu, dort ein wenig Schutz vor dem Wetter zu suchen"
        //   Hoffentlich bleibt es wenigstens trocken
        //  "darin bist du vor dem Wind geschützt"
        //  "du findest darin Schutz"
        //  Der Sturm peitscht die Äste über dir... Ein geschützter Platz wäre schön.
        //  Der Sturm peitscht die Äste über dir und es ist ziemlich dunkel. Ein geschützter Platz
        //  wäre schön.

        return alt.schonLaenger().build();
    }
}
