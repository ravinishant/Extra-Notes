package example.noteapp.rn.extranotes;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * Created by nishant on 10/26/15.
 */
public class Note implements Serializable {

    public Note(UUID uuid,String title, String note, Date date, String folderName) {
        super();
        this.Id = uuid;
        this.title = title;
        this.note = note;
        this.date = date;
        if(folderName != null)
            this.folder = folderName;
        else
            this.folder = "All Notes";
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public UUID getId() {
        return Id;
    }

    public void setId(UUID id) {
        Id = id;
    }

    private UUID Id;
    private String title;
    private String note;
    private Date date;
    private String folder;
}


