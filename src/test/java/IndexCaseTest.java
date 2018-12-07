import com.zheng.lucene.index.IndexCase;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexableField;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
    public void update() throws Exception {
        TextField field = new TextField("id", "11", Field.Store.YES);
        List<IndexableField> fields = new ArrayList<>();
        fields.add(field);
        indexCase.update("id", "1", fields);
    }

    
    
    @Test
    public void delete() throws Exception {
        indexCase.delete("email", "a@qq.com");
    }
    
    @Test
    public void deleteAll() throws Exception {
        indexCase.deleteAll();
    }

    @Test
    public void rollback() throws Exception {
        indexCase.rollback();
    }
    
    @Test
    public void merge() throws Exception {
        indexCase.merge(1);
    }
    
    @Test
    public void clean() throws Exception {
        indexCase.mergeDelete();
    }
    
    @Test
    public void indexFile() throws Exception {
        indexCase.indexFile(new File("C:\\Users\\zhenglian\\Desktop\\hello.docx"));
    }
    
}
