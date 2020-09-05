# Hse-Auth-Android

```
dependencies {
  implementation 'com.github.hseapp:Hse-Auth-Android:0.6'
}
```

## How to

#### 1. В AndroidManifest добавляем redirect_uri и client_id приложения
```
<application>
  
  ...
  
  <meta-data
            android:name="auth.hse.ru.client_id"
            android:value="your_app_client_id" />
            
  <meta-data
            android:name="auth.hse.ru.redirect_uri"
            android:value="your_app_redirect_uri" />
            
  ...
  
</application>
```

#### 2. Добавляем intent-filter в любое активити, которое у нас будет ловить code по redirect_uri

```
  <intent-filter>
    <action android:name="android.intent.action.VIEW" />

    <category android:name="android.intent.category.DEFAULT" />
    <category android:name="android.intent.category.BROWSABLE" />

    <data
      android:host="your_app_redirect_uri_host"
      android:scheme="your_app_redirect_uri_scheme" />
   </intent-filter>
```

#### 3. В выбранной активити переопределяем onNewIntent() и прокидываем интент в AuthHelper
```
override fun onNewIntent(intent: Intent?) {
        AuthHelper.onNewIntent(intent, this, REQUEST_LOGIN)
        super.onNewIntent(intent)
    }
```
REQUEST_LOGIN - это любая константа

#### 4. В этой же активити переопределяем onActivityResult(), чтобы получить ответ с токенами
```
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_LOGIN -> {
                if (resultCode != Activity.RESULT_OK || data == null) return
                val accessToken = data.getStringExtra(AuthConstants.KEY_ACCESS_TOKEN)
                val refreshToken = data.getStringExtra(AuthConstants.KEY_REFRESH_TOKEN)
                viewModel.updateLoginState(accessToken, refreshToken)
            }
        }
    }
```

#### 5. Запускаем авторизацию через AuthHelper там, где нам нужно, всё в той же активити
```
private fun requestLogin(mode: Mode) {
        AuthHelper.login(this, mode, REQUEST_LOGIN)
        }
```

В зависимости от наличия данных в AccountManager откроется либо webView с авторизацией, либо можно будет зайти по тем данным, которые есть в AccountManger. В любом случае токены будут прилетать в onActivityResult()


#### 6. TODO: about activity(singleTask) and about DI
