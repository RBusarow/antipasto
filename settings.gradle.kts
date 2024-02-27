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

rootProject.name = "mahout"

pluginManagement {
  repositories {
    gradlePluginPortal()
    mavenCentral()
    google()
  }

  includeBuild("build-logic/conventions")
  includeBuild("build-logic/delegate")

  plugins {
    id("com.rickbusarow.mahout.kotlin-jvm-module") apply false
    id("com.rickbusarow.mahout.root") apply false
  }
}

plugins {
  id("com.gradle.enterprise") version "3.16.2"
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
    google()
  }
}

gradleEnterprise {
  buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
    publishAlways()

    // https://docs.github.com/en/actions/learn-github-actions/variables#default-environment-variables
    tag(if (System.getenv("CI").isNullOrBlank()) "Local" else "CI")

    val gitHubActions = System.getenv("GITHUB_ACTIONS")?.toBoolean() ?: false

    if (gitHubActions) {
      // ex: `octocat/Hello-World` as in github.com/octocat/Hello-World
      val repository = System.getenv("GITHUB_REPOSITORY")!!
      val runId = System.getenv("GITHUB_RUN_ID")!!

      link(
        "GitHub Action Run",
        "https://github.com/$repository/actions/runs/$runId"
      )
    }
  }
}

include(
  ":mahout-api",
  ":mahout-core",
  ":mahout-gradle-plugin",
  ":mahout-settings-annotations",
  ":mahout-settings-generator"
)

includeBuild("build-logic")
