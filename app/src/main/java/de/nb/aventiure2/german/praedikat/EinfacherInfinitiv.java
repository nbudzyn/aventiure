package de.nb.aventiure2.german.praedikat;

import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToKonstituentenfolge;

import androidx.annotation.NonNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld;

/**
 * Eine einfache Infinitivkonstruktion ("den Frosch ignorieren", "das Leben genießen") ohne "zu".
 */
public class EinfacherInfinitiv extends AbstractEinfacherInfinitiv implements Infinitiv {
    private final Perfektbildung perfektbildung;

    EinfacherInfinitiv(
            @Nullable final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld konnektor,
            final Verb verb) {
        this(konnektor, TopolFelder.EMPTY, verb);
    }

    EinfacherInfinitiv(
            @Nullable final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld konnektor,
            final TopolFelder topolFelder,
            final Verb verb) {
        super(// "danach Spannendes" / ": Odysseus ist zurück."
                konnektor, topolFelder,
                verb.getInfinitiv() // "berichten"
        );

        perfektbildung = verb.getPerfektbildung();
    }

    private EinfacherInfinitiv(
            @Nullable final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld konnektor,
            final TopolFelder topolFelder, final String verbalkomplex,
            final Perfektbildung perfektbildung) {
        super(konnektor, topolFelder, verbalkomplex);

        this.perfektbildung = perfektbildung;
    }

    @Override
    public EinfacherInfinitiv mitKonnektorUndFallsKeinKonnektor() {
        if (getKonnektor() != null) {
            return this;
        }

        return mitKonnektor(NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld.UND);
    }

    @Override
    public EinfacherInfinitiv mitKonnektor(
            @Nullable final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld konnektor) {
        if (konnektor == null) {
            return this;
        }

        return new EinfacherInfinitiv(konnektor,
                getTopolFelder(),
                getVerbalkomplex(),
                perfektbildung);
    }

    @Override
    public EinfacherInfinitiv ohneKonnektor() {
        if (getKonnektor() == null) {
            return this;
        }

        return new EinfacherInfinitiv(null,
                getTopolFelder(),
                getVerbalkomplex(),
                perfektbildung);
    }

    @Override
    public boolean finiteVerbformBeiVerbletztstellungImOberfeld() {
        // "Zu der Abfolgeregel des Finitums am Ende gibt es folgende Ausnahme: Die finite
        // Form des Hilfsverbs haben steht - bei zwei oder drei Infinitiven - nicht am Ende,
        // sondern am
        // Anfang des gesamten Verbalkomplexes."
        // ( https://grammis.ids-mannheim.de/systematische-grammatik/1241 )
        return false;
    }

    @Override
    public Konstituentenfolge toKonstituentenfolgeOhneNachfeld(
            @Nullable final String finiteVerbformFuerOberfeld,
            final boolean nachfeldEingereiht) {
        return joinToKonstituentenfolge(
                getKonnektor(),
                getMittelfeld(),
                nachfeldEingereiht ? getNachfeld() : null,
                finiteVerbformFuerOberfeld, // "hat"
                getVerbalkomplex()); // "laufen"
        // (wollen)
    }

    @NonNull
    @Override
    public Perfektbildung getPerfektbildung() {
        // Betrifft nur die Modalverben, wo der Infinitiv ein Ersatzinfinitiv ist.
        // "Er hat glücklich sein wollen"
        return perfektbildung;
    }

    @Override
    @NonNull
    public Mittelfeld getMittelfeld() {
        return getTopolFelder().getMittelfeld();
    }

    @NonNull
    @Override
    public String getVerbalkomplex() {
        return super.getVerbalkomplex();
    }

    @Nonnull
    @NonNull
    @Override
    public Nachfeld getNachfeld() {
        return getTopolFelder().getNachfeld();
    }

    @Override
    @Nullable
    public Vorfeld getSpeziellesVorfeldSehrErwuenscht() {
        return getTopolFelder().getSpeziellesVorfeldSehrErwuenscht();
    }

    @Override
    @Nullable
    public Vorfeld getSpeziellesVorfeldAlsWeitereOption() {
        return getTopolFelder().getSpeziellesVorfeldAlsWeitereOption();
    }

    @Override
    @Nullable
    public Konstituentenfolge getRelativpronomen() {
        return getTopolFelder().getRelativpronomen();
    }

    @Override
    @Nullable
    public Konstituentenfolge getErstesInterrogativwort() {
        return getTopolFelder().getErstesInterrogativwort();
    }

    @Nullable
    @Override
    public Integer getAnzahlGeschachtelteReineInfinitiveWennPhraseNichtsAnderesEnthaelt() {
        return 1; // (nur this selbst)
    }
}
