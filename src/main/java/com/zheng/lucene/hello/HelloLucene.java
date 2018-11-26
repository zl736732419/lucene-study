package com.zheng.lucene.hello;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * @Author zhenglian
 * @Date 2018/11/21
 */
public class HelloLucene {

    /**
     * 决定是新建文档还是更新文档
     */
    private boolean create = true;

    public void index(Path indexPath, Path docsPath) {
        IndexWriter writer = null;
        try {
            Directory directory = FSDirectory.open(indexPath);
            IndexWriterConfig iwc = new IndexWriterConfig(new StandardAnalyzer());
            if (create) {
                iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            } else {
                iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            }
            writer = new IndexWriter(directory, iwc);
            indexDocs(writer, docsPath);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != writer) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void indexDocs(final IndexWriter writer, Path path) throws IOException {
        if (Files.isDirectory(path)) {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    indexDocs(writer, file);
                    return FileVisitResult.CONTINUE;
                }
            });
        } else {
            indexDoc(writer, path, Files.getLastModifiedTime(path).toMillis());
        }
    }

    private void indexDoc(IndexWriter writer, Path file, long lastModified) throws IOException {
        InputStream inputStream = Files.newInputStream(file);
        // path
        Document doc = new Document();
        Field pathField = new StringField("path", file.toString(), Field.Store.YES);
        doc.add(pathField);
        // last modified time mill seconds
        doc.add(new LongPoint("modified", lastModified));
        // title
        doc.add(new TextField("title", file.getFileName().toString(), Field.Store.YES));
        // content
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        doc.add(new TextField("content", reader));
        if (writer.getConfig().getOpenMode() == IndexWriterConfig.OpenMode.CREATE) {
            writer.addDocument(doc);
        } else {
            // 如果存在，则将相同path的文档替换
            writer.updateDocument(new Term("path", file.toString()), doc);
        }
    }


    public void search(Path indexPath, String field, String value, Integer size) {
        IndexReader reader = null;
        try {
            FSDirectory directory = FSDirectory.open(indexPath);
            reader = DirectoryReader.open(directory);
            IndexSearcher searcher = new IndexSearcher(reader);
            QueryParser parser = new QueryParser(field, new StandardAnalyzer());
            Query query = parser.parse(value);
            TopDocs topDocs = searcher.search(query, size);
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            int docId;
            Document document;
            for (ScoreDoc doc : scoreDocs) {
                docId = doc.doc;
                document = searcher.doc(docId);
                String path = document.get("path");
                System.out.println("path: " + path);
                String title = document.get("title");
                System.out.println("title: " + title);
                float score = doc.score;
                System.out.println("doc score: " + score);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
