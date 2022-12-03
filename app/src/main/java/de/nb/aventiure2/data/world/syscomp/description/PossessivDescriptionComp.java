package de.nb.aventiure2.data.world.syscomp.description;

import static java.util.Objects.requireNonNull;
import static de.nb.aventiure2.data.world.syscomp.description.PossessivDescriptionVorgabe.ALLES_ERLAUBT;
import static de.nb.aventiure2.data.world.syscomp.description.PossessivDescriptionVorgabe.NICHT_POSSESSIV;
import static de.nb.aventiure2.german.base.Person.P3;

import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.counter.CounterDao;
import de.nb.aventiure2.data.world.syscomp.description.impl.SimpleDescriptionComp;
import de.nb.aventiure2.german.base.ArtikelwortFlexionsspalte;
import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;
import de.nb.aventiure2.german.base.IArtikelworttypOderVorangestelltesGenitivattribut;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.base.NumerusGenus;
import de.nb.aventiure2.german.base.VorangestelltesGenitivattribut;
import de.nb.aventiure2.german.description.ITextContext;

/**
 * Implementierung von {@link AbstractDescriptionComp} für etwas, das
 * jemamden gehört (z.B. Rapunzels Haare).
 */
public class PossessivDescriptionComp extends SimpleDescriptionComp {
    private final CounterDao counterDao;

    /**
     * {@link GameObjectId} des Besitzers. Legt fest, wann "ihre Haare" o.Ä.
     * verwendet werden kann.
     */
    private final GameObjectId besitzerId;

    /**
     * {@link GameObjectId} für den <i>Namen des Besitzers</i> - der SC muss also diesen
     * Namen erst lernen ("Rapunzel"), bevor der Name verwendet wird ("Rapunzels Haare") -;
     * ist dies hier {@code null}, so wird der Name ohne Einschränkungen verwendet
     * ("Großmutters Häuschen").
     */
    @Nullable
    private final GameObjectId besitzerNameId;

    /**
     * Genitivattribut für die Bezeichnug (den Namen) des Besitzer (z.B. "Rapunzels").
     *
     * @see #besitzerNameId
     */
    private final String besitzerGenitivattributVorangestellt;

    /**
     * Konstruktor für eine <code>PossessivDescriptionComp</code>.
     *
     * @param descriptionTriple {@link DescriptionTriple} <i>ohne</i> Possessivangabe
     *                          (z.B. "Haare" / "die Haare" / "die Haare").
     *                          Dient auch als Basis, um die possessiven
     *                          Beschreibungen zu erzeugen ("Rapunzels
     *                          Haare", "ihre "Haare").
     *                          <i>Achtung!</i>, eventuelle Adjektivphrasen
     *                          werden zwar passend dekliniert, aber die eigentliche
     *                          Flexionsreihe wird jeweils unverändert
     *                          übernommen! Das kann zu Fehlern führen, wenn die Flexionsreihe
     *                          einer Nomialphrase nicht nur ein übliches Nomen ist,
     *                          sondern z.B. ein adjektivisch dekliniertes Nomen
     *                          ("der Große" -> *"Rapunzels Große" statt "Rapunzels Großer")
     *                          oder wenn die Flexionsreihe in anderer Weise bereits deklinierte
     *                          Elemente enthält ("der silberne Armreif" -> *"Rapunzels silberne
     *                          Armreif" statt "Rapunzels silberner Armreif").
     *                          Außerdem muss das Description-Triple in jedem der drei
     *                          Beschreibungsbereiche (unbekannt, bekannt lang, bekannt kurz)
     *                          mindestens eine {@link Nominalphrase} enthalten.
     */
    public PossessivDescriptionComp(
            final CounterDao counterDao,
            final GameObjectId id,
            final GameObjectId besitzerId,
            @Nullable final GameObjectId besitzerNameId,
            final String besitzerGenitivattributVorangestellt,
            final DescriptionTriple descriptionTriple) {
        super(id, descriptionTriple);
        this.counterDao = counterDao;

        this.besitzerId = requireNonNull(besitzerId);
        this.besitzerNameId = besitzerNameId;
        this.besitzerGenitivattributVorangestellt =
                requireNonNull(besitzerGenitivattributVorangestellt);
    }

    /**
     * Gibt eine <i>Possessiv-Beschreibung</i> (genauer: ein {@link DescriptionTriple}) zurück,
     * soweit möglich und gewünscht - sonst eine normale Beschreibung.
     * <p>
     * Es gibt zwei Arten von Possessiv-Beschreibungen:
     * <ol>
     *     <li>Beschreibungen mit Anapher auf den Besitzer ("ihre Haare")
     *     <li>Beschreibungen einem Genitivattribut für den Besitzer ("Rapunzels Haare")
     * </ol>
     *
     * @param besitzerKnown     Ob der Besitzer bekannt ist. (In einigen Fällen
     *                          irrelevant.)
     * @param besitzerNameKnown Ob der Names des Besitzers bekannt ist - nur
     *                          relevant, wenn eine {@link #besitzerNameId}
     */
    // Es gäbe Alternativen zu Sätzen wie "Rapunzel kämmt ihre Haare":
    // "Rapunzels Vater kämmt ihr [Personalpronomen im Dativ] die Haare"
    // "Rapunzel kämmt sich [Reflexivpronomen!] die Haare" - "sich" ist nur erlaubt, wenn die
    // Zuordnung ziemlich fest ist). Manchmal ist auch "deren" / "dessen" möglich (oder nötig), vgl.
    // "Sie hilft den Kindern und kämmt ihre Haare [=? die Haare der Kinder]" vs.
    // "Sie hilft den Kindern und kämmt deren Haare" /
    // "Sie hilft den Kindern und kämmt ihnen die Haare".
    DescriptionTriple getPossessivDescriptionTriple(
            final ITextContext textContext,
            final PossessivDescriptionVorgabe possessivDescriptionVorgabe,
            final boolean besitzerKnown,
            final boolean besitzerNameKnown) {
        @Nullable final IArtikelworttypOderVorangestelltesGenitivattribut possessivangabe =
                calcPossessivangabe(possessivDescriptionVorgabe,
                        textContext,
                        besitzerKnown, besitzerNameKnown);

        if (possessivangabe == null) {
            // Possessivangabe nicht möglich oder nicht gewünscht
            return getDescriptionTriple(); // "die Haare"
        }

        // Possessivangabe möglich und gewünscht
        return new DescriptionTriple(
                counterDao,
                altDescriptionsAtFirstSight(), // "Lange, goldene Haarzöpfe"
                // "ihre goldenen Haare" / "Rapunzels goldene Haare"
                mitArtikelworttypOderVorangestelltemGenitivattribut(
                        altNormalDescriptionsWhenKnown(),
                        possessivangabe),
                // "ihre Haare" / "Rapunzels Haare"
                mitArtikelworttypOderVorangestelltemGenitivattribut(
                        altShortDescriptionsWhenKnown(),
                        possessivangabe));
    }

