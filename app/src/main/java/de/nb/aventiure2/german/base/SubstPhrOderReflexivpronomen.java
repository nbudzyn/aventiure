package de.nb.aventiure2.german.base;

import androidx.annotation.Nullable;

public interface SubstPhrOderReflexivpronomen {
    String imStr(Kasus kasus);

    Konstituente imK(Kasus kasus);

    @Nullable
    NumerusGenus kannAlsBezugsobjektVerstandenWerdenFuer();

    @Nullable
    IBezugsobjekt getBezugsobjekt();

    boolean isUnbetontesPronomen();
}
