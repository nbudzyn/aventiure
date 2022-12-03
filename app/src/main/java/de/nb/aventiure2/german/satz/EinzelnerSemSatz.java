package de.nb.aventiure2.german.satz;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Objects.requireNonNull;
import static de.nb.aventiure2.german.base.Belebtheit.UNBELEBT;
import static de.nb.aventiure2.german.base.Numerus.SG;
import static de.nb.aventiure2.german.base.Person.P3;
import static de.nb.aventiure2.util.StreamUtil.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.Objects;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativSkopusSatz;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativVerbAllg;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativWohinWoher;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld;
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.base.PraedRegMerkmale;
import de.nb.aventiure2.german.base.SubstantivischPhrasierbar;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.ITextContext;
import de.nb.aventiure2.german.description.ImmutableTextContext;
import de.nb.aventiure2.german.praedikat.AbstractFinitesPraedikat;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.praedikat.Modalpartikel;
import de.nb.aventiure2.german.praedikat.SemPraedikatMitEinerObjektleerstelle;
import de.nb.aventiure2.german.praedikat.SemPraedikatOhneLeerstellen;

/**
 * Ein einzelner "semantischer Satz".
 */
public class EinzelnerSemSatz implements SemSatz {
    /**
     * Anschlusswort: "und", "aber", "oder", "sondern". Steht vor dem Vorfeld. Bei einer
     * {@link SemSatzReihe} aus zwei Sätzen ist der Konnektor genau das Anschlusswort des zweiten
     * Satzes.
     */
    @Nullable
    private final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld anschlusswort;

    /**
     * Das Subjekt des Satzes. Darf in seltenen Fällen fehlen ("Mich friert.")
     */
    @Nullable
    private final SubstantivischPhrasierbar subjekt;

    /**
     * Das Prädikat des Satzes, im Sinne des Verbs mit all seinen Ergänzungen und
     * Angabe - ohne das Subjekt.
     */
    private final SemPraedikatOhneLeerstellen praedikat;

    /**
     * Ein dem SemSatz direkt untergeordneter (Neben-) SemSatz, der den Status einer
     * <i>Angabe</i> hat
     * - der also nicht Subjekt o.Ä. ist.
     */
    @Nullable
    private final KonditionalSemSatz angabensatz;

    /**
     * Ob der Angabensatz - wenn es überhaupt einen gibt - nach Möglichkeit vorangestellt
     * werden soll
     */
    private final boolean angabensatzMoeglichstVorangestellt;

    public static ImmutableList<SemSatz> altSubjObjSaetze(
            @Nullable final SubstantivischPhrasierbar subjekt,
            final SemPraedikatMitEinerObjektleerstelle praedikat,
            final SubstantivischPhrasierbar objekt,
            final Collection<AdvAngabeSkopusVerbAllg> advAngaben) {
        return altSubjObjSaetze(subjekt, ImmutableList.of(praedikat), objekt, advAngaben);
    }

    public static ImmutableList<SemSatz> altSubjObjSaetze(
            @Nullable final SubstantivischPhrasierbar subjekt,
            final Collection<? extends SemPraedikatMitEinerObjektleerstelle> praedikate,
            final SubstantivischPhrasierbar objekt) {
        return mapToList(praedikate, v -> v.mit(objekt).alsSatzMitSubjekt(subjekt));
    }

    private static ImmutableList<SemSatz> altSubjObjSaetze(
            @Nullable final SubstantivischPhrasierbar subjekt,
            final Collection<? extends SemPraedikatMitEinerObjektleerstelle> praedikate,
            final SubstantivischPhrasierbar objekt,
            final Collection<AdvAngabeSkopusVerbAllg> advAngaben) {
        return advAngaben.stream()
                .flatMap(aa -> praedikate.stream()
                        .map(v -> v.mit(objekt)
                                .mitAdvAngabe(aa)
                                .alsSatzMitSubjekt(subjekt)))
                .collect(toImmutableList());
    }

    public EinzelnerSemSatz(@Nullable final SubstantivischPhrasierbar subjekt,
                            final SemPraedikatOhneLeerstellen praedikat) {
        this(null, subjekt, praedikat);
    }

    public EinzelnerSemSatz(
            @Nullable final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld anschlusswort,
            @Nullable final SubstantivischPhrasierbar subjekt,
            final SemPraedikatOhneLeerstellen praedikat) {
        this(anschlusswort, subjekt, praedikat, null, false);
    }

