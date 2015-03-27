import acm.graphics.*;
import acm.util.RandomGenerator;

import java.awt.Color;
import java.awt.Font;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.*;
import java.awt.event.*;



public class Trial extends GCanvas implements ExpConstants, MouseListener {
	private HashMap<Integer, GRect> Blocks;
	private int NumBlocks;
	private TrialState State = TrialState.STARTING;
	private TrialFeedback feedback = new TrialFeedback();
	private ArrayList<Integer> Sequence;
	private ArrayList<Integer> BlocksClicked;
	private final Semaphore readyToLeave = new Semaphore(0);
	private final Semaphore readyToBeginExp = new Semaphore(0);
	private final ReentrantLock lock = new ReentrantLock();
	private long startTime;
	
	//Constructor
	Trial() {
		Blocks = new HashMap<Integer, GRect>();
		BlocksClicked = new ArrayList<Integer>();
		Sequence = new ArrayList<Integer>();
		addMouseListener(this);
	}
	
/*
 * Runs a trial where the user is expected to remember numBlocks blocks correctly.
 * The rest of this process is actually implemented in the closure in addStartButton.
 */
	public TrialFeedback runTrial(int numBlocks) throws InterruptedException {
		startTime = System.currentTimeMillis();
		this.NumBlocks = numBlocks;
		setUpIntro();
		readyToLeave.acquire();	//Wait until the experiment is done.
		
		feedback.correct = checkResults();
		removeAll();
		setBackground(Color.white);
		if(feedback.correct) displayMessage("Correct", 0, kIntroFont);
		else displayMessage("Incorrect", 0, kIntroFont);
		return feedback;
	}

/*
 * Checks whether the blocks that the user clicked were the ones in the sequence.
 */
	private boolean checkResults() {
		if(Sequence.size() != BlocksClicked.size()) return false;
		for(int i = 0; i < Sequence.size(); i++) {
			if(Sequence.get(i) != BlocksClicked.get(i)) return false;
		}
		return true;
	}
	
/*
 * Sets up given trial by creating a DONE button and grid of blocks.
 */
	private void setUpTrial() {
		clearData();
		setBackground(Color.BLACK);
		makeGrid();
		highlightBlocks();
		addDoneButton();
		repaint();	//Done button was not appearing
		State = TrialState.RUNNING;	//Activates the mouseListener
	}
	
/*
 * Adds a DONE button to the screen. 
 * 
 * When clicked, the button will exit the trial if the trial has been finished.
 */
	private void addDoneButton() {
		JButton doneButton = addButton("DONE", kButtonHeight, kButtonWidth, kButtonOffset);
		doneButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				readyToLeave.release();
			}
		});
	}
	
/*
 * Clears data and screen.
 */
	private void clearData() {
		State = TrialState.STARTING;
		Blocks.clear();
		Sequence.clear();
		BlocksClicked.clear();
		removeAll();
	}
	
/*
 * Creates and adds a button to the screen.
 */
	private JButton addButton(String text, int height, int width, double offset) {
		JButton button = new JButton(text);
		double x = (getWidth() - width) / 2;
		double y = getHeight() - height - offset;
		button.setSize(width, height);
		add(button, x, y);
		return button;	//So that an action listener can be added.
	}
	

	
	/*
	 * Displays introductory text and adds a start button.
	 */
	private void setUpIntro() {
		removeAll();
		setBackground(Color.white);
		displayMessage("Lets see if you can do " + Integer.toString(NumBlocks) + " blocks.", 0, kIntroFont);
		displayMessage("Ready to start?", 1, kIntroFont);
		addStartButton();
	}
	
/*
 * Adds a start button and actionListener for it.
 * When button is pressed, trial begins.
 */
	private void addStartButton() {
		JButton startButton = addButton("START", kButtonHeight, kButtonWidth, getHeight() / 2);
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				feedback.timeToStart = (System.currentTimeMillis() - startTime) / 1000.0; //Convert to seconds
				Runnable worker = new Runnable() {
					public void run() {
						setUpTrial();
					}
				};
				Thread t = new Thread(worker);
				t.start();	//Shouldn't this go out of scope? It seems to work.
			}
		});
	}
	
	/*
	 * Generates and stores and random sequence (of NumBlocks blocks) and then highlights
	 * that sequence of blocks.
	 */
	private void highlightBlocks() {
		pause(kBeginningPause);
		generateSequence();

		for(final int blockNum: Sequence) {
			Runnable highlightWorker = new Runnable() {
				public void run() {
					highlightBlock(blockNum, kHighlightTime);
				}
			};
			
			Thread t = new Thread(highlightWorker);
			t.start();
			pauseThread(t, kBetweenHighlightTime + kHighlightTime);
		}
	}
	
