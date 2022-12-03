package de.nb.aventiure2.german.satz;

import static de.nb.aventiure2.german.base.NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld.ABER;
import static de.nb.aventiure2.german.base.NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld.UND;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import java.util.Arrays;
import java.util.Collection;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativSkopusSatz;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativVerbAllg;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativWohinWoher;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld;
import de.nb.aventiure2.german.description.ITextContext;
import de.nb.aventiure2.german.praedikat.AbstractFinitesPraedikat;
import de.nb.aventiure2.german.praedikat.Modalpartikel;
import de.nb.aventiure2.german.praedikat.SemPraedikatOhneLeerstellen;

/**
 * Ein "semantischer SemSatz", also ein SemSatz, in dem die einzelnen semantischen Elemente
 * (z.B. Diskursreferenten) noch nicht auf einzelne sprachliche Elemente
 * fixiert sind (z.B. als gewisse Nomen oder Personalpronomen).
 */
public interface SemSatz {
    /**
     * Gibt diesen <code>SemSatz</code> zurück, mit dem (vorangehenden) Anschlusswort "und",
     * falls
     * <ul>
     * <li>der Satz kein (vorangehendes) Anschlusswort hat
     * <li>und der Satz keine Satzreihung mit "und" ist.
     * </ul>
     */
    default SemSatz mitAnschlusswortUndFallsKeinAnschlusswortUndKeineSatzreihungMitUnd() {
        if (isSatzreihungMitUnd()) {
            return this;
        }

        return mitAnschlusswortUndFallsKeinAnschlusswort();
    }

    default SemSatz mitAnschlussworHoechstensAber() {
        if (getAnschlusswort() == ABER) {
            return this;
        }

        return ohneAnschlusswort();
    }

    /**
     * Gibt diesen <code>SemSatz</code> zurück, mit dem (vorangehenden) Anschlusswort "und",
     * falls der Satz kein Anschlusswort hat.
     */
    default SemSatz mitAnschlusswortUndFallsKeinAnschlusswort() {
        if (hasAnschlusswort()) {
            return this;
        }

        return mitAnschlusswort(UND);
    }

    default SemSatz ohneAnschlusswort() {
        return mitAnschlusswort(null);
    }

    SemSatz mitAnschlusswort(
            @Nullable NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld anschlusswort);

    default boolean hasAnschlusswort() {
        return getAnschlusswort() != null;
    }

    /**
     * Fügt dem Subjekt etwas hinzu wie "auch", "allein", "ausgerechnet",
     * "wenigstens" etc. (sofern das Subjekt eine Fokuspartikel erlaubt, ansonsten
     * wird sie verworfen)
     */
    SemSatz mitSubjektFokuspartikel(
            @Nullable String subjektFokuspartikel);

    default SemSatz mitModalpartikeln(final Modalpartikel... modalpartikeln) {
        return mitModalpartikeln(Arrays.asList(modalpartikeln));
    }

    SemSatz mitModalpartikeln(Collection<Modalpartikel> modalpartikeln);

    SemSatz mitAdvAngabe(@Nullable IAdvAngabeOderInterrogativSkopusSatz advAngabe);

    SemSatz mitAdvAngabe(@Nullable IAdvAngabeOderInterrogativVerbAllg advAngabe);

    SemSatz mitAdvAngabe(@Nullable IAdvAngabeOderInterrogativWohinWoher advAngabe);

    SemSatz mitAngabensatz(@Nullable final KonditionalSemSatz angabensatz,
                           final boolean angabensatzMoeglichstVorangestellt);

    SemSatz perfekt();

    ImmutableList<EinzelnerSyntSatz> getSyntSaetze(ITextContext textContext);

    NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld getAnschlusswort();

    /**
     * Gibt eine indirekte Frage zurück: Etwas wie
     * <ul>
     * <li>ob du etwas zu berichten hast
     * <li>was du zu berichten hast
     * <li>wer etwas zu berichten hat
     * <li>wer was zu berichten hat
     * <li>mit wem sie sich treffen wird
     * <li>wann du etwas zu berichten hast
     * <li>wessen Heldentaten wer zu berichten hat
     * <li>was zu erzählen du beginnen wirst
     * <li>was du zu erzählen beginnen wirst
     * <li>wie er helfen kann
     * <li>wer wie geholfen hat
     * </ul>
     */
    Konstituentenfolge getIndirekteFrage(
            final ITextContext textContext);

