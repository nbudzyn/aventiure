package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import java.util.Collection;

import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.KasusOderPraepositionalkasus;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static de.nb.aventiure2.german.base.GermanUtil.capitalize;
import static de.nb.aventiure2.german.base.GermanUtil.joinToNull;
import static java.util.Arrays.asList;

/**
 * Ein Prädikat (Verb ggf. mit Präfix) bei dem das Verb mit einem Subjekt und einem
 * (Präpositional-) Objekt steht - alle Leerstellen sind besetzt.
 */
class PraedikatSubjObjOhneLeerstellen implements PraedikatOhneLeerstellen {
    /**
     * Das Verb an sich, ohne Informationen zur Valenz, ohne Ergänzungen, ohne
     * Angaben
     */
    @NonNull
    private final Verb verb;

    /**
     * Der Kasus (z.B. Akkusativ, "die Kugel nehmen") oder Präpositionalkasus
     * (z.B. "mit dem Frosch reden"), mit dem dieses Verb steht (den dieses Verb regiert).
     */
    @NonNull
    private final KasusOderPraepositionalkasus kasusOderPraepositionalkasus;

    /**
     * Das Objekt (z.B. ein Ding, Wesen, Konzept oder deklinierbare Phrase)
     */
    private final SubstantivischePhrase objekt;


    public PraedikatSubjObjOhneLeerstellen(final Verb verb,
                                           final KasusOderPraepositionalkasus kasusOderPraepositionalkasus,
                                           final SubstantivischePhrase objekt) {
        this.verb = verb;
        this.kasusOderPraepositionalkasus = kasusOderPraepositionalkasus;
        this.objekt = objekt;
    }

    /**
     * Gibt einen Satz zurück mit diesem Prädikat.
     * ("Du nimmst den Ast")
     */
    @Override
    public String getDescriptionDuHauptsatz(
            final Collection<Modalpartikel> modalpartikeln) {
        return "Du " + getDescriptionHauptsatzMitEingespartemVorfeldSubj(modalpartikeln);
    }

    @Override
    public boolean duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen() {
        return true;
    }

    /**
     * Gibt einen Satz zurück mit diesem Prädikat, bei dem das Subjekt, das im Vorfeld
     * stünde, eingespart ist ("nimmst den Ast")
     */
    public String getDescriptionHauptsatzMitEingespartemVorfeldSubj(
            final Modalpartikel... modalpartikeln) {
        return getDescriptionHauptsatzMitEingespartemVorfeldSubj(
                asList(modalpartikeln)
        );
    }

    /**
     * Gibt einen Satz zurück mit diesem Prädikat, bei dem das Subjekt, das im Vorfeld
     * stünde, eingespart ist ("nimmst den Ast"), sowie ggf. diesen
     * Modalpartikeln ("nimmst den Ast eben doch").
     */
    public String getDescriptionHauptsatzMitEingespartemVorfeldSubj(
            final Collection<Modalpartikel> modalpartikeln) {
        return joinToNull(
                verb.getDuForm(),
                objekt.im(kasusOderPraepositionalkasus),
                joinToNull(modalpartikeln),
                verb.getPartikel());
    }

    /**
     * Gibt einen Satz zurück mit diesem Prädikat und dieser adverbialen Angabe.
     * ("Aus Langeweile nimmst du den Ast")
     */
    @Override
    public String getDescriptionDuHauptsatz(@NonNull final AdverbialeAngabe adverbialeAngabe) {
        if (verb.getPartikel() == null) {
            return capitalize(adverbialeAngabe.getText()) +
                    " " + verb.getDuForm() +
                    " du " +
                    objekt.im(kasusOderPraepositionalkasus);
        }

        return capitalize(adverbialeAngabe.getText()) +
                " " + verb.getDuForm() +
                " du " +
                objekt.im(kasusOderPraepositionalkasus) +
                " " + verb.getPartikel();
    }

    /**
     * Gibt eine Infinitivkonstruktion zurück mit Prädikat.
     * ("den Frosch ignorieren", "das Leben genießen")
     */
    @Override
    public String getDescriptionInfinitiv(final Person person, final Numerus numerus) {
        return objekt.im(kasusOderPraepositionalkasus) +
                " " + verb.getInfinitiv();
    }

    /**
     * Gibt eine Infinitivkonstruktion zurück mit Prädikat.
     * ("den Frosch zu ignorieren", "das Leben zu genießen")
     */
    @Override
    public String getDescriptionZuInfinitiv(final Person person, final Numerus numerus) {
        return getDescriptionZuInfinitiv(person, numerus, null);
    }

    /**
     * Gibt eine Infinitivkonstruktion zurück mit Prädikat.
     * ("den Frosch erneut zu ignorieren", "das Leben zu genießen")
     */
    @Override
    public String getDescriptionZuInfinitiv(final Person person, final Numerus numerus,
                                            @Nullable final AdverbialeAngabe adverbialeAngabe) {
        return joinToNull(objekt.im(kasusOderPraepositionalkasus),
                adverbialeAngabe,
                verb.getZuInfinitiv());
    }

    @NonNull
    public KasusOderPraepositionalkasus getKasusOderPraepositionalkasus() {
        return kasusOderPraepositionalkasus;
    }

    public SubstantivischePhrase getObjekt() {
        return objekt;
    }
}
