package org.rhyssaldanha.hamcrest.validation;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.collection.IsEmptyIterable;

import javax.validation.ConstraintViolation;
import javax.validation.Path;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.rhyssaldanha.hamcrest.validation.HasNodesAlongPath.path;

/**
 * A collection of hamcrest matchers to validate JSR-303
 * {@link javax.validation.ConstraintViolation}.
 */
public class ViolationMatchers {

    /**
     * Entry point to violation matchers.
     * Checks that a collection of violations contains at least one violation
     * and that each of the given matchers matches at least one violation.
     */
    @SafeVarargs
    public static <T> Matcher<Iterable<ConstraintViolation<T>>> violates(Matcher<? super ConstraintViolation<T>>... matchers) {
        return hasItems(matchers);
    }

    public static <T> Matcher<Iterable<? extends ConstraintViolation<T>>> fails() {
        return not(succeeds());
    }

    /**
     * Checks that there are no violations
     */
    public static <T> Matcher<Iterable<? extends ConstraintViolation<T>>> succeeds() {
        return IsEmptyIterable.emptyIterable();
    }

    /**
     * Checks that a violation satisfies a set of conditions.
     */
    @SafeVarargs
    public static <T> Matcher<ConstraintViolation<T>> violation(Matcher<? super ConstraintViolation<T>>... matchers) {
        return allOf(matchers);
    }

    /**
     * Checks that a violation occurs on a given property.
     * <p>
     * The property can be a nested property expression. For instance, expression <code>foo.bar</code> would
     * check that the violation applies to the <code>bar</code> property of the object accessed
     * by the <code>foo</code> property.
     */
    public static <T> Matcher<ConstraintViolation<T>> on(String pathExpression) {
        return new FeatureMatcher<>(path(pathExpression), "on path", "path") {
            @Override
            protected Path featureValueOf(ConstraintViolation<T> actual) {
                return actual.getPropertyPath();
            }
        };
    }

    /**
     * Checks that a violation error message is a given string.
     */
    public static <T> Matcher<ConstraintViolation<T>> withError(String messagePart) {
        return new FeatureMatcher<>(is(messagePart), "with message", "message") {
            @Override
            protected String featureValueOf(ConstraintViolation<T> actual) {
                return actual.getMessage();
            }
        };
    }
}
