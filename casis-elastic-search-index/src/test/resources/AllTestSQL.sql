DELETE my_index

PUT /my_index
{
  "settings": {
    "analysis": {
      "filter": {
        "autocomplete_filter": {
          "type": "edge_ngram",
          "min_gram": 1,
          "max_gram": 10
        }
      },
      "analyzer": {
        "autocomplete": { 
          "type": "custom",
          "tokenizer": "standard",
          "filter": [
            "lowercase",
            "autocomplete_filter"
          ]
        }
      }
    }
  },
  "mappings": {
    "my_type": {
      "properties": {
        "text": {
          "type": "string",
          "analyzer": "autocomplete", 
          "search_analyzer": "standard" 
        }
      }
    }
  }
}

PUT my_index/my_type/1
{
  "text": "Quick Brown Fox" 
}

PUT my_index/my_type/2
{
  "text": "Quick           Brown             Fox" 
}

GET my_index/_search
{
  "query": {
    "match": {
      "text": "Fox "
    }
  }
}


GET my_index/_search
{
  "query": {
    "match": {
      "text": {
        "query": "Quick Br",
        "operator": "and"
      }
    }
  }
}

# just test 

GET /twitter/tweet/AVTNMf1bIntc1yL2eJ12



# Page my own test example:
DELETE  stack
POST /stack
{
  "settings": {
    "analysis": {
      "analyzer": {
        "my_analyzer": {
          "analyzer": "english",
          "tokenizer": "keyword",
          "filter": [
            "lowercase",
            "stop",
            "snowball"
          ],
          "char_filter": [
            "html_strip"
          ]
        }
      }
    }
  },
  "mappings": {
    "question": {
      "properties": {
        "id": {
          "type": "long"
        },
        "date": {
          "type": "date"
        },
        "info_my": {
          "type": "string",
          "analyzer": "my_analyzer"
        },
        "info_std": {
          "type": "string",
          "analyzer": "standard"
        },
        "display_name": {
          "type": "string",
          "fields": {
            "raw": {
              "type": "string",
              "index": "not_analyzed"
            }
          }
        },
        "tag":{
          "type": "string",
          "index": "not_analyzed"
        }
      }
    }
  }
}

  PUT  stack/question/1
{
  "id":1,
  "date":19880313,
  "info_my":"The dog is good",
  "info_std":"The dog is good",
   "tag":["Java","C++"," Python"]
}
PUT  stack/question/2
{
  "id":2,
  "date":19900313,
  "info_my":"The cat and dog are good",
  "info_std":"The cats and dogs are good"
}
PUT  stack/question/3
{
  "id":3,
  "date":20000313,
  "info_my":"The pig is bad",
  "info_std":"The pig is bad"
}
PUT  stack/question/4
{
  "id":3,
  "date":"2003-03-13",
  "info_my":"The pig is bad",
  "info_std":"The pig is bad",
  "tag":["Java","C++"," Python"]
}
#agg - histogram
GET stack/question/_search
{
  "size": 0, 
  "aggs": {
    "date_difference": {
      "histogram": {
        "field": "id",
        "interval": 900313
      }
    }
  }
}

# aggs - terms
GET /stack/_search
{
  "size": 0, 
  "aggs": {
    "interst_ranking": {
      "terms": {
        "field": "info_std",
        "size": 10
      }
    }
  }
}

#query-match 
GET /stack/_search
{
  "query": {
    "match": {
      "tag": "Java"
    }
  }
}

# query-math -query
GET /stack/_search
{
  "query": {
    "match": {
      "info_std": {
        "type": "boolean", 
        "query": "The",
        "analyzer" : "standard",
        "minimum_should_match" : "1%"
      }
    }
  }
}

# multi_match
GET /stack/_search
{
  "query": {
    "multi_match": {
      "fields": [
        "tag",
        "info_std"
      ],
      "query": "and Java",
      "type" : "best_fields"
    }
  }
}

#Query: bool
GET /stack/questions/_search
{
  "query": {
    "bool": {
      "must": [
        {
          "match": {
            "tag": "C++"
          }
        },
        {
          "match": {
            "id": "1"
          }
        }
      ],
      "must_not": [
        {
          "match": {
            "id": "2"
          }
        }
      ],
      "should": [
        {
          "match": {
            "tag": "C++"
          }
        }
      ],
      "filter": [
        {
          "term": {
            "tag": "java"
          }
        }
      ]
    }
  }
}

# Query Range Date
GET stack/question/_search
{
  "query": {
    "bool": {
      "should": {
        "match": {
          "info_std": "and"
        }
      },
      "filter": {
        "range": {
          "date": {
            "gte": "19140910",
            "lte": "20140912"
          }
        }
      }
    }
  }
}


#Bool queries may be nested
GET /stack/questions/_search
{
  "query": {
    "bool": {
      "must": [
        {
          "bool": {
            "must": [
              {
                "match": {
                  "body": "ruby"
                }
              }
            ],
            "should": [
              {
                "match": {
                  "title": "ruby on rails"
                }
              }
            ]
          }
        }
      ],
      "must_not": [
        {
          "term": {
            "tags": "grails"
          }
        }
      ],
      "filter": {
        "range": {
          "comments": {
            "gte": 5,
            "lte": 40
          }
        }
      }
    }
  }
}

# sorting 
GET stack/question/_search
{
  "query": {
    "match": {
      "tag": "Java"
    }
  },
  "sort": [
    {
      "id": {
        "order": "asc"
      }
    },
    {
      "date": {
        "order": "desc"
      }
    }
  ]
}
#Highlighting 225
GET /stack/question/_search
{
  "query": {
    "match": {
      "info_std": "pig is bad" 
    }
  },
  "highlight": {
    "fields": {
      "info_std": {}
    }
  }
}

#227 Search for all questions showing newest ones first 
GET /stack/question/_search
{
  "sort": [
    {
      "date": {
        "order": "desc"
      }
    }
  ]
}

# 230 Find all questions that mention "elasticsearch" in the title
#     Then in addition to the title narrow the results to only
#     include ones that mention "exception" in the body
# method1
GET /stack/question/_search
{
  "query": {
    "match": {
      "info_std": "pig"
    }
  },"post_filter": {
    "query": {
      "match": {
        "tag": "Java"
      }
    }
  }
}
# method2
GET /stack/question/_search
{
  "query": {
    "bool": {
      "must": [
        {
          "match": {
            "info_std": "pig"
          }
        },
        {
          "match": {
            "tag": "Java"
          }
        }
      ]
    }
  }
}

# 232 Find all questions that have the phrase "java object" in the title
# Then expand the search to include more results by adding some slop
GET /stack/question/_search
{
  "query": {
    "match": {
      "info_std": {
        "type": "phrase", 
        "query": "The  bad",
        "slop":2
      }
    }
  }
}

#234 Add highlighting to the last query from exercise 232 
GET /stack/question/_search
{
  "query": {
    "match_phrase": {
      "info_std": {
        "query": "pig"
      }
    }
  },
  "highlight": {
    "fields": {
      "tag":"C++"
    }
  }
}

# 236 Find all questions and answers posted on 2014-09-10 to 2014-09-12
# method1 
GET /stack/question/_search
{
  "post_filter": {
    "range": {
      "date": {
        "gte": 19900920,
        "lte": 20102010
      }
    }
  }
}
# method2
GET /stack/question/_search
{
"query": {
  "range": {
    "date": {
      "gte": 1900,
      "lte": 2000
    }
  }
}
}
# method3
GET /stack/question/_search
{
"query": {
  "bool": {
    "filter": {
      "range": {
        "date": {
          "gte": 1900,
          "lte": 2000
        }
      }
    }
  }
}
}


# 239 Find questions which mention "android" in the title but are not tagged with "android"
GET /stack/question/_search
{
"query": {
  "bool": {
    "must": [
      {"match": {
        "tag": "Java"
      }}
    ],
    "must_not": [
      {"match": {
        "info_std": "pig"
      }}
    ]
  }
}
}

# 241 In exercise 6 we got back documents like the following
GET /stack/question/_search
{
"query": {
  "bool": {
    "must": [
      {"match": {
        "tag": "Java"
      }}
    ],
    "must_not": [
      {"fuzzy": {
        "info_std": "good"
      }}
    ]
  }
}
}


#271 
GET /stack/_search
{
  "size": 0,
  "suggest": {
    "title_suggestions": {
      "text": "the dog is",
      "term": {
        "field": "info_my",
        "suggest_mode": "always",
        "size": 2,
        "sort": "score"
      }
    }
  }
}
# 278  Suggesters - Completion- Requires custom mapping
DELETE  /music
PUT /music/
{
  "name":"love you"
}
GET /music/
PUT /music/song/_mapping
{
  "song": {
    "properties": {
      "name": {
        "type": "string"
      },
      "my_suggest": {
        "type": "completion",
        "analyzer": "simple",
        "payloads": true
      }
    }
  }
}

PUT /music/song/1
{
  "name": "Lithium",
  "my_suggest": {
    "input": [
      "Lithium",
      "Nirvana"
    ],
    "output": "Nirvana - Lithium",
    "payload": {
      "artistId": 2321
    },
    "weight": 34
  }
}

#280
POST /music/_suggest
{
  "song-suggest": {
    "text": "n",
    "completion": {
      "field": "my_suggest"
    }
  }
}
#281
POST /music/_suggest?pretty
{
  "song-suggest": {
    "text": "nurvun",
    "completion": {
      "field": "my_suggest",
      "fuzzy": {
        "fuzziness": 2
      }
    }
  }
}

