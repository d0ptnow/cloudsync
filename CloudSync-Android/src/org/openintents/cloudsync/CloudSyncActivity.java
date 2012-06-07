/*******************************************************************************
 * Copyright 2011 Google Inc. All Rights Reserved.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package org.openintents.cloudsync;

import org.openintents.cloudsync.client.MyRequestFactory;
import org.openintents.cloudsync.client.MyRequestFactory.HelloWorldRequest;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

/**
 * Main activity - requests "Hello, World" messages from the server and provides
 * a menu item to invoke the accounts activity.
 */
public class CloudSyncActivity extends Activity {
    /**
     * Tag for logging.
     */
    private static final String TAG = "CloudSyncActivity";

    /**
     * The current context.
     */
    private Context mContext = this;

    /**
     * A {@link BroadcastReceiver} to receive the response from a register or
     * unregister request, and to update the UI.
     */
    private final BroadcastReceiver mUpdateUIReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String accountName = intent.getStringExtra(DeviceRegistrar.ACCOUNT_NAME_EXTRA);
            int status = intent.getIntExtra(DeviceRegistrar.STATUS_EXTRA,
                    DeviceRegistrar.ERROR_STATUS);
            String message = null;
            String connectionStatus = Util.DISCONNECTED;
            if (status == DeviceRegistrar.REGISTERED_STATUS) {
                message = getResources().getString(R.string.registration_succeeded);
                connectionStatus = Util.CONNECTED;
            } else if (status == DeviceRegistrar.UNREGISTERED_STATUS) {
                message = getResources().getString(R.string.unregistration_succeeded);
            } else {
                message = getResources().getString(R.string.registration_error);
            }

            // Set connection status
            SharedPreferences prefs = Util.getSharedPreferences(mContext);
            prefs.edit().putString(Util.CONNECTION_STATUS, connectionStatus).commit();

