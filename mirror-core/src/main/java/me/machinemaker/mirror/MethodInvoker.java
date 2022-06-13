/*
 * GNU General Public License v3
 *
 * Mirror, a opinionated reflection library
 *
 * Copyright (C) 2022 Machine_Maker
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 */
package me.machinemaker.mirror;

import io.leangen.geantyref.TypeToken;
import java.lang.reflect.Method;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A helper type for invoking methods.
 */
public interface MethodInvoker {

    /**
     * Creates a new invoker for a method.
     *
     * @param method the method to invoke
     * @return a new invoker
     * @throws IllegalAccessException if you can't access the method
     */
    static MethodInvoker from(final Method method) throws IllegalAccessException {
        return new MethodInvokerImpl(method);
    }

    /**
     * Creates a new typed invoker for a method.
     *
     * @param method the method to invoke
     * @param type the return type
     * @return a new typed invoker
     * @param <T> return type
     * @throws IllegalAccessException if you can't access the method
     */
    static <T> Typed<T> typed(final Method method, final Class<T> type) throws IllegalAccessException {
        Util.checkParameterized(type);
        return typed(method, TypeToken.get(type));
    }

    /**
     * Creates a new typed invoker for a method.
     *
     * @param method the method to invoke
     * @param type the return type
     * @return a new typed invoker
     * @param <T> return type
     * @throws IllegalAccessException if you can't access the method
     */
    static <T> Typed<T> typed(final Method method, final TypeToken<T> type) throws IllegalAccessException {
        return new MethodInvokerImpl.TypedImpl<>(method, type);
    }

    /**
     * Invoke a method on a specific target object.
     *
     * @param target    the target object, or null for a static method.
     * @param arguments the arguments to pass to the method.
     * @return the return value, or null if is void.
     */
    @Nullable Object invoke(@Nullable Object target, Object... arguments);

    /**
     * Invoke the method on a specific target object requiring the
     * return value to not be null.
     *
     * @param target the target object
     * @param arguments method arguments
     * @return the not null return value
     */
    default Object require(@Nullable final Object target, final Object... arguments) {
        return Objects.requireNonNull(this.invoke(target, arguments), "method returned null for " + this);
    }

    /**
     * A typed helper for invoking methods.
     *
     * @param <T> return type
     */
    interface Typed<T> extends MethodInvoker {

        @Override
        @Nullable T invoke(@Nullable Object target, Object... arguments);

        @Override
        default Object require(final @Nullable Object target, final Object... arguments) {
            return Objects.requireNonNull(this.invoke(target, arguments), "method returned null for " + this);
        }
    }
}
