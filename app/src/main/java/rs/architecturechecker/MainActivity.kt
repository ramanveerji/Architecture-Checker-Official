package rs.architecturechecker

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val architectureTextView = findViewById<TextView>(R.id.architectureTextView)
        val notesTextView = findViewById<TextView>(R.id.notesTextView)
        val footerLink = findViewById<TextView>(R.id.footerLink)

        notesTextView.text = Html.fromHtml(
            getString(R.string.architecture_notes),
            Html.FROM_HTML_MODE_LEGACY
        )

        footerLink.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/ramanveerji/Architecture-Checker-Official"))
            startActivity(intent)
        }

        supportActionBar?.let { actionBar ->
            actionBar.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
            actionBar.setCustomView(R.layout.action_bar_title)
            val titleTextView = actionBar.customView.findViewById<TextView>(R.id.actionBarTitle)
            titleTextView.text = getString(R.string.check_architecture_action_bar_title)
        }

        Thread {
            val architecture = getDeviceArchitecture()
            runOnUiThread {
                val fullText = getString(R.string.cpu_architecture, architecture)
                val spannable = SpannableString(fullText)

                val heading = getString(R.string.cpu_architecture, "").trim()
                val headingLength = heading.indexOf("%s").takeIf { it >= 0 } ?: heading.length
                spannable.setSpan(
                    StyleSpan(Typeface.BOLD),
                    0,
                    headingLength,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                val startIndex = fullText.indexOf(architecture)
                if (startIndex != -1) {
                    spannable.setSpan(
                        ForegroundColorSpan(Color.GREEN),
                        startIndex,
                        startIndex + architecture.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }

                architectureTextView.text = spannable
            }
        }.start()
    }

    @Deprecated("Deprecated in Java", ReplaceWith("finishAffinity()"))
    @Suppress("MissingSuperCall")
    override fun onBackPressed() {
        finishAffinity()
    }

    private fun getDeviceArchitecture(): String {
        return try {
            val process = Runtime.getRuntime().exec("getprop ro.product.cpu.abilist")
            val reader = process.inputStream.bufferedReader()
            val output = reader.readLine() ?: getString(R.string.unknown)
            reader.close()
            output
        } catch (e: Exception) {
            getString(R.string.error_retrieving_architecture)
        }
    }
}
