package de.nb.aventiure2.german.description;

import static java.util.Optional.ofNullable;

import android.util.Pair;

import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.Map;
import java.util.Set;

import javax.annotation.concurrent.Immutable;

import de.nb.aventiure2.german.base.Belebtheit;
import de.nb.aventiure2.german.base.IBezugsobjekt;
import de.nb.aventiure2.german.base.NumerusGenus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.base.PhorikKandidat;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

/**
 * Eine <i>immutable</i>-Implementierung des textuellen Kontexts, in dem etwas beschrieben wird.
 * Der Kontext liefert Informationen über mögliche anaphorische
 * Bezüge <i>in der dritten Person</i>, die möglich sind ("Rapunzel schaut aus dem Fenster.
 * <i>Sie</i> ist schön anzusehen.") oder vielleicht sogar zwingend sind, wenn man
 * z.B. eine possessive Angabe machen möchte ("Rapunzel kämmt <i>ihre</i> Haare.",
 * aber nicht *"Rapunzel kämmt <i>Rapunzels</i> Haare").
 * <p>
 * Achtung! Der <code>ImmutableTextContext</code> muss alle anaphorischen Bezüge
 * enthalten, die <i>für possessive Angaben zwingend</i> sind! Ansonsten
 * könnte etwas generiert werden wie *"Rapunzel kämmt <i>Rapunzels</i> Haare"!
 * <p>
 * Der Aufrufer erhält immer dieselben Ergebnisse.
 * <p>
 * Diese Implementierung liefert keine Bezüge der ersten oder zweiten Person ("ich", "dein", ...).
 */
@Immutable
public class ImmutableTextContext implements ITextContext {
    /**
     * Der "leerer" textueller Kontext.
     * Anaphorische Bezüge, sind weder möglich noch zwingend.
     * <p>
     * Achtung! Dies hier darf nur verwendet werden, wenn es keine anaphorischen Bezüge
     * gibt, die <i>für possessive Angaben zwingend</i> sind! Ansonsten
     * könnte etwas generiert werden wie *"Rapunzel kämmt <i>Rapunzels</i> Haare"!
     */
    public static final ImmutableTextContext EMPTY = new ImmutableTextContext();

    /**
     * Alle möglichen anaphorische Bezüge, die für die dritte Person möglich sind ("Rapunzel
     * schaut aus dem Fenster. <i>Sie</i> ist schön anzusehen.") oder vielleicht sogar zwingend
     * sind, wenn man z.B. eine possessive Angabe machen möchte ("Rapunzel kämmt <i>ihre</i>
     * Haare.", aber nicht *"Rapunzel kämmt <i>Rapunzels</i> Haare").
     * <p>
     * Achtung! Fehlen hier anaphorischen Bezüge, die <i>für possessive Angaben zwingend</i>
     * sind, könnte etwas etwas generiert werden wie *"Rapunzel kämmt <i>Rapunzels</i> Haare"!
     */
    private final ImmutableMap<IBezugsobjekt, Pair<NumerusGenus, Belebtheit>> mglBezuegeP3;

    private ImmutableTextContext() {
        this(ImmutableSet.of());
    }

    /**
     * Erzeugt einen textuellen Kontext, in dem nur anaphorische
     * Bezüge auf durch diese Phrase beschriebene Bezugsobjekt möglich und zwingen sind
     * (in der dritten Person).
     * <p>
     * Achtung! Der <code>ImmutableTextContext</code> muss alle anaphorischen Bezüge
     * enthalten, die <i>für possessive Angaben zwingend</i> sind! Ansonsten
     * könnte etwas generiert werden wie *"Rapunzel kämmt <i>Rapunzels</i> Haare"!
     *
     * @param substantivischePhrase Die Phrase, die das Objekt beschreibt, auf das Bezüge
     *                              möglich sind. Es muss ein Bezugsobjekt angegeben sein.
     */
    public ImmutableTextContext(@Nullable final SubstantivischePhrase substantivischePhrase) {
        this(ofNullable(substantivischePhrase).map(SubstantivischePhrase::getPhorikKandidat)
                .orElse(null));
    }

    private ImmutableTextContext(@Nullable final PhorikKandidat mglBezugP3) {
        this(ofNullable(mglBezugP3).map(ImmutableSet::of).orElse(ImmutableSet.of()));
    }

    private ImmutableTextContext(final Set<PhorikKandidat> mglBezuegeP3) {
        this(mglBezuegeP3.stream()
                .collect(ImmutableMap.toImmutableMap(
                        PhorikKandidat::getBezugsobjekt,
                        pk -> Pair.create(pk.getNumerusGenus(), pk.getBelebtheit()))));
    }

    private ImmutableTextContext(
            final Map<IBezugsobjekt, Pair<NumerusGenus, Belebtheit>> mglBezuegeP3) {
        this.mglBezuegeP3 = ImmutableMap.copyOf(mglBezuegeP3);
    }

    @Nullable
    @Override
    public NumerusGenus getNumerusGenusAnaphWennMgl(final IBezugsobjekt bezugsobjekt) {
        return ofNullable(mglBezuegeP3.get(bezugsobjekt))
                .map(p -> p.first)
                .orElse(null);
    }

    @Nullable
    @Override
    public Personalpronomen getAnaphPersPronWennMgl(final IBezugsobjekt bezugsobjekt) {
        return ofNullable(mglBezuegeP3.get(bezugsobjekt))
                .map(p -> Personalpronomen.get(Person.P3, p.first, p.second))
                .orElse(null);
    }
}
