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
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

class FuzzyFieldImpl implements FuzzyField {

    private final Class<?> owner;
    private final Type type;
    private final List<String> names = new ArrayList<>();

    FuzzyFieldImpl(final Class<?> owner, final Type type) {
        this.owner = owner;
        this.type = type;
    }

    @Override
    public Class<?> owner() {
        return this.owner;
    }

    @Override
    public Type type() {
        return this.type;
    }

    @Override
    public FuzzyField names(final String... names) {
        this.names.addAll(Arrays.asList(names));
        return this;
    }

    @Override
    public FieldAccessor find() {
        try {
            return FieldAccessor.from(this.find0());
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
            if (this.type.equals(field.getGenericType())) {
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

    @Override
    public String toString() {
        return "FuzzyFieldImpl{" +
                "owner=" + this.owner +
                ", type=" + this.type +
                ", names=" + this.names +
                '}';
    }

    static class TypedImpl<T> extends FuzzyFieldImpl implements FuzzyField.Typed<T> {

        private final TypeToken<T> type;

        TypedImpl(final Class<?> owner, final TypeToken<T> type) {
            super(owner, type.getType());
            this.type = type;
        }

        @Override
        public TypeToken<T> typeToken() {
            return this.type;
        }

        @Override
        public FieldAccessor.Typed<T> find() {
            try {
                return FieldAccessor.typed(this.find0(), this.type);
            } catch (final IllegalAccessException ex) {
                throw new IllegalArgumentException("Could not access the found field", ex);
            }
        }

        @Override
        public FuzzyField.Typed<T> names(final String... names) {
            super.names(names);
            return this;
        }
    }
}
