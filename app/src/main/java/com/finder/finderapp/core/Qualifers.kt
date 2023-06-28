package com.finder.finderapp.core

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Yelp

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OpenRoute

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class HttpClientOpenRoute