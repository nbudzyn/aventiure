package de.nb.aventiure2.data.world.syscomp.storingplace;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.AbstractStatelessComponent;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.base.SpatialConnectionData;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;

/**
 * Component für ein {@link GameObject}: Das Game Object
 * (z.B. ein Raum) bietet die Möglichkeit, etwas abzulegen - und zwar an einem bestimmten Ort
 * (z.B. "auf dem Boden" oder "auf einem Tisch").
 */
public class StoringPlaceComp extends AbstractStatelessComponent {
    public static Supplier<Boolean> LEUCHTET_IMMER = () -> true;
    public static Supplier<Boolean> LEUCHTET_NIE = () -> false;

    private final TimeTaker timeTaker;
    private final World world;
    /**
     * Viele {@link ILocationGO}s können sich selbst wieder an einem Platz
     * befinden (z.B. ein Bett, in das man sich legen kann, steht selbst wieder
     * in einer Hütte); in diesem Fall ist hier die {@link LocationComp}
     * angegeben, die das Enthalten-sein beschreibt.
     * <p>
     * Viele Konzepte sind rekursiv - z.B. entscheidet die Komponente entweder selbst über ihre
     * Lichtverhältnisse - oder sie ist durchsichtig, und die übergeordnete Komponente
     * entscheidet über die Lichtverhältnisse.
     */
    @Nullable
    private final LocationComp locationComp;

    private final StoringPlaceType locationMode;

    @Nullable
    private final SpatialConnectionData spatialConnectionInData;
    @Nullable
    private final SpatialConnectionData spatialConnectionOutData;

    /**
     * Ob die Location <i>niedrig</i> ist, so dass man z.B. hier keine Kugel hochwerfen kann
     */
    private final boolean niedrig;

    /**
     * In wieweit das {@link ILocationGO} offen oder geschlossen ist. Besonders relevant für die
     * Ermittlung der Lichtverhältnisse, wie sehr der SC der Witterung ausgesetzt ist
     * (ist der SC drinnen oder draußen?) etc.
     */
    private final Geschlossenheit geschlossenheit;

    /**
     * Ermittelt, ob dieses Game Object (z.B. dieser Raum oder diese Tasche) leuchtet.
     * Beispielsweise "leuchtet" ein Raum vielleicht immmer (d.h. er ist viellicht
     * immer beleuchtet) und eine Tasche leuchtet nie, und eine Fackel leuchtet, bis sie
     * abgebrannt ist.
     */
    private final Supplier<Boolean> leuchtetErmittler;

    /**
     * Erzeugt eine Komponente, die nicht selbst über ihre Lichtverhältnisse bestimmt.
     */
    public StoringPlaceComp(final GameObjectId id,
                            final TimeTaker timeTaker,
                            final World world,
                            @Nullable final LocationComp locationComp,
                            final StoringPlaceType locationMode,
                            final boolean niedrig,
                            final Geschlossenheit geschlossenheit) {
        this(id, timeTaker, world, locationComp, locationMode, niedrig,
                geschlossenheit, LEUCHTET_NIE);
    }

    public StoringPlaceComp(final GameObjectId id,
                            final TimeTaker timeTaker,
                            final World world,
                            @Nullable final LocationComp locationComp,
                            final StoringPlaceType locationMode,
                            final boolean niedrig,
                            final Geschlossenheit geschlossenheit,
                            final Supplier<Boolean> leuchtetErmittler) {
        this(id, timeTaker, world, locationComp, locationMode, niedrig,
                geschlossenheit,
                leuchtetErmittler,
                null, null);
    }

    public StoringPlaceComp(final GameObjectId id,
                            final TimeTaker timeTaker,
                            final World world,
                            @Nullable final LocationComp locationComp,
                            final StoringPlaceType locationMode,
                            final boolean niedrig,
                            final Geschlossenheit geschlossenheit,
                            final Supplier<Boolean> leuchtetErmittler,
                            @Nullable final SpatialConnectionData spatialConnectionInData,
                            @Nullable final SpatialConnectionData spatialConnectionOutData) {
        super(id);
        this.timeTaker = timeTaker;
        this.world = world;
        this.locationComp = locationComp;
        this.locationMode = locationMode;
        this.niedrig = niedrig;
        this.geschlossenheit = geschlossenheit;
        this.leuchtetErmittler = leuchtetErmittler;
        this.spatialConnectionInData = spatialConnectionInData;
        this.spatialConnectionOutData = spatialConnectionOutData;
    }

    public StoringPlaceType getLocationMode() {
        return locationMode;
    }

    public boolean isNiedrig() {
        return niedrig;
    }

