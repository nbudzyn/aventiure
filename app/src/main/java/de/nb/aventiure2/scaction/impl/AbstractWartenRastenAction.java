package de.nb.aventiure2.scaction.impl;

import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.AvTimeSpan;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.counter.CounterDao;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingIntensity;
import de.nb.aventiure2.scaction.AbstractScAction;
import de.nb.aventiure2.scaction.stepcount.SCActionStepCountDao;

public abstract class AbstractWartenRastenAction extends AbstractScAction {
    static final String COUNTER_WARTEN_ODER_RASTEN_IN_FOLGE =
            "WartenAction_WARTEN_ODER_RASTEN_IN_FOLGE";
    protected final CounterDao counterDao;

    AbstractWartenRastenAction(
            final CounterDao counterDao, final SCActionStepCountDao scActionStepCountDao,
            final TimeTaker timeTaker,
            final Narrator n, final World world) {
        super(scActionStepCountDao, timeTaker, n, world);
        this.counterDao = counterDao;
    }

    @Override
    public String getType() {
        return "actionRastenWarten";
    }

    boolean automatischesEinschlafen() {
        if (counterDao.get(COUNTER_WARTEN_ODER_RASTEN_IN_FOLGE) >= 3) {
            return sc.feelingsComp().getMuedigkeit() >= FeelingIntensity.MERKLICH;
        }

        return sc.feelingsComp().getMuedigkeit() >= FeelingIntensity.STARK;
    }

    void narrateAndDoSchlafen() {
        final AvTimeSpan schlafdauer = sc.feelingsComp().calcSchlafdauerMensch();

        narrateAndDoEinschlafen(schlafdauer);
        sc.feelingsComp().narrateAndDoAufwachenSC(schlafdauer, true);
    }

    protected abstract void narrateAndDoEinschlafen(AvTimeSpan schlafdauer);
}
