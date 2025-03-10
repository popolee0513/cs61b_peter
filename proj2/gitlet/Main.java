package gitlet;



/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Peter
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */

    public static void main(String[] args) {
        // TODO: what if args is empty?
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                validateNumArgs("init", args, 1);
                Repository.setupPersistence();
                break;
            case "add":
                validateNumArgs("add", args, 2);
                Repository.addFile(args[1]);
                break;
            case "commit":
                validateNumArgs("commit", args, 2);
                Repository.commit(args[1]);
                break;
            case "checkout":
                if (args.length == 2) {
                    Repository.checkoutBranch(args[1]);
                } else if (args.length == 3) {
                    Repository.checkout(args[2],"");
                } else if (args.length == 4) {
                    Repository.checkout(args[3],args[1]);
                }
                break;

            case "global-log":
                validateNumArgs("global-log", args, 1);
                Repository.globalLog();
                break;
            case "rm":
                validateNumArgs("rm", args, 2);
                Repository.rm(args[1]);
                break;
            case "branch":
                validateNumArgs("branch", args, 2);
                Repository.createBranch(args[1]);
                break;
            case "find":
                validateNumArgs("find", args, 2);
                Repository.find(args[1]);
                break;
            case "rm-branch":
                validateNumArgs("rm-branch", args, 2);
                Repository.rmBranch(args[1]);
                break;
            case "merge":
                break;
            case "reset":
                break;
            case "status":
                break;
            case "log":
                break;

        }
    }

    public static void validateNumArgs(String cmd, String[] args, int n) {
        if (args.length != n) {
            throw new RuntimeException(
                    String.format("Invalid number of arguments for: %s.", cmd));
        }
    }

}
