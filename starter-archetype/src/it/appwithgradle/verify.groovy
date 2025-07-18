def appDir = new File("./target/it/appwithgradle/AppWithGradle")
def isWin = System.getProperty("os.name").toLowerCase().contains("win")
def gradleCmd = isWin ? ["cmd", "/c", "gradlew.bat", "build"] : ["./gradlew", "build"]
def proc = gradleCmd.execute(null, appDir)
proc.consumeProcessOutput(System.out, System.err)
def exit = proc.waitFor()
if (exit != 0) throw new RuntimeException("gradle build failed in AppWithGradle")