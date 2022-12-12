package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.DataHandler;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO implements TransactionDAO {

    Context context;
    DataHandler dataHandler;

    public PersistentTransactionDAO(Context c){
        context = c;
        dataHandler = new DataHandler(context);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        Transaction transaction = new Transaction(date,accountNo,expenseType,amount);
        dataHandler.addTransaction(transaction);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public List<Transaction> getAllTransactionLogs() throws ParseException {
        return dataHandler.getTransactionList();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) throws ParseException {
        List<Transaction> transactionList = dataHandler.getTransactionList();
        if (transactionList.size()<=limit){
            return transactionList;
        }else{
            return transactionList.subList(transactionList.size()-limit,transactionList.size());
        }
    }
}
