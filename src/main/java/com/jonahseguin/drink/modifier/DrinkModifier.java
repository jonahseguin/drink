package com.jonahseguin.drink.modifier;

import com.jonahseguin.drink.internal.CommandExecution;
import com.jonahseguin.drink.parametric.CommandParameter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public interface DrinkModifier<T> {

    Optional<T> modify(@Nonnull CommandExecution execution, @Nonnull CommandParameter commandParameter, @Nullable T argument);

}
