package lk.ac.mrt.cse.dbs.simpleexpensemanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class DataHandler extends SQLiteOpenHelper {


    private static final String DB_name = "200328P";
    private static final int VERSION = 3;

    //table names
    private static final String TABLE_1 = "accounts";
    private static final String TABLE_2 = "transactions";

    //table 1 columns
    private static final String ACCOUNT_NO = "account_no";
    private static final String BANK = "bank";
    private static final String NAME = "name";
    private static final String BALANCE = "balance";

    //table 2 columns
    private static final String ID = "id";
    private static final String DATE = "date";
    private static final String AMOUNT = "amount";
    private static final String TYPE = "type";
    private static final String ACCOUNT = "account";

    public DataHandler(Context context) {
        super(context, DB_name, null, VERSION);
    }

    //create tables
    @Override
    public void onCreate(SQLiteDatabase database) {
        String TABLE1_CREATE_QUERY = "CREATE TABLE "+ TABLE_1 +
                "("
                +ACCOUNT_NO+ " TEXT PRIMARY KEY, "
                +BANK + " TEXT, "
                +NAME + " TEXT, "
                +BALANCE +" DOUBLE "
                +");";

        String TABLE2_CREATE_QUERY = "CREATE TABLE "+ TABLE_2 +
                "("
                +ID+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
                +DATE+ " TEXT, "
                +ACCOUNT + " TEXT, "
                +TYPE + " TEXT, "
                +AMOUNT +" DOUBLE "
                +");";

        database.execSQL(TABLE1_CREATE_QUERY);
        database.execSQL(TABLE2_CREATE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int odlVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS "+TABLE_1);
        database.execSQL("DROP TABLE IF EXISTS "+TABLE_2);
        onCreate(database);
    }


    //add account details to table 1 (accounts)
    public void addAccount(Account account) {
        try{
            SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

            ContentValues contentValues = new ContentValues();

            contentValues.put(ACCOUNT_NO, account.getAccountNo());
            contentValues.put(BANK, account.getBankName());
            contentValues.put(NAME, account.getAccountHolderName());
            contentValues.put(BALANCE, account.getBalance());

            //save to table
            if(isExist(account.getAccountNo())){
                System.out.println("Account number is already exist");
            }else{
                sqLiteDatabase.insert(TABLE_1, null, contentValues);

            }
        }catch(Exception e){
            System.out.println(e);
        }
    }

    //get transaction list from table 2(transactions)
    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<Transaction> getTransactionList() throws ParseException {
        List<Transaction> trans = new ArrayList<>();
        try{
            SQLiteDatabase sqLiteDatabase = getReadableDatabase();
            String query = "SELECT * FROM " + TABLE_2;

            Cursor cursor = sqLiteDatabase.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    Transaction transaction = new Transaction();

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                    ExpenseType et;
                    if (cursor.getString(3).equals("EXPENSE")) {
                        et = ExpenseType.EXPENSE;
                    } else {
                        et = ExpenseType.INCOME;
                    }
                    transaction.setDate(sdf.parse(cursor.getString(1)));
                    transaction.setAccountNo(cursor.getString(2));
                    transaction.setExpenseType(et);
                    transaction.setAmount(cursor.getDouble(4));

                    trans.add(transaction);

                } while (cursor.moveToNext());
                cursor.close();
            }
        }catch (Exception e){
            System.out.println(e);
        }
        return trans;
    }

    //add transaction details to table 2 (transactions)
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addTransaction(Transaction transaction){
        try{
            SQLiteDatabase sqLiteDatabase = getWritableDatabase();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            ContentValues contentValues = new ContentValues();


            contentValues.put(DATE, sdf.format(transaction.getDate()));
            contentValues.put(ACCOUNT, transaction.getAccountNo());
            contentValues.put(TYPE, transaction.getExpenseType().name());
            contentValues.put(AMOUNT, transaction.getAmount());

            sqLiteDatabase.insert(TABLE_2, null, contentValues);

        }catch(Exception e){
            System.out.println(e);
        }
    }

    //get account number list from table 1 (accounts)
    public List<String> getAccountNoList() {
        List<String> accounts = new ArrayList<>();
        try{
            SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
            String query = "SELECT " + ACCOUNT_NO + " FROM " + TABLE_1;

            Cursor cursor = sqLiteDatabase.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    String account_no = cursor.getString(0);
                    accounts.add(account_no);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }catch (Exception e){
            System.out.println(e);
        }

        return accounts;
    }

    //get account list from table 1 (accounts)
    public List<Account> getAccountList(){
        List<Account> accounts = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        String query = "SELECT * FROM "+TABLE_1;

        Cursor cursor = sqLiteDatabase.rawQuery(query,null);

        if(cursor.moveToFirst()){
            do{
                Account account = new Account();

                account.setAccountNo(cursor.getString(0));
                account.setBankName(cursor.getString(1));
                account.setAccountHolderName(cursor.getString(2));
                account.setBalance(cursor.getDouble(3));

                accounts.add(account);
            }while (cursor.moveToNext());
            cursor.close();

        }
        return accounts;
    }

    //get account details from table 1 for relevant account number and return that account object
    public Account getAccount(String account_no){
        Account account = new Account();
        try{
            SQLiteDatabase sqLiteDatabase = getReadableDatabase();
            String query = "SELECT * FROM " + TABLE_1 + " WHERE " + ACCOUNT_NO + " == ? ";
            String[] selectionArgs = {account_no};
            Cursor cursor = sqLiteDatabase.rawQuery(query, selectionArgs);

            if (cursor.moveToFirst()) {
                account.setAccountNo(cursor.getString(0));
                account.setBankName(cursor.getString(1));
                account.setAccountHolderName(cursor.getString(2));
                account.setBalance(cursor.getDouble(3));
            }
            cursor.close();

        }catch(Exception e){
            System.out.println(e);
        }
        return account;
    }

    //Check
    public Boolean isExist(String account_no){
        boolean exist = false;
        try{
            SQLiteDatabase sqLiteDatabase = getReadableDatabase();
            String query = "SELECT * FROM " + TABLE_1 + " WHERE " + ACCOUNT_NO + " == ? ";
            String[] selectionArgs = {account_no};
            Cursor cursor = sqLiteDatabase.rawQuery(query, selectionArgs);

            if (cursor.moveToFirst()) {
                exist = true;
            }else {
                exist = false;
            }
            cursor.close();

        }catch(Exception e){
            System.out.println(e);
        }
        return exist;
    }

    // update account balance according to relevant transaction
    public void updateBalance(Double amount, ExpenseType expenseType,Account account){
        try{
            SQLiteDatabase sqLiteDatabase = getWritableDatabase();

            ContentValues contentValues = new ContentValues();


            contentValues.put(ACCOUNT_NO, account.getAccountNo());
            contentValues.put(BANK, account.getBankName());
            contentValues.put(NAME, account.getAccountHolderName());

            if (expenseType == ExpenseType.EXPENSE){
                contentValues.put(BALANCE, account.getBalance() - amount);
            }else {
                contentValues.put(BALANCE, account.getBalance() + amount);
            }

            String[] stringArgs = {account.getAccountNo()};
            sqLiteDatabase.update(TABLE_1, contentValues, ACCOUNT_NO + " = ?", stringArgs);

        }catch(Exception e){
            System.out.println(e);
        }
    }

    //delete account
    public Integer deleteAccount(String account_no){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String[] stringArgs = {account_no};
        return sqLiteDatabase.delete(TABLE_1,ACCOUNT_NO+" = ?",stringArgs);
    }
}
