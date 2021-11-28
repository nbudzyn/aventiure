package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Objects;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import de.nb.aventiure2.annotations.Komplement;
import de.nb.aventiure2.annotations.Valenz;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativSkopusSatz;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativVerbAllg;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativWohinWoher;
import de.nb.aventiure2.german.base.KasusOderPraepositionalkasus;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.Negationspartikelphrase;
import de.nb.aventiure2.german.base.Relativpronomen;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

/**
 * Ein (syntaktisches) Prädikat (Verb ggf. mit Präfix) bei dem das Verb mit einem Subjekt und einem
 * (Präpositional-) Objekt steht.
 */
@Immutable
class SyntPraedikatSubjObj extends AbstractAngabenfaehigesSyntPraedikat {
    /**
     * Der Kasus (z.B. Akkusativ, "die Kugel nehmen") oder Präpositionalkasus
     * (z.B. "mit dem Frosch reden"), mit dem dieses Verb steht (den dieses Verb regiert).
     */
    @NonNull
    private final KasusOderPraepositionalkasus kasusOderPraepositionalkasus;

    /**
     * Das Objekt
     */
    @Komplement
    private final SubstantivischePhrase objekt;

    SyntPraedikatSubjObj(final Verb verb,
                         final KasusOderPraepositionalkasus kasusOderPraepositionalkasus,
                         final SubstantivischePhrase objekt) {
        this(verb, kasusOderPraepositionalkasus,
                false,
                objekt);
    }

    @Valenz
    private SyntPraedikatSubjObj(final Verb verb,
                                 final KasusOderPraepositionalkasus kasusOderPraepositionalkasus,
                                 final boolean inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich,
                                 final SubstantivischePhrase objekt) {
        this(verb, kasusOderPraepositionalkasus,
                inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich,
                objekt,
                ImmutableList.of(),
                null, null, null,
                null);
    }

    SyntPraedikatSubjObj(
            final Verb verb,
            final KasusOderPraepositionalkasus kasusOderPraepositionalkasus,
            final boolean inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich,
            final SubstantivischePhrase objekt,
            final Iterable<Modalpartikel> modalpartikeln,
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabeSkopusSatz,
            @Nullable final Negationspartikelphrase negationspartikelphrase,
            @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabeSkopusVerbAllg,
            @Nullable final IAdvAngabeOderInterrogativWohinWoher advAngabeSkopusVerbWohinWoher) {
        super(verb, inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich, modalpartikeln,
                advAngabeSkopusSatz,
                negationspartikelphrase, advAngabeSkopusVerbAllg, advAngabeSkopusVerbWohinWoher);
        this.kasusOderPraepositionalkasus = kasusOderPraepositionalkasus;
        this.objekt = objekt;
    }

    @Nullable
    @Override
    @CheckReturnValue
    public Konstituentenfolge getRelativpronomen() {
        if (objekt instanceof Relativpronomen) {
            return objekt.imK(kasusOderPraepositionalkasus);
        }

        return null;
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final SyntPraedikatSubjObj that =
                (SyntPraedikatSubjObj) o;
        return kasusOderPraepositionalkasus.equals(that.kasusOderPraepositionalkasus) &&
                Objects.equals(objekt, that.objekt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), kasusOderPraepositionalkasus, objekt);
    }
}
