package ie.tcd.irws.searchengine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import ie.tcd.irws.searchengine.parsers.FBISParser;
import ie.tcd.irws.searchengine.parsers.FRParser;
import ie.tcd.irws.searchengine.parsers.LAParser;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import ie.tcd.irws.searchengine.parsers.FTParser;


public class SearchEngine 
{
	private static Directory indexDirectory;

	private static String FT_DIR = "corpus/ft";
	private static String FR_DIR = "corpus/fr94";
	private static String LA_DIR = "corpus/latimes";
	private static String FBIS_DIR = "corpus/fbis";
	private static String INDEX_DIR = "index";
	
    public static void main( String[] args ) throws IOException {
    	
    	Analyzer analyzer = new StandardAnalyzer();
    	indexDirectory = FSDirectory.open(Paths.get(INDEX_DIR));

    	buildIndex(analyzer);
    }

	/**
	 *  Method for building the index
	 *  @param Analyzer
	 *  @return void
	 *  @throws IOException
	 */
	private static void buildIndex(Analyzer analyzer) throws IOException {
		// create and configure an index writer
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
		IndexWriter iwriter = new IndexWriter(indexDirectory, config);

		// FT
		FTParser ftp = new FTParser(getFilePaths(FT_DIR));
		ftp.loadDocs(iwriter);

		// FR 
		FRParser frp = new FRParser(getFilePaths(FR_DIR));
		frp.loadDocs(iwriter);

		// LA
		LAParser lap = new LAParser(getFilePaths(LA_DIR));
		lap.loadDocs(iwriter);

		// FBIS
		FBISParser fbisp = new FBISParser(getFilePaths(FBIS_DIR));
		fbisp.loadDocs(iwriter);

		iwriter.close();
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
