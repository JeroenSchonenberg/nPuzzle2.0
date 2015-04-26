package nl.han.ica.mad.s478416.npuzzle.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import butterknife.ButterKnife;
import butterknife.InjectView;
import nl.han.ica.mad.s478416.npuzzle.R;
import nl.han.ica.mad.s478416.npuzzle.activities.gametypes.SingleplayerGameActivity;
import nl.han.ica.mad.s478416.npuzzle.SavegameManager;

public class MainMenuActivity extends Activity {
	@InjectView(R.id.btn_resume_game)
	Button buttonResumeGame;

	@InjectView(R.id.btn_new_game)
	Button buttonNewGame;

	@InjectView(R.id.btn_multiplayer_game)
	Button buttonMultiplayerGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
		ButterKnife.inject(this);

		buttonResumeGame.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				Intent intent = new Intent(MainMenuActivity.this, SingleplayerGameActivity.class);
				intent.putExtra(getString(R.string.key_resume_game), true);
				MainMenuActivity.this.startActivity(intent);
			}
		});

		buttonNewGame.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				Intent intent = new Intent(MainMenuActivity.this, SelectDifficultyActivity.class);
				MainMenuActivity.this.startActivity(intent);
			}
		});

		buttonMultiplayerGame.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				Intent intent = new Intent(MainMenuActivity.this, MultiplayerMenuActivity.class);
				MainMenuActivity.this.startActivity(intent);
			}
		});
    }

    @Override
    protected void onResume(){
        super.onResume();

        if(new SavegameManager(getApplicationContext()).saveGameExists()){
            buttonResumeGame.setVisibility(View.VISIBLE);
        } else {
            buttonResumeGame.setVisibility(View.GONE);
        }
    }
}
