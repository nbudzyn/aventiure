package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Ein Modalverb. Modalverben regieren den reinen Infinitiv
 * (z.B. <i>etw. tun können</i>).
 */
public enum Modalverb implements VerbMitValenz {
    DUERFEN("dürfen",
            "darf", "darfst", "darf", "dürft",
            Perfektbildung.HABEN,
            // Er hat nicht schlafen dürfen.
            // ?Er hat nicht schlafen gedurft.
            "dürfen"),
    // "Rapunzel kann die Haare herunterlassen"
    KOENNEN("können",
            "kann", "kannst", "kann", "könnt",
            Perfektbildung.HABEN,
            // Er hat nicht schlafen können.
            // *Er hat nicht schlafen gekonnt.
            "können"),
    MOEGEN("mögen",
            "mag", "magst", "mag", "mögt",
            Perfektbildung.HABEN,
            // Er hat nicht schlafen mögen.
            // *Er hat nicht schlafen gemocht.
            "mögen"),
    MUESSEN("müssen",
            "muss", "musst", "muss", "müsst",
            Perfektbildung.HABEN,
            // Er hat nicht schlafen müssen.
            // *Er hat nicht schlafen gemusst.
            "müssen"),
    SOLLEN("sollen",
            "soll", "sollst", "soll", "sollt",
            Perfektbildung.HABEN,
            // Er hat nicht schlafen sollen.
            // ?Er hat nicht schlafen gesollt.
            "sollen"),
    WOLLEN("wollen",
            "will", "willst", "will", "wollt",
            Perfektbildung.HABEN,
            // Er hat nicht schlafen wollen.
            // ?Er hat nicht schlafen gewollt.
            "wollen");

    // (Für brauchen / nicht brauchen, möchten, lassen, werden gibt es spezielle Regeln.
    // Ob das Modalverben sind, ist eher Ansichtssache.)

    /**
     * Das Verb an sich, ohne Informationen zur Valenz, ohne Ergänzungen, ohne
     * Angaben
     */
    @NonNull
    private final Verb verb;

    Modalverb(@NonNull final String infinitiv,
              @NonNull final String ichForm,
              @NonNull final String duForm,
              @NonNull final String erSieEsForm,
              @NonNull final String ihrForm,
              final Perfektbildung perfektbildung, final String partizipII) {
        this(new Verb(infinitiv, ichForm, duForm, erSieEsForm, ihrForm, perfektbildung,
                partizipII));

        checkArgument(infinitiv.equals(partizipII),
                "Bei Modalverben müssen Infinitiv und Partizip II gleich sein "
                        + "(\"Ersatzinfinitiv\"). Infinitiv: %s, Partizip II: %s",
                infinitiv, partizipII);
    }

    Modalverb(@NonNull final Verb verb) {
        this.verb = verb;
    }

    /**
     * Füllt die Leerstelle für den lexikalischen Kern.
     */
    public PraedikatModalverbOhneLeerstellen mitLexikalischemKern(
            final PraedikatOhneLeerstellen lexikalischerKern) {
        return new PraedikatModalverbOhneLeerstellen(verb, lexikalischerKern);
    }

    @Override
    @NonNull
    public Verb getVerb() {
        return verb;
    }
}
