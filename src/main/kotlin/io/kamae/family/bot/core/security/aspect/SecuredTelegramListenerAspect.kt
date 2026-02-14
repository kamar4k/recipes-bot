package io.kamae.family.bot.core.security.aspect

import io.kamae.family.bot.core.domain.model.TelegramUpdateEvent
import io.kamae.family.bot.core.security.consts.AuthorizationConstants
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Update

@Aspect
@Component
class SecuredTelegramListenerAspect(
    private val authenticationManager: AuthenticationManager,
) {
    init {
        logger.debug("SecuredTelegramListenerAspect bean initialized")
    }
    companion object {
        private const val SECURED_ANNOTATION =
            "io.kamae.family.bot.core.security.annotation.SecuredTelegramListener"

        private val logger = LoggerFactory.getLogger(SecuredTelegramListenerAspect::class.java)
    }

    @Before("@within($SECURED_ANNOTATION) && execution(* processUpdate(*)) && args(telegramUpdateEvent)")
    fun fillAuthorizationContext(joinPoint: JoinPoint, telegramUpdateEvent: TelegramUpdateEvent) {
        val username = extractUsername(telegramUpdateEvent.update)
        logger.debug("#auth extracted username=$username for update event")

        val authentication: Authentication = UsernamePasswordAuthenticationToken(
            username,
            AuthorizationConstants.EMPTY_PASSWORD
        )

        val authenticated = authenticationManager.authenticate(authentication)

        SecurityContextHolder.setContext(SecurityContextImpl(authenticated))
    }

    private fun extractUsername(update: Update?): String? {
        return when {
            update == null -> null
            update.hasMessage() -> update.message.from.userName
            update.hasCallbackQuery() -> update.callbackQuery.from.userName
            update.hasInlineQuery() -> update.inlineQuery.from.userName
            update.hasChosenInlineQuery() -> update.chosenInlineQuery.from.userName
            update.hasChannelPost() -> update.channelPost.chat.userName
            update.hasEditedChannelPost() -> update.editedChannelPost.chat.userName
            update.hasEditedMessage() -> update.editedMessage.from.userName
            else -> null
        }
    }
}