package nl.han.ica.mad.s478416.npuzzle;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import nl.han.ica.mad.s478416.npuzzle.model.Difficulty;

public class SavegameManager {
    private static final String IMG_KEY = "imgResId";
    private static final String DIFFICULTY_KEY = "difficulty";
    private static final String ARRANGEMENT_KEY = "arrangement";
    private static final String MOVE_COUNT_KEY = "moveCount";
	private static final String MOVE_HISTORY_KEY = "moveHistory";

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    public SavegameManager(Context context){
        this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
        this.editor = prefs.edit();
    }

    public boolean saveGameExists(){
		return (prefs.getInt(IMG_KEY, -1) != -1); // if there's an img resource saved it's save to assume the other data exists too
    }

    public int getSavedImgResId(){
        return prefs.getInt(IMG_KEY, 0);
    }

    public Difficulty getSavedDifficulty(){
        return Difficulty.valueOf( prefs.getString(DIFFICULTY_KEY, null) );
    }

    public int getSavedMoveCount(){
        return prefs.getInt(MOVE_COUNT_KEY, 0);
    }

    public Integer[] getSavedArrangement(){
		String[] sArrangement = prefs.getString(ARRANGEMENT_KEY, null).split(",");

		Integer[] arrangement = new Integer[ sArrangement.length ];
        for(int i = 0; i < sArrangement.length; i++) {
            if (!sArrangement[i].isEmpty()) arrangement[i] = Integer.parseInt(sArrangement[i]);
        }

        return arrangement;
    }

    public void save(int imgResId, Difficulty difficulty, Integer[] arrangement, int moves){
        String sArrangement = "";
        for (Integer i : arrangement) sArrangement += (i != null) ? (i + ",") : ","; // ~~magic~~
		sArrangement = sArrangement.substring(0, sArrangement.length() - 1); // remove last ","

        editor.putInt(IMG_KEY, imgResId);
        editor.putString(DIFFICULTY_KEY, difficulty.toString());
        editor.putString(ARRANGEMENT_KEY, sArrangement);
        editor.putInt(MOVE_COUNT_KEY, moves);
		// editor.putString(MOVE_HISTORY_KEY);
        editor.commit();
    }

    public void deleteSavegame(){
        editor.remove(IMG_KEY);
        editor.remove(DIFFICULTY_KEY);
        editor.remove(ARRANGEMENT_KEY);
        editor.remove(MOVE_COUNT_KEY);
		editor.remove(MOVE_HISTORY_KEY);
        editor.commit();
    }
}