    private EinzelnerSemSatz(
            @Nullable final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld anschlusswort,
            @Nullable final SubstantivischPhrasierbar subjekt,
            final SemPraedikatOhneLeerstellen praedikat,
            @Nullable final KonditionalSemSatz angabensatz,
            final boolean angabensatzMoeglichstVorangestellt) {
        this.angabensatzMoeglichstVorangestellt = angabensatzMoeglichstVorangestellt;
        if (praedikat.inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich()) {
            if (subjekt != null) {
                if (!(subjekt instanceof SubstantivischePhrase)) {
                    throw new IllegalStateException("Kein expletives es: " + subjekt);
                }

                Personalpronomen.checkExpletivesEs((SubstantivischePhrase) subjekt);
            }
        } else {
            requireNonNull(subjekt,
                    () -> "Subjekt null, fehlendes Subjekt " +
                            "für diese Prädikat nicht möglich: " + praedikat);
        }

        this.anschlusswort = anschlusswort;
        this.subjekt = subjekt;
        this.praedikat = praedikat;
        this.angabensatz = angabensatz;
    }

    @Override
    public EinzelnerSemSatz ohneAnschlusswort() {
        return (EinzelnerSemSatz) SemSatz.super.ohneAnschlusswort();
    }

    @Override
    public EinzelnerSemSatz mitAnschlusswortUndFallsKeinAnschlusswort() {
        return (EinzelnerSemSatz) SemSatz.super.mitAnschlusswortUndFallsKeinAnschlusswort();
    }

    @Override
    public EinzelnerSemSatz mitAnschlusswort(
            @Nullable final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld anschlusswort) {
        return new EinzelnerSemSatz(anschlusswort, subjekt, praedikat,
                angabensatz, angabensatzMoeglichstVorangestellt);
    }

    @Override
    public EinzelnerSemSatz mitSubjektFokuspartikel(
            @Nullable final String subjektFokuspartikel) {
        if (subjekt == null) {
            return this;
        }

        return new EinzelnerSemSatz(anschlusswort, subjekt.mitFokuspartikel(subjektFokuspartikel),
                praedikat, angabensatz, angabensatzMoeglichstVorangestellt);
    }

    @Override
    public EinzelnerSemSatz mitModalpartikeln(final Collection<Modalpartikel> modalpartikeln) {
        return new EinzelnerSemSatz(anschlusswort, subjekt,
                praedikat.mitModalpartikeln(modalpartikeln),
                angabensatz, angabensatzMoeglichstVorangestellt);
    }

