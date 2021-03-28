package de.nb.aventiure2.german.adjektiv;

import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativSkopusSatz;
import de.nb.aventiure2.german.base.IBezugsobjekt;
import de.nb.aventiure2.german.base.Kasus;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.NumerusGenus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.ZweiPraedikativa;
import de.nb.aventiure2.german.satz.Satz;

/**
 * Zwei Adjektivphrasen ohne Leerstellen, die mit <i>und</i>
 * verbunden werden
 */
public class ZweiAdjPhrOhneLeerstellen
        extends ZweiPraedikativa<AdjPhrOhneLeerstellen>
        implements AdjPhrOhneLeerstellen {

    public ZweiAdjPhrOhneLeerstellen(
            final AdjPhrOhneLeerstellen erst,
            final AdjPhrOhneLeerstellen zweit) {
        super(erst, zweit);
    }

    @Override
    public AdjPhrOhneLeerstellen mitGraduativerAngabe(
            @Nullable final GraduativeAngabe graduativeAngabe) {
        return new ZweiAdjPhrOhneLeerstellen(
                getErst().mitGraduativerAngabe(graduativeAngabe),
                getZweit()
        );
    }

    @Override
    public AdjPhrOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabe) {
        return new ZweiAdjPhrOhneLeerstellen(
                getErst().mitAdvAngabe(advAngabe),
                getZweit()
        );
    }

    @Nullable
    @Override
    public String getAttributivAnteilAdjektivattribut(final NumerusGenus numerusGenus,
                                                      final Kasus kasus,
                                                      final boolean artikelwortTraegtKasusendung) {
        // FIXME Test-driven implementieren
        return null;
    }

    @Nullable
    @Override
    public Satz getAttributivAnteilRelativsatz(
            final Person personBezugselement,
            final NumerusGenus numerusGenusBezugselement,
            @Nullable final IBezugsobjekt bezugsobjektBezugselement) {
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
    public Konstituentenfolge getPraedikativOderAdverbial(final Person person,
                                                          final Numerus numerus) {
        return Konstituentenfolge.joinToKonstituentenfolge(
                getErst().getPraedikativ(person, numerus),
                "und",
                getZweit().getPraedikativ(person, numerus)
        );
    }

    @Override
    public boolean enthaeltZuInfinitivOderAngabensatzOderErgaenzungssatz() {
        return getErst().enthaeltZuInfinitivOderAngabensatzOderErgaenzungssatz() ||
                getZweit().enthaeltZuInfinitivOderAngabensatzOderErgaenzungssatz();
    }
}
