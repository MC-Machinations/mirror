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
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FuzzyFieldTest {

    @Test
    void testSimpleFuzzyField() {
        final ExampleClass instance = new ExampleClass();

        final FieldAccessor accessor = Mirror.fuzzyField(ExampleClass.class, String.class).find();
        assertEquals("TEST", accessor.get(instance));

        accessor.set(instance, "OTHER_STRING");
        assertEquals("OTHER_STRING", instance.field);
    }


    @Test
    void testGenericFuzzyField() {
        final ExampleClass instance = new ExampleClass();

        final FieldAccessor.Typed<List<Character>> accessor = Mirror.typedFuzzyField(ExampleClass.class, new TypeToken<List<Character>>() {})
                .find();
        assertEquals(List.of('a', 'b'), accessor.get(instance));

        accessor.set(instance, List.of('c', 'd'));
        assertEquals(List.of('c', 'd'), instance.charList);
    }

    static class ExampleClass {

        @SuppressWarnings("FieldMayBeFinal")
        private List<Character> charList = List.of('a', 'b');
        @SuppressWarnings("FieldMayBeFinal")
        private String field = "TEST";
    }
}
