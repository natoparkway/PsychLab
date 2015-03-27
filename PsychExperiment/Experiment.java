
import acm.graphics.*;
import acm.program.GraphicsProgram;
import acm.program.Program;

import java.util.*;

public class Experiment extends Program implements ExpConstants {
	ExperimentFeedback feedback = new ExperimentFeedback();
	
	Experiment() {

	}
	
	public ExperimentFeedback runExperiment(GCanvas canvas) {
		Trial trial = new Trial();
		canvas = trial;
		add(canvas);
		for(int i = kMinLevel; i < kMaxLevel; i++) {
			if(!runLevel(i, trial)) i--;	//Repeat the sequence if failed
		}
		return feedback;
	}
	
	private boolean runLevel(int numBlocks, Trial trial) {
		int numCorrect = 0;
		
		for(int i = 0; i < kNumTrialsPerLevel; i++) {
			try {
				TrialFeedback trialResult = trial.runTrial(numBlocks);
				if(trialResult.correct) numCorrect++;
				feedback.addInput(numBlocks, trialResult);
			} catch (InterruptedException e) { e.printStackTrace();}
		}
		
		return numCorrect >= kMinCorrect;
	}
}

	