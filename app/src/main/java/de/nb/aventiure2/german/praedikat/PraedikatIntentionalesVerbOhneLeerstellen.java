package de.nb.aventiure2.german.praedikat;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.Collection;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.nb.aventiure2.annotations.Komplement;
import de.nb.aventiure2.annotations.Valenz;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativSkopusSatz;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativVerbAllg;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativWohinWoher;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.SubstPhrOderReflexivpronomen;

import static de.nb.aventiure2.german.base.Konstituentenfolge.kf;

/**
 * Ein Prädikat eines <i>Verbs mit intentionaler Bedeutung</i>,
 * in dem alle Leerstellen besetzt sind. Beispiele:
 * <ul>
 *     <li>"die junge Frau versucht ihre Haare wieder hinunterzulassen"
 *     <li>"die junge Frau versucht sich zu beruhigen"
 * </ul>
 * <p>
 * Adverbiale Angaben ("aus einer Laune heraus") können immer noch eingefügt werden.
 * <p>
 * "Die vom Subjekt bezeichnete Person will oder will nicht die Handlung
 * ausführen, die im Komplement genannt ist", siehe Peter Eisenberg,
 * Der Satz, S. 356 (Kapitel 11.2)
 */
public class PraedikatIntentionalesVerbOhneLeerstellen
        extends AbstractAngabenfaehigesPraedikatOhneLeerstellen {
    /**
     * "(...versucht) ihre Haare wieder hinunterzulassen"
     */
    @Komplement
    @Nonnull
    private final PraedikatOhneLeerstellen lexikalischerKern;

    @Valenz
    PraedikatIntentionalesVerbOhneLeerstellen(
            final Verb verb,
            final PraedikatOhneLeerstellen lexikalischerKern) {
        this(verb, ImmutableList.of(),
                null, null,
                null, lexikalischerKern);
    }

    private PraedikatIntentionalesVerbOhneLeerstellen(
            final Verb verb,
            final Iterable<Modalpartikel> modalpartikeln,
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabeSkopusSatz,
            @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabeSkopusVerbAllg,
            @Nullable final IAdvAngabeOderInterrogativWohinWoher advAngabeSkopusVerbWohinWoher,
            final PraedikatOhneLeerstellen lexikalischerKern) {
        super(verb, modalpartikeln,
                advAngabeSkopusSatz,
                advAngabeSkopusVerbAllg, advAngabeSkopusVerbWohinWoher);
        this.lexikalischerKern = lexikalischerKern;
    }

    @Override
    public PraedikatIntentionalesVerbOhneLeerstellen mitModalpartikeln(
            final Collection<Modalpartikel> modalpartikeln) {
        return new PraedikatIntentionalesVerbOhneLeerstellen(
                getVerb(),
                Iterables.concat(getModalpartikeln(), modalpartikeln),
                getAdvAngabeSkopusSatz(),
                getAdvAngabeSkopusVerbAllg(),
                getAdvAngabeSkopusVerbWohinWoher(),
                lexikalischerKern
        );
    }


    @Override
    public PraedikatIntentionalesVerbOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        return new PraedikatIntentionalesVerbOhneLeerstellen(
                getVerb(),
                getModalpartikeln(),
                advAngabe, getAdvAngabeSkopusVerbAllg(),
                getAdvAngabeSkopusVerbWohinWoher(),
                lexikalischerKern);
    }

    @Override
    public PraedikatIntentionalesVerbOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        return new PraedikatIntentionalesVerbOhneLeerstellen(
                getVerb(),
                getModalpartikeln(),
                getAdvAngabeSkopusSatz(), advAngabe,
                getAdvAngabeSkopusVerbWohinWoher(),
                lexikalischerKern
        );
    }

    @Override
    public PraedikatIntentionalesVerbOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativWohinWoher advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        return new PraedikatIntentionalesVerbOhneLeerstellen(
                getVerb(),
                getModalpartikeln(),
                getAdvAngabeSkopusSatz(),
                getAdvAngabeSkopusVerbAllg(),
                advAngabe,
                lexikalischerKern
        );
    }


    @Override
    public boolean kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden() {
        // "Gebeten dich zu waschen [gehst du ins Bad]"
        return true;
    }

    @Nullable
    @Override
    public Konstituentenfolge getSpeziellesVorfeldAlsWeitereOption(
            final Person person, final Numerus numerus) {
        @Nullable final Konstituentenfolge speziellesVorfeldFromSuper =
                super.getSpeziellesVorfeldAlsWeitereOption(person,
                        numerus);
        if (speziellesVorfeldFromSuper != null) {
            return speziellesVorfeldFromSuper;
        }

        // "Ihre Haare (versucht sie wieder hinunterzulassen)"
        return lexikalischerKern.getSpeziellesVorfeldAlsWeitereOption(person, numerus
        );
    }

    @Override
    @CheckReturnValue
    Konstituentenfolge getMittelfeldOhneLinksversetzungUnbetonterPronomen(
            final Person personSubjekt, final Numerus numerusSubjekt) {
        @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabeSkopusSatz =
                getAdvAngabeSkopusSatz();
        @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabeSkopusVerbAllg =
                getAdvAngabeSkopusVerbAllg();

        return Konstituentenfolge.joinToNullKonstituentenfolge(
                advAngabeSkopusSatz != null &&
                        advAngabeSkopusSatz.imMittelfeldErlaubt() ?
                        advAngabeSkopusSatz.getDescription(personSubjekt, numerusSubjekt) :
                        // "aus einer Laune heraus"
                        null, // (ins Nachfeld verschieben)
                kf(getModalpartikeln()), // "mal eben"
                advAngabeSkopusVerbAllg != null &&
                        advAngabeSkopusVerbAllg.imMittelfeldErlaubt() ?
                        advAngabeSkopusVerbAllg.getDescription(personSubjekt,
                                numerusSubjekt) :  // "erneut"
                        null, // (ins Nachfeld verschieben)
                advAngabeSkopusVerbAllg != null &&
                        !advAngabeSkopusVerbAllg.imMittelfeldErlaubt()
                        // Die advAngabeSkopusSatz schieben wir immer ins Nachfeld,
                        // daraus wird sie nach Möglichkeit ins Vorfeld gezogen werden.
                        ?
                        // -> Lex. Kern sollten wir aus dem Nachfeld vorziehen
                        lexikalischerKern.getZuInfinitiv(
                                // Es liegt Subjektkontrolle vor.
                                personSubjekt, numerusSubjekt
                        ) // "ihre Haare wieder hinunterzulassen"
                        : null, // (Normalfall: lexikalischer Kern im Nachfeld)
                getAdvAngabeSkopusVerbWohinWoherDescription(personSubjekt,
                        numerusSubjekt)
                // (kann es wohl gar nicht geben)
        );
    }

    @Nullable
    @Override
    SubstPhrOderReflexivpronomen getDat(
            final Person personSubjekt, final Numerus numerusSubjekt) {
        return null;
    }

    @Nullable
    @Override
    SubstPhrOderReflexivpronomen getAkk(
            final Person personSubjekt, final Numerus numerusSubjekt) {
        return null;
    }

    @Nullable
    @Override
    SubstPhrOderReflexivpronomen getZweitesAkk() {
        return null;
    }

    @Override
    public Konstituentenfolge getNachfeld(final Person personSubjekt,
                                          final Numerus numerusSubjekt) {
        @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabeSkopusSatz =
                getAdvAngabeSkopusSatz();
        @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabeSkopusVerbAllg =
                getAdvAngabeSkopusVerbAllg();
        return Konstituentenfolge.joinToNullKonstituentenfolge(
                advAngabeSkopusVerbAllg == null
                        || advAngabeSkopusVerbAllg.imMittelfeldErlaubt() ?
                        lexikalischerKern.getZuInfinitiv(
                                // Es liegt Subjektkontrolle vor.
                                personSubjekt, numerusSubjekt
                        ) // "(Du versuchst) dich zu waschen"
                        // Wir lassen die Kommata rund um den Infinitiv weg - das ist erlaubt.
                        : null,
                advAngabeSkopusVerbAllg != null
                        && !advAngabeSkopusVerbAllg.imMittelfeldErlaubt() ?
                        advAngabeSkopusVerbAllg
                                .getDescription(personSubjekt, numerusSubjekt)
                        // "glücklich, dich zu sehen"
                        : null,
                advAngabeSkopusSatz != null
                        && !advAngabeSkopusSatz.imMittelfeldErlaubt() ?
                        advAngabeSkopusSatz
                                .getDescription(personSubjekt, numerusSubjekt)
                        : null);
    }

    @Override
    public boolean umfasstSatzglieder() {
        return super.umfasstSatzglieder() || lexikalischerKern.umfasstSatzglieder();
    }

    @Nullable
    @Override
    public Konstituentenfolge getErstesInterrogativwort() {
        @Nullable
        Konstituentenfolge res = interroAdverbToKF(getAdvAngabeSkopusSatz());
        if (res != null) {
            return res;
        }

        res = interroAdverbToKF(getAdvAngabeSkopusVerbAllg());
        if (res != null) {
            return res;
        }

        res = interroAdverbToKF(getAdvAngabeSkopusVerbWohinWoher());
        if (res != null) {
            return res;
        }

        return lexikalischerKern.getErstesInterrogativwort();
    }
}
