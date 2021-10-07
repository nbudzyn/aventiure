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
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativSkopusSatz;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativVerbAllg;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativWohinWoher;
import de.nb.aventiure2.german.base.Interrogativpronomen;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.Negationspartikelphrase;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.Praedikativum;
import de.nb.aventiure2.german.base.Relativpronomen;
import de.nb.aventiure2.german.base.SubstPhrOderReflexivpronomen;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.satz.EinzelnerSatz;

/**
 * Ein Prädikat, bestehend aus Dativobjekt, <i>sein</i> und einer prädikativen
 * Adjektivphrase (oder Interrogativpronomen), allerdings
 * ohne Subjekt (außer optionalem expletivem "es"): "Mir ist kalt", "es ist mir kalt",
 * "ihm wird warm", "Ihm wird was?"
 * Alle Leerstellen sind besetzt.
 */
public class DativPraedikativumPraedikatOhneSubjAusserOptionalemExpletivemEsOhneLeerstellen
        extends AbstractAngabenfaehigesPraedikatOhneLeerstellen {
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

    public static DativPraedikativumPraedikatOhneSubjAusserOptionalemExpletivemEsMitEinerPraedikativumLeerstelle
    dativPraedikativumMitDat(final SubstantivischePhrase dat) {
        return new
                DativPraedikativumPraedikatOhneSubjAusserOptionalemExpletivemEsMitEinerPraedikativumLeerstelle(
                SeinUtil.VERB, dat
        );
    }

    public static DativPraedikativumPraedikatOhneSubjAusserOptionalemExpletivemEsMitEinerDatLeerstelle
    dativPraedikativumMitPraedikativum(final Praedikativum praedikativum) {
        return new
                DativPraedikativumPraedikatOhneSubjAusserOptionalemExpletivemEsMitEinerDatLeerstelle(
                SeinUtil.VERB, praedikativum
        );
    }

    public static DativPraedikativumPraedikatOhneSubjAusserOptionalemExpletivemEsMitEinerPraedikativumLeerstelle
    dativPraedikativumWerdenMitDat(final SubstantivischePhrase dat) {
        return new
                DativPraedikativumPraedikatOhneSubjAusserOptionalemExpletivemEsMitEinerPraedikativumLeerstelle(
                WerdenUtil.VERB, dat
        );
    }

    public static DativPraedikativumPraedikatOhneSubjAusserOptionalemExpletivemEsMitEinerDatLeerstelle
    dativPraedikativumWerdenMitPraedikativum(final Praedikativum praedikativum) {
        return new
                DativPraedikativumPraedikatOhneSubjAusserOptionalemExpletivemEsMitEinerDatLeerstelle(
                WerdenUtil.VERB, praedikativum
        );
    }

    @Valenz
    DativPraedikativumPraedikatOhneSubjAusserOptionalemExpletivemEsOhneLeerstellen(
            final Verb verb,
            final SubstantivischePhrase dat,
            final Praedikativum praedikativum) {
        this(verb, dat, praedikativum,
                ImmutableList.of(),
                null, null, null,
                null);
    }

    private DativPraedikativumPraedikatOhneSubjAusserOptionalemExpletivemEsOhneLeerstellen(
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

    public EinzelnerSatz alsSatz() {
        return alsSatzMitSubjekt(null);
    }

    @Override
    public DativPraedikativumPraedikatOhneSubjAusserOptionalemExpletivemEsOhneLeerstellen mitModalpartikeln(
            final Collection<Modalpartikel> modalpartikeln) {
        return new DativPraedikativumPraedikatOhneSubjAusserOptionalemExpletivemEsOhneLeerstellen(
                getVerb(), dat, praedikativum,
                Iterables.concat(getModalpartikeln(), modalpartikeln),
                getAdvAngabeSkopusSatz(),
                getNegationspartikel(), getAdvAngabeSkopusVerbAllg(),
                getAdvAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public DativPraedikativumPraedikatOhneSubjAusserOptionalemExpletivemEsOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        return new DativPraedikativumPraedikatOhneSubjAusserOptionalemExpletivemEsOhneLeerstellen(
                getVerb(),
                dat, praedikativum,
                getModalpartikeln(),
                advAngabe, getNegationspartikel(), getAdvAngabeSkopusVerbAllg(),
                getAdvAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public DativPraedikativumPraedikatOhneSubjAusserOptionalemExpletivemEsOhneLeerstellen neg() {
        return (DativPraedikativumPraedikatOhneSubjAusserOptionalemExpletivemEsOhneLeerstellen) super
                .neg();
    }

    @Override
    public DativPraedikativumPraedikatOhneSubjAusserOptionalemExpletivemEsOhneLeerstellen neg(
            @Nullable final Negationspartikelphrase negationspartikelphrase) {
        if (negationspartikelphrase == null) {
            return this;
        }

        return new DativPraedikativumPraedikatOhneSubjAusserOptionalemExpletivemEsOhneLeerstellen(
                getVerb(),
                dat, praedikativum,
                getModalpartikeln(),
                getAdvAngabeSkopusSatz(), negationspartikelphrase, getAdvAngabeSkopusVerbAllg(),
                getAdvAngabeSkopusVerbWohinWoher());
    }

    @Override
    public DativPraedikativumPraedikatOhneSubjAusserOptionalemExpletivemEsOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        return new DativPraedikativumPraedikatOhneSubjAusserOptionalemExpletivemEsOhneLeerstellen(
                getVerb(),
                dat, praedikativum,
                getModalpartikeln(),
                getAdvAngabeSkopusSatz(), getNegationspartikel(), advAngabe,
                getAdvAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public DativPraedikativumPraedikatOhneSubjAusserOptionalemExpletivemEsOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativWohinWoher advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        // Diese Position kann wohl nicht besetzt sein.
        return new DativPraedikativumPraedikatOhneSubjAusserOptionalemExpletivemEsOhneLeerstellen(
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
    public Konstituente getSpeziellesVorfeldSehrErwuenscht(final Person personSubjekt,
                                                           final Numerus numerusSubjekt,
                                                           final boolean nachAnschlusswort) {
        @Nullable final Konstituente speziellesVorfeldFromSuper =
                super.getSpeziellesVorfeldSehrErwuenscht(
                        personSubjekt, numerusSubjekt, nachAnschlusswort);
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
            final Person personSubjekt, final Numerus numerusSubjekt) {
        return Konstituentenfolge.joinToNullKonstituentenfolge(
                dat.datK(), // Peter
                kf(getModalpartikeln()), // "halt"
                getAdvAngabeSkopusSatzDescriptionFuerMittelfeld(personSubjekt,
                        numerusSubjekt),
                // "plötzlich"
                getAdvAngabeSkopusVerbTextDescriptionFuerMittelfeld(personSubjekt,
                        numerusSubjekt), // "erneut"
                getAdvAngabeSkopusVerbWohinWoherDescription(personSubjekt,
                        numerusSubjekt),
                // (kann wohl nicht besetzt sein?)
                praedikativum.getPraedikativOhneAnteilKandidatFuerNachfeld(
                        personSubjekt, numerusSubjekt, getNegationspartikel())
                // "kalt", "nicht kalt"
        );
    }

    @Nullable
    @Override
    SubstPhrOderReflexivpronomen getDat(
            final Person personSubjekt, final Numerus numerusSubjekt) {
        return dat;
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
    @Nullable
    public Konstituentenfolge getNachfeld(final Person personSubjekt,
                                          final Numerus numerusSubjekt) {
        return Konstituentenfolge.joinToNullKonstituentenfolge(
                praedikativum.getPraedikativAnteilKandidatFuerNachfeld(
                        personSubjekt, numerusSubjekt), // "dich zu sehen"
                getAdvAngabeSkopusVerbTextDescriptionFuerZwangsausklammerung(
                        personSubjekt,
                        numerusSubjekt),
                getAdvAngabeSkopusSatzDescriptionFuerZwangsausklammerung(personSubjekt,
                        numerusSubjekt)
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
                    P3, SG);
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
                    P3, SG);
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
        final DativPraedikativumPraedikatOhneSubjAusserOptionalemExpletivemEsOhneLeerstellen that =
                (DativPraedikativumPraedikatOhneSubjAusserOptionalemExpletivemEsOhneLeerstellen) o;
        return dat.equals(that.dat) &&
                praedikativum.equals(that.praedikativum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), dat, praedikativum);
    }
}
