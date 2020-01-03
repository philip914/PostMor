package com.mdhgroup2.postmor.Register;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.mdhgroup2.postmor.database.DTO.Account;
import com.mdhgroup2.postmor.database.interfaces.IAccountRepository;
import com.mdhgroup2.postmor.database.repository.AccountRepository;
import com.mdhgroup2.postmor.database.repository.DatabaseClient;

import java.util.List;
import java.util.Random;

import androidx.lifecycle.ViewModel;

public class RegisterViewModel extends ViewModel {
    private final IAccountRepository accountDB;
    private Account myAccount;
    private String confirmPassword;
    private List<String> addresses;
    private Random rand = new Random();
    private String callResult = "";

    public RegisterViewModel(){
        accountDB = DatabaseClient.getAccountRepository();
        myAccount = new Account();
        addresses = accountDB.getRandomAddresses(20);
    }

    public void generateAddresses(){
        myAccount.Address = addresses.get(rand.nextInt(2));
    }

    public String getAddress(){return myAccount.Address;}

    public void register(){
        myAccount.Password = "String123!";
        confirmPassword = myAccount.Password;
        dbRegister reg = new dbRegister();
        reg.execute(myAccount);
    }

    public void setAccoutName(String name){
        myAccount.Name = name;
    }

    public String getAccountName(){return myAccount.Name;}

    public void setAccountEmail(String email){
        myAccount.Email = email;
    }

    public void setAccountImage(Bitmap image){
        myAccount.Picture = image;
    }

    public void setAccountPassword(String password){
        myAccount.Password = password;
    }

    public void setAccountConfirmPassword(String password){confirmPassword = password;}

    public AccountRepository.PasswordStatus checkPasswordValidity(){
        AccountRepository.PasswordStatus valid = accountDB.isValidPassword(myAccount.Password);
        if(valid == AccountRepository.PasswordStatus.Ok);
        {
            if (myAccount.Password.equals(confirmPassword)) {
                return AccountRepository.PasswordStatus.Ok;
            }
            return valid;
        }
    }

    public String validateAccountInformation(){
        if(myAccount.Email == null || myAccount.Email.equals(""))
            return "Email field cannot be empty";
        else if (myAccount.Address == null)
            return "Address field cannot be empty, press the regenerate button.";
        else if (myAccount.Name == null || myAccount.Name.equals(""))
            return "Name field cannot be empty";
        return "True";
    }

    private class dbRegister extends AsyncTask<Account, Void, String>{
        @Override
        protected String doInBackground(Account... accounts) {
            String result = "";
            try {
//                result = accountDB.registerAccount(accounts[0]);
            }catch(Exception e){
                result = e.getMessage();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result){
            callResult = result;
        }
    }
}
