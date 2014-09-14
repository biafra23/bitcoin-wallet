package org.diskordia.okbitcoin;

import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.diskordia.okbitcoin.MyActivity.TAG;

public class ListenerService extends WearableListenerService {
    private static final long CONNECTION_TIME_OUT_MS = 1000;
    private String mNodeId;
    private GoogleApiClient mGoogleApiClient;

    public ListenerService() {
        Log.d(TAG, "ListenerService instantiated ");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        retrieveDeviceNode();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "onMessageReceived(): " + messageEvent);
        showToast(messageEvent.getPath());

    }

    private void showToast(String message) {
        Log.d(TAG, "showToast(): " + message);

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

        reply("Bar");
    }

    private void reply(String message) {

        mGoogleApiClient.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
        Wearable.MessageApi.sendMessage(mGoogleApiClient, mNodeId, message, null);
        mGoogleApiClient.disconnect();
        Log.d(TAG, "reply done");
    }

    private void retrieveDeviceNode() {
        Log.d(TAG, "retrieveDeviceNode()");
        new Thread(new Runnable() {
            @Override
            public void run() {
                mGoogleApiClient.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                NodeApi.GetConnectedNodesResult result =
                        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
                List<Node> nodes = result.getNodes();
                if (nodes.size() > 0) {
                    mNodeId = nodes.get(0).getId();
                    Log.d(TAG, "nodeId: " + mNodeId);
                }
                mGoogleApiClient.disconnect();
            }
        }).start();
    }
}
