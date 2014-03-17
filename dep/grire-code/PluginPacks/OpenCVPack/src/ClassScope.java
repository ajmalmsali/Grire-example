/* This file is part of the GRire project: https://code.google.com/p/grire/
 *
 * GRire is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GRire is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *  
 * Copyright (C) 2013 Lazaros Tsochatzidis <ltsochat at ee.duth.gr>
 */

import java.util.Vector;

/**
 * Author: Lazaros Tsochatzidis
 */
public class ClassScope {
    private static java.lang.reflect.Field LIBRARIES=null;

    static {
        try {
            LIBRARIES = ClassLoader.class.getDeclaredField("loadedLibraryNames");
            LIBRARIES.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public static String[] getLoadedLibraries(final ClassLoader loader) {
        final Vector<String> libraries;
        try {
            libraries = (Vector<String>) LIBRARIES.get(loader);
            return libraries.toArray(new String[] {});
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}