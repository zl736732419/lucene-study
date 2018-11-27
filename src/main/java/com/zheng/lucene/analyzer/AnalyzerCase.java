package com.zheng.lucene.analyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

import java.io.IOException;
import java.io.StringReader;

/**
 * @Author zhenglian
 * @Date 2018/11/27
 */
public class AnalyzerCase {
    
    public void displayToken(String text, Analyzer analyzer) throws IOException {
        TokenStream stream = analyzer.tokenStream("content", new StringReader(text));
        stream.reset();
        CharTermAttribute cta = stream.addAttribute(CharTermAttribute.class);
        while (stream.incrementToken()) {
            System.out.print("[" + cta + "]");
        }
        System.out.println();
    }

    public void displayAllTokenInfo(String text, Analyzer analyzer) throws IOException {
        TokenStream stream = analyzer.tokenStream("content", new StringReader(text));
        stream.reset();
        CharTermAttribute cta = stream.addAttribute(CharTermAttribute.class);
        OffsetAttribute offset = stream.addAttribute(OffsetAttribute.class);
        PositionIncrementAttribute position = stream.addAttribute(PositionIncrementAttribute.class);
        TypeAttribute type = stream.addAttribute(TypeAttribute.class);
        while (stream.incrementToken()) {
            System.out.println(cta + ":" + position.getPositionIncrement() + "[" + offset.startOffset() + "-" + offset.endOffset() + "] " + type.type());
        }
    }
    
}
