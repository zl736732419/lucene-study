import com.zheng.lucene.hello.HelloLucene;
import com.zheng.lucene.search.SearchCase;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * sort会导致最后匹配的文档得分不会被返回
 * 默认情况下(不指定sort)是按照文档得分逆序排序的
 * 只有在不指定sort的情况下才会返回得分信息
 * @Author zhenglian
 * @Date 2018/12/1
 */
public class SortedSearchTest {

    private HelloLucene helloLucene;
    private SearchCase searchCase;

    @Before
    public void init() {
        helloLucene = new HelloLucene();
        try {
            Path indexPath = Paths.get("E:\\lucene\\index");
            Path docsPath = Paths.get("E:\\lucene\\test");
            helloLucene.index(indexPath, docsPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        searchCase = new SearchCase();
    }
    
    @Test
    public void sortByDoc() throws Exception {
//        searchCase.orderBySort("content", "浏览记录", 10, Sort.INDEXORDER);
        searchCase.orderBySort("content", "浏览记录", 10, 
                new Sort(new SortField("content", SortField.Type.DOC)));
    }
    
    @Test
    public void sortByScore() throws Exception {
        searchCase.orderBySort("content", "浏览记录", 10, null);
//        searchCase.orderBySort("content", "浏览记录", 10, Sort.RELEVANCE);
//        searchCase.orderBySort("content", "浏览记录", 10,
//                new Sort(new SortField("content", SortField.Type.SCORE)));
    }

    @Test
    public void sortByFieldAsc() throws Exception {
        searchCase.orderBySort("content", "浏览记录", 10,
                new Sort(new SortField("size", SortField.Type.LONG)));
    }

    @Test
    public void sortByFieldDesc() throws Exception {
        searchCase.orderBySort("content", "浏览记录", 10,
                new Sort(new SortField("size", SortField.Type.LONG, true)));
    }

    @Test
    public void sortByStringFieldAsc() throws Exception {
        searchCase.orderBySort("content", "浏览记录", 10,
                new Sort(new SortField("title.sort", SortField.Type.STRING)));
    }

    @Test
    public void sortByStringFieldDesc() throws Exception {
        searchCase.orderBySort("content", "浏览记录", 10,
                new Sort(new SortField("title.sort", SortField.Type.STRING, true)));
    }
    
}
