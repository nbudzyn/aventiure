package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.Collection;

import javax.annotation.Nullable;

import de.nb.aventiure2.annotations.Komplement;
import de.nb.aventiure2.annotations.Valenz;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.SubstantivischePhraseOderReflexivpronomen;
import de.nb.aventiure2.german.base.WoertlicheRede;

import static de.nb.aventiure2.german.base.Konstituente.k;

/**
 * Ein Prädikat mit wörtlicher Rede, in dem alle Leerstellen besetzt sind. Beispiele:
 * <ul>
 *     <li>"„Lass dein Haar herunter“ rufen"
 * </ul>
 * <p>
 * Adverbiale Angaben ("laut") können immer noch eingefügt werden.
 */
public class PraedikatMitWoertlicherRedeOhneLeerstellen
        extends AbstractAngabenfaehigesPraedikatOhneLeerstellen {
    /**
     * Die wörtliche Rede
     */
    @Komplement
    @NonNull
    private final WoertlicheRede woertlicheRede;

    public PraedikatMitWoertlicherRedeOhneLeerstellen(
            final Verb verb, final String woertlicheRedeText) {
        this(verb, new WoertlicheRede(woertlicheRedeText));
    }

    @Valenz
    PraedikatMitWoertlicherRedeOhneLeerstellen(
            final Verb verb, final WoertlicheRede woertlicheRede) {
        this(verb, woertlicheRede, ImmutableList.of(), null,
                null, null);
    }

    private PraedikatMitWoertlicherRedeOhneLeerstellen(
            final Verb verb, final WoertlicheRede woertlicheRede,
            final Iterable<Modalpartikel> modalpartikeln,
            @Nullable final AdverbialeAngabeSkopusSatz adverbialeAngabeSkopusSatz,
            @Nullable final AdverbialeAngabeSkopusVerbAllg adverbialeAngabeSkopusVerbAllg,
            @Nullable
            final AdverbialeAngabeSkopusVerbWohinWoher adverbialeAngabeSkopusVerbWohinWoher) {
        super(verb, modalpartikeln, adverbialeAngabeSkopusSatz,
                adverbialeAngabeSkopusVerbAllg, adverbialeAngabeSkopusVerbWohinWoher);
        this.woertlicheRede = woertlicheRede;
    }

    @Override
    public PraedikatMitWoertlicherRedeOhneLeerstellen mitModalpartikeln(
            final Collection<Modalpartikel> modalpartikeln) {
        return new PraedikatMitWoertlicherRedeOhneLeerstellen(
                getVerb(), woertlicheRede,
                Iterables.concat(getModalpartikeln(), modalpartikeln),
                getAdverbialeAngabeSkopusSatz(),
                getAdverbialeAngabeSkopusVerbAllg(),
                getAdverbialeAngabeSkopusVerbWohinWoher()
        );
    }


    @Override
    public PraedikatMitWoertlicherRedeOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusSatz adverbialeAngabe) {
        if (adverbialeAngabe == null) {
            return this;
        }

        return new PraedikatMitWoertlicherRedeOhneLeerstellen(
                getVerb(),
                woertlicheRede,
                getModalpartikeln(),
                adverbialeAngabe, getAdverbialeAngabeSkopusVerbAllg(),
                getAdverbialeAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public PraedikatMitWoertlicherRedeOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusVerbAllg adverbialeAngabe) {
        if (adverbialeAngabe == null) {
            return this;
        }

        return new PraedikatMitWoertlicherRedeOhneLeerstellen(
                getVerb(),
                woertlicheRede,
                getModalpartikeln(),
                getAdverbialeAngabeSkopusSatz(), adverbialeAngabe,
                getAdverbialeAngabeSkopusVerbWohinWoher()
        );
    }

    @Override
    public PraedikatMitWoertlicherRedeOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusVerbWohinWoher adverbialeAngabe) {
        if (adverbialeAngabe == null) {
            return this;
        }

        return new PraedikatMitWoertlicherRedeOhneLeerstellen(
                getVerb(),
                woertlicheRede,
                getModalpartikeln(),
                getAdverbialeAngabeSkopusSatz(),
                getAdverbialeAngabeSkopusVerbAllg(),
                adverbialeAngabe
        );
    }

    @Override
    public boolean kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden() {
        return false;
    }

    @Override
    Iterable<Konstituente> getMittelfeldOhneLinksversetzungUnbetonterPronomen(
            final Person personSubjekt, final Numerus numerusSubjekt) {
        return Konstituente.joinToKonstituenten(
                getAdverbialeAngabeSkopusSatzDescription(), // "aus einer Laune heraus"
                getModalpartikeln(),  // "mal eben"
                getAdverbialeAngabeSkopusVerbAllgDescriptionFuerMittelfeld(), // "erneut"
                getAdverbialeAngabeSkopusVerbWohinWoherDescription()// "in ein Kissen"
        );
    }

    @Nullable
    @Override
    SubstantivischePhraseOderReflexivpronomen getDat(
            final Person personSubjekt, final Numerus numerusSubjekt) {
        return null;
    }

    @Nullable
    @Override
    SubstantivischePhraseOderReflexivpronomen getAkk(
            final Person personSubjekt, final Numerus numerusSubjekt) {
        return null;
    }

    @Nullable
    @Override
    SubstantivischePhraseOderReflexivpronomen getZweitesAkk() {
        return null;
    }

    @Override
    public Iterable<Konstituente> getNachfeld(final Person personSubjekt,
                                              final Numerus numerusSubjekt) {
        return Konstituente.joinToKonstituenten(
                getAdverbialeAngabeSkopusVerbAllgDescriptionFuerZwangsausklammerung(),
                k(woertlicheRede.fuerNachfeld(),
                        true,
                        true)); // "„Kommt alle her.“"

        //  Laut http://www.pro-publish.com/korrektor/zeichensetzung-interpunktion/den-doppelpunkt-richtig-setzen/
        //  ist es möglich, nach dem Doppelpunkt den Satz fortzuführen - dabei darf die
        //  wörtliche Rede allerdings nicht mit Punkt abgeschlossen worden sein:
        //  "Peter sagte: „Ich fahre jetzt nach Hause“, doch es wurde noch viel später an diesem
        //  Abend."
    }

    @Override
    public boolean umfasstSatzglieder() {
        return true;
    }


    @Nullable
    @Override
    public Konstituente getErstesInterrogativpronomen() {
        return null;
    }
}
