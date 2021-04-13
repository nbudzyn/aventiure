package de.nb.aventiure2.german.federkiel;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.Collection;
import java.util.function.BinaryOperator;

import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.Kasus;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.NumerusGenus;
import de.nb.federkiel.deutsch.grammatik.kategorie.Genus;
import de.nb.federkiel.deutsch.grammatik.wortart.flexion.FeatureStringConverter;
import de.nb.federkiel.feature.FeatureAssignment;
import de.nb.federkiel.feature.ThreeStateFeatureEqualityFormula;
import de.nb.federkiel.interfaces.IWordForm;
import de.nb.federkiel.logic.FormulaUtil;
import de.nb.federkiel.logic.UnassignedVariableException;
import de.nb.federkiel.logic.YieldsNoResultException;

/**
 * Static methods for easy use of the
 * <a href="https://github.com/nbudzyn/federkiel">Federkiel</a>
 * library.
 */
public class FederkielUtil {
    private FederkielUtil() {
    }

    public static ImmutableMap<Kasus, String> extractFlextionsreihe(
            final Collection<IWordForm> wortformen,
            final NumerusGenus numerusGenus,
            final BinaryOperator<IWordForm> chooseBest) {
        final ImmutableMap.Builder<Kasus, String> wortformJeKasus = ImmutableMap.builder();

        for (final Kasus kasus : Kasus.values()) {
            wortformJeKasus.put(kasus,
                    wortformen.stream()
                            .filter(w -> has(w, numerusGenus, kasus))
                            .reduce(chooseBest)
                            .map(IWordForm::getString)
                            .orElseGet(() -> {
                                throw new IllegalStateException(
                                        "Fehlende Wortform in " + wortformen + " für "
                                                + kasus + " " + numerusGenus);
                            }));
        }

        return wortformJeKasus.build();
    }

    private static boolean has(final IWordForm wortform,
                               final NumerusGenus numerusGenus, final Kasus kasus) {
        try {
            final FeatureAssignment featureAssignment = FeatureAssignment.of(
                    ImmutableList.of(),
                    wortform.getFeatures());

            return FormulaUtil.and(
                    ThreeStateFeatureEqualityFormula.featureEqualsExplicitValue(
                            "kasus",
                            FeatureStringConverter.toFeatureString(
                                    toFederkielKasus(kasus))),
                    ThreeStateFeatureEqualityFormula.featureEqualsExplicitValue(
                            "numerus",
                            FeatureStringConverter.toFeatureString(
                                    toFederkielNumerus(numerusGenus.getNumerus()))),
                    ThreeStateFeatureEqualityFormula.featureEqualsExplicitValue(
                            "genus",
                            FeatureStringConverter.toFeatureString(
                                    toFederkielGenus(numerusGenus)))
            )
                    .evaluate(featureAssignment);
        } catch (final UnassignedVariableException e) {
            throw new IllegalStateException("Die Formel verwendet gar keine Variablen!", e);
        } catch (final YieldsNoResultException e) {
            throw new IllegalStateException(
                    "Die Formel sollte auf jeden Fall ein Ergebnis haben!",
                    e);
        }
    }

    private static de.nb.federkiel.deutsch.grammatik.kategorie.Kasus
    toFederkielKasus(final Kasus kasus) {
        switch (kasus) {
            case NOM:
                return de.nb.federkiel.deutsch.grammatik.kategorie.Kasus.NOMINATIV;
            case DAT:
                return de.nb.federkiel.deutsch.grammatik.kategorie.Kasus.DATIV;
            case AKK:
                return de.nb.federkiel.deutsch.grammatik.kategorie.Kasus.AKKUSATIV;
            default:
                throw new IllegalArgumentException("Unerwarteter Kasus: " + kasus);
        }
    }

    private static de.nb.federkiel.deutsch.grammatik.kategorie.Numerus
    toFederkielNumerus(final Numerus numerus) {
        switch (numerus) {
            case SG:
                return de.nb.federkiel.deutsch.grammatik.kategorie.Numerus.SINGULAR;
            case PL:
                return de.nb.federkiel.deutsch.grammatik.kategorie.Numerus.PLURAL;
            default:
                throw new IllegalArgumentException("Unerwarteter Numerus: " + numerus);
        }
    }

    @Nullable
    private static Genus toFederkielGenus(final NumerusGenus numerusGenus) {
        switch (numerusGenus) {
            case M:
                return Genus.MASKULINUM;
            case F:
                return Genus.FEMININUM;
            case N:
                return Genus.NEUTRUM;
            case PL_MFN:
                return null;
            default:
                throw new IllegalArgumentException(
                        "Unerwarteter NumerusGenus: " + numerusGenus);
        }
    }

}
