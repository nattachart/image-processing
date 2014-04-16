import ij.plugin.filter.GaussianBlur;
import ij.process.ImageProcessor;

public class Filters {
	/**
	 * Gaussian type
	 */
	public static final int GAUSSIAN = 1;
	
	/**
	 * Accuracy of filter kernels
	 */
	private static final double ACCURACY = 0.01;
	
	/**
	 * Type of the filter to use in convolution
	 */
	private int filterType;
	
	/**
	 * Result image after applying the filter
	 */
	private ImageProcessor resultImage;
	
	/**
	 * The constructor
	 * @param type	The filter type
	 */
	public Filters(int type){
		filterType = type;
	}
	
	/**
	 * The method to set the filter type
	 * @param type	The new filter type to be set
	 */
	public void setType(int type){
		filterType = type;
	}
	
	/**
	 * Apply a low-pass filter to the image and return the result image
	 * @param orig		Original image
	 * @param sigma		The standard deviation of the filter
	 * @return			The low-pass filtered image
	 */
	public ImageProcessor getLowPassFilteredImage(ImageProcessor orig, double sigma){
		resultImage = orig.duplicate();
		if(filterType == GAUSSIAN){
			GaussianBlur gb = new GaussianBlur();
			gb.blurGaussian(resultImage, sigma, sigma, ACCURACY);
		}
		return resultImage;
	}
	
	/**
	 * Apply a high-pass filter to the image and return the result image
	 * @param orig		Original image
	 * @param sigma		The standard deviation of the filter
	 * @return			The high-pass filtered image
	 */
	public ImageProcessor getHighPassFilteredImage(ImageProcessor orig, double sigma){
		boolean canSubtract = true;
		ImageProcessor temp;
		if(filterType == GAUSSIAN){
			temp = this.getLowPassFilteredImage(orig, sigma);
			resultImage = Util.subtractImages(orig, temp);
		}
		return resultImage;
	}
}
