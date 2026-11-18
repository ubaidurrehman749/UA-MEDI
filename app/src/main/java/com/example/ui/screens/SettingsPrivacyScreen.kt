package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.local.AppLanguage
import com.example.data.local.UserPreferences
import com.example.ui.components.SectionHeader

@Composable
fun SettingsPrivacyScreen(
    preferences: UserPreferences,
    onToggleDarkMode: (Boolean) -> Unit,
    onSelectLanguage: (AppLanguage) -> Unit,
    onSaveDisplayName: (String) -> Unit,
    onLoadDemoData: () -> Unit,
    onDeleteAllData: () -> Unit,
    modifier: Modifier = Modifier
) {
    var nameInput by remember(preferences.displayName) { mutableStateOf(preferences.displayName) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Column(modifier = Modifier.widthIn(max = 720.dp)) {
                SectionHeader(
                    title = "Settings & Privacy",
                    subtitle = "Manage display preferences, bilingual language, and local data retention"
                )
            }
        }

        item {
            Card(
                modifier = Modifier.widthIn(max = 720.dp).fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = MaterialTheme.shapes.large
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Appearance & Language", style = MaterialTheme.typography.titleMedium)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Dark Mode", style = MaterialTheme.typography.bodyLarge)
                            Text("Comfortable low-light palette", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = preferences.isDarkMode,
                            onCheckedChange = onToggleDarkMode,
                            modifier = Modifier.testTag("settings_dark_mode_switch")
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Preferred Language", style = MaterialTheme.typography.bodyLarge)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = preferences.language == AppLanguage.ENGLISH,
                                onClick = { onSelectLanguage(AppLanguage.ENGLISH) },
                                label = { Text("English") }
                            )
                            FilterChip(
                                selected = preferences.language == AppLanguage.URDU,
                                onClick = { onSelectLanguage(AppLanguage.URDU) },
                                label = { Text("اردو") }
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text("Preferred Greeting Name (Optional)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        Button(onClick = { onSaveDisplayName(nameInput) }) {
                            Text("Save")
                        }
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.widthIn(max = 720.dp).fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = MaterialTheme.shapes.large
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(8.dp))
                        Text("Privacy Notice & Data Retention", style = MaterialTheme.typography.titleMedium)
                    }
                    Text(
                        text = "• On-Device Local Storage: Your medications, dose logs, health journal entries, saved report summaries, and appointment prep lists are stored strictly in a private local SQLite (Room) database on your device.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "• No Unnecessary Data Collection: HealthMate AI does not require account registration, national IDs, or cloud medical record syncing.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "• AI Processing Scope: Only messages, symptom forms, or uploaded report pages you explicitly submit to AI features are transmitted over encrypted HTTPS to Gemini via Firebase AI Logic (protected by Firebase App Check).",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "• Full Deletion Control: You can permanently erase all locally stored records below at any time.",
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton(onClick = onLoadDemoData, modifier = Modifier.weight(1f)) {
                            Text("Load Sample Data")
                        }
                        Button(
                            onClick = { showDeleteConfirm = true },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier.weight(1f).testTag("settings_delete_all_btn")
                        ) {
                            Icon(Icons.Default.DeleteForever, contentDescription = null)
                            Spacer(Modifier.width(4.dp))
                            Text("Delete All Data")
                        }
                    }
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete All Local Records?") },
            text = {
                Text("This permanently deletes all medications, dose history, journal logs, appointments, and saved report summaries from this device.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteAllData()
                        showDeleteConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete Permanently")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            }
        )
    }
}
