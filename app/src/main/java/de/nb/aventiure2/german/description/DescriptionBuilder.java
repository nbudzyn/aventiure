package de.nb.aventiure2.german.description;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.praedikat.PraedikatOhneLeerstellen;
import de.nb.aventiure2.german.satz.Satz;

import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToKonstituentenfolge;
import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToNullKonstituentenfolge;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.Person.P2;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.base.StructuralElement.WORD;

public class DescriptionBuilder {
    private DescriptionBuilder() {
    }

    @CheckReturnValue
    public static TextDescription paragraph(final Object... parts) {
        return neuerSatz(PARAGRAPH, parts)
                .beendet(PARAGRAPH);
    }

    @CheckReturnValue
    public static TextDescription neuerSatz(final Object... parts) {
        return neuerSatz(SENTENCE, parts);
    }

    @NonNull
    @CheckReturnValue
    public static TextDescription neuerSatz(
            final StructuralElement startsNew,
            final Object... parts) {
        return neuerSatz(startsNew, joinToKonstituentenfolge(parts));
    }

    @NonNull
    @CheckReturnValue
    public static TextDescription neuerSatz(final StructuralElement startsNew,
                                            final Konstituentenfolge konstituentenfolge) {
        return neuerSatz(startsNew,
                konstituentenfolge.joinToSingleKonstituente());
    }

    @NonNull
    @CheckReturnValue
    public static TextDescription neuerSatz(final StructuralElement startsNew,
                                            final Konstituente konstituente) {
        checkArgument(startsNew != WORD,
                "Neuer Satz unmöglich für " + startsNew);

        return new TextDescription(startsNew, konstituente);
    }

    @NonNull
    @CheckReturnValue
    public static TextDescription satzanschluss(final Object... parts) {
        return satzanschluss(joinToKonstituentenfolge(parts));
    }

    @NonNull
    @CheckReturnValue
    private static TextDescription satzanschluss(final Konstituentenfolge konstituentenfolge) {
        return satzanschluss(konstituentenfolge.joinToSingleKonstituente());
    }

    @NonNull
    @CheckReturnValue
    private static TextDescription satzanschluss(final Konstituente konstituente) {
        return new TextDescription(StructuralElement.WORD, konstituente);
    }

    @CheckReturnValue
    public static SimpleDuDescription du(final String verb, final Object... remainderParts) {
        return du(WORD, verb, remainderParts);
    }

    @CheckReturnValue
    public static SimpleDuDescription du(final StructuralElement startsNew,
                                         final String verb, final Object... remainderParts) {
        return du(startsNew, verb, joinToNullKonstituentenfolge(remainderParts));
    }

    @NonNull
    @CheckReturnValue
    public static SimpleDuDescription du(final StructuralElement startsNew, final String verb,
                                         @Nullable final Konstituentenfolge konstituentenfolge) {
        return du(startsNew, verb,
                konstituentenfolge != null ? konstituentenfolge.joinToSingleKonstituente() : null);
    }

    @NonNull
    @CheckReturnValue
    public static SimpleDuDescription du(final String verb,
                                         @Nullable final Konstituente konstituente) {
        return du(WORD, verb, konstituente);
    }

    @NonNull
    @CheckReturnValue
    public static SimpleDuDescription du(final StructuralElement startsNew, final String verb,
                                         @Nullable final Konstituente remainder) {
        return new SimpleDuDescription(startsNew, verb, remainder);
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
