package de.nb.aventiure2.data.world.syscomp.reaction.impl;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.NarrationDao;
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.time.AvTimeSpan;

import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;
import static de.nb.aventiure2.german.base.AllgDescription.neuerSatz;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;

/**
 * Ein einzelner Schritt, der im Rahmen einer Story (d.h. eines Mörchens o.Ä.)
 * erreicht werden kann.
 */
class StoryNode {
    protected final AvDatabase db;
    protected final World world;
    protected final NarrationDao n;

    // STORY Jede StoryNode gehört zu einer Story.
    //  Wenn eine StoryNode erreicht wird, wird "markiert", dass die StoryNode erreicht wurde,
    //  mit Schrittzähler.
    //  Jede StoryNode kann Voraussetzungen haben. Voraussetzungen sind andere
    //  StoryNodes - oder alternativ (oder zusätzlich) könnten die Voraussetzungen für
    //  StoryNodes auch beliebige Prüfungen sein.

    // TODO Es könnte einen extra Knopf geben, unter dem der benutzer in einem Baum (oder
    //  einem Graphen) sieht,
    //  welche StoryNodes er bisher erreicht hat. Die baumstruktur ordnet sich nach den
    //  Voraussetzungen der StoryNodes voneinander.

    // TODO Das Programm kann oben in der leiste jederzeit einen prozentsatz anzeigen, wie
    //  viele StoryNodes der benutzer schon erreicht hat.

    public StoryNode(final AvDatabase db,
                     final World world) {
        this.db = db;
        this.world = world;

        n = db.narrationDao();
    }

    public AvTimeSpan narrateAndDoWhileOpen() {
        // STORY Nur wenn der Benutzer länger nicht weiterkommt (länger kein
        //  neuer StoryNode erreicht) werden Sätze erzeugt wie
        //  "Wann soll eigentlich das Schlossfest sein?",
        //  "Vielleicht hättest du doch die Kugel mitnehmen sollen?" o.Ä.
        //  Als Tipp für den Froschprinzen z.B. durch einen NSC ankündigen lassen: Im Königreich
        //  nebenan ist der Prinz
        //  verschwunden.
        //  Tipp für Rapunzel: Mutter sammelt im Wald Holz und klagt ihr Leid.
        //  Tipps könnten von den StoryNodes generiert werden, die
        //  noch nicht erreicht, deren Voraussetzungen jedoch bereits gegeben sind.
        //  (Jede StoryNode könnte mehrere Hinweise erzeugen, aus denen
        //  der Narrator auswählen könnte.)
        //  Jede StoryNode hat eine Anzahl von Schritten, in denen sie
        //  erreicht sein sollte. Ein Tipp würde nur generiert, wenn es StoryNodes
        //  gibt, deren Voraussetzungen gegeben sind und deren Schrittzahl überschritten ist.
        //  Tipps sollten zum aktuellen (oder zu einem nahen) Raum passen (ein StoryNode
        //  könnte optional einen Lieblingsraum haben).
        //  Statt eines Tipps könnte ein Story Node auch eine neue Story starten.

        return n.addAlt(neuerSatz(SENTENCE,
                "Du hast das Gefühl, es gibt noch viel zu erleben",
                noTime()));
    }
}
