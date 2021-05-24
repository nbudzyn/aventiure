package de.nb.aventiure2.german.satz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.StructuredDescription;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbWohinWoher;
import de.nb.aventiure2.german.praedikat.Modalpartikel;
import de.nb.aventiure2.german.praedikat.PraedikatOhneLeerstellen;
import de.nb.aventiure2.german.praedikat.ZweiPraedikateOhneLeerstellen;

import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToKonstituentenfolge;
import static de.nb.aventiure2.german.base.NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld.UND;

public class Satzreihe implements Satz {
    private final Satz ersterSatz;

    /**
     * Ob zwischen den Sätzen (vor einem eventuellen Konnektor) möglichst ein Semikolon
     * gesetzt werden soll (sofern grammatikalisch zulässig). Bei {@code false} wird
     * (wenn überhaupt nötig) ein Komma gesetzt.
     */
    private final boolean moeglichstSemikolon;

    // Der Konnektor zwischen den beiden Sätzen ("und", "aber"...) ist einfach nur das
    // Anschlusswort der zweiten Satzes.

    private final EinzelnerSatz zweiterSatz;

    /**
     * Verbindet diese alternativen Sätze zu alternativen Satzreihen und sorgt im Regelfall
     * dafür, dass es jeweils nur ein einzelnes "und" gibt und die Teilsätze davor mit ","
     * verbunden sind.
     */
    @NonNull
    public static ImmutableSet<Satzreihe> altGereihtStandard(final Collection<Satz> altErste,
                                                             final Collection<Satz> altZweite) {
        final ImmutableSet.Builder<Satzreihe> alt = ImmutableSet.builder();
        for (final Satz ersterSatz : altErste) {
            for (final Satz zweiterSatz : altZweite) {
                // FIXME Wenn das Subjekt gleich ist (gleicher Text, gleiches Bezugsobjekt, kein
                //  Personalprononomen-ohne-Bezugsobjekt-außer-Expletivem-Es)
                //  und getPraedikatSofern...() != null
                //  zurückgibt, sollte man Sätze zusammenfassen:
                //  - Statt "Du gehst um die Ecke und du siehst einen Drachen" besser
                //    "Du gehst um die Ecke und siehst einen Drachen"
                //  - Statt "Es ist kalt und es ist windig" besser "Es ist kalt und windig".
                //  (Dann ist Satzreihe wohl nicht mehr der richtige Ort für diese Methode!)

                alt.add(gereihtStandard(ersterSatz, zweiterSatz));
            }
        }

        return alt.build();
    }

    /**
     * Verbindet die Sätze zu einer Satzreihe und sorgt im Regelfall
     * dafür, dass es nur ein einzelnes "und" gibt und die Teilsätze davor mit ","
     * verbunden sind.
     */
    @NonNull
    private static Satzreihe gereihtStandard(final Satz ersterSatz, final Satz zweiterSatz) {
        if (zweiterSatz instanceof EinzelnerSatz) {
            return gereihtStandard(ersterSatz, (EinzelnerSatz) zweiterSatz);
        }

        if (zweiterSatz instanceof Satzreihe) {
            final Satzreihe zweiterSatzAlsSatzreihe = (Satzreihe) zweiterSatz;

            return gereihtStandard(
                    gereihtStandard(ersterSatz, zweiterSatzAlsSatzreihe.ersterSatz),
                    zweiterSatzAlsSatzreihe.moeglichstSemikolon
                            || NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld
                            .traegtBedeutung(
                                    zweiterSatzAlsSatzreihe.ersterSatz.getAnschlusswort()),
                    zweiterSatzAlsSatzreihe.zweiterSatz);
        }

        throw new IllegalStateException("Unexpected subtype of Satz: " + zweiterSatz);
    }

    /**
     * Verbindet die Sätze zu einer Satzreihe und sorgt im Regelfall
     * dafür, dass es nur ein einzelnes "und" gibt und die Teilsätze davor mit ","
     * verbunden sind.
     */
    @NonNull
    private static Satzreihe gereihtStandard(final Satz ersterSatz,
                                             final EinzelnerSatz zweiterSatz) {
        if (ersterSatz instanceof Satzreihe
                && NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld.traegtBedeutung(
                ((Satzreihe) ersterSatz).zweiterSatz.getAnschlusswort())) {
            // "..., aber ...; ..."
            return gereihtStandard(ersterSatz, true, zweiterSatz);
        }

        // "... und ..."
        // "... und ..., aber..."
        // "..., ... und ..."
        return gereihtStandard(ersterSatz, false, zweiterSatz);
    }

