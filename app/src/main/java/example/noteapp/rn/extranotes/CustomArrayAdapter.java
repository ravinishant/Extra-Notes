package example.noteapp.rn.extranotes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by nishant on 10/27/15.
 */
public class CustomArrayAdapter extends ArrayAdapter<String> {


    private final Context context;
    private final List<String> values;
    private final String viewName;

    public CustomArrayAdapter(Context context, List<String> values, String viewName) {
        super(context, R.layout.list_view_item,values);
        this.context = context;
        this.values = values;
        this.viewName = viewName;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = null;
        switch (viewName)
        {
            case "FolderListView" :
                rowView = CreateFolderListView(inflater, parent, position);
                break;
            case "NotesListView" :
                rowView = CreateNotesListView(inflater, parent, position);
                break;
        }

        return rowView;
    }

    private View CreateNotesListView(LayoutInflater inflater, ViewGroup parent, final int position)
    {
        View rowView = inflater.inflate(R.layout.list_view_item, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.title_name);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.placeholder_icon);
        textView.setText(values.get(position).toString());

        ImageView deleteNotesBtn = (ImageView)rowView.findViewById(R.id.list_add_button);
        deleteNotesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context instanceof NotesListView)
                    ((NotesListView) context).saveData(null, true, position);
            }
        });

        String s = values.get(position).toString();

        System.out.println(s);
//        switch (position % 5)
//        {
//            case 0:
//                imageView.setImageResource(R.drawable.b);
//                break;
//            case 1:
//                imageView.setImageResource(R.drawable.g);
//                break;
//            case 2:
//                imageView.setImageResource(R.drawable.p);
//                break;
//            case 3:
//                imageView.setImageResource(R.drawable.r);
//                break;
//            case 4:
//                imageView.setImageResource(R.drawable.y);
//                break;
//        }
        return rowView;
    }

    private View CreateFolderListView(LayoutInflater inflater, ViewGroup parent, final int position)
    {
        View rowView = inflater.inflate(R.layout.folder_list_view_item, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.folder_name);
        textView.setText(values.get(position).toString());
        ImageView deleteNotesBtn = (ImageView)rowView.findViewById(R.id.folder_del_btn);
        if(values.get(position).equals("All Notes"))
        {
            deleteNotesBtn.setVisibility(View.INVISIBLE);
            return rowView;
        }


        deleteNotesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context instanceof FolderListView)
                    ((FolderListView) context).AddRemoveFolder(null, true, position);
            }
        });
        return rowView;
    }
}
