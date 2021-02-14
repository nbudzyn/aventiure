package de.nb.aventiure2.german.adjektiv;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import de.nb.aventiure2.german.base.Konstituentenfolge;
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
    ENTTAEUSCHT("enttäuscht"),
    ERLEICHTERT("erleichtert"),
    ERSTAUNT("erstaunt"),
    ERSCHOEPFT("erschöpft"),
    FREUDESTRAHLEND("freudestrahlend"),
    FROEHLICH("fröhlich"),
    GEKNICKT("geknickt"),
    GENAU("genau"),
    GENERVT("genervt"),
    GESPANNT("gespannt"),
    GLUECKLICH("glücklich"),
    HUNDEMUEDE("hundemüde"),
    MISSTRAUISCH("misstrauisch"),
    MUEDE("müde"),
    MUERRISCH("mürrisch"),
    SKEPTISCH("skeptisch"),
    TRAURIG("traurig"),
    TODMUEDE("todmüde"),
    TROTZIG("trotzig"),
    UEBERMUEDET("übermüdet"),
    UEBERRASCHT("überrascht"),
    UEBERRUMPELT("überrumpelt"),
    VERAERGERT("verärgert"),
    VERDROSSEN("verdrossen"),
    VERSCHUECHTERT("verschüchtert"),
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
    public Konstituentenfolge getPraedikativOderAdverbial(final Person person,
                                                          final Numerus numerus) {
        return toAdjPhr().getPraedikativ(person, numerus);
    }

    @Override
    @Nullable
    public Konstituentenfolge getPraedikativAnteilKandidatFuerNachfeld(final Person person,
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
