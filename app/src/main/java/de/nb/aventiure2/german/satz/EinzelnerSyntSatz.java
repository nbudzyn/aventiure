package de.nb.aventiure2.german.satz;

import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;
import static de.nb.aventiure2.german.base.Belebtheit.UNBELEBT;
import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToKonstituentenfolge;
import static de.nb.aventiure2.german.base.Konstituentenfolge.schliesseInKommaEin;
import static de.nb.aventiure2.german.base.Numerus.SG;
import static de.nb.aventiure2.german.base.Person.P2;
import static de.nb.aventiure2.german.base.Person.P3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import java.util.Objects;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import de.nb.aventiure2.german.base.IAlternativeKonstituentenfolgable;
import de.nb.aventiure2.german.base.IInterrogativwort;
import de.nb.aventiure2.german.base.Interrogativpronomen;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld;
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.base.PraedRegMerkmale;
import de.nb.aventiure2.german.base.Relativpronomen;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.praedikat.AbstractFinitesPraedikat;
import de.nb.aventiure2.german.praedikat.Vorfeld;

/**
 * Ein einzelner ("syntaktischer") Satz, in dem alle Diskursreferenten
 * (Personen, Objekte etc.) auf jeweils eine konkrete sprachliche Repräsentation (z.B. ein
 * konkretes Nomen oder Personalpronomen) festgelegt sind.
 */
@Immutable
public class EinzelnerSyntSatz implements IAlternativeKonstituentenfolgable {

    /**
     * Das Subjekt des Satzes. Darf in seltenen Fällen fehlen ("Mich friert.")
     */
    @Nullable
    private final SubstantivischePhrase subjekt;

    /**
     * Die Prädikate des Satzes, im Sinne des Verbs mit all seinen Ergänzungen und
     * Angabe - ohne das Subjekt.
     */
    @NonNull
    private final ImmutableList<AbstractFinitesPraedikat> praedikate;

    private final boolean beiFehlendemSubjektExpletivesEsMoeglich;

    /**
     * Ein dem Satz direkt untergeordneter (Neben-) SemSatz, der den Status einer
     * <i>Angabe</i> hat - der also nicht Subjekt o.Ä. ist.
     */
    @Nullable
    private final KonditionalSyntSatz angabensatz;

    /**
     * Ob der Angabensatz - wenn es überhaupt einen gibt - nach Möglichkeit vorangestellt
     * werden soll
     */
    private final boolean angabensatzMoeglichstVorangestellt;

    EinzelnerSyntSatz(@Nullable final SubstantivischePhrase subjekt,
                      final ImmutableList<AbstractFinitesPraedikat> praedikate,
                      final boolean beiFehlendemSubjektExpletivesEsMoeglich,
                      @Nullable final KonditionalSyntSatz angabensatz,
                      final boolean angabensatzMoeglichstVorangestellt) {
        this.subjekt = subjekt;
        this.praedikate = praedikate;
        this.beiFehlendemSubjektExpletivesEsMoeglich =
                beiFehlendemSubjektExpletivesEsMoeglich;
        this.angabensatz = angabensatz;
        this.angabensatzMoeglichstVorangestellt = angabensatzMoeglichstVorangestellt;
    }

    private EinzelnerSyntSatz mitSubjektExpletivesEs() {
        return new EinzelnerSyntSatz(Personalpronomen.EXPLETIVES_ES, praedikate,
                false, // Egal - Subjekt fehlt nicht
                angabensatz, angabensatzMoeglichstVorangestellt);
    }

    @Override
    public ImmutableCollection<Konstituentenfolge> toAltKonstituentenfolgen() {
        return altVerzweitsaetze();
    }

