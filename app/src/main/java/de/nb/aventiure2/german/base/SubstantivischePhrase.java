package de.nb.aventiure2.german.base;

import androidx.annotation.Nullable;

import static de.nb.aventiure2.german.base.Kasus.AKK;
import static de.nb.aventiure2.german.base.Kasus.DAT;
import static de.nb.aventiure2.german.base.Kasus.NOM;
import static de.nb.aventiure2.german.base.Konstituente.k;

/**
 * Eine Phrase, die substantivisch verwendet werden kann, also insbesondere ein Pronomen ("sie",
 * "du", "wir") oder eine (andere) Nominalphrase ("die goldene Kugel") oder eine
 * Reihung ("du und der Frosch").
 */
public abstract class SubstantivischePhrase
        implements DeklinierbarePhrase, SubstPhrOderReflexivpronomen, Praedikativum {
    @Override
    public abstract SubstantivischePhrase ohneFokuspartikel();

    /**
     * Fügt der substantivischen Phrase etwas hinzu wie "auch", "allein", "ausgerechnet",
     * "wenigstens" etc. (sofern die Phrase eine Fokuspartikel erlaubt, ansonsten
     * wird die Partikel verworfen)
     */
    public abstract SubstantivischePhrase mitFokuspartikel(@Nullable final String fokuspartikel);

    @Override
    public Konstituentenfolge getPraedikativ(final Person person, final Numerus numerus) {
        // Ein Bezug auf ein Prädikatsnomen kann es wohl nicht geben:
        // *"Petra ist Professor. Er ..."
        return new Konstituentenfolge(k(nomStr()));
    }


    @Override
    @Nullable
    public Konstituentenfolge getPraedikativAnteilKandidatFuerNachfeld(final Person person,
                                                                       final Numerus numerus) {
        return null;
    }

    /**
     * Ob die substantivische Phase mit einem Artikel beginnt, der mit einer
     * dazu geeigneten Präposition verschmolzen werden darf ("dem Haus" -> "zum Haus")
     * oder nicht ("einem Haus", "dem Haus zugewandte Straßenlaternen")
     */
    public abstract boolean erlaubtVerschmelzungVonPraepositionMitArtikel();

    /**
     * Gibt die substantivische Phrase im Dativ, aber ohne Artikel, zurück
     * ("(zum) Haus") - als Konstituentenfolge
     */
    abstract Konstituentenfolge artikellosDatK();

    /**
     * Gibt die substantivische Phrase im Dativ, aber ohne Artikel, zurück
     * ("(zum) Haus") - als String
     */
    public abstract String artikellosDatStr();

    final String imStr(final KasusOderPraepositionalkasus kasusOderPraepositionalkasus) {
        if (kasusOderPraepositionalkasus instanceof Kasus) {
            return imStr((Kasus) kasusOderPraepositionalkasus);
        }

        if (kasusOderPraepositionalkasus instanceof PraepositionMitKasus) {
            // FIXME Präpositionalkasus mit "es" sind problematisch, da "es"
            //  nicht phrasenbildend ist.
            //  - "in es" etc. wird vertreten durch "hinein", "auf es" durch "darauf" etc.
            //  - Man bräuchte wohl eine neue Klasse adverbialer Angaben
            //   wie DARAUF, DARUNTER, HINEIN etc., und jede
            //   Präposition MIT AKKUSATIV müsste zwingend
            //   eines dieser Adverbien referenzieren, das als
            //   Ersatz verwendet wird.
            //  - Dabei ändert sich vielleicht teilweise sogar die Zusammenschreibung?!
            //  ("Du willst es hineinlegen" statt *"Du willst es in es legen"?!)
            //  - Das scheint aber nicht bei belebten Dingen möglich zu sein:
            //  ?"Das ist unser Kind. Wir haben viel Geld hineingesteckt"
            //  ?"Das ist unser Kind. Wir haben einen Nachtisch dafür gekauft."

            final PraepositionMitKasus praepositionMitKasus =
                    (PraepositionMitKasus) kasusOderPraepositionalkasus;

            return praepositionMitKasus.getDescription(this)
                    // Das dürfen wir machen, weil nach der substantivischen Phrase ganz sicher
                    // kein Komma aussteht
                    .getString();
        }

        throw new IllegalArgumentException("Unexpected Kasus or Präpositionalkasus: " +
                kasusOderPraepositionalkasus);
    }

    @Override
    public String imStr(final Kasus kasus) {
        return DeklinierbarePhrase.super.imStr(kasus);
    }

    public Konstituentenfolge nomK() {
        return imK(NOM);
    }

    public Konstituentenfolge datK() {
        return imK(DAT);
    }

    public Konstituentenfolge akkK() {
        return imK(AKK);
    }

    @Override
    public final Konstituentenfolge imK(final Kasus kasus) {
        return imK((KasusOderPraepositionalkasus) kasus);
    }

    public abstract Konstituentenfolge imK(
            KasusOderPraepositionalkasus kasusOderPraepositionalkasus);

    /**
     * Gibt ein Personalpronomen für diese Phrase zurück.
     */
    public abstract Personalpronomen persPron();

    /**
     * Gibt ein {@link Reflexivpronomen} für diese Phrase zurück.
     */
    public abstract Reflexivpronomen reflPron();

    /**
     * Gibt einen Possessivartikel für diese Phrase zurück.
     */
    public abstract Possessivartikel possArt();

    /**
     * Gibt ein Relativpronomen für diese Phrase zurück.
     */
    public abstract Relativpronomen relPron();

    public abstract Numerus getNumerus();

    public abstract NumerusGenus getNumerusGenus();

    public abstract Person getPerson();
}
