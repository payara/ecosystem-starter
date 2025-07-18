def appDir = new File("./target/it/appwithmp/AppWithMP")
def isWin = System.getProperty("os.name").toLowerCase().contains("win")
def mvnCmd = isWin ? ["cmd", "/c", "mvnw.cmd", "verify"] : ["./mvnw", "verify"]
// Add execute permission to mvnw on Unix-like systems
if (!isWin) {
    def mvnw = new File(appDir, "mvnw")
    if (mvnw.exists()) {
        mvnw.setExecutable(true)
    }
}
def proc = mvnCmd.execute(null, appDir)
proc.consumeProcessOutput(System.out, System.err)
def exit = proc.waitFor()
if (exit != 0) throw new RuntimeException("mvn build failed in AppWithMP")