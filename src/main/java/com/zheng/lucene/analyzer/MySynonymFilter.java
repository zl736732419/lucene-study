package com.zheng.lucene.analyzer;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.AttributeSource;

import java.io.IOException;
import java.util.Set;
import java.util.Stack;

public class MySynonymFilter extends TokenFilter {
    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    
    private AttributeSource.State state;
    private Stack<String> stack = new Stack<>();
    private SynonymContext ctx;
    
    protected MySynonymFilter(TokenStream input) {
        super(input);
    }
    
    public MySynonymFilter(TokenStream input, SynonymContext ctx) {
        this(input);
        this.ctx = ctx;
    }

    @Override
    public final boolean incrementToken() throws IOException {
        if (!stack.empty()) {
            String synonym = stack.pop();
            restoreState(state);
            termAtt.setEmpty();
            termAtt.append(synonym);
            return true;
        }
        
        if (!input.incrementToken()) {
            return false;
        }
        String token = termAtt.toString();
        Set<String> synonyms = ctx.getSynonymMap(token);
        if (synonyms != null && synonyms.size() > 0) {
            state = captureState();
            stack.addAll(synonyms);
        }
        return true;
    }
}