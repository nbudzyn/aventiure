package de.nb.aventiure2.german.adjektiv;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativSkopusSatz;
import de.nb.aventiure2.german.base.Kasus;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.NumerusGenus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.Praedikativum;
import de.nb.aventiure2.german.satz.EinzelnerSatz;

/**
 * Ein Adjektiv, das keine Ergänzungen fordert.
 */
public enum AdjektivOhneErgaenzungen implements AdjPhrOhneLeerstellen {
    // FIXME einige von diesen sind eigentlich
    //  Partizipien II (oder vielleicht auch I) von Verben.
    //  Auch Partizipien ohne fehlende Ergänzungen sollten AdjPhrOhneLeerstellen
    //  sein. (Subj heizt Obj auf -> Partizip II "aufgeheizt")

    // FIXME Auch ("(Das Boot ist) zu sehen" und "(Das )zu lösende( Problem)"
    //  ("anzutreffende")... solte man wohl wie Patizip II behandeln.
    //  "zu " ist wohl quasi ein Präfix.

    // FIXME Partizipien mit fehlenden Ergänzungen sollten Adjektive mit X Ergänzungen
    //  sein (natürlich keine Enums, sondern separate Klassen).
    //  Erst einmal pro Verb-Valenz-Typ separat programmieren, wie man
    //  Partizipien erzeugt.

