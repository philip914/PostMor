package com.mdhgroup2.postmor.database.repository;

import com.mdhgroup2.postmor.database.DTO.EditMsg;
import com.mdhgroup2.postmor.database.Entities.Message;
import com.mdhgroup2.postmor.database.db.LetterDao;
import com.mdhgroup2.postmor.database.db.ManageDao;
import com.mdhgroup2.postmor.database.interfaces.ILetterRepository;

import java.util.ArrayList;
import java.util.Date;

public class LetterRepository implements ILetterRepository {

    private LetterDao letterdao;
    private ManageDao managedao;

    public LetterRepository(LetterDao letterDao, ManageDao manageDao){
        this.letterdao = letterDao;
        this.managedao = manageDao;
    }

    @Override
    public Date getPickupTime() {
        return managedao.getUserPickupTime();
    }

    @Override
    public EditMsg getOrStartGenerictDraft() {
        EditMsg msg = letterdao.getGenericDraft();
        if(msg == null){
            msg = new EditMsg();
            msg.InternalMessageID = managedao.getNewMsgId();

            createNewDraft();

        }
        return msg;
    }

    @Override
    public EditMsg getOrStartDraft(int recipientID) {
        EditMsg msg = letterdao.getDraftById(recipientID);
        if(msg == null){
            msg = new EditMsg();
            msg.InternalMessageID = managedao.getNewMsgId();
            msg.RecipientID = recipientID;

            createNewDraft(recipientID);
        }
        return msg;
    }

    private void createNewDraft(){
        Message msg = draftDefaults();
        msg.UserID = 0;
        managedao.addMessage(msg);
    }

    private void createNewDraft(int recipientID){
        Message msg = draftDefaults();
        msg.UserID = recipientID;
        managedao.addMessage(msg);
    }

    private Message draftDefaults(){
        Message msg = new Message();
        msg.InternalMessageID = managedao.getNewMsgId();
        msg.ExternalMessageID = 0;
        msg.WrittenBy = managedao.getUserId();
        msg.IsDraft = true;
        msg.IsOutgoing = false;
        msg.IsRead = false;
        msg.TimeStamp = null;
        msg.DeliveryTime = null;
        msg.SenderPublicKey = managedao.getUserPublicKey();
        msg.Images = null;
        msg.Text = null;
        return msg;
    }

    @Override
    public void saveDraft(EditMsg edit) {
        Message msg = letterdao.getMessageById(edit.InternalMessageID);
        msg.Text = edit.Text;
        msg.Images = edit.Images;
        msg.UserID = edit.RecipientID;

        letterdao.updateMessage(msg);
    }

    @Override
    public boolean sendDraft(EditMsg edit) {
        Message msg = letterdao.getMessageById(edit.InternalMessageID);
        if(msg == null){
            return false;
        }
        msg.Text = edit.Text;
        msg.Images = edit.Images;
        msg.UserID = edit.RecipientID;

        letterdao.incrementOutoing();

        // Send to server

        return true;
    }
}
