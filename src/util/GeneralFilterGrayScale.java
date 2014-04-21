package util;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class GeneralFilterGrayScale extends GeneralFilter{
	private int imgType;
	private int[] destRange;
	
	public GeneralFilterGrayScale(int imageType)
	{
		this.imgType = imageType;
		destRange = new int[2];
		if(imgType == PlugInFilter.DOES_8G)
		{
			destRange[MIN] = 0;
			destRange[MAX] = 255;
		}
		else if(imgType == PlugInFilter.DOES_16)
		{
			destRange[MIN] = 0;
			destRange[MAX] = (int)Math.pow(2, 16) - 1;
		}
	}
	
	public int[][] applyFilter2D(ImageProcessor ip, double[][] filter)
	{
		int[][] result = super.applyFilter2D(ip, filter);
		
		//Scale pixel values
		int [] srcRange = this.getMinMax(result);
		result = this.scaleImage(srcRange, destRange, result);
		
		return result;
	}
	
	public int[][] applyFilterXY(ImageProcessor ip, double[] x, double[] y)
	{
		//Apply the filter in rows
		int[][] result = this.applyFilter1D(ip, x, HORIZONTAL);
		//Scale pixel values
		int [] srcRange = this.getMinMax(result);
		result = this.scaleImage(srcRange, destRange, result);
		ip.setIntArray(result);
		
		//Apply the filter in columns
		result = this.applyFilter1D(ip, y, VERTICAL);
		//Scale pixel values
		srcRange = this.getMinMax(result);
		result = this.scaleImage(srcRange, destRange, result);
		
		return result;
	}
}
