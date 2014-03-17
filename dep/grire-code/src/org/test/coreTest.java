package org.test;

import org.grire.BagOfVisualWords.BOVWSystem;
import org.grire.BagOfVisualWords.Functions.*;
import org.grire.BagOfVisualWords.Interfaces.VisualWordDescriptor;
import org.grire.BagOfVisualWords.Interfaces.WeightingScheme;
import org.grire.BagOfVisualWords.Structures.Codebook;
import org.grire.BagOfVisualWords.Structures.ImagePool;
import org.grire.BagOfVisualWords.Structures.Index;
import org.grire.BagOfVisualWords.Structures.PoolFeatures;
import org.grire.GeneralUtilities.GeneralStorers.GeneralMapDBStorer;
import org.grire.GeneralUtilities.GeneralStorers.GeneralStorer;
import org.grire.GeneralUtilities.Interfaces.ClusteringAlgorithm;
import org.grire.GeneralUtilities.Interfaces.FeatureExtractor;
import org.grire.GeneralUtilities.Interfaces.SimilarityMeasure;
import org.grire.Helpers.Listeners.Listened;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.*;

/**
 * This is the basic test. It creates a whole new DB and performs basic actions of reading and writing.
 * It is meant to test functionality if a change in GeneralStorer implementation or in core classes
 * has been made.
 */
public class coreTest {

    GeneralStorer storer;
    ImagePool pool;
    PoolFeatures features;
    Codebook codebook;
    Index index;
    BOVWSystem system;

    @Before
    public void checkAndDeleteDBFile() {

        //----------------------------------------------------------------------------
        //  Modify the GeneralStorer implementation used to test your GeneralStorer
        //----------------------------------------------------------------------------
        this.storer = new GeneralMapDBStorer("testDB");
    }

    @Test
    public void importImagesTest() throws Exception {

        //Creating ImagePool
        pool = new ImagePool(storer,"testPool",true);
        Importer imp=new Importer(new String[]{"test_dataset"},pool);
        imp.run();

        //Testing
        pool = new ImagePool(storer,"testPool");
        assertEquals(6,pool.size());
        assertEquals("testPool",pool.getName());
        assertEquals("collection.testPool",pool.getDBName());
        for (Object s:pool) {
            assertNotNull(s);
            assertNotNull(pool.getImage((Long)s));
        }

        //Creating Features
        features=new PoolFeatures(storer,"testFeatures",true);
        testExtractor parser = new testExtractor();
        PoolFeatureExtractor pfe=new PoolFeatureExtractor(pool, parser,features);
        pfe.run();

        //Testing
        features=new PoolFeatures(storer,"testFeatures");
        assertEquals(6,features.size());
        assertEquals("testFeatures",features.getName());
        assertEquals("features.testFeatures",features.getDBName());
        assertTrue(features.getCreatedFrom().contains(pool.getDBName()));
        assertTrue(features.getCreatedFrom().contains(parser.toString()));
        assertTrue(pool.getUsedToCreate().contains(features.getDBName()));
        float[] initial=new float[12];
        for (Object o:features) {
            assertNotNull(o);
            Object[] feats=features.getImageFeatures((Long) o);
            assertNotNull(feats);
            assertEquals(10,feats.length);
            for (Object ob:feats){
                assertArrayEquals((float[])ob,initial,0.1f);
            }
        }

        //Create]ing Codebook
        codebook=new Codebook(storer,"testCodebook",true);
        ClusteringCodebookFactory ccf=new ClusteringCodebookFactory(features,new testClustering(),codebook);
        ccf.setPercentageOfData(1);
        ccf.run();

        //Testing
        codebook=new Codebook(storer,"testCodebook");
        assertEquals(60,codebook.size());
        for (Object o:codebook) {
            assertNotNull(o);
            float[] word=codebook.getCodebookWord((Integer)o);
            assertNotNull(word);
            assertArrayEquals(new float[12],word,0.1f);
        }

        //Creating Index
        index=new Index(storer,"testIndex",true);
        testDescriptor descriptor = new testDescriptor(codebook);

        IndexFactory ifac=new IndexFactory(features, descriptor,index);
        ifac.run();

        //Testing
        index=new Index(storer,"testIndex");
        assertEquals(6,index.size());
        for (Object o:index){
            assertNotNull(o);
            float[] desc=index.getImageDescriptor((Long)o);
            assertNotNull(desc);
            float[] expecteds = new float[15];
            Arrays.fill(expecteds,10);
            assertArrayEquals(expecteds,desc,0.1f);
        }

        system=new BOVWSystem(pool,descriptor,index);
        QueryPerformer qp=new QueryPerformer(system,new testScheme(),new testSimilarity());
        TreeSet map = qp.NewQuery(new File("test_dataset/ukbench00000.jpg"));
        assertEquals(6,map.size());
    }

