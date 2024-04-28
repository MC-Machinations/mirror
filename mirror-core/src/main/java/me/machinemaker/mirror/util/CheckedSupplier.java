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
package me.machinemaker.mirror.util;

/**
 * Represents a supplier of a value.
 * This handles checked exceptions.
 *
 * @param <T> the type of value being supplied
 * @param <E> the type of the exception that can be thrown
 */
@FunctionalInterface
public interface CheckedSupplier<T, E extends Throwable> {

    /**
     * Gets a result.
     *
     * @return a result
     * @throws E if an exception occurs
     */
    T get() throws E;

    /**
     * Returns a memoized supplier.
     *
     * @param supplier the supplier to memoize
     * @return a memoized supplier
     * @param <T> the type of value being supplied
     * @param <E> the type of the exception that can be thrown
     */
    static <T, E extends Throwable> CheckedSupplier<T, E> checkedMemoize(final CheckedSupplier<T, E> supplier) {
        return new CheckedSupplier<>() {
            private volatile T value;
            private boolean initialized = false;

            @Override
            public T get() throws E {
                if (!this.initialized) {
                    synchronized (this) {
                        if (!this.initialized) {
                            this.value = supplier.get();
                            this.initialized = true;
                        }
                        return this.value;
                    }
                }
                return this.value;
            }
        };
    }
}
