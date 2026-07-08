package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.WaterSavingTechnique
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EducationScreen(
    techniques: List<WaterSavingTechnique>,
    favoriteTechniqueIds: List<String> = emptyList(),
    onToggleFavorite: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateFlowOf("") }
    var showFavoritesOnly by remember { mutableStateFlowOf(false) }
    var expandedTechniqueId by remember { mutableStateFlowOf<String?>(null) }

    val filteredTechniques = remember(searchQuery, showFavoritesOnly, techniques, favoriteTechniqueIds) {
        val baseList = if (showFavoritesOnly) {
            techniques.filter { favoriteTechniqueIds.contains(it.id) }
        } else {
            techniques
        }

        if (searchQuery.isBlank()) {
            baseList
        } else {
            baseList.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                        it.shortDescription.contains(searchQuery, ignoreCase = true) ||
                        it.detailedDescription.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Search Bar Header (Styled to match the elegant editorial layout)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.padding(start = 24.dp, top = 16.dp, end = 24.dp, bottom = 12.dp)
            ) {
                Text(
                    text = "Conservation Practices",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        letterSpacing = (-0.5).sp
                    )
                )
                Text(
                    text = "Learn modern agricultural and household techniques to reduce groundwater consumption in Haryana.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                )

                // Elegant Search Field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search techniques (e.g. Drip)...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Icon",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear Search"
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f),
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("education_search_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterChip(
                        selected = !showFavoritesOnly,
                        onClick = { showFavoritesOnly = false },
                        label = { Text("All Practices") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.List,
                                contentDescription = "All",
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        shape = RoundedCornerShape(12.dp)
                    )
                    FilterChip(
                        selected = showFavoritesOnly,
                        onClick = { showFavoritesOnly = true },
                        label = { Text("Saved (${favoriteTechniqueIds.size})") },
                        leadingIcon = {
                            Icon(
                                imageVector = if (showFavoritesOnly) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorites",
                                tint = if (showFavoritesOnly) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("favorites_filter_chip")
                    )
                }
            }
        }

        // Techniques List
        if (filteredTechniques.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val emptyIcon = if (showFavoritesOnly) Icons.Default.FavoriteBorder else Icons.Default.SearchOff
                    val emptyTitle = if (showFavoritesOnly) "No Saved Techniques Yet" else "No Conservation Techniques Found"
                    val emptyDesc = if (showFavoritesOnly) {
                        "Tap the heart icon on any technique card to save it for quick offline reference."
                    } else {
                        "Try adjusting your keywords or search query."
                    }

                    Icon(
                        imageVector = emptyIcon,
                        contentDescription = emptyTitle,
                        tint = if (showFavoritesOnly) Color.Red.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        text = emptyTitle,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                    Text(
                        text = emptyDesc,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("techniques_list")
            ) {
                items(filteredTechniques) { technique ->
                    val isExpanded = expandedTechniqueId == technique.id
                    val isFavorite = favoriteTechniqueIds.contains(technique.id)
                    TechniqueItem(
                        technique = technique,
                        isExpanded = isExpanded,
                        isFavorite = isFavorite,
                        onFavoriteClick = { onToggleFavorite(technique.id) },
                        onExpandClick = {
                            expandedTechniqueId = if (isExpanded) null else technique.id
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TechniqueItem(
    technique: WaterSavingTechnique,
    isExpanded: Boolean,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onExpandClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rotationState by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(250),
        label = "ArrowRotation"
    )

    // Match icons dynamically from model values
    val displayIcon = when (technique.iconName) {
        "water" -> Icons.Default.WaterDrop
        "umbrella" -> Icons.Default.Umbrella
        "eco" -> Icons.Default.Spa
        "agriculture" -> Icons.Default.Agriculture
        "gradient" -> Icons.Default.Layers
        else -> Icons.AutoMirrored.Filled.MenuBook
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onExpandClick)
            .testTag("technique_card_${technique.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Main Row Header (Icon + Info + Expand Arrow)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Colored Icon Bubble
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                ) {
                    Icon(
                        imageVector = displayIcon,
                        contentDescription = technique.title,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Title and Short Description
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = technique.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        // Water saving badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(SafeGreen.copy(alpha = 0.12f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Saves ${technique.waterSavedPercent}",
                                color = SafeGreen,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = technique.shortDescription,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        maxLines = if (isExpanded) 10 else 2
                    )
                }

                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier.testTag("favorite_button_${technique.id}")
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                        tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        modifier = Modifier.size(22.dp)
                    )
                }

                IconButton(
                    onClick = onExpandClick,
                    modifier = Modifier.testTag("expand_arrow_${technique.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        modifier = Modifier.rotate(rotationState),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }

            // Expanded content with smooth height and visibility transition
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(animationSpec = tween(250)) + fadeIn(animationSpec = tween(200)),
                exit = shrinkVertically(animationSpec = tween(250)) + fadeOut(animationSpec = tween(200))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

                    Spacer(modifier = Modifier.height(12.dp))

                    // Detailed text description
                    Text(
                        text = technique.detailedDescription,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Steps / Implementation Guides (Editorial layout)
                    Text(
                        text = "HOW TO IMPLEMENT",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    technique.steps.forEachIndexed { index, step ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .padding(top = 2.dp)
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black)
                                )
                            }
                            Text(
                                text = step,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                lineHeight = 20.sp
                            )
                        }
                    }

                    // Haryana Govt Subsidy / Scheme highlight if available
                    if (!technique.haryanaGovtScheme.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Campaign,
                                        contentDescription = "Haryana Scheme",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "HARYANA GOVERNMENT SUPPORT",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    )
                                }
                                Text(
                                    text = technique.haryanaGovtScheme,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Simple state flow helper since standard ktx imports might differ
private fun <T> mutableStateFlowOf(initialValue: T) = mutableStateOf(initialValue)
