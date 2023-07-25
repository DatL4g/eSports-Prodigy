package dev.datlag.esports.prodigy.other

import kotlin.reflect.KClass

class Mapper private constructor(
    val mappings: Map<Pair<KClass<*>, KClass<*>>, (Any) -> Any>
) {

    inline fun <reified F : Any, reified T : Any> map(data: F): T {
        val mapper = mappings.getOrDefault(F::class to T::class, null)
        return mapper?.invoke(data) as? T ?: error("Could not map from ${F::class.qualifiedName} to ${T::class.qualifiedName}")
    }

    inline fun <reified F : Any, reified T : Any> mapCollection(data: Collection<F>): Collection<T> {
        val mapper = mappings.getOrDefault(F::class to T::class, null)
        return data.map {
            mapper?.invoke(it) as? T ?: error("Could not map from ${F::class.qualifiedName} to ${T::class.qualifiedName}")
        }
    }


    class Builder {
        val mappings: MutableMap<Pair<KClass<*>, KClass<*>>, (Any) -> Any> = mutableMapOf()

        inline fun <reified F : Any, reified T : Any> mapTypes(noinline builder: (F) -> T) = apply {
            mappings[F::class to T::class] = builder as (Any) -> Any
        }

        fun build() = Mapper(mappings)
    }

    companion object {
        fun build(builder: Builder.() -> Unit): Mapper = Builder().apply(builder).build()
    }
}