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
import org.grire.BagOfVisualWords.Interfaces.Stemmer;
import org.grire.BagOfVisualWords.Structures.Codebook;
import org.grire.GeneralUtilities.Data.GeneralMap;
import org.grire.Helpers.Listeners.Listened;

/**
 * Created with IntelliJ IDEA.
 * User: Lazaros
 * Date: 24/8/2013
 * Time: 6:16 μμ
 */

@PluginImplementation
public class StemmerTimer extends Stemmer {

    public StemmerTimer() {
    }

    Stemmer stemmer;
    volatile GeneralMap<String,String> map;

    volatile long timeElapsed;
    volatile long imagesNum;
    volatile long maxTime=Long.MIN_VALUE;
    volatile long minTime=Long.MAX_VALUE;
    volatile long avgTime;

    public StemmerTimer(Stemmer stemmer, GeneralMap map) {
        this.stemmer = stemmer;
        this.map = map;
    }

    @Override
    public int stemPoint(float[] p) {
        long start=System.nanoTime();
        int res = stemmer.stemPoint(p);
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
    public float getLastDistance() {
        return stemmer.getLastDistance();  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Codebook getCodebook() {
        return stemmer.getCodebook();  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setCodebook(Codebook codebook) {
        stemmer.setCodebook(codebook);
    }

    @Override
    public Listened setUp(Object... args) throws Exception {
        return stemmer.setUp();
    }

    @Override
    public Class[] getParameterTypes() {
        return new Class[]{Stemmer.class,GeneralMap.class};  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Class[] getSetUpParameterTypes() {
        return new Class[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getParameterNames() {
        return new String[]{"Stemmer to be used","Map for storing results"};
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

    @Override
    public String toString() {
        return "StemmetTimer{"+stemmer+"}";
    }
}
