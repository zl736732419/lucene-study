package com.zheng.lucene.analyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardTokenizer;

import java.io.Reader;

/**
 * @Author zhenglian
 * @Date 2018/12/1
 */
public class MySynonymAnalyzer extends Analyzer {
    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        final StandardTokenizer src = new StandardTokenizer();
        src.setMaxTokenLength(src.getMaxTokenLength());
        // token filter
        final TokenStream lowercase = new LowerCaseFilter(src);
        final TokenStream tok = new MySynonymFilter(lowercase, new SimpleSynonymContext());
        return new TokenStreamComponents(src, tok) {
            @Override
            protected void setReader(final Reader reader) {
                src.setMaxTokenLength(src.getMaxTokenLength());
                super.setReader(reader);
            }
        };
    }
    
}
