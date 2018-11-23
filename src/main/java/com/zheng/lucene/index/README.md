-----
##### Field.Store.YES/NO(存储域选项)

YES: 表示将字段的值保存到索引中，这样可以在查询时返回原始字段的值

NO: 表示不存储字段的值到索引中，那么在查询时无法得到原始字段的值，字段是否存储与我们能否在这个字段上进行查询时两码事儿，影响字段查询的是Field.Index

-----
##### Field.Index.*(索引域)

Index.ANALYZED: 进行分词，适用于标题，内容等

Index.NOT_ANALYZED: 索引，但是不分词，如身份证号，姓名，ID，适用于精确搜索

Index.ANALYZED_NOT_NORMS: 进行分词，但不存储norms信息，norms包含创建索引的时间和权值等信息，用于计算相关度分数，影响排序

Index.NOT_ANALYZED_NOT_NORMS: 不分词，同时不记录norms信息，精确查找不排序

Index.NO: 不进行索引，如此就无法进行查询

-----
但是在lecene7.5没有这个索引域选项了，取而代之的是定义的各种field对象,其中存储由StoredField表示，如果要索引，需要设置FieldType中的IndexOptions
参数