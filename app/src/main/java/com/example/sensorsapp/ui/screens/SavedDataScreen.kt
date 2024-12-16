package com.example.sensorsapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sensorsapp.R
import com.example.sensorsapp.ui.AppViewModelProvider
import com.example.sensorsapp.ui.data.SavedDataViewModel
import com.example.sensorsapp.ui.room.Measurement

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedDataScreen(
    navigateToEntry: () -> Unit,
    navigateToUpdate: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SavedDataViewModel = viewModel(factory = AppViewModelProvider.Factory)
){
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val savedDataUiState by viewModel.savedDataUiState.collectAsState()
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Sensor App")
                }
            )
        },
//        floatingActionButton = {
//            FloatingActionButton(
//                //
//            ) {
//                Icon(
//                    //
//                )
//            }
//        }
    ){ innerPadding ->

        SavedDataBody(
            entryList = savedDataUiState.entryList,
            onEntryClick = navigateToUpdate,
            modifier = modifier.padding(innerPadding)
                .fillMaxSize()
        )

    }
}

@Composable
private fun SavedDataBody(
    entryList: List<Measurement>,
    onEntryClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        if (entryList.isEmpty()) {
            Text(
                text = "stringResource(R.string.no_item_description)",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(contentPadding),
            )
        } else {
            EntryList(
                entryList = entryList,
                onItemClick = { onEntryClick(it.id) },
                contentPadding = contentPadding,
                modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small))
            )
        }
    }
}

@Composable
private fun EntryList(
    entryList: List<Measurement>,
    onItemClick: (Measurement) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        items(items = entryList, key = { it.id }) { item ->
            ArchiveEntry(item = item,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_small))
                    .clickable { onItemClick(item) })
        }
    }
}

@Composable
private fun ArchiveEntry(
    item: Measurement, modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleLarge,
                )
                Spacer(Modifier.weight(1f))
//                Text(
//                    text = item.formatedPrice(),
//                    style = MaterialTheme.typography.titleMedium
//                )
            }
//            Text(
//                text = stringResource(R.string.in_stock, item.quantity),
//                style = MaterialTheme.typography.titleMedium
//            )
        }
    }
}
