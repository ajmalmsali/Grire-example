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

package org.grire.GeneralUtilities.Interfaces;

import org.grire.Helpers.Plugins.GRirePlugin;

/**
 * These objects define how the final image descriptors will be compared to each other in order to form the final results of
 * a query.
 * @author Lazaros
 */
public abstract class SimilarityMeasure implements GRirePlugin {
    /**
     * Calculates the similarity between the vectors.
     * @param qv
     * @param v
     * @return
     */
    public abstract float calculate(float[] qv, float[] v);

    @Override
    public Class getComponentInterface() {
        return SimilarityMeasure.class;
    }

    public abstract boolean smallerTheBetter();
}
