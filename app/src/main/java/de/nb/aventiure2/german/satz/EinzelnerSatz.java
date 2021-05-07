package de.nb.aventiure2.german.satz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.Objects;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.german.base.DeklinierbarePhraseUtil;
import de.nb.aventiure2.german.base.Interrogativpronomen;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.base.Relativpronomen;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbWohinWoher;
import de.nb.aventiure2.german.praedikat.Modalpartikel;
import de.nb.aventiure2.german.praedikat.PraedikatMitEinerObjektleerstelle;
import de.nb.aventiure2.german.praedikat.PraedikatOhneLeerstellen;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToKonstituentenfolge;
import static de.nb.aventiure2.german.base.Numerus.SG;
import static de.nb.aventiure2.german.base.Person.P2;
import static de.nb.aventiure2.german.base.Person.P3;
import static de.nb.aventiure2.util.StreamUtil.*;

/**
 * Ein Satz.
 */
public class EinzelnerSatz implements Satz {
    /**
     * Anschlusswörter sind vor allem "und", "denn" und "aber". Sie stehen vor dem
     * Vorfeld.
     */
    @Nullable
    private final String anschlusswort;

    /**
     * Das Subjekt des Satzes. Darf in seltenen Fällen fehlen ("Mich friert.")
     */
    @Nullable
    private final SubstantivischePhrase subjekt;

    /**
     * Das Prädikat des Satzes, im Sinne des Verbs mit all seinen Ergänzungen und
     * Angabe - ohne das Subjekt.
     */
    private final PraedikatOhneLeerstellen praedikat;

    /**
     * Ein dem Satz direkt untergeordneter (Neben-) Satz, der den Status einer <i>Angabe</i> hat
     * - der also nicht Subjekt o.Ä. ist.
     */
    @Nullable
    private final Konditionalsatz angabensatz;

    public static ImmutableList<Satz> altSubjObjSaetze(
            @Nullable final SubstantivischePhrase subjekt,
            final PraedikatMitEinerObjektleerstelle praedikat, final SubstantivischePhrase objekt,
            final Collection<AdvAngabeSkopusVerbAllg> advAngaben) {
        return altSubjObjSaetze(subjekt, ImmutableList.of(praedikat), objekt, advAngaben);
    }

    public static ImmutableList<Satz> altSubjObjSaetze(
            @Nullable final SubstantivischePhrase subjekt,
            final Collection<? extends PraedikatMitEinerObjektleerstelle> praedikate,
            final SubstantivischePhrase objekt) {
        return mapToList(praedikate, v -> v.mit(objekt).alsSatzMitSubjekt(subjekt));
    }

    public static ImmutableList<Satz> altSubjObjSaetze(
            final SubstantivischePhrase subjekt,
            final Collection<? extends PraedikatMitEinerObjektleerstelle> praedikate,
            final SubstantivischePhrase objekt,
            final Collection<AdvAngabeSkopusVerbAllg> advAngaben) {
        return advAngaben.stream()
                .flatMap(aa -> praedikate.stream()
                        .map(v -> v.mit(objekt)
                                .mitAdvAngabe(aa)
                                .alsSatzMitSubjekt(subjekt)))
                .collect(toImmutableList());
    }

    public EinzelnerSatz(@Nullable final SubstantivischePhrase subjekt,
                         final PraedikatOhneLeerstellen praedikat) {
        this(null, subjekt, praedikat);
    }

    public EinzelnerSatz(@Nullable final String anschlusswort,
                         @Nullable final SubstantivischePhrase subjekt,
                         final PraedikatOhneLeerstellen praedikat) {
        this(anschlusswort, subjekt, praedikat, null);
    }

    private EinzelnerSatz(@Nullable final String anschlusswort,
                          @Nullable final SubstantivischePhrase subjekt,
                          final PraedikatOhneLeerstellen praedikat,
                          @Nullable final Konditionalsatz angabensatz) {
        if (praedikat.inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich()) {
            if (subjekt != null) {
                Personalpronomen.checkExpletivesEs(subjekt);
            }
        } else {
            Preconditions.checkNotNull(subjekt, "Subjekt null, fehlendes Subjekt " +
                    "für diese Prädikat nicht möglich: %s", praedikat);
        }

        this.anschlusswort = anschlusswort;
        this.subjekt = subjekt;
        this.praedikat = praedikat;
        this.angabensatz = angabensatz;
    }

