package com.ariqhisyamsyahputra0025.mobpro1.ui.screen

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Info
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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.layout.ContentScale
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
import com.ariqhisyamsyahputra0025.mobpro1.navigation.Screen
import com.ariqhisyamsyahputra0025.mobpro1.ui.theme.Mobpro1Theme

// 1. Data Class untuk menyimpan struktur menu
data class MenuMataUang(
    val idRoute: String,
    val idStringNama: Int, // Menggunakan Int agar bisa memanggil stringResource
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
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                actions = {
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
    // 2. Daftar menu dimasukkan ke dalam List
    val daftarMenu = listOf(
        MenuMataUang("dollar", R.string.kurs_dollar, R.drawable.bg_btn_dollar),
        MenuMataUang("euro", R.string.kurs_euro, R.drawable.bg_btn_euro),
        MenuMataUang("japaneseYen", R.string.kurs_yen, R.drawable.bg_btn_yen),
        MenuMataUang("mbg", R.string.kurs_mbg, R.drawable.bg_btn_omprengmbbg)
    )

    // 3. Menggunakan LazyVerticalGrid pengganti Column vertikal biasa
    LazyVerticalGrid(
        columns = GridCells.Fixed(2), // Menetapkan 2 kolom
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.fillMaxSize()
    ) {
        // Bagian Header / Judul (Memakan 2 kolom penuh)
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

        // Bagian Tombol Menu (Di-generate otomatis dari List)
        items(daftarMenu) { menu ->
            TombolMenuMataUang(
                menu = menu,
                onClick = { navController.navigate(menu.idRoute) }
            )
        }

        // Bagian Footer / Kartu (Memakan 2 kolom penuh)
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
    }
}

// 4. Komponen cetakan untuk satu tombol
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
                contentScale = ContentScale.Crop, // Tambahan agar gambar bulat sempurna
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