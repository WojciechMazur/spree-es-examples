# Basics

* Data is stored within indices

* Each index can have 1 on more primary shards

* Each primary shard can have 0..n replicas
  If we have N nodes, having N-1 replicas means full replication - all data is redundantly stored in each node

* Each shard is internally Apache Lucene independent index. 

* Each shard consists of 1 or more segments + in-memory buffer

* All data is internally immutable. 
  Each document modification (including deletion) increases document version. 
  
* When removing document it's just marked as deleted, same applies when document was updated. 
  Phisical removal of documents occours in segment merging phase. 

* `refresh` is a process in which data inside in-memoery buffer is written to newly created segment on disc

* All operations are stored in translog. When indexing new fields they're added both to in-memory buffer and    translog.
On refresh data from buffer is moved to new segment without an fsync. Buffer is cleared. 
When translog if full or after given period of time there is created a full comit. All data for buffer written to new segment, commit points writen to disk, cached flashed fsync and translog is delted. 
Full commit can be forced using `_flush`

# Data

* Index mapping defines which fields should be searchable and describes how to treat its data

* Nested fields are supported in Lucene they're flattend to top level fields. 

* Each field can contain 0, 1, or multiple values. Fields without data are not stored in Lucene. 


# Full text search

* For all documents in index there is created an inverted index - sorted list of all of unique values that occour in any document and each term. 

* When created inverted index values text is tokenized and normalized, eg. Dogs -> dog
  The same applies to the query 



# What to read: 
[Elasticsearch: The Definitvive Guide](https://www.elastic.co/guide/en/elasticsearch/guide/master/index.html) - internalls should be still valid, altough some things are no loner availabe in new verison
