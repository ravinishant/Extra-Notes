package example.noteapp.rn.extranotes;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Base64InputStream;
import android.util.Base64OutputStream;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class NotesListView extends AppCompatActivity {

    private List<Note> notes = new ArrayList<Note>();
    private String curFolder;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data != null) {
            Serializable extra = data.getSerializableExtra("Note");
            if (extra != null) {
                Note newNote = (Note) extra;
                saveData(newNote, false, -1);
                fetchSavedData(curFolder);
            }
        }
    }

    public void saveData(Note newNote, boolean isDelete, final Integer pos)
    {
        SharedPreferences mPrefs = getSharedPreferences("ListData", MODE_APPEND);

        final SharedPreferences.Editor ed = mPrefs.edit();
        if(isDelete)
        {
            handleDeleteNote(ed,pos);
        }
        else {
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();

            ObjectOutputStream objectOutput;
            try {
                objectOutput = new ObjectOutputStream(arrayOutputStream);
                objectOutput.writeObject(newNote);
                byte[] data = arrayOutputStream.toByteArray();
                objectOutput.close();
                arrayOutputStream.close();

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                Base64OutputStream b64 = new Base64OutputStream(out, Base64.DEFAULT);
                b64.write(data);
                b64.close();
                out.close();

                ed.putString(newNote.getId().toString(), new String(out.toByteArray()));
                ed.commit();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void handleDeleteNote(final SharedPreferences.Editor ed, final Integer pos)
    {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Delete Note")
                .setMessage("Confirm deleting note ?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String key = notes.get(pos).getId().toString();
                        ed.remove(key).commit();
                        fetchSavedData(curFolder);
                        return;
                    }

                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private ListView notesListView;
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_list_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Note List");
        Serializable extra = getIntent().getSerializableExtra("folderName");
        if(extra != null)
            curFolder = (String)extra;
        if(curFolder != null) {
            notesListView = (ListView) findViewById(android.R.id.list);
            notesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent editNoteIntent = new Intent(view.getContext(), NotesEditView2.class);
                    editNoteIntent.putExtra("Note", notes.get(position));
                    editNoteIntent.putExtra("folder", curFolder);
                    startActivityForResult(editNoteIntent, 1);
                }
            });
            registerForContextMenu(notesListView);

            fetchSavedData(curFolder);
        }
    }

    private void fetchSavedData(String folderName)
    {
        notes.clear();
        Note note = null;
        SharedPreferences mPrefs = getSharedPreferences("ListData",MODE_PRIVATE);

        Map<String, ?> all = mPrefs.getAll();
        for (Map.Entry<String, ?> entry: all.entrySet()) {

            ByteArrayInputStream byteArray = new ByteArrayInputStream(entry.getValue().toString().getBytes());
            Base64InputStream base64InputStream = new Base64InputStream(byteArray, Base64.DEFAULT);
            ObjectInputStream in;
            try {
                in = new ObjectInputStream(base64InputStream);
                note = (Note) in.readObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(note.getFolder().toString().equals(curFolder) || curFolder.equals("All Notes"))
                notes.add(note);
        }
        sortArrayList();

        populateList();
    }

    private void sortArrayList()
    {
        Collections.sort(notes, new Comparator<Note>() {
            @Override
            public int compare(Note lhs, Note rhs) {
                if (lhs.getDate() == null || rhs.getDate() == null)
                    return 0;
                return lhs.getDate().compareTo(rhs.getDate());
            }
        });
    }

    private void populateList() {

        List<String> values = new ArrayList<String>();

        for(Note note: notes)
        {
                values.add(note.getTitle().toString());
        }

        ArrayAdapter<String> adapter = new CustomArrayAdapter(this ,values, "NotesListView");
        notesListView.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            case R.id.action_cart:
                Intent editNoteIntent = new Intent(this,NotesEditView2.class);
                editNoteIntent.putExtra("folder", curFolder);
                startActivityForResult(editNoteIntent, 1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_context_menu,menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        saveData(null,true,info.position);
        return true;
    }

}
