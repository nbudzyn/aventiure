package de.nb.aventiure2.german.praedikat;

import static de.nb.aventiure2.german.base.Kasus.AKK;
import static de.nb.aventiure2.german.base.Konstituentenfolge.kf;
import static de.nb.aventiure2.german.praedikat.Praedikatseinbindung.firstInterrogativwort;
import static de.nb.aventiure2.german.praedikat.Praedikatseinbindung.firstRelativpronomen;

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
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.base.PraedRegMerkmale;
import de.nb.aventiure2.german.base.SubstantivischPhrasierbar;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.ITextContext;

/**
 * Ein "semantisches Prädikat", in dem ein Dativobjekt und Akkusativobjekt gesetzt sind
 * und es auch sonst keine Leerstellen gibt. Beispiel: "dem Frosch Angebote machen"
 */
public class SemPraedikatDatAkkOhneLeerstellen
        extends AbstractAngabenfaehigesSemPraedikatOhneLeerstellen {
    /**
     * Das Dativobjekt (z.B. "Angebote")
     */
    @NonNull
    @Komplement
    private final SubstantivischPhrasierbar dat;

    /**
     * Das Akkusativobjekte (z.B. der Frosch)
     */
    @NonNull
    @Komplement
    private final SubstantivischPhrasierbar akk;

    @Valenz
    SemPraedikatDatAkkOhneLeerstellen(
            final Verb verb,
            final SubstantivischPhrasierbar dat,
            final SubstantivischPhrasierbar akk) {
        this(verb, dat, akk,
                ImmutableList.of(), null, null, null,
                null);
    }

    private SemPraedikatDatAkkOhneLeerstellen(
            final Verb verb,
            final SubstantivischPhrasierbar dat,
            final SubstantivischPhrasierbar akk,
            final Iterable<Modalpartikel> modalpartikeln,
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabeSkopusSatz,
            @Nullable final Negationspartikelphrase negationspartikelphrase,
            @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabeSkopusVerbAllg,
            @Nullable final IAdvAngabeOderInterrogativWohinWoher advAngabeSkopusVerbWohinWoher) {
        super(verb, modalpartikeln,
                advAngabeSkopusSatz,
                negationspartikelphrase, advAngabeSkopusVerbAllg, advAngabeSkopusVerbWohinWoher);
        this.dat = dat;
        this.akk = akk;
    }

    @Override
    public SemPraedikatDatAkkOhneLeerstellen mitModalpartikeln(
            final Collection<Modalpartikel> modalpartikeln) {
        return new SemPraedikatDatAkkOhneLeerstellen(
                getVerb(), dat, akk,
                Iterables.concat(getModalpartikeln(), modalpartikeln),
                getAdvAngabeSkopusSatz(),
                getNegationspartikel(), getAdvAngabeSkopusVerbAllg(),
                getAdvAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public SemPraedikatDatAkkOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        return new SemPraedikatDatAkkOhneLeerstellen(
                getVerb(),
                dat, akk,
                getModalpartikeln(),
                advAngabe, getNegationspartikel(), getAdvAngabeSkopusVerbAllg(),
                getAdvAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public SemPraedikatDatAkkOhneLeerstellen neg() {
        return (SemPraedikatDatAkkOhneLeerstellen) super.neg();
    }

    @Override
    public SemPraedikatDatAkkOhneLeerstellen neg(
            @Nullable final Negationspartikelphrase negationspartikelphrase) {
        if (negationspartikelphrase == null) {
            return this;
        }

        return new SemPraedikatDatAkkOhneLeerstellen(
                getVerb(),
                dat, akk,
                getModalpartikeln(),
                getAdvAngabeSkopusSatz(), negationspartikelphrase, getAdvAngabeSkopusVerbAllg(),
                getAdvAngabeSkopusVerbWohinWoher());
    }

    @Override
    public SemPraedikatDatAkkOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        return new SemPraedikatDatAkkOhneLeerstellen(
                getVerb(),
                dat, akk,
                getModalpartikeln(),
                getAdvAngabeSkopusSatz(), getNegationspartikel(), advAngabe,
                getAdvAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public SemPraedikatDatAkkOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativWohinWoher advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        return new SemPraedikatDatAkkOhneLeerstellen(
                getVerb(),
                dat, akk,
                getModalpartikeln(),
                getAdvAngabeSkopusSatz(),
                getNegationspartikel(), getAdvAngabeSkopusVerbAllg(),
                advAngabe
        );
    }

    @Override
    public boolean kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden() {
        return true;
    }

    @Nullable
    public Vorfeld getSpeziellesVorfeldAlsWeitereOption(
            final PraedRegMerkmale praedRegMerkmale,
            final SubstantivischePhrase datPhrase,
            final SubstantivischePhrase akkPhrase) {
        @Nullable final Vorfeld speziellesVorfeldFromSuper =
                super.getGgfVorfeldAdvAngabeSkopusVerb(praedRegMerkmale);
        if (speziellesVorfeldFromSuper != null) {
            return speziellesVorfeldFromSuper;
        }

        if (getNegationspartikel() != null) {
            return null;
        }

        /*
         * "es" allein darf nicht im Vorfeld stehen, wenn es ein Objekt ist.
         * (Eisenberg Der Satz 5.4.2)
         */
        if (Personalpronomen.isPersonalpronomenEs(akkPhrase, AKK)) {
            return null;
        }

        /*
         * Phrasen (auch Personalpronomen) mit Fokuspartikel
         * sind häufig kontrastiv und daher oft für das Vorfeld geeignet.
         */
        if (akkPhrase.getFokuspartikel() != null) {
            // "Nur dem Frosch gibst du die Kugel"
            return new Vorfeld(akkPhrase.akkK());
        }
        if (datPhrase.getFokuspartikel() != null) {
            // "Nur die Kugel gibst du dem Frosch"
            return new Vorfeld(datPhrase.datK());
        }

        return null;
    }

    @Override
    public boolean hatAkkusativobjekt() {
        return true;
    }

    @Override
    TopolFelder getTopolFelder(final ITextContext textContext,
                               final PraedRegMerkmale praedRegMerkmale,
                               final boolean nachAnschlusswort) {
        final Konstituente advAngabeSkopusSatzSyntFuerMittelfeld =
                getAdvAngabeSkopusSatzDescriptionFuerMittelfeld(praedRegMerkmale);

        final SubstantivischePhrase datPhrase = dat.alsSubstPhrase(textContext);
        final SubstantivischePhrase akkPhrase = akk.alsSubstPhrase(textContext);

        final Praedikatseinbindung<SubstantivischePhrase> datEinbindung =
                new Praedikatseinbindung<>(datPhrase, SubstantivischePhrase::datK);

        final Praedikatseinbindung<SubstantivischePhrase> akkEinbindung =
                new Praedikatseinbindung<>(akkPhrase, SubstantivischePhrase::akkK);

        final Konstituente advAngabeSkopusVerbSyntFuerMittelfeld =
                getAdvAngabeSkopusVerbTextDescriptionFuerMittelfeld(
                        praedRegMerkmale);
        final Konstituente advAngabeSkopusVerbWohinWoherSynt =
                getAdvAngabeSkopusVerbWohinWoherDescription(praedRegMerkmale);

        return new TopolFelder(
                new Mittelfeld(
                        Konstituentenfolge.joinToKonstituentenfolge(
                                advAngabeSkopusSatzSyntFuerMittelfeld,
                                // "aus einer Laune heraus"
                                getNegationspartikel() != null ? kf(getModalpartikeln()) : null,
                                // "mal eben (nicht)"
                                getNegationspartikel(), // "nicht"
                                datEinbindung, // "dem Frosch"
                                getNegationspartikel() == null ? kf(getModalpartikeln()) : null,
                                // "mal eben"
                                akkEinbindung, // "das Buch"
                                advAngabeSkopusVerbSyntFuerMittelfeld,
                                // "erneut"
                                advAngabeSkopusVerbWohinWoherSynt
                                // "in die Hand"
                        ),
                        akkEinbindung, datEinbindung), // Reihenfolge umgedreht! "es ihm"
                new Nachfeld(
                        Konstituentenfolge.joinToNullKonstituentenfolge(
                                getAdvAngabeSkopusVerbTextDescriptionFuerZwangsausklammerung(
                                        praedRegMerkmale),
                                getAdvAngabeSkopusSatzDescriptionFuerZwangsausklammerung(
                                        praedRegMerkmale)
                        )),
                getVorfeldAdvAngabeSkopusSatz(praedRegMerkmale),
                getSpeziellesVorfeldAlsWeitereOption(praedRegMerkmale, datPhrase, akkPhrase),
                firstRelativpronomen(datEinbindung, akkEinbindung),
                // Reihenfolge normal: "wem du was gibst"
                firstInterrogativwort(advAngabeSkopusSatzSyntFuerMittelfeld,
                        datEinbindung, advAngabeSkopusVerbSyntFuerMittelfeld, akkEinbindung,
                        advAngabeSkopusVerbWohinWoherSynt));
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
        final SemPraedikatDatAkkOhneLeerstellen that = (SemPraedikatDatAkkOhneLeerstellen) o;
        return dat.equals(that.dat) &&
                akk.equals(that.akk);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), dat, akk);
    }
}
