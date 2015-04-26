package nl.han.ica.mad.s478416.npuzzle;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import nl.han.ica.mad.s478416.npuzzle.model.Difficulty;

/**
 * Created by jeroen on 03/04/15.
 */
public class SavegameManager {
    private static final String IMG_KEY = "imgResId";
    private static final String DIFFICULTY_KEY = "difficulty";
    private static final String STATE_KEY = "state";
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

    public Integer[] getSavedState(){
        Difficulty difficulty = getSavedDifficulty();

        String[] stateStr = prefs.getString(STATE_KEY, null).split(",");
        Integer[] state = new Integer[difficulty.getGridSize() * difficulty.getGridSize()];
        for(int i = 0; i < stateStr.length; i++) {
            if(!stateStr[i].isEmpty()){
                state[i] = Integer.parseInt(stateStr[i]);
            }
        }

        return state;
    }

    public void save(int imgResId, Difficulty difficulty, Integer[] state, int moves){
        String stateStr = "";
        for(Integer i : state){
            if(i != null){
                stateStr += i;
            }
            stateStr += ",";
        }
        stateStr = stateStr.substring(0, stateStr.length()-1);// remove last ","

        editor.putInt(IMG_KEY, imgResId);
        editor.putString(DIFFICULTY_KEY, difficulty.toString());
        editor.putString(STATE_KEY, stateStr);
        editor.putInt(MOVE_COUNT_KEY, moves);
        editor.commit();
    }

    public void deleteSavegame(){
        editor.remove(IMG_KEY);
        editor.remove(DIFFICULTY_KEY);
        editor.remove(STATE_KEY);
        editor.remove(MOVE_COUNT_KEY);
        editor.commit();
    }
}
