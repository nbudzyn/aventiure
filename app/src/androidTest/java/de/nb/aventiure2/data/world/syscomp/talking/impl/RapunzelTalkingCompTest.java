package de.nb.aventiure2.data.world.syscomp.talking.impl;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.common.collect.ImmutableList;
import com.google.common.truth.Correspondence;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import de.nb.aventiure2.androidtest.AndroidTestBase;
import de.nb.aventiure2.data.world.syscomp.talking.ITalkerGO;
import de.nb.aventiure2.german.base.PhorikKandidat;
import de.nb.aventiure2.german.description.SimpleDuDescription;
import de.nb.aventiure2.german.description.TimedDescription;

import static com.google.common.truth.Truth.assertThat;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static java.util.Objects.requireNonNull;


@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RapunzelTalkingCompTest extends AndroidTestBase {
    @Test
    public void altGespraechsbeginnOhneBegruessungZuneigungMinDeutlich() {
        // GIVEN
        final ITalkerGO<RapunzelTalkingComp> rapunzel = world.load(RAPUNZEL);

        // WHEN
        final ImmutableList<TimedDescription<SimpleDuDescription>> actual =
                rapunzel.talkingComp().altGespraechsbeginnOhneBegruessungZuneigungMinDeutlich();

        // THEN
        assertThat(actual).isNotEmpty();

        assertThat(actual)
                .comparingElementsUsing(Correspondence.transforming(
                        (TimedDescription<SimpleDuDescription> t) ->
                                requireNonNull(t).getDescription().getPhorikKandidat(),
                        "has a PhorikKandidat of"))
                //  .displayingDiffsPairedBy(t -> t)
                .containsExactly((PhorikKandidat) null);
    }
}