    /**
     * Verbindet die Sätze zu einer Satzreihe und sorgt im Regelfall
     * dafür, dass es nur ein einzelnes "und" gibt und die Teilsätze davor mit ","
     * verbunden sind.
     *
     * @param moeglichstSemikolonUndKeinUnd Ob die Sätze möglichst mit einem Semikolon verbunden
     *                                      werden sollen. In diesem Fall wird kein "und"
     *                                      eingefügt, und ein eventuell abschließendes "und" im
     *                                      ersten Satz bleibt erhalten
     */
    @NonNull
    private static Satzreihe gereihtStandard(final Satz ersterSatz,
                                             final boolean moeglichstSemikolonUndKeinUnd,
                                             final EinzelnerSatz zweiterSatz) {
        if (moeglichstSemikolonUndKeinUnd
                || NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld.traegtBedeutung(
                zweiterSatz.getAnschlusswort())) {
            // "...; ..."
            // "... und ..., aber..."
            return new Satzreihe(ersterSatz, moeglichstSemikolonUndKeinUnd, zweiterSatz);
        }

        // "..., ... und ..."
        return new Satzreihe(ohneUndKonnektor(ersterSatz),
                zweiterSatz.mitAnschlusswortUndFallsKeinAnschlusswort());
    }

    @NonNull
    private static Satz ohneUndKonnektor(final Satz satz) {
        if (!(satz instanceof Satzreihe)) {
            return satz;
        }

        return ((Satzreihe) satz).ohneUndKonnektor();
    }

    /**
     * Erzeugt eine neue Satzreihe mit diesem Konnektor. Es ist Verantwortung des Aufrufers,
     * unerwünschte Dopplungen wie "... und ... und ..." oder unschöne
     * Folgen wie "..., aber ..., aber ..." zu vermeiden, indem der erste Satz, wenn er
     * ebenfalls eine Satzreihe ist, z.B. keinen Konnektor enthält.
     */
    public Satzreihe(
            final Satz ersterSatz,
            final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld konnektor,
            final EinzelnerSatz zweiterSatz) {
        this(ersterSatz, false, konnektor, zweiterSatz);
    }

    public Satzreihe(
            final Satz ersterSatz,
            final boolean moeglichstSemikolon,
            final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld konnektor,
            final EinzelnerSatz zweiterSatz) {
        this(ersterSatz, moeglichstSemikolon, zweiterSatz.mitAnschlusswort(konnektor));
    }

    public Satzreihe(final Satz ersterSatz, final EinzelnerSatz zweiterSatz) {
        this(ersterSatz, false, zweiterSatz);
    }

    public Satzreihe(
            final Satz ersterSatz,
            final boolean moeglichstSemikolon,
            final EinzelnerSatz zweiterSatz) {
        this.ersterSatz = ersterSatz;
        this.moeglichstSemikolon = moeglichstSemikolon;
        this.zweiterSatz = zweiterSatz;
    }

    @Override
    public Satzreihe ohneAnschlusswort() {
        return (Satzreihe) Satz.super.ohneAnschlusswort();
    }

    @Override
    public Satzreihe mitAnschlusswort(
            @Nullable final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld anschlusswort) {
        return mapFirst(s -> s.mitAnschlusswort(anschlusswort));
    }

    @Override
    public Satzreihe mitSubjektFokuspartikel(@Nullable final String subjektFokuspartikel) {
        return mapFirst(s -> s.mitSubjektFokuspartikel(subjektFokuspartikel));
    }

    @Override
    public Satzreihe mitModalpartikeln(final Collection<Modalpartikel> modalpartikeln) {
        return mapFirst(s -> s.mitModalpartikeln(modalpartikeln));
    }

    @Override
    public Satzreihe mitAdvAngabe(@Nullable final AdvAngabeSkopusSatz advAngabe) {
        return mapFirst(s -> s.mitAdvAngabe(advAngabe));
    }

