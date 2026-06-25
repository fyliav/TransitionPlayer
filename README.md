This issue has been fixed for Android 14+ in [June 2026 Android Security Bulletin](https://source.android.com/docs/security/bulletin/2026/2026-06-01). [Click here to see the patch](https://android.googlesource.com/platform/frameworks/base/+/40c30bb5613ee94403723b37d0d6c58197d3d1b4)

## Writeup
TODO

I'll complete the writeup when I got a little free time

But before that I have to fight against school work and exams

## Test
Build the project, install generated apk file (if you use the Run button inside Android Studio, turn on "Always install with package manager")

Run the following command on PC
```shell
adb shell app_process '-Djava.class.path=$(pm path top.canyie.transitionplayer | cut -c9-) /system/bin top.canyie.transitionplayer.Main'
```
Then launch an arbitrary app by tapping its icon from the launcher 

A notification should be sent from the launcher app, and if you are on Android 14+, a fabricated overlay will be injected into the system, so `adb shell cmd overlay lookup android android:integer/config_multiuserMaximumUsers` should return 100

## Fixes
- [The animation delegate mechanism has been refactored and IApplicationThread handle is no longer being sent out of WindowManagerService](https://android.googlesource.com/platform/frameworks/base/+/40c30bb5613ee94403723b37d0d6c58197d3d1b4)
- Starting from Android 17, [call to IApplicationThread will be rejected if it is from non-system](https://cs.android.com/android/_/android/platform/frameworks/base/+/3a9b1c451a0d4a2e5cea997364ecba98912f85fb). I don't think it is an effective way to mitigate such exploits as I think attackers can trick ActivityManagerService into making calls with attacker-controlled apk path to target process (although I haven't tested it myself), but it's a signal that Android Security Team starting to taking actions
