package com.eat.chapter4;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import com.eat.L;
import com.eat.R;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;


public class PipeExampleActivity extends Activity {

    private static final String TAG = "PipeExampleActivity";
    private EditText editText;

    PipedReader pipedReader;
    PipedWriter pipedWriter;

    private Thread workerThread;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pipedReader = new PipedReader();
        pipedWriter = new PipedWriter();

        try {
            pipedWriter.connect(pipedReader);
        } catch (IOException e) {
            e.printStackTrace();
        }

        setContentView(R.layout.activity_pipe);
        editText = (EditText) findViewById(R.id.edit_text);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                L.d(getClass(), "ThreadId: %d", Thread.currentThread().getId());
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                L.d(getClass(), "ThreadId: %d", Thread.currentThread().getId());
                try {
                    // Only handle addition of characters
                    if (count > before) {
                        // Write the last entered character to the pipe
                        pipedWriter.write(charSequence.subSequence(before, count).toString());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                L.d(getClass(), "ThreadId: %d", Thread.currentThread().getId());
            }
        });

        workerThread = new Thread(new TextHandlerTask(pipedReader));
        workerThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        workerThread.interrupt();
        try {
            pipedReader.close();
            pipedWriter.close();
        } catch (IOException e) {
        }
    }

    private static class TextHandlerTask implements Runnable {
        private final PipedReader reader;

        public TextHandlerTask(PipedReader reader) {
            this.reader = reader;
        }

        @Override
        public void run() {
            L.d(getClass(), "ThreadId: %d", Thread.currentThread().getId());
            while (!Thread.currentThread().isInterrupted()) {
                L.d(getClass(), "[1] ThreadId: %d", Thread.currentThread().getId());

                try {
                    int i;
                    while ((i = reader.read()) != -1) {
                        char c = (char) i;
                        //ADD TEXT PROCESSING LOGIC HERE
                        Log.d(TAG, "char = " + c);
                        L.d(getClass(), "[2] ThreadId: %d", Thread.currentThread().getId());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
