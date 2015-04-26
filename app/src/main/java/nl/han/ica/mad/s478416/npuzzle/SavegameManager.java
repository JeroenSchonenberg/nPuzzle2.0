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

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    public SavegameManager(Context context){
        this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
        this.editor = prefs.edit();
    }

    public boolean saveGameExists(){
        if(prefs.getInt(IMG_KEY, -1) != -1){
            return true;
        }

        return false;
    }

    public int getSavedImgResId(){
        return prefs.getInt(IMG_KEY, 0);
    }

    public Difficulty getSavedDifficulty(){
        return Difficulty.valueOf(prefs.getString(DIFFICULTY_KEY, null));
    }

    public int getSavedMoveCount(){
        return prefs.getInt(MOVE_COUNT_KEY, 0);
    }

    public Integer[] getSavedArrangement(){
        Difficulty difficulty = getSavedDifficulty();

        String[] stateStr = prefs.getString(ARRANGEMENT_KEY, null).split(",");
        Integer[] state = new Integer[difficulty.getGridSize() * difficulty.getGridSize()];
        for(int i = 0; i < stateStr.length; i++) {
            if(!stateStr[i].isEmpty()){
                state[i] = Integer.parseInt(stateStr[i]);
            }
        }

        return state;
    }

    public void save(int imgResId, Difficulty difficulty, Integer[] arrangement, int moves){
        String sArrangement = "";
        for(Integer i : arrangement){
            if(i != null){
				sArrangement += i;
            }
			sArrangement += ",";
        }
		sArrangement = sArrangement.substring(0, sArrangement.length()-1);// remove last ","

        editor.putInt(IMG_KEY, imgResId);
        editor.putString(DIFFICULTY_KEY, difficulty.toString());
        editor.putString(ARRANGEMENT_KEY, sArrangement);
        editor.putInt(MOVE_COUNT_KEY, moves);
        editor.commit();
    }

    public void deleteSavegame(){
        editor.remove(IMG_KEY);
        editor.remove(DIFFICULTY_KEY);
        editor.remove(ARRANGEMENT_KEY);
        editor.remove(MOVE_COUNT_KEY);
        editor.commit();
    }
}
