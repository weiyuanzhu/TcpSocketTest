PATCH NOTES
================

v0.8.0
===================

###Updates:
- 0 Globalization(Multi-language)
- 1 Custom branding feature(custom logo)
- 2 BaseActivity, onResume(), load activity action bar
- 3 Update device action mode icons
- 4 Add items to device setting menu to be consistent with action mode
- 5 Add scroll bar for deviceInfoList and re-arrange layout
- 6 "userLandscape" instead of "landscape" so app can rotate now.
- 7 Add onSaveInstanceState, save current selected group and device location
       and in onCreate, re-select this device if savedInstanceStats is not null
- 8 Synchronize panel object between PanelActivity and DeviceActivity(use startActivityForResult)
- 9 Basic implementation of multi-screen support, (only Panel and Device Activity)
- 10 Tell user if no panel is selected (PanelActivity), when click on show information
- 11 Fetch and display panel report
- 12 Print and save report to PDF


###Fix:
- 1 device multi-selection bug: when check SparseBooleanArray, using wrong size
- 2 clear panelList when loading screen onResume()
- 3 PanelActivity onActivityResult, having different request code and Result_Cancelled


v 0.7.0
===================

###Updates:
- 1 Sort by faults is default for device list (also dummy devices have been added to demoMode)
- 2 Display improvements
- 3 DeviceName: ?[Click and hold to rename device] (easier for user to click)
- 4 Auto refresh now works in demo mode too
- 5 Set panel information, including panel name, contact, Tel, Mobile and passcode.
- 6 Refactor InputDialogFragment for better reuse, with a title field and type
- 7 Set inputType for different InputDialog type and hints
- 8 Implemented password validation when user access panels
- 9 Subtitle for panel/device action bar
- 10 Device list multi selection

###Fix:
- 1 Check if isDemo and if connection is null when change device name
- 2 InputDialog cancel doesn't set currentDisplayingPanel to null as expected


v 0.6.0
================

###Updates:
- 1 Device faulty is now coloured (Red for faults)
- 2 Icon for FT
- 3 Add pop-up menu for "Show loops"
- 4 Added "SearchView" Icon
- 5 Panel Info now displays faulty number by panel location(also trim spaces)
- 6 DeviceList, expand/collapse loop will now rest deviceInfoFragment page and highlight current loop
- 7 Loop (N) N is device number in the loop also a text showing number of faulty devices when loop is clicked
- 8 PanelLoadingScreen progress bar is back for displaying progress for loading panels
- 9 AutoRefresh for all device list(60 seconds) and currently selected device(5 seconds)
- 10 A stamp is displayed under device information with: AutoRefresh: and Last refreshed: 
- 11 DeviceList can be sorted now by: Faults, A-Z and unnamed
- 12 SettingActivity now has a <back navigation to navigate back to last activity who starts this.


###Fix:
- 1 Device failure faulty error, caused by a snippet of test code
- 2 NullPointAccess when accessing connectionList when PanelListFragmetn onPause()
- 3 Changed logic in DeviceinfoFragment for update device information to prevent crash for updating device information


v 0.5.0
================

##2014-06-09

###Updates:

- 1 UDP Connection for searching panels in the building
- 2 Implemented a Preference settings for users to choose options
- 3 When liveMode clicked, a dialog with list of panels will pop up for choose
- 4 User can choose to cache panel location next time when they search panels
- 5 Improved project branches(Master, Develop and Feature branches)



###Fix:
- 1 DemoMode will have duplicated panels when navigate back to loading screen and click it again.
- 2 PanelListDialog logics

v 0.4.6.1
================

###Update:

Device refresh faulty will now refresh device list icon too



v 0.4.5
================
##2014-04-28

(Project Management)Re-organize project src folders and adding Visual Paragiam diagrams in to project


v 0.4.4
================
##2014-04-21

###Update: 

- 1 (MH 4) Single click on device list or group(both expand and collapse)will close action mode if it exists.


v 0.4.3
================
##2014-04-18

###Updates

- 1 (MH 2)Long click on device will now release current action mode and start a new one;
- 2  add refresh button for device action bar
- 3 perform functionalities will refresh device faulty now.
- 4 deviceInfoFragment and device, add DT last time and lamp operation time;
- 5 DeviceInfoFragment, device address display for loop2 now displays 0-63 instead of 128-191

###Fix:

- 1 Fix  panel order of loading screen panels
- 2 (RB 6)DeviceList, loop2 devices FT will crash the app; Re-do the logic for getAddress when an item is long clicked; 

v 0.4.2
================
##2014-04-01

###Updates: 

- 1 name device,current device location will be prompt in the dialog window and cursor is at the last character.

v 0.4.1
================
##2014-03-26 (Before L&B)

