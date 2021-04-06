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
     * Die "Lichtdurchlässigkeit" dieses Ablageplatzes:
     * <ul>
     * <li>Entweder man kann hineinsehen und Licht scheint hinein (wenn außen Licht
     * ist, ggf. das Tageslicht) und hinaus (falls beleuchtet, vgl.
     * {@link #leuchtetErmittler}.
     * <li>Oder man kann nicht hineinsehen, Licht scheint nicht hinein und es scheint auch
     * kein Licht heraus.
     * </ul>
     * <p>
     * {@code true} bei typischen draußen-Räumen, die von der Sonne beleuchtet werden, und bei
     * Innenräumen mit Fenstern nach draußen. {@code false} z.B. unter dem Bett oder bei
     * Gegenständen, die an sich geschlossen sind.
     */
    private final boolean manKannHineinsehenUndLichtScheintHineinUndHinaus;

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
                            final boolean manKannHineinsehenUndLichtScheintHineinUndHinaus) {
        this(id, timeTaker, world, locationComp, locationMode, niedrig,
                manKannHineinsehenUndLichtScheintHineinUndHinaus, LEUCHTET_NIE);
    }

    public StoringPlaceComp(final GameObjectId id,
                            final TimeTaker timeTaker,
                            final World world,
                            @Nullable final LocationComp locationComp,
                            final StoringPlaceType locationMode,
                            final boolean niedrig,
                            final boolean manKannHineinsehenUndLichtScheintHineinUndHinaus,
                            final Supplier<Boolean> leuchtetErmittler) {
        this(id, timeTaker, world, locationComp, locationMode, niedrig,
                manKannHineinsehenUndLichtScheintHineinUndHinaus, leuchtetErmittler,
                null, null);
    }

    public StoringPlaceComp(final GameObjectId id,
                            final TimeTaker timeTaker,
                            final World world,
                            @Nullable final LocationComp locationComp,
                            final StoringPlaceType locationMode,
                            final boolean niedrig,
                            final boolean manKannHineinsehenUndLichtScheintHineinUndHinaus,
                            final Supplier<Boolean> leuchtetErmittler,
                            @Nullable final SpatialConnectionData spatialConnectionInData,
                            @Nullable final SpatialConnectionData spatialConnectionOutData) {
        super(id);
        this.timeTaker = timeTaker;
        this.world = world;
        this.locationComp = locationComp;
        this.locationMode = locationMode;
        this.niedrig = niedrig;
        this.manKannHineinsehenUndLichtScheintHineinUndHinaus =
                manKannHineinsehenUndLichtScheintHineinUndHinaus;
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

        if ( // Die outermost Location, aus der noch Licht in diese
            // StoringPlaceComp hineinscheint, ist draußen...
                outerMostStoringPlaceCompAusDerNochLichtScheinenKoennte
                        .manKannHineinsehenUndLichtScheintHineinUndHinaus
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

        // FIXME Wetterphänomene (Regen) und der "tageszeitliche Himmel"
        //  ("du siehst ein schönes Abendrot") nur dann erzählt werden, wenn der SC
        //  "draußen" ist oder "einen Blick auf den Himmel hat". Auch diese Fragen ließen
        //  sich analog beantworten.
        //  Grundsätzlich ist "draußen" wohl nichts anderes als
        //  outerStoringPlaceComp == null
        //  und manKannHineinsehenUndLichtScheintHineinUndHinaus == true.
        //  (Siehe oben.)
        //  Denkbar wäre allerdings eine (ergänzende?) Kategorisierung wie
        //  unter offenem Himmel, draußen geschützt (z.B. Wald), untergestellt,
        //  drinnen mit Ausblick, drinnen ohne Ausblick.
        //  Man könnte vielleicht manKannHineinsehenUndLichtScheintHineinUndHinaus
        //  entsprechend ergänzen.
    }

    public boolean manKannHineinsehenUndLichtScheintHineinUndHinaus() {
        return manKannHineinsehenUndLichtScheintHineinUndHinaus;
    }

    public Boolean leuchtet() {
        return leuchtetErmittler.get();
    }

    /**
     * Ermittelt die äußerste {@code StoringPlaceComp}, aus der noch Licht in diese
     *
     * @code StoringPlaceComp} hineinscheinen könnte.
     */
    private StoringPlaceComp getOuterMostStoringPlaceCompAusDerNochLichtScheinenKoennte() {
        StoringPlaceComp res = this;

        while (res.manKannHineinsehenUndLichtScheintHineinUndHinaus) {
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
