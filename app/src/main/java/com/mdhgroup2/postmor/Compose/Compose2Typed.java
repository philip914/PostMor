package com.mdhgroup2.postmor.Compose;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.mdhgroup2.postmor.MainActivity;
import com.mdhgroup2.postmor.MainActivityViewModel;
import com.mdhgroup2.postmor.R;
import com.mdhgroup2.postmor.database.DTO.Contact;
import com.mdhgroup2.postmor.database.DTO.EditMsg;

public class Compose2Typed extends Fragment {

    private Compose2TypedViewModel mViewModel;
    private MainActivityViewModel mainVM;
    private TextInputEditText inputEditText;
    private Button sendButton;


    public static Compose2Typed newInstance() {
        return new Compose2Typed();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.compose2_typed_fragment, container, false);

        mViewModel = ViewModelProviders.of(this).get(Compose2TypedViewModel.class);
        mainVM = ViewModelProviders.of(getActivity()).get(MainActivityViewModel.class);

        inputEditText = view.findViewById(R.id.c2typed_textInputEditText);
        sendButton = view.findViewById(R.id.c2typed_Send);


        // Find the recipient ID from the recipient fragment
        final Integer recipientID = null;
        final Contact recipient = mainVM.getChosenRecipient();

        // Observe livedata for draft
        mViewModel.getDraftMsg().observe(this, new Observer<EditMsg>() {
            @Override
            public void onChanged(EditMsg editMsg) {
                // Update the ui
                if(editMsg.Text != null){
                    inputEditText.setText(editMsg.Text);
                }
            }
        });

        // If compose is reopened from choosing contact,this will return false
        // and not get a new draft.
        if(mViewModel.isFirstTimeOpened){
            if(recipient == null) {// If no recipient has been chosen, send null
                mViewModel.getDraft(0);
                mViewModel.isFirstTimeOpened = false;
            }else{// Else send id
                mViewModel.getDraft(recipient.UserID);
                mViewModel.isFirstTimeOpened = false;
            }
        }

        mainVM.getChosenRec().observe(this, new Observer<Contact>() {
            @Override
            public void onChanged(Contact contact) {
                if(contact != null) {
                    mViewModel.changeRecipient(contact.UserID);
                }
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String text = inputEditText.getText().toString();

                if(mainVM.getChosenRecipient() != null) {
                    if (!text.equals("")) {
                        mViewModel.addText(inputEditText.getText().toString());
                        Log.d("test", "onClick: send");
                        //mViewModel.sendMessage();
                        SendMessageTask sendMessageTask = new SendMessageTask(getContext(), mViewModel);
                        sendMessageTask.execute(mViewModel.getMsg());
                    } else {
                        Toast.makeText(getActivity(),"No message!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getActivity(), "No recipient selected!", Toast.LENGTH_SHORT).show();

                }
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        mViewModel.addText(inputEditText.getText().toString());
        if(!mViewModel.isSend){
            mViewModel.saveDraft();
        }
        mainVM.removeRecipient();
        super.onDestroy();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(Compose2TypedViewModel.class);
        // TODO: Use the ViewModel
    }
}

class SendMessageTask extends AsyncTask<EditMsg, Void, Boolean>{

    private ProgressDialog mProgressDialog;
    private Context mContext;
    private Compose2TypedViewModel mViewModel;

    public SendMessageTask(Context context, Compose2TypedViewModel viewmodel){
        mContext = context;
        mProgressDialog = new ProgressDialog(mContext);
        mViewModel = viewmodel;
    }

    @Override
    protected void onPreExecute() {
        mProgressDialog.setMessage("Sending letter...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();
    }

    @Override
    protected Boolean doInBackground(EditMsg... editMsgs) {

        Log.d("test", "doInBackground: test");

        boolean returnValue = false;
        if(editMsgs != null){
            Log.d("test", "doInBackground: text: "+editMsgs[0].Text);
            Log.d("test", "doInBackground: recipient: "+editMsgs[0].RecipientID);
            Log.d("test", "doInBackground: messageID: "+editMsgs[0].InternalMessageID);
            returnValue = mViewModel.sendMessage(editMsgs[0]);
            Log.d("test", "doInBackground: send: "+returnValue);
        }

        return returnValue;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        if(mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }

    }
}
