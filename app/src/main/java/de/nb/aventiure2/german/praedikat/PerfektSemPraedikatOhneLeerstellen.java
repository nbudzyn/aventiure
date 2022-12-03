package de.nb.aventiure2.german.praedikat;

import static java.util.Objects.requireNonNull;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.nb.aventiure2.annotations.Komplement;
import de.nb.aventiure2.annotations.Valenz;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativSkopusSatz;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativVerbAllg;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativWohinWoher;
import de.nb.aventiure2.german.base.NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld;
import de.nb.aventiure2.german.base.Negationspartikelphrase;
import de.nb.aventiure2.german.base.PraedRegMerkmale;
import de.nb.aventiure2.german.description.ITextContext;

/**
 * Ein "semantisches Prädikat" im Perfekt, z.B. <i>ein guter Mensch geworden sein</i>,
 * <i>sich aufgeklart haben</i> oder <i>ein guter Mensch haben werden wollen</i>.
 * Umfasst auch Zusammensetzungen wie "ein guter Mensch und immer freundlich sein" oder
 * "ein guter Mensch sein und immer allen geholfen haben".
 */
public class PerfektSemPraedikatOhneLeerstellen implements SemPraedikatOhneLeerstellen {
    /**
     * Das Prädikat in seiner "ursprünglichen" Form (nicht im Perfekt). Die
     * "ursprüngliche Form" von <i>ein guter Mensch geworden sein</i> ist z.B.
     * <i>ein guter Mensch werden</i>.
     */
    @Nonnull
    @Komplement
    private final SemPraedikatOhneLeerstellen lexikalischerKern;

    @Valenz
    PerfektSemPraedikatOhneLeerstellen(final SemPraedikatOhneLeerstellen lexikalischerKern) {
        this.lexikalischerKern = lexikalischerKern;
    }

    @Override
    public PerfektSemPraedikatOhneLeerstellen mitModalpartikeln(
            final Collection<Modalpartikel> modalpartikeln) {
        // Bei einem Prädikat wie "halt sich aufgeklart haben"
        // trägt *"halt haben" keine wirkliche Bedeutung, "sich halt aufklaren"
        // aber durchaus.  Also speichern wir die Modalpartikeln im lexikalischen Kern und
        // erlauben keine zusätzlichen Angaben für das Hilfsverb haben / sein.
        return new PerfektSemPraedikatOhneLeerstellen(
                lexikalischerKern.mitModalpartikeln(modalpartikeln));
    }

