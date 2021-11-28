package de.nb.aventiure2.german.praedikat;

import static de.nb.aventiure2.german.base.Konstituentenfolge.kf;
import static de.nb.aventiure2.german.base.Numerus.SG;
import static de.nb.aventiure2.german.base.Person.P3;

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
import de.nb.aventiure2.german.base.Belebtheit;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativSkopusSatz;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativVerbAllg;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativWohinWoher;
import de.nb.aventiure2.german.base.Interrogativpronomen;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.Negationspartikelphrase;
import de.nb.aventiure2.german.base.PraedRegMerkmale;
import de.nb.aventiure2.german.base.Praedikativum;
import de.nb.aventiure2.german.base.Relativpronomen;
import de.nb.aventiure2.german.base.SubstPhrOderReflexivpronomen;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.ITextContext;
import de.nb.aventiure2.german.satz.EinzelnerSemSatz;

/**
 * Ein "semantisches Prädikat", bestehend aus Dativobjekt, <i>sein</i> und einer prädikativen
 * Adjektivphrase (oder Interrogativpronomen), allerdings
 * ohne Subjekt (außer optionalem expletivem "es"): "Mir ist kalt", "es ist mir kalt",
 * "ihm wird warm", "Ihm wird was?"
 * Alle Leerstellen sind besetzt.
 */
