package example.noteapp.rn.extranotes;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

public class NotesEditView2 extends AppCompatActivity {

    private boolean isEditMode = true;
    private EditText titleText;
    private EditText contentText;
    private EditText dateText;
    private String folderName;
    private UUID curId;
    public static class LineEditText extends EditText {

        private Rect mRect;
        private Paint mPaint;

        public LineEditText(Context context, AttributeSet attrs) {
            super(context, attrs);
            mRect = new Rect();
            mPaint = new Paint();
            mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            mPaint.setColor(Color.BLUE);
        }

        @Override
        protected void onDraw(Canvas canvas) {

            int height = getHeight();
            int line_height = getLineHeight();

            int count = height / line_height;

            if (getLineCount() > count)
                count = getLineCount();

            Rect r = mRect;
            Paint paint = mPaint;
            paint.setColor(Color.DKGRAY);
            paint.setStrokeWidth(2);

            int baseline = getLineBounds(0, r);

            for (int i = 0; i < count; i++) {

                canvas.drawLine(r.left, baseline + 1, r.right, baseline + 1, paint);
                baseline += getLineHeight();

                super.onDraw(canvas);
            }

        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            case R.id.save_action_bar:
                this.saveNote();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_save, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_edit_view2);
        setTitle("Edit Note");
         titleText = (EditText)findViewById(R.id.titleText);
         contentText = (EditText)findViewById(R.id.contentText);
         dateText = (EditText)findViewById(R.id.dateText);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Serializable extra = getIntent().getSerializableExtra("Note");
        Serializable extraFolderName = getIntent().getSerializableExtra("folder");
        if(extraFolderName != null)
            folderName = (String)extraFolderName;
        else
            folderName = "";
        if(extra != null && extraFolderName != null)
        {
            isEditMode = true;
            Note newNote = (Note)extra;
            titleText.setText(newNote.getTitle());
            contentText.setText(newNote.getNote());
            curId = newNote.getId();

            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String date = dateFormat.format(newNote.getDate());
            dateText.setText(date);
        }
        else
        {
            isEditMode = false;
            curId = UUID.randomUUID();
        }

    }

    private void saveNote() {
        if (titleText.getText().toString().trim().length() == 0) {
            Toast.makeText(NotesEditView2.this, "Please enter notes title", Toast.LENGTH_SHORT).show();
            return;
        }
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String date = dateFormat.format(Calendar.getInstance().getTime());
        dateText.setText(date);

        Intent returnIntent = new Intent();
        Note note = new Note(curId,titleText.getText().toString(), contentText.getText().toString(), Calendar.getInstance().getTime(),folderName);
        returnIntent.putExtra("Note", note);
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}
