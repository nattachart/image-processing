import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import ij.process.LUT;


public class GlowingIron_IndexedImage implements PlugInFilter {
	private ImagePlus imp;
	private static final int SIZE = 256;
	private static final int STEP = 2;
	@Override
	public void run(ImageProcessor ip) {
		byte[] r = new byte[SIZE];
		byte[] g = new byte[SIZE];
		byte[] b = new byte[SIZE];
		
		int i;
		byte value = 0;
		for(i=0; i<=84; i++){
			r[i] = value;
			g[i] = 0;
			b[i] = 0;
			value += STEP;
		}
		value = 0;
		for(i=85; i<=169; i++){
			r[i] = (byte)255;
			g[i] = value;
			b[i] = 0;
			value += STEP;
		}
		value = 0;
		for(i=170; i<=254; i++){
			r[i] = (byte)255;
			g[i] = (byte)255;
			b[i] = value;
			value += STEP;
		}
		r[i] = (byte)255;
		g[i] = (byte)255;
		b[i] = (byte)255;
		
		LUT lut = new LUT(r, g, b);
		
		ip.setLut(lut);
		
		imp.updateAndDraw();
	}

	@Override
	public int setup(String arg0, ImagePlus arg1) {
		imp = arg1;
		return DOES_8G;
	}

}
