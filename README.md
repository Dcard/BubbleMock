# BubbleMock
It's a mock library for Retrofit in android development, to control http response in run-time.

# Purpose
藉由 Bubble Framework 提供一個介面，以便在 runtime 改變 api 的 response，讓 PM or Designer 方便測試不同的情境

## Feature
- 針對指定的 api call 決定 mock response
- 動態啟用指定的 mock model

## Advantage
- 不需要更改 App 原本的邏輯
- 測試員可以隨時決定 mock response，甚至決定使用 server 的資料
- 開發者可以在 api ready 之前就先行開發


# Setup

1. Import library
```
implementation 'com.github.Dcard:BubbleMock:0.0.3'
```

2. Init `MockBubbleManager` with `MockResource`
    - You can init MockBubbleManager in your Application class.
    - `MyMockSource`, inherited from `MockSource`, would setup all the mock model.
```
class DcardApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        MockBubbleManager.init(MyMockSource())
    
    }

}
```

3. Add `BubbleMockInterceptor` into OkHttpClient.Builder
    - If you don't want to support changing mock response through Bubble in run time, you just have to do these first three steps. 

```
val mockInterceptor = BubbleMockInterceptor(
    isEnable = true
)
      

Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create(Gson()))
    .baseUrl(BASE_URL)
    .client(
        OkHttpClient.Builder()
            .addInterceptor(mockInterceptor)
            .build()
    )
    .build()
```

4. declare `BubbleActivity` in AndroidManifest
    - You have to implement your `AppTheme.NoActionBar` style, or just use `AppTheme` is fine as well. 
```
<activity
	android:name="tw.dcard.bubblemock.sample.screen.BubbleActivity"
	android:allowEmbedded="true"
	android:documentLaunchMode="always"
	android:resizeableActivity="true"
	android:theme="@style/AppTheme.NoActionBar"
	tools:ignore="UnusedAttribute" />
```
5. Call bubble launcher

```
class YourActivity: AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
    
        MockBubbleManager.getInstance().launchBubble(
            activity = this, 
            isEnable = true
        )
    }
}
```

# Mock Module 

## Structure
- MockSource: 所有的 mock request
    - MockRequest: 定義你要 Mock 的行為，包含基本資訊以及 target api
        - MockApi: 指定你要 Mock 的 api 以及定義 mocked response model
 

## MockApi
### 4 elements
- The whole path
```
// if the api url is: https://test.com/the/path/of/member?id=1234
val urlPaths = listof("the", "path", "of", "member")
```
- html method
```
val htmlMethod = "GET"
```
- parameters
```
// if the api url is: https://test.com/the/path/of/member?id=1234
val urlParams = mutableMapOf("id" to "1234")
```
- response object
```
// You can put DAO or json string
val responseObject = private fun getMembers(): String =
        "{\"age\":12,\"id\":132,\"name\":\"Belly World\"}"
        
val responseObject = Member(
    name = "Belly World",
    id = 132,
    age = 12
)

```
- final object 
```
val urlPaths = listof("the", "path", "of", "member")
val responseObject = Member(
    name = "Belly World",
    id = 132,
    age = 12
)
val mockApi = MockApi(urlParams).apply {
    htmlMethod = "GET"
    responseObject = responseObject
    urlParams = mutableMapOf("id" to "1234")
}
```

## MockRequest

### 3 elements
- Basic info
    - 基本資訊，以便顯示在 Bubble 介面上
- The list of MockApi: 
    - 一個 MockRequest 可能會含有多個 MockApi
    - 例如: 有一個顯示使用者的地址資訊的畫面，你必須先用 member name 去獲取 member id，然後再利用 member id 去獲取使用者的地址資訊
- Seletion State
     - 指示該 mock request 是否要直接啟用

### Demo

```
vak mockApiList = listof(mockApi)
val mockRequest1 = MockRequest(
    page = "Main Page", 
    name = "Member List - same data with address"
).apply {
    selected = false
    mockApiList = mockApiList
}
        
```

## DSL for MockRequest and MockApi
```
val mockRequest = 
    request(page = "Main Page", name = "Member List - several") {
        select {
            true
        }
        add {
            api("the", "path", "of", "member") {
                params {
                    param("id", 1234)
                }
                response {
                    Member(
                        name = "Belly World",
                        id = 132,
                        age = 12
                    )
                }
            }
        }
        add {
            api("another", "path") {
                response {
                    AnotherObject()
                }
            }
        }
    }
```

## MockSource

```
class MyMockSource : MockSource {
    val mockRequest = ... // just like code section above

    override fun create(): List<MockRequest> =
        mutableListOf<MockRequest>().apply {
            add(mockRequest)
        }
}
```

> You can check the whole setting in sample code.
> 


## Some other detail
- You have to make sure the whole api path and parameter are all exactly right.
- You can **Ignore** the value of parameter by using **ANY_PARAM_VALUE**
```
// regular way
urlParams = mutableMapOf("id" to ANY_PARAM_VALUE)

/// DSL
params {
    param("id", ANY_PARAM_VALUE)
}
```
- You can **Ignore** the value of api apth by using **ANY_PATH** (However, the size of path must be correct)
```
// regular way
val urlPaths = listof("the", "path", ANY_PATH, ANY_PATH)

// DSL
api("the", "path", ANY_PATH, ANY_PATH) {
    ...
}
```