    @Override
    public Satzreihe mitAdvAngabe(@Nullable final AdvAngabeSkopusVerbAllg advAngabe) {
        return mapFirst(s -> s.mitAdvAngabe(advAngabe));
    }

    @Override
    public Satzreihe mitAdvAngabe(@Nullable final AdvAngabeSkopusVerbWohinWoher advAngabe) {
        return mapFirst(s -> s.mitAdvAngabe(advAngabe));
    }

    @Override
    public Satzreihe mitAngabensatz(@Nullable final Konditionalsatz angabensatz,
                                    final boolean angabensatzMoeglichstVorangestellt) {
        return mapFirst(s -> s.mitAngabensatz(angabensatz, angabensatzMoeglichstVorangestellt));
    }

    @NonNull
    private Satzreihe mitKonnektorUndFallsKeinKonnektor() {
        return new Satzreihe(
                ersterSatz,
                zweiterSatz.mitAnschlusswortUndFallsKeinAnschlusswort());
    }

    private Satzreihe ohneUndKonnektor() {
        if (zweiterSatz.getAnschlusswort() != UND) {
            return this;
        }

        return new Satzreihe(ersterSatz, moeglichstSemikolon, zweiterSatz.ohneAnschlusswort());
    }

    @Override
    public Satzreihe perfekt() {
        return new Satzreihe(ersterSatz.perfekt(), zweiterSatz.perfekt());
    }

    @Override
    public Konstituentenfolge getIndirekteFrage() {
        return mitKonnektorUndFallsKeinKonnektor()
                .mapJoinToKonstituentenfolge(Satz::getIndirekteFrage, false);
    }

    @Override
    public Konstituentenfolge getRelativsatz() {
        return mitKonnektorUndFallsKeinKonnektor()
                .mapJoinToKonstituentenfolge(Satz::getRelativsatz, false);
    }

    @Override
    public Konstituentenfolge getVerbzweitsatzMitSpeziellemVorfeldAlsWeitereOption() {
        return mapJoinToKonstituentenfolge(
                Satz::getVerbzweitsatzMitSpeziellemVorfeldAlsWeitereOption,
                EinzelnerSatz::getVerbzweitsatzStandard);
    }

    @NonNull
    @Override
    public ImmutableList<Konstituentenfolge> altVerzweitsaetze() {
        final ImmutableList.Builder<Konstituentenfolge> res = ImmutableList.builder();

        for (final Konstituentenfolge ersterVerbzweitsatz : ersterSatz.altVerzweitsaetze()) {
            for (final Konstituentenfolge zweiterVerbzweitsatz : zweiterSatz.altVerzweitsaetze()) {
                res.add(joinToKonstituentenfolge(ersterVerbzweitsatz, zweiterVerbzweitsatz));
            }
        }

        return res.build();
    }

    @Override
    public Konstituentenfolge getVerbzweitsatzStandard() {
        return mapJoinToKonstituentenfolge(Satz::getVerbzweitsatzStandard);
    }

    @Override
    public Konstituentenfolge getVerbzweitsatzMitVorfeld(final String vorfeld) {
        return mapJoinToKonstituentenfolge(
                ersterEinzelnerSatz -> ersterEinzelnerSatz.getVerbzweitsatzMitVorfeld(vorfeld),
                EinzelnerSatz::getVerbzweitsatzStandard);
    }

    @Override
    public Konstituentenfolge getSatzanschlussOhneSubjektOhneAnschlusswortOhneVorkomma() {
        return mapJoinToKonstituentenfolge(
                Satz::getSatzanschlussOhneSubjektOhneAnschlusswortOhneVorkomma, false);
    }

    @Override
    public Konstituentenfolge getSatzanschlussOhneSubjektMitAnschlusswortOderVorkomma() {
        return mapJoinToKonstituentenfolge(
                Satz::getSatzanschlussOhneSubjektMitAnschlusswortOderVorkomma, false);
    }

    @Override
    public Konstituentenfolge getVerbletztsatz() {
        return mitKonnektorUndFallsKeinKonnektor()
                .mapJoinToKonstituentenfolge(Satz::getVerbletztsatz, false);
    }

