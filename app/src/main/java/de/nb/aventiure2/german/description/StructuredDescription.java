package de.nb.aventiure2.german.description;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import java.util.Objects;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.narration.Narration;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.PhorikKandidat;
import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbWohinWoher;
import de.nb.aventiure2.german.praedikat.PraedikatOhneLeerstellen;
import de.nb.aventiure2.german.satz.EinzelnerSatz;
import de.nb.aventiure2.german.satz.Satz;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToKonstituentenfolge;
import static de.nb.aventiure2.german.base.NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld.UND;
import static de.nb.aventiure2.german.base.NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld.traegtBedeutung;

/**
 * A description based on a structured data structure: A {@link EinzelnerSatz}.
 * Somehting like "Du gehst in den Wald" or "Peter liebt Petra"
 */
public class StructuredDescription extends AbstractFlexibleDescription<StructuredDescription> {
    /**
     * This {@link Narration} starts a new ... (paragraph, e.g.)
     */
    private final StructuralElement startsNew;
    /**
     * This {@link Narration} ends this ... (paragraph, e.g.)
     */
    private final StructuralElement endsThis;

    private final Satz satz;

    /**
     * Hierauf könnte sich ein Pronomen (z.B. ein Personalpronomen) unmittelbar
     * danach (<i>anaphorisch</i>) beziehen. Dazu müssen (in aller Regel) die grammatischen
     * Merkmale übereinstimmen und es muss mit dem Pronomen dieses Bezugsobjekt
     * gemeint sein.
     * <p>
     * Dieses Feld sollte nur gesetzt werden wenn man sich sicher ist, wenn es also keine
     * Fehlreferenzierungen, Doppeldeutigkeiten
     * oder unerwünschten Wiederholungen geben kann. Typische Fälle wären "Du nimmst die Lampe und
     * zündest sie an." oder "Du stellst die Lampe auf den Tisch und zündest sie an."
     * <p>
     * Negatitvbeispiele wäre:
     * <ul>
     *     <li>"Du stellst die Lampe auf die Theke und zündest sie an." (Fehlreferenzierung)
     *     <li>"Du nimmst den Ball und den Schuh und wirfst ihn in die Luft." (Doppeldeutigkeit)
     *     <li>"Du nimmst die Lampe und zündest sie an. Dann stellst du sie wieder ab,
     *     schaust sie dir aber dann noch einmal genauer an: Sie ... sie ... sie" (Unerwünschte
     *     Wiederholung)
     *     <li>"Du stellst die Lampe auf den Tisch. Der Tisch ist aus Holz und hat viele
     *     schöne Gravuren - er muss sehr wertvoll sein. Dann nimmst du sie wieder in die Hand."
     *     (Referenziertes Objekt zu weit entfernt.)
     * </ul>
     */
    @Nullable
    private PhorikKandidat phorikKandidat;

    StructuredDescription(final StructuralElement startsNew,
                          final Satz satz,
                          final StructuralElement endsThis) {
        super();
        // Das hier ist eine automatische Vorbelegung auf Basis des Satzes.
        // Bei Bedarf kann man das in den Params noch überschreiben.
        phorikKandidat = guessPhorikKandidat(startsNew, satz, endsThis);
        this.startsNew = startsNew;
        this.satz = satz;
        this.endsThis = endsThis;
    }

    private StructuredDescription(final DescriptionParams params,
                                  final StructuralElement startsNew,
                                  final Satz satz,
                                  final StructuralElement endsThis) {
        super(params);
        // Das hier ist eine automatische Vorbelegung auf Basis des Satzes.
        // Bei Bedarf kann man das in den Params noch überschreiben.
        phorikKandidat = guessPhorikKandidat(startsNew, satz, endsThis);
        this.startsNew = startsNew;
        this.satz = satz;
        this.endsThis = endsThis;
    }

    private static PhorikKandidat guessPhorikKandidat(
            final StructuralElement startsNew, final Satz satz,
            final StructuralElement endsThis) {
        return toSingleKonstituente(startsNew, satz, endsThis).getPhorikKandidat();
    }

    @Override
    @NonNull
    @CheckReturnValue
    public TextDescription toTextDescriptionSatzanschlussMitAnschlusswortOderVorkomma() {
        final Satz satzEvtlMitAnschlusswort =
                getSatz()
                        .mitAnschlusswortUndFallsKeinAnschlusswortUndKeineSatzreihungMitUnd();
        final Konstituente satzanschlussMitUndAberOderOderKomma =
                satzEvtlMitAnschlusswort
                        .getSatzanschlussOhneSubjektMitAnschlusswortOderVorkomma()
                        .joinToSingleKonstituente();
        return toTextDescriptionKeepParams(satzanschlussMitUndAberOderOderKomma)
                .undWartest(satzEvtlMitAnschlusswort.getAnschlusswort() != UND);
    }

    @CheckReturnValue
    public StructuredDescription mitAdvAngabe(
            @Nullable final AdvAngabeSkopusSatz advAngabe) {
        return copy(getSatz().mitAdvAngabe(advAngabe));
    }

    @CheckReturnValue
    public StructuredDescription mitAdvAngabe(
            @Nullable final AdvAngabeSkopusVerbAllg advAngabe) {
        return copy(getSatz().mitAdvAngabe(advAngabe));
    }

