package de.nb.aventiure2.german.base;

import androidx.annotation.Nullable;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.german.satz.EinzelnerSatz;

import static de.nb.aventiure2.german.praedikat.PraedikativumPraedikatOhneLeerstellen.praedikativumPraedikatMit;
import static de.nb.aventiure2.german.praedikat.PraedikativumPraedikatOhneLeerstellen.praedikativumPraedikatWerdenMit;

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

    default EinzelnerSatz alsEsIstSatz(@Nullable final String anschlusswort) {
        return praedikativumPraedikatMit(this)
                .alsSatzMitSubjekt(Personalpronomen.EXPLETIVES_ES)
                .mitAnschlusswort(anschlusswort);
    }

    default EinzelnerSatz alsEsWirdSatz() {
        return alsEsWirdSatz(null);
    }

    default EinzelnerSatz alsEsWirdSatz(@Nullable final String anschlusswort) {
        return praedikativumPraedikatWerdenMit(this)
                .alsSatzMitSubjekt(Personalpronomen.EXPLETIVES_ES)
                .mitAnschlusswort(anschlusswort);
    }

    /**
     * Gibt die Phrase zurück, wie sie als Prädikativum für ein Subjekt in dieser
     * Person und diesem Numerus verwendet wird, - ohne den Anteil, der nach Möglichkeit
     * ins Nachfeld gestellt werden soll.
     */
    @CheckReturnValue
    default Konstituentenfolge getPraedikativOhneAnteilKandidatFuerNachfeld(
            final Person person, final Numerus numerus) {
        return getPraedikativ(person, numerus).cutLast(
                getPraedikativAnteilKandidatFuerNachfeld(person, numerus));
    }

    /**
     * Gibt die Phrase zurück, wie sie als Prädikativum für ein Subjekt in dieser
     * Person und diesem Numerus verwendet wird:
     * "(Ich bin) ein Esel", "(Er ist) doof", "(Sie ist) ihrer selbst sicher" o.Ä.
     */
    Konstituentenfolge getPraedikativ(final Person person, final Numerus numerus);

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
