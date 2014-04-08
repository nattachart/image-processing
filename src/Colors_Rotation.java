import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;


public class Colors_Rotation implements PlugInFilter{
	private ImagePlus imp;
	
	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return DOES_RGB;
	}

	public void run(ImageProcessor ip) {
		int img[][] = ip.getIntArray();
		int red, green, blue, temp;
		
		for(int i=0; i<img.length; i++)
		{
			for(int j=0; j<img[i].length; j++)
			{
				red = (img[i][j] & 0x00ff0000) >> 16;
				green = (img[i][j] & 0x0000ff00) >> 8;
				blue = (img[i][j] & 0xff);
			
				temp = blue;
				blue = green;
				green = red;
				red = temp;
				temp = img[i][j] & 0xff000000;
				
				img[i][j] = temp;
				img[i][j] |= (red << 16) & 0x00ff0000;
				img[i][j] |= (green << 8) & 0x0000ff00;
				img[i][j] |= blue & 0xff;
			}
		}
		
		ImageProcessor result = new ColorProcessor(ip.getWidth(), ip.getHeight());
		result.setIntArray(img);
		
		ImagePlus resultWindow = new ImagePlus("R -> G -> B -> R", result);
		resultWindow.show();
	}
}
