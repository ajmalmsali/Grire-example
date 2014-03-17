package org.grire.Helpers;

import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Lazaros
 * Date: 28/8/2013
 * Time: 1:02 μμ
 * To change this template use File | Settings | File Templates.
 */
public class DatasetConverter {

    public static Instances convertSet(Set features) {
        Instances insts=new Instances("TrainingSet",new FastVector(((float[])features.iterator().next()).length),features.size());
        float[] atts;
        for (Object o:features) {
            atts= (float[]) o;
            Instance inst=new Instance(atts.length);
            for (int i=0;i<atts.length;i++) inst.setValue(i,atts[i]);
            insts.add(inst);
        }
        return insts;
    }

    public static float[][] convertInstances(Instances insts) {
        double[] temp;
        float[][] ret=new float[insts.numInstances()][];
        for (int i=0;i<ret.length;i++){
            temp=insts.attributeToDoubleArray(i);
            ret[i]=new float[temp.length];
            for (int j=0;j<temp.length;j++)
                ret[i][j]=(float)temp[j];
        }
        return ret;
    }
}
