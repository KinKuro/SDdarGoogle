package studies.kinkuro.sddargoogle.Timer;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class TimerService extends Service {

    int minute, second;
    boolean isStart = false;

    TimerThread thread;

    Intent broadIntent;

    public TimerService() {    }

    @Override
    public IBinder onBind(Intent intent) {
        return new ServiceBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        if(thread != null){
            stopTimer();
        }
    }

    ////InnerClass////////////////////////

    public class ServiceBinder extends Binder{

        public TimerService getService(){
            return TimerService.this;
        }
    }

    public class TimerThread extends Thread{
        boolean isOneSec = false;
        @Override
        public void run() {
            while(isStart){
                if(isOneSec){
                    second--;
                    if(second < 0){
                        second = 59;
                        minute--;
                        if(minute == 9 && second > 58){
                            //TODO:: 소리내기
                        }
                    }
                }
                isOneSec = !isOneSec;

                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                broadIntent.putExtra("minute", minute);
                broadIntent.putExtra("second", second);
                broadIntent.putExtra("isOneSec", isOneSec);
                sendBroadcast(broadIntent);

            }//반복구간
        }//run()...
    }//TimerThread...

    ////InnerClass.../////////////////////

    public void setTimer(int minute, int second){
        this.minute = minute;
        this.second = second;
    }

    public void startTimer(){
        isStart = true;
        if(thread == null) {
            broadIntent = new Intent("SDDAR_TIMER");

            thread = new TimerThread();
            thread.run();
        }
    }

    public void stopTimer(){
        isStart = false;
        if(thread != null)            thread = null;
    }

}
