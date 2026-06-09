import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidMultiplatformLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.ktor) apply false
    alias(libs.plugins.detekt) apply false
}

// Resolved here (not inside subprojects) because the `libs` catalog accessor is only
// available in the root script body, not within the cross-project subprojects {} block.
val detektFormatting = libs.detekt.formatting

// TODO (Ahmet SIRIM): migrate this subprojects {} wiring to a build-logic convention plugin
//  (precompiled script plugin) so each module does plugins { id("...") } instead of
//  apply(plugin = ...); subprojects {} breaks project isolation, weakens the configuration
//  cache, and triggers the IDE "apply plugin syntax is older" warning.
subprojects {
    // Apply detekt to every module so new modules are covered automatically.
    apply(plugin = "io.gitlab.arturbosch.detekt")

    dependencies {
        "detektPlugins"(detektFormatting)
    }

    // One detekt pass over all source sets without type resolution: detekt's per-source-set
    // tasks skip intermediate sets (e.g. iosMain), and type-resolution tasks misfire when
    // detekt's bundled Kotlin trails the project's; sourcing src/ with no classpath avoids both.
    tasks.register<io.gitlab.arturbosch.detekt.Detekt>("detektAll") {
        group = "verification"
        description = "Runs detekt over all Kotlin source sets without type resolution."
        buildUponDefaultConfig = true // start from detekt's bundled defaults
        config.setFrom(rootProject.files("config/detekt/detekt.yml")) // layer our overrides on top
        parallel = true // analyze source sets in parallel
        // Opt-in autoCorrect via `-PdetektAutoCorrect`; off in CI so it only checks, never rewrites.
        autoCorrect = providers.gradleProperty("detektAutoCorrect").isPresent
        setSource(files("src")) // all source sets; no classpath set => no type resolution
        include("**/*.kt") // Kotlin sources only
    }

    // Treat every compiler warning as an error on all targets (deprecated/unused/etc.),
    // and silence the project-wide expect/actual Beta warning so it doesn't trip the gate.
    tasks.withType<KotlinCompilationTask<*>>().configureEach {
        compilerOptions {
            allWarningsAsErrors.set(true)
            freeCompilerArgs.add("-Xexpect-actual-classes")
        }
    }
}
