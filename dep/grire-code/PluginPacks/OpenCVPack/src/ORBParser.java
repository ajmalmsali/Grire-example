import net.xeoh.plugins.base.annotations.PluginImplementation;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.FeatureDetector;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: Lazaros
 * Date: 20/5/2013
 * Time: 6:22 μμ
 * To change this template use File | Settings | File Templates.
 */
@PluginImplementation
public class ORBParser extends SURFParser {

    public ORBParser() {
        checkNativeLibrary() ;
    }

    public ORBParser(Float nfeatures, Float scaleFactor, Float nLevels, Float edgeThreshold, Float firstLevel, Float WTA_K, Float scoreType, Float patchSize) {
        detector= FeatureDetector.create(FeatureDetector.ORB);
        descriptor = DescriptorExtractor.create(DescriptorExtractor.ORB);
        try {
            File outputFile = File.createTempFile("orbDetectorParams", ".YAML");
            writeToFile(outputFile, "%YAML:1.0" +
                    "\nscaleFactor: "+scaleFactor.toString()+"f"+
                    "\nnLevels: "+Integer.toString(nLevels.intValue())+
                    "\nfirstLevel: "+Integer.toString(firstLevel.intValue())+
                    "\nedgeThreshold: "+Integer.toString(edgeThreshold.intValue())+
                    "\npatchSize: "+Integer.toString(patchSize.intValue())+
                    "\nWTA_K: "+Integer.toString(WTA_K.intValue())+
                    "\nscoreType: "+Integer.toString(scoreType.intValue())+
                    "\nnFeatures: "+Integer.toString(nfeatures.intValue())+
                    "\n");
            detector.read("orbDetectorParams");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Class[] getParameterTypes() {
        Class[] ret = new Class[8];
        Arrays.fill(ret,Float.class);
        return ret;
    }

    @Override
    public String[] getParameterNames() {
        return new String[]{
                "scaleFactor",
                "nLevels",
                "firstLevel",
                "edgeThreshold",
                "patchSize",
                "WTA_K",
                "scoreType (0: HARRIS _SCORE, 1: FAST_SCORE)",
                "nFeatures"
        };
    }

    @Override
    public String[] getDefaultParameterValues() {
        return new String[]{
                "1.2",
                "8",
                "0",
                "31",
                "31",
                "2",
                "0",
                "500"
        };    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public String toString() {
        return "OpenCvORBParser";
    }
}
