package de.nb.aventiure2.german.praedikat;

import static de.nb.aventiure2.german.base.Belebtheit.UNBELEBT;
import static de.nb.aventiure2.german.base.Numerus.SG;
import static de.nb.aventiure2.german.base.Person.P3;
import static de.nb.aventiure2.german.base.Personalpronomen.EXPLETIVES_ES;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld;
import de.nb.aventiure2.german.base.Negationspartikelphrase;
import de.nb.aventiure2.german.base.PraedRegMerkmale;
import de.nb.aventiure2.german.description.ITextContext;
import de.nb.aventiure2.german.satz.EinzelnerSemSatz;

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

    public EinzelnerSemSatz alsSatz() {
        return alsSatz(null);
    }

    private EinzelnerSemSatz alsSatz(
            final @Nullable
                    NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld anschlusswort) {
        return toPraedikat().alsSatzMitSubjekt(anschlusswort, EXPLETIVES_ES);
    }

    @Override
    public AbstractFinitesPraedikat getFinit(final ITextContext textContext,
                                             @Nullable
                                             final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld konnektor,
                                             final PraedRegMerkmale praedRegMerkmale) {
        praedRegMerkmale.checkExpletivesEs();

        return toPraedikat().getFinit(textContext, konnektor, praedRegMerkmale);
    }

    public ImmutableList<PartizipIIPhrase> getPartizipIIPhrase(
            final ITextContext textContext,
            final boolean nachAnschlusswort) {
        return getPartizipIIPhrasen(textContext, nachAnschlusswort, EXPLETIVES_ES);
    }

    @Override
    @CheckReturnValue
    public ImmutableList<PartizipIIPhrase> getPartizipIIPhrasen(
            final ITextContext textContext,
            final boolean nachAnschlusswort, final PraedRegMerkmale praedRegMerkmale) {
        praedRegMerkmale.checkExpletivesEs();

        return ImmutableList.of(verb.getPartizipIIPhrase());
    }

    public ImmutableList<Infinitiv> getInfinitiv(final ITextContext textContext,
                                                 final boolean nachAnschlusswort) {
        return getInfinitiv(textContext, nachAnschlusswort, EXPLETIVES_ES);
    }

    @Override
    public ImmutableList<Infinitiv> getInfinitiv(
            final ITextContext textContext,
            final boolean nachAnschlusswort, final PraedRegMerkmale praedRegMerkmale) {
        praedRegMerkmale.checkExpletivesEs();

        return ImmutableList.of(
                new EinfacherInfinitiv(null, verb));
    }

    public ImmutableList<ZuInfinitiv> getZuInfinitiv(final ITextContext textContext,
                                                     final boolean nachAnschlusswort) {
        return getZuInfinitiv(textContext, nachAnschlusswort, EXPLETIVES_ES);
    }

    @Override
    public ImmutableList<ZuInfinitiv> getZuInfinitiv(
            final ITextContext textContext,
            final boolean nachAnschlusswort, final PraedRegMerkmale praedRegMerkmale) {
        praedRegMerkmale.checkExpletivesEs();

        return ImmutableList.of(
                new EinfacherZuInfinitiv(null, verb));
    }

    @Override
    public SemPraedikatSubOhneLeerstellen neg(
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
    public SemPraedikatSubOhneLeerstellen toPraedikat() {
        return new SemPraedikatSubOhneLeerstellen(verb);
    }

    public TopolFelder getTopolFelder(final ITextContext textContext,
                                      final boolean nachAnschlusswort) {
        return getTopolFelder(textContext, new PraedRegMerkmale(P3, SG, UNBELEBT),
                nachAnschlusswort);
    }

    public TopolFelder getTopolFelder(final ITextContext textContext,
                                      final PraedRegMerkmale praedRegMerkmale,
                                      final boolean nachAnschlusswort) {
        return toPraedikat().getTopolFelder(textContext, praedRegMerkmale, nachAnschlusswort);
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
