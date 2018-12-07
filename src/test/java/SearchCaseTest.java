import com.zheng.lucene.index.IndexCase;
import com.zheng.lucene.search.SearchCase;
import com.zheng.lucene.util.LuceneUtil;
import org.apache.tika.Tika;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

/**
 * @Author zhenglian
 * @Date 2018/11/25
 */
public class SearchCaseTest {
    private IndexCase indexCase;
    private SearchCase searchCase;
    
    private LuceneUtil luceneUtil = LuceneUtil.getInstance();
    
    @Before
    public void init() {
        indexCase = new IndexCase();
        luceneUtil.initSearcherManager();
        luceneUtil.initScheduleReopenRefresh();
        searchCase = new SearchCase();
    }

    @Test
    public void delete() throws Exception {
        indexCase.delete("content", "jak");
    }

    @Test
    public void search() {
        searchCase.search("content", "hello");
    }

    @Test
    public void multiTimeSearch() throws Exception {
        // search用同一个reader, 在执行search过程中删除其中一个文档，看是否会对查询有影响
        for(int i = 0; i < 5; i++) {
            searchCase.docsInfo();
//            searchCase.search("content", "hello");
            indexCase.delete("content", "jak");
//            searchCase.docsInfo();
            Thread.sleep(2000);
        }
    }

    @Test
    public void docsInfo()  {
        searchCase.docsInfo();
    }
    
    @Test
    public void searchByTerm()  {
        searchCase.searchByTerm("id", "4", 6);
    }
    
    @Test
    public void searchByTermRange()  {
        searchCase.searchByTermRange("name", "lisi", "zhaoliu", 3);
    }
    
    @Test
    public void searchByIntRange()  {
        searchCase.searchByIntRange("id", 1, 4, 4);
    }
    
    @Test
    public void searchByPrefix() {
        searchCase.searchByPrefix("name", "z", 10);
    }

    @Test
    public void searchByWildcard() {
        searchCase.searchByWildcard("name", "zhang*", 10);
    }

    @Test
    public void searchByBool() {
        searchCase.searchByBool(10);
    }

    @Test
    public void searchByPhrase() throws Exception {
        searchCase.searchByPhrase(10);
    }

    @Test
    public void searchByFuzzy() throws Exception {
        searchCase.searchByFuzzy(10);
    }
    
    @Test
    public void searchByCustomQueryParser() throws Exception {
        // fuzzy
//        searchCase.searchByCustomQueryParser("name", "zhangsan~", 10);
        // wildcard
//        searchCase.searchByCustomQueryParser("name", "zhang?an", 10);
        // regexp
//        searchCase.searchByCustomQueryParser("name", "/[a-z]{8}/", 10);
        // term range
//        searchCase.searchByCustomQueryParser("name", "[lisi TO zhangsan]", 10);
        // numeric range
//        searchCase.searchByCustomQueryParser("id", "[1 TO 2]", 10);
        // date range
        searchCase.searchByCustomQueryParser("date", "[2018-11-11 TO 2018-12-20]", 10);
    }

    @Test
    public void highlight() throws Exception {
        String str = "lucene highlight first test";
        String keyword = "lucene";
        String result = searchCase.highlight(keyword, str);
        System.out.println("title: " + result);
        String content = new Tika().parseToString(new File("C:\\Users\\zhenglian\\Desktop\\hello.docx"));
        result = searchCase.highlight("hello", content);
        System.out.println("content: " + result);
    }
}
