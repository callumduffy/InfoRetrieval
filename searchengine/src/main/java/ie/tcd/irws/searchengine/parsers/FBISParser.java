package ie.tcd.irws.searchengine.parsers;

import org.apache.lucene.document.Document;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

public class FBISParser {

    private String[] filepaths;
    private ArrayList<Document> docs;

    public FBISParser(String[] filepaths){
        this.filepaths = filepaths;
        this.docs = loadDocs();
    }

    /**
     * Method to load content of all files into Document list, to be parsed with Analyzer
     * @return list of documents in directory, split by metadata
     */
    public ArrayList<Document> loadDocs(){

        File file;
        Document lucene_doc;

        for(String path : filepaths){
            file = new File(path);
            lucene_doc = new Document();

            org.jsoup.nodes.Document jsoup_doc = null;
            try {
                jsoup_doc = Jsoup.parse(file, "UTF-8", "");
            }
            catch (IOException ex) {
                System.out.println("Error parsing document: " + path);
                ex.printStackTrace(new PrintStream(System.out));
            }

            Elements docs = jsoup_doc.select("doc");
            for (Element doc : docs) {
                if (doc.tagName().equals("HEADER")) {
                    System.out.println(doc);
                }
            }

        }

        //temporary ret to stop error
        return null;
    }
}
