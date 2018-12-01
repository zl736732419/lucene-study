package com.zheng.lucene.analyzer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @Author zhenglian
 * @Date 2018/12/1
 */
public class SimpleSynonymContext implements SynonymContext {
    private Map<String, Object> synonymMap;
    
    public SimpleSynonymContext() {
        initSynonymMap();
    }

    private void initSynonymMap() {
        synonymMap = new HashMap<>();
        Set<String> set = new HashSet<>();
        set.add("love");
        set.add("enjoy");
        synonymMap.put("like", set);
    }

    @Override
    public Set<String> getSynonymMap(String token) {
        Set<String> synonyms = (Set<String>) synonymMap.get(token);
        return synonyms;
    }
}
