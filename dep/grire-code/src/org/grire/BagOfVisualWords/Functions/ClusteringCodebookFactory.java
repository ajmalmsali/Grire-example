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

package org.grire.BagOfVisualWords.Functions;

import org.grire.BagOfVisualWords.Structures.Codebook;
import org.grire.BagOfVisualWords.Structures.PoolFeatures;
import org.grire.Helpers.Listeners.ActionCompletedListener;
import org.grire.Helpers.Listeners.Listened;

import org.grire.GeneralUtilities.Interfaces.ClusteringAlgorithm;
import org.grire.Helpers.Listeners.ProgressMadeListener;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * This class creates a new lexicon (codebook) by performing a ClusteringAlgorithm on the features provided. By default, the
 * factory will create cluster from the 30% of the data but user have the ability to modify the percentage. The training set
 * is stored temporary in disk.
 */
public class ClusteringCodebookFactory extends Listened{

    protected ClusteringAlgorithm classifier;
    
    
    protected PoolFeatures features;
    protected Codebook codebook;
    protected float percentageOfData=0.3f;
    protected Integer trainingSetSize=0;

    public float getPercentageOfData() {
        return percentageOfData;
    }

    /**
     * Sets the percentage of the dataset that will be used as training set.
     * @param percentageOfData
     */
    public void setPercentageOfData(float percentageOfData) {
        this.percentageOfData = percentageOfData;
    }
    
    public ClusteringCodebookFactory(PoolFeatures ifeats, ClusteringAlgorithm ca, Codebook cb) {
        codebook=cb;
        features=ifeats;
        classifier=ca;
        classifier.addCompletionListener(new ActionCompletedListener() {
            @Override
            public void OnCompleted() {
                actionCompleted();
            }
        });
        classifier.addProgressListener(new ProgressMadeListener() {
            @Override
            public void OnProgressMade(int progress, int total, String message) {
                progressMade(progress,total,message);
            }
        });
    }

    @Override
    public void run() {
        try {
            if (!codebook.forCreation()) throw new Exception("Codebook not opened for creation. Use different constructor.");
            Set trainingSet = PartitionData();
            classifier.setFeatures(trainingSet);
            try {
                classifier.getClass().getMethod("setTrainingSize", Integer.class).invoke(classifier,trainingSetSize);
            } catch (Exception e) {}
            classifier.run();
            classifier.addProgressListener(new ProgressMadeListener() {
                @Override
                public void OnProgressMade(int progress, int total, String message) {
                    this.OnProgressMade(progress, total, message);
                }
            });
            codebook.storeCodebook(classifier.getMedians());
            codebook.setCreatedFrom(features);
            codebook.setCreatedFrom("Clustering: "+classifier.toString());
            features.setUsedToCreate(codebook);
            features.getStorer().DeleteSet("clusteringCodebookFactoryTemp");
            actionCompleted();
        } catch (Exception ex) {
            Logger.getLogger(ClusteringCodebookFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    protected Set PartitionData() {
        Set set = features.getStorer().GetSet("clusteringCodebookFactoryTemp");
        long size=features.totalFeatures();
        for (Object o : features) {
            Object[] imgFeats=  features.getImageFeatures((Long) o);
            for (int i=0;i<imgFeats.length;i++)
                if (Math.random()<percentageOfData){
                    set.add(imgFeats[i]);
                    progressMade(trainingSetSize++,(int)((float)size*percentageOfData+1),"Partitioning");
                }
        }
        return set;
    }

    @Override
    public String toString() {
        return "ClusteringCodebookFactory{" +
                "classifier=" + classifier +
                ", features=" + features +
                '}';
    }
}
