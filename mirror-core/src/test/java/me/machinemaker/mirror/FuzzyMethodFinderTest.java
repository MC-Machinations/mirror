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
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class FuzzyMethodFinderTest {

    @Test
    void testSimpleFuzzyMethod() {
        MethodHandle invoker = Mirror.fuzzyMethod(ExampleClass.class, Void.TYPE)
                .find();

        invoker = Mirror.fuzzyMethod(ExampleClass.class, Void.TYPE)
                .params(String.class)
                .find();
    }

    @Test
    void testAmbiguous() {
        assertThrows(AmbiguousFuzzyException.class, () -> {
            Mirror.fuzzyMethod(ExampleClass.class, Void.TYPE)
                    .params(Object.class)
                    .find();
        });

        Mirror.fuzzyMethod(ExampleClass.class, Void.TYPE)
                .params(Object.class)
                .names("method1")
                .find();
    }


    static class ExampleClass {


        private void method1() {
        }

        private void method1(final Object object1) {
        }

        private void method1a(final Object object1) {
        }

        private void method1a(final String string1) {
        }

        private String method2() {
            return "";
        }


    }
}
