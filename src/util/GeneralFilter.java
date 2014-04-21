package util;
import ij.IJ;
import ij.process.ImageProcessor;


public abstract class GeneralFilter {
	public static final int MAX = 0;
	public static final int MIN = 1;
	public static final int HORIZONTAL = 0;
	public static final int VERTICAL = 1;
	
	public static double[] inverse1DNormalizedKernel(double[] kernel){
		double[] inversed = new double[kernel.length];
		for(int i=0; i<kernel.length; i++){
			inversed[i] = 1 - kernel[i];
		}
		return inversed;
	}
	
	public static double[][] inverse2DNormalizedKernel(double[][] kernel){
		double[][] inversed = new double[kernel.length][kernel[0].length];
		for(int i=0; i<kernel.length; i++){
			for(int j=0; j<kernel[0].length; j++){
				inversed[i][j] = 1 - kernel[i][j];
				//IJ.write(inversed[i][j]+"");
			}
		}
		return inversed;
	}
	
	protected int[] getMinMax(int[][] a)
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
	
	protected int[][] scaleImage(int[] srcRange, int[] destRange, int[][] img)
	{
		int[][] scaled = new int[img.length][img[0].length];
		for(int i=0; i<img.length; i++)
		{
			for(int j=0; j<img[0].length; j++)
			{
				scaled[i][j] = this.scale(srcRange, destRange, img[i][j]);
			}
		}
		return scaled;
	}
	
	protected int scale(int[] srcRange, int[] destRange, int value)
	{
		int scaled;
		
		scaled = ((destRange[MAX]-destRange[MIN])*(value-srcRange[MIN])/(srcRange[MAX]-srcRange[MIN])) + destRange[MIN];
		
		return scaled;
	}
	
	public int[][] applyFilter2D(ImageProcessor ip, double[][] filter)
	{
		int imgH = ip.getHeight();
		int imgW = ip.getWidth();
		int centerX = filter.length / 2;
		int centerY = filter[0].length / 2;
		double temp;
		int result[][] = new int[imgW][imgH];
		
		//Apply kernel
		for(int i=0; i<imgH; i++)
		{
			for(int j=0; j<imgW; j++)
			{
				temp = 0;
				for(int l=centerY; l<filter[0].length; l++)
				{
					for(int k=centerX; k<filter.length; k++)
					{
						//1st quadrant of the filter; 3rd quadrant of the filter-covered area of the original image
						temp += ip.getPixel(j-(k-centerX), i-(l-centerY)) * filter[k][l];
//						IJ.write("pixel "+(j-(k-center)+"x"+(i-(l-center))+": "+ ip.getPixel(j-(k-center), i-(l-center))+"\n"));
//						IJ.write("imgH: "+imgH+", imgW: "+imgW+"\n");
						
						//3rd quadrant of the filter; 1st quadrant of the filter-covered area of the original image
						if(k != centerX || l != centerY)
							temp += Float.intBitsToFloat(ip.getPixel(j+(k-centerX), i+(l-centerY))) * filter[centerX-(k-centerX)][centerY-(l-centerY)];
						//IJ.write("pixel "+(j+(k-center)+"x"+(i+(l-center))+": "+ ip.getPixel(j+(k-center), i+(l-center))+"\n"));
						
						
						//2nd quadrant of the filter; 4th quadrant of the filter-covered area of the original image
						if(k != centerX && l != centerY)
							temp += Float.intBitsToFloat(ip.getPixel(j-(k-centerX), i+(l-centerY))) * filter[k][centerY-(l-centerY)];
						
						//4th quadrant of the filter; 2nd quadrant of the filter-covered area of the original image
						if(k != centerX && l != centerY)
						{
							temp += Float.intBitsToFloat(ip.getPixel(j+(k-centerX), i-(l-centerY))) * filter[centerY-(k-centerY)][l];
						}
					}
				}
				result[j][i] = (int)Math.round(temp);
			}
		}
		
		return result;
	}
	
	public int[][] applyFilter1D(ImageProcessor ip, double[] filter, int direction)
	{
		int imgH = ip.getHeight();
		int imgW = ip.getWidth();
		int center = filter.length / 2;
		double temp;
		int result[][] = new int[imgW][imgH];
		
		//Apply kernel in X direction
		for(int i=0; i<imgH; i++)
		{
			for(int j=0; j<imgW; j++)
			{
				temp = 0;
				for(int k=center; k<filter.length; k++)
				{
					if(direction == HORIZONTAL)
					{
						//Right side of kernel's center
						temp += ip.getPixel(j-(k-center), i) * filter[k];
						
						//Left side of kernel's center
						if(k != center)
							temp += ip.getPixel(j+(k-center), i) * filter[center-(k-center)];
					}
					else
					{
						//Right side of kernel's center
						temp += ip.getPixel(j, i-(k-center)) * filter[k];
						
						//Left side of kernel's center
						if(k != center)
							temp += ip.getPixel(j, i+(k-center)) * filter[center-(k-center)];
					}
				}
				result[j][i] = (int)Math.round(temp);
			}
		}
		
		return result;
	}
	
	public abstract int[][] applyFilterXY(ImageProcessor ip, double[] x, double[] y);
}
