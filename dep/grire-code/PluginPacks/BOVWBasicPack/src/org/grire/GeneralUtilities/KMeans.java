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


package org.grire.GeneralUtilities;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import org.grire.GeneralUtilities.Interfaces.ClusteringAlgorithm;
import org.grire.Helpers.Listeners.Listened;

import java.util.Arrays;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Lazaros
 */
@PluginImplementation
public class KMeans extends ClusteringAlgorithm {
    
    protected int numClusters = 256;
    protected int sizeOfSet=0;

    public int getNumClusters() {
        return numClusters;
    }

    protected Set features = null;
    protected ClusterPoint[] clusters = null;

   public Set getFeatures() {
        return features;
    }

    @Override
    public void setFeatures(Set features) {
        this.features = features;
    }

    protected float Threshold;

    public KMeans() {
    }

    public KMeans(Float classes, Float errorFactor) {
        this.numClusters=classes.intValue();
        Threshold=(float) Math.pow(10d,(-1*errorFactor.floatValue()));
    }
    
    protected void init() {
        // find first clusters:
        if (sizeOfSet==0) sizeOfSet=features.size();
        clusters = new ClusterPoint[numClusters];
        float probability= (float)numClusters / (float)sizeOfSet;
        int currCluster=0;
        big:
        while (true) {
            for (Object f : features) {
                if (Math.random()<probability) {
                    clusters[currCluster++]=new ClusterPoint((float[]) f);
                    if (currCluster==numClusters) break big;
                }
            }
        }
    }

    /**
     * Do one step and return the overall stress (squared error). You should do this until
     * the error is below a threshold or doesn't change a lot in between two subsequent steps.
     * @return
     */
    protected float clusteringStep() {
        reOrganizeFeatures();
        System.out.println("Reorganized!");
        recomputeMeans();
        System.out.println("Recomputed!");
        float overallStress = overallStress();
        resetClusters();
        return overallStress;
    }

    /**
     * Re-shuffle all features.
     */
    protected void reOrganizeFeatures() {
        int j=0;
        for (Object o : features) {
            //progressMade(++j,sizeOfSet,"Reorganizing features...");
            float[] current=(float[])o;
            ClusterPoint best = clusters[0];
            float minDistance = clusters[0].getSquaredEuclidianDistance(current);
            for (int i = 1; i < clusters.length; i++) {
                float v = clusters[i].getSquaredEuclidianDistance(current);
                if (minDistance > v) {
                    best = clusters[i];
                    minDistance = v;
                }
            }
            best.membersNumber++;
            for (int i=0;i<current.length;i++) {
                best.nextLocation[i]+=current[i];
            }
        }
    }

    /**
     * Computes the mean per cluster (averaged vector)
     */
    protected void recomputeMeans() {
        int j=0;
        for (ClusterPoint cluster : clusters) {
            //progressMade(++j,clusters.length,"Recomputing means...");
            if (cluster.membersNumber>0) {
                for (int i=0;i<cluster.nextLocation.length;i++) {
                    cluster.nextLocation[i]/=cluster.membersNumber;
                }
            }
        }
    }
    
    protected void resetClusters() {
        for (ClusterPoint cluster : clusters) {
            if (cluster.membersNumber > 0)
                cluster.swapLocations();
            cluster.nextLocation=new float[cluster.Location.length];
            cluster.membersNumber=0;
        }
    }

    /**
     * Squared error in classification.
     * @return
     */
    protected float overallStress() {
        float stress=0;
        for (int i=0;i<clusters.length;i++) {
            stress+=clusters[i].GetMovement();
        }
        return stress/clusters.length;
    }

    public ClusterPoint[] getClusters() {
        return clusters;
    }
    
    public KMeans setThreshold(float a) {
        this.Threshold=a;
        return this;
    }

    @Override
    public String toString() {
        return "KMeans{" + "Classes=" + numClusters + '}';
    }

    @Override
    public void run() {
        try {
            if (features==null) throw new Exception("Features not set.");
            init();
            System.out.println("KMeans: Initialized!");
            float curStress = this.clusteringStep(), lastStress = Float.MAX_VALUE;
            float init=0,goal=Float.MIN_VALUE, stress=Float.MAX_VALUE;
            int itters=0;
            while (stress > goal) {
                lastStress = curStress;
                curStress = this.clusteringStep();
                stress=Math.abs(curStress - lastStress);
                if (itters++==0)     {
                    goal=Threshold*stress;
                    init=stress;
                }
                System.out.println(Math.abs(curStress - lastStress));
                progressMade((int)(100/Math.log(init/goal)*Math.log(init/stress)),100,"KMeans running, Iteration: "+itters);
            }
        } catch (Exception ex) {
            Logger.getLogger(KMeans.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public float[][] getMedians() throws Exception{
        if (clusters==null) throw new Exception("KMeans did not run yet."); 
        float[][] ret=new float[clusters.length][];
        int locSize=clusters[0].Location.length;
        for (int i=0;i<clusters.length;i++) {
            ret[i]=Arrays.copyOf(clusters[i].Location, locSize);
        }
        return ret;
    }

    @Override
    public Listened setUp(Object... args) throws Exception {
        return null;
    }

    @Override
    public Class[] getParameterTypes() {
        return new Class[] {Float.class, Float.class};
    }

    @Override
    public Class[] getSetUpParameterTypes() {
        return new Class[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getParameterNames() {
        return new String[] {"Number Of Classes", "Error reduction factor"};  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getSetUpParameterNames() {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getDefaultParameterValues() {
        return new String[]{"256","4"};  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getDefaultSetUpParameterValues() {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean requiresSetUp() {
        return false;
    }

    public void setTrainingSize(Integer a) {
        sizeOfSet=a.intValue();
    }

}