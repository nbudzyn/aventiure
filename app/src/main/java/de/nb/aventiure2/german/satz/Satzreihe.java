package de.nb.aventiure2.german.satz;

import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToKonstituentenfolge;
import static de.nb.aventiure2.german.base.NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld.UND;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativSkopusSatz;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativVerbAllg;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativWohinWoher;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.StructuredDescription;
import de.nb.aventiure2.german.praedikat.Modalpartikel;
import de.nb.aventiure2.german.praedikat.SemPraedikatOhneLeerstellen;
import de.nb.aventiure2.german.praedikat.ZweiPraedikateOhneLeerstellenSem;

public class Satzreihe implements SemSatz {
    private final SemSatz ersterSemSatz;

    /**
     * Ob zwischen den Sätzen (vor einem eventuellen Konnektor) möglichst ein Semikolon
     * gesetzt werden soll (sofern grammatikalisch zulässig). Bei {@code false} wird
     * (wenn überhaupt nötig) ein Komma gesetzt.
     */
    private final boolean moeglichstSemikolon;

    // Der Konnektor zwischen den beiden Sätzen ("und", "aber"...) ist einfach nur das
    // Anschlusswort der zweiten Satzes.

    private final EinzelnerSemSatz zweiterSatz;

    /**
     * Verbindet diese alternativen Sätze zu alternativen Satzreihen und sorgt im Regelfall
     * dafür, dass es jeweils nur ein einzelnes "und" gibt und die Teilsätze davor mit ","
     * verbunden sind.
     */
    @NonNull
    public static ImmutableSet<Satzreihe> altGereihtStandard(final Collection<SemSatz> altErste,
                                                             final Collection<SemSatz> altZweite) {
        final ImmutableSet.Builder<Satzreihe> alt = ImmutableSet.builder();
        for (final SemSatz ersterSemSatz : altErste) {
            for (final SemSatz zweiterSemSatz : altZweite) {
                // FIXME Wenn das Subjekt gleich ist (gleicher Text, gleiches Bezugsobjekt, kein
                //  Personalprononomen-ohne-Bezugsobjekt-außer-Expletivem-Es)
                //  und getPraedikatSofern...() != null
                //  zurückgibt, sollte man die *Prädikate* reihen
                //  (vgl. ZweiPraedikateOhneLeerstellenSem):
                //  - Statt "Du gehst um die Ecke und du siehst einen Drachen" besser
                //    "Du gehst um die Ecke und siehst einen Drachen"
                //  - Statt "Es ist kalt und es ist windig" besser "Es ist kalt und windig".
                //  (Dann ist Satzreihe wohl nicht mehr der richtige Ort für diese Methode!)

                alt.add(gereihtStandard(ersterSemSatz, zweiterSemSatz));
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
    static Satzreihe gereihtStandard(final SemSatz ersterSemSatz, final SemSatz zweiterSemSatz) {
        if (zweiterSemSatz instanceof EinzelnerSemSatz) {
            return gereihtStandard(ersterSemSatz, (EinzelnerSemSatz) zweiterSemSatz);
        }

        if (zweiterSemSatz instanceof Satzreihe) {
            final Satzreihe zweiterSatzAlsSatzreihe = (Satzreihe) zweiterSemSatz;

            return gereihtStandard(
                    gereihtStandard(ersterSemSatz, zweiterSatzAlsSatzreihe.ersterSemSatz),
                    zweiterSatzAlsSatzreihe.moeglichstSemikolon
                            || NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld
                            .traegtBedeutung(
                                    zweiterSatzAlsSatzreihe.ersterSemSatz.getAnschlusswort()),
                    zweiterSatzAlsSatzreihe.zweiterSatz);
        }

        throw new IllegalStateException("Unexpected subtype of SemSatz: " + zweiterSemSatz);
    }

    /**
     * Verbindet die Sätze zu einer Satzreihe und sorgt im Regelfall
     * dafür, dass es nur ein einzelnes "und" gibt und die Teilsätze davor mit ","
     * verbunden sind.
     */
    @NonNull
    private static Satzreihe gereihtStandard(final SemSatz ersterSemSatz,
                                             final EinzelnerSemSatz zweiterSatz) {
        if (ersterSemSatz instanceof Satzreihe
                && NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld.traegtBedeutung(
                ((Satzreihe) ersterSemSatz).zweiterSatz.getAnschlusswort())) {
            // "..., aber ...; ..."
            return gereihtStandard(ersterSemSatz, true, zweiterSatz);
        }

        // "... und ..."
        // "... und ..., aber..."
        // "..., ... und ..."
        return gereihtStandard(ersterSemSatz, false, zweiterSatz);
    }

    /**
     * Verbindet die Sätze zu einer Satzreihe und sorgt im Regelfall
     * dafür, dass es nur ein einzelnes "und" gibt und die Teilsätze davor mit ","
     * verbunden sind.
     *
     * @param moeglichstSemikolonUndKeinUnd Ob die Sätze möglichst mit einem Semikolon verbunden
     *                                      werden sollen. In diesem Fall wird kein "und"
     *                                      eingefügt, und ein eventuell abschließendes "und" im
     *                                      ersten SemSatz bleibt erhalten
     */
    @NonNull
    private static Satzreihe gereihtStandard(final SemSatz ersterSemSatz,
                                             final boolean moeglichstSemikolonUndKeinUnd,
                                             final EinzelnerSemSatz zweiterSatz) {
        if (moeglichstSemikolonUndKeinUnd
                || NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld.traegtBedeutung(
                zweiterSatz.getAnschlusswort())) {
            // "...; ..."
            // "... und ..., aber..."
            return new Satzreihe(ersterSemSatz, moeglichstSemikolonUndKeinUnd, zweiterSatz);
        }

        // "..., ... und ..."
        return new Satzreihe(ohneUndKonnektor(ersterSemSatz),
                zweiterSatz.mitAnschlusswortUndFallsKeinAnschlusswort());
    }

    @NonNull
    private static SemSatz ohneUndKonnektor(final SemSatz semSatz) {
        if (!(semSatz instanceof Satzreihe)) {
            return semSatz;
        }

        return ((Satzreihe) semSatz).ohneUndKonnektor();
    }

    /**
     * Erzeugt eine neue Satzreihe mit diesem Konnektor. Es ist Verantwortung des Aufrufers,
     * unerwünschte Dopplungen wie "... und ... und ..." oder unschöne
     * Folgen wie "..., aber ..., aber ..." zu vermeiden, indem der erste SemSatz, wenn er
     * ebenfalls eine Satzreihe ist, z.B. keinen Konnektor enthält.
     */
    public Satzreihe(
            final SemSatz ersterSemSatz,
            final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld konnektor,
            final EinzelnerSemSatz zweiterSatz) {
        this(ersterSemSatz, false, konnektor, zweiterSatz);
    }

    private Satzreihe(
            final SemSatz ersterSemSatz,
            final boolean moeglichstSemikolon,
            final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld konnektor,
            final EinzelnerSemSatz zweiterSatz) {
        this(ersterSemSatz, moeglichstSemikolon, zweiterSatz.mitAnschlusswort(konnektor));
    }

    public Satzreihe(final SemSatz ersterSemSatz, final EinzelnerSemSatz zweiterSatz) {
        this(ersterSemSatz, false, zweiterSatz);
    }

    public Satzreihe(
            final SemSatz ersterSemSatz,
            final boolean moeglichstSemikolon,
            final EinzelnerSemSatz zweiterSatz) {
        this.ersterSemSatz = ersterSemSatz;
        this.moeglichstSemikolon = moeglichstSemikolon;
        this.zweiterSatz = zweiterSatz;
    }

    @Override
    public Satzreihe ohneAnschlusswort() {
        return (Satzreihe) SemSatz.super.ohneAnschlusswort();
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
    public Satzreihe mitAdvAngabe(@Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabe) {
        return mapFirst(s -> s.mitAdvAngabe(advAngabe));
    }

    @Override
    public Satzreihe mitAdvAngabe(@Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabe) {
        return mapFirst(s -> s.mitAdvAngabe(advAngabe));
    }

    @Override
    public Satzreihe mitAdvAngabe(@Nullable final IAdvAngabeOderInterrogativWohinWoher advAngabe) {
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
                ersterSemSatz,
                zweiterSatz.mitAnschlusswortUndFallsKeinAnschlusswort());
    }

    private Satzreihe ohneUndKonnektor() {
        if (zweiterSatz.getAnschlusswort() != UND) {
            return this;
        }

        return new Satzreihe(ersterSemSatz, moeglichstSemikolon, zweiterSatz.ohneAnschlusswort());
    }

    @Override
    public Satzreihe perfekt() {
        return new Satzreihe(ersterSemSatz.perfekt(), zweiterSatz.perfekt());
    }

    @Override
    public Konstituentenfolge getIndirekteFrage() {
        @Nullable final Konstituentenfolge zusammengefassteIndirekteFrageWennMoeglich =
                getZusammengefassteIndirekteFrageWennMoeglich();

        if (zusammengefassteIndirekteFrageWennMoeglich != null) {
            return zusammengefassteIndirekteFrageWennMoeglich;
        }

        // "wie die Grillen zirpen und ob und der Wind rauscht"
        return mitKonnektorUndFallsKeinKonnektor()
                .mapJoinToKonstituentenfolge(SemSatz::getIndirekteFrage, false);
    }

    @Nullable
    private Konstituentenfolge getZusammengefassteIndirekteFrageWennMoeglich() {
        @Nullable final SemPraedikatOhneLeerstellen
                erstesPraedikatWennOhneInformationsverlustMoeglich =
                ersterSemSatz.getPraedikatWennOhneInformationsverlustMoeglich();
        if (erstesPraedikatWennOhneInformationsverlustMoeglich == null) {
            return null;
        }

        @Nullable final SemPraedikatOhneLeerstellen
                zweitesPraedikatWennOhneInformationsverlustMoeglich =
                ersterSemSatz.getPraedikatWennOhneInformationsverlustMoeglich();
        if (zweitesPraedikatWennOhneInformationsverlustMoeglich == null) {
            return null;
        }

        @Nullable final Konstituentenfolge erstesInterrogativwortErsterSatz =
                erstesPraedikatWennOhneInformationsverlustMoeglich
                        .getErstesInterrogativwort();
        @Nullable final Konstituentenfolge erstesInterrogativwortZweiterSatz =
                zweitesPraedikatWennOhneInformationsverlustMoeglich
                        .getErstesInterrogativwort();

        if (!Objects.equals(erstesInterrogativwortErsterSatz, erstesInterrogativwortZweiterSatz)) {
            return null;
        }

        // IDEA: Vielleicht nur bis zu einer gewissen Länge zusammenfassen?

        if (erstesInterrogativwortErsterSatz == null) {
            // "ob die Grillen zirpen und der Wind rauscht"
            return joinToKonstituentenfolge(
                    ersterSemSatz.getIndirekteFrage(), // "ob die Grillen zirpen"
                    zweiterSatz.mitAnschlusswortUndFallsKeinAnschlusswort()
                            .getVerbletztsatz()); // "und der Wind rauscht"
        }

        // "wie die Grillen zirpen und der Wind rauscht"
        return joinToKonstituentenfolge(
                ersterSemSatz.getIndirekteFrage(), // "wie die Grillen zirpen"
                zweiterSatz.mitAnschlusswortUndFallsKeinAnschlusswort()
                        .getVerbletztsatz()
                        .cutFirst(
                                erstesInterrogativwortErsterSatz)); // "und der Wind rauscht"
    }

    @Override
    public Konstituentenfolge getRelativsatz() {
        // IDEA: Zusammengefasste Relativsätze wie bei zusammengefassten indirekten Fragen:
        //  "den du mir geschenkt und im Garten versteckt hast"

        return mitKonnektorUndFallsKeinKonnektor()
                .mapJoinToKonstituentenfolge(SemSatz::getRelativsatz, false);
    }

    @Override
    public Konstituentenfolge getVerbzweitsatzMitSpeziellemVorfeldAlsWeitereOption() {
        return mapJoinToKonstituentenfolge(
                SemSatz::getVerbzweitsatzMitSpeziellemVorfeldAlsWeitereOption,
                EinzelnerSemSatz::getVerbzweitsatzStandard);
    }

    @NonNull
    @Override
    public ImmutableList<Konstituentenfolge> altVerzweitsaetze() {
        final ImmutableList.Builder<Konstituentenfolge> res = ImmutableList.builder();

        for (final Konstituentenfolge ersterVerbzweitsatz : ersterSemSatz.altVerzweitsaetze()) {
            for (final Konstituentenfolge zweiterVerbzweitsatz : zweiterSatz.altVerzweitsaetze()) {
                res.add(joinToKonstituentenfolge(ersterVerbzweitsatz, zweiterVerbzweitsatz));
            }
        }

        return res.build();
    }

    @Override
    public Konstituentenfolge getVerbzweitsatzStandard() {
        return mapJoinToKonstituentenfolge(SemSatz::getVerbzweitsatzStandard);
    }

    @Override
    public Konstituentenfolge getVerbzweitsatzMitVorfeld(final String vorfeld) {
        return mapJoinToKonstituentenfolge(
                ersterEinzelnerSatz -> ersterEinzelnerSatz.getVerbzweitsatzMitVorfeld(vorfeld),
                EinzelnerSemSatz::getVerbzweitsatzStandard);
    }

    @Override
    public Konstituentenfolge getSatzanschlussOhneSubjektOhneAnschlusswortOhneVorkomma() {
        return mapJoinToKonstituentenfolge(
                SemSatz::getSatzanschlussOhneSubjektOhneAnschlusswortOhneVorkomma, false);
    }

    @Override
    public Konstituentenfolge getSatzanschlussOhneSubjektMitAnschlusswortOderVorkomma() {
        return mapJoinToKonstituentenfolge(
                SemSatz::getSatzanschlussOhneSubjektMitAnschlusswortOderVorkomma, false);
    }

    @Override
    public Konstituentenfolge getVerbletztsatz() {
        return mitKonnektorUndFallsKeinKonnektor()
                .mapJoinToKonstituentenfolge(SemSatz::getVerbletztsatz, false);
    }

    @Override
    public boolean hasSubjektDuBelebt() {
        return bothMatch(SemSatz::hasSubjektDuBelebt);
    }

    @Nullable
    @Override
    public SemPraedikatOhneLeerstellen getPraedikatWennOhneInformationsverlustMoeglich() {
        @Nullable final SemPraedikatOhneLeerstellen erstesPraedikat =
                ersterSemSatz.getPraedikatWennOhneInformationsverlustMoeglich();
        if (erstesPraedikat == null) {
            return null;
        }

        @Nullable final SemPraedikatOhneLeerstellen zweitesPraedikatOhneAnschlusswort =
                zweiterSatz.ohneAnschlusswort().getPraedikatWennOhneInformationsverlustMoeglich();
        if (zweitesPraedikatOhneAnschlusswort == null) {
            return null;
        }

        return new ZweiPraedikateOhneLeerstellenSem(
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
    @Nullable
    public SubstantivischePhrase getErstesSubjekt() {
        return ersterSemSatz.getErstesSubjekt();
    }

    @Override
    public boolean hatAngabensatz() {
        return anyMatch(SemSatz::hatAngabensatz);
    }

    private Konstituentenfolge mapJoinToKonstituentenfolge(
            final Function<SemSatz, Konstituentenfolge> function) {
        return mapJoinToKonstituentenfolge(function, true);
    }

    private Konstituentenfolge mapJoinToKonstituentenfolge(
            final Function<SemSatz, Konstituentenfolge> function,
            final boolean semikolonErlaubt) {
        return mapJoinToKonstituentenfolge(function, function::apply, semikolonErlaubt);
    }

    private Konstituentenfolge mapJoinToKonstituentenfolge(
            final Function<SemSatz, ?> functionFuerErstenSatz,
            final Function<EinzelnerSemSatz, Konstituentenfolge> functionFuerZweitenSatz) {
        return mapJoinToKonstituentenfolge(functionFuerErstenSatz, functionFuerZweitenSatz, true);
    }

    private Konstituentenfolge mapJoinToKonstituentenfolge(
            final Function<SemSatz, ?> functionFuerErstenSatz,
            final Function<EinzelnerSemSatz, Konstituentenfolge> functionFuerZweitenSatz,
            final boolean semikolonErlaubt) {
        return joinToKonstituentenfolge(
                functionFuerErstenSatz.apply(ersterSemSatz),
                semikolonErlaubt && moeglichstSemikolon ? ";" : null,
                functionFuerZweitenSatz.apply(zweiterSatz)
                        .withVorkommaNoetigMin(!zweiterSatz.hasAnschlusswort()));
    }

    private Satzreihe mapFirst(final Function<SemSatz, SemSatz> function) {
        return new Satzreihe(
                function.apply(ersterSemSatz),
                zweiterSatz);
    }

    private boolean anyMatch(final Predicate<SemSatz> predicate) {
        return Stream.of(ersterSemSatz, zweiterSatz).anyMatch(predicate);
    }

    private boolean bothMatch(final Predicate<SemSatz> predicate) {
        return Stream.of(ersterSemSatz, zweiterSatz).allMatch(predicate);
    }

    @Override
    @Nullable
    public NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld getAnschlusswort() {
        return ersterSemSatz.getAnschlusswort();
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
        return ersterSemSatz.equals(that.ersterSemSatz) &&
                moeglichstSemikolon == that.moeglichstSemikolon &&
                zweiterSatz.equals(that.zweiterSatz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ersterSemSatz, zweiterSatz);
    }
}
