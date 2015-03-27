import java.awt.Color;
import java.awt.Font;


public interface ExpConstants {
	public static final int kMaxLevel = 3;
	public static final int kMinLevel = 2;
	public static final int kNumTrialsPerLevel = 1;
	public static final int kMinCorrect = 1;
	public static final int kFeedbackTime = 1800;
	public static final int kSummaryPauseTime = 2100;

	
	public static final int kNumBlocks = 9;	//Should be a square
	public static final double kBlockSize = 50;
	public static final Color kBlockColor = Color.blue;
	public static final Color kHighlightColor = Color.yellow;
	public static final Color kDoubleClickedColor = Color.green;
	public static final int kHighlightTime = 1000;	//Milliseconds
	public static final int kBetweenHighlightTime = 250;
	public static final int kBeginningPause = 500;
	public static final int kClickedPauseTime = 100;

	
	public static final int[][] OFFSETS = {
		{52, -2},
		{17, 11},
		{-14, -19},
		{21, 0},
		{0, 0},
		{15, -2},
		{3, 3},
		{-3, 3},
		{-7, 2}
	};
	
	public static final double kButtonOffset = 10;
	public static final int kButtonHeight = 40;
	public static final int kButtonWidth = 70;
	public enum TrialState {
		STARTING, RUNNING, FINISHED
	}
	
	public static final int kIntroLabelOffset = 10;	//Offset of introductory label from top
	public static final Font kIntroFont = new Font("SansSerif", Font.BOLD , 25);
}
