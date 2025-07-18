def appDir = new File("./target/it/appwithtest/AppWithTest")
def mvnCmd = System.getProperty("os.name").toLowerCase().contains("win") ? "mvn.cmd" : "mvn"
def proc = [mvnCmd, "verify"].execute(null, appDir)
proc.consumeProcessOutput(System.out, System.err)
def exit = proc.waitFor()
if (exit != 0) throw new RuntimeException("mvn test failed in AppWithTest")