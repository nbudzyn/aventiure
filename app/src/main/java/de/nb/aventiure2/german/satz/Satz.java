package de.nb.aventiure2.german.satz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import java.util.Collection;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.german.base.Interrogativpronomen;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.Personalpronomen;
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

/**
 * Ein Satz.
 */
public class Satz {
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
        return praedikate.stream().map(v -> v.mit(objekt).alsSatzMitSubjekt(subjekt))
                .collect(toImmutableList());
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

    public Satz(@Nullable final SubstantivischePhrase subjekt,
                final PraedikatOhneLeerstellen praedikat) {
        this(null, subjekt, praedikat);
    }

    public Satz(@Nullable final String anschlusswort,
                @Nullable final SubstantivischePhrase subjekt,
                final PraedikatOhneLeerstellen praedikat) {
        this(anschlusswort, subjekt, praedikat, null);
    }

    public Satz(@Nullable final String anschlusswort,
                @Nullable final SubstantivischePhrase subjekt,
                final PraedikatOhneLeerstellen praedikat,
                @Nullable final Konditionalsatz angabensatz) {
        if (praedikat.inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich()) {
            if (subjekt != null) {
                Personalpronomen.checkExpletivesEs(subjekt);
            }
        } else {
            Preconditions.checkNotNull(subjekt, "Subjekt null, fehlendes Subjekt " +
                    "für diese Prädikat nicht möglich: " + praedikat);
        }

        this.anschlusswort = anschlusswort;
        this.subjekt = subjekt;
        this.praedikat = praedikat;
        this.angabensatz = angabensatz;
    }

    public Satz mitAnschlusswort(@Nullable final String anschlusswort) {
        return new Satz(anschlusswort, subjekt, praedikat, angabensatz);
    }

    private Satz mitSubjekt(@Nullable final SubstantivischePhrase subjekt) {
        return new Satz(anschlusswort, subjekt, praedikat, angabensatz);
    }

    /**
     * Fügt dem Subjekt etwas hinzu wie "auch", "allein", "ausgerechnet",
     * "wenigstens" etc. (sofern das Subjekt eine Fokuspartikel erlaubt, ansonsten
     * wird sie verworfen)
     */
    public Satz mitSubjektFokuspartikel(
            @Nullable final String subjektFokuspartikel) {
        if (subjekt == null) {
            return this;
        }

        return new Satz(anschlusswort, subjekt.mitFokuspartikel(subjektFokuspartikel),
                praedikat, angabensatz);
    }

    public Satz mitModalpartikeln(final Modalpartikel... modalpartikeln) {
        return new Satz(anschlusswort, subjekt, praedikat.mitModalpartikeln(modalpartikeln),
                angabensatz);
    }

    public Satz mitModalpartikeln(final Collection<Modalpartikel> modalpartikeln) {
        return new Satz(anschlusswort, subjekt, praedikat.mitModalpartikeln(modalpartikeln),
                angabensatz);
    }

    public Satz mitAdvAngabe(@Nullable final AdvAngabeSkopusSatz advAngabe) {
        return new Satz(anschlusswort, subjekt, praedikat.mitAdvAngabe(advAngabe),
                angabensatz);
    }

    public Satz mitAdvAngabe(
            @Nullable final AdvAngabeSkopusVerbAllg advAngabe) {
        return new Satz(anschlusswort, subjekt, praedikat.mitAdvAngabe(advAngabe),
                angabensatz);
    }

    public Satz mitAdvAngabe(
            @Nullable final AdvAngabeSkopusVerbWohinWoher advAngabe) {
        return new Satz(anschlusswort, subjekt, praedikat.mitAdvAngabe(advAngabe),
                angabensatz);
    }

    public Satz mitAngabensatz(@Nullable final Konditionalsatz angabensatz) {
        if (angabensatz == null) {
            return this;
        }

        return new Satz(anschlusswort, subjekt, praedikat, angabensatz);
    }

    /**
     * Gibt eine indirekte Frage zurück: Etwas wie
     * <ul>
     * <li>ob du etwas zu berichten hast
     * <li>was du zu berichten hast
     * <li>wer etwas zu berichten hat
     * <li>wer was zu berichten hat
     * <li>mit wem sie sich treffen wird
     * <li>wann du etwas zu berichten hast
     * <li>wessen Heldentaten wer zu berichten hat
     * <li>was zu erzählen du beginnen wirst
     * <li>was du zu erzählen beginnen wirst
     * <li>was du zu erzählen beginnen wirst
     * <li>wie er helfen kann
     * <li>wer wie geholfen hat
     * </ul>
     */
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

    /**
     * Gibt den Satz als Verbzweitsatz aus, bei dem nach Möglichkeit ein "spezielles"
     * Vorfeld gewählt wird, z.B. eine adverbiale Bestimmung: "am Abend hast du etwas zu berichten"
     */
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

    /**
     * Gibt den Satz als einige alternative Verbzweitsätze aus, z.B. "du hast
     * am Abend etwas zu berichten" oder "am Abend hast du etwas zu berichten"
     */
    @CheckReturnValue
    @NonNull
    public ImmutableList<Konstituentenfolge> altVerzweitsaetze() {
        final ImmutableList.Builder<Konstituentenfolge> res = ImmutableList.builder();

        res.add(getVerbzweitsatzStandard());

        if (subjekt == null && praedikat.inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich()
                && getSpeziellesVorfeldSehrErwuenscht() != null) {
            // "Es friert mich".
            res.add(mitSubjekt(Personalpronomen.EXPLETIVES_ES).getVerbzweitsatzStandard());
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

    /**
     * Gibt den Satz als Verbzweitsatz aus, bei dem das Subjekt im Vorfeld steht, z.B. "du hast
     * am Abend etwas zu berichten" oder "du nimmst den Ast"
     */
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

    public Konstituente getSpeziellesVorfeldSehrErwuenscht() {
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

    public Konstituentenfolge getVerbzweitsatzMitVorfeld(final String vorfeld) {
        return joinToKonstituentenfolge(
                anschlusswort, // "und"
                vorfeld,
                getPraedikatVerbzweitMitSubjektImMittelfeldFallsNoetig(),
                angabensatz != null ?
                        Konstituentenfolge.schliesseInKommaEin(angabensatz.getDescription()) :
                        null);
    }

    /**
     * Gibt den Satz als Verbletztsatz aus, z.B. "du etwas zu berichten hast"
     */
    Konstituentenfolge getVerbletztsatz() {
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
    public Konstituentenfolge getSatzanschlussOhneSubjekt() {
        return joinToKonstituentenfolge(
                anschlusswort, // "und"
                praedikat.getVerbzweit(getPersonFuerPraedikat(), getNumerusFuerPraedikat()),
                angabensatz != null ?
                        Konstituentenfolge.schliesseInKommaEin(angabensatz.getDescription()) :
                        null);
    }

    public boolean hasSubjektDu() {
        return subjekt instanceof Personalpronomen
                && subjekt.getPerson() == P2
                && subjekt.getNumerus() == SG;
    }

    @Nullable
    public SubstantivischePhrase getSubjekt() {
        return subjekt;
    }

    public PraedikatOhneLeerstellen getPraedikat() {
        return praedikat;
    }

    // equals() und hashCode() überschreiben wir extra nicht! Alle Satz-Objekte
    // sollen als "verschieden" gelten. Ansonsten müssten wir auch in allen
    // SubstantivischePhrase- und
    // PraedikatOhneLeerstellen-Implementierungen equals() und hashCode()
    // überschreiben.
    // Das wäre inhaltlich richtig, aber viel Arbeit.
}