public class DativPraedikativumSemPraedikatOhneSubjAusserOptionalemExpletivemEsOhneLeerstellen
        extends AbstractAngabenfaehigesSemPraedikatOhneLeerstellen {
    /**
     * Das Dativobjekt (z.B. "Angebote")
     */
    @NonNull
    @Komplement
    private final SubstantivischePhrase dat;

    /**
     * Die prädikative Adjektivphrase oder das Interrrogativpronomen
     */
    @Komplement
    @Nonnull
    private final Praedikativum praedikativum;

    public static DativPraedikativumSemPraedikatOhneSubjAusserOptionalemExpletivemEsMitEinerPraedikativumLeerstelle
    dativPraedikativumMitDat(final SubstantivischePhrase dat) {
        return new
                DativPraedikativumSemPraedikatOhneSubjAusserOptionalemExpletivemEsMitEinerPraedikativumLeerstelle(
                SeinUtil.VERB, dat
        );
    }

    public static DativPraedikativumSemPraedikatOhneSubjAusserOptionalemExpletivemEsMitEinerDatLeerstelle
    dativPraedikativumMitPraedikativum(final Praedikativum praedikativum) {
        return new
                DativPraedikativumSemPraedikatOhneSubjAusserOptionalemExpletivemEsMitEinerDatLeerstelle(
                SeinUtil.VERB, praedikativum
        );
    }

    public static DativPraedikativumSemPraedikatOhneSubjAusserOptionalemExpletivemEsMitEinerPraedikativumLeerstelle
    dativPraedikativumWerdenMitDat(final SubstantivischePhrase dat) {
        return new
                DativPraedikativumSemPraedikatOhneSubjAusserOptionalemExpletivemEsMitEinerPraedikativumLeerstelle(
                WerdenUtil.VERB, dat
        );
    }

    public static DativPraedikativumSemPraedikatOhneSubjAusserOptionalemExpletivemEsMitEinerDatLeerstelle
    dativPraedikativumWerdenMitPraedikativum(final Praedikativum praedikativum) {
        return new
                DativPraedikativumSemPraedikatOhneSubjAusserOptionalemExpletivemEsMitEinerDatLeerstelle(
                WerdenUtil.VERB, praedikativum
        );
    }

    @Valenz
    DativPraedikativumSemPraedikatOhneSubjAusserOptionalemExpletivemEsOhneLeerstellen(
            final Verb verb,
            final SubstantivischePhrase dat,
            final Praedikativum praedikativum) {
        this(verb, dat, praedikativum,
                ImmutableList.of(),
                null, null, null,
                null);
    }

    private DativPraedikativumSemPraedikatOhneSubjAusserOptionalemExpletivemEsOhneLeerstellen(
            final Verb verb,
            final SubstantivischePhrase dat,
            final Praedikativum praedikativum,
            final Iterable<Modalpartikel> modalpartikeln,
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabeSkopusSatz,
            @Nullable final Negationspartikelphrase negationspartikelphrase,
            @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabeSkopusVerbAllg,
            @Nullable final IAdvAngabeOderInterrogativWohinWoher advAngabeSkopusVerbWohinWoher) {
        super(verb, true, modalpartikeln,
                advAngabeSkopusSatz,
                negationspartikelphrase, advAngabeSkopusVerbAllg, advAngabeSkopusVerbWohinWoher);
        this.dat = dat;
        this.praedikativum = praedikativum;
    }

    public EinzelnerSemSatz alsSatz() {
        return alsSatzMitSubjekt(null);
    }

    @Override
    public DativPraedikativumSemPraedikatOhneSubjAusserOptionalemExpletivemEsOhneLeerstellen mitModalpartikeln(
            final Collection<Modalpartikel> modalpartikeln) {
        return new DativPraedikativumSemPraedikatOhneSubjAusserOptionalemExpletivemEsOhneLeerstellen(
                getVerb(), dat, praedikativum,
                Iterables.concat(getModalpartikeln(), modalpartikeln),
                getAdvAngabeSkopusSatz(),
                getNegationspartikel(), getAdvAngabeSkopusVerbAllg(),
                getAdvAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public DativPraedikativumSemPraedikatOhneSubjAusserOptionalemExpletivemEsOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        return new DativPraedikativumSemPraedikatOhneSubjAusserOptionalemExpletivemEsOhneLeerstellen(
                getVerb(),
                dat, praedikativum,
                getModalpartikeln(),
                advAngabe, getNegationspartikel(), getAdvAngabeSkopusVerbAllg(),
                getAdvAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public DativPraedikativumSemPraedikatOhneSubjAusserOptionalemExpletivemEsOhneLeerstellen neg() {
        return (DativPraedikativumSemPraedikatOhneSubjAusserOptionalemExpletivemEsOhneLeerstellen) super
                .neg();
    }

    @Override
    public DativPraedikativumSemPraedikatOhneSubjAusserOptionalemExpletivemEsOhneLeerstellen neg(
            @Nullable final Negationspartikelphrase negationspartikelphrase) {
        if (negationspartikelphrase == null) {
            return this;
        }

        return new DativPraedikativumSemPraedikatOhneSubjAusserOptionalemExpletivemEsOhneLeerstellen(
                getVerb(),
                dat, praedikativum,
                getModalpartikeln(),
                getAdvAngabeSkopusSatz(), negationspartikelphrase, getAdvAngabeSkopusVerbAllg(),
                getAdvAngabeSkopusVerbWohinWoher());
    }

    @Override
    public DativPraedikativumSemPraedikatOhneSubjAusserOptionalemExpletivemEsOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        return new DativPraedikativumSemPraedikatOhneSubjAusserOptionalemExpletivemEsOhneLeerstellen(
                getVerb(),
                dat, praedikativum,
                getModalpartikeln(),
                getAdvAngabeSkopusSatz(), getNegationspartikel(), advAngabe,
                getAdvAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public DativPraedikativumSemPraedikatOhneSubjAusserOptionalemExpletivemEsOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativWohinWoher advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        // Diese Position kann wohl nicht besetzt sein.
        return new DativPraedikativumSemPraedikatOhneSubjAusserOptionalemExpletivemEsOhneLeerstellen(
                getVerb(),
                dat, praedikativum,
                getModalpartikeln(),
                getAdvAngabeSkopusSatz(),
                getNegationspartikel(), getAdvAngabeSkopusVerbAllg(),
                advAngabe
        );
    }

    @Override
    public boolean kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden() {
        // *"Dir warm geworden verlässt du das Theater."
        return false;
    }

    @Nullable
    @Override
    public Konstituente getSpeziellesVorfeldSehrErwuenscht(final PraedRegMerkmale praedRegMerkmale,
                                                           final boolean nachAnschlusswort) {
        @Nullable final Konstituente speziellesVorfeldFromSuper =
                super.getSpeziellesVorfeldSehrErwuenscht(praedRegMerkmale, nachAnschlusswort);
        if (speziellesVorfeldFromSuper != null) {
            return speziellesVorfeldFromSuper;
        }

        if (getNegationspartikel() != null) {
            return null;
        }

        if (inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich()) { // gilt immer
            final Konstituentenfolge vorfeldCandidate = dat.datK();
            if (vorfeldCandidate.size() == 1 && vorfeldCandidate.get(0) instanceof Konstituente) {
                // "mir (wird warm)"
                return (Konstituente) vorfeldCandidate.get(0);
            }
        }

        return null;
    }

    @Override
    @Nullable
    @CheckReturnValue
    Konstituentenfolge getMittelfeldOhneLinksversetzungUnbetonterPronomen(
            final ITextContext textContext,
            final PraedRegMerkmale praedRegMerkmale) {
        return Konstituentenfolge.joinToNullKonstituentenfolge(
                dat.datK(), // Peter
                kf(getModalpartikeln()), // "halt"
                getAdvAngabeSkopusSatzDescriptionFuerMittelfeld(praedRegMerkmale),
                // "plötzlich"
                getAdvAngabeSkopusVerbTextDescriptionFuerMittelfeld(
                        praedRegMerkmale), // "erneut"
                getAdvAngabeSkopusVerbWohinWoherDescription(praedRegMerkmale),
                // (kann wohl nicht besetzt sein?)
                praedikativum.getPraedikativOhneAnteilKandidatFuerNachfeld(
                        praedRegMerkmale, getNegationspartikel())
                // "kalt", "nicht kalt"
        );
    }

    @Nullable
    @Override
    SubstPhrOderReflexivpronomen getDat(final PraedRegMerkmale praedRegMerkmale) {
        return dat;
    }

    @Override
    @Nullable
    SubstPhrOderReflexivpronomen getAkk(final PraedRegMerkmale praedRegMerkmale) {
        return null;
    }

    @Nullable
    @Override
    SubstPhrOderReflexivpronomen getZweitesAkk() {
        return null;
    }

    @Override
    @Nullable
    public Konstituentenfolge getNachfeld(final PraedRegMerkmale praedRegMerkmale) {
        return Konstituentenfolge.joinToNullKonstituentenfolge(
                praedikativum.getPraedikativAnteilKandidatFuerNachfeld(
                        praedRegMerkmale),
                // "dich zu sehen"
                getAdvAngabeSkopusVerbTextDescriptionFuerZwangsausklammerung(
                        praedRegMerkmale),
                getAdvAngabeSkopusSatzDescriptionFuerZwangsausklammerung(
                        praedRegMerkmale)
        );
    }

    @Override
    public boolean umfasstSatzglieder() {
        return true;
    }

    @Nullable
    @Override
    @CheckReturnValue
    public Konstituentenfolge getErstesInterrogativwort() {
        if (dat instanceof Interrogativpronomen) {
            return dat.datK();
        }

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

        if (praedikativum instanceof Interrogativpronomen) {
            return praedikativum.getPraedikativ(
                    // "Sie ist interessiert, wie euch ist",
                    P3, SG, Belebtheit.UNBELEBT);
        }

        return null;
    }

    @Nullable
    @Override
    @CheckReturnValue
    public Konstituentenfolge getRelativpronomen() {
        if (dat instanceof Relativpronomen) {
            return dat.datK();
        }

        // Es gibt noch Fälle (hier und in praktisch allen anderen Implementierung von
        // getRelativpronomen(), in denen das Relativpronomen im Relativsatz
        // weder Subjekt noch Objekt (oder Präpositionalobjekt) ist, sondern
        // adverbiale Angabe ("Letzten Freitag, DEN ich lange gearbeitet habe, hatte
        // ich einen Gedanken") oder adverbiale Angabe, gemeinsam mit einer Präposition
        // ("Letzten Freitag, (an DEM) ich lange gearbeitet habe, hatte
        // ich einen Gedanken").
        // Das könnte man ähnlich lösen wie bei getErstesInterrogativwort(), also
        // über etwas wie interroAdverbToKF().

        if (praedikativum instanceof Relativpronomen) {
            return praedikativum.getPraedikativ(
                    // "Sie ist interessiert, wie euch ist",
                    P3, SG, Belebtheit.UNBELEBT);
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
        final DativPraedikativumSemPraedikatOhneSubjAusserOptionalemExpletivemEsOhneLeerstellen
                that =
                (DativPraedikativumSemPraedikatOhneSubjAusserOptionalemExpletivemEsOhneLeerstellen) o;
        return dat.equals(that.dat) &&
                praedikativum.equals(that.praedikativum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), dat, praedikativum);
    }
}
