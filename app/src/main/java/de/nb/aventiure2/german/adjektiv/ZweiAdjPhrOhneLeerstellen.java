package de.nb.aventiure2.german.adjektiv;

import java.util.Objects;

import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;

/**
 * Zwei Adjektivphrasen ohne Leerstellen, die mit <i>und</i>
 * verbunden werden
 */
public class ZweiAdjPhrOhneLeerstellen implements AdjPhrOhneLeerstellen {
    private final AdjPhrOhneLeerstellen ersteAdjPhr;
    private final AdjPhrOhneLeerstellen zweiteAdjPhr;

    public ZweiAdjPhrOhneLeerstellen(
            final AdjPhrOhneLeerstellen ersteAdjPhr,
            final AdjPhrOhneLeerstellen zweiteAdjPhr) {
        this.ersteAdjPhr = ersteAdjPhr;
        this.zweiteAdjPhr = zweiteAdjPhr;
    }

    @Override
    public AdjPhrOhneLeerstellen mitGraduativerAngabe(
            @Nullable final GraduativeAngabe graduativeAngabe) {
        return new ZweiAdjPhrOhneLeerstellen(
                ersteAdjPhr.mitGraduativerAngabe(graduativeAngabe),
                zweiteAdjPhr
        );
    }

    @Override
    public Konstituentenfolge getPraedikativOderAdverbial(final Person person,
                                                          final Numerus numerus) {
        return Konstituentenfolge.joinToKonstituentenfolge(
                ersteAdjPhr.getPraedikativ(person, numerus),
                "und",
                zweiteAdjPhr.getPraedikativ(person, numerus)
        );
    }

    @Override
    @Nullable
    public Konstituentenfolge getPraedikativAnteilKandidatFuerNachfeld(final Person person,
                                                                       final Numerus numerus) {
        @Nullable final Konstituentenfolge ersterNachfeldAnteil =
                ersteAdjPhr.getPraedikativAnteilKandidatFuerNachfeld(person, numerus);

        @Nullable final Konstituentenfolge zweiterNachfeldAnteil =
                zweiteAdjPhr.getPraedikativAnteilKandidatFuerNachfeld(person, numerus);

        if (Objects.equals(ersterNachfeldAnteil, zweiterNachfeldAnteil)) {
            // -> "Sie ist glücklich und erfreut gewesen, dich zu sehen"
            return zweiterNachfeldAnteil;
        }

        // -> "Sie ist glücklich, dich zu sehen, gewesen, UND ERFREUT, DICH SO BALD WIEDER
        // ZU TREFFEN"
        return Konstituentenfolge.joinToKonstituentenfolge(
                "und",
                zweiteAdjPhr.getPraedikativ(person, numerus)
        ).withVorkommaNoetig();
    }

    @Nullable
    @Override
    public Konstituentenfolge getPraedikativOhneAnteilKandidatFuerNachfeld(final Person person,
                                                                           final Numerus numerus) {
        @Nullable final Konstituentenfolge ersterNachfeldAnteil =
                ersteAdjPhr.getPraedikativAnteilKandidatFuerNachfeld(person, numerus);

        @Nullable final Konstituentenfolge zweiterNachfeldAnteil =
                zweiteAdjPhr.getPraedikativAnteilKandidatFuerNachfeld(person, numerus);

        if (Objects.equals(ersterNachfeldAnteil, zweiterNachfeldAnteil)) {
            // -> "Sie ist glücklich und erfreut gewesen, dich zu sehen"
            return AdjPhrOhneLeerstellen.super
                    .getPraedikativOhneAnteilKandidatFuerNachfeld(person, numerus);
        }

        // -> "Sie ist GLÜCKLICH, DICH ZU SEHEN, gewesen, und erfreut, dich so bald wieder zu
        // treffen"
        return ersteAdjPhr.getPraedikativ(person, numerus);
    }

    @Override
    public boolean enthaeltZuInfinitivOderAngabensatzOderErgaenzungssatz() {
        return ersteAdjPhr.enthaeltZuInfinitivOderAngabensatzOderErgaenzungssatz() ||
                zweiteAdjPhr.enthaeltZuInfinitivOderAngabensatzOderErgaenzungssatz();
    }

}
