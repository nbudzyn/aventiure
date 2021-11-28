package de.nb.aventiure2.german.satz;

import static de.nb.aventiure2.german.base.NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld.UND;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import java.util.Arrays;
import java.util.Collection;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativSkopusSatz;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativVerbAllg;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativWohinWoher;
import de.nb.aventiure2.german.base.IAlternativeKonstituentenfolgable;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.ITextContext;
import de.nb.aventiure2.german.praedikat.Modalpartikel;
import de.nb.aventiure2.german.praedikat.SemPraedikatOhneLeerstellen;

/**
 * Ein "semantischer SemSatz", also ein SemSatz, in dem die einzelnen semantischen Elemente
 * (z.B. Diskursreferenten) noch nicht auf einzelne sprachliche Elemente
 * fixiert sind (z.B. als gewisse Nomen oder Personalpronomen).
 */
public interface SemSatz
        // FIXME Ist dieses Interface hier richtig?
        extends IAlternativeKonstituentenfolgable {
    default SemSatz mitAnschlusswortUndFallsKeinAnschlusswortUndKeineSatzreihungMitUnd() {
        if (isSatzreihungMitUnd()) {
            return this;
        }

        return mitAnschlusswortUndFallsKeinAnschlusswort();
    }

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

    SemSatz mitAngabensatz(@Nullable final Konditionalsatz angabensatz,
                           final boolean angabensatzMoeglichstVorangestellt);

    SemSatz perfekt();

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
    Konstituentenfolge getIndirekteFrage();

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
    Konstituentenfolge getRelativsatz();

    /**
     * Gibt den SemSatz als Verbzweitsatz aus, bei dem nach Möglichkeit ein "spezielles"
     * Vorfeld gewählt wird, z.B. eine adverbiale Bestimmung: "am Abend hast du etwas zu berichten"
     */
    Konstituentenfolge getVerbzweitsatzMitSpeziellemVorfeldAlsWeitereOption();

    @Override
    default ImmutableCollection<Konstituentenfolge> toAltKonstituentenfolgen() {
        return altVerzweitsaetze();
    }

    /**
     * Gibt den SemSatz als einige alternative Verbzweitsätze aus, z.B. "du hast
     * am Abend etwas zu berichten" oder "am Abend hast du etwas zu berichten"
     */
    @CheckReturnValue
    @NonNull
    ImmutableList<Konstituentenfolge> altVerzweitsaetze();

    /**
     * Gibt den SemSatz als Verbzweitsatz aus, bei dem das Subjekt im Vorfeld steht, z.B. "du hast
     * am Abend etwas zu berichten" oder "du nimmst den Ast"
     */
    Konstituentenfolge getVerbzweitsatzStandard();

    Konstituentenfolge getVerbzweitsatzMitVorfeld(String vorfeld);

    /**
     * Gibt den SemSatz in Verbzweitform aus, jedoch ohne Subjekt und ohne Anschlusswort
     * (d.h. ohne "und") und ohne Komma, beginnend mit dem Verb. Z.B. "hast
     * am Abend etwas zu berichten" oder "nimmst den Ast"
     */
    Konstituentenfolge getSatzanschlussOhneSubjektOhneAnschlusswortOhneVorkomma();

    /**
     * Gibt den SemSatz in Verbzweitform aus, jedoch ohne Subjekt, also beginnend mit
     * dem Anschlusswort (z.B. "und" - oder aber der Angabe, dass ein Vorkomma nötig ist) und dem
     * Verb. Z.B. "und hast am Abend etwas zu berichten" oder "[, ]aber nimmst den Ast"
     */
    Konstituentenfolge getSatzanschlussOhneSubjektMitAnschlusswortOderVorkomma();

    /**
     * Gibt den SemSatz als Verbletztsatz aus, z.B. "du etwas zu berichten hast"
     */
    Konstituentenfolge getVerbletztsatz();

    boolean hasSubjektDuBelebt();

    /**
     * Gibt das Prädikat des Satzes zurück, wenn das
     * (abgesehen vom Subjekt) ohne Informationsverlust
     * möglich ist (d.h. wenn das SemSatz keinen Adverbialsatz enthält, nicht
     * mir einem Anschlusswort beginnt etc.).
     */
    @Nullable
    SemPraedikatOhneLeerstellen getPraedikatWennOhneInformationsverlustMoeglich();

    boolean isSatzreihungMitUnd();

    @Nullable
    SubstantivischePhrase getErstesSubjekt();

    boolean hatAngabensatz();

    /**
     * Erzeugt aus diesem "semantischen Satz" einen eigentlichen ("syntaktischen")
     * Satz, wobei alle Diskursreferenten (Personen, Objekte etc.) auf jeweils
     * eine konkrete sprachliche Repräsentation (z.B. ein konkretes Nomen oder
     * Personalpronomen) festgelegt werden.
     */
    @Nonnull
    @CheckReturnValue
    SyntSatz toSynt(ITextContext textContext);
}
