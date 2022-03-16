package NYCU.student.mediapp

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.net.URLEncoder
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.android.volley.Request


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button_scan = findViewById<Button>(R.id.button)
        val button_listen = findViewById<Button>(R.id.button2)

        button_scan.setOnClickListener{
            startActivityForResult(Intent(this, QRcode::class.java),1)
        }

        button_listen.setOnClickListener{
            val result_text = findViewById<TextView>(R.id.textView)
            var url = "http://tts001.iptcloud.net:8804/display?text0="+URLEncoder.encode(result_text.text as String?, "utf-8")
            val queue = Volley.newRequestQueue(this)
            var translate:String
            val stringRequest = StringRequest(Request.Method.GET, url,
                Response.Listener<String> { response ->
                    // Display the first 500 characters of the response string.
                    translate = response.substring(0)
                    //Toast.makeText(this, translate, Toast.LENGTH_LONG).show()
                    url = "http://tts001.iptcloud.net:8804/synthesize_TLPA?text1=" + translate
                    val webview = findViewById<WebView>(R.id.webviewer)
                    webview.setVisibility(View.VISIBLE)
                    webview.loadUrl(url)
                },
                Response.ErrorListener { VolleyError ->
                    Toast.makeText(this, "連線失敗：${VolleyError}", Toast.LENGTH_LONG).show()
                })
            // Add the request to the RequestQueue.
            queue.add(stringRequest)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data?.extras?.let{
            if(requestCode == 1 && resultCode == Activity.RESULT_OK){
                val result_text = findViewById<TextView>(R.id.textView)
                val button_listen = findViewById<Button>(R.id.button2)
                val result = it.getString("key")
                val scan = it.getInt("scanned")
                if(scan == 1){
                    when(result?.startsWith("nycutcvgh", false)){
                        true -> {
                            result_text.setText(result.substring(9))
                            result_text.setTextColor(Color.BLACK)
                            button_listen.setVisibility(View.VISIBLE)
                        }
                        else -> {
                            result_text.setText("QR code內容格式錯誤，請勿掃描跟服藥指示無關之QR code")
                            result_text.setTextColor(getResources().getColor(R.color.gray))
                            button_listen.setVisibility(View.INVISIBLE)
                        }
                    }
                } else{
                    Toast.makeText(this, "請掃描QR code產生服藥說明", Toast.LENGTH_LONG)
                }
            }
        }
    }
}
