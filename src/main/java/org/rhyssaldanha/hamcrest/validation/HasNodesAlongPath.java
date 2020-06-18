package org.rhyssaldanha.hamcrest.validation;

import org.hamcrest.Description;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import javax.validation.Path;
import javax.validation.Path.Node;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;

public class HasNodesAlongPath extends TypeSafeMatcher<Path> {
    private final List<Matcher<? super Node>> nodeMatchers;

    public HasNodesAlongPath(List<Matcher<? super Node>> nodeMatchers) {
        this.nodeMatchers = nodeMatchers;
    }

    public static HasNodesAlongPath path(String expression) {
        return new HasNodesAlongPath(
                List.of(expression.split("\\.")).stream()
                .map(HasNodesAlongPath::nodeWithName)
                .collect(Collectors.toList())
        );
    }

    public static Matcher<? super Node> nodeWithName(String name) {
        return nodeWithName(equalTo(name));
    }

    public static Matcher<? super Node> nodeWithName(Matcher<? super String> nameMatcher) {
        return new FeatureMatcher<Node, String>(nameMatcher, "", "") {
            @Override
            protected String featureValueOf(Node actual) {
                return actual.getName();
            }
        };
    }

    @Override
    protected boolean matchesSafely(Path path) {
        return contains(nodeMatchers).matches(path);
    }

    public void describeTo(Description description) {
        description.appendList("", "->", "", nodeMatchers);
    }
}
