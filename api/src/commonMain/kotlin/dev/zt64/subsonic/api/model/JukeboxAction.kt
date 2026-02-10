package dev.zt64.subsonic.api.model

import kotlinx.serialization.Serializable

@Serializable
public sealed class JukeboxAction {
    internal sealed interface ActionId {
        val id: Long
    }

    public data object Status : JukeboxAction()

    public data object Start : JukeboxAction()

    public data object Stop : JukeboxAction()

    public data object Shuffle : JukeboxAction()

    public data object Clear : JukeboxAction()

    @Serializable
    public data class Add(override val id: Long) : JukeboxAction(), ActionId

    @Serializable
    public data class Set(override val id: Long) : JukeboxAction(), ActionId

    @Serializable
    public data class Remove(val index: Int) : JukeboxAction()

    @Serializable
    public data class Skip(val index: Int, val offset: Int = 0) : JukeboxAction()

    @Serializable
    public data class SetGain(val gain: Float) : JukeboxAction()
}