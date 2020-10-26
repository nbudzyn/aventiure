package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.base.Preconditions;

import de.nb.aventiure2.german.base.Kasus;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static de.nb.aventiure2.german.base.Kasus.AKK;
import static de.nb.aventiure2.german.base.Kasus.DAT;

/**
 * Ein Verb wie bitten, anregen, warnen, zwingen etc.
 * <p>
 * Zu direktiven Verben siehe Peter Eisenberg, Der Satz, S. 357 (Kapitel 11.2)
 */
public enum DirektivesVerb implements Praedikat {
    // "Rapunzel bitten, die Haare herunterzulassen"
    BITTEN("bitten", AKK, "bittest", Perfektbildung.HABEN, "gebeten");
    // Weitere direktive Verben sind anregen, warnen, zwingen,
    // hindern, beschwören, auffordern, überreden, beauftragen,
    // raten, erlauben, empfehlen, verbieten, gestatten

    /**
     * Das Verb an sich, ohne Informationen zur Valenz, ohne Ergänzungen, ohne
     * Angaben
     */
    @NonNull
    private final Verb verb;

    /**
     * Der Kasus mit dem dieses direktive Verb steht (den dieses Verb regiert).
     */
    @NonNull
    private final Kasus kasus;

    DirektivesVerb(@NonNull final String infinitiv,
                   @NonNull final Kasus kasus,
                   @NonNull final String duForm,
                   final Perfektbildung perfektbildung, final String partizipII) {
        this(new Verb(infinitiv, duForm, perfektbildung, partizipII), kasus);
    }

    DirektivesVerb(@NonNull final String infinitiv,
                   @NonNull final Kasus kasus,
                   @NonNull final String duForm,
                   @Nullable final String partikel,
                   final Perfektbildung perfektbildung, final String partizipII) {
        this(new Verb(infinitiv, duForm, partikel, perfektbildung, partizipII), kasus);
    }

    DirektivesVerb(@NonNull final Verb verb,
                   @NonNull final Kasus kasus) {
        Preconditions.checkArgument(
                kasus == DAT || kasus == AKK,
                "Direktives Verb mit ungültigem Kasus: " + kasus);
        this.verb = verb;
        this.kasus = kasus;
    }

    public PraedikatDirektivesVerbObjMitEinerLeerstelleFuerLexikalischenKern mitObj(
            final SubstantivischePhrase objekt) {
        return new PraedikatDirektivesVerbObjMitEinerLeerstelleFuerLexikalischenKern(verb,
                kasus,
                objekt);
    }
}
