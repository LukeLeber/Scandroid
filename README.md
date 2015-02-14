# Deckard (for Gradle)
[![Build Status](https://secure.travis-ci.org/robolectric/deckard-gradle.png?branch=master)](http://travis-ci.org/robolectric/deckard-gradle)

Deckard is the simplest possible Android project that uses Robolectric for testing and Gradle to build. It has one Activity, a single Robolectric test of that Activity, and an Espresso test of that Activity.

With just a bit of manual intervention, Deckard also imports into IntelliJ and Android Studio, due to their support for gradle. See below for instructions.

## Setup

*Note: These instructions assume you have a Java 1.6 [JDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html) installed.*

To start a new Android project:

1. Install the [Android SDK](http://developer.android.com/sdk/index.html). On Mac OS X with [Homebrew](http://brew.sh/) just run `brew install android-sdk`

2. Set your `ANDROID_HOME` environment variable to `/usr/local/opt/android-sdk`.

3. Run the Android SDK GUI and install `API 19` and `Build-tools 21.1.2`. You can start the GUI by invoking `android`

4. Download Deckard from GitHub:
    ```bash
    wget https://github.com/robolectric/deckard-gradle/archive/master.zip
    unzip master.zip
    mv deckard-master my-new-project
    ```

5. In the project directory you should be able to run the Robolectric tests:
    ```bash
    cd my-new-project
    ./gradlew clean test
    ```
6. You should also be able to run the Espresso tests: `./gradlew clean connectedAndroidTest`. Note: Make sure to start an Emulator or connect a device first so the test has something to connect to.
7. Change the names of things from 'Deckard' to whatever is appropriate for your project. Package name, classes, and the AndroidManifest are good places to start.

8. Build an app. Win.

## IntelliJ / Android Studio Support

### Compatibility
Currently we believe only IntelliJ EAP build 135.666 or later and Android Studio 0.5.0 or later will work with the tooling that deckard-gradle depends on.

### Importing
Import the project into IntelliJ or Android Studio by selecting 'Import Project' and selecting the project's `build.gradle`. When prompted, you can just pick the default gradle wrapper.

### REQUIRED: Tweaking the Module Dependency Order (i.e. Classpath)
For both IntelliJ IDEA and Android Studio, you will also need to change the classpath order for you dependencies. Otherwise you will see the dreaded `Stub!` exception:
```
    !!! JUnit version 3.8 or later expected:
    java.lang.RuntimeException: Stub!
      at junit.runner.BaseTestRunner.<init>(BaseTestRunner.java:5)
      at junit.textui.TestRunner.<init>(TestRunner.java:54)
      at junit.textui.TestRunner.<init>(TestRunner.java:48)
      at junit.textui.TestRunner.<init>(TestRunner.java:41)
```
* For Intellij, go to Project Structure -> Modules -> deckard-gradle pane. In the Dependencies tab, move the Module SDK dependency (i.e. Android API 19 Platform) to be the last item in the list.
* For Android Studio, dependency ordering is currently not modifiable via any GUI. Therefore, you must modify the project iml file directly as such and reload the project:

```
    	    	<orderEntry type="library" exported="" scope="TEST" name="wagon-provider-api-1.0-beta-6" level="project" />
    	    	<orderEntry type="library" exported="" scope="TEST" name="xercesMinimal-1.9.6.2" level="project" />
    	    	<orderEntry type="jdk" jdkName="Android API 19 Platform" jdkType="Android SDK" />			    		<---make sure this is the last orderEntry
    		</component>
    	</module>
```

NOTE: Android Studio aggressively re-writes your dependencies list (your .iml file) and subverts the technique used above to get the Android SDK to the bottom of the classpath. You will get the dreaded Stub! exception every time you re-open the project (and possibly more often).  For this reason we currently recommend IntelliJ; we hope this can be solved in the future.

### IntelliJ Extra Step
Gradle is now in charge of compilation, but IntelliJ still launches the test runner. So in order for IntelliJ to know where to find compiled classes, you have to tell it. This manual step will hopefully go away soon, but for now it's necessary:

1. Go to Project Structure -> Modules -> deckard-gradle -> Paths.
2. The value for 'Output path' should be filled in, but 'Test output path' will not be. Copy the text that's in 'Output path', paste into 'Test output path', but change the final 'build/intermediates/classes/debug' to 'build/test-classes'. This is because the gradle android test plugin currently dumps all compiled test output (for all variants) into the same directory. This means that currently variants are not fully supported.

### Android Studio Extra Setup
The above trick doesn't work for Android Studio, since that part of the module configuration GUI has been ripped out of the IDE. According to [this thread on the Robolectric google group](https://groups.google.com/forum/#!topic/robolectric/xsOpEwtdTi4), some people have managed to get Android Studio to find the classes compiled by gradle.

### Running the Robolectric Test
You should now be able to `DeckardActivityRobolectricTest`. Run it as a normal JUnit test - make sure to choose the JUnit test runner and not the Android one.

### Running the Espresso Test
To run the Espresso test, you need to set up a Run Configuration. Go to `Edit Configurations -> Defaults -> Android Tests` and, after choosing  the correct module (deckard-gradle), fill in the `Specific instrumentation test runner` field. The easiest way is to click the ellipsis button on the right and type in `GITR`. This will find `GoogleInstrumentationTestRunner`, which is what you want. The fully-qualified class name will appear. Now you can right click on the test method in `DeckardEspressoTest` and choose the Android test runner.
