package com.pnoni.swtbase;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Created by julio on 2016. 7. 6..
 */
public class Launcher {
    public static void main(String[] args) throws Exception {
        Display display = new Display();
        Shell shell = new Shell(display);

        shell.setMinimumSize(500, 300);

        Text helloWorldTest = new Text(shell, SWT.NONE);
        helloWorldTest.setText("Hello World SWT!!!");
        helloWorldTest.pack();

        shell.pack();
        shell.open();
        while(!shell.isDisposed()) {
            if(!display.readAndDispatch())
                display.sleep();
        }
        display.dispose();
    }
}
