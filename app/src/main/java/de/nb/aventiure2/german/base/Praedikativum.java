package de.nb.aventiure2.german.base;

import androidx.annotation.Nullable;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.german.praedikat.PraedikativumPraedikatOhneLeerstellen;
import de.nb.aventiure2.german.praedikat.SeinUtil;
import de.nb.aventiure2.german.praedikat.WerdenUtil;
import de.nb.aventiure2.german.satz.EinzelnerSatz;

/**
 * Eine Phrase, die als Prädikativum dienen kann: "(Peter ist) ein Esel", (Peter ist) doof".
 */
public interface Praedikativum {
    default ZweiPraedikativa<Praedikativum> und(final Praedikativum zweitesPraedikativum) {
        return new ZweiPraedikativa<>(this, zweitesPraedikativum);
    }

    default ZweiPraedikativa<Praedikativum> aber(final Praedikativum zweitesPraedikativum) {
        return new ZweiPraedikativa<>(this, true, "aber",
                zweitesPraedikativum);
    }


    default EinzelnerSatz alsEsIstSatz() {
        return alsEsIstSatz(null);
    }

    default EinzelnerSatz alsEsIstSatz(
            @Nullable final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld anschlusswort) {
        return alsPraedikativumPraedikat()
                .alsSatzMitSubjekt(Personalpronomen.EXPLETIVES_ES)
                .mitAnschlusswort(anschlusswort);
    }

    default EinzelnerSatz alsEsWirdSatz() {
        return alsEsWirdSatz(null);
    }

    default EinzelnerSatz alsEsWirdSatz(
            @Nullable final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld anschlusswort) {
        return alsWerdenPraedikativumPraedikat()
                .alsSatzMitSubjekt(Personalpronomen.EXPLETIVES_ES)
                .mitAnschlusswort(anschlusswort);
    }

    default PraedikativumPraedikatOhneLeerstellen alsPraedikativumPraedikat() {
        return new PraedikativumPraedikatOhneLeerstellen(SeinUtil.VERB,
                this);
    }

    default PraedikativumPraedikatOhneLeerstellen alsWerdenPraedikativumPraedikat() {
        return new PraedikativumPraedikatOhneLeerstellen(
                WerdenUtil.VERB, this);
    }

    /**
     * Gibt die Phrase zurück, wie sie als Prädikativum für ein Subjekt in dieser
     * Person und diesem Numerus verwendet wird, - ohne den Anteil, der nach Möglichkeit
     * ins Nachfeld gestellt werden soll.
     */
    default Konstituentenfolge getPraedikativOhneAnteilKandidatFuerNachfeld(
            final Person person, final Numerus numerus) {
        return getPraedikativOhneAnteilKandidatFuerNachfeld(person, numerus, null);
    }

    /**
     * Gibt die Phrase zurück, wie sie als Prädikativum für ein Subjekt in dieser
     * Person und diesem Numerus verwendet wird, - ohne den Anteil, der nach Möglichkeit
     * ins Nachfeld gestellt werden soll, - und ggf. mit dieser Negationsphrase verknüpft.
     */
    default Konstituentenfolge getPraedikativOhneAnteilKandidatFuerNachfeld(
            final Person person, final Numerus numerus,
            @Nullable final Negationspartikelphrase negationspartikel) {
        return getPraedikativ(person, numerus, negationspartikel).cutLast(
                getPraedikativAnteilKandidatFuerNachfeld(person, numerus));
    }


    /**
     * Gibt die prädikative Form zurück: "hoch", "glücklich, dich zu sehen",
     * "glücklich, sich erheben zu dürfen"
     */
    default Konstituentenfolge getPraedikativ(final SubstantivischePhrase bezug) {
        return getPraedikativ(bezug.getPerson(), bezug.getNumerus());
    }

    /**
     * Gibt die Phrase zurück, wie sie als Prädikativum für ein Subjekt in dieser
     * Person und diesem Numerus verwendet wird:
     * "(Ich bin) ein Esel", "(Er ist) doof", "(Sie ist) ihrer selbst sicher" o.Ä.
     */
    default Konstituentenfolge getPraedikativ(final Person person, final Numerus numerus) {
        return getPraedikativ(person, numerus, null);
    }

    /**
     * Gibt die Phrase zurück, wie sie als Prädikativum für ein Subjekt in dieser
     * Person und diesem Numerus verwendet wird, ggf. mit dieser Negation
     * verknüpft
     * "(Ich bin) ein Esel", "(Er ist) doof", "(Sie ist) ihrer selbst sicher",
     * "(Ich bin) kein Esel", "(Er ist) nicht doof)o.Ä.
     */
    Konstituentenfolge getPraedikativ(final Person person, final Numerus numerus,
                                      @Nullable Negationspartikelphrase negationspartikel);

    /**
     * Liefert die Teil-Konstituenten-Folge von {@link #getPraedikativ(Person, Numerus)},
     * die nach Möglichkeit in das Nachfeld gestellt werden sollte - so dass letztlich
     * eine diskontinuierliche Konstituente entsteht. Ist in einfachen Fällen leer.
     * <p>
     * Als Beispiel sollen sich letztlich Sätze ergeben wie "Sie ist glücklich gewesen, dich
     * zu sehen."
     */
    @Nullable
    @CheckReturnValue
    Konstituentenfolge getPraedikativAnteilKandidatFuerNachfeld(
            final Person person, final Numerus numerus);
}
