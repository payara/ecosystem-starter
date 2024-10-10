/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fish.payara.starter.resources;

import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerOutputStream extends OutputStream {
    private final Logger logger;
    private final Level level;
    private final StringBuilder buffer = new StringBuilder();

    public LoggerOutputStream(Logger logger, Level level) {
        this.logger = logger;
        this.level = level;
    }

    @Override
    public void write(int b) {
        if (b == '\n') {
            logger.log(level, buffer.toString());
            buffer.setLength(0);
        } else {
            buffer.append((char) b);
        }
    }
}
