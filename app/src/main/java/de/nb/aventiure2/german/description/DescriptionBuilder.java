package de.nb.aventiure2.german.description;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.german.base.IKonstituenteOrStructuralElement;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.praedikat.PraedikatOhneLeerstellen;
import de.nb.aventiure2.german.satz.Satz;

import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToKonstituentenfolge;
import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToNullKonstituentenfolge;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.base.StructuralElement.WORD;

public class DescriptionBuilder {
    private DescriptionBuilder() {
    }

    @CheckReturnValue
    public static TextDescription paragraph(final Object... parts) {
        return neuerSatz(DescriptionBuilder.prependAndAppendObjects(
                PARAGRAPH, parts, PARAGRAPH));
    }

    @NonNull
    @CheckReturnValue
    static Object[] prependAndAppendObjects(final Object firstObject,
                                            final Object[] objects, final Object lastObject) {
        final Object[] res = new Object[objects.length + 2];
        System.arraycopy(objects, 0, res, 1, objects.length);
        res[0] = firstObject;
        res[res.length - 1] = lastObject;
        return res;
    }

    @NonNull
    @CheckReturnValue
    static Object[] prependObject(final Object object, final Object[] objects) {
        final Object[] res = new Object[objects.length + 1];
        System.arraycopy(objects, 0, res, 1, objects.length);
        res[0] = object;
        return res;
    }

    @NonNull
    @CheckReturnValue
    private static Object[] appendObject(final Object[] objects, final Object object) {
        final Object[] res = new Object[objects.length + 1];
        System.arraycopy(objects, 0, res, 0, objects.length);
        res[res.length - 1] = object;
        return res;
    }

    @CheckReturnValue
    public static TextDescription neuerSatz(final Object... parts) {
        checkArgument(parts.length > 0, "parts was empty");
        if (!(parts[0] instanceof StructuralElement)) {
            return neuerSatz(prependObject(SENTENCE, parts));
        }

        return new TextDescription(joinToKonstituentenfolge(parts).joinToSingleKonstituente());
    }

    @NonNull
    @CheckReturnValue
    public static TextDescription satzanschluss(final Object... parts) {
        return satzanschluss(joinToKonstituentenfolge(parts));
    }

    @NonNull
    @CheckReturnValue
    private static TextDescription satzanschluss(final Konstituentenfolge konstituentenfolge) {
        return new TextDescription(konstituentenfolge.joinToSingleKonstituente());
    }

    @CheckReturnValue
    public static SimpleDuDescription duParagraph(
            final String verb, final Object... remainderParts) {
        return du(PARAGRAPH, verb, appendObject(remainderParts, PARAGRAPH));
    }

    @CheckReturnValue
    public static SimpleDuDescription du(final String verb, final Object... remainderParts) {
        return du(WORD, verb, remainderParts);
    }

    /**
     * Erzeugt eine {@link SimpleDuDescription} ohne Vorfeld-Satzglied. Es ist erlaubt, dass
     * die {@code }remainderParts} nichts als ein {@link StructuralElement} sind, z.B. ein
     * {@link StructuralElement#PARAGRAPH}.
     */
    @CheckReturnValue
    public static SimpleDuDescription du(final StructuralElement startsNew,
                                         final String verb, final Object... remainderParts) {
        return du(startsNew, verb, joinToNullKonstituentenfolge(remainderParts));
    }

    /**
     * Erzeugt eine {@link SimpleDuDescription} ohne Vorfeld-Satzglied. Es ist erlaubt, dass
     * die Konstituentenfolge nichts als ein {@link StructuralElement} enthält, z.B.
     * {@link StructuralElement#PARAGRAPH}.
     */
    @NonNull
    @CheckReturnValue
    public static SimpleDuDescription du(final StructuralElement startsNew, final String verb,
                                         @Nullable final Konstituentenfolge konstituentenfolge) {
        return du(startsNew, verb,
                konstituentenfolge != null ?
                        konstituentenfolge.joinToSingleKonstituenteOrStructuralElement() : null);
    }

    @NonNull
    @CheckReturnValue
    public static SimpleDuDescription du(final String verb,
                                         @Nullable final Konstituente konstituente) {
        return du(WORD, verb, konstituente);
    }

    @NonNull
    @CheckReturnValue
    public static SimpleDuDescription du(final String verb,
                                         @Nullable final StructuralElement endsWith) {
        return du(WORD, verb, endsWith);
    }

    /**
     * Erzeugt eine {@link SimpleDuDescription} ohne Vorfeld-Satzglied.
     */
    @NonNull
    @CheckReturnValue
    public static SimpleDuDescription du(final StructuralElement startsNew, final String verb,
                                         @Nullable
                                         final IKonstituenteOrStructuralElement remainder) {
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
        return du(startsNew, praedikat, WORD);
    }

    @NonNull
    @CheckReturnValue
    public static StructuredDescription du(final StructuralElement startsNew,
                                           final PraedikatOhneLeerstellen praedikat,
                                           final StructuralElement endsThis) {
        return satz(startsNew,
                praedikat.alsSatzMitSubjekt(duSc()),
                endsThis);
    }

    @NonNull
    @CheckReturnValue
    public static StructuredDescription satz(final Satz satz) {
        return satz(WORD, satz);
    }

    @NonNull
    @CheckReturnValue
    public static StructuredDescription satz(final StructuralElement startsNew, final Satz satz) {
        return satz(startsNew, satz, WORD);
    }

    @NonNull
    @CheckReturnValue
    public static StructuredDescription satz(final StructuralElement startsNew, final Satz satz,
                                             final StructuralElement endsThis) {
        return new StructuredDescription(startsNew, satz, endsThis);
    }
}
