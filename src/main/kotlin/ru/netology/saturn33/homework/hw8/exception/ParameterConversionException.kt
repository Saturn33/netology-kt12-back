package ru.netology.saturn33.homework.hw8.exception

class ParameterConversionException(parameterName: String, type: String, cause: Throwable? = null) :
    BadRequestException("Request parameter $parameterName couldn't be parsed/converted to $type", cause)
