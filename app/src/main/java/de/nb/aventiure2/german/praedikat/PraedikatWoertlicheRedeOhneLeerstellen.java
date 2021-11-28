package de.nb.aventiure2.german.praedikat;

import static de.nb.aventiure2.german.base.Konstituente.k;
import static de.nb.aventiure2.german.base.Konstituentenfolge.kf;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.Collection;
import java.util.Objects;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;

import de.nb.aventiure2.annotations.Komplement;
import de.nb.aventiure2.annotations.Valenz;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativSkopusSatz;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativVerbAllg;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativWohinWoher;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.Negationspartikelphrase;
import de.nb.aventiure2.german.base.PraedRegMerkmale;
import de.nb.aventiure2.german.base.SubstPhrOderReflexivpronomen;
import de.nb.aventiure2.german.base.WoertlicheRede;

/**
 * Ein Prädikat mit wörtlicher Rede, in dem alle Leerstellen besetzt sind. Beispiele:
 * <ul>
 *     <li>"„Lass dein Haar herunter“ rufen"
 * </ul>
 * <p>
 * Adverbiale Angaben ("laut") können immer noch eingefügt werden.
 */
public class PraedikatWoertlicheRedeOhneLeerstellen
        extends AbstractAngabenfaehigesPraedikatOhneLeerstellen {
    /**
     * Die wörtliche Rede
     */
    @Komplement
    @NonNull
    private final WoertlicheRede woertlicheRede;

    @Valenz
    PraedikatWoertlicheRedeOhneLeerstellen(
            final Verb verb, final WoertlicheRede woertlicheRede) {
        this(verb, woertlicheRede, ImmutableList.of(), null,
                null, null, null);
    }

    private PraedikatWoertlicheRedeOhneLeerstellen(
            final Verb verb, final WoertlicheRede woertlicheRede,
            final Iterable<Modalpartikel> modalpartikeln,
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabeSkopusSatz,
            @Nullable final Negationspartikelphrase negationspartikelphrase,
            @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabeSkopusVerbAllg,
            @Nullable final IAdvAngabeOderInterrogativWohinWoher advAngabeSkopusVerbWohinWoher) {
        super(verb, modalpartikeln,
                advAngabeSkopusSatz,
                negationspartikelphrase, advAngabeSkopusVerbAllg, advAngabeSkopusVerbWohinWoher);
        this.woertlicheRede = woertlicheRede;
    }

    @Override
    public PraedikatWoertlicheRedeOhneLeerstellen mitModalpartikeln(
            final Collection<Modalpartikel> modalpartikeln) {
        return new PraedikatWoertlicheRedeOhneLeerstellen(
                getVerb(), woertlicheRede,
                Iterables.concat(getModalpartikeln(), modalpartikeln),
                getAdvAngabeSkopusSatz(),
                getNegationspartikel(), getAdvAngabeSkopusVerbAllg(),
                getAdvAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public PraedikatWoertlicheRedeOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        return new PraedikatWoertlicheRedeOhneLeerstellen(
                getVerb(),
                woertlicheRede,
                getModalpartikeln(),
                advAngabe, getNegationspartikel(), getAdvAngabeSkopusVerbAllg(),
                getAdvAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public PraedikatWoertlicheRedeOhneLeerstellen neg() {
        return (PraedikatWoertlicheRedeOhneLeerstellen) super.neg();
    }

    @Override
    public PraedikatWoertlicheRedeOhneLeerstellen neg(
            @Nullable final Negationspartikelphrase negationspartikelphrase) {
        if (negationspartikelphrase == null) {
            return this;
        }

        return new PraedikatWoertlicheRedeOhneLeerstellen(
                getVerb(),
                woertlicheRede,
                getModalpartikeln(),
                getAdvAngabeSkopusSatz(), negationspartikelphrase, getAdvAngabeSkopusVerbAllg(),
                getAdvAngabeSkopusVerbWohinWoher());
    }

    @Override
    public PraedikatWoertlicheRedeOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        return new PraedikatWoertlicheRedeOhneLeerstellen(
                getVerb(),
                woertlicheRede,
                getModalpartikeln(),
                getAdvAngabeSkopusSatz(), getNegationspartikel(), advAngabe,
                getAdvAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public PraedikatWoertlicheRedeOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativWohinWoher advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        return new PraedikatWoertlicheRedeOhneLeerstellen(
                getVerb(),
                woertlicheRede,
                getModalpartikeln(),
                getAdvAngabeSkopusSatz(),
                getNegationspartikel(), getAdvAngabeSkopusVerbAllg(),
                advAngabe
        );
    }

    @Override
    public boolean kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden() {
        return false;
    }

    @Nullable
    @Override
    public Konstituente getSpeziellesVorfeldSehrErwuenscht(final PraedRegMerkmale praedRegMerkmale,
                                                           final boolean nachAnschlusswort) {
        final Konstituente speziellesVorfeldSehrErwuenschtFromSuper =
                super.getSpeziellesVorfeldSehrErwuenscht(praedRegMerkmale,
                        nachAnschlusswort);
        if (speziellesVorfeldSehrErwuenschtFromSuper != null) {
            return speziellesVorfeldSehrErwuenschtFromSuper;
        }

        if (getNegationspartikel() != null) {
            return null;
        }

        if (!nachAnschlusswort
                && !woertlicheRede.isLangOderMehrteilig()) {
            return k(woertlicheRede.getDescription(),
                    true,
                    true); // "„Kommt alle her[“, ]"
        }

        return null;
    }

    @Override
    @Nullable
    @CheckReturnValue
    Konstituentenfolge getMittelfeldOhneLinksversetzungUnbetonterPronomen(
            final PraedRegMerkmale praedRegMerkmale) {
        return Konstituentenfolge.joinToNullKonstituentenfolge(
                getAdvAngabeSkopusSatzDescriptionFuerMittelfeld(praedRegMerkmale),
                // "aus einer Laune heraus"
                kf(getModalpartikeln()),  // "mal eben"
                getAdvAngabeSkopusVerbTextDescriptionFuerMittelfeld(praedRegMerkmale), // "erneut"
                getNegationspartikel(), // "nicht"
                getAdvAngabeSkopusVerbWohinWoherDescription(praedRegMerkmale)
                // "in ein Kissen"
        );
    }

    @Nullable
    @Override
    SubstPhrOderReflexivpronomen getDat(final PraedRegMerkmale praedRegMerkmale) {
        return null;
    }

    @Nullable
    @Override
    SubstPhrOderReflexivpronomen getAkk(final PraedRegMerkmale praedRegMerkmale) {
        return null;
    }

    @Nullable
    @Override
    SubstPhrOderReflexivpronomen getZweitesAkk() {
        return null;
    }

    @Override
    public Konstituentenfolge getNachfeld(final PraedRegMerkmale praedRegMerkmale) {
        return Konstituentenfolge.joinToKonstituentenfolge(
                getAdvAngabeSkopusVerbTextDescriptionFuerZwangsausklammerung(praedRegMerkmale),
                getAdvAngabeSkopusSatzDescriptionFuerZwangsausklammerung(praedRegMerkmale),
                k(woertlicheRede.getDescription(),
                        true,
                        true)
                        .withVordoppelpunktNoetig()); // "[: ]„Kommt alle her[.“]"

        //  Laut http://www.pro-publish.com/korrektor/zeichensetzung-interpunktion/den
        //  -doppelpunkt-richtig-setzen/
        //  ist es möglich, nach dem Doppelpunkt den Satz fortzuführen - dabei darf die
        //  wörtliche Rede allerdings nicht mit Punkt abgeschlossen worden sein:
        //  "Peter sagte: „Ich fahre jetzt nach Hause“, doch es wurde noch viel später an diesem
        //  Abend."
    }

    @Override
    public boolean umfasstSatzglieder() {
        return true;
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

        return interroAdverbToKF(getAdvAngabeSkopusVerbWohinWoher());
    }

    @Nullable
    @Override
    @CheckReturnValue
    public Konstituentenfolge getRelativpronomen() {
        return null;
    }

    @Override
    public boolean equals(final @Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final PraedikatWoertlicheRedeOhneLeerstellen that =
                (PraedikatWoertlicheRedeOhneLeerstellen) o;
        return woertlicheRede.equals(that.woertlicheRede);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), woertlicheRede);
    }
}
