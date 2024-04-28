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
import java.lang.invoke.MethodHandle;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FuzzyFieldFinderTest {

    @Test
    void testSimpleFuzzyField() throws Throwable {
        final ExampleClass instance = new ExampleClass();

        final FuzzyFieldFinder fuzzyFieldFinder = Mirror.fuzzyField(ExampleClass.class, String.class);
        final MethodHandle getter = fuzzyFieldFinder.find(FuzzyFieldFinder.Type.GETTER);
        final MethodHandle setter = fuzzyFieldFinder.find(FuzzyFieldFinder.Type.SETTER);
        assertEquals("TEST", getter.invoke(instance));

        setter.invokeExact(instance, "OTHER_STRING");
        assertEquals("OTHER_STRING", instance.field);
    }


    @Test
    void testGenericFuzzyField() throws Throwable {
        final ExampleClass instance = new ExampleClass();

        final FuzzyFieldFinder fuzzyFieldFinder = Mirror.fuzzyField(ExampleClass.class, new TypeToken<List<Character>>() {}.getType());
        final MethodHandle getter = fuzzyFieldFinder.find(FuzzyFieldFinder.Type.GETTER);
        final MethodHandle setter = fuzzyFieldFinder.find(FuzzyFieldFinder.Type.SETTER);
        assertEquals(List.of('a', 'b'), getter.invoke(instance));

        setter.invokeExact(instance, List.of('c', 'd'));
        assertEquals(List.of('c', 'd'), instance.charList);
    }

    static class ExampleClass {

        @SuppressWarnings("FieldMayBeFinal")
        private List<Character> charList = List.of('a', 'b');
        @SuppressWarnings("FieldMayBeFinal")
        private String field = "TEST";
    }
}