    /**
     * Gibt die Lichtverhältnisse an diesem Ort zurück (z.B. in diesem Raum,
     * auf diesem Tisch, in der Tasche o.Ä.)
     */
    public Lichtverhaeltnisse getLichtverhaeltnisse() {
        if (leuchtet()) {
            return Lichtverhaeltnisse.HELL;
        }

        final StoringPlaceComp outerMostStoringPlaceCompAusDerNochLichtScheinenKoennte =
                getOuterMostStoringPlaceCompAusDerNochLichtScheinenKoennte();

        if (!getGameObjectId().equals(
                outerMostStoringPlaceCompAusDerNochLichtScheinenKoennte.getGameObjectId())
                && outerMostStoringPlaceCompAusDerNochLichtScheinenKoennte.leuchtet()) {
            // Die äußerste StoringComp, aus der noch Licht scheinen kann,
            // leuchtet.
            return Lichtverhaeltnisse.HELL;
        }

        final boolean draussen = outerMostStoringPlaceCompAusDerNochLichtScheinenKoennte
                .manKannHineinsehenUndLichtScheintHineinUndHinaus();
        if ( // Die outermost Location, aus der noch Licht in diese
            // StoringPlaceComp hineinscheint, ist draußen...
                draussen
                        //...und draußen ist es hell
                        && timeTaker.now().getTageszeit()
                        .getLichtverhaeltnisseDraussen() == Lichtverhaeltnisse.HELL) {
            return Lichtverhaeltnisse.HELL;
        }

        // Der SC oder ein anderes Game Object, das sich  "IN" DIESER LOCATION BEFINDET,
        // könnte eine Lichtquelle (Fackel) dabei haben.
        if (world.inventoryErleuchtetLocation(
                outerMostStoringPlaceCompAusDerNochLichtScheinenKoennte.getGameObjectId())) {
            return Lichtverhaeltnisse.HELL;
        }

        return Lichtverhaeltnisse.DUNKEL;
    }

    public boolean manKannHineinsehenUndLichtScheintHineinUndHinaus() {
        return geschlossenheit.manKannHineinsehenUndLichtScheintHineinUndHinaus();
    }

    public Boolean leuchtet() {
        return leuchtetErmittler.get();
    }

    /**
     * Ermittelt, wie sehr ein {@link ILocationGO}
     * drinnen oder draußen ist.
     */
    public DrinnenDraussen getDrinnenDraussen() {
        DrinnenDraussen res = DrinnenDraussen.DRAUSSEN_UNTER_OFFENEM_HIMMEL;
        @Nullable StoringPlaceComp tmpStoringPlaceComp = this;

        while (tmpStoringPlaceComp != null) {
            switch (tmpStoringPlaceComp.geschlossenheit) {
                case NACH_OBEN_WEITGEHEND_OFFEN_UND_UNGESCHUETZT:
                    break;
                case MAN_KANN_HINEINSEHEN_UND_LICHT_SCHEINT_HINEIN_UND_HINAUS:
                    res = DrinnenDraussen.DRAUSSEN_GESCHUETZT;
                    break;
                case MAN_KANN_NICHT_DIREKT_HINEINSEHEN_UND_LICHT_SCHEINT_NICHT_HINEIN_ODER_HINAUS:
                    return DrinnenDraussen.DRINNEN;
            }

            tmpStoringPlaceComp = tmpStoringPlaceComp.getOuterStoringPlaceComp();

            if (tmpStoringPlaceComp == null) {
                return res; // ==>
            }
        }

        return res;
    }

    /**
     * Ermittelt die äußerste {@code StoringPlaceComp}, aus der noch Licht in diese
     * {@code StoringPlaceComp} hineinscheinen könnte.
     */
    private StoringPlaceComp getOuterMostStoringPlaceCompAusDerNochLichtScheinenKoennte() {
        StoringPlaceComp res = this;

        while (res.manKannHineinsehenUndLichtScheintHineinUndHinaus()) {
            @Nullable final StoringPlaceComp outer = res.getOuterStoringPlaceComp();

            if (outer == null) {
                return res; // ==>
            }

            res = outer;
        }

        return res;
    }

    @Nullable
    public SpatialConnectionData getSpatialConnectionInData() {
        return spatialConnectionInData;
    }

    @Nullable
    public SpatialConnectionData getSpatialConnectionOutData() {
        return spatialConnectionOutData;
    }

    /**
     * Viele {@link ILocationGO}s befinden sich selbst wieder an einem Platz (z.B. steht ein Bett,
     * in das man sich legen kann, steht selbst wieder in einer Hütte); in diesem Fall
     * gibt diese Methode die <code>StoringPlaceComp</code> dieses ("äußeren") Platzes
     * zurück.
     * <p>
     * Hiermit lassen sich rekursive Konzepte einfach realisieren:
     * Eine <code>StoringPlaceComp</code> entscheidet z.B. nicht selbst über ihre
     * Lichtverhältnisse, sondern lässt das äußere Licht durch, lässt also ihre
     * "äußere" <code>StoringPlaceComp</code> über die Lichtverhältnisse entscheiden.
     */
    @Nullable
    private StoringPlaceComp getOuterStoringPlaceComp() {
        if (locationComp == null) {
            return null;
        }

        @Nullable final ILocationGO location = locationComp.getLocation();

        if (location == null) {
            return null;
        }

        return location.storingPlaceComp();
    }
}
