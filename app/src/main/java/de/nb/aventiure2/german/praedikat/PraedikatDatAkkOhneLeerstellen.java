package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.Collection;

import javax.annotation.Nullable;

import de.nb.aventiure2.annotations.Komplement;
import de.nb.aventiure2.annotations.Valenz;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativSkopusSatz;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativVerbAllg;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativWohinWoher;
import de.nb.aventiure2.german.base.Interrogativpronomen;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.SubstPhrOderReflexivpronomen;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static de.nb.aventiure2.german.base.Konstituentenfolge.kf;

/**
 * Ein Prädikat, in dem ein Dativobjekt und Akkusativobjekt gesetzt sind
 * und es auch sonst keine Leerstellen gibt. Beispiel: "dem Frosch Angebote machen"
 */
public class PraedikatDatAkkOhneLeerstellen
        extends AbstractAngabenfaehigesPraedikatOhneLeerstellen {
    /**
     * Das (Objekt / Wesen / Konzept für das) Dativobjekt (z.B. "Angebote")
     */
    @NonNull
    @Komplement
    private final SubstantivischePhrase dat;

    /**
     * Das (Objekt / Wesen / Konzept für das) Akkusativobjekte (z.B. der Frosch)
     */
    @NonNull
    @Komplement
    private final SubstantivischePhrase akk;

    @Valenz
    PraedikatDatAkkOhneLeerstellen(
            final Verb verb,
            final SubstantivischePhrase dat,
            final SubstantivischePhrase akk) {
        this(verb, dat, akk,
                ImmutableList.of(), null, null,
                null);
    }

    private PraedikatDatAkkOhneLeerstellen(
            final Verb verb,
            final SubstantivischePhrase dat,
            final SubstantivischePhrase akk,
            final Iterable<Modalpartikel> modalpartikeln,
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz adverbialeAngabeSkopusSatz,
            @Nullable final IAdvAngabeOderInterrogativVerbAllg adverbialeAngabeSkopusVerbAllg,
            @Nullable
            final IAdvAngabeOderInterrogativWohinWoher adverbialeAngabeSkopusVerbWohinWoher) {
        super(verb, modalpartikeln, adverbialeAngabeSkopusSatz,
                adverbialeAngabeSkopusVerbAllg, adverbialeAngabeSkopusVerbWohinWoher);
        this.dat = dat;
        this.akk = akk;
    }

    @Override
    public PraedikatDatAkkOhneLeerstellen mitModalpartikeln(
            final Collection<Modalpartikel> modalpartikeln) {
        return new PraedikatDatAkkOhneLeerstellen(
                getVerb(), dat, akk,
                Iterables.concat(getModalpartikeln(), modalpartikeln),
                getAdverbialeAngabeSkopusSatz(),
                getAdverbialeAngabeSkopusVerbAllg(),
                getAdverbialeAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public PraedikatDatAkkOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz adverbialeAngabe) {
        if (adverbialeAngabe == null) {
            return this;
        }

        return new PraedikatDatAkkOhneLeerstellen(
                getVerb(),
                dat, akk,
                getModalpartikeln(),
                adverbialeAngabe, getAdverbialeAngabeSkopusVerbAllg(),
                getAdverbialeAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public PraedikatDatAkkOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final IAdvAngabeOderInterrogativVerbAllg adverbialeAngabe) {
        if (adverbialeAngabe == null) {
            return this;
        }

        return new PraedikatDatAkkOhneLeerstellen(
                getVerb(),
                dat, akk,
                getModalpartikeln(),
                getAdverbialeAngabeSkopusSatz(), adverbialeAngabe,
                getAdverbialeAngabeSkopusVerbWohinWoher()
        );
    }


    @Override
    public PraedikatDatAkkOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final IAdvAngabeOderInterrogativWohinWoher adverbialeAngabe) {
        if (adverbialeAngabe == null) {
            return this;
        }

        return new PraedikatDatAkkOhneLeerstellen(
                getVerb(),
                dat, akk,
                getModalpartikeln(),
                getAdverbialeAngabeSkopusSatz(),
                getAdverbialeAngabeSkopusVerbAllg(),
                adverbialeAngabe
        );
    }

    @Override
    public boolean kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden() {
        return true;
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

        // "Die Kugel gibst du dem Frosch" - nicht schön, da meist "der
        // Frosch" das Thema ist und "die Kugel" das Rhema.

        return null;
    }

    @Override
    Konstituentenfolge getMittelfeldOhneLinksversetzungUnbetonterPronomen(
            final Person personSubjekt, final Numerus numerusSubjekt) {
        return Konstituentenfolge.joinToKonstituentenfolge(
                getAdverbialeAngabeSkopusSatzDescriptionFuerMittelfeld(personSubjekt,
                        numerusSubjekt),
                // "aus einer Laune heraus"
                dat.datK(), // "dem Frosch"
                kf(getModalpartikeln()), // "halt"
                getAdverbialeAngabeSkopusVerbTextDescriptionFuerMittelfeld(personSubjekt,
                        numerusSubjekt), // "auf deiner Flöte"
                akk.akkK(), // "ein Lied"
                getAdverbialeAngabeSkopusVerbWohinWoherDescription(personSubjekt,
                        numerusSubjekt));
    }

    @Override
    @Nullable
    public SubstantivischePhrase getDat(final Person personSubjekt,
                                        final Numerus numerusSubjekt) {
        return dat;
    }

    @Nullable
    @Override
    public SubstantivischePhrase getAkk(final Person personSubjekt,
                                        final Numerus numerusSubjekt) {
        return akk;
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
                getAdverbialeAngabeSkopusVerbTextDescriptionFuerZwangsausklammerung(personSubjekt,
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
    public Konstituentenfolge getErstesInterrogativwort() {
        @Nullable
        Konstituentenfolge res = interroAdverbToKF(getAdverbialeAngabeSkopusSatz());
        if (res != null) {
            return res;
        }

        if (dat instanceof Interrogativpronomen) {
            return dat.datK();
        }

        res = interroAdverbToKF(getAdverbialeAngabeSkopusVerbAllg());
        if (res != null) {
            return res;
        }

        if (akk instanceof Interrogativpronomen) {
            return akk.akkK();
        }

        return interroAdverbToKF(getAdverbialeAngabeSkopusVerbWohinWoher());
    }
}
