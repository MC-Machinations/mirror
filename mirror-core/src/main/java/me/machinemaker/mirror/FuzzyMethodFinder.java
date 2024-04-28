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

import java.lang.invoke.MethodHandle;

/**
 * A utility for finding methods based on limited information.
 */
public sealed interface FuzzyMethodFinder permits FuzzyMethodFinderImpl {

    /**
     * Get the owner of the method.
     *
     * @return the owner type
     */
    Class<?> owner();

    /**
     * Get the return type of the method.
     *
     * @return the return type
     */
    Class<?> returnType();

    /**
     * Set the method parameter types.
     *
     * @param params the method parameter types
     * @return this
     */
    FuzzyMethodFinder params(Class<?>... params);

    /**
     * Set the method names to check against.
     *
     * @param names possible method names
     * @return this
     */
    FuzzyMethodFinder names(String... names);

    /**
     * Attempt to find a matching method.
     *
     * @return the matched method
     */
    MethodHandle find();
}