# 291 Aggregation

# 299 aggregation scope and two kinds of filter

# The aggregation will be calculated over all documents 
#  with "Java" in the body and a comment_count of 10
GET stack/question/_search
{
  "query": {
    "bool": {
      "must": {
        "match": {
          "tag": "Java"
        }
      },
      "filter": {
        "term": {
          "id": "1"
        }
      }
    }
  },
  "aggregations": {
    "java_tags": {
      "terms": {
        "field": "tag"
      }
    }
  }
}

# The search hits will include all documents with
# "Java" in the body and comment_count of 10
GET stack/question/_search
{
  "query": {
    "match": {
      "tag": "Java"
    }
  },
  "post_filter": {
    "term": {
      "id": "1"
    }
  },
  "aggregations": {
    "java_tags": {
      "terms": {
        "field": "tag"
      }
    }
  }
}


#####################################################################################
####################### #88 start Text Analysis ****************************************************************************************************************************
#106
GET _analyze/
{
  "tokenizer": "keyword",
  "text": "New York"
}

#107
GET _analyze/
{
  "tokenizer" : "keyword",
  "filters" : ["lowercase"],
  "text" : "New York"
}

# 108
GET _analyze/
{
"tokenizer" : "standard",
"text" : "The quick fox jumped"
}

#110
GET _analyze/
{
  "tokenizer" : "whitespace",
  "filters" : ["lowercase", "stop"],
  "text" : "The quick fox jumped"
}

#page 111
DELETE  my_index
PUT my_index
{
  "settings": {
    "analysis": {
      "analyzer": {
        "my_analyzer": {
          "tokenizer": "whitespace",
          "filter": [
            "lowercase",
            "stop",
            "snowball"
          ]
        }
      }
    }
  }
}
GET my_index

GET my_index/_analyze/
{
  "analyzer" : "my_analyzer",
  "text" : "The quick fox jumped"
}

GET _analyze/
{
  "analyzer": "keyword",
  "tokenizer" : "whitespace",
  "filters": [
    "lowercase",
    "stop",
    "snowball"
  ],
  "text": "The quick fox jumped"
}

#114 Mapping Character Filter
DELETE  my_index
PUT my_index
{
  "settings": {
    "analysis": {
      "char_filter": {
        "&_to_and": {
          "type": "mapping",
          "mappings": [
            "& => and"
          ]
        }
      },
      "analyzer": {
        "custom_with_char_filter": {
          "tokenizer": "standard",
          "filter":["lowercase"],
          "char_filter": [
            "&_to_and"
          ]
        }
      }
    }
  }
}
GET my_index
GET my_index/_analyze/
{
  "analyzer" : "custom_with_char_filter",
  "text" : "The quick fox & & & & dog jumped "
}
#115 Pattern Replacement Character Filter

# 116 My own analysis
PUT /my_index
{
  "settings": {
    "analysis": {
      "char_filter": {
        "&_to_and": {
          "type": "mapping",
          "mappings": [
            "& => and"
          ]
        }
      },
      "filter": {
        "my_stopwords": {
          "type": "stop",
          "stopwords": [
            "the",
            "a"
          ]
        }
      },
      "analyzer": {
        "my_analyzer": {
          "type": "custom",
          "char_filter": [
            "html_strip",
            "&_to_and"
          ],
          "tokenizer": "standard",
          "filter": [
            "lowercase",
            "my_stopwords"
          ]
        }
      }
    }
  }
}

GET _analyze/
{
  "tokenizer":"keyword",
  "text":"It is unlikely that I'm especially good at analysis yet."
}

GET _analyze/
{
  "tokenizer": "whitespace",
  "text": "The quick fox jumped"
}
GET _analyze/
{
"tokenizer" : "keyword",
"filters" : ["lowercase"],
"text" : "New York"
}

GET _analyze/
{
"tokenizer" : "whitespace",
"filters" : ["lowercase", "stop"],
"text" : "The quick fox jumped"
}


# 118 Synonyms - Embedded
# “jump” and “hop” are now synonymous
# "quick" is a synonym with "fast", but "fast" is not with "quick"
PUT my_index
{
  "settings": {
    "analysis": {
      "filter": {
        "my_domain_synonyms": {
          "type": "synonym",
          "synonyms": [
            "jump, hop",
            "quick => fast"
          ]
        }
      },
      "analyzer": {
        "my_analyzer": {
          "tokenizer": "whitespace",
          "filter": [
            "lowercase",
            "stop",
            "snowball",
            "my_domain_synonyms"
          ]
        }
      }
    }
  }
}

# 119 Synonyms - From File
PUT /my_index
{
  "settings": {
    "analysis": {
      "analyzer": {
        "my_analyzer": {
          "tokenizer": "whitespace",
          "filter": [
            "lowercase",
            "stop",
            "snowball",
            "my_domain_synonyms"
          ]
        }
      },
      "filter": {
        "my_domain_synonyms": {
          "type": "synonym",
          "synonyms_path": "/path/to/elasticsearch/config/my_synonyms.txt"
        }
      }
    }
  }
}

# 126  -1-analyze the following text
GET _analyze/
{
  "tokenizer":"standard",
  "text":"It is unlikely that I'm especially good at analysis yet."
}

# 128  -2-add the lowercase token filter
GET _analyze/
{
  "tokenizer":"standard",
  "filters":"lowercase",
  "text":"It is unlikely that I'm especially good at analysis yet."
}

# 130  -3- Now add the snowball stemmer to the token filter.
GET _analyze/
{
  "tokenizer":"standard",
  "filters":["lowercase","snowball"],
  "text":"It is unlikely that I'm especially good at analysis yet."
}
# 132 -4- Now compare the results of the same string using the english analyzer and 
# the standard analyzer
# answer : Notice that the English analyzer removed stop words?
GET _analyze/
{
  "analyzer":"english",
  "text":"this is a good dog."
}

GET _analyze/
{
  "analyzer":"standard",
  "text":"this is a good dog.."
}

#134 -5- Create your own analyzer
POST /analyzertest/
{
  "settings":{
    "analysis":{
      "analyzer":{
        "my_analyzer":{
          "tokenizer":"standard",
          "filter":["lowercase","stop"]
        }
      }
    }
    
  }
}

GET analyzertest/_analyze
{
  "text":"<h1>It is unlikely that I'm especially good at analysis yet.</h1>",
  "analyzer":"my_analyzer"
}



# 137 -7-create a new analyzer called “analyzertest" with an “html_strip” char_filter
DELETE /analyzertest
POST /analyzertest/
{
  "settings":{
    "analysis":{
      "analyzer":{
        "my_analyzer":{
          "tokenizer":"standard",
          "filter":["lowercase","stop"],
          "char_filter":["html_strip"]
        }
      }
    }
    
  }
}

GET analyzertest/_analyze
{
  "text":"<h1>It is unlikely that I'm especially good at analysis yet.</h1>",
  "analyzer":"my_analyzer"
}


#139 -8-Create a filter called “my_edgengram_filter”
POST /ngramtest
{
  "settings":{
    "analysis":{
      "analyzer":{
        "my_analyzer":{
          "tokenizer":"standard",
          "filter":["lowercase","my_edgengram_filter"]
        }
      },
      "filter":{
        "my_edgengram_filter":{
          "type":"edgeNGram",
          "min_gram":2,
          "max_gram":6
        }
      }
    }
    
  }
}
GET ngramtest/_analyze
{
  "text":"I am probably pretty good at analyzers now",
  "analyzer":"my_analyzer"
}
GET ngramtest/_analyze
{
  "text":"Iamdogssdf heispig",
  "analyzer":"my_analyzer"
}






#####################################################################################
####################### #140 Mapping ****************************************************************************************************************************
#145 Dynamic Mappings -strict
PUT my_index/my_type/_mappings
{
  "dynamic": "strict",
  "properties": {
    "obj1": {
      "type": "object",
      "dynamic": true,
      "properties": {
        "obj2": {
          "type": "object",
          "dynamic": false,
          "properties": {
            "str3": {
              "type": "string"
            }
          }
        }
      }
    }
  }
}

PUT my_index/my_type/1
{
  "str1": "this field will fail the entire request",
  "obj1": {
    "str2": "this field would be dynamically added to mapping",
    "obj2": {
      "str3": "this field is already mapped",
      "str4": "this field would be ignored in mapping, but no error"
    }
  }
}

#148  Mapping API
PUT /stack
{
  "settings": {
    "number_of_replicas": 0
  },
  "mappings": {
    "question": {
      "properties": {
        "body": {
          "type": "string",
          "analyzer": "html_strip"
        }
      }
    }
  }
}

# Get the current mappings of a specific type
GET stack/_mapping/question

# 149 Dynamically update the mapping
PUT stack/_mapping/question
{
  "properties": {
    "app_version": {
      "type": "string"
    }
  }
}

# 150 Basic Mapping 
POST stack
{
  "mappings": {
    "question": {
      "properties": {
        "accepted_answer_id": {
          "type": "long"
        },
        "answer_count": {
          "type": "long"
        },
        "body": {
          "type": "string",
          "analyzer": "html_strip"
        }
      }
    }
  }
}

