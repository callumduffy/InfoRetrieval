package ie.tcd.irws.searchengine.parsers;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.index.IndexWriter;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.TextField;

//Parser for LA Times
public class LAParser {

    private String[] filepaths;

    public LAParser(String[] filepaths){
        this.filepaths = filepaths;
    }

    public void loadDocs(IndexWriter iwriter) throws IOException {


                File file;
                Document luceneDoc;
                org.jsoup.nodes.Document jsoupDoc;

                // Create a new field type which will store term vector information

                FieldType ft = new FieldType(TextField.TYPE_STORED);
                ft.setTokenized(true); //done as default
                ft.setStoreTermVectors(true);
                ft.setStoreTermVectorPositions(true);
                ft.setStoreTermVectorOffsets(true);
                ft.setStoreTermVectorPayloads(true);

                String headline;
                String graphic;
                String subject;

                for(String path: filepaths) {
                    file = new File(path);
                    jsoupDoc = Jsoup.parse(file, "UTF-8");
                    

                    System.out.println("Adding file: " + path);
                    Elements jsoupDocs = jsoupDoc.getElementsByTag("DOC");
                    for(Element docElement : jsoupDocs){

                        headline=docElement.getElementsByTag("HEADLINE").text();
                        graphic=docElement.getElementsByTag("GRAPHIC").text();
                        subject=docElement.getElementsByTag("SUBJECT").text();
                //Adding the most relevant text fields
                        luceneDoc = new Document();
                        luceneDoc.add(new Field("docno", docElement.getElementsByTag("DOCNO").text(), ft));
                        luceneDoc.add(new Field("docID", docElement.getElementsByTag("DOCID").text(), ft));
                        luceneDoc.add(new Field("date", docElement.getElementsByTag("DATE").text(), ft));
                        luceneDoc.add(new Field("section", docElement.getElementsByTag("SECTION").text(), ft));
                        luceneDoc.add(new Field("headline", headline, ft));
                        luceneDoc.add(new Field("byline", docElement.getElementsByTag("BYLINE").text(), ft));
                        luceneDoc.add(new Field("text", docElement.getElementsByTag("TEXT").text()+" "+headline+" "+graphic+" "+subject, ft));
                        luceneDoc.add(new Field("graphic", graphic, ft));
                        luceneDoc.add(new Field("subject", subject, ft));
                        luceneDoc.add(new Field("title", subject+ " " + headline, ft));

                        

                        iwriter.addDocument(luceneDoc);
                    }
                }
    }
}














