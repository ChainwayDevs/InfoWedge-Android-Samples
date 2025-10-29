package com.chainway.intentoutput;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

/**
 * Service to receive scan data from InfoWedge via Intent Output
 * Supports both regular service (SDK < 26) and foreground service (SDK >= 26)
 */
public class ScanDataService extends Service {
    private static final String TAG = "ScanDataService";
    private static final String CHANNEL_ID = "scan_service_channel";
    private static final int NOTIFICATION_ID = 1001;
    
    // Action for broadcasting scan data to MainActivity
    public static final String ACTION_DISPLAY_SCAN_DATA = "com.chainway.intentoutput.DISPLAY_SCAN_DATA";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service created");
        
        // Create notification channel for Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service started");
        
        // IMPORTANT: Must call startForeground() within 5 seconds on Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(NOTIFICATION_ID, createNotification("Service is running"));
        }
        
        // Process the intent from InfoWedge
        if (intent != null && "com.infowedge.data".equals(intent.getAction())) {
            processScanData(intent);
        } else {
            Log.w(TAG, "Received intent with unexpected action: " + 
                  (intent != null ? intent.getAction() : "null"));
        }
        
        // Stop the service after processing data
        stopSelf(startId);
        
        return START_NOT_STICKY;
    }

    /**
     * Process scan data received from InfoWedge
     */
    private void processScanData(Intent intent) {
        Log.d(TAG, "Processing scan data from InfoWedge");
        
        // Extract the result of the scan
        int result = intent.getIntExtra("result", -1);
        if (result == 1) {
            // Success - extract scan data
            String dataString = intent.getStringExtra("data_string");
            byte[] decodeData = intent.getByteArrayExtra("decode_data");
            int symbology = intent.getIntExtra("symbology", -1);
            int decodeTime = intent.getIntExtra("decode_time", -1);
            
            Log.d(TAG, "Scan result: SUCCESS");
            Log.d(TAG, "Data: " + dataString);
            Log.d(TAG, "Symbology: " + BarcodeSymbol.getSymbolName(symbology) + " (" + symbology + ")");
            Log.d(TAG, "Decode time: " + decodeTime + " ms");
            
            if (decodeData != null) {
                Log.d(TAG, "Raw data length: " + decodeData.length + " bytes");
            }
            
            // Update notification with scan data
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationManager manager = getSystemService(NotificationManager.class);
                if (manager != null) {
                    manager.notify(NOTIFICATION_ID, 
                        createNotification("Scanned: " + dataString));
                }
            }
            
            // Forward scan data to MainActivity for display
            forwardToMainActivity(intent);
            
            // TODO: Add your custom processing logic here
            // For example: save to database, send to server, etc.
            
        } else if (result == -1) {
            Log.d(TAG, "Scan result: CANCELLED");
        } else {
            Log.d(TAG, "Scan result: FAILURE");
        }
    }

    /**
     * Forward scan data to MainActivity for UI display
     */
    private void forwardToMainActivity(Intent originalIntent) {
        Log.d(TAG, "Forwarding scan data to MainActivity");
        
        // Create a new intent to broadcast to MainActivity
        Intent displayIntent = new Intent(ACTION_DISPLAY_SCAN_DATA);
        
        // Copy all extras from the original intent
        if (originalIntent.getExtras() != null) {
            displayIntent.putExtras(originalIntent.getExtras());
        }
        
        // Set the action so MainActivity can recognize it
        displayIntent.putExtra("original_action", originalIntent.getAction());
        displayIntent.putExtra("received_via", "Service");
        
        // Send broadcast to MainActivity
        sendBroadcast(displayIntent);
        
        Log.d(TAG, "Broadcast sent to MainActivity");
    }

    /**
     * Create notification channel for Android 8.0+
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Scan Service",
                NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Service for receiving scan data from InfoWedge");
            
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
                Log.d(TAG, "Notification channel created");
            }
        }
    }

    /**
     * Create notification for foreground service
     */
    private Notification createNotification(String contentText) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Scan Service")
            .setContentText(contentText)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true);
        
        return builder.build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service destroyed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // This service doesn't support binding
        return null;
    }
}

