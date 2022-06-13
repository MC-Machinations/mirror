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
import me.machinemaker.mirror.Mirror;
import org.bukkit.Bukkit;

/**
 * Paper specific utility class.
 */
public final class PaperMirror {

    public static final String OBC_PREFIX = Bukkit.getServer().getClass().getPackage().getName();
    public static final String NMS_PREFIX = "net.minecraft";

    public static final Class<?> CRAFT_SERVER_CLASS = Bukkit.getServer().getClass();
    public static final Object CRAFT_SERVER = Bukkit.getServer();

    public static final Class<?> MINECRAFT_SERVER_CLASS = findMinecraftClass("server.MinecraftServer");
    public static final Object MINECRAFT_SERVER = Mirror.fuzzyMethod(MINECRAFT_SERVER_CLASS, MINECRAFT_SERVER_CLASS).names("getServer").find().require(null);

    public static final Class<?> PLAYER_LIST_CLASS = findMinecraftClass("server.players.PlayerList");
    public static final Object PLAYER_LIST = Mirror.fuzzyMethod(MINECRAFT_SERVER_CLASS, PLAYER_LIST_CLASS).find().require(MINECRAFT_SERVER);

    private PaperMirror() {
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
