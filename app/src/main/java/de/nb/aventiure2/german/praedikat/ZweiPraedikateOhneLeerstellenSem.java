package de.nb.aventiure2.german.praedikat;

import static de.nb.aventiure2.german.base.NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld.UND;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.Objects;

import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativSkopusSatz;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativVerbAllg;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativWohinWoher;
import de.nb.aventiure2.german.base.NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld;
import de.nb.aventiure2.german.base.Negationspartikelphrase;
import de.nb.aventiure2.german.base.PraedRegMerkmale;
import de.nb.aventiure2.german.description.ITextContext;

/**
 * Zwei Prädikate mit Objekt ohne Leerstellen, erzeugen einen
 * <i>zusammengezogenen SemSatz</i>, in dem das Subjekt im zweiten Teil
 * <i>eingespart</i> ist ("Du hebst die Kugel auf und [du] nimmst ein Bad").
 */
public class ZweiPraedikateOhneLeerstellenSem
        implements SemPraedikatOhneLeerstellen {
    private final SemPraedikatOhneLeerstellen erstes;

    /**
     * der Konnektor zwischen den beiden Prädikaten:
     * "und", "aber", "oder" oder "sondern"
     */
    @Nullable
    private final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld konnektor;

    private final SemPraedikatOhneLeerstellen zweites;

    public ZweiPraedikateOhneLeerstellenSem(
            final SemPraedikatOhneLeerstellen erstes,
            final SemPraedikatOhneLeerstellen zweites) {
        this(erstes, UND, zweites);
    }

    public ZweiPraedikateOhneLeerstellenSem(
            final SemPraedikatOhneLeerstellen erstes,
            @Nullable final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld konnektor,
            final SemPraedikatOhneLeerstellen zweites) {
        this.erstes = erstes;
        this.konnektor = konnektor;
        this.zweites = zweites;
    }

    @Override
    public ZweiPraedikateOhneLeerstellenSem mitModalpartikeln(
            final Collection<Modalpartikel> modalpartikeln) {
        return new ZweiPraedikateOhneLeerstellenSem(
                erstes.mitModalpartikeln(modalpartikeln),
                konnektor,
                zweites
        );
    }

    @Override
    public ZweiPraedikateOhneLeerstellenSem mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabe) {
        return new ZweiPraedikateOhneLeerstellenSem(
                erstes.mitAdvAngabe(advAngabe),
                konnektor,
                zweites
        );
    }

    @Override
    public ZweiPraedikateOhneLeerstellenSem neg(
            @Nullable final Negationspartikelphrase negationspartikelphrase) {
        return new ZweiPraedikateOhneLeerstellenSem(
                erstes.neg(negationspartikelphrase),
                konnektor,
                zweites.neg(negationspartikelphrase)
        );
    }

    @Override
    public ZweiPraedikateOhneLeerstellenSem mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabe) {
        return new ZweiPraedikateOhneLeerstellenSem(
                erstes.mitAdvAngabe(advAngabe),
                konnektor,
                zweites
        );
    }

    @Override
    public ZweiPraedikateOhneLeerstellenSem mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativWohinWoher advAngabe) {
        return new ZweiPraedikateOhneLeerstellenSem(
                erstes.mitAdvAngabe(advAngabe),
                konnektor,
                zweites
        );
    }

    @Override
    public ImmutableList<AbstractFinitesPraedikat> getFinitePraedikate(
            final ITextContext textContext,
            @Nullable final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld anschlusswort,
            final PraedRegMerkmale praedRegMerkmale) {
        final ImmutableList.Builder<AbstractFinitesPraedikat> res = ImmutableList.builder();
        res.addAll(erstes.getFinitePraedikate(textContext, anschlusswort, praedRegMerkmale));
        res.addAll(zweites.getFinitePraedikate(textContext, konnektor, praedRegMerkmale));
        return res.build();
    }

    @Override
    // FIXME Funktioniert, aber das Konzept schließt
    //  wohl viele Möglichkeiten wie ("... und..., aber..." oder "..., ... und ...."
    //  aus. Konzept verbessern?
    public boolean hauptsatzLaesstSichBeiGleichemSubjektMitNachfolgendemVerbzweitsatzZusammenziehen() {
        // Etwas vermeiden wie "Du hebst die Kugel auf und polierst sie und nimmst eine
        // von den Früchten"

        if (!erstes
                .hauptsatzLaesstSichBeiGleichemSubjektMitNachfolgendemVerbzweitsatzZusammenziehen()) {
            return false;
        }

        if (!zweites
                .hauptsatzLaesstSichBeiGleichemSubjektMitNachfolgendemVerbzweitsatzZusammenziehen()) {
            return false;
        }

        return konnektor == null;
    }

    @Override
    public ImmutableList<PartizipIIOderErsatzInfinitivPhrase> getPartizipIIOderErsatzInfinitivPhrasen(
            final ITextContext textContext, final boolean nachAnschlusswort,
            final PraedRegMerkmale praedRegMerkmale) {
        final ImmutableList.Builder<PartizipIIOderErsatzInfinitivPhrase> res =
                ImmutableList.builder();

        for (final PartizipIIOderErsatzInfinitivPhrase partizipIIOderErsatzInfinitivPhrase :
                erstes.getPartizipIIOderErsatzInfinitivPhrasen(textContext, nachAnschlusswort,
                        praedRegMerkmale)) {
            res.add(partizipIIOderErsatzInfinitivPhrase);
        }

        final ImmutableList<PartizipIIOderErsatzInfinitivPhrase>
                partizipIIOderErsatzInfinitivPhrasen =
                zweites.getPartizipIIOderErsatzInfinitivPhrasen(textContext, nachAnschlusswort,
                        praedRegMerkmale);
        for (int i = 0;
             i < partizipIIOderErsatzInfinitivPhrasen.size(); i++) {
            final PartizipIIOderErsatzInfinitivPhrase partizipIIOderErsatzInfinitivPhrase =
                    partizipIIOderErsatzInfinitivPhrasen.get(i);
            res.add(
                    i == 0 ?
                            partizipIIOderErsatzInfinitivPhrase.mitKonnektor(konnektor)
                            :
                            (i == partizipIIOderErsatzInfinitivPhrasen.size() - 1 ?
                                    partizipIIOderErsatzInfinitivPhrase
                                            .mitKonnektorUndFallsKeinKonnektor() :
                                    partizipIIOderErsatzInfinitivPhrase));
        }

        return res.build();
    }

    @Override
    public ImmutableList<PartizipIIPhrase> getPartizipIIPhrasen(
            final ITextContext textContext,
            final boolean nachAnschlusswort, final PraedRegMerkmale praedRegMerkmale) {
        final ImmutableList.Builder<PartizipIIPhrase> res = ImmutableList.builder();

        for (final PartizipIIPhrase partizipIIPhrase :
                erstes.getPartizipIIPhrasen(textContext, nachAnschlusswort, praedRegMerkmale)) {
            res.add(partizipIIPhrase);
        }

        final ImmutableList<PartizipIIPhrase> partizipIIPhrasen =
                zweites.getPartizipIIPhrasen(textContext, nachAnschlusswort, praedRegMerkmale);
        for (int i = 0;
             i < partizipIIPhrasen.size(); i++) {
            final PartizipIIPhrase partizipIIPhrase =
                    partizipIIPhrasen.get(i);
            res.add(
                    i == 0 ?
                            partizipIIPhrase.mitKonnektor(konnektor)
                            :
                            (i == partizipIIPhrasen.size() - 1 ?
                                    partizipIIPhrase
                                            .mitKonnektorUndFallsKeinKonnektor() :
                                    partizipIIPhrase));
        }

        return res.build();
    }

    @Override
    public boolean kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden() {
        return erstes.kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden() &&
                zweites.kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden();
    }

    @Override
    public ImmutableList<Infinitiv> getInfinitiv(
            final ITextContext textContext,
            final boolean nachAnschlusswort, final PraedRegMerkmale praedRegMerkmale) {
        final ImmutableList.Builder<Infinitiv> res = ImmutableList.builder();

        for (final Infinitiv infinitiv :
                erstes.getInfinitiv(textContext, nachAnschlusswort, praedRegMerkmale)) {
            res.add(infinitiv);
        }

        final ImmutableList<Infinitiv> infinitive =
                zweites.getInfinitiv(textContext, nachAnschlusswort, praedRegMerkmale);
        for (int i = 0;
             i < infinitive.size(); i++) {
            final Infinitiv infinitiv = infinitive.get(i);
            res.add(
                    i == 0 ?
                            infinitiv.mitKonnektor(konnektor)
                            :
                            (i == infinitive.size() - 1 ?
                                    infinitiv
                                            .mitKonnektorUndFallsKeinKonnektor() :
                                    infinitiv));
        }

        return res.build();
    }

    @Override
    public ImmutableList<ZuInfinitiv> getZuInfinitiv(
            final ITextContext textContext,
            final boolean nachAnschlusswort, final PraedRegMerkmale praedRegMerkmale) {
        final ImmutableList.Builder<ZuInfinitiv> res = ImmutableList.builder();

        for (final ZuInfinitiv zuInfinitiv :
                erstes.getZuInfinitiv(textContext, nachAnschlusswort, praedRegMerkmale)) {
            res.add(zuInfinitiv);
        }

        final ImmutableList<ZuInfinitiv> zuInfinitive =
                zweites.getZuInfinitiv(textContext, nachAnschlusswort, praedRegMerkmale);
        for (int i = 0;
             i < zuInfinitive.size(); i++) {
            final ZuInfinitiv zuInfinitiv = zuInfinitive.get(i);
            res.add(
                    i == 0 ?
                            zuInfinitiv.mitKonnektor(konnektor)
                            :
                            (i == zuInfinitive.size() - 1 ?
                                    zuInfinitiv
                                            .mitKonnektorUndFallsKeinKonnektor() :
                                    zuInfinitiv));
        }

        return res.build();
    }

    @Override
    public boolean umfasstSatzglieder() {
        return erstes.umfasstSatzglieder() && zweites.umfasstSatzglieder();
    }

    @Override
    public boolean hatAkkusativobjekt() {
        return erstes.hatAkkusativobjekt() && zweites.hatAkkusativobjekt();
    }

    @Override
    public boolean isBezugAufNachzustandDesAktantenGegeben() {
        return erstes.isBezugAufNachzustandDesAktantenGegeben() &&
                zweites.isBezugAufNachzustandDesAktantenGegeben();
    }

    @Override
    public boolean inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich() {
        // "Mich friert und hungert".
        // Aber: "Es friert mich und regnet."
        return erstes.inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich()
                && zweites.inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ZweiPraedikateOhneLeerstellenSem that = (ZweiPraedikateOhneLeerstellenSem) o;
        return Objects.equals(erstes, that.erstes) &&
                konnektor == that.konnektor &&
                Objects.equals(zweites, that.zweites);
    }

    @Override
    public int hashCode() {
        return Objects.hash(erstes, konnektor, zweites);
    }
}
