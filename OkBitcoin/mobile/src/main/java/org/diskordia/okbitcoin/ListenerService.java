package org.diskordia.okbitcoin;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.concurrent.TimeUnit;

import de.schildbach.wallet.service.IPluginService;

import static org.diskordia.okbitcoin.MyActivity.TAG;

public class ListenerService extends WearableListenerService {
    private static final long CONNECTION_TIME_OUT_MS = 1000;
    private GoogleApiClient mGoogleApiClient;
    private IPluginService pluginService;

    private static String address;

    public ListenerService() {
        Log.d(TAG, "ListenerService instantiated ");

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "ListenerService.onCreate()");

        Intent pluginServiceIntent = new Intent();
        pluginServiceIntent.setClassName("de.schildbach.wallet_test", "de.schildbach.wallet.service.PluginService");
        bindService(pluginServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        System.out.println("====== service bound");

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(final ComponentName name, final IBinder binder) {
            Log.d(TAG, "onServiceConnected()");

            pluginService = IPluginService.Stub.asInterface(binder);
            try {
                Log.d(TAG, "getAddress()");
                address = pluginService.getAddress();

                Log.d(TAG, "address: " + address);
            } catch (RemoteException e) {
                Log.e(TAG, "RemoteException: ", e);
            }
        }

        @Override
        public void onServiceDisconnected(final ComponentName name) {
            pluginService = null;
        }
    };

    @Override
    public void onDestroy() {
        if (pluginService != null) {
            unbindService(serviceConnection);
        }
        super.onDestroy();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "onMessageReceived(): sourceNodeId: " + messageEvent.getSourceNodeId());
        showToast(messageEvent.getPath(), messageEvent.getSourceNodeId());
    }

    private void showToast(String message, String nodeId) {
        Log.d(TAG, "showToast(): " + message);
        if ("getAddress".equals(message)) {
            Toast.makeText(this, "Replying with address: " + address, Toast.LENGTH_LONG).show();
            reply(address, nodeId);

        } else {
            Toast.makeText(this, "Ignored: " + message, Toast.LENGTH_LONG).show();
        }
    }

    private void reply(String message, String nodeId) {
        Log.d(TAG, "reply(): message: " + message);
        Log.d(TAG, "reply():  nodeId: " + nodeId);
        mGoogleApiClient.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
        Wearable.MessageApi.sendMessage(mGoogleApiClient, nodeId, message, null);
        mGoogleApiClient.disconnect();
        Log.d(TAG, "reply done");
    }

    @Override
    public void onPeerConnected(Node peer) {
        super.onPeerConnected(peer);
        Log.d(TAG, "onPeerConnected(): peer: " + peer);
    }

    @Override
    public void onPeerDisconnected(Node peer) {
        super.onPeerDisconnected(peer);
        Log.d(TAG, "onPeerDisconnected(): peer: " + peer);
    }
}
