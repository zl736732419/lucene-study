import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @Author zhenglian
 * @Date 2018/11/21
 */
public class HelloLuceneTest {
    
    private HelloLucene helloLucene;
    private Path indexPath;
    private Path docsPath;

    @Before
    public void init() {
        helloLucene = new HelloLucene();
        indexPath = Paths.get("E:\\lucene\\index");
        docsPath = Paths.get("E:\\lucene\\test");
    }
    
    
    @Test
    public void index() {
        helloLucene.index(indexPath, docsPath);
    }
    
    @Test
    public void search() {
        helloLucene.search(indexPath, "contents", "浏览记录", 10);
    }
    
}