    @Override
    public EinzelnerSatz mitAnschlusswort(@Nullable final String anschlusswort) {
        return new EinzelnerSatz(anschlusswort, subjekt, praedikat, angabensatz);
    }

    private EinzelnerSatz mitSubjektExpletivesEs() {
        return new EinzelnerSatz(anschlusswort, Personalpronomen.EXPLETIVES_ES, praedikat,
                angabensatz);
    }

    @Override
    public EinzelnerSatz mitSubjektFokuspartikel(
            @Nullable final String subjektFokuspartikel) {
        if (subjekt == null) {
            return this;
        }

        return new EinzelnerSatz(anschlusswort, subjekt.mitFokuspartikel(subjektFokuspartikel),
                praedikat, angabensatz);
    }

    @Override
    public EinzelnerSatz mitModalpartikeln(final Collection<Modalpartikel> modalpartikeln) {
        return new EinzelnerSatz(anschlusswort, subjekt,
                praedikat.mitModalpartikeln(modalpartikeln),
                angabensatz);
    }

    @Override
    public EinzelnerSatz mitAdvAngabe(@Nullable final AdvAngabeSkopusSatz advAngabe) {
        return new EinzelnerSatz(anschlusswort, subjekt, praedikat.mitAdvAngabe(advAngabe),
                angabensatz);
    }

    @Override
    public EinzelnerSatz mitAdvAngabe(
            @Nullable final AdvAngabeSkopusVerbAllg advAngabe) {
        return new EinzelnerSatz(anschlusswort, subjekt, praedikat.mitAdvAngabe(advAngabe),
                angabensatz);
    }

    @Override
    public EinzelnerSatz mitAdvAngabe(
            @Nullable final AdvAngabeSkopusVerbWohinWoher advAngabe) {
        return new EinzelnerSatz(anschlusswort, subjekt, praedikat.mitAdvAngabe(advAngabe),
                angabensatz);
    }

    public EinzelnerSatz mitAngabensatz(@Nullable final Konditionalsatz angabensatz) {
        if (angabensatz == null) {
            return this;
        }

        return new EinzelnerSatz(anschlusswort, subjekt, praedikat, angabensatz);
    }

    @Override
    public EinzelnerSatz perfekt() {
        return new EinzelnerSatz(anschlusswort, subjekt, praedikat.perfekt(),
                angabensatz != null ? angabensatz.perfekt() : null);
    }

    @Override
    public Konstituentenfolge getIndirekteFrage() {
        // Zurzeit unterstützen wir nur Interrogativpronomen für die normalen Kasus 
        // wie "wer" oder "was" - sowie Interrogativadverbialien ("wann").
        // Später sollten auch unterstützt werden:
        // - Interrogativpronomen mit Präposition ("mit wem")
        // - "substantivische Interrogativphrasen" wie "wessen Heldentaten"
        // - "Infinitiv-Interrogativphrasen" wie "was zu erzählen"
        if (subjekt instanceof Interrogativpronomen) {
            // "wer etwas zu berichten hat", "wer was zu berichten hat"
            return getIndirekteFrageNachSubjekt();
        }

        @Nullable final Konstituentenfolge erstesInterrogativwortImPraedikat =
                praedikat.getErstesInterrogativwort();

        if (erstesInterrogativwortImPraedikat == null) {
            // "ob du etwas zu berichten hast"
            return getObFrage();
        }

        // "was du zu berichten hast", "wem er was gegeben hat", "wann er kommt"
        return joinToKonstituentenfolge(
                anschlusswort, // "und"
                erstesInterrogativwortImPraedikat, // "was" / "wem" / "wann"
                getVerbletztsatz().cutFirst(
                        erstesInterrogativwortImPraedikat
                ) // "du zu berichten hast", "wer zu berichten hat", "er kommt"
        );
    }

