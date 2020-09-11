package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import java.util.Collection;

import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.WoertlicheRede;

import static de.nb.aventiure2.german.base.GermanUtil.joinToNull;

/**
 * Ein Prädikat mit wörtlicher Rede, in dem alle Leerstellen besetzt sind. Beispiele:
 * <ul>
 *     <li>"„Lass dein Haar herunter“ rufen"
 * </ul>
 * <p>
 * Adverbiale Angaben ("laut") können immer noch eingefügt werden.
 */
public class PraedikatMitWoertlicherRedeOhneLeerstellen
        extends AbstractPraedikatOhneLeerstellen {
    /**
     * Die wörtliche Rede
     */
    @NonNull
    private final WoertlicheRede woertlicheRede;

    public PraedikatMitWoertlicherRedeOhneLeerstellen(
            final Verb verb, final String woertlicheRedeText) {
        this(verb, new WoertlicheRede(woertlicheRedeText));
    }

    public PraedikatMitWoertlicherRedeOhneLeerstellen(
            final Verb verb, final WoertlicheRede woertlicheRede) {
        this(verb, woertlicheRede, null,
                null, null);
    }

    private PraedikatMitWoertlicherRedeOhneLeerstellen(
            final Verb verb, final WoertlicheRede woertlicheRede,
            @Nullable final AdverbialeAngabeSkopusSatz adverbialeAngabeSkopusSatz,
            @Nullable final AdverbialeAngabeSkopusVerbAllg adverbialeAngabeSkopusVerbAllg,
            @Nullable
            final AdverbialeAngabeSkopusVerbWohinWoher adverbialeAngabeSkopusVerbWohinWoher) {
        super(verb, adverbialeAngabeSkopusSatz,
                adverbialeAngabeSkopusVerbAllg, adverbialeAngabeSkopusVerbWohinWoher);
        this.woertlicheRede = woertlicheRede;
    }

    @Override
    public PraedikatMitWoertlicherRedeOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusSatz adverbialeAngabe) {
        if (adverbialeAngabe == null) {
            return this;
        }

        // TODO Mehrere adverbiale Angaben zulassen, damit die bestehende nicht
        //  einfach überschrieben wird!
        return new PraedikatMitWoertlicherRedeOhneLeerstellen(
                getVerb(),
                woertlicheRede,
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

        // TODO Mehrere adverbiale Angaben zulassen, damit die bestehende nicht
        //  einfach überschrieben wird!
        return new PraedikatMitWoertlicherRedeOhneLeerstellen(
                getVerb(),
                woertlicheRede,
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

        // TODO Mehrere adverbiale Angaben zulassen, damit die bestehende nicht
        //  einfach überschrieben wird!
        return new PraedikatMitWoertlicherRedeOhneLeerstellen(
                getVerb(),
                woertlicheRede,
                getAdverbialeAngabeSkopusSatz(),
                getAdverbialeAngabeSkopusVerbAllg(),
                adverbialeAngabe
        );
    }

    @Override
    public boolean duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen() {
        return false;
    }

    @Nullable
    @Override
    public String getSpeziellesVorfeld() {
        final String speziellesVorfeldFromSuper = super.getSpeziellesVorfeld();
        if (speziellesVorfeldFromSuper != null) {
            return speziellesVorfeldFromSuper;
        }

        return null;
    }

    @Override
    public String getMittelfeld(final Collection<Modalpartikel> modalpartikeln) {
        return joinToNull(
                getAdverbialeAngabeSkopusSatz(), // "aus einer Laune heraus"
                modalpartikeln,  // "mal eben"
                getAdverbialeAngabeSkopusVerbAllg(), // "erneut"
                getAdverbialeAngabeSkopusVerbWohinWoher() // "in ein Kissen"
        );
    }

    @Override
    public String getNachfeld() {
        return joinToNull(
                ":",
                woertlicheRede.amSatzende()); // "„Kommt alle her.“"
    }
}
