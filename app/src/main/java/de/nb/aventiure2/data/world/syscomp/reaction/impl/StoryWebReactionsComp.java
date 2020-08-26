package de.nb.aventiure2.data.world.syscomp.reaction.impl;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.syscomp.reaction.AbstractReactionsComp;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.ISCActionReactions;
import de.nb.aventiure2.data.world.time.AvTimeSpan;

import static de.nb.aventiure2.data.world.gameobject.World.STORY_WEB;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;
import static de.nb.aventiure2.german.base.AllgDescription.neuerSatz;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;

/**
 * Reagiert auf die Aktionen des SCs und managet dabei die Stories, d.h. die kleinen
 * Geschichten / Märchen, die der Spieler erlebt. Im Wesentlichen gibt es drei Reaktionen:
 * <ul>
 * <li>Der Spieler erhält einen Tipp.
 * <li>Eine neue Story (ein neues Märchen) wird begonnen.
 * <li>(Es passiert nichts.)
 * </ul>
 */
public class StoryWebReactionsComp
        extends AbstractReactionsComp
        implements ISCActionReactions {
    public StoryWebReactionsComp(final AvDatabase db, final World world) {
        super(STORY_WEB, db, world);
    }

    @Override
    public AvTimeSpan afterScActionAndFirstWorldUpdate() {
        // STORY Nur wenn der Benutzer länger nicht weiterkommt (länger kein
        //  neuer Geschichtsschritt erreicht) werden Sätze erzeugt wie
        //  "Wann soll eigentlich das Schlossfest sein?",
        //  "Vielleicht hättest du doch die Kugel mitnehmen sollen?" o.Ä.
        //  Als Tipp für den Froschprinzen z.B. durch einen NSC ankündigen lassen: Im Königreich
        //  nebenan ist der Prinz
        //  verschwunden.
        //  Tipp für Rapunzel: Mutter sammelt im Wald Holz und klagt ihr Leid.
        //  Tipps könnten von den Geschichtsmeilensteinen generiert werden, die
        //  noch nicht erreicht, deren Voraussetzungen jedoch bereits gegeben sind.
        //  (Jeder Geschichtsmeilenstein könnte mehrere Hinweise erzeugen, aus denen
        //  der Narrator auswählen könnte.)
        //  Jeder Geschichtsmeilenstein hat eine Anzahl von Schritten, in denen er
        //  erreicht sein sollte. Ein Tipp würde nur generiert, wenn es Geschichtsmeilensteine
        //  gibt, deren Voraussetzungen gegeben sind und deren Schrittzahl überschritten ist.
        //  Tipps sollten zum aktuellen (oder zu einem nahen) Raum passen (ein Geschichtsmeilenstein
        //  könnte optional einen Lieblingsraum haben).
        //  Statt eines Tipps könnte auch eine neue Story starten.

        return n.add(neuerSatz(SENTENCE,
                "Du hast das Gefühl, es gibt noch viel zu erleben",
                noTime()));
    }
}
