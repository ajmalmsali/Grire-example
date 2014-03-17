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
package org.grire.BagOfVisualWords;


import org.grire.BagOfVisualWords.Structures.Index;
import org.grire.BagOfVisualWords.Structures.PoolFeatures;
import org.grire.BagOfVisualWords.Structures.ImagePool;

/**
 * Author: Lazaros Tsochatzidis
 */
import org.grire.BagOfVisualWords.Interfaces.VisualWordDescriptor;

/**
 * It is a class containing the basic structures and components that are needed for a complete Bag Of Visual
 * Word database.
 */
public class BOVWSystem {

    public ImagePool getCollection() {
        return collection;
    }

    public VisualWordDescriptor getDescriptor() {
        return descriptor;
    }

    public Index getIndex() {
        return index;
    }

    ImagePool collection;
    public VisualWordDescriptor descriptor;
    Index index;

    public BOVWSystem(ImagePool col, VisualWordDescriptor desc, Index in) {
        collection=col;
        descriptor=desc;
        index=in;
    }
}
