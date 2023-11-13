<p align="center">
  <a href="https://digital.hse.ru">
    <img width="200px" src="https://hse-media.hb.bizmrg.com/hsecore/hse-digital-logo-light/image-1582238921120.svg">
  </a>
</p>

<h1 align="center">HSEAuth-Android</h1>

**HSEAuth-Android** является клиентской реализацией стандарта OpenID Connect/OAuth 2.0 для Android. Библиотека позволяет взаимодействовать с OAuth провайдером на базе службы федерации Active Directory, а так же реализует функции мультиаккаунтности, бесшовного входа (SSO) и взаимодействие с API систем Высшей школы экономики с использованием единых авторизационных ключей.

Подробнее про OpenID Connect/OAuth 2.0: https://docs.microsoft.com/en-us/windows-server/identity/ad-fs/development/ad-fs-openid-connect-oauth-concepts

Для взаимодействия с авторизационным сервером (auth.hse.ru) подтребуются уникальные client_id и redirect_uri. Для их получения напишите нам на apps@hse.ru письмо с названием проекта, его кратким описанием и составом команды разработки.

В gradle добавляем:
```
repositories {
        ...
        mavenCentral()
        jcenter()
        maven(url = "https://jitpack.io")
}
    
...

dependencies {
  implementation 'com.github.hseapp:HseAuth-Android:1.1.0'
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

#### 3. В выбранной активити инициализируем AuthHelper
```
override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        AuthHelper.init(this)
    }
```

#### 4. В этой же активити переопределяем onActivityResult(), чтобы получить ответ с токенами
```
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_LOGIN -> {
                if (resultCode != Activity.RESULT_OK || data == null) return
                val accessToken = data.getStringExtra(AuthConstants.KEY_ACCESS_TOKEN)
                val refreshToken = data.getStringExtra(AuthConstants.KEY_REFRESH_TOKEN)
            }
        }
    }
```

#### 5. Запускаем авторизацию через AuthHelper там, где нам нужно, всё в той же активити
REQUEST_LOGIN - это любая константа
```
private fun requestLogin(mode: Mode) {
        AuthHelper.login(this, mode, REQUEST_LOGIN)
        }
```

В зависимости от наличия данных в AccountManager откроется либо webView с авторизацией, либо можно будет зайти по тем данным, которые есть в AccountManger. В любом случае токены будут прилетать в onActivityResult()

#### 6. Для обновления access-токена используйте метод AuthHelper.refreshToken
```
AuthHelper.refreshToken(refreshToken, callback = object : AuthHelper.OnTokenCallback {
                override fun onResult(model: TokensModel) {
                    Log.e(TAG, model.toString())
                }

                override fun onError(e: Exception) {
                    Log.e(TAG, null, e)
                }
            })
```

#### 7. Чтобы проверить свежий ли access-токен можно использовать метод AuthHelper.accessTokenIsFresh
```
val isTokenFresh: Boolean = AuthHelper.accessTokenIsFresh(accessToken)
```

#### 8. Для получения ФИО и аватара юзера используйте AuthHelper.getMe
```
AuthHelper.getMe(accessToken, onMeCallback = object : AuthHelper.OnMeCallback {
                override fun onResult(model: MeDataEntity) {
                    Log.e(TAG, model.toString())
                }

                override fun onError(e: Exception) {
                    Log.e(TAG, null, e)
                }
            })
```

Пример реализации можно посмотреть в тестовом приложении репозитория.
