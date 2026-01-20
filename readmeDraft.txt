read me short notes:

bm 25 algo proposed by ...

micro optimization by stopword deletions

stopword list from : https://gist.github.com/sebleier/554280

bm 25 parameters from where 

optimization scoring and construct heap done simontanously

optimazation : injestion also constructs the trie to avoid rescanning the index.

heavy  optimization :  agressive  pre sizeing of the hashs  during  the  ingestion.

mirco  optimization:  For top  k  updates  ,  for every token we  need to see if its there is the top k , the time complexity is linear to k , and due to many documents processed, this may cause micro lags (as K is very small), however to make it average constant time , maintain a seperate hashset for lookups , and a seperate Array for ordering.
----------------------------------------------------------------------------------------------------------------------------------------
 some  optimizations that i have not included  : 

Statefully  server side  autocomplete :  the system  calls  suggest() everytime and  travese from the root  everytime  ,  i understand that  in  millions of queries this may cause latency ,  however for the currant scope  it causes no significant issues and even when scaled to million ,with time based call suggest function may circumvent the problem .
Nevertheless  for high workload this may  be a  worthwhile  optimization.

------------------------------------------------------------------------------------------------------------------------------
for autocomptele deletions  :  a lazy  deletion strategy is  done  , with threshold  so  prevent  over staleness


I have used a direct normilized - result LRU cache ,in favour  low overhead for a lightweight library, To maximize Hit ratio somthing like arc caching would improve hit-miss ratio but add overhead and metadata - having plans to implement ARC in the near future. 


config exist to change parameters, constructor is overloaded 

persisting the internal variable of search engine class and not the class object itself for the sake of future proofing , refactoring , and flexibility.
even if i store it as an object itself ,it would not have any improvements in load times.

Startup is simple propotional to the number of entries. (looking for ways of optimizations, Currant implementation is final for version 1.0)


Remaining Work: Logging , Maven , Demo.


