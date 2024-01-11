plugins {
	id("com.android.application")
	id("kotlin-android")
	id("org.jetbrains.dokka")
	id("org.jlleitschuh.gradle.ktlint")
}

tasks.dokkaGfm.configure {
	outputDirectory.set(file(layout.buildDirectory.dir("../../documentation/reference")))
	dokkaSourceSets {
		named("main") {
			skipDeprecated.set(true)
			skipEmptyPackages.set(true)
			sourceRoots.from(file("src/main/java"))
			suppressInheritedMembers.set(true)
			includeNonPublic.set(true)
		}
	}
}

tasks.register("genDocs") {
	val ref = layout.buildDirectory.dir("../../documentation/reference")
	delete(ref)
	dependsOn("dokkaGfm")
	doLast {
		copy {
			from("$ref/index.md")
			into(ref)
			rename { "README.md" }
		}
	}
}

android {
	compileSdk = 33
	buildToolsVersion = "34.0.0"

	defaultConfig {
		applicationId = "com.fredhappyface.fhcode"
		minSdk = 26
		targetSdk = 33
		versionCode = 20230828
		versionName = "20230828"
		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		setProperty("archivesBaseName", "$applicationId-$versionName")
	}

	buildTypes {
		getByName("debug") { versionNameSuffix = "-debug" }
		getByName("release") {
			// versionNameSuffix = "-release"
			proguardFiles("proguard-android-optimize.txt", "proguard-rules.pro")
			isMinifyEnabled = false
		}
	}

	compileOptions {
		sourceCompatibility(JavaVersion.VERSION_17)
		targetCompatibility(JavaVersion.VERSION_17)
	}

	kotlinOptions { jvmTarget = "17" }
	namespace = "com.fredhappyface.fhcode"
}

dependencies {
	dokkaPlugin("org.jetbrains.dokka:android-documentation-plugin:1.9.10")
	implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.22")
	implementation("androidx.core:core-ktx:1.12.0")
	implementation("androidx.appcompat:appcompat:1.6.1")
	implementation("com.google.android.material:material:1.11.0")
	implementation("androidx.preference:preference-ktx:1.2.1")
	testImplementation("junit:junit:4.13.2")
	testImplementation("androidx.test.ext:junit:1.1.5")
	testImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

ktlint {
	android.set(true)
	coloredOutput.set(false)
	enableExperimentalRules.set(true)
	disabledRules.set(
		setOf(
			"indent",
			"parameter-list-wrapping",
			"experimental:argument-list-wrapping"
		)
	)
	reporters {
		reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN)
	}
}