    /**
     * Gibt ein Artikelwort ("ihr") oder ein vorgangestelltes Genitivattribut
     * ("Rapunzels") für dieses Game Object zurück - oder {@code null}, wenn beides nicht möglich
     * oder gewünscht ist.
     *
     * @param besitzerKnown     Ob der Besitzer bekannt ist. (In einigen Fällen
     *                          irrelevant.)
     * @param besitzerNameKnown Ob der Names des Besitzers bekannt ist - nur
     *                          relevant, wenn eine {@link #besitzerNameId}
     */
    @Nullable
    private IArtikelworttypOderVorangestelltesGenitivattribut calcPossessivangabe(
            final PossessivDescriptionVorgabe possessivDescriptionVorgabe,
            final ITextContext textContext,
            final boolean besitzerKnown, final boolean besitzerNameKnown) {
        if (possessivDescriptionVorgabe == NICHT_POSSESSIV) {
            return null;
        }

        @Nullable final NumerusGenus numerusGenusBesitzerAnaphWennMgl =
                textContext.getNumerusGenusAnaphWennMgl(besitzerId);

        @Nullable final ArtikelwortFlexionsspalte.Typ possessivartikel =
                getPossessivartikel(numerusGenusBesitzerAnaphWennMgl);

        if (possessivartikel != null) {
            return possessivartikel;
        }

        if (possessivDescriptionVorgabe
                == ALLES_ERLAUBT
                && besitzerKnown
                && (
                // Man muss den Namen nicht speziell kennenlernen...
                besitzerNameId == null
                        // ...oder man kennt den Namen
                        || besitzerNameKnown)) {
            return new VorangestelltesGenitivattribut(besitzerGenitivattributVorangestellt);
        }

        // Possessivangabe nicht möglich oder nicht gewünscht
        return null;
    }

    /**
     * Gibt einen Possesivartikel-Typ für die dritte Person und diesen
     * Numerus und diese Genus zurück ( {@link ArtikelwortFlexionsspalte.Typ#IHR} oder
     * {@link ArtikelwortFlexionsspalte.Typ#SEIN}) – oder {@code null}.
     */
    @Nullable
    private static ArtikelwortFlexionsspalte.Typ getPossessivartikel(
            @Nullable final NumerusGenus numerusGenus) {
        if (numerusGenus == null) {
            return null;
        }

        // "ihr" / "ihre
        return ArtikelwortFlexionsspalte.getPossessiv(P3, numerusGenus);
    }

    /**
     * Gibt die angegebenen Nominalphrasen zurück, allerdings mit diesem Artikelwort oder dieser
     * Genitivphrase vorangestellt - <i>Achtung!</i>, eventuelle Adjektivphrasen
     * werden zwar passend dekliniert, aber die eigentlich Flexionsreihe der Nominalphrase
     * wird unverändert übernommen! Das kann zu Fehlern führen,
     * wenn die Flexionsreihe nicht nur ein übliches Nomen ist,
     * sondern z.B. ein adjektivisch dekliniertes Nomen (setzt man bei "ein Studierender"
     * ein definites Artikelwort ergibt sich *"der Studierender") oder
     * wenn die Flexionsreihe in anderer Weise bereits deklinierte
     * Elemente enthält (setzt man bei "ein silberner Mond gegen Abend"
     * ein definites Artikelwort ergibt sich *"der silberner Mond gegen Abend").
     * <p>
     * Angegebene substantivische Phrasen, die keine Nominalphrasen sind, werden weggelassen.
     * Sind gar keine Nominalphrasen angegeben, ist das Ergebnis leer.
     */
    @Nonnull
    @CheckReturnValue
    private static ImmutableList<Nominalphrase> mitArtikelworttypOderVorangestelltemGenitivattribut(
            final Collection<? extends EinzelneSubstantivischePhrase> alt,
            final IArtikelworttypOderVorangestelltesGenitivattribut vorangestellt) {
        return alt.stream()
                .filter(p -> p instanceof Nominalphrase)
                .map(p -> (Nominalphrase) p)
                .map(np -> np.mitArtikelworttypOderVorangestelltemGenitivattribut(vorangestellt))
                .collect(ImmutableList.toImmutableList());
    }

    GameObjectId getBesitzerId() {
        return besitzerId;
    }

    @Nullable
    GameObjectId getBesitzerNameId() {
        return besitzerNameId;
    }
}
