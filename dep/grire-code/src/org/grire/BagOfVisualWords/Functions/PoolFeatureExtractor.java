/*
 * Copyright (C) 2013 Lazaros
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.grire.BagOfVisualWords.Functions;

import org.grire.BagOfVisualWords.Structures.ImagePool;
import org.grire.BagOfVisualWords.Structures.PoolFeatures;
import org.grire.GeneralUtilities.Interfaces.FeatureExtractor;
import org.grire.Helpers.Listeners.ConcurrentIterator;
import org.grire.Helpers.Listeners.Listened;

import java.io.File;

/**
 * As its  name states, this class extracts features from all the images in a pool and creates a PoolFeature structure for
 * storing them. The features are extracted using a FeatureExtractor component.
 */

public class PoolFeatureExtractor extends Listened {
    ImagePool collection;
    FeatureExtractor parser;
    PoolFeatures features;
    volatile int parsed,all;
    Thread[] threads;
    Boolean multithreaded;
    
    public PoolFeatureExtractor(ImagePool Collection, FeatureExtractor parser, PoolFeatures Features) {
        this.collection=Collection;
        this.parser=parser;
        this.features=Features;
        this.multithreaded=false;
    }

    public PoolFeatureExtractor(ImagePool Collection, FeatureExtractor parser, PoolFeatures Features, Boolean multithreaded) {
        this.collection=Collection;
        this.parser=parser;
        this.features=Features;
        this.multithreaded=multithreaded;
    }
    
    @Override
    public void run() {
        if (features.forCreation()) {
            all=collection.size();
            ConcurrentIterator iter = new ConcurrentIterator(collection.iterator());
            if (multithreaded) {
                int cores=Runtime.getRuntime().availableProcessors();
                threads=new Thread[cores];
                for (int t=0;t<cores;t++){
                    threads[t]= new FeatureExtractingThread(parser, iter, features);
                    threads[t].start();
                }
                for (int t=0;t<cores;t++){
                    try {
                        threads[t].join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
            }else{
                Long next= (Long) iter.getNext();
                float[][] ParseNew;
                while ( next != -1)  {
                    try {
                        ParseNew = parser.extract(new File(collection.getImage(next)));
                        features.setImageFeatures(next, ParseNew);
                        finishedOne();
                        //System.out.println(next+"  "+this);
                        next=(Long)iter.getNext();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        //Logger.getLogger(PoolFeatureExtractor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            features.setCreatedFrom(collection);
            features.setCreatedFrom(parser);
            collection.setUsedToCreate(features);
            actionCompleted();
        }
    }

    synchronized public void finishedOne() {
        progressMade(parsed++,all);
    }

    @Override
    public String toString() {
        return "PoolFeatureExtractor{" +
                "parser=" + parser +
                ", features=" + features +
                ", collection=" + collection +
                '}';
    }


    class FeatureExtractingThread extends Thread {

        FeatureExtractor parser=null;
        ConcurrentIterator iterator=null;
        PoolFeatures features=null;
        PoolFeatureExtractor base=null;

        FeatureExtractingThread(FeatureExtractor parser, ConcurrentIterator iter, PoolFeatures features) {
            this.parser = parser;
            this.iterator = iter;
            this.features=features;
        }

        @Override
        public void run() {
            Long next= (Long) iterator.getNext();
            float[][] ParseNew;
            while ( next != -1)  {
                try {
                    ParseNew = parser.extract(new File(collection.getImage(next)));
                    features.setImageFeatures(next, ParseNew);
                    finishedOne();
                    //System.out.println(next+"  "+this);
                    next=(Long)iterator.getNext();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    //Logger.getLogger(PoolFeatureExtractor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}


