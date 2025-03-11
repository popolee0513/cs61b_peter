package gitlet;
import java.io.File;
import java.io.Serializable;
import java.util.*;
import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *  @author Peter
 */
public class Repository implements Serializable {

    public static final File CWD = new File(System.getProperty("user.dir"));
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File BlOBS_FOLDER = join(GITLET_DIR,"blobs");
    public static final File COMMITS_FOLDER = join(GITLET_DIR,"commits");
    public static final File STAGING_FOLDER = join(GITLET_DIR,"staging");
    public static final File staging_a = join(STAGING_FOLDER ,"stagingAdd");
    public static final File staging_d = join(STAGING_FOLDER,"stagingDelete");
    public static final File envfile = join(GITLET_DIR,".env");

    public static void setupPersistence() {
        if (GITLET_DIR.exists() && GITLET_DIR.isDirectory()) {
            message("A Gitlet version-control system already exists in the current directory.");
            return ;
        } else {
            GITLET_DIR.mkdir();
            COMMITS_FOLDER.mkdir();
            BlOBS_FOLDER.mkdir();
            STAGING_FOLDER.mkdir();
            Commit firstCommit = new Commit(true,"initial commit");
            writeObject(envfile,"master");
            firstCommit.saveCommit();
            Graph graph = new Graph(firstCommit);
            graph.saveGraph();
        }
    }

    public static void addFile(String fileName) {
        HashMap<String,String> add;
        HashMap<String,String> delete;
        if (!staging_d.exists()){
            delete = new HashMap<String, String>();
            writeObject(staging_d,delete);
        } else {
            delete = readObject(staging_d,HashMap.class);
        }
        if (!staging_a.exists()) {
            add = new HashMap<String, String>();
        } else {
            add =  readObject(staging_a,HashMap.class);
        }
        if (delete.keySet().contains(fileName)){
            delete.remove(fileName);
            writeObject(staging_d,delete);
            return;
        }

        File dogFile = join(CWD, fileName);
        if (!dogFile.exists()) {
            message("File does not exist.");
            return;
        }

        String savedFile = readContentsAsString(dogFile);
        String env = readObject(envfile,String.class);
        String hexString = sha1(readContentsAsString(dogFile));

        // 獲取當前分支的最新提交, 如果內容相同, return
        Graph graph = Graph.getGraph();
        Commit latest = graph.branchMap.get(env);
        if (latest.file !=null) {
            if (latest.file.contains(fileName)) {
                if (latest.fileVersion.get(fileName).equals(hexString)) {
                    return;
                }
            }
        }
        writeContents(join(BlOBS_FOLDER,hexString),savedFile);
        add.put(fileName,hexString);
        writeObject(staging_a,add);
    }
    public static void commit(String message) {
        if (message.equals("")) {
            message("Please enter a commit message.");
            return ;
        }
        String env = readObject(envfile,String.class);
        Commit commit = new Commit(false,message);
        if (!staging_a.exists() && !staging_d.exists()) {
            message("No changes added to the commit.");
            return ;
        }
        HashMap<String,String> add = readObject(staging_a,HashMap.class);
        HashMap<String,String> delete = readObject(staging_d,HashMap.class);
        if (add.isEmpty() && delete.isEmpty()) {
            message("No changes added to the commit.");
            return ;
        }
        // 獲取當前分支的最新提交
        Graph graph = Graph.getGraph();
        Commit latest = graph.branchMap.get(env);
        if (latest.file != null) {
            for (String key : latest.fileVersion.keySet()) {
                if (!add.keySet().contains(key) && !delete.keySet().contains(key)) {
                    add.put(key, latest.fileVersion.get(key));
                }
            }
        }
        for (String key : add.keySet()) {
            commit.addFile(key,add);
        }
        commit.saveCommit();
        graph.addNode(commit,env);
        graph.saveGraph();
        add.clear();
        delete.clear();
        writeObject(staging_a,add);
        writeObject(staging_d,delete);
    }

    public static void checkout(String fileName, String commitID) {
        Graph graph = Graph.getGraph();
        String env = readObject(envfile,String.class);
        Commit target;
        if (commitID.equals("")) {
            target = graph.branchMap.get(env);
        } else {
            target = graph.find(commitID);
        }
        if (target == null) {
            message("No commit with that id exists.");
            return ;
        }

        if (target.file != null) {
            if (!target.file.contains(fileName)) {
                message("File does not exist in that commit.");
                return ;
            }
        } else {
            message("File does not exist in that commit.");
            return;
        }
        String fileVersion = target.fileVersion.get(fileName);
        HashMap<String,String> stagingArea = readObject(staging_a,HashMap.class);
        if (stagingArea.keySet().contains(fileName)) {
            stagingArea.remove(fileName);
        }

        String source = readContentsAsString(join(BlOBS_FOLDER,fileVersion));
        restrictedDelete(fileName);
        File dest = join(CWD,fileName);
        writeContents(dest,source);
    }
    public static void status() {
        Graph graph = Graph.getGraph();
        String env = readObject(envfile,String.class);
        message("=== Branches ===");
        for (String branch : graph.branchMap.keySet()) {
            if (branch.equals("<s>")) {
                continue;
            }
            if (branch.equals(env)) {
                message("*"+branch);
            }   else  {
                message(branch);
            }
        }
        System.out.println();
        message("=== Staged Files ===");
        if (staging_a.exists()) {
            HashMap<String,String> add = readObject(staging_a,HashMap.class);
            for (String key : add.keySet()) {
                message(key);
            }
        }
        System.out.println();
        message("=== Removed Files ===");
        if (staging_d.exists()) {
            HashMap<String,String> delete = readObject(staging_d,HashMap.class);
            for (String key : delete.keySet()) {
                message(key);
            }
        }
        System.out.println();
        message("=== Modifications Not Staged For Commit ===");
        System.out.println();
        message("=== Untracked Files ===");
        System.out.println();

    }
    private static boolean checkUntracked(String fileName) {
        String env = readObject(envfile, String.class);
        Graph graph = Graph.getGraph();
        if (!graph.branchMap.get(env).file.contains(fileName)) {
            HashMap<String, String> add = readObject(staging_a, HashMap.class);
            if (!add.keySet().contains(fileName)) {
                return true;
            }
            return false;
        }
        return false;
    }

