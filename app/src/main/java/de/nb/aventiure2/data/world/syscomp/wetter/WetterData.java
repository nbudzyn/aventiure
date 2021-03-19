package de.nb.aventiure2.data.world.syscomp.wetter;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableCollection;

import javax.annotation.concurrent.Immutable;

import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.data.time.Tageszeit;
import de.nb.aventiure2.data.world.base.Lichtverhaeltnisse;
import de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen;
import de.nb.aventiure2.german.base.ZweiPraedikativa;
import de.nb.aventiure2.german.description.AltDescriptionsBuilder;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.satz.Satz;

import static de.nb.aventiure2.german.base.Nominalphrase.EIN_SCHOENER_ABEND;
import static de.nb.aventiure2.german.base.Numerus.PL;
import static de.nb.aventiure2.german.base.Person.P3;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.alt;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.altNeueSaetze;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;

@Immutable
class WetterData {
    private final Temperatur tageshoechsttemperatur;

    private final Temperatur tagestiefsttemperatur;

    private final Windstaerke windstaerke;

    private final Bewoelkung bewoelkung;

    private final BlitzUndDonner blitzUndDonner;

    @SuppressWarnings("WeakerAccess")
    public WetterData(final Temperatur tageshoechsttemperatur,
                      final Temperatur tagestiefsttemperatur,
                      final Windstaerke windstaerke,
                      final Bewoelkung bewoelkung,
                      final BlitzUndDonner blitzUndDonner) {
        this.tageshoechsttemperatur = tageshoechsttemperatur;
        this.tagestiefsttemperatur = tagestiefsttemperatur;
        this.windstaerke = windstaerke;
        this.bewoelkung = bewoelkung;
        this.blitzUndDonner = blitzUndDonner;
    }

    AltDescriptionsBuilder altSCKommtNachDraussenInsWetter(final AvTime time) {
        final Temperatur temperatur = getTemperatur(time);

        final AltDescriptionsBuilder alt = alt();

        final ImmutableCollection<Satz> altBewoelkungGestirnSaetze =
                bewoelkung.altScKommtNachDraussenSaetze(time);
        alt.addAll(altBewoelkungGestirnSaetze);

        if (time.getTageszeit().getLichtverhaeltnisseDraussen() == Lichtverhaeltnisse.HELL) {
            alt.addAll(temperatur.altScKommtNachDraussenSaetze());
            // FIXME Wenn ein Wetteraspekt deutlich überwiegt, dann
            //  die anderen nicht nennen - oder nur nachrangig.
            if (time.getTageszeit() == Tageszeit.TAGSUEBER) {
                // FIXME Als Dauerbeschreibunge:
                // - "die Sonnenhitze brennt stark"
                // - "die Sonne scheint sehr warm"
                alt.addAll(altNeueSaetze(
                        altBewoelkungGestirnSaetze,
                        ";",
                        temperatur.altDerTagIstSaetze()));
                // FIXME "der Tag ist warm, die Sonne sticht"
                // FIXME "Als ... steht die Sonne schon hoch am Himmel und scheint heiß herunter."
                // FIXME Windstärke berücksichtigen
                // FIXME Blitz und Donner berücksichtigen
            } else if (time.getTageszeit() == Tageszeit.ABENDS) {
                if (temperatur.isBetweenIncluding(Temperatur.WARM, Temperatur.RECHT_HEISS)
                        && bewoelkung.compareTo(Bewoelkung.LEICHT_BEWOELKT) <= 0) {
                    alt.addAll(altNeueSaetze(
                            // FIXME Windstärke und Blitz / Donner berücksichtigen
                            altBewoelkungGestirnSaetze,
                            ";",
                            temperatur.altPraedikativa().stream()
                                    .map(a -> a.alsEsIstSatz()
                                            .mitAdvAngabe(
                                                    new AdvAngabeSkopusVerbAllg("noch")))));
                    if (bewoelkung.compareTo(Bewoelkung.LEICHT_BEWOELKT) <= 0) {
                        // FIXME Windstärke und Blitz / Donner berücksichtigen
                        alt.addAll(altNeueSaetze("Es ist",
                                EIN_SCHOENER_ABEND.nomK(),
                                "und noch",
                                temperatur.altPraedikativa().stream()
                                        .map(a -> a.getPraedikativ(P3, PL))));
                    }
                }
            }
        } else {
            alt.addAll(Lichtverhaeltnisse.altSCKommtNachDraussenInDunkelheit());
            alt.addAll(altNeueSaetze(
                    Lichtverhaeltnisse.altSCKommtNachDraussenInDunkelheit(),
                    ";",
                    temperatur.altScKommtNachDraussenSaetze()));

            // "draußen ist es dunkel und ziemlich kühl"
            alt.addAll(
                    temperatur.altPraedikativa().stream()
                            .map(tempAdjPhr -> new ZweiPraedikativa<>(
                                    AdjektivOhneErgaenzungen.DUNKEL, tempAdjPhr)
                                    .alsEsIstSatz()
                                    .mitAdvAngabe(new AdvAngabeSkopusSatz("draußen"))));

            // FIXME "Sobald die Sonne untergegangen ist, "
            //  (Uhrzeit berücksichtigen: time.kurzNachSonnenuntergang())

            if (time.kurzVorSonnenaufgang()) {
                alt.add(neuerSatz("die Sonne geht bald auf"));
                alt.add(neuerSatz("die Sonne ist noch nicht wieder hervorgekommen"));
                // FIXME "Als der Tag anbricht, noch ehe die Sonne aufgegangen ist"
            }

            // FIXME Windstärke berücksichtigen
            // FIXME Blitz und Donner berücksichtigen
        }
        return alt;
    }

