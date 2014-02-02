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

package ajmal.BagOfVisualWords.Functions;

import ajmal.BagOfVisualWords.BOVWSystem;
import ajmal.BagOfVisualWords.Interfaces.VisualWordDescriptor;
import ajmal.BagOfVisualWords.Interfaces.WeightingScheme;
import ajmal.BagOfVisualWords.Structures.Index;
import ajmal.GeneralUtilities.Interfaces.SimilarityMeasure;
import ajmal.Helpers.Listeners.Listened;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.*;

/**
 * This class is used to perform a query to the database previously created. It opens a new specified image and compares its
 * bag from the VisualWordDescriptor to all the other bags, after applying a WeightingScheme to both vectors, by using the
 * Similarity specified.
 */
public class QueryPerformer {
    protected WeightingScheme scheme;
    protected VisualWordDescriptor descriptor;
    protected SimilarityMeasure similarity;
    protected TreeSet<setItem> Scores=null;
    protected Index index;
    protected BOVWSystem system;
    
    public QueryPerformer(BOVWSystem system, WeightingScheme scheme, SimilarityMeasure similarity){
        this.scheme = scheme;
        this.descriptor = system.descriptor;
        this.similarity=similarity;
        this.index=system.getIndex();
        this.system=system;
        scheme.setIndex(index);
    }
    
    /**
     * Perform the query using the specified image as input.
     * @param img
     * @return
     * @throws Exception 
     */
    public TreeSet NewQuery(File img) {
        float[] vector = descriptor.extract(img);
        int sign=(similarity.smallerTheBetter()?1:-1);
        vector= scheme.formQueryVector(vector);
        Scores = new TreeSet<>();
        Set<Long> ids = index.lookUp(vector);
        float temp[],v;
        for (Object o : ids) {
            temp= scheme.formVector(index.getImageDescriptor(((Long) o).longValue()));
            v = sign * similarity.calculate(vector, temp);
            setItem item=new setItem(v,(Long)o);
            while (Scores.contains(item)) {
                item.score+=Float.MIN_VALUE;
            }
            Scores.add(item);
        }
        for (Object o : index)
            if (!ids.contains(o))
                Scores.add(new setItem(Scores.last().score+Float.MIN_VALUE,(Long)o));
        return Scores;
    }

    public TreeSet NewQuery(File img, int numOfResults) {
    	final long it2 = System.currentTimeMillis();
        float[] vector = descriptor.extract(img);
        final long it3 = System.currentTimeMillis();
        
        //System.out.println("[INFO] Feature Extraction took:				" + ((it3 - it2) / 1000.0) + " secs");
        
        
        int sign=(similarity.smallerTheBetter()?1:-1),counter=0;
        vector= scheme.formQueryVector(vector);
        Scores = new TreeSet<>();
        Set<Long> ids = index.lookUp(vector);
        float temp[],v;
        for (Object o : ids) {
            temp= scheme.formVector(index.getImageDescriptor(((Long) o).longValue()));
            v = sign * similarity.calculate(vector, temp);
            setItem item=new setItem(v,(Long)o);
            while (Scores.contains(item)) {
                item.score+=Float.MIN_VALUE;
            }
            counter++;
            Scores.add(item);
        }
        Iterator iterator = index.iterator();
        while (counter<numOfResults && iterator.hasNext()) {
            Object next = iterator.next();
            if (!ids.contains(next)) {
                Scores.add(new setItem(Scores.last().score+Float.MIN_VALUE,(Long)next));
                counter++;
            }
        }
        final long it4 = System.currentTimeMillis();
        //System.out.println("[INFO] Search took:				" + ((it4 - it3) / 1000.0) + " secs");
        
        return Scores;
    }

    /**
     * Return a Listened object that will perform a multiple query to the databse fetching the query images
     * from a text file
     * @param ExperimentName
     * @param Query
     * @param Results
     * @param imagesPerQuery
     * @return
     * @throws Exception
     */
    public Listened NewTRECQuery(final String ExperimentName, final InputStream Query, final OutputStream Results , final int imagesPerQuery) throws Exception {
        return new Listened() {
            @Override
            public void run() {
                Scanner sc=new Scanner(Query);
                PrintStream ps = new PrintStream(Results);
                String line,name;
                TreeSet<setItem> results;
                int curr,query=1,sign=(similarity.smallerTheBetter()?-1:1);
                while (sc.hasNextLine()) {
                    curr=0;
                    if (!"".equals(line=sc.nextLine())) {
                        results = NewQuery(new File(line),imagesPerQuery);
                        for (setItem item : results) {
                            try {
                                name=new File(system.getCollection().getImage(item.id)).getName();
                                ps.println(query +" 1 "+name.substring(0, name.lastIndexOf('.'))+" "+(++curr)+" "+sign*(item.score));
                            } catch (Exception e) {
                                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            }
                        }
                        query++;
                    }
                }
            }

            @Override
            public String toString() {
                return "TREC From file Experiment";
            }
        };
    }

    /**
     * Return a Listened object that will perform a multiple query to the databse fetching the query images
     * from a List of Strings.
     * @param ExperimentName
     * @param querylist
     * @param Results
     * @param imagesPerQuery
     * @return
     * @throws Exception
     */
    public Listened NewTRECQuery(final String ExperimentName, final List<String> querylist, final OutputStream Results , final int imagesPerQuery) throws Exception {
        return new Listened() {
            @Override
            public void run() {
                PrintStream ps = new PrintStream(Results);
                String name;
                
                TreeSet<setItem> results;
                int curr,query=1,sign=(similarity.smallerTheBetter()?-1:1);
                int size = querylist.size();
                for (String line:querylist) {
                    curr=0;
                    results = NewQuery(new File(line.replace(" ","\\ ")));
                    for (setItem item : results){
                        try {
                            name=new File(system.getCollection().getImage(item.id)).getName();
                            System.out.println(name.substring(0, name.lastIndexOf('.')));
                            ps.println("CC="+imagesPerQuery);
                            ps.println(query +" 1 "+name.substring(0, name.lastIndexOf('.'))+" "+(++curr)+" "+sign*item.score+" "+ExperimentName+" inter");
                        } catch (Exception e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }
                        if (curr<=imagesPerQuery)
                        {
                        	ps.println("Breakpoint"+curr+" "+imagesPerQuery);
                        	break;
                        }
                    }
                    progressMade(query++, size);
                }
                ps.close();
                actionCompleted();
            }

            @Override
            public String toString() {
                return "TREC Query";
            }
        };
    }

    public SimilarityMeasure getSimilarity() {
        return similarity;
    }

    public WeightingScheme getScheme() {
        return scheme;
    }

    public class setItem implements Comparable {
        public float score;
        public long id;

        setItem(float score, long id) {
            this.score = score;
            this.id = id;
        }

        @Override
        public boolean equals(Object obj) {
            return ((setItem) obj).score==score;
        }

        @Override
        public int compareTo(Object o) {
            setItem other=(setItem) o;
            if (other.score==score) return 0;
            else if (other.score < score) return 1;
            else if (other.score > score) return -1;
            return 0;
        }
    }
}
