package de.nb.aventiure2.german.base;

import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import javax.annotation.CheckReturnValue;

import static de.nb.aventiure2.german.base.GermanUtil.joinToString;
import static de.nb.aventiure2.german.base.Konstituente.k;
import static de.nb.aventiure2.german.base.Konstituentenfolge.kf;
import static de.nb.aventiure2.german.base.Numerus.PL;
import static de.nb.aventiure2.german.base.NumerusGenus.PL_MFN;
import static de.nb.aventiure2.german.base.Person.P3;
import static de.nb.aventiure2.util.StreamUtil.*;
import static java.util.stream.Collectors.toList;

/**
 * Eine Reihung (Aufzählung) von {@link SubstantivischePhrase}-Objekten, z.B.
 * "Peter, Paul und ich".
 */
public class SubstPhrReihung implements SubstantivischePhrase {
    private final ImmutableList<SubstantivischePhrase> elemente;

    SubstPhrReihung(final SubstantivischePhrase first,
                    final SubstantivischePhrase second) {
        this(ImmutableList.of(first, second));
    }

    public SubstPhrReihung(final List<SubstantivischePhrase> elemente) {
        this.elemente = ImmutableList.copyOf(elemente);
    }

    /**
     * Gibt die Phrase ohne Fokuspartikel <i>am Anfang</i> zurück
     */
    @Override
    public SubstPhrReihung ohneFokuspartikel() {
        // Bei etwas wie "sogar Peter, Paul und ich" bezieht sich das
        // sogar wohl auf die gesamte Phrase. Allerdings scheint mir
        // "sogar [[sogar Peter], Paul und ich]" kaum möglich.
        // Deshalb vereinfache ich und tue so, als gehöre die Fokuspartikel
        // der Reihung immer zum ersten Phrasenglied.
        if (elemente.get(0).getFokuspartikel() == null) {
            return this;
        }

        return withFirst(elemente.get(0).ohneFokuspartikel());
    }

    @Override
    public SubstPhrReihung mitFokuspartikel(@Nullable final String fokuspartikel) {
        if (Objects.equals(fokuspartikel, getFokuspartikel())) {
            return this;
        }

        return withFirst(elemente.get(0).mitFokuspartikel(fokuspartikel));
    }

    private SubstPhrReihung withFirst(final SubstantivischePhrase newFirst) {
        return new SubstPhrReihung(
                ImmutableList.<SubstantivischePhrase>builder()
                        .add(newFirst)
                        .addAll(elemente.subList(1, elemente.size()))
                        .build());
    }

    @Override
    public boolean erlaubtVerschmelzungVonPraepositionMitArtikel() {
        return elemente.get(0).erlaubtVerschmelzungVonPraepositionMitArtikel();
    }

    @Override
    public String nomStr() {
        return joinToString(nomK());
    }

    @Override
    public String datStr() {
        return joinToString(datK());
    }

    @Override
    public String artikellosDatStr() {
        return joinToString(artikellosDatK());
    }

    @Override
    public String akkStr() {
        return joinToString(akkK());
    }

    /**
     * Gibt die substantivische Phrase im Dativ, aber ohne Artikel, zurück
     * ("(zum) Haus") - als Konstituente
     */
    @Override
    public Konstituentenfolge artikellosDatK() {
        // "[er bleibt zum] Essen und dem Besäufnis"
        // "[er bleibt zum] Essen und der Besprechung

        return toAufzaehlung(
                ImmutableList.<Konstituentenfolge>builder()
                        .add(elemente.get(0).artikellosDatK())
                        .addAll(
                                mapToList(elemente.subList(1, elemente.size()),
                                        SubstantivischePhrase::datK)
                        )
                        .build());
    }

    @Override
    public Konstituentenfolge imK(final KasusOderPraepositionalkasus kasusOderPraepositionalkasus) {
        return toAufzaehlung(elemente.stream()
                .map(substPhr -> substPhr.imK(kasusOderPraepositionalkasus)));
    }

    private static Konstituentenfolge toAufzaehlung(final Stream<Konstituentenfolge> stream) {
        return toAufzaehlung(stream.collect(toList()));
    }

    @CheckReturnValue
    private static Konstituentenfolge toAufzaehlung(
            final List<? extends Konstituentenfolge> elemente) {
        final ImmutableList.Builder<IKonstituenteOrStructuralElement> res = ImmutableList.builder();

        for (int i = 0; i < elemente.size(); i++) {
            if (i == 0) {
                res.addAll(elemente.get(i));
            } else if (i == elemente.size() - 1) {
                res.add(k("und"));
                res.addAll(elemente.get(i));
            } else {
                res.addAll(elemente.get(i).withVorkommaNoetig());
            }
        }

        return kf(res.build());
    }

    @Override
    public boolean isUnbetontesPronomen() {
        return false;
    }

    public SubstPhrReihung und(final SubstantivischePhrase next) {
        return new SubstPhrReihung(
                ImmutableList.<SubstantivischePhrase>builder()
                        .addAll(elemente)
                        .add(next)
                        .build());
    }

    @Override
    public Personalpronomen persPron() {
        return Personalpronomen.get(getPerson(), getNumerusGenus(), getBezugsobjekt());
    }

    @Override
    public Reflexivpronomen reflPron() {
        return Reflexivpronomen.get(getPerson(), getNumerusGenus().getNumerus());
    }

    @Override
    public Possessivartikel possArt() {
        return Possessivartikel.get(getPerson(), getNumerusGenus());
    }

    @Override
    public Relativpronomen relPron() {
        return Relativpronomen.get(getPerson(), getNumerusGenus(), getBezugsobjekt());
    }

    @Nullable
    @Override
    public String getFokuspartikel() {
        return elemente.get(0).getFokuspartikel();
    }

    @Nullable
    @Override
    public IBezugsobjekt getBezugsobjekt() {
        return null;
    }

    @Override
    public Numerus getNumerus() {
        return PL;
    }

    @Override
    public NumerusGenus getNumerusGenus() {
        return PL_MFN;
    }

    @Override
    public Person getPerson() {
        return P3;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SubstPhrReihung that = (SubstPhrReihung) o;
        return elemente.equals(that.elemente);
    }

    @Override
    public int hashCode() {
        return Objects.hash(elemente);
    }
}
