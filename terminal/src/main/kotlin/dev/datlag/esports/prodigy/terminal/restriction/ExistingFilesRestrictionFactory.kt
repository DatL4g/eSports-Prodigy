package dev.datlag.esports.prodigy.terminal.restriction

import com.github.rvesse.airline.restrictions.ArgumentsRestriction
import com.github.rvesse.airline.restrictions.factories.ArgumentsRestrictionFactory

class ExistingFilesRestrictionFactory : ArgumentsRestrictionFactory {
    override fun createArgumentsRestriction(annotation: Annotation?): ArgumentsRestriction? {
        if (annotation is ExistingFiles) {
            return ExistingFilesRestriction(annotation.extraFlag)
        }
        return null
    }

    override fun supportedArgumentsAnnotations(): MutableList<Class<out Annotation>> {
        return mutableListOf(ExistingFiles::class.java)
    }
}