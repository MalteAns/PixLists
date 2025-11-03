package de.malteans.pixlists.lists.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.malteans.pixlists.core.presentation.components.CustomDialog
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import pixlists.composeapp.generated.resources.Res
import pixlists.composeapp.generated.resources.name_already_in_use
import pixlists.composeapp.generated.resources.rename_pixlist

@Composable
fun RenamePixListDialog(
    curName: String,
    invalideNames: List<String>,
    onDismiss: () -> Unit,
    onFinish: (String) -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        delay(150)
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    var nameField by remember {
        mutableStateOf(
            TextFieldValue(
                text = curName,
                selection = TextRange(curName.length)
            )
        )
    }

    var validToFinish by remember { mutableStateOf(false) }

    LaunchedEffect(nameField.text) {
        validToFinish = nameField.text.isNotBlank() && !invalideNames.contains(nameField.text)
    }

    CustomDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = stringResource(Res.string.rename_pixlist),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        },
        leftIcons = {
            IconButton(
                onClick = { onDismiss() }
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Close",
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        },
        rightIcons = {
            IconButton(
                onClick = { onFinish(nameField.text) },
                enabled = validToFinish,
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Submit",
                    tint = if (validToFinish) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                )
            }
        }
    ) {
        val invalid = invalideNames.contains(nameField.text)
        AnimatedVisibility(
            visible = invalid,
            enter = expandVertically(),
            exit = shrinkVertically(),
            modifier = Modifier
                .fillMaxWidth()
        ){
            Row {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Error",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .height(16.dp)
                )
                Text(
                    text = stringResource(Res.string.name_already_in_use),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        OutlinedTextField(
            value = nameField,
            onValueChange = { nameField = it.copy(text = it.text.replace("\n", " ")) },
            singleLine = true,
            label = { Text("Name") },
            isError = invalid,
            modifier = Modifier
                .focusRequester(focusRequester)
                .fillMaxWidth()
        )
    }
}