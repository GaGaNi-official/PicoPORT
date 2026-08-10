package moe.crx.overport.patches

import moe.crx.overport.patching.Patch

/**
 * Универсальный патч для обхода проверок Oculus/Meta в Unreal Engine играх
 * Поддерживает множество версий Unreal Engine и различных методов проверки
 */
val PATCH_UNIVERSAL_OCULUS_UNREAL = Patch("patch_universal_oculus_unreal", true) {
    // Патч для GameActivity в Epic Games UE4
    selectSmali(
        "com/epicgames/ue4/GameActivity",
        "com/epicgames/unreal/GameActivity"
    ) {
        // Патч для проверки manufacturer
        replace(
            "sget-object (\\w+), Landroid/os/Build;->MANUFACTURER:Ljava/lang/String;", 
            "const-string $1, \"Oculus\""
        )
        // Патч для проверки model  
        replace(
            "sget-object (\\w+), Landroid/os/Build;->MODEL:Ljava/lang/String;", 
            "const-string $1, \"Quest 2\""
        )
        // Патч для проверки brand
        replace(
            "sget-object (\\w+), Landroid/os/Build;->BRAND:Ljava/lang/String;", 
            "const-string $1, \"Oculus\""
        )
        // Патч для проверки device
        replace(
            "sget-object (\\w+), Landroid/os/Build;->DEVICE:Ljava/lang/String;", 
            "const-string $1, \"quest2\""
        )
        // Патч для проверки product
        replace(
            "sget-object (\\w+), Landroid/os/Build;->PRODUCT:Ljava/lang/String;", 
            "const-string $1, \"quest2\""
        )
    }

    // Патч для предотвращения ForceQuit
    selectSmali(
        "com/epicgames/ue4/GameActivity",
        "com/epicgames/unreal/GameActivity"
    ) {
        replace(
            "(\\.method public AndroidThunkJava_ForceQuit\\(\\)V.*?)(invoke-static \\{\\w\\d+?\\}, Ljava\\/lang\\/System;->exit\\(I\\)V)(.*?\\.end method)",
            "$1$3"
        )
    }

    // Патч для проверок OVRPlugin в Unreal
    selectSmali("OVRPlugin") {
        replace(
            "invoke-static \\{\\}, LOVRPlugin;->GetSystemProperty\\(\\)I", 
            "const/4 v0, 0x1\nreturn v0"
        )
    }

    // Патч для библиотек Oculus
    selectLibrary("libOVRPlugin.so") {
        // Патч для проверки manufacturer/model
        replaceHex(
            "1F 1C 00 72 ?? ?? ?? ?? E9 07 9F 1A C0 00 80 52 20 00 A0 72",
            "1F 1C 00 72 ?? ?? ?? ?? 29 00 80 52 C0 00 80 52 20 00 A0 72"
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
