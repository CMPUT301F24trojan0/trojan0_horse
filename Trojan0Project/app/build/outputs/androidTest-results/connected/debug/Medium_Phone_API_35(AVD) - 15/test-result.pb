
Ε5
d
ProfileBrowseDeleteTestcom.example.trojan0projecttestDeleteProfile2ίθ¤Ή€Ά§…:ιθ¤Ή€§ς¦¨.
ώandroidx.test.espresso.base.AssertionErrorHandler$AssertionFailedWithCauseError: View is present in the hierarchy: ConstraintLayout{id=-1, visibility=VISIBLE, width=1080, height=216, has-focus=false, has-focusable=false, has-window-focus=true, is-clickable=false, is-enabled=true, is-focused=false, is-focusable=false, is-layout-requested=false, is-selected=false, layout-params=android.widget.AbsListView$LayoutParams@YYYYYY, tag=null, root-is-layout-requested=false, has-input-connection=false, x=0.0, y=42.0, child-count=2}
Expected: is <false>
Got: was <true>

at dalvik.system.VMStack.getThreadStackTrace(Native Method)
at java.lang.Thread.getStackTrace(Thread.java:1841)
at androidx.test.espresso.base.AssertionErrorHandler.handleSafely(AssertionErrorHandler.java:35)
at androidx.test.espresso.base.AssertionErrorHandler.handleSafely(AssertionErrorHandler.java:26)
at androidx.test.espresso.base.DefaultFailureHandler$TypedFailureHandler.handle(DefaultFailureHandler.java:158)
at androidx.test.espresso.base.DefaultFailureHandler.handle(DefaultFailureHandler.java:120)
at androidx.test.espresso.ViewInteraction.waitForAndHandleInteractionResults(ViewInteraction.java:387)
at androidx.test.espresso.ViewInteraction.check(ViewInteraction.java:366)
at androidx.test.espresso.DataInteraction.check(DataInteraction.java:147)
at com.example.trojan0project.ProfileBrowseDeleteTest.testDeleteProfile(ProfileBrowseDeleteTest.java:67)
... 33 trimmed
Caused by: junit.framework.AssertionFailedError: View is present in the hierarchy: ConstraintLayout{id=-1, visibility=VISIBLE, width=1080, height=216, has-focus=false, has-focusable=false, has-window-focus=true, is-clickable=false, is-enabled=true, is-focused=false, is-focusable=false, is-layout-requested=false, is-selected=false, layout-params=android.widget.AbsListView$LayoutParams@YYYYYY, tag=null, root-is-layout-requested=false, has-input-connection=false, x=0.0, y=42.0, child-count=2}
Expected: is <false>
Got: was <true>

at androidx.test.espresso.matcher.ViewMatchers.assertThat(ViewMatchers.java:620)
at androidx.test.espresso.assertion.ViewAssertions$DoesNotExistViewAssertion.check(ViewAssertions.java:114)
at androidx.test.espresso.ViewInteraction$SingleExecutionViewAssertion.check(ViewInteraction.java:488)
at androidx.test.espresso.ViewInteraction$2.call(ViewInteraction.java:346)
at androidx.test.espresso.ViewInteraction$2.call(ViewInteraction.java:319)
at java.util.concurrent.FutureTask.run(FutureTask.java:264)
at android.os.Handler.handleCallback(Handler.java:959)
at android.os.Handler.dispatchMessage(Handler.java:100)
at android.os.Looper.loopOnce(Looper.java:232)
at android.os.Looper.loop(Looper.java:317)
at android.app.ActivityThread.main(ActivityThread.java:8705)
at java.lang.reflect.Method.invoke(Native Method)
at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:580)
at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:886)
$junit.framework.AssertionFailedErrorώandroidx.test.espresso.base.AssertionErrorHandler$AssertionFailedWithCauseError: View is present in the hierarchy: ConstraintLayout{id=-1, visibility=VISIBLE, width=1080, height=216, has-focus=false, has-focusable=false, has-window-focus=true, is-clickable=false, is-enabled=true, is-focused=false, is-focusable=false, is-layout-requested=false, is-selected=false, layout-params=android.widget.AbsListView$LayoutParams@YYYYYY, tag=null, root-is-layout-requested=false, has-input-connection=false, x=0.0, y=42.0, child-count=2}
Expected: is <false>
Got: was <true>

