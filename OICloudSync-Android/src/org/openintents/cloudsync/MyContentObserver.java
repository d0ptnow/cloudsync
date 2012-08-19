package org.openintents.cloudsync;

import java.util.TimerTask;

import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.os.Handler;
import android.util.Log;

public class MyContentObserver extends ContentObserver {
    public MyContentObserver(Handler h) {
        super(h);
    }

	@Override
    public boolean deliverSelfNotifications() {
        return true;
    }

    @Override
    public void onChange(boolean selfChange) {
        
        Log.d("vincent", "Something is fishy");
        
        SharedPreferences prefs = Util.getSharedPreferences(activity);
		boolean inSync = prefs.getBoolean(Util.IN_SYNC, false);
		long nowTime = System.currentTimeMillis();
		long lastTime = prefs.getLong(Util.LAST_TIME, 0);
		if( (nowTime-lastTime) < 30*1000 & inSync) {
			//This condition is added so that when contentProvider changes
			// because of the sync itself, it does not start another sync.
			return;
		}
        updateTimeTask.setActivity(getActivity());
        updateTimeTask.setHandle(timeHandle);
        timeHandle.removeCallbacks(updateTimeTask);
        timeHandle.postDelayed(updateTimeTask, 30000);
        super.onChange(selfChange);
        

    }
    UpdateTimeTask updateTimeTask = new UpdateTimeTask();
    
    Handler timeHandle = new Handler();
	private CloudSyncActivity activity;
	public CloudSyncActivity getActivity() {
		return activity;
	}

	public void setActivity(CloudSyncActivity cloudSyncActivity) {
		this.activity = cloudSyncActivity;
		
	}

}


class UpdateTimeTask extends TimerTask {
	private static final boolean debug = true;
	private static final String TAG = "UpdateTimeTask";
	private CloudSyncActivity activity;
	private Handler handle;

	public void run() {
		   SharedPreferences prefs = Util.getSharedPreferences(activity);
		   
		   SharedPreferences.Editor editor = prefs.edit();
		   boolean inSync = prefs.getBoolean(Util.IN_SYNC, false);
		   long nowTime = System.currentTimeMillis();
		   long lastTime = prefs.getLong(Util.LAST_TIME, 0);
		   if( (nowTime-lastTime) > Util.SYNC_DIFF_TIME | !inSync) {
			   //editor.putLong(Util.LAST_TIME, nowTime);
			   //editor.putBoolean(Util.IN_SYNC, true);
			   Log.d("vincent", "Do the sync baccha!!");
			   //editor.commit();
			   activity.startStandAloneSync();
		   } else {
			   // I hope when a sync is rejected because already a sync is taking place then
			   // using the handle that was passed passing this object itself and delay with 120 secs.
			   if (debug) Log.d(TAG,"the sync is called by ContentObserver but time diff is not enough its:-> "+(nowTime-lastTime)/1000);
			   handle.removeCallbacks(this);
			   handle.postDelayed(this, 120000);
		   }
		   
		   Log.d("vincent", "Time diff was: "+(nowTime-lastTime)/1000);
		   
	       
	   }

	public void setHandle(Handler timeHandle) {
		
		this.handle = timeHandle;
		
	}

	public void setActivity(CloudSyncActivity activity) {
		this.activity = activity;
		
	}
	}
    
    
  
