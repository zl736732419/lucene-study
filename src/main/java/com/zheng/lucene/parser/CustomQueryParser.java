package com.zheng.lucene.parser;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.util.BytesRef;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 禁用模糊查询(fuzzy)、通配符查询(wildcard)、正则表达式查询(regex)
 * @Author zhenglian
 * @Date 2018/12/2
 */
public class CustomQueryParser extends QueryParser {

    public CustomQueryParser(String f, Analyzer a) {
        super(f, a);
    }

    @Override
    protected Query getWildcardQuery(String field, String termStr) throws ParseException {
        throw new ParseException("考虑到性能问题，通配符查询已经被禁用，您需要输入更精确的查询条件进行查询");
    }

    @Override
    protected Query getRegexpQuery(String field, String termStr) throws ParseException {
        throw new ParseException("考虑到性能问题，正则表达式查询已经被禁用，您需要输入更精确的查询条件进行查询");
    }

    @Override
    protected Query getFuzzyQuery(String field, String termStr, float minSimilarity) throws ParseException {
        throw new ParseException("考虑到性能问题，模糊查询已经被禁用，您需要输入更精确的查询条件进行查询");
    }

    @Override
    protected Query getRangeQuery(String field, String part1, String part2, boolean startInclusive, boolean endInclusive) throws ParseException {
        // title, size
        if (Objects.equals(field, "title")) {
            BytesRef start = getAnalyzer().normalize(field, part1);
            BytesRef end = getAnalyzer().normalize(field, part2);
            return new TermRangeQuery(field, start, end, startInclusive, endInclusive);
        } else if (Objects.equals(field, "id")){
            Integer start = Integer.parseInt(part1);
            Integer end = Integer.parseInt(part2);
            return IntPoint.newRangeQuery(field, start, end);
        } else if (Objects.equals(field, "date")) {
            String regexp = "\\d{4}-\\d{2}-\\d{2}";
            Pattern pattern = Pattern.compile(regexp);
            if (!pattern.matcher(part1).matches() 
                    || !pattern.matcher(part2).matches()) {
                throw new ParseException("日期格式不正确，请输入正确格式["+regexp+"]的日期进行查询");
            }
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date start = sf.parse(part1);
                Date end = sf.parse(part2);
                return LongPoint.newRangeQuery(field, start.getTime(), end.getTime());
            } catch (java.text.ParseException e) {
                throw new ParseException("解析日期格式字符串出错，错误原因: " + e.getMessage());
            }
        }
        return super.getRangeQuery(field, part1, part2, startInclusive, endInclusive);
    }
}