    @Override
    public boolean hasSubjektDu() {
        return bothMatch(Satz::hasSubjektDu);
    }

    @Nullable
    @Override
    public PraedikatOhneLeerstellen getPraedikatWennOhneInformationsverlustMoeglich() {
        @Nullable final PraedikatOhneLeerstellen erstesPraedikat =
                ersterSatz.getPraedikatWennOhneInformationsverlustMoeglich();
        if (erstesPraedikat == null) {
            return null;
        }

        @Nullable final PraedikatOhneLeerstellen zweitesPraedikatOhneAnschlusswort =
                zweiterSatz.ohneAnschlusswort().getPraedikatWennOhneInformationsverlustMoeglich();
        if (zweitesPraedikatOhneAnschlusswort == null) {
            return null;
        }

        return new ZweiPraedikateOhneLeerstellen(
                erstesPraedikat,
                zweiterSatz.getAnschlusswort(),
                zweitesPraedikatOhneAnschlusswort);
    }

    /**
     * Ob es sich um eine Satzreihung mit dem Konnektor "und" handelt. Oft gibt
     * es bessere Lösungen, als diese Methode hier aufzurufen.
     *
     * @see #mitAnschlusswortUndFallsKeinAnschlusswortUndKeineSatzreihungMitUnd()
     * @see StructuredDescription#toTextDescriptionSatzanschlussMitAnschlusswortOderVorkomma()
     */
    @Override
    public boolean isSatzreihungMitUnd() {
        return zweiterSatz.getAnschlusswort() == UND;
    }

    @Override
    public SubstantivischePhrase getErstesSubjekt() {
        return ersterSatz.getErstesSubjekt();
    }

    @Override
    public boolean hatAngabensatz() {
        return anyMatch(Satz::hatAngabensatz);
    }

    private Konstituentenfolge mapJoinToKonstituentenfolge(
            final Function<Satz, Konstituentenfolge> function) {
        return mapJoinToKonstituentenfolge(function, true);
    }

    private Konstituentenfolge mapJoinToKonstituentenfolge(
            final Function<Satz, Konstituentenfolge> function,
            final boolean semikolonErlaubt) {
        return mapJoinToKonstituentenfolge(function, function::apply, semikolonErlaubt);
    }

    private Konstituentenfolge mapJoinToKonstituentenfolge(
            final Function<Satz, ?> functionFuerErstenSatz,
            final Function<EinzelnerSatz, Konstituentenfolge> functionFuerZweitenSatz) {
        return mapJoinToKonstituentenfolge(functionFuerErstenSatz, functionFuerZweitenSatz, true);
    }

    private Konstituentenfolge mapJoinToKonstituentenfolge(
            final Function<Satz, ?> functionFuerErstenSatz,
            final Function<EinzelnerSatz, Konstituentenfolge> functionFuerZweitenSatz,
            final boolean semikolonErlaubt) {
        return joinToKonstituentenfolge(
                functionFuerErstenSatz.apply(ersterSatz),
                semikolonErlaubt && moeglichstSemikolon ? ";" : null,
                functionFuerZweitenSatz.apply(zweiterSatz)
                        .withVorkommaNoetigMin(!zweiterSatz.hasAnschlusswort()));
    }

    private Satzreihe mapFirst(final Function<Satz, Satz> function) {
        return new Satzreihe(
                function.apply(ersterSatz),
                zweiterSatz);
    }

    private boolean anyMatch(final Predicate<Satz> predicate) {
        return Stream.of(ersterSatz, zweiterSatz).anyMatch(predicate);
    }

    private boolean bothMatch(final Predicate<Satz> predicate) {
        return Stream.of(ersterSatz, zweiterSatz).allMatch(predicate);
    }

    @Override
    @Nullable
    public NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld getAnschlusswort() {
        return ersterSatz.getAnschlusswort();
    }

    @NonNull
    @Override
    public String toString() {
        return getVerbzweitsatzStandard().joinToSingleKonstituente().toTextOhneKontext();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Satzreihe that = (Satzreihe) o;
        return ersterSatz.equals(that.ersterSatz) &&
                moeglichstSemikolon == that.moeglichstSemikolon &&
                zweiterSatz.equals(that.zweiterSatz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ersterSatz, zweiterSatz);
    }
}