    private Temperatur getTemperatur(final AvTime time) {
        return TagestemperaturverlaufUtil
                .calcTemperatur(tageshoechsttemperatur, tagestiefsttemperatur, time);
    }

    // FIXME Überall nach Sonne, Mond, hell, dunkel, wolk etc. suchen
    //  und 1. Widersprüche verhindern 2. Wetter hier zentralisieren.

    // FIXME Sonne:
    // "mit Sonnenaufgang (machts du dich auch den Weg...)"
    // "Als aber die ersten Sonnenstrahlen in den Garten fallen, so..."
    // "und als du erwachst und wieder zu dir selber kommst, bist
    //  du auf einer schönen Wiese, wo die Sonne scheint"
    // "als du siehst, wie die Sonnenstrahlen durch die Bäume hin- und hertanzen"
    // "du liegst in der Sonne ausgestreckt"
    // "Als aber die Sonne bald untergehen will, "
    // "Bei Sonnenaufgang kommt schon..."
    // "Bei Sonnenuntergang kommst du zu..."
    // "Du kommst in den Wald, und da es darin kühl und lieblich ist und die Sonne heiß
    // brennt, so..."
    // "Die Sonne geht auf, und ..."
    // "Du ... noch immer, als es schon hoher Tag ist"
    // "Es ist ein schöner Abend, die Sonne scheint
    // zwischen den Stämmen der Bäume hell ins dunkle Grün des
    // Waldes"
    // "Noch halb stand die Sonne über (dem Berg) und halb war sie unter."
    // "Nun ist die Sonne unter:"
    // "die Sonne geht bald auf"
    // "Als nun die Sonne durchs
    // Fensterlein schien und..."
    // "Wie du nun (dies und jenes tust) und zu Mittag die Sonne heiß brennt, wird dir so
    // warm und verdrießlich zumut:"
    // "Als nun die Sonne über dir steht stand, "
    // "Du hältst Mittag"
    // "Wie nun die Sonne kommt und du aufwachst..."
    // "durch die dichtbelaubten Äste dringt kein Sonnenstrahl"
    // "Als die Sonne untergeht..."
    // "Es dauert nicht lange, so siehst du die Sonne (hinter den Bergen) aufsteigen"
    // sitzt "in der Sonne"
    // "du bist von der Sonnenhitze müde"
    // liegst "mitten im heißen Sonnenschein"
    // "(Schwerter) blitzen in der Sonne"
    // du legst dich "in die Sonne"
    // "Die Sonne hat die Erde aufgetaut"
    // "Die Abendsonne scheint über (die
    // glänzenden Steine), sie schimmerten und leuchteten so prächtig
    //in allen Farben, daß..."
    // "aber was tust du die Augen auf, als du aus (der Finsternis)
    // heraus in das Tageslicht kommst, und den grünen Wald,
    //Blumen und Vögel und die Morgensonne am Himmel erblickst"
    // "Als nun die Sonne mitten über dem Walde steht..."
    // ", bis die Sonne sinkt und die Nacht einbricht."
    // "Als du aber am Morgen bei hellem Sonnenschein aufwachst, "
    // "die Sonne ist hinter (den Bergen) verschwunden"
    // "mittendurch rauscht ein klarer Bach, auf dem die Sonne glitzert"
    // "es bricht eben der erste Sonnenstrahl hervor"
    // "gegen Abend, als die Sonne (hinter die Berge) gesunken ist"
    // "Du erwachst vor Sonnenuntergang"
    // "Die Sonne will eben untergehen, als du erwachst"
    // "Du  (blieb unter der Linde sitzen), bis die Sonne
    // untergeht"
    // "Als die Sonne aufgeht, ..."
    // "in dem Augenblick dringt der erste Strahl der aufgehenden Sonne am Himmel herauf"

