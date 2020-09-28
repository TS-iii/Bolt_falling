package com.example.fallanddead;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;


import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

import java.util.ArrayList;

// 안드로이드 오레오 버전 이후 버전에 대한 노티 부분 적용

/*
링크

https://superfelix.tistory.com/43
//-> Application 상속 클래스 설명

https://github.com/AltBeacon/android-beacon-library-reference/blob/master/app/src/main/java/org/altbeacon/beaconreference/BeaconReferenceApplication.java
//-> altBeacon의 포그라운드 서비스 관련 부분 라이브러리

https://github.com/AltBeacon/android-beacon-library/blob/master/lib/src/main/java/org/altbeacon/beacon/BeaconManager.java
// altBeacon의 전체 라이브러리


*/

// 안드로이드 앱이 시작되면 무조건 Application을 상속 받은 클래스를 먼저 실행
// 앱의 시작점이므로 신속히 처리하고 넘어가는게 좋음- 지연되면 앱 시작이 느려짐

public class App extends Application implements BootstrapNotifier {

    private static final String TAG = "BeaconReferenceApp";
    private RegionBootstrap regionBootstrap;
    // https://altbeacon.github.io/android-beacon-library/javadoc/org/altbeacon/beacon/startup/RegionBootstrap.html
    // -> ReagionBootstrap 클래스에 대한 내용 설명

    private BackgroundPowerSaver backgroundPowerSaver;


    // 어플리케이션이 생성될때 호출, 액티비티나 서비스보다 항상 먼저 호출됨
    @Override
    public void onCreate(){

        super.onCreate();
       // PreferenceManager.setInt(this,"app",0);
        // BeaconManager은 싱글턴이라 다른 액티비티에서 써도 똑같은 애가 리턴됨
        // getInstanceForApplication(this)를 다른곳에서 써도 기존에 잇던 애가 반환됨.

        BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);

        // iBeacon을 사용할려면 필수로 작성해야함.
        // 원래 AltBeacon의 Default 라이브러리는 AltBeacon만 찾게끔 설계되어 있음
        // 나는 다른 타입 비콘(iBeacon)을 찾길 원하니 byte layout을 iBeacon에 맞게끔 설정해야함
        beaconManager.getBeaconParsers().clear();
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconManager.setDebug(true);


        // 포그라운드 서비스로 실행해 지속적으로 비콘 모니터링을함
//        Notification.Builder builder = new Notification.Builder(this);
//        builder.setSmallIcon(R.drawable.ic_launcher_background);
//        builder.setContentTitle("Scanning for Beacons");
//        Intent intent = new Intent(this, MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(
//                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
//        );
//
//
//        builder.setContentIntent(pendingIntent);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel("My Notification Channel ID",
//                    "My Notification Name", NotificationManager.IMPORTANCE_DEFAULT);
//            channel.setDescription("My Notification Channel Description");
//            NotificationManager notificationManager = (NotificationManager) getSystemService(
//                    Context.NOTIFICATION_SERVICE);
//            notificationManager.createNotificationChannel(channel);
//            builder.setChannelId(channel.getId());
//        }

        // 포그라운드 서비스로 실행
    //    beaconManager.enableForegroundServiceScanning(builder.build(), 456);

        // 원래 JobScheduler 방식으로 모니터링을 했다면 해제 (이제 포그라운드 서비스로 이용하므로)
        beaconManager.setEnableScheduledScanJobs(false);

        //스캔간 시간간격은 없음.
        beaconManager.setBackgroundBetweenScanPeriod(0);

        // 스캔 주기를 1.1초로 설정
        beaconManager.setBackgroundScanPeriod(1100);

        Log.d(TAG, "setting up background monitoring for beacons and power saving");


        // id 1,2,3을 모두 null로 둠으로 현재 보이는 모든 iBeacon을 찾음.
//        Region region = new Region("backgroundRegion",
//                null, null, null);
//        regionBootstrap = new RegionBootstrap(this, region);

        // 안드로이드 파워(배터리) 절약에 사용
        backgroundPowerSaver = new BackgroundPowerSaver(this);


    }

    // FADService에서 모니터링 종료할때 사용.
    // 이걸 disable 하지 않고 beaconmanager 포그라운드 서비스를 먼저 종료하면 앱 비정상 종료됨.
    public void disableMonitoring() {
        if (regionBootstrap != null) {
            regionBootstrap.disable();
            regionBootstrap = null;
        }
    }

    // 다시 모니터링을 시작할때 사용.
    public void enableMonitoring() {
        Region region = new Region("backgroundRegion",
                null, null, null);
        regionBootstrap = new RegionBootstrap(this, region);
    }


    // BootstrapNotifier 인터페이스를 사용할때 필수로 Override 해야하는 메소드들
    // 여기선 정의만 해두고 사용하지는 않음
    @Override
    public void didEnterRegion(Region region) {

    }

    @Override
    public void didExitRegion(Region region) {

    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {

    }
}
