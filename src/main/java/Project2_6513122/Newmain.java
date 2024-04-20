package Project2_6513122;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.io.File;
import java.util.*;
import java.lang.Math;

public class Newmain {
    public static void main(String[] args) {
        String path = "src/main/java/Project2_6513122/";
        String filename = "words_5757.txt";
        Scanner keyboard = new Scanner(System.in);
        WordGraph wg = new WordGraph();
        boolean play = true, init = false, isMenu = true;
        while(play) {
            try {
                Scanner filescan = new Scanner(new File(path+filename));
                ArrayList<String> node = new ArrayList<>();
                while(filescan.hasNext()) {
                    String line = filescan.nextLine();
                    node.add(line);
                }
                if(!init) {
                    System.out.println("... please wait : loading a file...");
                    wg.initialize(node);
                    init = true;
                }

                while (isMenu) {
                    isMenu = false;
                    System.out.println("Enter menu >> (S = search, L = ladder, Q = quit)");
                    String mode = keyboard.next().toLowerCase();

                    switch (mode) {
                        case "s" :
                            wg.search();
                            break;
                        case "l" :
                            wg.ladder();
                            break;
                        case "q" :
                            System.out.println("quit");
                            play = false;
                            break;
                        default :
                            isMenu = true;
                    }
                }


            } catch (Exception e) {
                System.out.println("Enter word file =");
                filename = keyboard.nextLine();
            }
        }
    }
}

class WordGraph {
    private final Graph<String, DefaultWeightedEdge> wordGraph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
    Scanner keyboard = new Scanner(System.in);

    public void initialize(ArrayList<String> node) { // create Graph with vertices and edges
        try {
            Graphs.addAllVertices(wordGraph, node);
            for (String word1 : node) {
                for (String word2 : node) {
                    if (!word1.equals(word2)) {
                        char[] charArray1 = word1.toCharArray();
                        char[] charArray2 = word2.toCharArray();

                        int diffCount = 0;
                        for (int i = 0; i < 5; i++) {
                            if (charArray1[i] != charArray2[i]) {
                                diffCount++;
                            }
                        }

                        if (diffCount == 1) { // Ladder step
                            Graphs.addEdgeWithVertices(wordGraph, word1, word2, Math.abs(word1.compareToIgnoreCase(word2)));
                        } else { // Elevator step
                            int count = 0;
                            Set<Integer> temp1 = new HashSet<>();
                            Set<Integer> temp2 = new HashSet<>();
                            for (int i = 0; i < charArray1.length; i++) {
                                for (int j = 0; j < charArray2.length; j++) {
                                    if (charArray1[i] == charArray2[j] && !temp1.contains(i) && !temp2.contains(j)) {
                                        count++;
                                        temp1.add(i);
                                        temp2.add(j);
                                    }
                                }
                            }
                            if (count == 5) {
                                Graphs.addEdgeWithVertices(wordGraph, word1, word2, 0);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void search() {
        int count = 0;
        System.out.println("Enter keyword to search = ");
        String subword = keyboard.nextLine();
        List<String> validWords = new ArrayList<>();
        for (String word : wordGraph.vertexSet()) {
            if (word.contains(subword)) {
                validWords.add(word);
            }
        }
        System.out.println("========== Available Words containing \"" + subword + "\" : ==========\n");
        for (String word : validWords) {
            System.out.printf("%s     ",word);
            count++;
            if (count == 10) {
                System.out.println();
                count = 0;
            }
        }
        System.out.println("\n");
    }


    public void ladder() {
        try {
            System.out.println("\nEnter 5-letter word1 : ");
            String word1 = keyboard.nextLine();
            System.out.println("Enter 5-letter word2 : ");
            String word2 = keyboard.nextLine();
            while (word1.length() != 5 || word2.length() != 5 || !wordGraph.containsVertex(word1) || !wordGraph.containsVertex(word2)) {
                    System.out.println("\n===== Invalid word please enter 5-letter word again : =====");
                    System.out.println("Enter 5-letter word1 : ");
                    word1 = keyboard.nextLine();
                    System.out.println("Enter 5-letter word2 : ");
                    word2 = keyboard.nextLine();
            }
            ShortestPathAlgorithm<String, DefaultWeightedEdge> shpath = new DijkstraShortestPath<>(wordGraph);
            GraphPath<String, DefaultWeightedEdge> gpath = shpath.getPath(word1, word2);
            if (gpath != null) {
                System.out.println("========== solution from [ "+word1+" ] to [ "+word2+" ] ==========\n");
                printGraphPath(gpath);
            } else {
                System.out.println("No solution way from [ "+word1+" ] to [ "+word2+" ]\n");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void printGraphPath(GraphPath<String, DefaultWeightedEdge> gpath) {
        int sumCost = 0;
        List<String> allnodes = gpath.getVertexList();
        System.out.printf("start => %s\n", allnodes.get(0));
        for (int i = 0; i < allnodes.size()-1; i++) {
            int cost = (int)wordGraph.getEdgeWeight(wordGraph.getEdge(allnodes.get(i), allnodes.get(i+1)));
            sumCost += cost;
            System.out.printf("[ %s     ->     %s ]  ", allnodes.get(i), allnodes.get(i+1));
            if (cost == 0) {
                System.out.printf("====  ( elevator + %d)\n", cost);
            } else {
                System.out.printf("====  ( ladder   + %d)\n", cost);
            }
        }
        System.out.printf("end => %s\n\n", allnodes.get(allnodes.size()-1));
        System.out.println("=== Transformation cost : "+sumCost+" ===\n");
        System.out.println("=================================================================");
    }
}