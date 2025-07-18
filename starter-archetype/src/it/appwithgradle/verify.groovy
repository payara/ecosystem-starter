def appDir = new File("./target/it/appwithgradle/AppWithGradle")
def isWin = System.getProperty("os.name").toLowerCase().contains("win")
def gradleCmd = isWin ? ["cmd", "/c", "gradlew.bat", "build"] : ["./gradlew", "build"]
// Add execute permission to gradlew on Unix-like systems
if (!isWin) {
    def gradlew = new File(appDir, "gradlew")
    if (gradlew.exists()) {
        gradlew.setExecutable(true)
    }
}
def proc = gradleCmd.execute(null, appDir)
proc.consumeProcessOutput(System.out, System.err)
def exit = proc.waitFor()
if (exit != 0) throw new RuntimeException("gradle build failed in AppWithGradle")