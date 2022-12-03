package de.nb.aventiure2.german.base;

import androidx.annotation.Nullable;

public interface SubstPhrOderReflexivpronomen extends UmstellbarePhrase {
    SubstPhrOderReflexivpronomen ohneFokuspartikel();

    @Nullable
    String getFokuspartikel();

    SubstPhrOderReflexivpronomen ohneNegationspartikelphrase();

    @Nullable
    Negationspartikelphrase getNegationspartikelphrase();

    String imStr(Kasus kasus);

    Konstituentenfolge imK(Kasus kasus);

    @Nullable
    default PhorikKandidat getPhorikKandidat() {
        @Nullable final IBezugsobjekt bezugsobjekt = getBezugsobjekt();

        if (bezugsobjekt == null) {
            return null;
        }

        @Nullable final NumerusGenus numerusGenus = kannAlsBezugsobjektVerstandenWerdenFuer();
        if (numerusGenus == null) {
            // Kann das überhaupt sein? - Egal.
            return null;
        }

        return new PhorikKandidat(numerusGenus, getBelebtheit(), bezugsobjekt);
    }

    @Nullable
    NumerusGenus kannAlsBezugsobjektVerstandenWerdenFuer();

    Belebtheit getBelebtheit();

    @Nullable
    IBezugsobjekt getBezugsobjekt();

    boolean isUnbetontesPronomen();
}
