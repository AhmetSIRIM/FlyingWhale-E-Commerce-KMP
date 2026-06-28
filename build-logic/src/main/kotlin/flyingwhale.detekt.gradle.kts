/**
 * Applies detekt with the formatting ruleset and registers `detektAll`.
 *
 * Contract:
 * - Sources scanned: every file under `src/`, recursively. No type resolution.
 * - Enforces `allWarningsAsErrors` on every `KotlinCompilationTask` of the consumer.
 * - Reads detekt configuration from `<rootProject>/config/detekt/detekt.yml`.
 */

import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

// Plugin id is bare (no `alias(libs.plugins.X)`) because precompiled scripts do
// not get the version-catalog accessor; the plugin's version is pinned via the
// `detekt-gradlePlugin` library dependency in build-logic/build.gradle.kts.
plugins {
    id("io.gitlab.arturbosch.detekt")
}

// Precompiled scripts do not generate the typed `libs.*` accessor either, so the
// catalog is resolved through the extension to look up library entries.
val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
    detektPlugins(libs.findLibrary("detekt-formatting").get())
}

// One detekt pass over all source sets without type resolution: detekt's per-source-set
// tasks skip intermediate sets (e.g. iosMain), and type-resolution tasks misfire when
// detekt's bundled Kotlin trails the project's; sourcing src/ with no classpath avoids both.
tasks.register<Detekt>("detektAll") {
    group = "verification"
    description = "Runs detekt over all Kotlin source sets without type resolution."
    buildUponDefaultConfig = true
    config.setFrom(rootProject.files("config/detekt/detekt.yml"))
    parallel = true
    // Opt-in autoCorrect via `-PdetektAutoCorrect`; off in CI so it only checks, never rewrites.
    autoCorrect = providers.gradleProperty("detektAutoCorrect").isPresent
    setSource(files("src"))
    include("**/*.kt")
}

tasks.withType<KotlinCompilationTask<*>>().configureEach {
    compilerOptions {
        allWarningsAsErrors.set(true)
    }
}
