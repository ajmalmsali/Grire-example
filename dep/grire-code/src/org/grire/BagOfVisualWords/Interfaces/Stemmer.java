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
 * Copyright (C) 2012 Lazaros Tsochatzidis <ltsochat at ee.duth.gr>
 */

package org.grire.BagOfVisualWords.Interfaces;

import org.grire.BagOfVisualWords.Structures.Codebook;
import org.grire.Helpers.Plugins.GRirePlugin;


/**
 * Stemmer’s are the objects that define how a new word from an image is being assigned to a codebook word. For example, a
 * random word will be considered as a word from the codebook from which it has the minimum euclidean distance.
 * @author Lazaros
 */
public abstract class Stemmer implements GRirePlugin {
    /**
     * Decides the word most similar to the given vector and return its id.
     * @param p
     * @return
     */
    public abstract int stemPoint(float[] p);

    /**
     * Return the distance of the last stemming performed.
     * @return
     */
    public abstract float getLastDistance();

    /**
     * Returns the codebook that the stemmer is currently using.
     * @return
     */
    public abstract Codebook getCodebook();


    /**
     * Sets the codebook that the stemmer will use.
     * @param codebook
     */
    public abstract void setCodebook(Codebook codebook);

    @Override
    public Class getComponentInterface() {
        return Stemmer.class;
    }
}
