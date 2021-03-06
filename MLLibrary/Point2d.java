public class Point2d {
	protected double x;
	protected double y;
	
	public Point2d() {
		this.x=0.0;
		this.y=0.0;
	}
	public Point2d(double x, double y) {
		this.x=x;
		this.y=y;
	}
	public double getX() {
		return this.x;
	}
	public double getY() {
		return this.y;
	}
	public void setX (double x) {
		this.x=x;
	}
	public void setY (double y) {
		this.y=y;
	}
	public String toString() {
		return this.x + "." + this.y;
	}
}

