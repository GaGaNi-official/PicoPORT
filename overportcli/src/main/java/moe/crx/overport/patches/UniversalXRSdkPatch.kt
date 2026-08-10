package moe.crx.overport.patches

import moe.crx.overport.patching.Patch

/**
 * Расширенный патч для поддержки различных XR SDK
 * Работает с Varjo, SteamVR, WMR и другими платформами
 */
val PATCH_UNIVERSAL_XR_SDK = Patch("patch_universal_xr_sdk", true) {
    // Патч для Varjo XR Plugin
    selectSmali("com/varjo/xr/*") {
        replace(
            "invoke-virtual \\{\\w+, \\w+\\}, Lcom/varjo/xr/Session;->isInitialized\\(\\)Z",
            "const/4 v0, 0x1\nreturn v0"
        )
    }

    // Патч для SteamVR Input
    selectSmali("com/valvesoftware/steamvr/*") {
        replace(
            "(getIsSteamVRReady\\(\\)Z.*?)return \\w+",
            "$1\nconst/4 v0, 0x1\nreturn v0"
        )
    }

    // Патч для Windows Mixed Reality
    selectSmali("com/microsoft/xr/*") {
        replace(
            "sget-object \\w+, Landroid/os/Build;->MANUFACTURER:Ljava/lang/String;",
            "const-string v0, \"Microsoft\""
        )
    }

    // Патч для各种 XR loader
    selectLibrary("libxr_loader.so") {
        replaceHex(
            "?? ?? ?? ?? ?? ?? 00 00 80 D2 1F 20 03 D5",
            "?? ?? ?? ?? ?? ?? 20 00 80 D2 1F 20 03 D5"
        )
    }

    // Патч для libopenxr_palaso.so
    selectLibrary("libopenxr_palaso.so") {
        replaceHex(
            "E0 03 14 AA 08 19 40 F9 00 01 3F D6",
            "E0 03 14 AA E0 03 1F AA 1F 20 03 D5"
        )
    }
}
