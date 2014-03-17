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
import org.grire.GeneralUtilities.Interfaces.FeatureExtractor;
import org.grire.Helpers.Plugins.GRirePlugin;

import java.io.File;

/**
 * This is the main interface of the GRire library and defines the whole process of the model. Its task is to form the final
 * bag (descriptor) of an image using all the previously created structures.
 * @author Lazaros Tsochatzidis <ltsochat at ee.duth.gr>
 */
public abstract class VisualWordDescriptor implements GRirePlugin {
    /**
     * Extracts the bag from an image file using the extractor set for local features.
     * @param img
     * @return
     */
    public abstract float[] extract(File img);

    /**
     * Extracts the bag using the given features without using the local feature extractor.
     * @param features
     * @return
     */
    public abstract float[] extract(Object[] features);

    /**
     * Returns the codebook that the descriptor is currently using.
     * @return
     */
    public abstract Codebook getCodebook();

    /**
     * Returns the feature extractor that the descriptor is currently using.
     * @return
     */
    public abstract FeatureExtractor getParser();

    @Override
    public Class getComponentInterface() {
        return VisualWordDescriptor.class;
    }
}
