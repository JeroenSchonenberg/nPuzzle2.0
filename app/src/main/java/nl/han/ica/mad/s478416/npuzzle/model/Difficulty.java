package nl.han.ica.mad.s478416.npuzzle.model;

/**
 * Created by jeroen on 03/04/15.
 */
public enum Difficulty{
    EASY(3), MEDIUM(4), HARD(5), DEBUG(2);

    private final int size;

    private Difficulty(int size){
        this.size = size;
    }

    public int getGridSize(){
        return size;
    }
}
