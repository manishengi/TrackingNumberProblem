package trackingnumberproblem;
import java.util.ArrayList;
import trackingnumberproblem.Range;
import trackingnumberproblem.Transaction;

public class TransactionManager {
    ArrayList<Transaction> transactions = new ArrayList<Transaction>();
    
    public void addNewTransaction(Transaction t){
        
        int len = transactions.size();
        
        if(len == 0){
            transactions.add(t);
            return;
        }
        
        Transaction current;
        
        for(int i = 0; i < len; i++){
            current = transactions.get(i);
            if (t.range.classify(current.range) == Range.Relation.SAME){
                overwriteCodes(i, t);
                break;
            }
            else if (t.range.classify(current.range) == Range.Relation.SUPERSET){
                if (i == 0){
                    overwriteCodes(i, t);
                }
                mergeIfPossible();
            }
            else if (t.range.classify(current.range) == Range.Relation.SUBSET){
                current.isDeleted = true;
                Transaction t1 = new Transaction(new Range(current.range.lo, t.range.lo-1),current.statusCode, current.transferCode);
                Transaction t2 = new Transaction(new Range(t.range.hi+1, current.range.hi),current.statusCode, current.transferCode);
                transactions.add(i, t1);
                transactions.add(i+1, t);
                transactions.add(i+2, t2);
                break;
            }
            else if (t.range.classify(current.range) == Range.Relation.LESSOVERLAP){
                System.out.println("Lessoverlap");
                current.isDeleted = true;
                Transaction t1 = new Transaction(new Range(t.range.hi+1, current.range.hi),current.statusCode, current.transferCode);
                transactions.add(i, t);
                transactions.add(i+1, t1);
                mergeIfPossible(); 
            }
            else if (t.range.classify(current.range) == Range.Relation.MOREOVERLAP){
                System.out.println("Moreoverlap "+ i);
                current.isDeleted = true;
                Transaction t1 = new Transaction(new Range(current.range.lo, t.range.lo - 1),current.statusCode, current.transferCode);
                transactions.add(i, t1);
                transactions.add(i+1, t);
                mergeIfPossible(); 
            }
            else if (t.range.classify(current.range) == Range.Relation.LESSDISJOINT || t.range.classify(current.range) == Range.Relation.MOREDISJOINT){
                transactions.add(i+1, t);
                mergeIfPossible();   
            }
        }
    }
    
    public void displayTransactions(){
        for(Transaction t : transactions) {
            if( !t.isDeleted) {
                String printString = "";        
                printString += t.range.getLow()+"\t"+t.range.getHigh()+"\t"+(t.statusCode+"")+"\t"+(t.transferCode+"");
                System.out.println(printString);
            }
        }
        System.out.println("--------------");
        
    }
    
    private void overwriteCodes(int index, Transaction t){
        Transaction x = transactions.get(index);
        x.statusCode = t.statusCode;
        x.transferCode = t.transferCode;
    }
    
    private void mergeIfPossible() {
        for (int i = 0; i < transactions.size()-1 ;i++) {
            
            if(transactions.get(i).isDeleted == false) 
            {
                for (int j = i+1; j < transactions.size() ;j++) 
                {
                    if(transactions.get(j).isDeleted == false)
                    {
                        if ((transactions.get(i).statusCode ==  transactions.get(j).statusCode) && ( transactions.get(i).transferCode ==  transactions.get(j).transferCode))
                             {
                                if( transactions.get(i).range.hi == transactions.get(j).range.lo - 1 )
                                {
                                        transactions.get(i).range.hi = transactions.get(j).range.hi;
                                        transactions.get(j).isDeleted = true;
                                }
                                
                                if (( transactions.get(i).range.lo == transactions.get(j).range.lo ) &&  ( transactions.get(i).range.hi == transactions.get(j).range.hi ) )
                                {
                                        transactions.get(j).isDeleted = true;
                                }
                                
                             }
                    }       
                    
                }
                
            }
        }
        
    }
    
    public String tableToString(){
    String s = "";
    Transaction t;
        for(int i = 0; i < transactions.size(); i++){
            t = transactions.get(i);
            if(!t.isDeleted){
                s+=t.range.lo + " " + t.range.hi + " " + t.statusCode + t.transferCode; 
            }
        }
        return s;
    }
}
