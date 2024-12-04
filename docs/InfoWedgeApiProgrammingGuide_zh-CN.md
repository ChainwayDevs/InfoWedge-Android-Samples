# InfoWedgeAPI 编程指南

> v1.6, 2024-12-04

## 目录

- [概述](#概述)
    - [示例代码](#示例代码)
    - [发送 Intent](#发送-intent)
    - [接收结果](#接收结果)
    - [单个广播多条命令](#单个广播多条命令)
    - [接收命令执行结果](#接收命令执行结果)
    - [InfoWedge API 列表](#infowedge-api-列表)
- [查询接口](#查询接口)
    - [获取版本信息（Get Version Info）](#获取版本信息get-version-info)
    - [获取活跃配置（Get Active Profile）](#获取活跃配置get-active-profile)
    - [获取 InfoWedge 状态（Get InfoWedge Status）](#获取-infowedge-状态get-infowedge-status)
- [配置接口](#配置接口)
    - [创建配置（Create Profile）](#创建配置create-profile)
    - [删除配置（Delete Profile）](#删除配置delete-profile)
    - [恢复出厂配置（Restore Config）](#恢复出厂配置restore-config)
    - [设置配置（Set Config）](#设置配置set-config)
        - [设置主参数](#设置主参数)
        - [设置悬浮扫码参数](#设置悬浮扫码参数)
        - [设置扫码头参数](#设置扫码头参数)
        - [设置 RFID 参数](#设置-rfid-参数)
        - [设置 GS1 条码格式化参数](#设置-gs1-条码格式化参数)
        - [设置基本数据格式化（BDF）参数](#设置基本数据格式化bdf参数)
        - [设置按键输出参数](#设置按键输出参数)
        - [设置广播输出参数](#设置广播输出参数)
        - [设置 IP 输出参数](#设置IP输出参数)
        - [设置剪贴板输出参数](#设置剪贴板输出参数)
        - [同时设置多个模块](#同时设置多个模块)
- [操作接口](#操作接口)
    - [启用 / 禁用 InfoWedge（Enable/Disable InfoWedge）](#启用--禁用-infowedgeenabledisable-infowedge)
    - [触发扫码头扫码（Soft Scan Trigger）](#触发扫码头扫码soft-scan-trigger)
    - [触发 RFID 扫描（Soft RFID Trigger）](#触发rfid扫描soft-rfid-trigger)
- [扫码头设置 API 命名](#扫码头设置-api-命名)

---

## 概述

InfoWedge API 主要通过 Android 的 Intent 运行 - 其他应用程序可以使用的特定命令来控制 InfoWedge，而无需直接访问 InfoWedge 的 UI 页面。

### 示例代码

示例代码可在以下仓库获得：

- [GitHub](https://github.com/ChainwayDevs/InfoWedge-Android-Samples.git)
- [Gitee](https://gitee.com/chainwaydevs/InfoWedge-Android-Samples.git)

### 发送 Intent

InfoWedge API 的调用是通过发送广播方式实现的。以下示例是获得 InfoWedge 的版本号：

```java
// 发送广播
Intent i = new Intent();
i.setAction("com.symbol.infowedge.api.ACTION");
i.putExtra("com.symbol.infowedge.api.GET_VERSION_INFO", "");
sendBroadcast(i);
```

### 接收结果

如果需要获得 InfoWedge API 的调用结果，需要注册一个广播接收器。以下示例是从回复的结果中读取版本信息：

```java
// 注册广播接收器
void registerReceivers() {
    IntentFilter filter = new IntentFilter();
    filter.addAction("com.symbol.infowedge.api.RESULT_ACTION");
    filter.addCategory("android.intent.category.DEFAULT");
    registerReceiver(resultBroadcastReceiver, filter);
}

// 接收命令结果的广播接收器
private BroadcastReceiver resultBroadcastReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        // 版本信息
        if (intent.hasExtra("com.symbol.infowedge.api.RESULT_GET_VERSION_INFO")) {
            Bundle res = intent.getBundleExtra("com.symbol.infowedge.api.RESULT_GET_VERSION_INFO");
            String infoWedgeVersion = res.getString("INFOWEDGE");
            Log.d(TAG, "version: " + infoWedgeVersion);
        }
    }
};
```

### 单个广播多条命令

在发送命令广播时，可以在 intent 中添加多个 extra ，这样一个广播可以执行多条命令。例如下面的命令获取了 InfoWedge 的版本号和当前活跃的配置：

```java
// 发送广播
Intent i = new Intent();
i.setAction("com.symbol.infowedge.api.ACTION");
i.putExtra("com.symbol.infowedge.api.GET_VERSION_INFO", "");
i.putExtra("com.symbol.infowedge.api.GET_ACTIVE_PROFILE", "");
sendBroadcast(i);

// 接收命令结果的广播接收器
private BroadcastReceiver resultBroadcastReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        // 版本信息
        if (intent.hasExtra("com.symbol.infowedge.api.RESULT_GET_VERSION_INFO")) {
            Bundle res = intent.getBundleExtra("com.symbol.infowedge.api.RESULT_GET_VERSION_INFO");
            String infoWedgeVersion = res.getString("INFOWEDGE");
            Log.d(TAG, "version: " + infoWedgeVersion);
        }

        // 当前活跃配置
        if (intent.hasExtra("com.symbol.infowedge.api.RESULT_GET_ACTIVE_PROFILE")) {
            String activeProfile = intent.getStringExtra("com.symbol.infowedge.api.RESULT_GET_ACTIVE_PROFILE");
            Log.d(TAG, "active profile: " + activeProfile);
        }
    }
};
```

### 接收命令执行结果

有些命令是操作类命令，默认不会返回操作的结果。如果需要获得操作的结果，需要在广播中添加 `SEND_RESULT` 和 `COMMAND_IDENTIFIER` 。`COMMAND_IDENTIFIER` 是用户自定义的命令 ID 字符串，在返回的操作结果中也会包含该参数。返回的结果中包含以下参数：

- **RESULT** [String] - 操作结果，`SUCCESS` 或 `FAILURE`
- **COMMAND** [String] - 操作命令
- **COMMAND_IDENTIFIER** [String] - 命令的 ID
- **RESULT_INFO** [Bundle] - 执行的结果参数，不同的命令有不同的结果参数

```java
// 发送广播
Intent i = new Intent();
i.setAction("com.symbol.infowedge.api.ACTION");
i.putExtra("com.symbol.infowedge.api.CREATE_PROFILE", "NewProfile");
i.putExtra("SEND_RESULT", "true");  // 要求发送结果广播
i.putExtra("COMMAND_IDENTIFIER", "1234");   // 命令 ID
sendBroadcast(i);

// 接收命令结果的广播接收器
private BroadcastReceiver resultBroadcastReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        StringBuilder sb = new StringBuilder();

        // 输出所有结果参数
        for (String key : extras.keySet()) {
            sb.append(key + ": " + extras.get(key) + "\n");
        }
        sb.append("---------------\n");

        // 命令名，命令 ID，命令结果
        String command = intent.getStringExtra("COMMAND");
        String commandId = intent.getStringExtra("COMMAND_IDENTIFIER");
        String result = intent.getStringExtra("RESULT");
        if (!TextUtils.isEmpty(command)) {
            sb.append("command: " + command + "\n");
        }
        if (!TextUtils.isEmpty(commandId)) {
            sb.append("command id: " + commandId + "\n");
        }
        if (!TextUtils.isEmpty(result)) {
            sb.append("result: " + result + "\n");
        }
        if (!TextUtils.isEmpty(commandId) || !TextUtils.isEmpty(command) || !TextUtils.isEmpty(result)) {
            sb.append("---------------\n");
        }

        // 执行结果信息
        if (intent.hasExtra("RESULT_INFO")) {
            sb.append("RESULT_INFO\n");
            Bundle bundle = intent.getBundleExtra("RESULT_INFO");
            for (String key : bundle.keySet()) {
                sb.append(key + ": " + bundle.get(key) + "\n");
            }
            sb.append("---------------\n");
        }
        
        Log.d(TAG, sb.toString());
    }
};
```

### InfoWedge API 列表

|类别|命令|命令名称|说明|
|:--:|:--|:--|:--|
|查询接口|获取活跃配置|GET_ACTIVE_PROFILE|获取当前的活跃配置|
||获取状态|GET_INFOWEDGE_STATUS|获取 InfoWedge 的开启状态|
||获取版本信息|GET_VERSION_INFO|获取相关模块的的版本信息|
|配置接口|创建配置|CREATE_PROFILE|创建一个默认的配置|
||删除配置|DELETE_PROFILE|删除一个或多个 Profile|
||恢复出厂配置|RESTORE_CONFIG|将配置恢复成出厂配置|
||设置配置|SET_CONFIG|设置某个 Profile 的内容|
|操作接口|启用 / 禁用 InfoWedge|ENABLE_INFOWEDGE|启用或禁用 InfoWedge 服务|
||触发扫码头扫码|SOFT_SCAN_TRIGGER|开始/停止/切换扫码头扫码|
||触发 RFID 扫描|SOFT_RFID_TRIGGER|开始/停止/切换 RFID 扫描|

## 查询接口

### 获取版本信息（Get Version Info）

```java
// 发送广播
Intent i = new Intent();
i.setAction("com.symbol.infowedge.api.ACTION");
i.putExtra("com.symbol.infowedge.api.GET_VERSION_INFO", "");
sendBroadcast(i);

// 接收命令结果的广播接收器
private BroadcastReceiver resultBroadcastReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        // 版本信息
        if (intent.hasExtra("com.symbol.infowedge.api.RESULT_GET_VERSION_INFO")) {
            Bundle res = intent.getBundleExtra("com.symbol.infowedge.api.RESULT_GET_VERSION_INFO");
            String infoWedgeVersion = res.getString("INFOWEDGE");   // InfoWedge 应用版本
            Log.d(TAG, "version: " + infoWedgeVersion);
        }
    }
};
```

### 获取活跃配置（Get Active Profile）

```java
// 发送广播
Intent i = new Intent();
i.setAction("com.symbol.infowedge.api.ACTION");
i.putExtra("com.symbol.infowedge.api.GET_ACTIVE_PROFILE", "");
sendBroadcast(i);

// 接收命令结果的广播接收器
private BroadcastReceiver resultBroadcastReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        // 活跃配置
        if (intent.hasExtra("com.symbol.infowedge.api.RESULT_GET_ACTIVE_PROFILE")) {
            String activeProfile = intent.getStringExtra("com.symbol.infowedge.api.RESULT_GET_ACTIVE_PROFILE");
            Log.d(TAG, "active profile: " + activeProfile);
        }
    }
};
```

### 获取 InfoWedge 状态（Get InfoWedge Status）

```java
// 发送广播
Intent i = new Intent();
i.setAction("com.symbol.infowedge.api.ACTION");
i.putExtra("com.symbol.infowedge.api.GET_INFOWEDGE_STATUS", "");
sendBroadcast(i);

// 接收命令结果的广播接收器
private BroadcastReceiver resultBroadcastReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        // InfoWedge 状态
        if (intent.hasExtra("com.symbol.infowedge.api.RESULT_GET_INFOWEDGE_STATUS")) {
            // 状态值: "ENABLED" 或 "DISABLED"
            String infoWedgeStatus = intent.getStringExtra("com.symbol.infowedge.api.RESULT_GET_INFOWEDGE_STATUS");
            Log.d(TAG, "Info Wedge status: " + infoWedgeStatus);
        }
    }
};
```

## 配置接口

### 创建配置（Create Profile）

```java
// 发送广播
Intent i = new Intent();
i.setAction("com.symbol.infowedge.api.ACTION");
i.putExtra("com.symbol.infowedge.api.CREATE_PROFILE", "NewProfile");
sendBroadcast(i);
```

**错误码：**

- **PROFILE_NAME_EMPTY** - 配置文件名为空
- **PROFILE_ALREADY_EXISTS** - 配置文件已经存在

### 删除配置（Delete Profile）

```java
// 发送广播
Intent i = new Intent();
i.setAction("com.symbol.infowedge.api.ACTION");
String[] values = {"NewProfile"};
i.putExtra("com.symbol.infowedge.api.DELETE_PROFILE", values);
sendBroadcast(i);
```

**错误码：**

- **PROFILE_NAME_EMPTY** - 配置文件名为空
- **OPERATION_NOT_ALLOWED** - 操作不被允许，例如删除默认的配置文件
- **PROFILE_NOT_FOUND** - 删除的配置文件不存在

### 恢复出厂配置（Restore Config）

```java
// 发送广播
Intent i = new Intent();
i.setAction("com.symbol.infowedge.api.ACTION");
i.putExtra("com.symbol.infowedge.api.RESTORE_CONFIG", "");
sendBroadcast(i);
```

### 设置配置（Set Config）

#### 设置主参数

```java
// 主参数设置
Bundle bMain = new Bundle();
bMain.putString("PROFILE_NAME", "NewProfile");  // 配置文件名
bMain.putString("PROFILE_ENABLED", "true");     // 启用该配置文件
bMain.putString("CONFIG_MODE", "CREATE_IF_NOT_EXIST");  // 如果配置文件不存在则创建

// 设置关联应用程序
Bundle bundleApp1 = new Bundle();
bundleApp1.putString("PACKAGE_NAME", "change.to.your.app.package"); // 关联应用程序的包名
bundleApp1.putStringArray("ACTIVITY_LIST", new String[]{"change.to.your.app.package.MainActivity", "change.to.your.app.package.About"});    // 关联的 Activity 列表

Bundle bundleApp2 = new Bundle();
bundleApp2.putString("PACKAGE_NAME", "another.app.package");    // 关联的另一个应用程序包名
bundleApp2.putStringArray("ACTIVITY_LIST", new String[]{"*"});  // * 号表示关联该应用的所有 Activity

// 将关联应用程序添加到主参数中
bMain.putParcelableArray("APP_LIST", new Bundle[] {
    bundleApp1,
    bundleApp2
});

Intent i = new Intent();
i.setAction("com.symbol.infowedge.api.ACTION");
i.putExtra("com.symbol.infowedge.api.SET_CONFIG", bMain);
sendBroadcast(i);
```

**CONFIG_MODE** 参数的取值：

- **CREATE_IF_NOT_EXIST** - 如果配置文件不存在则创建
- **OVERWRITE** - 如果配置文件已存在，则先恢复成默认值后再设置
- **UPDATE** - 将配置合并到已有的配置文件

#### 设置悬浮扫码参数

```java
// 主参数设置
Bundle bMain = new Bundle();
bMain.putString("PROFILE_NAME", "NewProfile");  // 配置文件名
bMain.putString("PROFILE_ENABLED", "true"); // 启用该配置文件
bMain.putString("CONFIG_MODE", "UPDATE");   // 将配置合并到已有的配置文件
bMain.putString("MEMORY_PROFILE", "false");  // 是否是临时配置。临时配置存在内存中，系统重启后会丢失

// 设置 DCP
Bundle bConfig = new Bundle();
bConfig.putString("PLUGIN_NAME", "DCP");    // 设置类型：DCP
bConfig.putString("RESET_CONFIG", "true"); // 重置原有 DCP 配置

// 设置 DCP 参数（若使用默认值的配置项，可不设置）
Bundle bParams = new Bundle();
bParams.putString("dcp_input_enabled", "true");   // 是否启用 DCP
bParams.putString("dcp_start_in", "BUTTON");   // 启动模式：FULLSCREEN，BUTTON，BUTTON_ONLY
bParams.putString("dcp_pos_x", "50"); // 悬浮按键位置的 X 坐标，最右侧坐标为 0
bParams.putString("dcp_pos_y", "50"); // 悬浮按键位置的 y 坐标，最下方坐标为 0

// 添加到主参数设置中
bConfig.putBundle("PARAM_LIST", bParams);
bMain.putBundle("PLUGIN_CONFIG", bConfig);

// 发送广播
Intent i = new Intent();
i.setAction("com.symbol.infowedge.api.ACTION");
i.putExtra("com.symbol.infowedge.api.SET_CONFIG", bMain);
sendBroadcast(i);
```

**dcp_start_in** 参数的取值：

- **FULLSCREEN** - 全屏模式
- **BUTTON** - 按键模式
- **BUTTON_ONLY** - 只有按键模式

#### 设置扫码头参数

```java
// 主参数设置
Bundle bMain = new Bundle();
bMain.putString("PROFILE_NAME", "NewProfile");  // 配置文件名
bMain.putString("PROFILE_ENABLED", "true"); // 启用该配置文件
bMain.putString("CONFIG_MODE", "UPDATE");   // 将配置合并到已有的配置文件

// 设置扫码头
Bundle bConfig = new Bundle();
bConfig.putString("PLUGIN_NAME", "BARCODE");    // 设置类型：扫码头
bConfig.putString("RESET_CONFIG", "true");     // 重置原有扫码头配置

// 设置扫码头参数（若使用默认值的配置项，可不设置）
Bundle bParams = new Bundle();
bParams.putString("barcode_enabled", "true");   // 是否启用扫码头
bParams.putString("barcode_trigger_keys", "LEFT_TRIGGER,CENTER_TRIGGER,RIGHT_TRIGGER");  // 触发扫码的按键，多个按键用逗号分隔
bParams.putString("barcode_trigger_mode", "0");   // 按键触发模式：0单次扫码，1连续扫码，2按住扫码，3瞄准扫码
bParams.putString("charset_name", "Auto");   // 解码使用的数据集：Auto，UTF-8，GBK，GB18030，ISO-8859-1，Shift_JIS
bParams.putString("success_audio_type", "2"); // 扫码成功时播放提示音 Di
bParams.putString("failure_audio", "false"); // 扫码失败时播放提示音
bParams.putString("vibrate", "false"); // 扫码成功时是否震动提示
// 启用 / 禁用条码
bParams.putString("decoder_code11", "true");    // 启用 Code11 条码
bParams.putString("decoder_code128", "false");  // 禁用 Code128 条码
// 所有条码启用 / 禁用 / 恢复默认
// bParams.putString("decoder_all_symbology", "true");      // 所有条码启用
// bParams.putString("decoder_all_symbology", "false");     // 所有条码禁用
// bParams.putString("decoder_all_symbology", "default");   // 所有条码恢复默认设置
// 设置条码的参数
bParams.putString("decoder_code128_length1", "1");  // 设置 Code128 条码长度 1
bParams.putString("decoder_code128_length2", "40"); // 设置 Code128 条码长度 2
bParams.putString("decoder_upca_report_check_digit", "true");   // Transmit UPC-A Check Digit
bParams.putString("decoder_ean13_report_check_digit", "true");  // Transmit EAN-13 Check Digit

// 添加到主参数设置中
bConfig.putBundle("PARAM_LIST", bParams);
bMain.putBundle("PLUGIN_CONFIG", bConfig);

// 发送广播
Intent i = new Intent();
i.setAction("com.symbol.infowedge.api.ACTION");
i.putExtra("com.symbol.infowedge.api.SET_CONFIG", bMain);
sendBroadcast(i);
```

**barcode_trigger_keys** 参数的取值：

- **LEFT_TRIGGER** - 左扫描键
- **CENTER_TRIGGER** - 中扫描键
- **RIGHT_TRIGGER** - 右扫描键
- **SCAN** - 扫描键（针对不区分左中右扫描键的设备）
- **GUN_TRIGGER** - 手柄扫描键

**success_audio_type** 参数的取值：

- **0** - 无提示音
- **1** - 嘟声
- **2** - 嘀声

**条码类型和参数** 的取值：

更多的启用/禁用的条码类型以及参数的格式请参考 [这里](#扫码头设置-api-命名)，使用表格中的 **API 命名** 列。

#### 设置 RFID 参数

```java
// 主参数设置
Bundle bMain = new Bundle();
bMain.putString("PROFILE_NAME", "NewProfile");  // 配置文件名
bMain.putString("PROFILE_ENABLED", "true"); // 启用该配置文件
bMain.putString("CONFIG_MODE", "UPDATE");   // 将配置合并到已有的配置文件

// 设置 RFID
Bundle bConfig = new Bundle();
bConfig.putString("PLUGIN_NAME", "RFID");   // 设置类型：RFID
bConfig.putString("RESET_CONFIG", "true");  // 重置原有配置

// 设置扫码头参数（若使用默认值的配置项，可不设置）
Bundle bParams = new Bundle();
bParams.putString("rfid_input_enabled", "true");   // 是否启用 RFID
bParams.putString("rfid_trigger_keys", "LEFT_TRIGGER,CENTER_TRIGGER,RIGHT_TRIGGER");  // 触发扫码的按键，多个按键用逗号分隔
bParams.putString("rfid_trigger_mode", "0");        // 按键触发模式：0按住，1连续
bParams.putString("rfid_beeper_enable", "true");    // 标签读取提示音
bParams.putString("rfid_output_mode", "0"); // 标签输出模式，0持续(单标签)，1持续(定时)，2单次，3信号优先
bParams.putString("rfid_timed_output_interval", "200"); // 定时输出间隔(ms)
bParams.putString("rfid_filter_duplicate_tags", "true"); // 过滤重复标签
bParams.putString("rfid_antenna_transmit_power", "30"); // 天线发射功率，范围 5~30 (dBm)
bParams.putString("rfid_frequency_mode", "2"); // 工作频率，具体数值参见下表
bParams.putString("rfid_tag_read_duration", "2000"); // 读取标签持续的时间，范围：0, 100~60000 (ms)
bParams.putString("rfid_separator_to_tags", "\\n"); // 输出多个标签时，标签之间的分隔字符串
bParams.putString("rfid_tag_output_data_format", "EPC"); // 设置输出标签的内容格式，具体格式参见下面的说明
bParams.putString("rfid_epc_user_data_type", "0"); // EPC 和 USER 存储区的数据的格式，0:16进制，1-ASCII
bParams.putString("rfid_pre_filter_enable", "true"); // 是否开启预过滤功能
bParams.putString("rfid_pre_filter_memory_bank", "0"); // 选择过滤的存储区，1-EPC, 2-TID, 3-User
bParams.putString("rfid_pre_filter_offset", "4"); // 预过滤时从第几个字节开始比对
bParams.putString("rfid_pre_filter_tag_pattern", "E012"); // 指定预过滤时比对的 16 进制格式的字符串
bParams.putString("rfid_post_filter_enable", "true"); // 是否开启后过滤功能
bParams.putString("rfid_post_filter_no_of_tags_to_read", "1"); // 读取标签数量
bParams.putString("rfid_post_filter_rssi", "-80"); // 标签信号阈值。范围: -100~0 (dBm)

// 添加到主参数设置中
bConfig.putBundle("PARAM_LIST", bParams);
bMain.putBundle("PLUGIN_CONFIG", bConfig);

// 发送广播
Intent i = new Intent();
i.setAction("com.symbol.infowedge.api.ACTION");
i.putExtra("com.symbol.infowedge.api.SET_CONFIG", bMain);
sendBroadcast(i);
```

##### RFID 工作频率

| 参数取值 | 频率范围 (MHz) | 地区/标准 |
| :-: | :-: | :-: |
| 1 | 840 ~ 845 | 中国标准 |
| 2 | 902 ~ 928 | 中国标准增强 |
| 4 | 865 ~ 868 | ETSI标准 |
| 8 | 902 ~ 928 | 美国标准 |
| 22 | 917 ~ 923 | 韩国 |
| 50 | 916.7 ~ 920.9 | 日本 |
| 51 | 915.4 ~ 919 | 南非 |
| 52 | 922 ~ 928 | 台湾 |
| 53 | 918 ~ 923 | 越南 |
| 54 | 915 ~ 928 | 秘鲁 |
| 55 | 866.0 ~ 867.6 | 俄罗斯 |
| 59 | 919 ~ 923 | 马来西亚 |
| 60 | 902 ~ 907.5, 915 ~ 928 | 巴西 |
| 61 | 916 ~ 920 | 新 ETSI |
| 62 | 920 ~ 926 | 澳大利亚 |
| 63 | 923 ~ 925 | 印度尼西亚 |
| 64 | 915 ~ 917 | 以色列 |
| 65 | 920 ~ 925 | 香港 |
| 66 | 920 ~ 928 | 新西兰 |
| 68 | 920 ~ 925 | 新加坡 |
| 69 | 920 ~ 925 | 泰国 |

##### 输出标签的内容格式

可以输出标签的 `TID`、`EPC`、`USER`、`RSSI`、`PC` 内容，例如设置的格式为：`TID;EPC;RSSI`，则最终会输出标签的 `TID` 、 `EPC` 和 `信号强度` 数据，中间用分号隔开：`E2003412013A03000109B2B8;E20000194859503031323334;-60.70`。

此外还支持截取部分内容输出的功能，例如设置的格式为：`TID[4,3]-EPC[0,4]:USER[0,8]`，则最终会输出的结果如下：`013A03-E2000019:3031323334353637` 。

注意：需要输出 `USER` 存储区的内容时，必须指定输出的字节范围，即 `USER` 字段必须使用 `USER[m,n]` 格式。

#### 设置 GS1 条码格式化参数

```java
// 主参数设置
Bundle bMain = new Bundle();
bMain.putString("PROFILE_NAME", "NewProfile");  // 配置文件名
bMain.putString("PROFILE_ENABLED", "true"); // 启用该配置文件
bMain.putString("CONFIG_MODE", "UPDATE");   // 将配置合并到已有的配置文件
bMain.putString("MEMORY_PROFILE", "false");  // 是否是临时配置。临时配置存在内存中，系统重启后会丢失

// 设置 GS1
Bundle bConfig = new Bundle();
bConfig.putString("PLUGIN_NAME", "GS1");    // 设置类型：GS1 条码格式化
bConfig.putString("RESET_CONFIG", "true"); // 重置原有配置

// 设置 GS1 参数（若使用默认值的配置项，可不设置）
Bundle bParams = new Bundle();
bParams.putString("gs1_enabled", "true");   // 是否启用 GS1 条码格式化
bParams.putString("gs1_separate", "false");   // 是否开启分隔解码
bParams.putString("gs1_new_line", "true");   // 是否开启分隔换行
bParams.putString("gs1_gs_format", "0"); // GS(0x1D) 字符处理：0删除，1保留，2替换成其它字符串
bParams.putString("gs1_gs_replace", "[GS]");    // GS 替换字符串

// 添加到主参数设置中
bConfig.putBundle("PARAM_LIST", bParams);
bMain.putBundle("PLUGIN_CONFIG", bConfig);

// 发送广播
Intent i = new Intent();
i.setAction("com.symbol.infowedge.api.ACTION");
i.putExtra("com.symbol.infowedge.api.SET_CONFIG", bMain);
sendBroadcast(i);
```

#### 设置基本数据格式化（BDF）参数

```java
// 主参数设置
Bundle bMain = new Bundle();
bMain.putString("PROFILE_NAME", "NewProfile");  // 配置文件名
bMain.putString("PROFILE_ENABLED", "true"); // 启用该配置文件
bMain.putString("CONFIG_MODE", "UPDATE");   // 将配置合并到已有的配置文件
bMain.putString("MEMORY_PROFILE", "false");  // 是否是临时配置。临时配置存在内存中，不会出现在 InfoWedge 主页的配置列表中，系统重启后会丢失

// 设置 BDF
Bundle bConfig = new Bundle();
bConfig.putString("PLUGIN_NAME", "BDF");    // 设置类型：BDF
bConfig.putString("RESET_CONFIG", "true"); // 重置原有 BDF 配置

// 设置 BDF 参数（若使用默认值的配置项，可不设置）
Bundle bParams = new Bundle();
bParams.putString("bdf_enabled", "true");   // 是否启用 BDF
bParams.putString("bdf_prefix", "A");   // BDF 前缀
bParams.putString("bdf_suffix", "B");   // BDF 后缀
bParams.putString("bdf_send_tab", "true"); // 是否发送 TAB 键
bParams.putString("bdf_send_enter", "true"); // 是否发送 ENTER 键
bParams.putString("bdf_delete_start", "1"); // 删除起始字符数
bParams.putString("bdf_delete_end", "2");    // 删除结束字符数
bParams.putString("bdf_delete_string", "DEL");  // 删除内容

// 添加到主参数设置中
bConfig.putBundle("PARAM_LIST", bParams);
bMain.putBundle("PLUGIN_CONFIG", bConfig);

// 发送广播
Intent i = new Intent();
i.setAction("com.symbol.infowedge.api.ACTION");
i.putExtra("com.symbol.infowedge.api.SET_CONFIG", bMain);
sendBroadcast(i);
```

### 设置按键输出参数

```java
// 主参数设置
Bundle bMain = new Bundle();
bMain.putString("PROFILE_NAME", "NewProfile");  // 配置文件名
bMain.putString("PROFILE_ENABLED", "true"); // 启用该配置文件
bMain.putString("CONFIG_MODE", "UPDATE");   // 将配置合并到已有的配置文件

// 设置按键输出
Bundle bConfig = new Bundle();
bConfig.putString("PLUGIN_NAME", "KEYSTROKE");    // 设置类型：KEYSTROKE
bConfig.putString("RESET_CONFIG", "true"); // 重置原有按键输出配置

// 设置按键输出参数（若使用默认值的配置项，可不设置）
Bundle bParams = new Bundle();
bParams.putString("keystroke_output_enabled", "true");   // 是否启用按键输出
bParams.putString("keystroke_output_type", "0");   // 设置按键输出类型，0输出到光标位置，1模拟按键，2覆盖光标位置

// 添加到主参数设置中
bConfig.putBundle("PARAM_LIST", bParams);
bMain.putBundle("PLUGIN_CONFIG", bConfig);

// 发送广播
Intent i = new Intent();
i.setAction("com.symbol.infowedge.api.ACTION");
i.putExtra("com.symbol.infowedge.api.SET_CONFIG", bMain);
sendBroadcast(i);
```

#### 设置广播输出参数

```java
// 主参数设置
Bundle bMain = new Bundle();
bMain.putString("PROFILE_NAME", "NewProfile");  // 配置文件名
bMain.putString("PROFILE_ENABLED", "true"); // 启用该配置文件
bMain.putString("CONFIG_MODE", "UPDATE");   // 将配置合并到已有的配置文件

// 设置 Intent 输出
Bundle bConfig = new Bundle();
bConfig.putString("PLUGIN_NAME", "INTENT");    // 设置类型：INTENT
bConfig.putString("RESET_CONFIG", "true"); // 重置原有广播输出配置

// 设置 INTENT 参数（若使用默认值的配置项，可不设置）
Bundle bParams = new Bundle();
bParams.putString("intent_output_enabled", "true");   // 是否启用广播输出
bParams.putString("intent_action", "com.infowedge.data");   // 设置广播输出的 action
bParams.putString("intent_data", "data_string");   // 设置广播输出的数据名称

// 添加到主参数设置中
bConfig.putBundle("PARAM_LIST", bParams);
bMain.putBundle("PLUGIN_CONFIG", bConfig);

// 发送广播
Intent i = new Intent();
i.setAction("com.symbol.infowedge.api.ACTION");
i.putExtra("com.symbol.infowedge.api.SET_CONFIG", bMain);
sendBroadcast(i);
```

#### 设置IP输出参数

```java
// 主参数设置
Bundle bMain = new Bundle();
bMain.putString("PROFILE_NAME", "NewProfile");  // 配置文件名
bMain.putString("PROFILE_ENABLED", "true"); // 启用该配置文件
bMain.putString("CONFIG_MODE", "UPDATE");   // 将配置合并到已有的配置文件

// 设置 IP 输出
Bundle bConfig = new Bundle();
bConfig.putString("PLUGIN_NAME", "IP");    // 设置类型：IP
bConfig.putString("RESET_CONFIG", "true"); // 重置原有 IP 输出配置

// 设置 IP 参数（若使用默认值的配置项，可不设置）
Bundle bParams = new Bundle();
bParams.putString("ip_output_enabled", "true"); // 是否启用广播输出
bParams.putString("ip_output_protocol", "UDP"); // 协议类型: TCP，UDP
bParams.putString("ip_output_address", "192.168.0.100"); // IP 地址
bParams.putString("ip_output_port", "55555"); // 端口号

// 添加到主参数设置中
bConfig.putBundle("PARAM_LIST", bParams);
bMain.putBundle("PLUGIN_CONFIG", bConfig);

// 发送广播
Intent i = new Intent();
i.setAction("com.symbol.infowedge.api.ACTION");
i.putExtra("com.symbol.infowedge.api.SET_CONFIG", bMain);
sendBroadcast(i);
```

#### 设置剪贴板输出参数

```java
// 主参数设置
Bundle bMain = new Bundle();
bMain.putString("PROFILE_NAME", "NewProfile");  // 配置文件名
bMain.putString("PROFILE_ENABLED", "true"); // 启用该配置文件
bMain.putString("CONFIG_MODE", "UPDATE");   // 将配置合并到已有的配置文件

// 设置剪贴板输出
Bundle bConfig = new Bundle();
bConfig.putString("PLUGIN_NAME", "CLIPBOARD");    // 设置类型：CLIPBOARD
bConfig.putString("RESET_CONFIG", "true"); // 重置原有剪贴板输出配置

// 设置剪贴板输出参数（若使用默认值的配置项，可不设置）
Bundle bParams = new Bundle();
bParams.putString("clipboard_output_enabled", "true");   // 是否启用剪贴板输出

// 添加到主参数设置中
bConfig.putBundle("PARAM_LIST", bParams);
bMain.putBundle("PLUGIN_CONFIG", bConfig);

// 发送广播
Intent i = new Intent();
i.setAction("com.symbol.infowedge.api.ACTION");
i.putExtra("com.symbol.infowedge.api.SET_CONFIG", bMain);
sendBroadcast(i);
```

#### 同时设置多个模块

```java
// 主参数设置
Bundle bMain = new Bundle();
bMain.putString("PROFILE_NAME", "NewProfile");  // 配置文件名
bMain.putString("PROFILE_ENABLED", "true"); // 启用该配置文件
bMain.putString("CONFIG_MODE", "CREATE_IF_NOT_EXIST");  // 如果配置文件不存在则创建
bMain.putString("RESET_CONFIG", "true");  // 如果配置存在则复位配置
bMain.putString("MEMORY_PROFILE", "false");  // 是否是临时配置。临时配置存在内存中，系统重启后会丢失

// 设置关联应用程序
Bundle bundleApp = new Bundle();
bundleApp.putString("PACKAGE_NAME", "com.chainway.infowedge.demo");    // 关联的应用程序包名
bundleApp.putStringArray("ACTIVITY_LIST", new String[]{"*"});  // * 号表示关联该应用的所有 Activity
bMain.putParcelableArray("APP_LIST", new Bundle[] { bundleApp });

// 设置扫码头
Bundle bBarcodeConfig = new Bundle();
bBarcodeConfig.putString("PLUGIN_NAME", "BARCODE");    // 设置类型：扫码头
bBarcodeConfig.putString("RESET_CONFIG", "true");     // 重置原有扫码头配置
Bundle bBarcodeParams = new Bundle();
bBarcodeParams.putString("barcode_trigger_mode", "1");   // 按键触发模式：0单次扫码，1连续扫码，2按住扫码，3瞄准扫码
bBarcodeParams.putString("failure_audio", "true"); // 扫码失败时播放提示音
bBarcodeParams.putString("vibrate", "true"); // 扫码成功时是否震动提示
bBarcodeParams.putString("decoder_code11", "true");    // 启用 Code11 条码
bBarcodeParams.putString("decoder_code128", "false");  // 禁用 Code128 条码
bBarcodeConfig.putBundle("PARAM_LIST", bBarcodeParams);

// 设置 BDF
Bundle bBdfConfig = new Bundle();
bBdfConfig.putString("PLUGIN_NAME", "BDF");    // 设置类型：BDF
bBdfConfig.putString("RESET_CONFIG", "true"); // 重置原有 BDF 配置
Bundle bBdfParams = new Bundle();
bBdfParams.putString("bdf_enabled", "true");   // 是否启用 BDF
bBdfParams.putString("bdf_prefix", "A");   // BDF 前缀
bBdfConfig.putBundle("PARAM_LIST", bBdfParams);

// 设置 Intent 输出
Bundle bIntentConfig = new Bundle();
bIntentConfig.putString("PLUGIN_NAME", "INTENT");    // 设置类型：INTENT
bIntentConfig.putString("RESET_CONFIG", "true");   // 重置原有广播输出配置
Bundle bIntentParams = new Bundle();
bIntentParams.putString("intent_output_enabled", "true");   // 是否启用广播输出
bIntentConfig.putBundle("PARAM_LIST", bIntentParams);

// 设置按键输出
Bundle bKeystrokeConfig = new Bundle();
bKeystrokeConfig.putString("PLUGIN_NAME", "KEYSTROKE");    // 设置类型：KEYSTROKE
bKeystrokeConfig.putString("RESET_CONFIG", "true"); // 重置原有按键输出配置
Bundle bKeystrokeParams = new Bundle();
bKeystrokeParams.putString("keystroke_output_enabled", "true");   // 是否启用按键输出
bKeystrokeParams.putString("keystroke_output_type", "0");   // 设置按键输出类型，0输出到光标位置，1模拟按键，2覆盖光标位置
bKeystrokeConfig.putBundle("PARAM_LIST", bKeystrokeParams);

// 设置剪贴板输出
Bundle bClipboardConfig = new Bundle();
bClipboardConfig.putString("PLUGIN_NAME", "CLIPBOARD");    // 设置类型：CLIPBOARD
bClipboardConfig.putString("RESET_CONFIG", "true"); // 重置原有剪贴板输出配置
Bundle bClipboardParams = new Bundle();
bClipboardParams.putString("clipboard_output_enabled", "true");   // 是否启用剪贴板输出
bClipboardConfig.putBundle("PARAM_LIST", bClipboardParams);

ArrayList<Bundle> bundlePluginConfig = new ArrayList<>();
bundlePluginConfig.add(bBarcodeConfig);
bundlePluginConfig.add(bBdfConfig);
bundlePluginConfig.add(bIntentConfig);
bundlePluginConfig.add(bKeystrokeConfig);
bundlePluginConfig.add(bClipboardConfig);
bMain.putParcelableArrayList("PLUGIN_CONFIG", bundlePluginConfig);

// 发送广播
Intent i = new Intent();
i.setAction("com.symbol.infowedge.api.ACTION");
i.putExtra("com.symbol.infowedge.api.SET_CONFIG", bMain);
sendBroadcast(i);
```

## 操作接口

### 启用 / 禁用 InfoWedge（Enable/Disable InfoWedge）

```java
// 发送广播
Intent i = new Intent();
i.setAction("com.symbol.infowedge.api.ACTION");
i.putExtra("com.symbol.infowedge.api.ENABLE_INFOWEDGE", true);  // true 开启，false 关闭
// 回复执行结果
i.putExtra("SEND_RESULT", "true");
i.putExtra("COMMAND_IDENTIFIER", "1234");
sendBroadcast(i);

// 接收命令结果的广播接收器
private BroadcastReceiver resultBroadcastReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        String command = intent.getStringExtra("COMMAND");
        String commandId = intent.getStringExtra("COMMAND_IDENTIFIER");
        String result = intent.getStringExtra("RESULT");

        String resultInfo = "";
        if (intent.hasExtra("RESULT_INFO")) {
            Bundle bundle = intent.getBundleExtra("RESULT_INFO");
            for (String key: bundle.keySet()) {
                resultInfo += key + ": " + bundle.getString(key) + "\n";
            }
        }

        String text = "Command: " + command + "\n"
            + "Result: " + result + "\n"
            + "Result Info: " + resultInfo + "\n"
            + "CID:" + commandId;
        Log.d(TAG, text);
    }
};
```

**错误码：**

- **INFOWEDGE_ALREADY_ENABLED** - InfoWedge 已经启用
- **INFOWEDGE_ALREADY_DISABLED** - InfoWedge 已经禁用

### 触发扫码头扫码（Soft Scan Trigger）

```java
// 发送广播
Intent i = new Intent();
i.setAction("com.symbol.infowedge.api.ACTION");
i.putExtra("com.symbol.infowedge.api.SOFT_SCAN_TRIGGER", "START"); // START, STOP, TOGGLE
i.putExtra("SEND_RESULT", "true");
i.putExtra("COMMAND_IDENTIFIER", "1234");
sendBroadcast(i);
```

**错误码：**

- **INFOWEDGE_DISABLED** - InfoWedge 未启用
- **PROFILE_DISABLED** - Profile 未启用
- **INPUT_NOT_ENABLED** - 输入未启用
- **PARAMETER_INVALID** - 参数错误

### 触发RFID扫描（Soft RFID Trigger）
```java
// 发送广播
Intent i = new Intent();
i.setAction("com.symbol.infowedge.api.ACTION");
i.putExtra("com.symbol.infowedge.api.SOFT_RFID_TRIGGER", "START"); // START, STOP, TOGGLE
i.putExtra("SEND_RESULT", "true");
i.putExtra("COMMAND_IDENTIFIER", "1234");
sendBroadcast(i);
```

**错误码：**

同 [触发扫码头扫码](#触发扫码头扫码soft-scan-trigger) 的错误码

## 扫码头设置 API 命名

### Zebra Decoders

|名称|API 命名|参数值 （默认值加 * 标识）|
|:--|:--|:--|
|**UPC-A**|decoder_upca|*true, false|
|&nbsp;&nbsp; - Transmit UPC-A Check Digit|decoder_upca_report_check_digit|*true, false|
|&nbsp;&nbsp; - UPC-A Preamble|decoder_upca_preamble|0 - Preamble None</br>*1 - Preamble Sys Char</br>2 - Preamble Country and Sys Char|
|**UPC-E**|decoder_upce|*true, false|
|&nbsp;&nbsp; - Transmit UPC-E Check Digit|decoder_upce_report_check_digit|*true, false|
|&nbsp;&nbsp; - Convert UPC-E to A|decoder_upce_convert_to_upca|true, *false|
|&nbsp;&nbsp; - UPC-E Preamble|decoder_upce_preamble|0 - Preamble None</br>*1 - Preamble Sys Char</br>2 - Preamble Country and Sys Char|
|**UPC-E1**|decoder_upce1|true, *false|
|&nbsp;&nbsp; - Transmit UPC-E1 Check Digit|decoder_upce1_report_check_digit|*true, false|
|&nbsp;&nbsp; - Convert UPC-E1 to A|decoder_upce1_convert_to_upca|true, *false|
|&nbsp;&nbsp; - UPC-E1 Preamble|decoder_upce1_preamble|0 - Preamble None</br>*1 - Preamble Sys Char</br>2 - Preamble Country and Sys Char|
|**EAN-8 / JAN 8**|decoder_ean8|*true, false|
|**EAN-13 / JAN 13**|decoder_ean13|*true, false|
|**Code 128**|decoder_code128|*true, false|
|&nbsp;&nbsp; - Length1|decoder_code128_length1|0–55 (*0)|
|&nbsp;&nbsp; - Length2|decoder_code128_length2|0–55 (*0)|
|&nbsp;&nbsp; - GS1-128|decoder_code128_enable_gs1128|*true, false|
|&nbsp;&nbsp; - ISBT 128|decoder_code128_enable_isbt128|*true, false|
|&nbsp;&nbsp; - ISBT Concatenation|decoder_code128_isbt128_concat_mode|*0 - Concat Mode Never</br>1 - Concat Mode Always</br>2 - Concat Mode Auto|
|&nbsp;&nbsp; - Check ISBT Table|decoder_code128_check_isbt_table|true, *false|
|**Code 39**|decoder_code39|*true, false|
|&nbsp;&nbsp; - Length1|decoder_code39_length1|0–55 (*2)|
|&nbsp;&nbsp; - Length2|decoder_code39_length2|0–55 (*55)|
|&nbsp;&nbsp; - Code 39 Check Digit Verification|decoder_code39_verify_check_digit|true, *false|
|&nbsp;&nbsp; - Trioptic Code 39|decoder_code39_trioptic|true, *false|
|&nbsp;&nbsp; - Convert Code 39 to Code 32|decoder_code39_convert_to_code32|true, *false|
|&nbsp;&nbsp; - Code 32 Prefix|decoder_code39_report_code32_prefix|true, *false|
|&nbsp;&nbsp; - Transmit Code 39 Check Digit|decoder_code39_report_check_digit|true, *false|
|&nbsp;&nbsp; - Code 39 Full ASCII Conversion|decoder_code39_full_ascii|true, *false|
|&nbsp;&nbsp; - Code 39 Reduced Quiet Zone|decoder_code39_enable_marginless_decode|true, *false|
|**Code 93**|decoder_code93|true, *false|
|&nbsp;&nbsp; - Length1|decoder_code93_length1|0–55 (*4)|
|&nbsp;&nbsp; - Length2|decoder_code93_length2|0–55 (*55)|
|**Code 11**|decoder_code11|true, *false|
|&nbsp;&nbsp; - Length1|decoder_code11_length1|0–55 (*4)|
|&nbsp;&nbsp; - Length2|decoder_code11_length2|0–55 (*55)|
|&nbsp;&nbsp; - Check Digit Verification|decoder_code11_verify_check_digit|true, *false|
|&nbsp;&nbsp; - Transmit Code 11 Check Digit|decoder_code11_report_check_digit|true, *false|
|**Interleaved 2 of 5**|decoder_i2of5|*true, false|
|&nbsp;&nbsp; - Length1|decoder_i2of5_length1|0–55 (*14)|
|&nbsp;&nbsp; - Length2|decoder_i2of5_length2|0–55 (*0)|
|&nbsp;&nbsp; - Check Digit Verification|decoder_i2of5_check_digit|true, *false|
|&nbsp;&nbsp; - Transmit I 2 of 5 Check Digit|decoder_i2of5_report_check_digit|true, *false|
|**Discrete (Standard) 2 of 5**|decoder_d2of5|true, *false|
|&nbsp;&nbsp; - Length1|decoder_d2of5_length1|0–55 (*12)|
|&nbsp;&nbsp; - Length2|decoder_d2of5_length2|0–55 (*0)|
|**Matrix 2 of 5**|decoder_matrix_2of5|true, *false|
|&nbsp;&nbsp; - Length1|decoder_matrix_2of5_length1|0–55 (*14)|
|&nbsp;&nbsp; - Length2|decoder_matrix_2of5_length2|0–55 (*0)|
|**Codabar**|decoder_codabar|true, *false|
|&nbsp;&nbsp; - Length1|decoder_codabar_length1|0–55 (*5)|
|&nbsp;&nbsp; - Length2|decoder_codabar_length2|0–55 (*55)|
|&nbsp;&nbsp; - CLSI Editing|decoder_codabar_clsi_editing|true, *false|
|&nbsp;&nbsp; - NOTIS Editing|decoder_codabar_notis_editing|true, *false|
|**MSI**|decoder_msi|true, *false|
|&nbsp;&nbsp; - Length1|decoder_msi_length1|0–55 (*4)|
|&nbsp;&nbsp; - Length2|decoder_msi_length2|0–55 (*55)|
|&nbsp;&nbsp; - MSI Check Digits|decoder_msi_check_digit|true, *false|
|&nbsp;&nbsp; - Transmit MSI Check Digit|decoder_msi_report_check_digit|true, *false|
|&nbsp;&nbsp; - MSI Check Digit Algorithm|decoder_msi_check_digit_scheme|*true, false|
|**Chinese 2 of 5**|decoder_chinese_2of5|true, *false|
|**Korean 3 of 5**|decoder_korean_3of5|true, *false|
|**US Planet**|decoder_usplanet|*true, false|
|&nbsp;&nbsp; - Transmit US Planet Check Digit|decoder_usplanet_report_check_digit|*true, false|
|**US Postnet**|decoder_uspostnet|*true, false|
|&nbsp;&nbsp; - Transmit US Postnet Check Digit|decoder_uspostnet_report_check_digit|*true, false|
|**UK Postal**|decoder_us_postal|*true, false|
|**Japan Postal**|decoder_japan_postal|*true, false|
|**Australia Post**|decoder_australia_post|*true, false|
|**GS1 DataBar Expanded**|decoder_gs1_databar_exp|true, *false|
|**GS1 DataBar Limited**|decoder_gs1_databar_lim|true, *false|
|**GS1 DataBar-14**|decoder_gs1_databar14|*true, false|
|**Composite CC-C**|decoder_composite_c|true, *false|
|**Composite CC-A/B**|decoder_composite_ab|true, *false|
|&nbsp;&nbsp; - UPC Composite Mode|decoder_composite_upc_link_mode|*0 - UPC Never Linked</br>1 - UPC Always Linked</br>2 - Auto discriminate UPC Composites|
|**Composite TLC-39**|decoder_composite_tlc39|true, *false|
|**PDF417**|decoder_pdf417|*true, false|
|**MicroPDF417**|decoder_micropdf417|true, *false|
|**Data Matrix**|decoder_datamatrix|*true, false|
|&nbsp;&nbsp; - Data Matrix Inverse|decoder_datamatrix_inverse|*0 - Regular Only</br>1 - Inverse Only</br>2 - Inverse Autodetect|
|&nbsp;&nbsp; - Decode Mirror Images|decoder_datamatrix_mirror|*0 - Never</br>1 - Always</br>2 - Auto|
|**MaxiCode**|decoder_maxicode|*true, false|
|**QR Code**|decoder_qrcode|*true, false|
|**MicroQR**|decoder_microqr|*true, false|
|**Aztec**|decoder_aztec|*true, false|
|&nbsp;&nbsp; - Aztec Inverse|decoder_aztec_inverse|*0 - Regular Only</br>1 - Inverse Only</br>2 - Inverse Autodetect|
|**Han Xin**|decoder_hanxin|true, *false|
|&nbsp;&nbsp; - Han Xin Inverse|decoder_hanxin_inverse|*0 - Regular Only</br>1 - Inverse Only</br>2 - Inverse Autodetect|
|**Grid Matrix**|decoder_grid_matrix|*true, false|
|&nbsp;&nbsp; - Grid Matrix Inverse|decoder_grid_matrix_inverse|*0 - Regular Only</br>1 - Inverse Only</br>2 - Inverse Autodetect|
|&nbsp;&nbsp; - Decode Mirror Images|decoder_grid_matrix_mirror|*0 - Never</br>1 - Always</br>2 - Auto|

### Newland Decoders

|名称|API 命名|参数值 （默认值加 * 标识）|
|:--|:--|:--|
|**UPC-A**|decoder_upca|*true, false|
|&nbsp;&nbsp; - Transmit UPC-A Check Digit|decoder_upca_report_check_digit|*true, false|
|&nbsp;&nbsp; - Transmit UPC-A Sys Char|decoder_upca_report_sys_char|*true, false|
|&nbsp;&nbsp; - Digit2|decoder_upca_digit2|true, *false|
|&nbsp;&nbsp; - Digit5|decoder_upca_digit5|true, *false|
|&nbsp;&nbsp; - AddonRequired|decoder_upca_addon_required|true, *false|
|&nbsp;&nbsp; - UsSysData|decoder_upca_report_us_sys_char|true, *false|
|&nbsp;&nbsp; - Only Data|decoder_upca_only_data|*true, false|
|&nbsp;&nbsp; - Coupon|decoder_upca_coupon|true, *false|
|&nbsp;&nbsp; - ReqCoupon|decoder_upca_req_coupon|true, *false|
|&nbsp;&nbsp; - Gs1Coupon|decoder_upca_gs1_coupon|true, *false|
|&nbsp;&nbsp; - CodeNum|decoder_upca_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_upca_num_fixed|true, *false|
|**UPC-E**|decoder_upce|*true, false|
|&nbsp;&nbsp; - Transmit UPC-E Check Digit|decoder_upce_report_check_digit|*true, false|
|&nbsp;&nbsp; - Transmit UPC-E Sys Char|decoder_upce_report_sys_char|*true, false|
|&nbsp;&nbsp; - Digit2|decoder_upce_digit2|true, *false|
|&nbsp;&nbsp; - Digit5|decoder_upce_digit5|true, *false|
|&nbsp;&nbsp; - MsgToupca|decoder_upce_msg_to_upca|true, *false|
|&nbsp;&nbsp; - AddonRequired|decoder_upce_addon_required|true, *false|
|&nbsp;&nbsp; - UsSysData|decoder_upce_report_us_sys_char|true, *false|
|&nbsp;&nbsp; - Only Data|decoder_upce_only_data|*true, false|
|&nbsp;&nbsp; - CodeNum|decoder_upce_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_upce_num_fixed|true, *false|
|**EAN-8 / JAN 8**|decoder_ean8|*true, false|
|&nbsp;&nbsp; - Transmit EAN-8 Check Digit|decoder_ean8_report_check_digit|*true, false|
|&nbsp;&nbsp; - Digit2|decoder_ean8_digit2|true, *false|
|&nbsp;&nbsp; - Digit5|decoder_ean8_digit5|true, *false|
|&nbsp;&nbsp; - AddonRequired|decoder_ean8_addon_required|true, *false|
|&nbsp;&nbsp; - CodeNum|decoder_ean8_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_ean8_num_fixed|true, *false|
|**EAN-13 / JAN 13**|decoder_ean13|*true, false|
|&nbsp;&nbsp; - Transmit EAN-13 Check Digit|decoder_ean13_report_check_digit|*true, false|
|&nbsp;&nbsp; - Digit2|decoder_ean13_digit2|true, *false|
|&nbsp;&nbsp; - Digit5|decoder_ean13_digit5|true, *false|
|&nbsp;&nbsp; - AddonRequired|decoder_ean13_addon_required|true, *false|
|&nbsp;&nbsp; - CodeNum|decoder_ean13_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_ean13_num_fixed|true, *false|
|**Code 128**|decoder_code128|*true, false|
|&nbsp;&nbsp; - Length1|decoder_code128_length1|1-127 (*1)|
|&nbsp;&nbsp; - Length2|decoder_code128_length2|1-127 (*127)|
|&nbsp;&nbsp; - CodeNum|decoder_code128_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_code128_num_fixed|true, *false|
|**Code 39**|decoder_code39|*true, false|
|&nbsp;&nbsp; - Length1|decoder_code39_length1|1-127 (*1)|
|&nbsp;&nbsp; - Length2|decoder_code39_length2|1-127 (*127)|
|&nbsp;&nbsp; - Code 39 Check Digit Verification|decoder_code39_verify_check_digit|true, *false|
|&nbsp;&nbsp; - Code 32 Prefix|decoder_code39_report_code32_prefix|true, *false|
|&nbsp;&nbsp; - Transmit Code 39 Check Digit|decoder_code39_report_check_digit|true, *false|
|&nbsp;&nbsp; - Code 39 Full ASCII Conversion|decoder_code39_full_ascii|true, *false|
|&nbsp;&nbsp; - TrsmtStartStop|decoder_code39_report_start_stop|true, *false|
|&nbsp;&nbsp; - Code32SpecEdit|decoder_code39_code32_spec_edit|true, *false|
|&nbsp;&nbsp; - Code32TrsmtChkChar|decoder_code39_code32_report_check_digit|true, *false|
|&nbsp;&nbsp; - Code32TrsmtStasrtStop|decoder_code39_code32_report_start_stop|true, *false|
|&nbsp;&nbsp; - CodeNum|decoder_code39_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_code39_num_fixed|true, *false|
|**Code 93**|decoder_code93|true, *false|
|&nbsp;&nbsp; - Length1|decoder_code93_length1|1-127 (*2)|
|&nbsp;&nbsp; - Length2|decoder_code93_length2|1-127 (*127)|
|&nbsp;&nbsp; - CodeNum|decoder_code93_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_code93_num_fixed|true, *false|
|**Code 11**|decoder_code11|true, *false|
|&nbsp;&nbsp; - Length1|decoder_code11_length1|1-127 (*6)|
|&nbsp;&nbsp; - Length2|decoder_code11_length2|1-127 (*127)|
|&nbsp;&nbsp; - Transmit Code 11 Check Digit|decoder_code11_report_check_digit|true, *false|
|&nbsp;&nbsp; - CodeNum|decoder_code11_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_code11_num_fixed|true, *false|
|&nbsp;&nbsp; - ChkMode|decoder_code11_chk_mode|*0 - OFF</br>1 - C11_MOD11</br>2 - C11_FIXED_MOD11_MOD11</br>3 - C11_FIXED_MOD11_MOD9</br>4 - C11_AUTO_MOD11_MOD11</br>5 - C11_C11_AUTO_MOD11_MOD9|
|**Interleaved 2 of 5**|decoder_i2of5|*true, false|
|&nbsp;&nbsp; - Length1|decoder_i2of5_length1|1-127 (*6)|
|&nbsp;&nbsp; - Length2|decoder_i2of5_length2|1-127 (*127)|
|&nbsp;&nbsp; - Check Digit Verification|decoder_i2of5_check_digit|true, *false|
|&nbsp;&nbsp; - Transmit I 2 of 5 Check Digit|decoder_i2of5_report_check_digit|true, *false|
|&nbsp;&nbsp; - CodeNum|decoder_i2of5_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_i2of5_num_fixed|true, *false|
|**Matrix 2 of 5**|decoder_matrix_2of5|true, *false|
|&nbsp;&nbsp; - Length1|decoder_matrix_2of5_length1|1-127 (*6)|
|&nbsp;&nbsp; - Length2|decoder_matrix_2of5_length2|1-127 (*127)|
|&nbsp;&nbsp; - Check Digit Verification|decoder_matrix_2of5_check_digit|true, *false|
|&nbsp;&nbsp; - Transmit Check Digit|decoder_matrix_2of5_report_check_digit|true, *false|
|&nbsp;&nbsp; - CodeNum|decoder_matrix_2of5_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_matrix_2of5_num_fixed|true, *false|
|**Codabar**|decoder_codabar|*true, false|
|&nbsp;&nbsp; - Length1|decoder_codabar_length1|1-127 (*4)|
|&nbsp;&nbsp; - Length2|decoder_codabar_length2|1-127 (*127)|
|&nbsp;&nbsp; - TrsmtStartStop|decoder_codabar_report_start_stop|true, *false|
|&nbsp;&nbsp; - CodeNum|decoder_codabar_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_codabar_num_fixed|true, *false|
|**US Planet**|decoder_usplanet|true, *false|
|&nbsp;&nbsp; - Transmit US Planet Check Digit|decoder_usplanet_report_check_digit|true, *false|
|**US Postnet**|decoder_uspostnet|true, *false|
|&nbsp;&nbsp; - Transmit US Postnet Check Digit|decoder_uspostnet_report_check_digit|true, *false|
|**Japan Postal**|decoder_japan_postal|true, *false|
|&nbsp;&nbsp; - Transmit Check Digit|decoder_japan_postal_report_check_digit|true, *false|
|**Australia Post**|decoder_australia_post|*true, false|
|**PDF417**|decoder_pdf417|*true, false|
|&nbsp;&nbsp; - Length1|decoder_pdf417_length1|1-2710 (*1)|
|&nbsp;&nbsp; - Length2|decoder_pdf417_length2|1-2710 (*2710)|
|&nbsp;&nbsp; - CodeNum|decoder_pdf417_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_pdf417_num_fixed|true, *false|
|&nbsp;&nbsp; - Inverse|decoder_pdf417_inverse|*0 - Normal</br>1 - Inverse</br>2 - Inversion Mode|
|&nbsp;&nbsp; - Mirror|decoder_pdf417_mirror_en|*true, false|
|&nbsp;&nbsp; - CloseECI|decoder_pdf417_close_eci|*true, false|
|**MicroPDF417**|decoder_micropdf417|true, *false|
|&nbsp;&nbsp; - Length1|decoder_micropdf417_length1|1-366 (*1)|
|&nbsp;&nbsp; - Length2|decoder_micropdf417_length2|1-366 (*366)|
|&nbsp;&nbsp; - CodeNum|decoder_micropdf417_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_micropdf417_num_fixed|true, *false|
|&nbsp;&nbsp; - Mirror|decoder_micropdf417_mirror_en|*true, false|
|&nbsp;&nbsp; - CloseECI|decoder_micropdf417_close_eci|*true, false|
|**Data Matrix**|decoder_datamatrix|*true, false|
|&nbsp;&nbsp; - Data Matrix Inverse|decoder_datamatrix_inverse|*0 - Normal</br>1 - Inverse</br>2 - Inversion Mode|
|&nbsp;&nbsp; - Length1|decoder_datamatrix_length1|1-3116 (*1)|
|&nbsp;&nbsp; - Length2|decoder_datamatrix_length2|1-3116 (*3116)|
|&nbsp;&nbsp; - CodeNum|decoder_datamatrix_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_datamatrix_num_fixed|true, *false|
|&nbsp;&nbsp; - Mirror|decoder_datamatrix_mirror_en|*true, false|
|&nbsp;&nbsp; - CloseECI|decoder_datamatrix_close_eci|*true, false|
|&nbsp;&nbsp; - RectAngle|decoder_datamatrix_rect_angle|true, *false|
|**MaxiCode**|decoder_maxicode|true, *false|
|&nbsp;&nbsp; - Length1|decoder_maxicode_length1|1-150 (*1)|
|&nbsp;&nbsp; - Length2|decoder_maxicode_length2|1-150 (*150)|
|&nbsp;&nbsp; - CodeNum|decoder_maxicode_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_maxicode_num_fixed|true, *false|
|&nbsp;&nbsp; - Mirror|decoder_maxicode_mirror_en|*true, false|
|**QR Code**|decoder_qrcode|*true, false|
|&nbsp;&nbsp; - Length1|decoder_qrcode_length1|1-7089 (*1)|
|&nbsp;&nbsp; - Length2|decoder_qrcode_length2|1-7089 (*7089)|
|&nbsp;&nbsp; - Inverse|decoder_qrcode_inverse|*0 - Normal</br>1 - Inverse</br>2 - Inversion Mode|
|&nbsp;&nbsp; - CloseECI|decoder_qrcode_close_eci|*true, false|
|&nbsp;&nbsp; - CodeNum|decoder_qrcode_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_qrcode_num_fixed|true, *false|
|&nbsp;&nbsp; - Model1|decoder_qrcode_model1|*true, false|
|&nbsp;&nbsp; - Mirror|decoder_qrcode_mirror_en|*true, false|
|**MicroQR**|decoder_microqr|*true, false|
|&nbsp;&nbsp; - Length1|decoder_microqr_length1|1-35 (*1)|
|&nbsp;&nbsp; - Length2|decoder_microqr_length2|1-35 (*35)|
|&nbsp;&nbsp; - CodeNum|decoder_microqr_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_microqr_num_fixed|true, *false|
|&nbsp;&nbsp; - Mirror|decoder_microqr_mirror_en|*true, false|
|**Aztec**|decoder_aztec|true, *false|
|&nbsp;&nbsp; - Aztec Inverse|decoder_aztec_inverse|*0 - Normal</br>1 - Inverse</br>2 - Inversion Mode|
|&nbsp;&nbsp; - Length1|decoder_aztec_length1|1-3832 (*1)|
|&nbsp;&nbsp; - Length2|decoder_aztec_length2|1-3832 (*3832)|
|&nbsp;&nbsp; - CodeNum|decoder_aztec_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_aztec_num_fixed|true, *false|
|&nbsp;&nbsp; - Mirror|decoder_aztec_mirror|*true, false|
|&nbsp;&nbsp; - CloseECI|decoder_aztec_close_eci|*true, false|
|**Han Xin**|decoder_hanxin|true, *false|
|&nbsp;&nbsp; - Han Xin Inverse|decoder_hanxin_inverse|*0 - Normal</br>1 - Inverse</br>2 - Inversion Mode|
|&nbsp;&nbsp; - Length1|decoder_hanxin_length1|1-7827 (*1)|
|&nbsp;&nbsp; - Length2|decoder_hanxin_length2|1-7827 (*7827)|
|&nbsp;&nbsp; - CodeNum|decoder_hanxin_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_hanxin_num_fixed|true, *false|
|&nbsp;&nbsp; - Mirror|decoder_hanxin_mirror|*true, false|
|&nbsp;&nbsp; - CloseECI|decoder_hanxin_close_eci|*true, false|
|**China Post**|decoder_china_post|true, *false|
|&nbsp;&nbsp; - Length1|decoder_china_post_length1|1-127 (*1)|
|&nbsp;&nbsp; - Length2|decoder_china_post_length2|1-127 (*127)|
|&nbsp;&nbsp; - Check Digit Verification|decoder_china_post_check_digit|true, *false|
|&nbsp;&nbsp; - Transmit Check Digit|decoder_china_post_report_check_digit|true, *false|
|&nbsp;&nbsp; - CodeNum|decoder_china_post_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_china_post_num_fixed|true, *false|
|**MSI Plessey**|decoder_msi_plessey|true, *false|
|&nbsp;&nbsp; - Length1|decoder_msi_plessey_length1|1-127 (*4)|
|&nbsp;&nbsp; - Length2|decoder_msi_plessey_length2|1-127 (*127)|
|&nbsp;&nbsp; - Transmit MSI Check Digit|decoder_msi_plessey_report_check_digit|true, *false|
|&nbsp;&nbsp; - CodeNum|decoder_msi_plessey_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_msi_plessey_num_fixed|true, *false|
|&nbsp;&nbsp; - ChkMode|decoder_msi_plessey_chk_mode|*0 - OFF</br>1 - MOD10</br>2 - MOD10MOD10</br>3 - MOD10MOD11|
|**AIM 128**|decoder_aim128|true, *false|
|&nbsp;&nbsp; - Length1|decoder_aim128_length1|1-127 (*1)|
|&nbsp;&nbsp; - Length2|decoder_aim128_length2|1-127 (*127)|
|&nbsp;&nbsp; - CodeNum|decoder_aim128_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_aim128_num_fixed|true, *false|
|**Code 16K**|decoder_code16k|true, *false|
|&nbsp;&nbsp; - Length1|decoder_code16k_length1|1-127 (*1)|
|&nbsp;&nbsp; - Length2|decoder_code16k_length2|1-127 (*127)|
|&nbsp;&nbsp; - CodeNum|decoder_code16k_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_code16k_num_fixed|true, *false|
|**Code 49**|decoder_code49|true, *false|
|&nbsp;&nbsp; - Length1|decoder_code49_length1|1-127 (*1)|
|&nbsp;&nbsp; - Length2|decoder_code49_length2|1-127 (*127)|
|&nbsp;&nbsp; - CodeNum|decoder_code49_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_code49_num_fixed|true, *false|
|**Industrial 2 of 5**|decoder_industrial_2of5|true, *false|
|&nbsp;&nbsp; - Check Digit Verification|decoder_industrial_2of5_check_digit|true, *false|
|&nbsp;&nbsp; - Transmit Check Digit|decoder_industrial_2of5_report_check_digit|true, *false|
|&nbsp;&nbsp; - Length1|decoder_industrial_2of5_length1|1-127 (*6)|
|&nbsp;&nbsp; - Length2|decoder_industrial_2of5_length2|1-127 (*127)|
|&nbsp;&nbsp; - CodeNum|decoder_industrial_2of5_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_industrial_2of5_num_fixed|true, *false|
|**ISBN**|decoder_isbn|true, *false|
|&nbsp;&nbsp; - Length|decoder_isbn_length|*0 - 10DIGIT</br>1 - 13DIGIT|
|&nbsp;&nbsp; - Digit2|decoder_isbn_digit2|true, *false|
|&nbsp;&nbsp; - Digit5|decoder_isbn_digit5|true, *false|
|&nbsp;&nbsp; - AddonRequired|decoder_isbn_addon_required|true, *false|
|&nbsp;&nbsp; - CodeNum|decoder_isbn_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_isbn_num_fixed|true, *false|
|**ISSN**|decoder_issn|true, *false|
|&nbsp;&nbsp; - Digit2|decoder_issn_digit2|true, *false|
|&nbsp;&nbsp; - Digit5|decoder_issn_digit5|true, *false|
|&nbsp;&nbsp; - AddonRequired|decoder_issn_addon_required|true, *false|
|&nbsp;&nbsp; - CodeNum|decoder_issn_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_issn_num_fixed|true, *false|
|**ITF-14**|decoder_itf14|true, *false|
|&nbsp;&nbsp; - Transmit Check Digit|decoder_itf14_report_check_digit|true, *false|
|&nbsp;&nbsp; - CodeNum|decoder_itf14_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_itf14_num_fixed|true, *false|
|**ITF-6**|decoder_itf6|true, *false|
|&nbsp;&nbsp; - Transmit Check Digit|decoder_itf6_report_check_digit|true, *false|
|&nbsp;&nbsp; - CodeNum|decoder_itf6_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_itf6_num_fixed|true, *false|
|**UK Plessey**|decoder_uk_plessey|true, *false|
|&nbsp;&nbsp; - Length1|decoder_uk_plessey_length1|1-127 (*2)|
|&nbsp;&nbsp; - Length2|decoder_uk_plessey_length2|1-127 (*127)|
|&nbsp;&nbsp; - Check Digits|decoder_uk_plessey_check_digit|true, *false|
|&nbsp;&nbsp; - Transmit Check Digit|decoder_uk_plessey_report_check_digit|true, *false|
|&nbsp;&nbsp; - CodeNum|decoder_uk_plessey_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_uk_plessey_num_fixed|true, *false|
|**RSS**|decoder_rss|*true, false|
|&nbsp;&nbsp; - Transmit Ai|decoder_rss_report_ai|true, *false|
|&nbsp;&nbsp; - CodeNum|decoder_rss_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_rss_num_fixed|true, *false|
|**Standard 2 of 5**|decoder_standard_2of5|true, *false|
|&nbsp;&nbsp; - Check Digit Verification|decoder_standard_2of5_check_digit|true, *false|
|&nbsp;&nbsp; - Transmit Check Digit|decoder_standard_2of5_report_check_digit|true, *false|
|&nbsp;&nbsp; - Length1|decoder_standard_2of5_length1|1-127 (*6)|
|&nbsp;&nbsp; - Length2|decoder_standard_2of5_length2|1-127 (*127)|
|&nbsp;&nbsp; - CodeNum|decoder_standard_2of5_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_standard_2of5_num_fixed|true, *false|
|**UCC/EAN-128**|decoder_ean128|*true, false|
|&nbsp;&nbsp; - Length1|decoder_ean128_length1|1-127 (*1)|
|&nbsp;&nbsp; - Length2|decoder_ean128_length2|1-127 (*127)|
|&nbsp;&nbsp; - CodeNum|decoder_ean128_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_ean128_num_fixed|true, *false|
|**Grid Matrix**|decoder_grid_matrix|true, *false|
|&nbsp;&nbsp; - Length1|decoder_grid_matrix_length1|1-2751 (*1)|
|&nbsp;&nbsp; - Length2|decoder_grid_matrix_length2|1-2751 (*2751)|
|&nbsp;&nbsp; - CloseECI|decoder_grid_matrix_close_eci|*true, false|
|**DotCode**|decoder_dotcode|*true, false|
|**China Post**|decoder_china_post|true, *false|
|&nbsp;&nbsp; - Check Digit Verification|decoder_china_post_check_digit|true, *false|
|&nbsp;&nbsp; - Transmit Check Digit|decoder_china_post_report_check_digit|true, *false|
|&nbsp;&nbsp; - Length1|decoder_china_post_length1|1-127 (*1)|
|&nbsp;&nbsp; - Length2|decoder_china_post_length2|1-127 (*127)|
|&nbsp;&nbsp; - CodeNum|decoder_china_post_code_num|1-127 (*1)|
|&nbsp;&nbsp; - NumFixed|decoder_china_post_num_fixed|true, *false|
|**USPS Intelligent Mail**|decoder_usps_itlgt_mail|true, *false|
|**KIX Code**|decoder_kixcode|true, *false|
|**Royal Mail Customer Bar Code**|decoder_rm4scc|true, *false|
|**OCR**|decoder_ocr|true, *false|