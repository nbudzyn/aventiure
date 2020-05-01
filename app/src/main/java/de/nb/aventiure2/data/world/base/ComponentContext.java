package de.nb.aventiure2.data.world.base;

import de.nb.aventiure2.data.world.syscomp.storingplace.Lichtverhaeltnisse;

/**
 * Kontext einer Komponente - letztlich das Game Object, das diese Komponente enthält.
 * Über diesen Kontext kann eine Komponente mit ihrem Game Object
 * interagieren und z.B. "Nachrichten schicken" oder "Fragen stellen",
 * die ggf. auch andere Components des Game Objects betreffen können.
 *
 * @see de.nb.aventiure2.data.world.base.IComponent#setContext(ComponentContext)
 */
public interface ComponentContext {
    Lichtverhaeltnisse getLichtverhaeltnisseInside();
}
