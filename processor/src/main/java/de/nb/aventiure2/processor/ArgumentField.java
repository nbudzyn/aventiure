package de.nb.aventiure2.processor;

import java.util.Objects;

import javax.annotation.concurrent.Immutable;

/**
 * Eine Argument für eine Valenz.
 */
@Immutable
class ArgumentField {
    private final String type;
    private final String name;

    ArgumentField(final String type, final String name) {
        this.type = type;
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ArgumentField that = (ArgumentField) o;
        return type.equals(that.type) &&
                name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, name);
    }
}
