package com.jetbrains.sample.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private enum class AvailabilityStatus(val label: String) { AVAILABLE("Available"), MAYBE("Maybe"), UNAVAILABLE("Unavailable") }
private enum class GatheringStatus(val label: String) { PROPOSED("Proposed"), FINALIZED("Finalized"), CANCELLED("Cancelled") }
private enum class AttendanceStatus(val label: String) { GOING("Going"), ON_THE_WAY("On the way"), LATE("Running late") }
private enum class AuthMode { LOGIN, REGISTER }
private enum class AppPage { CIRCLES, GATHERING, NOTIFICATIONS, PROFILE, CREATE_CIRCLE, CREATE_GATHERING, REVIEW }

private data class DateOption(val id: String, val date: String, val time: String, val baseAvailable: Int)
private data class Circle(val name: String, val members: Int)
private data class Gathering(val title: String, val organizer: String, val status: GatheringStatus, val options: List<DateOption>)
private data class Notice(val message: String, var read: Boolean = false)

private val demoOptions = listOf(
    DateOption("fri", "Friday, November 6", "8:00 PM", 4),
    DateOption("sat", "Saturday, November 7", "7:00 PM", 5),
    DateOption("sun", "Sunday, November 8", "7:00 PM", 2),
)

@Composable
fun Screen() {
    var signedIn by remember { mutableStateOf(false) }
    if (!signedIn) {
        AuthScreen { signedIn = true }
    } else {
        RugApp { signedIn = false }
    }
}

