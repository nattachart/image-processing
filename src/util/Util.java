package util;
import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ByteProcessor;
import ij.process.ShortProcessor;


public class Util {
	public static final int MAX = 0;
	public static final int MIN = 1;
	public static final int QUARTILE_1 = 0;
	public static final int QUARTILE_2 = 1;
	public static final int QUARTILE_3 = 2;
	
	public static int[] getMinMax(int[][] a)
	{
		int[] minMax = new int[2];
		
		for(int i=0; i<a.length; i++)
		{
			for(int j=0; j<a[i].length; j++)
			{
				if(a[i][j] > minMax[MAX])
					minMax[MAX] = a[i][j];
				
				if(a[i][j] < minMax[MIN])
					minMax[MIN] = a[i][j];
			}
		}
		return minMax;
	}
	
	public static double[] getMinMax(double[] a)
	{
		double[] minMax = new double[2];
		
		for(int i=0; i<a.length; i++)
		{
				if(a[i] > minMax[MAX])
					minMax[MAX] = a[i];
				
				if(a[i] < minMax[MIN])
					minMax[MIN] = a[i];
		}
		return minMax;
	}
	
	public static int scale(int[] srcRange, int[] destRange, int value)
	{
		int scaled;
		
		scaled = ((destRange[MAX]-destRange[MIN])*(value-srcRange[MIN])/(srcRange[MAX]-srcRange[MIN])) + destRange[MIN];
		
		return scaled;
	}
	
	public static double scale(double[] srcRange, double[] destRange, double value)
	{
		double scaled;
		
		scaled = ((destRange[MAX]-destRange[MIN])*(value-srcRange[MIN])/(srcRange[MAX]-srcRange[MIN])) + destRange[MIN];
		
		return scaled;
	}
	
	public static double[] getQuartiles(double[] valueRange)
	{
		double[] q = new double[3];
		double[] srcRange = new double[2];
		srcRange[MIN] = 0;
		srcRange[MAX] = 4;
		q[QUARTILE_1] = scale(srcRange, valueRange, 1);
		q[QUARTILE_2] = scale(srcRange, valueRange, 2);
		q[QUARTILE_3] = scale(srcRange, valueRange, 3);
		
		return q;
	}
	
	public static void showProcessor(ImageProcessor ip, String title)
	{
        ImagePlus win = new ImagePlus(title,ip);
        win.show();
	}
	
	public static ImageProcessor subtractImages(ImageProcessor img1, ImageProcessor img2){
		ImageProcessor result = null;
//		if(img1.getWidth() != img2.getWidth() || img1.getHeight() != img2.getHeight())
//			return false;
		
		if(img1.getNChannels() == 3){
			result = Util.subtractRGBImages((ColorProcessor)img1, (ColorProcessor)img2);
		}
		//else if(img1.isGrayscale()){
		else{
			result = Util.subtractGrayscaleImages(img1, img2);
		}
//		else{
//			return false;	//Neither grayscale nor RGB
//		}
//		return true;
		
		int min = (int)result.getMin();
		int max = (int)result.getMax();
		int[] srcRange = new int[2];
		srcRange[MIN] = min;
		srcRange[MAX] = max;
		
		int[] destRange = new int[2];
		destRange[MIN] = 0;
		destRange[MAX] = 255;
		
		for(int i=0; i<result.getWidth(); i++){
			for(int j=0; j<result.getHeight(); j++){
				result.putPixel(i, j, Util.scale(srcRange, destRange, result.getPixel(i, j)));
			}
		}
		
		return result;
	}
	
	/**
	 * This method subtracts RGB images img1 by img2. It assumes both images have the same size. It does not support grayscale and indexed images.
	 * @param img1		An image to be subtracted by img2
	 * @param img2		An image to subtract img1
	 * @return			The result subtracted image
	 */
	private static ColorProcessor subtractRGBImages(ColorProcessor img1, ColorProcessor img2){
		int w = img1.getWidth();
		int h = img1.getHeight();
		ColorProcessor result = new ColorProcessor(w, h);
		
		ByteProcessor red1=null, red2=null, green1=null, green2=null, blue1=null
				, blue2=null, alpha1=null, alpha2=null;
		red1 = img1.getChannel(1, red1);
		red2 = img1.getChannel(1, red2);
		green1 = img1.getChannel(2, green1);
		green2 = img1.getChannel(2, green2);
		blue1 = img1.getChannel(3, blue1);
		blue2 = img1.getChannel(3, blue2);
		alpha1 = img1.getChannel(4, alpha1);
		alpha2 = img1.getChannel(4, alpha2);
		
		ImageProcessor temp;
		
		temp = Util.subtractGrayscaleImages(red1, red2);
		result.setChannel(1, (ByteProcessor)temp);
		
		temp = Util.subtractGrayscaleImages(green1, green2);
		result.setChannel(2, (ByteProcessor)temp);
		
		temp = Util.subtractGrayscaleImages(blue1, blue2);
		result.setChannel(3, (ByteProcessor)temp);
		
//		temp = Util.subtractGrayscaleImages(alpha1, alpha2);
//		result.setChannel(4, (ByteProcessor)temp);
		
		result.setChannel(4, alpha1);
		
		return result;
	}
	
	/**
	 * This method subtracts grayscale images img1 by img2. It assumes both images have the same size. It does not support RGB and indexed images.
	 * @param img1		An image to be subtracted by img2
	 * @param img2		An image to subtract img1
	 * @return			The result subtracted image
	 */
	private static ImageProcessor subtractGrayscaleImages(ImageProcessor img1, ImageProcessor img2){
		ImageProcessor result;
		ImageProcessor rf=null, i1f=null, i2f=null; //For FloatProcessor
		int w = img1.getWidth();
		int h = img1.getHeight();
		boolean floatImg = false;
		
		// Check the type of the image
		if(img1 instanceof ByteProcessor){
			result = new ByteProcessor(w, h);
		}
		else if(img1 instanceof ShortProcessor){
			result = new ShortProcessor(w, h);
		}
		else{
			result = new FloatProcessor(w, h);
			floatImg = true;
			rf = (FloatProcessor)result;
			i1f = (FloatProcessor)img1;
			i2f = (FloatProcessor)img2;
		}
		
		for(int i=0; i < w; i++){
			for(int j=0; j < h; j++){
				if(floatImg){
					rf.setf(i, j, i1f.getf(i, j) - i2f.getf(i, j));
				}
				else{
					result.putPixel(i, j, img1.getPixel(i, j) - img2.getPixel(i, j));
					//result.putPixel(i, j, img1.getPixel(i, j));
				}
			}
		}
		
		return result;
	}
	
	public static ImageProcessor blendImages(ImageProcessor ip1, ImageProcessor ip2, double ip1Weight, double ip2Weight){
		//Assume that ip1 and ip2 have the same size.
		ImageProcessor result = new ByteProcessor(ip1.getWidth(), ip1.getHeight());
		double temp;
		
		for(int i=0; i<result.getWidth(); i++){
			for(int j=0; j<result.getHeight(); j++){
				temp = ip1Weight*ip1.getPixel(i, j) + ip2Weight*ip2.getPixel(i, j);
				result.putPixel(i, j, (int)Math.round(temp));
			}
		}
		
		return result;
	}
	
	public static ImageProcessor inverseImage(ImageProcessor ip){
		ImageProcessor result = ip.duplicate();
		for(int i=0; i<ip.getWidth(); i++){
			for(int j=0; j<ip.getHeight(); j++){
				result.putPixel(i, j, 255-ip.getPixel(i, j));
			}
		}
		return result;
	}
}
