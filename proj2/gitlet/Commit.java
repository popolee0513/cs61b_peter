package gitlet;
import java.io.File;
import static gitlet.Utils.*;
import static gitlet.Utils.sha1;

import java.io.Serializable;
import java.util.*;
import java.text.SimpleDateFormat;

public class Commit implements Serializable {

    private final String messages;
    private Date date;
    public ArrayList<String> file;
    public  HashMap<String, String> fileVersion;
    public Commit parent;
    public ArrayList<Commit> neighbors;
    public Commit merged;
    public String branch;
    public String commitID;

    public Commit(boolean isfirst, String messages) {
        if (isfirst) {
            this.date = new Date(0);
            this.branch = "master";
        }
        else {
            this.date = new Date();
        }
        this.messages = messages;
        this.file = new ArrayList<>();
        this.fileVersion = new HashMap<>();
        this.neighbors = new ArrayList<>();
        this.commitID = sha1(serialize(this));
    }
    public String getMessage() {
        return this.messages;
    }
    public String formatDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-8"));
        return sdf.format(this.date);
    }
    public void addFile(String fileName, HashMap<String,String> versionDict) {
        file.add(fileName);
        fileVersion.put(fileName,versionDict.get(fileName));
    }

    public void saveCommit() {
        File savePath = join(Repository.COMMITS_FOLDER,this.commitID );
        writeObject(savePath,this);
    }
}
