package com.ariqhisyamsyahputra0025.mobpro1.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ariqhisyamsyahputra0025.mobpro1.R
import com.ariqhisyamsyahputra0025.mobpro1.database.RiwayatDatabase
import com.ariqhisyamsyahputra0025.mobpro1.database.RiwayatKonversi
import com.ariqhisyamsyahputra0025.mobpro1.navigation.Screen
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    navController: NavHostController,
    namaMataUang: String,
    kurs: Float,
    simbolAsal: String,
    simbolTujuan: String
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
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
        CalculatorContent(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            namaMataUang = namaMataUang,
            kurs = kurs,
            simbolAsal = simbolAsal,
            simbolTujuan = simbolTujuan
        )
    }
}

@Composable
fun CalculatorContent(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    namaMataUang: String,
    kurs: Float,
    simbolAsal: String,
    simbolTujuan: String
) {
    // 1. Siapkan CoroutineScope dan DAO untuk menyimpan data ke database
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val dao = RiwayatDatabase.getDatabase(context).riwayatDao()

    var inputAmount by rememberSaveable { mutableStateOf("") }
    var resultValue by rememberSaveable { mutableFloatStateOf(0f) }
    var inputError by rememberSaveable { mutableStateOf(false) }

    val optAsalKeTujuan = "$simbolAsal ke $simbolTujuan"
    val optTujuanKeAsal = "$simbolTujuan ke $simbolAsal"

    val radioOptions = listOf(optAsalKeTujuan, optTujuanKeAsal)
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }

    val numberFormatter = remember { NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID")) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Kalkulator Kurs $namaMataUang",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 9.dp)
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
        Text(
            text = stringResource(id = R.string.instruksi_teks),
            style = MaterialTheme.typography.bodyLarge
        )

        Column(Modifier.selectableGroup()) {
            radioOptions.forEach { text ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .selectable(
                            selected = (text == selectedOption),
                            onClick = { onOptionSelected(text) },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (text == selectedOption),
                        onClick = null
                    )
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }

        OutlinedTextField(
            value = inputAmount,
            onValueChange = { inputAmount = it },
            label = { Text(text = stringResource(id = R.string.label_nominal)) },
            isError = inputError,
            trailingIcon = { IconPicker(inputError, selectedOption.split(" ")[0]) },
            supportingText = { ErrorHint(inputError) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    inputError = (inputAmount.isBlank() || inputAmount == "0")
                    if (inputError) return@Button

                    val amount = inputAmount.toFloatOrNull() ?: 0f

                    resultValue = when (selectedOption) {
                        optAsalKeTujuan -> amount * kurs
                        optTujuanKeAsal -> amount / kurs
                        else -> 0f
                    }

                    // 2. Simpan hasil perhitungan ke Database
                    scope.launch {
                        val riwayatBaru = RiwayatKonversi(
                            mataUang = namaMataUang,
                            nominal = amount,
                            hasil = resultValue,
                            tipeKonversi = selectedOption
                        )
                        dao.insertRiwayat(riwayatBaru)
                    }
                },
                modifier = Modifier.padding(end = 12.dp),
                contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp)
            ) {
                Text(text = stringResource(id = R.string.tombol_hitung))
            }

            OutlinedButton(
                onClick = {
                    inputAmount = ""
                    resultValue = 0f
                    inputError = false
                },
                contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp)
            ) {
                Text(text = stringResource(id = R.string.tombol_reset))
            }
        }

        if (resultValue != 0f) {
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            val targetCurrency = selectedOption.split(" ").last()
            val formattedResult = numberFormatter.format(resultValue.toLong())

            val message = stringResource(
                id = R.string.template_hasil_konversi,
                selectedOption,
                formattedResult,
                targetCurrency
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = stringResource(id = R.string.judul_hasil_konversi), style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = "$formattedResult $targetCurrency",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Button(
                    onClick = { shareData(context, message) },
                    modifier = Modifier.padding(top = 1.dp),
                    contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp)
                ) {
                    Text(text = stringResource(id = R.string.bagikan))
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
        }

        OutlinedButton(
            onClick = { navController.navigate(Screen.Home.route) },
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            Text(text = "Kembali ke Menu Utama")
        }
    }
}