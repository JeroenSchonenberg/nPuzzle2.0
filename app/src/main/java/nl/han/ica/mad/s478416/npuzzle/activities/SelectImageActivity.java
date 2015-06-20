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

public class SelectImageActivity extends Activity implements AdapterView.OnItemClickListener{
	@InjectView (R.id.gridview_available_images) GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_image);
		ButterKnife.inject(this);

		gridView.setAdapter(new PuzzleImagesAdapter(this));
		gridView.setOnItemClickListener(this);
    }

	@Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		int imageResId = (Integer) gridView.getItemAtPosition(position);

		Intent resultData = new Intent();
		resultData.putExtra(getString(R.string.key_image), imageResId);
		setResult(RESULT_OK, resultData);
		finish();
	}
}
