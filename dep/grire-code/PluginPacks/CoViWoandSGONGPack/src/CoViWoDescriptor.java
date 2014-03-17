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
 * Copyright (C) 2013 Lazaros Tsochatzidis <ltsochat at ee.duth.gr>
 */

import Components.CEDDtools.Fuzzy10Bin;
import Components.CEDDtools.Fuzzy24Bin;
import Components.CEDDtools.RGB2HSV;
import Components.EuclidianStemmer;
import net.xeoh.plugins.base.annotations.PluginImplementation;
import org.grire.BagOfVisualWords.Interfaces.Stemmer;
import org.grire.BagOfVisualWords.Interfaces.VisualWordDescriptor;
import org.grire.BagOfVisualWords.Structures.Codebook;
import org.grire.BagOfVisualWords.Structures.PoolFeatures;
import org.grire.GeneralUtilities.Data.Histogram;
import org.grire.GeneralUtilities.Interfaces.FeatureExtractor;
import org.grire.Helpers.Listeners.Listened;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

    /**
     * Author: Lazaros Tsochatzidis
     */
    @PluginImplementation
    public class CoViWoDescriptor extends VisualWordDescriptor {

        Stemmer stemmer;
        FeatureExtractor parser;
        Histogram histogram;
        Codebook codebook;
        int codebookSize;

        public CoViWoDescriptor() {
        }

        public CoViWoDescriptor(FeatureExtractor extractor,Codebook codebook, Histogram hist) {
            this.stemmer = new EuclidianStemmer();
            this.parser=extractor;
            this.histogram=hist;
            this.codebook=codebook;
            stemmer.setCodebook(codebook);
            codebookSize=codebook.size();
        }

    @Override
    public float[] extract(File image) {
        float[] descriptor = new float[24*codebook.size()],color;
        int assignment,distance,k=0;
        float[][] points = parser.extract(image);
        float[][] positions=parser.getPositions();

        for (float[] p : points) {
            assignment = stemmer.stemPoint(p);
            try {
                color = this.ExtractColor(ImageIO.read(image),(int) positions[k][0], (int) positions[k][1]);
            }catch(Exception e){
                return null;
            }
            distance = (int) Math.ceil(1000*euclidian(p, codebook.getCodebookWord(assignment)));
            for (int i=0;i<color.length;i++)
                color[i] *= histogram.getValueAt(distance);
            System.arraycopy(color, 0, descriptor, 24*assignment, 24);
            k++;
        }
        return descriptor;
    }

    protected float[] ExtractColor(BufferedImage image, int x, int y) {
        Color col;
        col = new Color(image.getRGB(x, y));

        RGB2HSV converter = new RGB2HSV();
        int[] hsv = converter.ApplyFilter(col.getRed(), col.getGreen(), col.getBlue());

        Fuzzy10Bin fuzzy10 = new Fuzzy10Bin(false);
        float[] fuzzy10table = fuzzy10.ApplyFilter(hsv[0], hsv[1], hsv[2], 2);

        Fuzzy24Bin fuzzy24 = new Fuzzy24Bin(false);
        float[] fuzzy24table = fuzzy24.ApplyFilter(hsv[0], hsv[1], hsv[2], fuzzy10table, 2);

        return fuzzy24table;
    }

    protected float euclidian(float[] QueryVector, float[] Vector) {
        float sum=0;
        for (int i=0;i<QueryVector.length;i++) {
            sum+=(QueryVector[i]-Vector[i])*(QueryVector[i]-Vector[i]);
        }
        return (float) Math.sqrt(sum);
    }

    @Override
    public float[] extract(Object[] features) {
        return new float[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Codebook getCodebook() {
        return stemmer.getCodebook();
    }

    @Override
    public FeatureExtractor getParser() {
        return this.parser;
    }

    @Override
    public String toString() {
        return "CoViWoDescriptor";
    }

    @Override
    public Listened setUp(final Object... args)  {
        return new Listened() {
            PoolFeatures features=(PoolFeatures)args[0];
            Stemmer stemmer1;
            String histogramName=(String)args[2];
            @Override
            public void run() {
                stemmer1=new EuclidianStemmer();
                stemmer1.setCodebook((Codebook)args[1]);
                int codebookSize=stemmer1.getCodebook().size();
                int featuresSize=features.size();
                int curr=0;
                float dataLength=0;
                float[] histogramArr=new float[2000];
                for (Object o:features) {
                    Object[] value = features.getImageFeatures((Long) o);
                    curr++;
                    this.progressMade(curr,featuresSize);
                    for (Object f:value) {
                        dataLength++;
                        stemmer1.stemPoint((float[]) f);
                        float v = stemmer1.getLastDistance();
                        if (v > 1.999d) {
                            histogramArr[1999]++;
                            break;
                        }
                        for (int i=(int) (v *1000); i<2000;i++){
                            histogramArr[i]++;
                        }
                    }
                }
                Histogram histogram= null;
                try {
                    histogram = new Histogram(features.getStorer(),histogramName,true);
                } catch (Exception e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                for (int i=0;i<2000;i++) {
                    histogramArr[i] /=  dataLength * (float) codebookSize;
                    histogram.setValueAt((float)i,histogramArr[i]);
                }
                actionCompleted();
            }

            @Override
            public String toString() {
                return "CoViWoSetUp{" +
                        "features=" + features +
                        ", histogramName='" + histogramName + '\'' +
                        '}';
            }
        };
    }

    @Override
    public Class[] getParameterTypes() {
        return new Class[] {FeatureExtractor.class,Codebook.class,Histogram.class};
    }

    @Override
    public Class[] getSetUpParameterTypes() {
        return new Class[]{PoolFeatures.class,Codebook.class,String.class};  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getParameterNames() {
        return new String[]{"Feature Extractor","Codebook","Distances Histogram"};  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getSetUpParameterNames() {
        return new String[]{"Features Collection","Codebook","Histogram name (will be produced)"};  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getDefaultParameterValues() {
        return new String[]{"","",""};  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getDefaultSetUpParameterValues() {
        return new String[]{"","",""};  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean requiresSetUp() {
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }
}