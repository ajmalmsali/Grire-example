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
package org.grire.GeneralUtilities;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import org.grire.GeneralUtilities.Data.GeneralMap;
import org.grire.GeneralUtilities.Interfaces.ClusteringAlgorithm;
import org.grire.Helpers.Listeners.ActionCompletedListener;
import org.grire.Helpers.Listeners.Listened;
import org.grire.Helpers.Listeners.ProgressMadeListener;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Lazaros
 * Date: 24/8/2013
 * Time: 7:30 μμ
 */

@PluginImplementation
public class ClusteringAlgorithmTimer extends ClusteringAlgorithm {

    public ClusteringAlgorithmTimer() {
    }

    ClusteringAlgorithm algorithm;
    volatile GeneralMap<String,String> map;

    public ClusteringAlgorithmTimer(ClusteringAlgorithm algorithm, GeneralMap map) {
        this.algorithm = algorithm;
        this.map = map;
    }

    @Override
    public void run() {
        long time=System.nanoTime();
        algorithm.run();
        time = System.nanoTime()-time;
        map.put("Time elapsed (nanosecond)",Long.toString(time));
    }

    @Override
    public void setFeatures(Set features) {
        algorithm.setFeatures(features);
    }

    @Override
    public float[][] getMedians() throws Exception {
        return algorithm.getMedians();  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void actionCompleted() {
        algorithm.actionCompleted();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void progressMade(int progress, int max) {
        algorithm.progressMade(progress, max);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void progressMade(int progress, int max, String message) {
        algorithm.progressMade(progress, max, message);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void addCompletionListener(ActionCompletedListener listener) {
        algorithm.addCompletionListener(listener);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void addProgressListener(ProgressMadeListener listener) {
        algorithm.addProgressListener(listener);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public Listened setUp(Object... args) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Class[] getParameterTypes() {
        return new Class[] {ClusteringAlgorithm.class,GeneralMap.class};  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Class[] getSetUpParameterTypes() {
        return new Class[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getParameterNames() {
        return new String[] {"Clustering Algorithm to be used","Map to store the results"};
    }

    @Override
    public String[] getSetUpParameterNames() {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getDefaultParameterValues() {
        return new String[] {"",""};
    }

    @Override
    public String[] getDefaultSetUpParameterValues() {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean requiresSetUp() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String toString() {
        return "ClusteringAlgorithmTimer{"+algorithm+"}";
    }
}
