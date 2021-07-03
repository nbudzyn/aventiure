package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld;
import de.nb.aventiure2.german.base.Negationspartikelphrase;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.satz.EinzelnerSatz;

import static de.nb.aventiure2.german.base.Konstituente.k;
import static de.nb.aventiure2.german.base.Numerus.SG;
import static de.nb.aventiure2.german.base.Person.P3;
import static de.nb.aventiure2.german.base.Personalpronomen.EXPLETIVES_ES;

public enum Witterungsverb implements VerbOhneLeerstellen {
    // Verben ohne Partikel
    AUFKLAREN("aufklaren", "klart", "auf", "aufgeklart"),
    BLITZEN("blitzen", "blitzt", "geblitzt"),
    DAEMMERN("dämmern", "dämmert", "gedämmert"),
    DONNERN("donnern", "donnert", "gedonnert"),
    FRIEREN("frieren", "friert", "gefroren"),
    GIESSEN("gießen", "gießt", "gegegossen"),
    HAGELN("hageln", "hagelt", "gehagelt"),
    REGNEN("regnen", "regnet", "geregnet"),
    SCHNEIEN("schneien", "schneit", "geschneit"),
    STUERMEN("stürmen", "stürmt", "gestürmt"),
    TAUEN("tauen", "taut", "getaut"),
    WEIHNACHTEN("weihnachten", "weihnachtet", "geweihnachtet"),
    ZIEHEN("ziehen", "zieht", "gezogen");

    /**
     * Das Verb an sich, ohne Informationen zur Valenz, ohne Ergänzungen, ohne
     * Angaben
     */
    @NonNull
    private final Verb verb;

    Witterungsverb(final String infinitiv,
                   final String esForm,
                   final String partizipII) {
        this(new Verb(infinitiv, null, null,
                esForm, null, Perfektbildung.HABEN,
                partizipII));
    }

    Witterungsverb(final String infinitiv,
                   final String esForm,
                   @Nullable final String partikel,
                   final String partizipII) {
        this(new Verb(infinitiv, null, null,
                esForm, null, partikel, Perfektbildung.HABEN,
                partizipII));
    }

    Witterungsverb(final Verb verbOhnePartikel,
                   final String partikel,
                   final Perfektbildung perfektbildung) {
        this(verbOhnePartikel.mitPartikel(partikel).mitPerfektbildung(perfektbildung));
    }

    Witterungsverb(final Verb verb) {
        this.verb = verb;
    }

    public EinzelnerSatz alsSatz() {
        return alsSatz(null);
    }

    private EinzelnerSatz alsSatz(
            final @Nullable
                    NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld anschlusswort) {
        return toPraedikat().alsSatzMitSubjekt(anschlusswort, EXPLETIVES_ES);
    }

    public Konstituentenfolge getVerbzweit() {
        return getVerbzweit(EXPLETIVES_ES);
    }

    @Override
    public Konstituentenfolge getVerbzweit(final Person person, final Numerus numerus) {
        Personalpronomen.checkExpletivesEs(person, numerus);

        return toPraedikat().getVerbzweit(person, numerus);
    }

    public Konstituentenfolge getVerbzweitMitSubjektImMittelfeld() {
        return getVerbzweitMitSubjektImMittelfeld(EXPLETIVES_ES);
    }

    @Override
    public Konstituentenfolge getVerbzweitMitSubjektImMittelfeld(
            final SubstantivischePhrase subjekt) {
        Personalpronomen.checkExpletivesEs(subjekt);

        return toPraedikat().getVerbzweitMitSubjektImMittelfeld(subjekt);
    }

    public Konstituentenfolge getVerbletzt() {
        return getVerbletzt(P3, SG);
    }

    @Override
    public Konstituentenfolge getVerbletzt(final Person person, final Numerus numerus) {
        Personalpronomen.checkExpletivesEs(person, numerus);

        return toPraedikat().getVerbletzt(person, numerus);
    }

    public ImmutableList<PartizipIIPhrase> getPartizipIIPhrase() {
        return getPartizipIIPhrasen(EXPLETIVES_ES);
    }

    @Override
    @CheckReturnValue
    public ImmutableList<PartizipIIPhrase> getPartizipIIPhrasen(final Person person,
                                                                final Numerus numerus) {
        Personalpronomen.checkExpletivesEs(person, numerus);

        return ImmutableList.of(verb.getPartizipIIPhrase());
    }

    public Konstituentenfolge getInfinitiv() {
        return getInfinitiv(P3, SG);
    }

    @Override
    public Konstituentenfolge getInfinitiv(final Person person, final Numerus numerus) {
        Personalpronomen.checkExpletivesEs(person, numerus);

        return Konstituentenfolge.joinToKonstituentenfolge(verb.getInfinitiv());
    }

    public Konstituentenfolge getZuInfinitiv() {
        return getZuInfinitiv(P3, SG);
    }

    @Override
    @CheckReturnValue
    public Konstituentenfolge getZuInfinitiv(final Person person, final Numerus numerus) {
        Personalpronomen.checkExpletivesEs(person, numerus);

        return new Konstituentenfolge(k(verb.getZuInfinitiv()));
    }

    public Konstituente getSpeziellesVorfeldSehrErwuenscht(final boolean nachAnschlusswort) {
        return getSpeziellesVorfeldSehrErwuenscht(P3, SG, nachAnschlusswort);
    }

    @Nullable
    @Override
    public Konstituente getSpeziellesVorfeldSehrErwuenscht(final Person person,
                                                           final Numerus numerus,
                                                           final boolean nachAnschlusswort) {
        Personalpronomen.checkExpletivesEs(person, numerus);

        return toPraedikat().getSpeziellesVorfeldSehrErwuenscht(person, numerus,
                nachAnschlusswort);
    }

    public Konstituentenfolge getSpeziellesVorfeldAlsWeitereOption() {
        return getSpeziellesVorfeldAlsWeitereOption(P3, SG);
    }

    @Nullable
    @Override
    public Konstituentenfolge getSpeziellesVorfeldAlsWeitereOption(final Person person,
                                                                   final Numerus numerus) {
        Personalpronomen.checkExpletivesEs(person, numerus);

        return toPraedikat().getSpeziellesVorfeldAlsWeitereOption(person, numerus
        );
    }

    @Override
    public PraedikatSubOhneLeerstellen neg(
            @Nullable final Negationspartikelphrase negationspartikelphrase) {
        return toPraedikat().neg(negationspartikelphrase);
    }

    @Override
    public boolean isBezugAufNachzustandDesAktantenGegeben() {
        // Bei "weggehen" ist ein Bezug auf den Nachzustand des Aktanten gegeben, bei
        // "gehen" nicht
        return verb.isPartikelverb();
    }

    @Override
    public PraedikatSubOhneLeerstellen toPraedikat() {
        return new PraedikatSubOhneLeerstellen(verb);
    }

    public Konstituentenfolge getNachfeld() {
        return getNachfeld(P3, SG);
    }

    @Nullable
    @Override
    public Konstituentenfolge getNachfeld(final Person person, final Numerus numerus) {
        Personalpronomen.checkExpletivesEs(person, numerus);

        return null;
    }

    @Override
    @NonNull
    public Verb getVerb() {
        return verb;
    }

    @Override
    public boolean inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich() {
        // "Jetzt donnert ES".
        return false;
    }
}
