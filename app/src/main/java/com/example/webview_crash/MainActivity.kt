package com.example.webview_crash

import android.os.Bundle
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.example.webview_crash.ui.theme.Webview_crashTheme

class MainActivity : ComponentActivity() {

    init {
        System.loadLibrary("native-lib") // Native 라이브러리 로드
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // UncaughtExceptionHandler 설정
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("UncaughtException", "Thread: ${thread.name}, Exception: ${throwable.message}", throwable)
            // 필요 시 앱 종료
            System.exit(2)
        }

        setContent {
            Webview_crashTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WebViewComponent(modifier = Modifier.padding(innerPadding))
                }
            }
        }

//         네이티브 크래시 핸들러 등록
//        registerNativeCrashHandler()

    }

//    private external fun registerNativeCrashHandler()

    // 네이티브에서 크래시 보고를 받는 메서드
//    fun makeCrashReport(reason: String, stackTrace: Array<StackTraceElement>, threadId: Int) {
//        Log.e("NativeCrash", "Crash Reason: $reason, Thread ID: $threadId")
//         추가적으로 크래시 정보 처리 로직을 구현할 수 있습니다.
//    }

}

@Composable
fun WebViewComponent(modifier: Modifier = Modifier) {
    // WebView의 자식 Composable을 관리하기 위한 함수입니다.
    AndroidView(factory = { context ->
        WebView(context).apply {
            // WebViewClient 설정 (새 창 띄우기 방지)
            webViewClient = object : WebViewClient() {
                override fun onReceivedError(
                    view: WebView?,
                    request: android.webkit.WebResourceRequest?,
                    error: android.webkit.WebResourceError?
                ) {
                    Log.e("WebViewError", "Error loading URL: ${request?.url}, Error: ${error?.description}")
                }

                override fun onReceivedHttpError(
                    view: WebView?,
                    request: android.webkit.WebResourceRequest?,
                    errorResponse: android.webkit.WebResourceResponse?
                ) {
                    Log.e("WebViewHttpError", "HTTP Error loading URL: ${request?.url}, Status Code: ${errorResponse?.statusCode}")
                }
            }

            // WebChromeClient 설정
            webChromeClient = object : WebChromeClient() {
                // 필요 시 WebChromeClient의 메서드를 오버라이드하여 추가적인 기능을 구현
                override fun onConsoleMessage(message: String, lineNumber: Int, sourceID: String) {
                    Log.e("WebChromeError", "JavaScript error: $message at line: $lineNumber in $sourceID")
                }

            }

            // chrome://version/ 호출
            loadUrl("chrome://crash")
        }
    }, modifier = modifier.fillMaxSize())
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Webview_crashTheme {
        Greeting("Android")
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}
