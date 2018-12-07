import org.apache.tika.Tika;
import org.junit.Test;

import java.io.File;

/**
 * @Author zhenglian
 * @Date 2018/12/5
 */
public class TikaTest {
    
    @Test
    public void parse() throws Exception {
        String content = new Tika().parseToString(new File("C:\\Users\\zhenglian\\Desktop\\hello.docx"));
        System.out.println(content);
    }
}
