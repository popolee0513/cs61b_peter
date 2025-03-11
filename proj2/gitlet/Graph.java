package gitlet;
import java.io.Serializable;
import java.util.*;
import java.io.File;
import static gitlet.Utils.*;


public class Graph implements Serializable {
    public Commit start;
    public HashMap<String, Commit> branchMap;
    Graph(Commit commit){
        this.start = commit;
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
        queue.addLast(start);
        while (!queue.isEmpty()) {
            Commit  node = queue.removeFirst();
            if (sha1(serialize(node)).substring(0, 6).equals(commitID.substring(0, 6))) {
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
        queue.addLast(start);
        boolean flag = false;
        while (!queue.isEmpty()) {
            Commit  node = queue.removeFirst();
            if (node.getMessage().equals(commitMessage)) {
                message(sha1(serialize(node)));
                flag = true;
            }
            for (Commit i : node.neighbors) {
                queue.addLast(i);
            }
        }
        if (!flag) {
            message("Found no commit with that message.");

        }    }
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
        branchMap.remove(branchName);
    }
    public void log(String env) {
        Commit head = this.branchMap.get(env);
        while (head!= null) {
            message("===");
            message("commit " + sha1(serialize(head)));
            message("Date: " + head.formatDate());
            message(head.getMessage());
            System.out.println();
            head = head.parent;
        }
    }
    public ArrayDeque<Commit> getRelation(String env) {
        Commit head = this.branchMap.get(env);
        ArrayDeque<Commit> rel = new ArrayDeque<Commit>();

        while (head!= null) {
            rel.addLast(head);
            head = head.parent;
        }
        return rel;
    }

    public Commit findAncestor(ArrayDeque<Commit> r1,ArrayDeque<Commit> r2) {
        Commit ancestor;
        for (Commit element : r1) {
            if (r2.contains(element)) {
                ancestor = element;
                return ancestor;
            }
        }
        return null;
    }

    public void printGraph() {
          Deque<Commit> queue = new ArrayDeque<>();
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