    @After
    public void cleanUp() {
        storer.close();
        File f1=new File("testDB");
        File f2=new File("testDB.p");
        if (f1.exists()) f1.delete();
        if (f2.exists()) f2.delete();
    }
}


class testExtractor extends FeatureExtractor {

    @Override
    public float[][] extract(File img) {
        float[][] ret=new float[10][];
        for (int i=0;i<10;i++)
            ret[i]=new float[12];
        return ret;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public float[][] extract(BufferedImage img) {
        float[][] ret=new float[10][];
        for (int i=0;i<10;i++)
            ret[i]=new float[12];
        return ret;
    }

    @Override
    public float[][] getPositions() {
        return new float[0][];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Listened setUp(Object... args) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Class[] getParameterTypes() {
        return new Class[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Class[] getSetUpParameterTypes() {
        return new Class[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getParameterNames() {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getSetUpParameterNames() {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getDefaultParameterValues() {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
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
class testClustering extends ClusteringAlgorithm {

    Set features;
    float[][] medians;

    @Override
    public void run() {
        medians=new float[features.size()][];
        int i=0;
        for (Object o:features) {
            medians[i++]=(float[])o;
        }
    }

    @Override
    public void setFeatures(Set features) {
        this.features=features;
    }

    @Override
    public float[][] getMedians() throws Exception {
        return medians;
    }

    @Override
    public Listened setUp(Object... args) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Class[] getParameterTypes() {
        return new Class[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Class[] getSetUpParameterTypes() {
        return new Class[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getParameterNames() {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getSetUpParameterNames() {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getDefaultParameterValues() {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
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
class testDescriptor extends VisualWordDescriptor {

    Codebook cb;

    testDescriptor(Codebook cb) {
        this.cb = cb;
    }

    @Override
    public float[] extract(File img) {
        float[] floats = new float[15];
        Arrays.fill(floats,10);
        return floats;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override

    public float[] extract(Object[] features) {
        float[] floats = new float[15];
        Arrays.fill(floats,10);
        return floats;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Codebook getCodebook() {
        return cb;
    }

    @Override
    public FeatureExtractor getParser() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Listened setUp(Object... args) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Class[] getParameterTypes() {
        return new Class[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Class[] getSetUpParameterTypes() {
        return new Class[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getParameterNames() {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getSetUpParameterNames() {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getDefaultParameterValues() {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
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
class testScheme extends WeightingScheme {

    @Override
    public float[] formVector(float[] TFVector) {
        return TFVector;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public float[] formQueryVector(float[] TFVector) {
        return TFVector;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setIndex(Index index) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Listened setUp(Object... args) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Class[] getParameterTypes() {
        return new Class[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Class[] getSetUpParameterTypes() {
        return new Class[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getParameterNames() {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getSetUpParameterNames() {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getDefaultParameterValues() {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
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
class testSimilarity extends SimilarityMeasure {

    int n=0;

    @Override
    public float calculate(float[] qv, float[] v) {
        return n++;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean smallerTheBetter() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Listened setUp(Object... args) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Class[] getParameterTypes() {
        return new Class[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Class[] getSetUpParameterTypes() {
        return new Class[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getParameterNames() {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getSetUpParameterNames() {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getDefaultParameterValues() {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
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