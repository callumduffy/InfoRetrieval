package hoei.A2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.TextField;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class LAParser {
	
	private String[] filepaths;
	private ArrayList<Document> docs;
	
	public LAParser(String[] filepaths){
		this.filepaths = filepaths;
		System.out.println(filepaths);
		this.docs = new ArrayList<Document>();
	}

	public ArrayList<Document> loadDocs() throws IOException {
		
		
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
				
				
				for(String path: filepaths) {
					file = new File(path);
					jsoupDoc = Jsoup.parse(file, "UTF-8");
					luceneDoc = new Document();
					
					
					//System.out.println("Parsing file: " + path);
			//	System.out.println("JsoupDoc " + jsoupDoc);
					
					Elements jsoupDocs = jsoupDoc.getElementsByTag("DOC");
					for(Element docElement : jsoupDocs){
						
						String content = docElement.getElementsByTag("TEXT").text();

						luceneDoc.add(new Field("docNo", docElement.getElementsByTag("DOCNO").text(), ft));
						luceneDoc.add(new Field("docID", docElement.getElementsByTag("DOCID").text(), ft));
						luceneDoc.add(new Field("date", docElement.getElementsByTag("DATE").text(), ft));
						luceneDoc.add(new Field("section", docElement.getElementsByTag("SECTION").text(), ft));
						luceneDoc.add(new Field("headline", docElement.getElementsByTag("HEADLINE").text(), ft));
						luceneDoc.add(new Field("byline", docElement.getElementsByTag("BYLINE").text(), ft));
						luceneDoc.add(new Field("text", docElement.getElementsByTag("TEXT").text(), ft));
						luceneDoc.add(new Field("graphic", docElement.getElementsByTag("GRAPHIC").text(), ft));
						luceneDoc.add(new Field("subject ", docElement.getElementsByTag("SUBJECT").text(), ft));
						
			
				
						
						System.out.println("Adding doc: " + docElement.getElementsByTag("DOCNO").text());
						
						docs.add(luceneDoc);
					
					}

					

				}
				return docs;
				
	}
	
}
					
			

				
				
				


	
	
	
	

	
