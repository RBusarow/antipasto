/*
 * Copyright (C) 2023 Rick Busarow
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package antipasto

import org.gradle.api.Plugin
import org.gradle.api.Project

/** Applies common conventions to any project. */
abstract class BaseModulePlugin : Plugin<Project> {
  override fun apply(target: Project) {

    target.plugins.apply(CheckPlugin::class.java)
    target.plugins.apply(CleanPlugin::class.java)
    target.plugins.apply(DependencyGuardConventionPlugin::class.java)
    target.plugins.apply(DetektConventionPlugin::class.java)
    target.plugins.apply(DokkaConventionPlugin::class.java)
    target.plugins.apply(KtLintConventionPlugin::class.java)
    target.plugins.apply(TestConventionPlugin::class.java)
  }
}

/** Applies conventions to any kotlin-jvm project. */
abstract class KotlinJvmModulePlugin : BaseModulePlugin() {
  override fun apply(target: Project) {

    target.extensions.create("jvmModule", KotlinJvmModuleExtension::class.java)

    target.plugins.apply(KotlinJvmConventionPlugin::class.java)

    super.apply(target)
  }
}

/** Applies conventions to any kotlin-multiplatform project. */
abstract class KotlinMultiplatformModulePlugin : BaseModulePlugin() {
  override fun apply(target: Project) {

    target.extensions.create("kmpModule", KotlinMultiplatformModuleExtension::class.java)

    target.plugins.apply(KotlinMultiplatformConventionPlugin::class.java)

    super.apply(target)
  }
}
