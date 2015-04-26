package nl.han.ica.mad.s478416.npuzzle.views;

import android.content.Context;
import android.widget.ImageView;

public class PuzzlePiece extends ImageView implements IPuzzlePiece{
    private int number;

    public PuzzlePiece(Context context, int number) {
        super(context, null, 0);
        this.number = number;
    }

    public int getNumber(){
        return number;
    }
}