###Add:

- 1 Demo Mode, will display pre-setup devices and panels;
- 2 Set device location for device, (32 chars max);
- 3 BaseAtivity which has flags and a method to check device connectivity. 
- 4 isDemo Flag for BaseActivity to check if it is in demo mode
- 5 import messageType package from N-LIght connector (Jie Yin)

###Updates: 

- 1 Change GTIN and Serial Number to apply to Mackwell global bar-code
- 2 Panel and device list, make text center_vertical
- 3 Device battery level display, added Decimal Format.
- 4 When function button clicked, action mode is not released, for better user experience
- 5 Remove version in panel list fragment
- 6 In "About", change "Mackwell N-Light Android" to "Mackwell N-Light Connect"
- 7 Set device name dialog also set the location in device list now;
- 8 Check isDemo when action clicked, it will not send out message if it is in demo mode;
- 9 Move panel IP address to panel info;
- 10 When navigate back to loading screen, in onResume(), re-enable buttons and hide progress bars
- 11 App_name and device info list descriptions
- 12 Swap imgButton on loading screen to plain button
- 13 PanelActivity can navigate back to loading screen now
- 14 Change to "Name Device Location" and "Enter"
- 15 Set location for device, both update local device list and remote device.
- 16 Add flag isLoading, 
- 17 disable button when isLoading
- 18 EmergencyStatus and EmergencyMode display
- 19 Device name can be fetch from eepRom now.(DataParser)
- 20 DeviceInfoActivity display device name



###Fix: 
- 1 MyExpandableListAdapter to have fixed headerTitle instead of get loop name;
- 2 ShowDevices for panels not working;
- 3 (RB 4)LoadingScreen demoBtn double click create duplicated panels


v 0.3
==============
##2014-03-06

###Add:

- 1 DeviceList and deviceInfo fragments split view
- 2 Device FT ST DT ID functions
- 3 Refresh button to main menu bar
- 4 Menu about (make a toast to show app_version)
- 5 Action mode for device item
- 6 Loop/devices long click, will now pop up an action bar for functions
- 7 Constants.java to store all static final value
- 8 SetCmdEnum and ToggleCmdEnum-

###Updates:

- 1 Change application theme to blue on white (Theme.Holo.Light)
- 2 Device Activity action bar title = panel's name
- 3 Add an image to device child item layout.xml and adapter
- 4 refreshStatus on PanelListFragment created
- 5 remove 3 buttons("gone") in PanelListFragment
- 6 Buttons on panelinfo fragment and image for device info as device item clicked
- 7 Device GTIN int array and device list select active background
- 8 ShowDevices to main menu
- 9 PanelListFragment extends ListFragment
- 10 passes panel object between activities
- 11 Loop() constructor to include 3 new devices for test
- 12 DeviceListActivity to check whether loop1&2 are null
- 13 Panel gtinArray to store GTIN integer in an array so it can be passed via Parcel
- 14 images to .png and transparent background
- 15 LoadingScreenActivity, load panels when start and put panelList in the intent and pass to next activity;
- 16 Panel, Device and Panel implement Parcelable interface to be passed between activities
- 17 Action bar title for panelActivity and DeviceListActivity 
- 18 All panel can be displayed in fragment now with states saved
- 19 "n/a" to "..."

###Fix

- 1 Panel and device GTIN array when parsing, gtinArray was not initiated, which will cause null point



v0.2
==============
##2014-02-13

###Add:

- 1 PanelList split view
- 2 App version on loading screen and main activity
- 3 Basic deviceInfo layout
- 4 Device and Loop object
- 5 Basic list view for panel info display

###Updates:
- 1 Change project name to wz-2013-002
- 2 List<Connection> to perform multi-threaded fetch
- 3 PanelInfo background picture
- 4 PanelInfoFragment can fetch data from panel now. v0.1
- 5 PanelList background will be highlighted when selected
- 6 .gitignore and AndroidManifest.xml
- 7 Added passcode and reportNumber; "-" instead of "?" when store panel info
- 8 PanelInfo UI, panel row: LinearLayout weight 1:1
- 9 Socket is not closed after each package received, instead, it is closed after all 16 packages received.
- 10 fetch all panel information by separating whole package into 16 smaller ones.
- 11 Connection's constructor to take a commandList<char[]> and run loop for each command in the list
- 12 DataParser Updated:
	- (1) removeJunkBytes: remove 8 bytes(3 head and 5 tail)
	- (2) getEepRom()
	- (3) getDeviceList()
- 13 Connection update for background listening
- 14 Download function Ok

###Fix:

- 1 SerialNumber and GTIN algorithm


v0.1
==============
##2014-01-17

- Basic socket connection has been setup.
- Can get overall faulty from panel.


