package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import javax.annotation.Nullable;

import de.nb.aventiure2.annotations.Argument;
import de.nb.aventiure2.annotations.Valenz;
import de.nb.aventiure2.german.base.GermanUtil;
import de.nb.aventiure2.german.base.Interrogativpronomen;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.base.Reflexivpronomen;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static de.nb.aventiure2.german.base.Konstituente.k;

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
    @Argument
    private final SubstantivischePhrase dat;

    /**
     * Das (Objekt / Wesen / Konzept für das) Akkusativobjekte (z.B. der Frosch)
     */
    @NonNull
    @Argument
    private final SubstantivischePhrase akk;

    @Valenz
    PraedikatDatAkkOhneLeerstellen(
            final Verb verb,
            final SubstantivischePhrase dat,
            final SubstantivischePhrase akk) {
        this(verb, dat, akk,
                null, null,
                null);
    }

    private PraedikatDatAkkOhneLeerstellen(
            final Verb verb,
            final SubstantivischePhrase dat,
            final SubstantivischePhrase akk,
            @Nullable final AdverbialeAngabeSkopusSatz adverbialeAngabeSkopusSatz,
            @Nullable final AdverbialeAngabeSkopusVerbAllg adverbialeAngabeSkopusVerbAllg,
            @Nullable
            final AdverbialeAngabeSkopusVerbWohinWoher adverbialeAngabeSkopusVerbWohinWoher) {
        super(verb, adverbialeAngabeSkopusSatz,
                adverbialeAngabeSkopusVerbAllg, adverbialeAngabeSkopusVerbWohinWoher);
        this.dat = dat;
        this.akk = akk;
    }

    @Override
    public PraedikatDatAkkOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusSatz adverbialeAngabe) {
        if (adverbialeAngabe == null) {
            return this;
        }

        // TODO Mehrere adverbiale Angaben zulassen, damit die bestehende nicht
        //  einfach überschrieben wird!
        return new PraedikatDatAkkOhneLeerstellen(
                getVerb(),
                dat, akk,
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

        // TODO Mehrere adverbiale Angaben zulassen, damit die bestehende nicht
        //  einfach überschrieben wird!
        return new PraedikatDatAkkOhneLeerstellen(
                getVerb(),
                dat, akk,
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

        // TODO Mehrere adverbiale Angaben zulassen, damit die bestehende nicht
        //  einfach überschrieben wird!
        return new PraedikatDatAkkOhneLeerstellen(
                getVerb(),
                dat, akk,
                getAdverbialeAngabeSkopusSatz(),
                getAdverbialeAngabeSkopusVerbAllg(),
                adverbialeAngabe
        );
    }

    @Override
    public boolean duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen() {
        return true;
    }

    @Override
    public boolean kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden() {
        return true;
    }

    @Override
    public @Nullable
    Konstituente getSpeziellesVorfeld(final Person person, final Numerus numerus) {
        @Nullable final Konstituente speziellesVorfeldFromSuper = super.getSpeziellesVorfeld(person,
                numerus);
        if (speziellesVorfeldFromSuper != null) {
            return speziellesVorfeldFromSuper;
        }

        return null; // "Die Kugel gibst du dem Frosch" - nicht schön, da meist "der
        // Frosch" das Thema ist und "die Kugel" das Rhema.
    }

    @Override
    public Iterable<Konstituente> getMittelfeld(final Collection<Modalpartikel> modalpartikeln,
                                                final Person personSubjekt,
                                                final Numerus numerusSubjekt) {
        // Duden 1356: "Schwach betonte Personal- und Reflexivpronomen stehen
        // unmittelbar nach der linken Satzklammer [...] Wackernagel-Position"

        final String akk = this.akk.akk();
        final String dat = this.dat.dat();
        if (Personalpronomen.isPersonalpronomen(akk) ||
                Reflexivpronomen.isReflexivpronomen(akk)) {

            if (Personalpronomen.isPersonalpronomen(dat) ||
                    Reflexivpronomen.isReflexivpronomen(dat)) {
                // Duden 1357: "Akkusativ > Dativ"
                return Konstituente.joinToKonstituenten(
                        akk, // "sie", Wackernagel-Position 1
                        dat, // "ihm", Wackernagel-Position 2
                        getAdverbialeAngabeSkopusSatzDescription(),
                        // "aus einer Laune heraus"
                        GermanUtil.joinToNullString(modalpartikeln), // "halt"
                        getAdverbialeAngabeSkopusVerbAllgDescription()); // "auf deiner Flöte"
            }

            return Konstituente.joinToKonstituenten(
                    akk, // "sie", Wackernagel-Position
                    getAdverbialeAngabeSkopusSatzDescription(), // "aus einer Laune heraus"
                    dat, // "dem Frosch"
                    GermanUtil.joinToNullString(modalpartikeln), // "halt"
                    getAdverbialeAngabeSkopusVerbAllgDescription()); // "auf deiner Flöte"
        }

        if (Personalpronomen.isPersonalpronomen(dat) ||
                Reflexivpronomen.isReflexivpronomen(dat)) {
            return Konstituente.joinToKonstituenten(
                    dat, // "ihm", Wackernagel-Position
                    getAdverbialeAngabeSkopusSatzDescription(), // "aus einer Laune heraus"
                    akk, // "die Melodie"
                    GermanUtil.joinToNullString(modalpartikeln), // "halt"

                    // FIXME Das hier betrifft alle Mittelfelder:
                    //  Adjektivphrasen als adverbiale
                    //  Angaben dürfen nur unter gewissen Voraussetzungen
                    //  im Mittelfeld stehen:
                    //  Die Beschränkung ist: Infinitivphrasen
                    //  können nicht im Mittelfeld stehen
                    //  (aber im Vorfeld oder Nachfeld). Das gilt auch für
                    //  Angaben- und Ergänzungssätze (z.B. indirekte Fragen).
                    //  Vgl. *"Sie schaut dich, glücklich dich zu sehen, an."
                    //  Möglichw wären "Sie schaut dich an, glücklich, dich zu
                    //  sehen." und "Glücklich, dich zu sehen, schaut sie dich
                    //  an." Offenbar muss dann auch die gesamte adverbiale
                    //  Adjektivphrase kontinuierlich bleiben!
                    //  Die Logik ist also in etwa: Falls die Phrase ... enthält,
                    //  Darf sie nicht ins Mittelfeld gestellt werden.
                    //  Es gilt also diese Logik: Die Phrase sollte dann ins
                    //  Vorfeld gestellt werden, wenn es keine Skopus-Satz
                    //  Adverbialie gibt. Wenn doch, dann ins Nachfeld.

                    getAdverbialeAngabeSkopusVerbAllgDescription()); // "auf deiner Flöte"
        }

        return Konstituente.joinToKonstituenten(
                getAdverbialeAngabeSkopusSatzDescription(), // "aus einer Laune heraus"
                dat, // "dem Frosch"
                GermanUtil.joinToNullString(modalpartikeln), // "halt"
                getAdverbialeAngabeSkopusVerbAllgDescription(), // "auf deiner Flöte"
                akk); // "ein Lied"
    }

    @Override
    public Iterable<Konstituente> getNachfeld(final Person personSubjekt,
                                              final Numerus numerusSubjekt) {
        return ImmutableList.of();
    }

    @Override
    public boolean umfasstSatzglieder() {
        return true;
    }

    @Override
    public boolean hatAkkusativobjekt() {
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