    private Konstituentenfolge getIndirekteFrageNachSubjekt() {
        // "wer etwas zu berichten hat", "wer was zu berichten hat", "was er zu berichten hat"
        return getVerbletztsatz();
    }

    private Konstituentenfolge getObFrage() {
        return joinToKonstituentenfolge(
                anschlusswort, // "und"
                "ob",
                getVerbletztsatz() // "du etwas zu berichten hast"
        );
    }

    @Override
    public Konstituentenfolge getRelativsatz() {
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

        @Nullable final Konstituentenfolge relativpronomenImPraedikat =
                praedikat.getRelativpronomen();

        if (relativpronomenImPraedikat == null) {
            throw new IllegalStateException("Kein (eindeutiges) Relativpronomen im Prädikat "
                    + "gefunden: " + praedikat.getVerbzweit(DeklinierbarePhraseUtil.EINER));
        }

        // "das du zu berichten hast", "dem er was gegeben hat", "der kommt"
        return joinToKonstituentenfolge(
                anschlusswort, // "und"
                relativpronomenImPraedikat, // "das" / "dem"
                getVerbletztsatz().cutFirst(
                        relativpronomenImPraedikat
                ) // "du zu berichten hast", "er was gegeben hat", "kommt"
        );
    }

    private Konstituentenfolge getRelativsatzMitRelativpronomenSubjekt() {
        // "der etwas zu berichten hat", "der was zu berichten hat", "die kommt"
        return getVerbletztsatz();
    }


    @Override
    public Konstituentenfolge getVerbzweitsatzMitSpeziellemVorfeldAlsWeitereOption() {
        @Nullable final Konstituentenfolge speziellesVorfeld =
                praedikat.getSpeziellesVorfeldAlsWeitereOption(
                        getPersonFuerPraedikat(), getNumerusFuerPraedikat()
                );
        if (speziellesVorfeld == null) {
            // Angabensätze können / sollten nur unter gewissen Voraussetzungen
            // ins Vorfeld gesetzt werden.

            return getVerbzweitsatzStandard();
        }

        return getVerbzweitsatzMitVorfeldAusMittelOderNachfeld(speziellesVorfeld);
    }

    private Person getPersonFuerPraedikat() {
        if (subjekt == null) {
            // "Mich friert"
            return P3;
        }

        return subjekt.getPerson();
    }

    @Override
    @CheckReturnValue
    @NonNull
    public ImmutableList<Konstituentenfolge> altVerzweitsaetze() {
        final ImmutableList.Builder<Konstituentenfolge> res = ImmutableList.builder();

        res.add(getVerbzweitsatzStandard());

        if (subjekt == null && praedikat.inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich()
                && getSpeziellesVorfeldSehrErwuenscht() != null) {
            // "Es friert mich".
            res.add(mitSubjektExpletivesEs().getVerbzweitsatzStandard());
        }

        @Nullable final Konstituentenfolge speziellesVorfeld =
                praedikat.getSpeziellesVorfeldAlsWeitereOption(
                        getPersonFuerPraedikat(), getNumerusFuerPraedikat()
                );
        if (speziellesVorfeld == null) {
            res.add(getVerbzweitsatzMitSpeziellemVorfeldAlsWeitereOption());
        }

        return res.build();
    }

    @Override
    public Konstituentenfolge getVerbzweitsatzStandard() {
        @Nullable final Konstituente speziellesVorfeld =
                getSpeziellesVorfeldSehrErwuenscht();
        if (speziellesVorfeld != null) {
            return getVerbzweitsatzMitVorfeldAusMittelOderNachfeld(speziellesVorfeld);
        }

        return joinToKonstituentenfolge(
                anschlusswort, // "und"
                subjekt != null ? subjekt.nomK() : Personalpronomen.EXPLETIVES_ES,
                praedikat.getVerbzweit(getPersonFuerPraedikat(), getNumerusFuerPraedikat()),
                angabensatz != null ?
                        Konstituentenfolge
                                .schliesseInKommaEin(angabensatz.getDescription()) :
                        null);
    }

