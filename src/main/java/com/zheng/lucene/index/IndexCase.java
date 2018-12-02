package com.zheng.lucene.index;

import com.zheng.lucene.util.LuceneUtil;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @Author zhenglian
 * @Date 2018/11/22
 */
public class IndexCase {
    // 主键，索引不分词存储
    private Integer[] ids = {1, 2, 3, 4, 5, 6};
    // 邮件，索引不分词存储
    private String[] emails = {"a@qq.com", "b@qq.com", "c@qq.com", "d@qq.com", "e@qq.com", "f@qq.com"};
    // 内容，索引分词不存储,不存储的字段在检索时无法得到其值
    private String[] contents = {"hello, i'm zhangsan", "hello, Jak", "hello what's your name?", "hello how old are you?",
            "hello none of your business", "hello oh my god!"};
    // 附件，索引不分词存储
    private int[] attachs = {2, 3, 1, 4, 5, 7};
    // 名称，索引不分词存储
    private String[] names = {"zhangsan", "lisi", "wangwu", "zhaoliu", "tianqi", "zhaoba"};
    // 日期，按Long类型存储
    private String[] dates = {
            "2018-11-11 09:30:00", "2018-11-25 09:30:00", "2018-11-30 09:30:00",
            "2018-12-01 09:30:00", "2018-12-10 09:30:00", "2018-12-20 09:30:00"
    };

    private Directory directory = LuceneUtil.getInstance().getDirectory();
    
    public void index() throws Exception {
        IndexWriterConfig iwc = new IndexWriterConfig(new StandardAnalyzer());
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter writer = new IndexWriter(directory, iwc);
        Document document;
        FieldType ft = getIndexStoredNotAnalyzeType();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (int i = 0; i < ids.length; i++) {
            document = new Document();
            Field id = new IntPoint("id", ids[i]);
            document.add(id);
            StoredField idStored = new StoredField("id.stored", ids[i]);
            document.add(idStored);
            Field email = new StoredField("email", emails[i], ft);
            document.add(email);
            Field content = new TextField("content", contents[i], Field.Store.NO);
            document.add(content);
            Field attach = new StoredField("attach", attachs[i] + "", ft);
            document.add(attach);
            Field name = new StoredField("name", names[i], ft);
            document.add(name);
            Field date = new LongPoint("date", sf.parse(dates[i]).getTime());
            document.add(date);
            Field storedDate = new StoredField("date.stored", dates[i]);
            document.add(storedDate);
            writer.addDocument(document);
        }
        writer.close();
    }

    /**
     * 全量字段更新，需要将所有字段值都列出来
     *
     * @param field
     * @param value
     * @param fields
     * @throws Exception
     */
    public void update(String field, String value, List<IndexableField> fields) throws Exception {
        IndexWriterConfig iwc = new IndexWriterConfig(new StandardAnalyzer());
        IndexWriter writer = new IndexWriter(directory, iwc);
        writer.updateDocument(new Term(field, value), fields);
        writer.close();
    }

    /**
     * 合并段文件，不建议使用，会耗性能，lucene会根据情况自动进行段合并
     *
     * @param segmentNumber
     * @throws Exception
     */
    public void merge(int segmentNumber) throws Exception {
        IndexWriterConfig iwc = new IndexWriterConfig(new StandardAnalyzer());
        IndexWriter writer = new IndexWriter(directory, iwc);
        writer.forceMerge(segmentNumber);
        writer.close();
    }

    private FieldType getIndexStoredNotAnalyzeType() {
        FieldType ft = new FieldType();
        ft.setStored(true);
        ft.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
        ft.freeze();
        return ft;
    }

    public void delete(String field, String value) throws Exception {
        IndexWriterConfig iwc = new IndexWriterConfig(new StandardAnalyzer());
        IndexWriter writer = new IndexWriter(directory, iwc);
        writer.deleteDocuments(new Term(field, value));
        writer.close();
    }
    
    public void deleteAll() throws Exception {
        IndexWriterConfig iwc = new IndexWriterConfig(new StandardAnalyzer());
        IndexWriter writer = new IndexWriter(directory, iwc);
        writer.deleteAll();
        writer.close();
    }

    public void rollback() throws Exception {
        IndexWriterConfig iwc = new IndexWriterConfig(new StandardAnalyzer());
        IndexWriter writer = new IndexWriter(directory, iwc);
        writer.rollback();
        writer.close();
    }

    public void mergeDelete() throws Exception {
        IndexWriterConfig iwc = new IndexWriterConfig(new StandardAnalyzer());
        IndexWriter writer = new IndexWriter(directory, iwc);
        writer.forceMergeDeletes();
        writer.close();
    }

}