# 158 Object fields --Two fields in the owner object
# owner.display_name
PUT stack/_mapping/question
{
  "properties": {
    "owner": {
      "properties": {
        "account_id": {
          "type": "string"
        },
        "display_name": {
          "type": "string",
          "fields": {
            "raw": {
              "type": "string",
              "analyzer": "keyword"
            }
          }
        },
        "id": {
          "type": "string"
        },
        "location": {
          "type": "string"
        }
      }
    }
  }
}

# 160 Multi-fields - Enables indexing a single document field in multiple ways
# Use "display_name" for search Use
# "display_name.raw" for aggs/sorting

POST stack
{
  "mappings": {
    "question": {
      "properties": {
        "display_name": {
          "type": "string",
          "fields": {
            "raw": {
              "type": "string",
              "index": "not_analyzed"
            }
          }
        }
      }
    }
  }
}
#168 Dynamic field mapping
PUT /test
{
  "mappings": {
    "my_type": {
      "dynamic_templates": [
        {
          "my_integer_fields": {
            "match": "i_*",
            "mapping": {
              "type": "integer"
            }
          }
        },
        {
          "my_multi_strings": {
            "match_mapping_type": "string",
            "mapping": {
              "type": "string",
              "index": "analyzed",
              "fields": {
                "sort": {
                  "type": "string",
                  "analyzer": "alpha_sort"
                },
                "raw": {
                  "type": "string",
                  "index": "not_analyzed"
                }
              }
            }
          }
        }
      ]
    }
  }
}
# 170 Index Templates
PUT _template/my_data_template
{
  "template": "my_data*",
  "settings": {
    "number_of_shards": 1
  },
  "mappings": {
    "_default_": {
      "_all": {
        "enabled": false
      }
    }
  },
  "order": 10
}


# 174 -1- Fetch all mappings for the "stack" index
GET /stack/_mapping

# 176 -2- Update the "question" mapping and add some metadata to the mapping
PUT /stack/question/_mapping
{
  "_meta": {
    "comment": "Metadata is fun!"
  }
}

# 178 -3- Update  a long instead of a string.
# answer : can not do it 
#  Update  search_analyzer "keyword" instead of the default (which is "standard")
# answer : override search_analyzer


# 181 -4-4 Update  "last_editor_display_name" to be back to "standard"
# need to revert this so future queries run properly

# 183 -5- Create a new index template called "mystack_wildcard"

# 185 -6- Prove that your "mystack_wildcard" template exists


#####################################################################################
####################### #189 Search 2.0  ****************************************************************************************************************************
# Query: match_all
GET /casis/item/_search/
{
  "query": {
    "match_all": {}
  }
}
# Implicit if not supplied
GET /casis/item/1

# 197 term and match

# match is used well
GET /casis/item/_search
{
  "_source":"UPD", 
  "query": {
   "match": {
     "COUNTRY": "AE"
   }
  }
}

#Term not used in text , only Structured search on numbers, dates, ranges and enumerations
GET /casis/item/_search
{
  "_source":"UPD", 
  "query": {
    "term": {
      "UPD": {
        "value": "20011128"
      }
    }
  }
}

#199 Query: match
GET /casis/item/_search
{
  "_source": "UPD",
  "query": {  "match": {
    "COUNTRY": {
      "query": "AE",
      "analyzer": "my_synonym_analyzer"
    }}

  }
}


#####################################################################################
####################### #243 Search Internal  ****************************************************************************************************************************


#####################################################################################
####################### #266  Suggest  ****************************************************************************************************************************


#####################################################################################
####################### #291  aggregation  ****************************************************************************************************************************





#####################################################################################
####################### twitter ******************************************************************************************************************************

GET /twitter/_validate/query?q=user:foo
{"valid":true,"_shards":{"total":1,"successful":1,"failed":0}}

PUT /twitter/tweet/1
{

    "user" : "kimchy",

    "post_date" : "2009-11-15T14:12:12",

    "message" : "trying out Elasticsearch"

  }

GET /twitter/tweet/_search/exists?q=user:kimchy


GET /twitter/tweet/_search/exists
{

    "query" : {

        "term" : { "user" : "kimchy" }

    }

}
GET /twitter/tweet/_count
{
  "query": {
    "term": {
      "user": "kimchy"
    }
  }
}
GET /twitter/tweet/_search
{
    "sort" : [
        { "price" : {"missing" : "_last"} }
    ],
    "query" : {
        "term" : { "user" : "kimchy" }
    }
}

GET /twitter/tweet/_search
{
  "query": {
    "term": {
      "user": "kimchy"
    }
  },
  "sort": [
    {
      "post_date": {
        "order": "asc"
      }
    }
  ]
}
GET /twitter/tweet/_search?q=user:kimchy

GET /_all/tweet/_search?q=tag:wow

GET /kimchy,elasticsearch/tweet/_search?q=tag:wow


GET /twitter/_search?q=user:kimchy
GET /twitter/tweet,user/_search?q=user:kimchy

POST /twitter/tweet?routing=kimchy
{
  "user": "kimchy",
  "postDate": "2009-11-15T14:12:12",
  "message": "trying out Elasticsearch"
}
GET /twitter/tweet/_search?routing=kimchy
{
  "query": {
    "bool": {
      "must": {
        "query_string": {
          "query": "trying out Elasticsearch"
        }
      },
      "filter": {
        "term": {
          "user": "kimchy"
        }
      }
    }
  }
}


GET /twitter/tweet/_termvectors
{

  "doc" : {

    "fullname" : "John Doe",

    "text" : "twitter test test test"

  }

}

GET /twitter/tweet/1/_termvectors?pretty=true
{

  "fields" : ["text", "some_field_without_term_vectors"],

  "offsets" : true,

  "positions" : true,

  "term_statistics" : true,

  "field_statistics" : true

}


PUT /twitter/
{
  "mappings": {
    "tweet": {
      "properties": {
        "text": {
          "type": "string",
          "term_vector": "with_positions_offsets_payloads",
          "store": true,
          "analyzer": "fulltext_analyzer"
        },
        "fullname": {
          "type": "string",
          "term_vector": "with_positions_offsets_payloads",
          "analyzer": "fulltext_analyzer"
        }
      }
    }
  },
  "settings": {
    "index": {
      "number_of_shards": 1,
      "number_of_replicas": 0
    },
    "analysis": {
      "analyzer": {
        "fulltext_analyzer": {
          "type": "custom",
          "tokenizer": "whitespace",
          "filter": [
            "lowercase",
            "type_as_payload"
          ]
        }
      }
    }
  }
}
GET /twitter/tweet/1/_termvectors?pretty=true
{

  "fields" : ["text"],

  "offsets" : true,

  "payloads" : true,

  "positions" : true,

  "term_statistics" : true,

  "field_statistics" : true

}

PUT /twitter/tweet/1?pretty=true
{
  "fullname": "John Doe",
  "text": "twitter test test test "
}


PUT /twitter/tweet/2?pretty=true
{

  "fullname" : "Jane Doe",

  "text" : "Another twitter test ..."

}

GET /twitter/tweet/1/_termvectors?fields=text,...

GET /twitter/tweet/1/_termvectors?pretty=true


GET /test/_mget
{
  "docs": [
    {
      "_type": "type",
      "_id": "1"
    },
    {
      "_type": "type",
      "_id": "2"
    }
  ]
}

GET /_mget
{
  "docs": [
    {
      "_index": "test",
      "_type": "type",
      "_id": "1"
    },
    {
      "_index": "test",
      "_type": "type",
      "_id": "2"
    }
  ]
}

GET /twitter/tweet/1?fields=title,content
GET /twitter/tweet/1/_source
GET /twitter/tweet/1/_source?_source_include=*.id&_source_exclude=entities

GET /twitter/tweet/1?_source=false
GET /twitter/tweet/1?_source_include=*.id&_source_exclude=entities
GET /twitter/tweet/1?_source=*.id,retweeted

PUT /twitter/tweet/1?timestamp=2009-11-15T14%3A12%3A12
{

    "user" : "kimchy",

    "message" : "trying out Elasticsearch"

}


POST /twitter/tweet?routing=kimchy
{

    "user" : "kimchy",

    "post_date" : "2009-11-15T14:12:12",

    "message" : "trying out Elasticsearch"

}


PUT /twitter/tweet/1
{
  "user": "kimchy",
  "post_date": "2009-11-15T14:12:12",
  "message": "trying out Elasticsearch"
}
PUT /twitter/tweet/1?version=12
{

    "message" : "elasticsearch now has versioning support, double cool!"

}
PUT /twitter/tweet/1?op_type=create
{

    "user" : "kimchy",

    "post_date" : "2009-11-15T14:12:12",

    "message" : "trying out Elasticsearch"

}
PUT /twitter/tweet/1/_create
{

    "user" : "kimchy",

    "post_date" : "2009-11-15T14:12:12",

    "message" : "trying out Elasticsearch"

}

#####################################################################################
####################### twitter ******************************************************************************************************************************    
# practice the APIs
GET /_cat/health?v
GET /_cat/nodes?v
GET /_cat/indices?v
DELETE /customer
PUT /customer?pretty

PUT /customer/external/1
{
  "name":"John Doe"
}
DELETE /customer?pretty
GET /_cat/indices?v
GET /_nodes/stats/process?pretty

