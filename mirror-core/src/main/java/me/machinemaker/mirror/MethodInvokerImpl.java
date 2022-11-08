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
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.checkerframework.checker.nullness.qual.Nullable;

class MethodInvokerImpl implements MethodInvoker {

    private final MethodHandle methodHandle;
    private final boolean isStatic;

    MethodInvokerImpl(final Method method) throws IllegalAccessException {
        method.trySetAccessible();
        this.methodHandle = Mirror.LOOKUP.unreflect(method);
        this.isStatic = Modifier.isStatic(method.getModifiers());
    }

    @Override
    public @Nullable Object invoke(final @Nullable Object target, final Object... arguments) {
        final int instanceIndex = this.isStatic ? 0 : 1;
        final Object[] args = new Object[arguments.length + instanceIndex];
        if (!this.isStatic) {
            if (target == null) {
                throw new IllegalArgumentException("Must pass an instance of the type for a non static method");
            }
            args[0] = target;
        }
        if (arguments.length > 0) {
            System.arraycopy(arguments, 0, args, instanceIndex, arguments.length);
        }
        try {
            return this.methodHandle.invokeWithArguments(args);
        } catch (final Throwable e) {
            throw new RuntimeException("Could not invoke method " + this.methodHandle, e);
        }
    }

    static class TypedImpl<T> extends MethodInvokerImpl implements Typed<T> {

        private final TypeToken<T> type;

        TypedImpl(final Method method, final TypeToken<T> type) throws IllegalAccessException {
            super(method);
            this.type = type;
        }

        @SuppressWarnings("unchecked")
        @Override
        public @Nullable T invoke(final @Nullable Object target, final Object... arguments) {
            final @Nullable Object value = super.invoke(target, arguments);
            if (GenericTypeReflector.erase(this.type.getType()).isInstance(value)) {
                return (T) value;
            } else {
                throw new IllegalStateException(value + " is not an instance of " + this.type);
            }
        }
    }
}
