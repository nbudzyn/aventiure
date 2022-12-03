package de.nb.aventiure2.german.praedikat;

import static de.nb.aventiure2.german.base.Konstituentenfolge.kf;
import static de.nb.aventiure2.german.praedikat.Praedikatseinbindung.firstInterrogativwort;
import static de.nb.aventiure2.german.praedikat.Praedikatseinbindung.firstRelativpronomen;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.Collection;
import java.util.Objects;

import javax.annotation.Nonnull;
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
import de.nb.aventiure2.german.base.Praedikativum;
import de.nb.aventiure2.german.base.SubstantivischPhrasierbar;
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
    private final SubstantivischPhrasierbar dat;

    /**
     * Die prädikative Adjektivphrase oder das Interrrogativpronomen
     */
    @Komplement
    @Nonnull
    private final Praedikativum praedikativum;

    public static DativPraedikativumSemPraedikatOhneSubjAusserOptionalemExpletivemEsMitEinerPraedikativumLeerstelle
    dativPraedikativumMitDat(final SubstantivischPhrasierbar dat) {
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
    dativPraedikativumWerdenMitDat(final SubstantivischPhrasierbar dat) {
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
            final SubstantivischPhrasierbar dat,
            final Praedikativum praedikativum) {
        this(verb, dat, praedikativum,
                ImmutableList.of(),
                null, null, null,
                null);
    }

    private DativPraedikativumSemPraedikatOhneSubjAusserOptionalemExpletivemEsOhneLeerstellen(
            final Verb verb,
            final SubstantivischPhrasierbar dat,
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
    private Vorfeld getSpeziellesVorfeldSehrErwuenscht(final PraedRegMerkmale praedRegMerkmale,
                                                       final SubstantivischePhrase datPhrase) {
        @Nullable final Vorfeld speziellesVorfeldFromSuper =
                getVorfeldAdvAngabeSkopusSatz(praedRegMerkmale);
        if (speziellesVorfeldFromSuper != null) {
            return speziellesVorfeldFromSuper;
        }

        if (getNegationspartikel() != null) {
            return null;
        }

        if (inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich()) { // gilt immer
            final Konstituentenfolge vorfeldCandidate = datPhrase.datK();
            if (vorfeldCandidate.size() == 1 && vorfeldCandidate.get(0) instanceof Konstituente) {
                // "mir (wird warm)"
                return new Vorfeld((Konstituente) vorfeldCandidate.get(0));
            }
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

        final SubstantivischePhrase datPhrase = dat.alsSubstPhrase(textContext);
        final Praedikatseinbindung<SubstantivischePhrase> datEinbindung =
                new Praedikatseinbindung<>(datPhrase, SubstantivischePhrase::datK);

        final Praedikatseinbindung<Praedikativum> praedikativumEinbindung =
                new Praedikatseinbindung<>(praedikativum,
                        p -> p.getPraedikativOhneAnteilKandidatFuerNachfeld(
                                praedRegMerkmale, getNegationspartikel()));

        final Konstituente advAngabeSkopusVerbSyntFuerMittelfeld =
                getAdvAngabeSkopusVerbTextDescriptionFuerMittelfeld(
                        praedRegMerkmale);
        final Konstituente advAngabeSkopusVerbWohinWoherSynt =
                getAdvAngabeSkopusVerbWohinWoherDescription(praedRegMerkmale);

        return new TopolFelder(
                new Mittelfeld(
                        Konstituentenfolge.joinToNullKonstituentenfolge(
                                datEinbindung, // Peter
                                kf(getModalpartikeln()), // "halt"
                                advAngabeSkopusSatzSyntFuerMittelfeld,
                                // "plötzlich"
                                advAngabeSkopusVerbSyntFuerMittelfeld, // "erneut"
                                advAngabeSkopusVerbWohinWoherSynt,
                                // (kann wohl nicht besetzt sein?)
                                praedikativumEinbindung
                                // "kalt", "nicht kalt"
                        ),
                        datEinbindung
                ),
                new Nachfeld(
                        Konstituentenfolge.joinToNullKonstituentenfolge(
                                praedikativum.getPraedikativAnteilKandidatFuerNachfeld(
                                        praedRegMerkmale),
                                // "dich zu sehen"
                                getAdvAngabeSkopusVerbTextDescriptionFuerZwangsausklammerung(
                                        praedRegMerkmale),
                                getAdvAngabeSkopusSatzDescriptionFuerZwangsausklammerung(
                                        praedRegMerkmale)
                        )),
                getSpeziellesVorfeldSehrErwuenscht(praedRegMerkmale, datPhrase),
                getGgfVorfeldAdvAngabeSkopusVerb(praedRegMerkmale),
                // Es gibt noch Fälle (hier und in praktisch allen anderen Verwendungen von
                // firstRelativpronomen(), in denen das Relativpronomen im Relativsatz
                // weder Subjekt noch Objekt (oder Präpositionalobjekt) ist, sondern
                // adverbiale Angabe ("Letzten Freitag, DEN ich lange gearbeitet habe, hatte
                // ich einen Gedanken") oder adverbiale Angabe, gemeinsam mit einer
                // Präposition
                // ("Letzten Freitag, (an DEM) ich lange gearbeitet habe, hatte
                // ich einen Gedanken").
                // Um das abzudecken müsste man diese Kandidaten auch an
                // firstRelativpronomen übergeben.
                firstRelativpronomen(datEinbindung, praedikativumEinbindung),
                firstInterrogativwort(datEinbindung, advAngabeSkopusSatzSyntFuerMittelfeld,
                        advAngabeSkopusVerbSyntFuerMittelfeld,
                        advAngabeSkopusVerbWohinWoherSynt, praedikativumEinbindung));
    }

    @Override
    public boolean umfasstSatzglieder() {
        return true;
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
