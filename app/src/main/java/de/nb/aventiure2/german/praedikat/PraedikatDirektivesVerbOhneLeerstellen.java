package de.nb.aventiure2.german.praedikat;


import androidx.annotation.NonNull;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.nb.aventiure2.annotations.Komplement;
import de.nb.aventiure2.annotations.Valenz;
import de.nb.aventiure2.german.base.GermanUtil;
import de.nb.aventiure2.german.base.Interrogativpronomen;
import de.nb.aventiure2.german.base.Kasus;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static de.nb.aventiure2.german.base.Konstituente.k;

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
public class PraedikatDirektivesVerbOhneLeerstellen
        extends AbstractAngabenfaehigesPraedikatOhneLeerstellen {
    /**
     * Der Kasus mit dem das direktive Verb steht.
     */
    @NonNull
    private final Kasus kasus;

    /**
     * Das Objekt, an das die "Direktive" geht
     */
    @Komplement
    private final SubstantivischePhrase objekt;

    /**
     * "(...bitten) ihre Haare wieder hinunterzulassen"
     */
    @Nonnull
    @Komplement
    private final PraedikatOhneLeerstellen lexikalischerKern;

    @Valenz
    PraedikatDirektivesVerbOhneLeerstellen(
            final Verb verb,
            final Kasus kasus,
            final SubstantivischePhrase objekt,
            final PraedikatOhneLeerstellen lexikalischerKern) {
        this(verb, kasus, objekt,
                null, null,
                null, lexikalischerKern);
    }

    private PraedikatDirektivesVerbOhneLeerstellen(
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

    @Override
    public boolean kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden() {
        return true;
    }

    @Nullable
    @Override
    public Konstituente getSpeziellesVorfeld(final Person person,
                                             final Numerus numerus) {
        @Nullable final Konstituente speziellesVorfeldFromSuper = super.getSpeziellesVorfeld(person,
                numerus);
        if (speziellesVorfeldFromSuper != null) {
            return speziellesVorfeldFromSuper;
        }


        final String objektImKasus = objekt.im(kasus);
        if (!"es" .equals(objektImKasus)) {
            // Wenn "es" ein Objekt ist, darf es nicht im Vorfeld stehen.
            // (Eisenberg Der Satz 5.4.2)
            return k(objektImKasus);  // "Die junge Frau (bittest du ...)"
        }

        return null;
    }

    @Override
    public Iterable<Konstituente> getMittelfeld(final Collection<Modalpartikel> modalpartikeln,
                                                final Person personSubjekt,
                                                final Numerus numerusSubjekt) {
        return Konstituente.joinToKonstituenten(
                getAdverbialeAngabeSkopusSatzDescription(), // "aus einer Laune heraus"
                GermanUtil.joinToNullString(modalpartikeln), // "mal eben"
                getAdverbialeAngabeSkopusVerbAllgDescriptionFuerMittelfeld(), // "erneut"
                objekt.im(kasus), // "die junge Frau"
                getAdverbialeAngabeSkopusVerbWohinWoherDescription()
                // (kann es wohl gar nicht geben)
        );

        //  Der lexikalische Kern könnte alternativ diskontinuierlich aufgeteilt werden:
        //   - "Ihre Haare die junge Frau wieder hinunterzulassen bitten"
        //   ("Ihre Haare bittest du die junge Frau wieder hinunterzulassen")

        //  Der lexikalische Kern könnte als dritte Alternative ebenfalls ins Mittelfeld
        //   gestellt werden (statt ins Nachfeld):
        //   - "Die junge Frau ihre Haare wieder hinunterzulassen bitten"
        //   - "Du hast die junge Frau ihre Haare wieder hinunterzulassen gebeten"
    }

    @Override
    public Iterable<Konstituente> getNachfeld(final Person personSubjekt,
                                              final Numerus numerusSubjekt) {
        return
                Konstituente.joinToKonstituenten(
                        lexikalischerKern.getZuInfinitiv(
                                // Es liegt "Objektkontrolle" vor.
                                objekt.getPerson(), objekt.getNumerusGenus().getNumerus()),
                        // "sich zu waschen"
                        getAdverbialeAngabeSkopusVerbAllgDescriptionFuerZwangsausklammerung()
                        // , glücklich, dich zu sehen
                        // Wir lassen diese Kommata weg - das ist erlaubt
                );
    }

    @Override
    public boolean umfasstSatzglieder() {
        return true;
    }

    @Override
    public boolean hatAkkusativobjekt() {
        return kasus == Kasus.AKK;
    }

    @Nullable
    @Override
    public Konstituente getErstesInterrogativpronomen() {
        if (objekt instanceof Interrogativpronomen) {
            return k(objekt.im(kasus));
        }

        return lexikalischerKern.getErstesInterrogativpronomen();
    }
}
