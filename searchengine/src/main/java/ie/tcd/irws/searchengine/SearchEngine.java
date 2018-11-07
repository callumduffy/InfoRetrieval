package ie.tcd.irws.searchengine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import org.apache.lucene.document.Document;

import ie.tcd.irws.searchengine.parsers.FTParser;

public class SearchEngine 
{
	private static ArrayList<Document> documents = new ArrayList<Document>();
	private static String FT_DIR = "corpus";
	
    public static void main( String[] args ) throws IOException{
    	//prepare for use when FTParser is complete
    	FTParser ftp = new FTParser(getFilePaths(FT_DIR));
    }
    
    /**
     * Method to get all paths of files in a given directory
     * @param directory
     * @return String array of all file paths
     * @throws IOException
     */
    private static String[] getFilePaths(String directory) throws IOException{
    	return Files.walk(Paths.get(directory))
    			.filter(Files::isRegularFile)
    			.map(Path::toAbsolutePath)
    			.map(Path::toString)
    			.toArray(String[]::new);
    }
    
}
