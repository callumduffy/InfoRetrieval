package ie.tcd.irws.searchengine;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import ie.tcd.irws.searchengine.handlers.QueryHandler;
import ie.tcd.irws.searchengine.parsers.QueryParser;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

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
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }


    /**
     * getIrrelevantTerms TESTS
     */
    public ArrayList<ArrayList<String>> getRelevantTermsHelper(String narr) throws IOException {
        ArrayList<ArrayList<String>> result = Utils.getRelevantTerms(narr);
        System.out.println();
        System.out.println("NARR:\n" + narr);
        System.out.println("RELEVANT TERMS: \n" + result.get(0).toString());
        System.out.println("IRRELEVANT TERMS: \n" + result.get(1).toString());
        return result;
    }

    /**
     * Close..
     */
    public void testGetRelevantTerms401() throws IOException {
        String narr = "A relevant document will focus on the causes of the lack of\n" +
                "integration in a significant way; that is, the mere mention of\n" +
                "immigration difficulties is not relevant.  Documents that discuss\n" +
                "immigration problems unrelated to Germany are also not relevant.";
        ArrayList<ArrayList<String>> result = getRelevantTermsHelper(narr);
        // The term "Germany" MUST occur
        assertTrue(result.get(0).toString().equals("[causes, lack, integration, immigration, problems, germany]"));
        assertTrue(result.get(1).toString().equals("[difficulties]"));
    }

    public void testGetRelevantTerms402() throws IOException {
        String narr = "Documents describing genetic or environmental factors relating\n" +
                "to understanding and preventing substance abuse and addictions\n" +
                "are relevant.  Documents pertaining to attention deficit disorders\n" +
                "tied in with genetics are also relevant, as are genetic disorders \n" +
                "affecting hearing or muscles.  The genome project is relevant\n" +
                "when tied in with behavior disorders (i.e., mood disorders,\n" +
                "Alzheimer's disease). ";
        ArrayList<ArrayList<String>> result = getRelevantTermsHelper(narr);
        assertTrue(result.get(0).toString().equals("[genetic, environmental, factors, understanding, preventing, substance, abuse, addictions, pertaining, attention, deficit, disorders, tied, genetics, affecting, hearing, muscles, genome, project, behavior, mood, alzheimer's, disease]"));
        assertTrue(result.get(1).toString().equals("[]"));
    }

    public void testGetRelevantTerms403() throws IOException {
        String narr = "A relevant document may include one or more of the\n" +
                "dietary intakes in the prevention of osteoporosis.\n" +
                "Any discussion of the disturbance of nutrition and\n" +
                "mineral metabolism that results in a decrease in \n" +
                "bone mass is also relevant.";
        ArrayList<ArrayList<String>> result = getRelevantTermsHelper(narr);
        assertTrue(result.get(0).toString().equals("[dietary, intakes, prevention, osteoporosis, discussion, disturbance, nutrition, mineral, metabolism, results, decrease, bone, mass]"));
        assertTrue(result.get(1).toString().equals("[]"));
    }

    public void testGetRelevantTerms404() throws IOException {
        String narr = "Any interruptions to the peace process not directly\n" +
                "attributable to acts of violence are not relevant.";
        ArrayList<ArrayList<String>> result = getRelevantTermsHelper(narr);
        assertTrue(result.get(0).toString().equals("[interruptions, peace, process, directly, attributable, acts, violence]"));
        assertTrue(result.get(1).toString().equals("[]"));
    }

    public void testGetRelevantTerms418() throws IOException {
        String narr = "Documents mentioning quilting books, quilting classes,\n" +
                "quilted objects, and museum exhibits of quilts are all relevant.  \n" +
                "Documents that discuss AIDs quilts are irrelevant, unless \n" +
                "there is specific mention that the quilts are being used \n" +
                "for fundraising.";
        ArrayList<ArrayList<String>> result = getRelevantTermsHelper(narr);
        assertTrue(result.get(0).toString().equals("[quilting, books, classes, quilted, objects, museum, exhibits, quilts, fundraising]"));
        assertTrue(result.get(1).toString().equals("[aids]"));
    }

    public void testGetRelevantTerms430() throws IOException {
        String narr = "Relevant documents must cite a specific instance of a human\n" +
                "attacked by killer bees.  Documents that note migration patterns\n" +
                "or report attacks on other animals are not relevant unless they\n" +
                "also cite an attack on a human.";
        ArrayList<ArrayList<String>> result = getRelevantTermsHelper(narr);
        assertTrue(result.get(0).toString().equals("[human, attacked, killer, bees, attack]"));
        assertTrue(result.get(1).toString().equals("[note, migration, patterns, report, attacks, other, animals]"));
    }

    public void testGetRelevantTerms431() throws IOException {
        String narr = "A relevant document will contain information on \n" +
                "current applications of robotic technology.  Discussions\n" +
                "of robotics research or simulations of robots are\n" +
                "not relevant.";
        ArrayList<ArrayList<String>> result = getRelevantTermsHelper(narr);
        assertTrue(result.get(0).toString().equals("[information, current, applications, robotic, technology]"));
        assertTrue(result.get(1).toString().equals("[discussions, robotics, research, simulations, robots]"));
    }
}
