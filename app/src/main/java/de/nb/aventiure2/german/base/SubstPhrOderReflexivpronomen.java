package de.nb.aventiure2.german.base;

import androidx.annotation.Nullable;

public interface SubstPhrOderReflexivpronomen {
    SubstPhrOderReflexivpronomen ohneFokuspartikel();

    @Nullable
    String getFokuspartikel();

    String imStr(Kasus kasus);

    Konstituente imK(Kasus kasus);

    @Nullable
    NumerusGenus kannAlsBezugsobjektVerstandenWerdenFuer();

    @Nullable
    IBezugsobjekt getBezugsobjekt();

    boolean isUnbetontesPronomen();
}
