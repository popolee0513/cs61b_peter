package gitlet;



/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Peter
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */

    public static void main(String[] args) {
        if (args.length ==0){
            System.out.println("Please enter a command.");
            return;
        }

        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                validateNumArgs("init", args, 1);
                Repository.setupPersistence();
                break;
            case "add":
                validateNumArgs("add", args, 2);
                if (!checkInit()) {break;};
                Repository.addFile(args[1]);
                break;
            case "commit":
                validateNumArgs("commit", args, 2);
                if (!checkInit()) {break;};
                Repository.commit(args[1]);
                break;
            case "checkout":
                if (args.length == 2) {
                    if (!checkInit()) {break;};
                    Repository.checkoutBranch(args[1]);
                } else if (args.length == 3) {
                    if (!checkInit()) {break;};
                    Repository.checkout(args[2],"");
                } else if (args.length == 4) {
                    if (!checkInit()) {break;};
                    if (!args[2].equals("--")) {
                        System.out.println("Incorrect operands.");
                    }
                    Repository.checkout(args[3],args[1]);
                }
                break;
            case "global-log":
                validateNumArgs("global-log", args, 1);
                if (!checkInit()) {break;};
                Repository.globalLog();
                break;
            case "rm":
                validateNumArgs("rm", args, 2);
                if (!checkInit()) {break;};
                Repository.rm(args[1]);
                break;
            case "branch":
                validateNumArgs("branch", args, 2);
                if (!checkInit()) {break;};
                Repository.createBranch(args[1]);
                break;
            case "find":
                validateNumArgs("find", args, 2);
                if (!checkInit()) {break;};
                Repository.find(args[1]);
                break;
            case "rm-branch":
                validateNumArgs("rm-branch", args, 2);
                if (!checkInit()) {break;};
                Repository.rmBranch(args[1]);
                break;
            case "merge":
                if (!checkInit()) {break;};
                break;
            case "reset":
                validateNumArgs("reset", args, 2);
                if (!checkInit()) {break;};
                Repository.reset(args[1]);
                break;
            case "status":
                validateNumArgs("status", args, 1);
                if (!checkInit()) {break;};
                Repository.status();
                break;
            case "log":
                validateNumArgs("log", args, 1);
                if (!checkInit()) {break;};
                Repository.log();
                break;
            default:
                System.out.println("No command with that name exists.");
                break;
        }
    }
    public static boolean  checkInit(){
        if (!Repository.GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            return false;
        }
        return true;
    }

    public static void validateNumArgs(String cmd, String[] args, int n) {
        if (args.length != n) {
            throw new RuntimeException(
                    String.format("Invalid number of arguments for: %s.", cmd));
        }
    }

}
