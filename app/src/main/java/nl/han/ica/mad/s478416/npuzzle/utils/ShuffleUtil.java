package nl.han.ica.mad.s478416.npuzzle.utils;

import java.util.ArrayList;
import java.util.Random;

public class ShuffleUtil {
	public static int[] genShuffleSequence(int length, int gridSize, int initialEmptySlot){
		int[] sequence = new int[length];
		int emptySlot = initialEmptySlot;
		int previousEmptySlot = initialEmptySlot;

		for(int i = 0; i < length; i++){
			ArrayList<Integer> possibilities = calcPossibleMoves(emptySlot, previousEmptySlot, gridSize);
			int randPossibility = possibilities.get( new Random().nextInt(possibilities.size() ));

			previousEmptySlot = emptySlot;	// remember the current emptyslot
			emptySlot = randPossibility;	// update emptyslot
			sequence[i] = randPossibility;	// add to the shuffle sequence
		}

		return sequence;
	}

	private static ArrayList<Integer> calcPossibleMoves(int empty, int prevEmpty, int gridSize){
		ArrayList<Integer> possibilities = new ArrayList<>();
		int slotCount = gridSize * gridSize;

		int u = empty - gridSize;
		int d = empty + gridSize;
		int l = empty - 1;
		int r = empty + 1;

		if ((u != prevEmpty) && (u >= 0)){
			possibilities.add(u);
		}

		if ((d != prevEmpty) && (d < slotCount)){
			possibilities.add(d);
		}

		if ((l != prevEmpty) && (empty % gridSize != 0) && (l > -1)){
			possibilities.add(l);
		}

		if ((r != prevEmpty) && (r % gridSize != 0) && (r < slotCount)){
			possibilities.add(r);
		}

		return possibilities;
	}
}