    @CheckReturnValue
    @NonNull
    ImmutableList<Konstituentenfolge> altVerzweitsaetze() {
        // FIXME Hier sicherstellen, dass (z.B. auf einem Objekt)
        //  toSustPhr() nur einmal aufgerufen wird!
        //  Lösung: Nicht mehrfach den textContext ins Prädikat hineingeben!
        //  Also: das spezielleVorfeldSehrErwuenscht muss Bestandteil eines
        //  FinitesPraedikat sein.

        final ImmutableList.Builder<Konstituentenfolge> res = ImmutableList.builder();

        res.add(getVerbzweitsatzStandard());

        final AbstractFinitesPraedikat erstesFinitesPraedikat = praedikate.get(0);
        @Nullable final Vorfeld speziellesVorfeldSehrErwuenscht =
                erstesFinitesPraedikat.getSpeziellesVorfeldSehrErwuenscht();

        if (subjekt == null && beiFehlendemSubjektExpletivesEsMoeglich
                && speziellesVorfeldSehrErwuenscht != null) {
            // "Es friert mich".
            res.add(mitSubjektExpletivesEs()
                    .getVerbzweitsatz(null));
        }

        @Nullable final Vorfeld speziellesVorfeldAlsWeitereOption =
                erstesFinitesPraedikat.getSpeziellesVorfeldAlsWeitereOption();

        if (speziellesVorfeldAlsWeitereOption != null) {
            res.add(getVerbzweitsatz(speziellesVorfeldAlsWeitereOption));
        }

        return res.build();
    }

    Konstituentenfolge getVerbzweitsatzMitSpeziellemVorfeldAlsWeitereOption() {
        final AbstractFinitesPraedikat erstesPraedikat = praedikate.get(0);

        @Nullable final Vorfeld speziellesVorfeldAlsWeitereOption =
                erstesPraedikat.getSpeziellesVorfeldAlsWeitereOption();

        // Angabensätze können / sollten nur unter gewissen Voraussetzungen
        // ins Vorfeld gesetzt werden.

        return getVerbzweitsatz(speziellesVorfeldAlsWeitereOption);
    }

    Konstituentenfolge getVerbzweitsatzStandard() {
        final AbstractFinitesPraedikat erstesFinitesPraedikat = praedikate.get(0);
        @Nullable final Vorfeld speziellesVorfeldAusMittelOderNachfeld =
                erstesFinitesPraedikat.getSpeziellesVorfeldSehrErwuenscht();

        return getVerbzweitsatz(speziellesVorfeldAusMittelOderNachfeld);
    }

    Konstituentenfolge getVerbzweitsatzMitVorfeld(final String vorfeld) {
        return getVerbzweitsatz(new Vorfeld(vorfeld), false);
    }

    private Konstituentenfolge getVerbzweitsatz(
            @Nullable final Vorfeld speziellesVorfeldAusMittelOderNachfeld) {
        return getVerbzweitsatz(speziellesVorfeldAusMittelOderNachfeld,
                true);
    }

    @Nullable
    private Konstituentenfolge getVerbzweitsatz(
            @Nullable final Vorfeld vorfeld,
            final boolean vorfeldAusMittelOderNachfeld) {
        Konstituentenfolge res = null;

        for (int i = 0; i < praedikate.size(); i++) {
            final AbstractFinitesPraedikat finit = praedikate.get(i);
            if (i == 0) {
                final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld konnektor =
                        finit.getKonnektor();

                @Nullable final Konstituentenfolge vorfeldKonstituentenfolge =
                        vorfeld != null ?
                                vorfeld.toKonstituentenfolge() :
                                null;

                if (vorfeldKonstituentenfolge != null) {
                    res = joinToKonstituentenfolge(
                            konnektor, // "und"
                            vorfeldKonstituentenfolge.withVorkommaNoetigMin(konnektor == null),
                            // "danach"
                            (subjekt != null ?
                                    finit.ohneKonnektor()
                                            .getVerbzweitMitSubjektImMittelfeld(subjekt) :
                                    finit.ohneKonnektor().getVerbzweit()).cutFirst(
                                    vorfeldAusMittelOderNachfeld ?
                                            vorfeldKonstituentenfolge : null))
                    // "bist du ein guter Mensch geworden"
                    ;
                } else if (angabensatzMoeglichstVorangestellt) {
                    res = joinToKonstituentenfolge(
                            konnektor, // "und"
                            angabensatz != null ?
                                    schliesseInKommaEin(angabensatz) :
                                    null, // "[, ]als er kommt[, ]"
                            finit.ohneKonnektor().getVerbzweitMitSubjektImMittelfeld(
                                    subjekt != null ? subjekt :
                                            Personalpronomen.EXPLETIVES_ES
                            )) // "bist du ein guter Mensch geworden"
                            .withVorkommaNoetigMin(konnektor == null);
                } else {
                    res = joinToKonstituentenfolge(
                            konnektor, // "und" /  "[, ]aber"
                            subjekt != null ? subjekt.nomK() :
                                    Personalpronomen.EXPLETIVES_ES.nomK(),
                            // "du"
                            finit.ohneKonnektor()
                                    .getVerbzweit()) // "bist ein guter Mensch geworden"
                            .withVorkommaNoetigMin(konnektor == null);
                }
            } else {
                res = joinToKonstituentenfolge(
                        res, // "bist ein guter Mensch geworden"
                        finit.getVerbzweit()
                                .withVorkommaNoetigMin(i < praedikate.size()
                                        - 1)); // "hast dabei viel Mühe gehabt"
            }
        }

        if (angabensatz != null && !angabensatzMoeglichstVorangestellt) {
            res = joinToKonstituentenfolge(
                    res, // "bist ein guter Mensch geworden"
                    schliesseInKommaEin(angabensatz));
        }

        return res;
    }

