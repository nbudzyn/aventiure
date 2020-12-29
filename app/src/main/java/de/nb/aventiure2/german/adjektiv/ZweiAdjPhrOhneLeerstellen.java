package de.nb.aventiure2.german.adjektiv;

import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.Konstituente;
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
    public Iterable<Konstituente> getPraedikativOderAdverbial(final Person person,
                                                              final Numerus numerus) {
        return Konstituente.joinToKonstituenten(
                ersteAdjPhr.getPraedikativ(person, numerus),
                "und",
                zweiteAdjPhr.getPraedikativ(person, numerus)
        );
    }

    @Override
    public Iterable<Konstituente> getPraedikativAnteilKandidatFuerNachfeld(final Person person,
                                                                           final Numerus numerus) {
        final Iterable<Konstituente> ersterNachfeldAnteil =
                ersteAdjPhr.getPraedikativAnteilKandidatFuerNachfeld(person, numerus);

        final Iterable<Konstituente> zweiterNachfeldAnteil =
                zweiteAdjPhr.getPraedikativAnteilKandidatFuerNachfeld(person, numerus);

        if (ersterNachfeldAnteil.equals(zweiterNachfeldAnteil)) {
            // -> "Sie ist glücklich und erfreut gewesen, dich zu sehen"
            return zweiterNachfeldAnteil;
        }

        // -> "Sie ist glücklich, dich zu sehen, gewesen, UND ERFREUT, DICH SO BALD WIEDER
        // ZU TREFFEN"
        return Konstituente.withVorkommaNoetig(
                Konstituente.joinToKonstituenten(
                        "und",
                        zweiteAdjPhr.getPraedikativ(person, numerus)
                )
        );
    }

    @Override
    public Iterable<Konstituente> getPraedikativOhneAnteilKandidatFuerNachfeld(final Person person,
                                                                               final Numerus numerus) {
        final Iterable<Konstituente> ersterNachfeldAnteil =
                ersteAdjPhr.getPraedikativAnteilKandidatFuerNachfeld(person, numerus);

        final Iterable<Konstituente> zweiterNachfeldAnteil =
                zweiteAdjPhr.getPraedikativAnteilKandidatFuerNachfeld(person, numerus);

        if (ersterNachfeldAnteil.equals(zweiterNachfeldAnteil)) {
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
