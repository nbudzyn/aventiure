package de.nb.aventiure2.german.praedikat;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.nb.aventiure2.annotations.Komplement;
import de.nb.aventiure2.annotations.Valenz;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativSkopusSatz;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativVerbAllg;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativWohinWoher;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.Negationspartikelphrase;
import de.nb.aventiure2.german.base.PraedRegMerkmale;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.ITextContext;

/**
 * Ein "semantisches Prädikat" im Perfekt, z.B. <i>ein guter Mensch geworden sein</i>,
 * <i>sich aufgeklart haben </i> oder <i>ein guter Mensch haben werden wollen</i>.
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

    @Nullable
    @Override
    @CheckReturnValue
    public Konstituentenfolge getErstesInterrogativwort() {
        return lexikalischerKern.getErstesInterrogativwort();
    }

    @Nullable
    @Override
    @CheckReturnValue
    public Konstituentenfolge getRelativpronomen() {
        return lexikalischerKern.getRelativpronomen();
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
    public Konstituentenfolge getVerbzweit(
            final ITextContext textContext,
            final PraedRegMerkmale praedRegMerkmale) {
        // bist ein guter Mensch geworden
        // bist ein guter Mensch geworden und immer ehrlich geblieben
        // bist ein guter Mensch geworden und hast dabei viel Mühe gehabt
        // hat sich aufgeklart
        // hat ein guter Mensch werden wollen
        Konstituentenfolge res = null;
        final ImmutableList<PartizipIIPhrase> partizipIIPhrasen =
                lexikalischerKern.getPartizipIIPhrasen(textContext, praedRegMerkmale);
        for (int i = 0; i < partizipIIPhrasen.size(); i++) {
            final PartizipIIPhrase partizipIIPhrase = partizipIIPhrasen.get(i);
            if (res == null) {
                res = Konstituentenfolge.joinToKonstituentenfolge(
                        partizipIIPhrase.getHilfsverb()
                                .getPraesensOhnePartikel(praedRegMerkmale.getPerson(),
                                        praedRegMerkmale.getNumerus()), // "bist"
                        partizipIIPhrase.getPhrase()); // "ein guter Mensch geworden"
            } else {
                res = Konstituentenfolge.joinToKonstituentenfolge(
                        res, // "bist ein guter Mensch geworden"
                        i < partizipIIPhrasen.size() - 1 ? "," : "und",
                        partizipIIPhrase.getHilfsverb()
                                .getPraesensOhnePartikel(praedRegMerkmale.getPerson(),
                                        praedRegMerkmale.getNumerus()), // "hast"
                        partizipIIPhrase.getPhrase()); // "dabei viel Mühe gehabt"
            }
        }

        return res;
    }

    @Override
    public Konstituentenfolge getVerbzweitMitSubjektImMittelfeld(
            final ITextContext textContext,
            final SubstantivischePhrase subjekt) {
        // bist du ein guter Mensch geworden
        // bist du ein guter Mensch geworden und immer ehrlich geblieben
        // bist du ein guter Mensch geworden und hast dabei viel Mühe gehabt
        // hat es sich aufgeklart
        // hat er ein guter Mensch werden wollen
        Konstituentenfolge res = null;
        final ImmutableList<PartizipIIPhrase> partizipIIPhrasen =
                lexikalischerKern.getPartizipIIPhrasen(textContext, subjekt);

        for (int i = 0; i < partizipIIPhrasen.size(); i++) {
            final PartizipIIPhrase partizipIIPhrase = partizipIIPhrasen.get(i);
            if (res == null) {
                res = Konstituentenfolge.joinToKonstituentenfolge(
                        partizipIIPhrase.getHilfsverb().getPraesensOhnePartikel(subjekt), // "bist"
                        subjekt.nomK(), // "du"
                        partizipIIPhrase.getPhrase()); // "ein guter Mensch geworden"
            } else {
                res = Konstituentenfolge.joinToKonstituentenfolge(
                        res, // "bist du ein guter Mensch geworden"
                        i < partizipIIPhrasen.size() - 1 ? "," : "und",
                        partizipIIPhrase.getHilfsverb().getPraesensOhnePartikel(subjekt), // "hast"
                        partizipIIPhrase.getPhrase()); // "dabei viel Mühe gehabt"
            }
        }

        return res;
    }

    @Override
    public Konstituentenfolge getVerbletzt(
            final ITextContext textContext,
            final PraedRegMerkmale praedRegMerkmale) {
        // ein guter Mensch geworden bist
        // ein guter Mensch geworden und immer ehrlich geblieben bist
        // ein guter Mensch geworden bist und dabei viel Mühe gehabt hast
        // sich aufgeklart hat
        // ein guter Mensch hat werden wollen (!) (nicht *"ein guter Mensch werden wollen hat")

        return haengeHilfsverbformAnPartizipIIPhrasenAn(praedRegMerkmale,
                hilfsverb -> hilfsverb.getPraesensOhnePartikel(
                        praedRegMerkmale.getPerson(),
                        praedRegMerkmale.getNumerus()));
    }


    @Override
    public ImmutableList<PartizipIIPhrase> getPartizipIIPhrasen(
            final ITextContext textContext,
            final PraedRegMerkmale praedRegMerkmale) {
        // Doppeltes Perfekt - man sollte schon einen sehr guten Grund haben,
        // das zu erzeugen:
        // ein guter Mensch geworden gewesen
        // ein guter Mensch geworden und immer ehrlich geblieben gewesen
        // ein guter Mensch geworden gewesen / dabei viel Mühe gehabt gehabt
        // sich aufgeklart gehabt
        // "(Das hatte er vorher nicht) wahrhaben wollen gehabt." (Ersatzinfinitiv!)
        // gesagt gehabt: "Hallo!"

        final ImmutableList.Builder<PartizipIIPhrase> res = ImmutableList.builder();

        final ImmutableList<PartizipIIPhrase> partizipIIPhrasenLexKern =
                lexikalischerKern.getPartizipIIPhrasen(textContext, praedRegMerkmale);

        for (int i = 0; i < partizipIIPhrasenLexKern.size(); i++) {
            final PartizipIIPhrase partizipIIPhraseLexKern = partizipIIPhrasenLexKern.get(i);

            if (i < partizipIIPhrasenLexKern.size() - 1) {
                res.add(new PartizipIIPhrase(
                        Konstituentenfolge.joinToKonstituentenfolge(
                                partizipIIPhraseLexKern.getPhrase(),  // "ein guter Mensch geworden"
                                partizipIIPhraseLexKern.getHilfsverb().getPartizipII()),
                        // "gewesen"
                        partizipIIPhraseLexKern.getPerfektbildung())); // (sein)
            } else {
                @Nullable final Konstituentenfolge nachfeld = getNachfeld(praedRegMerkmale);

                res.add(new PartizipIIPhrase(
                        Konstituentenfolge.joinToKonstituentenfolge(
                                partizipIIPhraseLexKern.getPhrase().cutLast(nachfeld), // "gesagt"
                                partizipIIPhraseLexKern.getHilfsverb().getPartizipII(), // "gehabt"
                                nachfeld), // : "Hallo!"
                        partizipIIPhraseLexKern.getPerfektbildung())); // (haben)
            }
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
    public Konstituentenfolge getInfinitiv(
            final ITextContext textContext,
            final PraedRegMerkmale praedRegMerkmale) {
        // ein guter Mensch geworden sein
        // ein guter Mensch geworden und immer ehrlich geblieben sein
        // ein guter Mensch geworden sein und dabei viel Mühe gehabt haben
        // sich aufgeklart haben
        // ein guter Mensch haben werden wollen (!) (nicht *"ein guter Mensch werden wollen haben")

        return haengeHilfsverbformAnPartizipIIPhrasenAn(praedRegMerkmale,
                Verb::getInfinitiv);
    }

    @Override
    public Konstituentenfolge getZuInfinitiv(
            final ITextContext textContext,
            final PraedRegMerkmale praedRegMerkmale) {
        // ein guter Mensch geworden zu sein
        // ein guter Mensch geworden und immer ehrlich geblieben zu sein
        // ein guter Mensch geworden zu sein und dabei viel Mühe gehabt zu haben
        // sich aufgeklart zu haben
        // gesagt zu haben: "Hallo!"

        // FIXME "(es ist wichtig, das) sagen gewollt zu haben" (in diesem
        //  Spezialfall *keine* Umstellung und auch *kein Ersatzinfinitiv*!)

        return haengeHilfsverbformAnPartizipIIPhrasenAn(praedRegMerkmale,
                Verb::getZuInfinitiv);
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

    @Nullable
    @Override
    public Konstituente getSpeziellesVorfeldSehrErwuenscht(final PraedRegMerkmale praedRegMerkmale,
                                                           final boolean nachAnschlusswort) {
        // "Danach hast den Wald verlassen."
        return lexikalischerKern.getSpeziellesVorfeldSehrErwuenscht(praedRegMerkmale,
                nachAnschlusswort);
    }

    @Nullable
    @Override
    public Konstituentenfolge getSpeziellesVorfeldAlsWeitereOption(
            final PraedRegMerkmale praedRegMerkmale) {
        // "Spannendes hat er berichtet."
        return lexikalischerKern.getSpeziellesVorfeldAlsWeitereOption(praedRegMerkmale);
    }

    @Nullable
    @Override
    public Konstituentenfolge getNachfeld(final PraedRegMerkmale praedRegMerkmale) {
        // Wir gehen oben implizit davon aus, dass - sollte der lexikalische Kern
        // mehrere Partizipien erfordern - das Nachfeld immer und ausschließlich
        // aus dem letzten der Partizipien stammt!

        return lexikalischerKern.getNachfeld(praedRegMerkmale);
    }

    @Override
    public boolean inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich() {
        // "Mich hat gefroren".
        return lexikalischerKern.inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich();
    }

    private Konstituentenfolge haengeHilfsverbformAnPartizipIIPhrasenAn(
            final PraedRegMerkmale praedRegMerkmale,
            final Function<Verb, String> verbformBuilder) {
        // ein guter Mensch geworden bist
        // ein guter Mensch geworden und immer ehrlich geblieben bist
        // ein guter Mensch geworden bist und dabei viel Mühe gehabt hast
        // sich aufgeklart hat
        // ein guter Mensch hat werden wollen (!) (nicht *"ein guter Mensch werden wollen hat")
        Konstituentenfolge res = null;
        final ImmutableList<PartizipIIPhrase> partizipIIPhrasen =
                lexikalischerKern.getPartizipIIPhrasen(textContext, praedRegMerkmale);

        @Nullable final Konstituentenfolge nachfeld = getNachfeld(praedRegMerkmale);
        for (int i = 0; i < partizipIIPhrasen.size(); i++) {
            final PartizipIIPhrase partizipIIPhrase = partizipIIPhrasen.get(i);
            if (res == null) {
                if (i < partizipIIPhrasen.size() - 1) {
                    res = Konstituentenfolge.joinToKonstituentenfolge(
                            partizipIIPhrase.getPhrase(), // "ein guter Mensch geworden"
                            verbformBuilder.apply(partizipIIPhrase.getHilfsverb())); // "bist"
                    // FIXME "ein guter Mensch hat werden wollen", nicht
                    //  ?"ein guter Mensch werden wollen hat"
                    //  Vielleicht .getMittelfeld() und .getRechteSatzklammer() aufrufen?
                } else {
                    res = Konstituentenfolge.joinToKonstituentenfolge(
                            partizipIIPhrase.getPhrase().cutLast(nachfeld), // "gesagt"
                            verbformBuilder.apply(partizipIIPhrase.getHilfsverb()), // "hast"
                            nachfeld);  // : "Hallo!"
                    // FIXME "ein guter Mensch hat werden wollen", nicht
                    //  ?"ein guter Mensch werden wollen hat"
                    //  Vielleicht .getMittelfeld() und .getRechteSatzklammer() aufrufen?
                }
            } else {
                if (i < partizipIIPhrasen.size() - 1) {
                    res = Konstituentenfolge.joinToKonstituentenfolge(
                            res, // "ein guter Mensch geworden bist"
                            i < partizipIIPhrasen.size() - 1 ? "," : "und",
                            partizipIIPhrase.getPhrase(), // "dabei viel Mühe gehabt"
                            verbformBuilder.apply(partizipIIPhrase.getHilfsverb())); // "hast"
                    // FIXME "ein guter Mensch hat werden wollen", nicht
                    //  ?"ein guter Mensch werden wollen hat"
                    //  Vielleicht .getMittelfeld() und .getRechteSatzklammer() aufrufen?
                } else {
                    res = Konstituentenfolge.joinToKonstituentenfolge(
                            res, // "ein guter Mensch geworden bist"
                            i < partizipIIPhrasen.size() - 1 ? "," : "und",
                            partizipIIPhrase.getPhrase().cutLast(nachfeld), // "gesagt"
                            verbformBuilder.apply(partizipIIPhrase.getHilfsverb()), // "hast"
                            nachfeld); // : "Hallo!"
                    // FIXME "ein guter Mensch hat werden wollen", nicht
                    //  ?"ein guter Mensch werden wollen hat"
                    //  Vielleicht .getMittelfeld() und .getRechteSatzklammer() aufrufen?
                }
            }
        }

        return res;
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