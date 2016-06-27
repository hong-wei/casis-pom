GET /casisvm/_search
{
  "query": {
    "term": {
      "CHEM_STRUCTURE_DATA.LINK_SRC_DB": "AIDS"
    }
  }
}


GET /casisvm1/_analyze?field=SRC_DB&text=AIDS
GET /casisvm
GET /casisvm/_search
{
  "_source": "SRC_DB",
  "query": {
    "term": {
      "SRC_DB": "aids"
    }
  }
}

GET /casisvm/_search
{
  "_source": "SRC_DB", 
  "query": {
    "bool": {
      "must": [
        {
          "match": {
            "_all": "dihydrochloride"
          }
        }
      ],
      "should": [
        {
          "match": {
            "_all": "terpol"
          }
        },
        {
          "match": {
            "CASIS_COMPANY.CO": "Tsumura"
          }
        },
        {
          "match": {
            "Document_CASIS-TX_TX": "FDA"
          }
        }
      ],
      "filter": [
        {
          "term": {
            "SRC_DB.raw": "AIDS"
          }
        }
      ]
    }
  }
}

GET /casisvm/_search
{
  "_source": "SRC_DB", 
  "query": {
    "bool": {
      "must": [
        {
          "match": {
            "_all": "dihydrochloride"
          }
        }
      ],
      "should": [
        {
          "match": {
            "_all": "terpol"
          }
        },
        {
          "match": {
            "CASIS_COMPANY.CO": "Tsumura"
          }
        },
        {
          "match": {
            "Document_CASIS-TX_TX": "FDA"
          }
        }
      ],
      "filter": [
        {
          "terms": {
            "SRC_DB": ["AIDS"]
          }
        }
      ]
    }
  }
}
GET /casisvm1
GET /casisvm1/_analyze?field=DOCNO 
GET /casisvm1/_analyze?field=SRC_DB&text=AIDS

GET /casisvm/_search
{
  "query": {
          "match_all": {}
}}



GET /casisvm1/_search
{
  "size": 10, 
  "query": {
    "match_all": {
    }
  },
  "aggregations": {
    "DOCNO": {
      "terms": {
        "field": "DOCUMENT.Document_CASIS-TX_TX.raw",
        "size": 0
      
      }
    }
  }
}

GET /casisvm1

# mapping all items
DELETE casisvm1
PUT /casisvm1
{
  "mappings": {
    "documents": {
      "dynamic_templates": [
        {
          "my_multi_strings": {
            "match_mapping_type": "string",
            "mapping": {
              "type": "string",
              "fields": {
                "raw": {
                  "type": "string",
                  "index": "not_analyzed"
                }
              }
            }
          }
        },
        {
          "my_date_fields": {
            "match": "DATEINSERTED",
            "mapping": {
              "type": "date",
              "format": "yyyy-mm-dd HH:mm:ss",
              "fields": {
                "raw": {
                  "type": "string",
                  "index": "not_analyzed"
                }
              }
            }
          }
        },
        {
          "my_integer_fields": {
            "match": "PART*",
            "mapping": {
              "type": "integer",
              "fields": {
                "raw": {
                  "type": "string",
                  "index":"not_analyzed"
                }
            }}
          }
        },
        {
          "my_integer_fields": {
            "match": "Document_CASIS-TX_TX",
            "mapping": {
              "type": "string",
              "fields": {
                "raw": {
                  "type": "string"
                }
            }}
          }
        },
        {
          "my_integer_fields": {
            "match": "Document_CASIS-TX_KeyClinicalInformation_PhaseIII",
            "mapping": {
              "type": "string",
              "fields": {
                "raw": {
                  "type": "string"
                }
            }}
          }
        },
        {
          "my_integer_fields": {
            "match": "Document_CASIS-TX_KeyClinicalInformation_PhaseI",
            "mapping": {
              "type": "string",
              "fields": {
                "raw": {
                  "type": "string"
                }
            }}
          }
        },
        {
          "my_integer_fields": {
            "match": "Document_CASIS-TX_KeyClinicalInformation_PhaseII",
            "mapping": {
              "type": "string",
              "fields": {
                "raw": {
                  "type": "string"
                }
            }}
          }
        },
        {
          "my_integer_fields": {
            "match": "Document_CASIS-TX_Marketing",
            "mapping": {
              "type": "string",
              "fields": {
                "raw": {
                  "type": "string"
                }
            }}
          }
        },
        {
          "my_integer_fields": {
            "match": "UPD",
            "mapping": {
              "type": "string",
              "fields":{
                "raw": {
                  "type": "string",
                  "index": "not_analyzed"
                }
            }}
          }
        }
      ]
    }
  }
}
PUT /casisvm1/_mapping/documents
{

  "dynamic": "ture",
  "properties": {}

}




#test 
DELETE casisvm1
PUT /casisvm1
{
  "mappings": {
    "documents": {
      "dynamic_templates": [
        {
          "my_integer_fields": {
            "match": "Document_CASIS-TX_TX",
            "mapping": {
              "type": "string",
              "fields": {
                "raw": {
                  "type": "string"
                }
              }
            }
          }
        },
        {
          "my_integer_fields": {
            "match": "Document_CASIS-TX_KeyClinicalInformation_PhaseIII",
            "mapping": {
              "type": "string",
              "fields": {
                "raw": {
                  "type": "string"
                }
              }
            }
          }
        }
      ]
    }
  }
}

GET casisvm1

