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
import java.lang.reflect.Type;

/**
 * A utility for finding fields based on limited information.
 */
public interface FuzzyField {

    /**
     * Get the owner of the field.
     *
     * @return the owner type
     */
    Class<?> owner();

    /**
     * Get the generic type of the field.
     *
     * @return the generic type
     */
    Type type();

    /**
     * Set the field names to check against.
     *
     * @param names possible field names
     * @return this
     */
    FuzzyField names(String... names);

    /**
     * Attempt to find a matching field.
     *
     * @return the matched field
     */
    FieldAccessor find();

    /**
     * A typed utility for finding fields based on limited information.
     *
     * @param <T> field type
     */
    interface Typed<T> extends FuzzyField {

        /**
         * Get the type token for the field type.
         *
         * @return the type token
         */
        TypeToken<T> typeToken();

        @Override
        FieldAccessor.Typed<T> find();

        @Override
        FuzzyField.Typed<T> names(String... names);
    }
}
