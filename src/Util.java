import ij.ImagePlus;
import ij.process.ImageProcessor;


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
}
