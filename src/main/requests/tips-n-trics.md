# Indexing
* When doing a lot of indexing set custom refresh rate - default 1s

* Disable automatic mapping creation. Elasticsearch by default can add all found fields in documents to mapping automaticallym, which may result is rapid increase in index size and slow indexing times. It can be set to either root or nested object


# Searching
* Rounding date ranges gives higher chances of hiting results in cache, eg. replace `now-1h` with `now-1h/m`   (rounded to minute)

# Storage

* Removing records invidually is expensive, however removeing whole index is chip - make time-based indices if you'll need to remove data in the future.

