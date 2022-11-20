package breakbadhabits.app.ui.screen

//@Composable
//fun HabitEventCreationScreen(
//    habitId: Int,
//    onFinished: () -> Unit
//) {
//    val dateTimeFormatter: DateTimeFormatter = appDependencies.dateTimeFormatter
//
//    val timeInputFeature = rememberEpicStoreEntry {
//        appDependencies.createHabitEventTimeInputFeature()
//    }
//    val commentInputFeature = rememberEpicStoreEntry {
//        appDependencies.createHabitEventCommentInputFeature()
//    }
//    val habitNameFeature = rememberEpicStoreEntry {
//        appDependencies.createHabitNameFeature(habitId)
//    }
//    val creationFeature = rememberEpicStoreEntry {
//        appDependencies.createHabitEventCreationFeature()
//    }
//
//    val habitName by habitNameFeature.state.collectAsState()
//    val time by timeInputFeature.input.collectAsState()
//    val timeValidation by timeInputFeature.validation.collectAsState()
//    val comment by commentInputFeature.input.collectAsState()
//
//    LaunchedEffect(true) {
//        if (time == null) {
//            timeInputFeature.changeInput(System.currentTimeMillis())
//        }
//    }
//
//    val dateSelectionState = rememberMaterialDialogState()
//    val timeSelectionState = rememberMaterialDialogState()
//
//    MaterialDialog(
//        dialogState = dateSelectionState,
//        buttons = {
//            positiveButton(stringResource(R.string.ok))
//            negativeButton(stringResource(R.string.cancel))
//        }
//    ) {
//        val calendar = Calendar.getInstance().apply {
//            time?.let(::setTimeInMillis)
//        }
//
//        datepicker(
//            initialDate = LocalDate.of(
//                calendar[Calendar.YEAR],
//                calendar[Calendar.MONTH] + 1,
//                calendar[Calendar.DAY_OF_MONTH],
//            ),
//            title = stringResource(R.string.select_date)
//        ) { date ->
//            timeInputFeature.changeInput(
//                Calendar.getInstance().apply {
//                    time?.let(::setTimeInMillis)
//                    set(Calendar.YEAR, date.year)
//                    set(Calendar.MONTH, date.monthValue - 1)
//                    set(Calendar.DAY_OF_MONTH, date.dayOfMonth)
//                }.timeInMillis
//            )
//        }
//    }
//
//    MaterialDialog(
//        dialogState = timeSelectionState,
//        buttons = {
//            positiveButton(stringResource(R.string.ok))
//            negativeButton(stringResource(R.string.cancel))
//        }
//    ) {
//        val calendar = Calendar.getInstance().apply {
//            time?.let(::setTimeInMillis)
//        }
//
//        timepicker(
//            initialTime = LocalTime.of(
//                calendar[Calendar.HOUR],
//                calendar[Calendar.MINUTE]
//            ),
//            is24HourClock = DateFormat.is24HourFormat(LocalContext.current),
//            title = stringResource(R.string.select_time)
//        ) { localTime ->
//            timeInputFeature.changeInput(
//                Calendar.getInstance().apply {
//                    time?.let(::setTimeInMillis)
//                    set(Calendar.HOUR_OF_DAY, localTime.hour)
//                    set(Calendar.MINUTE, localTime.minute)
//                }.timeInMillis
//            )
//        }
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .verticalScroll(rememberScrollState())
//    ) {
//        Title(
//            modifier = Modifier.padding(start = 18.dp, top = 18.dp, end = 18.dp, bottom = 4.dp),
//            text = stringResource(R.string.habitEventCreation_title)
//        )
//
//        Text(
//            modifier = Modifier.padding(start = 16.dp, top = 4.dp, end = 16.dp),
//            text = stringResource(
//                R.string.habitEventCreation_habitName,
//                habitName ?: ""
//            )
//        )
//
//        Text(
//            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
//            text = stringResource(R.string.habitEventCreation_event_description)
//        )
//
//        val calendar = Calendar.getInstance().apply {
//            time?.let(::setTimeInMillis)
//        }
//
//        Button(
//            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
//            onClick = dateSelectionState::show,
//            text = stringResource(
//                R.string.habitEventCreation_eventDate,
//                dateTimeFormatter.formatDate(calendar)
//            )
//        )
//
//        Button(
//            modifier = Modifier.padding(start = 16.dp, top = 2.dp, end = 16.dp),
//            onClick = {
//                timeSelectionState.show()
//            },
//            text = stringResource(
//                R.string.habitEventCreation_eventTime,
//                dateTimeFormatter.formatTime(calendar)
//            )
//        )
//
//        (timeValidation as? HabitEventTimeInputFeature.ValidationState.Executed)?.let {
//            when (it.result) {
//                is HabitEventTimeInputFeature.ValidationResult.BiggestThenCurrentTime -> {
//                    ErrorText(
//                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp),
//                        text = stringResource(R.string.habitEventCreation_eventTimeValidation_biggestThenCurrentTime)
//                    )
//                }
//                else -> {
//                    /* no-op */
//                }
//            }
//        }
//
//        Text(
//            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
//            text = stringResource(R.string.habitEventCreation_comment_description)
//        )
//
//        TextField(
//            modifier = Modifier
//                .padding(start = 16.dp, top = 8.dp, end = 16.dp)
//                .fillMaxWidth(),
//            value = comment ?: "",
//            onValueChange = commentInputFeature::changeInput,
//            label = stringResource(R.string.habitEventCreation_comment)
//        )
//
//        Spacer(modifier = Modifier.weight(1.0f))
//
//        Text(
//            modifier = Modifier
//                .padding(start = 16.dp, end = 16.dp, top = 32.dp)
//                .align(Alignment.End),
//            text = stringResource(R.string.habitEventCreation_finish_description)
//        )
//
//        Button(
//            modifier = Modifier
//                .padding(16.dp)
//                .align(Alignment.End),
//            enabled = (timeValidation as? HabitEventTimeInputFeature.ValidationState.Executed)?.let {
//                it.result is HabitEventTimeInputFeature.ValidationResult.Valid
//            } == true,
//            onClick = {
//                creationFeature.startCreation(
//                    habitId,
//                    time!!,
//                    comment
//                )
//                onFinished()
//            },
//            text = stringResource(R.string.habitEventCreation_finish),
//            interactionType = InteractionType.MAIN
//        )
//    }
//}