at dalvik.system.VMStack.getThreadStackTrace(Native Method)
at java.lang.Thread.getStackTrace(Thread.java:1841)
at androidx.test.espresso.base.AssertionErrorHandler.handleSafely(AssertionErrorHandler.java:35)
at androidx.test.espresso.base.AssertionErrorHandler.handleSafely(AssertionErrorHandler.java:26)
at androidx.test.espresso.base.DefaultFailureHandler$TypedFailureHandler.handle(DefaultFailureHandler.java:158)
at androidx.test.espresso.base.DefaultFailureHandler.handle(DefaultFailureHandler.java:120)
at androidx.test.espresso.ViewInteraction.waitForAndHandleInteractionResults(ViewInteraction.java:387)
at androidx.test.espresso.ViewInteraction.check(ViewInteraction.java:366)
at androidx.test.espresso.DataInteraction.check(DataInteraction.java:147)
at com.example.trojan0project.ProfileBrowseDeleteTest.testDeleteProfile(ProfileBrowseDeleteTest.java:67)
... 33 trimmed
Caused by: junit.framework.AssertionFailedError: View is present in the hierarchy: ConstraintLayout{id=-1, visibility=VISIBLE, width=1080, height=216, has-focus=false, has-focusable=false, has-window-focus=true, is-clickable=false, is-enabled=true, is-focused=false, is-focusable=false, is-layout-requested=false, is-selected=false, layout-params=android.widget.AbsListView$LayoutParams@YYYYYY, tag=null, root-is-layout-requested=false, has-input-connection=false, x=0.0, y=42.0, child-count=2}
Expected: is <false>
Got: was <true>

at androidx.test.espresso.matcher.ViewMatchers.assertThat(ViewMatchers.java:620)
at androidx.test.espresso.assertion.ViewAssertions$DoesNotExistViewAssertion.check(ViewAssertions.java:114)
at androidx.test.espresso.ViewInteraction$SingleExecutionViewAssertion.check(ViewInteraction.java:488)
at androidx.test.espresso.ViewInteraction$2.call(ViewInteraction.java:346)
at androidx.test.espresso.ViewInteraction$2.call(ViewInteraction.java:319)
at java.util.concurrent.FutureTask.run(FutureTask.java:264)
at android.os.Handler.handleCallback(Handler.java:959)
at android.os.Handler.dispatchMessage(Handler.java:100)
at android.os.Looper.loopOnce(Looper.java:232)
at android.os.Looper.loop(Looper.java:317)
at android.app.ActivityThread.main(ActivityThread.java:8705)
at java.lang.reflect.Method.invoke(Native Method)
at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:580)
at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:886)
"φ

logcatandroidΰ
έ/Users/gurleenbamrah/Desktop/trojan0_horse/Trojan0Project/app/build/outputs/androidTest-results/connected/debug/Medium_Phone_API_35(AVD) - 15/logcat-com.example.trojan0project.ProfileBrowseDeleteTest-testDeleteProfile.txt"Ί

device-infoandroid
/Users/gurleenbamrah/Desktop/trojan0_horse/Trojan0Project/app/build/outputs/androidTest-results/connected/debug/Medium_Phone_API_35(AVD) - 15/device-info.pb"»

device-info.meminfoandroid
•/Users/gurleenbamrah/Desktop/trojan0_horse/Trojan0Project/app/build/outputs/androidTest-results/connected/debug/Medium_Phone_API_35(AVD) - 15/meminfo"»

device-info.cpuinfoandroid
•/Users/gurleenbamrah/Desktop/trojan0_horse/Trojan0Project/app/build/outputs/androidTest-results/connected/debug/Medium_Phone_API_35(AVD) - 15/cpuinfo*
c
test-results.logOcom.google.testing.platform.runtime.android.driver.AndroidInstrumentationDriver©
¦/Users/gurleenbamrah/Desktop/trojan0_horse/Trojan0Project/app/build/outputs/androidTest-results/connected/debug/Medium_Phone_API_35(AVD) - 15/testlog/test-results.log 2
text/plain