    @Override
    public PerfektSemPraedikatOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabe) {
        // Bei einem Prädikat wie "sich leider aufgeklart haben"
        // trägt *"leider haben" keine wirkliche Bedeutung, "sich leider aufklaren"
        // aber durchaus.  Also speichern wir die Modalpartikeln im lexikalischen Kern und
        // erlauben keine zusätzlichen Angaben für das Hilfsverb haben / sein.
        return new PerfektSemPraedikatOhneLeerstellen(
                lexikalischerKern.mitAdvAngabe(advAngabe));
    }

    @Override
    public SemPraedikatOhneLeerstellen neg(
            @Nullable final Negationspartikelphrase negationspartikelphrase) {
        return new PerfektSemPraedikatOhneLeerstellen(
                lexikalischerKern.neg(negationspartikelphrase));
    }

    @Override
    public PerfektSemPraedikatOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativVerbAllg advAngabe) {
        return new PerfektSemPraedikatOhneLeerstellen(
                lexikalischerKern.mitAdvAngabe(advAngabe));
    }

    @Override
    public PerfektSemPraedikatOhneLeerstellen mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativWohinWoher advAngabe) {
        return new PerfektSemPraedikatOhneLeerstellen(
                lexikalischerKern.mitAdvAngabe(advAngabe));
    }

    @Override
    public boolean hauptsatzLaesstSichBeiGleichemSubjektMitNachfolgendemVerbzweitsatzZusammenziehen() {
        // Diese Einschränkung hängt wohl hauptsächlich vom Nachfeld ab, dass
        // auch in der zu-haben-Konstruktion das Nachfeld wird:
        // Du sagst: "Hallo!" -> Du hast gesagt: "Hallo!"

        return lexikalischerKern
                .hauptsatzLaesstSichBeiGleichemSubjektMitNachfolgendemVerbzweitsatzZusammenziehen();
    }

    @Override
    public ImmutableList<AbstractFinitesPraedikat> getFinitePraedikate(
            final ITextContext textContext,
            @Nullable final NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld anschlusswort,
            // FIXME anschlusswort wird gar nicht verwendet - ist das richtig?
            final PraedRegMerkmale praedRegMerkmale) {
        // bist ein guter Mensch geworden
        // bist ein guter Mensch geworden und immer ehrlich geblieben
        // bist ein guter Mensch geworden und hast dabei viel Mühe gehabt
        // hat sich aufgeklart
        // hat ein guter Mensch werden wollen (hat -> Ersatzinfinitiv!)
        final ImmutableList.Builder<AbstractFinitesPraedikat> res = ImmutableList.builder();
        final ImmutableList<PartizipIIOderErsatzInfinitivPhrase>
                partizipIIOderErsatzInfinitivPhrasen =
                lexikalischerKern.getPartizipIIOderErsatzInfinitivPhrasen(textContext,
                        // Partizipien stehen nach dem "bist" / "hast", nicht nach Anschlusswort!
                        false,
                        praedRegMerkmale);
        ImmutableList.Builder<PartizipIIOderErsatzInfinitivPhrase> children
                = ImmutableList.builder();

        @Nullable Verb lastHilfsverb = null;
        for (int i = 0; i < partizipIIOderErsatzInfinitivPhrasen.size(); i++) {
            final PartizipIIOderErsatzInfinitivPhrase partizipIIOderErsatzInfinitivPhrase =
                    partizipIIOderErsatzInfinitivPhrasen.get(i);
            final Verb hilfsverb = partizipIIOderErsatzInfinitivPhrase.getHilfsverbFuerPerfekt();
            if (lastHilfsverb != null && !lastHilfsverb.equals(hilfsverb)) {
                res.add(new KomplexesFinitesPraedikat(
                        requireNonNull(hilfsverb
                                .getPraesensOhnePartikel(praedRegMerkmale.getPerson(),
                                        praedRegMerkmale.getNumerus())), // "bist"
                        null,
                        children.build() // "ein guter Mensch geworden", "immer glücklich gewesen"
                ));

                children = ImmutableList.builder();
            }

            children.add(partizipIIOderErsatzInfinitivPhrase
                    // "ein guter Mensch geworden"
            );

            lastHilfsverb = hilfsverb;
        }

        res.add(new KomplexesFinitesPraedikat(
                requireNonNull(
                        requireNonNull(lastHilfsverb)
                                .getPraesensOhnePartikel(praedRegMerkmale.getPerson(),
                                        praedRegMerkmale.getNumerus())), // "bist"
                null,
                children.build() // "ein guter Mensch geworden", "immer glücklich gewesen"
        ));

        return res.build();
    }

    @Override
    public ImmutableList<PartizipIIOderErsatzInfinitivPhrase> getPartizipIIOderErsatzInfinitivPhrasen(
            final ITextContext textContext, final boolean nachAnschlusswort,
            final PraedRegMerkmale praedRegMerkmale) {
        // Doppeltes Perfekt - man sollte schon einen sehr guten Grund haben,
        // das zu erzeugen:
        // ein guter Mensch geworden gewesen
        // ein guter Mensch geworden und immer ehrlich geblieben gewesen
        // ein guter Mensch geworden gewesen / dabei viel Mühe gehabt gehabt
        // sich aufgeklart gehabt
        // "(Das hatte er vorher nicht) haben wissen wollen." (Ersatzinfinitiv
        // "wollen", außerdem Ersatzinfinitiv "haben" und Umstelllung von "haben" ins Oberfeld!)
        // gesagt gehabt: "Hallo!"

        final ImmutableList.Builder<PartizipIIOderErsatzInfinitivPhrase> res =
                ImmutableList.builder();

        final ImmutableList<PartizipIIOderErsatzInfinitivPhrase>
                partizipIIOderErsatzInfinitivPhrasenLexKern =
                lexikalischerKern
                        .getPartizipIIOderErsatzInfinitivPhrasen(textContext, nachAnschlusswort,
                                praedRegMerkmale);

        for (int i = 0; i < partizipIIOderErsatzInfinitivPhrasenLexKern.size(); i++) {
            final PartizipIIOderErsatzInfinitivPhrase partizipIIOderErsatzInfinitivPhraseLexKern =
                    partizipIIOderErsatzInfinitivPhrasenLexKern.get(i);
            // Im Regelfall false. true bei
            // "(Das hatte er vorher nicht) haben wissen wollen.":  (Ersatzinfinitiv
            // "wollen", außerdem Ersatzinfinitiv "haben" und "haben"
            // vorangestellt!)
            final boolean hilfsverbAlsErsatzinfinitivVorangestellt =
                    calcFiniteVerbformBeiVerbletztstellungImOberfeld(
                            partizipIIOderErsatzInfinitivPhraseLexKern);

            res.add(PartizipIIOderErsatzInfinitivPhrase.doppeltesPartizipIIOderErsatzinfinitiv(
                    partizipIIOderErsatzInfinitivPhraseLexKern,
                    hilfsverbAlsErsatzinfinitivVorangestellt));
            // "ein guter Mensch geworden gewesen"
        }

        return res.build();
    }

    /**
     * Ermittelt, ob die finite Verbform bei Verbletztstellung ins Oberfeld gestellt werden soll
     * (also an den Beginn des Verbalkomplexes: "[dass er ]seiner Tochter hat helfen wollen")
     * oder nicht ("dass er seine Tochter gesehen hat").
     */
    private static boolean calcFiniteVerbformBeiVerbletztstellungImOberfeld(
            final PartizipIIOderErsatzInfinitivPhrase partizipIIOderErsatzInfinitivPhrase) {
        // "Zu der Abfolgeregel des Finitums am Ende gibt es folgende Ausnahme: Die finite
        // Form des Hilfsverbs haben steht - bei zwei oder drei Infinitiven - nicht am Ende,
        // sondern am
        // Anfang des gesamten Verbalkomplexes."
        // ( https://grammis.ids-mannheim.de/systematische-grammatik/1241 )

        return partizipIIOderErsatzInfinitivPhrase.getHilfsverbFuerPerfekt()
                .equals(HabenUtil.VERB)
                && Optional.ofNullable(
                partizipIIOderErsatzInfinitivPhrase
                        .getAnzahlGeschachtelteReineInfinitiveWennPhraseNichtsAnderesEnthaelt())
                .orElse(0) >= 2;
    }

    @Override
    public ImmutableList<PartizipIIPhrase> getPartizipIIPhrasen(
            final ITextContext textContext,
            final boolean nachAnschlusswort, final PraedRegMerkmale praedRegMerkmale) {
        // Doppeltes Perfekt - man sollte schon einen sehr guten Grund haben,
        // das zu erzeugen:
        // ein guter Mensch geworden gewesen
        // ein guter Mensch geworden und immer ehrlich geblieben gewesen
        // ein guter Mensch geworden gewesen / dabei viel Mühe gehabt gehabt
        // sich aufgeklart gehabt
        // gesagt gehabt: "Hallo!"

        final ImmutableList.Builder<PartizipIIPhrase> res = ImmutableList.builder();

        final ImmutableList<PartizipIIPhrase> partizipIIPhrasenLexKern =
                lexikalischerKern.getPartizipIIPhrasen(textContext, nachAnschlusswort,
                        praedRegMerkmale);

        for (int i = 0; i < partizipIIPhrasenLexKern.size(); i++) {
            res.add(KomplexePartizipIIPhrase.doppeltesPartizipII(partizipIIPhrasenLexKern.get(i)));
            // "ein guter Mensch geworden gewesen"
        }

        return res.build();
    }

    @Override
    public boolean kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden() {
        // Auch diese Einschränkung hängt wohl hauptsächlich vom Nachfeld ab, dass
        // auch im doppelten Perfekt das Nachfeld wird:
        // gesagt: "Hallo!" -> gesagt gehabt: "Hallo!"

        return lexikalischerKern.kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden();
    }

    @Override
    public ImmutableList<Infinitiv> getInfinitiv(
            final ITextContext textContext,
            final boolean nachAnschlusswort, final PraedRegMerkmale praedRegMerkmale) {
        // ein guter Mensch geworden sein
        // ein guter Mensch geworden und immer ehrlich geblieben sein
        // ein guter Mensch geworden sein und dabei viel Mühe gehabt haben
        // sich aufgeklart haben
        // ein guter Mensch haben werden wollen (!) (nicht *"ein guter Mensch werden wollen haben")

        final ImmutableList.Builder<Infinitiv> res = ImmutableList.builder();
        final ImmutableList<PartizipIIPhrase>
                partizipIIPhrasen =
                lexikalischerKern.getPartizipIIPhrasen(textContext,
                        nachAnschlusswort, praedRegMerkmale);
        ImmutableList.Builder<PartizipIIPhrase> children = ImmutableList.builder();

        @Nullable Verb lastHilfsverb = null;
        for (int i = 0; i < partizipIIPhrasen.size(); i++) {
            final PartizipIIPhrase partizipIIPhrase = partizipIIPhrasen.get(i);
            final Verb hilfsverb = partizipIIPhrase.getHilfsverbFuerPerfekt();
            if (lastHilfsverb != null && !lastHilfsverb.equals(hilfsverb)) {
                res.add(
                        // "ein guter Mensch geworden sein" oder
                        // "ein guter Mensch geworden und immer ehrlich geblieben sein"
                        new KomplexerInfinitiv(
                                requireNonNull(hilfsverb.getInfinitiv()), // "sein"
                                hilfsverb.getPerfektbildung(),
                                children.build()
                                // "ein guter Mensch geworden", "immer glücklich geblieben"
                        ));

                children = ImmutableList.builder();
            }

            children.add(partizipIIPhrase
                    // "ein guter Mensch geworden"
            );

            lastHilfsverb = hilfsverb;
        }

        res.add(
                // "ein guter Mensch geworden sein" oder
                // "ein guter Mensch geworden und immer ehrlich geblieben sein"
                new KomplexerInfinitiv(
                        requireNonNull(requireNonNull(lastHilfsverb).getInfinitiv()), // "sein"
                        lastHilfsverb.getPerfektbildung(),
                        children.build()
                        // "ein guter Mensch geworden", "immer glücklich geblieben"
                ));

        return res.build();
    }

    @Override
    public ImmutableList<ZuInfinitiv> getZuInfinitiv(
            final ITextContext textContext,
            final boolean nachAnschlusswort, final PraedRegMerkmale praedRegMerkmale) {
        // ein guter Mensch geworden zu sein
        // ein guter Mensch geworden und immer ehrlich geblieben zu sein
        // ein guter Mensch geworden zu sein und dabei viel Mühe gehabt zu haben
        // sich aufgeklart zu haben
        // gesagt zu haben: "Hallo!"
        // ein guter Mensch geworden sein
        // ein guter Mensch geworden und immer ehrlich geblieben sein
        // ein guter Mensch geworden sein und dabei viel Mühe gehabt haben
        // sich aufgeklart haben
        // ein guter Mensch haben werden wollen (!) (nicht *"ein guter Mensch werden wollen haben")

        final ImmutableList.Builder<ZuInfinitiv> res = ImmutableList.builder();
        final ImmutableList<PartizipIIPhrase>
                partizipIIPhrasen =
                lexikalischerKern.getPartizipIIPhrasen(textContext,
                        nachAnschlusswort, praedRegMerkmale);
        ImmutableList.Builder<PartizipIIPhrase> children = ImmutableList.builder();

        @Nullable Verb lastHilfsverb = null;
        for (int i = 0; i < partizipIIPhrasen.size(); i++) {
            final PartizipIIPhrase partizipIIPhrase = partizipIIPhrasen.get(i);
            final Verb hilfsverb = partizipIIPhrase.getHilfsverbFuerPerfekt();
            if (lastHilfsverb != null && !lastHilfsverb.equals(hilfsverb)) {
                res.add(
                        // "ein guter Mensch geworden zu sein" oder
                        // "ein guter Mensch geworden und immer ehrlich geblieben zu sein"
                        new KomplexerZuInfinitiv(
                                requireNonNull(hilfsverb.getInfinitiv()), // "sein"
                                children.build()
                                // "ein guter Mensch geworden", "immer glücklich geblieben"
                        ));

                children = ImmutableList.builder();
            }

            children.add(partizipIIPhrase
                    // "ein guter Mensch geworden"
            );

            lastHilfsverb = hilfsverb;
        }

        res.add(
                // "ein guter Mensch geworden zu sein" oder
                // "ein guter Mensch geworden und immer ehrlich geblieben zu sein"
                new KomplexerZuInfinitiv(
                        requireNonNull(requireNonNull(lastHilfsverb).getInfinitiv()), // sein"
                        children.build()
                        // "ein guter Mensch geworden", "immer glücklich geblieben"
                ));

        return res.build();
    }

    @Override
    public boolean umfasstSatzglieder() {
        return lexikalischerKern.umfasstSatzglieder();
    }

    @Override
    public boolean hatAkkusativobjekt() {
        return lexikalischerKern.hatAkkusativobjekt();
    }

    @Override
    public boolean isBezugAufNachzustandDesAktantenGegeben() {
        // Eher wohl nein, denn bei etwas wie "du hast den Wald verlassen" lag ja der
        // "Nachzustand" schon vor, bevor die Information mitgeteilt wurde.
        return false;
    }

    @Override
    public boolean inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich() {
        // "Mich hat gefroren".
        return lexikalischerKern.inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final PerfektSemPraedikatOhneLeerstellen that = (PerfektSemPraedikatOhneLeerstellen) o;
        return lexikalischerKern.equals(that.lexikalischerKern);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lexikalischerKern);
    }
}