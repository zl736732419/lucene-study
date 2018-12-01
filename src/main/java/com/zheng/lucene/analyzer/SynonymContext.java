package com.zheng.lucene.analyzer;

import java.util.Set;

/**
 * @Author zhenglian
 * @Date 2018/12/1
 */
public interface SynonymContext {
    Set<String> getSynonymMap(String token);
}
