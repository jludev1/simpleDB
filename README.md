## Overall Design and Implementation
This implementation uses an "undo log" approach to implement the "Simple Database Challenge". A data hash map stores all the key value pairs. Another hash map is used to store the count of each value. Each update (SET/UNSET) modifies the data hash map directly and update the count of the new and old value. The count of "null" value is not tracked.

Transaction is supported by a set of undo logs organized in a linked list. When a new transaction starts, a new undo log is added to the front of the undo log list. An undo log is a hash map itself that stores the original value for each key modified in the current transaction. When SET/UNSET is called the first time on a key, the key / original value pair will be added to the undo log and will not be changed again in the same transaction. 

When rollback is called, the original values from the undo log will be set on the data hash map and the value counts will be adjusted in the same way during SET/UNSET. The undo log for the current transaction will then be removed from the undo log list. 

When commit is called, we only need to clear the undo log list, as the data and value counts always reflect the latest state. 
  
## Performance 
As hash map is used to store the data and value count, GET and NUMEQUALTO are O(1).
BEGIN, SET and UNSET are also mainly get/put operations on the hash map and should be O(1).
COMMIT only clears the undo log list, it's time will be constant.
ROLLBACK's complexity will depend on the number of keys that need to be set. As the number of keys modified in a transaction is relatively small, the time will likely to be constant as well. 

## Memory Usage
As the number of keys modified in a transaction is relatively small, the hash map memory footprint for each undo log will be small.  

## Source Code
Two classes used in the implementation:
   * SimpleDB class - It mainly handles input and output. It invokes DataStore class for data operations.
   * DataStore class - It has internal data structures to store key value pairs and supports read/write operations and the transaction behaviors. 

## Usage 

1. Down load the files into a folder
2. navigate to the bin sub folder in a terminal
3. run `java SimpleDB`


## Alternative Solution 
An alternative solution can use the "shadow value" approach, where the value modified in a nested transaction "shadows" the value in the nesting transaction. Depending on implementation, this approach may have the following limitations:
(1) Iterative lookup for a variable across nested transactions. The time it takes to look up a variable may depend on the height of the transaction stack.
(2) Special handling for UNSET. Need to a way to tell if the value is intentionally set to null in the current transaction or just not modified. 
(3) More work for COMMIT. When commit is invoked, we need to "fold" changes across the transaction stack and update related value counts. This could be a performance issue for a very high transaction stack.