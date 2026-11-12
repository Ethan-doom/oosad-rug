package com.jetbrains.sample.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private enum class AvailabilityStatus(val label: String) {
    AVAILABLE("Available"),
    MAYBE("Maybe"),
    UNAVAILABLE("Unavailable"),
}

private data class GatheringOption(
    val id: String,
    val date: String,
    val time: String,
    val availableMembers: Int,
)

private val sampleOptions = listOf(
    GatheringOption("fri", "Friday, November 6", "8:00 PM", 4),
    GatheringOption("sat", "Saturday, November 7", "7:00 PM", 5),
    GatheringOption("sun", "Sunday, November 8", "7:00 PM", 2),
)

private fun rankedOptions(
    options: List<GatheringOption>,
    responses: Map<String, AvailabilityStatus>,
): List<Pair<GatheringOption, Int>> =
    options.map { option ->
        option to option.availableMembers + if (responses[option.id] == AvailabilityStatus.AVAILABLE) 1 else 0
    }.sortedByDescending { it.second }

private enum class AuthMode {
    LOGIN,
    REGISTER,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Screen() {
    var isSignedIn by remember { mutableStateOf(false) }
    var authMode by remember { mutableStateOf(AuthMode.LOGIN) }

    MaterialTheme {
        if (isSignedIn) {
            MainScreen(onSignOut = { isSignedIn = false })
        } else {
            AuthScreen(
                mode = authMode,
                onModeChange = { authMode = it },
                onAuthenticated = { isSignedIn = true },
            )
        }
    }
}

@Composable
private fun AuthScreen(
    mode: AuthMode,
    onModeChange: (AuthMode) -> Unit,
    onAuthenticated: () -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                text = "R.U.G",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Plan better. Gather more.",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(32.dp))
            Text(
                text = if (mode == AuthMode.LOGIN) "Welcome back" else "Create your account",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(16.dp))

            if (mode == AuthMode.REGISTER) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; errorMessage = null },
                    label = { Text("Full name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(12.dp))
            }
            OutlinedTextField(
                value = email,
                onValueChange = { email = it; errorMessage = null },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; errorMessage = null },
                label = { Text("Password") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(16.dp))

            errorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 12.dp),
                )
            }

            Button(
                onClick = {
                    val missingRequiredField = email.isBlank() ||
                        password.isBlank() ||
                        (mode == AuthMode.REGISTER && name.isBlank())
                    if (missingRequiredField) {
                        errorMessage = "Complete all required fields to continue."
                    } else {
                        onAuthenticated()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (mode == AuthMode.LOGIN) "Log in" else "Create account")
            }
            TextButton(
                onClick = {
                    errorMessage = null
                    onModeChange(if (mode == AuthMode.LOGIN) AuthMode.REGISTER else AuthMode.LOGIN)
                },
                modifier = Modifier.align(Alignment.CenterHorizontally),
            ) {
                Text(
                    if (mode == AuthMode.LOGIN) "New to R.U.G? Create an account"
                    else "Already have an account? Log in",
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreen(onSignOut: () -> Unit) {
    val responses = remember { mutableStateMapOf<String, AvailabilityStatus>() }
    var submitted by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("R.U.G") },
                actions = {
                    TextButton(onClick = onSignOut) {
                        Text(
                            "Log out",
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(selected = false, onClick = {}, icon = { Text("!") }, label = { Text("Alerts") })
                NavigationBarItem(selected = true, onClick = {}, icon = { Text("○") }, label = { Text("Circles") })
                NavigationBarItem(selected = false, onClick = {}, icon = { Text("□") }, label = { Text("Gathering") })
                NavigationBarItem(selected = false, onClick = {}, icon = { Text("●") }, label = { Text("Profile") })
            }
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
                item {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Team Ryzza",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "Dinner · 7 members",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Dinner", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text("Organized by Ariz · Pending", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.height(8.dp))
                            Text("Choose your availability for every date option.")
                        }
                    }
                }

                item {
                    Text(
                        text = if (submitted) "Availability submitted" else "Respond to dates",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }

                items(sampleOptions, key = { it.id }) { option ->
                    AvailabilityOptionCard(
                        option = option,
                        selectedStatus = responses[option.id],
                        onStatusSelected = {
                            responses[option.id] = it
                            submitted = false
                            errorMessage = null
                        },
                    )
                }

                item {
                    errorMessage?.let { message ->
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(
                                text = message,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(16.dp),
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                    }

                    Button(
                        onClick = {
                            val missing = sampleOptions.filterNot { responses.containsKey(it.id) }
                            if (missing.isNotEmpty()) {
                                submitted = false
                                errorMessage = "Choose a status for every date option before submitting."
                            } else {
                                submitted = true
                                errorMessage = null
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(if (submitted) "Update Availability" else "Submit Availability")
                    }
                }

                if (submitted) {
                    item {
                        SuggestedDatesCard(rankedOptions(sampleOptions, responses))
                    }
                }

                item { Spacer(Modifier.height(8.dp)) }
        }
    }
}

@Composable
private fun AvailabilityOptionCard(
    option: GatheringOption,
    selectedStatus: AvailabilityStatus?,
    onStatusSelected: (AvailabilityStatus) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(option.date, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(option.time, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                AvailabilityStatus.values().forEach { status ->
                    FilterChip(
                        selected = selectedStatus == status,
                        onClick = { onStatusSelected(status) },
                        label = { Text(status.label) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun SuggestedDatesCard(rankedOptions: List<Pair<GatheringOption, Int>>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                "Suggested dates",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                "Ranked by the number of available members.",
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Spacer(Modifier.height(12.dp))
            rankedOptions.forEachIndexed { index, (option, count) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("${index + 1}.", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(8.dp))
                    Column(Modifier.weight(1f)) {
                        Text(option.date, fontWeight = FontWeight.SemiBold)
                        Text(option.time, style = MaterialTheme.typography.bodySmall)
                    }
                    if (index == 0) {
                        Text("Recommended", fontWeight = FontWeight.Bold)
                    } else {
                        Text("$count available")
                    }
                }
            }
        }
    }
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Screen()
        }
    }
}