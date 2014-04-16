import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;


public class Low_High_Filter implements PlugInFilter{
	public int setup(String arg, ImagePlus imp)
	{
		return DOES_8G | DOES_RGB | DOES_16 | DOES_32;
	}
	
	public void run(ImageProcessor ip)
	{
		int w = ip.getWidth();
		int h = ip.getHeight();
		double sigma = 3;
		
		ImagePlus hpImg, lpImg;
		ImageProcessor hp, lp;
		
		Filters filter = new Filters(Filters.GAUSSIAN);
		
		lp = filter.getLowPassFilteredImage(ip, sigma);
		hp = filter.getHighPassFilteredImage(ip, sigma);
		
		hpImg = new ImagePlus("High-Pass Filtered Image", hp);
		lpImg = new ImagePlus("Low-Pass Filtered Image", lp);
		
		hpImg.show();
		lpImg.show();
	}
}
