package ro.codespace.ppd;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class Main {

    public static void main(String... args) throws ExecutionException, InterruptedException {
        var graph = DirectedGraph.generateRandomHamiltonian(100, 101912);


        var seqFinder = new HamiltonianCycleFinder(1);
        var seqStartTime = System.currentTimeMillis();
        var seqCycle = seqFinder.findCycle(graph);
        var seqTime = System.currentTimeMillis() - seqStartTime;
        System.out.println("Sequential took " + seqTime + "ms");


        var parFinder = new HamiltonianCycleFinder(4);
        var parStartTime = System.currentTimeMillis();
        var parCycle = parFinder.findCycle(graph);
        var parTime = System.currentTimeMillis() - parStartTime;
        System.out.println("Parallel took " + parTime + "ms");
    }
}
