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

package Components;

import org.grire.BagOfVisualWords.Interfaces.Stemmer;
import org.grire.BagOfVisualWords.Structures.Codebook;
import org.grire.Helpers.Listeners.Listened;

/**
 * Classifies each feature to a word of the codebook calculating the smallest
 * euclidian distance.
 * @author Lazaros
 */

public class EuclidianStemmer extends Stemmer {
    
    protected float[][] codebook;
    private float lastDist=-1f;
    protected Codebook OCodebook;

    public EuclidianStemmer() {
    }


    @Override
    public int stemPoint(float[] p) {
        if (codebook==null) codebook=OCodebook.toArray();
        int minId=0,size=codebook.length;
        float minValue = Float.MAX_VALUE;
        float curValue;
        
        for (int i=0;i<size;i++){
            curValue = squaredEuclidianDistance(codebook[i],p);
            if (curValue < minValue) {
                minValue = curValue;
                minId=i;
            }
        }
        this.lastDist = minValue;
        return minId;
    }

    protected float squaredEuclidianDistance(float[] a,float[] b) {
        float ret=0;
        for (int i=0;i<a.length;i++){
            ret+=(a[i]-b[i])*(a[i]-b[i]);
        }
        return ret;
    }
    
    /**
     *
     * @return
     * @throws Exception
     */
    @Override
    public float getLastDistance() {
        return (float) Math.sqrt(lastDist);
    }

    @Override
    public String toString() {
        return "EuclidianStemmer";
    }

    @Override
    public Codebook getCodebook() {
        return OCodebook;
    }

    @Override
    public void setCodebook(Codebook codebook) {
        OCodebook=codebook;
    }


    @Override
    public Listened setUp(Object... args) {
        return null;
    }

    @Override
    public Class[] getParameterTypes() {
        return new Class[]{};
    }

    @Override
    public Class[] getSetUpParameterTypes() {
        return new Class[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getParameterNames() {
        return new String[]{};  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getSetUpParameterNames() {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getDefaultParameterValues() {
        return new String[]{};  //To change body of implemented methods use File | Settings | File Templates.
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
