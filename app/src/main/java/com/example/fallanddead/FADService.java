package com.example.fallanddead;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;


public class FADService extends Service implements  BeaconConsumer {

    private static final String TAG="FADService";
    private BeaconManager beaconManager;

    private List<Beacon> beaconList=new ArrayList<>();

    private Thread thread;
    private int tf=1;
    PersonDatabase database;
    private ArrayList<String> maclist=new ArrayList<>();
    private Stack<String> alarmlist=new Stack<>();

    public FADService() {
    }


    // 서비스가 처음 실행되면
    // 스레드 하나 실행하고
    // 그 스레드는 루프 돌면서 계속 블루투스 비콘 수신하고
    // 전역 변수 (싱글턴 디자인 패턴)에 저장된 목록에 있는 mac주소와 일치하는
    // 비콘을 발견하면 신호 값을 확인해서 알람 여부를 판단한다.
    // 이 코드를 onCreate() 부분에 작성

    @Override
    public void onCreate() {

        Log.d("TTTTTTT","ONCREATE!!!!!!!!!!!");
        super.onCreate();
        tf=1;
        beaconManager = BeaconManager.getInstanceForApplication(this);
     //   if(PreferenceManager.getInt(this,"app")==1){
            PreferenceManager.setInt(this,"app",0);

            App app=(App)getApplication();


            Notification.Builder builder = new Notification.Builder(this);
            builder.setSmallIcon(R.mipmap.ic_main);
            builder.setContentTitle("Scanning for Beacons");
            Intent intent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
            );


            builder.setContentIntent(pendingIntent);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("My Notification Channel ID",
                        "My Notification Name", NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription("My Notification Channel Description");
                NotificationManager notificationManager = (NotificationManager) getSystemService(
                        Context.NOTIFICATION_SERVICE);
                notificationManager.createNotificationChannel(channel);
                builder.setChannelId(channel.getId());
            }

            beaconManager.enableForegroundServiceScanning(builder.build(), 456);
            app.enableMonitoring();
 //       }




        beaconManager.bind(this);


        if(thread==null) {
            Log.d("TTTTTTT","DDDDDDDDDDDDD");
            thread = new FADThread();
            thread.start();
        }

        database=PersonDatabase.getInstance(this);


    }

    @Override
    public void onDestroy() {
        Log.d("TTTTTTT","ONDESTROY!!!!!!!!!!!");
        tf=0;
        beaconManager.unbind(this);
        PreferenceManager.setInt(this,"app",1);

        for(int i=0;i<maclist.size();i++){

            PreferenceManager.setString(getApplicationContext(),maclist.get(i),"");
        }

        App app=(App)getApplication();
        app.disableMonitoring();

        beaconManager.disableForegroundServiceScanning();

        super.onDestroy();

    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            // 비콘이 감지되면 해당 함수가 호출된다. Collection<Beacon> beacons에는 감지된 비콘의 리스트가,
            // region에는 비콘들에 대응하는 Region 객체가 들어온다.
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    beaconList.clear();
                    for (Beacon beacon : beacons) {
                        Log.d("TTTTTTTTTTTTT","BBBBBBBBBBBBb");
                        beaconList.add(beacon);
                    }
                }
            }

        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {   }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId){


        Log.d("TTTTTTTTTTTt","CVXCVXCVXCVXCVXXCVXVXCV");
    //    String input=intent.getStringExtra("inputExtra");
//        Intent notificationIntent=new Intent(this,MainActivity.class);

//        PendingIntent pendingIntent=PendingIntent.getActivity(this,
//                0,notificationIntent,0);
//
//        Notification notification=new NotificationCompat.Builder(this,"My Notification Channel ID")
//                .setContentTitle("Example Service")
//                .setContentText("hello")
//                .setSmallIcon(R.mipmap.ic_main)
//                .setContentIntent(pendingIntent)
//                .build();
//
//        startForeground(1,notification);


       Bundle bundle= intent.getExtras();
       String add=bundle.getString("add","");

       if(add.equals("t")){

           String mac=bundle.getString("mac");
           maclist.add(mac);


       } else if(add.equals("f")){

           String mac=bundle.getString("mac");
           maclist.remove(mac);

       }

        return START_STICKY;

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
            return null;
    }



    private class FADThread extends Thread {

        private static final String TAG="FADThread";


        public FADThread(){ // 초기화 작업

             }

             public void run(){
            // 스레드에게 수행시킬 동작 구현



                 while(tf==1){

                     if(PreferenceManager.getInt(getApplicationContext(),"num")==0){
                         Intent intent=new Intent(getApplicationContext(),FADService.class);
                         stopService(intent);

                         break;

                     }

                    int k=0;

                    for(int i=0;i<beaconList.size();i++){

                        Beacon beacon=beaconList.get(i);
                        String address=beacon.getBluetoothAddress();

                        if(!maclist.contains(address))
                            continue;

                        String uuid=beacon.getId1().toString();
                        int major=beacon.getId2().toInt();
                        int minor=beacon.getId3().toInt();


                        Log.d("TTTTTSSSSS","major:"+major+" minor:"+minor+" address:"+address);

                        if(major==1){
                            k=1;
                            alarmlist.push(address+"1");
                        }
                        else if(major==3){

                            k=1;
                            alarmlist.push(address+"3");

                        }

                 }

                    if(k==1)
                    {

                        alarm();

                    }

                     try { Thread.sleep(5000); } catch(Exception e){}

             }  // while 끝


    }

}

// 하나의 스레드만 이 메소드를 사용하도록 설정 (동시에 X)
public synchronized void alarm(){

    Uri notification= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    Ringtone ringtone=RingtoneManager.getRingtone(getApplicationContext(),notification);
    ringtone.play();

    int fw=0;
    String mac=alarmlist.pop();
    String name="";
    char tf=mac.charAt(mac.length()-1);

    if(tf=='3')
        fw=2;   //배회
    else if(tf=='1')
        fw=1; // 낙상



    mac=mac.substring(0,mac.length()-1);



//    maclist.remove(mac);
//    PreferenceManager.removeKey(getApplicationContext(),mac);

    ArrayList<PersonInfo> result=database.selectAll();

    for(int i=0;i<result.size();i++){

        if(result.get(i).mac.equals(mac)){
            name=result.get(i).name;
            break;
        }
    }


    NotificationCompat.Builder builder=new NotificationCompat.Builder(this,"default");

    builder.setSmallIcon(R.mipmap.ic_main);
    builder.setContentTitle("알람");


    if(fw==1)
    builder.setContentText(name + "님 낙상");
    else if(fw==2)
        builder.setContentText(name + "님 배회");

    builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

    NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        notificationManager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
    }

    PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE );
    PowerManager.WakeLock wakeLock = pm.newWakeLock( PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAGG:dd");
    wakeLock.acquire(3000);
    notificationManager.notify(1, builder.build());


}

}
