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
import java.lang.reflect.Field;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A helper type for accessing fields.
 */
public interface FieldAccessor {

    /**
     * Creates a new accessor for a field.
     *
     * @param field the field to access
     * @return a new accessor
     * @throws IllegalAccessException if you can't access the field
     */
    static FieldAccessor from(final Field field) throws IllegalAccessException {
        return new FieldAccessorImpl(field);
    }

    /**
     * Creates a new typed accessor for a field.
     *
     * @param field the field to access
     * @param type the field type
     * @return a new typed accessor
     * @param <T> field type
     * @throws IllegalAccessException if you can't access the field
     */
    static <T> FieldAccessor.Typed<T> typed(final Field field, final Class<T> type) throws IllegalAccessException {
        Util.checkParameterized(type);
        return typed(field, TypeToken.get(type));
    }

    /**
     * Creates a new typed accessor for a field.
     *
     * @param field the field to access
     * @param type the field type
     * @return a new typed accessor
     * @param <T> field type
     * @throws IllegalAccessException if you can't access the field
     */
    static <T> FieldAccessor.Typed<T> typed(final Field field, final TypeToken<T> type) throws IllegalAccessException {
        return new FieldAccessorImpl.TypedImpl<>(field, type);
    }

    /**
     * Sets the field value.
     *
     * @param instance instance to set the field on, or null for static fields
     * @param value the value to set the field to
     */
    void set(@Nullable Object instance, @Nullable Object value);

    /**
     * Gets the field value.
     *
     * @param instance the instance to get the field from, or null for static fields
     * @return the field value
     */
    @Nullable Object get(@Nullable Object instance);

    /**
     * Gets the field value, requiring it not is null.
     *
     * @param instance the instance to get the field from, or null for static fields
     * @return the not null field value
     */
    default Object require(@Nullable final Object instance) {
        return Objects.requireNonNull(this.get(instance), "field value was null for " + this);
    }

    /**
     * A typed helper for accessing fields.
     *
     * @param <T> field type
     */
    interface Typed<T> extends FieldAccessor {

        @Override
        void set(@Nullable Object instance, @Nullable Object value);

        @Override
        @Nullable T get(@Nullable Object instance);

        @Override
        default T require(final @Nullable Object instance) {
            return Objects.requireNonNull(this.get(instance), "field value was null for " + this);
        }
    }
}
