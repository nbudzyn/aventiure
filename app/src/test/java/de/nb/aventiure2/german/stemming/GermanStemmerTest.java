package de.nb.aventiure2.german.stemming;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;
import static de.nb.aventiure2.german.stemming.GermanStemmer.toDiscriminator;

public class GermanStemmerTest {
    @Test
    public void test() {
        assertThat(toDiscriminator("singt")).isEqualTo("sing");
        assertThat(toDiscriminator("singen")).isEqualTo("sing");
        assertThat(toDiscriminator("beliebt")).isEqualTo("belieb");
        assertThat(toDiscriminator("beliebtester")).isEqualTo("belieb");

        assertThat(toDiscriminator("stören")).isEqualTo("stor");
        // Hier ist ein Fehler bei Caumanns (dort: "stö")

        assertThat(toDiscriminator("stöhnen")).isEqualTo("stoh");
        assertThat(toDiscriminator("Schauspielerin")).isEqualTo("schauspieleri");
        assertThat(toDiscriminator("Schauspielerinnen")).isEqualTo("schauspieleri");
        assertThat(toDiscriminator("Engländerin")).isEqualTo("englanderi");
        assertThat(toDiscriminator("Engländerinnen")).isEqualTo("englanderi");
    }
}
