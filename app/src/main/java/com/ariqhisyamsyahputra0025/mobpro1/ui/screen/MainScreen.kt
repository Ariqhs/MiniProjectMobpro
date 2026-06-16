package com.ariqhisyamsyahputra0025.mobpro1.ui.screen

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.ariqhisyamsyahputra0025.mobpro1.R
import com.ariqhisyamsyahputra0025.mobpro1.navigation.Screen
import com.ariqhisyamsyahputra0025.mobpro1.network.ApiState
import com.ariqhisyamsyahputra0025.mobpro1.network.DiaryEntry
import com.ariqhisyamsyahputra0025.mobpro1.pref.SettingPreferences
import com.ariqhisyamsyahputra0025.mobpro1.ui.MainViewModel
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import kotlinx.coroutines.launch

data class MenuMataUang(
    val namaMataUang: String,
    val kurs: Float,
    val simbolAsal: String,
    val simbolTujuan: String,
    val idStringNama: Int,
    val ikonRes: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavHostController,
    userEmail: String,
    viewModel: MainViewModel = viewModel()
) {
    val context = LocalContext.current
    val pref = remember { SettingPreferences(context) }
    val isDarkMode by pref.getThemeSetting.collectAsState(initial = false)
    val scope = rememberCoroutineScope()

    var showAddDialog by remember { mutableStateOf(false) }
    var bitmap: Bitmap? by remember { mutableStateOf(null) }

    val launcher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        bitmap = getCroppedImage(context.contentResolver, result)
        if (bitmap != null) showAddDialog = true
    }

    LaunchedEffect(userEmail) {
        viewModel.getDiaries(userEmail)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Travel Diary") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                actions = {
                    IconButton(onClick = { scope.launch { pref.saveThemeSetting(!isDarkMode) } }) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                            contentDescription = "Ganti Tema",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = { navController.navigate(Screen.About.route) }) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = "Info",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                val options = CropImageContractOptions(
                    null, CropImageOptions(
                        imageSourceIncludeGallery = false,
                        imageSourceIncludeCamera = true,
                        fixAspectRatio = true
                    )
                )
                launcher.launch(options)
            }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah Diary")
            }
        }
    ) { innerPadding ->
        ScreenContent(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            viewModel = viewModel,
            userEmail = userEmail
        )

        if (showAddDialog && bitmap != null) {
            AddDiaryDialog(
                bitmap = bitmap!!,
                onDismiss = { showAddDialog = false },
                onSave = { title, amount, currency ->
                    viewModel.addEntry(userEmail, title, amount, currency, bitmap!!)
                    showAddDialog = false
                    Toast.makeText(context, "Menyimpan ke server...", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

@Composable
fun ScreenContent(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: MainViewModel,
    userEmail: String
) {
    val context = LocalContext.current
    val pref = remember { SettingPreferences(context) }
    val isGridLayout by pref.getLayoutSetting.collectAsState(initial = false)
    val scope = rememberCoroutineScope()

    val diariesState by viewModel.diaries.collectAsState()
    var itemToDelete by remember { mutableStateOf<DiaryEntry?>(null) }

    val daftarMenu = listOf(
        MenuMataUang("Dollar", 16000f, "USD", "IDR", R.string.kurs_dollar, R.drawable.bg_btn_dollar),
        MenuMataUang("Euro", 17500f, "EUR", "IDR", R.string.kurs_euro, R.drawable.bg_btn_euro),
        MenuMataUang("Yen", 105f, "JPY", "IDR", R.string.kurs_yen, R.drawable.bg_btn_yen),
        MenuMataUang("MBG", 15000f, "MBG", "IDR", R.string.kurs_mbg, R.drawable.bg_btn_omprengmbbg)
    )

    Column(modifier = modifier.fillMaxSize()) {
        when (diariesState) {
            is ApiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is ApiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Gagal mengambil data dari internet.")
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.getDiaries(userEmail) }) {
                            Text("Coba Lagi")
                        }
                    }
                }
            }
            is ApiState.Success -> {
                val diaries = (diariesState as ApiState.Success<List<DiaryEntry>>).data

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item(span = { GridItemSpan(2) }) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Kalkulator Cepat",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                    }

                    items(daftarMenu) { menu ->
                        TombolMenuMataUang(menu = menu, onClick = {
                            navController.navigate("calculator/${menu.namaMataUang}/${menu.kurs}/${menu.simbolAsal}/${menu.simbolTujuan}")
                        })
                    }

                    item(span = { GridItemSpan(2) }) {
                        Column(modifier = Modifier.padding(top = 24.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Riwayat Perjalanan & Pengeluaran",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                IconButton(onClick = { scope.launch { pref.saveLayoutSetting(!isGridLayout) } }) {
                                    Icon(
                                        imageVector = if (isGridLayout) Icons.AutoMirrored.Filled.List else Icons.Filled.GridView,
                                        contentDescription = "Ganti Tampilan",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            HorizontalDivider(modifier = Modifier.padding(bottom = 8.dp))

                            if (diaries.isEmpty()) {
                                Text(
                                    text = "Belum ada diary tersimpan di server.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(vertical = 16.dp)
                                )
                            }
                        }
                    }

                    items(
                        items = diaries,
                        key = { it.id },
                        span = { GridItemSpan(if (isGridLayout) 1 else maxLineSpan) }
                    ) { diary ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column {
                                AsyncImage(
                                    model = diary.imageUrl,
                                    contentDescription = diary.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxWidth().height(120.dp)
                                )
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(text = diary.title, fontWeight = FontWeight.Bold)
                                    Text(text = "${diary.currencyCode} ${diary.foreignAmount}")
                                    Text(text = "Rp ${diary.convertedIdr}", color = MaterialTheme.colorScheme.primary)

                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                        IconButton(onClick = { itemToDelete = diary }) {
                                            Icon(Icons.Filled.Delete, contentDescription = "Hapus", tint = Color.Red)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item(span = { GridItemSpan(2) }) {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
            else -> {}
        }
    }

    if (itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("Hapus Data") },
            text = { Text("Apakah Anda yakin ingin menghapus '${itemToDelete?.title}' dari server?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteEntry(userEmail, itemToDelete!!.id)
                    itemToDelete = null
                }) {
                    Text("Hapus", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) { Text("Batal") }
            }
        )
    }
}

@Composable
fun AddDiaryDialog(
    bitmap: Bitmap,
    onDismiss: () -> Unit,
    onSave: (String, Double, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var currency by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Pengeluaran") },
        text = {
            Column {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().aspectRatio(1f).clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Nama Barang/Pengalaman") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = currency,
                    onValueChange = { currency = it.uppercase() },
                    label = { Text("Mata Uang (Contoh: USD)") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Harga/Nominal") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: 0.0
                    onSave(title, amount, currency)
                },
                enabled = title.isNotBlank() && currency.isNotBlank() && amountText.isNotBlank()
            ) {
                Text("Simpan ke Server")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}

@Composable
fun TombolMenuMataUang(menu: MenuMataUang, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.height(140.dp),
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
                modifier = Modifier.size(70.dp).padding(bottom = 8.dp).clip(CircleShape)
            )
            Text(text = stringResource(id = menu.idStringNama), textAlign = TextAlign.Center)
        }
    }
}

private fun getCroppedImage(resolver: ContentResolver, result: CropImageView.CropResult): Bitmap? {
    if (!result.isSuccessful) {
        Log.e("IMAGE", "Error: ${result.error}")
        return null
    }
    val uri = result.uriContent ?: return null
    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
        MediaStore.Images.Media.getBitmap(resolver, uri)
    } else {
        val source = ImageDecoder.createSource(resolver, uri)
        ImageDecoder.decodeBitmap(source)
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
    val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(android.content.Intent.EXTRA_TEXT, message)
    }
    context.startActivity(android.content.Intent.createChooser(shareIntent, null))
}