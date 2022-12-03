package de.nb.aventiure2.german.satz;

import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToKonstituentenfolge;
import static de.nb.aventiure2.german.base.NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld.UND;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.Collection;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativSkopusSatz;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativVerbAllg;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativWohinWoher;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld;
import de.nb.aventiure2.german.description.ITextContext;
import de.nb.aventiure2.german.description.ImmutableTextContext;
import de.nb.aventiure2.german.description.StructuredDescription;
import de.nb.aventiure2.german.praedikat.AbstractFinitesPraedikat;
import de.nb.aventiure2.german.praedikat.Modalpartikel;
import de.nb.aventiure2.german.praedikat.SemPraedikatOhneLeerstellen;
import de.nb.aventiure2.german.praedikat.ZweiPraedikateOhneLeerstellenSem;

public class SemSatzReihe implements SemSatz {
    private final SemSatz ersterSatz;

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
    public static ImmutableSet<SemSatzReihe> altGereihtStandard(final Collection<SemSatz> altErste,
                                                                final Collection<SemSatz> altZweite) {
        final ImmutableSet.Builder<SemSatzReihe> alt = ImmutableSet.builder();
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
    static SemSatzReihe gereihtStandard(final SemSatz ersterSemSatz, final SemSatz zweiterSemSatz) {
        if (zweiterSemSatz instanceof EinzelnerSemSatz) {
            return gereihtStandard(ersterSemSatz, (EinzelnerSemSatz) zweiterSemSatz);
        }

        if (zweiterSemSatz instanceof SemSatzReihe) {
            final SemSatzReihe zweiterSatzAlsSemSatzReihe = (SemSatzReihe) zweiterSemSatz;

            return gereihtStandard(
                    gereihtStandard(ersterSemSatz, zweiterSatzAlsSemSatzReihe.ersterSatz),
                    zweiterSatzAlsSemSatzReihe.moeglichstSemikolon
                            || NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld
                            .traegtBedeutung(
                                    zweiterSatzAlsSemSatzReihe.ersterSatz.getAnschlusswort()),
                    zweiterSatzAlsSemSatzReihe.zweiterSatz);
        }

        throw new IllegalStateException("Unexpected subtype of SemSatz: " + zweiterSemSatz);
    }

    /**
     * Verbindet die Sätze zu einer Satzreihe und sorgt im Regelfall
     * dafür, dass es nur ein einzelnes "und" gibt und die Teilsätze davor mit ","
     * verbunden sind.
     */
    @NonNull
    private static SemSatzReihe gereihtStandard(final SemSatz ersterSemSatz,
                                                final EinzelnerSemSatz zweiterSatz) {
        if (ersterSemSatz instanceof SemSatzReihe
                && NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld.traegtBedeutung(
                ((SemSatzReihe) ersterSemSatz).zweiterSatz.getAnschlusswort())) {
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
    private static SemSatzReihe gereihtStandard(final SemSatz ersterSemSatz,
                                                final boolean moeglichstSemikolonUndKeinUnd,
                                                final EinzelnerSemSatz zweiterSatz) {
        if (moeglichstSemikolonUndKeinUnd
                || NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld.traegtBedeutung(
                zweiterSatz.getAnschlusswort())) {
            // "...; ..."
            // "... und ..., aber..."
            return new SemSatzReihe(ersterSemSatz, moeglichstSemikolonUndKeinUnd, zweiterSatz);
        }

        // "..., ... und ..."
        return new SemSatzReihe(ohneUndKonnektor(ersterSemSatz),
                zweiterSatz.mitAnschlusswortUndFallsKeinAnschlusswort());
    }

    @NonNull
    private static SemSatz ohneUndKonnektor(final SemSatz semSatz) {
        if (!(semSatz instanceof SemSatzReihe)) {
            return semSatz;
        }

        return ((SemSatzReihe) semSatz).ohneUndKonnektor();
    }

    /**
     * Erzeugt eine neue Satzreihe mit diesem Konnektor. Es ist Verantwortung des Aufrufers,
     * unerwünschte Dopplungen wie "... und ... und ..." oder unschöne
     * Folgen wie "..., aber ..., aber ..." zu vermeiden, indem der erste SemSatz, wenn er
     * ebenfalls eine Satzreihe ist, z.B. keinen Konnektor enthält.
     */
    public SemSatzReihe(
            final SemSatz ersterSatz,
            final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld konnektor,
            final EinzelnerSemSatz zweiterSatz) {
        this(ersterSatz, false, konnektor, zweiterSatz);
    }

    private SemSatzReihe(
            final SemSatz ersterSatz,
            final boolean moeglichstSemikolon,
            final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld konnektor,
            final EinzelnerSemSatz zweiterSatz) {
        this(ersterSatz, moeglichstSemikolon, zweiterSatz.mitAnschlusswort(konnektor));
    }

    public SemSatzReihe(final SemSatz ersterSatz, final EinzelnerSemSatz zweiterSatz) {
        this(ersterSatz, false, zweiterSatz);
    }

    public SemSatzReihe(
            final SemSatz ersterSatz,
            final boolean moeglichstSemikolon,
            final EinzelnerSemSatz zweiterSatz) {
        this.ersterSatz = ersterSatz;
        this.moeglichstSemikolon = moeglichstSemikolon;
        this.zweiterSatz = zweiterSatz;
    }

    @Override
    public SemSatzReihe ohneAnschlusswort() {
        return (SemSatzReihe) SemSatz.super.ohneAnschlusswort();
    }

    @Override
    public SemSatzReihe mitAnschlusswort(
            @Nullable final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld anschlusswort) {
        return mapFirst(s -> s.mitAnschlusswort(anschlusswort));
    }

    @Override
    public SemSatzReihe mitSubjektFokuspartikel(@Nullable final String subjektFokuspartikel) {
        return mapFirst(s -> s.mitSubjektFokuspartikel(subjektFokuspartikel));
    }

    @Override
    public SemSatzReihe mitModalpartikeln(final Collection<Modalpartikel> modalpartikeln) {
        return mapFirst(s -> s.mitModalpartikeln(modalpartikeln));
    }

    @Override
    public SemSatzReihe mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabe) {
        return mapFirst(s -> s.mitAdvAngabe(advAngabe));
    }

    @Override
    public SemSatzReihe mitAdvAngabe(@Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabe) {
        return mapFirst(s -> s.mitAdvAngabe(advAngabe));
    }

    @Override
    public SemSatzReihe mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativWohinWoher advAngabe) {
        return mapFirst(s -> s.mitAdvAngabe(advAngabe));
    }

    @Override
    public SemSatzReihe mitAngabensatz(@Nullable final KonditionalSemSatz angabensatz,
                                       final boolean angabensatzMoeglichstVorangestellt) {
        return mapFirst(s -> s.mitAngabensatz(angabensatz, angabensatzMoeglichstVorangestellt));
    }

    @NonNull
    private SemSatzReihe mitKonnektorUndFallsKeinKonnektor() {
        return new SemSatzReihe(
                ersterSatz,
                zweiterSatz.mitAnschlusswortUndFallsKeinAnschlusswort());
    }

    private SemSatzReihe ohneUndKonnektor() {
        if (zweiterSatz.getAnschlusswort() != UND) {
            return this;
        }

        return new SemSatzReihe(ersterSatz, moeglichstSemikolon,
                zweiterSatz.ohneAnschlusswort());
    }

    @Override
    public SemSatzReihe perfekt() {
        return new SemSatzReihe(ersterSatz.perfekt(), zweiterSatz.perfekt());
    }

    @Override
    public Konstituentenfolge getIndirekteFrage(final ITextContext textContext) {
        @Nullable final Konstituentenfolge zusammengefassteIndirekteFrageWennMoeglich =
                getZusammengefassteIndirekteFrageWennMoeglich(textContext);

        if (zusammengefassteIndirekteFrageWennMoeglich != null) {
            return zusammengefassteIndirekteFrageWennMoeglich;
        }

        // "wie die Grillen zirpen und ob und der Wind rauscht"
        return mitKonnektorUndFallsKeinKonnektor()
                .mapJoinToKonstituentenfolge(SemSatz::getIndirekteFrage,
                        textContext, false);
    }

    @Nullable
    private Konstituentenfolge getZusammengefassteIndirekteFrageWennMoeglich(
            final ITextContext textContext) {
        @Nullable final ImmutableList<AbstractFinitesPraedikat> ersteFinitePraedikate =
                ersterSatz
                        .getFinitePraedikateWennOhneInformationsverlustMoeglich(textContext,
                                false);
        if (ersteFinitePraedikate == null) {
            return null;
        }

        @Nullable final ImmutableList<AbstractFinitesPraedikat> zweiteFinitePraedikate =
                zweiterSatz.getFinitePraedikateWennOhneInformationsverlustMoeglich(textContext,
                        true);
        if (zweiteFinitePraedikate == null) {
            return null;
        }

        @Nullable final Konstituentenfolge letztesInterrogativwortErsteFinitePraedikate =
                ersteFinitePraedikate.get(ersteFinitePraedikate.size() - 1)
                        .getErstesInterrogativwort();

        @Nullable final Konstituentenfolge erstesInterrogativwortZweiteFinitePraedikate =
                zweiteFinitePraedikate.get(0)
                        .getErstesInterrogativwort();

        if (!Objects.equals(letztesInterrogativwortErsteFinitePraedikate,
                erstesInterrogativwortZweiteFinitePraedikate)) {
            return null;
        }

        // IDEA: Vielleicht nur bis zu einer gewissen Länge zusammenfassen?

        if (erstesInterrogativwortZweiteFinitePraedikate == null) {
            // "ob die Grillen zirpen und der Wind rauscht"

            // FIXME Hier sicherstellen, dass (z.B. auf einem Objekt)
            //  toSustPhr() nur einmal aufgerufen wird!
            //  (Derzeit hier doppelter Aufruf!)
            //  Denkbar: ersterSatz.getSynSaetze(), deren "Kern" jeweils ein oder mehrere
            //  finite Praedikate sind.

            return joinToKonstituentenfolge(
                    ersterSatz.getIndirekteFrage(textContext), // "ob die Grillen zirpen"
                    // FIXME Müsste sich hier nicht der textContext ändern?
                    zweiterSatz.mitAnschlusswortUndFallsKeinAnschlusswort()
                            .getVerbletztsatz(textContext)); // "und der Wind rauscht"
        }

        // "wie die Grillen zirpen und der Wind rauscht"

        // FIXME Hier sicherstellen, dass (z.B. auf einem Objekt)
        //  toSustPhr() nur einmal aufgerufen wird!
        //  (Derzeit hier doppelter Aufruf!)

        return joinToKonstituentenfolge(
                ersterSatz.getIndirekteFrage(textContext), // "wie die Grillen zirpen"
                // FIXME müsste sich hier nicht der Text-Context ändern?
                zweiterSatz.mitAnschlusswortUndFallsKeinAnschlusswort()
                        .getVerbletztsatz(textContext)
                        .cutFirst(
                                letztesInterrogativwortErsteFinitePraedikate)); // "und der Wind
        // rauscht"
    }

    @Override
    public Konstituentenfolge getRelativsatz(final ITextContext textContext) {
        // IDEA: Zusammengefasste Relativsätze wie bei zusammengefassten indirekten Fragen:
        //  "den du mir geschenkt und im Garten versteckt hast"

        return mitKonnektorUndFallsKeinKonnektor()
                .mapJoinToKonstituentenfolge(SemSatz::getRelativsatz,
                        textContext, false);
    }

    @Override
    public Konstituentenfolge getVerbzweitsatzMitSpeziellemVorfeldAlsWeitereOption(
            final ITextContext textContext) {
        return mapJoinToKonstituentenfolge(
                SemSatz::getVerbzweitsatzMitSpeziellemVorfeldAlsWeitereOption,
                EinzelnerSemSatz::getVerbzweitsatzStandard,
                textContext);
    }

    @NonNull
    @Override
    public ImmutableList<Konstituentenfolge> altVerzweitsaetze(final ITextContext textContext) {
        final ImmutableList.Builder<Konstituentenfolge> res = ImmutableList.builder();

        for (final Konstituentenfolge ersterVerbzweitsatz :
                ersterSatz.altVerzweitsaetze(textContext)) {
            for (final Konstituentenfolge zweiterVerbzweitsatz :
                    zweiterSatz.altVerzweitsaetze(textContext)) {
                res.add(joinToKonstituentenfolge(ersterVerbzweitsatz, zweiterVerbzweitsatz));
            }
        }

        return res.build();
    }

    @Override
    public Konstituentenfolge getVerbzweitsatzStandard(final ITextContext textContext) {
        return mapJoinToKonstituentenfolge(SemSatz::getVerbzweitsatzStandard,
                textContext);
    }

    @Override
    public Konstituentenfolge getVerbzweitsatzMitVorfeld(
            final String vorfeld, final ITextContext textContext) {
        return mapJoinToKonstituentenfolge(
                (ersterEinzelnerSatz, tc) -> ersterEinzelnerSatz.getVerbzweitsatzMitVorfeld(
                        vorfeld, tc),
                EinzelnerSemSatz::getVerbzweitsatzStandard,
                textContext);
    }

    @Override
    public Konstituentenfolge getSatzanschlussOhneSubjektOhneAnschlusswortOhneVorkomma(
            final ITextContext textContext) {
        return mapJoinToKonstituentenfolge(
                SemSatz::getSatzanschlussOhneSubjektOhneAnschlusswortOhneVorkomma,
                textContext, false);
    }

    @Override
    public Konstituentenfolge getSatzanschlussOhneSubjektMitAnschlusswortOderVorkomma(
            final ITextContext textContext) {
        return mapJoinToKonstituentenfolge(
                SemSatz::getSatzanschlussOhneSubjektMitAnschlusswortOderVorkomma,
                textContext,
                false);
    }

    /**
     * Gibt den SemSatz als Verbletztsatz aus, z.B. "du um die Ecke gegangen bist und plötzlich
     * nicht hast weitergekonnt"
     */
    @Override
    public Konstituentenfolge getVerbletztsatz(
            final ITextContext textContext,
            final boolean anschlussAusserAberUnterdruecken) {
        final SemSatzReihe semSatzReihe = mitKonnektorUndFallsKeinKonnektor();
        return joinToKonstituentenfolge(
                semSatzReihe.ersterSatz.getVerbletztsatz(
                        textContext,
                        anschlussAusserAberUnterdruecken),
                // "du um die Ecke gegangen bist"
                // FIXME müsste sich hier nicht der Text-Context ändern?
                semSatzReihe.zweiterSatz.getVerbletztsatz(textContext)
                        .withVorkommaNoetigMin(!semSatzReihe.zweiterSatz.hasAnschlusswort())
                // "und plötzlich nicht hast weitergekonnt"
        );
    }

    @Override
    public boolean hasSubjektDuBelebt() {
        return bothMatch(SemSatz::hasSubjektDuBelebt);
    }

    @Nullable
    @Override
    public SemPraedikatOhneLeerstellen getPraedikatWennOhneInformationsverlustMoeglich() {
        @Nullable final SemPraedikatOhneLeerstellen erstesPraedikat =
                ersterSatz.getPraedikatWennOhneInformationsverlustMoeglich();
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

    @Override
    public ImmutableList<EinzelnerSyntSatz> getSyntSaetze(final ITextContext textContext) {
        return ImmutableList.<EinzelnerSyntSatz>builder()
                .addAll(ersterSatz.getSyntSaetze(textContext))
                // FIXME Müsste sich nicht hier der Textkontext ändern?
                .addAll(zweiterSatz.getSyntSaetze(textContext))
                .build();
    }

    @Nullable
    @Override
    public ImmutableList<AbstractFinitesPraedikat> getFinitePraedikateWennOhneInformationsverlustMoeglich(
            final ITextContext textContext, final boolean vorLetztemZumindestUnd) {
        @Nullable final ImmutableList<AbstractFinitesPraedikat> ersteFinitePraedikate =
                ersterSatz
                        .getFinitePraedikateWennOhneInformationsverlustMoeglich(textContext,
                                false);
        if (ersteFinitePraedikate == null) {
            return null;
        }

        @Nullable final ImmutableList<AbstractFinitesPraedikat> zweiteFinitePraedikate =
                zweiterSatz.getFinitePraedikateWennOhneInformationsverlustMoeglich(textContext,
                        vorLetztemZumindestUnd);
        if (zweiteFinitePraedikate == null) {
            return null;
        }

        return ImmutableList.<AbstractFinitesPraedikat>builder()
                .addAll(ersteFinitePraedikate)
                .addAll(zweiteFinitePraedikate)
                .build();
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
    public boolean hatAngabensatz() {
        return anyMatch(SemSatz::hatAngabensatz);
    }

    private Konstituentenfolge mapJoinToKonstituentenfolge(
            final BiFunction<SemSatz, ITextContext, Konstituentenfolge> function,
            final ITextContext textContext) {
        return mapJoinToKonstituentenfolge(function, textContext, true);
    }

    private Konstituentenfolge mapJoinToKonstituentenfolge(
            final BiFunction<SemSatz, ITextContext, Konstituentenfolge> function,
            final ITextContext textContext,
            final boolean semikolonErlaubt) {
        return mapJoinToKonstituentenfolge(function, function::apply,
                textContext,
                semikolonErlaubt);
    }

    private Konstituentenfolge mapJoinToKonstituentenfolge(
            final BiFunction<SemSatz, ITextContext, ?> functionFuerErstenSatz,
            final BiFunction<EinzelnerSemSatz, ITextContext, Konstituentenfolge> functionFuerZweitenSatz,
            final ITextContext textContext) {
        return mapJoinToKonstituentenfolge(functionFuerErstenSatz, functionFuerZweitenSatz,
                textContext, true);
    }

    private Konstituentenfolge mapJoinToKonstituentenfolge(
            final BiFunction<SemSatz, ITextContext, ?> functionFuerErstenSatz,
            final BiFunction<EinzelnerSemSatz, ITextContext, Konstituentenfolge>
                    functionFuerZweitenSatz,
            final ITextContext textContext,
            final boolean semikolonErlaubt) {
        return joinToKonstituentenfolge(
                functionFuerErstenSatz.apply(ersterSatz, textContext),
                semikolonErlaubt && moeglichstSemikolon ? ";" : null,
                // FIXME müsste sich hier nicht der Text-Context ändern?
                functionFuerZweitenSatz.apply(zweiterSatz, textContext)
                        .withVorkommaNoetigMin(!zweiterSatz.hasAnschlusswort()));
    }

    private SemSatzReihe mapFirst(final Function<SemSatz, SemSatz> function) {
        return new SemSatzReihe(
                function.apply(ersterSatz),
                zweiterSatz);
    }

    private boolean anyMatch(final Predicate<SemSatz> predicate) {
        return Stream.of(ersterSatz, zweiterSatz).anyMatch(predicate);
    }

    private boolean bothMatch(final Predicate<SemSatz> predicate) {
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
        return getVerbzweitsatzStandard(ImmutableTextContext.EMPTY)
                .joinToSingleKonstituente().toTextOhneKontext();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SemSatzReihe that = (SemSatzReihe) o;
        return ersterSatz.equals(that.ersterSatz) &&
                moeglichstSemikolon == that.moeglichstSemikolon &&
                zweiterSatz.equals(that.zweiterSatz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ersterSatz, zweiterSatz);
    }
}
