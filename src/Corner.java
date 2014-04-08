

//package imagingbook.pub.corners;
//
//import imagingbook.pub.corners.Corner;

/**
 * 2013/06/09 Changed to 'final float' coordinates (WB).
 */
public class Corner implements Comparable<Corner> {
	
	protected final float u;
	protected final float v;
	protected final float q;

	public float getU() {
		return u;
	}

	public float getV() {
		return v;
	}

	public float getQ() {
		return q;
	}
	
	public Corner (float u, float v, float q) {
		this.u = u;
		this.v = v;
		this.q = q;
	}
    
	public int compareTo (Corner c2) {
		//used for sorting corners by corner strength q
		if (this.q > c2.q) return -1;
		if (this.q < c2.q) return 1;
		else return 0;
	}
	
	double dist2 (Corner c2){
		//returns the squared distance between this corner and corner c2
		float dx = this.u - c2.u;
		float dy = this.v - c2.v;
		return (dx * dx) + (dy * dy);	
	}
}