    Konstituentenfolge getIndirekteFrage() {
        // Zurzeit unterstützen wir nur Interrogativpronomen für die normalen Kasus
        // wie "wer" oder "was" - sowie Interrogativadverbialien ("wann").
        // Später sollten auch unterstützt werden:
        // - Interrogativpronomen mit Präposition ("mit wem")
        // - "substantivische Interrogativphrasen" wie "wessen Heldentaten"
        // - "Infinitiv-Interrogativphrasen" wie "was zu erzählen"

        return getVerbletztsatz(true,
                // "wer etwas zu berichten hat", "wer was zu berichten hat"
                // "wer etwas zu berichten hat", "wer was zu berichten hat", "was er zu berichten
                // hat"
                !(subjekt instanceof Interrogativpronomen),
                false)
                .withVorkommaNoetigMin(!praedikate.get(0).hasKonnektor());
    }

    Konstituentenfolge getRelativsatz() {
        // Zurzeit unterstützen wir nur die reinen Relativpronomen für die normalen Kasus
        // wie "der" oder "das".
        // Später sollten auch unterstützt werden:
        // - Relativpronomen mit Präposition ("mit dem")
        // - "substantivische Relativphrasen" wie "dessen Heldentaten"
        // - "Infinitiv-Relativphrasen" wie "die Geschichte, die zu erzählen du vergessen hast"
        // - "Relativsätze mit Interrogativadverbialien": "der Ort, wo"
        if (subjekt instanceof Relativpronomen) {
            // "der etwas zu berichten hat", "der was zu berichten hat", "die kommt"
            return getRelativsatzMitRelativpronomenSubjekt();
        }

        return getVerbletztsatz(true,
                false, true)
                .withVorkommaNoetigMin(!praedikate.get(0).hasKonnektor());
    }

    private Konstituentenfolge getRelativsatzMitRelativpronomenSubjekt() {
        // "der etwas zu berichten hat", "der was zu berichten hat", "die kommt"
        return getVerbletztsatz(true, false,
                false)
                .withVorkommaNoetigMin(!praedikate.get(0).hasKonnektor());
    }

    /**
     * Gibt den Satz als Verbletztsatz aus, z.B. "du etwas zu berichten hast"
     * oder "er durch den Wald hat laufen wollen".
     */
    @Nullable
    Konstituentenfolge getVerbletztsatz(final boolean anschlussAusserAberUnterdruecken) {
        return getVerbletztsatz(anschlussAusserAberUnterdruecken,
                false, false);
    }

