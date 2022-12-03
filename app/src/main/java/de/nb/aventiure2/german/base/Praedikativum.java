package de.nb.aventiure2.german.base;

import androidx.annotation.Nullable;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.german.praedikat.PraedikativumSemPraedikatOhneLeerstellen;
import de.nb.aventiure2.german.praedikat.SeinUtil;
import de.nb.aventiure2.german.praedikat.WerdenUtil;
import de.nb.aventiure2.german.satz.EinzelnerSemSatz;

/**
 * Eine Phrase, die als Prädikativum dienen kann: "(Peter ist) ein Esel", (Peter ist) doof".
 */
// FIXME SemPraedikativum? Oder irgendwie "generisch-agnostisch"? (Auch Subklassen...)
public interface Praedikativum extends UmstellbarePhrase {
    default ZweiPraedikativa<Praedikativum> und(final Praedikativum zweitesPraedikativum) {
        return new ZweiPraedikativa<>(this, zweitesPraedikativum);
    }

    default ZweiPraedikativa<Praedikativum> aber(final Praedikativum zweitesPraedikativum) {
        return new ZweiPraedikativa<>(this, true, "aber",
                zweitesPraedikativum);
    }

    default EinzelnerSemSatz alsEsIstSatz() {
        return alsEsIstSatz(null);
    }

    default EinzelnerSemSatz alsEsIstSatz(
            @Nullable final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld anschlusswort) {
        return alsPraedikativumPraedikat()
                .alsSatzMitSubjekt(Personalpronomen.EXPLETIVES_ES)
                .mitAnschlusswort(anschlusswort);
    }

    default EinzelnerSemSatz alsEsWirdSatz() {
        return alsEsWirdSatz(null);
    }

    default EinzelnerSemSatz alsEsWirdSatz(
            @Nullable final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld anschlusswort) {
        return alsWerdenPraedikativumPraedikat()
                .alsSatzMitSubjekt(Personalpronomen.EXPLETIVES_ES)
                .mitAnschlusswort(anschlusswort);
    }

    default PraedikativumSemPraedikatOhneLeerstellen alsPraedikativumPraedikat() {
        return new PraedikativumSemPraedikatOhneLeerstellen(SeinUtil.VERB,
                this);
    }

    default PraedikativumSemPraedikatOhneLeerstellen alsWerdenPraedikativumPraedikat() {
        return new PraedikativumSemPraedikatOhneLeerstellen(
                WerdenUtil.VERB, this);
    }

    /**
     * Gibt die Phrase zurück, wie sie als Prädikativum für ein Subjekt in dieser
     * Person und diesem Numerus verwendet wird, - ohne den Anteil, der nach Möglichkeit
     * ins Nachfeld gestellt werden soll.
     */
    default Konstituentenfolge getPraedikativOhneAnteilKandidatFuerNachfeld(
            final PraedRegMerkmale praedRegMerkmale) {
        return getPraedikativOhneAnteilKandidatFuerNachfeld(praedRegMerkmale, null);
    }

    /**
     * Gibt die Phrase zurück, wie sie als Prädikativum für ein Subjekt in dieser
     * Person und diesem Numerus verwendet wird, - ohne den Anteil, der nach Möglichkeit
     * ins Nachfeld gestellt werden soll, - und ggf. mit dieser Negationsphrase verknüpft.
     */
    default Konstituentenfolge getPraedikativOhneAnteilKandidatFuerNachfeld(
            final PraedRegMerkmale praedRegMerkmale,
            @Nullable final Negationspartikelphrase negationspartikel) {
        return getPraedikativ(praedRegMerkmale, negationspartikel).cutLast(
                getPraedikativAnteilKandidatFuerNachfeld(praedRegMerkmale));
    }

    /**
     * Gibt die prädikative Form zurück: "hoch", "glücklich, dich zu sehen",
     * "glücklich, sich erheben zu dürfen"
     */
    default Konstituentenfolge getPraedikativ(final SubstantivischePhrase bezug) {
        return getPraedikativ(bezug.getPraedRegMerkmale());
    }

    default Konstituentenfolge getPraedikativ(final Person person, final Numerus numerus,
                                              final Belebtheit belebtheit) {
        return getPraedikativ(new PraedRegMerkmale(person, numerus, belebtheit));
    }

    /**
     * Gibt die Phrase zurück, wie sie als Prädikativum für ein Subjekt in dieser
     * Person und diesem Numerus verwendet wird:
     * "(Ich bin) ein Esel", "(Er ist) doof", "(Sie ist) ihrer selbst sicher" o.Ä.
     */
    default Konstituentenfolge getPraedikativ(final PraedRegMerkmale praedRegMerkmale) {
        return getPraedikativ(praedRegMerkmale, null);
    }

    /**
     * Gibt die Phrase zurück, wie sie als Prädikativum für ein Subjekt in dieser
     * Person und diesem Numerus verwendet wird, ggf. mit dieser Negation
     * verknüpft
     * "(Ich bin) ein Esel", "(Er ist) doof", "(Sie ist) ihrer selbst sicher",
     * "(Ich bin) kein Esel", "(Er ist) nicht doof)o.Ä.
     */
    Konstituentenfolge getPraedikativ(PraedRegMerkmale praedRegMerkmale,
                                      @Nullable Negationspartikelphrase negationspartikel);

    /**
     * Liefert die Teil-Konstituenten-Folge von {@link #getPraedikativ(SubstantivischePhrase)},
     * die nach Möglichkeit in das Nachfeld gestellt werden sollte - so dass letztlich
     * eine diskontinuierliche Konstituente entsteht. Ist in einfachen Fällen leer.
     * <p>
     * Als Beispiel sollen sich letztlich Sätze ergeben wie "Sie ist glücklich gewesen, dich
     * zu sehen."
     */
    @Nullable
    @CheckReturnValue
    Konstituentenfolge getPraedikativAnteilKandidatFuerNachfeld(PraedRegMerkmale praedRegMerkmale);
}
