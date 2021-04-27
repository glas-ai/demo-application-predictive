package com.autoencoder.glasdemoapp.shared.utils.persistence

import com.autoencoder.glasdemoapp.shared.utils.persistence.HawkKeys.CONFIG_FILE_PATH
import com.orhanobut.hawk.Hawk

interface PersistenceService {

    var configFilePath: String?
        get() = Hawk.get(CONFIG_FILE_PATH)
        set(configFilePath) {
            Hawk.put(CONFIG_FILE_PATH, configFilePath)
        }
}