    // FIXME Hat man Partizipien II, kann man auch Perfekt bilden!
    ABGEKUEHLT("abgekühlt"),
    ALLMAEHLICH("allmählich"),
    ANBRECHEND("anbrechend"),
    ANDERS("anders", "ander"),
    ANGENEHM("angenehm"),
    ANGESPANNT("angespannt"),
    AUFGEBRACHT("aufgebracht"),
    AUFGEDREHT("aufgedreht"),
    // "schlecht aufgelegt", ...
    AUFGELEGT("aufgelegt"),
    AUFGEHEIZT("aufgeheizt"),
    AUSZUHALTEN("auszuhalten"), // So etwas ähnliches wie ein Partizip...
    BEDECKT("bedeckt"),
    BEDROHLICH("bedrohlich"),
    BEGEISTERT("begeistert"),
    BEGINNEND("beginnend"),
    BEISSEND("beißend"),
    BITTERKALT("bitterkalt"),
    BLEIERN("bleiern"),
    BLEIGRAU("bleigrau"),
    BENOMMEN("benommen"),
    BESONDERS("besonders", "besonder"),
    BETRUEBT("betrübt"),
    BEWEGT("bewegt"),
    BEWOELKT("bewölkt"),
    BEZOGEN("bezogen"),
    BLAU("blau"),
    BRAUSEND("brausend"),
    BRUELLEND("brüllend"),
    BRUETEND("brütend"),
    DICK("dick"),
    DRUECKEND("drückend"),
    DUESTER("düster"),
    DUNKEL("dunkel", "dunkl"),
    DUNKLER("dunkler"),
    // TODO EINBRECHEND z.B. sollte besser ein spezielles Partizip II sein.
    //  Kaum prädikativ sinnvoll.
    EINBRECHEND("einbrechend"),
    EISIG("eisig"),
    EISKALT("eiskalt"),
    EISEKALT("eisekalt"),
    EITEL_NICHT_FLEKTIERBAR(Adjektiv.nichtFlektierbar("eitel")),
    ENTTAEUSCHT("enttäuscht"),
    ERHELLT("erhellt"),
    ERLEICHTERT("erleichtert"),
    ERSTAUNT("erstaunt"),
    ERTRAEGLICHER("erträglicher"),
    ERSCHOEPFT("erschöpft"),
    FINSTER("finster", "finstr"),
    FLIMMERND("flimmernd"),
    FLIRREND("flirrend"),
    FREUDESTRAHLEND("freudestrahlend"),
    FRISCH("frisch"),
    FROEHLICH("fröhlich"),
    FROSTIG("frostig"),
    GANZ("ganz"),
    GEKNICKT("geknickt"),
    GENAU("genau"),
    GENERVT("genervt"),
    GESPANNT("gespannt"),
    GETRUEBT("getrübt"),
    GLUECKLICH("glücklich"),
    GLUEHEND("glühend"),
    GLUTHEISS("glutheiß"),
    GOLDEN("golden"),
    GRAU("grau"),
    GROB("grob"),
    GROSS("groß"),
    GRUEN("grün"),
    HAESSLICH("hässlich"),
    HART("hart"),
    HEFTIG("heftig"),
    HEISS("heiß"),
    HELL("hell"),
    // Achtung! Nicht prädikativ möglich? ?"Der Tag ist hellicht"
    HELLICHT("hellicht"),
    HERB("herb"),
    HOCH("hoch", "hoh"), // Steigerung höher, höchst!
    HUNDEMUEDE("hundemüde"),
    JUNG("jung"),
    KALT("kalt"),
    KLAR("klar"),
    KLEIN("klein"),
    KLIRREND("klirrend"),
    KRAEFTIG("kräftig"),
    KRAFTVOLL("kraftvoll"),
    KRUMM("krumm"),
    KUEHL("kühl"),
    KUEHLER("kühler"),
    LANG("lang"),
    LANGSAM("langsam"),
    LAU("lau"),
    LAUT("laut"),
    LEICHT("leicht"),
    LICHTLOS("lichtlos"),
    MAGER("mager"),
    MISSTRAUISCH("misstrauisch"),
    MORGENDLICH("morgendlich"),
    MUEDE("müde"),
    MUERRISCH("mürrisch"),
    NAECHTLICH("nächtlich"),
    NACHTSCHWARZ("nachtschwarz"),
    NEU("neu"),
    OFFEN("offen"),
    PFEIFEND("pfeifend"),
    RAU("rau"),
    RAUSCHEND("rauschend"),
    ROSA(Adjektiv.nichtFlektierbar("rosa")),
    ROETLICH("rötlich"),
    SANFT("sanft"),
    SAUSEND("sausend"),
    SCHUMMRIG("schummrig"),
    SCHWER("schwer"),
    SENGEND("sengend"),
    SCHLECHT("schlecht"),
    SCHOEN("schön"),
    SKEPTISCH("skeptisch"),
    SPUERBAR("spürbar"),
    STERNENKLAR("sternenklar"),
    STARK("stark"),
    STEHEND("stehend"),
    STOCKDUNKEL("stockdunkel"),
    STRAHLEND("strahlend"),
    // FIXME Partizip I...!
    STUERMEND("stürmend"),
    STUERMISCH("stürmisch"),
    TIEF("tief"),
    TRAURIG("traurig"),
    TODMUEDE("todmüde"),
    TOSEND("tosend"),
    TOBEND("tobend"),
    TROTZIG("trotzig"),
    TRUEB("trüb"),
    UEBERMUEDET("übermüdet"),
    UEBERRASCHT("überrascht"),
    UEBERRUMPELT("überrumpelt"),
    UNANGENEHM("unangenehm"),
    UNBEWEGT("unbewegt"),
    UNERTRAEGLICH("unerträglich"),
    UNERWARTET("unerwartet"),
    VERAERGERT("verärgert"),
    VERDROSSEN("verdrossen"),
    VERHANGEN("verhangen"),
    VERSCHUECHTERT("verschüchtert"),
    VERUNSICHERT("verunsichert"),
    VERWIRRT("verwirrt"),
    VERWUNDERT("verwundert"),
    WACH("wach"),
    WARM("warm"),
    WAERMER("wärmer"),
    WEISS("weiß"),
    WINDIG("windig"),
    WINDSTILL("windstill"),
    WOLKENLOS("wolkenlos"),
    WOLKENVERHANGEN("wolkenverhangen"),
    WUNDERSCHOEN("wunderschön"),
    ZORNIG("zornig"),
    ZUFRIEDEN("zufrieden");
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
    public EinzelnerSatz alsEsIstSatz() {
        return alsEsIstSatz(null);
    }

    @Override
    public EinzelnerSatz alsEsIstSatz(final @Nullable String anschlusswort) {
        return toAdjPhr().alsEsIstSatz(anschlusswort);
    }

    @Override
    public EinzelnerSatz alsEsWirdSatz() {
        return alsEsWirdSatz(null);
    }

    @Override
    public EinzelnerSatz alsEsWirdSatz(final @Nullable String anschlusswort) {
        return toAdjPhr().alsEsWirdSatz(anschlusswort);
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
    public Praedikativum getAttributivAnteilRelativsatz(
            final Kasus kasusBezugselement) {
        return toAdjPhr().getAttributivAnteilRelativsatz(
                kasusBezugselement);
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

    @Override
    public boolean hasVorangestellteAngaben() {
        return false;
    }
}
