package com.tavi.di

import com.tavi.repository.HunterRepository
import com.tavi.repository.HunterRepositoryAlternative
import com.tavi.repository.HunterRepositoryImplAlternative
import com.tavi.repository.HunterRepositoryImpl
import org.koin.dsl.module

val koinModule = module {
    single<HunterRepository> {
        HunterRepositoryImpl()
    }
    //Alternative
    /*
    single<HunterRepositoryAlternative> {
        HunterRepositoryImplAlternative()
    }*/
}