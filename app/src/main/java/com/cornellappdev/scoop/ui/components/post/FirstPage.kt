package com.cornellappdev.scoop.ui.components.post

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Place
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import com.cornellappdev.scoop.R
import com.cornellappdev.scoop.data.models.rideTypeToString
import com.cornellappdev.scoop.data.models.stringToRideType
import com.cornellappdev.scoop.ui.components.general.CityPicker
import com.cornellappdev.scoop.ui.theme.Green
import com.cornellappdev.scoop.ui.viewmodel.PostScreenViewModel

/**
 * This composable creates the first page of the posting a ride flow which contains the type of
 * ride (either shared uber or driving) as well as the departure and arrival locations.
 */
@Composable
fun FirstPage(
    onProceedClicked: () -> Unit,
    postScreenViewModel: PostScreenViewModel
) {
    val departureText = rememberSaveable {
        mutableStateOf(
            postScreenViewModel.ride.departureLocationName.orEmpty()
        )
    }
    val arrivalText = rememberSaveable {
        mutableStateOf(
            postScreenViewModel.ride.arrivalLocationName.orEmpty()
        )
    }
    val proceedEnabled = rememberSaveable {
        mutableStateOf(
            postScreenViewModel.ride.departureLocationName != ""
                    && postScreenViewModel.ride.arrivalLocationName != ""
                    && postScreenViewModel.ride.type != null
        )
    }
    val updateProceedEnabled = {
        proceedEnabled.value = postScreenViewModel.ride.departureLocationName != ""
                && postScreenViewModel.ride.arrivalLocationName != ""
                && postScreenViewModel.ride.type != null
    }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(horizontal = 37.dp)
                .padding(top = 60.dp)
        ) {
            TransportationSection(
                postScreenViewModel,
                proceedEnabled,
                modifier = Modifier.onFocusChanged {
                    updateProceedEnabled()
                }
            )
            Spacer(
                modifier = Modifier.height(24.dp)
            )
            CityPicker(
                cityState = departureText,
                placeholder = "Departure location",
                icon = Icons.Filled.NearMe,
                modifier = Modifier.onFocusChanged {
                    postScreenViewModel.setDepartureName(departureText.value)
                },
                disableDivider = true,
                placeholderColor = Color(0xFF001E2D),
                onCityChanged = { name, placeId ->
                    postScreenViewModel.setDepartureName(name)
                    postScreenViewModel.setDeparturePlaceId(placeId)
                    updateProceedEnabled()
                }
            )
            Spacer(
                modifier = Modifier.height(24.dp)
            )
            CityPicker(
                cityState = arrivalText,
                placeholder = "Arrival location",
                icon = Icons.Filled.Place,
                modifier = Modifier.onFocusChanged {
                    postScreenViewModel.setArrivalName(arrivalText.value)
                },
                disableDivider = true,
                placeholderColor = Color(0xFF001E2D),
                onCityChanged = { name, placeId ->
                    postScreenViewModel.setArrivalName(name)
                    postScreenViewModel.setArrivalPlaceId(placeId)
                    updateProceedEnabled()
                }
            )

            Column(
                modifier = Modifier
                    .align(Alignment.End)
                    .wrapContentSize()
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .weight(1f),
                verticalArrangement = Arrangement.Bottom
            ) {
                Button(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(bottom = 118.dp)
                        .width(86.dp)
                        .height(51.dp),
                    shape = RoundedCornerShape(26.dp),
                    enabled = proceedEnabled.value,
                    onClick = {
                        onProceedClicked()
                    },
                    contentPadding = PaddingValues(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFFCEE9DC),
                        disabledBackgroundColor = Color(0xFFDBE5DF)
                    )
                ) {
                    Text(
                        "Next",
                        style = MaterialTheme.typography.body1,
                        color = (if (proceedEnabled.value) Color.Black else Color(0xFF001E2D)),
                        fontWeight = Bold
                    )
                }
            }
        }
    }
}

@Composable
fun TransportationSection(
    postScreenViewModel: PostScreenViewModel,
    proceedEnabled: MutableState<Boolean>,
    modifier: Modifier = Modifier
) {
    val items = listOf("Student Driver", "Shared Taxi")
    val selectedValue = rememberSaveable {
        mutableStateOf(
            postScreenViewModel.ride.type
        )
    }
    val isSelectedItem: (String) -> Boolean = {
        rideTypeToString(selectedValue.value) == when (it) {
            "Student Driver" -> "Student Driver"
            "Shared Taxi" -> "Shared Taxi"
            else -> "asdf"
        }
    }
    val updateProceedEnabled = {
        proceedEnabled.value = postScreenViewModel.ride.departureLocationName != ""
                && postScreenViewModel.ride.arrivalLocationName != ""
                && postScreenViewModel.ride.type != null
    }

    // postScreenViewModel.setType()

    Column {
        Text(
            stringResource(R.string.transportation_method),
            style = MaterialTheme.typography.subtitle2
        )
        Spacer(
            modifier = Modifier.height(8.dp)
        )
        items.forEach { item ->
            Row(
                verticalAlignment = CenterVertically,
                modifier = modifier
                    .selectable(
                        selected = isSelectedItem(item),
                        interactionSource = MutableInteractionSource(),
                        indication = null,
                        onClick = {
                            postScreenViewModel.setType(item)
                            selectedValue.value = stringToRideType(item)
                            updateProceedEnabled()
                        },
                        role = Role.RadioButton
                    )
                    .padding(10.dp)
            ) {
                RadioButton(
                    selected = isSelectedItem(item),
                    onClick = null,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Green
                    )
                )
                Spacer(
                    modifier = Modifier.width(14.dp)
                )
                Text(
                    text = item,
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}