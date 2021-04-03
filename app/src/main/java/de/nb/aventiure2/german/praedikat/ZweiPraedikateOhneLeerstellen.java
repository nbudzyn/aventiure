package de.nb.aventiure2.german.praedikat;

import java.util.Collection;
import java.util.Objects;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativSkopusSatz;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativVerbAllg;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativWohinWoher;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

/**
 * Zwei Prädikate mit Objekt ohne Leerstellen, erzeugen einen
 * <i>zusammengezogenen Satz</i>, in dem das Subjekt im zweiten Teil
 * <i>eingespart</i> ist ("Du hebst die Kugel auf und [du] nimmst ein Bad").
 */
public class ZweiPraedikateOhneLeerstellen
        implements PraedikatOhneLeerstellen {
    private final PraedikatOhneLeerstellen ersterSatz;
    private final PraedikatOhneLeerstellen zweiterSatz;

    public ZweiPraedikateOhneLeerstellen(
            final PraedikatOhneLeerstellen ersterSatz,
            final PraedikatOhneLeerstellen zweiterSatz) {
        this.ersterSatz = ersterSatz;
        this.zweiterSatz = zweiterSatz;
    }

    @Override
    public PraedikatOhneLeerstellen mitModalpartikeln(
            final Collection<Modalpartikel> modalpartikeln) {
        return new ZweiPraedikateOhneLeerstellen(
                ersterSatz.mitModalpartikeln(modalpartikeln),
                zweiterSatz
        );
    }

    @Override
    public ZweiPraedikateOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabe) {
        return new ZweiPraedikateOhneLeerstellen(
                ersterSatz.mitAdvAngabe(advAngabe),
                zweiterSatz
        );
    }

    @Override
    public ZweiPraedikateOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabe) {
        return new ZweiPraedikateOhneLeerstellen(
                ersterSatz.mitAdvAngabe(advAngabe),
                zweiterSatz
        );
    }

    @Override
    public ZweiPraedikateOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativWohinWoher advAngabe) {
        return new ZweiPraedikateOhneLeerstellen(
                ersterSatz.mitAdvAngabe(advAngabe),
                zweiterSatz
        );
    }

    @Override
    public boolean hauptsatzLaesstSichBeiGleichemSubjektMitNachfolgendemVerbzweitsatzZusammenziehen() {
        // Etwas vermeiden wie "Du hebst die Kugel auf und polierst sie und nimmst eine
        // von den Früchten"
        return false;
    }

    @Override
    public Konstituentenfolge getVerbzweit(final Person person, final Numerus numerus) {
        return Konstituentenfolge.joinToKonstituentenfolge(
                // "hebst die goldene Kugel auf"
                ersterSatz.getVerbzweit(person, numerus),
                "und",
                zweiterSatz.getVerbzweit(person, numerus)
                // "nimmst ein Bad"
        );
    }

    @Override
    public Konstituentenfolge getVerbzweitMitSubjektImMittelfeld(
            final SubstantivischePhrase subjekt) {
        return Konstituentenfolge.joinToKonstituentenfolge(
                ersterSatz.getVerbzweitMitSubjektImMittelfeld(subjekt),
                // "ziehst du erst noch eine Weile um die Häuser"
                "und",
                zweiterSatz.getVerbzweit(subjekt.getPerson(), subjekt.getNumerus())
                // "fällst dann todmüde ins Bett."
        );
    }

    @Override
    public Konstituentenfolge getVerbletzt(final Person person, final Numerus numerus) {
        return Konstituentenfolge.joinToKonstituentenfolge(
                ersterSatz.getVerbletzt(person, numerus),
                "und",
                zweiterSatz.getVerbletzt(person, numerus));
    }

    @Override
    public Konstituentenfolge getPartizipIIPhrase(final Person person, final Numerus numerus) {
        return Konstituentenfolge.joinToKonstituentenfolge(
                ersterSatz.getPartizipIIPhrase(person, numerus),
                "und",
                zweiterSatz.getPartizipIIPhrase(person, numerus)
        );
    }

    @Override
    public boolean kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden() {
        return ersterSatz.kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden() &&
                zweiterSatz.kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden();
    }

    @Override
    public Konstituentenfolge getInfinitiv(final Person person, final Numerus numerus) {
        return Konstituentenfolge.joinToKonstituentenfolge(
                ersterSatz.getInfinitiv(person, numerus),
                "und",
                zweiterSatz.getInfinitiv(person, numerus));
    }

    @Override
    public Konstituentenfolge getZuInfinitiv(final Person person, final Numerus numerus) {
        return Konstituentenfolge.joinToKonstituentenfolge(
                ersterSatz.getZuInfinitiv(person, numerus),
                "und",
                zweiterSatz.getZuInfinitiv(person, numerus));
    }

    @Override
    public boolean umfasstSatzglieder() {
        return ersterSatz.umfasstSatzglieder() && zweiterSatz.umfasstSatzglieder();
    }

    @Override
    public boolean bildetPerfektMitSein() {
        return ersterSatz.bildetPerfektMitSein() && zweiterSatz.bildetPerfektMitSein();
    }

    @Override
    public boolean hatAkkusativobjekt() {
        return ersterSatz.hatAkkusativobjekt() && zweiterSatz.hatAkkusativobjekt();
    }

    @Override
    public boolean isBezugAufNachzustandDesAktantenGegeben() {
        return ersterSatz.isBezugAufNachzustandDesAktantenGegeben() &&
                zweiterSatz.isBezugAufNachzustandDesAktantenGegeben();
    }

    @Nullable
    @Override
    public Konstituente getSpeziellesVorfeldSehrErwuenscht(final Person person,
                                                           final Numerus numerus,
                                                           final boolean nachAnschlusswort) {
        return ersterSatz.getSpeziellesVorfeldSehrErwuenscht(person, numerus, nachAnschlusswort);
    }

    @Nullable
    @Override
    public Konstituentenfolge getSpeziellesVorfeldAlsWeitereOption(final Person person,
                                                                   final Numerus numerus) {
        return ersterSatz.getSpeziellesVorfeldAlsWeitereOption(person, numerus);
    }

    @Nullable
    @Override
    public Konstituentenfolge getNachfeld(final Person person, final Numerus numerus) {
        return zweiterSatz.getNachfeld(person, numerus);
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
                ersterSatz.getErstesInterrogativwort();
        @Nullable final Konstituentenfolge erstesInterrogativwortZweiterSatz =
                zweiterSatz.getErstesInterrogativwort();

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
                ersterSatz.getRelativpronomen();
        @Nullable final Konstituentenfolge relativpronomenZweiterSatz =
                zweiterSatz.getRelativpronomen();

        if (Objects.equals(relativpronomenErsterSatz, relativpronomenZweiterSatz)) {
            // Beide gleich, vielleicht auch beide null
            return relativpronomenErsterSatz;
        }

        // Verboten ist etwas wie  *"Sie sieht das Buch, das sie aufhebt und die Kugel mitnimmt."

        return null;
    }

    @Override
    public boolean inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich() {
        // "Mich friert und hungert".
        // Aber: "Es friert mich und regnet."
        return ersterSatz.inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich()
                && zweiterSatz.inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich();
    }
}
