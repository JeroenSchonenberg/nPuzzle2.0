package nl.han.ica.mad.s478416.npuzzle.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import nl.han.ica.mad.s478416.npuzzle.R;
import nl.han.ica.mad.s478416.npuzzle.activities.gametypes.VersusMultiplayerActivity;

public class VersusGameFinished extends Activity {
	@InjectView(R.id.title) TextView title;
	@InjectView(R.id.opponentsAvatar) ImageView opponentsAvatarImageView;
	@InjectView(R.id.myAvatar) ImageView myAvatarImageView;
	@InjectView(R.id.btn_rematch) Button buttonRematch;
	@InjectView(R.id.btn_main_menu) Button buttonMainMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_versus_game_finished);
		ButterKnife.inject(this);

		Intent i = getIntent();
		boolean iWon = i.getBooleanExtra(getString(R.string.key_victory_indicator), false);
		String myAvatarUrl = i.getStringExtra(getString(R.string.key_my_avatar));
		String opponentsAvatarUrl = i.getStringExtra(getString(R.string.key_opponents_avatar));

		title.setText(iWon ? "YOU WIN" : "YOU LOSE");
		Picasso.with(this).load(opponentsAvatarUrl).resize(255, 255).into(opponentsAvatarImageView);
		Picasso.with(this).load(myAvatarUrl).resize(255, 255).into(myAvatarImageView);

		buttonRematch.setOnClickListener(onButtonRematchClick);
		buttonMainMenu.setOnClickListener(onButtonMainMenuClick);
	}

	private View.OnClickListener onButtonRematchClick = new View.OnClickListener() {
		@Override public void onClick(View v) {
			startActivityDiscardStack(VersusMultiplayerActivity.class);
		}
	};

	private View.OnClickListener onButtonMainMenuClick = new View.OnClickListener() {
		@Override public void onClick(View v) {
			startActivityDiscardStack(MainMenuActivity.class);
		}
	};

	private void startActivityDiscardStack(Class c){
		Intent i = new Intent(this, c);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(i);
		finish();
	}
}
