import net.xeoh.plugins.base.annotations.PluginImplementation;
import org.grire.GeneralUtilities.Interfaces.ClusteringAlgorithm;
import org.grire.Helpers.Listeners.Listened;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.TermCriteria;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Lazaros
 * Date: 5/11/2013
 * Time: 6:27 μμ
 * To change this template use File | Settings | File Templates.
 */
@PluginImplementation
public class cvKMeans extends ClusteringAlgorithm {

    int classes,attempts;
    float epsilon;
    Set features;
    Mat a,centers;

    public cvKMeans() {
    }

    public cvKMeans(Float classes, Float attempts, Float epsilon) {
        this.classes = classes.intValue();
        this.attempts = attempts.intValue();
        this.epsilon = epsilon;
    }

    @Override
    public void run() {
        int cols=0;
        for (Object o : features){
            cols = ((float[])o).length;
            break;
        }
        Mat bestLabels=new Mat();

        centers=new Mat();
        a=new Mat(features.size(),cols,5);
        int row=0,col=0;
        float[] feat;
        for (Object o : features){
            feat=(float[])o;
            for (col=0;col<feat.length;col++)
                a.put(row,col,(float)feat[col]);
            row++;
        }

        TermCriteria criteria=new TermCriteria(TermCriteria.COUNT+TermCriteria.EPS,attempts,epsilon);
        org.opencv.core.Core.kmeans(a, classes, bestLabels, criteria, attempts, Core.KMEANS_RANDOM_CENTERS, centers);
    }

    @Override
    public void setFeatures(Set features) {
        this.features=features;
    }

    @Override
    public float[][] getMedians() throws Exception {
        float[][] ret=new float[centers.rows()][];
        for (int j=0;j<centers.rows();j++) {
            ret[j]=new float[centers.cols()];
            for (int i=0;i<centers.cols();i++)
                ret[j][i]=(float)centers.get(j,i)[0];
        }
        return ret;
    }

    @Override
    public Listened setUp(Object... args) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Class[] getParameterTypes() {
        return new Class[]{Float.class,Float.class,Float.class};  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Class[] getSetUpParameterTypes() {
        return new Class[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getParameterNames() {
        return new String[]{"Number of classes","Max number of iterations","Movement of centers threshold (epsilon)"};  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getSetUpParameterNames() {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getDefaultParameterValues() {
        return new String[]{"256","",""};
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
        return "cvKMeans{" +
                "classes=" + classes +
                ", attempts=" + attempts +
                ", epsilon=" + epsilon +
                '}';
    }
}
