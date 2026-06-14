package com.hydowned.util

import com.hypixel.hytale.component.ArchetypeChunk
import com.hypixel.hytale.component.Component
import com.hypixel.hytale.component.Holder
import java.lang.reflect.Constructor

object HolderUtil {
    private val holderCtor: Constructor<*> by lazy {
        val ctor = Holder::class.java.getDeclaredConstructor()
        ctor.isAccessible = true
        ctor
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> toHolder(index: Int, chunk: ArchetypeChunk<T>): Holder<T> {
        val arch = chunk.archetype
        val len = arch.length()
        val components = arrayOfNulls<Component<T>>(len)
        for (i in 0 until len) {
            val ct = arch.get(i) ?: continue
            components[i] = chunk.getComponent(index, ct)
        }
        val holder = holderCtor.newInstance() as Holder<T>
        holder.init(arch, components)
        return holder
    }
}
