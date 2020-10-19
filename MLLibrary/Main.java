public class Main {
	public static void main(String[]args) {
		DataPoint[] sampleData = new DataPoint[2];
		sampleData[0]= new DataPoint (1,1, "red");
		sampleData[1]= new DataPoint (2,2,"blue");
		
		Model model = new Model ();
		model.train(sampleData);
		
		DataPoint[] testData = new DataPoint[1];
		testData[0] = new DataPoint (1,1,"");
		
		System.out.println(model.test(testData));
	}

