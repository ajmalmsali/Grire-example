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

package ajmal.BagOfVisualWords.Interfaces;

import ajmal.BagOfVisualWords.Structures.Index;
import ajmal.Helpers.Plugins.GRirePlugin;

/**
 * The weighting scheme used during the retrieval process defines how the final vector of an image will  be formed just
 * before it is compared with other vectors. This is a crucial part of the retrieval process serving many purposes like
 * discriminating the more important words from the insignificant ones (stopwords etc.) and has great effect on the overal
 * performance.
 * @author Lazaros
 */
public abstract class WeightingScheme implements GRirePlugin {

    /**
     * Applies the scheme to the indexed vector.
     * @param TFVector
     * @return
     */
    public abstract float[] formVector(float[] TFVector);

    /**
     * Applies the scheme to the query vector.
     * @param TFVector
     * @return
     */
    public abstract float[] formQueryVector(float[] TFVector);

    /**
     * Sets the index to use.
     * @param index
     */
    public abstract void setIndex(Index index);

    @Override
    public Class getComponentInterface() {
        return WeightingScheme.class;
    }
}