#####################################################################################
####################### aggration of the ES ******************************************************************************************************************************    
GET /casis/item/1
GET /casis
# aggregations example1 
GET /casis/item/_search
{
  "size": 0,
  "aggs": {
    "CO_VS_COUNTRY": {
      "filters": {
        "filters": {
          "CO": {
            "match": {
              "CO": "FLOW PHAR."
            }
          }
    }
  }
}}}

# 328 Filters With Average Aggregation
# Get an average on another field from the buckets that were created
GET /casis/item/_search
{
  "size": 0,
  "aggs": {
    "CO_VS_COUNTRY": {
      "filters": {
        "filters": {
          "CO": {
            "match": {
              "CO": "FLOW PHAR."
            }
          },
          "COUNTRY": {
            "match": {
              "COUNTRY": "PK"
            }
          }
        }
      },
      "aggs": {
        "average_number": {
          "avg": {
            "field": "Document.PackInfo.NumberOfIngredients"
          }
        }
      }
    }
  }
}

# 318 Histogram Aggregation -- onle long , date
GET /casis/item/_search
{
  "size": 0,
  "aggs": {
      "comment_ranges": {
        "range": {
          "field": "Document.PackInfo.NumberOfIngredients",
          "ranges": [
            {
              "key":"first",
              "to": 1
            },
            {
               "key":"Second",
              "from": 1,
              "to": 2
            },
            {
              "key":"Third",
              "from": 3
            }
          ]
        }
      },
      "comment_counts_fixed": {
      "histogram": {
        "field": "Document.PackInfo.NumberOfIngredients",
        "interval": 2
      }
      }
  }
}

# 308 Find minimum,maximum,stats,extended stats value for a field
# 313 top 5 terms, 
# 317 Terms Aggregation Order , 
GET /casis/item/_search
{
  "size": 0,
  "aggs": {
    "newest": {
      "max": {
        "field": "UPD"
      }
    },
    "oldest": {
      "min": {
        "field": "UPD"
      }
    },
    "UPD_stats": {
      "stats": {
        "field": "UPD"
      }
    },
    "UPD_ext_stats": {
      "extended_stats": {
        "field": "UPD"
      }
    },
    "popular_tags": {
      "terms": {
        "field": "Document.LaunchDetails.CASIS-USE.CASIS-IND.Indication",
        "size": 5,
        "shard_size": 20,
        "show_term_doc_count_error": true,
        "order": {
          "_term": "dsc"
        }
      }
    }
  }
}

#307  Calculate the sum of a field -- only long ,date
GET /casis/item/_search
{
  "size": 0,
  "aggs": {
    "List_all":{
      "terms": {
       "field": "Document.PackInfo.NumberOfIngredients"
      }
    },
    "total_views": {
      "sum": {
        "field": "Document.PackInfo.NumberOfIngredients"
      }
    }
  }
} 

#303 aggregation by year interval = 1 year ,and more aggregations
GET /casis/item/_search
{
  "size":0, 
  "aggs": {
    "my_date_histo": {
      "date_histogram": {
        "field": "UPD",
        "interval": "year"
      },
      "aggs": {
        "top_THE_USE": {
          "terms": {
            "field": "THE_USE"
          },
          "aggs": {
            "views": {
              "sum": {
                "field": "PART"
              }
            }
          }
        }
      }
    }
  }
}
# 303 aggregation by year interval = 1 year
GET /casis/item/_search
{
  "size":10, 
  "aggs": {
    "my_date_histo": {
      "date_histogram": {
        "field": "UPD",
        "interval": "year"
      }
    }
  }
}
# query-bool-match-post_filter-aggregation
GET /casis/item/_search
{
  "query": {
    "match": {
      "STATUS":"CORPORATION"
    }
  },
  "aggregations": {
    "countries": {
      "terms": {
        "field": "COUNTRY"
      }
    }
  }
}

# paging-query-bool-match-filter-aggregation
GET /casis/item/_search
{
  "from": 0, 
  "size": 1, 
  "query": {
    "bool": {
      "must": {
        "match": {
          "Document.LaunchDetails.CASIS-CO.Manufacturer": "Vitalhealth Pharma"
        }
      },
      "filter": {
        "range": {
          "UPD": {
            "gte": 19000920,
            "lte": 21130930
          }
        }
      }
    }
  },"sort": [
    {
      "UPD": {
        "order": "desc"
      }
    }
  ],
  "aggregations": {
    "DOCNO_stats": {
      "terms": {
        "field": "UPD"
      }
    }
  }
}

# Basic Aggregation as string
GET /casis/item/_search
{
  "from": 0,
  "size": 0,
  "aggregations": {
    "countries": {
      "terms": {
        "field": "COUNTRY"
      }
    }
  }
}

# Basic Aggregation as long
GET /casis/item/_search
{
  "size": 0,
  "from":6,
  "aggs": {
    "the data stats": {
      "stats": {
        "field": "PART"
      }
    }
  }
}

# Basic Aggregation as date
GET /casis/item/_search
{
  "size": 0,
  "from":6,
  "aggs": {
    "the data stats": {
      "stats": {
        "field": "Document.CASIS-UPD.PublicationDate.CCYYMMDD"
      }
    }
  }
}

# get item as the specific element and see the specific area -- String     
GET /casis/item/_search
{
  "_source":["Document.LaunchDetails.Ingredients.Ingredient", 
            "Document.LaunchDetails.CASIS-DSTA.LaunchDate.CCYYMM"],
  "query": {
    "match": {
      "Document.LaunchDetails.Ingredients.Ingredient":"various vitamins and minerals"
    }
  }
}

# get item as the specific element -- date
GET /casis/item/_search
{
  "query": {
    "match": {
      "Document.LaunchDetails.CASIS-DSTA.LaunchDate.CCYYMM": 20000101
    }
  }
}

# get all the data
GET /casis/item/_search
{
  "query": {
    "match_all": {}
  }
}

# casis paging
GET /casis/item/_search
{
  "from": 5,
  "size": 1
}


#  matching all  elements
POST /casis/item/_search
{
  "from": 0, 
  "size": 1, 
  "_source": [
"DOCNO","SRC_DB","PART","UPD","DATEINSERTED","CO","COUNTRY","STATUS","CN","DSTA","THE_USE","CODE","SOURCE","CASNO","MDNUMBER","Document","Document.CASIS-DOCNO","Document.CASIS-UPD","Document.CASIS-UPD.PublicationDate","Document.LaunchDetails","Document.LaunchDetails.LaunchDateComment","Document.LaunchDetails.NewChemicalEntity","Document.LaunchDetails.Ingredients","Document.LaunchDetails.Ingredients.Ingredient","Document.LaunchDetails.CASIS-USE","Document.LaunchDetails.CASIS-USE.CASIS-ACT","Document.LaunchDetails.CASIS-USE.CASIS-ACT.Class","Document.LaunchDetails.CASIS-USE.CASIS-ACT.Class.ClassCode","Document.LaunchDetails.CASIS-USE.CASIS-ACT.Class.ClassDescription","Document.LaunchDetails.CASIS-USE.CASIS-IND","Document.LaunchDetails.CASIS-USE.CASIS-IND.Indication","Document.LaunchDetails.CASIS-CN","Document.LaunchDetails.CASIS-CN.BrandName","Document.LaunchDetails.CASIS-CO","Document.LaunchDetails.CASIS-CO.CASIS-NORMALIZED-CO=20","Document.LaunchDetails.CASIS-CO.Manufacturer","Document.LaunchDetails.CASIS-CO.Corporation","Document.LaunchDetails.CASIS-DSTA","Document.LaunchDetails.CASIS-DSTA.CASIS-NORMALIZED-DSTA","Document.LaunchDetails.CASIS-DSTA.Country","Document.LaunchDetails.CASIS-DSTA.LaunchDate","Document.LaunchDetails.CASIS-TX","Document.LaunchDetails.CASIS-TX.Biotech","Document.LaunchDetails.CASIS-TX.Unbranded","Document.LaunchDetails.CASIS-RN","Document.LaunchDetails.CASIS-RN.CASInfo","Document.LaunchDetails.CASIS-RN.CASInfo.CASItem","Document.PackInfo","Document.PackInfo.ExcipientInfo","Document.PackInfo.ExcipientInfo.Excipient","Document.PackInfo.PriceInfo","Document.PackInfo.PriceInfo.Price","Document.PackInfo.DoseFormInfo","Document.PackInfo.DoseFormInfo.DoseForm","Document.PackInfo.NumberOfIngredients","Document.PackInfo.CASIS-TX","Document.PackInfo.CASIS-TX.CompositionInfo","Document.PackInfo.CASIS-TX.CompositionInfo.Composition","Document.LaunchStatus","Document.LaunchStatus.RecordStatus","Document.CASIS-MDNUMBER"]
}

# matching the specific item
POST /casis/item/_search
{
  "query": {
    "match": {
      "SRC_DB": "DGL"
    }
  }
}

# get all the data
GET /casis/_search
{}

# casis string query
GET /casis/item/_search
GET /casis/item/_search?size=2
GET /casis/item/_search?size=1&from=6

