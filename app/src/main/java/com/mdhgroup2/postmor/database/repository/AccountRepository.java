package com.mdhgroup2.postmor.database.repository;

import com.mdhgroup2.postmor.database.DTO.Account;
import com.mdhgroup2.postmor.database.Entities.Settings;
import com.mdhgroup2.postmor.database.db.AccountDao;
import com.mdhgroup2.postmor.database.db.Converters;
import com.mdhgroup2.postmor.database.db.ManageDao;
import com.mdhgroup2.postmor.database.db.Utils;
import com.mdhgroup2.postmor.database.interfaces.IAccountRepository;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AccountRepository implements IAccountRepository {

    private AccountDao accountDb;
    private ManageDao manageDb;

    public AccountRepository(AccountDao accountDao, ManageDao manageDao){
        accountDb = accountDao;
        manageDb = manageDao;
    }

    @Override
    public List<String> getRandomAddresses(int count) {
        List<String> ls = new ArrayList<>();
        ls.add("Bajskastargatan 55");
        ls.add("Kattpissgatan 42  ");
        return ls;
    }

    @Override
    public boolean isValidPassword(String pass) {
        // Check locally
        return true;
    }

    @Override
    public boolean registerAccount(Account acc) {
        // Query server and, on success, save to database.


        String data = String.format("{" +
                "\"name\" : \"%s\", " +
                "\"password\" : \"%s\", " +
                "\"email\" : \"%s\", " +
                "\"adress\" : \"%s\", " +
                "\"picture\" : \"%s\"" +
                "}",
                acc.Name,
                acc.Password,
                acc.Email,
                acc.Address,
                Converters.bitmapToBase64(acc.Picture));

        try {
            JSONObject json = Utils.APIPost(Utils.baseURL + "/identity/register", new JSONObject(data));

            Settings s = new Settings();
            s.ID = -1; // Server response
            s.Address = acc.Address;
            s.ProfilePicture = acc.Picture;
            s.PickupTime = null; // Server response
            s.DeliveryTime = null; // Server response
            s.PublicKey = null; // Server response
            s.PrivateKey = null; // Server response
            s.AuthToken = null; // Server response
            s.RefreshToken = null; // Server response

            s.Email = acc.Email;
            s.Password = acc.Password;
            s.OutgoingLetterCount = 0;
            s.IsLoggedIn = true;

            DatabaseClient.nukeDatabase();

            accountDb.registerAccount(s);
        }
        catch (IOException e){
            return false;
        }
        catch(JSONException j){
            // Return an object with more descriptive errors.
            return false;
        }






        return false; // Server success or fail
    }

    @Override
    public boolean signIn(String email, String password) {
        // Query server for login and, on success, log in locally.
        // If account is not the current in Settings, clear database.
        if(manageDb.getUserEmail() == email && manageDb.getUserPassword() == password){
            // Query server
            accountDb.setSignedIn();
            return manageDb.refreshToken();
        }
        else{
            String data = String.format("{" +
                    "\"email\" : \"%s\", " +
                    "\"password\" : \"%s\"" +
                    "}", email, password);
//            String data = "{}";

            String authToken;
            String refreshToken;

            try{
                JSONObject json = Utils.APIPost(Utils.baseURL + "/identity/login", new JSONObject(data));

                authToken = json.getString("token");

                refreshToken = json.getString("refreshToken");


            }
            catch (IOException e){
                return false;
            }
            catch (JSONException j){
                return false;
                // Failed to update key. Possibly offline.
            }

            // Query server
            //
            // On success, empty all tables, and fetch all data.

            String data2 = String.format("{\"token\" : \"%s\"}", authToken);
            try {
                JSONObject json = Utils.APIPost(Utils.baseURL + "/user/fetchalldata", new JSONObject(data2));
                DatabaseClient.nukeDatabase();
            }
            catch (JSONException | IOException e){
                return false;
            }
            // API "/user/fetchalldata"
            // Save all data
            manageDb.setAuthToken(authToken);
            manageDb.setRefreshToken(refreshToken);

            return true;
        }
    }



    @Override
    public void signOut() {
        accountDb.setSignedOut();
        manageDb.setAuthToken("");
        manageDb.setRefreshToken("");
    }
}
