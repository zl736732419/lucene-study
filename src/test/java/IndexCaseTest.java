import com.zheng.lucene.index.IndexCase;
import org.junit.Before;
import org.junit.Test;

/**
 * @Author zhenglian
 * @Date 2018/11/23
 */
public class IndexCaseTest {
    private IndexCase indexCase;
    
    @Before
    public void init() {
        indexCase = new IndexCase();
    }
    
    @Test
    public void index() throws Exception {
        indexCase.index();
    }
    
    @Test
    public void search() throws Exception {
        indexCase.search("id", "5");
    }
}
