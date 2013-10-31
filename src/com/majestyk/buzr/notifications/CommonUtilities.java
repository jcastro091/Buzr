package com.majestyk.buzr.notifications;

import android.content.Context;
import android.content.Intent;

public final class CommonUtilities {

    public static final String SERVER_URL = "http://buzr-dev.elasticbeanstalk.com/";

    public static final String SENDER_ID = "1022222579050";

    static final String TAG = "Buzr";

    static final String DISPLAY_MESSAGE_ACTION = "com.majestyk.buzr.DISPLAY_MESSAGE";
    
    static final String EXTRA_MESSAGE = "message";

    static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }
}
