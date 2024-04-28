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
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

record FuzzyFieldFinderImpl(List<String> names, Class<?> owner, Class<?> fieldType, java.lang.reflect.Type genericFieldType) implements FuzzyFieldFinder {

    FuzzyFieldFinderImpl(final Class<?> owner, final Class<?> fieldType) {
        this(new ArrayList<>(), owner, fieldType, fieldType);
    }

    FuzzyFieldFinderImpl(final Class<?> owner, final java.lang.reflect.Type fieldType) {
        this(new ArrayList<>(), owner, GenericTypeReflector.erase(fieldType), fieldType);
    }

    FuzzyFieldFinderImpl {
        names = new ArrayList<>(names);
    }

    @Override
    public FuzzyFieldFinder names(final String... names) {
        this.names.addAll(Arrays.asList(names));
        return this;
    }

    @Override
    public MethodHandle find(final Type accessType) {
        try {
            return switch (accessType) {
                case GETTER -> MethodHandles.privateLookupIn(this.owner, Mirror.LOOKUP).unreflectGetter(this.find0());
                case SETTER -> MethodHandles.privateLookupIn(this.owner, Mirror.LOOKUP).unreflectSetter(this.find0());
            };
        } catch (final IllegalAccessException ex) {
            throw new IllegalArgumentException("Could not access the found field", ex);
        }
    }

    Field find0() {
        final Set<Field> fields = new LinkedHashSet<>();
        fields.addAll(Arrays.asList(this.owner.getDeclaredFields()));
        fields.addAll(Arrays.asList(this.owner.getFields()));
        fields.removeIf(Field::isSynthetic);

        final Set<Field> match = new LinkedHashSet<>();
        for (final Field field : fields) {
            if (this.fieldType.equals(field.getType())) {
                match.add(field);
            }
        }

        if (match.isEmpty()) {
            throw new IllegalArgumentException("Could not find a field with " + this);
        } else if (match.size() == 1) {
            return match.iterator().next();
        }

        if (this.names.isEmpty()) {
            throw new AmbiguousFuzzyException("Found multiple fields that match + " + this + ". Try adding a field name.");
        }
        for (final Field field : match) {
            for (final String name : this.names) {
                if (field.getName().equals(name)) {
                    return field;
                }
            }
        }
        throw new IllegalStateException("Found multiple fields that match, but none match any names provided. " + this + ": " + match);
    }
}
