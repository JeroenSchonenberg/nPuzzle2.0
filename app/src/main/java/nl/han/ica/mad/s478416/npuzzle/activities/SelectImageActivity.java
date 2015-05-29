package nl.han.ica.mad.s478416.npuzzle.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import nl.han.ica.mad.s478416.npuzzle.PuzzleImagesAdapter;
import nl.han.ica.mad.s478416.npuzzle.R;
import nl.han.ica.mad.s478416.npuzzle.activities.gametypes.SingleplayerGameActivity;

public class SelectImageActivity extends Activity {
	@InjectView (R.id.gridview_available_images) GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_image);
		ButterKnife.inject(this);

		gridView.setAdapter(new PuzzleImagesAdapter(this));
		gridView.setOnItemClickListener(onGridItemClickListener);
    }

    AdapterView.OnItemClickListener onGridItemClickListener = new AdapterView.OnItemClickListener() {
        @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            int imageResId = (Integer) gridView.getItemAtPosition(position);

			Intent returnIntent = new Intent();
			returnIntent.putExtra(getString(R.string.key_image), imageResId);
			setResult(RESULT_OK,returnIntent);
			finish();

			/*
            Intent intent = new Intent(SelectImageActivity.this, SingleplayerGameActivity.class);
			intent.putExtra(getString(R.string.key_image), imageResId);
            intent.putExtras(getIntent().getExtras());   // pass on all the settings we received
            SelectImageActivity.this.startActivity(intent);
            */
        }
    };
}
