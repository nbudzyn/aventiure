package de.nb.aventiure2.german.praedikat;

import androidx.annotation.Nullable;

import java.util.Collection;

import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;

import static java.util.Arrays.asList;

public interface PraedikatOhneLeerstellen extends Praedikat, DuTextPart {
    PraedikatOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusSatz adverbialeAngabe);

    PraedikatOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusVerbAllg adverbialeAngabe);

    PraedikatOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusVerbWohinWoher adverbialeAngabe);

    // TODO Modalpartikeln führen zu einem
    //  neuen AbstractPraedikat führen, dass man dann auch speichern
    //  und weiterreichen kann!
    @Override
    default String getDuHauptsatz() {
        return getDuHauptsatz(new Modalpartikel[0]);
    }

    /**
     * Gibt einen Satz zurück mit diesem Prädikat
     * ("Du nimmst den Ast")
     * und ggf. diesen Modalpartikeln, die sich <i>auf das gesamte
     * Prädikat beziehen</i>
     * ("Du nimmst den Ast besser doch")
     */
    default String getDuHauptsatz(final Modalpartikel... modalpartikeln) {
        return getDuHauptsatz(asList(modalpartikeln));
    }

    default String getDuHauptsatz(final Collection<Modalpartikel> modalpartikeln) {
        return "Du " + getDuSatzanschlussOhneSubjekt(modalpartikeln);
    }

    /**
     * Gibt einen Satz zurück mit diesem Prädikat, bei dem das Subjekt, das im Vorfeld
     * stünde, eingespart ist ("nimmst den Ast")
     */
    @Override
    default String getDuSatzanschlussOhneSubjekt() {
        return getDuSatzanschlussOhneSubjekt(new Modalpartikel[0]);
    }

    /**
     * Gibt einen Satz zurück mit diesem Prädikat, bei dem das Subjekt, das im Vorfeld
     * stünde, eingespart ist ("nimmst den Ast")
     */
    default String getDuSatzanschlussOhneSubjekt(final Modalpartikel... modalpartikeln) {
        return getDuSatzanschlussOhneSubjekt(asList(modalpartikeln)
        );
    }

    /**
     * Gibt einen Satz zurück mit diesem Prädikat, bei dem das Subjekt, das im Vorfeld
     * stünde, eingespart ist ("nimmst den Ast"), sowie ggf. diesen
     * Modalpartikeln ("nimmst den Ast eben doch").
     */
    String getDuSatzanschlussOhneSubjekt(final Collection<Modalpartikel> modalpartikeln);

    boolean duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen();

    /**
     * Gibt eine Infinitivkonstruktion mit diesem
     * Prädikat zurück ("das Schwert nehmen")
     * <p>
     * Implizit (oder bei reflexiven Verben auch explizit) hat der
     * Infinitiv eine Person und einen Numerus - Beispiel:
     * "[Ich möchte] die Kugel an mich nehmen"
     * (nicht *"[Ich möchte] die Kugel an sich nehmen")
     */
    String getInfinitiv(final Person person, final Numerus numerus);

    /**
     * Gibt eine Infinitivkonstruktion mit dem zu-Infinitiv mit diesem
     * Prädikat zurück ("das Schwert zu nehmen")
     * <p>
     * Implizit (oder bei reflexiven Verben auch explizit) hat der
     * zu-Infinitiv eine Person und einen Numerus - Beispiel:
     * "[Ich gedenke,] die Kugel an mich zu nehmen"
     * (nicht *"[Ich gedenke,] die Kugel an sich zu nehmen")
     */
    String getZuInfinitiv(final Person person, final Numerus numerus);
}
