package nl.han.ica.mad.s478416.npuzzle.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

import butterknife.ButterKnife;
import butterknife.InjectView;
import nl.han.ica.mad.s478416.npuzzle.R;

public class MultiplayerMenuActivity extends Activity {
	@InjectView(R.id.btn_new_coop_game) 	Button btnNewCoopGame;
	@InjectView(R.id.btn_new_versus_game) 	Button btnNewVersusGame;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_multiplayer_menu);
		ButterKnife.inject(this);
	}
}
