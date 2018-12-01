import com.zheng.lucene.analyzer.AnalyzerCase;
import com.zheng.lucene.analyzer.MyStopAnalyzer;
import com.zheng.lucene.analyzer.MySynonymAnalyzer;
import com.zheng.lucene.search.SearchCase;
import com.zheng.lucene.util.LuceneUtil;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author zhenglian
 * @Date 2018/11/27
 */
public class AnalyzerCaseTest {
    private AnalyzerCase analyzerCase;
    private SearchCase searchCase;
    
    @Before
    public void init() {
        analyzerCase = new AnalyzerCase();
        searchCase = new SearchCase();
    }
    
    @Test
    public void displayTerm() throws Exception {
        String str = "hello, my name is zl, i'm coming from china chongqing, my tel is 123456";
        Analyzer a1 = new StandardAnalyzer();
        Analyzer a2 = new WhitespaceAnalyzer();
        Analyzer a3 = new SimpleAnalyzer();
        Analyzer a4 = new KeywordAnalyzer();
        Analyzer a5 = new EnglishAnalyzer();
        analyzerCase.displayToken(str, a1);
        System.out.println("--------------");
        analyzerCase.displayToken(str, a2);
        System.out.println("--------------");
        analyzerCase.displayToken(str, a3);
        System.out.println("--------------");
        analyzerCase.displayToken(str, a4);
        System.out.println("--------------");
        analyzerCase.displayToken(str, a5);
    }

    @Test
    public void displayAllTermInfo() throws Exception {
        String str = "how are you thank you";
        Analyzer a1 = new StandardAnalyzer();
        Analyzer a2 = new WhitespaceAnalyzer();
        Analyzer a3 = new SimpleAnalyzer();
        Analyzer a4 = new KeywordAnalyzer();
        Analyzer a5 = new EnglishAnalyzer();
        analyzerCase.displayAllTokenInfo(str, a1);
        System.out.println("--------------");
        analyzerCase.displayAllTokenInfo(str, a2);
        System.out.println("--------------");
        analyzerCase.displayAllTokenInfo(str, a3);
        System.out.println("--------------");
        analyzerCase.displayAllTokenInfo(str, a4);
        System.out.println("--------------");
        analyzerCase.displayAllTokenInfo(str, a5);
    }
    
    @Test
    public void myStopWordAnalyzer() throws Exception {
        String str = "how are you";
        Set<String> stops = new HashSet<>();
        stops.add("you");
        Analyzer a1 = new MyStopAnalyzer(stops);
        analyzerCase.displayToken(str, a1);
        System.out.println("---------------");
        Analyzer a2 = new EnglishAnalyzer();
        analyzerCase.displayToken(str, a2);
    }
    
    @Test
    public void synonymAnalyzer() throws Exception {
        String string = "i like you";
        Analyzer a1 = new MySynonymAnalyzer();
        analyzerCase.displayAllTokenInfo(string, a1);
    }
    
    @Test
    public void synonymSearch() throws Exception {
        IndexWriterConfig iwc = new IndexWriterConfig(new MySynonymAnalyzer());
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        Directory directory = LuceneUtil.getInstance().getDirectory();
        IndexWriter writer = new IndexWriter(directory, iwc);
        Document document = new Document();
        String str = "i like you";
        FieldType ft = getIndexStoredAnalyzeType();
        document.add(new StoredField("content", str, ft));
        writer.addDocument(document);
        writer.close();

        IndexSearcher searcher = new IndexSearcher(LuceneUtil.getInstance().getReader());
        QueryParser parser = new QueryParser("content", new MySynonymAnalyzer());
        Query query = parser.parse("enjoy");
        int size = 10;
        TopDocs topDocs = searcher.search(query, size);
        searchCase.printSearchResult(topDocs, searcher);
    }

    private FieldType getIndexStoredAnalyzeType() {
        FieldType ft = new FieldType();
        ft.setStored(true);
        ft.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
        ft.setTokenized(true);
        ft.freeze();
        return ft;
    }
    
}