GET /casisvm1/_search
{
  "size": 1, 
  "query": {
    "match_all": {
    }
  },
  "aggregations": {
    "DOCNO": {
      "terms": {
        "field": "DOCUMENT.Document_CASIS-TX_KeyClinicalInformation_PhaseIII.raw",
        "size": 0
      
      }
    }
  }
}


PUT /casisvm4/documents/115

# get mapping of casisvm4
GET casisvm1


# test search for casish a
GET /casisvm1/_search
{
  "size": 1,
  "from": 0,
  "query": {
    "match_all": {}
  },
  "aggs": {
    "aa": {
      "terms": {
        "field": "DOCNO",
        "size": 100
      }
    }
  }
}
DELETE /casisvm4
GET /casisvm4

GET /casisvm1/_search
{
  "size": 100, 
  "query": {
    "match_all": {
    }
  },
  "aggregations": {
    "DOCNO": {
      "terms": {
        "field": "DOCUMENT.TG",
        "size": 0
      
      }
    }
  }
}



GET /casisvm4/_search
{
  "query": {
    "match": {
      "_all": "AI11511"
    }
  }
}

GET /casisvm5/_search
{
  "query": {
    "match": {
      "DOCNO": "AI11511"
    }
  }
}


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


# seach all the value in ES
GET /casis/_search
{
  "query": {
    "query_string": {
      "query": "Vitalhealth Pharma"
    }
  }
}
# complex native lunece more complex queries


# seach all the value in ES
GET /casis/_search
{
  "query": {
    "match": {
      "_all": 8
    }
  }
}

GET /casis/item/_search
{
  "query": {
    "bool": {
      "filter": {
        "range": {
          "UPD": {
            "gte": 20070828,
            "lte": 20170828
          }
        }
      },
      "must": [
        {
          "match": {
            "PART": "8"
          }
        }
      ]
    }
  }
}
  
  
# simple query 

# solution 2 for White Board
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
  "size": 1,
  
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


# solution 1 for White Board 
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

GET /casis/item/_search
{
  "text_search": {
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
          "order": "1"
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


# delete the mapping 
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

"Document":{
  "LaunchDetails": {
    "LaunchDateComment": "",
    "Ingredients": {
      "Ingredient": "biotin"
    },
    "NewChemicalEntity": "",
    "CASIS-DSTA": {
      "LaunchDate": {
        "content": "01 Feb 2007",
        "CCYYMM": 20070201
      },
      "CASIS-NORMALIZED-DSTA": "TR: Launched 20070201",
      "Country": "Turkey"
    },
    "CASIS-CO": {
      "Manufacturer": "Country Life",
      "Corporation": "COUNTRY LIFE",
      "CASIS-NORMALIZED-CO": [
        "Manufacturer: Country Life",
        "COUNTRY LIFE"
      ]
    },
    "CASIS-RN": {
      "CASInfo": {
        "CASItem": ""
      }
    },
    "CASIS-CN": {
      "BrandName": "C-L BIOTIN"
    },
    "CASIS-TX": {
      "Unbranded": "Yes",
      "Biotech": "No"
    },
    "CASIS-USE": {
      "CASIS-ACT": {
        "Class": {
          "ClassDescription": "Other Dermatological Preparations",
          "ClassCode": "D11A"
        }
      },
      "CASIS-IND": {
        "Indication": "Aids healthy hair and nails-"
      }
    }
  },
  "LaunchStatus": {
    "RecordStatus": ""
  },
  "CASIS-UPD": {
    "PublicationDate": {
      "content": "28 Aug 2007",
      "CCYYMMDD": 20070828
    }
  },
  "CASIS-DOCNO": "DGL1342925",
  "PackInfo": {
    "PriceInfo": {
      "Price": ""
    },
    "NumberOfIngredients": 1,
    "ExcipientInfo": {
      "Excipient": ""
    },
    "DoseFormInfo": {
      "DoseForm": "tabs"
    },
    "CASIS-TX": {
      "CompositionInfo": {
        "Composition": "tabs: biotin, 1 mg"
      }
    }
  },
  "CASIS-MDNUMBER": "MD000001"
}	
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



#  Percolator https://www.elastic.co/guide/en/elasticsearch/reference/2.3/search-percolate.html
 

PUT /my-index1
{
  "mappings": {
    "my-type": {
      "properties": {
        "message": {
          "type": "string"
        }
      }
    }
  }
}

PUT /my-index/.percolator/1
  {
      "query" : {
          "match" : {
              "message" : " new fice"
          }
      }
  }

PUT /my-index/.percolator/2
{
    "query" : {
        "match" : {
            "message" : "A new fice"
        }
    }
}

PUT /my-index/.percolator/3
{
    "query" : {
        "match" : {
            "message" : "A new fice tree"
        }
    }
}

GET /my-index/my-type/_percolate
{
    "doc" : {
        "message" : "A new bonsai tree in the office"
    }
}

GET /my-index/my-type/_percolate
{
    "doc" : {
        "message" : " a"
    }
}
GET /my-index/my-type/_percolate/count
{

   "doc" : {
       "message" : "a"
   }

}

GET /my-index1/my-type/1/_percolate


# http://blog.florian-hopf.de/2013/08/getting-started-with-elasticsearch-part.html

PUT /blog
{
  "mappings": {
    "talk": {
      "properties": {
        "title": {
          "type": "string",
          "store": "yes",
          "analyzer": "german"
        }
      }
    }
  }
}



