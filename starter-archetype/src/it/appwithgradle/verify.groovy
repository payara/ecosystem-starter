def appDir = new File(new File("."), "AppWithGradle")
def isWin = System.getProperty("os.name").toLowerCase().contains("win")
def gradleCmd = isWin ? ["cmd", "/c", "gradlew.bat", "build"] : ["./gradlew", "build"]
def proc = new ProcessBuilder(gradleCmd).directory(appDir).start()
proc.consumeProcessOutput(System.out, System.err)
def exit = proc.waitFor()
if (exit != 0) throw new RuntimeException("gradle build failed in AppWithGradle")