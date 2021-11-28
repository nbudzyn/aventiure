package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Objects;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativSkopusSatz;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativVerbAllg;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativWohinWoher;
import de.nb.aventiure2.german.base.Negationspartikelphrase;

/**
 * Ein  (syntaktisches) Prädikat mit "eigenen" adverbialen Angaben ("aus Langeweile").
 * Alle Diskursreferenten (Personen, Objekte etc.) sind auf jeweils eine konkrete sprachliche
 * Repräsentation (z.B. ein konkretes Nomen oder Personalpronomen) festgelegt. (Um das
 * sicherzustellen sollen alle Ableitungen immutable sein.)
 * <p>
 * Beispiele:
 * <ul>
 *     <li>"das Buch nehmen"
 *     <li>"dem Frosch Angebote machen"
 *     <li>"dem Frosch endlich Angebote machen"
 * </ul>
 * <p>
 */
@Immutable
abstract class AbstractAngabenfaehigesSyntPraedikat implements SyntPraedikat {
    /**
     * Das Verb an sich, ohne Informationen zur Valenz, ohne Ergänzungen, ohne
     * Angaben
     */
    @NonNull
    private final Verb verb;

    /**
     * Gibt an, ob dieses Prädikat in der Regel ohne Subjekt steht
     * ("Mich friert"), aber optional ein expletives "es" möglich ist
     * ("Es friert mich").
     */
    private final boolean inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich;

    private final ImmutableList<Modalpartikel> modalpartikeln;

    @Nullable
    private final IAdvAngabeOderInterrogativSkopusSatz advAngabeSkopusSatz;

    @Nullable
    private final Negationspartikelphrase negationspartikelphrase;

    @Nullable
    private final IAdvAngabeOderInterrogativVerbAllg advAngabeSkopusVerbAllg;

    @Nullable
    private final IAdvAngabeOderInterrogativWohinWoher advAngabeSkopusVerbWohinWoher;

    public AbstractAngabenfaehigesSyntPraedikat(
            final Verb verb,
            final boolean inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich) {
        this(verb, inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich, ImmutableList.of(),
                null, null,
                null, null);
    }

    AbstractAngabenfaehigesSyntPraedikat(
            final Verb verb,
            final Iterable<Modalpartikel> modalpartikeln,
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabeSkopusSatz,
            @Nullable final Negationspartikelphrase negationspartikelphrase,
            @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabeSkopusVerbAllg,
            @Nullable final IAdvAngabeOderInterrogativWohinWoher advAngabeSkopusVerbWohinWoher) {
        this(verb, false,
                modalpartikeln, advAngabeSkopusSatz,
                negationspartikelphrase, advAngabeSkopusVerbAllg,
                advAngabeSkopusVerbWohinWoher);
    }

    AbstractAngabenfaehigesSyntPraedikat(
            final Verb verb,
            final boolean inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich,
            final Iterable<Modalpartikel> modalpartikeln,
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabeSkopusSatz,
            @Nullable final Negationspartikelphrase negationspartikelphrase,
            @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabeSkopusVerbAllg,
            @Nullable final IAdvAngabeOderInterrogativWohinWoher advAngabeSkopusVerbWohinWoher) {
        this.verb = verb;
        this.inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich =
                inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich;
        this.modalpartikeln = ImmutableList.copyOf(modalpartikeln);
        this.advAngabeSkopusSatz = advAngabeSkopusSatz;
        this.negationspartikelphrase = negationspartikelphrase;
        this.advAngabeSkopusVerbAllg = advAngabeSkopusVerbAllg;
        this.advAngabeSkopusVerbWohinWoher = advAngabeSkopusVerbWohinWoher;
    }


    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AbstractAngabenfaehigesSyntPraedikat that =
                (AbstractAngabenfaehigesSyntPraedikat) o;
        return inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich
                == that.inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich &&
                verb.equals(that.verb) &&
                Objects.equals(modalpartikeln, that.modalpartikeln) &&
                Objects.equals(advAngabeSkopusSatz, that.advAngabeSkopusSatz) &&
                Objects.equals(negationspartikelphrase, that.negationspartikelphrase) &&
                Objects.equals(advAngabeSkopusVerbAllg, that.advAngabeSkopusVerbAllg) &&
                Objects
                        .equals(advAngabeSkopusVerbWohinWoher, that.advAngabeSkopusVerbWohinWoher);
    }

    @Override
    public int hashCode() {
        return Objects
                .hash(verb, inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich, modalpartikeln,
                        advAngabeSkopusSatz, negationspartikelphrase, advAngabeSkopusVerbAllg,
                        advAngabeSkopusVerbWohinWoher);
    }
}
