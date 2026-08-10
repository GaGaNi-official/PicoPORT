package moe.crx.overport.patches

import moe.crx.overport.patching.Patch

/**
 * Универсальный патч для обхода всех проверок Oculus/Meta в Unity играх
 * Поддерживает множество версий Unity и различных методов проверки
 */
val PATCH_UNIVERSAL_OCULUS_UNITY = Patch("patch_universal_oculus_unity", true) {
    // Патч для основных классов Oculus Unity SDK
    selectSmali("com/unity/oculus/OculusUnity") {
        replace(
            "(getIsOnOculusHardware\\(\\)Z.*?)return (\\w+)", 
            "$1\nconst/4 $2, 0x1\nreturn $2"
        )
    }

    // Патч для OVRCameraRig и связанных классов
    selectSmali("OVRCameraRig") {
        replace(
            "sget-object (\\w+), Landroid/os/Build;->MANUFACTURER:Ljava/lang/String;", 
            "const-string $1, \"Oculus\""
        )
        replace(
            "sget-object (\\w+), Landroid/os/Build;->MODEL:Ljava/lang/String;", 
            "const-string $1, \"Quest 2\""
        )
    }

    // Патч для OVRPlugin
    selectSmali("OVRPlugin") {
        replace(
            "invoke-static \\{\\}, LOVRPlugin;->GetSystemProperty\\(\\)I", 
            "const/4 v0, 0x1\nreturn v0"
        )
    }

    // Патч для проверок в Oculus Integration SDK
    selectSmali("com/oculus/ovr/OVR") {
        replace(
            "invoke-static \\{\\}, Lcom/oculus/ovr/OVR;->IsOculusPlatform\\(\\)Z", 
            "const/4 v0, 0x1\nreturn v0"
        )
    }

    // Библиотечный патч для libOculusXRPlugin.so
    selectLibrary("libOculusXRPlugin.so") {
        // Патч для проверки manufacturer
        replaceHex(
            "1F 1C 00 72 ?? ?? ?? ?? E9 07 9F 1A C0 00 80 52 20 00 A0 72 ?? ?? ?? ??",
            "1F 1C 00 72 ?? ?? ?? ?? 29 00 80 52 C0 00 80 52 20 00 A0 72 ?? ?? ?? ??"
        )
        // Патч для проверки model
        replaceHex(
            "00 28 18 BF 01 20 ?? ?? 08 70 06 20 C0 F2 01 00",
            "01 20 00 BF 00 BF ?? ?? 08 70 06 20 C0 F2 01 00"
        )
        // Дополнительный патч для новых версий
        replaceHex(
            "E0 03 14 AA 08 19 40 F9 00 01 3F D6 A0 07 00 B4",
            "E0 03 14 AA E0 03 1F AA 1F 20 03 D5 A0 07 00 B4"
        )
    }

    // Патч для libopenxr_loader.so если присутствует
    selectLibrary("libopenxr_loader.so") {
        replaceHex(
            "?? ?? ?? ?? ?? ?? 6D 20 00 F9 ?? ?? ?? ?? ?? ?? 00 20 40 F9",
            "?? ?? ?? ?? ?? ?? 6D 20 00 F9 ?? ?? ?? ?? ?? ?? 00 20 40 F9"
        )
    }
}
