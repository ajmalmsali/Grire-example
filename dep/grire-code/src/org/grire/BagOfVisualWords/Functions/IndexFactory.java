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

import org.grire.BagOfVisualWords.Structures.PoolFeatures;
import org.grire.BagOfVisualWords.Structures.Index;
import org.grire.BagOfVisualWords.Interfaces.VisualWordDescriptor;
import org.grire.Helpers.Listeners.ConcurrentIterator;
import org.grire.Helpers.Listeners.Listened;


/**
 * This class is used to create the index of the images in the pool using the features provided. In fact, the actual bag for
 * each image is created according to the supplied VisualWordDescriptor component. In the same time the Document Frequencies
 * are computed for each word and they are also stored in the defined Index.
 */
public class IndexFactory extends Listened {
    protected VisualWordDescriptor descriptor;
    protected PoolFeatures features;
    protected Index index;
    protected Boolean multithreaded;
    volatile int size,c;
    volatile int DocumentFrequencies[];
    
    /**
     * @param descriptor
     * @throws Exception
     */
    public IndexFactory(PoolFeatures cf, VisualWordDescriptor descriptor, Index in){
        this.descriptor = descriptor;
        features=cf;
        index=in;
        this.multithreaded=false;
    }

    public IndexFactory(PoolFeatures cf, VisualWordDescriptor descriptor, Index in, Boolean multithreaded){
        this.descriptor = descriptor;
        features=cf;
        index=in;
        this.multithreaded=multithreaded;
    }
    
    @Override
    public void run() {
        c=0;
        size=features.size();
        DocumentFrequencies=new int[descriptor.getCodebook().size()];
        ConcurrentIterator concurrentIterator = new ConcurrentIterator(features.iterator());
        if (multithreaded){
            int cores=Runtime.getRuntime().availableProcessors();
            Thread[] threads=new Thread[cores];
            for (int i=0;i<cores;i++) {
                threads[i]=new DescriptorCreatingThread(concurrentIterator,descriptor);
                threads[i].start();
            }
            for (int i=0;i<cores;i++) {
                try {
                    threads[i].join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }else{
            Long next=(Long)concurrentIterator.getNext();
            while (next!=-1) {
                System.out.println(next+"  "+this);
                float[] desc = descriptor.extract(features.getImageFeatures(next));
                for (int i=0;i<desc.length;i++)
                    if (desc[i]>0)
                        DocumentFrequencies[i]++;
                index.setImageDescriptor(next, desc);
                finishedOne();
                next=(Long)concurrentIterator.getNext();
            }
        }
        index.setDocumentFrequencies(DocumentFrequencies);
        index.setCreatedFrom(descriptor,features);
        features.setUsedToCreate(index);
        actionCompleted();
    }

    void finishedOne() {
        this.progressMade(++c,size);
    }

    @Override
    public String toString() {
        return "IndexFactory{" +
                "descriptor=" + descriptor +
                ", features=" + features +
                ", index=" + index +
                '}';
    }

    class DescriptorCreatingThread extends Thread {

        ConcurrentIterator iter;
        VisualWordDescriptor descriptor;

        DescriptorCreatingThread(ConcurrentIterator iter, VisualWordDescriptor desc) {
            this.iter = iter;
            descriptor=desc;
        }

        @Override
        public void run() {
            Long next=(Long)iter.getNext();
            while (next!=-1) {
                System.out.println(next+"  "+this);
                float[] desc = descriptor.extract(features.getImageFeatures(next));
                for (int i=0;i<desc.length;i++)
                    if (desc[i]>0)
                        DocumentFrequencies[i]++;
                index.setImageDescriptor(next, desc);
                finishedOne();
                next=(Long)iter.getNext();
            }
        }
    }
}
