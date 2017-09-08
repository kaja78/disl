package org.disl.pattern

/**
 * Execute shell command.
 */
abstract class ExecuteShellStep extends Step {

    File workingDirectory=new File(System.properties.'user.dir')
    String processOutput



    @Override
    protected int executeInternal() {
        Process process = createProcess(getCode())
        processOutput=process.text
        process.waitFor()
        if (!isIgnoreErrors() && process.exitValue() != 0) {
            throw new RuntimeException("""\
Shell command exited with retun code ${process.exitValue()}.

${processOutput}""")
        }
        return process.exitValue()
    }

    protected Process createProcess(String command) {
        new ProcessBuilder(addShellPrefix(command))
                .directory(workingDirectory)
                .start()

    }

    protected String[] addShellPrefix(String command) {
        if (isWindows()) {
            return ['cmd','/c',command]
        } else {
            return ['sh','-c',command]
        }
    }


    boolean isWindows() {
        System.getProperty('os.name').toLowerCase().contains('windows')
    }
}
