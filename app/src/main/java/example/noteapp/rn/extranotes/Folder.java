package example.noteapp.rn.extranotes;

import java.io.Serializable;
import java.util.List;

/**
 * Created by nishant on 11/1/15.
 */
public class Folder implements Serializable {

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public Folder(String folderName) {
        super();
        this.folderName = folderName;
    }

    private String folderName;
}
