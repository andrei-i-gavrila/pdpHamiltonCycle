package ro.codespace.ppd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class HamiltonianCycleFinder {

    private int threadCount;

    public HamiltonianCycleFinder(int threadCount) {
        this.threadCount = threadCount;
    }

    public List<Integer> findCycle(DirectedGraph graph) throws ExecutionException, InterruptedException {
        var executor = Executors.newFixedThreadPool(threadCount);

        var finders = new ArrayList<CompletableFuture<LinkedHashSet<Integer>>>();
        var found = new AtomicBoolean(false);
        for (var node : graph.getNodes()) {
            var future = CompletableFuture.supplyAsync(() -> {

                var stack = new Stack<Integer>();
                stack.push(node);

                var pathStack = new Stack<LinkedHashSet<Integer>>();
                pathStack.add(new LinkedHashSet<>(Collections.singletonList(node)));

                while (!stack.empty()) {
                    if (found.get()) {
                        break;
                    }
                    var currentNode = stack.pop();
                    var currentPath = pathStack.pop();

                    if (currentPath.size() == graph.size() && graph.neighboursOf(currentNode).contains(node)) {
                        found.set(true);
                        return currentPath;
                    }

                    for (var adjacentNode : graph.neighboursOf(currentNode)) {
                        if (!currentPath.contains(adjacentNode)) {
                            stack.push(adjacentNode);
                            var newPath = new LinkedHashSet<>(currentPath);
                            newPath.add(adjacentNode);
                            pathStack.push(newPath);
                        }
                    }
                }
                while (!found.get());
                return null;
            }, executor);

            finders.add(future);
        }

        @SuppressWarnings("unchecked") var foundPath = (LinkedHashSet<Integer>) CompletableFuture.anyOf(finders.toArray(new CompletableFuture[0])).get();
        executor.shutdown();
        return new ArrayList<>(foundPath);
    }
}
