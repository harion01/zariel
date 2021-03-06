package org.metalscraps.eso.lang.client.config

import org.metalscraps.eso.lang.client.config.ClientConfig.ClientConfigOptions.*
import org.metalscraps.eso.lang.lib.config.ESOConfig
import org.metalscraps.eso.lang.lib.config.ESOConfigOptions
import java.nio.file.Path

class ClientConfig(configDirPath: Path, configPath: Path) : ESOConfig(configDirPath, configPath) {

    internal val isLaunchAfterUpdate: Boolean
        get() = getConf(LAUNCH_ESO_AFTER_UPDATE).toBoolean()

    internal val isUpdateLang: Boolean
        get() = getConf(UPDATE_LANG).toBoolean()

    internal val isUpdateDestination: Boolean
        get() = getConf(UPDATE_DESTINATIONS).toBoolean()

    internal val isEnableZanataListener: Boolean
        get() = getConf(ENABLE_ZANATA_LISTENER).toBoolean()

    internal val localLangVersion: Long
        get() = getConf(LOCAL_LANG_VERSION).toLong()

    internal val zanataManagerX: Int
        get() = getConf(ZANATA_MANAGER_X).toInt()

    internal val zanataManagerY: Int
        get() = getConf(ZANATA_MANAGER_Y).toInt()

    internal val zanataManagerW: Int
        get() = getConf(ZANATA_MANAGER_WIDTH).toInt()

    internal val zanataManagerH: Int
        get() = getConf(ZANATA_MANAGER_HEIGHT).toInt()

    internal val zanataManagerO: Float
        get() = getConf(ZANATA_MANAGER_OPACITY).toFloat()

    enum class ClientConfigOptions(private val v: String) : ESOConfigOptions {

        LOCAL_LANG_VERSION("local_lang_ver"),
        ZANATA_MANAGER_X("zanata_manager_x"),
        ZANATA_MANAGER_Y("zanata_manager_y"),
        ZANATA_MANAGER_WIDTH("zanata_manager_width"),
        ZANATA_MANAGER_HEIGHT("zanata_manager_height"),
        ZANATA_MANAGER_OPACITY("zanata_manager_opacity"),
        LAUNCH_ESO_AFTER_UPDATE("launchESOafterUpdate"),
        UPDATE_LANG("doUpdateLang"),
        UPDATE_DESTINATIONS("doUpdateDestnations"),
        ENABLE_ZANATA_LISTENER("enableZanataListener");

        override fun toString(): String { return v }
    }
}
