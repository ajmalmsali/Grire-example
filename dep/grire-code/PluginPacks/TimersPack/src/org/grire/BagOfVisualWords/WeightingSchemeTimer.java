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

import net.xeoh.plugins.base.annotations.PluginImplementation;
import org.grire.BagOfVisualWords.Interfaces.WeightingScheme;
import org.grire.BagOfVisualWords.Structures.Index;
import org.grire.GeneralUtilities.Data.GeneralMap;
import org.grire.Helpers.Listeners.Listened;

/**
 * Created with IntelliJ IDEA.
 * User: Lazaros
 * Date: 24/8/2013
 * Time: 7:18 μμ
 */

@PluginImplementation
public class WeightingSchemeTimer extends WeightingScheme {

    @Override
    public String toString() {
        return "WeightingSchemeTimer{"+weightingScheme+"}";
    }

    public WeightingSchemeTimer() {
    }

    WeightingScheme weightingScheme;

    public WeightingSchemeTimer(WeightingScheme weightingScheme, GeneralMap map) {
        this.weightingScheme = weightingScheme;
        this.map = map;
    }

    volatile GeneralMap<String,String> map;

    volatile long timeElapsed;
    volatile long imagesNum;
    volatile long maxTime=Long.MIN_VALUE;
    volatile long minTime=Long.MAX_VALUE;
    volatile long avgTime;

    @Override
    public float[] formVector(float[] TFVector) {
        long start=System.nanoTime();
        float[] res = weightingScheme.formVector(TFVector);
        long time=System.nanoTime()-start;
        update(time);
        return res;
    }

    @Override
    public float[] formQueryVector(float[] TFVector) {
        long start=System.nanoTime();
        float[] res = weightingScheme.formQueryVector(TFVector);
        long time=System.nanoTime()-start;
        update(time);
        return res;
    }

    protected void update(long time) {
        timeElapsed+=time;
        imagesNum++;
        if (time>maxTime)  maxTime=time;
        if (time<minTime)  minTime=time;
        avgTime=timeElapsed/imagesNum;

        map.put("Total time (nanosecond)",Long.toString(timeElapsed));
        map.put("Number of images processed",Long.toString(imagesNum));
        map.put("Average time (nanosecond)",Long.toString(avgTime));
        map.put("Longest time (nanosecond)",Long.toString(maxTime));
        map.put("Shortest time (nanosecond)",Long.toString(minTime));
    }

    @Override
    public void setIndex(Index index) {
        weightingScheme.setIndex(index);
    }

    @Override
    public Listened setUp(Object... args) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Class[] getParameterTypes() {
        return new Class[]{WeightingScheme.class,GeneralMap.class};  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Class[] getSetUpParameterTypes() {
        return new Class[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getParameterNames() {
        return new String[]{"Wighting scheme to be used", "General map for storing the results"};
    }

    @Override
    public String[] getSetUpParameterNames() {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getDefaultParameterValues() {
        return new String[]{"",""};  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getDefaultSetUpParameterValues() {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean requiresSetUp() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
