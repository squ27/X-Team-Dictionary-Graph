import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Junit test class to test class GraphProcessor and WordProcessor
 */

public class GraphProcessorTest {
    
    private GraphProcessor g;
    private static File tempFile; // Temporary file to be created
    private static String fileName; // The path name of the temporary file created
    
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        try {
            tempFile = File.createTempFile("x67",".txt"); // Creates the temporary file
            fileName = tempFile.getCanonicalPath(); // Sets fileName to the path of the temp file
        }
        catch (IOException e) {
            
        }
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }
    
    /**
     * Takes a string of words and writes each word to a line of a temporary file.
     * 
     * @param s - A string of words separated by commas, each word to be written to a line
     */
    private void testFile(String s) {
        String strings[] = s.split(",");
        try {
            PrintStream f = new PrintStream(tempFile);
            for (int i = 0; i < strings.length; i++) {
                f.println(strings[i]);
            }
        }
        catch (FileNotFoundException e ) {
        }
        catch (IOException e) {    
        }
    }
    
    @Test
    /**
     * Tests to make sure that the shortest path using getShortestPath 
     * in GraphProcessor between two words that are the same is null
     */
    public final void test_1_shortestPathOfZero() {
        testFile("cat");
        GraphProcessor g = new GraphProcessor();
        g.populateGraph(fileName);
        boolean flag = false;
        List<String> s = g.getShortestPath("cat", "cat");
        flag = (s.size() != 0);
        if (flag) fail("Expected empty list, got " + s);
    }
    
    @Test
    /**
     * Tests to make sure that the shortest distance using the getShortestDistance test 
     * in GraphProcessor between two words that are the same is -1
     */
    public final void test_2_shortestDistanceOfZero() {
        testFile("cat,hat,hate,hater,hit,car");
        GraphProcessor g = new GraphProcessor();
        g.populateGraph(fileName);
        boolean flag = false;
        int n = g.getShortestDistance("cat","cat");
        flag = (n!=-1); 
        if (flag) fail("Expected -1, got " + n );
    }
    
    @Test
    public final void test_3_populateGraphOfZero() {
        GraphProcessor g = new GraphProcessor();
        boolean flag = false;
        Integer i = g.populateGraph(null);
        flag = (i != -1);
        if (flag) fail("Expected -1, got " + i);
    }
    
    @Test
    public final void test_4_populateGraphOfOne() {
        testFile("cat");
        GraphProcessor g = new GraphProcessor();
        boolean flag = false;
        int n = g.populateGraph(fileName);
        flag = (n!=1);
        if (flag) fail("Expected 1, got " + n );
    }
    
    @Test
    public final void test_5_populateGraphOfThree() {
        testFile("cat,hat,hate");
        GraphProcessor g = new GraphProcessor();
        boolean flag = false;
        int n = g.populateGraph(fileName);
        flag = (n!=3);
        if (flag) fail("Expected 3, got " + n );
    }
    
    @Test
    public final void test_6_populateGraphOfTen() {
        testFile("cat,hat,hate,word,aa,BB,left,right,up,down");
        GraphProcessor g = new GraphProcessor();
        boolean flag = false;
        int n = g.populateGraph(fileName);
        flag = (n!=10);
        if (flag) fail("Expected 10, got " + n );
    }
    
    @Test
    /**
     * Tests to make sure that the shortest distance using the getShortestDistance test 
     * in GraphProcessor between two words that are one change away from each other is 1
     */
    public final void test_7_shortestDistanceOfOne() {
        testFile("cat,hat,hate,hater,hit,car");
        GraphProcessor g = new GraphProcessor();
        g.populateGraph(fileName);
        boolean flag = false;
        int n = g.getShortestDistance("cat","hat");
        flag = (n!=1);
        if (flag) fail("Expected 1, got " + n );
    }
    
    @Test
    /**
     * Tests to make sure that the shortest distance using the getShortestDistance test 
     * in GraphProcessor between two words that are two changes away from each other is 2
     */
    public void test_8_shortestDistanceOfTwo() {
        testFile("cat,hat,hate,hater,hit,car");
        GraphProcessor g = new GraphProcessor();
        g.populateGraph(fileName);
        boolean flag = false;
        int n = g.getShortestDistance("cat","hate");
        flag = (n!=2);
        if (flag) fail("Expected 2, got " + n );
    }
    
    @Test
    /**
     * Tests to make sure that the shortest distance using the getShortestDistance test 
     * in GraphProcessor between two words that are three changes away from each other is 3
     */
    public void test_9_shortestDistanceOfThree() {
        testFile("cat,hat,hate,hater,hit,car");
        GraphProcessor g = new GraphProcessor();
        g.populateGraph(fileName);
        boolean flag = false;
        int n = g.getShortestDistance("cat","hater");
        flag = (n!=3);
        if (flag) fail("Expected 3, got " + n );
    }
    
    @Test
    /**
     * Tests to make sure that the shortest path using the getShortestPath test 
     * in GraphProcessor between two words that are one change away returns the correct list 
     * with two words
     */
    public void test_11_shortestPathOfOne() {
        testFile("cat,hat,hate,hater,hit,car");
        GraphProcessor g = new GraphProcessor();
        g.populateGraph(fileName);
        boolean flag = false;
        List<String> p = g.getShortestPath("cat", "hat");
        flag = flag || (p.size() != 2);
        flag = flag || (!p.get(0).equals("CAT"));
        flag = flag || (!p.get(1).equals("HAT"));
        if (flag) fail("Expected [ CAT,HAT ], got [ " + String.join(",", p) + " ]");
    }
    
    @Test
    /**
     * Tests to make sure that the shortest path using the getShortestPath test 
     * in GraphProcessor between two words that are two changes away returns the correct list 
     * with three words
     */
    public void test_12_shortestPathOfTwo() {
        testFile("cat,hat,hate,hater,hit,car");
        GraphProcessor g = new GraphProcessor();
        g.populateGraph(fileName);
        boolean flag = false;
        List<String> p = g.getShortestPath("cat", "hate");
        flag = flag || (p.size() != 3);
        flag = flag || (!p.get(0).equals("CAT"));
        flag = flag || (!p.get(1).equals("HAT"));
        flag = flag || (!p.get(2).equals("HATE"));
        if (flag) fail("Expected [ CAT,HAT,HATE ], got [ " 
        + String.join(",", p) + " ]");
    }
    
    @Test
    /**
     * Tests to make sure that the shortest path using the getShortestPath test 
     * in GraphProcessor between two words that are three changes away returns the correct list 
     * with four words
     */
    public void test_13_shortestPathOfThree() {
        testFile("cat,hat,hate,hater,hit,car");
        GraphProcessor g = new GraphProcessor();
        g.populateGraph(fileName);
        boolean flag = false;
        List<String> p = g.getShortestPath("cat", "hater");
        flag = flag || (p.size() != 4);
        flag = flag || (!p.get(0).equals("CAT"));
        flag = flag || (!p.get(1).equals("HAT"));
        flag = flag || (!p.get(2).equals("HATE"));
        flag = flag || (!p.get(3).equals("HATER"));
        if (flag) fail("Expected [ CAT,HAT,HATE,HATER ], got [ " 
        + String.join(",", p) + " ]");
    }
    
    @Test
    /**
     * Tests to make sure that the shortest path using the getShortestPath test 
     * in GraphProcessor between two words that have no adjacency returns a -1.
     */
    public void test_14_pathWithNoPath() {
        testFile("apple,octopus,drag");
        GraphProcessor g = new GraphProcessor();
        g.populateGraph(fileName);
        boolean flag = false;
        Integer i = g.getShortestDistance("apple", "octopus");
        flag = (i != -1);
        if (flag) fail("Expected -1, got " + i);
    }
    
    @Test
    /**
     * Tests that two words that are not adjacent do not return as adjacent.
     */
    public void test_15_isAdjacentFalse() {
        WordProcessor g = new WordProcessor();
        boolean flag = false;
        flag = g.isAdjacent("cat", "dog");
        if (flag) fail("Expected: false Got:" + flag);
    }
    
    @Test
    /**
     * Tests that two words that are adjacent, the later with one subtraction,
     * are still considered adjacent.
     */
    public void test_16_isAdjacentReplacement() {
        
    }
    
    @Test
    /**
     * Tests that two words that are adjacent, the later with one addition,
     * are still considered adjacent.
     */
    public void test_17_isAdjacentAddition() {
        
    }
    
    @Test
    /**
     * Tests that two words that are adjacent, the later with one subtraction,
     * are still considered adjacent.
     */
    public void test_18_isAdjacentSubtraction() {
        
    }
    
    @Test
    // gives 5 words and makes sure they come out in the right order
    public void test_18_wordProcessorTest() {
        try {
            testFile("cat,hat,hate,hit,car");
            Stream<String> w = WordProcessor.getWordStream(fileName);
            //List<String> a = w.collect(Collectors::toList);
            boolean flag = false;
        }
        catch (IOException e) {
        }   
    }   
}
