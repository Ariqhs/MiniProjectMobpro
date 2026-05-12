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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(
    navController: NavHostController,
    id: Int,
    nama: String,
    nominalLama: Float,
    kurs: Float,
    simbolAsal: String,
    simbolTujuan: String,
    tipeLama: String
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val dao = RiwayatDatabase.getDatabase(context).riwayatDao()

    val initialInput = if (nominalLama % 1.0f == 0f) nominalLama.toInt().toString() else nominalLama.toString()
    var inputAmount by rememberSaveable { mutableStateOf(initialInput) }
    var inputError by rememberSaveable { mutableStateOf(false) }

    val optAsalKeTujuan = "$simbolAsal ke $simbolTujuan"
    val optTujuanKeAsal = "$simbolTujuan ke $simbolAsal"
    val radioOptions = listOf(optAsalKeTujuan, optTujuanKeAsal)

    val (selectedOption, onOptionSelected) = remember { mutableStateOf(tipeLama) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Ubah Riwayat") },
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
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Edit Konversi $nama",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 9.dp)
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

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

            Button(
                onClick = {
                    inputError = (inputAmount.isBlank() || inputAmount == "0")
                    if (inputError) return@Button

                    val amount = inputAmount.toFloatOrNull() ?: 0f

                    val hasilBaru = when (selectedOption) {
                        optAsalKeTujuan -> amount * kurs
                        optTujuanKeAsal -> amount / kurs
                        else -> 0f
                    }

                    scope.launch {
                        val riwayatUpdate = RiwayatKonversi(
                            id = id,
                            mataUang = nama,
                            nominal = amount,
                            hasil = hasilBaru,
                            tipeKonversi = selectedOption
                        )
                        dao.updateRiwayat(riwayatUpdate)
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                Text(text = "Simpan Perubahan")
            }
        }
    }
}