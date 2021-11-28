package de.nb.aventiure2.german.praedikat;

import static de.nb.aventiure2.german.base.NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld.UND;

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
import de.nb.aventiure2.german.base.Negationspartikelphrase;
import de.nb.aventiure2.german.base.PraedRegMerkmale;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

/**
 * Zwei Prädikate mit Objekt ohne Leerstellen, erzeugen einen
 * <i>zusammengezogenen SemSatz</i>, in dem das Subjekt im zweiten Teil
 * <i>eingespart</i> ist ("Du hebst die Kugel auf und [du] nimmst ein Bad").
 */
public class ZweiPraedikateOhneLeerstellenSem
        implements SemPraedikatOhneLeerstellen {
    private final SemPraedikatOhneLeerstellen erstes;

    /**
     * der Konnektor zwischen den beiden Prädikaten:
     * "und", "aber", "oder" oder "sondern"
     */
    @Nullable
    private final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld konnektor;

    private final SemPraedikatOhneLeerstellen zweites;

    public ZweiPraedikateOhneLeerstellenSem(
            final SemPraedikatOhneLeerstellen erstes,
            final SemPraedikatOhneLeerstellen zweites) {
        this(erstes, UND, zweites);
    }

    public ZweiPraedikateOhneLeerstellenSem(
            final SemPraedikatOhneLeerstellen erstes,
            @Nullable final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld konnektor,
            final SemPraedikatOhneLeerstellen zweites) {
        this.erstes = erstes;
        this.konnektor = konnektor;
        this.zweites = zweites;
    }

    @Override
    public ZweiPraedikateOhneLeerstellenSem mitModalpartikeln(
            final Collection<Modalpartikel> modalpartikeln) {
        return new ZweiPraedikateOhneLeerstellenSem(
                erstes.mitModalpartikeln(modalpartikeln),
                konnektor,
                zweites
        );
    }

    @Override
    public ZweiPraedikateOhneLeerstellenSem mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabe) {
        return new ZweiPraedikateOhneLeerstellenSem(
                erstes.mitAdvAngabe(advAngabe),
                konnektor,
                zweites
        );
    }

    @Override
    public ZweiPraedikateOhneLeerstellenSem neg(
            @Nullable final Negationspartikelphrase negationspartikelphrase) {
        return new ZweiPraedikateOhneLeerstellenSem(
                erstes.neg(negationspartikelphrase),
                konnektor,
                zweites.neg(negationspartikelphrase)
        );
    }

    @Override
    public ZweiPraedikateOhneLeerstellenSem mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabe) {
        return new ZweiPraedikateOhneLeerstellenSem(
                erstes.mitAdvAngabe(advAngabe),
                konnektor,
                zweites
        );
    }

    @Override
    public ZweiPraedikateOhneLeerstellenSem mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativWohinWoher advAngabe) {
        return new ZweiPraedikateOhneLeerstellenSem(
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
    public Konstituentenfolge getVerbzweit(final PraedRegMerkmale praedRegMerkmale) {
        return Konstituentenfolge.joinToKonstituentenfolge(
                // "hebst die goldene Kugel auf"
                erstes.getVerbzweit(praedRegMerkmale),
                konnektor,
                zweites.getVerbzweit(praedRegMerkmale).withVorkommaNoetigMin(konnektor == null)
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
                zweites.getVerbzweit(subjekt.getPraedRegMerkmale())
                        .withVorkommaNoetigMin(konnektor == null)
                // "fällst dann todmüde ins Bett."
        );
    }

    @Override
    public Konstituentenfolge getVerbletzt(final PraedRegMerkmale praedRegMerkmale) {
        return Konstituentenfolge.joinToKonstituentenfolge(
                erstes.getVerbletzt(praedRegMerkmale),
                konnektor,
                zweites.getVerbletzt(praedRegMerkmale).withVorkommaNoetigMin(konnektor == null));
    }

    @Override
    public ImmutableList<PartizipIIPhrase> getPartizipIIPhrasen(
            final PraedRegMerkmale praedRegMerkmale) {
        final ImmutableList.Builder<PartizipIIPhrase> res = ImmutableList.builder();

        PartizipIIPhrase tmp = null;
        @Nullable NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld tmpKonnektor = UND;
        for (final PartizipIIPhrase partizipIIPhrase :
                erstes.getPartizipIIPhrasen(praedRegMerkmale)) {
            tmp = PartizipIIPhrase.joinBeiGleicherPerfektbildung(
                    res, tmp, tmpKonnektor, partizipIIPhrase);
            tmpKonnektor = UND;
        }

        tmpKonnektor = konnektor; // "[, ]aber"
        for (final PartizipIIPhrase partizipIIPhrase :
                zweites.getPartizipIIPhrasen(praedRegMerkmale)) {
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
    public Konstituentenfolge getInfinitiv(final PraedRegMerkmale praedRegMerkmale) {
        return Konstituentenfolge.joinToKonstituentenfolge(
                erstes.getInfinitiv(praedRegMerkmale),
                konnektor,
                zweites.getInfinitiv(praedRegMerkmale));
    }

    @Override
    public Konstituentenfolge getZuInfinitiv(final PraedRegMerkmale praedRegMerkmale) {
        return Konstituentenfolge.joinToKonstituentenfolge(
                erstes.getZuInfinitiv(praedRegMerkmale),
                konnektor,
                zweites.getZuInfinitiv(praedRegMerkmale));
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
    public Konstituente getSpeziellesVorfeldSehrErwuenscht(final PraedRegMerkmale praedRegMerkmale,
                                                           final boolean nachAnschlusswort) {
        return erstes.getSpeziellesVorfeldSehrErwuenscht(praedRegMerkmale, nachAnschlusswort);
    }

    @Nullable
    @Override
    public Konstituentenfolge getSpeziellesVorfeldAlsWeitereOption(
            final PraedRegMerkmale praedRegMerkmale) {
        return erstes.getSpeziellesVorfeldAlsWeitereOption(praedRegMerkmale);
    }

    @Nullable
    @Override
    public Konstituentenfolge getNachfeld(final PraedRegMerkmale praedRegMerkmale) {
        return zweites.getNachfeld(praedRegMerkmale);
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
        final ZweiPraedikateOhneLeerstellenSem that = (ZweiPraedikateOhneLeerstellenSem) o;
        return Objects.equals(erstes, that.erstes) &&
                konnektor == that.konnektor &&
                Objects.equals(zweites, that.zweites);
    }

    @Override
    public int hashCode() {
        return Objects.hash(erstes, konnektor, zweites);
    }
}
