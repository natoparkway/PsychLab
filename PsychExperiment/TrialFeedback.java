public class TrialFeedback {
	public double timeToStart;
	public boolean correct;

	
	TrialFeedback() {
		
	}
	
	public String toString() {
		return Double.toString(timeToStart) + " " + correct;
	}
}
