package de.nb.aventiure2.data.world.syscomp.wetter.windstaerke;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.description.AltDescriptionsBuilder;
import de.nb.aventiure2.german.satz.EinzelnerSatz;

import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.altNeueSaetze;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static de.nb.aventiure2.util.StreamUtil.*;

@SuppressWarnings({"DuplicateBranchesInSwitch", "MethodMayBeStatic", "RedundantSuppression"})
public class WindstaerkeDescDescriber {
    private final WindstaerkePraedikativumDescriber praedikativumDescriber;
    private final WindstaerkeSatzDescriber satzDescriber;

    public WindstaerkeDescDescriber(final WindstaerkeSatzDescriber satzDescriber,
                                    final WindstaerkePraedikativumDescriber praedikativumDescriber) {
        this.satzDescriber = satzDescriber;
        this.praedikativumDescriber = praedikativumDescriber;
    }

    /**
     * Gibt Beschreibungen zurück, wenn der SC aus dem Wind nach drinnen kommt -
     * je nach Windstärke oft leer.
     */
    public ImmutableCollection<AbstractDescription<?>> altKommtNachDrinnen(
            final AvTime time,
            final Windstaerke windstaerkeFrom) {
        final AltDescriptionsBuilder alt = AltDescriptionsBuilder.alt();
        if (windstaerkeFrom.compareTo(Windstaerke.KRAEFTIGER_WIND) >= 0) {
            alt.addAll(mapToSet(
                    praedikativumDescriber.altDraussenSubstPhr(windstaerkeFrom, time),
                    windUndWetter ->
                            du("bist",
                                    "hier vor", windUndWetter.datK(),
                                    "geschützt").mitVorfeldSatzglied("hier")));
            alt.addAll(mapToSet(
                    praedikativumDescriber.altDraussenSubstPhr(windstaerkeFrom, time),
                    windUndWetter ->
                            du("findest",
                                    "hier Zuflucht vor", windUndWetter.datK())
                                    .mitVorfeldSatzglied("hier")));
            alt.addAll(mapToSet(
                    praedikativumDescriber.altDraussenSubstPhr(windstaerkeFrom, time),
                    windUndWetter ->
                            du("findest",
                                    "hier Schutz vor", windUndWetter.datK())
                                    .mitVorfeldSatzglied("hier")));
        }

        return alt.build();
    }

    /**
     * Gibt Beschreibungen zurück, wenn der SC in einen windgeschützteren Bereich kommt -
     * je nach Windstärke oft leer.
     */
    public ImmutableCollection<AbstractDescription<?>> altAngenehmerAlsVorLocation(
            final Windstaerke windstaerkeFrom,
            final Windstaerke windstaerkeTo) {
        checkArgument(windstaerkeFrom.compareTo(windstaerkeTo) > 0);

        final AltDescriptionsBuilder alt = AltDescriptionsBuilder.alt();

        alt.addAll(satzDescriber.altAngenehmerAlsVorLocation(windstaerkeFrom, windstaerkeTo));

        if (windstaerkeFrom.compareTo(Windstaerke.WINDIG) >= 0) {
            alt.add(du("suchst", "so ein wenig Schutz vor dem Wetter")
                    .mitVorfeldSatzglied("so"));
        }

        return alt.schonLaenger().build();
    }

    /**
     * Gibt alternative {@link AbstractDescription}s zurück, die die Windstärke
     * beschreiben, wie der SC sie erlebt, wenn er nach draußen kommt
     */
    @NonNull
    public ImmutableCollection<AbstractDescription<?>> altKommtNachDraussen(final AvTime time,
                                                                            final Windstaerke windstaerke) {
        final AltDescriptionsBuilder alt = AltDescriptionsBuilder.alt();

        final ImmutableCollection<EinzelnerSatz> altSaetze =
                satzDescriber.altKommtNachDraussen(time, windstaerke);
        alt.addAll(altSaetze);

        switch (windstaerke) {
            case WINDSTILL:
                alt.add(neuerSatz("draußen ist kein Lufthauch zu spüren"),
                        neuerSatz("draußen geht kein Wind"));
                break;
            case LUEFTCHEN:
                alt.add(neuerSatz("draußen ist kaum ein Lufthauch zu spüren"));
                break;
            case WINDIG:
                alt.add(neuerSatz("draußen geht ein Wind"),
                        neuerSatz("draußen weht ein Wind"));
                break;
            case KRAEFTIGER_WIND:
                alt.add(neuerSatz("draußen geht ein kräftiger Wind"),
                        neuerSatz("draußen weht ein kräftiger Wind"));
                break;
            case STURM:
                break;
            case SCHWERER_STURM:
                alt.addAll(altNeueSaetze(
                        ImmutableList.of(
                                "draußen tobt ein heftiges Unwetter",
                                "draußen braust ein heftiger Sturm"
                        ),
                        SENTENCE,
                        "Nur mit Mühe kannst du dich auf den Beinen halten"
                ));
                break;
            default:
                throw new IllegalArgumentException("Unexpected Windstaerke");
        }

        return alt.schonLaenger().build();
    }

    /**
     * Gibt alternative {@link AbstractDescription}s zurück, die die Windstärke
     * beschreiben.
     */
    @NonNull
    public ImmutableCollection<AbstractDescription<?>> alt(final AvTime time,
                                                           final Windstaerke windstaerke) {
        final AltDescriptionsBuilder alt = AltDescriptionsBuilder.alt();

        final ImmutableCollection<EinzelnerSatz> altSaetze = satzDescriber.alt(time, windstaerke,
                false);
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
                break;
            case KRAEFTIGER_WIND:
                alt.add(neuerSatz("es geht ein kräftiger Wind"),
                        neuerSatz("es weht ein kräftiger Wind"));
                break;
            case STURM:
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

        return alt.schonLaenger().build();
    }
}
