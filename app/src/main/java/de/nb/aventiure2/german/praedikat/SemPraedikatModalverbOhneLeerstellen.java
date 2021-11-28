package de.nb.aventiure2.german.praedikat;


import static java.util.Objects.requireNonNull;

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
import de.nb.aventiure2.german.base.PraedRegMerkmale;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

/**
 * Ein "semantisches Prädikat" mit einem Modalverb, bei dem alle Leerstellen gefüllt sind
 * (z.B. <i>schlafen müssen</i>,  <i>einen Apfel essen wollen</i>).
 */
public class SemPraedikatModalverbOhneLeerstellen implements SemPraedikatOhneLeerstellen {
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
    private final SemPraedikatOhneLeerstellen lexikalischerKern;

    @Valenz
    SemPraedikatModalverbOhneLeerstellen(
            final Verb verb,
            final SemPraedikatOhneLeerstellen lexikalischerKern) {
        this.verb = verb;
        this.lexikalischerKern = lexikalischerKern;
    }

    @Override
    public SemPraedikatModalverbOhneLeerstellen mitModalpartikeln(
            final Collection<Modalpartikel> modalpartikeln) {
        // Der Einfachheit halber tun wir so, als würde sich das "halt"
        // bei "halt singen können" auf das Singen beziehen.
        // Das ist eine Vereinfachung. Jedenfalls ist ein doppeltes "halt" unmöglich.
        return new SemPraedikatModalverbOhneLeerstellen(
                verb, lexikalischerKern.mitModalpartikeln(modalpartikeln));
    }

    @Override
    public SemPraedikatModalverbOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabe) {
        // Der Einfachheit halber tun wir so, als würde sich das "leider"
        // bei "leider singen können" auf das Singen beziehen.
        // Das ist eine Vereinfachung. Jedenfalls ist ein doppeltes "leider" unmöglich.
        return new SemPraedikatModalverbOhneLeerstellen(
                verb, lexikalischerKern.mitAdvAngabe(advAngabe));
    }

    @Override
    public SemPraedikatModalverbOhneLeerstellen neg(
            @Nullable final Negationspartikelphrase negationspartikelphrase) {
        // Der Einfachheit halber tun wir so, als würde sich die Negationspartikel(phrase)
        // bei "nicht singen können" auf das Singen beziehen.
        // Das ist eine Vereinfachung. Jedenfalls wäre ein doppeltes "nicht" sehr ungebräuchlich.
        return new SemPraedikatModalverbOhneLeerstellen(
                verb, lexikalischerKern.neg(negationspartikelphrase));
    }

    @Override
    public SemPraedikatModalverbOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabe) {
        return new SemPraedikatModalverbOhneLeerstellen(
                verb, lexikalischerKern.mitAdvAngabe(advAngabe));
    }

    @Override
    public SemPraedikatModalverbOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativWohinWoher advAngabe) {
        return new SemPraedikatModalverbOhneLeerstellen(
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
    public Konstituentenfolge getVerbzweit(final PraedRegMerkmale praedRegMerkmale) {
        // möchtest Spannendes berichten
        // kannst dich waschen
        // möchtest sagen: "Hallo!"

        return Konstituentenfolge.joinToKonstituentenfolge(
                requireNonNull(verb.getPraesensOhnePartikel(
                        praedRegMerkmale.getPerson(), praedRegMerkmale.getNumerus())), // "möchtest"
                lexikalischerKern.getInfinitiv(praedRegMerkmale)); // "dich waschen"
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
                lexikalischerKern.getInfinitiv(subjekt)); // "dich waschen"
    }

    @Override
    public Konstituentenfolge getVerbletzt(final PraedRegMerkmale praedRegMerkmale) {
        // Spannendes berichten möchtest
        // dich waschen möchtest
        // sagen möchtest: "Hallo!"

        @Nullable final Konstituentenfolge nachfeld = getNachfeld(praedRegMerkmale);

        return Konstituentenfolge.joinToKonstituentenfolge(
                lexikalischerKern.getInfinitiv(praedRegMerkmale).cutLast(
                        // "Spannendes berichten"
                        nachfeld),
                requireNonNull(verb.getPraesensMitPartikel(
                        praedRegMerkmale.getPerson(), praedRegMerkmale.getNumerus())), // "möchtest"
                nachfeld); // : Odysseus ist zurück.
    }

    @Override
    public ImmutableList<PartizipIIPhrase> getPartizipIIPhrasen(
            final PraedRegMerkmale praedRegMerkmale) {
        // Spannendes berichten wollen (Ersatzinfinitiv!)
        // dich waschen wollen
        // sagen wollen: "Hallo!"

        @Nullable final Konstituentenfolge nachfeld = getNachfeld(praedRegMerkmale);

        return ImmutableList.of(new PartizipIIPhrase(
                Konstituentenfolge.joinToKonstituentenfolge(
                        lexikalischerKern.getInfinitiv(praedRegMerkmale).cutLast(
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
    public Konstituentenfolge getInfinitiv(final PraedRegMerkmale praedRegMerkmale) {
        // Spannendes berichten wollen
        // dich waschen wollen
        // sagen wollen: "Hallo!"

        @Nullable final Konstituentenfolge nachfeld = getNachfeld(praedRegMerkmale);

        return Konstituentenfolge.joinToKonstituentenfolge(
                lexikalischerKern.getInfinitiv(praedRegMerkmale).cutLast(
                        // "Spannendes berichten"
                        nachfeld),
                verb.getInfinitiv(), // wollen
                nachfeld); // : Odysseus ist zurück.
    }

    @Override
    public Konstituentenfolge getZuInfinitiv(final PraedRegMerkmale praedRegMerkmale) {
        // Spannendes berichten zu wollen
        // dich waschen zu wollen
        // sagen zu wollen: "Hallo!"
        @Nullable final Konstituentenfolge nachfeld = getNachfeld(praedRegMerkmale);

        return Konstituentenfolge.joinToKonstituentenfolge(
                lexikalischerKern.getInfinitiv(praedRegMerkmale).cutLast(
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
    public Konstituente getSpeziellesVorfeldSehrErwuenscht(final PraedRegMerkmale praedRegMerkmale,
                                                           final boolean nachAnschlusswort) {
        // "Danach möchtest du Spannendes berichten."
        return lexikalischerKern.getSpeziellesVorfeldSehrErwuenscht(praedRegMerkmale,
                nachAnschlusswort);
    }

    @Nullable
    @Override
    public Konstituentenfolge getSpeziellesVorfeldAlsWeitereOption(
            final PraedRegMerkmale praedRegMerkmale) {
        // "Spannendes möchest du berichten."
        return lexikalischerKern.getSpeziellesVorfeldAlsWeitereOption(praedRegMerkmale);
    }

    @Nullable
    @Override
    public Konstituentenfolge getNachfeld(final PraedRegMerkmale praedRegMerkmale) {
        return lexikalischerKern.getNachfeld(praedRegMerkmale);
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
        final SemPraedikatModalverbOhneLeerstellen that = (SemPraedikatModalverbOhneLeerstellen) o;
        return verb.equals(that.verb) &&
                lexikalischerKern.equals(that.lexikalischerKern);
    }

    @Override
    public int hashCode() {
        return Objects.hash(verb, lexikalischerKern);
    }
}
