package de.nb.aventiure2.german.praedikat;

import static de.nb.aventiure2.german.base.Konstituente.k;
import static de.nb.aventiure2.german.base.Konstituentenfolge.kf;
import static de.nb.aventiure2.german.praedikat.Praedikatseinbindung.firstInterrogativwort;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.Collection;
import java.util.Objects;

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
import de.nb.aventiure2.german.base.WoertlicheRede;
import de.nb.aventiure2.german.description.ITextContext;

/**
 * Ein "semantisches Prädikat" mit wörtlicher Rede, in dem alle Leerstellen besetzt sind. Beispiele:
 * <ul>
 *     <li>"„Lass dein Haar herunter“ rufen"
 * </ul>
 * <p>
 * Adverbiale Angaben ("laut") können immer noch eingefügt werden.
 */
public class SemPraedikatWoertlicheRedeOhneLeerstellen
        extends AbstractAngabenfaehigesSemPraedikatOhneLeerstellen {
    /**
     * Die wörtliche Rede
     */
    @Komplement
    @NonNull
    private final WoertlicheRede woertlicheRede;

    @Valenz
    SemPraedikatWoertlicheRedeOhneLeerstellen(
            final Verb verb, final WoertlicheRede woertlicheRede) {
        this(verb, woertlicheRede, ImmutableList.of(), null,
                null, null, null);
    }

    private SemPraedikatWoertlicheRedeOhneLeerstellen(
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
    public SemPraedikatWoertlicheRedeOhneLeerstellen mitModalpartikeln(
            final Collection<Modalpartikel> modalpartikeln) {
        return new SemPraedikatWoertlicheRedeOhneLeerstellen(
                getVerb(), woertlicheRede,
                Iterables.concat(getModalpartikeln(), modalpartikeln),
                getAdvAngabeSkopusSatz(),
                getNegationspartikel(), getAdvAngabeSkopusVerbAllg(),
                getAdvAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public SemPraedikatWoertlicheRedeOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        return new SemPraedikatWoertlicheRedeOhneLeerstellen(
                getVerb(),
                woertlicheRede,
                getModalpartikeln(),
                advAngabe, getNegationspartikel(), getAdvAngabeSkopusVerbAllg(),
                getAdvAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public SemPraedikatWoertlicheRedeOhneLeerstellen neg() {
        return (SemPraedikatWoertlicheRedeOhneLeerstellen) super.neg();
    }

    @Override
    public SemPraedikatWoertlicheRedeOhneLeerstellen neg(
            @Nullable final Negationspartikelphrase negationspartikelphrase) {
        if (negationspartikelphrase == null) {
            return this;
        }

        return new SemPraedikatWoertlicheRedeOhneLeerstellen(
                getVerb(),
                woertlicheRede,
                getModalpartikeln(),
                getAdvAngabeSkopusSatz(), negationspartikelphrase, getAdvAngabeSkopusVerbAllg(),
                getAdvAngabeSkopusVerbWohinWoher());
    }

    @Override
    public SemPraedikatWoertlicheRedeOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        return new SemPraedikatWoertlicheRedeOhneLeerstellen(
                getVerb(),
                woertlicheRede,
                getModalpartikeln(),
                getAdvAngabeSkopusSatz(), getNegationspartikel(), advAngabe,
                getAdvAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public SemPraedikatWoertlicheRedeOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativWohinWoher advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        return new SemPraedikatWoertlicheRedeOhneLeerstellen(
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
    public Vorfeld getSpeziellesVorfeldSehrErwuenscht(final PraedRegMerkmale praedRegMerkmale,
                                                      final boolean nachAnschlusswort,
                                                      final Konstituente syntWoertlicheRede) {
        @Nullable final Vorfeld speziellesVorfeldFromSuper =
                getVorfeldAdvAngabeSkopusSatz(praedRegMerkmale);
        if (speziellesVorfeldFromSuper != null) {
            return speziellesVorfeldFromSuper;
        }

        if (getNegationspartikel() != null) {
            return null;
        }

        if (!nachAnschlusswort
                && !woertlicheRede.isLangOderMehrteilig()) {
            return new Vorfeld(syntWoertlicheRede); // "„Kommt alle her[“, ]"
        }

        return null;
    }

    @Override
    public boolean hatAkkusativobjekt() {
        return false;
    }

    @Override
    TopolFelder getTopolFelder(final ITextContext textContext,
                               final PraedRegMerkmale praedRegMerkmale,
                               final boolean nachAnschlusswort) {
        final Konstituente advAngabeSkopusSatzSyntFuerMittelfeld =
                getAdvAngabeSkopusSatzDescriptionFuerMittelfeld(praedRegMerkmale);
        final Konstituente advAngabeSkopusVerbSyntFuerMittelfeld =
                getAdvAngabeSkopusVerbTextDescriptionFuerMittelfeld(
                        praedRegMerkmale);
        final Konstituente advAngabeSkopusVerbWohinWoherSynt =
                getAdvAngabeSkopusVerbWohinWoherDescription(praedRegMerkmale);

        final Konstituente syntWoertlicheRede = k(woertlicheRede.getDescription(),
                true,
                true);
        return new TopolFelder(
                new Mittelfeld(
                        Konstituentenfolge.joinToNullKonstituentenfolge(
                                advAngabeSkopusSatzSyntFuerMittelfeld,
                                // "aus einer Laune heraus"
                                kf(getModalpartikeln()),  // "mal eben"
                                advAngabeSkopusVerbSyntFuerMittelfeld,
                                // "erneut"
                                getNegationspartikel(), // "nicht"
                                advAngabeSkopusVerbWohinWoherSynt
                                // "in ein Kissen"
                        )),
                new Nachfeld(
                        Konstituentenfolge.joinToKonstituentenfolge(
                                getAdvAngabeSkopusVerbTextDescriptionFuerZwangsausklammerung(
                                        praedRegMerkmale),
                                getAdvAngabeSkopusSatzDescriptionFuerZwangsausklammerung(
                                        praedRegMerkmale),
                                syntWoertlicheRede.withVordoppelpunktNoetig())),
                getSpeziellesVorfeldSehrErwuenscht(praedRegMerkmale,
                        nachAnschlusswort,
                        syntWoertlicheRede),
                getGgfVorfeldAdvAngabeSkopusVerb(praedRegMerkmale), // "[: ]„Kommt alle her[.“]"
                null,
                firstInterrogativwort(advAngabeSkopusSatzSyntFuerMittelfeld,
                        advAngabeSkopusVerbSyntFuerMittelfeld,
                        advAngabeSkopusVerbWohinWoherSynt));

        //  Laut http://www.pro-publish.com/korrektor/zeichensetzung-interpunktion/den
        //  -doppelpunkt-richtig-setzen/
        //  ist es möglich, nach dem Doppelpunkt den SemSatz fortzuführen - dabei darf die
        //  wörtliche Rede allerdings nicht mit Punkt abgeschlossen worden sein:
        //  "Peter sagte: „Ich fahre jetzt nach Hause“, doch es wurde noch viel später an diesem
        //  Abend."
    }

    @Override
    public boolean umfasstSatzglieder() {
        return true;
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
        final SemPraedikatWoertlicheRedeOhneLeerstellen that =
                (SemPraedikatWoertlicheRedeOhneLeerstellen) o;
        return woertlicheRede.equals(that.woertlicheRede);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), woertlicheRede);
    }
}
