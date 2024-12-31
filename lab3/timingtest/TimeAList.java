package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeAList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeAListConstruction();
    }

    public static void timeAListConstruction() {
        // TODO: YOUR CODE HERE
        AList<Integer> Ns = new AList<Integer>();
        AList<Double> times = new AList<Double>();
        Ns.addLast(1000);
        Ns.addLast(2000);
        Ns.addLast(4000);
        Ns.addLast(8000);
        Ns.addLast(16000);
        Ns.addLast(32000);
        Ns.addLast(64000);
        Ns.addLast(128000);
        AList<Integer> opCounts = Ns;
        for (int i = 0; i < Ns.size(); i += 1){
            double time = trial(Ns.get(i));
            times.addLast(time);
        }
        printTimingTable(Ns , times, opCounts);

    }

    public static double  trial(int input_size){
        Stopwatch sw = new Stopwatch();
        AList<Integer> L = new AList<Integer>();
        for (int i = 0; i < input_size; i++) {
            L.addLast(100);
        }
        double v = sw.elapsedTime();  // Get elapsed time in seconds
        return v;  // Return elapsed time as a double
    }

}
