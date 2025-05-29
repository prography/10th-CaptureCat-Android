package com.prography.util

class RetrofitFailureStateException(error: String ?, val code: Int) : Exception(error) {
}