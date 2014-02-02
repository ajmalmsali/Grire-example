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

package ajmal.BagOfVisualWords;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import ajmal.BagOfVisualWords.Structures.Index;
import ajmal.BagOfVisualWords.Interfaces.WeightingScheme;
import ajmal.Helpers.Listeners.Listened;

/**
 * SMART System is both a WeightingScheme and a SimilarityMeasure and its functionality is
 * determined by the notation string.
 * @author Lazaros
 */
@PluginImplementation
public class SMARTModel extends WeightingScheme {
    protected Index index;
    private String notation;
    
    private int N=0;
    private int[] DFs;

    public SMARTModel() {
    }

    public SMARTModel(String notation)  {
        this.notation=notation;
    }



    @Override
    public float[] formVector(float[] TFVector) {
        return Form(TFVector,0);
    }
    
    @Override
    public float[] formQueryVector(float[] TFVector) {
        return Form(TFVector,4);
    }

    @Override
    public void setIndex(Index index) {
        this.index=index;
        this.N=index.size();
        DFs=index.getDocumentFrequencies();

    }

    protected float[] Form(float[] a, int offset) {
        float[] ret=new float[a.length];
        System.arraycopy(a, 0, ret, 0, ret.length);
        switch(notation.charAt(0+offset)) {
            case 'l':
                for (int i=0;i<ret.length;i++)
                    ret[i]=1f+(float)Math.log(ret[i]);
                break;
            case 'a':
                int maxi=0;
                for (int i=1;i<ret.length;i++)
                    if (ret[i]>ret[maxi])
                        maxi=i;
                float maxtf=ret[maxi];
                for (int i=0;i<ret.length;i++)
                    ret[i]=0.5f+0.5f*ret[i]/maxtf;
                break;
            case 'b':
                for (int i=0;i<ret.length;i++)
                    ret[i]=(ret[i] > 0f ? 1f : 0f);
                break;
            case 'L':
                float ave=0;
                for (int i=0;i<ret.length;i++)
                    ave+=ret[i];
                ave/= ((float) ret.length);
                for (int i=0;i<ret.length;i++)
                    ret[i]=(1f+(float)Math.log(ret[i]))/(1f+(float)Math.log(ave));
                break;
        }
        
        switch (notation.charAt(1+offset)) {
            case 't':
                for (int i=0;i<ret.length;i++) {
                    Integer df= DFs[i];
                    float temp=1;
                    if (df!=0) {
                        temp=(float)Math.log( (float)N/ Float.valueOf( df ));
                    }
                    ret[i]*=temp;
                }
                break;
            case 'p':
                for (int i=0;i<ret.length;i++) {
                    float prob=(float)Math.log(((float)this.N-(float)DFs[i])/(float)DFs[i]);
                    ret[i]*=(prob>0 ? prob : 0);
                }
                break;
        }
        
        if (notation.charAt(2+offset)=='c') {
            float norm=norm(ret);
                for (int i=0;i<ret.length;i++)
                    ret[i]/=(norm==0?1:norm);
        }
        return ret;
    }
    
    protected float norm(float[] a) {
        Float ret=0f;
        for (Float n: a) {
            ret+=n*n;
        }
        return (float) Math.sqrt(ret);
    }

    @Override
    public Listened setUp(Object... args) {
        return null;
    }

    @Override
    public Class[] getParameterTypes() {
        Class[] ret=new Class[1];
        ret[0]=String.class;
        return ret;
    }

    @Override
    public Class[] getSetUpParameterTypes() {
        return new Class[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getParameterNames() {
        return new String[]{"Notation"};  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getSetUpParameterNames() {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getDefaultParameterValues() {
        return new String[]{"nnn:nnn"};  //To change body of implemented methods use File | Settings | File Templates.
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
        return "SMARTModel:"+notation;
    }
}