@Composable
private fun AuthScreen(onAuthenticated: () -> Unit) {
    var mode by remember { mutableStateOf(AuthMode.LOGIN) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    Surface(Modifier.fillMaxSize()) {
        Column(
            Modifier.fillMaxSize().padding(28.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
        ) {
            Text("R.U.G", style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            Text("Plan better. Gather more.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(32.dp))
            Text(if (mode == AuthMode.LOGIN) "Welcome back" else "Create your account", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            if (mode == AuthMode.REGISTER) {
                OutlinedTextField(name, { name = it }, label = { Text("Full name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(12.dp))
            }
            OutlinedTextField(email, { email = it }, label = { Text("Email") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(password, { password = it }, label = { Text("Password") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            error?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp)) }
            Spacer(Modifier.height(16.dp))
            Button({
                if (email.isBlank() || password.isBlank() || (mode == AuthMode.REGISTER && name.isBlank())) error = "Complete all required fields."
                else onAuthenticated()
            }, Modifier.fillMaxWidth()) { Text(if (mode == AuthMode.LOGIN) "Log in" else "Create account") }
            TextButton({ mode = if (mode == AuthMode.LOGIN) AuthMode.REGISTER else AuthMode.LOGIN; error = null }, Modifier.align(Alignment.CenterHorizontally)) {
                Text(if (mode == AuthMode.LOGIN) "New to R.U.G? Create an account" else "Already have an account? Log in")
            }
        }
    }
}

@Composable
private fun RugApp(onLogout: () -> Unit) {
    val circles = remember { mutableStateListOf(Circle("Team Ryzza", 7)) }
    val notices = remember { mutableStateListOf(Notice("Dinner has 3 date options awaiting your response."), Notice("Welcome to R.U.G.")) }
    var gathering by remember { mutableStateOf(Gathering("Dinner", "Ariz", GatheringStatus.PROPOSED, demoOptions)) }
    var page by remember { mutableStateOf(AppPage.CIRCLES) }
    var attendance by remember { mutableStateOf<AttendanceStatus?>(null) }
    val responses = remember { mutableStateMapOf<String, AvailabilityStatus>() }
    var message by remember { mutableStateOf<String?>(null) }

    fun submitAvailability() {
        if (gathering.status != GatheringStatus.PROPOSED) {
            message = "Voting is ${gathering.status.label.lowercase()}. Earlier responses were kept."
        } else if (gathering.options.any { responses[it.id] == null }) {
            message = "Choose Available, Unavailable or Maybe for every option."
        } else {
            message = "Availability saved. Suggested dates have been refreshed."
            notices.add(0, Notice("Your availability for ${gathering.title} was saved."))
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("R.U.G") }, actions = { TextButton(onLogout) { Text("Log out") } }) },
        bottomBar = {
            NavigationBar {
                TextButton({ page = AppPage.CIRCLES }) { Text("Circles") }
                TextButton({ page = AppPage.GATHERING }) { Text("Gathering") }
                TextButton({ page = AppPage.NOTIFICATIONS }) { Text("Alerts") }
                TextButton({ page = AppPage.PROFILE }) { Text("Profile") }
            }
        },
    ) { insets ->
        when (page) {
            AppPage.CIRCLES -> CirclePage(circles, { page = AppPage.CREATE_CIRCLE }, { page = AppPage.GATHERING })
            AppPage.GATHERING -> GatheringPage(gathering, responses, attendance, message, { responses[it.first] = it.second; message = null }, ::submitAvailability, { page = AppPage.CREATE_GATHERING }, { page = AppPage.REVIEW }, { attendance = it }, insets)
            AppPage.NOTIFICATIONS -> NotificationPage(notices, insets)
            AppPage.PROFILE -> ProfilePage(onLogout, insets)
            AppPage.CREATE_CIRCLE -> CreateCirclePage({ name -> circles.add(Circle(name, 1)); page = AppPage.CIRCLES }, { page = AppPage.CIRCLES }, insets)
            AppPage.CREATE_GATHERING -> CreateGatheringPage({ title, options -> gathering = Gathering(title, "You", GatheringStatus.PROPOSED, options); page = AppPage.GATHERING }, { page = AppPage.GATHERING }, insets)
            AppPage.REVIEW -> ReviewPage(gathering, { gathering = gathering.copy(status = GatheringStatus.FINALIZED); notices.add(0, Notice("${gathering.title} was finalized.")); page = AppPage.GATHERING }, { gathering = gathering.copy(status = GatheringStatus.CANCELLED); page = AppPage.GATHERING }, insets)
        }
    }
}

@Composable
private fun CirclePage(circles: List<Circle>, create: () -> Unit, openGathering: () -> Unit) {
    AppColumn("Your Circles") {
        circles.forEach { circle ->
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text(circle.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("${circle.members} members", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    Button(openGathering) { Text("View Dinner gathering") }
                }
            }
        }
        Button(create, Modifier.fillMaxWidth()) { Text("Create Circle") }
        OutlinedButton({}, Modifier.fillMaxWidth()) { Text("Accept Circle Invitation") }
    }
}

@Composable
private fun GatheringPage(
    gathering: Gathering,
    responses: Map<String, AvailabilityStatus>,
    attendance: AttendanceStatus?,
    message: String?,
    select: (Pair<String, AvailabilityStatus>) -> Unit,
    submit: () -> Unit,
    create: () -> Unit,
    review: () -> Unit,
    setAttendance: (AttendanceStatus) -> Unit,
    insets: androidx.compose.foundation.layout.PaddingValues,
) {
    AppColumn("Gathering Details", insets) {
        Text(gathering.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("Organized by ${gathering.organizer} · ${gathering.status.label}")
        if (gathering.status == GatheringStatus.PROPOSED) {
            gathering.options.forEach { option ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(14.dp)) {
                        Text(option.date, fontWeight = FontWeight.SemiBold)
                        Text(option.time)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            AvailabilityStatus.values().forEach { status ->
                                FilterChip(responses[option.id] == status, { select(option.id to status) }, label = { Text(status.label) }, modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
            message?.let { Text(it, color = if (responses.size == gathering.options.size) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error) }
            Button(submit, Modifier.fillMaxWidth()) { Text("Submit Availability") }
        }
        SuggestedCard(gathering.options, responses)
        Text("Attendance", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            AttendanceStatus.values().forEach { status -> FilterChip(attendance == status, { setAttendance(status) }, label = { Text(status.label) }) }
        }
        Button(review, Modifier.fillMaxWidth()) { Text("Review Member Availability") }
        OutlinedButton(create, Modifier.fillMaxWidth()) { Text("Create New Gathering") }
    }
}

@Composable
private fun SuggestedCard(options: List<DateOption>, responses: Map<String, AvailabilityStatus>) {
    val ranked = options.sortedByDescending { it.baseAvailable + if (responses[it.id] == AvailabilityStatus.AVAILABLE) 1 else 0 }
    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
        Column(Modifier.padding(16.dp)) {
            Text("Suggested Dates", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            ranked.forEachIndexed { index, option ->
                Text("${index + 1}. ${option.date} ${option.time} — ${option.baseAvailable} available${if (index == 0) " (Recommended)" else ""}")
            }
        }
    }
}

@Composable
private fun ReviewPage(gathering: Gathering, finalize: () -> Unit, cancel: () -> Unit, insets: androidx.compose.foundation.layout.PaddingValues) {
    AppColumn("Organizer Review", insets) {
        Text("${gathering.title} · ${gathering.status.label}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("Member availability", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        listOf("Ethan", "Aira", "Ariz", "Mika", "Jo").forEach { member ->
            Text("$member    Available    Maybe    Pending", modifier = Modifier.padding(vertical = 5.dp))
        }
        SuggestedCard(gathering.options, emptyMap())
        Button(finalize, Modifier.fillMaxWidth()) { Text("Finalize Gathering Schedule") }
        OutlinedButton(cancel, Modifier.fillMaxWidth()) { Text("Cancel Gathering") }
    }
}

@Composable
private fun NotificationPage(notices: MutableList<Notice>, insets: androidx.compose.foundation.layout.PaddingValues) {
    AppColumn("Notifications", insets) {
        notices.forEach { notice ->
            Card(Modifier.fillMaxWidth()) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(notice.message, fontWeight = if (notice.read) FontWeight.Normal else FontWeight.Bold)
                        Text(if (notice.read) "Read" else "Unread", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    if (!notice.read) TextButton({ notice.read = true }) { Text("Mark read") }
                }
            }
        }
    }
}

@Composable
private fun ProfilePage(logout: () -> Unit, insets: androidx.compose.foundation.layout.PaddingValues) {
    AppColumn("Profile", insets) {
        Text("Ethan Malcolm Porte", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("ethan@example.com", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(12.dp))
        Text("Account settings", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        OutlinedButton({}, Modifier.fillMaxWidth()) { Text("Manage Account") }
        Button(logout, Modifier.fillMaxWidth()) { Text("Log out") }
    }
}

@Composable
private fun CreateCirclePage(save: (String) -> Unit, cancel: () -> Unit, insets: androidx.compose.foundation.layout.PaddingValues) {
    var name by remember { mutableStateOf("") }
    AppColumn("Create Circle", insets) {
        OutlinedTextField(name, { name = it }, label = { Text("Circle name") }, modifier = Modifier.fillMaxWidth())
        Button({ if (name.isNotBlank()) save(name) }, Modifier.fillMaxWidth()) { Text("Create Circle") }
        TextButton(cancel) { Text("Cancel") }
    }
}

@Composable
private fun CreateGatheringPage(save: (String, List<DateOption>) -> Unit, cancel: () -> Unit, insets: androidx.compose.foundation.layout.PaddingValues) {
    var title by remember { mutableStateOf("") }
    var optionsText by remember { mutableStateOf("Friday, November 6|8:00 PM\nSaturday, November 7|7:00 PM\nSunday, November 8|7:00 PM") }
    AppColumn("Create Gathering", insets) {
        OutlinedTextField(title, { title = it }, label = { Text("Gathering name") }, modifier = Modifier.fillMaxWidth())
        Text("Add date options (one per line: date|time)")
        OutlinedTextField(optionsText, { optionsText = it }, modifier = Modifier.fillMaxWidth(), minLines = 4)
        Button({
            val options = optionsText.lines().mapIndexedNotNull { index, line ->
                val parts = line.split("|")
                if (parts.size == 2) DateOption("new$index", parts[0], parts[1], 0) else null
            }
            if (title.isNotBlank() && options.isNotEmpty()) save(title, options)
        }, Modifier.fillMaxWidth()) { Text("Create Gathering") }
        TextButton(cancel) { Text("Cancel") }
    }
}

@Composable
private fun AppColumn(title: String, insets: androidx.compose.foundation.layout.PaddingValues = androidx.compose.foundation.layout.PaddingValues(0.dp), content: @Composable ColumnScope.() -> Unit) {
    LazyColumn(Modifier.fillMaxSize().padding(insets).padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Spacer(Modifier.height(16.dp))
            Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
        }
        item { Column(verticalArrangement = Arrangement.spacedBy(12.dp), content = content) }
        item { Spacer(Modifier.height(24.dp)) }
    }
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { Screen() } }
    }
}
