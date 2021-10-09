package de.nb.aventiure2.german.base;

import static de.nb.aventiure2.german.base.Kasus.AKK;
import static de.nb.aventiure2.german.base.Kasus.DAT;
import static de.nb.aventiure2.german.base.Kasus.NOM;
import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToKonstituentenfolge;
import static de.nb.aventiure2.german.base.Person.P3;

import androidx.annotation.Nullable;

import javax.annotation.CheckReturnValue;

/**
 * Eine Phrase, die substantivisch verwendet werden kann, also insbesondere ein Pronomen ("sie",
 * "du", "wir") oder eine (andere) Nominalphrase ("die goldene Kugel") oder eine
 * Reihung ("du und der Frosch").
 */
public interface SubstantivischePhrase
        extends DeklinierbarePhrase, SubstPhrOderReflexivpronomen, Praedikativum {
    @Override
    SubstantivischePhrase ohneNegationspartikelphrase();

    /**
     * Verknüpft die Substantivische Phrase mit dieser Negation - verwendet dabei
     * nach Möglichkeit negativ-indefinite Wörter:
     * <ul>
     * <li>"der Mörder" -> "nicht der Mörder"
     * <li>"ein Mörder" -> "kein Mörder"
     * <li>"ein Verdächtiger" -> "schon lange kein Verdächtiger mehr"
     * </ul>
     *
     * @param negationspartikelphrase Die Negationspartikelphrase: "nicht",
     *                                "noch nicht",
     *                                "nicht mehr", "längst nicht mehr" o.Ä.
     */
    default SubstantivischePhrase neg(final Negationspartikelphrase negationspartikelphrase) {
        return neg(negationspartikelphrase, true);
    }

    /**
     * Verknüpft die Substantivische Phrase mit dieser Negation:
     * <ul>
     * <li>"der Mörder" -> "nicht der Mörder"
     * <li>"ein Mörder" -> "nicht ein Mörder" / "kein Mörder"
     * <li>"ein Verdächtiger" ->  "schon lange nicht mehr Verdächtiger" / "schon lange kein
     * Verdächtiger mehr"
     * </ul>
     *
     * @param negationspartikelphrase                     Die Negationspartikelphrase: "nicht",
     *                                                    "noch nicht",
     *                                                    "nicht mehr", "längst nicht mehr" o.Ä.
     * @param moeglichstNegativIndefiniteWoerterVerwenden Ob statt "nicht ..." möglichst
     *                                                    negativ-indefinitve Wörter verwendet
     *                                                    werden sollen, z.B. "kein...",
     *                                                    "niemand" etc.
     */
    SubstantivischePhrase neg(Negationspartikelphrase negationspartikelphrase,
                              boolean moeglichstNegativIndefiniteWoerterVerwenden);

    @Override
    SubstantivischePhrase ohneFokuspartikel();

    /**
     * Fügt der substantivischen Phrase etwas hinzu wie "auch", "allein", "ausgerechnet",
     * "wenigstens" etc. (sofern die Phrase eine Fokuspartikel erlaubt, ansonsten
     * wird die Partikel verworfen)
     */
    SubstantivischePhrase mitFokuspartikel(@Nullable final String fokuspartikel);

    @Override
    default Konstituentenfolge getPraedikativ(final Person person, final Numerus numerus,
                                              @Nullable
                                              final Negationspartikelphrase negationspartikel) {
        if (negationspartikel == null) {
            return getPraedikativ(person, numerus);
        }

        return neg(negationspartikel).getPraedikativ(person, numerus);
    }

    @Override
    @CheckReturnValue
    default Konstituentenfolge getPraedikativ(final Person person, final Numerus numerus) {
        // Einen Bezug auf ein Prädikatsnomen kann es wohl nicht geben:
        // *"Petra ist Professor. Er ..."
        // Also Bezug entfernen!
        return joinToKonstituentenfolge(
                nomK().joinToSingleKonstituente()
                        .withBezugsobjektUndKannVerstandenWerdenAls(
                                null, null));
    }

    @Override
    @Nullable
    default Konstituentenfolge getPraedikativAnteilKandidatFuerNachfeld(final Person person,
                                                                        final Numerus numerus) {
        return null;
    }

    /**
     * Ob die substantivische Phase mit einem Artikel beginnt, der mit einer
     * dazu geeigneten Präposition verschmolzen werden darf ("dem Haus" -> "zum Haus")
     * oder nicht ("einem Haus", "dem Haus zugewandte Straßenlaternen")
     */
    boolean erlaubtVerschmelzungVonPraepositionMitArtikel();

    /**
     * Gibt die substantivische Phrase im Dativ, aber ohne Artikel, zurück
     * ("(zum) Haus") - als Konstituentenfolge
     */
    Konstituentenfolge artikellosDatK();

    /**
     * Gibt die substantivische Phrase im Akkusativ, aber ohne Artikel, zurück
     * ("(ins) Haus") - als Konstituentenfolge
     */
    Konstituentenfolge artikellosAkkK();

    @Nullable
    @Override
    default NumerusGenus kannAlsBezugsobjektVerstandenWerdenFuer() {
        if (getPerson() != P3) {
            return null;
        }

        return getNumerusGenus();
    }

    /**
     * Gibt die substantivische Phrase im Dativ, aber ohne Artikel, zurück
     * ("(zum) Haus") - als String
     */
    String artikellosDatStr();


    /**
     * Gibt die substantivische Phrase im Akkusativ, aber ohne Artikel, zurück
     * ("(ins) Haus") - als String
     */
    String artikellosAkkStr();

    default String imStr(final KasusOderPraepositionalkasus kasusOderPraepositionalkasus) {
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
                    .getText();
        }

        throw new IllegalArgumentException("Unexpected Kasus or Präpositionalkasus: " +
                kasusOderPraepositionalkasus);
    }

    @Override
    default String imStr(final Kasus kasus) {
        return DeklinierbarePhrase.super.imStr(kasus);
    }

    default Konstituentenfolge nomK() {
        return imK(NOM);
    }

    default Konstituentenfolge datK() {
        return imK(DAT);
    }

    default Konstituentenfolge akkK() {
        return imK(AKK);
    }

    @Override
    default Konstituentenfolge imK(final Kasus kasus) {
        return imK((KasusOderPraepositionalkasus) kasus);
    }

    Konstituentenfolge imK(
            KasusOderPraepositionalkasus kasusOderPraepositionalkasus);

    /**
     * Gibt ein Personalpronomen für diese Phrase zurück.
     */
    Personalpronomen persPron();

    /**
     * Gibt ein {@link Reflexivpronomen} für diese Phrase zurück.
     */
    Reflexivpronomen reflPron();

    /**
     * Gibt einen Possessivartikel für diese Phrase zurück.
     */
    ArtikelwortFlexionsspalte.Typ possArt();

    /**
     * Gibt ein Relativpronomen für diese Phrase zurück.
     */
    Relativpronomen relPron();

    default Numerus getNumerus() {
        return getNumerusGenus().getNumerus();
    }

    NumerusGenus getNumerusGenus();

    Person getPerson();
}
