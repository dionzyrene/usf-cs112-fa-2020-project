import java.util.ArrayList;

public abstract class Model{
	private double maxRedX=0;
	private double maxRedY=0;

	
	
		abstract void train (ArrayList<DataPoint>data) {
			double maxRedX =0;
			double maxRedX=0;
		
		}
		abstract String test (ArrayList<DataPoint> data) {
			DataPoint testData = data [0];
			//dummy model
			
			if (Math.abs(testDate.getX()- this.maxRedX)> Math.abs(testDate.getX()- this.maxBlueX)) {
				return "blue";
			
		}
			else {
				return "red";
		}
			
		}
		abstract Double getAccuracy(ArrayList<DataPoint> data) {
		
		}
		abstract Double getPrecision(ArrayList<DataPoint> data) {
			
	
}
}

