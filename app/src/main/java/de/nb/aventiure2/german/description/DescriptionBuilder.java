package de.nb.aventiure2.german.description;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.base.Wortfolge;
import de.nb.aventiure2.german.praedikat.PraedikatOhneLeerstellen;
import de.nb.aventiure2.german.satz.Satz;

import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.Person.P2;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.base.StructuralElement.WORD;
import static de.nb.aventiure2.german.base.Wortfolge.joinToNullWortfolge;
import static de.nb.aventiure2.german.base.Wortfolge.joinToWortfolge;

public class DescriptionBuilder {
    private DescriptionBuilder() {
    }

    @CheckReturnValue
    public static TextDescription paragraph(final Object... parts) {
        return neuerSatz(PARAGRAPH, parts)
                .beendet(PARAGRAPH);
    }

    public static TextDescription neuerSatz(final Object... parts) {
        return neuerSatz(SENTENCE, parts);
    }

    @NonNull
    @CheckReturnValue
    public static TextDescription neuerSatz(
            final StructuralElement startsNew,
            final Object... parts) {
        return neuerSatz(startsNew, joinToWortfolge(parts));
    }

    @NonNull
    @CheckReturnValue
    public static TextDescription neuerSatz(final StructuralElement startsNew,
                                            final Wortfolge wortfolge) {
        checkArgument(startsNew != WORD,
                "Neuer Satz unmöglich für " + startsNew);

        return new TextDescription(startsNew, wortfolge);
    }

    @NonNull
    @CheckReturnValue
    public static TextDescription satzanschluss(final Object... parts) {
        return satzanschluss(joinToWortfolge(parts));
    }

    @NonNull
    @CheckReturnValue
    private static TextDescription satzanschluss(final Wortfolge wortfolge) {
        return new TextDescription(StructuralElement.WORD, wortfolge);
    }

    @CheckReturnValue
    public static SimpleDuDescription du(final String verb, final Object... remainderParts) {
        return du(WORD, verb, remainderParts);
    }

    @CheckReturnValue
    public static SimpleDuDescription du(final StructuralElement startsNew,
                                         final String verb, final Object... remainderParts) {
        return du(startsNew, verb, joinToNullWortfolge(remainderParts));
    }

    @NonNull
    @CheckReturnValue
    public static SimpleDuDescription du(final String verb,
                                         @Nullable final Wortfolge remainder) {
        return du(WORD, verb, remainder);
    }

    @NonNull
    @CheckReturnValue
    public static SimpleDuDescription du(final StructuralElement startsNew, final String verb,
                                         @Nullable final Wortfolge remainder) {
        // FIXME nomStr() etc. suchen und prüfen
        // FIXME phorikKandidate suchen und prüfen

        return new SimpleDuDescription(startsNew,
                verb,
                remainder != null ? remainder.getString() : null,
                remainder != null && remainder.woertlicheRedeNochOffen(),
                remainder != null && remainder.kommaStehtAus(),
                remainder != null ? remainder.getPhorikKandidat() : null);
    }

    @CheckReturnValue
    public static StructuredDescription du(final PraedikatOhneLeerstellen praedikat) {
        return du(StructuralElement.WORD, praedikat);
    }

    @NonNull
    @CheckReturnValue
    public static StructuredDescription du(final StructuralElement startsNew,
                                           final PraedikatOhneLeerstellen praedikat) {
        return satz(startsNew,
                praedikat.alsSatzMitSubjekt(Personalpronomen.get(P2,
                        // Wir behaupten hier, der Adressat wäre männlich.
                        // Es ist die Verantwortung des Aufrufers, keine
                        // Sätze mit Konstruktionen wie "Du, der du" zu erzeugen, die
                        // weibliche Adressaten ("du, die du") ausschließen.
                        M, SPIELER_CHARAKTER)));
    }

    @NonNull
    @CheckReturnValue
    public static StructuredDescription satz(final Satz satz) {
        return satz(WORD, satz);
    }

    @NonNull
    @CheckReturnValue
    public static StructuredDescription satz(final StructuralElement startsNew, final Satz satz) {
        return new StructuredDescription(startsNew, satz);
    }
}
