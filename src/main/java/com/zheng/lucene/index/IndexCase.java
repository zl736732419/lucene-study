package com.zheng.lucene.index;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

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
    private String[] contents = {"hello, i'm zhangsan", "hi, Jak", "what's your name?", "how old are you?", 
            "none of your business", "oh my god!"};
    // 附件，索引不分词存储
    private int[] attachs = {2,3,1,4,5,7};
    // 名称，索引不分词存储
    private String[] names = {"zhangsan", "lisi", "wangwu", "zhaoliu", "tianqi", "zhaoba"};

    private Directory directory;
    
    public IndexCase() {
        try {
            directory = FSDirectory.open(Paths.get("E:\\lucene\\index2"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void index() throws Exception {
        IndexWriterConfig iwc = new IndexWriterConfig(new StandardAnalyzer());
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter writer = new IndexWriter(directory, iwc);
        Document document;
        FieldType ft = getIndexStoredNotAnalyzeType();
        for (int i = 0 ; i < ids.length; i++) {
            document = new Document();
            Field id = new StoredField("id", ids[i]+"", ft);
            document.add(id);
            Field email = new StoredField("email", emails[i], ft);
            document.add(email);
            Field content = new TextField("content", contents[i], Field.Store.NO);
            document.add(content);
            Field attach = new StoredField("attach", attachs[i]+"", ft);
            document.add(attach);
            Field name = new StoredField("name", names[i], ft);
            document.add(name);
            writer.addDocument(document);
        }
        writer.close();
    }

    private FieldType getIndexStoredNotAnalyzeType() {
        FieldType ft = new FieldType();
        ft.setStored(true);
        ft.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
        ft.freeze();
        return ft;
    }

    public void search(String field, String value) throws Exception {
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);
        QueryParser parser = new QueryParser(field, new StandardAnalyzer());
        Query query = parser.parse(value);
        int size = 10;
        TopDocs topDocs = searcher.search(query, size);
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc sd : scoreDocs) {
            int docId = sd.doc;
            Document doc = searcher.doc(docId);
            for (IndexableField f : doc.getFields()) {
                System.out.println(f.name() + " : " + f.stringValue());
            }
        }

    }

}