DELETE casis
# Design the mapping 
PUT casis
{
   "mappings": {
      "item": {
        "properties": {
          "CASIS_COMPANY": {
            "properties": {
              "CO": {
                "type": "string"
              },
              "COUNTRY": {
                "type": "string"
              },
              "SRC_DB": {
                "type": "string"
              },
              "STATUS": {
                "type": "string"
              }
            }
          },
          "CASIS_COMPOUND": {
            "properties": {
              "CN": {
                "type": "string"
              },
              "SRC_DB": {
                "type": "string"
              }
            }
          },
          "CASIS_DEVSTATUS": {
            "properties": {
              "COUNTRY": {
                "type": "string"
              },
              "DSTA": {
                "type": "string"
              },
              "SRC_DB": {
                "type": "string"
              }
            }
          },
          "CASIS_USE": {
            "properties": {
              "CODE": {
                "type": "string"
              },
              "SOURCE": {
                "type": "string"
              },
              "SRC_DB": {
                "type": "string"
              },
              "THE_USE": {
                "type": "string"
              }
            }
          },
          "CASNO": {
            "type": "string"
          },
          "CN": {
            "type": "string"
          },
          "CO": {
            "type": "string"
          },
          "CODE": {
            "type": "string"
          },
          "COUNTRY": {
            "type": "string"
          },
          "DATEINSERTED": {
            "type": "string"
          },
          "DOCNO": {
            "type": "string"
          },
          "DOC_STRUC_LINK": {
            "properties": {
              "CASNO": {
                "type": "string"
              },
              "MDNUMBER": {
                "type": "string"
              },
              "SRC_DB": {
                "type": "string"
              }
            }
          },
          "DSTA": {
            "type": "string"
          },
          "Document": {
            "properties": {
              "CASIS-DOCNO": {
                "type": "string"
              },
              "CASIS-MDNUMBER": {
                "type": "string"
              },
              "CASIS-UPD": {
                "properties": {
                  "PublicationDate": {
                    "properties": {
                      "CCYYMMDD": {
                        "type": "date",
                        "format": "yyyyMMdd"
                      },
                      "content": {
                        "type": "string"
                      }
                    }
                  }
                }
              },
              "LaunchDetails": {
                "properties": {
                  "CASIS-CN": {
                    "properties": {
                      "BrandName": {
                        "type": "string"
                      }
                    }
                  },
                  "CASIS-CO": {
                    "properties": {
                      "CASIS-NORMALIZED-CO": {
                        "type": "string"
                      },
                      "Corporation": {
                        "type": "string"
                      },
                      "Manufacturer": {
                        "type": "string"
                      }
                    }
                  },
                  "CASIS-DSTA": {
                    "properties": {
                      "CASIS-NORMALIZED-DSTA": {
                        "type": "string"
                      },
                      "Country": {
                        "type": "string"
                      },
                      "LaunchDate": {
                        "properties": {
                          "CCYYMM": {
                            "type": "date",
                            "format": "yyyyMMdd"
                          },
                          "content": {
                            "type": "string"
                          }
                        }
                      }
                    }
                  },
                  "CASIS-RN": {
                    "properties": {
                      "CASInfo": {
                        "properties": {
                          "CASItem": {
                            "type": "string"
                          }
                        }
                      }
                    }
                  },
                  "CASIS-TX": {
                    "properties": {
                      "Biotech": {
                        "type": "string"
                      },
                      "Unbranded": {
                        "type": "string"
                      }
                    }
                  },
                  "CASIS-USE": {
                    "properties": {
                      "CASIS-ACT": {
                        "properties": {
                          "Class": {
                            "properties": {
                              "ClassCode": {
                                "type": "string"
                              },
                              "ClassDescription": {
                                "type": "string"
                              }
                            }
                          }
                        }
                      },
                      "CASIS-IND": {
                        "properties": {
                          "Indication": {
                            "type": "string"
                          }
                        }
                      }
                    }
                  },
                  "CASISRN": {
                    "properties": {
                      "CASInfo": {
                        "properties": {
                          "CASItem": {
                            "type": "string"
                          }
                        }
                      }
                    }
                  },
                  "Ingredients": {
                    "properties": {
                      "Ingredient": {
                        "type": "string"
                      }
                    }
                  },
                  "LaunchDateComment": {
                    "type": "string"
                  },
                  "NewChemicalEntity": {
                    "type": "string"
                  }
                }
              },
              "LaunchStatus": {
                "properties": {
                  "RecordStatus": {
                    "type": "string"
                  }
                }
              },
              "PackInfo": {
                "properties": {
                  "CASIS-TX": {
                    "properties": {
                      "CompositionInfo": {
                        "properties": {
                          "Composition": {
                            "type": "string"
                          }
                        }
                      }
                    }
                  },
                  "DoseFormInfo": {
                    "properties": {
                      "DoseForm": {
                        "type": "string"
                      }
                    }
                  },
                  "ExcipientInfo": {
                    "properties": {
                      "Excipient": {
                        "type": "string"
                      }
                    }
                  },
                  "NumberOfIngredients": {
                    "type": "long"
                  },
                  "PriceInfo": {
                    "properties": {
                      "Price": {
                        "type": "string"
                      }
                    }
                  }
                }
              }
            }
          },
          "MDNUMBER": {
            "type": "string"
          },
          "PART": {
            "type": "long"
          },
          "SOURCE": {
            "type": "string"
          },
          "SRC_DB": {
            "type": "string"
          },
          "STATUS": {
            "type": "string"
          },
          "THE_USE": {
            "type": "string"
          },
          "UPD": {
            "type": "date",
            "format": "yyyyMMdd"
          }
        }
      }
    }
}

# seach all the value in ES
GET /casis/_search
{
  "query": {
    "query_string": {
      "query": "DGL1342925"
    }
  }
}

# solution 1 for White Board
  GET /casis/item/_search
{
  "query": {
    "bool": {
      "filter": {
        "range": {
          "UPD": {
            "gte": 20000120,
            "lte": 20100120
          }
        }
      },
      "must": [
        {
          "match": {
            "SRC_DB": "DGL"
          }
        }
      ]
    }
  },
  
  "from": 0,
  "size": 5,
  
  "sort": [
    {
      "UPD": {
        "order": "asc"
      }
    }
  ],
  "aggregations": {
    "aggregation_by_date": {
      "terms": {
        "field": "orig.raw",
        "order": {
          "_count": "desc"
        },
        "size": 3
      }
    }
  }
}

# solution 0 for White Board 
GET /casis/item/_search
{
  "post_filter": {
    "term": {
      "PART": "8"
    }
  },
  
  "from": "0",
  "size": "2", 
  
  "sort": [
    {
      "SRC_DB": {
        "order": "desc"
      }
    }
  ], 
  
  "aggregations": {
    "aggregation_by_date": {
      "terms": {
        "field": "SRC_DB"
      }
    }
  }
}

# get the mapping 
GET casis

GET  casis/_search
{
  "_source": "DOC_STRUC_LINK.MDNUMBER", 
  "query": {
    "match": {
      "DOC_STRUC_LINK.MDNUMBER": "MD000001"
    }
  }
}
PUT /casis/item/1
{
"DOCNO":"DGL1235379",
"SRC_DB":"DGL",
"PART":8,
"UPD":20011128,
"DATEINSERTED":"03-MAR-16",

"CASIS_COMPANY" :[
  {"SRC_DB":"DGL","CO":"Flow Phar.","COUNTRY":"","STATUS" :"MANUFACTURER"},
  {"SRC_DB":"DGL","CO":"FLOW PHAR.","COUNTRY":"","STATUS" :"CORPORATION"}],   

"CASIS_COMPOUND" :[ 
  {"SRC_DB":"DGL","CN":"C-FORTE"}],    
  
"CASIS_DEVSTATUS":[
  {"SRC_DB":"DGL","DSTA":"Launched 20000101","COUNTRY":"PK"}],   
  
"CASIS_USE"      :[
  {"SRC_DB":"DGL","THE_USE":"Multivitamins with Minerals","CODE":"A11A","SOURCE" :"ACT"},
  {"SRC_DB":"DGL","THE_USE":"Vitamin and mineral supplementation","CODE":"","SOURCE" :"IND"}], 
    
"DOC_STRUC_LINK" :[
  {"SRC_DB":"DGL","CASNO":"","MDNUMBER":"MD000001"}],  

"Document":{  
   "CASIS-MDNUMBER":"MD000001",
   "LaunchDetails":{  
      "CASIS-RN":{  
         "CASInfo":{  
            "CASItem":""
         }
      },
      "LaunchDateComment":"",
      "Ingredients":{  
         "Ingredient":"various vitamins and minerals"
      },
      "NewChemicalEntity":"",
      "CASIS-USE":{  
         "CASIS-IND":{  
            "Indication":"Vitamin and mineral supplementation"
         },
         "CASIS-ACT":{  
            "Class":{  
               "ClassDescription":"Multivitamins with Minerals",
               "ClassCode":"A11A"
            }
         }
      },
      "CASIS-TX":{  
         "Unbranded":"No",
         "Biotech":"No"
      },
      "CASIS-CO":{  
         "Manufacturer":"Flow Phar-",
         "Corporation":"FLOW PHAR-",
         "CASIS-NORMALIZED-CO":[  
            "Manufacturer: Flow Phar-",
            "FLOW PHAR-"
         ]
      },
      "CASIS-CN":{  
         "BrandName":"C-FORTE"
      },
      "CASIS-DSTA":{  
         "LaunchDate":{  
            "content":"01 Jan 2000",
            "CCYYMM":20000101
         },
         "CASIS-NORMALIZED-DSTA":"PK: Launched 20000101",
         "Country":"Pakistan"
      }
   },
   "LaunchStatus":{  
      "RecordStatus":""
   },
   "PackInfo":{  
      "PriceInfo":{  
         "Price":"tabs coated 30: PKR 93-500 (RPP)"
      },
      "CASIS-TX":{  
         "CompositionInfo":{  
            "Composition":"tabs coated: various vitamins and minerals"
         }
      },
      "NumberOfIngredients":1,
      "ExcipientInfo":{  
         "Excipient":""
      },
      "DoseFormInfo":{  
         "DoseForm":"tabs coated"
      }
   },
   "CASIS-UPD":{  
      "PublicationDate":{  
         "content":"28 Nov 2001",
         "CCYYMMDD":20011128
      }
   },
   "CASIS-DOCNO":"DGL1235379"
}

}

