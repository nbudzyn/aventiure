package de.nb.aventiure2.german.base;

import androidx.annotation.Nullable;

public interface SubstPhrOderReflexivpronomen {
    SubstPhrOderReflexivpronomen ohneFokuspartikel();

    @Nullable
    String getFokuspartikel();

    SubstPhrOderReflexivpronomen ohneNegationspartikelphrase();

    @Nullable
    Negationspartikelphrase getNegationspartikelphrase();

    String imStr(Kasus kasus);

    Konstituentenfolge imK(Kasus kasus);

    @Nullable
    NumerusGenus kannAlsBezugsobjektVerstandenWerdenFuer();

    @Nullable
    IBezugsobjekt getBezugsobjekt();

    boolean isUnbetontesPronomen();
}
