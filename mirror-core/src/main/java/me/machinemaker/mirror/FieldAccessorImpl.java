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

import io.leangen.geantyref.GenericTypeReflector;
import io.leangen.geantyref.TypeToken;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import org.checkerframework.checker.nullness.qual.Nullable;

class FieldAccessorImpl implements FieldAccessor {

    final Field field;
    private final boolean isStatic;
    private final MethodHandle getter;
    private final MethodHandle setter;

    FieldAccessorImpl(final Field field) throws IllegalAccessException {
        field.trySetAccessible();
        this.field = field;
        this.getter = Mirror.LOOKUP.unreflectGetter(field);
        this.setter = Mirror.LOOKUP.unreflectSetter(field);
        this.isStatic = Modifier.isStatic(field.getModifiers());
    }

    @Override
    public void set(@Nullable final Object instance, @Nullable final Object value) {
        try {
            if (this.isStatic) {
                this.setter.invoke(value);
            } else {
                if (instance == null) {
                    throw new IllegalArgumentException("Must pass an instance of the type for a non-static field");
                }
                this.setter.invoke(instance, value);
            }
        } catch (final Throwable e) {
            throw new RuntimeException("Could not set field " + this.setter, e);
        }
    }

    @Override
    public @Nullable Object get(@Nullable final Object instance) {
        try {
            if (this.isStatic) {
                return this.getter.invoke();
            } else {
                if (instance == null) {
                    throw new IllegalArgumentException("Must pass an instance of the type for a non-static field");
                }
                return this.getter.invoke(instance);
            }
        } catch (final Throwable e) {
            throw new RuntimeException("Could not get field " + this.getter, e);
        }
    }

    @Override
    public String toString() {
        return "FieldAccessorImpl{" +
                "field=" + this.field +
                '}';
    }

    static class TypedImpl<T> extends FieldAccessorImpl implements FieldAccessor.Typed<T> {

        private final TypeToken<T> type;

        TypedImpl(final Field field, final TypeToken<T> type) throws IllegalAccessException {
            super(field);
            this.type = type;
        }

        @SuppressWarnings("unchecked")
        @Override
        public @Nullable T get(final @Nullable Object instance) {
            final @Nullable Object value = super.get(instance);
            if (value == null || GenericTypeReflector.erase(this.type.getType()).isInstance(value)) {
                return (T) value;
            }
            throw new IllegalStateException(value + " is not an instance of " + this.type.getType());
        }

        @Override
        public String toString() {
            return "TypedImpl{" +
                    "type=" + this.type +
                    ", field=" + this.field +
                    '}';
        }
    }
}
