package ie.tcd.irws.searchengine.handlers;

import ie.tcd.irws.searchengine.Utils;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.QueryParserBase;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.search.BoostQuery;

//import javax.rmi.CORBA.Util;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;


public class QueryHandler {

    // the location of the search index
    private String indexDirectoryPath;
    // Limit the number of search results we get
    private int maxResults;
    private Analyzer analyzer;
    private String trecPath;

    private Directory indexDirectory;
    private DirectoryReader ireader;
    private IndexSearcher isearcher;


    public QueryHandler(String t_indexDirectory, Analyzer t_analyzer, int t_maxResults , String t_trecPath) throws IOException {
        indexDirectoryPath = t_indexDirectory;
        analyzer = t_analyzer;
        maxResults = t_maxResults;
        trecPath = t_trecPath;
        initQueryHandler();
    }

    private void initQueryHandler() throws IOException
    {
        try {
            indexDirectory = FSDirectory.open(Paths.get(indexDirectoryPath));
        } catch (IOException e) {
            throw new IOException("An error occurred while opening the index.");
        }

        // create objects to read and search across the index
        try {
            ireader = DirectoryReader.open(indexDirectory);
        } catch (IOException e) {
            throw new IOException("An error occurred while reading the index.");
        }


        isearcher = new IndexSearcher(ireader);
    }

    public void setMaxResults(int newMaxResults) {
        maxResults = newMaxResults;

    }

    public void setSimilarityMethod(Similarity similarity)
    {
        isearcher.setSimilarity(similarity);

    }

    public List<ScoreDoc[]> query(List<HashMap<String, String>> queries, Boolean evaluate) throws IOException, ParseException
    {



        List<ScoreDoc[]>  results = new ArrayList<>();



        //PrintWriter for writing to results file
        PrintWriter pw = new PrintWriter(Paths.get(trecPath).toAbsolutePath().toString());


        for (HashMap query : queries) {

            BooleanQuery.Builder queryString = createQuery(query);
            ScoreDoc[] queryResults = runQuery(queryString);
            results.add(queryResults);

            //If the results are to be saved to trec_eval file
            if (evaluate == true){
                for (int i = 0; i < queryResults.length; i++) {
                    Document d = isearcher.doc(queryResults[i].doc);
                    String docno = d.get("docno");
        
                    pw.println(query.get("num") + " Q0 "+docno+" "+( i + 1) +" "+queryResults[i].score+" "+"0");
                }
            }
        }
        pw.close();
        return results;
    }

    private BooleanQuery.Builder createQuery(HashMap<String, String> topicMap) throws ParseException, IOException
    {

        /*
        Field Names:
            text
            date
            docno
            title
            *More to be added*
        */


        String descText = escapeSpecialCharacters(topicMap.get("desc"));
        String narrText = escapeSpecialCharacters(topicMap.get("narr")); //To be seperated into should/should not
        String titleText = escapeSpecialCharacters(topicMap.get("title"));
        String relText = escapeSpecialCharacters(topicMap.get("rel"));
        String nRelText = escapeSpecialCharacters(topicMap.get("nRel"));


        BooleanQuery.Builder bq = new BooleanQuery.Builder();

        QueryParser qp1 = new QueryParser("text", analyzer);
        //Query text - Document text
        Query query1 = qp1.parse(descText);
        query1 = new BoostQuery(query1, (float)0.5);
        
        //Query title - Document text
        Query query2 = qp1.parse(titleText);
        query2 = new BoostQuery(query2, (float)1.5);


        //Relevant terms from narrative
        if (relText.length() > 0){
            Query query3 = qp1.parse(relText);
            query3 = new BoostQuery(query3, (float)0.5);
            bq.add(query3, BooleanClause.Occur.SHOULD);
        }


/*
        //This piece of code brings down performance from 0.27 to 0.20, need to find alternative way
       // of using non relevant terms
        //Not relevant terms from narrative
        if (nRelText.length() > 0){
            Query query4 = qp1.parse(nRelText);
            query4 = new BoostQuery(query4, (float)0.5);
            bq.add(query4, BooleanClause.Occur.MUST_NOT);
        }
        */

        //query description - document title
        QueryParser qp2 = new QueryParser("title", analyzer);
        Query query5 = qp2.parse(descText);
        query5 = new BoostQuery(query5, (float)0.5);
        
        //query title - document title
        Query query6 = qp2.parse(titleText);
        query6 = new BoostQuery(query6, (float)0.5);



        //Build Boolean Query
        bq.add(query1, BooleanClause.Occur.SHOULD);
        bq.add(query2, BooleanClause.Occur.SHOULD);

        //These queries seem to damage performance
        //bq.add(query5, BooleanClause.Occur.SHOULD);
        //bq.add(query6, BooleanClause.Occur.SHOULD);

        // add phrase queries for phraseLength of 2
        ArrayList<PhraseQuery> phraseQueries = constructPhraseQueries(descText, 2);
        for(int i = 0; i < phraseQueries.size(); i++) {
            bq.add(new BoostQuery(phraseQueries.get(i), (float)2.5), BooleanClause.Occur.SHOULD);
        }

        // add phrase queries for phraseLength of 3
        phraseQueries = constructPhraseQueries(descText, 3);
        for(int i = 0; i < phraseQueries.size(); i++) {
            bq.add(new BoostQuery(phraseQueries.get(i), (float)2.5), BooleanClause.Occur.SHOULD);
        }



/*        Constructs name queries and boosts them but it has no effect
 //       ArrayList<PhraseQuery> nameQuery = constructNameQuery(descText);

        for(int i = 0; i < nameQuery.size(); i++) {
            Query nQuery = nameQuery.get(i);
            nQuery = new BoostQuery(nQuery, (float)2);
            bq.add(nQuery, BooleanClause.Occur.MUST_NOT);
            System.out.println(nameQuery.get(i));
        }

*/
        return bq;


    }

