package de.nb.aventiure2.german.praedikat;

import static de.nb.aventiure2.german.base.Kasus.AKK;
import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToKonstituentenfolge;
import static de.nb.aventiure2.german.base.Konstituentenfolge.kf;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.Collection;
import java.util.Objects;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;

import de.nb.aventiure2.annotations.Komplement;
import de.nb.aventiure2.annotations.Valenz;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativSkopusSatz;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativVerbAllg;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativWohinWoher;
import de.nb.aventiure2.german.base.Interrogativpronomen;
import de.nb.aventiure2.german.base.KasusOderPraepositionalkasus;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.Negationspartikelphrase;
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.base.PraedRegMerkmale;
import de.nb.aventiure2.german.base.Relativpronomen;
import de.nb.aventiure2.german.base.SubstantivischPhrasierbar;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.ITextContext;

/**
 * Ein "semantisches Prädikat" (Verb ggf. mit Präfix) bei dem das Verb mit einem Subjekt und einem
 * (Präpositional-) Objekt steht - alle Leerstellen sind besetzt.
 */
public class SemPraedikatSubjObjOhneLeerstellen
        extends AbstractAngabenfaehigesSemPraedikatOhneLeerstellen {
    /**
     * Der Kasus (z.B. Akkusativ, "die Kugel nehmen") oder Präpositionalkasus
     * (z.B. "mit dem Frosch reden"), mit dem dieses Verb steht (den dieses Verb regiert).
     */
    @NonNull
    private final KasusOderPraepositionalkasus kasusOderPraepositionalkasus;

    /**
     * Das Objekt
     */
    @Komplement
    private final SubstantivischPhrasierbar objekt;

    SemPraedikatSubjObjOhneLeerstellen(final Verb verb,
                                       final KasusOderPraepositionalkasus kasusOderPraepositionalkasus,
                                       final SubstantivischPhrasierbar objekt) {
        this(verb, kasusOderPraepositionalkasus,
                false,
                objekt);
    }

    @Valenz
    SemPraedikatSubjObjOhneLeerstellen(final Verb verb,
                                       final KasusOderPraepositionalkasus kasusOderPraepositionalkasus,
                                       final boolean inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich,
                                       final SubstantivischPhrasierbar objekt) {
        this(verb, kasusOderPraepositionalkasus,
                inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich,
                objekt,
                ImmutableList.of(),
                null, null, null,
                null);
    }

    SemPraedikatSubjObjOhneLeerstellen(
            final Verb verb,
            final KasusOderPraepositionalkasus kasusOderPraepositionalkasus,
            final boolean inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich,
            final SubstantivischPhrasierbar objekt,
            final Iterable<Modalpartikel> modalpartikeln,
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabeSkopusSatz,
            @Nullable final Negationspartikelphrase negationspartikelphrase,
            @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabeSkopusVerbAllg,
            @Nullable final IAdvAngabeOderInterrogativWohinWoher advAngabeSkopusVerbWohinWoher) {
        super(verb, inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich, modalpartikeln,
                advAngabeSkopusSatz,
                negationspartikelphrase, advAngabeSkopusVerbAllg, advAngabeSkopusVerbWohinWoher);
        this.kasusOderPraepositionalkasus = kasusOderPraepositionalkasus;
        this.objekt = objekt;
    }

    @Override
    public SemPraedikatSubjObjOhneLeerstellen mitModalpartikeln(
            final Collection<Modalpartikel> modalpartikeln) {
        return new SemPraedikatSubjObjOhneLeerstellen(
                getVerb(), kasusOderPraepositionalkasus,
                inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich(),
                objekt,
                Iterables.concat(getModalpartikeln(), modalpartikeln),
                getAdvAngabeSkopusSatz(),
                getNegationspartikel(), getAdvAngabeSkopusVerbAllg(),
                getAdvAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public SemPraedikatSubjObjOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        return new SemPraedikatSubjObjOhneLeerstellen(
                getVerb(),
                kasusOderPraepositionalkasus,
                inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich(),
                objekt,
                getModalpartikeln(),
                advAngabe, getNegationspartikel(), getAdvAngabeSkopusVerbAllg(),
                getAdvAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public SemPraedikatSubjObjOhneLeerstellen neg() {
        return (SemPraedikatSubjObjOhneLeerstellen) super.neg();
    }

    @Override
    public SemPraedikatSubjObjOhneLeerstellen neg(
            @Nullable final Negationspartikelphrase negationspartikelphrase) {
        if (negationspartikelphrase == null) {
            return this;
        }

        return new SemPraedikatSubjObjOhneLeerstellen(
                getVerb(),
                kasusOderPraepositionalkasus,
                inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich(),
                objekt,
                getModalpartikeln(),
                getAdvAngabeSkopusSatz(), negationspartikelphrase, getAdvAngabeSkopusVerbAllg(),
                getAdvAngabeSkopusVerbWohinWoher());
    }


    @Override
    public SemPraedikatSubjObjOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        return new SemPraedikatSubjObjOhneLeerstellen(
                getVerb(),
                kasusOderPraepositionalkasus,
                inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich(),
                objekt,
                getModalpartikeln(),
                getAdvAngabeSkopusSatz(), getNegationspartikel(), advAngabe,
                getAdvAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public SemPraedikatSubjObjOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativWohinWoher advAngabe) {
        if (advAngabe == null) {
            return this;
        }

        return new SemPraedikatSubjObjOhneLeerstellen(
                getVerb(),
                kasusOderPraepositionalkasus,
                inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich(),
                objekt,
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

    // FIXME Vielleicht eine "Containerklasse" bauen, die Vorfeld, Nachfeld etc.
    //  enthält - so wie sie zusammenpassen?! Dann wäre speziellesVorfeldSehrErwuenscht
    //  vielleicht nur ein boolean-Parameter?!
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

        if (inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich()) {
            final Konstituentenfolge vorfeldCandidate = objekt.imK(kasusOderPraepositionalkasus);
            if (vorfeldCandidate.size() == 1 && vorfeldCandidate.get(0) instanceof Konstituente) {
                // "mich (friert)"
                return (Konstituente) vorfeldCandidate.get(0);
            }
        }

        return null;
    }

    @Override
    public @Nullable
    Konstituentenfolge getSpeziellesVorfeldAlsWeitereOption(
            final PraedRegMerkmale praedRegMerkmale) {
        @Nullable final Konstituentenfolge speziellesVorfeldFromSuper =
                super.getSpeziellesVorfeldAlsWeitereOption(praedRegMerkmale);
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
        if (Personalpronomen.isPersonalpronomenEs(objekt, kasusOderPraepositionalkasus)) {
            return null;
        }

        /*
         * Phrasen (auch Personalpronomen) mit Fokuspartikel
         * sind häufig kontrastiv und daher oft für das Vorfeld geeignet.
         */
        if (objekt.getFokuspartikel() != null) {
            // "Nur die Kugel nimmst du"
            return objekt.imK(kasusOderPraepositionalkasus);
        }

        return null;
    }

    @Override
    public boolean hatAkkusativobjekt() {
        return kasusOderPraepositionalkasus == AKK;
    }

    @Override
    TopolFelder getTopolFelder(final ITextContext textContext,
                               final PraedRegMerkmale praedRegMerkmale) {
        final SubstantivischePhrase syntObjekt = objekt.alsSubstPhrase(textContext);
        // FIXME Eigentlich kann sich der Kontext vor der Auswahl, welches syntaktische
        //  Objekt geeignet ist, verändert haben! Besser sollte
        //  joinToKonstituentenfolge() die Umwandlung in das syntaktische Objekt übernehmen!
        //  Dazu müsste allerdings:
        //  - joinToKonstituentenfolge() den textContext (zu Anfang) kennen
        //  - den Text-Kontext ggf. ändern
        //  - außerdem irgendwie zurückmelden, was die Objekte oder zumindest die
        //  unbetonten Pronomen sind (oder man macht das anders mit den unbetonten
        //  Pronomen...)

        return new TopolFelder(
                new Mittelfeld(
                        joinToKonstituentenfolge(
                                getAdvAngabeSkopusSatzDescriptionFuerMittelfeld(praedRegMerkmale),
// "aus einer Laune heraus"
                                getNegationspartikel() != null ? kf(getModalpartikeln()) : null,
                                // "besser doch (nicht)"
                                getNegationspartikel(), // "nicht"
                                syntObjekt.imK(kasusOderPraepositionalkasus),
                                getNegationspartikel() == null ? kf(getModalpartikeln()) : null,
                                // "besser doch"
                                getAdvAngabeSkopusVerbTextDescriptionFuerMittelfeld(
                                        praedRegMerkmale),
                                // "erneut"
                                getAdvAngabeSkopusVerbWohinWoherDescription(praedRegMerkmale)
                                // "auf den Tisch"
                        ),
                        syntObjekt, kasusOderPraepositionalkasus),
                new Nachfeld(
                        Konstituentenfolge.joinToNullKonstituentenfolge(
                                getAdvAngabeSkopusVerbTextDescriptionFuerZwangsausklammerung(
                                        praedRegMerkmale),
                                getAdvAngabeSkopusSatzDescriptionFuerZwangsausklammerung(
                                        praedRegMerkmale)
                        )));
    }

    @Override
    public boolean
    umfasstSatzglieder() {
        return true;
    }

    // FIXME Diese Methode umstellen mit einer immutable State-Klasse wie Mittelfeld!
    @Nullable
    @Override
    @CheckReturnValue
    public Konstituentenfolge getErstesInterrogativwort() {
        @Nullable
        Konstituentenfolge res = interroAdverbToKF(getAdvAngabeSkopusSatz());
        if (res != null) {
            return res;
        }

        res = interroAdverbToKF(getAdvAngabeSkopusVerbAllg());
        if (res != null) {
            return res;
        }

        if (objekt instanceof Interrogativpronomen) {
            return objekt.imK(kasusOderPraepositionalkasus);
        }

        return interroAdverbToKF(getAdvAngabeSkopusVerbWohinWoher());
    }

    // FIXME Diese Methode umstellen mit einer immutable State-Klasse wie Mittelfeld!
    @Nullable
    @Override
    @CheckReturnValue
    public Konstituentenfolge getRelativpronomen() {
        if (objekt instanceof Relativpronomen) {
            return objekt.imK(kasusOderPraepositionalkasus);
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
        final SemPraedikatSubjObjOhneLeerstellen that =
                (SemPraedikatSubjObjOhneLeerstellen) o;
        return kasusOderPraepositionalkasus.equals(that.kasusOderPraepositionalkasus) &&
                Objects.equals(objekt, that.objekt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), kasusOderPraepositionalkasus, objekt);
    }
}
