package de.nb.aventiure2.german.satz;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.nb.aventiure2.german.description.ITextContext;

/**
 * Ein "semantischer Nebensatz", der mit einer Kondition beginnt. Beispiel:
 * "als du das hörst"
 */
public class KonditionalSemSatz {
    @Nonnull
    private final String kondition;

    @Nonnull
    private final SemSatz semSatz;

    public KonditionalSemSatz(@Nonnull final String kondition, @Nonnull final SemSatz semSatz) {
        this.kondition = kondition;
        this.semSatz = semSatz;
    }

    public KonditionalSemSatz perfekt() {
        return new KonditionalSemSatz(kondition, semSatz.perfekt());
    }

    KonditionalSyntSatz getSynt(final ITextContext textContext) {
        return new KonditionalSyntSatz(kondition, semSatz.getSyntSaetze(textContext));
    }

    @Nonnull
    KonditionalSemSatz stelleVoran(@Nullable final KonditionalSemSatz other) {
        if (other == null) {
            return this;
        }

        // FIXME Hier kann es zu dem Fall kommen, dass die
        //  Kondition unterschiedliche ist, z.B. wenn man
        //  "als ich dich gesehen habe" mit "weil ich dich schon länger
        //  ansprechen wollte" reiht:
        //  "als ich dich gesehen habe und weil ich dich schon länger
        //  ansprechen wollte".
        //  Man bräuchte dazu eine Konditionalsatzreihe und könnte
        //  einige Dinge so lösen wie bei der Satzreihe.
        // if (kondition.equals(other.kondition)) {

        // FIXME Hier könnte man Satzreihungen vermeiden,
        //  wenn das Subjekt beider Sätze gleich ist
        //  ("als und um die Ecke kommst und du den Troll siehst") - und stattdessen
        //  besser die "Prädikate reihen" ("als du um die Ecke kommst und den
        //  Troll siehst").

        return new KonditionalSemSatz(other.kondition,
                SemSatzReihe.gereihtStandard(other.semSatz, semSatz));
        // }
    }
}
