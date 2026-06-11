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
//  (precompiled script plugin) so each module does plugins { id("...") }. subprojects {} is a
//  cross-project block with no type-safe DSL accessors, which forces three symptoms below:
//   1. apply(plugin = "...") instead of the plugins {} DSL -> IDE "apply syntax is older" warning.
//   2. stringly-typed "detektPlugins"(...) instead of the typed detektPlugins(...) accessor.
//   3. the `libs` version-catalog accessor is unavailable here, forcing detektFormatting to be
//      resolved outside the block (below).
//  It also breaks project isolation and weakens the configuration cache.
//  A convention plugin fixes 1 and 2 cleanly (plugin applied in the same precompiled script ->
//  plugins {} DSL + typed accessors). Symptom 3 is only traded, not removed: `libs` is also
//  unavailable inside precompiled script plugins, so it needs a separate workaround there
//  (e.g. add the catalog to build-logic and read it via VersionCatalogsExtension).
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

    // Treat every compiler warning as an error on all targets (deprecated/unused/etc.).
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask<*>>().configureEach {
        compilerOptions {
            allWarningsAsErrors.set(true)
        }
    }
}
