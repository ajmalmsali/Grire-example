import net.xeoh.plugins.base.annotations.PluginImplementation;
import org.grire.GeneralUtilities.Interfaces.FeatureExtractor;
import org.grire.Helpers.Listeners.Listened;

import java.awt.image.BufferedImage;
import java.io.File;

@PluginImplementation
public class FeatureExtractorDebug extends FeatureExtractor {

    int featNum,featSize;

    public FeatureExtractorDebug(Float featuresNumber, Float featureSize) {
        featNum=featuresNumber.intValue();
        featSize=featureSize.intValue();
    }

    public FeatureExtractorDebug() {
    }

    @Override
    public float[][] extract(File img) {
        float[][] temp=new float[featNum][];
        for (int i=0;i<featNum;i++)
            temp[i]=new float[featSize];
        return temp;
    }

    @Override
    public float[][] extract(BufferedImage img) {
        float[][] temp=new float[featNum][];
        for (int i=0;i<featNum;i++)
            temp[i]=new float[featSize];
        return temp;
    }

    @Override
    public float[][] getPositions() {
        float[][] temp=new float[featNum][];
        for (int i=0;i<featNum;i++)
            temp[i]=new float[2];
        return temp;
    }

    @Override
    public Listened setUp(Object... args) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Class[] getParameterTypes() {
        return new Class[]{Float.class,Float.class};  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Class[] getSetUpParameterTypes() {
        return new Class[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getParameterNames() {
        return new String[]{"Number of features","Size of features"};  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getSetUpParameterNames() {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getDefaultParameterValues() {
        return new String[]{"500","64"};  //To change body of implemented methods use File | Settings | File Templates.
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
