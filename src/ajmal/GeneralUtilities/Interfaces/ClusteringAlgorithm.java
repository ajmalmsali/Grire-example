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

package ajmal.GeneralUtilities.Interfaces;

import ajmal.Helpers.Listeners.Listened;
import ajmal.Helpers.Plugins.GRirePlugin;

import java.util.Set;

/**
 * This interface is used for implementing a clustering process. This interface requires the data in the form of a Set
 * which may be also useful for using it just as a classifier for other purposes.
 * @author Lazarps
 */
public abstract class ClusteringAlgorithm extends Listened implements Runnable,GRirePlugin {

    @Override public abstract void run();

    /**
     * Sets the training set of the algorithm.
     * @param features
     */
    public abstract void setFeatures(Set features);

    /**
     * Returns the medians of the classes created which will be the codebook words.
     * @return
     * @throws Exception
     */
    public abstract float[][] getMedians() throws Exception;

    @Override
    public Class getComponentInterface() {
        return ClusteringAlgorithm.class;
    }
}
