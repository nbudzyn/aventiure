package de.nb.aventiure2.german.praedikat;


import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.Objects;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.nb.aventiure2.annotations.Komplement;
import de.nb.aventiure2.annotations.Valenz;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativSkopusSatz;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativVerbAllg;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativWohinWoher;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.Negationspartikelphrase;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static java.util.Objects.requireNonNull;

/**
 * Ein Prädikat mit einem Modalverb, bei dem alle Leerstellen gefüllt sind
 * (z.B. <i>schlafen müssen</i>,  <i>einen Apfel essen wollen</i>).
 */
public class PraedikatModalverbOhneLeerstellen implements PraedikatOhneLeerstellen {
    /**
     * Das Modalverb an sich, ohne Informationen zur Valenz, ohne Ergänzungen, ohne
     * Angaben
     */
    @NonNull
    private final Verb verb;

    /**
     * Das Prädikat in seiner "ursprünglichen" Form (ohne "können", "möchten", ...). Die
     * "ursprüngliche Form" von <i>laufen können</i> ist z.B.
     * <i>laufen</i>.
     */
    @Nonnull
    @Komplement
    private final PraedikatOhneLeerstellen lexikalischerKern;

    @Valenz
    PraedikatModalverbOhneLeerstellen(
            final Verb verb,
            final PraedikatOhneLeerstellen lexikalischerKern) {
        this.verb = verb;
        this.lexikalischerKern = lexikalischerKern;
    }

    @Override
    public PraedikatModalverbOhneLeerstellen mitModalpartikeln(
            final Collection<Modalpartikel> modalpartikeln) {
        // Der Einfachheit halber tun wir so, als würde sich das "halt"
        // bei "halt singen können" auf das Singen beziehen.
        // Das ist eine Vereinfachung. Jedenfalls ist ein doppeltes "halt" unmöglich.
        return new PraedikatModalverbOhneLeerstellen(
                verb, lexikalischerKern.mitModalpartikeln(modalpartikeln));
    }

