package nl.han.ica.mad.s478416.npuzzle.model;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by Jeroen Schonenberg (478416) on 27/03/15.
 */
public class PuzzleModel {
	private ArrayList<IPuzzleModelObserver> observers;

    private int imageResId;
    private Difficulty difficulty;
    private int moves;
	private boolean isShuffled;
    private Integer[] arrangement;
	private Stack moveHistory;
	private long startTime = 0;
	private long endTime;

    public PuzzleModel(int imageResId, Difficulty difficulty, boolean isShuffled){
		this.observers = new ArrayList<>();

        this.imageResId = imageResId;
        this.difficulty = difficulty;
		this.isShuffled = isShuffled;
		this.moves = 0;
		this.moveHistory = new Stack();
        this.arrangement = new Integer[difficulty.getGridSize() * difficulty.getGridSize()];

		// setup default arrangement
		for(int i = 0; i < arrangement.length - 1; i++){
			arrangement[i] = i;
		}
    }

	public PuzzleModel(int imageResId, Difficulty difficulty, boolean shuffled, int moves, Integer[] arrangement) {
		this(imageResId, difficulty, shuffled);

		this.moves = moves;
		if (arrangement != null) this.arrangement = arrangement;
	}

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
		if (startTime == 0) startTime = System.nanoTime();	// start timer if not yet started

		if (this.isFinished()) notifyObservers();
    }

    private boolean isFinished(){
        for(int i = 0; i < arrangement.length; i++){
            if(arrangement[i] != null && arrangement[i] != i) { // volgorde niet veranderen lol
                    return false;
            }
        }

		endTime = System.nanoTime();

        return true;
    }

	/* SIMPLE GET & SET */

	public void setMoveCount(int moveCount){
		this.moves = moveCount;
	}

    public Integer[] getArrangement(){
        return arrangement;
    }

    public int getEmptySlot(){
		return java.util.Arrays.asList(arrangement).indexOf(null);
    }

	public int getTime(){
		return (int)((endTime - startTime) / 1000000); // return the time it took to solve the puzzle, in ms
	}

	public void resetTimer(){
		startTime = 0;
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

	public boolean isShuffled(){ return isShuffled; }

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
