package de.nb.aventiure2.german.praedikat;


import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.nb.aventiure2.annotations.Komplement;
import de.nb.aventiure2.annotations.Valenz;
import de.nb.aventiure2.german.base.Kasus;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.Reflexivpronomen;
import de.nb.aventiure2.german.base.SubstPhrOderReflexivpronomen;

import static de.nb.aventiure2.german.base.Kasus.AKK;
import static de.nb.aventiure2.german.base.Kasus.DAT;
import static de.nb.aventiure2.german.base.Konstituentenfolge.kf;

/**
 * Ein Prädikat eines Verbs wie "sich [Akk] freuen, zu ...". Dabei ist es das Subjekt, dass ... tut
 * (<i>Subjektkontrolle</i>). Beispiel:
 * <ul>
 *     <li>Du freust dich, sie zu sehen
 *     <li>Ich stelle mir vor, umzuziehen
 * </ul>
 * <p>
 * Adverbiale Angaben ("aus einer Laune heraus") können immer noch eingefügt werden.
 */
public class PraedikatReflZuInfSubjektkontrollen
        extends AbstractAngabenfaehigesPraedikatOhneLeerstellen {
    /**
     * Der Kasus mit dem das Verb reflexiv steht - z.B. Akkusativ ("sich freuen") oder
     * Dativ ("sich vorstellen")
     */
    @NonNull
    private final Kasus kasus;

    /**
     * "(...freut sich, ) das du kommst"
     */
    @Komplement
    @Nonnull
    private final PraedikatOhneLeerstellen lexikalischerKern;

    @Valenz
    PraedikatReflZuInfSubjektkontrollen(
            final Verb verb,
            final Kasus kasus,
            final PraedikatOhneLeerstellen lexikalischerKern) {
        this(verb, kasus, ImmutableList.of(),
                null, null,
                null, lexikalischerKern);
    }

    private PraedikatReflZuInfSubjektkontrollen(
            final Verb verb,
            final Kasus kasus,
            final Iterable<Modalpartikel> modalpartikeln,
            @Nullable final AdverbialeAngabeSkopusSatz adverbialeAngabeSkopusSatz,
            @Nullable final AdverbialeAngabeSkopusVerbAllg adverbialeAngabeSkopusVerbAllg,
            @Nullable
            final AdverbialeAngabeSkopusVerbWohinWoher adverbialeAngabeSkopusVerbWohinWoher,
            final PraedikatOhneLeerstellen lexikalischerKern) {
        super(verb, modalpartikeln, adverbialeAngabeSkopusSatz,
                adverbialeAngabeSkopusVerbAllg, adverbialeAngabeSkopusVerbWohinWoher);
        this.kasus = kasus;
        this.lexikalischerKern = lexikalischerKern;
    }

    @Override
    public PraedikatReflZuInfSubjektkontrollen mitModalpartikeln(
            final Collection<Modalpartikel> modalpartikeln) {
        return new PraedikatReflZuInfSubjektkontrollen(
                getVerb(),
                kasus,
                Iterables.concat(getModalpartikeln(), modalpartikeln),
                getAdverbialeAngabeSkopusSatz(),
                getAdverbialeAngabeSkopusVerbAllg(),
                getAdverbialeAngabeSkopusVerbWohinWoher(),
                lexikalischerKern
        );
    }

    @Override
    public PraedikatReflZuInfSubjektkontrollen mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusSatz adverbialeAngabe) {
        if (adverbialeAngabe == null) {
            return this;
        }

        return new PraedikatReflZuInfSubjektkontrollen(
                getVerb(),
                kasus,
                getModalpartikeln(),
                adverbialeAngabe, getAdverbialeAngabeSkopusVerbAllg(),
                getAdverbialeAngabeSkopusVerbWohinWoher(),
                lexikalischerKern);
    }

    @Override
    public PraedikatReflZuInfSubjektkontrollen mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusVerbAllg adverbialeAngabe) {
        if (adverbialeAngabe == null) {
            return this;
        }

        return new PraedikatReflZuInfSubjektkontrollen(
                getVerb(),
                kasus,
                getModalpartikeln(),
                getAdverbialeAngabeSkopusSatz(), adverbialeAngabe,
                getAdverbialeAngabeSkopusVerbWohinWoher(),
                lexikalischerKern
        );
    }

    @Override
    public PraedikatReflZuInfSubjektkontrollen mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusVerbWohinWoher adverbialeAngabe) {
        if (adverbialeAngabe == null) {
            return this;
        }

        return new PraedikatReflZuInfSubjektkontrollen(
                getVerb(),
                kasus,
                getModalpartikeln(),
                getAdverbialeAngabeSkopusSatz(),
                getAdverbialeAngabeSkopusVerbAllg(),
                adverbialeAngabe,
                lexikalischerKern
        );
    }

    @Override
    public boolean kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden() {
        // *"Dich gefreut, sie zu sehen, [gehst du ins Bad]"
        return false;
    }

    @Nullable
    @Override
    public Konstituente getSpeziellesVorfeldAlsWeitereOption(
            final Person person, final Numerus numerus) {
        @Nullable final Konstituente speziellesVorfeldFromSuper =
                super.getSpeziellesVorfeldAlsWeitereOption(person, numerus);
        if (speziellesVorfeldFromSuper != null) {
            return speziellesVorfeldFromSuper;
        }

        // ?"Rapunzel freust du, dich zu sehen" - eher nicht.

        return null;
    }

    @Override
    Konstituentenfolge getMittelfeldOhneLinksversetzungUnbetonterPronomen(
            final Person personSubjekt, final Numerus numerusSubjekt) {
        @Nullable final AdverbialeAngabeSkopusSatz adverbialeAngabeSkopusSatz =
                getAdverbialeAngabeSkopusSatz();
        @Nullable final AdverbialeAngabeSkopusVerbAllg adverbialeAngabeSkopusVerbAllg =
                getAdverbialeAngabeSkopusVerbAllg();

        return Konstituentenfolge.joinToKonstituentenfolge(
                Reflexivpronomen.get(personSubjekt, numerusSubjekt).imK(kasus), // "dich"
                adverbialeAngabeSkopusSatz != null &&
                        adverbialeAngabeSkopusSatz.imMittelfeldErlaubt() ?
                        adverbialeAngabeSkopusSatz.getDescription(personSubjekt, numerusSubjekt) :
                        // "aus einer Laune heraus"
                        null, // (ins Nachfeld verschieben)
                kf(getModalpartikeln()), // "mal eben"
                adverbialeAngabeSkopusVerbAllg != null &&
                        adverbialeAngabeSkopusVerbAllg.imMittelfeldErlaubt() ?
                        adverbialeAngabeSkopusVerbAllg.getDescription(personSubjekt,
                                numerusSubjekt) :  // "erneut"
                        null, // (ins Nachfeld verschieben)
                adverbialeAngabeSkopusVerbAllg != null &&
                        !adverbialeAngabeSkopusVerbAllg.imMittelfeldErlaubt()
                        // Die adverbialeAngabeSkopusSatz schieben wir immer ins Nachfeld,
                        // daraus wird sie nach Möglichkeit ins Vorfeld gezogen werden.
                        ?
                        // -> Lex. Kern sollten wir aus dem Nachfeld vorziehen
                        Konstituentenfolge.schliesseInKommaEin(
                                lexikalischerKern.getZuInfinitiv(
                                        // Es liegt "Subjektkontrolle" vor.
                                        personSubjekt, numerusSubjekt
                                )) // ", Rapunzel zu sehen[, ]"
                        : null, // (Normalfall: lexikalischer Kern im Nachfeld)
                getAdverbialeAngabeSkopusVerbWohinWoherDescription(personSubjekt,
                        numerusSubjekt)
                // (kann es wohl gar nicht geben)
        );
    }

    @Nullable
    @Override
    SubstPhrOderReflexivpronomen getDat(
            final Person personSubjekt, final Numerus numerusSubjekt) {
        if (kasus == DAT) {
            return Reflexivpronomen.get(personSubjekt, numerusSubjekt);
        }

        return null;
    }

    @Nullable
    @Override
    SubstPhrOderReflexivpronomen getAkk(
            final Person personSubjekt, final Numerus numerusSubjekt) {
        if (kasus == AKK) {
            return Reflexivpronomen.get(personSubjekt, numerusSubjekt);
        }

        return null;
    }

    @Nullable
    @Override
    SubstPhrOderReflexivpronomen getZweitesAkk() {
        return null;
    }

    @Override
    @Nullable
    public Konstituentenfolge getNachfeld(final Person personSubjekt,
                                          final Numerus numerusSubjekt) {
        @Nullable final AdverbialeAngabeSkopusSatz adverbialeAngabeSkopusSatz =
                getAdverbialeAngabeSkopusSatz();
        @Nullable final AdverbialeAngabeSkopusVerbAllg adverbialeAngabeSkopusVerbAllg =
                getAdverbialeAngabeSkopusVerbAllg();
        return Konstituentenfolge.joinToNullKonstituentenfolge(
                adverbialeAngabeSkopusVerbAllg == null
                        || adverbialeAngabeSkopusVerbAllg.imMittelfeldErlaubt() ?
                        Konstituentenfolge.schliesseInKommaEin(
                                lexikalischerKern.getZuInfinitiv(
                                        // Es liegt Subjektkontrolle vor.
                                        personSubjekt, numerusSubjekt
                                )) // "(Du freust dich), Rapunzel zu sehen[,] "
                        // Wir lassen die Kommata rund um den Infinitiv weg - das ist erlaubt.
                        : null,
                adverbialeAngabeSkopusVerbAllg != null
                        && !adverbialeAngabeSkopusVerbAllg.imMittelfeldErlaubt() ?
                        adverbialeAngabeSkopusVerbAllg.getDescription(personSubjekt, numerusSubjekt)
                        // "glücklich, dich zu sehen"
                        : null,
                adverbialeAngabeSkopusSatz != null
                        && !adverbialeAngabeSkopusSatz.imMittelfeldErlaubt() ?
                        adverbialeAngabeSkopusSatz
                                .getDescription(personSubjekt, numerusSubjekt)
                        : null);
    }

    @Override
    public boolean umfasstSatzglieder() {
        return super.umfasstSatzglieder()
                // Ich bin nicht sicher, ob das reflexivische "sich" als Satzglied gilt.
                // Sagen wir mal sicherheitshalber: Nein.
                || lexikalischerKern.umfasstSatzglieder();
    }

    @Nullable
    @Override
    public Konstituente getErstesInterrogativpronomen() {
        return lexikalischerKern.getErstesInterrogativpronomen();
    }
}