    /**
     * Gibt den Satz als Verbletztsatz aus, z.B.:
     * <ul>
     * <li>"du etwas zu berichten hast"
     * <li>"er durch den Wald hat laufen wollen"
     * <li>"er eine Schlange gesehen und sich sehr erschreckt hat"
     * <li>"er auf eine Schlange gestoßen ist und sich sehr erschreckt hat"
     * </ul>
     *
     * @param interrogativwoerterVoranstellen Ob je finites Prädikat das erste
     *                                        {@link IInterrogativwort}
     *                                        ("wann", "wen", ...) vorangestellt werden soll
     *                                        (bzw. bei fehlendem Interrogativwort ein "ob")
     *                                        - oder nicht. Damit erzeugt diese Methode
     *                                        Interrogativnebensätze wie "was du zu berichten hast"
     *                                        oder "ob du etwas zu berichten hast".
     * @param interrogativwoerterVoranstellen Ob je finites Prädikat das (erste)
     *                                        {@link Relativpronomen}
     *                                        ("den", "die", ...) vorangestellt werden soll -
     *                                        oder nicht. Damit erzeugt diese Methode Relativsätze
     *                                        wie "die du gesehen hast".
     */
    @NonNull
    Konstituentenfolge getVerbletztsatz(
            final boolean anschlussAusserAberUnterdruecken,
            final boolean interrogativwoerterVoranstellen,
            final boolean relativpronomenVoranstellen) {
        Konstituentenfolge res = null;
        Konstituentenfolge vorigesInterrogativwort = null;

        for (int i = 0; i < praedikate.size(); i++) {
            final AbstractFinitesPraedikat finit = praedikate.get(i);

            final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld konnektor;
            if (i == 0) {
                konnektor = getEffektivesAnschlusswort(anschlussAusserAberUnterdruecken, finit);
            } else {
                konnektor = finit.getKonnektor();
            }

            final boolean vorkommaNoetigMin = i > 0 && konnektor == null;

            final Konstituentenfolge subjektPosition =
                    (i == 0 && subjekt != null) ? subjekt.nomK() : null;

            // Nachfeld nur beim letzten Prädikat nachstellen - sonst vor dem
            // Verb "einreihen"!
            final boolean nachfeldNachstellen = i == praedikate.size() - 1;

            Konstituentenfolge scharnierwort = null;
            Konstituentenfolge entferntesScharnierwort = null;
            if (relativpronomenVoranstellen) {
                entferntesScharnierwort = requireNonNull(
                        finit.getRelativpronomen(),
                        "Kein (eindeutiges) Relativpronomen im finiten Prädikat "
                                + "gefunden: " + finit);
                scharnierwort = entferntesScharnierwort;
            } else if (interrogativwoerterVoranstellen) {
                entferntesScharnierwort = finit.getErstesInterrogativwort();
                if (entferntesScharnierwort != null) {
                    if (!entferntesScharnierwort.equals(vorigesInterrogativwort)) {
                        scharnierwort = entferntesScharnierwort;
                    } else {
                        scharnierwort = null;
                    }
                } else {
                    scharnierwort = joinToKonstituentenfolge("ob");
                }
                vorigesInterrogativwort = entferntesScharnierwort;
            }

            res = joinToKonstituentenfolge(
                    res, //  "ein guter Mensch geworden bist"
                    joinToKonstituentenfolge(
                            konnektor, // "und" /  "[, ]aber"
                            scharnierwort, // "wann" / "ob" / "den"
                            subjektPosition, // "du"
                            finit.ohneKonnektor()
                                    .getVerbletzt(nachfeldNachstellen)
                                    .cutFirst(entferntesScharnierwort)
                            // "dabei viel Mühe gehabt hast"
                    ).withVorkommaNoetigMin(vorkommaNoetigMin)
            );
        }

        if (angabensatz != null) {
            res = joinToKonstituentenfolge(
                    res, // "ein guter Mensch geworden bist"
                    schliesseInKommaEin(angabensatz));
        }

        return requireNonNull(res);
    }

    @Nullable
    private static NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld getEffektivesAnschlusswort(
            final boolean anschlussAusserAberUnterdruecken, final AbstractFinitesPraedikat finit) {
        @Nullable final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld
                tmp = finit.getKonnektor();
        if (anschlussAusserAberUnterdruecken && tmp !=
                NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld.ABER) {
            return null;
        }

        return tmp;
    }

