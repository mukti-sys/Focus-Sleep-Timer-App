package com.mukti.focus.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mukti.focus.data.model.AppInfo
import com.mukti.focus.ui.theme.TerracottaPrimaryLight

@Composable
fun AppItemRow(
    app: AppInfo,
    onToggle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onToggle(app.packageName) }
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App Icon placeholder / drawable
        androidx.compose.foundation.Canvas(
            modifier = Modifier.size(36.dp),
            onDraw = {
                drawCircle(color = androidx.compose.ui.graphics.Color(0xFFE0E0E0))
            }
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = app.appName,
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 17.sp),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )

        Checkbox(
            checked = app.isDistracting,
            onCheckedChange = { onToggle(app.packageName) },
            colors = CheckboxDefaults.colors(
                checkedColor = TerracottaPrimaryLight,
                checkmarkColor = androidx.compose.ui.graphics.Color.White,
                uncheckedColor = MaterialTheme.colorScheme.outline
            )
        )
    }
}
