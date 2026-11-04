package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Project
import com.example.ui.components.formatTimestamp
import com.example.ui.theme.CosmicBlack
import com.example.ui.theme.DeepSpace
import com.example.ui.theme.DeepViolet
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.viewmodel.EditorSection
import com.example.ui.viewmodel.EditorViewModel

@Composable
fun RecentVideosScreen(
    viewModel: EditorViewModel,
    modifier: Modifier = Modifier
) {
    val allProjects by viewModel.allProjects.collectAsState()
    val activeProject by viewModel.activeProject.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepSpace)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(DeepViolet),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "3. RECENT VIDEOS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Project Library & Export History",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                }
            }

            // New Project Button
            Button(
                onClick = {
                    viewModel.generateVideoFromPrompt()
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonViolet),
                modifier = Modifier.testTag("new_project_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = TextWhite, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("New Project", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextWhite)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (allProjects.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("No recent video projects. Tap 'New Project' to create one.", color = TextMuted)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(allProjects) { proj ->
                    ProjectCardItem(
                        project = proj,
                        isActive = proj.id == activeProject?.id,
                        onOpen = {
                            viewModel.selectProject(proj.id)
                            viewModel.setSection(EditorSection.EDIT)
                        },
                        onExport = {
                            viewModel.selectProject(proj.id)
                            viewModel.openExportDialog()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProjectCardItem(
    project: Project,
    isActive: Boolean,
    onOpen: () -> Unit,
    onExport: () -> Unit
) {
    Surface(
        onClick = onOpen,
        shape = RoundedCornerShape(14.dp),
        color = SurfaceDark,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isActive) NeonCyan else SurfaceBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("project_item_${project.id}")
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Video Thumbnail Box
            Box(
                modifier = Modifier
                    .size(width = 80.dp, height = 54.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF312E81), Color(0xFF1E1B4B))
                        )
                    )
                    .border(1.dp, if (isActive) NeonPink else SurfaceBorder, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Movie,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = project.name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        maxLines = 1
                    )
                    if (isActive) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(shape = RoundedCornerShape(4.dp), color = DeepViolet) {
                            Text(
                                text = "ACTIVE",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonPink,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "${project.aspectRatio.label} • ${project.resolution.label} • ${formatTimestamp(project.durationMs)}",
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }

            // Action Buttons
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onExport, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Download, contentDescription = "Export", tint = NeonCyan, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onOpen, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Open Editor", tint = TextWhite, modifier = Modifier.size(22.dp))
                }
            }
        }
    }
}
