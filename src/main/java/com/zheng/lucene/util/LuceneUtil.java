package com.zheng.lucene.util;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * @Author zhenglian
 * @Date 2018/11/25
 */
public class LuceneUtil {
    private Directory directory;
    private IndexReader reader;
    
    private LuceneUtil() {
        init();
    }
    
    private void init() {
        try {
//            directory = FSDirectory.open(Paths.get("E:\\lucene\\index2"));
            directory = MMapDirectory.open(Paths.get("E:\\lucene\\index2"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 应用程序获取reader进行搜索操作，
     * 每次获取reader实例时都需要进行change检查，目的是为了解决在查询时对索引进行操作之后能重新同步索引
     * 以致于索引更新的结果能及时反映到搜索结果中
     * @return
     */
    public IndexReader getReader() {
        try {
            if (null == reader) {
                reader = DirectoryReader.open(directory);
            } else {
                IndexReader newReader = DirectoryReader.openIfChanged((DirectoryReader) reader);
                if (null != newReader) {
                    // 关闭原来的reader
                    reader.close();
                    reader = newReader;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reader;
    }
    
    private static class Inner {
        private static LuceneUtil instance = new LuceneUtil(); 
    }
    
    public static LuceneUtil getInstance() {
        return Inner.instance;
    }

    public IndexSearcher getSearcher() {
        IndexReader reader = getReader();
        return new IndexSearcher(reader);
    }
    
    public Directory getDirectory() {
        return directory;
    }
}
