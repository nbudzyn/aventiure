package de.nb.aventiure2.german.base;

import androidx.annotation.Nullable;

import java.util.Objects;

/**
 * Zwei Prädikativa, die mit <i>und</i> verbunden werden
 */
public class ZweiPraedikativa<P extends Praedikativum> implements Praedikativum {
    private final P erst;
    private final P zweit;

    public ZweiPraedikativa(
            final P erst,
            final P zweitesPraedikativum) {
        this.erst = erst;
        zweit = zweitesPraedikativum;
    }

    @Override
    public Konstituentenfolge getPraedikativ(final Person person, final Numerus numerus) {
        return Konstituentenfolge.joinToKonstituentenfolge(
                erst.getPraedikativ(person, numerus),
                "und",
                zweit.getPraedikativ(person, numerus)
        );
    }

    @Nullable
    @Override
    public Konstituentenfolge getPraedikativAnteilKandidatFuerNachfeld(final Person person,
                                                                       final Numerus numerus) {
        @Nullable final Konstituentenfolge ersterNachfeldAnteil =
                erst.getPraedikativAnteilKandidatFuerNachfeld(person, numerus);

        @Nullable final Konstituentenfolge zweiterNachfeldAnteil =
                zweit.getPraedikativAnteilKandidatFuerNachfeld(person, numerus);

        if (Objects.equals(ersterNachfeldAnteil, zweiterNachfeldAnteil)) {
            // -> "Sie ist glücklich und erfreut gewesen, dich zu sehen"
            return zweiterNachfeldAnteil;
        }

        // -> "Sie ist glücklich, dich zu sehen, gewesen, UND ERFREUT, DICH SO BALD WIEDER
        // ZU TREFFEN"
        return Konstituentenfolge.joinToKonstituentenfolge(
                "und",
                zweit.getPraedikativ(person, numerus)
        ).withVorkommaNoetig();
    }


    @Nullable
    @Override
    public Konstituentenfolge getPraedikativOhneAnteilKandidatFuerNachfeld(final Person person,
                                                                           final Numerus numerus) {
        @Nullable final Konstituentenfolge ersterNachfeldAnteil =
                erst.getPraedikativAnteilKandidatFuerNachfeld(person, numerus);

        @Nullable final Konstituentenfolge zweiterNachfeldAnteil =
                zweit.getPraedikativAnteilKandidatFuerNachfeld(person, numerus);

        if (Objects.equals(ersterNachfeldAnteil, zweiterNachfeldAnteil)) {
            // -> "Sie ist glücklich und erfreut gewesen, dich zu sehen"
            return Praedikativum.super
                    .getPraedikativOhneAnteilKandidatFuerNachfeld(person, numerus);
        }

        // -> "Sie ist GLÜCKLICH, DICH ZU SEHEN, gewesen, und erfreut, dich so bald wieder zu
        // treffen"
        return erst.getPraedikativ(person, numerus);
    }

    protected P getErst() {
        return erst;
    }

    protected P getZweit() {
        return zweit;
    }
}