    // FIXME "Der Himmel ist blau, die Luft mild"
    //  "Der Himmel ist blau und eine frische
    //   Luft weht dir entgegen"

    // FIXME Nacht:
    // "Bei einbrechender Nacht"
    // "Der Mond scheint über..."

    // FIXME
    //  "ein kühles Lüftchen streicht durch das Laub"
    //  Der Wind wird stärker
    //  Der Wind pfeift dir ums Gesicht
    //  In der Ferne hörst du Donnergrollen
    //  Hat es eben geblitzt?
    //  "der Regen schlägt dir ins Gesicht und der Wind zaust dein Haar"
    //  Ein Sturm zieht auf
    //  Hoffentlich bleibt es wenigstens trocken
    //  (Kein Regen - keine nassen Klamotten o.Ä.)
    //  "Die Äste biegen sich"
    //  "das Gezweig"
    //  "es kommt ein starker Wind"
    //  "es weht beständig ein harter Wind"
    //  "der Wind raschelt in den Bäumen, und die Wolken ziehen ganz nah über deinem Haupt weg"
    //  "der Wind saust"
    //  Der Wind ist jetzt sehr kräftig und angenehm. Kalt ist es geworden.
    //  Der Sturm biegt die Bäume.
    //  "darin bist du vor Wind und Wetter geschützt"
    //  "Dich friert" "Um Mitternacht geht der Wind so kalt, dass
    //  dir nicht warm werden will"
    //  "du frierst am ganzen Leibe"
    //  "du bist halb erfroren und willst dich nur ein wenig wärmen"
    //  "du reibst die Hände"
    //  "du bist so erfroren"
    //  "Sobald die Sonne wieder warm scheint, gehst du..."
    //  "dich wärmen"
    //  "Es ist warmes Wetter"
    //  "der Tag ist warm, die Sonne sticht"
    //  "du erwärmst dich"
    //  "Die Hitze wird drückender, je näher der Mittag kommt" (KEIN WIND)
    //  "von der Hitze des Tages ermüdet"
    //  "du bist von der Sonnenhitze müde"
    //  "Sturm"
    //  "es stürmt", "du findest darin Schutz"
    //  "der Wind rauscht draußen in den Bäumen"
    //  "Weil aber das Wetter so schlecht geworden, und Wind und Regen stürmte,
    //   kannst du nicht weiter und kehrst [...] ein."
    //  Ein ziemlicher Krach (Hexe geht nicht mehr spazieren. Schlossfest?!)
    //  Der Sturm peitscht die Äste über dir und es ist ziemlich dunkel. Ein geschützter Platz
    //  wäre schön.
    //  Langsam scheint sich das Wetter wieder zu bessern / der Sturm flaut allmählich ab.
    //  "Der Wind legt sich, und auf den Bäumen vor [...] regt sich kein Blättchen mehr"
    //  "Es geht kein Wind, und bewegt sich kein Blättchen"
    //  "Kein Wind weht"
    //  Fürs Wetter lässt sich wohl einiges von Hunger oder Müdigkeit übernehmen.
    //  Man braucht regelmäßige Hinweise (je nach Dramatik des Wettes).
    //  Außerdem sollten alle "heiß..."–Hinweise dort zentralisiert werden.
    //  Schwierigkeit: Die Texte müssten alle Skalen gleichzeitig berücksichtigen?

    // FIXME Nur dramturgisch geändert, nicht automatisch?
    // FIXME Wetter beeinflusst Stimmung von SC, Rapunzel, Zauberin (Listener-Konzept!)


    Bewoelkung getBewoelkung() {
        return bewoelkung;
    }

    @NonNull
    BlitzUndDonner getBlitzUndDonner() {
        return blitzUndDonner;
    }

    @NonNull
    Temperatur getTageshoechsttemperatur() {
        return tageshoechsttemperatur;
    }

    @NonNull
    Temperatur getTagestiefsttemperatur() {
        return tagestiefsttemperatur;
    }

    @NonNull
    Windstaerke getWindstaerke() {
        return windstaerke;
    }
}
