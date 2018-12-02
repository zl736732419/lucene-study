import com.zheng.lucene.index.IndexCase;
import com.zheng.lucene.search.SearchCase;
import org.junit.Before;
import org.junit.Test;

/**
 * @Author zhenglian
 * @Date 2018/11/25
 */
public class SearchCaseTest {
    
    private IndexCase indexCase;
    private SearchCase searchCase;
    
    @Before
    public void init() {
        indexCase = new IndexCase();
        try {
            indexCase.index();
        } catch (Exception e) {
            e.printStackTrace();
        }
        searchCase = new SearchCase();
    }
    
    @Test
    public void search() throws Exception {
        searchCase.search("id", "4");
    }

    @Test
    public void multiTimeSearch() throws Exception {
        // search用同一个reader, 在执行search过程中删除其中一个文档，看是否会对查询有影响
        for (int i = 0; i < 2; i++) {
            searchCase.search("content", "hello");
            Thread.sleep(10000);
        }
    }

    @Test
    public void docsInfo() throws Exception {
        searchCase.docsInfo();
    }
    
    @Test
    public void searchByTerm() throws Exception {
        searchCase.searchByTerm("content", "hello", 6);
    }
    
    @Test
    public void searchByTermRange() throws Exception {
        searchCase.searchByTermRange("name", "lisi", "zhaoliu", 3);
    }
    
    @Test
    public void searchByIntRange() throws Exception {
        searchCase.searchByIntRange("id", 1, 4, 4);
    }
    
    @Test
    public void searchByPrefix() throws Exception {
        searchCase.searchByPrefix("name", "z", 10);
    }

    @Test
    public void searchByWildcard() throws Exception {
        searchCase.searchByWildcard("name", "zhang*", 10);
    }

    @Test
    public void searchByBool() throws Exception {
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
}
