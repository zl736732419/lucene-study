package com.zheng.lucene.analyzer;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.StopwordAnalyzerBase;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;

import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author zhenglian
 * @Date 2018/11/27
 */
public class MyStopAnalyzer extends StopwordAnalyzerBase {
    
    private Set<String> stops = new HashSet<>();
    
    public MyStopAnalyzer() {
        addDefaultStopWords();
    }
    
    public MyStopAnalyzer(Set<String> stops) {
        this();
        this.stops.addAll(stops);
    }

    private void addDefaultStopWords() {
        CharArraySet defaultStopSet = EnglishAnalyzer.getDefaultStopSet();
        defaultStopSet.stream()
                .filter(word -> null != word)
                .forEach(word -> {
                    char[] chars = (char[]) word;
                    stops.add(new String(chars));
                });
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        // tokenizer
        final StandardTokenizer src = new StandardTokenizer();
        src.setMaxTokenLength(src.getMaxTokenLength());
        // token filter
        TokenStream tok = new LowerCaseFilter(src);
        tok = new StopFilter(tok, CharArraySet.copy(stops));
        
        return new TokenStreamComponents(src, tok) {
            @Override
            protected void setReader(final Reader reader) {
                src.setMaxTokenLength(src.getMaxTokenLength());
                super.setReader(reader);
            }
        };
    }
}
