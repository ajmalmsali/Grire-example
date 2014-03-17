import net.xeoh.plugins.base.annotations.PluginImplementation;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.FeatureDetector;

import java.io.File;
import java.io.IOException;

@PluginImplementation
public class MSERnFREAK extends SURFParser {
    public MSERnFREAK() {
        checkNativeLibrary() ;
    }

    public MSERnFREAK(Boolean orientationNormalized, Boolean scaleNormalized,Float patternScale, Float nOctaves) {
        detector= FeatureDetector.create(FeatureDetector.MSER);
        descriptor = DescriptorExtractor.create(DescriptorExtractor.FREAK);
        try {
            File outputFile = File.createTempFile("freakDetectorParams", ".YAML");
            writeToFile(outputFile, "%YAML:1.0" +
                    "\npatternScale: "+patternScale.toString()+"f"+
                    "\nnOctaves: "+Integer.toString(nOctaves.intValue())+
                    "\norientationNormalized"+orientationNormalized.toString()+
                    "\nscaleNormalized"+scaleNormalized.toString()+
                    "\n");
            detector.read("freakDetectorParams");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Class[] getParameterTypes() {
        return new Class[]{Boolean.class,Boolean.class,Float.class,Float.class};
    }

    @Override
    public String[] getParameterNames() {
        return new String[]{"Orientation Normalizes","Scale Normalized","Pattern Scale","Num of octaves"};
    }

    @Override
    public String[] getDefaultParameterValues() {
        return new String[]{"True","True","22.0","4",};
    }

    @Override
    public String toString() {
        return "MSERnFREAKParser";
    }
}
