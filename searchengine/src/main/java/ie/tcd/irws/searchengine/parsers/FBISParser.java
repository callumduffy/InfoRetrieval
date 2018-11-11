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

    public FBISParser(String[] filepaths){
        this.filepaths = filepaths;
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

            System.out.println("Parsing file: " + path);
            org.jsoup.nodes.Document jsoupDoc = Jsoup.parse(file, "UTF-8", "");

            Elements jsoupDocs = jsoupDoc.getElementsByTag("DOC");

            for(Element docElement : jsoupDocs){
                luceneDoc = new Document();

                String text = docElement.getElementsByTag("TEXT").text();
                /* where possible, get the main text.
                 * Removes some noise. E.g:
                 * Language: Macedonian
                 * Article Type:CSO
                 * [Article by V.V.A.: "The Law on the Census Is Ready, but... Fear of Politicized Count"]
                 */
                if(text.contains("[Text]")) {
                    text = text.substring(text.indexOf("[Text]")+6);
                }
                luceneDoc.add(new Field("text", text, ft));
                luceneDoc.add(new Field("title", docElement.getElementsByTag("TI").text(), ft));

                //stringFields
                luceneDoc.add(new StringField("docno", docElement.getElementsByTag("DOCNO").text(), Field.Store.YES));
                luceneDoc.add(new StringField("date", docElement.getElementsByTag("DATE1").text(), Field.Store.YES));

                iwriter.addDocument(luceneDoc);
            }
        }
    }
}
