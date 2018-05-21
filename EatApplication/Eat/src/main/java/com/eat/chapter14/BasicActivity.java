package com.eat.chapter14;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.eat.L;
import com.eat.R;

public class BasicActivity extends Activity implements LoaderManager.LoaderCallbacks<Integer>{

    private static final int BASIC_LOADER_ID = 0;

    TextView tvResult;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic);
        tvResult = (TextView) findViewById(R.id.text_result);
        getLoaderManager().initLoader(BASIC_LOADER_ID, null, this);
        L.d(getClass(), "ThreadId: %d", Thread.currentThread().getId());
    }

    @Override
    public Loader<Integer> onCreateLoader(int id, Bundle args) {
        L.d(getClass(), "ThreadId: %d", Thread.currentThread().getId());
        return new BasicLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<Integer> loader, Integer data) {
        tvResult.setText(Integer.toString(data));
        L.d(getClass(), "ThreadId: %d", Thread.currentThread().getId());
    }

    @Override
    public void onLoaderReset(Loader<Integer> loader) {
        // Empty
    }

    public void onLoad(View v) {
        L.d(getClass(), "ThreadId: %d", Thread.currentThread().getId());
        getLoaderManager().getLoader(BASIC_LOADER_ID).forceLoad();
    }

    public void onCancel(View v) {
        L.d(getClass(), "ThreadId: %d", Thread.currentThread().getId());
        getLoaderManager().getLoader(BASIC_LOADER_ID).cancelLoad();
    }
}