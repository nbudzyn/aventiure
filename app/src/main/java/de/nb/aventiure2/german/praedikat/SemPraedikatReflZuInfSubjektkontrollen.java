package de.nb.aventiure2.german.praedikat;


import static de.nb.aventiure2.german.base.Kasus.AKK;
import static de.nb.aventiure2.german.base.Konstituentenfolge.kf;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.Collection;
import java.util.Objects;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.nb.aventiure2.annotations.Komplement;
import de.nb.aventiure2.annotations.Valenz;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativSkopusSatz;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativVerbAllg;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativWohinWoher;
import de.nb.aventiure2.german.base.Kasus;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.Negationspartikelphrase;
import de.nb.aventiure2.german.base.PraedRegMerkmale;
import de.nb.aventiure2.german.base.Reflexivpronomen;
import de.nb.aventiure2.german.description.ITextContext;

/**
 * Ein "semantisches Prädikat" eines Verbs wie "sich [Akk] freuen, zu ...". Dabei ist es das
 * Subjekt, dass ... tut
 * (<i>Subjektkontrolle</i>). Beispiel:
 * <ul>
 *     <li>Du freust dich, sie zu sehen
 *     <li>Ich stelle mir vor, umzuziehen
 * </ul>
 * <p>
 * Adverbiale Angaben ("aus einer Laune heraus") können immer noch eingefügt werden.
 */
