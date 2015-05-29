package nl.han.ica.mad.s478416.npuzzle.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import butterknife.ButterKnife;
import butterknife.InjectView;
import nl.han.ica.mad.s478416.npuzzle.R;
import nl.han.ica.mad.s478416.npuzzle.model.Difficulty;

public class SelectDifficultyActivity extends Activity implements View.OnClickListener{
	@InjectView(R.id.btn_debug) 	Button buttonDebug;
	@InjectView(R.id.btn_easy) 		Button buttonEasy;
	@InjectView(R.id.btn_normal) 	Button buttonNormal;
	@InjectView(R.id.btn_hard) 		Button buttonHard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_difficulty);
		ButterKnife.inject(this);

		buttonDebug.setOnClickListener(this);
		buttonEasy.setOnClickListener(this);
		buttonNormal.setOnClickListener(this);
		buttonHard.setOnClickListener(this);
	}

	@Override
	public void onClick(View v){
		switch(v.getId()){
			case R.id.btn_debug:
				startChoosePuzzleActivity(Difficulty.DEBUG);
				break;
			case R.id.btn_easy:
				startChoosePuzzleActivity(Difficulty.EASY);
				break;
			case R.id.btn_normal:
				startChoosePuzzleActivity(Difficulty.MEDIUM);
				break;
			case R.id.btn_hard:
				startChoosePuzzleActivity(Difficulty.HARD);
				break;
		}
	}

    public void startChoosePuzzleActivity(Difficulty chosenDifficulty) {
		Intent returnIntent = new Intent();
		returnIntent.putExtra(getString(R.string.key_difficulty), chosenDifficulty);
		setResult(RESULT_OK,returnIntent);
		finish();

		/*
        Intent intent = new Intent(SelectDifficultyActivity.this, SelectImageActivity.class);
        intent.putExtra(getString(R.string.key_difficulty), chosenDifficulty);
        SelectDifficultyActivity.this.startActivity(intent);
        */
    }
}
