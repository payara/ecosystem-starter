def appDir = new File("./target/it/appwithgradle/AppWithGradle")
def isWin = System.getProperty("os.name").toLowerCase().contains("win")
// Add execute permission to gradlew on Unix-like systems
if (!isWin) {
    def gradlew = new File(appDir, "gradlew")
    if (gradlew.exists()) {
        gradlew.setExecutable(true)
    }
}
def gradleCmd = isWin ? ["cmd", "/c", "gradlew.bat", "build"] : ["./gradlew", "build"]
def proc = gradleCmd.execute(null, appDir)
proc.consumeProcessOutput(System.out, System.err)
def exit = proc.waitFor()
if (exit != 0) throw new RuntimeException("gradle build failed in AppWithGradle")