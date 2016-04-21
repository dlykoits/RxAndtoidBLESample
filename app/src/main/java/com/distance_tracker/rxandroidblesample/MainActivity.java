package com.distance_tracker.rxandroidblesample;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.polidea.rxandroidble.RxBleClient;
import com.polidea.rxandroidble.RxBleConnection;
import com.polidea.rxandroidble.RxBleDevice;

import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();
    private static final String MAC = "00:22:D0:8E:23:94";
    private Button connectBtn;
    private CoordinatorLayout coordinator;

    private RxBleClient rxBleClient;
    private RxBleDevice rxBleDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rxBleClient = RxBleClient.create(this);

        coordinator = (CoordinatorLayout) findViewById(R.id.coordinator);
        connectBtn = (Button) findViewById(R.id.connect);
        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showString("start connection establishing");
                rxBleDevice = rxBleClient.getBleDevice(MAC);
                rxBleDevice.observeConnectionStateChanges().subscribe(connectionStateSubscription);
                rxBleDevice.establishConnection(MainActivity.this, true)
                        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<RxBleConnection>() {
                            @Override
                            public void onCompleted() {
                                showString("connection onCompleted");
                            }

                            @Override
                            public void onError(Throwable e) {
                                showString("connection onError: " + e.getMessage());
                            }

                            @Override
                            public void onNext(RxBleConnection rxBleConnection) {
                                showString("connection established");
                            }
                        });
            }
        });
    }

    private Observer<RxBleConnection.RxBleConnectionState> connectionStateSubscription = new Observer<RxBleConnection.RxBleConnectionState>() {
        @Override
        public void onCompleted() {
            showString("conection state onCompleted");
        }

        @Override
        public void onError(Throwable e) {
            showString("conection state onError");
        }

        @Override
        public void onNext(RxBleConnection.RxBleConnectionState rxBleConnectionState) {
            showString("conection state onNext: " + rxBleConnectionState.toString());
        }
    };

    private void showString(String s) {
        Snackbar.make(coordinator, s, Snackbar.LENGTH_INDEFINITE).show();
        Log.d(TAG, s);
    }

}