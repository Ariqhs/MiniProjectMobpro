package com.ariqhisyamsyahputra0025.mobpro1.ui.screen

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.ariqhisyamsyahputra0025.mobpro1.R
import com.ariqhisyamsyahputra0025.mobpro1.navigation.Screen
import com.ariqhisyamsyahputra0025.mobpro1.ui.theme.Mobpro1Theme
import java.text.NumberFormat
import java.util.Locale

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
        ScreenContent(Modifier.padding(innerPadding))
    }
}

@Composable
fun ScreenContent(modifier: Modifier = Modifier) {
    var inputAmount by remember { mutableStateOf("") }
    var resultValue by remember { mutableFloatStateOf(0f) }
    var inputError by remember { mutableStateOf(false) }

    val radioOptions = listOf("USD ke IDR", "IDR ke USD", "IDR ke MBG")
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }

    val context = LocalContext.current
    val exchangeRateUsdToIdr = 16000f
    val exchangeRateIdrToMbg = 15000f

    val numberFormatter = remember { NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID")) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Pilih jenis konversi dan masukkan nominal:",
            style = MaterialTheme.typography.bodyLarge
        )

        // Radio Button Group
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
            label = { Text(text = "Nominal") },
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
                        "USD ke IDR" -> amount * exchangeRateUsdToIdr
                        "IDR ke USD" -> amount / exchangeRateUsdToIdr
                        "IDR ke MBG" -> amount / exchangeRateIdrToMbg
                        else -> 0f
                    }
                },
                modifier = Modifier.padding(end = 12.dp),
                contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp)
            ) {
                Text(text = "Hitung")
            }

            OutlinedButton(
                onClick = {
                    inputAmount = ""
                    resultValue = 0f
                    inputError = false
                },
                contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp)
            ) {
                Text(text = "Reset")
            }
        }

        if (resultValue != 0f) {
            val targetCurrency = selectedOption.split(" ").last()
            val formattedResult = numberFormatter.format(resultValue.toLong())
            val message = "Hasil konversi $selectedOption: $formattedResult $targetCurrency"

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            Text(text = "Hasil Konversi:", style = MaterialTheme.typography.bodyLarge)
            Text(
                text = "$formattedResult $targetCurrency",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Button(
                onClick = { shareData(context, message) },
                modifier = Modifier.padding(top = 16.dp),
                contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp)
            ) {
                Text(text = stringResource(id = R.string.bagikan))
            }
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
        Text(text = "Input tidak boleh kosong atau 0")
    }
}

private fun shareData(context: Context, message: String) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, message)
    }
    context.startActivity(Intent.createChooser(shareIntent, null))
}