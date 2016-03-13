import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

class DataStore {
    // a list of rollback logs for nested transactions
    private List<Map> rollbackLog = null;
    
    private Map<String, String> data = null; 
    
    private Map<String, Long> valueCounter = null;

    public DataStore() {
        data = new HashMap<String, String>();
        valueCounter = new HashMap<String, Long>();
        rollbackLog = new LinkedList<Map>();
    }
 
    public String get(String key) {
        return data.get(key);
    }
    
    public void set(String key, String value) {
        String oldValue = data.get(key);
        if ((value == null && oldValue == null) || (value != null && value.equals(oldValue)))
            return;
        
        data.put(key, value);
        updateValueCount(value, oldValue);
        log(key, oldValue);
    }

    public void unset(String key) {
        set(key, null);
    }
    
    public long count(String value) {
        Long num = valueCounter.get(value);
        return num == null ? 0 : num;
    }
    
    public void createTransaction() {
        // add a new rollback log at the beginning of the rollback log list
        Map<String, String> log = new HashMap<String, String>();
        rollbackLog.add(0, log);
    }
    
    public void rollback() {
        if (rollbackLog.isEmpty())
            return;

        Map<String, String> currentLog = rollbackLog.get(0);
        if (currentLog == null)
            return;
        
        //restore the original values and update value counts
        currentLog.forEach((key, origValue) -> {
            String value = data.get(key);
            data.put(key, origValue);
            updateValueCount(origValue, value);
        });
        
        //update the current rollback log
        rollbackLog.remove(0);
    }
    
    public void commit() {
        //discard all rollback logs
        rollbackLog.clear();
    }   
    
    private void log(String key, String value) {
        if (rollbackLog.isEmpty())
            return;
        
        Map<String, String> currentLog = rollbackLog.get(0);
        if (currentLog == null)
            return;
        
        if (currentLog.get(key) == null)
            currentLog.put(key, value);
    }
    
    private void updateValueCount(String newValue, String oldValue) {
        if (oldValue != null) {
            long num = count(oldValue);
            valueCounter.put(oldValue, --num);
        }
        if (newValue != null) {
            // increase the count of the new value
            long num = count(newValue);
            valueCounter.put(newValue, ++num);            
        }
    }   
}