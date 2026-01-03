package io.kamae.recipes.infrastructure.store.adapter.mapper

import io.kamae.recipes.application.dto.RecipeDto
import io.kamae.recipes.application.dto.RecipeShortInfoDto
import io.kamae.recipes.infrastructure.store.entity.RecipeEntity
import io.kamae.recipes.infrastructure.store.entity.RecipeSummaryProjection
import org.mapstruct.*
import org.mapstruct.MappingConstants.ComponentModel
import java.time.LocalDateTime
import java.util.UUID

@Mapper(componentModel = ComponentModel.SPRING)
abstract class RecipeStoreMapper {

    @BeanMapping(ignoreByDefault = true, nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
    @Mappings(
        Mapping(target = "id"),
        Mapping(target = "title"),
        Mapping(target = "ingredients", qualifiedByName = ["parseIngredients"]),
        Mapping(target = "instructions")
    )
    abstract fun mapEntityToDto(entity: RecipeEntity?): RecipeDto

    @BeanMapping(ignoreByDefault = true)
    @Mappings(
        Mapping(target = "id", expression = "java(generateUUID())"),
        Mapping(target = "title"),
        Mapping(target = "ingredients", qualifiedByName = ["serializeIngredients"]),
        Mapping(target = "instructions"),
        Mapping(target = "createdAt", expression = "java(currentDateTime())"),
    )
    abstract fun mapDtoToEntityWithGeneratedId(dto: RecipeDto): RecipeEntity

    abstract fun mapSummaryListToDto(entityList: List<RecipeSummaryProjection>): List<RecipeShortInfoDto>

    @BeanMapping(ignoreByDefault = true)
    @Mappings(
        Mapping(target = "id"),
        Mapping(target = "title"),
    )
    abstract fun mapSummaryToShortInfoDto(entity: RecipeSummaryProjection): RecipeShortInfoDto

    protected fun generateUUID() = UUID.randomUUID().toString()

    protected fun currentDateTime() = LocalDateTime.now()

    @Named("parseIngredients")
    fun parseIngredients(ingredientsStr: String) = ingredientsStr.split("\n")

    @Named("serializeIngredients")
    fun serializeIngredients(ingredients: List<String>) = ingredients.joinToString(separator = "\n")
}