package de.nb.aventiure2.german.adjektiv;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativSkopusSatz;
import de.nb.aventiure2.german.base.IBezugsobjekt;
import de.nb.aventiure2.german.base.Kasus;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.NumerusGenus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.satz.Satz;

/**
 * Ein Adjektiv, das keine Ergänzungen fordert.
 */
public enum AdjektivOhneErgaenzungen implements AdjPhrOhneLeerstellen {
    ANDERS("anders", "ander"),
    ANGESPANNT("angespannt"),
    AUFGEBRACHT("aufgebracht"),
    AUFGEDREHT("aufgedreht"),
    // "schlecht aufgelegt", ...
    AUFGELEGT("aufgelegt"),
    BEDECKT("bedeckt"),
    BEGEISTERT("begeistert"),
    BENOMMEN("benommen"),
    BESONDERS("besonders", "besonder"),
    BETRUEBT("betrübt"),
    BEWEGT("bewegt"),
    BEWOELKT("bewölkt"),
    BEZOGEN("bezogen"),
    BLAU("blau"),
    DICK("dick"),
    DUESTER("düster"),
    DUNKEL("dunkel", "dunkl"),
    EITEL_NICHT_FLEKTIERBAR(Adjektiv.nichtFlektierbar("eitel")),
    ENTTAEUSCHT("enttäuscht"),
    ERHELLT("erhellt"),
    ERLEICHTERT("erleichtert"),
    ERSTAUNT("erstaunt"),
    ERSCHOEPFT("erschöpft"),
    FINSTER("finster", "finstr"),
    FREUDESTRAHLEND("freudestrahlend"),
    FROEHLICH("fröhlich"),
    GANZ("ganz"),
    GEKNICKT("geknickt"),
    GENAU("genau"),
    GENERVT("genervt"),
    GESPANNT("gespannt"),
    GETRUEBT("getrübt"),
    GLUECKLICH("glücklich"),
    GOLDEN("golden"),
    GROB("grob"),
    HAESSLICH("hässlich"),
    HEISS("heiß"),
    HELL("hell"),
    HOCH("hoch", "hoh"), // Steigerung höher, höchst!
    HUNDEMUEDE("hundemüde"),
    JUNG("jung"),
    KALT("kalt"),
    KLAR("klar"),
    KLEIN("klein"),
    KLIRREND("klirrend"),
    KRUMM("krumm"),
    KUEHL("kühl"),
    LANG("lang"),
    MAGER("mager"),
    MISSTRAUISCH("misstrauisch"),
    MORGENDLICH("morgendlich"),
    MUEDE("müde"),
    MUERRISCH("mürrisch"),
    ROETLICH("rötlich"),
    SANFT("sanft"),
    SENGEND("sengend"),
    SCHLECHT("schlecht"),
    SCHOEN("schön"),
    SKEPTISCH("skeptisch"),
    STARK("stark"),
    STRAHLEND("strahlend"),
    TRAURIG("traurig"),
    TODMUEDE("todmüde"),
    TROTZIG("trotzig"),
    TRUEB("trüb"),
    UEBERMUEDET("übermüdet"),
    UEBERRASCHT("überrascht"),
    UEBERRUMPELT("überrumpelt"),
    VERAERGERT("verärgert"),
    VERDROSSEN("verdrossen"),
    VERSCHUECHTERT("verschüchtert"),
    VERUNSICHERT("verunsichert"),
    VERWIRRT("verwirrt"),
    VERWUNDERT("verwundert"),
    ZUFRIEDEN("zufrieden"),
    WACH("wach"),
    WARM("warm"),
    WOLKENVERHANGEN("wolkenverhangen"),
    WUNDERSCHOEN("wunderschön"),
    ZORNIG("zornig");

    /**
     * Das Adjektiv an sich, ohne Informationen zur Valenz
     */
    @NonNull
    private final Adjektiv adjektiv;

    AdjektivOhneErgaenzungen(final String praedikativ) {
        this(new Adjektiv(praedikativ));
    }

    AdjektivOhneErgaenzungen(final String praedikativ, final String stamm) {
        this(new Adjektiv(praedikativ, stamm));
    }

    AdjektivOhneErgaenzungen(final Adjektiv adjektiv) {
        this.adjektiv = adjektiv;
    }

    @Override
    public Satz alsEsIstSatz() {
        return alsEsIstSatz(null);
    }

    @Override
    public Satz alsEsIstSatz(final @Nullable String anschlusswort) {
        return toAdjPhr().alsEsIstSatz(anschlusswort);
    }

    @Override
    public AdjPhrOhneErgaenzungenOhneLeerstellen mitGraduativerAngabe(
            final GraduativeAngabe graduativeAngabe) {
        return toAdjPhr().mitGraduativerAngabe(graduativeAngabe);
    }

    @Override
    public AdjPhrOhneErgaenzungenOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabe) {
        return toAdjPhr().mitAdvAngabe(advAngabe);
    }

    @Nullable
    @Override
    public String getAttributivAnteilAdjektivattribut(final NumerusGenus numerusGenus,
                                                      final Kasus kasus,
                                                      final boolean artikelwortTraegtKasusendung) {
        return toAdjPhr().getAttributivAnteilAdjektivattribut(numerusGenus, kasus,
                artikelwortTraegtKasusendung);
    }

    @Nullable
    @Override
    public Satz getAttributivAnteilRelativsatz(
            final Person personBezugselement,
            final NumerusGenus numerusGenusBezugselement,
            final Kasus kasusBezugselement,
            @Nullable final IBezugsobjekt bezugsobjektBezugselement) {
        return toAdjPhr().getAttributivAnteilRelativsatz(personBezugselement,
                numerusGenusBezugselement, kasusBezugselement, bezugsobjektBezugselement);
    }

    @Nullable
    @Override
    public AdjPhrOhneLeerstellen getAttributivAnteilLockererNachtrag(
            final Kasus kasusBezugselement) {
        return toAdjPhr().getAttributivAnteilLockererNachtrag(kasusBezugselement);
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

    @NonNull
    @VisibleForTesting
    Adjektiv getAdjektiv() {
        return adjektiv;
    }
}
