package ie.tcd.irws.searchengine.parsers;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
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

    private Directory indexDirectory;
    private DirectoryReader ireader;
    private IndexSearcher isearcher;

    QueryHandler(String t_indexDirectory, Analyzer t_analyzer, int t_maxResults ) throws IOException {
        indexDirectoryPath = t_indexDirectory;
        analyzer = t_analyzer;
        maxResults = t_maxResults;
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

    public List<ScoreDoc[]> query(List<HashMap<String, String>> queries) throws IOException, ParseException
    {
        List<ScoreDoc[]>  results = new ArrayList<>();
        for (HashMap query : queries) {
            String queryString = createQueryString(query);
            ScoreDoc[] queryResults = query(queryString);
            results.add(queryResults);
        }
        return results;
    }

    private String createQueryString(HashMap<String, String> topicMap)
    {
        String descText = topicMap.get("desc");
        return escapeSpecialCharacters(descText);
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

    private ScoreDoc[] query(String queryString) throws IOException, ParseException {

        QueryParser parser = new QueryParser("content", analyzer);
        Query query;
        try {
            System.out.println("Querying "+ queryString);
            query = parser.parse(queryString);

        } catch (ParseException e) {
            throw new ParseException("An error occurred while parsing the query string.");
        }

        ScoreDoc[] hits;

        // Get the set of results
        try {
            hits = isearcher.search(query, maxResults).scoreDocs;
        } catch (IOException e) {
            throw new IOException("An error occurred while searching the index.");
        }
        return hits;
    }

}
