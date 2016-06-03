package cphbusiness.dk.androidproject;

import android.content.Context;
import android.telephony.SmsManager;
import android.widget.Toast;

/**
 * Created by kalkun on 01-06-2016.
 */
public class SMSService {
    private Context ctx;

    public SMSService(Context ctx){
        this.ctx = ctx;
    }

    public void sendSMS(User sendFrom, User sendTo){
        String message = "IByenApp, you have recived a friend request from: " + sendFrom.getName()
                + ". Go to My Requests menu in IByenApp to accept the request";

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(sendTo.getPhoneNo(), null, message, null, null);
            Toast.makeText(ctx, "SMS send", Toast.LENGTH_SHORT).show();
        }

        catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(ctx, "SMS Failed!", Toast.LENGTH_SHORT).show();
        }
    }
}
