import java.util.ArrayList;


public class ExperimentFeedback implements ExpConstants {
	private LevelFeedback[] results = new LevelFeedback[kMaxLevel + 1];
	
	ExperimentFeedback() {
		for(int i = 0; i < results.length ; i++) {
			results[i] = new LevelFeedback();
		}
	}
	
	public void addInput(int index, TrialFeedback fb) {
		if(fb.correct) results[index].numSuccesses++;
		results[index].numAttempts++;
		results[index].timeToStart.add(fb.timeToStart);
	}
	
	public int getNumAttempts(int index) {
		return results[index].numAttempts;
	}
	
	public int getNumSuccesses(int index) {
		return results[index].numSuccesses;
	}
	
	public ArrayList<Double> getTimeToStart(int index) {
		return results[index].timeToStart;
	}
	
	public String getStrNumAttempts(int index) {
		return Integer.toString(getNumAttempts(index));
	}
	
	public String getStrNumSuccesses(int index) {
		return Integer.toString(getNumSuccesses(index));
	}
	
	public String getStrTimeToStart(int index) {
		String result = "";
		for(double value: getTimeToStart(index)) {
			result += Double.toString(value) + ",";
		}
		return result.substring(0, result.length() - 2);	//Remove trailing comma
	}

}
