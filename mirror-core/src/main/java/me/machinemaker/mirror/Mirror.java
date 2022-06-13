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
import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Main class for the Mirror reflections library.
 */
public final class Mirror {

    static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    private Mirror() {
    }

    /**
     * Create an untyped fuzzy method finder.
     *
     * @param owner a class in the type hierarchy of the method you are looking for
     * @param returnType the return type of the method
     * @return a new untyped fuzzy method finder
     */
    public static FuzzyMethod fuzzyMethod(final Class<?> owner, final Class<?> returnType) {
        return new FuzzyMethodImpl(owner, returnType);
    }

    /**
     * Create a typed fuzzy method finder.
     *
     * @param owner a class in the type hierarchy of the method you are looking for
     * @param returnType the return type of the method
     * @return a new untyped fuzzy method finder
     * @param <T> return type
     */
    public static <T> FuzzyMethod.Typed<T> typedFuzzyMethod(final Class<?> owner, final Class<T> returnType) {
        Util.checkParameterized(returnType);
        return typedFuzzyMethod(owner, TypeToken.get(returnType));
    }

    /**
     * Create a typed fuzzy method finder.
     *
     * @param owner a class in the type hierarchy of the method you are looking for
     * @param returnType the return type of the method
     * @return a new untyped fuzzy method finder
     * @param <T> return type
     */
    public static <T> FuzzyMethod.Typed<T> typedFuzzyMethod(final Class<?> owner, final TypeToken<T> returnType) {
        return new FuzzyMethodImpl.TypedImpl<>(owner, returnType);
    }

    /**
     * Create an untyped fuzzy field finder.
     *
     * @param owner a class which has the field you are looking for
     * @param type the field type
     * @return a new untyped fuzzy field finder
     */
    public static FuzzyField fuzzyField(final Class<?> owner, final Class<?> type) {
        return new FuzzyFieldImpl(owner, type);
    }

    /**
     * Create a typed fuzzy field finder.
     *
     * @param owner a class which has the field you are looking for
     * @param type the field type
     * @return a new typed fuzzy field finder
     * @param <T> field type
     */
    public static <T> FuzzyField.Typed<T> typedFuzzyField(final Class<?> owner, final Class<T> type) {
        Util.checkParameterized(type);
        return typedFuzzyField(owner, TypeToken.get(type));
    }

    /**
     * Create a typed fuzzy field finder.
     *
     * @param owner a class which has the field you are looking for
     * @param type the field type
     * @return a new typed fuzzy field finder
     * @param <T> field type
     */
    public static <T> FuzzyField.Typed<T> typedFuzzyField(final Class<?> owner, final TypeToken<T> type) {
        return new FuzzyFieldImpl.TypedImpl<>(owner, type);
    }

    /**
     * Get a class by its canonical name.
     *
     * @param name the canonical name
     * @return the class
     * @throws IllegalArgumentException if class not found
     */
    public static Class<?> getClass(final String name) {
        try {
            return Class.forName(name);
        } catch (final ClassNotFoundException ex) {
            throw new IllegalArgumentException("Could not find a class with name " + name, ex);
        }
    }

    /**
     * Find a class from a set of possible names.
     *
     * @param names possible class names
     * @return the first matching class
     * @throws IllegalArgumentException if no names matched a class
     */
    public static Class<?> findClass(final String... names) {
        for (final String name : names) {
            try {
                return Class.forName(name);
            } catch (final ClassNotFoundException ignored) {
            }
        }
        throw new IllegalArgumentException("None of " + Arrays.toString(names) + " could be matched to a class");
    }

    /**
     * Tries to get a class by its canonical name, returning
     * null if none found.
     *
     * @param name the canonical name
     * @return the class or null if not found
     */
    public static @Nullable Class<?> maybeGetClass(final String name) {
        try {
            return Class.forName(name);
        } catch (final ClassNotFoundException ex) {
            return null;
        }
    }

    /**
     * Tries to find a class from a set of possible names, returning
     * null if none found.
     *
     * @param names possible class names
     * @return the first matching class or null if none found
     */
    public static @Nullable Class<?> maybeFindClass(final String... names) {
        for (final String name : names) {
            try {
                return Class.forName(name);
            } catch (final ClassNotFoundException ignored) {
            }
        }
        return null;
    }
}
