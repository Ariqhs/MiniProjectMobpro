package com.ariqhisyamsyahputra0025.mobpro1.ui.screen

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.ariqhisyamsyahputra0025.mobpro1.R
import com.ariqhisyamsyahputra0025.mobpro1.pref.SettingPreferences
import com.ariqhisyamsyahputra0025.mobpro1.database.RiwayatDatabase
import com.ariqhisyamsyahputra0025.mobpro1.database.RiwayatKonversi
import com.ariqhisyamsyahputra0025.mobpro1.navigation.Screen
import com.ariqhisyamsyahputra0025.mobpro1.ui.theme.Mobpro1Theme
import kotlinx.coroutines.launch

data class MenuMataUang(
    val namaMataUang: String,
    val kurs: Float,
    val simbolAsal: String,
    val simbolTujuan: String,
    val idStringNama: Int,
    val ikonRes: Int
)

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun MainScreenPreview() {
    Mobpro1Theme {
        MainScreen(rememberNavController())
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController) {
    val context = LocalContext.current
    val pref = SettingPreferences(context)
    val isDarkMode by pref.getThemeSetting.collectAsState(initial = false)
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                actions = {
                    IconButton(
                        onClick = {
                            scope.launch {
                                pref.saveThemeSetting(!isDarkMode)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                            contentDescription = "Ganti Tema",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = { navController.navigate(Screen.About.route) }) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = stringResource(R.string.tentang_aplikasi),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        ScreenContent(
            modifier = Modifier.padding(innerPadding),
            navController = navController
        )
    }
}

@Composable
fun ScreenContent(modifier: Modifier = Modifier, navController: NavHostController) {
    val context = LocalContext.current
    val database = RiwayatDatabase.getDatabase(context)
    val dao = database.riwayatDao()

    val daftarRiwayat by dao.getAllRiwayat().collectAsState(initial = emptyList())
    var riwayatYangMauDihapus by remember { mutableStateOf<RiwayatKonversi?>(null) }
    val scope = rememberCoroutineScope()

    val pref = SettingPreferences(context)
    val isGridLayout by pref.getLayoutSetting.collectAsState(initial = false)

    val daftarMenu = listOf(
        MenuMataUang("Dollar", 16000f, "USD", "IDR", R.string.kurs_dollar, R.drawable.bg_btn_dollar),
        MenuMataUang("Euro", 17500f, "EUR", "IDR", R.string.kurs_euro, R.drawable.bg_btn_euro),
        MenuMataUang("Yen", 105f, "JPY", "IDR", R.string.kurs_yen, R.drawable.bg_btn_yen),
        MenuMataUang("MBG", 15000f, "MBG", "IDR", R.string.kurs_mbg, R.drawable.bg_btn_omprengmbbg)
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.fillMaxSize()
    ) {
        item(span = { GridItemSpan(2) }) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.judul_halaman_utama),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 4.dp, top = 18.dp)
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }
        }

        items(daftarMenu) { menu ->
            TombolMenuMataUang(
                menu = menu,
                onClick = {
                    navController.navigate("calculator/${menu.namaMataUang}/${menu.kurs}/${menu.simbolAsal}/${menu.simbolTujuan}")
                }
            )
        }

        item(span = { GridItemSpan(2) }) {
            Column {
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    val gradientColors = listOf(Red, Blue)
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.card_kurs),
                            style = TextStyle(
                                brush = Brush.linearGradient(
                                    colors = gradientColors
                                )
                            )
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        Text(text = "\uD83C\uDDFA\uD83C\uDDF8 1 USD = Rp 16.000        \uD83C\uDDEA\uD83C\uDDFA 1 EUR = Rp 17.500", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "\uD83C\uDDEF\uD83C\uDDF5 1 JPY = Rp 105              \uD83C\uDF72 1 MBG = Rp 15.000", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        item(span = { GridItemSpan(2) }) {
            Column(modifier = Modifier.padding(top = 24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Riwayat Konversi Terakhir",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (daftarRiwayat.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    pref.saveLayoutSetting(!isGridLayout)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (isGridLayout) Icons.AutoMirrored.Filled.List else Icons.Filled.GridView,
                                contentDescription = "Ganti Tampilan",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(bottom = 8.dp))

                if (daftarRiwayat.isEmpty()) {
                    Text(
                        text = "Belum ada riwayat konversi.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
            }
        }

        items(
            items = daftarRiwayat,
            key = { riwayat -> riwayat.id },
            span = { GridItemSpan(if (isGridLayout) 1 else maxLineSpan) }
        ) { riwayat ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                if (isGridLayout) {
                    Column(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = riwayat.tipeKonversi, style = MaterialTheme.typography.labelLarge)
                        Text(
                            text = "${riwayat.nominal} -> ${riwayat.hasil}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            IconButton(onClick = {
                                val menu = daftarMenu.find { it.namaMataUang == riwayat.mataUang }
                                val kurs = menu?.kurs ?: 0f
                                val simbolAsal = menu?.simbolAsal ?: ""
                                val simbolTujuan = menu?.simbolTujuan ?: ""
                                navController.navigate("edit/${riwayat.id}/${riwayat.mataUang}/${riwayat.nominal}/${kurs}/${simbolAsal}/${simbolTujuan}/${riwayat.tipeKonversi}")
                            }) {
                                Icon(imageVector = Icons.Filled.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                            }
                            IconButton(onClick = { riwayatYangMauDihapus = riwayat }) {
                                Icon(imageVector = Icons.Filled.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = riwayat.tipeKonversi, style = MaterialTheme.typography.labelLarge)
                            Text(
                                text = "${riwayat.nominal} -> ${riwayat.hasil}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Row {
                            IconButton(onClick = {
                                val menu = daftarMenu.find { it.namaMataUang == riwayat.mataUang }
                                val kurs = menu?.kurs ?: 0f
                                val simbolAsal = menu?.simbolAsal ?: ""
                                val simbolTujuan = menu?.simbolTujuan ?: ""
                                navController.navigate("edit/${riwayat.id}/${riwayat.mataUang}/${riwayat.nominal}/${kurs}/${simbolAsal}/${simbolTujuan}/${riwayat.tipeKonversi}")
                            }) {
                                Icon(imageVector = Icons.Filled.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                            }
                            IconButton(onClick = { riwayatYangMauDihapus = riwayat }) {
                                Icon(imageVector = Icons.Filled.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }

    if (riwayatYangMauDihapus != null) {
        AlertDialog(
            onDismissRequest = { riwayatYangMauDihapus = null },
            title = {
                Text(text = "Hapus Data")
            },
            text = {
                Text(text = "Apakah Anda yakin ingin menghapus data konversi ${riwayatYangMauDihapus?.nominal} ke ${riwayatYangMauDihapus?.tipeKonversi}?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            dao.deleteRiwayat(riwayatYangMauDihapus!!)
                            riwayatYangMauDihapus = null
                        }
                    }
                ) {
                    Text("Hapus", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { riwayatYangMauDihapus = null }
                ) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun TombolMenuMataUang(menu: MenuMataUang, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.height(160.dp),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = menu.ikonRes),
                contentDescription = stringResource(id = menu.idStringNama),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(90.dp)
                    .padding(bottom = 8.dp)
                    .clip(CircleShape)
            )
            Text(
                text = stringResource(id = menu.idStringNama),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun IconPicker(isError: Boolean, unit: String) {
    if (isError) {
        Icon(imageVector = Icons.Filled.Warning, contentDescription = null)
    } else {
        Text(text = unit)
    }
}

@Composable
fun ErrorHint(isError: Boolean) {
    if (isError) {
        Text(text = "Input tidak bisa kosong atau 0")
    }
}

fun shareData(context: Context, message: String) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, message)
    }
    context.startActivity(Intent.createChooser(shareIntent, null))
}