PUT /casis/item/2
{
"DOCNO":"DGL1509096",
"SRC_DB":"DGL",
"PART":8,
"UPD":20140928,
"DATEINSERTED":"03-MAR-16",

"CASIS_COMPANY" :[
  {"SRC_DB":"DGL","CO":"OPHTH-PHARMA","COUNTRY":"","STATUS" :"MANUFACTURER"},
  {"SRC_DB":"DGL","CO":"OPHTH-PHARMA","COUNTRY":"","STATUS" :"CORPORATION"}],   

"CASIS_COMPOUND" :[ 
  {"SRC_DB":"DGL","CN":"C-SPOR"}],    
  
"CASIS_DEVSTATUS":[
  {"SRC_DB":"DGL","DSTA":"Launched 20140201","COUNTRY":"PK"}],   
  
"CASIS_USE"      :[
  {"SRC_DB":"DGL","THE_USE":"Keratoconjunctivitis sicca.","CODE":"","SOURCE" :"IND"},
  {"SRC_DB":"DGL","THE_USE":"Other Ophthalmologicals","CODE":"S1X","SOURCE" :"ACT"}], 
    
"DOC_STRUC_LINK" :[
  {"SRC_DB":"DGL","CASNO":"","MDNUMBER":"MD000001"}],  

"Document":{  
      "CASIS-MDNUMBER":"MD000001",
      "LaunchDetails":{  
         "CASISRN":{  
            "CASInfo":{  
               "CASItem":""
            }
         },
         "LaunchDateComment":"",
         "Ingredients":{  
            "Ingredient":"ciclosporin"
         },
         "NewChemicalEntity":"",
         "CASIS-USE":{  
            "CASIS-IND":{  
               "Indication":"Keratoconjunctivitis sicca-"
            },
            "CASIS-ACT":{  
               "Class":{  
                  "ClassDescription":"Other Ophthalmologicals",
                  "ClassCode":"S1X"
               }
            }
         },
         "CASIS-TX":{  
            "Unbranded":"No",
            "Biotech":"No"
         },
         "CASIS-CO":{  
            "Manufacturer":"OPHTH-PHARMA",
            "Corporation":"OPHTH PHARMA",
            "CASIS-NORMALIZED-CO":[  
               "Manufacturer: OPHTH-PHARMA",
               "OPHTH PHARMA"
            ]
         },
         "CASIS-CN":{  
            "BrandName":"C-SPOR"
         },
         "CASIS-DSTA":{  
            "LaunchDate":{  
               "content":"01 Feb 2014",
               "CCYYMM":20140201
            },
            "CASIS-NORMALIZED-DSTA":"PK: Launched 20140201",
            "Country":"Pakistan"
         }
      },
      "LaunchStatus":{  
         "RecordStatus":""
      },
      "PackInfo":{  
         "PriceInfo":{  
            "Price":"suspension ophthalmic 10-00 ml 1: PKR 170-000 (RPP)"
         },
         "CASIS-TX":{  
            "CompositionInfo":{  
               "Composition":"suspension ophthalmic: ciclosporin base, 0-05 %"
            }
         },
         "NumberOfIngredients":1,
         "ExcipientInfo":{  
            "Excipient":""
         },
         "DoseFormInfo":{  
            "DoseForm":"suspension ophthalmic"
         }
      },
      "CASIS-UPD":{  
         "PublicationDate":{  
            "content":"28 Sep 2014",
            "CCYYMMDD":20140928
         }
      },
      "CASIS-DOCNO":"DGL1509096"
   }

}

PUT /casis/item/3
{
"DOCNO":"DGL1510070",
"SRC_DB":"DGL",
"PART":8,
"UPD":20140928,
"DATEINSERTED":"03-MAR-16",

"CASIS_COMPANY" :[
  {"SRC_DB":"DGL","CO":"THE MEDIC PHARM","COUNTRY":"","STATUS" :"MANUFACTURER"},
  {"SRC_DB":"DGL","CO":"THE MEDIC PHARM","COUNTRY":"","STATUS" :"CORPORATION"}],   

"CASIS_COMPOUND" :[ 
  {"SRC_DB":"DGL","CN":"C-TA CAP"}],    
  
"CASIS_DEVSTATUS":[
  {"SRC_DB":"DGL","DSTA":"Launched 20140201","COUNTRY":"TH"}],   
  
"CASIS_USE"      :[
  {"SRC_DB":"DGL","THE_USE":"Vitamin C deficiency.","CODE":"","SOURCE" :"IND"},
  {"SRC_DB":"DGL","THE_USE":"Vitamin C","CODE":"A11G","SOURCE" :"ACT"}], 
    
"DOC_STRUC_LINK" :[
  {"SRC_DB":"DGL","CASNO":"","MDNUMBER":"MD000001"}],  

"Document":{"LaunchDetails":{"LaunchDateComment":"","Ingredients":{"Ingredient":"ascorbic acid"},"NewChemicalEntity":"","CASIS-DSTA":{"LaunchDate":{"content":"01 Feb 2014","CCYYMM":20140201},"CASIS-NORMALIZED-DSTA":"TH: Launched 20140201","Country":"Thailand"},"CASIS-CO":{"Manufacturer":"THE MEDIC PHARM","Corporation":"THE MEDIC PHARM","CASIS-NORMALIZED-CO":["Manufacturer: THE MEDIC PHARM","THE MEDIC PHARM"]},"CASIS-RN":{"CASInfo":{"CASItem":""}},"CASIS-CN":{"BrandName":"C-TA CAP"},"CASIS-TX":{"Unbranded":"No","Biotech":"No"},"CASIS-USE":{"CASIS-ACT":{"Class":{"ClassDescription":"Vitamin C","ClassCode":"A11G"}},"CASIS-IND":{"Indication":"Vitamin C deficiency-"}}},"LaunchStatus":{"RecordStatus":""},"CASIS-UPD":{"PublicationDate":{"content":"28 Sep 2014","CCYYMMDD":20140928}},"CASIS-DOCNO":"DGL1510070","PackInfo":{"PriceInfo":{"Price":"caps 60: THB 121-500 (RSP)"},"NumberOfIngredients":1,"ExcipientInfo":{"Excipient":""},"DoseFormInfo":{"DoseForm":"caps"},"CASIS-TX":{"CompositionInfo":{"Composition":"caps: ascorbic acid base, 500 mg"}}},"CASIS-MDNUMBER":"MD000001"}
}

PUT /casis/item/4
{
"DOCNO":"DGL1235621",
"SRC_DB":"DGL",
"PART":8,
"UPD":20011128,
"DATEINSERTED":"03-MAR-16",

"CASIS_COMPANY" :[
  {"SRC_DB":"DGL","CO":"Rekah","COUNTRY":"","STATUS" :"MANUFACTURER"},
  {"SRC_DB":"DGL","CO":"REKAH","COUNTRY":"","STATUS" :"CORPORATION"}],   

"CASIS_COMPOUND" :[ 
  {"SRC_DB":"DGL","CN":"C-TAHIM KID"}],    
  
"CASIS_DEVSTATUS":[
  {"SRC_DB":"DGL","DSTA":"Launched 20010601","COUNTRY":"IL"}],   
  
"CASIS_USE"      :[
  {"SRC_DB":"DGL","THE_USE":"Vitamin C, Including Combinations with Minerals","CODE":"A11G","SOURCE" :"ACT"}], 
    
"DOC_STRUC_LINK" :[
  {"SRC_DB":"DGL","CASNO":"","MDNUMBER":"MD000001"}], 
  
"Document":{"LaunchDetails":{"LaunchDateComment":"","Ingredients":{"Ingredient":"ascorbic acid"},"NewChemicalEntity":"","CASIS-DSTA":{"LaunchDate":{"content":"01 Jun 2001","CCYYMM":20010601},"CASIS-NORMALIZED-DSTA":"IL: Launched 20010601","Country":"Israel"},"CASIS-CO":{"Manufacturer":"Rekah","Corporation":"REKAH","CASIS-NORMALIZED-CO":["Manufacturer: Rekah","REKAH"]},"CASIS-RN":{"CASInfo":{"CASItem":""}},"CASIS-CN":{"BrandName":"C-TAHIM KID"},"CASIS-TX":{"Unbranded":"No","Biotech":"No"},"CASIS-USE":{"CASIS-ACT":{"Class":{"ClassDescription":"Vitamin C, Including Combinations with Minerals","ClassCode":"A11G"}},"CASIS-IND":{"Indication":""}}},"LaunchStatus":{"RecordStatus":""},"CASIS-UPD":{"PublicationDate":{"content":"28 Nov 2001","CCYYMMDD":20011128}},"CASIS-DOCNO":"DGL1235621","PackInfo":{"PriceInfo":{"Price":"tabs 100: ILS 31-300 (RPP)"},"NumberOfIngredients":1,"ExcipientInfo":{"Excipient":""},"DoseFormInfo":{"DoseForm":"tabs"},"CASIS-TX":{"CompositionInfo":{"Composition":"tabs: ascorbic acid, 200 mg"}}},"CASIS-MDNUMBER":"MD000001"}
}