    @Nonnull
    public Konstituentenfolge getSatzanschlussOhneSubjektOhneAnschlusswortOhneVorkomma() {
        return getSatzanschlussOhneSubjekt(false);
    }

    @Nonnull
    public Konstituentenfolge getSatzanschlussOhneSubjektMitAnschlusswortOderVorkomma() {
        return getSatzanschlussOhneSubjekt(true);
    }

    /**
     * Gibt diesen einzelnen Satz in Verbzweitform zurück, jedoch ohne Subjekt.
     *
     * @param mitAnschlusswortOderVorkomma ob die Verbzweitform
     *                                     <ul>
     *                                      <li>mit Anschlusswort oder Vorkomma beginnen soll
     *                                      ("und hast am Abend etwas zu berichten" /
     *                                      "[, ]aber nimmst den Ast")
     *                                      <li>oder aber nicht ("hast am Abend etwas zu
     *                                      berichten" / "nimmst den Ast")
     *                                     </ul>
     */
    @Nonnull
    Konstituentenfolge getSatzanschlussOhneSubjekt(
            final boolean mitAnschlusswortOderVorkomma) {
        Konstituentenfolge res = null;

        for (int i = 0; i < praedikate.size(); i++) {
            final AbstractFinitesPraedikat finit = praedikate.get(i);

            res = joinToKonstituentenfolge(
                    res, // "bist ein guter Mensch geworden"
                    finit.getVerbzweit()
                            .withVorkommaNoetigMin(
                                    (i == 0 && mitAnschlusswortOderVorkomma
                                            && !finit.hasKonnektor())
                                            ||
                                            (i > 0 && i < praedikate.size() - 1)));
            // "und hast dabei viel Mühe gehabt"
        }

        if (angabensatz != null && !angabensatzMoeglichstVorangestellt) {
            res = joinToKonstituentenfolge(
                    res, // "bist ein guter Mensch geworden und hast dabei viel Mühe gehabt"
                    schliesseInKommaEin(angabensatz));
        }

        return requireNonNull(res);
    }

    public boolean hasSubjektDuBelebt() {
        return subjekt instanceof Personalpronomen
                && subjekt.getPerson() == P2
                && subjekt.getNumerus() == SG;
    }

    @Nullable
    public SubstantivischePhrase getSubjekt() {
        return subjekt;
    }

    @Nullable
    public ImmutableList<AbstractFinitesPraedikat> getPraedikateWennOhneInformationsverlustMoeglich() {
        if (angabensatz != null) {
            return null;
        }

        return praedikate;
    }

    private PraedRegMerkmale getPraedRegMerkmale() {
        if (subjekt == null) {
            // "Mich friert"
            return new PraedRegMerkmale(P3, SG, UNBELEBT);
        }

        return subjekt.getPraedRegMerkmale();
    }

    public boolean hatAngabensatz() {
        return angabensatz != null;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final EinzelnerSyntSatz that = (EinzelnerSyntSatz) o;
        return angabensatzMoeglichstVorangestellt == that.angabensatzMoeglichstVorangestellt
                && Objects.equals(subjekt, that.subjekt) && praedikate.equals(that.praedikate)
                && beiFehlendemSubjektExpletivesEsMoeglich
                == that.beiFehlendemSubjektExpletivesEsMoeglich
                && Objects.equals(angabensatz, that.angabensatz);
    }

    @Override
    public int hashCode() {
        return hash(subjekt, praedikate,
                beiFehlendemSubjektExpletivesEsMoeglich,
                angabensatz, angabensatzMoeglichstVorangestellt);
    }

    @Override
    public String toString() {
        return "EinzelnerSyntSatz{" +
                "subjekt=" + subjekt +
                ", praedikate=" + praedikate +
                ", beiFehlendemSubjektExpletivesEsMoeglich="
                + beiFehlendemSubjektExpletivesEsMoeglich
                +
                ", angabensatz=" + angabensatz +
                ", angabensatzMoeglichstVorangestellt=" + angabensatzMoeglichstVorangestellt +
                '}';
    }
}
