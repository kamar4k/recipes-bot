package io.kamae.family.bot.core.security.hierarchy

class BotAppRoleHierarchy(val hierarchySegments: List<RoleHierarchySegment>) {

    companion object {
        @JvmStatic
        fun builder() = Builder()
    }

    class Builder {
        private val hierarchySegments: MutableList<RoleHierarchySegment> = mutableListOf()

        fun segment(main: String, included: String): Builder {
            hierarchySegments.add(RoleHierarchySegment(main, included))
            return this
        }

        fun build(): BotAppRoleHierarchy {
            return BotAppRoleHierarchy(this.hierarchySegments)
        }
    }
}