PUT /casis/item/5
{
"DOCNO":"DGL1485036",
"SRC_DB":"DGL",
"PART":8,
"UPD":20130928,
"DATEINSERTED":"03-MAR-16",

"CASIS_COMPANY" :[
  {"SRC_DB":"DGL","CO":"Vitalhealth Pharma","COUNTRY":"","STATUS" :"MANUFACTURER"},
  {"SRC_DB":"DGL","CO":"Vitalhealth Pharma","COUNTRY":"","STATUS" :"CORPORATION"}],   

"CASIS_COMPOUND" :[ 
  {"SRC_DB":"DGL","CN":"C-TAMIN"}],    
  
"CASIS_DEVSTATUS":[
  {"SRC_DB":"DGL","DSTA":"Launched 20121001","COUNTRY":"AE"}],   
  
"CASIS_USE"      :[
  {"SRC_DB":"DGL","THE_USE":"Dietary supplementation to support the immune system function.","CODE":"","SOURCE" :"IND"},
  {"SRC_DB":"DGL","THE_USE":"Vitamin C","CODE":"A11G","SOURCE" :"ACT"}], 
    
"DOC_STRUC_LINK" :[
  {"SRC_DB":"DGL","CASNO":"","MDNUMBER":"MD000001"}], 
  
"Document":{"LaunchDetails":{"LaunchDateComment":"","Ingredients":{"Ingredient":["ascorbic acid","bioflavonoids","rosa canina"]},"NewChemicalEntity":"","CASIS-DSTA":{"LaunchDate":{"content":"01 Oct 2012","CCYYMM":20121001},"CASIS-NORMALIZED-DSTA":"AE: Launched 20121001","Country":"United Arab Emirates"},"CASIS-CO":{"Manufacturer":"Vitalhealth Pharma","Corporation":"Vitalhealth Pharma","CASIS-NORMALIZED-CO":["Manufacturer: Vitalhealth Pharma","Vitalhealth Pharma"]},"CASIS-RN":{"CASInfo":{"CASItem":""}},"CASIS-CN":{"BrandName":"C-TAMIN"},"CASIS-TX":{"Unbranded":"No","Biotech":"No"},"CASIS-USE":{"CASIS-ACT":{"Class":{"ClassDescription":"Vitamin C","ClassCode":"A11G"}},"CASIS-IND":{"Indication":"Dietary supplementation to support the immune system function-"}}},"LaunchStatus":{"RecordStatus":""},"CASIS-UPD":{"PublicationDate":{"content":"28 Sep 2013","CCYYMMDD":20130928}},"CASIS-DOCNO":"DGL1485036","PackInfo":{"PriceInfo":{"Price":"tabs coated 30: AED 40-000 (RPP)"},"NumberOfIngredients":3,"ExcipientInfo":{"Excipient":""},"DoseFormInfo":{"DoseForm":"tabs coated"},"CASIS-TX":{"CompositionInfo":{"Composition":"tabs coated: ascorbic acid base; bioflavonoids base; rosa canina base"}}},"CASIS-MDNUMBER":"MD000001"}
}

PUT /casis/item/6
{
"DOCNO":"DGL1283941",
"SRC_DB":"DGL",
"PART":8,
"UPD":20040828,
"DATEINSERTED":"03-MAR-16",

"CASIS_COMPANY" :[
  {"SRC_DB":"DGL","CO":"Myung Moon","COUNTRY":"","STATUS" :"MANUFACTURER"},
  {"SRC_DB":"DGL","CO":"Myung Moon","COUNTRY":"","STATUS" :"CORPORATION"}],   

"CASIS_COMPOUND" :[ 
  {"SRC_DB":"DGL","CN":"C.I.A"}],    
  
"CASIS_DEVSTATUS":[
  {"SRC_DB":"DGL","DSTA":"Launched 20040401","COUNTRY":"KR"}],   
  
"CASIS_USE"      :[
  {"SRC_DB":"DGL","THE_USE":"Pain.","CODE":"","SOURCE" :"IND"},
  {"SRC_DB":"DGL","THE_USE":"Narcotics","CODE":"N2A","SOURCE" :"ACT"}], 
    
"DOC_STRUC_LINK" :[
  {"SRC_DB":"DGL","CASNO":"","MDNUMBER":"MD000001"}], 

"Document":{"LaunchDetails":{"LaunchDateComment":"","Ingredients":{"Ingredient":["codeine","ibuprofen","paracetamol"]},"NewChemicalEntity":"","CASIS-DSTA":{"LaunchDate":{"content":"01 Apr 2004","CCYYMM":20040401},"CASIS-NORMALIZED-DSTA":"KR: Launched 20040401","Country":"South Korea"},"CASIS-CO":{"Manufacturer":"Myung Moon","Corporation":"MYUNG MOON","CASIS-NORMALIZED-CO":["Manufacturer: Myung Moon","MYUNG MOON"]},"CASIS-RN":{"CASInfo":{"CASItem":""}},"CASIS-CN":{"BrandName":"C-I-A"},"CASIS-TX":{"Unbranded":"No","Biotech":"No"},"CASIS-USE":{"CASIS-ACT":{"Class":{"ClassDescription":"Narcotics","ClassCode":"N2A"}},"CASIS-IND":{"Indication":"Pain-"}}},"LaunchStatus":{"RecordStatus":""},"CASIS-UPD":{"PublicationDate":{"content":"28 Aug 2004","CCYYMMDD":20040828}},"CASIS-DOCNO":"DGL1283941","PackInfo":{"PriceInfo":{"Price":"caps 300: KRW 111818-000 (RSP)"},"NumberOfIngredients":3,"ExcipientInfo":{"Excipient":""},"DoseFormInfo":{"DoseForm":"caps"},"CASIS-TX":{"CompositionInfo":{"Composition":"caps: codeine phosphate, 10 mg; ibuprofen, 200 mg; paracetamol, 250 mg"}}},"CASIS-MDNUMBER":"MD000001"}
}

PUT /casis/item/7
{
"DOCNO":"DGL1342924",
"SRC_DB":"DGL",
"PART":8,
"UPD":20070828,
"DATEINSERTED":"03-MAR-16",


"CASIS_COMPANY" :[
  {"SRC_DB":"DGL","CO":"Country Life","COUNTRY":"","STATUS" :"MANUFACTURER"},
  {"SRC_DB":"DGL","CO":"COUNTRY LIFE","COUNTRY":"","STATUS" :"CORPORATION"}],   

"CASIS_COMPOUND" :[ 
  {"SRC_DB":"DGL","CN":"C.L BETA CAROTENE"}],    
  
"CASIS_DEVSTATUS":[
  {"SRC_DB":"DGL","DSTA":"Launched 20070201","COUNTRY":"TR"}],   
  
"CASIS_USE"      :[
  {"SRC_DB":"DGL","THE_USE":"Aids healthy skin.","CODE":"","SOURCE" :"IND"},
  {"SRC_DB":"DGL","THE_USE":"Vitamin A and D including Combinations of the Two","CODE":"A11C","SOURCE" :"ACT"}], 
    
"DOC_STRUC_LINK" :[
  {"SRC_DB":"DGL","CASNO":"","MDNUMBER":"MD000001"}], 


"Document":{"LaunchDetails":{"LaunchDateComment":"","Ingredients":{"Ingredient":"betacarotene"},"NewChemicalEntity":"","CASIS-DSTA":{"LaunchDate":{"content":"01 Feb 2007","CCYYMM":20070201},"CASIS-NORMALIZED-DSTA":"TR: Launched 20070201","Country":"Turkey"},"CASIS-CO":{"Manufacturer":"Country Life","Corporation":"COUNTRY LIFE","CASIS-NORMALIZED-CO":["Manufacturer: Country Life","COUNTRY LIFE"]},"CASIS-RN":{"CASInfo":{"CASItem":""}},"CASIS-CN":{"BrandName":"C-L BETA CAROTENE"},"CASIS-TX":{"Unbranded":"Yes","Biotech":"No"},"CASIS-USE":{"CASIS-ACT":{"Class":{"ClassDescription":"Vitamin A and D including Combinations of the Two","ClassCode":"A11C"}},"CASIS-IND":{"Indication":"Aids healthy skin-"}}},"LaunchStatus":{"RecordStatus":""},"CASIS-UPD":{"PublicationDate":{"content":"28 Aug 2007","CCYYMMDD":20070828}},"CASIS-DOCNO":"DGL1342924","PackInfo":{"PriceInfo":{"Price":""},"NumberOfIngredients":1,"ExcipientInfo":{"Excipient":""},"DoseFormInfo":{"DoseForm":"caps"},"CASIS-TX":{"CompositionInfo":{"Composition":"caps: betacarotene, 25 IU"}}},"CASIS-MDNUMBER":"MD000001"}	
}

