package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.Collection;

import javax.annotation.Nullable;

import de.nb.aventiure2.annotations.Komplement;
import de.nb.aventiure2.annotations.Valenz;
import de.nb.aventiure2.german.base.Interrogativpronomen;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.base.SubstantivischePhraseOderReflexivpronomen;

import static de.nb.aventiure2.german.base.Konstituente.k;
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
            @Nullable final AdverbialeAngabeSkopusSatz adverbialeAngabeSkopusSatz,
            @Nullable final AdverbialeAngabeSkopusVerbAllg adverbialeAngabeSkopusVerbAllg,
            @Nullable
            final AdverbialeAngabeSkopusVerbWohinWoher adverbialeAngabeSkopusVerbWohinWoher) {
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
            @Nullable final AdverbialeAngabeSkopusSatz adverbialeAngabe) {
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
            @Nullable final AdverbialeAngabeSkopusVerbAllg adverbialeAngabe) {
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
            @Nullable final AdverbialeAngabeSkopusVerbWohinWoher adverbialeAngabe) {
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

    @Override
    public @Nullable
    Konstituente getSpeziellesVorfeldAlsWeitereOption(final Person person, final Numerus numerus) {
        @Nullable final Konstituente speziellesVorfeldFromSuper =
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
                dat.dat(), // "dem Frosch"
                kf(getModalpartikeln()), // "halt"
                getAdverbialeAngabeSkopusVerbAllgDescriptionFuerMittelfeld(personSubjekt,
                        numerusSubjekt), // "auf deiner Flöte"
                akk.akk()); // "ein Lied"
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
    SubstantivischePhraseOderReflexivpronomen getZweitesAkk() {
        return null;
    }

    @Override
    public Konstituentenfolge getNachfeld(final Person personSubjekt,
                                          final Numerus numerusSubjekt) {
        return Konstituentenfolge.joinToKonstituentenfolge(
                getAdverbialeAngabeSkopusVerbAllgDescriptionFuerZwangsausklammerung(personSubjekt,
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
    public Konstituente getErstesInterrogativpronomen() {
        if (dat instanceof Interrogativpronomen) {
            return k(dat.dat());
        }

        if (akk instanceof Interrogativpronomen) {
            return k(akk.akk());
        }

        return null;
    }
}
