package org.grire.GeneralUtilities;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import org.grire.GeneralUtilities.Interfaces.ClusteringAlgorithm;
import org.grire.Helpers.DatasetConverter;
import org.grire.Helpers.Listeners.Listened;
import weka.clusterers.SimpleKMeans;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Lazaros
 * Date: 21/8/2013
 * Time: 9:35 μμ
 * To change this template use File | Settings | File Templates.
 */

@PluginImplementation
public class Simple_KMeans extends ClusteringAlgorithm {

    Set features;
    SimpleKMeans kmeans;
    int numClusters;

    public Simple_KMeans() {
    }

    public Simple_KMeans(Float numClusters) {
        this.numClusters = numClusters.intValue();
    }

    @Override
    public void run() {
        kmeans = new SimpleKMeans();
        try {
            progressMade(0,100,"Started");
            kmeans.setNumClusters(numClusters);
            kmeans.buildClusterer(DatasetConverter.convertSet(features));
            actionCompleted();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Override
    public String toString() {
        return "Simple_KMeans{"+numClusters+"}";
    }

    @Override
    public void setFeatures(Set features) {
        this.features=features;
    }

    @Override
    public float[][] getMedians() throws Exception {
        return DatasetConverter.convertInstances(kmeans.getClusterCentroids());
    }

    @Override
    public Listened setUp(Object... args) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Class[] getParameterTypes() {
        return new Class[]{Float.class};  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Class[] getSetUpParameterTypes() {
        return new Class[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getParameterNames() {
        return new String[]{"Number of classes"};  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getSetUpParameterNames() {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getDefaultParameterValues() {
        return new String[]{"256"};  //To change body of implemented methods use File | Settings | File Templates.
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
