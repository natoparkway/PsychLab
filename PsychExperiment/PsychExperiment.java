/*
 * File: BlankClass.java
 * ---------------------
 * This class is a blank one that you can change at will. Remember, if you change
 * the class name, you'll need to change the filename so that it matches.
 * Then you can extend GraphicsProgram, ConsoleProgram, or DialogProgram as you like.
 */

import java.awt.Color;
import java.net.*;
import java.util.StringTokenizer;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.SwingUtilities;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import acm.program.*;
import acm.graphics.*;

public class PsychExperiment extends Program implements ExpConstants {   
    private String responseID = "";

    public static void main(String args[]) {
    	new PsychExperiment().start();
    }
    
    public void run() {
    	//To do:
    	//Fix Trial so that only one duplicate try is allowed
    	//Contend for errors in getting queries
    	//Get uniqueKey from user!
    	
    	String uniqueKey = "0.dtr6w7ny";
    	QualtricsQuery query = new QualtricsQuery();
    	cacheLegacyData(query);
    	ExperimentFeedback feedback = runExperiment();
    	String responseID = query.getResponseId(uniqueKey);


    	query.sendResults(feedback, responseID);


    }
    
    private void cacheLegacyData(final QualtricsQuery query) {
		Runnable worker = new Runnable() {
			public void run() {
				query.getResponseId("");	//This will just cache the data
			}
		};
		Thread t = new Thread(worker);
		t.start();
    }
    
	private ExperimentFeedback runExperiment() {
		ExperimentFeedback feedback = new ExperimentFeedback();
		Trial trial = new Trial();
		add(trial);
		for(int i = kMinLevel; i <= kMaxLevel; i++) {
			if(!runLevel(i, trial, feedback)) {
				i--;	//Repeat the sequence if failed
				trial.showMessage("Not Yet");
				pause(kSummaryPauseTime);
			}
		}
		trial.showMessage("You're all done!");
		return feedback;
	}
	
	private boolean runLevel(int numBlocks, Trial trial, ExperimentFeedback feedback) {
		int numCorrect = 0;
		
		for(int i = 0; i < kNumTrialsPerLevel; i++) {
			try {
				TrialFeedback trialResult = trial.runTrial(numBlocks);
				System.out.println(trialResult.toString());
				if(trialResult.correct) numCorrect++;
				feedback.addInput(numBlocks, trialResult);
			} catch (InterruptedException e) { e.printStackTrace();}
			pause(kFeedbackTime);
		}
		
		return numCorrect >= kMinCorrect;
	}
	
	
    

    
    
    
    
    
    
    
    
    
    
    
	private void get_friends() throws Exception {
		try {
			String sampleGetAllFriends ="https://survey.qualtrics.com/WRAPI/ControlPanel/api.php?API_SELECT=ControlPanel&Version=2.4&Request=getLegacyResponseData&User=nokun%40stanford.edu%23stanforduniversity&Token=d5JD7tjNg2RylNHw2lLAMP84aQcV4ZSYfoii2jV1&Format=JSON&SurveyID=SV_8jozPpWIKLD5sJn";
			String test = "https://survey.qualtrics.com/WRAPI/ControlPanel/api.php?API_SELECT=ControlPanel&Version=2.4&Request=updateResponseEmbeddedData&User=nokun%40stanford.edu%23stanforduniversity&Token=d5JD7tjNg2RylNHw2lLAMP84aQcV4ZSYfoii2jV1&SurveyID=SV_8jozPpWIKLD5sJn&ResponseID=R_795jtcSwSiVKuTH&ED[yPos]=42&ED[xPos]=42,85&ED[time]=42&Format=JSON";
			
			URL oracle = new URL(test);
			BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));
	
		    String inputLine = in.readLine();
		    in.close();
		
		    JSONParser parser = new JSONParser();
			Object obj = new Object();
	
			
			obj = parser.parse(inputLine);
		
			JSONObject jObj = (JSONObject)obj;
			System.out.println(jObj);
			
			Iterator<JSONObject> keys = jObj.values().iterator();
			while(keys.hasNext()) {
				String responseId = (String) keys.next().get("ResponseID");
				System.out.println(responseId);
			}
//			URL oracle = new URL(sampleGetAllFriends);
//			BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));
//	
//		    String inputLine = in.readLine();
//		    in.close();
//		
//		    JSONParser parser = new JSONParser();
//			Object obj = new Object();
//	
//			
//			obj = parser.parse(inputLine);
//		
//			JSONObject jObj = (JSONObject)obj;
//			System.out.println(jObj);
//			////			JSONObject friend_obj = (JSONObject) jObj.get("friends");
////			JSONArray data_arr = (JSONArray) obj;
//			
//			Iterator<JSONObject> keys = jObj.values().iterator();
//			while(keys.hasNext()) {
//				String responseId = (String) keys.next().get("ResponseID");
//				System.out.println(responseId);
//			}
		} catch(Exception pe) {
		    System.out.println("Error");
		    System.out.println(pe);
		}
	}
}

