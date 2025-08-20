# DevRev SDK for Android
DevRev SDK, used for integrating DevRev services into your Android app.

- [DevRev SDK for Android](#devrev-sdk-for-android)
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
      - [Identify an anonymous user](#identify-an-anonymous-user)
      - [Identify an unverified user](#identify-an-unverified-user)
      - [Identify a verified user](#identify-a-verified-user)
        - [Generate an AAT](#generate-an-aat)
        - [Exchange your AAT for a session token](#exchange-your-aat-for-a-session-token)
        - [Identify the verified user](#identify-the-verified-user)
      - [Updating the user](#updating-the-user)
      - [Logout](#logout)
      - [Identity model](#identity-model)
        - [Properties](#properties)
        - [User traits](#user-traits)
        - [Organization traits](#organization-traits)
        - [Account traits](#account-traits)
    - [PLuG support chat](#plug-support-chat)
      - [Creating a new conversation](#creating-a-new-conversation)
      - [Support button](#support-button)
    - [In-app link handling](#in-app-link-handling)
    - [Dynamic theme configuration](#dynamic-theme-configuration)
    - [Analytics](#analytics)
    - [Session analytics](#session-analytics)
    - [Opt-in or out](#opt-in-or-out)
      - [Session recording](#session-recording)
      - [Session properties](#session-properties)
      - [Mask sensitive data](#mask-sensitive-data)
        - [Mask using predefined tags](#mask-using-predefined-tags)
        - [Mask web view elements](#mask-web-view-elements)
        - [Unmask web view elements](#unmask-web-view-elements)
      - [Custom masking provider](#custom-masking-provider)
      - [Timers](#timers)
      - [Track screens](#track-screens)
    - [Manage screen transitions](#manage-screen-transitions)
      - [Check if the screen is transitioning](#check-if-the-screen-is-transitioning)
      - [Set screen transitioning state](#set-screen-transitioning-state)
    - [Push notifications](#push-notifications)
      - [Configuration](#configuration)
      - [Register for push notifications](#register-for-push-notifications)
      - [Unregister from push notifications](#unregister-from-push-notifications)
      - [Handle push notifications](#handle-push-notifications)
  - [Troubleshooting](#troubleshooting)
    - [ProGuard](#proguard)
  - [Migration guide](#migration-guide)

## Quickstart guide

### Requirements
- Android Studio 2025.1.1 or later.
- Android Gradle Plugin 8.2 or later.
- Gradle 8.9 or later.
- Minimum Android API level 24.
- Recommended: An SSH key configured locally and registered with [GitHub](https://docs.github.com/en/github/authenticating-to-github/connecting-to-github-with-ssh).

### Integration

To integrate the latest version of our SDK into your app, follow these steps:

#### Step 1

Our SDK is available on Maven Central. To access it, add `mavenCentral` to your root level `build.gradle` file.

```gradle
repositories {
    mavenCentral()
}
```

After completing these steps in your gradle files, you should be able to import and use the DevRev SDK in your Android application.

#### Step 2

- Kotlin
  Add the following dependencies to your app's `build.gradle.kts` file to get the latest version of our SDK:
    ```kotlin
    dependencies {
        implementation("ai.devrev.sdk:devrev-sdk:<VERSION>")
    }
    ```
- Groovy
    Add the following dependencies to your app's `build.gradle` file to get the latest version of our SDK:
    ```groovy
    dependencies {
        implementation 'ai.devrev.sdk:devrev-sdk:<VERSION>'
    }
    ```

#### ProGuard rules

If you are using ProGuard in your project, add the following lines to your configuration:

```bash
-keep class ai.devrev.** { *; }
-keep class com.userexperior.* { *; }
```

### Set up the DevRev SDK

1. Open the DevRev web app at [https://app.devrev.ai](https://app.devrev.ai) and go to the **Settings** page.
2. Under **PLuG settings** copy the value under **Your unique App ID**.
3. After obtaining the credentials, you can configure the DevRev SDK in your app.

> [!WARNING]
> The DevRev SDK must be configured before you can use any of its features.

The SDK becomes ready for use once the following configuration method is executed.

- Kotlin
    ```kotlin
    DevRev.configure(context: Context, appId: String)
    ```
- Java
    ```java
    DevRev.INSTANCE.configure(Context context, String appId);
    ```

Ensure that the custom application is specified in the `AndroidManifest.xml` file:

```xml
<application
    android:name=".MyApp">
</application>
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

> [!TIP]
> The property `prefersDialogMode`, when set to `true`, enables the SDK to open the screens in the app's main task/activity.

1. Call the following method inside your `Application` class to configure the SDK:

> [!TIP]
> If you don't have a custom `Application` class, create one as shown below.

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

1. In the `onCreate` method of your `Application`, configure the DevRev SDK with the required parameters using the credentials obtained earlier.
2. Ensure that the custom application is specified in the `AndroidManifest.xml`, as shown below:
    ```xml
    <application
        android:name=".FooApplication">
    </application>
    ```

### Sample app

We provide a sample app demonstrating use cases with both XML-based UI and Jetpack Compose as part our [public repository](https://github.com/devrev/devrev-sdk-android).

Before running the sample app, make sure to configure it with your DevRev credentials. To do this, update the `ai.devrev.sdk.sample.DevRevApplication` class with your credentials.

## Features

### Identification

To access certain features of the DevRev SDK, user identification is required.

The identification function should be placed appropriately in your app after the user logs in. If you have the user information available at app launch, call the function after the `DevRev.configure(context, appID)` method.

> [!TIP]
> The `Identity` structure allows for custom fields in the user, organization, and account traits. These fields must be configured through the DevRev app before they can be utilized. For more information, refer to [Object customization](https://devrev.ai/docs/product/object-customization).

#### Identify an anonymous user

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

#### Identify an unverified user

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

For example:

- Kotlin
    ```kotlin
    // Identify an unverified user using their email address as the user identifier.
    DevRev.identifyUnverifiedUser(Identity(userId = "user@example.org"))
    ```
- Java
    ```java
    // Identify an unverified user using their email address as the user identifier.
    DevRev.identifyUnverifiedUser(
        new Identity("user@example.org", null, null, null, null, null)
    );
    ```

#### Identify a verified user

The verified identification method is used to identify users with an identifier unique to your system within the DevRev platform. The verification is done through a token exchange process between you and the DevRev backend.

The steps to identify a verified user are as follows:
1. Generate an AAT for your system (preferably through your backend).
2. Exchange your AAT for a session token for each user of your system.
3. Pass the user identifier and the exchanged session token to the `DevRev.identifyVerifiedUser(_:sessionToken:)` method.

> [!WARNING]
> For security reasons, it is **strongly recommended** that the token exchange is executed on your backend to prevent exposing your application access token (AAT).

##### Generate an AAT

1. Open the DevRev web app at [https://app.devrev.ai](https://app.devrev.ai) and go to the **Settings** page.
2. Open the **PLuG Tokens** page.
3. Under the **Application access tokens** panel, click **New token** and copy the token that's displayed.

> [!WARNING]
> Ensure that you copy the generated application access token, as you cannot view it again.

##### Exchange your AAT for a session token

In order to proceed with identifying the user, you need to exchange your AAT for a session token. This step will help you identify a user of your own system within the DevRev platform.

Here is a simple example of an API request to the DevRev backend to exchange your AAT for a session token:
> [!WARNING]
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

The response of the API call contains a session token that you can use with the verified identification method in your app.

> [!WARNING]
> As a good practice, **your** app should retrieve the exchanged session token from **your** backend at app launch or any relevant app lifecycle event.

##### Identify the verified user

Pass the user identifier and the exchanged session token to the verified identification method:

```kotlin
DevRev.identifyVerifiedUser(userId: String, sessionToken: String)
```

```java
DevRev.INSTANCE.identifyVerifiedUser(String userId, String sessionToken);
```

For example:

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

The function accepts the `DevRev.Identity` ojbect.

> [!WARNING]
> The `userID` property cannot be updated.

#### Logout

You can perform a logout of the current user by calling the following method:

- Kotlin
    ```kotlin
    DevRev.logout(context: Context, deviceId: String)
    ```
- Java
    ```java
    DevRev.logout(context: Context, deviceId: String)
    ```

The user is logged out by clearing their credentials, as well as unregistering the device from receiving push notifications, and stopping the session recording.

#### Identity model

The `Identity` class is used to provide user, organization, and account information when identifying users or updating their details. This class is used primarily with the `identifyUnverifiedUser(_:)` and `updateUser(_:)` methods.

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

##### User traits

The `UserTraits` class contains detailed information about the user:

> [!NOTE]
> All properties in `UserTraits` are optional.

| Property | Type | Description |
|----------|------|-------------|
| `displayName` | `String?` | The displayed name of the user |
| `email` | `String?` | The user's email address |
| `fullName` | `String?` | The user's full name |
| `userDescription` | `String?` | A description of the user |
| `phoneNumbers` | `[String]?` | Array of the user's phone numbers |
| `customFields` | `[String: Any]?` | Dictionary of custom fields configured in DevRev |

##### Organization traits

The `OrganizationTraits` class contains detailed information about the organization:

> [!NOTE]
> All properties in `OrganizationTraits` are optional.

| Property | Type | Description |
|----------|------|-------------|
| `displayName` | `String?` | The displayed name of the organization |
| `domain` | `String?` | The organization's domain |
| `organizationDescription` | `String?` | A description of the organization |
| `phoneNumbers` | `[String]?` | Array of the organization's phone numbers |
| `tier` | `String?` | The organization's tier or plan level |
| `customFields` | `[String: Any]?` | Dictionary of custom fields configured in DevRev |

##### Account traits

The `AccountTraits` class contains detailed information about the account:

> [!NOTE]
> All properties in `AccountTraits` are optional.

| Property | Type | Description |
|----------|------|-------------|
| `displayName` | `String?` | The displayed name of the account |
| `domains` | `[String]?` | Array of domains associated with the account |
| `accountDescription` | `String?` | A description of the account |
| `phoneNumbers` | `[String]?` | Array of the account's phone numbers |
| `websites` | `[String]?` | Array of websites associated with the account |
| `tier` | `String?` | The account's tier or plan level |
| `customFields` | `[String: Any]?` | Dictionary of custom fields configured in DevRev |

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

#### Support button

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

The support button can be customized using default parameters, enabling you to tailor its appearance to your application's design:

```xml
android:src="@your_drawable_here"
android:backgroundTint="@your_background_color"
```

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

For example:

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

The DevRev SDK allows you to configure the theme dynamically based on the system appearance, or use the theme configured on the DevRev portal. By default, the theme is dynamic and follows the system appearance.

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
    DevRev.trackEvent(
        name: String,
        properties: HashMap<String, String>
    )
    ```
- Java
    ```java
    DevRevAnalyticsExtKt.trackEvent(
        DevRev instance,
        String name,
        HashMap<String, String> properties
    );
    ```

For example:

- Kotlin
    ```kotlin
    DevRev.trackEvent(
        name = "open-message-screen",
        properties = mapOf("id" to "foo-bar-1337")
    )
    ```
- Java
    ```java
    DevRevAnalyticsExtKt.trackEvent(
        DevRev.INSTANCE,
        "open-message-screen",
        new HashMap<>().put("id", "foo-bar-1337")
    );
    ```

### Session analytics

The DevRev SDK provides observability features to help you understand how your users are interacting with your app.

### Opt-in or out

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

- Kotlin
    ```kotlin
    DevRev.isMonitoringEnabled
    ```
- Java
    ```java
    DevRevObservabilityExtKt.isMonitoringEnabled(DevRev.INSTANCE);
    ```

If the user was disabled for session recording by using the `stopAllMonitoring()` method, you can use this method to enable recording at runtime.

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
    |`DevRev.startRecording()`   | Starts the session recording.                             |
    |`DevRev.stopRecording()`    | Ends the session recording and uploads it to the portal. |
    |`DevRev.pauseRecording()`   | Pauses the ongoing session recording.                     |
    |`DevRev.resumeRecording()`  | Resumes a paused session recording.                       |
    |`DevRev.processAllOnDemandSessions()`  | Stops the ongoing user recording and sends all on-demand sessions along with the current recording. |

- Java
    | Method                                                                      | Action                                                                 |
    |-----------------------------------------------------------------------------|------------------------------------------------------------------------|
    | `DevRevObservabilityExtKt.startRecording(DevRev.INSTANCE, context)`         | Starts the session recording.                                          |
    | `DevRevObservabilityExtKt.stopRecording(DevRev.INSTANCE)`                   | Ends the session recording and uploads it to the portal.               |
    | `DevRevObservabilityExtKt.pauseRecording(DevRev.INSTANCE)`                  | Pauses the ongoing session recording.                                  |
    | `DevRevObservabilityExtKt.resumeRecording(DevRev.INSTANCE)`                 | Resumes a paused session recording.                                    |
    | `DevRevObservabilityExtKt.processAllOnDemandSessions(DevRev.INSTANCE)`      | Stops the ongoing user recording and sends all on-demand sessions along with the current recording.     |

Using this property returns the status of the session recording:

- Kotlin
    ```kotlin
    DevRev.isRecording
    ```
- Java
    ```java
    DevRevObservabilityExtKt.isRecording(DevRev.INSTANCE);
    ```

To check if on-demand sessions are enabled, use:

- Kotlin
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
    DevRev.addSessionProperties(properties: HashMap<String, String>)
    ```
- Java
    ```java
    DevRevObservabilityExtKt.addSessionProperties(DevRev.INSTANCE, HashMap<String, String> properties);
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

#### Mask sensitive data

To protect sensitive data, the DevRev SDK provides an auto-masking feature that masks data before sending to the server. Input views such as text fields, text views, and web views are automatically masked.

While the auto-masking feature may be sufficient for most situations, you can manually mark/unmark additional views as sensitive.

The SDK provides two approaches to manually mask or unmask your views, using a set of predefined tags or using the API methods.

<Callout intent="tip">
Use the tag method only when you don't have any other tag already applied to your UI element.
</Callout>

##### Mask using predefined tags

Masking views using the predefined tags is the simplest way to mask your views, use the tag `devrev-mask` to mask a view and `devrev-unmask` to unmask a view.

- Mark a view as masked:
  ```xml
  android:tag="devrev-mask"
  ```

- Mark a view as unmasked:
  ```xml
  android:tag="devrev-unmask"
  ```

For example:

```xml
<!-- A masked web view -->
<WebView
    android:id="@+id/webview2"
    android:layout_width="fill_parent"
    android:layout_height="200dp"
    android:background="@android:color/transparent"
    android:tag="devrev-mask"/>

<!-- An unmasked web view -->
<WebView
    android:id="@+id/webview3"
    android:layout_width="fill_parent"
    android:layout_height="200dp"
    android:background="@android:color/transparent"
    android:tag="devrev-unmask"/>
```

The tags can also be set programmatically:

- Kotlin
  - Mark a view as masked:
    ```kotlin
    val firstView: View = findViewById(R.id.firstView)
    firstView.tag = "devrev-mask"
  ```
  - Mark a view as unmasked:
    ```kotlin
    val secondView: View = findViewById(R.id.secondView)
    secondView.tag = "devrev-unmask"
    ```

- Java
  - Mark a view as masked:
    ```java
    View firstView = findViewById(R.id.firstView);
    firstView.setTag("devrev-mask");
    ```
  - Mark a view as unmasked:
    ```java
    View secondView = findViewById(R.id.secondView);
    secondView.setTag("devrev-unmask");
    ```

##### Mask using the API methods

You can also use the API methods to mask or unmask your views programmatically.

- Kotlin
  - Mark a view as masked:
    ```kotlin
    DevRev.markSensitiveViews(sensitiveViews: List<View>)
    ```
  - Mark a view as unmasked:
    ```kotlin
    DevRev.unmarkSensitiveViews(sensitiveViews: List<View>)
    ```

- Java
  - Mark a view as masked:
    ```java
    DevRevObservabilityExtKt.markSensitiveViews(DevRev.INSTANCE, List<View> sensitiveViews);
    ```
  - Mark a view as unmasked:
    ```java
    DevRevObservabilityExtKt.unmarkSensitiveViews(DevRev.INSTANCE, List<View> sensitiveViews);
    ```

For example:

- Kotlin
    ```kotlin
    // Mark two views as masked.

    val view1 = findViewById(R.id.view1)
    val view2 = findViewById(R.id.view2)

    DevRev.markSensitiveViews(listOf(view1, view2))

    // Mark two views as unmasked.

    val view3 = findViewById(R.id.view3)
    val view4 = findViewById(R.id.view4)

    DevRev.unmarkSensitiveViews(listOf(view3, view4))
    ```
- Java
    ```java
    // Mark two views as masked.

    View view1 = findViewById(R.id.view1);
    View view2 = findViewById(R.id.view2);

    List<View> sensitiveViewsList = new ArrayList<>();
    sensitiveViewsList.add(view1);
    sensitiveViewsList.add(view2);

    DevRevObservabilityExtKt.markSensitiveViews(DevRev.INSTANCE, sensitiveViewsList);

    // Mark two views as unmasked.

    View view3 = findViewById(R.id.view3);
    View view4 = findViewById(R.id.view4);

    List<View> sensitiveViewsList = new ArrayList<>();
    sensitiveViewsList.add(view3);
    sensitiveViewsList.add(view4);

    DevRevObservabilityExtKt.unmarkSensitiveViews(DevRev.INSTANCE, sensitiveViewsList);
    ```

##### Mask Jetpack Compose views

If you want to mask any Jetpack Compose UI elements or views, you can apply a mask on it using a modifier.

```kotlin
modifier = Modifier.markAsMaskedLocation("VIEW_NAME_OR_ID")
```

For example:

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

If you wish to explicitly mask any `WebView` element on a web page, you can do so by using the class `devrev-mask`.

For example:

```html
<label class="devrev-mask">OTP: 12345</label>
```

##### Unmask web view elements

If you wish to explicitly unmask any manually masked `WebView` element, you can do so by using the class `devrev-unmask`.

For example:

```html
<input type="text" placeholder="Enter Username" name="username" required class="devrev-unmask">
```

#### Custom masking provider

For advanced use cases, you can provide a custom masking provider to explicitly specify which regions of the UI should be masked during snapshots.

You can implement your own masking logic by creating a class that implements the `MaskLocationProvider` interface and setting your custom object as the masking provider. This allows you to specify explicit regions to be masked or to skip snapshots entirely.

| Symbol | Description |
|-|-|
|`DevRev.setMaskLocationProvider(maskLocationProvider: MaskLocationProvider)`|A custom provider that determines which UI regions should be masked for privacy during snapshots (this overrides any previously set provider).|
|`MaskLocationProvider`|An interface for providing explicit masking locations for UI snapshots.|
|`SnapshotMask`|An object that describes the regions of a snapshot to be masked.|

For example:

- Kotlin
```kotlin
import com.userexperior.bridge.model.MaskLocationProvider
import com.userexperior.bridge.model.SnapshotMask
import ai.devrev.sdk.setMaskLocationProvider
import ai.devrev.sdk.DevRev
import android.graphics.Rect

class MyMaskingProvider : MaskLocationProvider {
    init {
      // Register the custom masking provider
      DevRev.setMaskLocationProvider(this)
    }
    
    override fun provideSnapshotMask(): SnapshotMask {
      // Create a MutableSet to hold the masked regions
      val locations: MutableSet<Rect> = mutableSetOf()

      // Define the regions to be masked
      val rect1 = Rect(0, 0, 100, 50)
      val rect2 = Rect(50, 50, 150, 100)

      // Add the regions to the Set
      locations.add(rect1)
      locations.add(rect2)

      return SnapshotMask(
        locations,
        false
      )
    }
}
```
- Java
```java
import com.userexperior.bridge.model.MaskLocationProvider;
import com.userexperior.bridge.model.SnapshotMask;
import ai.devrev.sdk.DevRevObservabilityExtKt;
import ai.devrev.sdk.DevRev;
import android.graphics.Rect;
import java.util.HashSet;
import java.util.Set;

public class MyMaskingProvider implements MaskLocationProvider {
    public MyMaskingProvider() {
      // Register the custom masking provider
      DevRevObservabilityExtKt.setMaskLocationProvider(DevRev.INSTANCE, this);
    }
    
    @Override
    public SnapshotMask provideSnapshotMask() {
        // Create a Set to hold the masked regions
        Set<Rect> locations = new HashSet<>();

        // Define the regions to be masked
        Rect rect1 = new Rect(0, 0, 100, 100);
        Rect rect2 = new Rect(50, 50, 150, 150);

        // Add the regions to the Set
        locations.add(rect1);
        locations.add(rect2);
        
        return new SnapshotMask(
            locations,
            false
        );
    }
}
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

For example:

- Kotlin
```kotlin
DevRev.startTimer("response-time", properties: {"id": "task-1337"})

// Perform the task that you want to measure.

DevRev.endTimer("response-time", properties: {"id": "task-1337"})
```
- Java
```java
DevRevObservabilityExtKt.startTimer(DevRev.INSTANCE, "response-time", new HashMap<String, String>().put("id", "task-1337"));

// Perform the task that you want to measure.

DevRevObservabilityExtKt.endTimer(DevRev.INSTANCE, "response-time", new HashMap<String, String>().put("id", "task-1337"));
```

#### Track screens

The DevRev SDK offers automatic screen tracking to help you understand how users navigate through your app. Although activities and fragments are automatically tracked, you can manually track screens using the following method:

- Kotlin
```kotlin
DevRev.trackScreenName(screenName: String)
```
- Java
```java
DevRevObservabilityExtKt.trackScreenName(DevRev.INSTANCE, String screenName);
```

For example:

- Kotlin
```kotlin
DevRev.trackScreenName("profile-screen")
```
- Java
```java
DevRevObservabilityExtKt.trackScreenName(DevRev.INSTANCE, "profile-screen");
```

### Manage screen transitions

The DevRev SDK allows tracking of screen transitions to understand the user navigation within your app.
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

// Mark the transition as ended.
DevRev.setInScreenTransitioning(false)
```
- Java
```java
// Mark the transition as started.
DevRevObservabilityExtKt.setInScreenTransitioning(DevRev.INSTANCE, true)

// Mark the transition as ended.
DevRevObservabilityExtKt.setInScreenTransitioning(DevRev.INSTANCE, false)
```

### Push notifications

You can configure your app to receive push notifications from the DevRev SDK. The SDK is designed to handle push notifications and execute actions based on the notification's content.

The DevRev backend sends push notifications to your app to notify users about new messages in the PLuG support chat.

#### Configuration

To receive push notifications, you need to configure your DevRev organization by following the instructions in the [push notifications](https://developer.devrev.ai/public/sdks/mobile/push-notification) section.

You need to ensure that your Android app is configured to receive push notifications. To set it up, follow the [Firebase documentation](https://firebase.google.com/docs/cloud-messaging/android/client).

#### Register for push notifications

> [!TIP]
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

For example:

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