public class SemPraedikatReflZuInfSubjektkontrollen
        extends AbstractAngabenfaehigesSemPraedikatOhneLeerstellen {
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
    private final SemPraedikatOhneLeerstellen lexikalischerKern;

    @Valenz
    SemPraedikatReflZuInfSubjektkontrollen(
            final Verb verb,
            final Kasus kasus,
            final SemPraedikatOhneLeerstellen lexikalischerKern) {
        this(verb, kasus, ImmutableList.of(),
                null, null, null,
                null, lexikalischerKern);
    }

    private SemPraedikatReflZuInfSubjektkontrollen(
            final Verb verb,
            final Kasus kasus,
            final Iterable<Modalpartikel> modalpartikeln,
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabeSkopusSatz,
            @Nullable final Negationspartikelphrase negationspartikelphrase,
            @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabeSkopusVerbAllg,
            @Nullable final IAdvAngabeOderInterrogativWohinWoher advAngabeSkopusVerbWohinWoher,
            final SemPraedikatOhneLeerstellen lexikalischerKern) {
        super(verb, modalpartikeln,
                advAngabeSkopusSatz,
                negationspartikelphrase, advAngabeSkopusVerbAllg, advAngabeSkopusVerbWohinWoher);
        this.kasus = kasus;
        this.lexikalischerKern = lexikalischerKern;
    }

    @Override
    public SemPraedikatReflZuInfSubjektkontrollen mitModalpartikeln(
            final Collection<Modalpartikel> modalpartikeln) {
        return new SemPraedikatReflZuInfSubjektkontrollen(
                getVerb(),
                kasus,
                Iterables.concat(getModalpartikeln(), modalpartikeln),
                getAdvAngabeSkopusSatz(),
                getNegationspartikel(), getAdvAngabeSkopusVerbAllg(),
                getAdvAngabeSkopusVerbWohinWoher(),
                lexikalischerKern
        );
    }

    @Override
    public SemPraedikatReflZuInfSubjektkontrollen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        return new SemPraedikatReflZuInfSubjektkontrollen(
                getVerb(),
                kasus,
                getModalpartikeln(),
                advAngabe, getNegationspartikel(), getAdvAngabeSkopusVerbAllg(),
                getAdvAngabeSkopusVerbWohinWoher(),
                lexikalischerKern);
    }

    @Override
    public SemPraedikatReflZuInfSubjektkontrollen neg() {
        return (SemPraedikatReflZuInfSubjektkontrollen) super.neg();
    }

    @Override
    public SemPraedikatReflZuInfSubjektkontrollen neg(
            @Nullable final Negationspartikelphrase negationspartikelphrase) {
        if (negationspartikelphrase == null) {
            return this;
        }

        return new SemPraedikatReflZuInfSubjektkontrollen(
                getVerb(), kasus,
                getModalpartikeln(),
                getAdvAngabeSkopusSatz(), negationspartikelphrase, getAdvAngabeSkopusVerbAllg(),
                getAdvAngabeSkopusVerbWohinWoher(),
                lexikalischerKern);
    }

    @Override
    public SemPraedikatReflZuInfSubjektkontrollen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        return new SemPraedikatReflZuInfSubjektkontrollen(
                getVerb(),
                kasus,
                getModalpartikeln(),
                getAdvAngabeSkopusSatz(), getNegationspartikel(), advAngabe,
                getAdvAngabeSkopusVerbWohinWoher(),
                lexikalischerKern
        );
    }

    @Override
    public SemPraedikatReflZuInfSubjektkontrollen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativWohinWoher advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        return new SemPraedikatReflZuInfSubjektkontrollen(
                getVerb(),
                kasus,
                getModalpartikeln(),
                getAdvAngabeSkopusSatz(),
                getNegationspartikel(), getAdvAngabeSkopusVerbAllg(),
                advAngabe,
                lexikalischerKern
        );
    }

    @Override
    public boolean kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden() {
        // *"Dich gefreut, sie zu sehen, [gehst du ins Bad]"
        return false;
    }

    @Override
    public boolean hatAkkusativobjekt() {
        return kasus == AKK;
    }

    @Override
    TopolFelder getTopolFelder(final ITextContext textContext,
                               final PraedRegMerkmale praedRegMerkmale) {
        @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabeSkopusSatz =
                getAdvAngabeSkopusSatz();
        @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabeSkopusVerbAllg =
                getAdvAngabeSkopusVerbAllg();

        final Reflexivpronomen reflexivpronomen = Reflexivpronomen.get(praedRegMerkmale);

        return new TopolFelder(
                new Mittelfeld(
                        Konstituentenfolge.joinToKonstituentenfolge(
                                reflexivpronomen.imK(kasus),
                                // "dich"
                                advAngabeSkopusSatz != null &&
                                        advAngabeSkopusSatz.imMittelfeldErlaubt() ?
                                        advAngabeSkopusSatz.getDescription(praedRegMerkmale) :
                                        // "aus einer Laune heraus"
                                        null, // (ins Nachfeld verschieben)
                                kf(getModalpartikeln()), // "mal eben"
                                advAngabeSkopusVerbAllg != null &&
                                        advAngabeSkopusVerbAllg.imMittelfeldErlaubt() ?
                                        advAngabeSkopusVerbAllg.getDescription(praedRegMerkmale) :
                                        // "erneut"
                                        null, // (ins Nachfeld verschieben)
                                getNegationspartikel(), // "nicht"
                                advAngabeSkopusVerbAllg != null &&
                                        !advAngabeSkopusVerbAllg.imMittelfeldErlaubt()
                                        // Die advAngabeSkopusSatz schieben wir immer ins Nachfeld,
                                        // daraus wird sie nach Möglichkeit ins Vorfeld gezogen
                                        // werden.
                                        ?
                                        // -> Lex. Kern sollten wir aus dem Nachfeld vorziehen
                                        Konstituentenfolge.schliesseInKommaEin(
                                                lexikalischerKern.getZuInfinitiv(
                                                        // Es liegt "Subjektkontrolle" vor.
                                                        textContext, praedRegMerkmale
                                                )) // ", Rapunzel zu sehen[, ]"
                                        : null, // (Normalfall: lexikalischer Kern im Nachfeld)
                                getAdvAngabeSkopusVerbWohinWoherDescription(praedRegMerkmale)
                                // (kann es wohl gar nicht geben)
                        ), reflexivpronomen, kasus),
                new Nachfeld(
                        Konstituentenfolge.joinToNullKonstituentenfolge(
                                advAngabeSkopusVerbAllg == null
                                        || advAngabeSkopusVerbAllg.imMittelfeldErlaubt() ?
                                        Konstituentenfolge.schliesseInKommaEin(
                                                lexikalischerKern.getZuInfinitiv(
                                                        // Es liegt Subjektkontrolle vor.
                                                        textContext, praedRegMerkmale
                                                )) // "(Du freust dich), Rapunzel zu sehen[,] "
                                        // Wir lassen die Kommata rund um den Infinitiv weg - das
                                        // ist
                                        // erlaubt.
                                        : null,
                                advAngabeSkopusVerbAllg != null
                                        && !advAngabeSkopusVerbAllg.imMittelfeldErlaubt() ?
                                        advAngabeSkopusVerbAllg.getDescription(praedRegMerkmale)
                                        // "glücklich, dich zu sehen"
                                        : null,
                                advAngabeSkopusSatz != null
                                        && !advAngabeSkopusSatz.imMittelfeldErlaubt() ?
                                        advAngabeSkopusSatz.getDescription(praedRegMerkmale)
                                        : null)));
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
    @CheckReturnValue
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

        res = lexikalischerKern.getErstesInterrogativwort();
        if (res != null) {
            return res;
        }

        return interroAdverbToKF(getAdvAngabeSkopusVerbWohinWoher());
    }

    @Nullable
    @Override
    @CheckReturnValue
    public Konstituentenfolge getRelativpronomen() {
        return lexikalischerKern.getRelativpronomen();
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
        final SemPraedikatReflZuInfSubjektkontrollen that =
                (SemPraedikatReflZuInfSubjektkontrollen) o;
        return kasus == that.kasus &&
                lexikalischerKern.equals(that.lexikalischerKern);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), kasus, lexikalischerKern);
    }
}