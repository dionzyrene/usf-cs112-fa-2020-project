import java.util.ArrayList;

public abstract class Model{
	private double maxf1=0;
	private double maxf2=0;


	
	
		abstract void train (DataPoint[] sampleData) {
			double maxRedf1 =0;
			double maxRedf2=0;
			int numf1 =0;
			int numf2=0;
		
			for (int i=0; i<trainData.length; i++) {
				if (trainData[i].getLabel()== "red") {
					maxf1 += trainData[i].getf1();
					numf1+= 1;
				}
				
				if (trainData[i].getLabel()== "blue") {
					maxf2 += trainData[i].getf1();
					numf2 +=1;
						
				}
			}
		}
		abstract String test (ArrayList<DataPoint> data) {
			DataPoint testData = data [0];
			//dummy model
			
			if (Math.abs(testDate.getX()- this.maxf1)> Math.abs(testDate.getX()- this.maxf2)) {
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
