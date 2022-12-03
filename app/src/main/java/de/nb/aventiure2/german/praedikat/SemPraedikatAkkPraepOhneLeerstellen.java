package de.nb.aventiure2.german.praedikat;

import static de.nb.aventiure2.german.base.Kasus.AKK;
import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToKonstituentenfolge;
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
import de.nb.aventiure2.german.base.PraepositionMitKasus;
import de.nb.aventiure2.german.base.SubstantivischPhrasierbar;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.ITextContext;

/**
 * Ein "semantisches Prädikat", in dem ein Akkusativobjekt und ein Präpositionalobjekt gesetzt sind
 * und es auch sonst keine Leerstellen gibt. Beispiel: "den Frosch in die Hände nehmen"
 */
public class SemPraedikatAkkPraepOhneLeerstellen
        extends AbstractAngabenfaehigesSemPraedikatOhneLeerstellen {
    @Komplement
    @NonNull
    private final SubstantivischPhrasierbar akk;

    @Komplement
    @NonNull
    private final SubstantivischPhrasierbar praep;

    @NonNull
    private final PraepositionMitKasus praepositionMitKasus;

    @Valenz
    SemPraedikatAkkPraepOhneLeerstellen(
            final Verb verb,
            final PraepositionMitKasus praepositionMitKasus,
            final SubstantivischPhrasierbar akk,
            final SubstantivischPhrasierbar praep) {
        this(verb, praepositionMitKasus, akk, praep,
                ImmutableList.of(), null, null, null,
                null);
    }

    private SemPraedikatAkkPraepOhneLeerstellen(
            final Verb verb,
            final PraepositionMitKasus praepositionMitKasus,
            final SubstantivischPhrasierbar akk,
            final SubstantivischPhrasierbar praep,
            final Iterable<Modalpartikel> modalpartikeln,
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabeSkopusSatz,
            @Nullable final Negationspartikelphrase negationspartikelphrase,
            @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabeSkopusVerbAllg,
            @Nullable final IAdvAngabeOderInterrogativWohinWoher advAngabeSkopusVerbWohinWoher) {
        super(verb, modalpartikeln,
                advAngabeSkopusSatz,
                negationspartikelphrase, advAngabeSkopusVerbAllg, advAngabeSkopusVerbWohinWoher);
        this.praepositionMitKasus = praepositionMitKasus;
        this.praep = praep;
        this.akk = akk;
    }

    @Override
    public SemPraedikatAkkPraepOhneLeerstellen mitModalpartikeln(
            final Collection<Modalpartikel> modalpartikeln) {
        return new SemPraedikatAkkPraepOhneLeerstellen(
                getVerb(), praepositionMitKasus, akk, praep,
                Iterables.concat(getModalpartikeln(), modalpartikeln),
                getAdvAngabeSkopusSatz(),
                getNegationspartikel(), getAdvAngabeSkopusVerbAllg(),
                getAdvAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public SemPraedikatAkkPraepOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        // Man könnte mehrere adverbiale Angaben derselben Art zulassen, damit die bestehende nicht
        //  einfach überschrieben wird!
        return new SemPraedikatAkkPraepOhneLeerstellen(
                getVerb(),
                praepositionMitKasus, akk, praep,
                getModalpartikeln(),
                advAngabe, getNegationspartikel(), getAdvAngabeSkopusVerbAllg(),
                getAdvAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public SemPraedikatAkkPraepOhneLeerstellen neg() {
        return (SemPraedikatAkkPraepOhneLeerstellen) super.neg();
    }

    @Override
    public SemPraedikatAkkPraepOhneLeerstellen neg(
            @Nullable final Negationspartikelphrase negationspartikelphrase) {
        if (negationspartikelphrase == null) {
            return this;
        }

        return new SemPraedikatAkkPraepOhneLeerstellen(
                getVerb(),
                praepositionMitKasus, akk, praep,
                getModalpartikeln(),
                getAdvAngabeSkopusSatz(), negationspartikelphrase, getAdvAngabeSkopusVerbAllg(),
                getAdvAngabeSkopusVerbWohinWoher());
    }

    @Override
    public SemPraedikatAkkPraepOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        // Man könnte mehrere adverbiale Angaben derselben Art zulassen, damit die bestehende nicht
        //  einfach überschrieben wird!
        return new SemPraedikatAkkPraepOhneLeerstellen(
                getVerb(),
                praepositionMitKasus, akk, praep,
                getModalpartikeln(),
                getAdvAngabeSkopusSatz(), getNegationspartikel(), advAngabe,
                getAdvAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public SemPraedikatAkkPraepOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativWohinWoher advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        // Man könnte mehrere adverbiale Angaben derselben Art zulassen, damit die bestehende nicht
        //  einfach überschrieben wird!
        return new SemPraedikatAkkPraepOhneLeerstellen(
                getVerb(),
                praepositionMitKasus, akk, praep,
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
         * "es" allein darf nicht im Vorfeld stehen, wenn es ein Objekt ist
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
            // "Nur die Kugel nimmst du an dich"
            return new Vorfeld(akkPhrase.akkK());
        }

        // "Nur in das Glas schüttest du den Wein" - wirkt seltsam

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

        final SubstantivischePhrase akkPhrase = akk.alsSubstPhrase(textContext);
        final SubstantivischePhrase praepPhrase = praep.alsSubstPhrase(textContext);

        final Praedikatseinbindung<SubstantivischePhrase> akkEinbindung =
                new Praedikatseinbindung<>(akkPhrase, SubstantivischePhrase::akkK);

        final Praedikatseinbindung<SubstantivischePhrase> praepEinbindung =
                new Praedikatseinbindung<>(praepPhrase, p -> p.imK(praepositionMitKasus));

        final Konstituente advAngabeSkopusVerbSyntFuerMittelfeld =
                getAdvAngabeSkopusVerbTextDescriptionFuerMittelfeld(
                        praedRegMerkmale);
        final Konstituente advAngabeSkopusVerbWohinWoherSynt =
                getAdvAngabeSkopusVerbWohinWoherDescription(praedRegMerkmale);

        return new TopolFelder(
                new Mittelfeld(
                        joinToKonstituentenfolge(
                                advAngabeSkopusSatzSyntFuerMittelfeld,
                                // "aus einer Laune heraus"
                                getNegationspartikel() != null ? kf(getModalpartikeln()) :
                                        null,
                                // "besser doch (nicht)"
                                getNegationspartikel(), // "nicht"
                                akkEinbindung, // "das Teil"
                                getNegationspartikel() == null ? kf(getModalpartikeln()) :
                                        null,
                                // "besser doch"
                                advAngabeSkopusVerbSyntFuerMittelfeld,
                                // "erneut"
                                advAngabeSkopusVerbWohinWoherSynt,
                                // "aus der La main"
                                praepEinbindung // "nach dem Weg
                        ),
                        akkEinbindung, praepEinbindung),
                new Nachfeld(
                        Konstituentenfolge.joinToNullKonstituentenfolge(
                                getAdvAngabeSkopusVerbTextDescriptionFuerZwangsausklammerung(
                                        praedRegMerkmale),
                                getAdvAngabeSkopusSatzDescriptionFuerZwangsausklammerung(
                                        praedRegMerkmale)
                        )),
                getVorfeldAdvAngabeSkopusSatz(praedRegMerkmale),
                getSpeziellesVorfeldAlsWeitereOption(praedRegMerkmale, akkPhrase),
                firstRelativpronomen(akkEinbindung, praepEinbindung),
                firstInterrogativwort(advAngabeSkopusSatzSyntFuerMittelfeld,
                        akkEinbindung, advAngabeSkopusVerbSyntFuerMittelfeld,
                        advAngabeSkopusVerbWohinWoherSynt,
                        praepEinbindung));
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
        final SemPraedikatAkkPraepOhneLeerstellen that = (SemPraedikatAkkPraepOhneLeerstellen) o;
        return akk.equals(that.akk) &&
                praep.equals(that.praep) &&
                praepositionMitKasus == that.praepositionMitKasus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), akk, praep, praepositionMitKasus);
    }
}
