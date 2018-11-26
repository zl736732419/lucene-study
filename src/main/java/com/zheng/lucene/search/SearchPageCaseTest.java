package com.zheng.lucene.search;

import org.apache.lucene.search.ScoreDoc;
import org.junit.Before;
import org.junit.Test;

/**
 * @Author zhenglian
 * @Date 2018/11/26
 */
public class SearchPageCaseTest {
    
    private SearchCase searchCase;
    
    @Before
    public void init() {
        searchCase = new SearchCase();
    }

    @Test
    public void searchByPage() throws Exception {
        searchCase.searchByPage("浏览记录", 1, 3);
        searchCase.searchByPage("浏览记录", 2, 3);
        searchCase.searchByPage("浏览记录", 3, 3);
        searchCase.searchByPage("浏览记录", 4, 3);
        searchCase.searchByPage("浏览记录", 5, 3);
    }
    
    @Test
    public void scroll() throws Exception {
        ScoreDoc sd = searchCase.scroll(null, "浏览记录", 3);
        sd = searchCase.scroll(sd, "浏览记录", 3);
        sd = searchCase.scroll(sd, "浏览记录", 3);
        sd = searchCase.scroll(sd, "浏览记录", 3);
        sd = searchCase.scroll(sd, "浏览记录", 3);
        sd = searchCase.scroll(sd, "浏览记录", 3);
    }
    
}
