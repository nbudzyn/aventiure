package de.nb.aventiure2.german.praedikat;

import static de.nb.aventiure2.german.base.Konstituentenfolge.kf;
import static de.nb.aventiure2.german.praedikat.Praedikatseinbindung.firstInterrogativwort;
import static de.nb.aventiure2.german.praedikat.Praedikatseinbindung.firstRelativpronomen;

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
import de.nb.aventiure2.german.base.Praedikativum;
import de.nb.aventiure2.german.description.ITextContext;

/**
 * Ein "semantisches Prädikat", bestehend aus <i>sein</i> und einem Prädikativum ("müde sein",
 * "glücklich sein, dich zu sehen") - alle Leerstellen sind besetzt.
 */
public class PraedikativumSemPraedikatOhneLeerstellen
        extends AbstractAngabenfaehigesSemPraedikatOhneLeerstellen {
    /**
     * Das Prädikativum
     */
    @Komplement
    private final Praedikativum praedikativum;

    @Valenz
    public PraedikativumSemPraedikatOhneLeerstellen(final Verb verb,
                                                    final Praedikativum praedikativum) {
        this(verb, praedikativum,
                ImmutableList.of(),
                null, null, null,
                null);
    }

    private PraedikativumSemPraedikatOhneLeerstellen(
            final Verb verb,
            final Praedikativum praedikativum,
            final Iterable<Modalpartikel> modalpartikeln,
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabeSkopusSatz,
            @Nullable final Negationspartikelphrase negationspartikelphrase,
            @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabeSkopusVerbAllg,
            @Nullable final IAdvAngabeOderInterrogativWohinWoher advAngabeSkopusVerbWohinWoher) {
        super(verb, modalpartikeln,
                advAngabeSkopusSatz,
                negationspartikelphrase, advAngabeSkopusVerbAllg, advAngabeSkopusVerbWohinWoher);
        this.praedikativum = praedikativum;
    }

    @Override
    public PraedikativumSemPraedikatOhneLeerstellen mitModalpartikeln(
            final Collection<Modalpartikel> modalpartikeln) {
        return new PraedikativumSemPraedikatOhneLeerstellen(
                getVerb(), praedikativum,
                Iterables.concat(getModalpartikeln(), modalpartikeln),
                getAdvAngabeSkopusSatz(),
                getNegationspartikel(), getAdvAngabeSkopusVerbAllg(),
                getAdvAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public PraedikativumSemPraedikatOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        return new PraedikativumSemPraedikatOhneLeerstellen(
                getVerb(),
                praedikativum,
                getModalpartikeln(),
                advAngabe, getNegationspartikel(), getAdvAngabeSkopusVerbAllg(),
                getAdvAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public PraedikativumSemPraedikatOhneLeerstellen neg() {
        return (PraedikativumSemPraedikatOhneLeerstellen) super.neg();
    }


    @Override
    public PraedikativumSemPraedikatOhneLeerstellen neg(
            @Nullable final Negationspartikelphrase negationspartikelphrase) {
        if (negationspartikelphrase == null) {
            return this;
        }

        return new PraedikativumSemPraedikatOhneLeerstellen(
                getVerb(),
                praedikativum,
                getModalpartikeln(),
                getAdvAngabeSkopusSatz(), negationspartikelphrase, getAdvAngabeSkopusVerbAllg(),
                getAdvAngabeSkopusVerbWohinWoher());
    }

    @Override
    public PraedikativumSemPraedikatOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        return new PraedikativumSemPraedikatOhneLeerstellen(
                getVerb(),
                praedikativum,
                getModalpartikeln(),
                getAdvAngabeSkopusSatz(), getNegationspartikel(), advAngabe,
                getAdvAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public PraedikativumSemPraedikatOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativWohinWoher advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        // Diese Position kann wohl nicht besetzt sein.
        return new PraedikativumSemPraedikatOhneLeerstellen(
                getVerb(),
                praedikativum,
                getModalpartikeln(),
                getAdvAngabeSkopusSatz(),
                getNegationspartikel(), getAdvAngabeSkopusVerbAllg(),
                advAngabe
        );
    }

    @Override
    public boolean kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden() {
        // ?"Endlich wieder einmal glücklich gewesen verlässt du das Theater."
        return false;

        // (Möglich wäre auf jeden Fall die Negation:
        //  "Lange nicht mehr so glücklich gewesen verlässt du das Theater.")
    }

    // Spezielles Vorfeld:
    // "Ein Esel ist Peter", "Glücklich ist Peter" - das wäre stark markiert, wir lassen
    // es hier aus.

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

        final Praedikatseinbindung<Praedikativum> praedikativumEinbindung =
                new Praedikatseinbindung<>(praedikativum,
                        p -> p.getPraedikativOhneAnteilKandidatFuerNachfeld(
                                praedRegMerkmale,
                                getNegationspartikel()));

        final Konstituente advAngabeSkopusVerbSyntFuerMittelfeld =
                getAdvAngabeSkopusVerbTextDescriptionFuerMittelfeld(
                        praedRegMerkmale);
        final Konstituente advAngabeSkopusVerbWohinWoherSynt =
                getAdvAngabeSkopusVerbWohinWoherDescription(praedRegMerkmale);

        return new TopolFelder(
                new Mittelfeld(
                        Konstituentenfolge.joinToNullKonstituentenfolge(
                                advAngabeSkopusSatzSyntFuerMittelfeld,
                                // "plötzlich"
                                kf(getModalpartikeln()), // "halt"
                                advAngabeSkopusVerbSyntFuerMittelfeld,
                                // "erneut"
                                advAngabeSkopusVerbWohinWoherSynt,
                                // (kann wohl nicht besetzt sein?)
                                praedikativumEinbindung
                                // "glücklich", "ein Esel", "sich ihrer selbst gewiss",
                                // "sehr
                                // glücklich
                                // [, dich zu sehen]", "kein Esel",  "schon lange kein
                                // Verdächtiger mehr"
                        )
                ),
                new Nachfeld(
                        Konstituentenfolge.joinToNullKonstituentenfolge(
                                praedikativum
                                        .getPraedikativAnteilKandidatFuerNachfeld(
                                                praedRegMerkmale),
                                // "dich zu sehen"
                                getAdvAngabeSkopusVerbTextDescriptionFuerZwangsausklammerung(
                                        praedRegMerkmale),
                                getAdvAngabeSkopusSatzDescriptionFuerZwangsausklammerung(
                                        praedRegMerkmale)
                        )),
                getVorfeldAdvAngabeSkopusSatz(praedRegMerkmale),
                getGgfVorfeldAdvAngabeSkopusVerb(praedRegMerkmale),
                firstRelativpronomen(praedikativumEinbindung),
                firstInterrogativwort(advAngabeSkopusSatzSyntFuerMittelfeld,
                        advAngabeSkopusVerbSyntFuerMittelfeld,
                        advAngabeSkopusVerbWohinWoherSynt,
                        praedikativumEinbindung));
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
        final PraedikativumSemPraedikatOhneLeerstellen that =
                (PraedikativumSemPraedikatOhneLeerstellen) o;
        return Objects.equals(praedikativum, that.praedikativum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), praedikativum);
    }
}
