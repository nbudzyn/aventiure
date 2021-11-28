package de.nb.aventiure2.german.base;

import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToKonstituentenfolge;
import static de.nb.aventiure2.german.base.Konstituentenfolge.schliesseInKommaEin;

import androidx.annotation.Nullable;

import java.util.Objects;

/**
 * Zwei Prädikativa, die mit <i>und</i> verbunden werden
 */
public class ZweiPraedikativa<P extends Praedikativum> implements Praedikativum {
    private final P erst;
    private final P zweit;
    private final String konnektor;
    private final boolean konnektorErfordertKommata;

    public ZweiPraedikativa(
            final P erst,
            final P zweit) {
        this(erst, false, "und", zweit);
    }

    public ZweiPraedikativa(
            final P erst,
            final boolean konnektorErfordertKommata, final String konnektor,
            final P zweitesPraedikativum) {
        this.erst = erst;
        this.konnektor = konnektor;
        this.konnektorErfordertKommata = konnektorErfordertKommata;
        zweit = zweitesPraedikativum;
    }

    @Override
    public Konstituentenfolge getPraedikativ(final PraedRegMerkmale praedRegMerkmale,
                                             @Nullable
                                             final Negationspartikelphrase negationspartikel) {
        // "schon lange kein Esel mehr und schon lange nicht mehr doof"
        return Konstituentenfolge.joinToKonstituentenfolge(
                erst.getPraedikativ(praedRegMerkmale, negationspartikel),
                schliesseInKommaEin(
                        joinToKonstituentenfolge(
                                konnektor,
                                zweit.getPraedikativ(praedRegMerkmale, negationspartikel)),
                        konnektorErfordertKommata)
        );
    }

    @Nullable
    @Override
    public Konstituentenfolge getPraedikativAnteilKandidatFuerNachfeld(
            final PraedRegMerkmale praedRegMerkmale) {
        @Nullable final Konstituentenfolge ersterNachfeldAnteil =
                erst.getPraedikativAnteilKandidatFuerNachfeld(praedRegMerkmale);

        @Nullable final Konstituentenfolge zweiterNachfeldAnteil =
                zweit.getPraedikativAnteilKandidatFuerNachfeld(praedRegMerkmale);

        if (Objects.equals(ersterNachfeldAnteil, zweiterNachfeldAnteil)) {
            // -> "Sie ist glücklich und erfreut gewesen, dich zu sehen"
            return zweiterNachfeldAnteil;
        }

        // -> "Sie ist glücklich, dich zu sehen, gewesen, UND ERFREUT, DICH SO BALD WIEDER
        // ZU TREFFEN"
        return Konstituentenfolge.joinToKonstituentenfolge(
                schliesseInKommaEin(
                        joinToKonstituentenfolge(
                                konnektor, zweit.getPraedikativ(praedRegMerkmale)),
                        konnektorErfordertKommata)
        ).withVorkommaNoetig();
    }


    @Nullable
    @Override
    public Konstituentenfolge getPraedikativOhneAnteilKandidatFuerNachfeld(
            final PraedRegMerkmale praedRegMerkmale) {
        @Nullable final Konstituentenfolge ersterNachfeldAnteil =
                erst.getPraedikativAnteilKandidatFuerNachfeld(praedRegMerkmale);

        @Nullable final Konstituentenfolge zweiterNachfeldAnteil =
                zweit.getPraedikativAnteilKandidatFuerNachfeld(praedRegMerkmale);

        if (Objects.equals(ersterNachfeldAnteil, zweiterNachfeldAnteil)) {
            // -> "Sie ist glücklich und erfreut gewesen, dich zu sehen"
            return Praedikativum.super
                    .getPraedikativOhneAnteilKandidatFuerNachfeld(praedRegMerkmale);
        }

        // -> "Sie ist GLÜCKLICH, DICH ZU SEHEN, gewesen, und erfreut, dich so bald wieder zu
        // treffen"
        return erst.getPraedikativ(praedRegMerkmale);
    }

    protected P getErst() {
        return erst;
    }

    protected P getZweit() {
        return zweit;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ZweiPraedikativa<?> that = (ZweiPraedikativa<?>) o;
        return konnektorErfordertKommata == that.konnektorErfordertKommata &&
                erst.equals(that.erst) &&
                zweit.equals(that.zweit) &&
                konnektor.equals(that.konnektor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(erst, zweit, konnektor, konnektorErfordertKommata);
    }
}
