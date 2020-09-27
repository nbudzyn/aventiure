package de.nb.aventiure2.german.praedikat;


import androidx.annotation.NonNull;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.Kasus;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static de.nb.aventiure2.german.base.GermanUtil.joinToNull;
import static de.nb.aventiure2.german.base.Person.P3;

/**
 * Ein Prädikat eines <i>direktiven Verbs</i>, in dem alle Leerstellen besetzt sind. Beispiele:
 * <ul>
 *     <li>"die junge Frau bitten ihre Haare wieder hinunterzulassen"
 *     <li>"die junge Frau bitten sich zu beruhigen"
 * </ul>
 * <p>
 * Adverbiale Angaben ("aus einer Laune heraus") können immer noch eingefügt werden.
 * <p>
 * Zu direktiven Verben siehe Peter Eisenberg, Der Satz, S. 357 (Kapitel 11.2)
 */
public class PraedikatDirektivesVerbOhneLeerstellen extends AbstractPraedikatOhneLeerstellen {
    /**
     * Der Kasus mit dem das direktive Verb steht.
     */
    @NonNull
    private final Kasus kasus;

    /**
     * Das Objekt, an das die "Direktive" geht
     */
    private final SubstantivischePhrase objekt;

    /**
     * "(...bitten) ihre Haare wieder hinunterzulassen"
     */
    @Nonnull
    private final PraedikatOhneLeerstellen lexikalischerKern;

    public PraedikatDirektivesVerbOhneLeerstellen(
            final Verb verb,
            final Kasus kasus,
            final SubstantivischePhrase objekt,
            final PraedikatOhneLeerstellen lexikalischerKern) {
        this(verb, kasus, objekt,
                null, null,
                null, lexikalischerKern);
    }

    PraedikatDirektivesVerbOhneLeerstellen(
            final Verb verb,
            final Kasus kasus,
            final SubstantivischePhrase objekt,
            @Nullable final AdverbialeAngabeSkopusSatz adverbialeAngabeSkopusSatz,
            @Nullable final AdverbialeAngabeSkopusVerbAllg adverbialeAngabeSkopusVerbAllg,
            @Nullable
            final AdverbialeAngabeSkopusVerbWohinWoher adverbialeAngabeSkopusVerbWohinWoher,
            final PraedikatOhneLeerstellen lexikalischerKern) {
        super(verb, adverbialeAngabeSkopusSatz,
                adverbialeAngabeSkopusVerbAllg, adverbialeAngabeSkopusVerbWohinWoher);
        this.kasus = kasus;
        this.objekt = objekt;
        this.lexikalischerKern = lexikalischerKern;
    }

    @Override
    public PraedikatDirektivesVerbOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusSatz adverbialeAngabe) {
        if (adverbialeAngabe == null) {
            return this;
        }

        return new PraedikatDirektivesVerbOhneLeerstellen(
                getVerb(),
                kasus,
                objekt,
                adverbialeAngabe, getAdverbialeAngabeSkopusVerbAllg(),
                getAdverbialeAngabeSkopusVerbWohinWoher(),
                lexikalischerKern);
    }

    @Override
    public PraedikatDirektivesVerbOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusVerbAllg adverbialeAngabe) {
        if (adverbialeAngabe == null) {
            return this;
        }

        return new PraedikatDirektivesVerbOhneLeerstellen(
                getVerb(),
                kasus,
                objekt,
                getAdverbialeAngabeSkopusSatz(), adverbialeAngabe,
                getAdverbialeAngabeSkopusVerbWohinWoher(),
                lexikalischerKern
        );
    }

    @Override
    public PraedikatDirektivesVerbOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusVerbWohinWoher adverbialeAngabe) {
        if (adverbialeAngabe == null) {
            return this;
        }

        return new PraedikatDirektivesVerbOhneLeerstellen(
                getVerb(),
                kasus,
                objekt,
                getAdverbialeAngabeSkopusSatz(),
                getAdverbialeAngabeSkopusVerbAllg(),
                adverbialeAngabe,
                lexikalischerKern
        );
    }

    @Override
    public boolean duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen() {
        return true;
    }

    @Nullable
    @Override
    public String getSpeziellesVorfeld() {
        final String speziellesVorfeldFromSuper = super.getSpeziellesVorfeld();
        if (speziellesVorfeldFromSuper != null) {
            return speziellesVorfeldFromSuper;
        }

        return objekt.im(kasus); // "Die junge Frau (bittest du ...)"
    }

    @Override
    public String getMittelfeld(final Collection<Modalpartikel> modalpartikeln) {
        return joinToNull(
                getAdverbialeAngabeSkopusSatz(), // "aus einer Laune heraus"
                joinToNull(modalpartikeln), // "mal eben"
                getAdverbialeAngabeSkopusVerbAllg(), // "erneut"
                objekt.im(kasus), // "die junge Frau"
                getAdverbialeAngabeSkopusVerbWohinWoher() // (kann es wohl gar nicht geben)
        );

        //  STORY Der lexikalische Kern könnte ebenfalls ins Mittelfeld gestellt werden:
        //   - "Die junge Frau ihre Haare wieder hinunterzulassen bitten"

        //  STORY Der lexikalische Kern könnte diskontinuierlich aufgeteilt werden:
        //   - "Ihre Haare die junge Frau wieder hinunterzulassen bitten"
        //   ("Ihre Haare bittest du die junge Frau wieder hinunterzulassen")
    }

    @Override
    public String getNachfeld() {
        return lexikalischerKern.getZuInfinitiv(
                P3, objekt.getNumerusGenus().getNumerus()
        ); // "sich zu waschen"
        // Wir lassen die Kommata weg - das ist erlaubt und dann
        // kann man auch mehrere solche Sätze hintereinanderhängen
    }
}
