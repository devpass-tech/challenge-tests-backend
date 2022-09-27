package io.devpass.creditcard

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.junit.jupiter.api.Test

class ArchTest {

    private val classesToCheck = ClassFileImporter().importClasspath()

    @Test
    fun `Nothing on domain should depend on transport`() {
        noClasses().that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAPackage("..transport..")
            .allowEmptyShould(true)
            .check(classesToCheck)
    }

    @Test
    fun `Nothing on domain should depend on data`() {
        noClasses().that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAPackage("..data..")
            .allowEmptyShould(true)
            .check(classesToCheck)
    }

    @Test
    fun `Nothing on transport should depend on data`() {
        noClasses().that().resideInAPackage("..transport..")
            .should().dependOnClassesThat().resideInAPackage("..data..")
            .allowEmptyShould(true)
            .check(classesToCheck)
    }

    @Test
    fun `Nothing on data should depend on transport`() {
        noClasses().that().resideInAPackage("..transport..")
            .should().dependOnClassesThat().resideInAPackage("..data..")
            .allowEmptyShould(true)
            .check(classesToCheck)
    }

    @Test
    fun `Nothing on data should depend on infrastructure`() {
        noClasses().that().resideInAPackage("..data..")
            .should().dependOnClassesThat().resideInAPackage("..infrastructure..")
            .allowEmptyShould(true)
            .check(classesToCheck)
    }
}
