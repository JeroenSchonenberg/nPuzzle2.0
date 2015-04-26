package nl.han.ica.mad.s478416.npuzzle.model;

import java.util.ArrayList;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

public class PuzzleModel {
	private ArrayList<IPuzzleModelObserver> observers;

    private int imageResId;
    private Difficulty difficulty;
	private boolean isShuffled;
    private int moves;
    private Integer[] arrangement;
	private Stack moveHistory;
	private Long startTime;
	private Long endTime;

    public PuzzleModel(int imageResId, Difficulty difficulty, boolean isShuffled, int moves, Integer[] arrangement){
		this.observers = new ArrayList<>();
		this.moveHistory = new Stack();

        this.imageResId = imageResId;
        this.difficulty = difficulty;
		this.isShuffled = isShuffled;
		this.moves = moves;

		if(arrangement != null){
			this.arrangement = arrangement;
		} else { // use default arrangement
			this.arrangement = new Integer[difficulty.getGridSize() * difficulty.getGridSize()];
			for (int i = 0; i < this.arrangement.length - 1; i++) this.arrangement[i] = i;
		}
    };

    public boolean pieceNeighboursEmptySlot(int pieceId){
        int pieceSlot = getPieceSlot(pieceId);
        int emptySlot = getEmptySlot();
		int gridSize = difficulty.getGridSize();

        if((pieceSlot - gridSize == emptySlot)
            || (pieceSlot + gridSize == emptySlot)
            || (pieceSlot + 1 == emptySlot && emptySlot % gridSize != 0)
            || (pieceSlot - 1 == emptySlot && pieceSlot % gridSize != 0) ){
            return true;
        } else {
            return false;
        }
    }

    public void movePieceToEmptySlot(int pieceId){
        int emptySlot = getEmptySlot();
        int oldPieceSlot = getPieceSlot(pieceId);
		arrangement[emptySlot] = pieceId;
		arrangement[oldPieceSlot] = null;

        moves++;
		moveHistory.push(pieceId);
		startTime = (startTime == null) ? System.nanoTime() : startTime; // start timer if not yet started

		if (this.isFinished()) notifyObservers();
    }

    private boolean isFinished(){
        for(int i = 0; i < arrangement.length; i++){
            if (arrangement[i] != null && arrangement[i] != i) return false;
        }

		endTime = System.nanoTime();
        return true;
    }

	public int getTime(){
		if(startTime != null && endTime != null){	// game is finished
			return (int) TimeUnit.MILLISECONDS.convert(endTime - startTime, TimeUnit.NANOSECONDS);
		} else if (startTime != null){				// game is not yet finished but it has started
			return (int) TimeUnit.MILLISECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
		} else {									// game hasn't even started yet
			return 0;
		}
	}

	public void setMoveCount(int moveCount){
		this.moves = moveCount;
	}

    public Integer[] getArrangement(){
        return arrangement;
    }

    public int getEmptySlot(){
		return java.util.Arrays.asList(arrangement).indexOf(null);
    }

	public void resetTimer(){
		startTime = null;
	}

    public int getPieceSlot(int pieceId){
        return java.util.Arrays.asList(arrangement).indexOf(pieceId);
    }

	public int getImageResourceId(){
		return imageResId;
	}

    public int getMoveCount(){
        return moves;
    }

	public Stack getMoveHistory(){
		return moveHistory;
	}

    public Difficulty getDifficulty(){
        return difficulty;
    }

	public boolean isShuffled(){
		return isShuffled;
	}

	public void setShuffled(){
		this.isShuffled = true;
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
