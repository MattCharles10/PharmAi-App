
package com.example.pharmai.presentation.component.pill

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pharmai.domain.model.PillSearchCriteria

@Composable
fun PillSearchCriteria(
    criteria: PillSearchCriteria,
    onCriteriaChange: (PillSearchCriteria) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Search by Criteria",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Imprint field
            OutlinedTextField(
                value = criteria.imprint,
                onValueChange = { newValue ->
                    onCriteriaChange(criteria.copy(imprint = newValue))
                },
                label = { Text("Imprint (e.g., BAYER, L10)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Color field
            OutlinedTextField(
                value = criteria.color,
                onValueChange = { newValue ->
                    onCriteriaChange(criteria.copy(color = newValue))
                },
                label = { Text("Color (e.g., White, Blue)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Shape field
            OutlinedTextField(
                value = criteria.shape,
                onValueChange = { newValue ->
                    onCriteriaChange(criteria.copy(shape = newValue))
                },
                label = { Text("Shape (e.g., Round, Oval)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Search button
            Button(
                onClick = onSearch,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = criteria.imprint.isNotBlank() || criteria.color.isNotBlank() || criteria.shape.isNotBlank()
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Search Pills")
            }
        }
    }
}