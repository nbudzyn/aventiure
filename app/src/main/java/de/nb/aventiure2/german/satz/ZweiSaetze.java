package de.nb.aventiure2.german.satz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.Objects;

import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbWohinWoher;
import de.nb.aventiure2.german.praedikat.Modalpartikel;
import de.nb.aventiure2.german.praedikat.PraedikatOhneLeerstellen;
import de.nb.aventiure2.german.praedikat.ZweiPraedikateOhneLeerstellen;

import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToKonstituentenfolge;

public class ZweiSaetze implements Satz {
    private final EinzelnerSatz ersterSatz;
    private final EinzelnerSatz zweiterSatz;

    private ZweiSaetze(
            final EinzelnerSatz ersterSatz,
            final EinzelnerSatz zweiterSatz) {
        this.ersterSatz = ersterSatz;
        this.zweiterSatz = zweiterSatz;
    }

    @Override
    public ZweiSaetze mitAnschlusswort(@Nullable final String anschlusswort) {
        return new ZweiSaetze(
                ersterSatz.mitAnschlusswort(anschlusswort),
                zweiterSatz);
    }

    @Override
    public ZweiSaetze mitSubjektFokuspartikel(@Nullable final String subjektFokuspartikel) {
        return new ZweiSaetze(
                ersterSatz.mitSubjektFokuspartikel(subjektFokuspartikel),
                zweiterSatz);
    }

    @Override
    public ZweiSaetze mitModalpartikeln(final Collection<Modalpartikel> modalpartikeln) {
        return new ZweiSaetze(
                ersterSatz.mitModalpartikeln(modalpartikeln),
                zweiterSatz);
    }

    @Override
    public ZweiSaetze mitAdvAngabe(@Nullable final AdvAngabeSkopusSatz advAngabe) {
        return new ZweiSaetze(
                ersterSatz.mitAdvAngabe(advAngabe),
                zweiterSatz);
    }

    @Override
    public ZweiSaetze mitAdvAngabe(@Nullable final AdvAngabeSkopusVerbAllg advAngabe) {
        return new ZweiSaetze(
                ersterSatz.mitAdvAngabe(advAngabe),
                zweiterSatz);
    }

    @Override
    public ZweiSaetze mitAdvAngabe(@Nullable final AdvAngabeSkopusVerbWohinWoher advAngabe) {
        return new ZweiSaetze(
                ersterSatz.mitAdvAngabe(advAngabe),
                zweiterSatz);
    }

    @Override
    public Konstituentenfolge getIndirekteFrage() {
        return joinToKonstituentenfolge(
                ersterSatz.getIndirekteFrage(),
                ", und",
                zweiterSatz.getIndirekteFrage());
    }

    @Override
    public Konstituentenfolge getRelativsatz() {
        return joinToKonstituentenfolge(
                ersterSatz.getRelativsatz(),
                ", und",
                zweiterSatz.getRelativsatz());
    }

    @Override
    public Konstituentenfolge getVerbzweitsatzMitSpeziellemVorfeldAlsWeitereOption() {
        return joinToKonstituentenfolge(
                ersterSatz.getVerbzweitsatzMitSpeziellemVorfeldAlsWeitereOption(),
                ", und",
                zweiterSatz.getVerbzweitsatzStandard());
    }

    @NonNull
    @Override
    public ImmutableList<Konstituentenfolge> altVerzweitsaetze() {
        final ImmutableList.Builder<Konstituentenfolge> res = ImmutableList.builder();

        for (final Konstituentenfolge ersterVerbzweitsatz : ersterSatz.altVerzweitsaetze()) {
            for (final Konstituentenfolge zweiterVerbzweitsatz : zweiterSatz.altVerzweitsaetze()) {
                res.add(joinToKonstituentenfolge(
                        ersterVerbzweitsatz, ", und", zweiterVerbzweitsatz));
            }
        }

        return res.build();
    }

    @Override
    public Konstituentenfolge getVerbzweitsatzStandard() {
        return joinToKonstituentenfolge(
                ersterSatz.getVerbzweitsatzStandard(), ", und",
                zweiterSatz.getVerbzweitsatzStandard());
    }

    @Override
    public Konstituentenfolge getVerbzweitsatzMitVorfeld(final String vorfeld) {
        return joinToKonstituentenfolge(
                ersterSatz.getVerbzweitsatzMitVorfeld(vorfeld), ", und",
                zweiterSatz.getVerbzweitsatzStandard());
    }

    @Override
    public Konstituentenfolge getSatzanschlussOhneSubjekt() {
        return joinToKonstituentenfolge(
                ersterSatz.getSatzanschlussOhneSubjekt(), "und",
                zweiterSatz.getSatzanschlussOhneSubjekt());
    }

    @Override
    public boolean hasSubjektDu() {
        return ersterSatz.hasSubjektDu() && zweiterSatz.hasSubjektDu();
    }

    @Override
    public PraedikatOhneLeerstellen getPraedikat() {
        return new ZweiPraedikateOhneLeerstellen(
                ersterSatz.getPraedikat(), zweiterSatz.getPraedikat());
    }

    @Override
    public boolean isSatzreihungMitUnd() {
        return true;
    }

    @Override
    public SubstantivischePhrase getErstesSubjekt() {
        return ersterSatz.getSubjekt();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ZweiSaetze that = (ZweiSaetze) o;
        return ersterSatz.equals(that.ersterSatz) &&
                zweiterSatz.equals(that.zweiterSatz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ersterSatz, zweiterSatz);
    }
}
