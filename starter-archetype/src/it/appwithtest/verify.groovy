def appDir = new File("./target/it/appwithtest/AppWithTest")
def mvnCmd = System.getProperty("os.name").toLowerCase().contains("win") ? "mvnw.cmd" : "mvnw"
// Add execute permission to mvnw on Unix-like systems
if (!isWin) {
    def mvnw = new File(appDir, "mvnw")
    if (mvnw.exists()) {
        mvnw.setExecutable(true)
    }
}
def proc = [mvnCmd, "verify"].execute(null, appDir)
proc.consumeProcessOutput(System.out, System.err)
def exit = proc.waitFor()
if (exit != 0) throw new RuntimeException("mvn test failed in AppWithTest")