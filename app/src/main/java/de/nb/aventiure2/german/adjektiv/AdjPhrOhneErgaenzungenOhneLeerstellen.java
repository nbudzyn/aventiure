package de.nb.aventiure2.german.adjektiv;


import static de.nb.aventiure2.german.base.Person.P3;

import javax.annotation.Nullable;

import de.nb.aventiure2.annotations.Valenz;
import de.nb.aventiure2.german.base.Belebtheit;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativSkopusSatz;
import de.nb.aventiure2.german.base.Kasus;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.NumerusGenus;
import de.nb.aventiure2.german.base.PraedRegMerkmale;
import de.nb.aventiure2.german.base.Praedikativum;

/**
 * Eine Adjektivphrase die keine Ergänzungen fordert. Beispiele:
 * <ul>
 *     <li>"glücklich"
 *     <li>"sehr glücklich"
 * </ul>
 */
public class AdjPhrOhneErgaenzungenOhneLeerstellen extends AbstractAdjPhrOhneLeerstellen {
    @Valenz
    AdjPhrOhneErgaenzungenOhneLeerstellen(
            final Adjektiv adjektiv) {
        this(null, null, adjektiv);
    }

    private AdjPhrOhneErgaenzungenOhneLeerstellen(
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabeSkopusSatz,
            @Nullable final GraduativeAngabe graduativeAngabe,
            final Adjektiv adjektiv) {
        super(advAngabeSkopusSatz, graduativeAngabe, adjektiv);
    }

    @Override
    public AdjPhrOhneErgaenzungenOhneLeerstellen mitGraduativerAngabe(
            @Nullable final GraduativeAngabe graduativeAngabe) {
        if (graduativeAngabe == null) {
            return this;
        }

        return new AdjPhrOhneErgaenzungenOhneLeerstellen(
                getAdvAngabeSkopusSatz(), graduativeAngabe,
                getAdjektiv()
        );
    }

    @Override
    public AdjPhrOhneErgaenzungenOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        return new AdjPhrOhneErgaenzungenOhneLeerstellen(
                advAngabe, getGraduativeAngabe(),
                getAdjektiv()
        );
    }

    @Nullable
    @Override
    public String getAttributivAnteilAdjektivattribut(final NumerusGenus numerusGenus,
                                                      final Belebtheit belebtheit,
                                                      final Kasus kasus,
                                                      final boolean artikelwortTraegtKasusendung) {
        return Konstituentenfolge.joinToKonstituentenfolge(
                getAdvAngabeSkopusSatzDescription(P3, numerusGenus.getNumerus(), belebtheit),
                // "immer noch"
                getGraduativeAngabe(), // "sehr"
                getAdjektiv().getAttributiv(numerusGenus, kasus, artikelwortTraegtKasusendung)
                // "zufriedenen"
        ).joinToSingleKonstituente().toTextOhneKontext();
    }

    @Nullable
    @Override
    public Praedikativum getAttributivAnteilRelativsatz(
            final Kasus kasusBezugselement) {
        return null;
    }

    @Nullable
    @Override
    public AdjPhrOhneLeerstellen getAttributivAnteilLockererNachtrag(
            final Kasus kasusBezugselement) {
        return null;
    }

    @Override
    public Konstituentenfolge getPraedikativOderAdverbial(final PraedRegMerkmale praedRegMerkmale) {
        return Konstituentenfolge.joinToKonstituentenfolge(
                getAdvAngabeSkopusSatzDescription(praedRegMerkmale),
                // "immer noch"
                getGraduativeAngabe(), // "sehr"
                getAdjektiv().getPraedikativ() // "zufrieden"
        );
    }

    @Override
    @Nullable
    public Konstituentenfolge getPraedikativAnteilKandidatFuerNachfeld(
            final PraedRegMerkmale praedRegMerkmale) {
        return null;
    }

    @Override
    public boolean enthaeltZuInfinitivOderAngabensatzOderErgaenzungssatz() {
        if (getAdvAngabeSkopusSatz() == null) {
            return false;
        }

        return getAdvAngabeSkopusSatz()
                .enthaeltZuInfinitivOderAngabensatzOderErgaenzungssatz();
    }
}
