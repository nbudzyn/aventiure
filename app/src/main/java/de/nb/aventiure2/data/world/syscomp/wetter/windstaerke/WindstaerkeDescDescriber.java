package de.nb.aventiure2.data.world.syscomp.wetter.windstaerke;

import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.data.time.AvTimeSpan.ONE_DAY;
import static de.nb.aventiure2.data.time.AvTimeSpan.span;
import static de.nb.aventiure2.german.base.ArtikelwortFlexionsspalte.Typ.INDEF;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.Personalpronomen.EXPLETIVES_ES;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.altNeueSaetze;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static de.nb.aventiure2.util.StreamUtil.*;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import de.nb.aventiure2.data.time.AvDateTime;
import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.data.world.base.Change;
import de.nb.aventiure2.data.world.syscomp.wetter.base.WetterParamChange;
import de.nb.aventiure2.data.world.syscomp.wetter.tageszeit.TageszeitAdvAngabeWannDescriber;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.description.AltDescriptionsBuilder;
import de.nb.aventiure2.german.satz.EinzelnerSatz;
import de.nb.aventiure2.german.satz.Konditionalsatz;

@SuppressWarnings({"DuplicateBranchesInSwitch", "MethodMayBeStatic", "RedundantSuppression"})
public class WindstaerkeDescDescriber {
    private final TageszeitAdvAngabeWannDescriber tageszeitAdvAngabeWannDescriber;
    private final WindstaerkePraedikativumDescriber praedikativumDescriber;
    private final WindstaerkeSatzDescriber satzDescriber;

    public WindstaerkeDescDescriber(
            final TageszeitAdvAngabeWannDescriber tageszeitAdvAngabeWannDescriber,
            final WindstaerkeSatzDescriber satzDescriber,
            final WindstaerkePraedikativumDescriber praedikativumDescriber) {
        this.tageszeitAdvAngabeWannDescriber = tageszeitAdvAngabeWannDescriber;
        this.satzDescriber = satzDescriber;
        this.praedikativumDescriber = praedikativumDescriber;
    }

    /**
     * Gibt Alternativen zurück, die beschreiben, wie die Windstaerke sich um eine Stufe
     * (Windstärkewechsel) oder um mehrere Stufen (Windstärkesprung) verändert hat.*
     */
    public ImmutableCollection<AbstractDescription<?>> altSprungOderWechsel(
            final Change<AvDateTime> dateTimeChange,
            final WetterParamChange<Windstaerke> change,
            final boolean auchZeitwechselreferenzen) {
        final AltDescriptionsBuilder alt = AltDescriptionsBuilder.alt();

        alt.addAll(altNeueSaetze(
                PARAGRAPH,
                satzDescriber.altSprungOderWechsel(
                        dateTimeChange, change, auchZeitwechselreferenzen)));

        // IDEA Wind / Sturm - dynamisch, unter Bezug auf Features des Umwelt
        //  (Blätter)
        //  WINDIG -> WINDSTILL "Der Wind legt sich, und auf den Bäumen vor [...] regt sich kein
        //  Blättchen mehr"

        final ImmutableSet<Konstituente> altSpWann;
        final ImmutableSet<Konstituentenfolge> altSpWannSaetze;

        if (span(dateTimeChange).shorterThan(ONE_DAY) && auchZeitwechselreferenzen) {
            final Change<AvTime> timeChange = dateTimeChange.map(AvDateTime::getTime);

            altSpWann = mapToSet(
                    tageszeitAdvAngabeWannDescriber.altSpWannDraussen(timeChange),
                    gegenMitternacht -> gegenMitternacht.getDescription(EXPLETIVES_ES));

            altSpWannSaetze = mapToSet(tageszeitAdvAngabeWannDescriber
                            .altSpWannKonditionalsaetzeDraussen(timeChange),
                    Konditionalsatz::getDescription);
        } else {
            altSpWann = ImmutableSet.of();
            altSpWannSaetze = ImmutableSet.of();
        }

        if (change.delta() == 1) {
            // "es kommt ein Wind"
            alt.addAll(altNeueSaetze(
                    "es kommt",
                    change.getNachher().altSpNomenFlexionsspalte().stream()
                            .map(wind -> np(INDEF, wind).nomK())));
        }

        if (change.getVorher() == Windstaerke.WINDIG
                && change.getNachher() == Windstaerke.KRAEFTIGER_WIND) {
            alt.add(neuerSatz("es kommt ein starker Wind"));
            if (!altSpWann.isEmpty()) {
                alt.addAll(altNeueSaetze(altSpWann, "kommt ein starker Wind"));
            }
            if (!altSpWannSaetze.isEmpty()) {
                alt.addAll(altNeueSaetze(altSpWannSaetze, ", kommt ein starker Wind"));
            }
        }

        return alt.schonLaenger().build();
    }

    /**
     * Gibt Beschreibungen zurück, wenn der SC aus dem Wind nach drinnen kommt -
     * je nach Windstärke oft leer.
     */
    public ImmutableCollection<AbstractDescription<?>> altSpKommtNachDrinnen(
            final AvTime time,
            final Windstaerke windstaerkeFrom) {
        final AltDescriptionsBuilder altSp = AltDescriptionsBuilder.alt();
        if (windstaerkeFrom.compareTo(Windstaerke.KRAEFTIGER_WIND) >= 0) {
            altSp.addAll(mapToSet(
                    praedikativumDescriber.altDraussenSubstPhr(windstaerkeFrom, time),
                    windUndWetter ->
                            du("bist",
                                    "hier vor", windUndWetter.datK(),
                                    "geschützt").mitVorfeldSatzglied("hier")));
            altSp.addAll(mapToSet(
                    praedikativumDescriber.altDraussenSubstPhr(windstaerkeFrom, time),
                    windUndWetter ->
                            du("findest",
                                    "hier Zuflucht vor", windUndWetter.datK())
                                    .mitVorfeldSatzglied("hier")));
            altSp.addAll(mapToSet(
                    praedikativumDescriber.altDraussenSubstPhr(windstaerkeFrom, time),
                    windUndWetter ->
                            du("findest",
                                    "hier Schutz vor", windUndWetter.datK())
                                    .mitVorfeldSatzglied("hier")));
        }

        return altSp.schonLaenger().build();
    }

    /**
     * Gibt Beschreibungen zurück, wenn der SC in einen windgeschützteren Bereich kommt -
     * je nach Windstärke oft leer.
     */
    public ImmutableCollection<AbstractDescription<?>> altSpAngenehmerAlsVorLocation(
            final Windstaerke windstaerkeFrom,
            final Windstaerke windstaerkeTo) {
        checkArgument(windstaerkeFrom.compareTo(windstaerkeTo) > 0);

        final AltDescriptionsBuilder altSp = AltDescriptionsBuilder.alt();

        altSp.addAll(satzDescriber.altSpAngenehmerAlsVorLocation(windstaerkeFrom, windstaerkeTo));

        if (windstaerkeFrom.compareTo(Windstaerke.WINDIG) >= 0) {
            altSp.add(du("suchst", "so ein wenig Schutz vor dem Wetter")
                    .mitVorfeldSatzglied("so"));
        }

        return altSp.schonLaenger().build();
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

        alt.addAll(
                // (nicht leer)
                satzDescriber.altSp(time, windstaerke,
                        false, false));

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
