package de.nb.aventiure2.german.adjektiv;

import com.google.common.base.Joiner;

import java.util.Objects;

import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativSkopusSatz;
import de.nb.aventiure2.german.base.Kasus;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.NumerusGenus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.Praedikativum;
import de.nb.aventiure2.german.base.ZweiPraedikativa;

/**
 * Zwei Adjektivphrasen ohne Leerstellen, die mit <i>und</i>
 * verbunden werden
 */
public class ZweiAdjPhrOhneLeerstellen
        extends ZweiPraedikativa<AdjPhrOhneLeerstellen>
        implements AdjPhrOhneLeerstellen {
    /**
     * Ob die Adjektiv-Phrasen gleichrangig sind (dann werden sie als
     * Adjektivattribute durch Komma getrennt - "leichte, herbe( Rotweine)") -
     * oder nicht (dann werden sie - wenn sie nicht durch einen Konnektor wie
     * "aber" verbunden sind - als Ajektivattribute nicht durch Komma getrennt
     * "dunkles bayrisches( Bier)".
     * <p>
     * Typische Beispiele für nicht gleichrangige Adjektive:
     * <li>
     * <li>Nur das zweite Adjektiv bezeichnet eine Farbe ("(ein )wolkenloser blauer( Himmel)")
     * <li>Nur das zweite Adjektiv bezeichnet ein Material ("(die )alte steinerne( Brücke)")
     * <li>Nur das zweite Adjektiv bezeichnet eine Herkunft ("(ein )bekannter spanischer( Autor)")
     * <li>Nur das zweite Adjektiv nennt eine Zugehörigkeit ("(eine )wichtige amtliche(
     * Mitteilung)")
     */
    private final boolean gleichrangigOderAdversativAlsoKommaBeiAttributiverVerwendung;

    /**
     * Der Konnektor, mit dem die Adjektiv-Phrasen in jedem Fall
     * (insbesondere die attributiver Verwendung) verbunden werden sollen.
     * Bei einem adversativen Konnektor ("aber", "wenn auch", "sondern"...)
     * muss auch
     * {@link #gleichrangigOderAdversativAlsoKommaBeiAttributiverVerwendung} gesetzt werden:
     * "schön, aber teuer" / "das schöne, aber teuer Kleid"
     */
    @Nullable
    private final String konnektor;
    // FIXME NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld für den
    //  Konnektor verwenden?
    //  Und gleichrangigOderAdversativAlsoKommaBeiAttributiverVerwendung zu
    //  gleichrangigAlsoKommaBeiAttributiverVerwendung vereinfachen?

    public ZweiAdjPhrOhneLeerstellen(
            final AdjPhrOhneLeerstellen erst,
            final boolean gleichrangigOderAdversativAlsoKommaBeiAttributiverVerwendung,
            final AdjPhrOhneLeerstellen zweit) {
        this(erst, gleichrangigOderAdversativAlsoKommaBeiAttributiverVerwendung,
                null, zweit
        );
    }

    public ZweiAdjPhrOhneLeerstellen(
            final AdjPhrOhneLeerstellen erst,
            final boolean gleichrangigOderAdversativAlsoKommaBeiAttributiverVerwendung,
            final @Nullable String konnektor, final AdjPhrOhneLeerstellen zweit) {
        super(erst, zweit);

        this.gleichrangigOderAdversativAlsoKommaBeiAttributiverVerwendung =
                gleichrangigOderAdversativAlsoKommaBeiAttributiverVerwendung;
        this.konnektor = konnektor;
    }

    @Override
    public AdjPhrOhneLeerstellen mitGraduativerAngabe(
            @Nullable final GraduativeAngabe graduativeAngabe) {
        return new ZweiAdjPhrOhneLeerstellen(
                getErst().mitGraduativerAngabe(graduativeAngabe),
                gleichrangigOderAdversativAlsoKommaBeiAttributiverVerwendung, konnektor,
                getZweit()
        );
    }

    @Override
    public AdjPhrOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabe) {
        return new ZweiAdjPhrOhneLeerstellen(
                getErst().mitAdvAngabe(advAngabe),
                gleichrangigOderAdversativAlsoKommaBeiAttributiverVerwendung, konnektor,
                getZweit()
        );
    }

    @Nullable
    @Override
    public String getAttributivAnteilAdjektivattribut(final NumerusGenus numerusGenus,
                                                      final Kasus kasus,
                                                      final boolean artikelwortTraegtKasusendung) {
        @Nullable final String erstesAdjektivattribut =
                getErst().getAttributivAnteilAdjektivattribut(
                        numerusGenus, kasus, artikelwortTraegtKasusendung);

        @Nullable final String zweitesAdjektivattribut =
                getZweit().getAttributivAnteilAdjektivattribut(
                        numerusGenus, kasus, artikelwortTraegtKasusendung);
        // "große", "glücklich, dich zu sehen" ->
        // "(der )große(Mann, glücklich, dich zu sehen)"

        // "glücklich, dich zu sehen", "große" ->
        // "(der )große(Mann, glücklich, dich zu sehen)" (umgedreht)

        final String separator = kommaBeiAdjektivattributen() ?
                // "leichte, herbe Rotweine", "dunkles, sehr bayrisches Bier",
                // "ein schönes, aber teures Kleid"
                ", " + (konnektor != null ? konnektor : "") :
                // "dunkles bayrisches Bier", "wolkenloser blauer Himmel"
                // "sehr dunkles bayrisches Bier", "oft sehr dunkle bayrische Bier"
                // (feste Verbindung aus Substantiv und zweiter Adjektivphrase)
                // "schöne und große Menschen"
                " " + (konnektor != null ? konnektor : "");
        return Joiner.on(separator)
                .skipNulls().join(
                        erstesAdjektivattribut,
                        zweitesAdjektivattribut);
    }

    private boolean kommaBeiAdjektivattributen() {
        return
                // "(das ) dunkle, sehr bayrische( Bier)
                getZweit().hasVorangestellteAngaben()
                        // "heller, herber Rotwein"
                        || gleichrangigOderAdversativAlsoKommaBeiAttributiverVerwendung;
    }

    @Nullable
    @Override
    public Praedikativum getAttributivAnteilRelativsatz(
            final Kasus kasusBezugselement) {
        @Nullable final Praedikativum erstesPraedikativumFuerRelativsatz = getErst()
                .getAttributivAnteilRelativsatz(
                        kasusBezugselement);
        @Nullable final Praedikativum zweitesPraedikativumFuerRelativsatz = getZweit()
                .getAttributivAnteilRelativsatz(
                        kasusBezugselement);

        if (zweitesPraedikativumFuerRelativsatz == null) {
            return erstesPraedikativumFuerRelativsatz;
        }

        if (erstesPraedikativumFuerRelativsatz == null) {
            return zweitesPraedikativumFuerRelativsatz;
        }

        return new ZweiPraedikativa<>(erstesPraedikativumFuerRelativsatz,
                zweitesPraedikativumFuerRelativsatz);
    }

    @Nullable
    @Override
    public AdjPhrOhneLeerstellen getAttributivAnteilLockererNachtrag(
            final Kasus kasusBezugselement) {
        @Nullable final AdjPhrOhneLeerstellen ersterLockererNachtrag =
                getErst().getAttributivAnteilLockererNachtrag(kasusBezugselement);
        @Nullable final AdjPhrOhneLeerstellen zweiterLockererNachtrag =
                getZweit().getAttributivAnteilLockererNachtrag(kasusBezugselement);
        if (zweiterLockererNachtrag == null) {
            return ersterLockererNachtrag;
        }

        if (ersterLockererNachtrag == null) {
            return zweiterLockererNachtrag;
        }

        return new ZweiAdjPhrOhneLeerstellen(ersterLockererNachtrag,
                true,
                konnektor,
                zweiterLockererNachtrag
                // Wenn schon ein lockerer Nachtrag nötig ist, dann ist der zweite
                // lockere Nachtrag bestimmt komplexer ("glücklich, dich zu sehen")
                // Tendenziell wäre allein deshalb schon ein Kommma nötig - und außerdem
                // kann dann diese Adjektivphrasen-Reihung ohnehin nicht direkt als
                // Adjektivattribute verwendet werden.
        );
    }

    @Override
    public Konstituentenfolge getPraedikativOderAdverbial(final Person person,
                                                          final Numerus numerus) {
        return Konstituentenfolge.joinToKonstituentenfolge(
                getErst().getPraedikativ(person, numerus),
                konnektor != null ?
                        (gleichrangigOderAdversativAlsoKommaBeiAttributiverVerwendung ?
                                ", " + konnektor : konnektor) :
                        // Wenn kein Konnektor angegeben ist, schreiben wir "und" -
                        // dann ist kein Komma notwendig, da "und" nicht adversativ ist.
                        "und",
                getZweit().getPraedikativ(person, numerus)
        );

        // IDEA Hier sollte man doppelte zu-Infinitive, Fragesätze etc. verhindern:
        //  "überrascht, dich zu sehen, und gücklich, dich zu sehen[,]" ->
        //  "überrascht und glücklich, dich zu sehen[,]"
    }

    @Override
    public boolean enthaeltZuInfinitivOderAngabensatzOderErgaenzungssatz() {
        return getErst().enthaeltZuInfinitivOderAngabensatzOderErgaenzungssatz() ||
                getZweit().enthaeltZuInfinitivOderAngabensatzOderErgaenzungssatz();
    }

    @Override
    public boolean hasVorangestellteAngaben() {
        return getErst().hasVorangestellteAngaben();
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
        final ZweiAdjPhrOhneLeerstellen that = (ZweiAdjPhrOhneLeerstellen) o;
        return gleichrangigOderAdversativAlsoKommaBeiAttributiverVerwendung
                == that.gleichrangigOderAdversativAlsoKommaBeiAttributiverVerwendung &&
                Objects.equals(konnektor, that.konnektor);
    }

    @Override
    public int hashCode() {
        return Objects
                .hash(super.hashCode(),
                        gleichrangigOderAdversativAlsoKommaBeiAttributiverVerwendung,
                        konnektor);
    }
}
