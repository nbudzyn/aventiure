package de.nb.aventiure2.german.praedikat;

/**
 * Eine adverbiale Angabe, die sich eher auf den gesamten Satz bezieht, z.B.
 * "LEIDER kannst du schlecht singen" /
 * "Du kannst LEIDER schlecht singen".
 * <p>
 * Hierhink gehören auf jeden Fall, die "Satzadverbien / Satzadverbialien", also diejennigen
 * Adverbien / adverbialen Angaben k, bei denne folgende Paraphrase eines Satzes s, der k enthält,
 * möglich ist (Test): Es ist k der Fall, dass s', wobei s' aus s entsteht durch Weglassen von k.
 * ("vielleicht", "gestern", ...)
 */
public class AdverbialeAngabeSkopusSatz extends AbstractAdverbialeAngabe {
    public AdverbialeAngabeSkopusSatz(final String text) {
        super(text);
    }
}
