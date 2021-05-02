package de.nb.aventiure2.german.satz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import java.util.Arrays;
import java.util.Collection;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.german.base.IAlternativeKonstituentenfolgable;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbWohinWoher;
import de.nb.aventiure2.german.praedikat.Modalpartikel;
import de.nb.aventiure2.german.praedikat.PraedikatOhneLeerstellen;

public interface Satz extends IAlternativeKonstituentenfolgable {
    default Satz mitAnschlusswortUndSofernNichtSchonEnthalten() {
        if (isSatzreihungMitUnd()) {
            return ohneAnschlusswort();
        }

        return mitAnschlusswort("und");
    }

    default Satz ohneAnschlusswort() {
        return mitAnschlusswort(null);
    }

    Satz mitAnschlusswort(@Nullable String anschlusswort);

    /**
     * Fügt dem Subjekt etwas hinzu wie "auch", "allein", "ausgerechnet",
     * "wenigstens" etc. (sofern das Subjekt eine Fokuspartikel erlaubt, ansonsten
     * wird sie verworfen)
     */
    Satz mitSubjektFokuspartikel(
            @Nullable String subjektFokuspartikel);

    default Satz mitModalpartikeln(final Modalpartikel... modalpartikeln) {
        return mitModalpartikeln(Arrays.asList(modalpartikeln));
    }

    Satz mitModalpartikeln(Collection<Modalpartikel> modalpartikeln);

    Satz mitAdvAngabe(@Nullable AdvAngabeSkopusSatz advAngabe);

    Satz mitAdvAngabe(
            @Nullable AdvAngabeSkopusVerbAllg advAngabe);

    Satz mitAdvAngabe(
            @Nullable AdvAngabeSkopusVerbWohinWoher advAngabe);

    Satz perfekt();

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
     * Gibt einen Relativsatz zurück: Etwas wie
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
     * Gibt den Satz als Verbzweitsatz aus, bei dem nach Möglichkeit ein "spezielles"
     * Vorfeld gewählt wird, z.B. eine adverbiale Bestimmung: "am Abend hast du etwas zu berichten"
     */
    Konstituentenfolge getVerbzweitsatzMitSpeziellemVorfeldAlsWeitereOption();

    @Override
    default ImmutableCollection<Konstituentenfolge> toAltKonstituentenfolgen() {
        return altVerzweitsaetze();
    }

    /**
     * Gibt den Satz als einige alternative Verbzweitsätze aus, z.B. "du hast
     * am Abend etwas zu berichten" oder "am Abend hast du etwas zu berichten"
     */
    @CheckReturnValue
    @NonNull
    ImmutableList<Konstituentenfolge> altVerzweitsaetze();

    /**
     * Gibt den Satz als Verbzweitsatz aus, bei dem das Subjekt im Vorfeld steht, z.B. "du hast
     * am Abend etwas zu berichten" oder "du nimmst den Ast"
     */
    Konstituentenfolge getVerbzweitsatzStandard();

    Konstituentenfolge getVerbzweitsatzMitVorfeld(String vorfeld);

    Konstituentenfolge getSatzanschlussOhneSubjekt();

    /**
     * Gibt den Satz als Verbletztsatz aus, z.B. "du etwas zu berichten hast"
     */
    Konstituentenfolge getVerbletztsatz();

    boolean hasSubjektDu();

    PraedikatOhneLeerstellen getPraedikat();

    boolean isSatzreihungMitUnd();

    SubstantivischePhrase getErstesSubjekt();
}