    public static void log() {
        String env = readObject(envfile,String.class);
        Graph graph = Graph.getGraph();
        graph.log(env);
    }
    public static void globalLog() {
        Graph graph = Graph.getGraph();
        graph.printGraph();
    }

    public static void rm(String fileName) {
        String env = readObject(envfile,String.class);
        if (!staging_a.exists() &&  !staging_d.exists()){
            message("No reason to remove the file.");
            return;
        }
        HashMap<String,String> add = readObject(staging_a,HashMap.class);
        HashMap<String,String> delete = readObject(staging_d,HashMap.class);
        Graph prevGraph = Graph.getGraph();
        Commit head = prevGraph.branchMap.get(env);
        if (!add.keySet().contains(fileName) && !head.file.contains(fileName)) {
            message("No reason to remove the file.");
        }
        if (add.keySet().contains(fileName)) {
            add.remove(fileName);
            writeObject(staging_a,add);
        }
        if (head.file.contains(fileName)){
            delete.put(fileName,head.fileVersion.get(fileName));
            writeObject(staging_d,delete);
            if (join(CWD,fileName).exists()) {
                restrictedDelete(fileName);
            }
        }
    }
    public static void createBranch(String newBranch){
        String env = readObject(envfile,String.class);
        Graph graph = Graph.getGraph();
        if (graph.branchMap.keySet().contains(newBranch)) {
            message("A branch with that name already exists.");
            return;
        }
        graph.branchMap.put(newBranch,graph.branchMap.get(env));
        graph.saveGraph();
    }
    public static void find(String commitMessage) {
        Graph graph = Graph.getGraph();
        graph.findMessage(commitMessage);
    }
    public static void rmBranch(String branchName) {
        Graph graph = Graph.getGraph();
        String env = readObject(envfile,String.class);
        if (!graph.branchMap.keySet().contains(branchName)) {
            message("A branch with that name does not exist.");
            return ;
        }
        if (branchName.equals(env)){
            message("Cannot remove the current branch.");
            return;
        }
        graph.removeBranch(branchName);
        graph.saveGraph();
    }
    public static void checkoutBranch(String branchName) {
        Graph graph = Graph.getGraph();
        String env = readObject(envfile,String.class);
        if (!graph.branchMap.keySet().contains(branchName)) {
            message("No such branch exists.");
            return ;
        }
        if (branchName.equals(env)){
            message("No need to checkout the current branch.");
            return;
        }
        if (!staging_a.exists() && !staging_d.exists()) {
            writeObject(envfile,branchName);
            return ;
        }
        HashMap<String,String> add = readObject(staging_a,HashMap.class);
        HashMap<String,String> delete = readObject(staging_d,HashMap.class);
        List<String> files = plainFilenamesIn(CWD);
        Commit latest = graph.branchMap.get(env);
        for (String s : files) {
            // check whether there is an untracked file
            if (checkUntracked(s)) {
                message("There is an untracked file in the way; delete it, or add and commit it first.");
                return ;
            }
        }
        for (String s : files) {
            File file = join(CWD,s);
            restrictedDelete(file);
        }
        add.clear();
        delete.clear();
        writeObject(staging_a,add);
        writeObject(staging_d,delete);
        Commit target = graph.branchMap.get(branchName);
        for (String f : target.file) {
            String file = readContentsAsString(join(BlOBS_FOLDER,target.fileVersion.get(f)));
            writeContents(join(CWD,f),file);
        }
        writeObject(envfile,branchName);
    }
    public static void reset(String commitID){
        Graph graph = Graph.getGraph();
        Commit node = graph.find(commitID);
        if (node == null) {
            message("No commit with that id exists.");
            return ;
        }
        List<String> files = plainFilenamesIn(CWD);
        String env = readObject(envfile,String.class);
        Commit latest = graph.branchMap.get(env);
        for (String s : files) {
            // check whether there is an untracked file
            if (checkUntracked(s)) {
                message("There is an untracked file in the way; delete it, or add and commit it first.");
                return ;
            }
        }
        graph.branchMap.put(env,node);
        graph.saveGraph();
        HashMap<String,String> add = readObject(staging_a,HashMap.class);
        HashMap<String,String> delete = readObject(staging_d,HashMap.class);
        add.clear();
        delete.clear();
        writeObject(staging_a,add);
        writeObject(staging_d,delete);
    }
}

