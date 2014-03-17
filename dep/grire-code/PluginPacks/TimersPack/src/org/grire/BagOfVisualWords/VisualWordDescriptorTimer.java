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
import org.grire.BagOfVisualWords.Interfaces.VisualWordDescriptor;
import org.grire.BagOfVisualWords.Structures.Codebook;
import org.grire.GeneralUtilities.Data.GeneralMap;
import org.grire.GeneralUtilities.Interfaces.FeatureExtractor;
import org.grire.Helpers.Listeners.Listened;

import java.io.File;

@PluginImplementation
public class VisualWordDescriptorTimer extends VisualWordDescriptor {

    @Override
    public String toString() {
        return "VisualWordDescriptorTimer{"+descriptor+"}";
    }

    public VisualWordDescriptorTimer() {
    }

    VisualWordDescriptor descriptor;
    volatile GeneralMap<String,String> map;
    volatile long timeElapsed;
    volatile long imagesNum;
    volatile long maxTime=Long.MIN_VALUE;
    volatile long minTime=Long.MAX_VALUE;
    volatile long avgTime;

    public VisualWordDescriptorTimer(VisualWordDescriptor descriptor, GeneralMap map) {
        this.descriptor = descriptor;
        this.map=map;
    }

    @Override
    public float[] extract(File img) {
        long start=System.nanoTime();
        float[] extract = descriptor.extract(img);
        long time=System.nanoTime()-start;
        update(time);
        return extract;
    }

    @Override
    public float[] extract(Object[] features) {
        long start=System.nanoTime();
        float[] extract = descriptor.extract(features);
        long time=System.nanoTime()-start;
        update(time);
        return extract;
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
    public Codebook getCodebook() {
        return descriptor.getCodebook();
    }

    @Override
    public FeatureExtractor getParser() {
        return descriptor.getParser();
    }

    @Override
    public Listened setUp(Object... args) throws Exception {
        return descriptor.setUp();
    }

    @Override
    public Class[] getParameterTypes() {
        return new Class[]{VisualWordDescriptor.class,GeneralMap.class};
    }

    @Override
    public Class[] getSetUpParameterTypes() {
        return new Class[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getParameterNames() {
        return new String[]{"Descriptor to be used", "Map for storing results"};
    }

    @Override
    public String[] getSetUpParameterNames() {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getDefaultParameterValues() {
        return new String[]{"",""};
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
