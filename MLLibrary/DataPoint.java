import java.util.ArrayList;

public class DataPoint {
	private double f1;
	private double f2;
	private String label;
	private String type;
	
	//appropriate constructor
	public DataPoint (double f1, double f2, String label, String type) {
		this.f1 = f1;
		this.f2 = f2;
		this.label = label;
		this.type = type;
	}
	
	//no arg. constructor
	public DataPoint() {
		this(0,0,"","");
	}
	
	public double getX() {
		return this.f1;
	}
	
	public double getY() {
		return this.f2;
	}
	
	public String getLabel() {
		return this.label;
	}
	
	public String getType() {
		return this.type;
	}
	
	public void setX(double val) {
		f1=val;
	}
	
	public void setY(double val) {
		f2=val;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public void setType(String type) {
		this.type = type;
	}

}

