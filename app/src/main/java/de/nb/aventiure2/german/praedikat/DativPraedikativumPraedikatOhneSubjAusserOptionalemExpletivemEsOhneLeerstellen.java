package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

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
import de.nb.aventiure2.german.base.Interrogativpronomen;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.Praedikativum;
import de.nb.aventiure2.german.base.SubstPhrOderReflexivpronomen;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.satz.Satz;

import static de.nb.aventiure2.german.base.Konstituentenfolge.kf;
import static de.nb.aventiure2.german.base.Numerus.SG;
import static de.nb.aventiure2.german.base.Person.P3;

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
                null, null,
                null);
    }

    private DativPraedikativumPraedikatOhneSubjAusserOptionalemExpletivemEsOhneLeerstellen(
            final Verb verb,
            final SubstantivischePhrase dat,
            final Praedikativum praedikativum,
            final Iterable<Modalpartikel> modalpartikeln,
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz adverbialeAngabeSkopusSatz,
            @Nullable final IAdvAngabeOderInterrogativVerbAllg adverbialeAngabeSkopusVerbAllg,
            @Nullable
            final IAdvAngabeOderInterrogativWohinWoher adverbialeAngabeSkopusVerbWohinWoher) {
        super(verb, true, modalpartikeln,
                adverbialeAngabeSkopusSatz,
                adverbialeAngabeSkopusVerbAllg, adverbialeAngabeSkopusVerbWohinWoher);
        this.dat = dat;
        this.praedikativum = praedikativum;
    }

    public Satz alsSatz() {
        return alsSatzMitSubjekt(null);
    }

    @Override
    public DativPraedikativumPraedikatOhneSubjAusserOptionalemExpletivemEsOhneLeerstellen mitModalpartikeln(
            final Collection<Modalpartikel> modalpartikeln) {
        return new DativPraedikativumPraedikatOhneSubjAusserOptionalemExpletivemEsOhneLeerstellen(
                getVerb(), dat, praedikativum,
                Iterables.concat(getModalpartikeln(), modalpartikeln),
                getAdverbialeAngabeSkopusSatz(),
                getAdverbialeAngabeSkopusVerbAllg(),
                getAdverbialeAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public DativPraedikativumPraedikatOhneSubjAusserOptionalemExpletivemEsOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz adverbialeAngabe) {
        if (adverbialeAngabe == null) {
            return this;
        }

        return new DativPraedikativumPraedikatOhneSubjAusserOptionalemExpletivemEsOhneLeerstellen(
                getVerb(),
                dat, praedikativum,
                getModalpartikeln(),
                adverbialeAngabe, getAdverbialeAngabeSkopusVerbAllg(),
                getAdverbialeAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public DativPraedikativumPraedikatOhneSubjAusserOptionalemExpletivemEsOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final IAdvAngabeOderInterrogativVerbAllg adverbialeAngabe) {
        if (adverbialeAngabe == null) {
            return this;
        }

        return new DativPraedikativumPraedikatOhneSubjAusserOptionalemExpletivemEsOhneLeerstellen(
                getVerb(),
                dat, praedikativum,
                getModalpartikeln(),
                getAdverbialeAngabeSkopusSatz(), adverbialeAngabe,
                getAdverbialeAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public DativPraedikativumPraedikatOhneSubjAusserOptionalemExpletivemEsOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final IAdvAngabeOderInterrogativWohinWoher adverbialeAngabe) {
        if (adverbialeAngabe == null) {
            return this;
        }

        // Diese Position kann wohl nicht besetzt sein.
        return new DativPraedikativumPraedikatOhneSubjAusserOptionalemExpletivemEsOhneLeerstellen(
                getVerb(),
                dat, praedikativum,
                getModalpartikeln(),
                getAdverbialeAngabeSkopusSatz(),
                getAdverbialeAngabeSkopusVerbAllg(),
                adverbialeAngabe
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

        if (inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich()) { // gilt immer
            final Konstituentenfolge vorfeldCandidate = dat.datK();
            if (vorfeldCandidate.size() == 1 && vorfeldCandidate.get(0) instanceof Konstituente) {
                // "mir (wird warm)"
                return (Konstituente) vorfeldCandidate.get(0);
            }
        }

        return null;
    }

    @SuppressWarnings("RedundantIfStatement")
    @Override
    public @Nullable
    Konstituentenfolge getSpeziellesVorfeldAlsWeitereOption(final Person person,
                                                            final Numerus numerus) {
        @Nullable final Konstituentenfolge speziellesVorfeldFromSuper =
                super.getSpeziellesVorfeldAlsWeitereOption(person, numerus);
        if (speziellesVorfeldFromSuper != null) {
            return speziellesVorfeldFromSuper;
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
                getAdverbialeAngabeSkopusSatzDescriptionFuerMittelfeld(personSubjekt,
                        numerusSubjekt),
                // "plötzlich"
                getAdverbialeAngabeSkopusVerbTextDescriptionFuerMittelfeld(personSubjekt,
                        numerusSubjekt), // "erneut"
                getAdverbialeAngabeSkopusVerbWohinWoherDescription(personSubjekt,
                        numerusSubjekt),
                // (kann wohl nicht besetzt sein?)
                praedikativum.getPraedikativOhneAnteilKandidatFuerNachfeld(
                        personSubjekt, numerusSubjekt)
                // "kalt"
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
                getAdverbialeAngabeSkopusVerbTextDescriptionFuerZwangsausklammerung(
                        personSubjekt,
                        numerusSubjekt),
                getAdverbialeAngabeSkopusSatzDescriptionFuerZwangsausklammerung(personSubjekt,
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
        Konstituentenfolge res = interroAdverbToKF(getAdverbialeAngabeSkopusSatz());
        if (res != null) {
            return res;
        }

        res = interroAdverbToKF(getAdverbialeAngabeSkopusVerbAllg());
        if (res != null) {
            return res;
        }

        res = interroAdverbToKF(getAdverbialeAngabeSkopusVerbWohinWoher());
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
}
