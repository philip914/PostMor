package com.mdhgroup2.postmor.database.interfaces;

import com.mdhgroup2.postmor.database.DTO.Contact;
import com.mdhgroup2.postmor.database.DTO.UserCard;

import java.util.List;

public interface IContactRepository {
    UserCard findByAddress(String address);
    List<Contact> getContacts();
    void addContact(int ID);
    void deleteContact(int ID);
    UserCard getUserCard(int ID);

}