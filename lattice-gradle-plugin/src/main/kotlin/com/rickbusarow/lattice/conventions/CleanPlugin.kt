/*
 * Copyright (C) 2024 Rick Busarow
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

package com.rickbusarow.lattice.conventions

import com.rickbusarow.kgx.applyOnce
import com.rickbusarow.lattice.core.stdlib.isOrphanedBuildOrGradleDir
import com.rickbusarow.lattice.core.stdlib.isOrphanedGradleProperties
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.SourceTask
import org.gradle.language.base.plugins.LifecycleBasePlugin
import java.io.File

public abstract class CleanPlugin : Plugin<Project> {
  override fun apply(target: Project) {

    target.plugins.applyOnce("base")

    val deleteEmptyDirs = target.tasks
      .register("deleteEmptyDirs", Delete::class.java) { task ->
        task.description = "Delete all empty directories within a project."

        val subprojectDirs = target.subprojects
          .mapTo(mutableSetOf()) { it.projectDir.absolutePath }

        val projectDir = target.projectDir

        task.doLast { _ ->

          projectDir.walkBottomUp()
            .filter { it.isDirectory }
            .filterNot { dir -> subprojectDirs.any { dir.path.startsWith(it) } }
            .filterNot { it.path.contains(".gradle") }
            .filterNot { it.path.contains(".git") }
            .filter { it.listFiles().isNullOrEmpty() }
            .forEach { it.deleteRecursively() }
        }
      }

    target.tasks.named(LifecycleBasePlugin.CLEAN_TASK_NAME) { task ->
      task.dependsOn(deleteEmptyDirs)
    }

    target.tasks.register("cleanGradle", SourceTask::class.java) { task ->
      task.source(".gradle")

      val projectDir = target.projectDir

      task.doLast { _ ->
        projectDir.walkBottomUp()
          .filter { it.isDirectory }
          .filter { it.path.contains(".gradle") }
          .all { it.deleteRecursively() }
      }
    }

    target.tasks.register("deleteSrcGen", Delete::class.java) { task ->
      task.setDelete("src-gen")
    }

    if (target == target.rootProject) {
      val deleteOrphanedProjectDirs = target.tasks
        .register("deleteOrphanedProjectDirs", Delete::class.java) { task ->

          task.description = buildString {
            append("Delete any 'build' or `.gradle` directory or `gradle.properties` file ")
            append("without an associated Gradle project.")
          }

          val websiteBuildDir = "${target.rootDir}/website/node_modules"
          val projectDir = target.projectDir

          task.doLast { _ ->

            projectDir.walkBottomUp()
              .filterNot { it.path.contains(".git") }
              .filterNot { it.path.startsWith(websiteBuildDir) }
              .filter { it.isOrphanedBuildOrGradleDir() || it.isOrphanedGradleProperties() }
              .forEach(File::deleteRecursively)
          }
        }

      deleteEmptyDirs.configure {
        it.dependsOn(deleteOrphanedProjectDirs)
      }
    }
  }
}