/*
 * Pauses a thread for a given number of milliseconds.
 */
	private void pauseThread(Thread t, int pauseTime) {
		try {
			t.sleep(kBetweenHighlightTime + kHighlightTime);
		} catch (InterruptedException e) {
			System.out.println("Something went wrong when Thread slept");
			e.printStackTrace();
		}
	}
	
	//This should not be public. It should be PsychExperiment. Oh well.
	public void showMessage(String msg) {
		removeAll();
		setBackground(Color.white);
		displayMessage(msg, 0, kIntroFont);
	}
	
/*
 * Pauses for a given number of milliseconds.
 */
	private void pause(int pauseTime) {
		try {
			Thread.currentThread().sleep(pauseTime);
		} catch (InterruptedException e) {
			System.out.println("Thread could not sleep.");
			e.printStackTrace();
		}
	}
	
/*
 * Highlights a given block.
 */
	private void highlightBlock(int blockNum, int pauseTime) {
		GRect block = Blocks.get(blockNum);
		block.setFillColor(kHighlightColor);
		pause(pauseTime);
		block.setFillColor(kBlockColor);
	}
	
/*
 * Generates a random sequence of NumBlocks numbers between 0 and 
 * kNumBlock - 1.
 */
	private void generateSequence() {
		RandomGenerator rgen = RandomGenerator.getInstance();
		for(int i = 0; i < NumBlocks; i++) {
			Sequence.add(rgen.nextInt(0, kNumBlocks - 1));
		}
		
	}
	
/*
 * Displays a given message. Message is centered by default. 
 * 
 * numLabels indicates how many labels have been placed down before the current label,
 * which affects offset vales.
 */
	private void displayMessage(String msg, int numLabels, Font font) {
		GLabel label = new GLabel(msg);
		label.setFont(font);
		double x = (getWidth() - label.getWidth()) / 2;
		double y = label.getHeight() * (numLabels + 1) + kIntroLabelOffset;
		add(label, x ,y);
	}
	
/*
 * Creates a n x n grid of blocks.
 */
	private void makeGrid() {
		int gridSize = (int) Math.sqrt(kNumBlocks);
		int blockNum = 0;
		for(int i = 0; i < gridSize; i++) {
			for(int j = 0; j < gridSize; j++) {
				addBlock(i, j, gridSize, blockNum, true);
				blockNum++;
			}
		}
	}
	
/*
 * Adds a block to the canvas and a HashMap.
 */
	private void addBlock(int rowPos, int colPos, int gridSize, int blockNum, boolean addToBlocks) {
		double blockXSpacing = (getWidth() - gridSize * kBlockSize) / (gridSize + 1);
		double blockYSpacing = (getHeight() - gridSize * kBlockSize) / (gridSize + 1);
		double xPos = (colPos + 1) * blockXSpacing + colPos * kBlockSize + OFFSETS[blockNum][0];
		double yPos = (rowPos + 1) * blockYSpacing + rowPos * kBlockSize + OFFSETS[blockNum][1];

		GRect block = new GRect(xPos, yPos, kBlockSize, kBlockSize);
		block.setFillColor(kBlockColor);
		block.setFilled(true);
		if(addToBlocks) Blocks.put(blockNum, block);
		
		add(block);
	}

	//Mouse listener overrides. Just to prevent compiler from complaining.
	@Override public void mouseEntered(MouseEvent arg0) {}
	@Override public void mouseExited(MouseEvent arg0) {}
	@Override public void mouseReleased(MouseEvent arg0) {}
	@Override public void mouseClicked(MouseEvent e) {}

/*
 * Adds the number of the block which was clicked to the BlocksClicked array.
 * If something other than a block was pressed, nothing happens.
 */
	@Override
	public void mousePressed(MouseEvent e) {
		if(State != TrialState.RUNNING) return;
		lock.lock();
		GObject clicked = getElementAt(e.getX(), e.getY());
		int blockNum = findClickedBlock(clicked);
		if(blockNum == -1) return;	//They clicked a non-block
		BlocksClicked.add(blockNum);
		lock.unlock();
	}
	

/*
 * Finds whether a block was clicked - if so, it highlights it for some duration of time.
 */
	private GRect previousBlock = null;
	private int findClickedBlock(final GObject clicked) {
		int found = -1;
		for(final int blockNum: Blocks.keySet()) {
			if(clicked == Blocks.get(blockNum)) {
				Runnable highlightWorker = new Runnable() {
					public void run() {
						BlocksClicked.add(blockNum);
						highlightBlock(blockNum, kHighlightTime);
					}
				};
				
				Thread t = new Thread(highlightWorker);
				t.start();
			}
		}
		
		return found;
	}
}
