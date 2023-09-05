package shell.units;

import java.io.*;

public class Redirector extends Thread {
    // private final InputStream is;
    // private final OutputStream os;

    // public Redirector(InputStream is, OutputStream os) {
    // this.is = is;
    // this.os = os;
    // }

    // public void run() {
    // try {
    // is.transferTo(os);
    // } catch (IOException e) {
    // e.printStackTrace();
    // throw new RuntimeException(e.getMessage());
    // }
    // }
    private final InputStream _process1Output;
    private final OutputStream _process2Input;

    public Redirector(InputStream process1Output, OutputStream process2Input) {
        _process1Output = process1Output;
        _process2Input = process2Input;
    }

    /**
     * Perform the copy operation
     */
    private final void transmit() throws IOException {
        int value;

        while ((value = _process1Output.read()) != -1) {
            _process2Input.write(value);
            _process2Input.flush(); // I'm pretty sure this isn't needed
        }

        _process1Output.close();
        _process2Input.close();
    }

    public void run() {
        try {
            transmit();
        } catch (IOException error) {
            throw new IOError(error);
        }
    }
}
