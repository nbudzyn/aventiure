package de.nb.aventiure2.german.base;

import androidx.annotation.Nullable;

import static de.nb.aventiure2.german.base.Artikel.Typ.DEF;
import static de.nb.aventiure2.german.base.Artikel.Typ.INDEF;
import static de.nb.aventiure2.german.base.Flexionsreihe.fr;
import static de.nb.aventiure2.german.base.GermanUtil.joinToNullString;
import static de.nb.aventiure2.german.base.NumerusGenus.F;
import static de.nb.aventiure2.german.base.NumerusGenus.N;
import static de.nb.aventiure2.german.base.NumerusGenus.PL_MFN;
import static de.nb.aventiure2.german.base.Person.P3;

/**
 * Eine Nominalphrase, z.B. "ein dicker, hässlicher Frosch".
 */
public class Nominalphrase
        extends SubstantivischePhrase
        implements IErlaubtAttribute {
    @Nullable
    private final Artikel.Typ artikelTyp;

    private final Flexionsreihe flexionsreiheArtikellos;

    // Allgemeine Nominalphrasen, die sich nicht auf ein
    // AvObject oder eine AbstractEntity beziehen.
    public static final Nominalphrase ANGEBOTE =
            np(PL_MFN, INDEF, "Angebote", "Angeboten");
    public static final Nominalphrase ASTGABEL =
            np(F, DEF, "Astgabel");
    public static final Nominalphrase DEIN_HERZ =
            np(N, null, "dein Herz",
                    "deinem Herzen");
    public static final Nominalphrase DINGE =
            np(PL_MFN, DEF, "Dinge", "Dingen");
    public static final Nominalphrase EIN_GESPRAECH =
            np(N, INDEF, "Gespräch");
    public static final Nominalphrase FREUDE_OHNE_ART =
            np(F, null, "FREUDE");
    public static final Nominalphrase GESPRAECH =
            np(N, DEF, "Gespräch");
    public static final Nominalphrase IHRE_HAARE =
            np(PL_MFN, null, "ihre Haare",
                    "ihren Haaren");
    public static final Nominalphrase WUT_OHNE_ART =
            np(F, null, "Wut");

    public static Nominalphrase np(final NumerusGenus numerusGenus,
                                   @Nullable final Artikel.Typ artikelTyp,
                                   final String nominalNominativDativUndAkkusativ) {
        return np(numerusGenus, artikelTyp,
                nominalNominativDativUndAkkusativ, nominalNominativDativUndAkkusativ);
    }

    public static Nominalphrase np(final NumerusGenus numerusGenus,
                                   @Nullable final Artikel.Typ artikelTyp,
                                   final String nominalNominativUndAkkusativ,
                                   final String nominalDativ) {
        return np(numerusGenus, artikelTyp,
                nominalNominativUndAkkusativ, nominalDativ, nominalNominativUndAkkusativ);
    }

    public static Nominalphrase np(final NumerusGenus numerusGenus,
                                   @Nullable final Artikel.Typ artikelTyp,
                                   final String nominalNominativ,
                                   final String nominalDativ,
                                   final String nominalAkkusativ) {
        return np(numerusGenus, artikelTyp,
                fr(nominalNominativ, nominalDativ, nominalAkkusativ));
    }

    public static Nominalphrase np(final NumerusGenus numerusGenus,
                                   @Nullable final Artikel.Typ artikelTyp,
                                   final Flexionsreihe flexionsreiheArtikellos) {
        return new Nominalphrase(numerusGenus, artikelTyp, flexionsreiheArtikellos);
    }

    public Nominalphrase(final NumerusGenus numerusGenus,
                         @Nullable final Artikel.Typ artikelTyp,
                         final Flexionsreihe flexionsreiheArtikellos) {
        super(numerusGenus);
        this.artikelTyp = artikelTyp;
        this.flexionsreiheArtikellos = flexionsreiheArtikellos;
    }

    @Override
    public boolean erlaubtVerschmelzungVonPraepositionMitArtikel() {
        if (artikelTyp == null) {
            return false;
        }

        return artikelTyp.erlaubtVerschmelzungMitPraeposition();
    }

    @Override
    public String nom() {
        @Nullable final Artikel artikel = getArtikel();

        if (artikel == null) {
            return flexionsreiheArtikellos.nom();
        }

        return joinToNullString(artikel.nom(), flexionsreiheArtikellos.nom());
    }

    @Override
    public String dat() {
        @Nullable final Artikel artikel = getArtikel();

        if (artikel == null) {
            return artikellosDat();
        }

        return joinToNullString(artikel.dat(), flexionsreiheArtikellos.dat());
    }

    @Override
    public String artikellosDat() {
        return flexionsreiheArtikellos.dat();
    }

    @Override
    public String akk() {
        @Nullable final Artikel artikel = getArtikel();

        if (artikel == null) {
            return flexionsreiheArtikellos.akk();
        }

        return joinToNullString(artikel.akk(), flexionsreiheArtikellos.akk());
    }

    @Nullable
    public Artikel getArtikel() {
        return Artikel.get(artikelTyp, getNumerusGenus());
    }

    @Override
    public Personalpronomen persPron() {
        return Personalpronomen.get(P3, getNumerusGenus());
    }

    @Override
    public Reflexivpronomen reflPron() {
        return Reflexivpronomen.get(P3, getNumerusGenus().getNumerus());
    }

    @Override
    public Possessivartikel possArt() {
        return Possessivartikel.get(P3, getNumerusGenus());
    }

    @Override
    public Relativpronomen relPron() {
        return Relativpronomen.get(P3, getNumerusGenus());
    }

    @Override
    public boolean isUnbetontesPronomen() {
        return false;
    }

    @Override
    public Person getPerson() {
        return P3;
    }
}
