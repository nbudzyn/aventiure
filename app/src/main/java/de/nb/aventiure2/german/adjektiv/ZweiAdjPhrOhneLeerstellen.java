package de.nb.aventiure2.german.adjektiv;

import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativSkopusSatz;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.ZweiPraedikativa;

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
