package xyz.bluspring.nicknamer.commands.arguments

import com.google.common.collect.Iterables
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource
import net.minecraft.command.CommandSource
import net.minecraft.command.EntitySelector
import net.minecraft.command.EntitySelectorReader
import net.minecraft.entity.Entity
import java.util.concurrent.CompletableFuture

class LocalEntityArgumentType : ArgumentType<EntitySelector> {
    override fun <S : Any?> listSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        if (context.source is CommandSource) {
            val stringReader = StringReader(builder.input)
            stringReader.cursor = builder.start

            val source = context.source as CommandSource
            val selector = EntitySelectorReader(stringReader, false)

            try {
                selector.read()
            } catch (_: Exception) {}

            return selector.listSuggestions(builder) { builderx: SuggestionsBuilder ->
                val collection: Collection<String> = source.playerNames
                val iterable =
                    Iterables.concat(
                        collection,
                        source.entitySuggestions
                    )

                CommandSource.suggestMatching(iterable, builderx)
            }
        } else {
            return Suggestions.empty()
        }
    }

    override fun parse(reader: StringReader): EntitySelector {
        val selectorReader = EntitySelectorReader(reader)

        return selectorReader.read()
    }

    companion object {
        fun entity(): LocalEntityArgumentType {
            return LocalEntityArgumentType()
        }

        fun getEntity(context: CommandContext<FabricClientCommandSource>, name: String): Entity {
            val argument = context.getArgument(name, EntitySelector::class.java)
            val player = context.source.player

            return argument.getEntity(null)
        }
    }
}