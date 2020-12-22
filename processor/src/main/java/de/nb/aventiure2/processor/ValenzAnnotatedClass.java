package de.nb.aventiure2.processor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Set;

import javax.annotation.concurrent.Immutable;
import javax.lang.model.element.VariableElement;

import de.nb.aventiure2.annotations.Valenz;

/**
 * Represents a class, annotated with @{@link Valenz}.
 */
@Immutable
class ValenzAnnotatedClass {
    private final String packageName;
    private final String simpleName;
    private final ImmutableList<ArgumentField> argumente;
    private final ImmutableList<VariableElement> sonstigeFelder;

    ValenzAnnotatedClass(final String packageName,
                         final String simpleName,
                         final Collection<ArgumentField> argumente,
                         final Collection<VariableElement> sonstigeFelder)
            throws IllegalArgumentException {
        this.packageName = packageName;
        this.simpleName = simpleName;
        this.argumente = ImmutableList.copyOf(argumente);
        this.sonstigeFelder = ImmutableList.copyOf(sonstigeFelder);
    }

    @SuppressWarnings("UnstableApiUsage")
    Collection<ValenzClassToBeGenerated> getClassesToGenerate() {
        final ImmutableList.Builder<ValenzClassToBeGenerated> res = ImmutableList.builder();

        for (int i = argumente.size() - 1; i > 0; i--) {
            for (final Set<ArgumentField> combination :
                    Sets.combinations(Sets.newLinkedHashSet(argumente), i)) {
                res.add(new ValenzClassToBeGenerated(packageName,
                        packageName.equals("") ? simpleName :
                                packageName + "." + simpleName,
                        argumente,
                        combination,
                        Sets.difference(Sets.newLinkedHashSet(argumente), combination),
                        sonstigeFelder));
            }
        }

        return res.build();
    }
}
