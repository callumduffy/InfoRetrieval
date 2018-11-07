package ie.tcd.irws.searchengine.parsers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.TextField;

//Parser going to be used for the Financial Times dataset
public class FTParser {
	
	private String[] filepaths;
	private ArrayList<Document> docs;
	
	public FTParser(String[] filepaths){
		this.filepaths = filepaths;
		this.docs = new ArrayList<Document>();
	}
	
	/**
	 * Method to load content of all files into Document list, to be parsed with Analyzer
	 * @return list of documents in directory, split by metadata
	 * @throws IOException 
	 */
	public ArrayList<Document> loadDocs() throws IOException{
		
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
		
		for(String path : filepaths){
			
			file = new File(path);
			jsoupDoc = Jsoup.parse(file, "UTF-8");
			luceneDoc = new Document();
			
			Elements jsoupDocs = jsoupDoc.getElementsByTag("DOC");
			
			//test output prints
			System.out.println("Parsing file: " + path);
			
			for(Element docElement : jsoupDocs){
				
				luceneDoc.add(new Field("docno", docElement.getElementsByTag("DOCNO").text(), ft));
				luceneDoc.add(new Field("profile", docElement.getElementsByTag("PROFILE").text(), ft));
				luceneDoc.add(new Field("date", docElement.getElementsByTag("DATE").text(), ft));
				luceneDoc.add(new Field("headline", docElement.getElementsByTag("HEADLINE").text(), ft));
				luceneDoc.add(new Field("text", docElement.getElementsByTag("TEXT").text(), ft));
				luceneDoc.add(new Field("pub", docElement.getElementsByTag("PUB").text(), ft));
				
				//possibly make this a stringfield instead, so as not to index
				luceneDoc.add(new Field("page", docElement.getElementsByTag("PAGE").text(), ft));
				
				System.out.println("Adding doc: " + docElement.getElementsByTag("DOCNO").text());
				
				docs.add(luceneDoc);
			}
		}
		
		return docs;
	}
	
}