    @CheckReturnValue
    public StructuredDescription mitAdvAngabe(
            @Nullable final AdvAngabeSkopusVerbWohinWoher advAngabe) {
        return copy(getSatz().mitAdvAngabe(advAngabe));
    }


    /**
     * Fügt dem Subjekt des Satzes etwas hinzu wie "auch", "allein", "ausgerechnet",
     * "wenigstens" etc. (sofern das Subjekt eine Fokuspartikel erlaubt, ansonsten
     * wird sie verworfen)
     */
    public StructuredDescription mitSubjektFokuspartikel(
            @Nullable final String subjektFokuspartikel) {
        return withSatz(satz.mitSubjektFokuspartikel(subjektFokuspartikel));
    }

    private StructuredDescription withSatz(final Satz other) {
        return copy(other);
    }

    @Override
    public StructuralElement getStartsNew() {
        return startsNew;
    }

    @Override
    public StructuralElement getEndsThis() {
        return endsThis;
    }

    @Override
    @CheckReturnValue
    public ImmutableList<TextDescription> altTextDescriptions() {
        return satz.altVerzweitsaetze().stream()
                .map(kf ->
                        joinToKonstituentenfolge(
                                getStartsNew(),
                                kf,
                                getEndsThis()
                        )
                )
                .map(Konstituentenfolge::joinToSingleKonstituente)
                .map(this::toTextDescriptionKeepParams)
                .collect(toImmutableList());
    }


    @CheckReturnValue
    private StructuredDescription copy(final Satz satz) {
        return new StructuredDescription(copyParams(), getStartsNew(), satz, getEndsThis());
    }

    @Override
    public boolean hasSubjektDu() {
        return getSatz().hasSubjektDu();
    }

    @Override
    public Konstituente toSingleKonstituenteMitVorfeld(final String vorfeld) {
        return
                joinToKonstituentenfolge(
                        getStartsNew(),
                        satz.getVerbzweitsatzMitVorfeld(vorfeld),
                        getEndsThis()
                )
                        .joinToSingleKonstituente();
    }

    @Override
    public Konstituente toSingleKonstituente() {
        return toSingleKonstituente(getStartsNew(), satz, getEndsThis());
    }

    @CheckReturnValue
    private static Konstituente toSingleKonstituente(final StructuralElement startsNew,
                                                     final Satz satz,
                                                     final StructuralElement endsThis) {
        return joinToKonstituentenfolge(
                startsNew,
                satz.getVerbzweitsatzStandard(),
                endsThis
        ).joinToSingleKonstituente();
    }

    @Override
    @Nullable
    @CheckReturnValue
    protected Konstituente toSingleKonstituenteMitSpeziellemVorfeldOrNull() {
        @Nullable final Konstituentenfolge speziellesVorfeld =
                satz.getVerbzweitsatzMitSpeziellemVorfeldAlsWeitereOption();
        if (speziellesVorfeld == null) {
            return null;
        }

        return joinToKonstituentenfolge(
                getStartsNew(),
                speziellesVorfeld,
                getEndsThis()
        ).joinToSingleKonstituente();
    }

    @Override
    @CheckReturnValue
    public Konstituente toSingleKonstituenteSatzanschlussOhneSubjektOhneAnschlusswortOhneKomma() {
        return joinToKonstituentenfolge(
                satz.getSatzanschlussOhneSubjektOhneAnschlusswortOhneVorkomma(),
                getEndsThis())
                .joinToSingleKonstituente();
    }

    /**
     * Gibt das Prädikat des Satzes zurück, wenn das
     * (abgesehen vom Subjekt) ohne Informationsverlust
     * möglich ist (d.h. wenn das Satz keinen Adverbialsatz enthält, nicht
     * mir einem Anschlusswort beginnt etc.).
     */
    @Nullable
    public PraedikatOhneLeerstellen getPraedikatWennOhneInformationsverlustMoeglich() {
        return satz.getPraedikatWennOhneInformationsverlustMoeglich();
    }

    public Satz getSatz() {
        return satz;
    }

    @Override
    public StructuredDescription komma(final boolean kommaStehtAus) {
        // Bewirkt nichts. Der Satz weiß schon selbst, wann ein Komma nötig ist.
        // (Aber die Methode erleichert das Handling, sodass z.B. die
        // TimedDescription problemlos komma() implementieren kann etc.)
        return this;
    }

    @Override
    public StructuredDescription phorikKandidat(@Nullable final PhorikKandidat phorikKandidat) {
        this.phorikKandidat = phorikKandidat;
        return this;
    }

    @Override
    @Nullable
    public PhorikKandidat getPhorikKandidat() {
        return phorikKandidat;
    }

    @Override
    public boolean hasAnschlusswortDasBedeutungTraegt() {
        return traegtBedeutung(getSatz().getAnschlusswort());
    }

    @Override
    public boolean equals(final @Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final StructuredDescription that = (StructuredDescription) o;
        return startsNew == that.startsNew &&
                endsThis == that.endsThis &&
                Objects.equals(satz, that.satz) &&
                Objects.equals(phorikKandidat, that.phorikKandidat);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), startsNew, endsThis, satz, phorikKandidat);
    }
}
