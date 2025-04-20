package ru.kolsanovafit.workouts.extentions

import androidx.fragment.app.Fragment
import ru.kolsanovafit.workouts.R
import ru.kolsanovafit.workouts.domain.entity.NetworkError

fun Fragment.formatErrorMessage(error: NetworkError): String {
    return when (error) {
        is NetworkError.ServerError ->
            getString(R.string.server_error, error.code, error.serverMessage)

        is NetworkError.ConnectionError ->
            getString(R.string.connection_error)

        is NetworkError.UnknownError ->
            getString(R.string.unknown_error)
    }
}