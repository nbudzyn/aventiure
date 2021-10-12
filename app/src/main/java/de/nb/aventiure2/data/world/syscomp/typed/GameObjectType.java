package de.nb.aventiure2.data.world.syscomp.typed;

/**
 * Typ eines {@link ITypedGO}.
 * Der Typ erlaubt es, alle Objekte eines Typs zu finden. Typen sind hauptsächlich
 * für Objekte relevant, die on-the-fly erzeugt werden - wo es also mehrere
 * Objekte <i>desselben Typs</i> gibt.
 */
public enum GameObjectType {
    AUSGERUPFTE_BINSEN,
    /**
     * Objekt-Typ für lange Binsenseile. Der SC kann mehrere lange Binsenseile flechten und sie
     * an denselben Ort legen. Dann liegen dort "zwei lange Binsenseile" o.Ä.
     */
    LANGES_BINSENSEIL
}
