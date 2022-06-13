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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

class FuzzyMethodImpl implements FuzzyMethod {

    final List<Class<?>> params = new ArrayList<>();
    final List<String> names = new ArrayList<>();
    private final Class<?> owner;
    private final Class<?> returnType;

    FuzzyMethodImpl(final Class<?> owner, final Class<?> returnType) {
        this.owner = owner;
        this.returnType = returnType;
    }

    static boolean parametersMatch(final Class<?>[] methodParameters, final List<Class<?>> fuzzyParameters) {
        if (methodParameters.length != fuzzyParameters.size()) {
            return false;
        }
        for (int i = 0; i < methodParameters.length; i++) {
            if (!methodParameters[i].equals(fuzzyParameters.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Class<?> owner() {
        return this.owner;
    }

    @Override
    public Class<?> returnType() {
        return this.returnType;
    }

    @Override
    public FuzzyMethod params(final Class<?>... params) {
        if (!this.params.isEmpty()) {
            throw new IllegalStateException("You already set the params on this fuzzy method");
        }
        this.params.addAll(Arrays.asList(params));
        return this;
    }

    @Override
    public FuzzyMethod names(final String... names) {
        this.names.addAll(Arrays.asList(names));
        return this;
    }

    @Override
    public MethodInvoker find() {
        try {
            return MethodInvoker.from(this.find0());
        } catch (final IllegalAccessException ex) {
            throw new IllegalArgumentException("Could not access the found method", ex);
        }
    }

    Method find0() {
        final Set<Method> methods = new LinkedHashSet<>();
        methods.addAll(Arrays.asList(this.owner.getDeclaredMethods()));
        methods.addAll(Arrays.asList(this.owner.getMethods()));
        methods.removeIf(method -> method.isSynthetic() || method.isBridge());

        final Set<Method> match = new LinkedHashSet<>();
        final Set<Method> almostMatch = new LinkedHashSet<>();
        for (final Method method : methods) {
            if (method.getDeclaringClass().equals(Object.class)) {
                continue; // skip these
            }
            if (this.returnType.equals(method.getReturnType()) && parametersMatch(method.getParameterTypes(), this.params)) {
                match.add(method);
            } else if (this.returnType.isAssignableFrom(method.getReturnType()) && parametersMatch(method.getParameterTypes(), this.params)) {
                almostMatch.add(method);
            }
        }

        if (match.isEmpty() && almostMatch.isEmpty()) {
            throw new IllegalArgumentException("Could not find a method with " + this);
        } else if (match.size() == 1 && almostMatch.isEmpty()) {
            return match.iterator().next();
        } else if (match.isEmpty() && almostMatch.size() == 1) {
            return almostMatch.iterator().next();
        }
        final Set<Method> nameCheck = new LinkedHashSet<>(match);
        nameCheck.addAll(almostMatch);
        if (this.names.isEmpty()) {
            throw new AmbiguousFuzzyException("Found multiple methods that match " + this + ": " + nameCheck + ". Try adding a method name.");
        }
        for (final Method method : nameCheck) {
            for (final String name : this.names) {
                if (method.getName().equals(name)) {
                    return method;
                }
            }
        }
        throw new IllegalStateException("Found multiple methods that match, but none match any names provided. " + this + ": " + nameCheck);
    }

    @Override
    public String toString() {
        return "FuzzyMethodImpl{" +
                "owner=" + this.owner +
                ", returnType=" + this.returnType +
                (this.params.isEmpty() ? "" : ", params=" + this.params) +
                (this.names.isEmpty() ? "" : ", names=" + this.names) +
                '}';
    }

    static class TypedImpl<T> extends FuzzyMethodImpl implements Typed<T> {

        private final TypeToken<T> returnType;

        TypedImpl(final Class<?> owner, final TypeToken<T> returnType) {
            super(owner, GenericTypeReflector.erase(returnType.getType()));
            this.returnType = returnType;
        }

        @Override
        public TypeToken<T> returnTypeToken() {
            return this.returnType;
        }

        @Override
        public MethodInvoker.Typed<T> find() {
            try {
                return MethodInvoker.typed(this.find0(), this.returnType);
            } catch (final IllegalAccessException ex) {
                throw new IllegalArgumentException("Could not access the found method", ex);
            }
        }

        @Override
        public FuzzyMethod.Typed<T> params(final Class<?>... params) {
            super.params(params);
            return this;
        }

        @Override
        public FuzzyMethod.Typed<T> names(final String... names) {
            super.names(names);
            return this;
        }

        @Override
        public String toString() {
            return "FuzzyMethodImpl$TypedImpl{" +
                    "returnType=" + this.returnTypeToken().getType() +
                    ", owner=" + this.owner() +
                    (this.params.isEmpty() ? "" : ", params=" + this.params) +
                    (this.names.isEmpty() ? "" : ", names=" + this.names) +
                    '}';
        }
    }
}
