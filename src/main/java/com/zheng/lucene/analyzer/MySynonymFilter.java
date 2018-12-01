package com.zheng.lucene.analyzer;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.util.AttributeSource;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class MySynonymFilter extends TokenFilter {

    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    private final PositionIncrementAttribute posIncrAtt = addAttribute(PositionIncrementAttribute.class);

    private AttributeSource.State state;
    private Stack<String> stack = new Stack<>();
    
    protected MySynonymFilter(TokenStream input) {
        super(input);
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
        Set<String> synonyms = getSynonyMap(token);
        if (synonyms != null && synonyms.size() > 0) {
            state = captureState();
            stack.addAll(synonyms);
        }
        return true;
    }

    private Set<String> getSynonyMap(String token) {
        Map<String, Object> synonyMap = new HashMap<>();
        Set<String> set = new HashSet<>();
        set.add("love");
        set.add("enjoy");
        synonyMap.put("like", set);
        Set<String> synonyms = (Set<String>) synonyMap.get(token);
        return synonyms;
    }
}