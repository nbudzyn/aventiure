package de.nb.aventiure2.german.adjektiv;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import java.util.Locale;

import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativSkopusSatz;
import de.nb.aventiure2.german.base.Kasus;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld;
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
    ALT,
    ANBRECHEND,
    ANDERS("anders", "ander"),
    ANGENEHM,
    ANGESPANNT,
    AUFGEBRACHT,
    AUFGEDREHT,
    // "schlecht aufgelegt", ...
    AUFGELEGT,
    AUFGEHEIZT,
    AUSGERUPFT,
    AUSZUHALTEN, // So etwas ähnliches wie ein Partizip...
    BEAENGSTIGEND("beängstigend"),
    BEDECKT,
    BEDROHLICH,
    BEGEISTERT,
    BEGINNEND,
    BEISSEND("beißend"),
    BITTERKALT,
    BLEIERN,
    BLEIGRAU,
    BENOMMEN,
    BESONDERS("besonders", "besonder"),
    BETRUEBT("betrübt"),
    BEWEGT,
    BEWOELKT("bewölkt"),
    BEZOGEN,
    BLAU,
    BRAUSEND,
    BRUELLEND("brüllend"),
    BRUETEND("brütend"),
    DICK,
    DRUECKEND("drückend"),
    DUENN("dünn"),
    DUESTER("düster"),
    DUNKEL("dunkel", "dunkl"),
    DUNKLER("dunkler"),
    // TODO EINBRECHEND z.B. sollte besser ein spezielles Partizip II sein.
    //  Kaum prädikativ sinnvoll.
    EINBRECHEND,
    EINFACH,
    EISIG,
    EISKALT,
    EISEKALT,
    EITEL_NICHT_FLEKTIERBAR(Adjektiv.nichtFlektierbar("eitel")),
    ENTTAEUSCHT("enttäuscht"),
    ERHELLT,
    ERLEICHTERT,
    ERSTAUNT,
    ERTRAEGLICHER("erträglicher"),
    ERSCHOEPFT("erschöpft"),
    FINSTER("finster", "finstr"),
    FLIMMERND,
    FLIRREND,
    FREUDESTRAHLEND,
    FRISCH,
    FROEHLICH("fröhlich"),
    FROSTIG,
    GANZ,
    GEKLEIDET,
    GEKNICKT,
    GENAU,
    GENERVT,
    GESAMMELT,
    GESPANNT,
    GETRUEBT("getrübt"),
    GLUECKLICH("glücklich"),
    GLUEHEND("glühend"),
    GLUTHEISS("glutheiß"),
    GOLDEN,
    GRAU,
    GROB,
    GROSS("groß"),
    GRUEN("grün"),
    HANDLICH,
    HAESSLICH("hässlich"),
    HART,
    HEFTIG,
    HEISS("heiß"),
    HELL,
    // Achtung! Nicht prädikativ möglich? ?"Der Tag ist hellicht"
    HELLICHT,
    HERB,
    HOCH("hoch", "hoh"), // Steigerung höher, höchst!
    HUNDEMUEDE("hundemüde"),
    JUNG,
    KALT,
    KLAR,
    KLEIN,
    KLIRREND,
    KRAEFTIG("kräftig"),
    KRAFTVOLL,
    KRUMM,
    KUEHL("kühl"),
    KUEHLER("kühler"),
    LANG,
    LANGSAM,
    LAU,
    LAUT,
    LEICHT,
    LICHTLOS,
    MAGER,
    MISSTRAUISCH,
    MORGENDLICH,
    MUEDE("müde"),
    MUERRISCH("mürrisch"),
    NAECHTLICH("nächtlich"),
    NACHTSCHWARZ,
    NEU,
    OFFEN,
    PFEIFEND,
    RAU,
    RAUSCHEND,
    ROSA(Adjektiv.nichtFlektierbar("rosa")),
    ROETLICH("rötlich"),
    SANFT,
    SAUSEND,
    SCHUMMRIG,
    SCHWAECHER("schwächer"),
    SCHWER,
    SENGEND,
    SCHLECHT,
    SCHOEN("schön"),
    SKEPTISCH,
    SPUERBAR("spürbar"),
    STERNENKLAR,
    STARK,
    STAERKER("stärker"),
    STEHEND,
    STOCKDUNKEL("stockdunkel", "stockdunkl"),
    STRAHLEND,
    // FIXME Partizip I...!
    STUERMEND("stürmend"),
    STUERMISCH("stürmisch"),
    TIEF,
    TRAURIG,
    TODMUEDE("todmüde"),
    TOSEND,
    TOBEND,
    TROTZIG,
    TRUEB("trüb"),
    UEBERMUEDET("übermüdet"),
    UEBERRASCHT("überrascht"),
    UEBERRUMPELT("überrumpelt"),
    UNANGENEHM,
    UNBEWEGT,
    UNERTRAEGLICH("unerträglich"),
    UNERWARTET,
    VERAERGERT("verärgert"),
    VERDROSSEN,
    VERHANGEN,
    VERSCHUECHTERT("verschüchtert"),
    VERUNSICHERT,
    VERWIRRT,
    VERWUNDERT,
    WACH,
    WARM,
    WAERMER("wärmer"),
    WEISS("weiß"),
    WINDIG,
    WINDGESCHUETZT("windgeschützt"),
    WINDSTILL,
    WOLKENLOS,
    WOLKENVERHANGEN,
    WUNDERSCHOEN("wunderschön"),
    ZORNIG,
    ZUFRIEDEN;

    /**
     * Das Adjektiv an sich, ohne Informationen zur Valenz - Achtung, kann auch {@code null}
     * sein - dann wird es on the fly berechnet, vgl. {@link #getAdjektiv()}.
     */
    @Nullable
    private final Adjektiv adjektiv;

    AdjektivOhneErgaenzungen() {
        adjektiv = null;
    }

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
    public EinzelnerSatz alsEsIstSatz(
            final @Nullable
                    NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld anschlusswort) {
        return toAdjPhr().alsEsIstSatz(anschlusswort);
    }

    @Override
    public EinzelnerSatz alsEsWirdSatz() {
        return alsEsWirdSatz(null);
    }

    @Override
    public EinzelnerSatz alsEsWirdSatz(
            final @Nullable
                    NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld anschlusswort) {
        return toAdjPhr().alsEsWirdSatz(anschlusswort);
    }

    @Override
    public AdjPhrOhneErgaenzungenOhneLeerstellen mitGraduativerAngabe(
            @Nullable final GraduativeAngabe graduativeAngabe) {
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
        return new AdjPhrOhneErgaenzungenOhneLeerstellen(getAdjektiv());
    }

    /**
     * Gibt das Adjektiv zurück, ggf. on-the-fly berechnet.
     */
    @NonNull
    @VisibleForTesting
    Adjektiv getAdjektiv() {
        if (adjektiv != null) {
            return adjektiv;
        }

        return new Adjektiv(name().toLowerCase(Locale.GERMANY));
    }

    @Override
    public boolean hasVorangestellteAngaben() {
        return false;
    }
}
