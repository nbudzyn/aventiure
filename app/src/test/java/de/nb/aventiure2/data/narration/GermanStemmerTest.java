package de.nb.aventiure2.data.narration;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;
import static de.nb.aventiure2.data.narration.GermanStemmer.toDiscriminator;

public class GermanStemmerTest {
    @Test
    public void test() {
        assertThat(toDiscriminator("singt")).isEqualTo("sing");
        assertThat(toDiscriminator("singen")).isEqualTo("sing");
        assertThat(toDiscriminator("beliebt")).isEqualTo("bel&b");
        assertThat(toDiscriminator("beliebtester")).isEqualTo("bel&b");

        assertThat(toDiscriminator("stören")).isEqualTo("stor");
        // Hier ist ein Fehler bei Caumanns (dort: "stö")

        assertThat(toDiscriminator("stöhnen")).isEqualTo("stoh");
        assertThat(toDiscriminator("Schauspielerin")).isEqualTo("$ausp&leri");
        assertThat(toDiscriminator("Schauspielerinnen")).isEqualTo("$ausp&leri");
        assertThat(toDiscriminator("Engländerin")).isEqualTo("englanderi");
        assertThat(toDiscriminator("Engländerinnen")).isEqualTo("englanderi");
    }
}
