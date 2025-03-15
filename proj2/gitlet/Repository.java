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
        message("*"+env);
        for (String branch : graph.branchMap.keySet()) {
            if (branch.equals("<s>")) {
                continue;
            }
            if (branch.equals(env)) {
                continue;
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
        HashMap<String, String> delete = readObject(staging_d, HashMap.class);
        if (delete.keySet().contains(fileName)) {
           if (join(CWD,fileName).exists()) {
               return true;
           }
        }
        //未被追蹤
        if (!graph.branchMap.get(env).file.contains(fileName)) {
            HashMap<String, String> add = readObject(staging_a, HashMap.class);
            // 未被暫存
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
        Commit target = graph.branchMap.get(branchName);
        for (String s : files) {
            // check whether there is an untracked file
            if (checkUntracked(s) && target.file.contains(s)) {
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
    public static void merge(String branchName) {
        String env = readObject(envfile,String.class);
        HashMap<String,String> add = readObject(staging_a,HashMap.class);
        Graph graph = Graph.getGraph();
        HashMap<String,String> delete = readObject(staging_d,HashMap.class);
        if (env.equals(branchName)) {
            message("Cannot merge a branch with itself.");
            return;
        }
        if (!graph.branchMap.keySet().contains(branchName)) {
            message("A branch with that name does not exist.");
            return;
        }
        if (add.isEmpty() == false || delete.isEmpty() == false) {
            message("You have uncommitted changes.");
            return;
        }
        List<String> filesCWD = plainFilenamesIn(CWD);
        for (String s : filesCWD) {
            if (checkUntracked(s)) {
                message("There is an untracked file in the way; delete it, or add and commit it first.");
                return ;
            }
        }
        Commit ancestor = graph.findAncestor(graph.getRelation(env),graph.getRelation(branchName)) ;
        if ( sha1(serialize(ancestor)).equals(sha1(serialize(graph.branchMap.get(branchName))))
        ) {
            message("Given branch is an ancestor of the current branch.");
            return;
        }

        if (sha1(serialize(ancestor)).equals(sha1(serialize(graph.branchMap.get(env))))) {
            message("Current branch fast-forwarded.");
            graph.branchMap.put(env,graph.branchMap.get(branchName));
            checkoutBranch(branchName);
            return ;
        }
        Commit current = graph.branchMap.get(env);
        Commit merged = graph.branchMap.get(branchName);
        for (String file : ancestor.file) {
            if (current.file.contains(file)) {
                if (!merged.file.contains(file) &&
                        current.fileVersion.get(file).equals(ancestor.fileVersion.get(file))) {
                    restrictedDelete(file);
                    delete.put(file,current.fileVersion.get(file));
                }
                if (!merged.file.contains(file) &&
                        !current.fileVersion.get(file).equals(ancestor.fileVersion.get(file))) {
                    message("Encountered a merge conflict.");
                    String curFile = readContentsAsString(join(BlOBS_FOLDER,current.fileVersion.get(file)));
                    String out = "<<<<<<< HEAD\n"
                                  + curFile
                                  + "=======\n"
                                  + ">>>>>>>\n";
                    writeContents(join(BlOBS_FOLDER,sha1(serialize(out))),out);
                    writeContents(join(CWD,file),out);
                    add.put(file,sha1(serialize(out)));
                }
            }
            if (merged.file.contains(file)) {
                if (!current.file.contains(file) &&
                        !merged.fileVersion.get(file).equals(ancestor.fileVersion.get(file))) {
                    message("Encountered a merge conflict.");
                    String mergedFile = readContentsAsString(join(BlOBS_FOLDER,merged.fileVersion.get(file)));
                    String out = "<<<<<<< HEAD\n"
                                 + "=======\n"
                                 + mergedFile
                                 + ">>>>>>>\n";
                    writeContents(join(BlOBS_FOLDER,sha1(serialize(out))),out);
                    writeContents(join(CWD,file),out);
                    add.put(file,sha1(serialize(out)));
                }
            }

            if (current.file.contains(file) && merged.file.contains(file)) {
                // 兩分支相同方式修改的檔案
                if (current.fileVersion.get(file).equals(merged.fileVersion.get(file))) {
                    continue;
                }
                //給定分支修改過的檔案（未修改於當前分支）
                else if (!merged.fileVersion.get(file).equals(ancestor.fileVersion.get(file))
                        && current.fileVersion.get(file).equals(ancestor.fileVersion.get(file))) {
                    add.put(file,merged.fileVersion.get(file));
                    String c = readContentsAsString(join(BlOBS_FOLDER,merged.fileVersion.get(file)));
                    writeContents(join(CWD,file),c);
                }
                //當前分支修改過的檔案（未修改於給定分支)
                else if (merged.fileVersion.get(file).equals(ancestor.fileVersion.get(file))
                        && !current.fileVersion.get(file).equals(ancestor.fileVersion.get(file))) {
                    continue;
                }
                else if (!current.fileVersion.get(file).equals(merged.fileVersion.get(file))) {
                    message("Encountered a merge conflict.");
                    String curFile = readContentsAsString(join(BlOBS_FOLDER,current.fileVersion.get(file)));
                    String mergeFile = readContentsAsString(join(BlOBS_FOLDER,merged.fileVersion.get(file)));
                    String out = "<<<<<<< HEAD\n"
                                 + curFile
                                 + "=======\n"
                                 + mergeFile
                                 + ">>>>>>>\n";
                    writeContents(join(BlOBS_FOLDER,sha1(serialize(out))),out);
                    writeContents(join(CWD,file),out);
                    add.put(file,sha1(serialize(out)));
                }
            }
        }
        //僅在當前分支存在的檔案
        //for (String file : current.file) {
        //if (!ancestor.file.contains(file) && !merged.file.contains(file)){
        //
        //    }
        //}
        // 僅在給定分支存在的檔案
        for (String file : merged.file) {
            if (!ancestor.file.contains(file) && !current.file.contains(file)){
                add.put(file,merged.fileVersion.get(file));
                String c = readContentsAsString(join(BlOBS_FOLDER,merged.fileVersion.get(file)));
                writeContents(join(CWD,file),c);
            }
        }
        writeObject(staging_a,add);
        writeObject(staging_d,delete);
        commit("Merged "+ branchName +" into " + env + ".");
    }

}

