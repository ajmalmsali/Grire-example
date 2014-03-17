import net.xeoh.plugins.base.annotations.PluginImplementation;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.FeatureDetector;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Lazaros
 * Date: 20/5/2013
 * Time: 6:21 μμ
 * To change this template use File | Settings | File Templates.
 */
@PluginImplementation
public class SIFTParser extends SURFParser {
    public SIFTParser() {
        checkNativeLibrary() ;
    }

    public SIFTParser(Float nfeatures,Float nOctaveLayers, Float contrastThreshold, Float edgeThreshold,Float sigma) {
        detector= FeatureDetector.create(FeatureDetector.SIFT);
        descriptor = DescriptorExtractor.create(DescriptorExtractor.SIFT);
        try {
            File outputFile = File.createTempFile("siftDetectorParams", ".YAML");
            writeToFile(outputFile, "%YAML:1.0" +
                    "\nsigma: "+sigma.toString()+"f"+
                    "\ncontrastThreshold: "+contrastThreshold.toString()+"f"+
                    "\nnOctaveLayers: "+Integer.toString(nOctaveLayers.intValue())+
                    "\nedgeThreshold: "+edgeThreshold.toString()+"f"+
                    "\nnfeatures: "+Integer.toString(nfeatures.intValue())+
                    "\n");
            detector.read("siftDetectorParams");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "OpenCvSIFTParser";
    }

    @Override
    public Class[] getParameterTypes() {
        return new Class[]{Float.class,Float.class,Float.class,Float.class,Float.class};
    }

    @Override
    public String[] getParameterNames() {
        return new String[]{"Number of features","Number of Octaves","Contrast Threshold","Edge Threshold","Sigma"};
    }

    @Override
    public String[] getDefaultParameterValues() {
        return new String[]{"0","3","0.04","10","1.6"};
    }
}
