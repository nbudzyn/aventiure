package de.nb.aventiure2.data.world.base;

/**
 * Kontext einer Komponente - letztlich das Game Object, das diese Komponente enthält.
 * Über diesen Kontext kann eine Komponente mit ihrem Game Object
 * interagieren und z.B. "Nachrichten schicken" oder "Fragen stellen",
 * die ggf. auch andere Components des Game Objects betreffen können.
 *
 * @see de.nb.aventiure2.data.world.base.IComponent#setContext(ComponentContext)
 */
public interface ComponentContext {
    // TODO Verwenden oder löschen
}
