package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.AdrGuideScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.ManualLookupScreen
import com.example.ui.screens.ScannerScreen
import com.example.ui.theme.AdrOrange
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AdrPlateViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: AdrPlateViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    viewModel: AdrPlateViewModel
) {
    val selectedTab by viewModel.selectedTab.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = AdrOrange,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "33",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
                                    color = Color.Black
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "ADR Levha Okuyucu",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "Tehlikeli Madde Plaka ve Kod Analizi",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .testTag("bottom_navigation_bar")
                    .windowInsetsPadding(WindowInsets.navigationBars),
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { viewModel.setSelectedTab(0) },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 0) Icons.Filled.PhotoCamera else Icons.Outlined.PhotoCamera,
                            contentDescription = "Plaka Tara"
                        )
                    },
                    label = { Text("Plaka Tara", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("nav_item_scanner")
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { viewModel.setSelectedTab(1) },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 1) Icons.Filled.Pin else Icons.Outlined.Pin,
                            contentDescription = "Kod Sorgula"
                        )
                    },
                    label = { Text("Kod Sorgula", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("nav_item_lookup")
                )

                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { viewModel.setSelectedTab(2) },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 2) Icons.Filled.History else Icons.Outlined.History,
                            contentDescription = "Geçmiş"
                        )
                    },
                    label = { Text("Geçmiş", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("nav_item_history")
                )

                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { viewModel.setSelectedTab(3) },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 3) Icons.AutoMirrored.Filled.MenuBook else Icons.AutoMirrored.Outlined.MenuBook,
                            contentDescription = "ADR Rehber"
                        )
                    },
                    label = { Text("Rehber", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("nav_item_guide")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> ScannerScreen(viewModel = viewModel)
                1 -> ManualLookupScreen(viewModel = viewModel)
                2 -> HistoryScreen(viewModel = viewModel)
                3 -> AdrGuideScreen()
            }
        }
    }
}
