package de.nb.aventiure2.german.praedikat;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.Objects;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativSkopusSatz;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativVerbAllg;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativWohinWoher;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static de.nb.aventiure2.german.base.NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld.UND;

/**
 * Zwei Prädikate mit Objekt ohne Leerstellen, erzeugen einen
 * <i>zusammengezogenen Satz</i>, in dem das Subjekt im zweiten Teil
 * <i>eingespart</i> ist ("Du hebst die Kugel auf und [du] nimmst ein Bad").
 */
public class ZweiPraedikateOhneLeerstellen
        implements PraedikatOhneLeerstellen {
    private final PraedikatOhneLeerstellen erstes;

    /**
     * der Konnektor zwischen den beiden Prädikaten:
     * "und", "aber", "oder" oder "sondern"
     */
    @Nullable
    private final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld konnektor;

    private final PraedikatOhneLeerstellen zweites;

    public ZweiPraedikateOhneLeerstellen(
            final PraedikatOhneLeerstellen erstes,
            final PraedikatOhneLeerstellen zweites) {
        this(erstes, UND, zweites);
    }

    public ZweiPraedikateOhneLeerstellen(
            final PraedikatOhneLeerstellen erstes,
            @Nullable final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld konnektor,
            final PraedikatOhneLeerstellen zweites) {
        this.erstes = erstes;
        this.konnektor = konnektor;
        this.zweites = zweites;
    }

    @Override
    public PraedikatOhneLeerstellen mitModalpartikeln(
            final Collection<Modalpartikel> modalpartikeln) {
        return new ZweiPraedikateOhneLeerstellen(
                erstes.mitModalpartikeln(modalpartikeln),
                konnektor,
                zweites
        );
    }

    @Override
    public ZweiPraedikateOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabe) {
        return new ZweiPraedikateOhneLeerstellen(
                erstes.mitAdvAngabe(advAngabe),
                konnektor,
                zweites
        );
    }

    @Override
    public ZweiPraedikateOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabe) {
        return new ZweiPraedikateOhneLeerstellen(
                erstes.mitAdvAngabe(advAngabe),
                konnektor,
                zweites
        );
    }

    @Override
    public ZweiPraedikateOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativWohinWoher advAngabe) {
        return new ZweiPraedikateOhneLeerstellen(
                erstes.mitAdvAngabe(advAngabe),
                konnektor,
                zweites
        );
    }

    @Override
    // FIXME Funktioniert, aber das Konzept schließt
    //  wohl viele Möglichkeiten wie ("... und..., aber..." oder "..., ... und ...."
    //  aus. Konzept verbessern?
    public boolean hauptsatzLaesstSichBeiGleichemSubjektMitNachfolgendemVerbzweitsatzZusammenziehen() {
        // Etwas vermeiden wie "Du hebst die Kugel auf und polierst sie und nimmst eine
        // von den Früchten"

        if (!erstes
                .hauptsatzLaesstSichBeiGleichemSubjektMitNachfolgendemVerbzweitsatzZusammenziehen()) {
            return false;
        }

        if (!zweites
                .hauptsatzLaesstSichBeiGleichemSubjektMitNachfolgendemVerbzweitsatzZusammenziehen()) {
            return false;
        }

        return konnektor == null;
    }

    @Override
    public Konstituentenfolge getVerbzweit(final Person person, final Numerus numerus) {
        return Konstituentenfolge.joinToKonstituentenfolge(
                // "hebst die goldene Kugel auf"
                erstes.getVerbzweit(person, numerus),
                konnektor,
                zweites.getVerbzweit(person, numerus)
                        .withVorkommaNoetigMin(konnektor == null)
                // "nimmst ein Bad"
        );
    }

    @Override
    public Konstituentenfolge getVerbzweitMitSubjektImMittelfeld(
            final SubstantivischePhrase subjekt) {
        return Konstituentenfolge.joinToKonstituentenfolge(
                erstes.getVerbzweitMitSubjektImMittelfeld(subjekt),
                // "ziehst du erst noch eine Weile um die Häuser"
                konnektor,
                zweites.getVerbzweit(subjekt.getPerson(), subjekt.getNumerus())
                        .withVorkommaNoetigMin(konnektor == null)
                // "fällst dann todmüde ins Bett."
        );
    }

    @Override
    public Konstituentenfolge getVerbletzt(final Person person, final Numerus numerus) {
        return Konstituentenfolge.joinToKonstituentenfolge(
                erstes.getVerbletzt(person, numerus),
                konnektor,
                zweites.getVerbletzt(person, numerus)
                        .withVorkommaNoetigMin(konnektor == null));
    }

    @Override
    public ImmutableList<PartizipIIPhrase> getPartizipIIPhrasen(final Person person,
                                                                final Numerus numerus) {
        final ImmutableList.Builder<PartizipIIPhrase> res = ImmutableList.builder();

        PartizipIIPhrase tmp = null;
        @Nullable NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld tmpKonnektor = UND;
        for (final PartizipIIPhrase partizipIIPhrase :
                erstes.getPartizipIIPhrasen(person, numerus)) {
            tmp = PartizipIIPhrase.joinBeiGleicherPerfektbildung(
                    res, tmp, tmpKonnektor, partizipIIPhrase);
            tmpKonnektor = UND;
        }

        tmpKonnektor = konnektor; // "[, ]aber"
        for (final PartizipIIPhrase partizipIIPhrase :
                zweites.getPartizipIIPhrasen(person, numerus)) {
            tmp = PartizipIIPhrase.joinBeiGleicherPerfektbildung(
                    res, tmp, tmpKonnektor, partizipIIPhrase);
            tmpKonnektor = UND;
        }

        return res.build();
    }

    @Override
    public boolean kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden() {
        return erstes.kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden() &&
                zweites.kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden();
    }

    @Override
    public Konstituentenfolge getInfinitiv(final Person person, final Numerus numerus) {
        return Konstituentenfolge.joinToKonstituentenfolge(
                erstes.getInfinitiv(person, numerus),
                konnektor,
                zweites.getInfinitiv(person, numerus));
    }

    @Override
    public Konstituentenfolge getZuInfinitiv(final Person person, final Numerus numerus) {
        return Konstituentenfolge.joinToKonstituentenfolge(
                erstes.getZuInfinitiv(person, numerus),
                konnektor,
                zweites.getZuInfinitiv(person, numerus));
    }

    @Override
    public boolean umfasstSatzglieder() {
        return erstes.umfasstSatzglieder() && zweites.umfasstSatzglieder();
    }

    @Override
    public boolean hatAkkusativobjekt() {
        return erstes.hatAkkusativobjekt() && zweites.hatAkkusativobjekt();
    }

    @Override
    public boolean isBezugAufNachzustandDesAktantenGegeben() {
        return erstes.isBezugAufNachzustandDesAktantenGegeben() &&
                zweites.isBezugAufNachzustandDesAktantenGegeben();
    }

    @Nullable
    @Override
    public Konstituente getSpeziellesVorfeldSehrErwuenscht(final Person person,
                                                           final Numerus numerus,
                                                           final boolean nachAnschlusswort) {
        return erstes.getSpeziellesVorfeldSehrErwuenscht(person, numerus, nachAnschlusswort);
    }

    @Nullable
    @Override
    public Konstituentenfolge getSpeziellesVorfeldAlsWeitereOption(final Person person,
                                                                   final Numerus numerus) {
        return erstes.getSpeziellesVorfeldAlsWeitereOption(person, numerus);
    }

    @Nullable
    @Override
    public Konstituentenfolge getNachfeld(final Person person, final Numerus numerus) {
        return zweites.getNachfeld(person, numerus);
    }

    @Nullable
    @Override
    @CheckReturnValue
    public Konstituentenfolge getErstesInterrogativwort() {
        // Das hier ist etwas tricky.
        // Denkbar wäre so etwas wie "Sie ist gespannt, was du aufhebst und mitnimmmst."
        // Dazu müsste sowohl im aufheben- als auch im mitnehmen-Prädikat dasselbe
        // Interrogativwort angegeben sein.
        @Nullable final Konstituentenfolge erstesInterrogativwortErsterSatz =
                erstes.getErstesInterrogativwort();
        @Nullable final Konstituentenfolge erstesInterrogativwortZweiterSatz =
                zweites.getErstesInterrogativwort();

        if (Objects.equals(
                erstesInterrogativwortErsterSatz, erstesInterrogativwortZweiterSatz)) {
            return erstesInterrogativwortErsterSatz;
        }

        // Verhindern müssen wir so etwas wie *"Sie ist gespannt, was du aufhebst und die Kugel
        // mitnimmmst." - In dem Fall wäre nur eine indirekte ob-Frage gültig:
        // "Sie ist gespannt, ob du was aufhebst und die Kugel mitnimmst."

        return null;
    }

    @Nullable
    @Override
    @CheckReturnValue
    public Konstituentenfolge getRelativpronomen() {
        // Das hier ist etwas tricky.
        // Denkbar wäre so etwas wie "Sie sieht das Buch, das sie aufhebt und mitnimmmt."
        // Dazu müsste sowohl im aufheben- als auch im mitnehmen-Prädikat dasselbe
        // Relativpronomen ("das") angegeben sein.
        @Nullable final Konstituentenfolge relativpronomenErsterSatz =
                erstes.getRelativpronomen();
        @Nullable final Konstituentenfolge relativpronomenZweiterSatz =
                zweites.getRelativpronomen();

        if (Objects.equals(relativpronomenErsterSatz, relativpronomenZweiterSatz)) {
            // Beide gleich, vielleicht auch beide null
            return relativpronomenErsterSatz;
        }

        // Verboten ist etwas wie  *"Sie sieht das Buch, das sie aufhebt und die Kugel
        // mitnimmt."

        return null;
    }

    @Override
    public boolean inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich() {
        // "Mich friert und hungert".
        // Aber: "Es friert mich und regnet."
        return erstes.inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich()
                && zweites.inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ZweiPraedikateOhneLeerstellen that = (ZweiPraedikateOhneLeerstellen) o;
        return Objects.equals(erstes, that.erstes) &&
                konnektor == that.konnektor &&
                Objects.equals(zweites, that.zweites);
    }

    @Override
    public int hashCode() {
        return Objects.hash(erstes, konnektor, zweites);
    }
}
