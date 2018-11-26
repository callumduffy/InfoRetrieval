package ie.tcd.irws.searchengine;

//import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import ie.tcd.irws.searchengine.handlers.QueryHandler;
import ie.tcd.irws.searchengine.parsers.QueryParser;
import ie.tcd.irws.searchengine.Utils;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import static ie.tcd.irws.searchengine.Utils.getPhrases;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }


    /**
     * getPhrases TESTS
     */
    public void testGetPhrases() {
        String desc = "What is the impact of poaching on the world's various wildlife preserves?";
        ArrayList<ArrayList<String>> phrases = getPhrases(desc);
        System.out.println(phrases.toString());
        assertTrue(phrases.toString().equals("[[What, is], [is, the], [the, impact], [impact, of], [of, poaching], [poaching, on], [on, the], [the, worlds], [worlds, various], [various, wildlife], [wildlife, preserves]]"));
    }
}
