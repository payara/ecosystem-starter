def appDir = new File(new File("."), "AppWithMP")
def isWin = System.getProperty("os.name").toLowerCase().contains("win")
def mvnCmd = isWin ? ["cmd", "/c", "mvnw.cmd", "verify"] : ["./mvnw", "verify"]
def proc = new ProcessBuilder(mvnCmd).directory(appDir).start()
proc.consumeProcessOutput(System.out, System.err)
def exit = proc.waitFor()
if (exit != 0) throw new RuntimeException("mvn build failed in AppWithMP")