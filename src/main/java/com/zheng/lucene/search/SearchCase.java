package com.zheng.lucene.search;

import com.zheng.lucene.parser.CustomQueryParser;
import com.zheng.lucene.util.LuceneUtil;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.function.FunctionScoreQuery;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.DoubleValuesSource;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.util.BytesRef;

/**
 * @Author zhenglian
 * @Date 2018/11/25
 */
public class SearchCase {

    public void search(String field, String value) {
        IndexSearcher searcher = LuceneUtil.getInstance().getSearcher();
        QueryParser parser = new QueryParser(field, new StandardAnalyzer());
        int size = 10;
        try {
            Query query = parser.parse(value);
            searchAndPrint(searcher, query, size);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void printSearchResult(TopDocs topDocs, IndexSearcher searcher) throws Exception {
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        System.out.println("共查询到" + scoreDocs.length + "条记录");
        for (ScoreDoc sd : scoreDocs) {
            printDoc(sd, searcher);
        }
        System.out.println();
    }

    private void printDoc(ScoreDoc sd, IndexSearcher searcher) throws Exception {
        System.out.println("=========================");
        int docId = sd.doc;
        Document doc = searcher.doc(docId);
        for (IndexableField f : doc.getFields()) {
            System.out.println(f.name() + ":" + f.stringValue() + " ");
        }
        System.out.println("score:" + sd.score);
        System.out.println("doc:" + sd.doc);
    }

    public void docsInfo() {
        IndexSearcher searcher = LuceneUtil.getInstance().getSearcher();
        try {
            IndexReader reader = searcher.getIndexReader();
            System.out.println("实际可用的文档数：" + reader.numDocs());
            System.out.println("当前存在的所有文档数(包括删除的文档): " + reader.maxDoc());
            System.out.println("已经删除的文档数: " + reader.numDeletedDocs());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            LuceneUtil.getInstance().release(searcher);
        }
    }
    
    public void searchByTerm(String field, String value, int num) {
        IndexSearcher searcher = LuceneUtil.getInstance().getSearcher();
        Query query = new TermQuery(new Term(field, value));
        searchAndPrint(searcher, query, num);
    }

    /**
     * 文本范围查找
     * @param field
     * @param start
     * @param end
     * @param num
     * @throws Exception
     */
    public void searchByTermRange(String field, String start, String end, int num) {
        IndexSearcher searcher = LuceneUtil.getInstance().getSearcher();
        
        Query query = new TermRangeQuery(field, new BytesRef(start), new BytesRef(end), 
                true, true);
        searchAndPrint(searcher, query, num);
    }

    /**
     * 数字字段范围搜索
     * @param field
     * @param start
     * @param end
     * @param num
     * @throws Exception
     */
    public void searchByIntRange(String field, int start, int end, int num) {
        IndexSearcher searcher = LuceneUtil.getInstance().getSearcher();
        Query query = IntPoint.newRangeQuery(field, start, end);
        searchAndPrint(searcher, query, num);
    }

    /**
     * 前缀搜索
     * @param field
     * @param value
     * @param num
     * @throws Exception
     */
    public void searchByPrefix(String field, String value, int num) {
        IndexSearcher searcher = LuceneUtil.getInstance().getSearcher();
        Query query = new PrefixQuery(new Term(field, value));
        searchAndPrint(searcher, query, num);
    }

    /**
     * 通配符搜索
     * @param field
     * @param value 含通配符的表达式,*:任意字符, ?:一个字符
     * @param num
     * @throws Exception
     */
    public void searchByWildcard(String field, String value, int num) {
        IndexSearcher searcher = LuceneUtil.getInstance().getSearcher();
        Query query = new WildcardQuery(new Term(field, value));
        searchAndPrint(searcher, query, num);
    }

    /**
     * 组合查询
     * @throws Exception
     */
    public void searchByBool(int num) {
        IndexSearcher searcher = LuceneUtil.getInstance().getSearcher();
        Query query = new BooleanQuery.Builder().add(new BooleanClause(new TermQuery(new Term("name", "zhangsan")), BooleanClause.Occur.MUST))
                .add(new TermQuery(new Term("content", "zhangsan")), BooleanClause.Occur.MUST)
                .build();
        searchAndPrint(searcher, query, num);
    }

    public void searchByPhrase(int num) throws Exception {
        IndexSearcher searcher = LuceneUtil.getInstance().getSearcher();
        Query query = new PhraseQuery(1, "content", "hello", "my");
        searchAndPrint(searcher, query, num);
    }

    public void searchByFuzzy(int num) throws Exception {
        IndexSearcher searcher = LuceneUtil.getInstance().getSearcher();
        Query query = new FuzzyQuery(new Term("name", "zhangsem"), 2, 5);
        searchAndPrint(searcher, query, num);
    }

    private void searchAndPrint(IndexSearcher searcher, Query query, int num) {
        try {
            TopDocs topDocs = searcher.search(query, num);
            printSearchResult(topDocs,searcher);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            LuceneUtil.getInstance().release(searcher);
        }
    }

    /**
     * 原始做法，先查询出所有数据，然后内存中分页
     * 低效！会导致deep page问题
     * @param value
     * @param pageNo
     * @param pageSize
     * @throws Exception
     */
    public void searchByPage(String value, int pageNo, int pageSize) {
        System.out.println("search " + pageNo + " page.");
        IndexSearcher searcher = LuceneUtil.getInstance().getSearcher();
        try {
            QueryParser parser = new QueryParser("content", new StandardAnalyzer());
            Query query = parser.parse(value);
            int maxRow = 100;
            TopDocs topDocs = searcher.search(query, maxRow);
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            int docNum = scoreDocs.length;
            System.out.println("got " + docNum + " records");
            int total = maxRow >  docNum ? docNum : maxRow;
            int from = (pageNo - 1) * pageSize;
            if (from >= total) {
                return;
            }
            int end = pageNo * pageSize;
            end = end < total ? end : total;

            ScoreDoc sd;
            for (int i = from; i < end; i++) {
                sd = scoreDocs[i];
                printDoc(sd, searcher);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            LuceneUtil.getInstance().release(searcher);
        }
    }

    /**
     * 在lucene内部通过ScoreDoc.doc来截断文档结果，如此每次查询的时候就不必查询整个记录
     * 而是从指定的位置开始查询，避免deep-paging
     * if (score > after.score || (score == after.score && doc <= afterDoc)) {
     *     return;
     * }
     * score > after.score: 表示当前doc得分比after高，应该排在前面，也就是前一页的数据
     * score == after.score && doc <= afterDoc：比较两者的doc，doc在lucene中递增，doc比after小也表示排在当前doc前面，也在前一页
     * es中的scroll api就是采用的这个技术
     * @param after
     * @param value
     * @param size
     * @return
     * @throws Exception
     */
    public ScoreDoc scroll(ScoreDoc after, String value, int size) {
        IndexSearcher searcher = LuceneUtil.getInstance().getSearcher();
        try {
            QueryParser parser = new QueryParser("content", new StandardAnalyzer());
            Query query = parser.parse(value);
            TopDocs topDocs = searcher.searchAfter(after, query, size);
            printSearchResult(topDocs, searcher);
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            return scoreDocs[scoreDocs.length - 1];
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            LuceneUtil.getInstance().release(searcher);
        }
        return null;
    }

    /**
     * 根据文档索引顺序排序
     * @param field
     * @param value
     * @param size
     * @throws Exception
     */
    public void orderBySort(String field, String value, int size, Sort sort) {
        IndexSearcher searcher = LuceneUtil.getInstance().getSearcher();
        try {
            QueryParser parser = new QueryParser(field, new StandardAnalyzer());
            Query query = parser.parse(value);
            TopDocs topDocs;
            if (null != sort) {
                topDocs = searcher.search(query, size, sort);
            } else {
                topDocs = searcher.search(query, size);
            }
            printSearchResult(topDocs, searcher);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            LuceneUtil.getInstance().release(searcher);
        }
    }
    
    public void orderByBoostValue(String field, String value, int size, DoubleValuesSource boost) {
        IndexSearcher searcher = LuceneUtil.getInstance().getSearcher();
        try {
            QueryParser parser = new QueryParser(field, new StandardAnalyzer());
            Query q = parser.parse(value);
            
            // boosting query
            FunctionScoreQuery query = FunctionScoreQuery.boostByValue(q, boost);
            TopDocs topDocs = searcher.search(query, size);
            printSearchResult(topDocs, searcher);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            LuceneUtil.getInstance().release(searcher);
        }
    }

    public void orderByBoostQuery(Query q, Query boostMatch, float boost, int size) {
        IndexSearcher searcher = LuceneUtil.getInstance().getSearcher();
        // boosting query
        Query query = FunctionScoreQuery.boostByQuery(q, boostMatch, boost);
        searchAndPrint(searcher, query, size);
    }
    
    public void searchByCustomQueryParser(String field, String value, int size) throws Exception {
        IndexSearcher searcher = LuceneUtil.getInstance().getSearcher();
        QueryParser parser = new CustomQueryParser(field, new StandardAnalyzer());
        Query query = parser.parse(value);
        searchAndPrint(searcher, query, size);
    }

    public String highlight(String keyword, String text) throws Exception {
        // 要高亮的文本
//        MultiFieldQueryParser parser = new MultiFieldQueryParser(new String[] {"title", "content"}, new StandardAnalyzer());
        QueryParser parser = new QueryParser("title", new StandardAnalyzer());
        Query query = parser.parse(keyword);
        QueryScorer qs = new QueryScorer(query);
        Fragmenter f = new SimpleSpanFragmenter(qs, 20);
        Highlighter h = new Highlighter(qs);
        h.setTextFragmenter(f);
        String result = h.getBestFragment(new StandardAnalyzer(), "title", text);
        return result;
    }
    
}
