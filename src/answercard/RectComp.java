package answercard;

import org.opencv.core.Rect;

public class RectComp {
	
	public Rect rm ;

	public RectComp(Rect rms) {
		super();
		this.rm = rms;
	}
	
	
	
	
	public Rect getRm() {
		return rm;
	}




	public void setRm(Rect rm) {
		this.rm = rm;
	}




	public boolean operator(RectComp ti) {
		 return rm.x < ti.rm.x;  
	}

}