    @Override
    public PraedikatModalverbOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabe) {
        // Der Einfachheit halber tun wir so, als würde sich das "leider"
        // bei "leider singen können" auf das Singen beziehen.
        // Das ist eine Vereinfachung. Jedenfalls ist ein doppeltes "leider" unmöglich.
        return new PraedikatModalverbOhneLeerstellen(
                verb, lexikalischerKern.mitAdvAngabe(advAngabe));
    }

    @Override
    public PraedikatModalverbOhneLeerstellen neg(
            @Nullable final Negationspartikelphrase negationspartikelphrase) {
        // Der Einfachheit halber tun wir so, als würde sich die Negationspartikel(phrase)
        // bei "nicht singen können" auf das Singen beziehen.
        // Das ist eine Vereinfachung. Jedenfalls wäre ein doppeltes "nicht" sehr ungebräuchlich.
        return new PraedikatModalverbOhneLeerstellen(
                verb, lexikalischerKern.neg(negationspartikelphrase));
    }

    @Override
    public PraedikatModalverbOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabe) {
        return new PraedikatModalverbOhneLeerstellen(
                verb, lexikalischerKern.mitAdvAngabe(advAngabe));
    }

    @Override
    public PraedikatModalverbOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativWohinWoher advAngabe) {
        return new PraedikatModalverbOhneLeerstellen(
                verb, lexikalischerKern.mitAdvAngabe(advAngabe));
    }

    @Nullable
    @Override
    @CheckReturnValue
    public Konstituentenfolge getErstesInterrogativwort() {
        return lexikalischerKern.getErstesInterrogativwort();
    }

    @Nullable
    @Override
    @CheckReturnValue
    public Konstituentenfolge getRelativpronomen() {
        return lexikalischerKern.getRelativpronomen();
    }

    @Override
    public boolean hauptsatzLaesstSichBeiGleichemSubjektMitNachfolgendemVerbzweitsatzZusammenziehen() {
        // Diese Einschränkung hängt wohl hauptsächlich vom Nachfeld ab, dass
        // auch mit Modalverb das Nachfeld wird:
        // Du sagst: "Hallo!" -> Du möchest sagen: "Hallo!"

        return lexikalischerKern
                .hauptsatzLaesstSichBeiGleichemSubjektMitNachfolgendemVerbzweitsatzZusammenziehen();
    }

    @Override
    public Konstituentenfolge getVerbzweit(final Person person, final Numerus numerus) {
        // möchtest Spannendes berichten
        // kannst dich waschen
        // möchtest sagen: "Hallo!"

        return Konstituentenfolge.joinToKonstituentenfolge(
                requireNonNull(verb.getPraesensOhnePartikel(person, numerus)), // "möchtest"
                lexikalischerKern.getInfinitiv(person, numerus)); // "dich waschen"
    }

    @Override
    public Konstituentenfolge getVerbzweitMitSubjektImMittelfeld(
            final SubstantivischePhrase subjekt) {
        // möchtest du Spannendes  berichten
        // möchtest du dich  waschen
        // möchtest du sagen: "Hallo!"

        return Konstituentenfolge.joinToKonstituentenfolge(
                requireNonNull(verb.getPraesensOhnePartikel(subjekt)), // "möchtest"
                subjekt.nomK(), // "du"
                lexikalischerKern.getInfinitiv(
                        subjekt.getPerson(), subjekt.getNumerus())); // "dich waschen"
    }

    @Override
    public Konstituentenfolge getVerbletzt(final Person person, final Numerus numerus) {
        // Spannendes berichten möchtest
        // dich waschen möchtest
        // sagen möchtest: "Hallo!"

        @Nullable final Konstituentenfolge nachfeld = getNachfeld(person, numerus);

        return Konstituentenfolge.joinToKonstituentenfolge(
                lexikalischerKern.getInfinitiv(person, numerus).cutLast(
                        // "Spannendes berichten"
                        nachfeld),
                requireNonNull(verb.getPraesensMitPartikel(person, numerus)), // "möchtest"
                nachfeld); // : Odysseus ist zurück.
    }

    @Override
    public ImmutableList<PartizipIIPhrase> getPartizipIIPhrasen(final Person person,
                                                                final Numerus numerus) {
        // Spannendes berichten wollen (Ersatzinfinitiv!)
        // dich waschen wollen
        // sagen wollen: "Hallo!"

        @Nullable final Konstituentenfolge nachfeld = getNachfeld(person, numerus);

        return ImmutableList.of(new PartizipIIPhrase(
                Konstituentenfolge.joinToKonstituentenfolge(
                        lexikalischerKern.getInfinitiv(person, numerus).cutLast(
                                // "Spannendesberichten"
                                nachfeld),
                        verb.getPartizipII(), // "wollen" (Partizip II ist bereits Ersatzinfinitiv!)
                        nachfeld), // : Odysseus ist zurück.
                verb.getPerfektbildung()));
    }

    @Override
    public boolean kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden() {
        // Auch diese Einschränkung hängt wohl hauptsächlich vom Nachfeld ab, dass
        // auch mit einem Modalverb das Nachfeld wird:
        // gesagt: "Hallo!" -> sagen gewollt: "Hallo!"

        return lexikalischerKern.kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden();
    }

    @Override
    public Konstituentenfolge getInfinitiv(final Person person, final Numerus numerus) {
        // Spannendes berichten wollen
        // dich waschen wollen
        // sagen wollen: "Hallo!"

        @Nullable final Konstituentenfolge nachfeld = getNachfeld(person, numerus);

        return Konstituentenfolge.joinToKonstituentenfolge(
                lexikalischerKern.getInfinitiv(person, numerus).cutLast(
                        // "Spannendes berichten"
                        nachfeld),
                verb.getInfinitiv(), // wollen
                nachfeld); // : Odysseus ist zurück.
    }

    @Override
    public Konstituentenfolge getZuInfinitiv(final Person person, final Numerus numerus) {
        // Spannendes berichten zu wollen
        // dich waschen zu wollen
        // sagen zu wollen: "Hallo!"
        @Nullable final Konstituentenfolge nachfeld = getNachfeld(person, numerus);

        return Konstituentenfolge.joinToKonstituentenfolge(
                lexikalischerKern.getInfinitiv(person, numerus).cutLast(
                        // "Spannendes berichten"
                        nachfeld),
                verb.getZuInfinitiv(), // zu wollen
                nachfeld); // : Odysseus ist zurück.
    }

    @Override
    public boolean umfasstSatzglieder() {
        return lexikalischerKern.umfasstSatzglieder();
    }

    @Override
    public boolean hatAkkusativobjekt() {
        return lexikalischerKern.hatAkkusativobjekt();
    }

    @Override
    public boolean isBezugAufNachzustandDesAktantenGegeben() {
        // Bei "Du musst für immer bei mir bleiben" oder "Du möchtest das Gefängnis verlassen"
        // ist nicht unbedingt ein Bezug auf den Nachzustand gegeben, weil es sich tendenziell nur
        // um ein "Handlungsziel" handelt, nicht unbedingt um eine Aktion.
        return false;
    }

    @Nullable
    @Override
    public Konstituente getSpeziellesVorfeldSehrErwuenscht(final Person person,
                                                           final Numerus numerus,
                                                           final boolean nachAnschlusswort) {
        // "Danach möchtest du Spannendes berichten."
        return lexikalischerKern.getSpeziellesVorfeldSehrErwuenscht(person, numerus,
                nachAnschlusswort);
    }

    @Nullable
    @Override
    public Konstituentenfolge getSpeziellesVorfeldAlsWeitereOption(final Person person,
                                                                   final Numerus numerus) {
        // "Spannendes möchest du berichten."
        return lexikalischerKern.getSpeziellesVorfeldAlsWeitereOption(person, numerus
        );
    }

    @Nullable
    @Override
    public Konstituentenfolge getNachfeld(final Person person, final Numerus numerus) {
        return lexikalischerKern.getNachfeld(person, numerus);
    }

    @Override
    public boolean inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich() {
        // "Mich muss frieren""
        return lexikalischerKern.inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final PraedikatModalverbOhneLeerstellen that = (PraedikatModalverbOhneLeerstellen) o;
        return verb.equals(that.verb) &&
                lexikalischerKern.equals(that.lexikalischerKern);
    }

    @Override
    public int hashCode() {
        return Objects.hash(verb, lexikalischerKern);
    }
}
