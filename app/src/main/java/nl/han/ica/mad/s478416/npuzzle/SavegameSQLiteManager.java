package nl.han.ica.mad.s478416.npuzzle;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import nl.han.ica.mad.s478416.npuzzle.model.Difficulty;

public class SavegameSQLiteManager extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "nPuzzleDB";
    private static final String TABLE_NAME = "SaveGames";

    private static Integer lastLoad_imgResId;
    private static Difficulty lastLoad_difficulty;
    private static Integer[] lastLoad_arrangement;
    private static Integer lastLoad_moves;

    public SavegameSQLiteManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " ( " +
                "id int, " +
                "imgResId int, "+
                "difficulty String, "+
                "arrangement String, "+
                "moves int )"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME);
        this.onCreate(db);
    }

    public void loadSaveGame(){
        SQLiteDatabase db = this.getWritableDatabase();
        String [] columns = {"id","imgResId","difficulty","arrangement","moves"};
        Cursor cursor = db.query(TABLE_NAME, columns,"id = ?", new String[] { "1"}, null,null,null,null);
        if (cursor.moveToFirst()) {

            String[] sArrangement = cursor.getString(3).split(",");
            Integer[] arrangement = new Integer[ sArrangement.length ];
            for(int i = 0; i < sArrangement.length; i++) {
                if (!sArrangement[i].isEmpty()) arrangement[i] = Integer.parseInt(sArrangement[i]);
            }
            lastLoad_arrangement = arrangement;
            lastLoad_imgResId = cursor.getInt(1);
            lastLoad_moves = cursor.getInt(4);
            lastLoad_difficulty = Difficulty.valueOf(cursor.getString(2));
        }
    }
    public int getSavedImgResId(){
        return this.lastLoad_imgResId;
    }

    public Difficulty getSavedDifficulty(){
        return this.lastLoad_difficulty;
    }

    public int getSavedMoveCount(){
        return this.lastLoad_moves;
    }

    public Integer[] getSavedArrangement(){ return this.lastLoad_arrangement; }

    public boolean saveGameExists(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String [] {"id"},"id = ?", new String[] { "1"}, null,null,null,null);
        if (cursor.moveToFirst()) {
            return true;
        }else{
            return false;
        }
    }

    public void saveGame(int imgResId, Difficulty difficulty, Integer[] arrangement, int moves){
        String sArrangement = "";
        for (Integer i : arrangement) sArrangement += (i != null) ? (i + ",") : ","; // ~~magic~~
        sArrangement = sArrangement.substring(0, sArrangement.length() - 1); // remove last ","

        SQLiteDatabase db = this.getWritableDatabase();
        String InsertQuery = "INSERT INTO " + TABLE_NAME + " VALUES ('1','"
                + imgResId + "','"
                + difficulty.toString() + "','"
                + sArrangement + "','"
                + moves + "')";
        db.execSQL(InsertQuery);
        db.close();
    }
    public void deleteSavegame(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME," id = ?",new String[] {"1"});
        db.close();
    }

}