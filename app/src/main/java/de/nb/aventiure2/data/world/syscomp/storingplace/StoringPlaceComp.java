package de.nb.aventiure2.data.world.syscomp.storingplace;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.AbstractStatelessComponent;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.base.SpatialConnectionData;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;

/**
 * Component für ein {@link GameObject}: Das Game Object
 * (z.B. ein Raum) bietet die Möglichkeit, etwas abzulegen - und zwar an einem bestimmten Ort
 * (z.B. "auf dem Boden" oder "auf einem Tisch").
 */
public class StoringPlaceComp extends AbstractStatelessComponent {
    private final TimeTaker timeTaker;
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
     * Ermittelt, ob dieses Game Object (z.B. dieser Raum oder diese Tasche) seine
     * Lichtverhältnisse eigenständig festlegt. Beispielsweise ist ein Raum viellicht
     * immer beleuchtet und eine Tasche vielleicht immer dunkel (oder dunkel, wenn
     * sie geschlossen ist und ansonsten lässt sich die Lichtverhältnisse von ihrer
     * Umgebung ermitteln).
     * <p>
     * Ist hier <code>null</code> angegeben (oder gibt der {@link Supplier}
     * null zurück), so werden die Lichtverhältnisse
     * davon bestimmt, wo sich dieses Game Object befindet - oder ggf. von der "Umwelt" /
     * Tageszeit.
     */
    @Nullable
    private final Supplier<Lichtverhaeltnisse> lichtverhaeltnisseErmittler;

    /**
     * Erzeugt eine Komponente, die nicht selbst über ihre Lichtverhältnisse bestimmt.
     */
    public StoringPlaceComp(final GameObjectId id,
                            final TimeTaker timeTaker,
                            @Nullable final LocationComp locationComp,
                            final StoringPlaceType locationMode) {
        this(id, timeTaker, locationComp, locationMode, null);
    }

    public StoringPlaceComp(final GameObjectId id,
                            final TimeTaker timeTaker,
                            @Nullable final LocationComp locationComp,
                            final StoringPlaceType locationMode,
                            final Supplier<Lichtverhaeltnisse> lichtverhaeltnisseErmittler) {
        this(id, timeTaker, locationComp, locationMode, lichtverhaeltnisseErmittler,
                null, null);
    }

    public StoringPlaceComp(final GameObjectId id,
                            final TimeTaker timeTaker,
                            @Nullable final LocationComp locationComp,
                            final StoringPlaceType locationMode,
                            @Nullable
                            final Supplier<Lichtverhaeltnisse> lichtverhaeltnisseErmittler,
                            @Nullable final SpatialConnectionData spatialConnectionInData,
                            @Nullable final SpatialConnectionData spatialConnectionOutData) {
        super(id);
        this.timeTaker = timeTaker;
        this.locationComp = locationComp;
        this.locationMode = locationMode;
        this.lichtverhaeltnisseErmittler = lichtverhaeltnisseErmittler;
        this.spatialConnectionInData = spatialConnectionInData;
        this.spatialConnectionOutData = spatialConnectionOutData;
    }


    public StoringPlaceType getLocationMode() {
        return locationMode;
    }

    /**
     * Gibt die Lichtverhältnisse an diesem Ort zurück (z.B. in diesem Raum,
     * auf diesem Tisch, in der Tasche o.Ä.)
     */
    public Lichtverhaeltnisse getLichtverhaeltnisse() {
        // IDEA Der SC oder ein anderes Game Object, das sich  "IN" DIESER LOCATION BEFINDET, könnte eine
        //  Lichtquelle (Fackel) dabei haben.
        //  Hier ist zu bedenken, dass Fackeln zwar von einem Tisch strahlen - aber nicht aus
        //  einer Kiste! Es wäre eine rekursive Prüfung über alle "enthaltenen" Objekte
        //  nötig, ob sie "ihre Lichtverhältnisse selbst bestimmen" / "leuchten" - aber nur
        //  soweit die Objekte "undurchsichtig" sind. Man bräuchte dazu also eine Art
        //  "Undurchsichtigkeitsermittler"?! Letztlich wäre wohl das Konzept:
        //  Es ist überall dunkel - es sei denn es leuchtet weiter innen oder weiter außen
        //  und die Schwelle dorthin ist durchsichtig.
        //  Problem auch: Die "enthaltenen" Objekte kann diese Komponente nicht festellen, das
        //  muss ein System tun (locationSystem?). Die Komponente könnte allerdings das
        //  location-System kennen...

        if (lichtverhaeltnisseErmittler != null) {
            @Nullable final Lichtverhaeltnisse automonFestgelegteLichtverhaeltnisse =
                    lichtverhaeltnisseErmittler.get();
            if (automonFestgelegteLichtverhaeltnisse != null) {
                return automonFestgelegteLichtverhaeltnisse;
            }
        }

        @Nullable final StoringPlaceComp outerStoringPlaceComp = getOuterStoringPlaceComp();

        if (outerStoringPlaceComp != null) {
            // Die Komponente ist "durchsichtig" und übernimmt die Lichtverhältnisse aus
            // ihrer Container-Komponente.
            return outerStoringPlaceComp.getLichtverhaeltnisse();
        }

        // Es gibt keine Container-Komponente. Die Komponente übernimmt die Lichtverhältnisse -
        // aus den "Umweltverhältnissen", konkret: Aus der Tageszeit
        return timeTaker.now().getTageszeit().getLichtverhaeltnisseDraussen();

        // IDEA Möglicherweise sollen Wetterphänomene (Regen) und der "tageszeitliche Himmel"
        //  ("du siehst ein schönes Abendrot") nur dann erzählt werden, wenn der SC
        //  "draußen" ist oder "einen Blick auf den Himmel hat". Auch diese Fragen ließen
        //  sich wohl analog rekursiv beantworten.
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
