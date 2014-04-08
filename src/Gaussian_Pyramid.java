import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.Convolver;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import ij.process.ByteProcessor;
import imagingbook.lib.filters.GaussianFilter;

public class Gaussian_Pyramid implements PlugInFilter{
	private double sigma;
	
	public int setup(String arg, ImagePlus imp)
	{
		return DOES_8G;
	}
	
	public void run(ImageProcessor ip)
	{
		ImageProcessor result = ip;
		sigma = this.getSigma("Sigma:");
		for(int i=0; i<3; i++)
		{
			sigma = sigma * Math.pow(sigma, i);
			result = this.unsharpMask(result, sigma);
			result = this.reduceResolutionBy2(result);
			this.showProcessor(result, "step "+(i+1));
		}
	}
	
	public ImageProcessor unsharpMask(ImageProcessor ip, double sigma)
	{
		float[] kernel = GaussianFilter.makeGaussKernel1d(sigma);
		ImageProcessor blured = ip.duplicate();
		Convolver cv = new Convolver();
		cv.setNormalize(true);
		cv.convolve(blured, kernel, 1, kernel.length);
		cv.convolve(blured, kernel, kernel.length, 1);
		
		//ip.insert(blured, 0, 0);
		
		//showProcessor(blured, "Gaussian Blured");
		
		return blured;
	}
	
	public ImageProcessor reduceResolutionBy2(ImageProcessor ip)
	{
		int w = ip.getWidth();
		int h = ip.getHeight();
		
		ByteProcessor reduced = new ByteProcessor(w/2, h/2);
		
		for(int i=0, k=0; i<w; i+=2, k++)
		{
			for(int j=0, l=0; j<h; j+=2, l++)
			{
				reduced.putPixel(k, l, ip.getPixel(i, j));
			}
		}
		
		return reduced;
	}
	
	private double getSigma(String arg)
	{
		GenericDialog gd = new GenericDialog("Gaussian Radius");
		gd.addNumericField("Sigma " + arg + ": ", 1, 1);
		gd.showDialog();
		if(gd.wasCanceled())
			return 0;
		return gd.getNextNumber(); //Get sigma from the user
	}
	
	public void showProcessor(ImageProcessor ip, String title)
	{
        ImagePlus win = new ImagePlus(title,ip);
        win.show();
	}
}
