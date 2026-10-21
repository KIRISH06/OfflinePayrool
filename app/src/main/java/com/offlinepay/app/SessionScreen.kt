package com.offlinepay.app

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SessionScreen(job: UssdJob, subtitle: String, onClose: () -> Unit) {
    val ctx = LocalContext.current
    val session = remember { PaySession(ctx, job) }
    LaunchedEffect(Unit) { session.start() }
    val close = { session.cancel(); onClose() }
    BackHandler { close() }

    val title = if (job is UssdJob.Balance) tr("Balance", "बैलेंस") else tr("Payment", "पेमेंट")

    Column(
        Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        BackRow(title, close)
        if (subtitle.isNotBlank()) {
            Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Text(subtitle, Modifier.padding(16.dp), fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
        when (val s = session.state) {
            is SState.Working -> Column(
                Modifier.fillMaxWidth().padding(vertical = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CircularProgressIndicator(color = Purple)
                Text(s.text, color = Color.Gray)
            }

            is SState.NeedPin -> PinPanel(s.bankMsg) { session.submitPin(it) }

            is SState.Manual -> ManualPanel(s.msg, onReply = { session.reply(it) }, onClose = close)

            is SState.Done -> {
                Column(
                    Modifier.fillMaxWidth().padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(if (s.ok) "✅" else "❌", fontSize = 56.sp)
                    Text(
                        if (s.ok) tr("Ho gaya", "हो गया") else tr("Nahi hua", "नहीं हुआ"),
                        fontSize = 22.sp, fontWeight = FontWeight.Bold,
                        color = if (s.ok) Green else Red
                    )
                    Text(s.msg, color = Color.DarkGray)
                }
                Button(onClick = onClose, modifier = Modifier.fillMaxWidth().height(52.dp)) {
                    Text(tr("Done", "ठीक है"))
                }
            }
        }
    }
}

@Composable
private fun PinPanel(bankMsg: String, onSubmit: (String) -> Unit) {
    var pin by remember { mutableStateOf("") }
    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3CD))) {
        Column(Modifier.padding(16.dp)) {
            Text(tr("Bank ka sandesh (receiver ka naam jaanch lein)", "बैंक का संदेश (पाने वाले का नाम जाँच लें)"),
                fontSize = 12.sp, color = Color.DarkGray)
            Spacer(Modifier.height(6.dp))
            Text(bankMsg, fontSize = 15.sp)
        }
    }
    OutlinedTextField(
        value = pin,
        onValueChange = { pin = it.filter(Char::isDigit).take(6) },
        label = { Text("UPI PIN") },
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
    Button(
        onClick = { val p = pin; pin = ""; onSubmit(p) },
        enabled = pin.length in 4..6,
        modifier = Modifier.fillMaxWidth().height(54.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Green)
    ) { Text(tr("Pay karein", "पे करें"), fontSize = 16.sp) }
    Text(
        tr("PIN seedha bank ko jaata hai. App ise save ya record nahi karta.",
            "PIN सीधे बैंक को जाता है। ऐप इसे सेव या रिकॉर्ड नहीं करता।"),
        fontSize = 12.sp, color = Color.Gray
    )
}

@Composable
private fun ManualPanel(msg: String, onReply: (String) -> Unit, onClose: () -> Unit) {
    var text by remember { mutableStateOf("") }
    val opts = remember(msg) { UssdFlow.options(msg) }
    Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Text(msg, Modifier.padding(16.dp), fontSize = 15.sp)
    }
    if (opts.isNotEmpty()) {
        opts.forEach { (num, label) ->
            OutlinedButton(onClick = { onReply(num) }, modifier = Modifier.fillMaxWidth()) {
                Text("$num. $label")
            }
        }
    } else {
        OutlinedTextField(
            value = text, onValueChange = { text = it }, singleLine = true,
            label = { Text(tr("Jawab likhein", "जवाब लिखें")) },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = { val t = text; text = ""; onReply(t) },
            enabled = text.isNotBlank(), modifier = Modifier.fillMaxWidth().height(50.dp)
        ) { Text(tr("Bhejein", "भेजें")) }
    }
    TextButton(onClick = onClose) { Text(tr("Band karein", "बंद करें")) }
}
