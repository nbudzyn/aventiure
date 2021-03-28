package de.nb.aventiure2.german.adjektiv;


import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.nb.aventiure2.annotations.Komplement;
import de.nb.aventiure2.annotations.Valenz;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativSkopusSatz;
import de.nb.aventiure2.german.base.Kasus;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.NumerusGenus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.praedikat.PraedikatOhneLeerstellen;
import de.nb.aventiure2.german.satz.Satz;

import static de.nb.aventiure2.german.base.Konstituente.k;

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
    public String getAttributivAnteilAdjektivattribut(final NumerusGenus numerusGenus, final Kasus kasus,
                                                      final boolean artikelwortTraegtKasusendung) {
        // FIXME Test-driven implementieren
        return null;
    }

    @Nullable
    @Override
    public Satz getAttributivAnteilRelativsatz() {
        // FIXME Test-driven implementieren
        return null;
    }

    @Nullable
    @Override
    public AdjPhrOhneLeerstellen getAttributivAnteilLockererNachtrag() {
        // FIXME Test-driven implementieren
        return null;
    }

    @Override
    @CheckReturnValue
    public Konstituentenfolge getPraedikativOderAdverbial(final Person personSubjekt,
                                                          final Numerus numerusSubjekt) {
        return Konstituentenfolge.joinToKonstituentenfolge(
                getAdvAngabeSkopusSatzDescription(personSubjekt, numerusSubjekt), // "immer noch"
                getGraduativeAngabe(), // "sehr"
                k(getAdjektiv().getPraedikativ()), // "glücklich"
                getPraedikativAnteilKandidatFuerNachfeld(personSubjekt, numerusSubjekt)
                // ", sich erheben zu dürfen[, ]"
        );
    }

    @Override
    public Konstituentenfolge getPraedikativAnteilKandidatFuerNachfeld(final Person person,
                                                                       final Numerus numerus) {
        return Konstituentenfolge.schliesseInKommaEin(
                lexikalischerKern.getZuInfinitiv(person, numerus)
                // "[,] sich erheben zu dürfen[,] "
        );
    }

    @Override
    public boolean enthaeltZuInfinitivOderAngabensatzOderErgaenzungssatz() {
        return true;
    }
}
