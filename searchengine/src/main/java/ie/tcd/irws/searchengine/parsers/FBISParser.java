package ie.tcd.irws.searchengine.parsers;

import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FBISParser {

    private String[] filepaths;
    private ArrayList<Document> docs;

    public FBISParser(String[] filepaths){
        this.filepaths = filepaths;
        this.docs = new ArrayList<>();
    }

    /**
     * Method to load content of all files into Document list, to be parsed with Analyzer
     * @return list of documents in directory, split by metadata
     */
    public void loadDocs(IndexWriter iwriter) throws IOException{

        File file;
        Document luceneDoc;

        // Create a new field type which will store term vector information
        FieldType ft = new FieldType(TextField.TYPE_STORED);
        ft.setTokenized(true); //done as default
        ft.setStoreTermVectors(true);
        ft.setStoreTermVectorPositions(true);
        ft.setStoreTermVectorOffsets(true);
        ft.setStoreTermVectorPayloads(true);

        for(String path : filepaths){
            file = new File(path);
            luceneDoc = new Document();

            org.jsoup.nodes.Document jsoupDoc = null;
            jsoupDoc = Jsoup.parse(file, "UTF-8", "");

            Elements jsoupDocs = jsoupDoc.getElementsByTag("DOC");

            //test output prints
            System.out.println("Parsing file: " + path);

            for(Element docElement : jsoupDocs){
                luceneDoc.add(new Field("text", docElement.getElementsByTag("TEXT").text(), ft));

                //stringFields
                luceneDoc.add(new StringField("docno", docElement.getElementsByTag("DOCNO").text(), Field.Store.YES));
                luceneDoc.add(new StringField("date", docElement.getElementsByTag("DATE1").text(), Field.Store.YES));
                luceneDoc.add(new StringField("ht", docElement.getElementsByTag("HT").text(), Field.Store.YES));

                System.out.println("Adding doc: " + docElement.getElementsByTag("DOCNO").text());

                iwriter.addDocument(luceneDoc);
            }
        }
    }
}
