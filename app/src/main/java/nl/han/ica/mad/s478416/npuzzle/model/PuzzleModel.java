package nl.han.ica.mad.s478416.npuzzle.model;

import java.util.ArrayList;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

public class PuzzleModel {
	private ArrayList<IPuzzleModelObserver> observers;

    private int imageResId;
    private Difficulty difficulty;
	private boolean isShuffled;

	private Integer[] arrangement;
	private int moveCount;
	private ArrayList<Integer> moveHistory;

	private Long startTime;
	private Long endTime;

    public PuzzleModel(int imageResId, Difficulty difficulty, boolean isShuffled, int moveCount, Integer[] arrangement){
		this.observers = new ArrayList<>();
		this.moveHistory = new ArrayList<>();

        this.imageResId = imageResId;
        this.difficulty = difficulty;
		this.isShuffled = isShuffled;
		this.moveCount = moveCount;

		if(arrangement != null){
			this.arrangement = arrangement;
		} else { // use default arrangement
			this.arrangement = new Integer[difficulty.getGridSize() * difficulty.getGridSize()];
			for (int i = 0; i < this.arrangement.length - 1; i++) this.arrangement[i] = i;
		}
    }

    public boolean pieceNeighboursEmptySlot(int pieceId){
		int gridSize = difficulty.getGridSize();
        int pieceSlot = getPieceSlot(pieceId);
        int emptySlot = getEmptySlot();

		boolean above = pieceSlot - gridSize == emptySlot;
		boolean under = pieceSlot + gridSize == emptySlot;
		boolean leftOf = (pieceSlot + 1 == emptySlot) && (emptySlot % gridSize != 0);
		boolean rightOf = (pieceSlot - 1 == emptySlot) && (pieceSlot % gridSize != 0);

		return (above || under || leftOf || rightOf);
    }

    public void movePieceToEmptySlot(int pieceId){
        int emptySlot = getEmptySlot();
        int oldPieceSlot = getPieceSlot(pieceId);
		arrangement[emptySlot] = pieceId;
		arrangement[oldPieceSlot] = null;

		moveCount++;
		moveHistory.add(pieceId);
		if (startTime == null) {
			startTime = System.nanoTime(); // start timer if not yet started
		}

		// check if the puzzle is finished
		if (this.isShuffled && this.isFinished()) {
			notifyObservers();
		}
    }

    private boolean isFinished(){
        for(int i = 0; i < arrangement.length; i++){
            if (arrangement[i] != null && arrangement[i] != i) {
				return false;
			}
        }

		endTime = System.nanoTime();
        return true;
    }

	public int getTime(){
		if(startTime == null){
			return 0;
		} else {
			long time = (endTime != null) ? (endTime - startTime) : (System.nanoTime() - startTime);
			return (int) TimeUnit.MILLISECONDS.convert(time, TimeUnit.NANOSECONDS);
		}
	}

    public Integer[] getArrangement(){
        return arrangement;
    }

	public int getPieceNumber(int slot){
		return arrangement[slot];
	}

    public int getEmptySlot(){
		return java.util.Arrays.asList(arrangement).indexOf(null);
    }

    public int getPieceSlot(int pieceId){
        return java.util.Arrays.asList(arrangement).indexOf(pieceId);
    }

	public int getImageResourceId(){
		return imageResId;
	}

    public Difficulty getDifficulty(){
        return difficulty;
    }

	public int getGridSize(){
		return difficulty.getGridSize();
	}

	public boolean isShuffled(){
		return isShuffled;
	}

	public void setShuffled(){
		this.isShuffled = true;
	}

	public void setMoveCount(int moveCount){
		this.moveCount = moveCount;
	}

	public int getMoveCount(){
		return moveCount;
	}

	public ArrayList<Integer> getMoveHistory(){
		return moveHistory;
	}

	public void resetTimer(){
		startTime = null;
	}

	/* OBSERVER / OBSERVABLE */

	public void addObserver(IPuzzleModelObserver observer){
		this.observers.add(observer);
	}

	private void notifyObservers(){
		for(IPuzzleModelObserver observer : observers){
			observer.onPuzzleFinished(this);
		}
	}
}
