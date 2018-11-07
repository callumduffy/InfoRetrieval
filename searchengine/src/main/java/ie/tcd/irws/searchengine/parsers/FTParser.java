package ie.tcd.irws.searchengine.parsers;

import java.io.File;
import java.util.ArrayList;
import org.apache.lucene.document.Document;

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
	 */
	public ArrayList<Document> loadDocs(){
		
		File file;
		Document doc;
		
		for(String path : filepaths){
			file = new File(path);
			doc = new Document();
			
			//now ready to start parsing with jsoup
			
		}
		
		//temporary ret to stop error
		return null;
	}
	
}
