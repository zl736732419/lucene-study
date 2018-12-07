package com.zheng.lucene.util;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Author zhenglian
 * @Date 2018/11/25
 */
public class LuceneUtil {
    private Directory directory;
    @Deprecated
    private IndexReader reader;

    private SearcherManager sm;
    
    private LuceneUtil() {
        init();
    }

    private void init() {
        try {
            initDirectory();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initScheduleReopenRefresh() {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(() -> {
            try {
                sm.maybeRefresh();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, 5, 5, TimeUnit.SECONDS);
    }

    public IndexWriter getWriter(IndexWriterConfig.OpenMode openMode) {
        IndexWriterConfig iwc = new IndexWriterConfig(new StandardAnalyzer());
        if (null != openMode) {
            iwc.setOpenMode(openMode);
        }
        IndexWriter writer = null;
        try {
            writer = new IndexWriter(directory, iwc);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return writer;
    }

//    /**
//     * 这里的writer要和index是同一个，否则会出现锁并发访问问题
//     * @param writer
//     */
//    public void startReopenThread(IndexWriter writer) {
//        ControlledRealTimeReopenThread thread = new ControlledRealTimeReopenThread(writer, sm,
//                5.0, 0.025);
//        thread.setDaemon(true);
//        thread.setName("lucene reopen thread");
//        thread.start();
//    }

    public void initSearcherManager() {
        try {
            sm = new SearcherManager(LuceneUtil.getInstance().getDirectory(), new SearcherFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initDirectory() throws Exception {
//        directory = FSDirectory.open(Paths.get("E:\\lucene\\index2"));
        directory = MMapDirectory.open(Paths.get("E:\\lucene\\index"));
//        Directory delegateDirectory = MMapDirectory.open(Paths.get("E:\\lucene\\index"));
//        directory = new NRTCachingDirectory(delegateDirectory, 5.0, 60.0);
    }

    /**
     * 应用程序获取reader进行搜索操作，
     * 每次获取reader实例时都需要进行change检查，目的是为了解决在查询时对索引进行操作之后能重新同步索引
     * 以致于索引更新的结果能及时反映到搜索结果中
     *
     * @return
     * @see SearcherManager#acquire()
     * @deprecated 已经被searcherManager工具类管理所取代
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