    private String escapeSpecialCharacters(String s){
        s = s.replace("\\", "");
        s = s.replace("+", "");
        s = s.replace("-", "");
        s = s.replace("&&", "");
        s = s.replace("||", "");
        s = s.replace("!", "");
        s = s.replace("(", "");
        s = s.replace(")", "");
        s = s.replace("[", "");
        s = s.replace("]", "");
        s = s.replace("{", "");
        s = s.replace("}", "");
        s = s.replace("^", "");
        s = s.replace("\"", "");
        s = s.replace("~", "");
        s = s.replace("*", "");
        s = s.replace("?", "");
        s = s.replace(":", "");
        s = s.replace("/", "");

        return s;
    }

    /**
     * Method for constructing a list of phrase queries using the given text
     * @param text
     * @return ArrayList of PhraseQuery
     */
    private ArrayList<PhraseQuery> constructPhraseQueries(String text, int phraseLength) {
        ArrayList<PhraseQuery> ret = new ArrayList<>();
        ArrayList<ArrayList<String>> phrases = Utils.getPhrases(text, phraseLength);
        for(int i = 0; i < phrases.size(); i++) {
            PhraseQuery.Builder builder = new PhraseQuery.Builder();
            for(int j = 0; j < phraseLength; j++){
                builder.add(new Term("text", phrases.get(i).get(j)), j);
            }
            PhraseQuery pq = builder.build();
            ret.add(pq);
        }

        return ret;
    }

    private ArrayList<PhraseQuery> constructNameQuery(String text) {

        ArrayList<PhraseQuery> ret = new ArrayList<>();
        String[] name = text.trim().replaceAll("[^A-Za-z0-9 ]", "").split("\\s+");


        for (int i = 0; i < name.length; i++) {
            char charCheck = name[i].charAt(0);
            boolean isUpperCase = Character.isUpperCase(charCheck);

            if (isUpperCase == true && !name[i].equals("What")&& !name[i].equals("Give")&& !name[i].equals("Provide")&& !name[i].equals("The")&& !name[i].equals("Three") && !name[i].equals("Am")&& !name[i].equals("Where")&& !name[i].equals("Why")&& !name[i].equals("Who")&& !name[i].equals("When")&& !name[i].equals("How")&& !name[i].equals("Find")&& !name[i].equals("In")&& !name[i].equals("Do")&& !name[i].equals("Identify")&& !name[i].equals("Is")) {
                PhraseQuery.Builder builder = new PhraseQuery.Builder();
                builder.add(new Term("text",name[i]));
                PhraseQuery pq = builder.build();
                ret.add(pq);
            }

        }
        return ret;
    }


    private ScoreDoc[] runQuery(BooleanQuery.Builder query_string) throws IOException, ParseException {

        ScoreDoc[] hits;

        try {
            hits = isearcher.search(query_string.build(), maxResults).scoreDocs;

        } catch (IOException e) {
            throw new IOException("An error occurred while searching the index.");
        }
      

        return hits;
    }



}
