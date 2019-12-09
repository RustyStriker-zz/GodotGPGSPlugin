# GodotGPGSPlugin
This is an android plugin for the godot game engine(https://github.com/okamstudio/godot) 3.2 or higher.

Currently this plugin supports:
 - Connecting to google Play
 - Achievements
 - Leaderboards support

## How to use
 - Configure, install and enable the "Android Custom Template" for your project, just follow the [official documentation](https://docs.godotengine.org/en/latest/getting_started/workflow/export/android_custom_build.html)
 - download or clone this repository
 - drop the folder inside the `res://android/` directory on your Godot project.
 - change the ```bash{ Your app id HERE }``` to your app id found in the google play developer console, should look like:
 ```
 <resources>

 	<string name="google_play_app_id">1234567890</string>

 </resources>
```

 - on the Project Settings -> Android -> Modules, add the string:
 ```
 org/godotengine/godot/PlayGames
 ```
 - if you have more than one module then seperate them by using `,` so it will look like this:
 ```
 org/godotengine/godot/someRandomModule,org/godotengine/godot/PlayGames
```
 - on the Project -> Export -> Android -> Options -> Permissions: check the permissions for Access Network State and Internet
## Known errors
 - SilentLogIn() doesnt work
 - when building the game to your android device(at lease for me) it doesnt connect to the google play games successfully
    so exit the app and open it again, this time it should work.


## Api Refrence
The Following methods are available
```python
# Init play services:
google_init(id) # id should be given using get_instance_ID() in godot

# Connect
connect() # Used to connect the player using the UI

# Disconenct
disconnect() # not really a lot to tell here

# Achievements
achievementUnlock(id) # Pass the Achievement id

achievementIncrease(id,amount) # Pass the Achievement id and the amount, for increment Achievements

achievementShowList() # Shows the list of Achievements

# Leaderboard
leaderSubmit(id, score) # Pass the leaderboard's id and the score you want to register

leaderShow(id) # Shows a specific leaderboard by given id

leaderShowList() # Shows the list of all leaderboards

# Callbacks
# ----------

_on_successful_sign_in() # Callback for player signed in

_on_failed_sign_in() # Callback for then the player failed to sign in

_on_achievement_unlock(id) # Callback for when an Achievement is unlocked, returning the Achievement id

_on_achievement_increase(id,amount) # Callback for when an Achievement is increased, returning the Achievement id and amount

_on_achievement_list_show() # Callback for when the Achievement list is shown

_on_achievement_list_failed(error) # Callback for when the Achievement list failed to show, returns the error in a string format

_on_leader_submit() # Callback for when a score was submitted to the leaderboard

_on_leader_show() # Callback for when a leaderboard is shown

_on_leader_failed(error) # Callback for when a leaderboard failed to show, returns the error in a string format
```

## Troubleshooting
 - First of all make sure you are able to compile to android without the module
 - using logcat is really useful and probably the best way:
 ```
 adb -d logcat godot:V godotGoogle:V *:S
 ```
 - fill free to change the code for your desires.

## To do:
 - Silent log in(if needed at all)

## For any question or problem feel free to open an issue or mail me on avivr903@gmail.com


 ## License
 MIT License
