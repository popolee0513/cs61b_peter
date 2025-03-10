package gitlet;
import java.io.Serializable;
import java.util.*;
import java.io.File;
import static gitlet.Utils.*;


public class Graph implements Serializable {
    public Commit start;
    public HashMap<String, Commit> branchMap;
    Graph(Commit commit){
        this.start =commit;
        this.branchMap = new HashMap<>();
        this.branchMap.put("<s>",this.start);
        this.branchMap.put("master",this.start);
    }
    public static Graph getGraph() {
        File dogFile =  join(Repository.GITLET_DIR,"graph");
        return readObject(dogFile , Graph.class);
    }
    public void saveGraph() {
        File graphfile = join(Repository.GITLET_DIR,"graph");
        writeObject(graphfile,this);
    }

    public void addNode(Commit commit,String branchName) {
           Commit head = branchMap.get(branchName);
           commit.branch = branchName;
           head.neighbors.add(commit);
           commit.parent = head;
           branchMap.put(branchName,commit);
    }

    public Commit find(String commitID) {
        Deque<Commit> queue = new ArrayDeque<>();
        Commit start = this.branchMap.get("<s>");
        queue.addLast(start);
        while (!queue.isEmpty()) {
            Commit  node = queue.removeFirst();
            if (sha1(serialize(node)).equals(commitID)) {
                return node;
            }
            System.out.println();
            for (Commit i : node.neighbors) {
                queue.addLast(i);
            }
        }
        return null;
    }

    public void findMessage(String commitMessage) {
        Deque<Commit> queue = new ArrayDeque<>();
        Commit start = this.branchMap.get("<s>");
        queue.addLast(start);
        while (!queue.isEmpty()) {
            Commit  node = queue.removeFirst();
            if (node.getMessage().equals(commitMessage)) {
                message(sha1(serialize(node)));
            }
            System.out.println();
            for (Commit i : node.neighbors) {
                queue.addLast(i);
            }
        }
    }
    public void removeBranch(String branchName) {
        Commit head = this.branchMap.get(branchName);
        while (head.branch.equals(branchName)) {
            head = head.parent;
        }
        for (Commit i : head.neighbors) {
            if (i.branch.equals(branchName)) {
                head.neighbors.remove(i);
                break;
            }
        }
    }

    public void printGraph() {
          Deque<Commit> queue = new ArrayDeque<>();
          Commit start = this.branchMap.get("<s>");
          queue.addLast(start);
          while (!queue.isEmpty()) {
                 Commit  node = queue.removeFirst();
                 message("===");
                 message("commit " + sha1(serialize(node)));
                 message("Date: " + node.formatDate());
                 message(node.getMessage());
                 System.out.println();
                 for (Commit i : node.neighbors) {
                     queue.addLast(i);
                 }
          }

    }


}
