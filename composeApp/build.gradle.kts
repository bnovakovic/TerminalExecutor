import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
}

val teVersion: String by project

kotlin {
    jvm("desktop")
    
    sourceSets {
        val desktopMain by getting
        
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.kotlin.serialization)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }
    }
}


compose.desktop {
    application {
        mainClass = "com.bojan.terminalexecutor.MainKt"

        buildTypes.release.proguard {
            isEnabled.set(false)
            configurationFiles.from(File(rootDir, "proguard-rules.pro"))
            joinOutputJars.set(true)
        }

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "TerminalExecutor"
            packageVersion = teVersion
            description = "Executes terminal commans for you"
            copyright = "© 2025 Bojan Novakovic. All rights reserved."
            vendor = "Bojan Novaković"
            licenseFile.set(File(rootDir, "LICENSE"))
            includeAllModules = true

            val iconsFolder = project.file("icons")
            macOS {
                iconFile.set(iconsFolder.resolve("launcher_icon.icns"))
            }
            linux {
                iconFile.set(iconsFolder.resolve("launcher_icon.png"))
            }
            windows {
                iconFile.set(iconsFolder.resolve("launcher_icon.ico"))
            }
        }
    }
}

tasks.register("getVersionName") {
    doLast {
        println(teVersion)
    }
}