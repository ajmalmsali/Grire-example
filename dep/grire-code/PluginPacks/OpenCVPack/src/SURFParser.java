import net.xeoh.plugins.base.annotations.PluginImplementation;
import org.grire.GeneralUtilities.Interfaces.FeatureExtractor;
import org.grire.Helpers.Listeners.Listened;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.KeyPoint;
import org.opencv.highgui.Highgui;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created with IntelliJ IDEA.
 * User: Lazaros
 * Date: 20/5/2013
 * Time: 5:34 μμ
 * To change this template use File | Settings | File Templates.
 */
@PluginImplementation
public class SURFParser extends FeatureExtractor {

    FeatureDetector detector;
    DescriptorExtractor descriptor;
    float[][] positions;

    //static { System.load(new File("./opencv_java245.dll").getAbsolutePath());}

    public SURFParser() {
        checkNativeLibrary();
    }

    public SURFParser(Float hessianThreshold,Float nOctaves,Float nOctaveLayers, Boolean extended, Boolean upright) {
        detector=FeatureDetector.create(FeatureDetector.SURF);
        descriptor =DescriptorExtractor.create(DescriptorExtractor.SURF);
        try {
            File outputFile = File.createTempFile("surfDetectorParams", ".YAML");
            writeToFile(outputFile, "%YAML:1.0" +
                    "\nnOctaveLayers: "+nOctaveLayers.toString()+"f"+
                    "\nhessianThreshold: "+hessianThreshold.toString()+"f"+
                    "\nnOctaveLayers: "+Integer.toString(nOctaveLayers.intValue())+
                    "\nextended: "+(extended?"true":"false")+
                    "\nupright: "+upright.toString()+
                    "\nnOctaves: "+Integer.toString(nOctaves.intValue())+
                    "\n");
            detector.read("surfDetectorParams");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @Override
    public float[][] extract(File img) {
        Mat im = Highgui.imread(img.getAbsolutePath());
        return Parse(im);
    }

    @Override
    public float[][] extract(BufferedImage img) {
        byte[] pixels = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
        Mat im=new Mat(img.getHeight(),img.getWidth(), CvType.CV_8UC3);
        im.put(0,0,pixels);
        return Parse(im);
    }

    @Override
    public float[][] getPositions() {
        return positions;
    }

    public float[][] Parse(Mat image) {
        MatOfKeyPoint keypoints = new MatOfKeyPoint();
        detector.detect(image, keypoints);
        positions=new float[keypoints.height()][2];
        KeyPoint[] keyPointsArr = keypoints.toArray();
        for (int i=0;i<positions.length;i++) {
            positions[i][0]=(float)keyPointsArr[i].pt.x;
            positions[i][1]=(float)keyPointsArr[i].pt.y;
        }
        Mat descriptors = new Mat();
        descriptor.compute(image,keypoints, descriptors);
        float[][] ret=new float[descriptors.height()][];
        int width = descriptors.width();
        for (int i=0;i<ret.length;i++) {
            ret[i]=new float[width];
            for (int j=0;j<width;j++)
                ret[i][j]=(float)descriptors.get(i,j)[0];
        }
        return ret;
    }

    protected void checkNativeLibrary() {
        String[] loadedLibraries = ClassScope.getLoadedLibraries(this.getClass().getClassLoader());
        for (String s:loadedLibraries) {
            int i = s.lastIndexOf('\\');
            String ss = s.substring(i+1);
            String lookFor, ext;
            if(System.getProperty("os.name").startsWith("Windows")){
                lookFor= "opencv_java246";
                ext=".dll";
            } else {
                lookFor= "libopencv_java246";
                ext=".so";
            }
            lookFor+=(System.getProperty("os.arch").equals("x86_64") || System.getProperty("os.arch").equals("amd64") ? "_x64" : "_x86");
            lookFor+=ext;
            if (!ss.contains(lookFor))
                System.load(new File(lookFor).getAbsolutePath());
        }
    }

    protected void writeToFile(File file, String data) {
        try {
            FileOutputStream stream = new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(stream);
            outputStreamWriter.write(data);
            outputStreamWriter.close();
            stream.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public Listened setUp(Object... args) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Class[] getParameterTypes() {
        return new Class[]{Float.class,Float.class,Float.class,Boolean.class,Boolean.class};  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Class[] getSetUpParameterTypes() {
        return new Class[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getParameterNames() {
        return new String[]{"Hessian Threshold","Num of Octaves", "Num of Octaves layers","Extended?","Upright?"};  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getSetUpParameterNames() {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getDefaultParameterValues() {
        return new String[]{"300","4","2","True","False"};  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getDefaultSetUpParameterValues() {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean requiresSetUp() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String toString() {
        return "OpenCvSURFParser";
    }
}
