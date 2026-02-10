package dev.zt64.subsonic.client

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.*

internal class ArrayUnwrapSerializer<T>(
    tSerializer: KSerializer<T>
) : JsonTransformingSerializer<List<T>>(ListSerializer(tSerializer)) {
    override fun transformDeserialize(element: JsonElement): JsonElement {
        return element.jsonObject.entries.singleOrNull()?.value?.jsonArray ?: JsonArray(emptyList())
    }
}