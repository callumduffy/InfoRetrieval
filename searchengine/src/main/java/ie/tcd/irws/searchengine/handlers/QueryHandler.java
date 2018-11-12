package ie.tcd.irws.searchengine.handlers;
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

import java.io.IOException;
import java.io.PrintWriter;
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

    private BooleanQuery.Builder createQuery(HashMap<String, String> topicMap) throws ParseException
    {
        /*
        Field Names:
            text
            date
            docno
            *More to be added*
        */
        
        String descText = escapeSpecialCharacters(topicMap.get("desc"));
        String narrText = escapeSpecialCharacters(topicMap.get("narr")); //To be seperated into should/should not
        String titleText = escapeSpecialCharacters(topicMap.get("title"));

        BooleanQuery.Builder bq = new BooleanQuery.Builder();
        QueryParser qp1 = new QueryParser("text", analyzer);
        Query query1 = qp1.parse(descText);
        query1 = new BoostQuery(query1, (float)0.5);
        

        QueryParser qp2 = new QueryParser("text", analyzer);
        Query query2 = qp2.parse(titleText);
        query2 = new BoostQuery(query2, (float)1.5);

        bq.add(query1, BooleanClause.Occur.SHOULD);
        bq.add(query2, BooleanClause.Occur.SHOULD);
        //Build Boolean Query
        
        return bq;
    }

    private String escapeSpecialCharacters(String s){
        s = s.replace("\\", "\\\\");
        s = s.replace("+", "\\+");
        s = s.replace("-", "\\-");
        s = s.replace("&&", "\\&&");
        s = s.replace("||", "\\||");
        s = s.replace("!", "\\!");
        s = s.replace("(", "\\(");
        s = s.replace(")", "\\)");
        s = s.replace("[", "\\[");
        s = s.replace("]", "\\]");
        s = s.replace("{", "\\{");
        s = s.replace("}", "\\}");
        s = s.replace("^", "\\^");
        s = s.replace("\"", "\\\"");
        s = s.replace("~", "\\~");
        s = s.replace("*", "\\*");
        s = s.replace("?", "\\?");
        s = s.replace(":", "\\:");
        return s;
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
