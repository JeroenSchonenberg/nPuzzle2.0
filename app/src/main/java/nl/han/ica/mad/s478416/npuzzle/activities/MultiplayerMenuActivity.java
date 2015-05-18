package nl.han.ica.mad.s478416.npuzzle.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import butterknife.ButterKnife;
import butterknife.InjectView;
import nl.han.ica.mad.s478416.npuzzle.R;
import nl.han.ica.mad.s478416.npuzzle.activities.gametypes.VersusMultiplayerGameActivity;

public class MultiplayerMenuActivity extends Activity {
	@InjectView(R.id.btn_new_coop_game) 	Button btnNewCoopGame;
	@InjectView(R.id.btn_new_versus_game) 	Button btnNewVersusGame;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_multiplayer_menu);
		ButterKnife.inject(this);

	btnNewVersusGame.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			startActivity(new Intent(MultiplayerMenuActivity.this, VersusMultiplayerGameActivity.class));
		}
	});
	}
}
