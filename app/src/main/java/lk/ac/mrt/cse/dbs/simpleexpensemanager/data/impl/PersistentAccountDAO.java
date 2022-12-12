package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.DataHandler;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO implements AccountDAO {
    Context context;
    DataHandler dataHandler;

    public PersistentAccountDAO(Context c){
        context = c;
        dataHandler = new DataHandler(context);
    }

    @Override
    public List<String> getAccountNumbersList() {
        return dataHandler.getAccountNoList();
    }

    @Override
    public List<Account> getAccountsList() {
        return dataHandler.getAccountList();
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        if(dataHandler.isExist(accountNo)){
            return dataHandler.getAccount(accountNo);
        }else {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
    }

    @Override
    public void addAccount(Account account) {
        dataHandler.addAccount(account);
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        if(dataHandler.isExist(accountNo)){
            dataHandler.deleteAccount(accountNo);
        }else{
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        if(dataHandler.isExist(accountNo)){
            Account account = dataHandler.getAccount(accountNo);
            dataHandler.updateBalance(amount,expenseType,account);
        }else{
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
    }
}
