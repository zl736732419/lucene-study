import com.zheng.lucene.analyzer.AnalyzerCase;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.junit.Before;
import org.junit.Test;

/**
 * @Author zhenglian
 * @Date 2018/11/27
 */
public class AnalyzerCaseTest {
    private AnalyzerCase analyzerCase;
    
    @Before
    public void init() {
        analyzerCase = new AnalyzerCase();
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
}