            // Display a notification
            Util.generateNotification(mContext, String.format(message, accountName));
        }
    };

    /**
     * Begins the activity.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        // Register a receiver to provide register/unregister notifications
        registerReceiver(mUpdateUIReceiver, new IntentFilter(Util.UPDATE_UI_INTENT));
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences prefs = Util.getSharedPreferences(mContext);
        String connectionStatus = prefs.getString(Util.CONNECTION_STATUS, Util.DISCONNECTED);
        if (Util.DISCONNECTED.equals(connectionStatus)) {
            startActivity(new Intent(this, AccountsActivity.class));
        }
        setScreenContent(R.layout.hello_world);
    }

    /**
     * Shuts down the activity.
     */
    @Override
    public void onDestroy() {
        unregisterReceiver(mUpdateUIReceiver);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        // Invoke the Register activity
        menu.getItem(0).setIntent(new Intent(this, AccountsActivity.class));
        return true;
    }

    // Manage UI Screens

    private void setHelloWorldScreenContent() {
        setContentView(R.layout.hello_world);

        final TextView helloWorld = (TextView) findViewById(R.id.hello_world);
        final Button sayHelloButton = (Button) findViewById(R.id.say_hello);
        String tag="vincent";
        
        
        /**
        Uri customUri = Uri.parse(IdMapContentProvider.CONTENT_URI.toString());
        Log.d(tag, "customeUri:-> "+customUri.toString());
        
        ContentValues values = new ContentValues();
        values.put(IdMapTable.COLUMN_LOCAL_ID,100000+Math.round((Math.random()*30)));
        values.put(IdMapTable.COLUMN_APPENG_ID,Math.round((Math.random()*30)) );
        values.put(IdMapTable.PACKAGE_NAME,"com.vettukal.oi");
        
        Uri insertUri;
        insertUri = getContentResolver().insert(customUri, values);
        Log.d(tag, "inserted uri custom return:-> "+insertUri.toString());
        
        Cursor customcurse = getContentResolver().query(customUri, null, null, null, null);
        if (customcurse.moveToFirst()) { }

        int totRows = customcurse.getCount();
        Log.d(tag, "totalrow:-> "+totRows );
        for(int i=0;i<totRows;i++) {
        	Log.d(tag, customcurse.getString(0));
        	Log.d(tag, customcurse.getString(1));
        	Log.d(tag, customcurse.getString(2));
            customcurse.moveToNext();
        }
        
        /**
        Uri deleteUri = Uri.parse(ModifyContentProvider.CONTENT_URI.toString());
        deleteUri = Uri.withAppendedPath(deleteUri, "1");
        int delRetVal = getContentResolver().delete(deleteUri, null, null);
        Log.d(tag, "the return value from the deleted op is : "+delRetVal);
        */
        
        /**
        Uri modUri = Uri.parse(ModifyContentProvider.CONTENT_URI.toString());
        values = new ContentValues();
        values.put(ModifyTable.COLUMN_LOCAL_ID, 0156);
        values.put(ModifyTable.COLUMN_MODIGY_DATE, 0154345);
        insertUri = getContentResolver().insert(modUri, values);
        Log.d(tag, "inserted Mod uri is "+insertUri.toString());
        
         modUri = Uri.parse(ModifyContentProvider.CONTENT_URI.toString());
        modUri = Uri.withAppendedPath(modUri, "1");
        values = new ContentValues();
        values.put(ModifyTable.COLUMN_LOCAL_ID, 56);
        values.put(ModifyTable.COLUMN_MODIGY_DATE, 54345);
        int retVal = getContentResolver().update(modUri, values, null, null);
        Log.d(tag, "after update on 1 _id the return value is:-> "+retVal);
        
        modUri = Uri.parse(ModifyContentProvider.CONTENT_URI.toString());
        
        customcurse = getContentResolver().query(modUri, null, null, null, null);
        if (customcurse.moveToFirst()) { }

        totRows = customcurse.getCount();
        Log.d(tag, "totalrow in modtable:-> "+totRows );
        for(int i=0;i<totRows;i++) {
        	Log.d(tag, customcurse.getString(0));
        	Log.d(tag, customcurse.getString(1));
        	Log.d(tag, customcurse.getString(2));
            customcurse.moveToNext();
        }
        */
        sayHelloButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                sayHelloButton.setEnabled(false);
                helloWorld.setText(R.string.contacting_server);

                // Use an AsyncTask to avoid blocking the UI thread
                new AsyncTask<Void, Void, String>() {
                    private String message;

                    @Override
                    protected String doInBackground(Void... arg0) {
                        MyRequestFactory requestFactory = Util.getRequestFactory(mContext,
                                MyRequestFactory.class);
                        final HelloWorldRequest request = requestFactory.helloWorldRequest();
                        Log.i(TAG, "Sending request to server");
                        request.getMessage().fire(new Receiver<String>() {
                            @Override
                            public void onFailure(ServerFailure error) {
                                message = "Failure: " + error.getMessage();
                            }

                            @Override
                            public void onSuccess(String result) {
                                message = result;
                            }
                        });
                        return message;
                    }

                    @Override
                    protected void onPostExecute(String result) {
                        helloWorld.setText(result);
                        sayHelloButton.setEnabled(true);
                    }
                }.execute();
            }
        });
        
        final Button syncButton = (Button) findViewById(R.id.sync_test);
        syncButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	helloWorld.setText("Syncing Process Started");
            	testSync();
            }
			       
        });
    }

    /**
     * Sets the screen content based on the screen id.
     */
    private void setScreenContent(int screenId) {
        setContentView(screenId);
        switch (screenId) {
            case R.layout.hello_world:
                setHelloWorldScreenContent();
                break;
        }
    }
    
    private void testSync() {
    	
    	AsyncTaskList atl = new AsyncTaskList(this);  
    	String[][] strnull = null;
        atl.execute(strnull);
		
	}
    
    void doneSyncing() {
    	final TextView helloWorld = (TextView) findViewById(R.id.hello_world);
    	final Button syncButton = (Button) findViewById(R.id.sync_test);
    	helloWorld.setText("Finished!");
    	syncButton.setClickable(true);
	}
}
