package de.nb.aventiure2.german.adjektiv;


import static de.nb.aventiure2.german.base.Konstituente.k;

import java.util.Objects;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.nb.aventiure2.annotations.Komplement;
import de.nb.aventiure2.annotations.Valenz;
import de.nb.aventiure2.german.base.Belebtheit;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativSkopusSatz;
import de.nb.aventiure2.german.base.Kasus;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.NumerusGenus;
import de.nb.aventiure2.german.base.PraedRegMerkmale;
import de.nb.aventiure2.german.base.Praedikativum;
import de.nb.aventiure2.german.praedikat.PraedikatOhneLeerstellen;

/**
 * Eine Adjektivphrase mit zu-Infinitiv, in der alle Leerstellen besetzt sind. Beispiel:
 * <ul>
 *     <li>"glücklich, Peter zu sehen"
 *     <li>"sehr glücklich, Peter zu sehen"
 * </ul>
 */
public class AdjPhrMitZuInfinitivOhneLeerstellen extends AbstractAdjPhrOhneLeerstellen {
    /**
     * "(...glücklich,) Peter zu sehen"
     */
    @Nonnull
    @Komplement
    private final PraedikatOhneLeerstellen lexikalischerKern;

    @Valenz
    AdjPhrMitZuInfinitivOhneLeerstellen(
            final Adjektiv adjektiv,
            final PraedikatOhneLeerstellen lexikalischerKern) {
        this(null, null, adjektiv, lexikalischerKern);
    }

    private AdjPhrMitZuInfinitivOhneLeerstellen(
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabeSkopusSatz,
            @Nullable final GraduativeAngabe graduativeAngabe,
            final Adjektiv adjektiv,
            final PraedikatOhneLeerstellen lexikalischerKern) {
        super(advAngabeSkopusSatz, graduativeAngabe, adjektiv);
        this.lexikalischerKern = lexikalischerKern;
    }

    @Override
    public AdjPhrMitZuInfinitivOhneLeerstellen mitGraduativerAngabe(
            @Nullable final GraduativeAngabe graduativeAngabe) {
        if (graduativeAngabe == null) {
            return this;
        }

        return new AdjPhrMitZuInfinitivOhneLeerstellen(
                getAdvAngabeSkopusSatz(), graduativeAngabe,
                getAdjektiv(),
                lexikalischerKern
        );
    }

    @Override
    public AdjPhrMitZuInfinitivOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        return new AdjPhrMitZuInfinitivOhneLeerstellen(
                advAngabe, getGraduativeAngabe(),
                getAdjektiv(),
                lexikalischerKern
        );
    }

    @Nullable
    @Override
    public String getAttributivAnteilAdjektivattribut(final NumerusGenus numerusGenus,
                                                      final Belebtheit belebtheit,
                                                      final Kasus kasus,
                                                      final boolean artikelwortTraegtKasusendung) {
        return null;
    }

    @Nullable
    @Override
    public Praedikativum getAttributivAnteilRelativsatz(
            final Kasus kasusBezugselement) {
        if (kasusBezugselement == Kasus.NOM) {
            // besser als lockerer Nachtrag: "Rapunzel, glücklich, dich zu sehen, ..."
            return null;
        }

        // "(die )glücklich(ist ), dich zu sehen"
        return this;
    }

    @Nullable
    @Override
    public AdjPhrOhneLeerstellen getAttributivAnteilLockererNachtrag(
            final Kasus kasusBezugselement) {
        if (kasusBezugselement == Kasus.NOM) {
            return this;
        }

        // Nebensatz - ansonsten kann es zu Missverständnissen oder falscher
        // Bedeutung kommen: "Du hilfst Rapunzel, glücklich dich zu sehen"
        // hat nicht die intendierte Bedeutung!

        return null;
    }

    @Override
    @CheckReturnValue
    public Konstituentenfolge getPraedikativOderAdverbial(
            final PraedRegMerkmale praedRegMerkmale) {
        return Konstituentenfolge.joinToKonstituentenfolge(
                getAdvAngabeSkopusSatzDescription(praedRegMerkmale),
                // "immer noch"
                getGraduativeAngabe(), // "sehr"
                k(getAdjektiv().getPraedikativ()), // "glücklich"
                getPraedikativAnteilKandidatFuerNachfeld(praedRegMerkmale)
                // ", sich erheben zu dürfen[, ]"
        );
    }

    @Override
    public Konstituentenfolge getPraedikativAnteilKandidatFuerNachfeld(
            final PraedRegMerkmale praedRegMerkmale) {
        return Konstituentenfolge.schliesseInKommaEin(
                lexikalischerKern.getZuInfinitiv(praedRegMerkmale)
                // "[,] sich erheben zu dürfen[,] "
        );
    }

    @Override
    public boolean enthaeltZuInfinitivOderAngabensatzOderErgaenzungssatz() {
        return true;
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
        final AdjPhrMitZuInfinitivOhneLeerstellen that = (AdjPhrMitZuInfinitivOhneLeerstellen) o;
        return lexikalischerKern.equals(that.lexikalischerKern);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), lexikalischerKern);
    }
}