    private Konstituente getSpeziellesVorfeldSehrErwuenscht() {
        return praedikat.getSpeziellesVorfeldSehrErwuenscht(
                getPersonFuerPraedikat(), getNumerusFuerPraedikat(),
                anschlusswort != null);
    }

    private Konstituentenfolge getVerbzweitsatzMitVorfeldAusMittelOderNachfeld(
            final Konstituente vorfeld) {
        return getVerbzweitsatzMitVorfeldAusMittelOderNachfeld(
                joinToKonstituentenfolge(vorfeld));
    }

    private Konstituentenfolge getVerbzweitsatzMitVorfeldAusMittelOderNachfeld(
            final Konstituentenfolge vorfeld) {

        return joinToKonstituentenfolge(
                anschlusswort, // "und"
                vorfeld,
                getPraedikatVerbzweitMitSubjektImMittelfeldFallsNoetig().cutFirst(vorfeld),
                angabensatz != null ?
                        Konstituentenfolge.schliesseInKommaEin(angabensatz.getDescription()) :
                        null);
    }

    private Konstituentenfolge getPraedikatVerbzweitMitSubjektImMittelfeldFallsNoetig() {
        return subjekt != null ?
                praedikat.getVerbzweitMitSubjektImMittelfeld(subjekt) :
                praedikat.getVerbzweit(getPersonFuerPraedikat(), getNumerusFuerPraedikat());
    }

    @Override
    public Konstituentenfolge getVerbzweitsatzMitVorfeld(final String vorfeld) {
        return joinToKonstituentenfolge(
                anschlusswort, // "und"
                vorfeld,
                getPraedikatVerbzweitMitSubjektImMittelfeldFallsNoetig(),
                angabensatz != null ?
                        Konstituentenfolge.schliesseInKommaEin(angabensatz.getDescription()) :
                        null);
    }

    @Override
    public Konstituentenfolge getVerbletztsatz() {
        return joinToKonstituentenfolge(
                anschlusswort, // "und"
                subjekt != null ? subjekt.nomK() : null,
                praedikat.getVerbletzt(getPersonFuerPraedikat(), getNumerusFuerPraedikat()),
                angabensatz != null ?
                        Konstituentenfolge.schliesseInKommaEin(angabensatz.getDescription()) :
                        null);
    }

    private Numerus getNumerusFuerPraedikat() {
        if (subjekt == null) {
            // "Mich friert"
            return SG;
        }

        return subjekt.getNumerus();
    }

    /**
     * Gibt den Satz in Verbzweitform aus, jedoch ohne Subjekt, also beginnend mit
     * dem Anschlusswort (z.B. "und") und dem Verb. Z.B. "und hast
     * am Abend etwas zu berichten" oder "und nimmst den Ast"
     */
    @Override
    public Konstituentenfolge getSatzanschlussOhneSubjekt() {
        return joinToKonstituentenfolge(
                anschlusswort, // "und"
                praedikat.getVerbzweit(getPersonFuerPraedikat(), getNumerusFuerPraedikat()),
                angabensatz != null ?
                        Konstituentenfolge.schliesseInKommaEin(angabensatz.getDescription()) :
                        null);
    }

    @Override
    public boolean hasSubjektDu() {
        return subjekt instanceof Personalpronomen
                && subjekt.getPerson() == P2
                && subjekt.getNumerus() == SG;
    }

    @Override
    public SubstantivischePhrase getErstesSubjekt() {
        return getSubjekt();
    }

    @Nullable
    public SubstantivischePhrase getSubjekt() {
        return subjekt;
    }

    @Override
    public PraedikatOhneLeerstellen getPraedikat() {
        return praedikat;
    }

    @Override
    public boolean isSatzreihungMitUnd() {
        return false;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final EinzelnerSatz that = (EinzelnerSatz) o;
        return Objects.equals(anschlusswort, that.anschlusswort) &&
                Objects.equals(subjekt, that.subjekt) &&
                Objects.equals(praedikat, that.praedikat) &&
                Objects.equals(angabensatz, that.angabensatz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(anschlusswort, subjekt, praedikat, angabensatz);
    }
}