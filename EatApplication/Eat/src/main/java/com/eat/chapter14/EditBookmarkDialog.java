package com.eat.chapter14;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.eat.L;
import com.eat.R;

public class EditBookmarkDialog extends DialogFragment {

    static EditBookmarkDialog newInstance(ChromeBookmarkActivity.ChromeBookmarkAsyncHandler asyncQueryHandler) {
        L.d("[EditBookmarkDialog] ThreadId: %d", Thread.currentThread().getId());
        EditBookmarkDialog dialog = new EditBookmarkDialog(asyncQueryHandler);
        return dialog;
    }

    ChromeBookmarkActivity.ChromeBookmarkAsyncHandler mAsyncQueryHandler;

    public EditBookmarkDialog(ChromeBookmarkActivity.ChromeBookmarkAsyncHandler asyncQueryHandler) {
        mAsyncQueryHandler = asyncQueryHandler;
        L.d(getClass(), "ThreadId: %d", Thread.currentThread().getId());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        L.d(getClass(), "ThreadId: %d", Thread.currentThread().getId());
        View v = inflater.inflate(R.layout.dialog_edit_bookmark, container, false);
        final EditText editName = (EditText) v.findViewById(R.id.edit_name);
        final EditText editUrl = (EditText) v.findViewById(R.id.edit_url);
        Button buttonSave = (Button) v.findViewById(R.id.button_save);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String name = editName.getText().toString();
                String url = editUrl.getText().toString();
                mAsyncQueryHandler.insert(name, url);
                dismiss();
            }
        });

        return v;
    }


}
