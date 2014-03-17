import net.xeoh.plugins.base.annotations.PluginImplementation;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.FeatureDetector;

import java.io.File;
import java.io.IOException;


@PluginImplementation
public class BRISKParser extends SURFParser{

    public BRISKParser() {
        checkNativeLibrary();
    }

    public BRISKParser(Float thresh, Float octaves, Float patternScale){
        detector= FeatureDetector.create(FeatureDetector.BRISK);
        descriptor = DescriptorExtractor.create(DescriptorExtractor.BRISK);
        try {
            File outputFile = File.createTempFile("briskDetectorParams", ".YAML");
            writeToFile(outputFile, "%YAML:1.0" +
                    "\npatternScale: "+patternScale.toString()+"f"+
                    "\nthreshold: "+Integer.toString(thresh.intValue())+
                    "\noctaves: "+Integer.toString(octaves.intValue())+
                    "\n");
            detector.read("briskDetectorParams");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Class[] getParameterTypes() {
        return new Class[]{Float.class,Float.class,Float.class};
    }

    @Override
    public String[] getParameterNames() {
        return new String[] {"(thresh) FAST/AGAST detection threshold score","(octaves) Detection octaves. Use 0 to do single scale","(patternScale) apply this scale to the pattern used for sampling the neighbourhood of a keypoint."};
    }

    @Override
    public String[] getDefaultParameterValues() {
        return new String[]{"30","3","1"};
    }

    @Override
    public String toString() {
        return "OpenCvBRISKParser";
    }
}
