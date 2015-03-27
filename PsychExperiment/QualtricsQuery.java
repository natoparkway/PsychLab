import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.lang.*;

import org.json.*;
import org.json.simple.parser.JSONParser;


public class QualtricsQuery implements ExpConstants {
	private static final String rootURL = "https://survey.qualtrics.com/WRAPI/ControlPanel/api.php?API_SELECT=ControlPanel&Version=2.4";
	private static final String userInfo = "User=nokun%40stanford.edu%23stanforduniversity";
	private static final String APIToken = "Token=d5JD7tjNg2RylNHw2lLAMP84aQcV4ZSYfoii2jV1";
	private static final String surveyID = "SurveyID=SV_8jozPpWIKLD5sJn";
	private static final String updateData = "Request=updateResponseEmbeddedData";
	private static final String format = "Format=JSON";
	private static final String getLegacyData = "https://survey.qualtrics.com/WRAPI/ControlPanel/api.php?API_SELECT=ControlPanel&Version=2.4&Request=getLegacyResponseData&User=nokun%40stanford.edu%23stanforduniversity&Token=d5JD7tjNg2RylNHw2lLAMP84aQcV4ZSYfoii2jV1&Format=JSON&SurveyID=SV_8jozPpWIKLD5sJn";
	private boolean cachedLegacyData = false;
	private JSONObject legacyData = new JSONObject();
	
	QualtricsQuery() {
		
	}
	
	private String makeRequest(ExperimentFeedback fb, String responseID) {
		String request = rootURL + "&" + updateData + "&" + userInfo + "&" + APIToken + "&" + surveyID + "&" + format;
		request += "&ResponseID=" + responseID;
		
		request = appendResults(fb, request);
		
		System.out.println(request);
		return request;
	}
	
	private String appendResults(ExperimentFeedback fb, String result) {
		for(int i = kMinLevel; i <= kMaxLevel; i++) {
			result += "&ED[Span-" + Integer.toString(i) + "-numAttempts]=" + fb.getStrNumAttempts(i);
			result += "&ED[Span-" + Integer.toString(i) + "-numCorrect]=" + fb.getStrNumSuccesses(i);
			result += "&ED[Span-" + Integer.toString(i) + "-timesToStart]=" + fb.getStrTimeToStart(i);
		}
		
		return result;
	}
	
//	String test = "https://survey.qualtrics.com/WRAPI/ControlPanel/api.php?API_SELECT=ControlPanel&Version=2.4&Request=updateResponseEmbeddedData&User=nokun%40stanford.edu%23stanforduniversity&Token=d5JD7tjNg2RylNHw2lLAMP84aQcV4ZSYfoii2jV1&SurveyID=SV_8jozPpWIKLD5sJn&ResponseID=R_795jtcSwSiVKuTH&ED[yPos]=42&ED[xPos]=42,85&ED[time]=42&Format=JSON";
	
	public String getResponseId(String uniqueKey) {
		try {
			if(!cachedLegacyData) {
				URL oracle = new URL(getLegacyData);
				BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));
		
			    String inputLine = in.readLine();
			    in.close();
			
				JSONObject response = new JSONObject(inputLine);
				legacyData = response;
				cachedLegacyData = true;
			}
			System.out.println(legacyData);
			
			Iterator keysLocal = legacyData.keys();	//So that it's a local copy
			Iterator<JSONObject> keys = keysLocal;
			
			while(keys.hasNext()) {
				String key = JSONObject.valueToString(keys.next()).substring(1);
				key = key.substring(0, key.length() - 1);	//Remove quotes

				JSONObject value = legacyData.optJSONObject(key);
				if(value == null) continue;
				if(value.optString("UniqueKey").equals(uniqueKey)) return key;
			}
		} catch(Exception pe) {
		    System.out.println(pe);
		    return null;
		}
		
		return null;
	}
	
	public boolean sendResults(ExperimentFeedback fb, String responseID) {
		String request = makeRequest(fb, responseID);
		try {
			URL oracle = new URL(request);
			BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));
		    in.close();
		} catch(Exception pe) {
		    System.out.println(pe);
		    return false;
		}
		
		return true;
	}
}