    @Override
    public EinzelnerSemSatz mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabe) {
        return new EinzelnerSemSatz(anschlusswort, subjekt, praedikat.mitAdvAngabe(advAngabe),
                angabensatz, angabensatzMoeglichstVorangestellt);
    }

    @Override
    public EinzelnerSemSatz mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabe) {
        return new EinzelnerSemSatz(anschlusswort, subjekt, praedikat.mitAdvAngabe(advAngabe),
                angabensatz, angabensatzMoeglichstVorangestellt);
    }

    @Override
    public EinzelnerSemSatz mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativWohinWoher advAngabe) {
        return new EinzelnerSemSatz(anschlusswort, subjekt, praedikat.mitAdvAngabe(advAngabe),
                angabensatz, angabensatzMoeglichstVorangestellt);
    }

    @Override
    public EinzelnerSemSatz mitAngabensatz(
            @Nullable final KonditionalSemSatz angabensatz,
            final boolean angabensatzMoeglichstVorangestellt) {
        if (angabensatz == null) {
            return this;
        }

        if (this.angabensatz != null) {
            return new EinzelnerSemSatz(anschlusswort, subjekt, praedikat,
                    this.angabensatz.stelleVoran(angabensatz),
                    this.angabensatzMoeglichstVorangestellt
                            || angabensatzMoeglichstVorangestellt);
        }

        return new EinzelnerSemSatz(anschlusswort, subjekt, praedikat, angabensatz,
                angabensatzMoeglichstVorangestellt);
    }

    @Override
    public EinzelnerSemSatz perfekt() {
        return new EinzelnerSemSatz(anschlusswort, subjekt, praedikat.perfekt(),
                angabensatz != null ? angabensatz.perfekt() : null,
                angabensatzMoeglichstVorangestellt);
    }

    @Override
    public Konstituentenfolge getIndirekteFrage(final ITextContext textContext) {
        return getSyntSatz(textContext).getIndirekteFrage();
    }

    @Override
    public Konstituentenfolge getRelativsatz(final ITextContext textContext) {
        return getSyntSatz(textContext).getRelativsatz();
    }

    @Override
    public Konstituentenfolge getVerbzweitsatzMitSpeziellemVorfeldAlsWeitereOption(
            final ITextContext textContext) {
        // FIXME Hier sicherstellen, dass (z.B. auf einem Objekt)
        //  toSustPhr() nur einmal aufgerufen wird!

        return getSyntSatz(textContext).getVerbzweitsatzMitSpeziellemVorfeldAlsWeitereOption();
    }

    @Override
    @CheckReturnValue
    @NonNull
    public ImmutableList<Konstituentenfolge> altVerzweitsaetze(final ITextContext textContext) {
        // FIXME Hier sicherstellen, dass (z.B. auf einem Objekt)
        //  toSustPhr() nur einmal aufgerufen wird!

        return getSyntSatz(textContext).altVerzweitsaetze();
    }

    @Override
    public Konstituentenfolge getVerbzweitsatzStandard(final ITextContext textContext) {
        // FIXME Hier sicherstellen, dass (z.B. auf einem Objekt)
        //  toSustPhr() nur einmal aufgerufen wird!
        //  Lösung: Nicht mehrfach den textContext ins Prädikat hineingeben!

        return getSyntSatz(textContext).getVerbzweitsatzStandard();
    }

    @Override
    public Konstituentenfolge getVerbzweitsatzMitVorfeld(
            final String vorfeld, final ITextContext textContext) {
        // FIXME Hier sicherstellen, dass (z.B. auf einem Objekt)
        //  toSustPhr() nur einmal aufgerufen wird!
        //  Lösung: Nicht mehrfach den textContext ins Prädikat hineingeben!

        return getSyntSatz(textContext).getVerbzweitsatzMitVorfeld(vorfeld);
    }

    /**
     * Gibt den Satz als Verbletztsatz aus, z.B. "du etwas zu berichten hast"
     * oder "er durch den Wald hat laufen wollen".
     */
    @Override
    @Nullable
    public Konstituentenfolge getVerbletztsatz(
            final ITextContext textContext,
            final boolean anschlussAusserAberUnterdruecken) {
        return getSyntSatz(textContext)
                .getVerbletztsatz(anschlussAusserAberUnterdruecken,
                        false, false);
    }

    @Override
    public Konstituentenfolge getSatzanschlussOhneSubjektOhneAnschlusswortOhneVorkomma(
            final ITextContext textContext) {
        return getSatzanschlussOhneSubjekt(textContext, false);
    }

    @Override
    public Konstituentenfolge getSatzanschlussOhneSubjektMitAnschlusswortOderVorkomma(
            final ITextContext textContext) {
        return getSatzanschlussOhneSubjekt(textContext, true);
    }

    /**
     * Gibt diesen einzelnen "semantischen Satz"" in Verbzweitform aus, jedoch ohne Subjekt.
     *
     * @param mitAnschlusswortOderVorkomma ob die Verbzweitform
     *                                     <ul>
     *                                      <li>mit Anschlusswort oder Vorkomma beginnen soll
     *                                      ("und hast am Abend etwas zu berichten" /
     *                                      "[, ]aber nimmst den Ast")
     *                                      <li>oder aber nicht ("hast am Abend etwas zu
     *                                      berichten" / "nimmst den Ast")
     *                                     </ul>
     */
    private Konstituentenfolge getSatzanschlussOhneSubjekt(
            final ITextContext textContext,
            final boolean mitAnschlusswortOderVorkomma) {
        return getSyntSatz(textContext).getSatzanschlussOhneSubjekt(mitAnschlusswortOderVorkomma);
    }


    @Override
    public ImmutableList<EinzelnerSyntSatz> getSyntSaetze(final ITextContext textContext) {
        return ImmutableList.of(getSyntSatz(textContext));
    }

    private EinzelnerSyntSatz getSyntSatz(final ITextContext textContext) {
        @Nullable final SubstantivischePhrase subjPhrase =
                subjekt != null ?
                        subjekt.alsSubstPhrase(textContext) : null;

        // FIXME Müsste sich hier der textContext vielleicht ändern?
        //  Andererseits ist hier vielleicht gar nicht klar, in welcher Reihenfolge
        //  Subjekt und die anderen substantivierbaren Satz-Elemente auftreten -
        //  erst bei der "Linearisierung" ist die Reihenfolge bekannt - sollte also
        //  die "Linearisierung" den Text-Context jeweils anpassen?

        final ImmutableList<AbstractFinitesPraedikat> finitePraedikate =
                getFinitePraedikate(textContext,
                        subjPhrase,
                        true, true);

        return new EinzelnerSyntSatz(subjPhrase,
                finitePraedikate,
                praedikat.inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich(),
                angabensatz != null ? angabensatz.getSynt(textContext) : null,
                angabensatzMoeglichstVorangestellt);
    }

    @Nullable
    @Override
    public ImmutableList<AbstractFinitesPraedikat> getFinitePraedikateWennOhneInformationsverlustMoeglich(
            final ITextContext textContext, final boolean vorLetztemZumindestUnd) {
        if (angabensatz != null) {
            return null;
        }

        @Nullable final SubstantivischePhrase subjPhrase =
                subjekt != null ? subjekt.alsSubstPhrase(textContext) : null;

        return getFinitePraedikate(textContext, subjPhrase, true,
                vorLetztemZumindestUnd);
    }

    private ImmutableList<AbstractFinitesPraedikat> getFinitePraedikate(
            final ITextContext textContext,
            @Nullable final SubstantivischePhrase subjPhrase,
            final boolean mitErstenAnschlusswortOderKonnektor,
            final boolean vorLetztemZumindestUnd) {

        final PraedRegMerkmale praedRegMerkmale = extractPraedRegMerkmale(subjPhrase);

        return getFinitePraedikate(textContext, praedRegMerkmale,
                mitErstenAnschlusswortOderKonnektor, vorLetztemZumindestUnd);
    }

    private static PraedRegMerkmale extractPraedRegMerkmale(
            @Nullable final SubstantivischePhrase subjPhrase) {
        if (subjPhrase == null) {
            // "Mich friert"
            return new PraedRegMerkmale(P3, SG, UNBELEBT);
        } else {
            return subjPhrase.getPraedRegMerkmale();
        }
    }

    private ImmutableList<AbstractFinitesPraedikat> getFinitePraedikate(
            final ITextContext textContext,
            final PraedRegMerkmale praedRegMerkmale,
            final boolean mitErstenAnschlusswortOderKonnektor,
            final boolean vorLetztemZumindestUnd) {
        final ImmutableList<AbstractFinitesPraedikat> finitePraedikate =
                praedikat.getFinitePraedikate(textContext, anschlusswort, praedRegMerkmale);

        final ImmutableList.Builder<AbstractFinitesPraedikat> res = ImmutableList.builder();
        for (int i = 0; i < finitePraedikate.size(); i++) {
            if (i == 0) {
                res
                        .add(mitErstenAnschlusswortOderKonnektor ?
                                finitePraedikate.get(0).mitKonnektor(
                                        NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld
                                                .getStrongest(
                                                        anschlusswort,
                                                        finitePraedikate.get(0).getKonnektor())) :
                                finitePraedikate.get(0).ohneKonnektor());

            } else if (i == finitePraedikate.size() - 1) {
                res.add(vorLetztemZumindestUnd ?
                        finitePraedikate.get(i).mitKonnektor(
                                firstNonNull(finitePraedikate.get(i).getKonnektor(),
                                        NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld.UND)) :
                        finitePraedikate.get(i));
            } else {
                res.add(finitePraedikate.get(i));
            }
        }

        return res.build();
    }

    @Override
    public boolean hasSubjektDuBelebt() {
        if (!(subjekt instanceof Personalpronomen)) {
            return false;
        }

        return ((Personalpronomen) subjekt).isP2SgBelebt();
    }

    @Override
    @Nullable
    public NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld getAnschlusswort() {
        return anschlusswort;
    }

    @Override
    @Nullable
    public SemPraedikatOhneLeerstellen getPraedikatWennOhneInformationsverlustMoeglich() {
        if (NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld.traegtBedeutung(anschlusswort)
                || angabensatz != null) {
            return null;
        }

        return praedikat;
    }

    @Override
    public boolean isSatzreihungMitUnd() {
        return false;
    }

    @Override
    public boolean hatAngabensatz() {
        return angabensatz != null;
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
        final EinzelnerSemSatz that = (EinzelnerSemSatz) o;
        return Objects.equals(anschlusswort, that.anschlusswort) &&
                Objects.equals(subjekt, that.subjekt) &&
                Objects.equals(praedikat, that.praedikat) &&
                Objects.equals(angabensatz, that.angabensatz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(anschlusswort, subjekt, praedikat, angabensatz);
    }
}