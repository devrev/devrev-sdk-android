# DevRev SDK for Android
DevRev SDK, used for integrating DevRev services into your Android app.

## Table of contents
- [DevRev SDK for Android](#devrev-sdk-for-android)
  - [Table of contents](#table-of-contents)
  - [Quickstart guide](#quickstart-guide)
    - [Requirements](#requirements)
    - [Integration](#integration)
      - [Step 1](#step-1)
      - [Step 2](#step-2)
      - [ProGuard rules](#proguard-rules)
    - [Set up the DevRev SDK](#set-up-the-devrev-sdk)
    - [Sample app](#sample-app)
  - [Features](#features)
    - [Identification](#identification)
      - [Anonymous identification](#anonymous-identification)
      - [Unverified identification](#unverified-identification)
      - [Verified identification](#verified-identification)
        - [Generate an AAT](#generate-an-aat)
        - [Exchange your AAT for a session token](#exchange-your-aat-for-a-session-token)
        - [Identifying the verified user](#identifying-the-verified-user)
      - [Examples](#examples)
      - [Updating the user](#updating-the-user)
      - [Logout](#logout)
      - [Identity model](#identity-model)
        - [Properties](#properties)
          - [UserTraits](#usertraits)
          - [OrganizationTraits](#organizationtraits)
          - [AccountTraits](#accounttraits)
    - [PLuG support chat](#plug-support-chat)
      - [Creating a new conversation](#creating-a-new-conversation)
    - [In-app link handling](#in-app-link-handling)
      - [Examples](#examples-1)
    - [Dynamic theme configuration](#dynamic-theme-configuration)
    - [Analytics](#analytics)
      - [Examples](#examples-2)
    - [Session analytics](#session-analytics)
      - [Opting-in or out](#opting-in-or-out)
      - [Session recording](#session-recording)
      - [Session properties](#session-properties)
      - [Masking sensitive data](#masking-sensitive-data)
        - [Mask](#mask)
          - [Using tag](#using-tag)
          - [Example](#example)
          - [Using API](#using-api)
          - [Examples](#examples-3)
        - [Unmask](#unmask)
          - [Using tag](#using-tag-1)
          - [Example](#example-1)
          - [Using API](#using-api-1)
          - [Examples](#examples-4)
        - [Mask Jetpack Compose views](#mask-jetpack-compose-views)
          - [Example](#example-2)
        - [Mask web view elements](#mask-web-view-elements)
          - [Example](#example-3)
        - [Unmask web view elements](#unmask-web-view-elements)
          - [Example](#example-4)
      - [Custom masking provider](#custom-masking-provider)
        - [Example](#example-5)
      - [Timers](#timers)
        - [Examples](#examples-5)
      - [Screen tracking](#screen-tracking)
        - [Examples](#examples-6)
    - [Screen transition management](#screen-transition-management)
      - [Check if the screen is transitioning](#check-if-the-screen-is-transitioning)
      - [Set screen transitioning state](#set-screen-transitioning-state)
    - [Push notifications](#push-notifications)
      - [Register for push notifications](#register-for-push-notifications)
      - [Unregister from push notifications](#unregister-from-push-notifications)
      - [Handle push notifications](#handle-push-notifications)
        - [Examples](#examples-7)
  - [Troubleshooting](#troubleshooting)
    - [ProGuard](#proguard)
  - [Migration guide](#migration-guide)

## Quickstart guide
### Requirements
- Android Studio 2025.1.1 or later.
- Android Gradle Plugin 8.2 or later.
- Gradle 8.9 or later.
- Minimum Android API level should be 24.

### Integration
To integrate the latest version of our SDK into your app, follow these steps:

#### Step 1
The DevRev SDK is available on Maven Central. To access it, add `mavenCentral` to your root-level `build.gradle` file.

```gradle
repositories {
    mavenCentral()
}
```

After completing these steps in your Gradle files, you should be able to import and use the DevRev SDK in your Android app.

#### Step 2
- Kotlin
Add the following dependencies to your app's `build.gradle.kts` file to get the latest version of our SDK:
```kotlin
dependencies {
    implementation("ai.devrev.sdk:devrev-sdk:<version>")
}
```

- Groovy
Add the following dependencies to your app's `build.gradle` file to get the latest version of our SDK:
```groovy
dependencies {
    implementation 'ai.devrev.sdk:devrev-sdk:<version>'
}
```

#### ProGuard rules
If you are using ProGuard in your project, you must add the following lines to your configuration:
```bash
-keep class ai.devrev.** { *; }
-keep class com.userexperior.* { *; }
```

### Set up the DevRev SDK
1. Open the DevRev web app at [https://app.devrev.ai](https://app.devrev.ai) and go to the **Settings** page.
2. Under **PLuG settings** copy the value under **Your unique App ID**.
3. After obtaining the credentials, you can configure the DevRev SDK in your app.

The SDK will be ready for use once you execute the following configuration method.
- Kotlin
```kotlin
DevRev.configure(context: Context, appId: String, prefersDialogMode: Boolean)
```
- Java
```java
DevRev.INSTANCE.configure(Context context, String appId, Boolean prefersDialogMode);
```

Use this property to check whether the DevRev SDK has been configured:
- Kotlin
```kotlin
DevRev.isConfigured
```
- Java
```java
DevRev.INSTANCE.isConfigured();
```

> [!NOTE]
> `prefersDialogMode`, if set to true, enables the SDK to open the screens in the app's main task/activity

4. To configure the SDK, you need to call the following method inside your `Application` class:

> [!NOTE]
If you don’t have a custom `Application` class, create one as shown below.

- Kotlin
```kotlin
import ai.devrev.sdk.DevRev

class FooApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        DevRev.configure(
            context = this,
            appId = "<APP_ID>"
        )
    }
}
```

- Java
```java
import ai.devrev.sdk.DevRev;

public class FooApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DevRev.INSTANCE.configure(
            this,
            "<APP_ID>"
        );
    }
}
```

5. In the `onCreate` method of your `Application`, configure the DevRev SDK with the required parameters using the credentials obtained earlier.
6. Ensure that the custom application is specified in the `AndroidManifest.xml`, as shown below:
```xml
<application
    android:name=".FooApplication">
</application>
```

### Sample app
This repository includes a sample app demonstrating use cases with both XML-based UI and Jetpack Compose.

Before running the sample app, make sure to configure it with your DevRev credentials. To do this, update the `ai.devrev.sdk.sample.DevRevApplication` class with your credentials.

## Features
### Identification
To access certain features of the DevRev SDK, user identification is required.

The identification function should be placed appropriately in your app after the user logs in. If you have the user information available at app launch, call the function after the `DevRev.configure(context, appID)` method.

> [!IMPORTANT]
> The `Identity` structure allows for custom fields in the user, organization, and account traits. These fields must be configured through the DevRev app before they can be utilized. For more information, refer to [Object customization](https://devrev.ai/docs/product/object-customization).

#### Anonymous identification
The anonymous identification method allows you to create an anonymous user with an optional user identifier, ensuring that no other data is stored or associated with the user.

- Kotlin

```kotlin
DevRev.identifyAnonymousUser(
    userId: String
)
```

- Java

```java
DevRev.INSTANCE.identifyAnonymousUser(
    String userId
);
```

#### Unverified identification
The unverified identification method identifies users with a unique identifier, but it does not verify their identity with the DevRev backend.

- Kotlin
```kotlin
DevRev.identifyUnverifiedUser(
    identity: Identity
)
```

- Java
```java
DevRev.INSTANCE.identifyUnverifiedUser(
    Identity identity
);
```

The function accepts the `DevRev.Identity` structure, with the user identifier (`userID`) as the only required property, all other properties are optional.

#### Verified identification
The verified identification method is used to identify users with an identifier unique to your system within the DevRev platform. The verification is done through a token exchange process between you and the DevRev backend.

The steps to identify a verified user are as follows:
1. Generate an AAT for your system (preferably through your backend).
2. Exchange your AAT for a session token for each user of your system.
3. Pass the user identifier and the exchanged session token to the `DevRev.identifyVerifiedUser(userId, sessionToken)` method.

> [!CAUTION]
> For security reasons we **strongly recommend** that the token exchange is executed on your backend to prevent exposing your application access token (AAT).
##### Generate an AAT
1. Open the DevRev web app at [https://app.devrev.ai](https://app.devrev.ai) and go to the **Settings** page.
2. Open the **PLuG Tokens** page.
3. Under the **Application access tokens** panel, click **New token** and copy the token that's displayed.

> [!IMPORTANT]
> Ensure that you copy the generated application access token, as you cannot view it again.
##### Exchange your AAT for a session token
In order to proceed with identifying the user, you need to exchange your AAT for a session token. This step will help you identify a user of your own system within the DevRev platform.

Here is a simple example of an API request to the DevRev backend to exchange your AAT for a session token:
> [!CAUTION]
> Make sure that you replace the `<AAT>` and `<YOUR_USER_ID>` with the actual values.
```bash
curl \
--location 'https://api.devrev.ai/auth-tokens.create' \
--header 'accept: application/json, text/plain, */*' \
--header 'content-type: application/json' \
--header 'authorization: <AAT>' \
--data '{
  "rev_info": {
    "user_ref": "<YOUR_USER_ID>"
  }
}'
```

The response of the API call will contain a session token that you can use with the verified identification method in your app.

> [!NOTE]
> As a good practice, **your** app should retrieve the exchanged session token from **your** backend at app launch or any relevant app lifecycle event.

##### Identifying the verified user
Pass the user identifier and the exchanged session token to the verified identification method:
```kotlin
DevRev.identifyVerifiedUser(userId: String, sessionToken: String)
```

```java
DevRev.INSTANCE.identifyVerifiedUser(String userId, String sessionToken);
```

#### Examples
- Kotlin
```kotlin
// Identify an anonymous user without a user identifier.
DevRev.identifyAnonymousUser("abcd1234")

// Identify an unverified user with its email address as a user identifier.
DevRev.identifyUnverifiedUser(Identity(userId = "foo@example.org"))

// Identify a verified user with its user identifier and session token.
DevRev.identifyVerifiedUser("foo@example.org", "session-token-1234")
```

- Java
```java
// Identify an anonymous user without a user identifier.
DevRev.INSTANCE.identifyAnonymousUser("abcd1234");

// Identify an unverified user with its email address as a user identifier.
DevRev.identifyUnverifiedUser(
    new Identity("foo@example.org", null, null, null, null, null)
);

// Identify a verified user with its user identifier and session token.
DevRev.INSTANCE.identifyVerifiedUser("foo@example.org", "session-token-1234");
```

The identification function should be placed at the appropriate place in your app after you log in your user. If you have the user information at app launch, call the function after the `DevRev.configure(context, appID)` method.

Use this property to check whether the user has been provided to the SDK:
- Kotlin
```kotlin
DevRev.isUserIdentified
```
- Java
```java
DevRev.INSTANCE.isUserIdentified();
```

#### Updating the user
To update a user's information, use the following method:

- Kotlin
```kotlin
DevRev.updateUser(
    identity: Identity
)
```

- Java
```java
DevRev.INSTANCE.updateUser(
    Identity identity
);
```

The function accepts the `DevRev.Identity` object.

> [!IMPORTANT]
> The `userID` property can *not* be updated.

#### Logout
You can perform a logout of the current user by calling the following method:

```kotlin
DevRev.logout(context: Context, deviceId: String)
```

```java
DevRev.INSTANCE.logout(Context context, String deviceId);
```

The user will be logged out by clearing their credentials, as well as unregistering the device from receiving push notifications, and stopping the session recording.

#### Identity model
The `Identity` class is used to provide user, organization, and account information when identifying users or updating their details. This class is used primarily with the `identifyUnverifiedUser` and `updateUser` methods.

##### Properties
The `Identity` class contains the following properties:

| Property             | Type                  | Required | Description                                   |
|----------------------|-----------------------|----------|-----------------------------------------------|
| `userID`             | `String`              | ✅        | A unique identifier for the user              |
| `organizationID`     | `String?`             | ❌        | An identifier for the user's organization     |
| `accountID`          | `String?`             | ❌        | An identifier for the user's account          |
| `userTraits`         | `UserTraits?`         | ❌        | Additional information about the user         |
| `organizationTraits` | `OrganizationTraits?` | ❌        | Additional information about the organization |
| `accountTraits`      | `AccountTraits?`      | ❌        | Additional information about the account      |

> [!NOTE]
> The custom fields properties defined as part of the user, organization and account traits, must be configured in the DevRev web app **before** they can be used. See [Object customization](https://devrev.ai/docs/product/object-customization) for more information.

###### UserTraits
The `UserTraits` class contains detailed information about the user:

> [!NOTE]
> All properties in `UserTraits` are optional.

| Property       | Type                | Description                               |
|----------------|---------------------|-------------------------------------------|
| `displayName`  | `String?`           | The displayed name of the user            |
| `email`        | `String?`           | The user's email address                  |
| `fullName`     | `String?`           | The user's full name                      |
| `description`  | `String?`           | A description of the user                 |
| `phoneNumbers` | `List<String>?`     | List of the user's phone numbers          |
| `customFields` | `Map<String, Any>?` | Map of custom fields configured in DevRev |

###### OrganizationTraits
The `OrganizationTraits` class contains detailed information about the organization:

> [!NOTE]
> All properties in `OrganizationTraits` are optional.

| Property       | Type                | Description                               |
|----------------|---------------------|-------------------------------------------|
| `displayName`  | `String?`           | The displayed name of the organization    |
| `domain`       | `String?`           | The organization's domain                 |
| `description`  | `String?`           | A description of the organization         |
| `phoneNumbers` | `List<String>?`     | List of the organization's phone numbers  |
| `tier`         | `String?`           | The organization's tier or plan level     |
| `customFields` | `Map<String, Any>?` | Map of custom fields configured in DevRev |

###### AccountTraits
The `AccountTraits` class contains detailed information about the account:

> [!NOTE]
> All properties in `AccountTraits` are optional.

| Property       | Type                | Description                                  |
|----------------|---------------------|----------------------------------------------|
| `displayName`  | `String?`           | The displayed name of the account            |
| `domains`      | `List<String>?`     | List of domains associated with the account  |
| `description`  | `String?`           | A description of the account                 |
| `phoneNumbers` | `List<String>?`     | List of the account's phone numbers          |
| `websites`     | `List<String>?`     | List of websites associated with the account |
| `tier`         | `String?`           | The account's tier or plan level             |
| `customFields` | `Map<String, Any>?` | Map of custom fields configured in DevRev    |

### PLuG support chat
Once user identification is complete, you can start using the chat (conversations) dialog supported by our DevRev SDK. To open the chat dialog, your application should use the `showSupport` API, as shown in the following example:

- Kotlin
```kotlin
DevRev.showSupport(context: Context)
```

- Java
```java
DevRevExtKt.showSupport(DevRev.INSTANCE, context);
```

#### Creating a new conversation
You have the ability to create a new conversation from within your app. The method will show the support chat screen and create a new conversation at the same time.

- Kotlin
```kotlin
DevRev.createSupportConversation(context: Context)
```

- Java
```java
DevRev.INSTANCE.createSupportConversation(context);
```

The DevRev SDK also provides a support button, which can be integrated into your application. To include it on the current screen, add the following code to your XML layout:
```xml
<ai.devrev.sdk.plug.view.PlugFloatingActionButton
    android:id="@+id/plug_fab"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_margin="24dp"
    app:layout_constraintBottom_toBottomOf="parent"
    app:layout_constraintEnd_toEndOf="parent" />
```
The support button can also accept default parameters like
```kotlin
android:src="@your_drawable_here"
```
and/or
```kotlin
android:backgroundTint="@your_background_color"
```

At this stage, your app is fully configured to utilize all functionalities of the DevRev SDK. Pressing the support button directs the user to the chat interface, enabling effective interaction and support.

### In-app link handling
The DevRev SDK provides a mechanism to handle links opened from within any screen that is part of the DevRev SDK.

You can fully customize the link handling behavior by setting the specialized in-app link handler. That way you can decide what should happen when a link is opened from within the app.

- Kotlin
```kotlin
DevRev.setInAppLinkHandler(handler: (String) -> Unit)
```

- Java
```java
DevRev.INSTANCE.setInAppLinkHandler(Function1<String, Unit> handler)
```

You can further customize the behavior by setting the `setShouldDismissModalsOnOpenLink` boolean flag. This flag controls whether the DevRev SDK should dismiss the top-most modal screen when a link is opened.

- Kotlin
```kotlin
DevRev.setShouldDismissModalsOnOpenLink(value: boolean)
```

- Java
```java
DevRev.INSTANCE.setShouldDismissModalsOnOpenLink(boolean value)
```

#### Examples
- Kotlin
```kotlin
DevRev.setInAppLinkHandler { link ->
    // Do something here
}

DevRev.setShouldDismissModalsOnOpenLink(false)
```

- Java
```java
DevRev.INSTANCE.setInAppLinkHandler(link -> {
    // Do something here
    return kotlin.Unit.INSTANCE;
});

DevRev.INSTANCE.setShouldDismissModalsOnOpenLink(false);
```

### Dynamic theme configuration

The DevRev SDK allows you to configure the theme dynamically based on the system appearance, or use the theme configured on the DevRev portal. By default, the theme will be dynamic and follow the system appearance.

- Kotlin
```kotlin
DevRev.setShouldPreferSystemTheme(value: Boolean)
```

- Java
```java
DevRev.INSTANCE.setShouldPreferSystemTheme(boolean value);
```

### Analytics
The DevRev SDK allows you to send custom analytic events by using a name and a string dictionary. You can track these events using the following function:

- Kotlin
```kotlin
DevRev.trackEvent(name: String, properties: HashMap<String, String>)
```
- Java
```java
DevRevAnalyticsExtKt.trackEvent(DevRev instance, String name, HashMap<String, String> properties);
```

#### Examples
- Kotlin
```kotlin
DevRev.trackEvent(name = "open-message-screen", properties = {"id": "foo-bar-1337"})
```

- Java
```java
DevRevAnalyticsExtKt.trackEvent(DevRev.INSTANCE, "open-message-screen", new HashMap<>().put("id", "foo-bar-1337"));
```

### Session analytics
The DevRev SDK provides observability features to help you understand how your users are interacting with your app.

#### Opting-in or out
Session analytics features are opted-in by default, enabling them from the start. However, you can opt-out using the following method:

- Kotlin
```kotlin
DevRev.stopAllMonitoring()
```

- Java
```java
DevRevObservabilityExtKt.stopAllMonitoring(DevRev.INSTANCE);
```

To opt back in, use the following method:

- Kotlin
```kotlin
DevRev.resumeAllMonitoring()
```

- Java
```java
DevRevObservabilityExtKt.resumeAllMonitoring(DevRev.INSTANCE);
```

You can check whether session monitoring has been enabled by using this property:
```kotlin
DevRev.isMonitoringEnabled
```

- Java
```java
DevRevObservabilityExtKt.isMonitoringEnabled(DevRev.INSTANCE);
```

If the user was disabled for session recording by using the stopAllMonitoring() method, you can use this method to enable recording at runtime.
> [!NOTE]
> This feature will only store a monitoring permission flag, it will **not** provide any UI or dialog.

#### Session recording
You can enable session recording to capture user interactions with your app.

> [!CAUTION]
> The session recording feature is opt-out and is enabled by default.

Here are the available methods to help you control the session recording feature:

- Kotlin

| Method                                 | Action                                                                 |
|----------------------------------------|------------------------------------------------------------------------|
| `DevRev.startRecording()`              | Starts session recording.                                              |
| `DevRev.stopRecording()`               | Stops session recording and uploads it to the portal.                  |
| `DevRev.pauseRecording()`              | Pauses the ongoing session recording.                                  |
| `DevRev.resumeRecording()`             | Resumes a paused session recording.                                    |
| `DevRev.processAllOnDemandSessions()`  | Uploads all offline sessions on demand, including the current one.     |

- Java

| Method                                                                      | Action                                                                 |
|-----------------------------------------------------------------------------|------------------------------------------------------------------------|
| `DevRevObservabilityExtKt.startRecording(DevRev.INSTANCE, context)`         | Starts session recording.                                              |
| `DevRevObservabilityExtKt.stopRecording(DevRev.INSTANCE)`                   | Stops session recording and uploads it to the portal.                  |
| `DevRevObservabilityExtKt.pauseRecording(DevRev.INSTANCE)`                  | Pauses the ongoing session recording.                                  |
| `DevRevObservabilityExtKt.resumeRecording(DevRev.INSTANCE)`                 | Resumes a paused session recording.                                    |
| `DevRevObservabilityExtKt.processAllOnDemandSessions(DevRev.INSTANCE)`      | Uploads all offline sessions on demand, including the current one.     |


Using this property will return the status of the session recording:
```kotlin
DevRev.isRecording
```

- Java
```java
DevRevObservabilityExtKt.isRecording(DevRev.INSTANCE);
```

To check if on-demand sessions are enabled, use:
```kotlin
DevRev.areOnDemandSessionsEnabled
```
- Java
```java
DevRevObservabilityExtKt.areOnDemandSessionsEnabled(DevRev.INSTANCE);
```

#### Session properties
You can add custom properties to the session recording to help you understand the context of the session. The properties are defined as a dictionary of string values.

- Kotlin
```kotlin
DevRev.addSessionProperties(properties: HashMap<String, Any>)
```
- Java
```java
DevRevObservabilityExtKt.addSessionProperties(DevRev.INSTANCE, HashMap<String, Object> properties);
```
To clear the session properties in scenarios such as user logout or when the session ends, use the following method:

- Kotlin
```kotlin
DevRev.clearSessionProperties()
```
- Java
```java
DevRevObservabilityExtKt.clearSessionProperties(DevRev.INSTANCE);
```

#### Masking sensitive data
To protect sensitive data, the DevRev SDK provides an auto-masking feature that masks data before sending to the server. Input views such as text fields, text views, and web views are automatically masked.

While the auto-masking feature may be sufficient for most situations, you can manually mark/unmark additional views as sensitive.

##### Mask
###### Using tag
> [!NOTE]
> Use Tag method only when you don't have any other tag already applied to your UI element.

```xml
android:tag="devrev-mask"
```

###### Example
```xml
<WebView
    android:id="@+id/webview2"
    android:layout_width="fill_parent"
    android:layout_height="200dp"
    android:background="@android:color/transparent"
    android:tag="devrev-mask"/>
```

You can also set the tag programmatically:
- Kotlin
```kotlin
val anyView: View = findViewById(R.id.anyView)
anyView.tag = "devrev-mask"
```

- Java
```java
View anyView = findViewById(R.id.anyView);
anyView.setTag("devrev-mask");
```

###### Using API
- Kotlin
```kotlin
DevRev.markSensitiveViews(sensitiveViews: List<View>)
```

- Java
```java
DevRevObservabilityExtKt.markSensitiveViews(DevRev.INSTANCE, List<View> sensitiveViews);
```

###### Examples
- Kotlin
```kotlin
val view1 = findViewById(R.id.view1)
val view2 = findViewById(R.id.view2)

DevRev.markSensitiveViews(listOf(view1, view2))
```

- Java
```java
View view1 = findViewById(R.id.view1);
View view2 = findViewById(R.id.view2);

List<View> sensitiveViewsList = new ArrayList<>();
sensitiveViewsList.add(view1);
sensitiveViewsList.add(view2);

DevRevObservabilityExtKt.markSensitiveViews(DevRev.INSTANCE, sensitiveViewsList);
```

##### Unmask
###### Using tag
> [!NOTE]
> Use Tag method only when you don't have any other tag already applied to your UI element.

```xml
android:tag="devrev-unmask"
```

###### Example
```xml
<WebView
    android:id="@+id/webview2"
    android:layout_width="fill_parent"
    android:layout_height="200dp"
    android:background="@android:color/transparent"
    android:tag="devrev-unmask"/>
```

You can also set the tag programmatically:
- Kotlin
```kotlin
val anyView: View = findViewById(R.id.anyView)
anyView.tag = "devrev-unmask"
```

- Java
```java
View anyView = findViewById(R.id.anyView);
anyView.setTag("devrev-unmask");
```

###### Using API
- Kotlin
```kotlin
DevRev.unmarkSensitiveViews(sensitiveViews: List<View>)
```

- Java
```java
DevRevObservabilityExtKt.unmarkSensitiveViews(DevRev.INSTANCE, List<View> sensitiveViews);
```

###### Examples
- Kotlin
```kotlin
val view1 = findViewById(R.id.view1)
val view2 = findViewById(R.id.view2)

DevRev.unmarkSensitiveViews(listOf(view1, view2))
```

- Java
```java
View view1 = findViewById(R.id.view1);
View view2 = findViewById(R.id.view2);

List<View> sensitiveViewsList = new ArrayList<>();
sensitiveViewsList.add(view1);
sensitiveViewsList.add(view2);

DevRevObservabilityExtKt.unmarkSensitiveViews(DevRev.INSTANCE, sensitiveViewsList);
```

##### Mask Jetpack Compose views
If you want to mask any Jetpack Compose UI elements or views, you can apply a mask on it using a modifier.

```kotlin
modifier = Modifier.markAsMaskedLocation("Name or ID of the Compose View")
```

###### Example
```kotlin
TextField(
       modifier = Modifier
             .markAsMaskedLocation("myTextField")
             .padding(horizontal = 20.dp)
             .onGloballyPositioned { coordinates = it },
       value = input,
       onValueChange = { input = it }
)
```

##### Mask web view elements
If you wish to mask any `WebView` element on a web page explicitly, you can mask it by using the class 'devrev-mask'.

###### Example
```html
<label class="devrev-mask">OTP: 12345</label>
```

##### Unmask web view elements
If you wish to explicitly unmask any manually masked `WebView` element, you can unmask it by using the class 'devrev-unmask'.

###### Example
```html
<input type="text" placeholder="Enter Username" name="username" required class="devrev-unmask">
```

#### Custom masking provider

For advanced use cases, you can provide a custom masking provider to explicitly specify which regions of the UI should be masked during snapshots.

You can implement your own masking logic by creating a class that implements the `MaskLocationProvider` interface and setting your custom object as the masking provider. This allows you to specify explicit regions to be masked or to skip snapshots entirely.

- `DevRev.setMaskLocationProvider(maskLocationProvider: MaskLocationProvider)`  
  Sets a custom provider that determines which UI regions should be masked during snapshots. This overrides any previously set provider.

- `MaskLocationProvider`  
  Interface that must be implemented to return a `SnapshotMask` object containing masked regions and snapshot behavior.

- `SnapshotMask`  
  Class used to describe regions of the screen to be masked and whether a snapshot should be skipped.

- `SnapshotMask.Location`  
  Represents a specific rectangular area of the screen that should be masked.

##### Example
- Kotlin
```kotlin
import com.userexperior.bridge.model.MaskLocationProvider
import com.userexperior.bridge.model.SnapshotMask
import com.userexperior.bridge.model.SnapshotMask.Location
import ai.devrev.sdk.DevRev

class MyMaskingProvider : MaskLocationProvider {
    override fun provideSnapshotMask(): SnapshotMask {
        val region = SnapshotMask.Location(x = 10, y = 10, width = 100, height = 40)
        return SnapshotMask(
            locations = listOf(region),
            shouldSkip = false
        )
    }
}

DevRev.setMaskLocationProvider(MyMaskingProvider())
```

- Java
```java
import java.util.Arrays;
import com.userexperior.bridge.model.MaskLocationProvider;
import com.userexperior.bridge.model.SnapshotMask;
import com.userexperior.bridge.model.SnapshotMask.Location;
import ai.devrev.sdk.DevRev;

public class MyMaskingProvider implements MaskLocationProvider {
    @Override
    public SnapshotMask provideSnapshotMask() {
        Location region = new Location(10, 10, 100, 40);
        return new SnapshotMask(
            Arrays.asList(region),
            false
        );
    }
}

DevRev.setMaskLocationProvider(new MyMaskingProvider());
```

> [!NOTE]
> Setting a new provider will override any previously set masking location provider.

#### Timers
The DevRev SDK offers a timer mechanism to measure the time spent on specific tasks, allowing you to track events such as response time, loading time, or any other duration-based metrics.

The mechanism works using balanced start and stop methods that both accept a timer name and an optional dictionary of properties.

To start a timer, use the following method:

- Kotlin
```kotlin
DevRev.startTimer(name: String, properties: HashMap<String, String>)
```

- Java
```java
DevRevObservabilityExtKt.startTimer(DevRev.INSTANCE, String name, HashMap<String, String> properties);
```

To stop a timer, use the following method:

- Kotlin
```kotlin
DevRev.endTimer(name: String, properties: HashMap<String, String>)
```
- Java
```java
DevRevObservabilityExtKt.endTimer(DevRev.INSTANCE, String name, HashMap<String, String> properties);
```

##### Examples
- Kotlin
```kotlin
DevRev.startTimer("response-time", properties: {"id": "foo-bar-1337"})

// Perform the task that you want to measure.

DevRev.endTimer("response-time", properties: {"id": "foo-bar-1337"})
```
- Java
```java
DevRevObservabilityExtKt.startTimer(DevRev.INSTANCE, "response-time", new HashMap<String, String>().put("id", "foo-bar-1337"));

// Perform the task that you want to measure.

DevRevObservabilityExtKt.endTimer(DevRev.INSTANCE, "response-time", new HashMap<String, String>().put("id", "foo-bar-1337"));
```

#### Screen tracking
The DevRev SDK offers automatic screen tracking to help you understand how users navigate through your app. Although activities and fragments are automatically tracked, you can manually track screens using the following method:

- Kotlin
```kotlin
DevRev.trackScreenName(screenName: String)
```
- Java
```java
DevRevObservabilityExtKt.trackScreenName(DevRev.INSTANCE, String screenName);
```

##### Examples
- Kotlin
```kotlin
DevRev.trackScreenName("profile-screen")
```
- Java
```java
DevRevObservabilityExtKt.trackScreenName(DevRev.INSTANCE, "profile-screen");
```

### Screen transition management
The DevRev SDK allows tracking of screen transitions to understand user navigation within your app.
You can check if a screen transition is in progress and manually update the state using the following methods:

#### Check if the screen is transitioning
- Kotlin
```kotlin
val isTransitioning = DevRev.isInScreenTransitioning
```
- Java
```java
boolean isTransitioning = DevRevObservabilityExtKt.isInScreenTransitioning(DevRev.INSTANCE);
```

#### Set screen transitioning state
- Kotlin
```kotlin
// Mark the transition as started.
DevRev.setInScreenTransitioning(true)

// Mark the transition as stopped.
DevRev.setInScreenTransitioning(false)
```
- Java
```java
// Mark the transition as started.
DevRevObservabilityExtKt.setInScreenTransitioning(DevRev.INSTANCE, true)

// Mark the transition as stopped.
DevRevObservabilityExtKt.setInScreenTransitioning(DevRev.INSTANCE, false)
```

### Push notifications

You can configure your app to receive push notifications from the DevRev SDK. The SDK is designed to handle push notifications and execute actions based on the notification's content.

The DevRev backend sends push notifications to your app to notify users about new messages in the PLuG support chat.

> [!CAUTION]
To receive push notifications, you need to configure your DevRev organization by following the instructions in the [push notifications](https://developer.devrev.ai/public/sdks/mobile/push-notification) section.

You need to ensure that your Android app is configured to receive push notifications. To set it up, follow the [Firebase documentation](https://firebase.google.com/docs/cloud-messaging/android/client).

#### Register for push notifications
> [!IMPORTANT]
> Push notifications require that the SDK has been configured and the user has been identified (unverified and anonymous users). The user identification is required to send the push notification to the correct user.

The DevRev SDK offers a method to register your device for receiving push notifications. You can register for push notifications using the following method:

- Kotlin
```kotlin
DevRev.registerDeviceToken(
  context: Context,
  deviceToken: String,
  deviceId: String
)
```
- Java
```java
DevRev.INSTANCE.registerDeviceToken(
  Context context,
  String deviceToken,
  String deviceId
);
```
The method requires a device identifier that persists across device restarts and app launches. This could be a Firebase installation ID, Android ID, or a unique system identifier. To obtain the device token for Firebase Cloud Messaging, follow these steps:

1. Use the `FirebaseMessaging` object.
2. Call the `firebaseMessaging.token.await()` method.

This method will generate and return the device token.

```kotlin
val firebaseMessaging = FirebaseMessaging.getInstance()
val token = firebaseMessaging.token.await()
// Use the token as needed
```

#### Unregister from push notifications
If your app no longer needs to receive push notifications, you can unregister the device.

Use the following method to unregister the device:

The method requires the device identifier, which should be the same as the one used when registering the device.

- Kotlin
```kotlin
DevRev.unregisterDevice(
  context: Context,
  deviceId: String
)
```
- Java
```java
DevRev.INSTANCE.unregisterDevice(
  Context context,
  String deviceId
);
```

#### Handle push notifications
The DevRev SDK currently does not support automatic handling of push notifications. To open the PLuG chat and manage navigation internally, you must pass the message payload received in the notification to the SDK.

- Kotlin
```kotlin
DevRev.processPushNotification(
    context: Context,
    userInfo: String
)
```
- Java
```java
DevRev.INSTANCE.processPushNotification(
    Context context,
    String userInfo
);
```
To extract the notification payload, do the following:
1. In Firebase Cloud Messaging (FCM), when a push notification is received, it triggers the `onMessageReceived` function in your `FirebaseMessagingService` class.
2. This function receives a `RemoteMessage` object as a parameter, which contains the notification data.
3. The `RemoteMessage` object has a `data` property, which is a map containing key-value pairs of the notification payload.
4. To extract a specific piece of data from the payload, use the key to access the value in the data map.
5. To retrieve the "message" from the payload:

- Kotlin
```kotlin
val message = remoteMessage.data["message"]
```

- Java
```java
String messageData = remoteMessage.getData().get("message");
```

##### Examples
- Kotlin
```kotlin
class MyFirebaseMessagingService: FirebaseMessagingService {
    // ...

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // ...
        val messageData = remoteMessage.data["message"]
        DevRev.processPushNotification(messageData)
    }
}
```
- Java
```java
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    // ...

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...
        String messageData = remoteMessage.getData().get("message");
        DevRev.processPushNotification(messageData);
    }
}
```

## Troubleshooting
- **Issue**: Can't import the SDK into my app.
  **Solution**: Double-check the setup process and ensure that `mavenCentral()` is present in the project-level repositories block.

- **Issue**: How does the DevRev SDK handle errors?
  **Solution**: The DevRev SDK reports all errors using Android's logging utility. Look for error messages in Android Studio's Logcat after applying `DEVREV SDK` filter.

- **Issue**: Support chat won't show.
  **Solution**: Ensure you have correctly called one of the identification methods: `DevRev.identifyUnverifiedUser(...)`, `DevRev.identifyVerifiedUser(...)`, or `DevRev.identifyAnonymousUser(...)`.

- **Issue**: Not receiving push notifications.
  **Solution**: Ensure that your app is configured to receive push notifications and that your device is registered with the DevRev SDK.

### ProGuard
- **Issue**: Missing class `com.google.android.play.core.splitcompat.SplitCompatApplication`.
  **Solution**: Add the following line to your `proguard-rules.pro` file: `-dontwarn com.google.android.play.core.**`.

- **Issue**: Missing class issue due to transitive Flutter dependencies.
  **Solution**: Add the following lines to your `proguard-rules.pro` file:
  ```bash
  -keep class io.flutter.** { *; }
  -keep class io.flutter.plugins.** { *; }
  -keep class GeneratedPluginRegistrant { *; }
  ```

## Migration guide
If you are migrating from the legacy UserExperior SDK to the new DevRev SDK, please refer to the [Migration Guide](./MIGRATION.md) for detailed instructions and feature equivalence.