PUT /casis/item/8
{
"DOCNO":"DGL1342925",
"SRC_DB":"DGL",
"PART":8,
"UPD":20070828,
"DATEINSERTED":"03-MAR-16",


"CASIS_COMPANY" :[
  {"SRC_DB":"DGL","CO":"Country Life","COUNTRY":"","STATUS" :"MANUFACTURER"},
  {"SRC_DB":"DGL","CO":"COUNTRY LIFE","COUNTRY":"","STATUS" :"CORPORATION"}],   

"CASIS_COMPOUND" :[ 
  {"SRC_DB":"DGL","CN":"C.L BIOTIN"}],    
  
"CASIS_DEVSTATUS":[
  {"SRC_DB":"DGL","DSTA":"Launched 20070201","COUNTRY":"TR"}],   
  
"CASIS_USE"      :[
  {"SRC_DB":"DGL","THE_USE":"Aids healthy hair and nails.","CODE":"","SOURCE" :"IND"},
  {"SRC_DB":"DGL","THE_USE":"Other Dermatological Preparations","CODE":"D11A","SOURCE" :"ACT"}], 
    
"DOC_STRUC_LINK" :[
  {"SRC_DB":"DGL","CASNO":"","MDNUMBER":"MD000001"}], 


"Document":{"LaunchDetails":{"LaunchDateComment":"","Ingredients":{"Ingredient":"biotin"},"NewChemicalEntity":"","CASIS-DSTA":{"LaunchDate":{"content":"01 Feb 2007","CCYYMM":20070201},"CASIS-NORMALIZED-DSTA":"TR: Launched 20070201","Country":"Turkey"},"CASIS-CO":{"Manufacturer":"Country Life","Corporation":"COUNTRY LIFE","CASIS-NORMALIZED-CO":["Manufacturer: Country Life","COUNTRY LIFE"]},"CASIS-RN":{"CASInfo":{"CASItem":""}},"CASIS-CN":{"BrandName":"C-L BIOTIN"},"CASIS-TX":{"Unbranded":"Yes","Biotech":"No"},"CASIS-USE":{"CASIS-ACT":{"Class":{"ClassDescription":"Other Dermatological Preparations","ClassCode":"D11A"}},"CASIS-IND":{"Indication":"Aids healthy hair and nails-"}}},"LaunchStatus":{"RecordStatus":""},"CASIS-UPD":{"PublicationDate":{"content":"28 Aug 2007","CCYYMMDD":20070828}},"CASIS-DOCNO":"DGL1342925","PackInfo":{"PriceInfo":{"Price":""},"NumberOfIngredients":1,"ExcipientInfo":{"Excipient":""},"DoseFormInfo":{"DoseForm":"tabs"},"CASIS-TX":{"CompositionInfo":{"Composition":"tabs: biotin, 1 mg"}}},"CASIS-MDNUMBER":"MD000001"}	
}

PUT /casis/item/9
{
"DOCNO":"DGL1342926",
"SRC_DB":"DGL",
"PART":8,
"UPD":20070828,
"DATEINSERTED":"03-MAR-16",

"CASIS_COMPANY" :[
  {"SRC_DB":"DGL","CO":"Country Life","COUNTRY":"","STATUS" :"MANUFACTURER"},
  {"SRC_DB":"DGL","CO":"COUNTRY LIFE","COUNTRY":"","STATUS" :"CORPORATION"}],   

"CASIS_COMPOUND" :[ 
  {"SRC_DB":"DGL","CN":"C.L CO-Q10"}],    
  
"CASIS_DEVSTATUS":[
  {"SRC_DB":"DGL","DSTA":"Launched 20070201","COUNTRY":"TR"}],   
  
"CASIS_USE"      :[
  {"SRC_DB":"DGL","THE_USE":"Aids cardiovascular health, antioxidant.","CODE":"","SOURCE" :"IND"},
  {"SRC_DB":"DGL","THE_USE":"All Other Cardiac Preparations","CODE":"C1X","SOURCE" :"ACT"}], 
    
"DOC_STRUC_LINK" :[
  {"SRC_DB":"DGL","CASNO":"","MDNUMBER":"MD000001"}], 
  
"Document":{"LaunchDetails":{"LaunchDateComment":"","Ingredients":{"Ingredient":"ubidecarenone"},"NewChemicalEntity":"","CASIS-DSTA":{"LaunchDate":{"content":"01 Feb 2007","CCYYMM":20070201},"CASIS-NORMALIZED-DSTA":"TR: Launched 20070201","Country":"Turkey"},"CASIS-CO":{"Manufacturer":"Country Life","Corporation":"COUNTRY LIFE","CASIS-NORMALIZED-CO":["Manufacturer: Country Life","COUNTRY LIFE"]},"CASIS-RN":{"CASInfo":{"CASItem":""}},"CASIS-CN":{"BrandName":"C-L CO-Q10"},"CASIS-TX":{"Unbranded":"Yes","Biotech":"No"},"CASIS-USE":{"CASIS-ACT":{"Class":{"ClassDescription":"All Other Cardiac Preparations","ClassCode":"C1X"}},"CASIS-IND":{"Indication":"Aids cardiovascular health, antioxidant-"}}},"LaunchStatus":{"RecordStatus":""},"CASIS-UPD":{"PublicationDate":{"content":"28 Aug 2007","CCYYMMDD":20070828}},"CASIS-DOCNO":"DGL1342926","PackInfo":{"PriceInfo":{"Price":""},"NumberOfIngredients":1,"ExcipientInfo":{"Excipient":""},"DoseFormInfo":{"DoseForm":"caps"},"CASIS-TX":{"CompositionInfo":{"Composition":"caps: ubidecarenone, 30 MIU"}}},"CASIS-MDNUMBER":"MD000001"}	
}

PUT /casis/item/10
{
"DOCNO":"DGL1483041",
"SRC_DB":"DGL",
"PART":8,
"UPD":20130928,
"DATEINSERTED":"03-MAR-16",

"CASIS_COMPANY" :[
  {"SRC_DB":"DGL","CO":"CHALVER","COUNTRY":"","STATUS" :"MANUFACTURER"},
  {"SRC_DB":"DGL","CO":"CHALVER","COUNTRY":"","STATUS" :"CORPORATION"}],   

"CASIS_COMPOUND" :[ 
  {"SRC_DB":"DGL","CN":"FLUZETRIN F"}],    
  
"CASIS_DEVSTATUS":[
  {"SRC_DB":"DGL","DSTA":"Launched 20110201","COUNTRY":"EC"}],   
  
"CASIS_USE"      :[
  {"SRC_DB":"DGL","THE_USE":"Cold Preparations Without Anti-infectives","CODE":"R5A","SOURCE" :"ACT"},
  {"SRC_DB":"DGL","THE_USE":"Common cold, influenza.","CODE":"","SOURCE" :"IND"}], 
    
"DOC_STRUC_LINK" :[
  {"SRC_DB":"DGL","CASNO":"","MDNUMBER":"MD000001"}], 
  
"Document":{"LaunchDetails":{"LaunchDateComment":"","Ingredients":{"Ingredient":["cetirizine","paracetamol","phenylephrine"]},"NewChemicalEntity":"","CASIS-DSTA":{"LaunchDate":{"content":"01 Feb 2011","CCYYMM":20110201},"CASIS-NORMALIZED-DSTA":"EC: Launched 20110201","Country":"Ecuador"},"CASIS-CO":{"Manufacturer":"CHALVER","Corporation":"CHALVER","CASIS-NORMALIZED-CO":["Manufacturer: CHALVER","CHALVER"]},"CASIS-RN":{"CASInfo":{"CASItem":""}},"CASIS-CN":{"BrandName":"FLUZETRIN F"},"CASIS-TX":{"Unbranded":"No","Biotech":"No"},"CASIS-USE":{"CASIS-ACT":{"Class":{"ClassDescription":"Cold Preparations Without Anti-infectives","ClassCode":"R5A"}},"CASIS-IND":{"Indication":"Common cold, influenza-"}}},"LaunchStatus":{"RecordStatus":""},"CASIS-UPD":{"PublicationDate":{"content":"28 Sep 2013","CCYYMMDD":20130928}},"CASIS-DOCNO":"DGL1483041","PackInfo":{"PriceInfo":{"Price":""},"NumberOfIngredients":3,"ExcipientInfo":{"Excipient":""},"DoseFormInfo":{"DoseForm":["tabs","drops oral","syrup oral"]},"CASIS-TX":{"CompositionInfo":{"Composition":["tabs: cetirizine hydrochloride, 5 mg; paracetamol base, 500 mg; phenylephrine hydrochloride, 10 mg","syrup oral: cetirizine hydrochloride, 2-5 mg/5 ml; paracetamol base, 325 mg/5 ml; phenylephrine hydrochloride, 5 mg/5 ml","drops oral: cetirizine hydrochloride, 1 mg/1 ml; paracetamol base, 100 mg/1 ml; phenylephrine hydrochloride, 10 mg/1 ml"]}}},"CASIS-MDNUMBER":"MD000001"}
}


GET /casis/item/_search
{
  "query": {
    "bool": {
      "filter": {
        "range": {
          "UPD": {
            "gte": 20000120,
            "lte": 20100120
          }
        }
      },
      "must": [
        {
          "match": {
            "SRC_DB": "DGL"
          }
        }
      ]
    }
  },
  "from": 0,
  "size": 5,
  "sort": [
    {
      "UPD": {
        "order": "asc"
      }
    }
  ],
  "aggregations": {
    "aggregation_by_date": {
      "terms": {
        "field": "UPD",
        "order": {
          "_count": "desc"
        },
        "size": 3
      }
    }
  }
}



