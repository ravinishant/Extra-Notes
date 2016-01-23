package example.noteapp.rn.extranotes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Base64InputStream;
import android.util.Base64OutputStream;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FolderListView extends AppCompatActivity {

    private ListView folderListView;
    private List<Folder> folders = new ArrayList<Folder>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_list_view);
        setTitle("Folders");
        getSupportActionBar().setIcon(R.drawable.notebook71);
        folders.add(new Folder("All Notes"));
        folderListView = (ListView) findViewById(android.R.id.list);
        folderListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent listNoteIntent = new Intent(view.getContext(), NotesListView.class);
                listNoteIntent.putExtra("folderName", folders.get(position).getFolderName().toString());
                startActivity(listNoteIntent);
            }

        });
        registerForContextMenu(folderListView);
        fetchFolders();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.folder_add_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        builder
                .setTitle("Folder Name")
                .setMessage("Enter the folder to create")
                .setView(input)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        String value = input.getText().toString();
                        if (input.getText().toString().trim().length() == 0) {
                            Toast.makeText(FolderListView.this, "Please enter a valid folder name", Toast.LENGTH_SHORT).show();
                        } else {
                            AddRemoveFolder(new Folder(value), false, -1);
                        }
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                    }

                });

        builder.show();
        input.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        return true;
    }

    public void AddRemoveFolder(Folder folder, boolean isDelete, Integer pos)
    {
        SharedPreferences mPrefs = getSharedPreferences("FolderData1", MODE_APPEND);
        final SharedPreferences.Editor ed = mPrefs.edit();
        if(isDelete) {
            handleDeleteFolder(ed,pos);
        }
        else {
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();

            ObjectOutputStream objectOutput;
            try {
                objectOutput = new ObjectOutputStream(arrayOutputStream);
                objectOutput.writeObject(folder);
                byte[] data = arrayOutputStream.toByteArray();
                objectOutput.close();
                arrayOutputStream.close();

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                Base64OutputStream b64 = new Base64OutputStream(out, Base64.DEFAULT);
                b64.write(data);
                b64.close();
                out.close();

                ed.putString(folder.getFolderName().toString(), new String(out.toByteArray()));
                ed.commit();
            } catch (IOException e) {
                e.printStackTrace();
            }
            fetchFolders();
        }
    }

    private void handleDeleteFolder(final SharedPreferences.Editor ed, final Integer pos)
    {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Delete Folder")
                .setMessage("Confirm deleting folder ?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String key = folders.get(pos).getFolderName().toString();
                        ed.remove(key).commit();
                        fetchFolders();
                        return;
                    }

                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void fetchFolders()
    {
        folders.clear();
        folders.add(new Folder("All Notes"));
        Folder folder = null;
        SharedPreferences mPrefs = getSharedPreferences("FolderData1",MODE_PRIVATE);

        Map<String, ?> all = mPrefs.getAll();
        for (Map.Entry<String, ?> entry: all.entrySet()) {

            ByteArrayInputStream byteArray = new ByteArrayInputStream(entry.getValue().toString().getBytes());
            Base64InputStream base64InputStream = new Base64InputStream(byteArray, Base64.DEFAULT);
            ObjectInputStream in;
            try {
                in = new ObjectInputStream(base64InputStream);
                folder = (Folder) in.readObject();
            } catch (Exception e) {
                e.printStackTrace();
            }

            folders.add(folder);
        }
//        sortArrayList();

        populateList();
    }

    private void populateList() {

        List<String> values = new ArrayList<String>();
        if(folders.size() < 1)
            return;
        for(Folder folder: folders)
        {
            values.add(folder.getFolderName().toString());
        }

        ArrayAdapter<String> adapter = new CustomArrayAdapter(this ,values, "FolderListView");
        folderListView.setAdapter(adapter);

    }
}
