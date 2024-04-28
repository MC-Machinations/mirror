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

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Type;
import java.util.Arrays;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

/**
 * Main class for the Mirror reflections library.
 */
@DefaultQualifier(NonNull.class)
public final class Mirror {

    static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    private Mirror() {
    }

    /**
     * Create a fuzzy method finder.
     *
     * @param owner a class in the type hierarchy of the method you are looking for
     * @param returnType the return type of the method
     * @return a new fuzzy method finder
     */
    public static FuzzyMethodFinder fuzzyMethod(final Class<?> owner, final Class<?> returnType) {
        return new FuzzyMethodFinderImpl(owner, returnType);
    }

    /**
     * Create a fuzzy field finder.
     *
     * @param owner a class which has the field you are looking for
     * @param fieldType the field type
     * @return a new fuzzy field finder
     */
    public static FuzzyFieldFinder fuzzyField(final Class<?> owner, final Class<?> fieldType) {
        return new FuzzyFieldFinderImpl(owner, fieldType);
    }

    /**
     * Create a fuzzy field finder.
     *
     * @param owner a class which has the field you are looking for
     * @param genericFieldType the generic field type
     * @return a new fuzzy field finder
     */
    public static FuzzyFieldFinder fuzzyField(final Class<?> owner, final Type genericFieldType) {
        return new FuzzyFieldFinderImpl(owner, genericFieldType);
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
