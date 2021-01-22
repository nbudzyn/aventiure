package de.nb.aventiure2.german.base;

import androidx.annotation.Nullable;

import java.util.Objects;

import static de.nb.aventiure2.german.base.Kasus.AKK;
import static de.nb.aventiure2.german.base.Kasus.DAT;
import static de.nb.aventiure2.german.base.Kasus.NOM;
import static de.nb.aventiure2.german.base.Konstituente.k;
import static de.nb.aventiure2.german.base.Person.P3;

/**
 * Eine Phrase, die substantivisch verwendet werden kann, also insbesondere ein Pronomen ("sie",
 * "du", "wir") oder eine (andere) Nominalphrase ("die goldene Kugel").
 */
public abstract class SubstantivischePhrase
        implements DeklinierbarePhrase, SubstPhrOderReflexivpronomen, Praedikativum {
    private final NumerusGenus numerusGenus;

    /**
     * Eine Person, ein Gegenstand, ein Konzept o.Ä., auf das sich diese substantivische
     * Phrase bezieht.
     */
    @Nullable
    private final IBezugsobjekt bezugsobjekt;

    public SubstantivischePhrase(final NumerusGenus numerusGenus,
                                 @Nullable final IBezugsobjekt bezugsobjekt) {
        this.numerusGenus = numerusGenus;
        this.bezugsobjekt = bezugsobjekt;
    }

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
     * ("(zum) Haus") - als Konstituente
     */
    Konstituente artikellosDatK() {
        return k(artikellosDatStr(), getNumerusGenus(), getBezugsobjekt());
    }

    /**
     * Gibt die substantivische Phrase im Dativ, aber ohne Artikel, zurück
     * ("(zum) Haus") - als String
     */
    public abstract String artikellosDatStr();

    private String imStr(final KasusOderPraepositionalkasus kasusOderPraepositionalkasus) {
        if (kasusOderPraepositionalkasus instanceof Kasus) {
            return imStr((Kasus) kasusOderPraepositionalkasus);
        }

        if (kasusOderPraepositionalkasus instanceof PraepositionMitKasus) {
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

    public Konstituente nomK() {
        return imK(NOM);
    }

    public Konstituente datK() {
        return imK(DAT);
    }

    public Konstituente akkK() {
        return imK(AKK);
    }

    public Konstituente imK(final KasusOderPraepositionalkasus kasusOderPraepositionalkasus) {
        return k(imStr(kasusOderPraepositionalkasus), getNumerusGenus(), getBezugsobjekt());
    }

    @Override
    public Konstituente imK(final Kasus kasus) {
        return k(imStr(kasus), kannAlsBezugsobjektVerstandenWerdenFuer(), getBezugsobjekt());
    }

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

    @Nullable
    @Override
    public NumerusGenus kannAlsBezugsobjektVerstandenWerdenFuer() {
        if (getPerson() != P3) {
            return null;
        }

        return numerusGenus;
    }

    @Override
    @Nullable
    public IBezugsobjekt getBezugsobjekt() {
        return bezugsobjekt;
    }

    public Numerus getNumerus() {
        return getNumerusGenus().getNumerus();
    }

    public NumerusGenus getNumerusGenus() {
        return numerusGenus;
    }

    public abstract Person getPerson();

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SubstantivischePhrase that = (SubstantivischePhrase) o;
        return numerusGenus == that.numerusGenus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(numerusGenus);
    }
}
