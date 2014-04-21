package hybridimage;

import ij.IJ;
import ij.plugin.filter.Convolver;
import ij.plugin.filter.GaussianBlur;
import ij.plugin.filter.PlugInFilter;
import ij.process.*;
import util.*;

public class Filters {
	public static final int GAUSSIAN = 1;
	public static final int LAPLACIAN = 2;
	
	/**
	 * Accuracy of filter kernels
	 */
	private static final double ACCURACY = 0.01;
	
	/**
	 * Type of the low-pass filter to use in convolution
	 */
	private int lpFilterType;
	
	/**
	 * Type of the high-pass filter to use in convolution
	 */
	private int hpFilterType;
	
	/**
	 * Result image after applying the filter
	 */
	private ImageProcessor resultImage;
	
	/**
	 * Low-pass Filter's kernel (1D)
	 */
	private double[] lpKernel;
	
	/**
	 * High-pass Filter's kernel (2D)
	 */
	private double[][] hpKernel;
	
	/**
	 * High-pass filter's sigma value of the filter
	 */
	private double hpSigma;
	
	/**
	 * Low-pass filter's sigma value of the filter
	 */
	private double lpSigma;
	
	/**
	 * The constructor
	 * @param type	The filter type
	 * @param sigma The sigma value of the filter
	 */
	public Filters(int lpType, int hpType, double lpSigma, double hpSigma){
		this.lpSigma = lpSigma;
		this.hpSigma = hpSigma;
		this.setType(lpType, hpType);
	}
	
	/**
	 * The method to set the filter type
	 * @param type	The new filter type to be set
	 */
	public void setType(int lpType, int hpType){
		lpFilterType = lpType;
		hpFilterType = hpType;
		if(lpFilterType == GAUSSIAN){
			String[] sigmaArg = {lpSigma+""};
			lpKernel = GaussianFilter.constructFilter1D(sigmaArg);
		}
		
		if(hpFilterType == LAPLACIAN){
			String[] sigmaArg = {hpSigma+""};
			hpKernel = LoGFilter.constructFilter2D(sigmaArg);
		}
	}
	
	/**
	 * Apply a low-pass filter to the image and return the result image
	 * @param orig		Original image
	 * @return			The low-pass filtered image
	 */
	public ImageProcessor getLowPassFilteredImage(ImageProcessor orig){
		GeneralFilter filter;
		resultImage = orig.duplicate();
		if(orig instanceof ColorProcessor){
			filter = new GeneralFilterRGB();
		}
		else{
			int type;
			if(orig instanceof ShortProcessor)
				type = PlugInFilter.DOES_16;
			else
				type = PlugInFilter.DOES_8G;
			filter = new GeneralFilterGrayScale(type);
		}
		
		int[][] result = filter.applyFilterXY(resultImage, lpKernel, lpKernel);
		resultImage.setIntArray(result);
		
		return resultImage;
	}
//	public ImageProcessor getLowPassFilteredImage(ImageProcessor orig, double sigma){
//		final int maxRadius = 100;
//		resultImage = orig.duplicate();
//		if(filterType == GAUSSIAN){
//			GaussianBlur gb = new GaussianBlur();
////			float[][] kernel = gb.makeGaussianKernel(sigma, ACCURACY, maxRadius);
////			for(int i=0; i<kernel[0].length; i++)
////				IJ.write(kernel[0][i]+",");
////			Convolver c = new Convolver();
////			resultImage.convolve(kernel[0], kernel[0].length, kernel[0].length);
//			gb.blurGaussian(resultImage, sigma, sigma, ACCURACY);
//		}
//		return resultImage;
//	}
	
	/**
	 * Apply a high-pass filter to the image and return the result image
	 * @param orig		Original image
	 * @return			The high-pass filtered image
	 */
//	public ImageProcessor getHighPassFilteredImage(ImageProcessor orig){
//		GeneralFilter filter;
//		resultImage = orig.duplicate();
//		if(orig instanceof ColorProcessor){
//			filter = new GeneralFilterRGB();
//		}
//		else{
//			int type;
//			if(orig instanceof ShortProcessor)
//				type = PlugInFilter.DOES_16;
//			else
//				type = PlugInFilter.DOES_8G;
//			filter = new GeneralFilterGrayScale(type);
//		}
//		
//		//double[][] kernel2D = GaussianFilter.constructFilter2D(new String[]{sigma+""});
//		//double[][] highPassKernel = GeneralFilter.inverse2DNormalizedKernel(kernel2D);
//		double[] highPassKernel = GeneralFilter.inverse1DNormalizedKernel(kernel);
//		int[][] result = filter.applyFilterXY(resultImage, highPassKernel, highPassKernel);
//		//int[][] result = filter.applyFilter2D(resultImage, highPassKernel);
//		resultImage.setIntArray(result);
//		
//		return resultImage;
//	}
//	public ImageProcessor getHighPassFilteredImage(ImageProcessor orig){
//		ImageProcessor temp;
//		
//		temp = this.getLowPassFilteredImage(orig);
//		resultImage = Util.subtractImages(orig, temp);
//		
//		return resultImage;
//	}
	public ImageProcessor getHighPassFilteredImage(ImageProcessor orig){
		GeneralFilter filter;
		if(orig instanceof ColorProcessor){
			filter = new GeneralFilterRGB();
		}
		else{
			int type;
			if(orig instanceof ShortProcessor)
				type = PlugInFilter.DOES_16;
			else
				type = PlugInFilter.DOES_8G;
			filter = new GeneralFilterGrayScale(type);
		}
		resultImage = orig.duplicate();
		hpKernel = LoGFilter.constructFilter2D(new String[]{hpSigma+""});
		int[][] result = filter.applyFilter2D(resultImage, hpKernel);
		resultImage.setIntArray(result);
		resultImage = Util.inverseImage(resultImage);
		
		return resultImage;
	}
}
