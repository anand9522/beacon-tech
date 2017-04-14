package com.think.mozzo_test_java.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

/**
 * Created by anand on 19/12/16.
 */

public class DataSyncAdapter extends AbstractThreadedSyncAdapter {
    public DataSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {
        //Synchronization Code here
        /* we can do things like
          1) downloading data from a server
          2) Uploading data to server
          3) be sure to handle network related exceptions.
        */
         // For exapmple i am connecting to a server
    }
}
