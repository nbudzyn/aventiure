package de.nb.aventiure2.german.adjektiv;

import androidx.annotation.NonNull;

import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;

/**
 * Ein Adjektiv, das keine Ergänzungen fordert.
 */
public enum AdjektivOhneErgaenzungen implements AdjPhrOhneLeerstellen {
    ANGESPANNT("angespannt"),
    AUFGEDREHT("aufgedreht"),
    BEGEISTERT("begeistert"),
    BENOMMEN("benommen"),
    BETRUEBT("betrübt"),
    BEWEGT("bewegt"),
    ERSCHOEPFT("erschöpft"),
    FROEHLICH("fröhlich"),
    GEKNICKT("geknickt"),
    GESPANNT("gespannt"),
    GLUECKLICH("glücklich"),
    HUNDEMUEDE("hundemüde"),
    MUEDE("müde"),
    RESERVIERT("reserviert"),
    TRAURIG("traurig"),
    TODMUEDE("todmüde"),
    TROTZIG("trotzig"),
    UEBERMUEDET("übermüdet"),
    UEBERRASCHT("überrascht"),
    UEBERRUMPELT("überrumpelt"),
    VERAERGERT("verärgert"),
    VERSTIMMT("verstimmt"),
    VERUNSICHERT("verunsichert"),
    VERWIRRT("verwirrt"),
    VERWUNDERT("verwundert"),
    ZUFRIEDEN("zufrieden"),
    WACH("wach"),
    ZORNIG("zornig");

    /**
     * Das Adjektiv an sich, ohne Informationen zur Valenz
     */
    @NonNull
    private final Adjektiv adjektiv;

    AdjektivOhneErgaenzungen(@NonNull final String praedikativ) {
        this(new Adjektiv(praedikativ));
    }

    AdjektivOhneErgaenzungen(@NonNull final Adjektiv adjektiv) {
        this.adjektiv = adjektiv;
    }

    @Override
    public AdjPhrOhneErgaenzungenOhneLeerstellen mitGraduativerAngabe(
            final GraduativeAngabe graduativeAngabe) {
        return toAdjPhr().mitGraduativerAngabe(graduativeAngabe);
    }

    @Override
    public Iterable<Konstituente> getPraedikativOderAdverbial(final Person person,
                                                              final Numerus numerus) {
        return toAdjPhr().getPraedikativ(person, numerus);
    }

    @Override
    public Iterable<Konstituente> getPraedikativAnteilKandidatFuerNachfeld(final Person person,
                                                                           final Numerus numerus) {
        return toAdjPhr().getPraedikativAnteilKandidatFuerNachfeld(person, numerus);
    }

    @Override
    public boolean enthaeltZuInfinitivOderAngabensatzOderErgaenzungssatz() {
        return toAdjPhr().enthaeltZuInfinitivOderAngabensatzOderErgaenzungssatz();
    }

    private AdjPhrOhneErgaenzungenOhneLeerstellen toAdjPhr() {
        return new AdjPhrOhneErgaenzungenOhneLeerstellen(adjektiv);
    }
}
