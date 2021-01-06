package de.nb.aventiure2.german.base;

public interface SubstantivischePhraseOderReflexivpronomen {
    String imStr(Kasus kasus);

    Konstituente imK(Kasus kasus);

    boolean isUnbetontesPronomen();
}
