/*
 * Copyright 2020-2022 JetBrains s.r.o. and respective authors and developers.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE.txt file.
 */

package org.jetbrains.compose.resources

import androidx.compose.ui.graphics.ImageBitmap
import java.io.IOException

@ExperimentalResourceApi
actual fun resource(path: String): Resource = DesktopResourceImpl(path)

actual typealias ResourcesRawResult = ByteArray
actual typealias ResourcesRawImageResult = ByteArray

internal actual fun ResourcesRawImageResult.rawToImageBitmap(): ImageBitmap = this.toImageBitmap()

actual suspend fun ResourcesRawResult.asResourcesRawImageResult(): ResourcesRawImageResult {
    return this
}

@ExperimentalResourceApi
private class DesktopResourceImpl(path: String) : AbstractResourceImpl(path) {
    override suspend fun readBytes(): ByteArray {
        val classLoader = Thread.currentThread().contextClassLoader ?: (::DesktopResourceImpl.javaClass.classLoader)
        val resource = classLoader.getResourceAsStream(path)
        if (resource != null) {
            return resource.readBytes()
        } else {
            throw MissingResourceException(path)
        }
    }
}

internal actual class MissingResourceException actual constructor(path: String) :
    IOException("Missing resource with path: $path")