    /**
     * Gibt einen RelativSemSatz zurück: Etwas wie
     * <ul>
     * <li>das du zu berichten hast
     * <li>der etwas zu berichten hat
     * <li>der was zu berichten hat
     * <li>mit dem sie sich treffen wird
     * <li>dessen Heldentaten wer zu berichten hat
     * <li>das zu erzählen du beginnen wirst
     * <li>das du zu erzählen beginnen wirst
     * <li>der wie geholfen hat
     * </ul>
     */
    Konstituentenfolge getRelativsatz(
            final ITextContext textContext);

    /**
     * Gibt den SemSatz als Verbzweitsatz aus, bei dem nach Möglichkeit ein "spezielles"
     * Vorfeld gewählt wird, z.B. eine adverbiale Bestimmung: "am Abend hast du etwas zu berichten"
     */
    Konstituentenfolge getVerbzweitsatzMitSpeziellemVorfeldAlsWeitereOption(
            final ITextContext textContext);

    /**
     * Gibt den SemSatz als einige alternative Verbzweitsätze aus, z.B. "du hast
     * am Abend etwas zu berichten" oder "am Abend hast du etwas zu berichten"
     */
    @CheckReturnValue
    @NonNull
    ImmutableList<Konstituentenfolge> altVerzweitsaetze(
            final ITextContext textContext);

    /**
     * Gibt den SemSatz als Verbzweitsatz aus, bei dem das Subjekt im Vorfeld steht, z.B. "du hast
     * am Abend etwas zu berichten" oder "du nimmst den Ast"
     */
    Konstituentenfolge getVerbzweitsatzStandard(
            final ITextContext textContext);

    Konstituentenfolge getVerbzweitsatzMitVorfeld(
            String vorfeld, final ITextContext textContext);

    /**
     * Gibt den SemSatz in Verbzweitform aus, jedoch ohne Subjekt und ohne Anschlusswort
     * (d.h. ohne "und") und ohne Komma, beginnend mit dem Verb. Z.B. "hast
     * am Abend etwas zu berichten" oder "nimmst den Ast"
     */
    Konstituentenfolge getSatzanschlussOhneSubjektOhneAnschlusswortOhneVorkomma(
            final ITextContext textContext);

    /**
     * Gibt den SemSatz in Verbzweitform aus, jedoch ohne Subjekt, also beginnend mit
     * dem Anschlusswort (z.B. "und" - oder aber der Angabe, dass ein Vorkomma nötig ist) und dem
     * Verb. Z.B. "und hast am Abend etwas zu berichten" oder "[, ]aber nimmst den Ast"
     */
    Konstituentenfolge getSatzanschlussOhneSubjektMitAnschlusswortOderVorkomma(
            final ITextContext textContext);

    /**
     * Gibt den SemSatz als Verbletztsatz aus, z.B. "du etwas zu berichten hast"
     */
    default Konstituentenfolge getVerbletztsatz(final ITextContext textContext) {
        return getVerbletztsatz(textContext, false);
    }

    /**
     * Gibt den SemSatz als Verbletztsatz aus, z.B. "du etwas zu berichten hast"
     */
    @Nullable
    Konstituentenfolge getVerbletztsatz(
            final ITextContext textContext,
            final boolean anschlussAusserAberUnterdruecken);

    boolean hasSubjektDuBelebt();

    /**
     * Gibt das Prädikat des Satzes zurück, wenn das
     * (abgesehen vom Subjekt) ohne Informationsverlust
     * möglich ist (d.h. wenn das SemSatz keinen Adverbialsatz enthält, nicht
     * mir einem Anschlusswort beginnt etc.).
     */
    @Nullable
    SemPraedikatOhneLeerstellen getPraedikatWennOhneInformationsverlustMoeglich();

    /**
     * Gibt die einzelnen "finiten Prädikate" des Satzes zurück, wenn das
     * (abgesehen vom Subjekt) ohne Informationsverlust
     * möglich ist (z.B. wenn das SemSatz keinen Adverbialsatz enthält).
     */
    @Nullable
    ImmutableList<AbstractFinitesPraedikat> getFinitePraedikateWennOhneInformationsverlustMoeglich(
            ITextContext textContext, final boolean vorLetztemZumindestUnd);

    /**
     * Gibt zurück, ob es sich um eine Satzreihung mit "und" handelt ("er kommt und siegt") oder
     * nicht nicht ("er kommt", "er kommt, aber er war zu spät").
     */
    boolean isSatzreihungMitUnd();

    boolean hatAngabensatz();
}
