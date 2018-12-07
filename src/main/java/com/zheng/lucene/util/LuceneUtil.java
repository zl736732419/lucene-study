package com.zheng.lucene.util;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.ControlledRealTimeReopenThread;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.store.NRTCachingDirectory;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * @Author zhenglian
 * @Date 2018/11/25
 */
public class LuceneUtil {
    private Directory directory;
    private IndexWriter writer;
    private SearcherManager sm;
    @Deprecated
    private IndexReader reader;
    
    private LuceneUtil() {
        init();
    }
    
    private void init() {
        try {
            initDirectory();
            initWriter();
            initSearcherManager();
            startReopenThread();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initSearcherManager() throws Exception {
        sm = new SearcherManager(writer, true, true, new SearcherFactory());
    }

    private void initDirectory() throws Exception {
//        directory = FSDirectory.open(Paths.get("E:\\lucene\\index2"));
//        directory = MMapDirectory.open(Paths.get("E:\\lucene\\index"));
        Directory delegateDirectory = MMapDirectory.open(Paths.get("E:\\lucene\\index"));
        directory = new NRTCachingDirectory(delegateDirectory, 5.0, 60.0);
    }

    private void startReopenThread() {
        ControlledRealTimeReopenThread thread = new ControlledRealTimeReopenThread(writer, sm, 
                5.0, 0.025);
        thread.setDaemon(true);
        thread.setName("lucene reopen thread");
        thread.start();
    }

    /**
     * 应用程序获取reader进行搜索操作，
     * 每次获取reader实例时都需要进行change检查，目的是为了解决在查询时对索引进行操作之后能重新同步索引
     * 以致于索引更新的结果能及时反映到搜索结果中
     * @deprecated 已经被searcherManager工具类管理所取代
     * @see SearcherManager#acquire()  
     * @return
     */
    @Deprecated
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
    
    public void initWriter() {
        IndexWriterConfig iwc = new IndexWriterConfig(new StandardAnalyzer());
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        try {
            writer = new IndexWriter(directory, iwc);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public IndexWriter getWriter() {
        return writer;
    }
    
    private static class Inner {
        private static LuceneUtil instance = new LuceneUtil(); 
    }
    
    public static LuceneUtil getInstance() {
        return Inner.instance;
    }

    public IndexSearcher getSearcher() {
        try {
            return sm.acquire();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void release(IndexSearcher searcher) {
        try {
            sm.release(searcher);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public Directory getDirectory() {
        return directory;
    }
}
