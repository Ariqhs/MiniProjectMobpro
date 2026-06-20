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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.ClearCredentialException
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.ariqhisyamsyahputra0025.mobpro1.BuildConfig
import com.ariqhisyamsyahputra0025.mobpro1.R
import com.ariqhisyamsyahputra0025.mobpro1.model.User
import com.ariqhisyamsyahputra0025.mobpro1.navigation.Screen
import com.ariqhisyamsyahputra0025.mobpro1.network.ApiState
import com.ariqhisyamsyahputra0025.mobpro1.network.DiaryEntry
import com.ariqhisyamsyahputra0025.mobpro1.network.UserDataStore
import com.ariqhisyamsyahputra0025.mobpro1.SettingPreferences
import com.ariqhisyamsyahputra0025.mobpro1.ui.MainViewModel
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.Dispatchers
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
    viewModel: MainViewModel = viewModel()
) {
    val context = LocalContext.current
    val pref = remember { SettingPreferences(context) }
    val isDarkMode by pref.getThemeSetting.collectAsState(initial = false)
    val scope = rememberCoroutineScope()

    val dataStore = remember { UserDataStore(context) }
    val user by dataStore.userFlow.collectAsState(initial = User())
    var showProfilDialog by remember { mutableStateOf(false) }

    var showAddDialog by remember { mutableStateOf(false) }
    var bitmap: Bitmap? by remember { mutableStateOf(null) }

    val launcher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        bitmap = getCroppedImage(context.contentResolver, result)
        if (bitmap != null) showAddDialog = true
    }

    LaunchedEffect(user.email) {
        if (user.email.isNotEmpty()) {
            viewModel.getDiaries(user.email)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.app_name)) },
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
                            contentDescription = stringResource(R.string.tentang_aplikasi),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = {
                        if (user.email.isEmpty()) {
                            scope.launch(Dispatchers.IO) { signIn(context, dataStore) }
                        } else {
                            showProfilDialog = true
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = stringResource(R.string.profil),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (user.email.isNotEmpty()) {
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
        }
    ) { innerPadding ->
        ScreenContent(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            viewModel = viewModel,
            userEmail = user.email
        )

        if (showAddDialog && bitmap != null && user.email.isNotEmpty()) {
            AddDiaryDialog(
                bitmap = bitmap!!,
                onDismiss = { showAddDialog = false },
                onSave = { title, amount, currency ->
                    viewModel.addEntry(user.email, title, amount, currency, bitmap!!)
                    showAddDialog = false
                    Toast.makeText(context, "Menyimpan ke server...", Toast.LENGTH_SHORT).show()
                }
            )
        }

        if (showProfilDialog) {
            ProfilDialog(
                user = user,
                onDismissRequest = { showProfilDialog = false },
                onConfirmation = {
                    scope.launch(Dispatchers.IO) { signOut(context, dataStore) }
                    showProfilDialog = false
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

                    if (userEmail.isEmpty()) {
                        Text(
                            text = "Silahkan login menggunakan akun Google di pojok kanan atas untuk menyimpan diary Anda ke server.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                }
            }

            if (userEmail.isNotEmpty()) {
                when (diariesState) {
                    is ApiState.Loading -> {
                        item(span = { GridItemSpan(2) }) {
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                    is ApiState.Error -> {
                        item(span = { GridItemSpan(2) }) {
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

                        if (diaries.isEmpty()) {
                            item(span = { GridItemSpan(2) }) {
                                Text(
                                    text = "Belum ada diary tersimpan.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(vertical = 16.dp)
                                )
                            }
                        } else {
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
                        }
                    }
                    else -> {}
                }
            }

            item(span = { GridItemSpan(2) }) {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    if (itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("Hapus Data") },
            text = { Text("Apakah Anda yakin ingin menghapus '${itemToDelete?.title}' ?") },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDiaryDialog(
    bitmap: Bitmap,
    onDismiss: () -> Unit,
    onSave: (String, Double, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }

    // Persiapan data untuk dropdown
    val currencyOptions = listOf("USD", "EUR", "JPY", "MBG", "IDR")
    var currency by remember { mutableStateOf(currencyOptions[0]) } // Set nilai default ke USD
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Pengeluaran") },
        text = {
            Column {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Nama Barang/Pengalaman") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = currency,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Mata Uang") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        currencyOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    currency = option
                                    expanded = false // Tutup menu setelah dipilih
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Harga/Nominal") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
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
                Text(stringResource(R.string.simpan))
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


private suspend fun signIn(context: Context, dataStore: UserDataStore) {
    val googleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(BuildConfig.API_KEY)
        .build()

    val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    try {
        val credentialManager = CredentialManager.create(context)
        val result = credentialManager.getCredential(context, request)
        handleSignIn(result, dataStore)
    } catch (e: GetCredentialException) {
        Log.e("SIGN-IN", "Error: ${e.errorMessage}")
    }
}

private suspend fun handleSignIn(result: androidx.credentials.GetCredentialResponse, dataStore: UserDataStore) {
    val credential = result.credential
    if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
        try {
            val googleId = GoogleIdTokenCredential.createFrom(credential.data)
            val nama = googleId.displayName ?: ""
            val email = googleId.id
            val photoUrl = googleId.profilePictureUri?.toString() ?: ""
            dataStore.saveData(User(nama, email, photoUrl))
        } catch (e: GoogleIdTokenParsingException) {
            Log.e("SIGN-IN", "Error: ${e.message}")
        }
    } else {
        Log.e("SIGN-IN", "Error: unrecognized custom credential type.")
    }
}

private suspend fun signOut(context: Context, dataStore: UserDataStore) {
    try {
        val credentialManager = CredentialManager.create(context)
        credentialManager.clearCredentialState(ClearCredentialStateRequest())
        dataStore.saveData(User())
    } catch (e: ClearCredentialException) {
        Log.e("SIGN-IN", "Error: ${e.errorMessage}")
    }
}

@Suppress("DEPRECATION")
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
        Text(text = "Input tidak boleh kosong atau 0")
    }
}

fun shareData(context: Context, message: String) {
    val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(android.content.Intent.EXTRA_TEXT, message)
    }
    context.startActivity(android.content.Intent.createChooser(shareIntent, null))
}