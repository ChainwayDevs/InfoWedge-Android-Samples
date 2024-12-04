# InfoWedge API Programming Guide

> v1.6, 2024-12-04

## Table of Contents

- [Overview](#overview)
    - [Example Code](#example-code)
    - [Send Intent](#send-intent)
    - [Receive Results](#receive-results)
    - [Single Broadcast, Multiple Commands](#single-broadcast-multiple-commands)
    - [Receive Command Execution Results](#receive-command-execution-results)
    - [InfoWedge API List](#infowedge-api-list)
- [Query Interfaces](#query-interfaces)
    - [Get Version Info](#get-version-info)
    - [Get Active Profile](#get-active-profile)
    - [Get InfoWedge Status](#get-infowedge-status)
- [Configuration Interfaces](#configuration-interfaces)
    - [Create Profile](#create-profile)
    - [Delete Profile](#delete-profile)
    - [Restore Config](#restore-config)
    - [Set Config](#set-config)
        - [Set Main Parameters](#set-main-parameters)
        - [Set DCP Parameters](#set-dcp-parameters)
        - [Set Barcode Parameters](#set-barcode-parameters)
        - [Set RFID Parameters](#set-rfid-parameters)
        - [Set GS1 Formatting Parameters](#set-gs1-formatting-parameters)
        - [Set Basic Data Formatting (BDF) Parameters](#set-basic-data-formatting-bdf-parameters)
        - [Set Keystroke Output Parameters](#set-keystroke-output-parameters)
        - [Set Intent Output Parameters](#set-intent-output-parameters)
        - [Set IP Output Parameters](#set-ip-output-parameters)
        - [Set Clipboard Output Parameters](#set-clipboard-output-parameters)
        - [Set Multiple Modules](#set-multiple-modules)
- [Operation Interfaces](#operation-interfaces)
    - [Enable or Disable InfoWedge](#enable-or-disable-infowedge)
    - [Soft Scan Trigger](#soft-scan-trigger)
    - [Soft RFID Trigger](#soft-rfid-trigger)
- [Barcode Settings API Naming](#barcode-settings-api-naming)

---

## Overview

InfoWedge API mainly operates through Android's Intent - specific commands that other applications can use to control InfoWedge without directly accessing InfoWedge's UI.

### Example Code

Example code can be found in the following repositories:

- [GitHub](https://github.com/ChainwayDevs/InfoWedge-Android-Samples.git)
- [Gitee](https://gitee.com/chainwaydevs/InfoWedge-Android-Samples.git)

### Send Intent

The InfoWedge API is called by sending a broadcast. The following example retrieves the version number of InfoWedge:

```java
// Send broadcast
Intent i = new Intent();
i.setAction("com.symbol.infowedge.api.ACTION");
i.putExtra("com.symbol.infowedge.api.GET_VERSION_INFO", "");
sendBroadcast(i);
```

### Receive Results

To get the results of an InfoWedge API call, you need to register a broadcast receiver. The following example reads the version information from the reply:

```java
// Register broadcast receiver
void registerReceivers() {
    IntentFilter filter = new IntentFilter();
    filter.addAction("com.symbol.infowedge.api.RESULT_ACTION");
    filter.addCategory("android.intent.category.DEFAULT");
    registerReceiver(resultBroadcastReceiver, filter);
}

// Broadcast receiver for receiving command results
private BroadcastReceiver resultBroadcastReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Version Info
        if (intent.hasExtra("com.symbol.infowedge.api.RESULT_GET_VERSION_INFO")) {
            Bundle res = intent.getBundleExtra("com.symbol.infowedge.api.RESULT_GET_VERSION_INFO");
            String infoWedgeVersion = res.getString("INFOWEDGE");
            Log.d(TAG, "version: " + infoWedgeVersion);
        }
    }
};
```

### Single Broadcast, Multiple Commands

When sending a command broadcast, you can add multiple extras to the intent, allowing one broadcast to execute multiple commands. For example, the following commands retrieve the InfoWedge version number and the current active profile:

```java
// Send broadcast
Intent i = new Intent();
i.setAction("com.symbol.infowedge.api.ACTION");
i.putExtra("com.symbol.infowedge.api.GET_VERSION_INFO", "");
i.putExtra("com.symbol.infowedge.api.GET_ACTIVE_PROFILE", "");
sendBroadcast(i);

// Broadcast receiver for receiving command results
private BroadcastReceiver resultBroadcastReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Version Info
        if (intent.hasExtra("com.symbol.infowedge.api.RESULT_GET_VERSION_INFO")) {
            Bundle res = intent.getBundleExtra("com.symbol.infowedge.api.RESULT_GET_VERSION_INFO");
            String infoWedgeVersion = res.getString("INFOWEDGE");
            Log.d(TAG, "version: " + infoWedgeVersion);
        }

        // Current Active Profile
        if (intent.hasExtra("com.symbol.infowedge.api.RESULT_GET_ACTIVE_PROFILE")) {
            String activeProfile = intent.getStringExtra("com.symbol.infowedge.api.RESULT_GET_ACTIVE_PROFILE");
            Log.d(TAG, "active profile: " + activeProfile);
        }
    }
};
```

### Receive Command Execution Results

Some commands are operational and do not return results by default. To obtain the results of these operations, you need to include `SEND_RESULT` and `COMMAND_IDENTIFIER` in the broadcast. `COMMAND_IDENTIFIER` is a user-defined command ID string, which will also be included in the returned operation results. The returned results include the following parameters:

- **RESULT** [String] - Operation result, `SUCCESS` or `FAILURE`
- **COMMAND** [String] - Operation command
- **COMMAND_IDENTIFIER** [String] - Command ID
- **RESULT_INFO** [Bundle] - Execution result parameters, different commands may have different result parameters

```java
// Send broadcast
Intent i = new Intent();
i.setAction("com.symbol.infowedge.api.ACTION");
i.putExtra("com.symbol.infowedge.api.CREATE_PROFILE", "NewProfile");
i.putExtra("SEND_RESULT", "true");  // Request result broadcast
i.putExtra("COMMAND_IDENTIFIER", "1234");   // Command ID
sendBroadcast(i);

// Broadcast receiver for receiving command results
private BroadcastReceiver resultBroadcastReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        StringBuilder sb = new StringBuilder();

        // Output all result parameters
        for (String key : extras.keySet()) {
            sb.append(key + ": " + extras.get(key) + "\n");
        }
        sb.append("---------------\n");

        // Command name, command ID, command result
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

        // Execution result information
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

### InfoWedge API List

| Category              | Command                  | Command Name                 | Description                       |
|:---------------------|:-------------------------|:-----------------------------|:----------------------------------|
| Query Interfaces     | Get Active Profile       | GET_ACTIVE_PROFILE           | Retrieve the current active profile |
|                      | Get Status               | GET_INFOWEDGE_STATUS         | Retrieve the enabled status of InfoWedge |
|                      | Get Version Info         | GET_VERSION_INFO             | Retrieve version information of related modules |
| Configuration Interfaces | Create Profile        | CREATE_PROFILE               | Create a default profile          |
|                      | Delete Profile           | DELETE_PROFILE               | Delete one or more profiles       |
|                      | Restore Config           | RESTORE_CONFIG               | Restore configuration to factory settings |
|                      | Set Config               | SET_CONFIG                   | Set the configurations of a profile      |
| Operation Interfaces | Enable/Disable InfoWedge | ENABLE_INFOWEDGE             | Enable or disable the InfoWedge service |
|                      | Trigger Scanner Scan     | SOFT_SCAN_TRIGGER            | Start/Stop/Toggle scanner scanning |
|                      | Trigger RFID Scan        | SOFT_RFID_TRIGGER            | Start/Stop/Toggle RFID scanning    |

## Query Interfaces

### Get Version Info

```java
// Send broadcast
Intent i = new Intent();
i.setAction("com.symbol.infowedge.api.ACTION");
i.putExtra("com.symbol.infowedge.api.GET_VERSION_INFO", "");
sendBroadcast(i);

// Broadcast receiver for receiving command results
private BroadcastReceiver resultBroadcastReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Version Info
        if (intent.hasExtra("com.symbol.infowedge.api.RESULT_GET_VERSION_INFO")) {
            Bundle res = intent.getBundleExtra("com.symbol.infowedge.api.RESULT_GET_VERSION_INFO");
            String infoWedgeVersion = res.getString("INFOWEDGE");   // InfoWedge application version
            Log.d(TAG, "version: " + infoWedgeVersion);
        }
    }
};
```

### Get Active Profile

```java
// Send broadcast
Intent i = new Intent();
i.setAction("com.symbol.infowedge.api.ACTION");
i.putExtra("com.symbol.infowedge.api.GET_ACTIVE_PROFILE", "");
sendBroadcast(i);

// Broadcast receiver for receiving command results
private BroadcastReceiver resultBroadcastReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Active Profile
        if (intent.hasExtra("com.symbol.infowedge.api.RESULT_GET_ACTIVE_PROFILE")) {
            String activeProfile = intent.getStringExtra("com.symbol.infowedge.api.RESULT_GET_ACTIVE_PROFILE");
            Log.d(TAG, "active profile: " + activeProfile);
        }
    }
};
```

### Get InfoWedge Status

```java
// Send broadcast
Intent i = new Intent();
i.setAction("com.symbol.infowedge.api.ACTION");
i.putExtra("com.symbol.infowedge.api.GET_INFOWEDGE_STATUS", "");
sendBroadcast(i);

// Broadcast receiver for receiving command results
private BroadcastReceiver resultBroadcastReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        // InfoWedge Status
        if (intent.hasExtra("com.symbol.infowedge.api.RESULT_GET_INFOWEDGE_STATUS")) {
            // Status value: "ENABLED" or "DISABLED"
            String infoWedgeStatus = intent.getStringExtra("com.symbol.infowedge.api.RESULT_GET_INFOWEDGE_STATUS");
            Log.d(TAG, "Info Wedge status: " + infoWedgeStatus);
        }
    }
};
```

## Configuration Interfaces

### Create Profile

```java
// Send broadcast
Intent i = new Intent();
i.setAction("com.symbol.infowedge.api.ACTION");
i.putExtra("com.symbol.infowedge.api.CREATE_PROFILE", "NewProfile");
sendBroadcast(i);
```

**Error Codes:**

- **PROFILE_NAME_EMPTY** - Profile name is empty
- **PROFILE_ALREADY_EXISTS** - Profile already exists

### Delete Profile

```java
// Send broadcast
Intent i = new Intent();
i.setAction("com.symbol.infowedge.api.ACTION");
String[] values = {"NewProfile"};
i.putExtra("com.symbol.infowedge.api.DELETE_PROFILE", values);
sendBroadcast(i);
```

**Error Codes:**

- **PROFILE_NAME_EMPTY** - Profile name is empty
- **OPERATION_NOT_ALLOWED** - Operation not allowed, e.g., deleting the default profile
- **PROFILE_NOT_FOUND** - Profile to be deleted does not exist

### Restore Config

```java
// Send broadcast
Intent i = new Intent();
i.setAction("com.symbol.infowedge.api.ACTION");
i.putExtra("com.symbol.infowedge.api.RESTORE_CONFIG", "");
sendBroadcast(i);
```

### Set Config

#### Set Main Parameters

```java
// Main parameters
Bundle bMain = new Bundle();
bMain.putString("PROFILE_NAME", "NewProfile");  // Profile name
bMain.putString("PROFILE_ENABLED", "true");     // Enable this profile
bMain.putString("CONFIG_MODE", "CREATE_IF_NOT_EXIST");  // Create if profile does not exist

// Set associated applications
Bundle bundleApp1 = new Bundle();
bundleApp1.putString("PACKAGE_NAME", "change.to.your.app.package"); // Associated application package name
bundleApp1.putStringArray("ACTIVITY_LIST", new String[]{"change.to.your.app.package.MainActivity", "change.to.your.app.package.About"});    // List of associated activities

Bundle bundleApp2 = new Bundle();
bundleApp2.putString("PACKAGE_NAME", "another.app.package");    // Another associated application package name
bundleApp2.putStringArray("ACTIVITY_LIST", new String[]{"*"});  // * means associate all activities of the application

// Add associated applications to main parameters
bMain.putParcelableArray("APP_LIST", new Bundle[] {
    bundleApp1,
    bundleApp2
});

Intent i = new Intent();
i.setAction("com.symbol.infowedge.api.ACTION");
i.putExtra("com.symbol.infowedge.api.SET_CONFIG", bMain);
sendBroadcast(i);
```

**CONFIG_MODE** parameter values:

- **CREATE_IF_NOT_EXIST** - Create if profile does not exist
- **OVERWRITE** - Overwrite existing profile with default values before setting
- **UPDATE** - Merge configuration into an existing profile

#### Set DCP Parameters

```java
// Main parameters
Bundle bMain = new Bundle();
bMain.putString("PROFILE_NAME", "NewProfile");  // Profile name
bMain.putString("PROFILE_ENABLED", "true"); // Enable this profile
bMain.putString("CONFIG_MODE", "UPDATE");   // Merge configuration into an existing profile
bMain.putString("MEMORY_PROFILE", "false");  // Whether it's a temporary profile. Temporary profiles exist in memory and are lost after system reboot.

// Set DCP
Bundle bConfig = new Bundle();
bConfig.putString("PLUGIN_NAME", "DCP");    // Set type: DCP
bConfig.putString("RESET_CONFIG", "true"); // Reset existing DCP configuration

// Set DCP parameters (optional if using default values)
Bundle bParams = new Bundle();
bParams.putString("dcp_input_enabled", "true");   // Enable DCP
bParams.putString("dcp_start_in", "BUTTON");   // Start mode: FULLSCREEN, BUTTON, BUTTON_ONLY
bParams.putString("dcp_pos_x", "50"); // X coordinate of the floating button position, rightmost coordinate is 0
bParams.putString("dcp_pos_y", "50"); // Y coordinate of the floating button position, bottom coordinate is 0

// Add to main parameters
bConfig.putBundle("PARAM_LIST", bParams);
bMain.putBundle("PLUGIN_CONFIG", bConfig);

// Send broadcast
Intent i = new Intent();
i.setAction("com.symbol.infowedge.api.ACTION");
i.putExtra("com.symbol.infowedge.api.SET_CONFIG", bMain);
sendBroadcast(i);
```

**dcp_start_in** parameter values:

- **FULLSCREEN** - Full-screen mode
- **BUTTON** - Button mode
- **BUTTON_ONLY** - Button-only mode

#### Set Barcode Parameters

```java
// Main parameters
Bundle bMain = new Bundle();
bMain.putString("PROFILE_NAME", "NewProfile");  // Profile name
bMain.putString("PROFILE_ENABLED", "true"); // Enable this profile
bMain.putString("CONFIG_MODE", "UPDATE");   // Merge configuration into an existing profile

// Set barcode
Bundle bConfig = new Bundle();
bConfig.putString("PLUGIN_NAME", "BARCODE");    // Set type: barcode
bConfig.putString("RESET_CONFIG", "true");     // Reset existing barcode configuration

// Set barcode parameters (optional if using default values)
Bundle bParams = new Bundle();
bParams.putString("barcode_enabled", "true");   // Enable barcode
bParams.putString("barcode_trigger_keys", "LEFT_TRIGGER,CENTER_TRIGGER,RIGHT_TRIGGER");  // Trigger keys for scanning, multiple keys separated by commas
bParams.putString("barcode_trigger_mode", "0");   // Trigger mode: 0-Press and Release, 1-Press and Continue, 2-Trigger, 3-Timed Release
bParams.putString("charset_name", "Auto");   // Charset for decoding: Auto, UTF-8, GBK, GB18030, ISO-8859-1, Shift_JIS
bParams.putString("success_audio_type", "2"); // Play beep sound on successful scan
bParams.putString("failure_audio", "false"); // Play beep sound on failed scan
bParams.putString("vibrate", "false"); // Vibrate on successful scan
// Enable/Disable decoders
bParams.putString("decoder_code11", "true");    // Enable Code11 decoder
bParams.putString("decoder_code128", "false");  // Disable Code128 decoder
// Enable/Disable/Restore default for all decoders
// bParams.putString("decoder_all_symbology", "true");      // Enable all decoders
// bParams.putString("decoder_all_symbology", "false");     // Disable all decoders
// bParams.putString("decoder_all_symbology", "default");   // Restore default settings for all decoders
// Set decoder parameters
bParams.putString("decoder_code128_length1", "1");  // Set Code128 decoder length 1
bParams.putString("decoder_code128_length2", "40"); // Set Code128 decoder length 2
bParams.putString("decoder_upca_report_check_digit", "true");   // Transmit UPC-A Check Digit
bParams.putString("decoder_ean13_report_check_digit", "true");  // Transmit EAN-13 Check Digit

// Add to main parameters
bConfig.putBundle("PARAM_LIST", bParams);
bMain.putBundle("PLUGIN_CONFIG", bConfig);

// Send broadcast
Intent i = new Intent();
i.setAction("com.symbol.infowedge.api.ACTION");
i.putExtra("com.symbol.infowedge.api.SET_CONFIG", bMain);
sendBroadcast(i);
```

**barcode_trigger_keys** parameter values:

- **LEFT_TRIGGER** - Left scan key
- **CENTER_TRIGGER** - Center scan key
- **RIGHT_TRIGGER** - Right scan key
- **SCAN** - Scan key (for devices without separate left, center, and right scan keys)
- **GUN_TRIGGER** - Handle scan key

**success_audio_type** parameter values:

- **0** - None
- **1** - Du
- **2** - Di

**decoder types and parameters** values:

For more decoder types and parameter formats, refer to [here](#barcode-settings-api-naming), using the **API Naming** column in the table.

#### Set RFID Parameters

```java
// Main parameters
Bundle bMain = new Bundle();
bMain.putString("PROFILE_NAME", "NewProfile");  // Profile name
bMain.putString("PROFILE_ENABLED", "true"); // Enable this profile
bMain.putString("CONFIG_MODE", "UPDATE");   // Merge configuration into an existing profile

// Set RFID
Bundle bConfig = new Bundle();
bConfig.putString("PLUGIN_NAME", "RFID");   // Set type: RFID
bConfig.putString("RESET_CONFIG", "true");  // Reset existing configuration

// Set RFID parameters (optional if using default values)
Bundle bParams = new Bundle();
bParams.putString("rfid_input_enabled", "true");   // Enable RFID
bParams.putString("rfid_trigger_keys", "LEFT_TRIGGER,CENTER_TRIGGER,RIGHT_TRIGGER");  // Trigger keys for scanning, multiple keys separated by commas
bParams.putString("rfid_trigger_mode", "0");        // Trigger mode: 0-Immediate, 1-Continuous
bParams.putString("rfid_beeper_enable", "true");    // Beep sound on tag read
bParams.putString("rfid_output_mode", "0"); // Tag output mode, 0-Continuous(Single Tag), 1-Continuous (Timed), 2-One-time Output, 3-RSSI first
bParams.putString("rfid_timed_output_interval", "200"); // Timed output interval (ms)
bParams.putString("rfid_filter_duplicate_tags", "true"); // Filter duplicate tags
bParams.putString("rfid_antenna_transmit_power", "30"); // Antenna transmit power, range 5~30 (dBm)
bParams.putString("rfid_frequency_mode", "2"); // Frequency mode, see table below
bParams.putString("rfid_tag_read_duration", "2000"); // Tag read duration, range: 0, 100~60000 (ms)
bParams.putString("rfid_separator_to_tags", "\\n"); // Separator string between multiple tags
bParams.putString("rfid_tag_output_data_format", "EPC"); // Set output tag data format, see description below
bParams.putString("rfid_epc_user_data_type", "0"); // Data format for EPC and USER memory banks, 0-Hex, 1-ASCII
bParams.putString("rfid_pre_filter_enable", "true"); // Enable pre filters
bParams.putString("rfid_pre_filter_memory_bank", "0"); // Select memory bank for filtering, 1-EPC, 2-TID, 3-User
bParams.putString("rfid_pre_filter_offset", "4"); // Start byte for pre-filtering comparison
bParams.putString("rfid_pre_filter_tag_pattern", "E012"); // Hexadecimal string for pre-filtering comparison
bParams.putString("rfid_post_filter_enable", "true"); // Enable post filters
bParams.putString("rfid_post_filter_no_of_tags_to_read", "1"); // Number of tags to read
bParams.putString("rfid_post_filter_rssi", "-80"); // RSSI threshold. Range: -100~0 (dBm)

// Add to main parameters
bConfig.putBundle("PARAM_LIST", bParams);
bMain.putBundle("PLUGIN_CONFIG", bConfig);

// Send broadcast
Intent i = new Intent();
i.setAction("com.symbol.infowedge.api.ACTION");
i.putExtra("com.symbol.infowedge.api.SET_CONFIG", bMain);
sendBroadcast(i);
```

##### RFID Operating Frequency

| Parameter Value | Frequency Range (MHz) | Region/Standard |
| :-: | :-: | :-: |
| 1 | 840 ~ 845 | China Standard |
| 2 | 902 ~ 928 | Enhanced China Standard |
| 4 | 865 ~ 868 | ETSI Standard |
| 8 | 902 ~ 928 | US Standard |
| 22 | 917 ~ 923 | Korea |
| 50 | 916.7 ~ 920.9 | Japan |
| 51 | 915.4 ~ 919 | South Africa |
| 52 | 922 ~ 928 | Taiwan |
| 53 | 918 ~ 923 | Vietnam |
| 54 | 915 ~ 928 | Peru |
| 55 | 866.0 ~ 867.6 | Russia |
| 59 | 919 ~ 923 | Malaysia |
| 60 | 902 ~ 907.5, 915 ~ 928 | Brazil |
| 61 | 916 ~ 920 | New ETSI |
| 62 | 920 ~ 926 | Australia |
| 63 | 923 ~ 925 | Indonesia |
| 64 | 915 ~ 917 | Israel |
| 65 | 920 ~ 925 | Hong Kong |
| 66 | 920 ~ 928 | New Zealand |
| 68 | 920 ~ 925 | Singapore |
| 69 | 920 ~ 925 | Thailand |

##### Output Tag Content Format

You can output tag `TID`, `EPC`, `USER`, `RSSI`, `PC` . For example, if the format is set to: `TID;EPC;RSSI`, the final output will be the tag's `TID`, `EPC`, and `signal strength` data, separated by semicolons: `E2003412013A03000109B2B8;E20000194859503031323334;-60.70`.

Additionally, you can output partial content, for example, if the format is set to: `TID[4,3]-EPC[0,4]:USER[0,8]`, the final output will be: `013A03-E2000019:3031323334353637`.

Note: When outputting `USER` memory bank content, you must specify the byte range, i.e., the `USER` field must use the `USER[m,n]` format.

#### Set GS1 Formatting Parameters

```java
// Main parameters
Bundle bMain = new Bundle();
bMain.putString("PROFILE_NAME", "NewProfile");  // Profile name
bMain.putString("PROFILE_ENABLED", "true"); // Enable this profile
bMain.putString("CONFIG_MODE", "UPDATE");   // Merge configuration into an existing profile
bMain.putString("MEMORY_PROFILE", "false");  // Whether it's a temporary profile. Temporary profiles exist in memory and are lost after system reboot.

// Set GS1
Bundle bConfig = new Bundle();
bConfig.putString("PLUGIN_NAME", "GS1");    // Set type: GS1 code Formatting
bConfig.putString("RESET_CONFIG", "true"); // Reset existing configuration

// Set GS1 parameters (optional if using default values)
Bundle bParams = new Bundle();
bParams.putString("gs1_enabled", "true");   // Enable GS1 code Formatting
bParams.putString("gs1_separate", "false");   // Disable separate code format
bParams.putString("gs1_new_line", "true");   // Enable separate a new line
bParams.putString("gs1_gs_format", "0"); // GS(0x1D) character handling: 0-Remove, 1-Reserve, 2-Replace with other string
bParams.putString("gs1_gs_replace", "[GS]");    // replace GS string

// Add to main parameters
bConfig.putBundle("PARAM_LIST", bParams);
bMain.putBundle("PLUGIN_CONFIG", bConfig);

// Send broadcast
Intent i = new Intent();
i.setAction("com.symbol.infowedge.api.ACTION");
i.putExtra("com.symbol.infowedge.api.SET_CONFIG", bMain);
sendBroadcast(i);
```

#### Set Basic Data Formatting (BDF) Parameters

```java
// Main parameters
Bundle bMain = new Bundle();
bMain.putString("PROFILE_NAME", "NewProfile");  // Profile name
bMain.putString("PROFILE_ENABLED", "true"); // Enable this profile
bMain.putString("CONFIG_MODE", "UPDATE");   // Merge configuration into an existing profile
bMain.putString("MEMORY_PROFILE", "false");  // Whether it's a temporary profile. Temporary profiles exist in memory and will not appear in the InfoWedge main configuration list, and are lost after system reboot.

// Set BDF
Bundle bConfig = new Bundle();
bConfig.putString("PLUGIN_NAME", "BDF");    // Set type: BDF
bConfig.putString("RESET_CONFIG", "true"); // Reset existing BDF configuration

// Set BDF parameters (optional if using default values)
Bundle bParams = new Bundle();
bParams.putString("bdf_enabled", "true");   // Enable BDF
bParams.putString("bdf_prefix", "A");   // Prefix to data
bParams.putString("bdf_suffix", "B");   // Suffix to data
bParams.putString("bdf_send_tab", "true"); // Send TAB key
bParams.putString("bdf_send_enter", "true"); // Send ENTER key
bParams.putString("bdf_delete_start", "1"); // Delete leading characters
bParams.putString("bdf_delete_end", "2");    // Delete trailing characters
bParams.putString("bdf_delete_string", "DEL");  // Delete specified string

// Add to main parameters
bConfig.putBundle("PARAM_LIST", bParams);
bMain.putBundle("PLUGIN_CONFIG", bConfig);

// Send broadcast
Intent i = new Intent();
i.setAction("com.symbol.infowedge.api.ACTION");
i.putExtra("com.symbol.infowedge.api.SET_CONFIG", bMain);
sendBroadcast(i);
```

### Set Keystroke Output Parameters

```java
// Main parameters
Bundle bMain = new Bundle();
bMain.putString("PROFILE_NAME", "NewProfile");  // Profile name
bMain.putString("PROFILE_ENABLED", "true"); // Enable this profile
bMain.putString("CONFIG_MODE", "UPDATE");   // Merge configuration into an existing profile

// Set keystroke output
Bundle bConfig = new Bundle();
bConfig.putString("PLUGIN_NAME", "KEYSTROKE");    // Set type: keystroke
bConfig.putString("RESET_CONFIG", "true"); // Reset existing keystroke output configuration

// Set keystroke output parameters (optional if using default values)
Bundle bParams = new Bundle();
bParams.putString("keystroke_output_enabled", "true");   // Enable keystroke output
bParams.putString("keystroke_output_type", "0");   // Set keystroke output type, 0-Append on cursor, 1-Simulate keystroke, 2-Replace on cursor

// Add to main parameters
bConfig.putBundle("PARAM_LIST", bParams);
bMain.putBundle("PLUGIN_CONFIG", bConfig);

// Send broadcast
Intent i = new Intent();
i.setAction("com.symbol.infowedge.api.ACTION");
i.putExtra("com.symbol.infowedge.api.SET_CONFIG", bMain);
sendBroadcast(i);
```

#### Set Intent Output Parameters

```java
// Main parameters
Bundle bMain = new Bundle();
bMain.putString("PROFILE_NAME", "NewProfile");  // Profile name
bMain.putString("PROFILE_ENABLED", "true"); // Enable this profile
bMain.putString("CONFIG_MODE", "UPDATE");   // Merge configuration into an existing profile

// Set Intent output
Bundle bConfig = new Bundle();
bConfig.putString("PLUGIN_NAME", "INTENT");    // Set type: Intent
bConfig.putString("RESET_CONFIG", "true"); // Reset existing broadcast output configuration

// Set Intent parameters (optional if using default values)
Bundle bParams = new Bundle();
bParams.putString("intent_output_enabled", "true");   // Enable broadcast output
bParams.putString("intent_action", "com.infowedge.data");   // Set intent action
bParams.putString("intent_data", "data_string");   // Set data output name

// Add to main parameters
bConfig.putBundle("PARAM_LIST", bParams);
bMain.putBundle("PLUGIN_CONFIG", bConfig);

// Send broadcast
Intent i = new Intent();
i.setAction("com.symbol.infowedge.api.ACTION");
i.putExtra("com.symbol.infowedge.api.SET_CONFIG", bMain);
sendBroadcast(i);
```

#### Set IP Output Parameters

```java
// Main parameters
Bundle bMain = new Bundle();
bMain.putString("PROFILE_NAME", "NewProfile");  // Profile name
bMain.putString("PROFILE_ENABLED", "true"); // Enable this profile
bMain.putString("CONFIG_MODE", "UPDATE");   // Merge configuration into an existing profile

// Set IP output
Bundle bConfig = new Bundle();
bConfig.putString("PLUGIN_NAME", "IP");    // Set type: IP
bConfig.putString("RESET_CONFIG", "true"); // Reset existing IP output configuration

// Set IP parameters (optional if using default values)
Bundle bParams = new Bundle();
bParams.putString("ip_output_enabled", "true"); // Enable broadcast output
bParams.putString("ip_output_protocol", "UDP"); // Protocol type: TCP, UDP
bParams.putString("ip_output_address", "192.168.0.100"); // IP address
bParams.putString("ip_output_port", "55555"); // Port number

// Add to main parameters
bConfig.putBundle("PARAM_LIST", bParams);
bMain.putBundle("PLUGIN_CONFIG", bConfig);

// Send broadcast
Intent i = new Intent();
i.setAction("com.symbol.infowedge.api.ACTION");
i.putExtra("com.symbol.infowedge.api.SET_CONFIG", bMain);
sendBroadcast(i);
```

#### Set Clipboard Output Parameters

```java
// Main parameters
Bundle bMain = new Bundle();
bMain.putString("PROFILE_NAME", "NewProfile");  // Profile name
bMain.putString("PROFILE_ENABLED", "true"); // Enable this profile
bMain.putString("CONFIG_MODE", "UPDATE");   // Merge configuration into an existing profile

// Set clipboard output
Bundle bConfig = new Bundle();
bConfig.putString("PLUGIN_NAME", "CLIPBOARD");    // Set type: clipboard
bConfig.putString("RESET_CONFIG", "true"); // Reset existing clipboard output configuration

// Set clipboard output parameters (optional if using default values)
Bundle bParams = new Bundle();
bParams.putString("clipboard_output_enabled", "true");   // Enable clipboard output

// Add to main parameters
bConfig.putBundle("PARAM_LIST", bParams);
bMain.putBundle("PLUGIN_CONFIG", bConfig);

// Send broadcast
Intent i = new Intent();
i.setAction("com.symbol.infowedge.api.ACTION");
i.putExtra("com.symbol.infowedge.api.SET_CONFIG", bMain);
sendBroadcast(i);
```

#### Set Multiple Modules

```java
// Main parameters
Bundle bMain = new Bundle();
bMain.putString("PROFILE_NAME", "NewProfile");  // Profile name
bMain.putString("PROFILE_ENABLED", "true"); // Enable this profile
bMain.putString("CONFIG_MODE", "CREATE_IF_NOT_EXIST");  // Create if profile does not exist
bMain.putString("RESET_CONFIG", "true");  // Reset configuration if profile exists
bMain.putString("MEMORY_PROFILE", "false");  // Whether it's a temporary profile. Temporary profiles exist in memory and are lost after system reboot.

// Set associated applications
Bundle bundleApp = new Bundle();
bundleApp.putString("PACKAGE_NAME", "com.chainway.infowedge.demo");    // Associated application package name
bundleApp.putStringArray("ACTIVITY_LIST", new String[]{"*"});  // * means associate all activities of the application
bMain.putParcelableArray("APP_LIST", new Bundle[] { bundleApp });

// Set barcode
Bundle bBarcodeConfig = new Bundle();
bBarcodeConfig.putString("PLUGIN_NAME", "BARCODE");    // Set type: barcode
bBarcodeConfig.putString("RESET_CONFIG", "true");     // Reset existing barcode configuration
Bundle bBarcodeParams = new Bundle();
bBarcodeParams.putString("barcode_trigger_mode", "1");   // Trigger mode: 0-Press and Release, 1-Press and Continue, 2-Trigger, 3-Timed Release
bBarcodeParams.putString("failure_audio", "true"); // Play beep sound on failed scan
bBarcodeParams.putString("vibrate", "true"); // Vibrate on successful scan
bBarcodeParams.putString("decoder_code11", "true");    // Enable Code11 decoder
bBarcodeParams.putString("decoder_code128", "false");  // Disable Code128 decoder
bBarcodeConfig.putBundle("PARAM_LIST", bBarcodeParams);

// Set BDF
Bundle bBdfConfig = new Bundle();
bBdfConfig.putString("PLUGIN_NAME", "BDF");    // Set type: BDF
bBdfConfig.putString("RESET_CONFIG", "true"); // Reset existing BDF configuration
Bundle bBdfParams = new Bundle();
bBdfParams.putString("bdf_enabled", "true");   // Enable BDF
bBdfParams.putString("bdf_prefix", "A");   // Prefix to data
bBdfConfig.putBundle("PARAM_LIST", bBdfParams);

// Set Intent output
Bundle bIntentConfig = new Bundle();
bIntentConfig.putString("PLUGIN_NAME", "INTENT");    // Set type: Intent
bIntentConfig.putString("RESET_CONFIG", "true");   // Reset existing broadcast output configuration
Bundle bIntentParams = new Bundle();
bIntentParams.putString("intent_output_enabled", "true");   // Enable broadcast output
bIntentConfig.putBundle("PARAM_LIST", bIntentParams);

// Set keystroke output
Bundle bKeystrokeConfig = new Bundle();
bKeystrokeConfig.putString("PLUGIN_NAME", "KEYSTROKE");    // Set type: keystroke
bKeystrokeConfig.putString("RESET_CONFIG", "true"); // Reset existing keystroke output configuration
Bundle bKeystrokeParams = new Bundle();
bKeystrokeParams.putString("keystroke_output_enabled", "true");   // Enable keystroke output
bKeystrokeParams.putString("keystroke_output_type", "0");   // Set keystroke output type, 0-Append on cursor, 1-Simulate keystroke, 2-Replace on cursor
bKeystrokeConfig.putBundle("PARAM_LIST", bKeystrokeParams);

// Set clipboard output
Bundle bClipboardConfig = new Bundle();
bClipboardConfig.putString("PLUGIN_NAME", "CLIPBOARD");    // Set type: clipboard
bClipboardConfig.putString("RESET_CONFIG", "true"); // Reset existing clipboard output configuration
Bundle bClipboardParams = new Bundle();
bClipboardParams.putString("clipboard_output_enabled", "true");   // Enable clipboard output
bClipboardConfig.putBundle("PARAM_LIST", bClipboardParams);

ArrayList<Bundle> bundlePluginConfig = new ArrayList<>();
bundlePluginConfig.add(bBarcodeConfig);
bundlePluginConfig.add(bBdfConfig);
bundlePluginConfig.add(bIntentConfig);
bundlePluginConfig.add(bKeystrokeConfig);
bundlePluginConfig.add(bClipboardConfig);
bMain.putParcelableArrayList("PLUGIN_CONFIG", bundlePluginConfig);

// Send broadcast
Intent i = new Intent();
i.setAction("com.symbol.infowedge.api.ACTION");
i.putExtra("com.symbol.infowedge.api.SET_CONFIG", bMain);
sendBroadcast(i);
```

## Operation Interfaces

### Enable or Disable InfoWedge

```java
// Send broadcast
Intent i = new Intent();
i.setAction("com.symbol.infowedge.api.ACTION");
i.putExtra("com.symbol.infowedge.api.ENABLE_INFOWEDGE", true);  // true to enable, false to disable
// Reply with execution result
i.putExtra("SEND_RESULT", "true");
i.putExtra("COMMAND_IDENTIFIER", "1234");
sendBroadcast(i);

// Broadcast receiver for receiving command results
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

**Error Codes:**

- **INFOWEDGE_ALREADY_ENABLED** - InfoWedge is already enabled
- **INFOWEDGE_ALREADY_DISABLED** - InfoWedge is already disabled

### Soft Scan Trigger

```java
// Send broadcast
Intent i = new Intent();
i.setAction("com.symbol.infowedge.api.ACTION");
i.putExtra("com.symbol.infowedge.api.SOFT_SCAN_TRIGGER", "START"); // START, STOP, TOGGLE
i.putExtra("SEND_RESULT", "true");
i.putExtra("COMMAND_IDENTIFIER", "1234");
sendBroadcast(i);
```

**Error Codes:**

- **INFOWEDGE_DISABLED** - InfoWedge is not enabled
- **PROFILE_DISABLED** - Profile is not enabled
- **INPUT_NOT_ENABLED** - Input is not enabled
- **PARAMETER_INVALID** - Invalid parameter

### Soft RFID Trigger
```java
// Send broadcast
Intent i = new Intent();
i.setAction("com.symbol.infowedge.api.ACTION");
i.putExtra("com.symbol.infowedge.api.SOFT_RFID_TRIGGER", "START"); // START, STOP, TOGGLE
i.putExtra("SEND_RESULT", "true");
i.putExtra("COMMAND_IDENTIFIER", "1234");
sendBroadcast(i);
```

**Error Codes:**

Same as [Trigger Scanner Scan](#trigger-scanner-scan) error codes

## Barcode Settings API Naming

### Zebra Decoders

| Name | API Naming | Parameter Values (default values marked with *) |
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

| Name | API Naming | Parameter Values (default values marked with *) |
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