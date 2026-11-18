package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.education.EducationArticle
import com.example.data.education.HealthEducationCatalog
import com.example.data.local.AppLanguage
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.SectionHeader
import com.example.ui.state.AiUiState

@Composable
fun HealthEducationScreen(
    language: AppLanguage,
    aiSummaries: Map<String, AiUiState<String>>,
    onSummarizeArticle: (EducationArticle) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isUrdu = language == AppLanguage.URDU
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val filteredArticles = remember(searchQuery, selectedCategory) {
        HealthEducationCatalog.articles.filter { article ->
            val catMatch = selectedCategory == "All" || article.category == selectedCategory
            val q = searchQuery.trim().lowercase()
            val queryMatch = q.isEmpty() ||
                article.titleEn.lowercase().contains(q) ||
                article.titleUr.lowercase().contains(q) ||
                article.summaryEn.lowercase().contains(q) ||
                article.category.lowercase().contains(q)
            catMatch && queryMatch
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Column(modifier = Modifier.widthIn(max = 720.dp)) {
                SectionHeader(
                    title = if (isUrdu) "صحت تعلیمی مرکز" else "Health Education Center",
                    subtitle = "Verified general health guides in English & Urdu with public health sources"
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    placeholder = { Text(if (isUrdu) "موضوع تلاش کریں..." else "Search topics (e.g., sleep, heart, diabetes)...") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("education_search_input")
                )
                Spacer(Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(HealthEducationCatalog.categories) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat) }
                        )
                    }
                }
            }
        }

        if (filteredArticles.isEmpty()) {
            item {
                EmptyStateCard(
                    icon = Icons.Default.MenuBook,
                    title = "No Matching Articles",
                    description = "Try clearing your search filter or selecting 'All' categories.",
                    actionLabel = "Reset Filter",
                    onAction = {
                        searchQuery = ""
                        selectedCategory = "All"
                    },
                    modifier = Modifier.widthIn(max = 720.dp)
                )
            }
        } else {
            items(filteredArticles, key = { it.id }) { article ->
                val summaryState = aiSummaries[article.id]
                Card(
                    modifier = Modifier.widthIn(max = 720.dp).fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = MaterialTheme.shapes.small) {
                            Text(
                                text = article.category,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Text(
                            text = if (isUrdu) article.titleUr else article.titleEn,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = if (isUrdu) article.bodyUr else article.bodyEn,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        HorizontalDivider()
                        Text(
                            text = if (isUrdu) "اکثر پوچھے جانے والے سوالات" else "Frequently Asked Questions",
                            style = MaterialTheme.typography.titleSmall
                        )
                        article.faqs.forEach { faq ->
                            Text(
                                text = "Q: ${if (isUrdu) faq.questionUr else faq.questionEn}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "A: ${if (isUrdu) faq.answerUr else faq.answerEn}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilledTonalButton(
                                onClick = { onSummarizeArticle(article) },
                                enabled = summaryState !is AiUiState.Loading,
                                modifier = Modifier.weight(1f).testTag("edu_ai_summary_${article.id}")
                            ) {
                                if (summaryState is AiUiState.Loading) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                    Spacer(Modifier.width(6.dp))
                                    Text("Summarizing...")
                                } else {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("AI Key Takeaways")
                                }
                            }
                            OutlinedButton(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(article.sourceUrl))
                                    context.startActivity(intent)
                                }
                            ) {
                                Icon(Icons.Default.OpenInNew, contentDescription = "Open source", modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Source")
                            }
                        }

                        when (summaryState) {
                            is AiUiState.Success -> {
                                Surface(color = MaterialTheme.colorScheme.tertiaryContainer, shape = MaterialTheme.shapes.medium) {
                                    Text(
                                        text = summaryState.data,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }
                            is AiUiState.Error -> {
                                Text(summaryState.message, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                            }
                            else -> Unit
                        }

                        Text(
                            text = "Source: ${article.sourceName} • Checked: ${article.dateChecked}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
