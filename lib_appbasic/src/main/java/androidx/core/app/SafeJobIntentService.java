package androidx.core.app;

import android.os.Build;

import com.yh.appbasic.logger.LibLogs;

/**
 * issue: https://github.com/evernote/android-job/issues/255
 * <p>
 * copy from: https://github.com/evernote/android-job
 */
public abstract class SafeJobIntentService extends JobIntentService{
    
    @Override
    GenericWorkItem dequeueWork(){
        try{
            return super.dequeueWork();
        }catch(SecurityException e){
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public void onCreate(){
        super.onCreate();
        LibLogs.logD("CREATING: " + this);
        // override mJobImpl with safe class to ignore SecurityException
        if(Build.VERSION.SDK_INT >= 26){
            mJobImpl = new SafeJobIntentServiceImpl(this);
        }else{
            mJobImpl = null;
        }
    }
    
}