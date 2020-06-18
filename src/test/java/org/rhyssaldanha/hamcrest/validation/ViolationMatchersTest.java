package org.rhyssaldanha.hamcrest.validation;

import lombok.Value;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.rhyssaldanha.hamcrest.validation.ViolationMatchers.fails;
import static org.rhyssaldanha.hamcrest.validation.ViolationMatchers.on;
import static org.rhyssaldanha.hamcrest.validation.ViolationMatchers.succeeds;
import static org.rhyssaldanha.hamcrest.validation.ViolationMatchers.violates;
import static org.rhyssaldanha.hamcrest.validation.ViolationMatchers.violation;
import static org.rhyssaldanha.hamcrest.validation.ViolationMatchers.withError;

class ViolationMatchersTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void shouldBeInvalid() {
        final Item item = new Item(null, 1, "");

        final Set<ConstraintViolation<Item>> violations = validator.validate(item);

        assertThat(violations, fails());
        assertThat(violations, violates(
                violation(on("name"), withError("must not be null")),
                violation(on("size"), withError("must be greater than or equal to 3")),
                violation(on("compoundFieldName"), withError("must not be blank"))
        ));
        assertFails(() -> assertThat(violations, succeeds()));
    }

    private void assertFails(final Executable executable) {
        assertThrows(AssertionError.class, executable);
    }

    @Test
    void shouldBeInvalid_nested() {
        final Item item = new Item(null, 10, "field");
        final Container container = new Container(item);

        final Set<ConstraintViolation<Container>> violations = validator.validate(container);

        assertThat(violations, violates(violation(on("item.name"), withError("must not be null"))));
    }

    @Test
    void shouldBeValid() {
        final Item item = new Item("name", 10, "field");

        final Set<ConstraintViolation<Item>> violations = validator.validate(item);

        assertFails(() -> assertThat(violations, fails()));
        assertThat(violations, succeeds());
    }

    @Value
    static class Item {
        @NotNull
        String name;

        @Min(3)
        Integer size;

        @NotBlank
        String compoundFieldName;
    }

    @Value
    static class Container {
        @Valid
        Item item;
    }
}