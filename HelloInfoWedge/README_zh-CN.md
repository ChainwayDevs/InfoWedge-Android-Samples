# InfoWedge API 使用入门

> 此示例应用程序演示了使用 InfoWedge API 的入门编程。

[![language](https://img.shields.io/badge/en-English-green.svg)](README.md)

## 演示环境

- InfoWedge - v1.48
- 设备 - MC62

## 概述
此示例应用程序演示了如何开始使用 InfoWedge API 进行编程。运行示例应用程序并点击 `GET INFO` 按钮以获取有关 InfoWedge 的一些信息。

![1.png](./pics/1.png) ![overview.png](./pics/overview.png)

## 示例代码说明

1. **注册广播接收器并过滤结果。** 这是在示例应用程序的 `onCreate()` 方法中完成的：
    ```java
    // 注册广播接收器并过滤结果
    IntentFilter filter = new IntentFilter();
    filter.addAction("com.symbol.infowedge.api.RESULT_ACTION");
    filter.addCategory("android.intent.category.DEFAULT");
    registerReceiver(resultBroadcastReceiver, filter);
    ```
2. **处理按钮点击事件。** 这是在示例应用程序的 `onCreate()` 方法中完成的：
    ```java
    Button btnGetInfo = findViewById(R.id.button_get_info);
    btnGetInfo.setOnClickListener(v -> {
        // 发送多个 intents 作为 extras 以获取不同的信息
        Intent i = new Intent();
        i.setAction("com.symbol.infowedge.api.ACTION");

        // 获取设备上当前安装的 InfoWedge 的版本号
        i.putExtra("com.symbol.infowedge.api.GET_VERSION_INFO", "");

        // 获取 InfoWedge 的状态（"enabled" 或 "disabled"）作为字符串 extra
        i.putExtra("com.symbol.infowedge.api.GET_INFOWEDGE_STATUS", "");

        // 获取 InfoWedge 当前使用的配置文件的名称
        i.putExtra("com.symbol.infowedge.api.GET_ACTIVE_PROFILE", "");

        sendBroadcast(i);
    });
    ```
3. **定义广播接收器。** 在 `resultBroadcastReceiver` 中获取有关 InfoWedge 的信息并显示在屏幕上。这是在示例应用程序的 MainActivity.java 中完成的：
    ```java
    private BroadcastReceiver resultBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            StringBuilder sb = new StringBuilder();

            // InfoWedge 的版本号
            if (intent.hasExtra("com.symbol.infowedge.api.RESULT_GET_VERSION_INFO")) {
                Bundle res = intent.getBundleExtra("com.symbol.infowedge.api.RESULT_GET_VERSION_INFO");
                String infoWedgeVersion = res.getString("INFOWEDGE");
                sb.append("version:\t").append(infoWedgeVersion).append("\n");
                Log.d(TAG, "version: " + infoWedgeVersion);
            }

            // 当前使用的配置文件名称
            if (intent.hasExtra("com.symbol.infowedge.api.RESULT_GET_ACTIVE_PROFILE")) {
                String activeProfile = intent.getStringExtra("com.symbol.infowedge.api.RESULT_GET_ACTIVE_PROFILE");
                sb.append("profile:\t").append(activeProfile).append("\n");
                Log.d(TAG, "profile: " + activeProfile);
            }

            // InfoWedge 的状态
            if (intent.hasExtra("com.symbol.infowedge.api.RESULT_GET_INFOWEDGE_STATUS")) {
                // status: "ENABLED" or "DISABLED"
                String infoWedgeStatus = intent.getStringExtra("com.symbol.infowedge.api.RESULT_GET_INFOWEDGE_STATUS");
                sb.append("status:\t").append(infoWedgeStatus).append("\n");
                Log.d(TAG, "status: " + infoWedgeStatus);
            }

            ((TextView)findViewById(R.id.label_info_data)).setText(sb.toString());
        }
    };
    ```