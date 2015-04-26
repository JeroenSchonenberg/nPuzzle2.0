package nl.han.ica.mad.s478416.npuzzle.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;
import nl.han.ica.mad.s478416.npuzzle.R;
import nl.han.ica.mad.s478416.npuzzle.SavegameManager;
import nl.han.ica.mad.s478416.npuzzle.model.Difficulty;

public class GameFinishedActivity extends Activity implements View.OnClickListener {
	@InjectView(R.id.container)			LinearLayout container;
	@InjectView(R.id.puzzleImage)		ImageView puzzleImage;

    @InjectView(R.id.difficultyValue) 	TextView textViewDifficulty;
	@InjectView(R.id.movesValue) 		TextView textViewMoves;
	@InjectView(R.id.timeValue) 		TextView textViewTime;

	@InjectView(R.id.btn_new_game)  	Button buttonNewGame;
	@InjectView(R.id.btn_main_menu)  	Button buttonMainMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_finished);
		ButterKnife.inject(this);

		new SavegameManager(getApplicationContext()).deleteSavegame();

		// RETRIEVE DATA FROM INTENT
        Intent i = getIntent();
        int imgResId = i.getIntExtra(getString(R.string.key_image), 0);
        Difficulty difficulty = (Difficulty)i.getSerializableExtra(getString(R.string.key_difficulty));
        int movesCount = i.getIntExtra(getString(R.string.key_moves_count), 0);
		int timeMilliseconds = i.getIntExtra(getString(R.string.key_time), 0);
		float timeSeconds = ((float) timeMilliseconds) / 1000;

		// SETUP UI
		container.measure(0, 0);

		Picasso.with(this)
				.load(imgResId)
				.placeholder(R.drawable.placeholder_puzzle_image)
				.resize(container.getMeasuredWidth(), container.getMeasuredWidth())
				.into(puzzleImage);

        textViewDifficulty.setText(difficulty.toString());
        textViewMoves.setText(Integer.toString(movesCount));
		textViewTime.setText(String.format("%.2f", timeSeconds) + getString(R.string.seconds_suffix));

        buttonNewGame.setOnClickListener(this);
        buttonMainMenu.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.btn_new_game:
                startActivityDiscardStack(SelectDifficultyActivity.class);
                break;
            case R.id.btn_main_menu:
                startActivityDiscardStack(MainMenuActivity.class);
                break;
        }
    }

	@Override
	public void onBackPressed(){
		startActivityDiscardStack(MainMenuActivity.class);
	}

    private void startActivityDiscardStack(Class c){
        Intent i = new Intent(this, c);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }
}
