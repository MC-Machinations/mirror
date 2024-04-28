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
package me.machinemaker.mirror.paper;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;
import me.machinemaker.mirror.Mirror;
import me.machinemaker.mirror.util.CheckedSupplier;
import org.bukkit.Bukkit;

import static com.google.common.base.Suppliers.memoize;
import static me.machinemaker.mirror.util.CheckedSupplier.checkedMemoize;

/**
 * Paper specific utility class.
 */
public final class PaperMirror {

    @SuppressWarnings("OptionalOfNullableMisuse")
    public static final String OBC_PREFIX = Optional.ofNullable(Bukkit.getServer()).map(s -> s.getClass().getPackage().getName()).orElse("org.bukkit.craftbukkit");
    public static final String NMS_PREFIX = "net.minecraft";

    private static final Supplier<Class<?>> CRAFT_SERVER_CLASS = memoize(() -> Bukkit.getServer().getClass());
    private static final Supplier<Object> CRAFT_SERVER = memoize(Bukkit::getServer);

    private static final Supplier<Class<?>> MINECRAFT_SERVER_CLASS = memoize(() -> findMinecraftClass("server.MinecraftServer"));
    private static final CheckedSupplier<Object, Throwable> MINECRAFT_SERVER = checkedMemoize(() -> Mirror.fuzzyMethod(minecraftServerClass(), minecraftServerClass()).names("getServer").find().invoke());

    private static final Supplier<Class<?>> PLAYER_LIST_CLASS = memoize(() -> findMinecraftClass("server.players.PlayerList"));
    private static final CheckedSupplier<Object, Throwable> PLAYER_LIST = checkedMemoize(() -> Mirror.fuzzyMethod(minecraftServerClass(), playerListClass()).find().invoke(minecraftServer()));

    private PaperMirror() {
    }

    /**
     * Retrieve the CraftServer class.
     *
     * @return the CraftServer class
     */
    public static Class<?> craftServerClass() {
        return CRAFT_SERVER_CLASS.get();
    }

    /**
     * Retrieve the CraftServer instance.
     *
     * @return the CraftServer instance
     */
    public static Object craftServer() {
        return CRAFT_SERVER.get();
    }

    /**
     * Retrieve the MinecraftServer class.
     *
     * @return the MinecraftServer class
     */
    public static Class<?> minecraftServerClass() {
        return MINECRAFT_SERVER_CLASS.get();
    }

    /**
     * Retrieve the MinecraftServer instance.
     *
     * @return the MinecraftServer instance
     * @throws Throwable if the method invocation fails
     */
    public static Object minecraftServer() throws Throwable {
        return MINECRAFT_SERVER.get();
    }

    /**
     * Retrieve the PlayerList class.
     *
     * @return the PlayerList class
     */
    public static Class<?> playerListClass() {
        return PLAYER_LIST_CLASS.get();
    }

    /**
     * Retrieve the PlayerList instance.
     *
     * @return the PlayerList instance
     * @throws Throwable if the method invocation fails
     */
    public static Object playerList() throws Throwable {
        return PLAYER_LIST.get();
    }

    /**
     * Retrieve a class in the org.bukkit.craftbukkit.VERSION.* package.
     *
     * @param name the name of the class, excluding the package
     * @return the craftbukkit class
     * @throws IllegalArgumentException If the class doesn't exist
     */
    public static Class<?> getCraftBukkitClass(final String name) {
        return Mirror.getClass(OBC_PREFIX + "." + name);
    }

    /**
     * Retrieve a class in the org.bukkit.craftbukkit.VERSION.* package.
     *
     * @param names the names of classes to check for
     * @return the first matching craftbukkit class
     * @throws IllegalArgumentException If the class doesn't exist
     */
    public static Class<?> findCraftBukkitClass(final String... names) {
        for (final String name : names) {
            try {
                return getCraftBukkitClass(name);
            } catch (final IllegalArgumentException ignored) {
            }
        }
        throw new IllegalArgumentException("None of " + Arrays.toString(names) + " could be matched to a craftbukkit class");
    }

    /**
     * Retrieve a class in the net.minecraft.server.VERSION.* package.
     *
     * @param name the name of the class
     * @return the minecraft class
     * @throws IllegalArgumentException If the class doesn't exist
     */
    public static Class<?> getMinecraftClass(final String name) {
        return Mirror.getClass(NMS_PREFIX + "." + name);
    }

    /**
     * Retrieve a class in the net.minecraft.server.VERSION.* package.
     *
     * @param names the names of the class
     * @return the first matching minecraft class
     * @throws IllegalArgumentException If the none of the names match a class
     */
    public static Class<?> findMinecraftClass(final String... names) {
        for (final String name : names) {
            try {
                return getMinecraftClass(name);
            } catch (final IllegalArgumentException ignored) {
            }
        }
        throw new IllegalArgumentException("None of " + Arrays.toString(names) + " could be matched to a minecraft class");
    }

}
