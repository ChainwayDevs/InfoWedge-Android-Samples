# Intent 输出

> 本示例演示了应用程序如何通过 Android Intent 接收扫码结果

[![language](https://img.shields.io/badge/en-English-green.svg)](README.md)

## 演示环境

- InfoWedge - v1.71
- 设备 - MC62

## 概述

本示例演示了应用程序如何通过 Android Intent 来接收扫码结果。**支持 InfoWedge 的四种 Intent 发送方式：**

| 发送类型 | 接收方式 | 使用场景 |
|--------------|---------|---------|
| **0 - Broadcast** | 广播接收器 | 后台接收，通用性最好 |
| **1 - Start Activity** | Activity 接收 | 需要将应用带到前台并显示数据 |
| **2 - Start Service** | Service 接收 | 后台处理（Android 7.1 及以下） |
| **3 - Start Foreground Service** | 前台 Service 接收 | 后台处理（Android 8.0+，需要通知） |

**操作步骤：**

1. 配置 InfoWedge 的 `Profile0` ，打开 Intent 输出功能，选择合适的发送类型。

    ![1.png](./pics/1.png) ![2.png](./pics/2.png)

2. 运行示例应用程序，扫描条码。应用程序将显示以下信息：
   - 数据接收方式（Broadcast / Activity / Service）
   - 扫码结果
   - 条码数据
   - 原始数据
   - 条码类型
   - 扫码时长

    ![3.png](./pics/3.png) ![overview.png](./pics/overview.png)

## 示例代码说明

### 方式一：通过广播接收（Delivery Type 0）

1. **注册广播接收器。** 当 InfoWedge 配置为发送广播 intent 时，应用程序必须注册广播接收器。这是在示例应用程序的 `onCreate()` 方法中完成的：
    ```java
    // Register for the intent sent by InfoWedge
    IntentFilter filter = new IntentFilter();
    filter.addCategory(Intent.CATEGORY_DEFAULT);
    filter.addAction("com.infowedge.data");
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        registerReceiver(myBroadcastReceiver, filter, Context.RECEIVER_EXPORTED);
    } else {
        registerReceiver(myBroadcastReceiver, filter);
    }
    ```

2. **定义广播接收器。** 这是在示例应用程序的 MainActivity.java 中完成的：
    ```java
    private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Bundle bundle = intent.getExtras();

            // 将 intent 的内容输出到日志
            for (String key : bundle.keySet()) {
                Log.v(TAG, key + " : " + bundle.get(key));
            }

            if (action.equals("com.infowedge.data")) {
                // Received a barcode scan
                try {
                    // Update the text view on the screen with the data received
                    displayScanResult(intent, "via Broadcast");
                } catch (Exception e) {
                    // Catch if the UI does not exist when we receive the broadcast
                }
            }
        }
    };
    ```

3. **InfoWedge Intent Output 配置**

    ![4.png](./pics/4.png)

### 方式二：通过 Activity 接收（Delivery Type 1）

1. **处理 Activity 接收的 Intent。** 在 `onCreate()` 方法中处理首次创建时的 intent：
    ```java
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Handle the initial intent when activity is first created
        displayScanResult(getIntent(), "via Activity (onCreate)");
        
        // ... 注册广播接收器的代码 ...
    }
    ```

2. **在 AndroidManifest.xml 中配置 Activity。** 必须添加 intent-filter 以接收来自 InfoWedge 的 intent：
    ```xml
    <activity
        android:name=".MainActivity"
        android:launchMode="singleTop"
        android:exported="true">
        <intent-filter>
            <action android:name="android.intent.action.MAIN" />
            <category android:name="android.intent.category.LAUNCHER" />
        </intent-filter>
        <!-- Intent filter for InfoWedge Start Activity delivery -->
        <intent-filter>
            <action android:name="com.infowedge.data" />
            <!-- DEFAULT category is REQUIRED for implicit intents to work -->
            <category android:name="android.intent.category.DEFAULT" />
        </intent-filter>
    </activity>
    ```
    **关键点：**
    - `android:launchMode="singleTop"` - 确保 Activity 已存在时调用 `onNewIntent()` 而不是创建新实例
    - `<category android:name="android.intent.category.DEFAULT" />` - 隐式 Intent 必须包含此 category

3. **实现 onNewIntent() 方法。** 当 Activity 处于 singleTop 模式时，新的 intent 会触发此方法：
    ```java
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // Handle new intent when activity is reused (due to singleTop launch mode)
        setIntent(intent);  // Update the current intent
        displayScanResult(intent, "via Activity (onNewIntent)");
    }
    ```

4. **InfoWedge Intent Output 配置**

    ![5.png](./pics/5.png)

### 方式三：通过 Service 接收（Delivery Type 2 或 3）

1. **创建 Service 类。** 创建 `ScanDataService.java` 来接收来自 InfoWedge 的扫码数据：
    ```java
    public class ScanDataService extends Service {
        private static final String TAG = "ScanDataService";
        public static final String ACTION_DISPLAY_SCAN_DATA = 
            "com.chainway.intentoutput.DISPLAY_SCAN_DATA";

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            // IMPORTANT: Must call startForeground() within 5 seconds on Android 8.0+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForeground(NOTIFICATION_ID, createNotification("Service is running"));
            }

            // Process the intent from InfoWedge
            if (intent != null && "com.infowedge.data".equals(intent.getAction())) {
                processScanData(intent);
            }

            stopSelf(startId);
            return START_NOT_STICKY;
        }

        private void processScanData(Intent intent) {
            // Extract and log scan data
            int result = intent.getIntExtra("result", -1);
            if (result == 1) {
                String dataString = intent.getStringExtra("data_string");
                // ... process data ...
                
                // Forward to MainActivity for display
                forwardToMainActivity(intent);
            }
        }
    }
    ```

2. **在 AndroidManifest.xml 中注册 Service：**
    ```xml
    <!-- Permission required for foreground service on Android 9.0+ -->
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE_DATA_SYNC" />

    <service
        android:name=".ScanDataService"
        android:exported="true"
        android:foregroundServiceType="dataSync">
        <intent-filter>
            <action android:name="com.infowedge.data" />
        </intent-filter>
    </service>
    ```

3. **注册接收器以接收 Service 转发的数据。** 在 MainActivity 的 `onCreate()` 方法中：
    ```java
    // Register for broadcasts from ScanDataService
    IntentFilter serviceFilter = new IntentFilter();
    serviceFilter.addAction(ScanDataService.ACTION_DISPLAY_SCAN_DATA);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        registerReceiver(serviceDataReceiver, serviceFilter, Context.RECEIVER_NOT_EXPORTED);
    } else {
        registerReceiver(serviceDataReceiver, serviceFilter);
    }
    ```

4. **定义接收器以处理 Service 转发的数据：**
    ```java
    // Receiver for data forwarded from ScanDataService
    private BroadcastReceiver serviceDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ScanDataService.ACTION_DISPLAY_SCAN_DATA.equals(intent.getAction())) {
                Log.d(TAG, "Received scan data from ScanDataService");
                try {
                    displayScanResult(intent, "via Service (forwarded to Activity)");
                } catch (Exception e) {
                    Log.e(TAG, "Error displaying scan result from service", e);
                }
            }
        }
    };
    ```

5. **InfoWedge Intent Output 配置**

    需要设置 `Intent package name` 为 `com.chainway.intentoutput`，否则无法接收数据。

    ![6.png](./pics/6.png)

### 通用方法：显示扫描数据

**提取扫描的数据并将其显示在屏幕上。** `displayScanResult()` 方法可以处理来自所有接收方式的数据：
```java
private void displayScanResult(Intent intent, String howDataReceived) {
    // Check if this is a valid scan data intent
    String action = intent.getAction();
    if (action == null) return;
    
    // Accept data from InfoWedge directly or forwarded from ScanDataService
    if (!action.equals("com.infowedge.data") && 
        !action.equals(ScanDataService.ACTION_DISPLAY_SCAN_DATA)) {
        return;
    }

    final TextView lblScanData = (TextView) findViewById(R.id.lblScanData);
    StringBuilder sb = new StringBuilder();

    // How the data was received
    sb.append("[how data received]\n").append(howDataReceived).append("\n\n");

    // Extract the result of the scan
    int result = intent.getIntExtra("result", -1);
    switch (result) {
        case 1:
            sb.append("[result]\n").append("success").append("\n\n");
            break;
        case -1:
            sb.append("[result]\n").append("cancel").append("\n\n");
            break;
        default:
            sb.append("[result]\n").append("failure").append("\n\n");
            break;
    }

    if (result != 1) {
        // 使用接收到的数据更新屏幕上的文本信息
        lblScanData.setText(sb.toString());
        return;
    }

    // 提取扫码数据
    sb.append("[scan data]\n").append(intent.getStringExtra("data_string")).append("\n\n");

    // 从 intent 中提取原始数据
    byte[] decode_data = intent.getByteArrayExtra("decode_data");
    if (decode_data != null) {
        sb.append("[raw data]\n").append(new String(decode_data)).append("\n\n");
    }

    // 提取条码类型
    int symbol = intent.getIntExtra("symbology", -1);
    if (symbol == -1) {
        sb.append("[symbology]\nunknown\n\n");
    } else {
        sb.append("[symbology]\n").append(BarcodeSymbol.getSymbolName(symbol))
                .append(" (").append(symbol).append(")").append("\n\n");
    }

    // 提取解码所花费的时间
    sb.append("[decode time]\n").append(intent.getIntExtra("decode_time", -1)).append(" ms\n\n");

    // Update the text view on the screen with the data received
    lblScanData.setText(sb.toString());
}
```

## InfoWedge 配置建议

### Delivery Type 选择

| Delivery Type | 推荐场景 | 注意事项 |
|--------------|---------|---------|
| **0 - Broadcast** | ✅ 推荐日常使用 | 最稳定，兼容性最好，应用无需在前台 |
| **1 - Start Activity** | 需要将应用带到前台 | 会打断用户当前操作 |
| **2 - Start Service** | 后台处理（旧版 Android） | Android 8.0+ 受限，代码会自动回退到前台服务 |
| **3 - Start Foreground Service** | 后台处理（新版 Android） | 需要显示通知，必须在 5 秒内调用 startForeground() |

### 配置参数

```
Intent Output:
  - Enable: true
  - Action: com.infowedge.data
  - Category: android.intent.category.DEFAULT (可选)
  - Package Name: com.chainway.intentoutput
  - Delivery Type: 0/1/2/3 (根据需求选择)
```

## 数据字段说明

Intent 中包含的扫码数据字段：

| 字段名 | 类型 | 说明 |
|--------|------|------|
| `result` | int | 扫码结果：1=成功, -1=取消, 其他=失败 |
| `data_string` | String | 扫码数据（字符串形式） |
| `decode_data` | byte[] | 原始字节数据 |
| `symbology` | int | 条码类型代码 |
| `decode_time` | int | 解码耗时（毫秒） |
